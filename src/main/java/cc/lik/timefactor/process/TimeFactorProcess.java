package cc.lik.timefactor.process;

import cc.lik.timefactor.service.SettingConfigGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.User;
import run.halo.app.core.extension.content.Tag;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.infra.ExternalLinkProcessor;
import run.halo.app.infra.SystemInfo;
import run.halo.app.infra.SystemInfoGetter;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimeFactorProcess implements TemplateHeadProcessor {
    private static final DateTimeFormatter BAIDU_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter GOOGLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private record SeoData(
        String title,
        String description,
        String coverUrl,
        String postUrl,
        String author,
        String baiduPubDate,
        String baiduUpdDate,
        String googlePubDate,
        String googleUpdDate,
        String siteName,
        String siteLogo,
        String keywords,
        String content
    ) {}

    private final ReactiveExtensionClient client;
    private final SettingConfigGetter settingConfigGetter;
    private final ExternalLinkProcessor externalLinkProcessor;
    private final SystemInfoGetter systemInfoGetter;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler handler) {
        var modelFactory = context.getModelFactory();
        var postName = Optional.ofNullable(context.getVariable("name"))
            .map(Object::toString)
            .filter(name -> !name.isEmpty())
            .orElse(null);
            
        if (postName == null) {
            return Mono.empty();
        }

        return client.fetch(Post.class, postName)
            .flatMap(post -> buildSeoData(post)
                .flatMap(seoData -> generateSeoTags(seoData, model, modelFactory)));
    }

    private Mono<SeoData> buildSeoData(Post post) {
        return Mono.zip(
            client.fetch(User.class, post.getSpec().getOwner()),
            findTag(post),
            settingConfigGetter.getBasicConfig(),
            systemInfoGetter.get()
        ).map(tuple -> {
            var user = tuple.getT1();
            var keywords = tuple.getT2();
            var config = tuple.getT3();
            var systemInfo = tuple.getT4();
            
            var author = Optional.of(user)
                .map(User::getSpec)
                .map(User.UserSpec::getDisplayName)
                .orElse(post.getSpec().getOwner());
                
            var postUrl = externalLinkProcessor.processLink(post.getStatus().getPermalink());
            var title = post.getSpec().getTitle();
            var description = generateDescription(post);
            // Debug: Log description source for troubleshooting
            System.out.println("DEBUG: Description generated - Length: " + description.length() + ", Source: " +
                (post.getSpec().getExcerpt() != null && !post.getSpec().getExcerpt().getRaw().trim().isEmpty() ? "excerpt" : "content"));
            var coverUrl = externalLinkProcessor.processLink(
                Optional.ofNullable(post.getSpec().getCover())
                    .filter(cover -> !cover.isBlank())
                    .orElse(config.getDefaultImage())
            );
            
            var publishInstant = post.getSpec().getPublishTime();
            var updateInstant = post.getStatus().getLastModifyTime();
            var zoneId = ZoneId.systemDefault();
            
            var baiduPubDate = formatDateTime(publishInstant, BAIDU_FORMATTER, zoneId);
            var baiduUpdDate = formatDateTime(updateInstant, BAIDU_FORMATTER, zoneId);
            var googlePubDate = formatDateTime(publishInstant, GOOGLE_FORMATTER, zoneId);
            var googleUpdDate = formatDateTime(updateInstant, GOOGLE_FORMATTER, zoneId);
            
            var siteName = systemInfo.getTitle();
            var siteLogo = externalLinkProcessor.processLink(systemInfo.getLogo());
            var siteKeywords = Optional.ofNullable(systemInfo.getSeo())
                .map(SystemInfo.SeoProp::getKeywords)
                .orElse("");
            
            var finalKeywords = keywords.isBlank() ? siteKeywords : keywords;

            // Get post content for structured data analysis
            // Note: Content access may not be available in current Halo API
            var content = "";

            return new SeoData(
                title, description, coverUrl, postUrl, author,
                baiduPubDate, baiduUpdDate, googlePubDate, googleUpdDate,
                siteName, siteLogo, finalKeywords, content
            );
        });
    }

    private Mono<Void> generateSeoTags(SeoData seoData, IModel model, IModelFactory modelFactory) {
        return settingConfigGetter.getBasicConfig()
            .map(config -> {
                var sb = new StringBuilder();
                
                // 使用if-else简化配置检查
                if (config.isEnableOGTimeFactor()) {
                    sb.append(genOGMeta(seoData));
                }
                if (config.isEnableMetaTimeFactor()) {
                    sb.append(genBytedanceMeta(seoData.baiduPubDate(), seoData.baiduUpdDate()));
                }
                if (config.isEnableBaiduTimeFactor()) {
                    sb.append(genBaiduScript(seoData.title(), seoData.postUrl(), seoData.baiduPubDate(), seoData.baiduUpdDate()));
                }
                if (config.isEnableStructuredData()) {
                    sb.append(genSchemaOrgScript(seoData));
                }
                if (config.isEnableCanonicalTag()) {
                    sb.append(genCanonicalTag(seoData.postUrl()));
                    sb.append("<!-- DEBUG: Canonical tag generated -->\n");
                } else {
                    sb.append("<!-- DEBUG: Canonical tag disabled -->\n");
                }
                if (config.isEnableTwitterCard()) {
                    sb.append(genTwitterCard(seoData));
                    sb.append("<!-- DEBUG: Twitter Card generated -->\n");
                } else {
                    sb.append("<!-- DEBUG: Twitter Card disabled -->\n");
                }
                if (config.isEnableMetaRobots()) {
                    sb.append(genMetaRobots(config.getRobotsIndex(), config.getRobotsFollow()));
                    sb.append("<!-- DEBUG: Meta Robots generated -->\n");
                } else {
                    sb.append("<!-- DEBUG: Meta Robots disabled -->\n");
                }

                // Enhanced Social Media Optimization
                if (config.isEnableEnhancedSocial()) {
                    sb.append(genEnhancedOGTags(seoData));
                    sb.append("<!-- DEBUG: Enhanced Social Media tags generated -->\n");
                } else {
                    sb.append("<!-- DEBUG: Enhanced Social Media tags disabled -->\n");
                }
                if (config.isEnableLinkedInTags()) {
                    sb.append(genLinkedInTags(seoData));
                    sb.append("<!-- DEBUG: LinkedIn tags generated -->\n");
                } else {
                    sb.append("<!-- DEBUG: LinkedIn tags disabled -->\n");
                }
                if (config.isEnableFacebookTags()) {
                    sb.append(genFacebookTags(seoData));
                    sb.append("<!-- DEBUG: Facebook tags generated -->\n");
                } else {
                    sb.append("<!-- DEBUG: Facebook tags disabled -->\n");
                }

                // Enhanced Structured Data
                if (config.isEnableFAQSchema() || config.isEnableHowToSchema()) {
                    var contentType = detectContentType(seoData.content(), config.getContentTypeDetection());
                    if ("faq".equals(contentType) && config.isEnableFAQSchema()) {
                        sb.append(genFAQSchema(seoData));
                        sb.append("<!-- DEBUG: FAQ Schema generated -->\n");
                    } else if ("howto".equals(contentType) && config.isEnableHowToSchema()) {
                        sb.append(genHowToSchema(seoData));
                        sb.append("<!-- DEBUG: How-To Schema generated -->\n");
                    }
                }
                
                model.add(modelFactory.createText(sb.toString()));
                return Mono.<Void>empty();
            })
            .then();
    }

    private Mono<String> findTag(Post post) {
        var tagNames = post.getSpec().getTags();
        if (tagNames == null || tagNames.isEmpty()) {
            return Mono.just("");
        }

        var listOptions = new ListOptions();
        listOptions.setFieldSelector(FieldSelector.of(
            QueryFactory.in("metadata.name", tagNames.toArray(new String[0]))
        ));

        return client.listAll(Tag.class, listOptions, Sort.by(Sort.Order.asc("metadata.name")))
            .map(tag -> Optional.ofNullable(tag.getSpec().getDisplayName())
                .orElse(tag.getMetadata().getName()))
            .collectList()
            .map(list -> String.join(",", list))
            .onErrorReturn("")
            .defaultIfEmpty("");
    }

    private String formatDateTime(java.time.Instant instant, DateTimeFormatter formatter, ZoneId zoneId) {
        return Optional.ofNullable(instant)
            .map(inst -> inst.atZone(zoneId).format(formatter))
            .orElse("");
    }

    private String generateDescription(Post post) {
        // First try to get excerpt
        var excerpt = Optional.ofNullable(post.getSpec().getExcerpt())
            .map(ex -> ex.getRaw())
            .filter(ex -> !ex.trim().isEmpty())
            .orElse(null);

        if (excerpt != null) {
            return excerpt;
        }

        // Fallback: generate description from post content
        // Note: In Halo, post content might not be directly accessible in PostSpec
        // This is a placeholder for when content access is available
        return Optional.ofNullable(post.getSpec())
            .map(spec -> {
                // Try to access content if available (this might need adjustment based on Halo API)
                try {
                    // For now, return a default description when excerpt is not available
                    // TODO: Implement proper content access when Halo API allows it
                    return "Article by " + post.getSpec().getOwner() + " - " + post.getSpec().getTitle();
                } catch (Exception e) {
                    return "";
                }
            })
            .orElse("");
    }

    private String extractSummaryFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        // Remove HTML tags
        var textContent = content.replaceAll("<[^>]*>", "").trim();

        // Remove extra whitespace
        textContent = textContent.replaceAll("\\s+", " ");

        // Take first 160 characters for SEO description
        if (textContent.length() > 160) {
            textContent = textContent.substring(0, 157) + "...";
        }

        return textContent;
    }

    private String genOGMeta(SeoData seoData) {
        return """
            <meta property="og:type" content="article"/>
            <meta property="og:title" content="%s"/>
            <meta property="og:description" content="%s"/>
            <meta property="og:image" content="%s"/>
            <meta property="og:url" content="%s"/>
            <meta property="og:release_date" content="%s"/>
            <meta property="og:modified_time" content="%s"/>
            <meta property="og:author" content="%s"/>
            """.formatted(
                seoData.title(), seoData.description(), seoData.coverUrl(), seoData.postUrl(),
                seoData.baiduPubDate(), seoData.baiduUpdDate(), seoData.author()
            );
    }

    private String genBytedanceMeta(String publishDate, String updateDate) {
        return """
            <meta property="bytedance:published_time" content="%s"/>
            <meta property="bytedance:updated_time" content="%s"/>
            """.formatted(publishDate, updateDate);
    }

    private String genBaiduScript(String title, String url, String publishDate, String updateDate) {
        return """
            <script type="application/ld+json">
            {
              "@context": "https://ziyuan.baidu.com/contexts/cambrian.jsonld",
              "@id": "%s",
              "title": "%s",
              "pubDate": "%s",
              "upDate": "%s"
            }
            </script>
            """.formatted(url, title, publishDate, updateDate);
    }

    private String genSchemaOrgScript(SeoData seoData) {
        return """
            <script type="application/ld+json">
            {
              "@context": "https://schema.org",
              "@type": "BlogPosting",
              "mainEntityOfPage": {
                "@type": "WebPage",
                "@id": "%s"
              },
              "headline": "%s",
              "description": "%s",
              "datePublished": "%s",
              "dateModified": "%s",
              "author": {
                "@type": "Person",
                "name": "%s"
              },
              "publisher": {
                "@type": "Organization",
                "name": "%s",
                "logo": {
                  "@type": "ImageObject",
                  "url": "%s"
                }
              },
              "image": "%s",
              "url": "%s",
              "keywords": "%s"
            }
            </script>
            """.formatted(
                seoData.postUrl(), seoData.title(), seoData.description(),
                seoData.googlePubDate(), seoData.googleUpdDate(), seoData.author(),
                seoData.siteName(), seoData.siteLogo(), seoData.coverUrl(),
                seoData.postUrl(), seoData.keywords()
            );
    }

    private String genCanonicalTag(String postUrl) {
        return """
            <link rel="canonical" href="%s"/>
            """.formatted(postUrl);
    }

    private String genTwitterCard(SeoData seoData) {
        return """
            <meta name="twitter:card" content="summary_large_image"/>
            <meta name="twitter:title" content="%s"/>
            <meta name="twitter:description" content="%s"/>
            <meta name="twitter:image" content="%s"/>
            <meta name="twitter:url" content="%s"/>
            <meta name="twitter:site" content="@%s"/>
            """.formatted(
                seoData.title(), seoData.description(), seoData.coverUrl(),
                seoData.postUrl(), seoData.siteName()
            );
    }

    private String genMetaRobots(String robotsIndex, String robotsFollow) {
        var robotsContent = robotsIndex + "," + robotsFollow;
        return """
            <meta name="robots" content="%s"/>
            """.formatted(robotsContent);
    }

    private String detectContentType(String content, String detectionMode) {
        if ("disabled".equals(detectionMode)) {
            return "none";
        }

        if ("manual".equals(detectionMode)) {
            // For manual mode, we'd need additional metadata - for now return none
            return "none";
        }

        // Auto-detection logic
        if (content == null || content.isEmpty()) {
            return "none";
        }

        var lowerContent = content.toLowerCase();

        // FAQ detection patterns (English + Indonesian)
        if (lowerContent.contains("faq") ||
            lowerContent.contains("frequently asked questions") ||
            lowerContent.contains("question:") ||
            lowerContent.contains("answer:") ||
            lowerContent.contains("pertanyaan") ||
            lowerContent.contains("jawaban") ||
            lowerContent.contains("tanya jawab") ||
            lowerContent.contains("q&a")) {
            return "faq";
        }

        // How-To detection patterns (English + Indonesian)
        if (lowerContent.contains("how to") ||
            lowerContent.contains("tutorial") ||
            lowerContent.contains("guide") ||
            lowerContent.contains("step") ||
            lowerContent.contains("步骤") ||
            lowerContent.contains("cara") ||
            lowerContent.contains("panduan") ||
            lowerContent.contains("langkah") ||
            lowerContent.contains("bagaimana")) {
            return "howto";
        }

        return "none";
    }

    private String genFAQSchema(SeoData seoData) {
        // Extract Q&A pairs from content (simplified implementation)
        var questions = extractQuestions(seoData.content());

        if (questions.isEmpty()) {
            return "";
        }

        var faqEntities = questions.stream()
            .map(q -> """
                {
                  "@type": "Question",
                  "name": "%s",
                  "acceptedAnswer": {
                    "@type": "Answer",
                    "text": "%s"
                  }
                }""".formatted(q.question(), q.answer()))
            .collect(Collectors.joining(","));

        return """
            <script type="application/ld+json">
            {
              "@context": "https://schema.org",
              "@type": "FAQPage",
              "mainEntity": [%s]
            }
            </script>
            """.formatted(faqEntities);
    }

    private String genHowToSchema(SeoData seoData) {
        // Extract steps from content (simplified implementation)
        var steps = extractSteps(seoData.content());

        if (steps.isEmpty()) {
            return "";
        }

        var stepEntities = steps.stream()
            .map(step -> """
                {
                  "@type": "HowToStep",
                  "text": "%s"
                }""".formatted(step))
            .collect(Collectors.joining(","));

        return """
            <script type="application/ld+json">
            {
              "@context": "https://schema.org",
              "@type": "HowTo",
              "name": "%s",
              "description": "%s",
              "step": [%s]
            }
            </script>
            """.formatted(seoData.title(), seoData.description(), stepEntities);
    }

    private List<QuestionAnswer> extractQuestions(String content) {
        var questions = new ArrayList<QuestionAnswer>();
        if (content == null) return questions;

        // Simple pattern matching for Q&A
        var lines = content.split("\n");
        String currentQuestion = null;

        for (var line : lines) {
            var trimmed = line.trim();
            if (trimmed.toLowerCase().startsWith("q:") ||
                trimmed.toLowerCase().startsWith("question:") ||
                trimmed.toLowerCase().startsWith("pertanyaan:")) {
                currentQuestion = trimmed.substring(trimmed.indexOf(":") + 1).trim();
            } else if (currentQuestion != null &&
                      (trimmed.toLowerCase().startsWith("a:") ||
                       trimmed.toLowerCase().startsWith("answer:") ||
                       trimmed.toLowerCase().startsWith("jawaban:"))) {
                var answer = trimmed.substring(trimmed.indexOf(":") + 1).trim();
                questions.add(new QuestionAnswer(currentQuestion, answer));
                currentQuestion = null;
            }
        }

        return questions;
    }

    private List<String> extractSteps(String content) {
        var steps = new ArrayList<String>();
        if (content == null) return steps;

        var lines = content.split("\n");
        for (var line : lines) {
            var trimmed = line.trim();
            if (trimmed.matches("\\d+\\..*") || // 1. Step text
                trimmed.toLowerCase().startsWith("step") ||
                trimmed.toLowerCase().startsWith("langkah")) {
                steps.add(trimmed);
            }
        }

        return steps;
    }

    private String genEnhancedOGTags(SeoData seoData) {
        return """
            <meta property="og:site_name" content="%s"/>
            <meta property="og:locale" content="en_US"/>
            <meta property="og:image:width" content="1200"/>
            <meta property="og:image:height" content="630"/>
            <meta property="og:image:alt" content="%s"/>
            <meta property="article:author" content="%s"/>
            <meta property="article:published_time" content="%s"/>
            <meta property="article:modified_time" content="%s"/>
            <meta property="article:section" content="Blog"/>
            """.formatted(
                seoData.siteName(), seoData.title(), seoData.author(),
                seoData.googlePubDate(), seoData.googleUpdDate()
            );
    }

    private String genLinkedInTags(SeoData seoData) {
        return """
            <meta property="og:title" content="%s"/>
            <meta property="og:description" content="%s"/>
            <meta property="og:image" content="%s"/>
            <meta property="og:url" content="%s"/>
            <meta property="og:type" content="article"/>
            """.formatted(
                seoData.title(), seoData.description(), seoData.coverUrl(), seoData.postUrl()
            );
    }

    private String genFacebookTags(SeoData seoData) {
        return """
            <meta property="fb:app_id" content=""/>
            <meta property="og:title" content="%s"/>
            <meta property="og:description" content="%s"/>
            <meta property="og:image" content="%s"/>
            <meta property="og:url" content="%s"/>
            <meta property="og:type" content="article"/>
            <meta property="article:author" content="%s"/>
            <meta property="article:published_time" content="%s"/>
            """.formatted(
                seoData.title(), seoData.description(), seoData.coverUrl(),
                seoData.postUrl(), seoData.author(), seoData.googlePubDate()
            );
    }

    private record QuestionAnswer(String question, String answer) {}
}

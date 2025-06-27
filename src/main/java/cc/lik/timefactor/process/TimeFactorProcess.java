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
import run.halo.app.infra.SystemInfoGetter;
import run.halo.app.theme.dialect.TemplateHeadProcessor;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TimeFactorProcess implements TemplateHeadProcessor {
    private static final DateTimeFormatter BAIDU_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter GOOGLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final ReactiveExtensionClient client;
    private final SettingConfigGetter settingConfigGetter;
    private final ExternalLinkProcessor externalLinkProcessor;
    private final SystemInfoGetter systemInfoGetter;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler handler) {
        final IModelFactory modelFactory = context.getModelFactory();
        var name = context.getVariable("name") == null ? null : context.getVariable("name").toString();
        if (name == null || name.isEmpty()) return Mono.empty();

        return client.fetch(Post.class, name)
            .flatMap(post -> Mono.zip(
                client.fetch(User.class, post.getSpec().getOwner()),
                findTag(post),
                settingConfigGetter.getBasicConfig(),
                systemInfoGetter.get()
            ).flatMap(tuple -> {
                var user = tuple.getT1();
                var keywords = tuple.getT2();
                var config = tuple.getT3();
                var systemInfo = tuple.getT4();
                var author =
                    user.getSpec() != null ? user.getSpec().getDisplayName() : post.getSpec().getOwner();
                var postUrl = externalLinkProcessor.processLink(post.getStatus().getPermalink());
                var title = post.getSpec().getTitle();
                var description = post.getSpec().getExcerpt().getRaw();
                var coverUrl = externalLinkProcessor.processLink(
                    post.getSpec().getCover() != null && !post.getSpec().getCover().isBlank() 
                        ? post.getSpec().getCover() 
                        : config.getDefaultImage()
                );
                var publishInstant = post.getSpec().getPublishTime();
                var updateInstant = post.getStatus().getLastModifyTime();
                var baiduPubDate = publishInstant != null ? publishInstant.atZone(ZoneId.systemDefault()).format(BAIDU_FORMATTER) : "";
                var baiduUpdDate = updateInstant != null ? updateInstant.atZone(ZoneId.systemDefault()).format(BAIDU_FORMATTER) : "";
                var googlePubDate = publishInstant != null ? publishInstant.atZone(ZoneId.systemDefault()).format(GOOGLE_FORMATTER) : "";
                var googleUpdDate = updateInstant != null ? updateInstant.atZone(ZoneId.systemDefault()).format(GOOGLE_FORMATTER) : "";
                var siteName = systemInfo.getTitle();
                var siteLogo = externalLinkProcessor.processLink(systemInfo.getLogo());
                var siteKeywords = systemInfo.getSeo() != null ? systemInfo.getSeo().getKeywords() : "";
                // 优先用文章标签，否则用全局SEO keywords
                var finalKeywords = !keywords.isBlank() ? keywords : siteKeywords;
                var sb = new StringBuilder();
                if (config.isEnableOGTimeFactor()) {
                    sb.append(genOGMeta(title, description, coverUrl, postUrl, baiduPubDate,
                        baiduUpdDate, author));
                }
                if (config.isEnableMetaTimeFactor()) {
                    sb.append(genBytedanceMeta(baiduPubDate, baiduUpdDate));
                }
                if (config.isEnableBaiduTimeFactor()) {
                    sb.append(genBaiduScript(title, postUrl, baiduPubDate, baiduUpdDate));
                }
                if (config.isEnableStructuredData()) {
                    sb.append(genSchemaOrgScript(title, description, coverUrl, author, siteName, siteLogo,
                        postUrl, googlePubDate, googleUpdDate, finalKeywords));
                }
                model.add(modelFactory.createText(sb.toString()));
                return Mono.empty();
            }));
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
            .map(tag -> {
                var displayName = tag.getSpec().getDisplayName();
                return displayName != null ? displayName : tag.getMetadata().getName();
            })
            .collectList()
            .map(list -> String.join(",", list))
            .onErrorReturn("")
            .defaultIfEmpty("");
    }

    private String genOGMeta(String title, String description, String coverUrl, String url, String publishDate, String updateDate, String author) {
        return String.format(
            """
                <meta property="og:type" content="article"/>
                <meta property="og:title" content="%s"/>
                <meta property="og:description" content="%s"/>
                <meta property="og:image" content="%s"/>
                <meta property="og:url" content="%s"/>
                <meta property="og:release_date" content="%s"/>
                <meta property="og:modified_time" content="%s"/>
                <meta property="og:author" content="%s"/>
                """,
            title, description, coverUrl, url, publishDate, updateDate, author
        );
    }

    private String genBytedanceMeta(String publishDate, String updateDate) {
        return String.format(
            "<meta property=\"bytedance:published_time\" content=\"%s\"/>" +
            "<meta property=\"bytedance:updated_time\" content=\"%s\"/>",
            publishDate, updateDate
        );
    }

    private String genBaiduScript(String title, String url, String publishDate, String updateDate) {
        return String.format(
            """
                <script type="application/ld+json">\
                {
                  "@context": "https://ziyuan.baidu.com/contexts/cambrian.jsonld",
                  "@id": "%s",
                  "title": "%s",
                  "pubDate": "%s",
                  "upDate": "%s"
                }\
                </script>""",
            url, title, publishDate, updateDate
        );
    }

    private String genSchemaOrgScript(String title, String description, String coverUrl, String author, String siteName, String siteLogo, String url, String publishDate, String updateDate, String keywords) {
        return String.format(
            """
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
            """,
            url, title, description, publishDate, updateDate, author, siteName, siteLogo, coverUrl, url, keywords
        );
    }
}
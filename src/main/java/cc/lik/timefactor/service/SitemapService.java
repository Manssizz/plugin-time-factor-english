package cc.lik.timefactor.service;

import cc.lik.timefactor.service.SettingConfigGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Tag;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.infra.ExternalLinkProcessor;
import run.halo.app.infra.SystemInfoGetter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SitemapService {
    private static final DateTimeFormatter SITEMAP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ReactiveExtensionClient client;
    private final SettingConfigGetter settingConfigGetter;
    private final ExternalLinkProcessor externalLinkProcessor;
    private final SystemInfoGetter systemInfoGetter;

    public Mono<String> generateSitemapXml() {
        return settingConfigGetter.getBasicConfig()
            .flatMap(config -> {
                if (!config.isEnableXmlSitemap()) {
                    return Mono.just("");
                }

                return Mono.zip(
                    generatePostsSitemap(config),
                    generateCategoriesSitemap(config),
                    generateTagsSitemap(config),
                    systemInfoGetter.get()
                ).map(tuple -> {
                    var postsXml = tuple.getT1();
                    var categoriesXml = tuple.getT2();
                    var tagsXml = tuple.getT3();
                    var systemInfo = tuple.getT4();

                    var siteUrl = externalLinkProcessor.processLink(systemInfo.getUrl().toString());

                    return """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                        %s
                        %s
                        %s
                        </urlset>
                        """.formatted(postsXml, categoriesXml, tagsXml);
                });
            });
    }

    private Mono<String> generatePostsSitemap(SettingConfigGetter.BasicConfig config) {
        var listOptions = new ListOptions();
        listOptions.setFieldSelector(FieldSelector.of(
            QueryFactory.equal("spec.publishPhase", "published")
        ));

        return client.listAll(Post.class, listOptions, null)
            .map(post -> {
                var postUrl = externalLinkProcessor.processLink(post.getStatus().getPermalink());
                var lastMod = formatLastMod(post.getStatus().getLastModifyTime());
                var priority = config.getSitemapDefaultPriority();
                var changefreq = config.getSitemapUpdateFrequency();

                return """
                    <url>
                      <loc>%s</loc>
                      <lastmod>%s</lastmod>
                      <changefreq>%s</changefreq>
                      <priority>%s</priority>
                    </url>
                    """.formatted(postUrl, lastMod, changefreq, priority);
            })
            .collectList()
            .map(urls -> String.join("", urls))
            .defaultIfEmpty("");
    }

    private Mono<String> generateCategoriesSitemap(SettingConfigGetter.BasicConfig config) {
        if (!config.isIncludeCategoriesInSitemap()) {
            return Mono.just("");
        }

        return systemInfoGetter.get()
            .flatMap(systemInfo -> {
                var baseUrl = systemInfo.getUrl().toString();
                return client.listAll(Category.class, null, null)
                    .map(category -> {
                        var categoryUrl = externalLinkProcessor.processLink(baseUrl + "/categories/" + category.getMetadata().getName());
                        var lastMod = formatLastMod(category.getMetadata().getCreationTimestamp());
                        var priority = "0.3"; // Lower priority for categories
                        var changefreq = "weekly";

                        return """
                            <url>
                              <loc>%s</loc>
                              <lastmod>%s</lastmod>
                              <changefreq>%s</changefreq>
                              <priority>%s</priority>
                            </url>
                            """.formatted(categoryUrl, lastMod, changefreq, priority);
                    })
                    .collectList()
                    .map(urls -> String.join("", urls));
            })
            .defaultIfEmpty("");
    }

    private Mono<String> generateTagsSitemap(SettingConfigGetter.BasicConfig config) {
        if (!config.isIncludeTagsInSitemap()) {
            return Mono.just("");
        }

        return systemInfoGetter.get()
            .flatMap(systemInfo -> {
                var baseUrl = systemInfo.getUrl().toString();
                return client.listAll(Tag.class, null, null)
                    .map(tag -> {
                        var tagUrl = externalLinkProcessor.processLink(baseUrl + "/tags/" + tag.getMetadata().getName());
                        var lastMod = formatLastMod(tag.getMetadata().getCreationTimestamp());
                        var priority = "0.3"; // Lower priority for tags
                        var changefreq = "weekly";

                        return """
                            <url>
                              <loc>%s</loc>
                              <lastmod>%s</lastmod>
                              <changefreq>%s</changefreq>
                              <priority>%s</priority>
                            </url>
                            """.formatted(tagUrl, lastMod, changefreq, priority);
                    })
                    .collectList()
                    .map(urls -> String.join("", urls));
            })
            .defaultIfEmpty("");
    }

    private String formatLastMod(java.time.Instant instant) {
        return Optional.ofNullable(instant)
            .map(inst -> inst.atZone(ZoneId.systemDefault()).format(SITEMAP_DATE_FORMAT))
            .orElse(java.time.LocalDate.now().format(SITEMAP_DATE_FORMAT));
    }
}

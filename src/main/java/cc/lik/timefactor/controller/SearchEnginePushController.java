package cc.lik.timefactor.controller;

import cc.lik.timefactor.service.SearchEnginePushService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apis/api.plugin.halo.run/v1alpha1/timefactor")
public class SearchEnginePushController {

    private final SearchEnginePushService searchEnginePushService;

    public SearchEnginePushController(SearchEnginePushService searchEnginePushService) {
        this.searchEnginePushService = searchEnginePushService;
    }

    @PostMapping("/push-to-search-engines")
    public Mono<ResponseEntity<String>> pushToSearchEngines(
            @RequestParam String url,
            @RequestParam(required = false) String sitemapUrl) {

        return searchEnginePushService.pushToSearchEngines(url, sitemapUrl)
            .then(Mono.just(ResponseEntity.ok("Successfully initiated push to search engines for URL: " + url)))
            .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                .body("Failed to push to search engines: " + e.getMessage())));
    }

    @PostMapping("/push-sitemap")
    public Mono<ResponseEntity<String>> pushSitemap(@RequestParam String sitemapUrl) {
        return searchEnginePushService.pushToSearchEngines(null, sitemapUrl)
            .then(Mono.just(ResponseEntity.ok("Successfully initiated sitemap push to search engines: " + sitemapUrl)))
            .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                .body("Failed to push sitemap to search engines: " + e.getMessage())));
    }
}

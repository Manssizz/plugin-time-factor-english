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
    public ResponseEntity<String> pushToSearchEngines(
            @RequestParam String url,
            @RequestParam(required = false) String sitemapUrl) {

        try {
            searchEnginePushService.pushToSearchEngines(url, sitemapUrl);
            return ResponseEntity.ok("Successfully initiated push to search engines for URL: " + url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to push to search engines: " + e.getMessage());
        }
    }

    @PostMapping("/push-sitemap")
    public ResponseEntity<String> pushSitemap(@RequestParam String sitemapUrl) {
        try {
            searchEnginePushService.pushToSearchEngines(null, sitemapUrl);
            return ResponseEntity.ok("Successfully initiated sitemap push to search engines: " + sitemapUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to push sitemap to search engines: " + e.getMessage());
        }
    }
}

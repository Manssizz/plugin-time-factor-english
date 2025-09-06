package cc.lik.timefactor.controller;

import cc.lik.timefactor.service.AnalyticsIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/apis/api.plugin.halo.run/v1alpha1/timefactor/analytics")
public class AnalyticsController {

    private final AnalyticsIntegrationService analyticsService;

    public AnalyticsController(AnalyticsIntegrationService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/google-analytics-code")
    public Mono<ResponseEntity<String>> getGoogleAnalyticsCode() {
        return analyticsService.getGoogleAnalyticsSetupCode()
            .map(code -> ResponseEntity.ok(code))
            .defaultIfEmpty(ResponseEntity.ok(""));
    }

    @GetMapping("/search-console-verification")
    public Mono<ResponseEntity<String>> getSearchConsoleVerification() {
        return analyticsService.getSearchConsoleVerification()
            .map(code -> ResponseEntity.ok(code))
            .defaultIfEmpty(ResponseEntity.ok(""));
    }

    @GetMapping("/core-web-vitals")
    public Mono<ResponseEntity<Map<String, Object>>> getCoreWebVitals(@RequestParam String url) {
        return analyticsService.getCoreWebVitals(url)
            .map(data -> ResponseEntity.ok(data))
            .defaultIfEmpty(ResponseEntity.ok(Map.of()));
    }

    @GetMapping("/indexing-status")
    public Mono<ResponseEntity<Map<String, Object>>> getIndexingStatus() {
        return analyticsService.getIndexingStatus()
            .map(status -> ResponseEntity.ok(status))
            .defaultIfEmpty(ResponseEntity.ok(Map.of()));
    }

    @GetMapping("/seo-performance")
    public Mono<ResponseEntity<Map<String, Object>>> getSEOPerformanceMetrics() {
        return analyticsService.getSEOPerformanceMetrics()
            .map(metrics -> ResponseEntity.ok(metrics))
            .defaultIfEmpty(ResponseEntity.ok(Map.of()));
    }
}

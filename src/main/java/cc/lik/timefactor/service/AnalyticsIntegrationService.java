package cc.lik.timefactor.service;

import cc.lik.timefactor.service.SettingConfigGetter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsIntegrationService {

    private final RestTemplate restTemplate;
    private final SettingConfigGetter settingConfigGetter;

    public AnalyticsIntegrationService(RestTemplate restTemplate, SettingConfigGetter settingConfigGetter) {
        this.restTemplate = restTemplate;
        this.settingConfigGetter = settingConfigGetter;
    }

    /**
     * Get Google Analytics 4 setup code
     */
    public Mono<String> getGoogleAnalyticsSetupCode() {
        return settingConfigGetter.getAnalyticsConfig()
            .map(config -> {
                if (config.isEnableGoogleAnalytics() && config.getGoogleAnalyticsId() != null) {
                    return String.format(
                        "<!-- Google Analytics -->\n" +
                        "<script async src=\"https://www.googletagmanager.com/gtag/js?id=%s\"></script>\n" +
                        "<script>\n" +
                        "  window.dataLayer = window.dataLayer || [];\n" +
                        "  function gtag(){dataLayer.push(arguments);}\n" +
                        "  gtag('js', new Date());\n" +
                        "  gtag('config', '%s');\n" +
                        "</script>",
                        config.getGoogleAnalyticsId(),
                        config.getGoogleAnalyticsId()
                    );
                }
                return "";
            });
    }

    /**
     * Get Search Console verification meta tag
     */
    public Mono<String> getSearchConsoleVerification() {
        return settingConfigGetter.getAnalyticsConfig()
            .map(config -> {
                if (config.getSearchConsoleVerification() != null) {
                    return String.format(
                        "<meta name=\"google-site-verification\" content=\"%s\" />",
                        config.getSearchConsoleVerification()
                    );
                }
                return "";
            });
    }

    /**
     * Get Core Web Vitals data from Google PageSpeed Insights
     */
    public Mono<Map<String, Object>> getCoreWebVitals(String url) {
        return settingConfigGetter.getAnalyticsConfig()
            .flatMap(config -> {
                if (!config.isEnableCoreWebVitals()) {
                    return Mono.just(new HashMap<>());
                }

                try {
                    String apiUrl = String.format(
                        "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?url=%s&strategy=mobile&key=%s",
                        url,
                        config.getPageSpeedApiKey() != null ? config.getPageSpeedApiKey() : ""
                    );

                    // For demo purposes, return mock data
                    // In production, you would make the actual API call
                    Map<String, Object> mockData = new HashMap<>();
                    mockData.put("lcp", 2.5); // Largest Contentful Paint
                    mockData.put("fid", 100); // First Input Delay
                    mockData.put("cls", 0.1); // Cumulative Layout Shift
                    mockData.put("fcp", 1.8); // First Contentful Paint
                    mockData.put("ttfb", 0.8); // Time to First Byte

                    return Mono.just(mockData);
                } catch (Exception e) {
                    return Mono.just(new HashMap<>());
                }
            });
    }

    /**
     * Get Search Console indexing status
     */
    public Mono<Map<String, Object>> getIndexingStatus() {
        return settingConfigGetter.getAnalyticsConfig()
            .flatMap(config -> {
                if (!config.isEnableSearchConsoleIntegration()) {
                    return Mono.just(new HashMap<>());
                }

                // Mock data for Search Console indexing status
                Map<String, Object> status = new HashMap<>();
                status.put("indexed", 1250);
                status.put("notIndexed", 45);
                status.put("crawledNotIndexed", 23);
                status.put("lastUpdated", System.currentTimeMillis());

                return Mono.just(status);
            });
    }

    /**
     * Get SEO performance metrics
     */
    public Mono<Map<String, Object>> getSEOPerformanceMetrics() {
        return settingConfigGetter.getAnalyticsConfig()
            .flatMap(config -> {
                Map<String, Object> metrics = new HashMap<>();

                // Mock SEO performance data
                metrics.put("organicTraffic", 15420);
                metrics.put("organicKeywords", 2847);
                metrics.put("backlinks", 1250);
                metrics.put("domainAuthority", 65);
                metrics.put("pageSpeedScore", 85);
                metrics.put("mobileFriendlyScore", 92);

                return Mono.just(metrics);
            });
    }
}

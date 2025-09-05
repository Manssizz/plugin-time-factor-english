package cc.lik.seotoolset.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service for checking basic website information including crawling status and robots.txt
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebsiteInfoService {

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    public record WebsiteInfo(
        boolean isReachable,
        int responseCode,
        String robotsTxtContent,
        boolean hasRobotsTxt,
        long responseTime
    ) {}

    /**
     * Check website crawling status and robots.txt
     */
    public Mono<WebsiteInfo> checkWebsiteInfo(String baseUrl) {
        return Mono.fromCallable(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // Check main site
                HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

                HttpResponse<String> mainResponse = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());
                boolean isReachable = mainResponse.statusCode() >= 200 && mainResponse.statusCode() < 400;

                // Check robots.txt
                String robotsUrl = baseUrl.endsWith("/") ? baseUrl + "robots.txt" : baseUrl + "/robots.txt";
                String robotsContent = "";
                boolean hasRobots = false;

                try {
                    HttpRequest robotsRequest = HttpRequest.newBuilder()
                        .uri(URI.create(robotsUrl))
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();

                    HttpResponse<String> robotsResponse = httpClient.send(robotsRequest, HttpResponse.BodyHandlers.ofString());
                    if (robotsResponse.statusCode() == 200) {
                        robotsContent = robotsResponse.body();
                        hasRobots = true;
                    }
                } catch (Exception e) {
                    log.debug("Failed to fetch robots.txt: {}", e.getMessage());
                }

                long responseTime = System.currentTimeMillis() - startTime;

                return new WebsiteInfo(isReachable, mainResponse.statusCode(), robotsContent, hasRobots, responseTime);

            } catch (Exception e) {
                log.error("Failed to check website info for {}: {}", baseUrl, e.getMessage());
                return new WebsiteInfo(false, 0, "", false, System.currentTimeMillis() - startTime);
            }
        });
    }

    /**
     * Validate robots.txt content
     */
    public Mono<String> validateRobotsTxt(String robotsTxt) {
        return Mono.fromCallable(() -> {
            if (robotsTxt == null || robotsTxt.trim().isEmpty()) {
                return "Empty robots.txt file";
            }

            StringBuilder issues = new StringBuilder();

            // Check for User-agent directive
            if (!robotsTxt.contains("User-agent:")) {
                issues.append("Missing User-agent directive. ");
            }

            // Check for Disallow directive
            if (!robotsTxt.contains("Disallow:")) {
                issues.append("Missing Disallow directive. ");
            }

            // Check for Sitemap directive
            if (!robotsTxt.contains("Sitemap:")) {
                issues.append("Missing Sitemap directive. ");
            }

            return issues.length() > 0 ? issues.toString().trim() : "Valid robots.txt";
        });
    }
}

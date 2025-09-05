package cc.lik.seotoolset.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service for recording crawler request logs and detecting abnormal requests.
 */
@Slf4j
@Service
public class CrawlerLogService {

    private final List<CrawlerRequestLog> logs = new CopyOnWriteArrayList<>();

    public record CrawlerRequestLog(
        String ip,
        String userAgent,
        Instant timestamp,
        String url,
        boolean isAbnormal
    ) {}

    /**
     * Record a crawler request log.
     */
    public Mono<Void> recordRequest(String ip, String userAgent, String url) {
        return Mono.fromRunnable(() -> {
            boolean abnormal = detectAbnormal(ip, userAgent, url);
            CrawlerRequestLog logEntry = new CrawlerRequestLog(ip, userAgent, Instant.now(), url, abnormal);
            logs.add(logEntry);
            if (abnormal) {
                log.warn("Abnormal crawler request detected: {}", logEntry);
            }
        });
    }

    /**
     * Detect abnormal requests based on simple heuristics.
     */
    private boolean detectAbnormal(String ip, String userAgent, String url) {
        // Example heuristic: empty user agent or suspicious URL patterns
        if (userAgent == null || userAgent.isBlank()) {
            return true;
        }
        if (url.contains("eval(") || url.contains("base64")) {
            return true;
        }
        // Add more heuristics as needed
        return false;
    }

    /**
     * Get all crawler request logs.
     */
    public Mono<List<CrawlerRequestLog>> getAllLogs() {
        return Mono.just(new ArrayList<>(logs));
    }
}

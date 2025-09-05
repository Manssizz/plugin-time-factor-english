package cc.lik.seotoolset.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for analyzing pages to check SEO-related optimization items.
 */
@Slf4j
@Service
public class PageAnalysisService {

    /**
     * Analyze the given page content and return a map of SEO issues or suggestions.
     * For simplicity, this example checks for presence of title and meta description.
     */
    public Mono<Map<String, String>> analyzePage(String pageContent) {
        return Mono.fromCallable(() -> {
            Map<String, String> results = new HashMap<>();

            if (pageContent == null || pageContent.isEmpty()) {
                results.put("error", "Page content is empty");
                return results;
            }

            if (!pageContent.contains("<title>")) {
                results.put("title", "Missing <title> tag");
            }

            if (!pageContent.contains("<meta name=\"description\"")) {
                results.put("metaDescription", "Missing meta description tag");
            }

            // Additional SEO checks can be added here

            if (results.isEmpty()) {
                results.put("status", "Page passed basic SEO checks");
            }

            return results;
        });
    }
}

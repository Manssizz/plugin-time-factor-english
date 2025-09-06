package cc.lik.timefactor.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentAnalysisService {

    public ContentAnalysisResult analyzeContent(String content, String title, String keywords) {
        if (content == null || content.trim().isEmpty()) {
            return new ContentAnalysisResult(0, 0.0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        // Clean content
        String cleanContent = cleanContent(content);
        String[] words = cleanContent.split("\\s+");
        int wordCount = words.length;

        // Calculate readability score
        double readabilityScore = calculateFleschKincaidReadability(cleanContent);

        // Analyze keyword density
        List<KeywordDensity> keywordDensity = analyzeKeywordDensity(cleanContent, keywords);

        // Generate internal linking suggestions
        List<String> linkingSuggestions = generateInternalLinkingSuggestions(cleanContent, title);

        // Generate content optimization suggestions
        List<String> optimizationSuggestions = generateContentOptimizationSuggestions(wordCount, cleanContent, title);

        return new ContentAnalysisResult(wordCount, readabilityScore, keywordDensity,
                                       linkingSuggestions, optimizationSuggestions);
    }

    private String cleanContent(String content) {
        // Remove HTML tags
        String clean = content.replaceAll("<[^>]*>", "");

        // Remove extra whitespace
        clean = clean.replaceAll("\\s+", " ").trim();

        return clean;
    }

    private double calculateFleschKincaidReadability(String content) {
        String[] sentences = content.split("[.!?]+");
        String[] words = content.split("\\s+");
        String[] syllables = content.split("[aeiouyAEIOUY]+");

        int sentenceCount = Math.max(1, sentences.length);
        int wordCount = Math.max(1, words.length);
        int syllableCount = Math.max(1, syllables.length);

        // Flesch-Kincaid Grade Level formula
        double gradeLevel = 0.39 * (wordCount / (double) sentenceCount) +
                           11.8 * (syllableCount / (double) wordCount) - 15.59;

        // Convert to readability score (higher is better)
        double readabilityScore = Math.max(0, Math.min(100, 100 - gradeLevel * 5));

        return Math.round(readabilityScore * 100.0) / 100.0;
    }

    private List<KeywordDensity> analyzeKeywordDensity(String content, String keywords) {
        List<KeywordDensity> results = new ArrayList<>();
        if (keywords == null || keywords.trim().isEmpty()) {
            return results;
        }

        String[] keywordArray = keywords.split(",");
        String lowerContent = content.toLowerCase();
        String[] words = lowerContent.split("\\s+");
        int totalWords = words.length;

        for (String keyword : keywordArray) {
            String cleanKeyword = keyword.trim().toLowerCase();
            if (cleanKeyword.isEmpty()) continue;

            int count = 0;
            for (String word : words) {
                if (word.contains(cleanKeyword)) {
                    count++;
                }
            }

            double density = totalWords > 0 ? (count / (double) totalWords) * 100 : 0;
            String recommendation = getKeywordDensityRecommendation(density);

            results.add(new KeywordDensity(cleanKeyword, count, density, recommendation));
        }

        return results.stream()
                .sorted((a, b) -> Double.compare(b.density, a.density))
                .collect(Collectors.toList());
    }

    private String getKeywordDensityRecommendation(double density) {
        if (density < 0.5) {
            return "Consider increasing keyword usage for better SEO";
        } else if (density > 3.0) {
            return "Keyword density too high - may be considered keyword stuffing";
        } else {
            return "Good keyword density";
        }
    }

    private List<String> generateInternalLinkingSuggestions(String content, String title) {
        List<String> suggestions = new ArrayList<>();
        String lowerContent = content.toLowerCase();

        // Common topics that might benefit from internal linking
        Map<String, String> topicKeywords = Map.of(
            "seo", "SEO best practices",
            "marketing", "digital marketing",
            "content", "content creation",
            "social", "social media",
            "analytics", "web analytics",
            "optimization", "website optimization"
        );

        for (Map.Entry<String, String> entry : topicKeywords.entrySet()) {
            if (lowerContent.contains(entry.getKey()) && !title.toLowerCase().contains(entry.getKey())) {
                suggestions.add("Consider linking to your " + entry.getValue() + " guide");
            }
        }

        return suggestions;
    }

    private List<String> generateContentOptimizationSuggestions(int wordCount, String content, String title) {
        List<String> suggestions = new ArrayList<>();

        // Word count suggestions
        if (wordCount < 300) {
            suggestions.add("Content is too short. Aim for at least 300 words for better SEO");
        } else if (wordCount > 2000) {
            suggestions.add("Content is quite long. Consider breaking it into multiple parts");
        }

        // Title optimization
        if (title.length() < 30) {
            suggestions.add("Title is too short. Aim for 30-60 characters for better click-through rates");
        } else if (title.length() > 60) {
            suggestions.add("Title is too long. Consider shortening for better display in search results");
        }

        // Content structure
        String[] paragraphs = content.split("\n\n");
        if (paragraphs.length < 3) {
            suggestions.add("Add more paragraphs to improve content structure and readability");
        }

        // Heading suggestions
        if (!content.contains("#") && !content.contains("##")) {
            suggestions.add("Consider adding headings (H2, H3) to improve content structure");
        }

        return suggestions;
    }

    public record ContentAnalysisResult(
        int wordCount,
        double readabilityScore,
        List<KeywordDensity> keywordDensity,
        List<String> linkingSuggestions,
        List<String> optimizationSuggestions
    ) {}

    public record KeywordDensity(
        String keyword,
        int count,
        double density,
        String recommendation
    ) {}
}

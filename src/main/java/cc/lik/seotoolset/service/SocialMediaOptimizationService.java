package cc.lik.seotoolset.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for social media optimization (SMO) including OpenGraph, Twitter Cards, and WeChat/QQ sharing.
 */
@Slf4j
@Service
public class SocialMediaOptimizationService {

    /**
     * Generate OpenGraph meta tags.
     */
    public Mono<String> generateOpenGraphTags(String title, String description, String imageUrl, String url, String releaseDate, String modifiedTime, String author) {
        return Mono.fromCallable(() -> {
            return String.format("""
                <meta property="og:type" content="article"/>
                <meta property="og:title" content="%s"/>
                <meta property="og:description" content="%s"/>
                <meta property="og:image" content="%s"/>
                <meta property="og:url" content="%s"/>
                <meta property="og:release_date" content="%s"/>
                <meta property="og:modified_time" content="%s"/>
                <meta property="og:author" content="%s"/>
                """, title, description, imageUrl, url, releaseDate, modifiedTime, author);
        });
    }

    /**
     * Generate Twitter Card meta tags.
     */
    public Mono<String> generateTwitterCardTags(String title, String description, String imageUrl) {
        return Mono.fromCallable(() -> {
            return String.format("""
                <meta name="twitter:card" content="summary_large_image"/>
                <meta name="twitter:title" content="%s"/>
                <meta name="twitter:description" content="%s"/>
                <meta name="twitter:image" content="%s"/>
                """, title, description, imageUrl);
        });
    }

    /**
     * Generate WeChat/QQ sharing optimization tags.
     */
    public Mono<String> generateWeChatTags(String title, String description, String imageUrl) {
        return Mono.fromCallable(() -> {
            return String.format("""
                <meta property="og:title" content="%s"/>
                <meta property="og:description" content="%s"/>
                <meta property="og:image" content="%s"/>
                <meta name="format-detection" content="telephone=no"/>
                """, title, description, imageUrl);
        });
    }
}

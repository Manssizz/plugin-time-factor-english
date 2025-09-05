package cc.lik.timefactor.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicConfig> getBasicConfig();

    @Data
    class BasicConfig {
        public static final String GROUP = "basic";
        private boolean enableBaiduTimeFactor;
        private boolean enableOGTimeFactor;
        private boolean enableMetaTimeFactor;
        private boolean enableStructuredData;
        private boolean enableCanonicalTag;
        private boolean enableTwitterCard;
        private boolean enableMetaRobots;
        private String robotsIndex;
        private String robotsFollow;
        private boolean enableFAQSchema;
        private boolean enableHowToSchema;
        private String contentTypeDetection;
        private boolean enableEnhancedSocial;
        private boolean enableLinkedInTags;
        private boolean enableFacebookTags;
        private String socialImageOptimization;
        private String defaultImage;
        private boolean enableArticleSchema;
        private String articleSchemaType;
        private String publisherName;
        private boolean includeBreadcrumbs;
        private boolean estimateReadingTime;
    }
}

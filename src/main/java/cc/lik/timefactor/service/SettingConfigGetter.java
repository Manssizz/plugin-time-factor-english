package cc.lik.timefactor.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicConfig> getBasicConfig();
    Mono<SocialConfig> getSocialConfig();
    Mono<AdvancedConfig> getAdvancedConfig();
    Mono<WebmasterConfig> getWebmasterConfig();
    Mono<AnalyticsConfig> getAnalyticsConfig();

    @Data
    class BasicConfig {
        public static final String GROUP = "basic";
        private boolean enableBaiduTimeFactor;
        private boolean enableOGTimeFactor;
        private boolean enableMetaTimeFactor;
        private boolean enableStructuredData;
        private boolean enableCanonicalTag;
        private boolean enableMetaRobots;
        private String robotsIndex;
        private String robotsFollow;
    }

    @Data
    class SocialConfig {
        public static final String GROUP = "social";
        private boolean enableTwitterCard;
        private boolean enableEnhancedSocial;
        private boolean enableLinkedInTags;
        private boolean enableFacebookTags;
        private String facebookAppId;
        private String socialImageOptimization;
    }

    @Data
    class AdvancedConfig {
        public static final String GROUP = "advanced";
        private boolean enableFAQSchema;
        private boolean enableHowToSchema;
        private String contentTypeDetection;
        private boolean enableAutoAltText;
        private boolean enableImageFilenameOptimization;
        private boolean enableDynamicOGImages;
        private boolean enableLazyLoading;
        private String imageQuality;
        private int maxImageWidth;
        private int maxImageHeight;
        private String defaultImage;
    }

    @Data
    class WebmasterConfig {
        public static final String GROUP = "webmaster";
        private boolean enableAutoPush;
        private boolean autoPushOnPublish;
        private String siteUrl;
        private String sitemapUrl;
        private boolean enableGooglePush;
        private String googleApiKey;
        private boolean enableBingPush;
        private String bingApiKey;
        private boolean enableBaiduPush;
        private String baiduApiKey;
    }

    @Data
    class AnalyticsConfig {
        public static final String GROUP = "analytics";
        private boolean enableGoogleAnalytics;
        private String googleAnalyticsId;
        private boolean enableSearchConsoleIntegration;
        private String searchConsoleVerification;
        private boolean enableCoreWebVitals;
        private String pageSpeedApiKey;
        private boolean enableSEODashboard;
    }
}

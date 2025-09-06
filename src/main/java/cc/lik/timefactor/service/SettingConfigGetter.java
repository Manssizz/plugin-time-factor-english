package cc.lik.timefactor.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicConfig> getBasicConfig();
    Mono<AdvancedConfig> getAdvancedConfig();

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
        private boolean enableAutoAltText;
        private boolean enableImageFilenameOptimization;
        private boolean enableDynamicOGImages;
        private boolean enableLazyLoading;
        private String imageQuality;
        private int maxImageWidth;
        private int maxImageHeight;
    }

    @Data
    class AdvancedConfig {
        public static final String GROUP = "advanced";
        private boolean enableAutoPush;
        private boolean autoPushOnPublish;
        private boolean enableGooglePush;
        private String googleApiKey;
        private boolean enableBingPush;
        private String bingApiKey;
        private boolean enableBaiduPush;
        private String baiduApiKey;
        private String siteUrl;
    }
}

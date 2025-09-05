package cc.lik.seotoolset.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicConfig> getBasicConfig();

    @Data
    class BasicConfig {
        public static final String GROUP = "basic";
        private boolean enableCanonicalTags;
        private boolean enablePageAnalysis;
        private boolean enableCrawlerLogs;
        private boolean enableWebsiteInfo;
        private boolean enableOpenGraph;
        private boolean enableTwitterCards;
        private boolean enableWeChatSharing;
        private boolean enableSchemaOrg;
        private boolean enableBaiduStructured;
        private String googleVerificationCode;
        private String baiduVerificationCode;
        private String bingVerificationCode;
        private boolean enableRedirects;
        private boolean autoDetectBrokenLinks;
        private String defaultImage;
    }
}

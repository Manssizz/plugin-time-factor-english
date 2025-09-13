package cc.lik.timefactor.service.impl;


import cc.lik.timefactor.service.SettingConfigGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Component
@RequiredArgsConstructor
public class SettingConfigGetterImpl implements SettingConfigGetter {
    private final ReactiveSettingFetcher settingFetcher;

    @Override
    public Mono<BasicConfig> getBasicConfig() {
       return settingFetcher.fetch(BasicConfig.GROUP, BasicConfig.class)
           .defaultIfEmpty(new BasicConfig());
    }

    @Override
    public Mono<SocialConfig> getSocialConfig() {
        return settingFetcher.fetch(SocialConfig.GROUP, SocialConfig.class)
            .defaultIfEmpty(new SocialConfig());
    }

    @Override
    public Mono<AdvancedConfig> getAdvancedConfig() {
        return settingFetcher.fetch(AdvancedConfig.GROUP, AdvancedConfig.class)
            .defaultIfEmpty(new AdvancedConfig());
    }

    @Override
    public Mono<WebmasterConfig> getWebmasterConfig() {
        return settingFetcher.fetch(WebmasterConfig.GROUP, WebmasterConfig.class)
            .defaultIfEmpty(new WebmasterConfig());
    }

    @Override
    public Mono<AnalyticsConfig> getAnalyticsConfig() {
        return settingFetcher.fetch(AnalyticsConfig.GROUP, AnalyticsConfig.class)
            .defaultIfEmpty(new AnalyticsConfig());
    }
}

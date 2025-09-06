package cc.lik.timefactor.service;

import cc.lik.timefactor.service.SettingConfigGetter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchEnginePushService {

    private static final Logger logger = LoggerFactory.getLogger(SearchEnginePushService.class);

    private final SettingConfigGetter settingConfigGetter;
    private final RestTemplate restTemplate;

    public SearchEnginePushService(SettingConfigGetter settingConfigGetter) {
        this.settingConfigGetter = settingConfigGetter;
        this.restTemplate = new RestTemplate();
    }

    public Mono<Void> pushToSearchEngines(String url, String sitemapUrl) {
        return settingConfigGetter.getAdvancedConfig()
            .doOnNext(config -> {
                if (!config.isEnableAutoPush()) {
                    logger.info("Auto push to search engines is disabled");
                    return;
                }

                if (config.isEnableGooglePush()) {
                    pushToGoogle(url, sitemapUrl, config.getGoogleApiKey(), config.getSiteUrl());
                }

                if (config.isEnableBingPush()) {
                    pushToBing(url, sitemapUrl, config.getBingApiKey(), config.getSiteUrl());
                }

                if (config.isEnableBaiduPush()) {
                    pushToBaidu(url, sitemapUrl, config.getBaiduApiKey(), config.getSiteUrl());
                }
            })
            .then();
    }

    private void pushToGoogle(String url, String sitemapUrl, String apiKey, String siteUrl) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Google API key not configured");
            return;
        }

        try {
            // Google Search Console API endpoint for URL submission
            String googleUrlEndpoint = "https://www.googleapis.com/webmasters/v3/sites/" +
                java.net.URLEncoder.encode(siteUrl, "UTF-8") + "/urlNotifications:publish";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("url", url);
            requestBody.put("type", "URL_UPDATED");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(googleUrlEndpoint, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully pushed URL to Google: {}", url);
            } else {
                logger.error("Failed to push URL to Google: {}", response.getStatusCode());
            }

            // Also submit sitemap if provided
            if (sitemapUrl != null && !sitemapUrl.isEmpty()) {
                submitSitemapToGoogle(sitemapUrl, apiKey, siteUrl);
            }

        } catch (Exception e) {
            logger.error("Error pushing to Google", e);
        }
    }

    private void submitSitemapToGoogle(String sitemapUrl, String apiKey, String siteUrl) {
        try {
            String sitemapEndpoint = "https://www.googleapis.com/webmasters/v3/sites/" +
                java.net.URLEncoder.encode(siteUrl, "UTF-8") + "/sitemaps/" +
                java.net.URLEncoder.encode(sitemapUrl, "UTF-8");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            restTemplate.put(sitemapEndpoint, entity);
            logger.info("Successfully submitted sitemap to Google: {}", sitemapUrl);

        } catch (Exception e) {
            logger.error("Error submitting sitemap to Google", e);
        }
    }

    private void pushToBing(String url, String sitemapUrl, String apiKey, String siteUrl) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Bing API key not configured");
            return;
        }

        try {
            // Bing Webmaster API endpoint
            String bingEndpoint = "https://ssl.bing.com/webmaster/api.svc/json/SubmitUrl";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("siteUrl", siteUrl);
            requestBody.put("url", url);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(bingEndpoint + "?apikey=" + apiKey, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully pushed URL to Bing: {}", url);
            } else {
                logger.error("Failed to push URL to Bing: {}", response.getStatusCode());
            }

            // Submit sitemap if provided
            if (sitemapUrl != null && !sitemapUrl.isEmpty()) {
                submitSitemapToBing(sitemapUrl, apiKey, siteUrl);
            }

        } catch (Exception e) {
            logger.error("Error pushing to Bing", e);
        }
    }

    private void submitSitemapToBing(String sitemapUrl, String apiKey, String siteUrl) {
        try {
            String sitemapEndpoint = "https://ssl.bing.com/webmaster/api.svc/json/SubmitSitemap";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("siteUrl", siteUrl);
            requestBody.put("sitemapUrl", sitemapUrl);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sitemapEndpoint + "?apikey=" + apiKey, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully submitted sitemap to Bing: {}", sitemapUrl);
            } else {
                logger.error("Failed to submit sitemap to Bing: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error submitting sitemap to Bing", e);
        }
    }

    private void pushToBaidu(String url, String sitemapUrl, String apiKey, String siteUrl) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Baidu API key not configured");
            return;
        }

        try {
            // Baidu Webmaster API endpoint
            String baiduEndpoint = "http://data.zz.baidu.com/urls?site=" + siteUrl + "&token=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            HttpEntity<String> entity = new HttpEntity<>(url, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(baiduEndpoint, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully pushed URL to Baidu: {}", url);
            } else {
                logger.error("Failed to push URL to Baidu: {}", response.getStatusCode());
            }

            // Submit sitemap if provided
            if (sitemapUrl != null && !sitemapUrl.isEmpty()) {
                submitSitemapToBaidu(sitemapUrl, apiKey, siteUrl);
            }

        } catch (Exception e) {
            logger.error("Error pushing to Baidu", e);
        }
    }

    private void submitSitemapToBaidu(String sitemapUrl, String apiKey, String siteUrl) {
        try {
            String sitemapEndpoint = "http://data.zz.baidu.com/sitemap?site=" + siteUrl + "&token=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            HttpEntity<String> entity = new HttpEntity<>(sitemapUrl, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sitemapEndpoint, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully submitted sitemap to Baidu: {}", sitemapUrl);
            } else {
                logger.error("Failed to submit sitemap to Baidu: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error submitting sitemap to Baidu", e);
        }
    }
}

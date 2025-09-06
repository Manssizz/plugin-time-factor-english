package cc.lik.timefactor.service;

import cc.lik.timefactor.service.SettingConfigGetter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void pushToSearchEngines(String url, String sitemapUrl) {
        if (!isAutoPushEnabled()) {
            logger.info("Auto push to search engines is disabled");
            return;
        }

        if (isGooglePushEnabled()) {
            pushToGoogle(url, sitemapUrl);
        }

        if (isBingPushEnabled()) {
            pushToBing(url, sitemapUrl);
        }

        if (isBaiduPushEnabled()) {
            pushToBaidu(url, sitemapUrl);
        }
    }

    private boolean isAutoPushEnabled() {
        return settingConfigGetter.getSetting("enableAutoPush", Boolean.class, false);
    }

    private boolean isGooglePushEnabled() {
        return settingConfigGetter.getSetting("enableGooglePush", Boolean.class, false);
    }

    private boolean isBingPushEnabled() {
        return settingConfigGetter.getSetting("enableBingPush", Boolean.class, false);
    }

    private boolean isBaiduPushEnabled() {
        return settingConfigGetter.getSetting("enableBaiduPush", Boolean.class, false);
    }

    private void pushToGoogle(String url, String sitemapUrl) {
        String apiKey = settingConfigGetter.getSetting("googleApiKey", String.class, "");
        if (apiKey.isEmpty()) {
            logger.warn("Google API key not configured");
            return;
        }

        try {
            // Google Search Console API endpoint for URL submission
            String googleUrlEndpoint = "https://www.googleapis.com/webmasters/v3/sites/" +
                java.net.URLEncoder.encode(getSiteUrl(), "UTF-8") + "/urlNotifications:publish";

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
                submitSitemapToGoogle(sitemapUrl, apiKey);
            }

        } catch (Exception e) {
            logger.error("Error pushing to Google", e);
        }
    }

    private void submitSitemapToGoogle(String sitemapUrl, String apiKey) {
        try {
            String sitemapEndpoint = "https://www.googleapis.com/webmasters/v3/sites/" +
                java.net.URLEncoder.encode(getSiteUrl(), "UTF-8") + "/sitemaps/" +
                java.net.URLEncoder.encode(sitemapUrl, "UTF-8");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.put(sitemapEndpoint, entity);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully submitted sitemap to Google: {}", sitemapUrl);
            } else {
                logger.error("Failed to submit sitemap to Google: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error submitting sitemap to Google", e);
        }
    }

    private void pushToBing(String url, String sitemapUrl) {
        String apiKey = settingConfigGetter.getSetting("bingApiKey", String.class, "");
        if (apiKey.isEmpty()) {
            logger.warn("Bing API key not configured");
            return;
        }

        try {
            // Bing Webmaster API endpoint
            String bingEndpoint = "https://ssl.bing.com/webmaster/api.svc/json/SubmitUrl";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("siteUrl", getSiteUrl());
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
                submitSitemapToBing(sitemapUrl, apiKey);
            }

        } catch (Exception e) {
            logger.error("Error pushing to Bing", e);
        }
    }

    private void submitSitemapToBing(String sitemapUrl, String apiKey) {
        try {
            String sitemapEndpoint = "https://ssl.bing.com/webmaster/api.svc/json/SubmitSitemap";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("siteUrl", getSiteUrl());
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

    private void pushToBaidu(String url, String sitemapUrl) {
        String apiKey = settingConfigGetter.getSetting("baiduApiKey", String.class, "");
        if (apiKey.isEmpty()) {
            logger.warn("Baidu API key not configured");
            return;
        }

        try {
            // Baidu Webmaster API endpoint
            String baiduEndpoint = "http://data.zz.baidu.com/urls?site=" + getSiteUrl() + "&token=" + apiKey;

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
                submitSitemapToBaidu(sitemapUrl, apiKey);
            }

        } catch (Exception e) {
            logger.error("Error pushing to Baidu", e);
        }
    }

    private void submitSitemapToBaidu(String sitemapUrl, String apiKey) {
        try {
            String sitemapEndpoint = "http://data.zz.baidu.com/sitemap?site=" + getSiteUrl() + "&token=" + apiKey;

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

    private String getSiteUrl() {
        // This should be configured or retrieved from the application context
        // For now, return a placeholder - this would need to be implemented based on the actual site URL
        return settingConfigGetter.getSetting("siteUrl", String.class, "https://example.com");
    }
}

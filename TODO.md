sk# SEO Toolset Plugin Implementation

## Completed Tasks
- [x] Rename plugin from timefactor to seo-toolset
- [x] Update plugin class name
- [x] Update settings.yaml with new plugin name and comprehensive settings
- [x] Create WebsiteInfoService for crawling status and robots.txt checks
- [x] Create PageAnalysisService for SEO optimization analysis
- [x] Create CrawlerLogService for request logging and anomaly detection
- [x] Create SocialMediaOptimizationService for OpenGraph, Twitter, WeChat cards
- [x] Update TimeFactorProcess configuration method calls to match new settings
- [x] Fix compilation errors and package imports
- [x] Add GitHub Actions workflow with manual trigger support
- [x] Update test class to use SeoToolsetPlugin and correct package structure
- [x] Fix meta og and twitter tags to be visible in browser inspect element by using SocialMediaOptimizationService in TimeFactorProcess

## Pending Tasks
- [ ] Create VerificationCodeService for search engine verification codes
- [ ] Create StructuredDataService for Schema.org markup
- [ ] Create LinkRedirectionService for redirect management
- [ ] Update TimeFactorProcess to SeoToolsetProcess with enhanced features
- [ ] Create new processors for each feature
- [ ] Update package structure if needed
- [ ] Test all new features

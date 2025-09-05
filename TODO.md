# TODO: Add Canonical and Twitter Meta Tags for Articles

## Completed Tasks
- [x] Updated BasicConfig class to include enableCanonicalTag and enableTwitterCard flags
- [x] Added checkboxes in settings.yaml for enabling canonical tags and Twitter cards
- [x] Updated TimeFactorProcess.java to generate canonical and Twitter meta tags
- [x] Added genCanonicalTag() method to generate canonical link tag
- [x] Added genTwitterCard() method to generate Twitter Card meta tags
- [x] Added debug comments to verify if canonical and Twitter meta tags are being generated
- [x] Renamed plugin from "time-factor" to "seo-toolkit"
- [x] Updated display name to "SEO Toolkit"
- [x] Updated all references in plugin.yaml, settings.yaml, and settings.gradle
- [x] Added Meta Robots control with index/follow settings
- [x] Implemented Enhanced Structured Data with FAQ and How-To schemas
- [x] Added Indonesian language support for content type detection
- [x] Updated settings with new configuration options
- [x] Implemented Advanced Social Media Optimization
- [x] Added enhanced Open Graph tags with image dimensions
- [x] Added LinkedIn-specific meta tags
- [x] Added Facebook-specific meta tags
- [x] Implemented Article Schema Markup for rich snippets
- [x] Added configurable article types (BlogPosting, NewsArticle, etc.)
- [x] Included breadcrumb navigation in schema
- [x] Added reading time estimation

## Features Added
- **Canonical Tag**: `<link rel="canonical" href="post-url"/>` - Prevents duplicate content issues
- **Twitter Card**: Complete set of Twitter Card meta tags including:
  - twitter:card (summary_large_image)
  - twitter:title
  - twitter:description
  - twitter:image
  - twitter:url
  - twitter:site
- **Smart Description Generation**: Automatically generates SEO descriptions from article content when excerpt is not available
  - Fallback to post content if excerpt is empty
  - HTML tag removal and text cleaning
  - Automatic truncation to 160 characters for optimal SEO
- **Meta Robots Control**: Advanced search engine indexing control
  - Enable/disable robots meta tags
  - Control indexing (index/noindex)
  - Control link following (follow/nofollow)
  - Default: index,follow for optimal SEO
- **Article Schema Markup**: Complete JSON-LD structured data for articles
  - Rich snippets in Google Search results
  - Configurable article types (BlogPosting, NewsArticle, TechArticle, ScholarlyArticle)
  - Publisher information and author details
  - Breadcrumb navigation integration
  - Reading time estimation (200 words/minute)
  - Enhanced search visibility and CTR

## Configuration
All SEO features are enabled by default and can be controlled via the plugin settings:
- "Enable Canonical Tag" checkbox
- "Enable Twitter Card" checkbox
- "Enable Article Schema" checkbox
- "Article Type" selector (BlogPosting/NewsArticle/TechArticle/ScholarlyArticle)
- "Publisher Name" text field
- "Include Breadcrumbs" checkbox
- "Estimate Reading Time" checkbox

## Next Steps
- [ ] Test the implementation by building the project
- [ ] Verify meta tags are generated correctly in article pages
- [ ] Check HTML source for debug comments to diagnose the issue

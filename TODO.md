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

## Features Added
- **Canonical Tag**: `<link rel="canonical" href="post-url"/>` - Prevents duplicate content issues
- **Twitter Card**: Complete set of Twitter Card meta tags including:
  - twitter:card (summary_large_image)
  - twitter:title
  - twitter:description
  - twitter:image
  - twitter:url
  - twitter:site

## Configuration
Both features are enabled by default and can be controlled via the plugin settings:
- "Enable Canonical Tag" checkbox
- "Enable Twitter Card" checkbox

## Next Steps
- [ ] Test the implementation by building the project
- [ ] Verify meta tags are generated correctly in article pages
- [ ] Check HTML source for debug comments to diagnose the issue

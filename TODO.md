# TODO: Add fb:app_id Support

## Tasks
- [x] Update SettingConfigGetter.java to add enableFacebookAppId boolean and fbAppId string fields to BasicConfig
- [x] Update settings.yaml to add "Enable Facebook App ID" checkbox in social group
- [x] Update settings.yaml to add "Facebook App ID" textarea with conditional display (if enableFacebookAppId)
- [x] Update TimeFactorProcess.java genFacebookTags method to conditionally add fb:app_id meta tag using config

## Followup
- [ ] Test settings UI for show/hide functionality
- [ ] Verify meta tag generation

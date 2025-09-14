# TODO: Add support for fetchpriority=high

## Steps to Complete

- [x] Add new setting "Enable Fetch Priority High for Cover Images" to settings.yaml in advanced group
- [x] Update BasicConfig class in SettingConfigGetter.java to include enableFetchPriorityHigh field
- [x] Add genPreloadLink method to TimeFactorProcess.java
- [x] Modify generateSeoTags in TimeFactorProcess.java to conditionally call genPreloadLink
- [ ] Test the implementation to ensure preload link is added correctly

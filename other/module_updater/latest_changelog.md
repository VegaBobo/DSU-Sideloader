### DSU Sideloader v2.03 (8)

- Always enforce "settings_dynamic_system" flag when possible.
    - May fix installation stuck for some devices
- Write installation script to ExternalFilesDir (only adb operation mode)
- Added developer options, enabled by doing multiple taps in application icon on about section, enables:
    - Full logcat logging (may help diagnose installation issues)
    - Disable storage checks (only for testing, not recommended)
- Added unlocked bootloader warning
- Other minor fixes and improvements

Read more at: https://github.com/VegaBobo/DSU-Sideloader/releases/tag/2.03
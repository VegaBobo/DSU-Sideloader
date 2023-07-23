# DSU Sideloader

A simple app made to help users easily install GSIs via DSU's Android feature.
<div>
<img src="https://raw.githubusercontent.com/VegaBobo/DSU-Sideloader/master/other/preview_1.png" alt="preview" width="200"/>  
<img src="https://raw.githubusercontent.com/VegaBobo/DSU-Sideloader/master/other/preview_2.png" alt="preview" width="200"/>  
</div>

## Requirements
- Android 10 or higher
- Unlocked Bootloader
- Device with Dynamic Partitions
- A GSI you want to use!

Community GSIs: https://github.com/phhusson/treble_experimentations/wiki/Generic-System-Image-%28GSI%29-list

Google GSIs: https://developer.android.com/topic/generic-system-image/releases

**Remember to use GSIs compatible with your architeture, vndk implementation..*

## Downloads

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/vegabobo.dsusideloader/)

Or download the latest APK from the [Releases Section](https://github.com/VegaBobo/DSU-Sideloader/releases/latest).
For testing builds, you can check artifacts at [Actions](https://github.com/VegaBobo/DSU-Sideloader/actions) tab

## How to use?
1. Install app
2. When opening for the first time, you need to give read/write permission to a folder, create a new folder and allow access  
   **this folder will be used to store temporary files, like extracted GSIs from compressed files)*
3. Select a GSI to install  
   **accepted formats are: gz, xz, img and zip (only DSU packages)
4. You can customize installation as you want  
   **like changing userdata size for dynamic system*  
   **changing gsi file size is not recommended (let app do it automatically)*
5. Tap on "Install"
6. Wait until finishes! (it may take a some time)
7. Once it finishes, next step may vary:
	- If built-in installer is enabled, no extra step is required.
	- When built-in installer is disabled, on rooted/system/shizuku operation mode, DSU screen will appear, prompting you to confirm installation, after that, check your notifications, DSU should start installing GSI.
	- On ADB operation mode, you will be prompted to run a command in adb, once you run, DSU screen will appear asking you to confirm installation, after that, DSU should start installing GSI.
8. Once dynamic system is ready, you can boot it through notifications, or, if operation mode is supported, directly from our app.

For more usage information, you can check [Operation modes](#operation-modes)

## Operation modes

DSU Sideloader support multiple operation modes, they will define how our app will work, also, the operation mode is obtained automatically, and by now, is impossible to change it manually, the picked operation mode will be the best available (the priority is written below, in which, the most feature supported, is the highest number, and the most basic one is the lowest).

1. ADB: Default operation mode when other modes aren't available
- Only prepare selected image to be installed via DSU system-app
- Requires adb command to start installation (which will invoke DSU system-app to install the prepared file)
2. Shizuku: When running app with Shizuku (Obtained when Shizuku permission is granted)
- Same as ADB, however, it does not require to run any adb command
- Support tracking installation progress ¹ ² ³
- Support installation diagnostics (if a common error is detected, it may give you useful information) ¹ ³
3. Root: When running app with root permissions (Obtained when user grant root permission)
- All features avaiable in Shizuku, however, does not require any special permissions
- DynamicSystem API features (check if DSU is installed, reboot to DSU, discard..., everthing directly from app)
- Support built-in DSU installer ⁴ ⁵
4. System mode: When running as system-app (Obtained by installing our Magisk module)
- All features avaiable in Shizuku
- Fixes for some common gsi/dsu-related SELinux denials
- Custom gsid binary (can fix some installation errors in some devices ⁵ ⁶
5. System/Root mode: When running as system-app with granted root permission (Obtained by installing our Magisk module and granting root permission)
- All features available in root and system operation mode

¹ Requires READ_LOGS permission.
<br>
² Partital support on Android 10 and 11.
<br>
³ Android 13 requires "One-time log access".
<br>
⁴ Feature not supported on Android 10.
<br>
⁵ Experimental feature, built-in installer code is [here](https://github.com/VegaBobo/DSU-Sideloader/blob/master/app/src/main/java/vegabobo/dsusideloader/installer/root/DSUInstaller.kt).
<br>
⁶ Module including custom gsid binary is optional, changes made to AOSP gsid binary can be found [here](https://github.com/VegaBobo/DSU-Sideloader/tree/master/magisk-module/src/main/resources/aosp_patches).

#### Recomendations

- For non-rooted devices, Shizuku is a pretty nice operation mode, it support most features with no hassle, however, you need to install and setup [Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api) app in your device.
- For rooted devices, Root operation mode is more than ok for most people.
- If you're having issues with DSU feature, go with System/Root.
- Rooted devices via Magisk, should be running Magisk v24 or higher, older versions may break DSU feature.
- We highly recommend using this app with Stock ROM, some Custom ROM builds may also work fine.

## Common questions

1. DSU installation finishes with no errors, but device doesn't boot into installed DSU, what should i do?
- It is likely that AVB is preventing device from booting installed images, try flashing disabled vbmeta, check [this](https://developer.android.com/topic/generic-system-image#flash-gsi) for more info.
2. Why isn't possible to set a high userdata value?
- The more storage you have free, the more you can use to be your userdata, some Android versions limit the maximum allowed for allocation (this limitation is 40%, and is not our app limitation, it is a thing from Android itself, you can use our custom gsid binary, which reduces this limitation to 20%, is possible to eliminate it, but no clue if there is some implications, so, i just decided to decrease it).
3. Why "Unmount SD" option exists?
- If available, DSU priorizes allocation in sdcard, but allocating in sdcard is not supported in some cases (it may depends on filesystem present on sd, and if the allocation in SD is allowed by OS itself), since allocating in SD may cause installation errors in some devices, that options is here to enforce allocation in device storage.
4. Why built-in installer requires root?
- Because it uses Android's internal DynamicSystem API, which requires "MANAGE_DYNAMIC_SYSTEM", which is a signature protection level, the convenient way to circumvent it, is by using root. shell (2000) has "INSTALL_DYNAMIC_SYSTEM", which is able to call DSU system-app (this one has "MANAGE_DYNAMIC_SYSTEM") to install images.
5. How about updates?
- Our app comes with a updater, you can check updates in "About" section.
6. Other question? problem?
- Feel free to start a issue, for troubleshooting, don't forget to send logs (logs can be obtained on installation phase, directly on app, when operation mode support installation diagnostics).

## About DSU
DSU (Dynamic System Updates), is a feature introduced on Android 10, that let developers boot GSIs without touching current system partition, this is done by creating new partitions to hold a GSI and a separated userdata, to boot on them when desired.

Unfortunelly, DSU depends on Dynamic Partitions (your device need to support, otherwise, won't work), and most GSIs requires unlocked bootloader to get them booting properly (since only OEM-Signed GSIs are allowed to boot on locked bootloader).

GSIs can be installed via DSU without root access, using ADB, running some commands, you can read more about installation process here: https://developer.android.com/topic/dsu

Once installation finishes, Android creates a persistent notification allowing you to boot into "Dynamic System" (GSI installed via DSU), and you can boot into installed GSI, without touching your system partition, or breaking the "real userdata" partition.

After booting Dynamic System, you can try and test whatever you want, when you need to switch back to device's original system image, everything you need to do, is just, a simple reboot!

When doing a long test, that may requires lots of reboots, this can be a pain, however, is possible to enable "sticky mode", that enforces dynamic system, instead of device's original system image, once tests are done, you can disable sticky mode and return to original system image.

That is basically a quickly explanation about DSU, a amazing feature, like a "dual-boot" solution, limited, however, very safe (since no read-only partition will be modified, and if GSI does not boot, just a simple reboot will return you to the original device's system image).

You can read more about DSU here: https://source.android.com/devices/tech/ota/dynamic-system-updates

## How to enable Sticky Mode?

Reboot to Dynamic System, and:
- use this command on adb: `adb shell gsi_tool enable`
- or from local adb shell:  `gsi_tool enable`
- or from local rooted shell (eg. Termux on rooted GSI):  `su -c 'gsi_tool enable'`

When sticky mode is enabled, device will always boot into dynamic system, instead of device's original system image.

To disable, use the same command, instead of `enable` , use `disable`

## Other

For translators, we now have a Crowdin, feel free send your translations:
<br>https://crowdin.com/translate/dsu-sideloader/<br>
App icon made by [WSTxda](https://github.com/WSTxda)

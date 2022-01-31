
# DSU Sideloader

A simple app made to help users easily install GSIs via DSU's Android feature.

## Requirements
- Android 10 or higher
- Unlocked Bootloader
- Device with Dynamic Partitions
- A GSI you want to use!

Community GSIs: https://github.com/phhusson/treble_experimentations/wiki/Generic-System-Image-%28GSI%29-list

Google GSIs: https://developer.android.com/topic/generic-system-image/releases

**Remember to use GSIs compatible with your architeture, vndk implementation..*

You don't need root to use this app, however, running on non-rooted devices, requires adb (you will be prompted to run a shell script to invoke DSU installation activity)

Rooted devices via Magisk, should be running Magisk v24 or higher, older versions may break DSU feature.

We highly recommend using this app with Stock ROM, Custom ROMs aren't supported.

## How to use?
1. Install app
2. When opening for the first time, you need to give read/write permission to a folder, create a new folder and allow access

	**this folder will be used to store temporary files, like extracted GSIs from compressed files)*

3. Select a GSI to install

	**accepted formats are: gz, xz and img*

4. You can customize installation as you want

	**like changing userdata size for dynamic system*

	**changing gsi file size is not recommended (let app do it automatically)*

5. Tap on "Install GSI via DSU"
6. Wait until finishes! (it may take a some time)
7. Once it finishes, next step may vary:
	  - On rooted devices, DSU screen will appear, prompting you to confirm installation, after that, check your notifications, DSU should start installing GSI
	  - On non-rooted devices, you will be prompted to run a command in adb, once you run, DSU screen will appear asking you to confirm installation, after that, DSU should start installing GSI
8. Once dynamic system is ready, you can boot it through notifications

## Other information
- DSU feature may be broken in some ROMs.
- gsid does not let you install GSIs via DSU when you have less than 40% of free storage.
- gsid checks if selected GSI size is multiple of 512 (preventing corrupted system images).
- If you have disabled/debloated system apps, make sure "Dynamic System Updates" app is not disabled.
- To use "ADB mode" on rooted device, deny root permission.

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


## Why creating this app?

Since this process can be done without any app, using adb, why creating a app to do that?

Well, to be honest, i think the same, however, having a app that can automate the "installation" process, and making DSUs more easier for end-user, would be a nice thing, also, i want to learn some kotlin, so, i've made this app!

## AOSP Patches
This folder contains patches made to AOSP, we use those patches to build our custom gsid.

Android 12 and 12L (based on 12.1.0_r11):
- libfiemap: Check filesystem features on f2fs_dev if they aren't avaiable on f2fs path
- gsid: Decrease required 40% of free storage to 20%
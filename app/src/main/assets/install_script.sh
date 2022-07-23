#!/bin/sh

# Script made to launch DSU installation activity.
# Values are populared by String.format

# Prevent users running Magisk
# equal or older than 23016
# since this version may break DSU boot
# this script was made for users running without root
# but some users may, just, well, refuse root permission
magisk_version=$(su -V) &>/dev/null

if [ ! -z "$magisk_version" ]; then
  if [ $magisk_version -lt 23016 ]; then
    echo "Detected older Magisk version, please update to the latest Magisk build"
    exit 1
  fi
fi

# clean logcat before running commands
debug_mode=%DEBUG_MODE
if [ $debug_mode == true ]; then
  logcat -c
fi

# unmount sdcard
umount_sd=%UNMOUNT_SD
if [ $umount_sd == true ]; then
  SDCARD=$(sm list-volumes | grep -v null | grep public)
  sm unmount $SDCARD
fi

# required prop
setprop persist.sys.fflag.override.settings_dynamic_system true

# invoke DSU activity
am start-activity -n com.android.dynsystem/com.android.dynsystem.VerificationActivity \
  -a android.os.image.action.START_INSTALL %INSTALLATION_ARGS

if [ $umount_sd == true ]; then
  (sleep 60 && sm mount $SDCARD) &
fi

echo
echo "DSU installation has been started! check your notifications"

if [ $debug_mode == true ]; then
  echo
  echo "You're running on debug mode, logs are saved in /sdcard/logcat_dsu.txt"
  echo "Once a error happen, you can press CTRL+C to exit"
  echo
  (echo "%INSTALLATION_INFO" > /sdcard/logcat_dsu.txt)
  (logcat >> /sdcard/logcat_dsu.txt)
fi

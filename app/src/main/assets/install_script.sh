#!/bin/sh

# Script made to launch DSU installation activity.
# Unrooted users can run this script using ADB to start
# installation of GSI via DSU.
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

# required prop
setprop persist.sys.fflag.override.settings_dynamic_system true

# invoke DSU activity
am start-activity -n com.android.dynsystem/com.android.dynsystem.VerificationActivity \
  -a android.os.image.action.START_INSTALL \
  -d %s \
  --el KEY_SYSTEM_SIZE %s \
  --el KEY_USERDATA_SIZE %s

# if debug mode == log it (greping for gsid and dynsys)
# else delete installation file
debug_mode=%s
if [ $debug_mode == true ]; then
  logcat -c
  echo "" > /sdcard/dsu_sideloader_logs.txt
  (logcat | grep -e gsid -e dynsys) | tee /sdcard/dsu_sideloader_logs.txt
else
  rm '%s'
fi

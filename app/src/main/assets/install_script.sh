#!/bin/sh

# unmount sdcard
umount_sd=%UNMOUNT_SD
if [ $umount_sd == true ]; then
  SDCARD=$(sm list-volumes | grep -v null | grep public)
  sm unmount $SDCARD
fi

# required prop
setprop persist.sys.fflag.override.settings_dynamic_system true

# invoke DSU activity
%ACTION_INSTALL

if [ $umount_sd == true ]; then
  (sleep 60 && sm mount $SDCARD) &
fi

echo
echo "DSU installation has been started! check your notifications"

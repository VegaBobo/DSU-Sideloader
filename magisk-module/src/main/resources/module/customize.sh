#!/bin/sh
function install_gsid() {
  ui_print "- Installing custom gsid binary..."
  ui_print "- API Level: $API"
  ui_print "- Arch: $ARCH"
  mkdir -p $MODPATH/system/bin
  cp "$MODPATH/bin-$ARCH/$API/gsid" $MODPATH/system/bin/gsid
  chmod +x $MODPATH/system/bin/gsid
  chcon u:object_r:gsid_exec:s0 $MODPATH/system/bin/gsid

  # Minimum allowed for allocation is hardcoded in gsid binary
  # values below are used by DSU Sideloader UI, nothing else
  echo "ro.vegabobo.dsusideloader.gsid_min_alloc=0.20" >>$MODPATH/system.prop
}

function clean() {
  rm -rf $MODPATH/bin-arm
  rm -rf $MODPATH/bin-arm64
}

if [[ $API -le "33" && $API -ge "29" ]] &&
  [[ $ARCH == arm64 || $ARCH == arm ]]; then
  install_gsid
  clean
  setprop persist.sys.fflag.override.settings_dynamic_system true
fi

echo "- Installing DSU Sideloader..."
pm install $MODPATH/system/priv-app/DSUSideloader/ReleaseDSUSideloader.apk
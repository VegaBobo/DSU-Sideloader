#!/bin/sh
function install_gsid() {
  ui_print "- Installing custom gsid binary..."
  ui_print "- API Level: $API"
  ui_print "- Arch: $ARCH"
  mkdir -p $MODPATH/system/bin
  cat "$MODPATH/bin-$ARCH/$API/gsid" > $MODPATH/system/bin/gsid

  # Minimum allowed for allocation is hardcoded in gsid binary
  # values below are used by DSU Sideloader UI, nothing else
  echo "ro.vegabobo.dsusideloader.gsid_min_alloc=0.20" >> $MODPATH/system.prop
}

if [[ $API -le "33" && $API -ge "29" ]] &&
   [[ $ARCH == arm64 || $ARCH == arm ]]
then
    install_gsid
fi
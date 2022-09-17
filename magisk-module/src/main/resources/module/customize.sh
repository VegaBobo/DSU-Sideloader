#!/bin/sh
if [[ $API == "32" ]] || [[ $API == "31" ]]; then
  if [[ $ARCH == "arm64" ]]; then
    ui_print "- Installing custom gsid binary.."
    mkdir -p $MODPATH/system/bin
    mv $MODPATH/bin/Android12/gsid $MODPATH/system/bin/gsid
    # Minimum allowed for allocation is hardcoded in gsid binary
    # values below are used by DSU Sideloader UI, nothing else
    echo "ro.vegabobo.dsusideloader.gsid_min_alloc=0.20" >> $MODPATH/system.prop
  fi
fi

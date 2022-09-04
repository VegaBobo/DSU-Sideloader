#!/bin/sh
if [[ $API == "32" ]] || [[ $API == "31" ]]; then
  if [[ $ARCH == "arm64" ]]; then
    ui_print "- Installing custom gsid binary.."
    mkdir -p $MODPATH/system/bin
    mv $MODPATH/bin/Android12/gsid $MODPATH/system/bin/gsid
  fi
fi

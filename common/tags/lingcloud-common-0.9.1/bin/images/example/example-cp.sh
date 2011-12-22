#!/bin/bash
#
# Copyright (C) 2008-2011,
# LingCloud Team,
# Institute of Computing Technology,
# Chinese Academy of Sciences.
# P.O.Box 2704, 100190, Beijing, China.
#
# http://lingcloud.org
#
# Example images for LingCloud test.
# Put the script and two images in $NFS_MOUNT_DIR/lingcloud-images,
# and then execute the script.

mkdir -pv ./vam/upload/disc
mkdir -pv ./vam/upload/disk
command cp -fv example.iso ./vam/upload/disc
command cp -fv example.img ./vam/upload/disk

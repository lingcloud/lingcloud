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
# Author: Xiaoyi Lu
# Email: luxiaoyi@software.ict.ac.cn
# Purpose: get static metainfo from all physical nodes.
#

IFCMD="/sbin/ifconfig -a"
IP="0.0.0.0"

get_interfaces_name() {
    $IFCMD | grep "Link encap:Ethernet.*HWaddr" | cut -d" " -f1
}

get_interfaces() {
    $IFCMD | grep "Link encap:Ethernet.*HWaddr" | sed 's/ *Link encap:Ethernet.*HWaddr /-/g' | tr -s ' ' | grep "^$1-" | cut -d" " -f1
}

get_interface_ip() {
    $IFCMD | awk 'BEGIN{}/HWaddr|inet/{ if ($2 == "Link"){ if (flag == 1) if (flag == 1) printf "-0.0.0.0 "; printf $1"-"$5;flag=1;} if ($1 == "inet") { printf "-"substr($2,6)" "; flag=0;} }END{ if (flag == 1) printf "-0.0.0.0"}'
    return 0
}

ARCH=`uname -m`

if [ -f /proc/cpuinfo ]; then
    MODELNAME=\"
    MODELNAME=$MODELNAME`grep -m 1 "model name" /proc/cpuinfo | cut -d: -f2 | sed -e 's/^ *//' | sed -e 's/$/"/'`
fi

HOSTNAME=`uname -n`

NICs=`get_interface_ip`

CPUSPEED=`cat /proc/cpuinfo | awk '/cpu MHz/{ ret=$4; }END{ print ret; }'`
CPUNUM=`cat /proc/cpuinfo | awk 'BEGIN{ ret = 0 }/processor/{ ret++; }END{ print ret; }'`
MEMTOTAL=`free | awk '/Mem/{print $2}'`

echo -n ARCH=$ARCH";"
echo -n MODELNAME=$MODELNAME";"
echo -n HOSTNAME=$HOSTNAME";"
echo -n NICs=$NICs";"
echo -n CPUSPEED=$CPUSPEED";"
echo -n CPUNUM=$CPUNUM";"
echo -n MEMTOTAL=$MEMTOTAL

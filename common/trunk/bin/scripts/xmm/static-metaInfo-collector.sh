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
    NUM=`$IFCMD | wc -l`
    names=`get_interfaces_name`
    #echo ${name_array[1]}
    tag=1
    index=1
    result=""
    for ((i=1;i<=$NUM;i++))
    do
      line=`$IFCMD | sed -n "$i p"`
      prefix=`echo $line | cut -d" " -f1`
      cur_name=`echo $names | cut -d" " -f$index`
      #echo cur_name=$cur_name
      #echo $prefix $cur_name
      if [ "$prefix" == "$cur_name" ]; then
	#Indicates we need to get the next line of IP.
	#echo $cur_name
	nicmac=`get_interfaces $cur_name`
	if [ x"$nicmac" == "x" ]; then
		continue;
	fi
	#echo "nicmac=$nicmac*"
	if [ x"$result" == "x" ]; then
		result=$nicmac
	else 
		result=$result" "$nicmac
	fi
	#result=$result" "$nicmac
	#echo $result
	tag=0
	continue
     fi
     if [ $tag -eq 0 ]; then
	tag=1
	#FIXME Some interface doesn't contain ip address.
	result_tmp=`echo $line | awk -F"inet addr:" '{print $2}' | tr -s ' ' | cut -d" " -f1`
	if [ "$result_tmp" == "" ]; then
	  result=$result"-"$IP
	else
	  result=$result"-"$result_tmp
	fi
	index=`expr $index + 1`
     fi
     done
     #echo $result | tr -s ' '
     echo $result
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

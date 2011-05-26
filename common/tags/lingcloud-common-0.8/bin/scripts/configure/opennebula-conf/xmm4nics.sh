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
# Purpose: Add new attributes in Host info of OpenNebula.

export LANG=en_US

IFCMD="/sbin/ifconfig -a"
IP="0.0.0.0"

get_interfaces_name() {
    $IFCMD | grep "Link encap:Ethernet.*HWaddr" | cut -d" " -f1
}

get_interfaces() {
    $IFCMD | grep "Link encap:Ethernet.*HWaddr" | sed 's/ *Link encap:Ethernet.*HWaddr /-/g'
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
      #echo $prefix $cur_name
      if [ "$prefix" == "$cur_name" ]; then
	#Indicates we need to get the next line of IP.
	tag=0
	continue
     fi
     if [ $tag -eq 0 ]; then
	tag=1
	#FIXME Some interface doesn't contain ip address.
	result_tmp=`echo $line | awk -F"inet addr:" '{print $2}' | tr -s ' ' | cut -d" " -f1`
	if [ "$result_tmp" == "" ]; then
	  result=$result" "$IP
	else
	  result=$result" "$result_tmp
	fi
	index=`expr $index + 1`
     fi
     done
     echo $result | tr -s ' '
}

get_nics_info() {
     ips=`get_interface_ip`
     interfaces=`get_interfaces`
     interfaces_num=`echo $ips | wc -w`
     interfaces_num_check=`echo $interfaces | wc -w`
     result=""
     if [ $interfaces_num -eq $interfaces_num_check ]; then
        for ((i=1;i<=$interfaces_num;i++))
        do
     	   cur_nic=`echo $interfaces | cut -d" " -f$i`
	   eachip=`echo $ips | cut -d" " -f$i`
     	   result=$result" "$cur_nic"-"$eachip
  	done
     else
  	result=`echo "Error for get NICs information of Host : $HOSTNAME"`
     fi
     echo $result | tr -s ' '
}

result=`get_nics_info`
echo NICs=\"$result\"



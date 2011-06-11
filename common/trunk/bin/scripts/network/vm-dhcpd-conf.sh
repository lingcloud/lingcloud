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
# Generate the dhcpd.conf section for virtual machine for LingCloud.
# It will read the publicIpPool as the IP range for VMs
# from LingCloud configure file - molva.conf.
# If failed, it will ask the user to input publicIpPool.
# The IP allocated to the VM is according to its MAC.
# IP: x.x.x.x - MAC: ee:ee:xx:xx:xx:xx
#
# Usage: vm-dhcpd-conf.sh 
#	 vm-dhcpd-conf.sh | tee outputFile

function chkRange {
	echo "$1" | egrep '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{0,3}-[0-9]{1,3}' > /dev/null
	return $?
}

function fixIp2Host {
	echo -e "\thost $1 {"
	echo -e "\t\thardware ethernet $2;"
	echo -e "\t\tfixed-address $3;"
	echo -e "\t}"
	echo ""
}

function ipRange2Host {
	local range=$1
	description=$2
	if [ "$range" = "" ]
	then
		while [ 0 ]
		do
			echo "## $description"
			echo "## Can't get the ip range for vm"
			echo -n "## Do you want to exit (y/n)?"
			read out
			if [ "$out" != "y" -a "$out" != "Y" ]
			then
				echo -n "## Input the ip range (e.g. 192.168.0.1-100): "
				read range
				if chkRange $range
				then
					break
				fi
			else
				exit 1
			fi
		done
	fi

	echo ""

	macPrefix="EE:EE"

	tmp=`echo $range | awk -F '-' '{print $1}'`
	ipSep1=`echo $tmp | awk -F '.' '{print $1}'`
	ipSep2=`echo $tmp | awk -F '.' '{print $2}'`
	ipSep3=`echo $tmp | awk -F '.' '{print $3}'`
	ipSep4=`echo $tmp | awk -F '.' '{print $4}'`
	ipSep5=`echo $range | awk -F '-' '{print $2}'`

	ipPrefix="$ipSep1.$ipSep2.$ipSep3"

	macPrefix="$macPrefix:"`echo "obase=16;ibase=10;$ipSep1"| bc`
	macPrefix="$macPrefix:"`echo "obase=16;ibase=10;$ipSep2"| bc`
	macPrefix="$macPrefix:"`echo "obase=16;ibase=10;$ipSep3"| bc`

	suffix=$ipSep4
	while [ $suffix -le $ipSep5 ]
	do
		ip="$ipSep1.$ipSep2.$ipSep3.$suffix"
		host="vm-node$suffix"
		mac="$macPrefix:"`echo "obase=16;ibase=10;$suffix"| bc`
		fixIp2Host $host $mac $ip
		suffix=$(($suffix + 1))
	done
}

LINGCLOUD_HOME="$PWD/../../.."
LINGCLOUD_CONF_DIR="$LINGCLOUD_HOME/conf"
LINGCLOUD_CONF_MOLVA="$LINGCLOUD_CONF_DIR/molva.conf"

range=`grep "publicIpPool=" "$LINGCLOUD_CONF_MOLVA" | egrep -v "^[[:blank:]]*#" | head -1 | cut -d= -f2-`
range=`eval echo $range`
ipRange2Host "$range" "Public ip pool"

range=`grep "makeApplianceIpPool=" "$LINGCLOUD_CONF_MOLVA" | egrep -v "^[[:blank:]]*#" | head -1 | cut -d= -f2-`
range=`eval echo $range`
ipRange2Host "$range" "Make appliance ip pool"

exit 0

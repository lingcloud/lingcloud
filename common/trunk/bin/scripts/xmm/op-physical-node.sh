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

failed="false"
sucess="true"

function stop_pnnode
{
	# $1 is the ip of the physical machine
	ssh $1 halt -p
	status=$?
	if [ $status != "0" ]
		then { echo "$failed";  }
	else {
		echo "$sucess";
	}
}
function start_pnnode
{
	# $1 is the mac of the physical machine
	ether-wake $1
	status=$?
	if [ $status != "0" ]
		then { echo "$failed";  }
	else {
		echo "$sucess";
	}
}
function ping_pnnode
{
	# $1 is the ip of the physical machine
	ping -c 5 $1 >/dev/null  2>/dev/null
	status=$?
	if [ $status != "0" ]
		then { echo "$failed";  }
	else {
		echo "$sucess";
	}
}


if [ $2 = "stop" ] 
 then
	stop_pnnode $1;
else
	{
		if [ $2 = "start" ] 
			then {
				start_pnnode $1;
			}
		else {
			ping_pnnode $1;
		} 
	}
fi
exit 0

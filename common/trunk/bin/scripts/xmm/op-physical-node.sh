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
success="true"

# 
IP_OR_MAC="$1"
OPERATION="$2"

function stop_pnnode
{
	# $1 is the ip of the physical machine
	ssh $IP_OR_MAC halt -p
	if [ "$?" != "0" ]
	then
		echo "$failed"
	else
		echo "$success"
	fi
}

function start_pnnode
{
	# $1 is the mac of the physical machine
	ether-wake $IP_OR_MAC
	if [ "$?" != "0" ]
	then
		echo "$failed"
	else
		echo "$success"
	fi
}

function ping_pnnode
{
	# $1 is the ip of the physical machine
	ping -w 2 $IP_OR_MAC >/dev/null 2>/dev/null
	if [ "$?" != "0" ]
	then
		echo "$failed"
	else
		echo "$success"
	fi
}

if [ "$OPERATION" = "stop" ]
then
	stop_pnnode
else
	if [ "$OPERATION" = "start" ]
	then
		start_pnnode
	else
		if [ "$OPERATION" = "ping" ]
		then ping_pnnode
		fi
	fi
fi

exit 0

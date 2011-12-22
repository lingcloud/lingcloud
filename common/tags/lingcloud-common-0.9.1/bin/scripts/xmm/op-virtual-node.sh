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

HOSTIP="$1"
VMID="$2"
OPERATION="$3"

function stop_vnnode
{
	ssh $HOSTIP xm shutdown -w $VMID >/dev/null 2>/dev/null
	if [ "$?" != "0" ]
	then
		echo "$failed"
	else
		echo "$success"
	fi
}

if [ "$OPERATION" = "shutdown" ]
then
	stop_vnnode
fi

exit 0

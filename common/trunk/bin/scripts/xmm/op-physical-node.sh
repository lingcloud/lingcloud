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
}
function start_pnnode
{
	# $1 is the mac of the pnysical machine
	ether-wake $1
}

if [ $2 = "stop" ] ; then
	stop_pnnode $1;
else
	start_pnnode $1;
fi
echo "$sucess"
exit 0

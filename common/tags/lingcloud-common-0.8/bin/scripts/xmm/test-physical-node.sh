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

function test_ssh {
	# $1 is the physical node ip
	ssh -o StrictHostKeyChecking=no 	\
		-o PasswordAuthentication=no 	\
		-o ConnectTimeout=2 $1 "exit"
	if [ $? -ne 0 ] ; then
		echo "$failed: SSH can't login without password."
		exit 1
	fi
}

function test_partition {
	# $1 is the type of patition
	case $2 in
		VM)
			# for VM partition test
			ssh root@$1 "export PATH=\"/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:\$PATH\"; type xm" > /dev/null
			if [ $? -ne 0 ]; then
				echo "$failed: xen can find in $1"
				exit 2;
			fi
		;;
		Common)
			# for regular partition test
			# TODO
			
		;;
		*)
			# TODO
			
		;;
	esac
}

if [ $# -lt 1 ] ; then
	echo "Usage: test-physical-node.sh ip [type]"
	exit -1;
fi

test_ssh $1
test_partition $1 $2

echo "$sucess"
exit 0

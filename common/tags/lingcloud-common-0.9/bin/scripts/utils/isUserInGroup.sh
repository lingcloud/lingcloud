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

USERNAME="$1"
GROUPNAME="$2"

for n in `id -Gn "$USERNAME"`
do
	if [ "$?" != "0" ]
	then
		break
	fi

	if [ "$n" = "$GROUPNAME" ]
	then
		echo "true"
		exit 0
	fi
done

echo "false"
exit 1


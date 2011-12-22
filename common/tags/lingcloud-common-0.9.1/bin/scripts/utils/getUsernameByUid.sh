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

_UID="$1"

echo "$_UID" | egrep -q "^[0-9]+$"
if [ "$?" != "0" ]
then
	exit 1
fi

perl -e "(\$login,\$pass,\$uid,\$gid) = getpwuid($_UID); print \"\$login\\n\";"

exit $?


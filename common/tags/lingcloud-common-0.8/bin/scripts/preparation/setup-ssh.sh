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
# Setup SSH autologin
# Usage: setup-ssh.sh [SERVER]

SERVER="$1"

while [ "x$SERVER" = "x" ];
do
	echo -n "Please input SSH login node (LingCloud server) IP: "
	read SERVER
done

echo "SSH login by key setup."
echo "After setup, you can login without password from server root@$SERVER to local root@$HOSTNAME."

if [ ! -e /root/.ssh/id_rsa.pub ]
then
	echo "Call ssh-keygen (please accept the default file location.)"
	ssh-keygen -t rsa -P ''
fi

echo "Copy server key to local."
rm -f /tmp/id_rsa.pub.$SERVER
scp root@$SERVER:/root/.ssh/id_rsa.pub /tmp/id_rsa.pub.$SERVER
if [ "$?" != "0" ]
then
	echo "Failed to run scp."
	exit 1
fi

echo "Append server key to local authorized keys."
cat /tmp/id_rsa.pub.$SERVER >> /root/.ssh/authorized_keys
if [ "$?" != "0" ]
then
	echo "Failed to append /root/.ssh/authorized_keys."
	exit 1
fi
chmod 700 /root/.ssh
chmod 600 /root/.ssh/authorized_keys

echo "SSH login by key setup done."

exit 0


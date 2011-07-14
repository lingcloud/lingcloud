#!/bin/sh

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


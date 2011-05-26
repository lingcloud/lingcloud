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
# Script for LingCloud start/stop
# Usage: lingcloud.sh [start|stop|status|restart]

pushd `dirname "$0"`

# Environment

LINGCLOUD_ACTION="$1"

LINGCLOUD_HOME="$PWD/.."
LINGCLOUD_TOMCAT_HOME="$LINGCLOUD_HOME/dist/bin/tomcat"
LINGCLOUD_CONF_DIR="$LINGCLOUD_HOME/conf"
LINGCLOUD_CONF_MOLVA="$LINGCLOUD_CONF_DIR/molva.conf"

export JAVA_HOME="$LINGCLOUD_HOME/dist/bin/jre"
export JAVA_OPTS="-Dlingcloud.home=$LINGCLOUD_HOME"
export CLASSPATH="$LINGCLOUD_HOME/dist/bin/jre/lib"
export PATH="$LINGCLOUD_HOME/dist/bin/jre/bin:$PATH"

# Functions

onerror ()
{
	local _ERRINFO="$1"
	[ "$_ERRINFO" = "" ] && _ERRINFO="no detailed error information"
	echo "Error: $_ERRINFO."
	local _ERRINPUTED=""
	while [ "$_ERRINPUTED" != "y" -a "$_ERRINPUTED" != "n" \
		-a "$_ERRINPUTED" != "Y" -a "$_ERRINPUTED" != "N" ]
	do
		echo -n "Do you want to exit (y/n)? "
		read _ERRINPUTED
		if [ "$_ERRINPUTED" = "y" -o "$_ERRINPUTED" = "Y" ]
		then
			exit 1
		elif [ "$_ERRINPUTED" = "n" -o "$_ERRINPUTED" = "N" ]
		then
			return 0
		fi
	done
}

get_conf ()
{
	# Get configuration
	echo "== Get configuration =="
	ONE_LOCATION=`grep "oneDir=" "$LINGCLOUD_CONF_MOLVA" | egrep -v "^[[:blank:]]*#" | head -1 | cut -d= -f2-` || onerror "cannot get oneDir"
	echo "ONE_LOCATION is $ONE_LOCATION"
}

lingcloud_start ()
{
	get_conf

	# Start OpenNebula
	echo "== Start OpenNebula =="
	export ONE_LOCATION
	[ -d "$ONE_LOCATION" ] || onerror "ONE_LOCATION \"$ONE_LOCATION\" is invalid"
	export ONE_AUTH="$ONE_LOCATION/etc/one_auth"
	export ONE_XMLRPC="http://localhost:2633/RPC2"
	export PATH="$ONE_LOCATION/bin:$PATH"
	one start || onerror "one start error"

	# Start Tomcat
	echo "== Start Tomcat =="
	"$LINGCLOUD_TOMCAT_HOME/bin/startup.sh" || onerror "tomcat startup error"

	echo "== Done =="
}

lingcloud_stop ()
{
	get_conf

	# Stop Tomcat
	echo "== Stop Tomcat =="
	"$LINGCLOUD_TOMCAT_HOME/bin/shutdown.sh" || onerror "tomcat stop error"
	echo "Killing ..."
	kill -9 `ps -ef | grep -v "\bgrep\b" | grep "lingcloud" | grep "\borg\.apache\.catalina\.startup\.Bootstrap\b" | awk '{print $2}'` &> /dev/null

	# Stop OpenNebula
	echo "== Stop OpenNebula =="
	export ONE_LOCATION
	[ -d "$ONE_LOCATION" ] || onerror "ONE_LOCATION \"$ONE_LOCATION\" is invalid"
	export ONE_AUTH="$ONE_LOCATION/etc/one_auth"
	export ONE_XMLRPC="http://localhost:2633/RPC2"
	export PATH="$ONE_LOCATION/bin:$PATH"
	one stop || onerror "one stop error"

	echo "== Done =="
}

lingcloud_status ()
{
	ps aux | grep -v "\bgrep\b" | grep "lingcloud" | grep -q "\borg\.apache\.catalina\.startup\.Bootstrap\b"
	if [ "$?" == "0" ]
	then
		echo "Tomcat is running."
	else
		echo "Tomcat is not running."
	fi

	ps aux | grep -v "\bgrep\b" | grep -q "\boned\b"
	if [ "$?" == "0" ]
	then
		echo "OpenNebula is running."
	else
		echo "OpenNebula is not running."
	fi
}

lingcloud_restart ()
{
	lingcloud_stop
	sleep 1
	lingcloud_start
}

# Begin here

case "$LINGCLOUD_ACTION" in
	start)
		lingcloud_start
		;;
	stop)
		lingcloud_stop
		;;
	status)
		lingcloud_status
		;;
	restart)
		lingcloud_restart
		;;
	*)
		echo "Usage: lingcloud.sh [start|stop|status|restart]"
		;;
esac

popd

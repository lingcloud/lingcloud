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
# Usage: ./install-monitor.sh [MONITOR_SYSTEM: ganglia] [MONITOR_ROLE: server|client] [MONITOR_DIR]

gangError() {
	local _ERRINFO="$1"
	[ "$_ERRINFO" = "" ] && _ERRINFO="no detailed information"
	echo "ERROR: $_ERRINFO."
	local _ERRORINPUTED=""
	while [ true ] 
	do
		echo -n "Do you want to exit (y/n)? "
		read _ERRORINPUTED
		if [ "$_ERRORINPUTED" = "y" -o "$_ERRORINPUTED" = "Y" ]
		then
			exit 1
		elif [ "$_ERRORINPUTED" = "n" -o "$_ERRORINPUTED" = "N" ]
		then
			return 0
		fi
	done
}

onUsage() {
	echo "# Install monitor server for LingCloud"
	echo "# Usage: [MONITOR_SYSTEM: ganglia] [MONITOR_ROLE: server|client] [MONITOR_DIR]"
	return 0
}

addOnce() {
	if [ "$#" != "2" ]
	then
		return 1
	fi
	sed -i "s;^[[:blank:]]*$1;;g" "$2" &> /dev/null
	echo "$1" >> "$2"
	return 0;
}

MONITOR_SYSTEM="$1"
MONITOR_ROLE="$2"
MONITOR_DIR="$3"

if [ "$#" != "3" ]
then
	echo "# Arguments are not correct."
	onUsage
	exit 1
fi

if [ "$MONITOR_SYSTEM" != "ganglia" ]
then
	echo "# Monitor system is not supported."
	onUsage
	exit 1
fi

if [ "$MONITOR_ROLE" != "server" -a "$MONITOR_ROLE" != "client" ] 
then
	echo "# Monitor role is not correct."
	onUsage
	exit 1
fi

CONFUSE_HOME="$MONITOR_DIR/confuse"
GANGLIA_HOME="$MONITOR_DIR/ganglia"

onConfigureAfterInstall() {
	local ROLE="$1"
	if [ "$MONITOR_ROLE" = "server" ] 
	then
		addOnce "$GANGLIA_HOME/sbin/start-gmond-as-server.sh" "/etc/rc.local"
		$GANGLIA_HOME/sbin/start-gmond-as-server.sh
		addOnce "$GANGLIA_HOME/sbin/start-gmetad.sh" "/etc/rc.local"
		$GANGLIA_HOME/sbin/start-gmetad.sh
		echo "# LingCloud monitor server needs to open TCP and UDP ports 8649 and 8651."
		echo -n "# Do you want to open them (8649 and 8651) now (y/n)? "
		read _INPUT_
		if [ "$_INPUT_" = "Y" -o "$_INPUT_" = "y" ]
		then
			iptables -I INPUT -p tcp --dport 8649 -j ACCEPT
			iptables -I INPUT -p tcp --dport 8651 -j ACCEPT
			iptables -I INPUT -p udp --dport 8649 -j ACCEPT
			iptables -I INPUT -p udp --dport 8651 -j ACCEPT
			service iptables save
			echo "# The ports 8649 and 8651 are opened and saved."
		else
			echo "# The ports 8649 and 8651 are not opened. Please open them manually."
		fi
	else
		addOnce "$GANGLIA_HOME/sbin/start-gmond-as-client.sh" "/etc/rc.local"
		$GANGLIA_HOME/sbin/start-gmond-as-client.sh
	fi
	return 0
}

case "$MONITOR_SYSTEM" in
ganglia)

	yum install apr-devel apr-util check-devel cairo-devel pango-devel libxml2-devel     \
		glib2-devel dbus-devel freetype-devel fontconfig-devel gcc-c++ expat-devel 	\
		pcre-devel python-devel libXrender-devel
	if [ -d "$GANGLIA_HOME/sbin" ]
	then
		# The dirtory exists.
		onConfigureAfterInstall $MONITOR_ROLE
	else
		# Install the monitor system.
		OLD_DIR=`dirname $0`
		TMP_DIR="/tmp/lingcloud-monitor"
		[ -e "$TMP_DIR" ] && command rm -rf "$TMP_DIR"
		mkdir -p "$TMP_DIR"

		pushd "$OLD_DIR"

		command cp dependencies/src/confuse*.tar.gz "$TMP_DIR"
		command cp dependencies/src/rrdtool*.tar.gz "$TMP_DIR"
		command cp dependencies/src/ganglia*.tar.gz "$TMP_DIR"

		pushd "$TMP_DIR"

		export PKG_CONFIG_PATH=/usr/lib/pkgconfig		# for CentOS 5.3

		# Install RRDtool
		tar zxvf rrdtool-*.tar.gz 
		pushd rrdtool-*/
		./configure --prefix=/usr/local/rrdtool || gangError "RRDtool configure failed!"
		make || gangError "RRDtool make failed!"
		make install || gangError "RRDtool make install failed!"
		ldconfig						# make sure you have the new rrdtool libraries linked.
		popd

		# Install Confuse
		tar zxvf confuse-*.tar.gz
		pushd confuse-*/
		./configure --prefix="$CONFUSE_HOME" CFLAGS=-fPIC --disable-nls || gangError "Confuse configure failed!"
					#...CFLAGS=-fPIC --disable-nls fixes error -> libconfuse.a: could not read symbols: Bad value
		make || gangError "Confuse make failed!"
		make install || gangError "Confuse make install failed!"
		ln -s "$CONFUSE_HOME/lib" "$CONFUSE_HOME/lib64"
		popd

		# Install Ganglia
		tar xzvf ganglia-*.tar.gz
		pushd ganglia-*/
		./configure --prefix="$GANGLIA_HOME"		\
			--sysconfdir="$GANGLIA_HOME/etc"	\
			CFLAGS="-I/usr/include" 		\
			CPPFLAGS="-I/usr/include" 		\
			LDFLAGS="-L/usr/lib64" 			\
			--with-libconfuse="$CONFUSE_HOME"	\
			--with-librrd=/usr/local/rrdtool	\
			--with-expat=builtin			\
			--with-gmetad 				\
			--enable-gexec || gangError "Ganglia configure failed!"
		make || gangError "Ganglia make failed!"
		make install || gangError "Ganglia make install failed!"
		popd

		popd

		# Configure for Ganglia
		GANGLIA_PYTHON_MODULE_DIR="$GANGLIA_HOME/lib64/ganglia/python_modules"
		mkdir -p "$GANGLIA_HOME/rrds"
		mkdir -p "$GANGLIA_PYTHON_MODULE_DIR"
		chown nobody:nobody "$GANGLIA_HOME/rrds"
		
		command cp conf/monitor/start-gm*.sh "$GANGLIA_HOME/sbin"
		command cp conf/monitor/gm*.conf "$GANGLIA_HOME/etc"
		command cp conf/monitor/*.pyconf "$GANGLIA_HOME/etc/conf.d"
		command cp conf/monitor/*.py "$GANGLIA_PYTHON_MODULE_DIR"
		
		echo "rrd_rootdir \"$GANGLIA_HOME/rrds\"" >> "$GANGLIA_HOME/etc/gmetad.conf"
		sed -i "s;^GANGLIA_HOME=.*;GANGLIA_HOME=$GANGLIA_HOME;g" $GANGLIA_HOME/sbin/start-g*.sh

		onConfigureAfterInstall "$MONITOR_ROLE"

		popd

		command rm -rf "$TMP_DIR"
	fi
	;;

esac

exit 0


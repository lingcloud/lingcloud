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
# Install dependencies from distribution software source
# Usage: install-dependencies.sh [NODE_TYPE: main|xen] [METHOD: yum|zypper|apt] <OPENNEBULA_DIR>

pushd `dirname "$0"`

NODE_TYPE="$1"
METHOD="$2"
OPENNEBULA_DIR="$3"

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

case "`uname -m`" in
	x86_64|amd64)	ARCH="x86_64"	;;
	*)		ARCH="i386"	;;
esac 

echo "==== Install dependencies for $NODE_TYPE node by $METHOD ($ARCH) ===="

case $NODE_TYPE in
main)
	case $METHOD in
	yum)
		# Common
		echo "  * yum for common"
		yum install openssh-server openssh-clients gcc gcc-c++ libstdc++ libstdc++-devel flex bison || onerror "yum error"
		# Molva
		echo "  * yum for Molva"
		yum install mysql* || onerror "yum error"
		# OpenNebula
		echo "  * yum for OpenNebula"
		yum install mysql* ruby* openssl openssl-devel curl-devel unixODBC unixODBC-devel libxml2-devel || onerror "yum error"
		echo "  * rpm for xmlrpc-c xmlrpc-c-devel"
		rpm -Uhv dependencies/rpms/xmlrpc-c-*.$ARCH.rpm || onerror "rpm error (you should ignore this error if the packages are already installed)"
		echo "  * yum for scons"
		rpm -Uhv dependencies/rpms/scons-*.rpm || onerror "rpm error (you should ignore this error if the packages are already installed)"
		pushd dependencies/src
		tar xzf sqlite-autoconf-*.tar.gz || onerror "tar error"
		tar xzf sqlite3-ruby-*.tar.gz || onerror "tar error"
		tar xzf opennebula-*.tar.gz || onerror "tar error"
			pushd sqlite-autoconf-*/
			echo "  * src for sqlite"
			./configure --prefix=/usr || onerror "configure error"
			make || onerror "make error"
			make install || onerror "make install error"
			popd
			pushd sqlite3-ruby-*/
			echo "  * src for sqlite3-ruby"
			ruby setup.rb config || onerror "ruby config error"
			ruby setup.rb setup || onerror "ruby setup error"
			ruby setup.rb install || onerror "ruby install error"
			popd
			pushd opennebula-*/
			echo "  * src for opennebula"
			scons mysql=yes || onerror "scons error"
			mkdir -p "$OPENNEBULA_DIR" || onerror "mkdir error"
			./install.sh -d "$OPENNEBULA_DIR" || onerror "install error"
			popd
		rm -rf sqlite-autoconf-*/ sqlite3-ruby-*/ opennebula-*/
		popd
		chkconfig mysqld on
		;;
	zypper)
		echo "unimplemented method"
		exit 1
		;;
	apt)
		echo "unimplemented method"
		exit 1
		;;
	*)
		echo "bad method"
		exit 1
		;;
	esac
	;;
xen)
	case $METHOD in
	yum)
		# Common
		echo "  * yum for common"
		yum install openssh-server openssh-clients gcc gcc-c++ libstdc++ libstdc++-devel flex bison || onerror "yum error"
		# OpenNebula
		echo "  * yum for OpenNebula (xen)"
		yum install ruby* qemu* kvm-qemu-img || onerror "yum error"
		;;
	zypper)
		echo "unimplemented method"
		exit 1
		;;
	apt)
		echo "unimplemented method"
		exit 1
		;;
	*)
		echo "bad method"
		exit 1
		;;
	esac
	;;
nfs)
	case $METHOD in
	yum)
		# Common
		echo "  * yum for common"
		yum install openssh-server openssh-clients || onerror "yum error"
		# NFS
		echo "  * yum for NFS"
		yum install nfs-utils nfs-utils-lib portmap system-config-nfs || onerror "yum error"
		# QEMU
		echo "  * yum for QEMU"
		yum install qemu* kvm-qemu-img || onerror "yum error"
		;;
	zypper)
		echo "unimplemented method"
		exit 1
		;;
	apt)
		echo "unimplemented method"
		exit 1
		;;
	*)
		echo "bad method"
		exit 1
		;;
	esac
	;;
app)
	case $METHOD in
	yum)
		# Common
		echo "  * yum for common"
		yum install openssh-server openssh-clients || onerror "yum error"
		# OpenNebula
		echo "  * yum for QEMU"
		yum install qemu* kvm-qemu-img || onerror "yum error"
		;;
	zypper)
		echo "unimplemented method"
		exit 1
		;;
	apt)
		echo "unimplemented method"
		exit 1
		;;
	*)
		echo "bad method"
		exit 1
		;;
	esac
	;;
general)
	case $METHOD in
	yum)
		# Common
		echo "  * yum for common"
		yum install openssh-server openssh-clients || onerror "yum error"
		;;
	zypper)
		echo "unimplemented method"
		exit 1
		;;
	apt)
		echo "unimplemented method"
		exit 1
		;;
	*)
		echo "bad method"
		exit 1
		;;
	esac
	;;
*)
	echo "bad node type"
	exit 1
	;;
esac

popd

exit 0


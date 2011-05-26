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
# Preparation script for LingCloud nodes
# Usage: preparation.sh <_CONFIG_INFO_FILE>

# 0. Predefinitions

pushd `dirname "$0"`

_CONFIG_INFO_FILE="$1"

echo "= Preparation script for LingCloud nodes ="

if [ "$_CONFIG_INFO_FILE" = "" ]
then
	_CONFIG_INFO_FILE="preparation.info"
fi

if [ -e "$_CONFIG_INFO_FILE" ]
then
	echo "Use preparation information from \"$_CONFIG_INFO_FILE\"."
	source "$_CONFIG_INFO_FILE"
else
	echo "Preparation information file \"$_CONFIG_INFO_FILE\" not found, ignore."
fi

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

do_set ()
{
	local _SETNAME="$1"
	local _SETINFO="$2"
	[ "$_SETINFO" = "" ] && _SETINFO="$_SETNAME"
	local _WHETHER=""
	eval "_WHETHER=\"\${$_SETNAME}\""
	if [ "$_WHETHER" = "1" ]
	then
		echo "== Execute: $_SETINFO =="
		return 0
	elif [ "$_WHETHER" = "0" ]
	then
		echo "== Ignore: $_SETINFO =="
		return 1
	else
		local _WHETHER_SET=""
		while [ "$_WHETHER_SET" != "y" -a "$_WHETHER_SET" != "n" \
			-a "$_WHETHER_SET" != "Y" -a "$_WHETHER_SET" != "N" ]
		do
			echo -n "Do you want to $_SETINFO (y/n)? "
			read _WHETHER_SET
			if [ "$_WHETHER_SET" = "y" -o "$_WHETHER_SET" = "Y" ]
			then
				echo "== Execute: $_SETINFO =="
				return 0
			elif [ "$_WHETHER_SET" = "n" -o "$_WHETHER_SET" = "N" ]
			then
				echo "== Ignore: $_SETINFO =="
				return 1
			fi
		done
	fi
}

get_val ()
{
	# WARNING: For some special punctuations in bash like quotation marks,
	# You must write it with the escape character.
	local _VALNAME="$1"
	local _VALINFO="$2"
	[ "$_VALINFO" = "" ] && _VALINFO="$_VALNAME"
	local _VALREGEX="$3"
	[ "$_VALREGEX" = "" ] && _VALREGEX="[[:print:]]*"
	local _VAL=""
	eval "_VAL=\"\${$_VALNAME}\""
	echo -n "$_VAL" | egrep -q "$_VALREGEX"
	if [ "$?" = "0" ]
	then
		echo "$_VALINFO ($_VALNAME) is set: $_VAL"
	else
		local _VAL_MATCHED="1"
		while [ "$_VAL_MATCHED" != "0" ]
		do
			echo "$_VALINFO ($_VALNAME) is unset or invalid."
			echo -n "Please set $_VALINFO ($_VALNAME): "
			read _VAL
			echo -n "$_VAL" | egrep -q "$_VALREGEX"
			_VAL_MATCHED="$?"
		done
		eval "$_VALNAME=\"$_VAL\""
	fi
	return 0
}

get_file ()
{
	local _FILE_VALNAME="$1"
	local _FILE_VALINFO="$2"
	local _FILE_VAL=""
	get_val "$_FILE_VALNAME" "$_FILE_VALINFO" "[[:print:]]*"
	eval "_FILE_VAL=\"\${$_FILE_VALNAME}\""
	while [ ! -e "$_FILE_VAL" ];
	do
		echo "File or directory \"$_FILE_VAL\" is not exists."
		eval "$_FILE_VALNAME=\"\""
		get_val "$_FILE_VALNAME" "$_FILE_VALINFO" "[[:print:]]*"
		eval "_FILE_VAL=\"\${$_FILE_VALNAME}\""
	done
	return 0
}

find_and_set ()
{
	local _FIND_FILE="$1"
	local _FIND_PATTERN="$2"
	local _FIND_REPLACE="$3"
	local _FIND_OPTION="$4"
	local _FIND_LINE=""

	# Option:	a - if not found, append it;
	#		i - if not found, ignore it;

	_FIND_LINE=`egrep -n "$_FIND_PATTERN" "$_FIND_FILE" | egrep -v "^[[:digit:]]*:[[:blank:]]*#" | head -1 | cut -f1 -d':'`
	if [ "$_FIND_LINE" != "" ]
	then
		echo "Find \"$_FIND_PATTERN\" in \"$_FIND_FILE\" and set it to \"$_FIND_REPLACE\"."
		sed -i "${_FIND_LINE}s/^/#/" "$_FIND_FILE" || onerror "failed to modify \"$_FIND_FILE\""
		sed -i "${_FIND_LINE}i\\${_FIND_REPLACE}" "$_FIND_FILE" || onerror "failed to modify \"$_FIND_FILE\""
		return 0
	else
		echo -n "$_FIND_OPTION" | grep -q "a"
		if [ "$?" = "0" ]
		then
			echo "No \"$_FIND_PATTERN\" found, append \"$_FIND_REPLACE\" into \"$_FIND_FILE\"."
			echo "$_FIND_REPLACE" >> "$_FIND_FILE" || onerror "failed to write \"$_FIND_FILE\""
			return 0
		fi
		echo -n "$_FIND_OPTION" | grep -q "i"
		if [ "$?" = "0" ]
		then
			echo "No \"$_FIND_PATTERN\" found, ignore setting \"$_FIND_FILE\"."
			return 0
		fi
	fi

	echo "Set nothing in \"$_FIND_FILE\""
	return 1
}

# Prepare LingCloud environment

do_set "SET_FSTAB_NFS" "set NFS auto mount"
if [ "$?" = "0" ]
then
	get_val "FSTAB_NFS_SERVER" "NFS server" "[[:print:]]*"
	get_val "FSTAB_NFS_MOUNT_DIR" "NFS directory (remote export path and local mount path must be the same)" "[[:print:]]*"
	FSTAB_NFS_SERVER_DIR="$FSTAB_NFS_MOUNT_DIR"
	if [ ! -d "$FSTAB_NFS_MOUNT_DIR" ]
	then
		echo "Make directory \"$FSTAB_NFS_MOUNT_DIR\"."
		mkdir -p "$FSTAB_NFS_MOUNT_DIR" || onerror "failed to make directory"
	fi
	FSTAB_NFS_STRING="$FSTAB_NFS_SERVER:$FSTAB_NFS_SERVER_DIR	$FSTAB_NFS_MOUNT_DIR	nfs	soft,intr,rsize=32768,wsize=32768,rw	0	0"
	egrep -v "^[[:blank:]]*#" /etc/fstab | egrep -q "$FSTAB_NFS_SERVER:$FSTAB_NFS_SERVER_DIR[[:blank:]]*$FSTAB_NFS_MOUNT_DIR"
	if [ "$?" = "0" ]
	then
		echo "This NFS auto mount record is already in /etc/fstab. If it does not work, please add \"$FSTAB_NFS_STRING\" manually."
	else
		echo "Modify /etc/fstab."
		echo "$FSTAB_NFS_STRING" >> /etc/fstab || onerror "failed to modify /etc/fstab"
	fi
	echo "Mount NFS."
	mount -a || onerror "failed to mount NFS"
fi

do_set "SET_INSTALL_DEPENDENCIES" "install dependencies (e.g. mysql)"
if [ "$?" = "0" ]
then
	get_file "INSTALL_DEPENDENCIES_SCRIPT" "install dependencies script"
	echo "ARGS: $INSTALL_DEPENDENCIES_SCRIPT_ARGS"
	bash "$INSTALL_DEPENDENCIES_SCRIPT" $INSTALL_DEPENDENCIES_SCRIPT_ARGS || onerror "failed to install dependencies"
fi

do_set "SET_LOCAL_HOSTNAME" "set local hostname"
if [ "$?" = "0" ]
then
	get_val "LOCAL_HOSTNAME" "local hostname" "[[:print:]]*"
	LOCAL_HOSTNAME_BAD="1"
	if [ -w "/etc/hostname" ]
	then
		echo "Modify /etc/hostname."
		echo "$LOCAL_HOSTNAME" > "/etc/hostname" || onerror "failed to write /etc/hostname"
		LOCAL_HOSTNAME_BAD="0"
	fi
	if [ -w "/etc/sysconfig/network" ]
	then
		echo "Modify /etc/sysconfig/network."
		find_and_set "/etc/sysconfig/network" "HOSTNAME=" "HOSTNAME=$LOCAL_HOSTNAME" "a" || onerror "failed to modify /etc/sysconfig/network"
		LOCAL_HOSTNAME_BAD="0"
	fi
	[ "$LOCAL_HOSTNAME_BAD" = "1" ] && onerror "neither /etc/hostname nor /etc/sysconfig/network is available"
	echo "Enable hostname."
	hostname "$LOCAL_HOSTNAME" || onerror "failed to enable hostname"
fi

do_set "SET_HOSTS_FILE" "copy hosts file"
if [ "$?" = "0" ]
then
	get_file "HOSTS_FILE" "hosts file"
	get_val "HOSTS_FILE_APPEND" "whether appending to /etc/hosts [1=appending, 0=replacing] (1/0)" "^[01]$"
	if [ "$HOSTS_FILE_APPEND" = "1" ]
	then
		echo "Append \"$HOSTS_FILE\" into /etc/hosts"
		cat "$HOSTS_FILE" >> /etc/hosts || onerror "failed to write /etc/hosts"
	else
		echo "Use \"$HOSTS_FILE\" to replace /etc/hosts"
		cat "$HOSTS_FILE" > /etc/hosts || onerror "failed to write /etc/hosts"
	fi
fi

do_set "SET_SSH_HOSTKEY_NO" "set SSH StrictHostKeyChecking no"
if [ "$?" = "0" ]
then
	get_val "SET_SSH_HOSTKEY_NO_USER" "username for whom to set SSH StrictHostKeyChecking no"
	if [ "$SET_SSH_HOSTKEY_NO_USER" = "root" ]
	then
		HOSTKEY_NO_USER_HOME="/root"
	else
		HOSTKEY_NO_USER_HOME="/home/$SET_SSH_HOSTKEY_NO_USER"
	fi
	if [ ! -d "$HOSTKEY_NO_USER_HOME/.ssh" ]
	then
		mkdir -p "$HOSTKEY_NO_USER_HOME/.ssh" || onerror "failed to create .ssh"
		chmod 700 "$HOSTKEY_NO_USER_HOME/.ssh" || onerror "failed to chmod .ssh"
		chown "$SET_SSH_HOSTKEY_NO_USER:$SET_SSH_HOSTKEY_NO_USER" "$HOSTKEY_NO_USER_HOME/.ssh" || onerror "failed to chown .ssh"
	fi
	if [ ! -f "$HOSTKEY_NO_USER_HOME/.ssh/config" ]
	then
		touch "$HOSTKEY_NO_USER_HOME/.ssh/config" || onerror "failed to create .ssh/config"
		chown "$SET_SSH_HOSTKEY_NO_USER:$SET_SSH_HOSTKEY_NO_USER" "$HOSTKEY_NO_USER_HOME/.ssh/config" || onerror "failed to chown .ssh/config"
	fi
	find_and_set "$HOSTKEY_NO_USER_HOME/.ssh/config" "StrictHostKeyChecking" "StrictHostKeyChecking no" "a" || onerror "failed to modify .ssh/config"
fi

do_set "SET_SUDOERS_NOTTY" "disable sudo requiretty"
if [ "$?" = "0" ]
then
	find_and_set "/etc/sudoers" "Defaults[[:blank:]]*requiretty" "\# requiretty is disabled" "i" || onerror "failed to modify /etc/sudoers"
fi

do_set "SET_XEN_MEM_GRUB" "set Xen dom0_mem in Grub"
if [ "$?" = "0" ]
then
	get_file "XEN_MEM_GRUB_FILE" "Grub configuration file"
	get_val "XEN_MEM_GRUB_SIZE" "Xen dom0_mem" "^[[:digit:]]+[BKMG]?$"

	KERNEL_LINE_OLD=`grep "kernel.*xen\.gz" "$XEN_MEM_GRUB_FILE" | egrep -v "^[[:blank:]]*#" | head -1`
	KERNEL_LINE_NEW="	"
	if [ "$KERNEL_LINE_OLD" != "" ]
	then
		echo "Old Xen kernel configuration: \"$KERNEL_LINE_OLD\""
		for KERNEL_LINE_TOKEN in $KERNEL_LINE_OLD
		do
			echo "$KERNEL_LINE_TOKEN" | grep -q "dom0_mem="
			if [ "$?" != "0" ]
			then
				KERNEL_LINE_NEW="$KERNEL_LINE_NEW $KERNEL_LINE_TOKEN"
			fi
		done
		KERNEL_LINE_NEW="$KERNEL_LINE_NEW dom0_mem=$XEN_MEM_GRUB_SIZE"
		echo "New Xen kernel configuration: \"$KERNEL_LINE_NEW\""
		find_and_set "$XEN_MEM_GRUB_FILE" "kernel.*xen\.gz" "$KERNEL_LINE_NEW" "" || onerror "failed to modify \"$XEN_MEM_GRUB_FILE\""
	else
		onerror "no Xen kernel found in Grub configuration file"
	fi
fi

do_set "SET_XEN_VNC" "set Xen VNC listening"
if [ "$?" = "0" ]
then
	find_and_set "/etc/xen/xend-config.sxp" "vnc-listen" "(vnc-listen '0.0.0.0')" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	find_and_set "/etc/xen/xend-config.sxp" "vncpasswd" "(vncpasswd '')" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	echo "Restart Xen."
	/etc/init.d/xend restart || onerror "failed to restart Xen"
fi

do_set "SET_XEN_IMAGE_FORMAT_PROBING" "set Xen image format probing"
if [ "$?" = "0" ]
then
	find_and_set "/etc/xen/xend-config.sxp" "enable-image-format-probing" "(enable-image-format-probing yes)" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	echo "Restart Xen."
	/etc/init.d/xend restart || onerror "failed to restart Xen"
fi

do_set "SET_XEN_RELOCATION" "set Xen relocation"
if [ "$?" = "0" ]
then
	get_val "SET_XEN_RELOCATION_PORT" "Xen relocation port" "^[[:digit:]]+$"
	get_val "SET_XEN_RELOCATION_HOSTS_ALLOW" "Xen relocation allowd hosts (IPs)" "[[:print:]]*"
	find_and_set "/etc/xen/xend-config.sxp" "xend-relocation-server" "(xend-relocation-server yes)" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	find_and_set "/etc/xen/xend-config.sxp" "xend-relocation-port" "(xend-relocation-port $SET_XEN_RELOCATION_PORT)" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	find_and_set "/etc/xen/xend-config.sxp" "xend-relocation-address" "(xend-relocation-address '')" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	find_and_set "/etc/xen/xend-config.sxp" "xend-relocation-hosts-allow" "(xend-relocation-hosts-allow '$SET_XEN_RELOCATION_HOSTS_ALLOW')" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	find_and_set "/etc/xen/xend-config.sxp" "network-script" "(network-script network-bridge)" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	find_and_set "/etc/xen/xend-config.sxp" "vif-script" "(vif-script vif-bridge)" "a" || onerror "failed to modify /etc/xen/xend-config.sxp"
	echo "Restart Xen."
	/etc/init.d/xend restart || onerror "failed to restart Xen"
fi

do_set "SET_MYSQL_PASSWORD" "setup MySQL root password"
if [ "$?" = "0" ]
then
	get_val "MYSQL_PASSWORD" "MySQL root password"
	service mysqld restart
	mysqladmin -uroot password "$MYSQL_PASSWORD" || onerror "failed to run mysqladmin"
fi

do_set "SET_SETUP_SSH" "setup SSH autologin"
if [ "$?" = "0" ]
then
	get_file "SETUP_SSH_SCRIPT" "setup SSH script"
	bash "$SETUP_SSH_SCRIPT" $SETUP_SSH_SCRIPT_ARGS || onerror "failed to setup SSH autologin"
fi

do_set "SET_SETUP_VNC" "setup VNC autostart"
if [ "$?" = "0" ]
then
	get_file "SETUP_VNC_SCRIPT" "setup VNC script"
	bash "$SETUP_VNC_SCRIPT" $SETUP_VNC_SCRIPT_ARGS || onerror "failed to setup VNC autostart"
fi

# Done

popd

echo "Done."


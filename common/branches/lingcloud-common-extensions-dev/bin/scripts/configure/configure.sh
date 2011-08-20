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
# Configuration script for LingCloud
# Usage: configure.sh <_CONFIG_INFO_FILE>

# 0. Predefinitions

pushd `dirname "$0"`

_CONFIG_INFO_FILE="$1"
_LINGCLOUD_HOME="$PWD/../../.."

echo "= Configuration script for LingCloud ="

if [ "$_CONFIG_INFO_FILE" = "" ]
then
	_CONFIG_INFO_FILE="configure.info"
fi

if [ -e "$_CONFIG_INFO_FILE" ]
then
	echo "Use configuration information from \"$_CONFIG_INFO_FILE\"."
	source "$_CONFIG_INFO_FILE"
else
	echo "Configuration information file \"$_CONFIG_INFO_FILE\" not found, ignore."
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

sub_file ()
{
	local _SUB_FILE_IN="$1"
	local _SUB_FILE_OUT="$2"
	echo "Variables substitution from $_SUB_FILE_IN to $_SUB_FILE_OUT."
	> "$_SUB_FILE_OUT" || return 1
	local _LINE=""
	cat "$_SUB_FILE_IN" | while read _LINE
	do
		eval "echo \"$_LINE\"" >> "$_SUB_FILE_OUT" || return 1
	done
	return 0
}

# 1. Create configuration files from template files

do_set "DO_CONFIG_MOLVA" "create configuration files from template files"
if [ "$?" = "0" ]
then
	get_val "LINGCLOUD_SERVER" "LingCloud install server"
	get_val "LINGCLOUD_HOME_DIR" "LingCloud install directory"
	get_val "LINGCLOUD_MOUNT_IMAGES_DIR" "LingCloud local mount images directory"
	get_val "PUBLIC_IP_POOL" "the public IP pool for runtime virtual machines"
	get_val "MAKE_APPLIANCE_HOST" "the host used to make appliance"
	get_val "MAKE_APPLIANCE_IP_POOL" "the public IP pool for virtual appliances being made"
	get_val "MONITOR_SYSTEM_TYPE" "monitor system type"
	get_val "MONITOR_SERVER" "monitor server"
	get_val "ACCESS_CONTROL_ENABLE" "whether enable access control for portal (true/false)"
	get_val "ACCESS_CONTROL_ADMIN" "the admin group name for access control"
	get_val "ACCESS_CONTROL_USER" "the user group name for access control"
	get_val "UI_LANGUAGE" "Display language of UI (e.g. en, zh)"

	get_val "NFS_SERVER" "NFS server"
	get_val "NFS_MOUNT_DIR" "NFS local mount directory"
	get_val "OPENNEBULA_DIR" "OpenNebula install directory"
	get_val "OPENNEBULA_USERNAME" "existed local username for OpenNebula"
	get_val "OPENNEBULA_PASSWORD" "existed local password for OpenNebula"
	get_val "OPENNEBULA_MYSQL_USERNAME" "existed MySQL username for OpenNebula"
	get_val "OPENNEBULA_MYSQL_PASSWORD" "existed MySQL password for OpenNebula"

	_REGNUM="[0-9]\{1,3\}"
	_IP_1=`expr match "$MAKE_APPLIANCE_IP_POOL" "^\($_REGNUM\)\\.$_REGNUM\\.$_REGNUM\\.$_REGNUM-$_REGNUM$"`
	_IP_2=`expr match "$MAKE_APPLIANCE_IP_POOL" "^$_REGNUM\\.\($_REGNUM\)\\.$_REGNUM\\.$_REGNUM-$_REGNUM$"`
	_IP_3=`expr match "$MAKE_APPLIANCE_IP_POOL" "^$_REGNUM\\.$_REGNUM\\.\($_REGNUM\)\\.$_REGNUM-$_REGNUM$"`
	_IP_START=`expr match "$MAKE_APPLIANCE_IP_POOL" "^$_REGNUM\\.$_REGNUM\\.$_REGNUM\\.\($_REGNUM\)-$_REGNUM$"`
	_IP_END=`expr match "$MAKE_APPLIANCE_IP_POOL" "^$_REGNUM\\.$_REGNUM\\.$_REGNUM\\.$_REGNUM-\($_REGNUM\)$"`
	MAKE_APPLIANCE_VM_COUNT=0
	MAKE_APPLIANCE_VM=""
	if [ -n "$_IP_START"  ] && [ -n "$_IP_END" ] && [ "$_IP_END" -ge "$_IP_START" ]
	then
		MAKE_APPLIANCE_VM_COUNT=$((_IP_END-_IP_START+1))
		_MAC_1=`printf "%02x" $_IP_1`
		_MAC_2=`printf "%02x" $_IP_2`
		_MAC_3=`printf "%02x" $_IP_3`
		for ((_i=_IP_START,_j=1;_i<=_IP_END;_i++,_j++))
		do
			_MAC_4=`printf "%02x" $_i`
			MAKE_APPLIANCE_VM="${MAKE_APPLIANCE_VM}makeApplianceVM$_j=appliance-$_j,02:00:00:01:00:$_MAC_4|ee:ee:$_MAC_1:$_MAC_2:$_MAC_3:$_MAC_4,xenbr0,$((80+_j))
" # newline is necessary
		done
	fi

	pushd "$_LINGCLOUD_HOME/conf"
	ls *.tmpl &> /dev/null
	if [ "$?" = "0" ]
	then
		for _FILE_IN in *.tmpl
		do
			_FILE_OUT="${_FILE_IN/%.tmpl/}"
			> "$_FILE_OUT" || onerror "failed to create configuration file"
			sub_file "$_FILE_IN" "$_FILE_OUT" || onerror "failed to do variables substitution"
		done
	fi
	popd
fi

# 2.1. Configure OpenNebula

do_set "DO_CONFIG_OPENNEBULA" "configure OpenNebula"
if [ "$?" = "0" ]
then
	get_val "NFS_SERVER" "NFS server"
	get_val "NFS_MOUNT_DIR" "NFS local mount directory"
	get_val "OPENNEBULA_DIR" "OpenNebula install directory"
	get_val "OPENNEBULA_USERNAME" "existed local username for OpenNebula"
	get_val "OPENNEBULA_PASSWORD" "existed local password for OpenNebula"
	get_val "OPENNEBULA_MYSQL_USERNAME" "existed MySQL username for OpenNebula"
	get_val "OPENNEBULA_MYSQL_PASSWORD" "existed MySQL password for OpenNebula"

	[ -d "$OPENNEBULA_DIR" ] || onerror "failed to access OpenNebula install directory"	
	echo "$OPENNEBULA_USERNAME:$OPENNEBULA_PASSWORD" > "$OPENNEBULA_DIR/etc/one_auth" || onerror "failed to write one_auth"
	pushd "opennebula-conf" 
	ls *.tmpl &> /dev/null
	if [ "$?" = "0" ]
	then
		for _FILE_IN in *.tmpl
		do
			_FILE_OUT="${_FILE_IN/%.tmpl/}"
			> "$_FILE_OUT" || onerror "failed to create configuration file"
			sub_file "$_FILE_IN" "$_FILE_OUT" || onerror "failed to do variables substitution"
		done
	fi
	chmod a+x *.sh
	popd
	command	cp "opennebula-conf/oned.conf" "$OPENNEBULA_DIR/etc" || onerror "failed to copy oned.conf"
	command cp "opennebula-conf/vmm_ssh_xen.conf" "$OPENNEBULA_DIR/etc/vmm_ssh" || onerror "failed to copy vmm_ssh_xen.conf"
	command cp "opennebula-conf/im_xen.conf" "$OPENNEBULA_DIR/etc/im_xen" || onerror "failed to copy im_xen.conf"
	command cp "opennebula-conf/xmm4nics.sh" "$OPENNEBULA_DIR/lib/remotes/im/xen.d" || onerror "failed to copy xmm4nics.sh"
	command cp "opennebula-conf/tm_clone.sh" "$OPENNEBULA_DIR/lib/tm_commands/nfs" || onerror "failed to copy tm_clone.sh"
	command cp "opennebula-conf/CommandManager.rb" "$OPENNEBULA_DIR/lib/ruby" || onerror "failed to copy CommandManager.rb"
fi

# 2.2. Initialize OpenNebula database

do_set "DO_INIT_OPENNEBULA_DB" "initialize OpenNebula database"
if [ "$?" = "0" ]
then
	get_val "OPENNEBULA_MYSQL_USERNAME" "existed MySQL username for OpenNebula"
	get_val "OPENNEBULA_MYSQL_PASSWORD" "existed MySQL password for OpenNebula"
	
	if [ "$OPENNEBULA_MYSQL_USERNAME" = "root" ]
	then
		MYSQL_ROOT_PASSWORD="$OPENNEBULA_MYSQL_PASSWORD"
	fi
	get_val "MYSQL_ROOT_PASSWORD" "MySQL root passwd"
	
	mysql -v -uroot -p"$MYSQL_ROOT_PASSWORD" < "opennebula-conf/inittable.sql" || onerror "failed to initialize OpenNebula database"
	echo "GRANT ALL PRIVILEGES ON lingcloudone.* TO '$OPENNEBULA_USERNAME'@'%' IDENTIFIED BY \"$OPENNEBULA_MYSQL_PASSWORD\"; FLUSH PRIVILEGES;" | mysql -v -uroot -p"$MYSQL_ROOT_PASSWORD"
fi

# 3. Initialize Molva naming database

do_set "DO_INIT_NAMING_DB" "initalize Molva naming database"
if [ "$?" = "0" ]
then
	get_val "NAMING_MYSQL_USERNAME" "existed MySQL username for Molva naming"
	get_val "NAMING_MYSQL_PASSWORD" "existed MySQL password for Molva naming"

	pushd "naming-conf" 
	ls *.tmpl &> /dev/null
	if [ "$?" = "0" ]
	then
		for _FILE_IN in *.tmpl
		do
			_FILE_OUT="${_FILE_IN/%.tmpl/}"
			> "$_FILE_OUT" || onerror "failed to create configuration file"
			sub_file "$_FILE_IN" "$_FILE_OUT" || onerror "failed to do variables substitution"
		done
	fi
	popd
	
	if [ "$NAMING_MYSQL_USERNAME" = "root" ]
	then
		MYSQL_ROOT_PASSWORD="$NAMING_MYSQL_PASSWORD"
	fi
	get_val "MYSQL_ROOT_PASSWORD" "MySQL root passwd"
	
	mysql -v -uroot -p"$MYSQL_ROOT_PASSWORD" < "naming-conf/naming.sql" || onerror "failed to initalize Molva naming database"
	echo "GRANT ALL PRIVILEGES ON naming.* TO '$NAMING_MYSQL_USERNAME'@'%' IDENTIFIED BY \"$NAMING_MYSQL_PASSWORD\"; FLUSH PRIVILEGES;" | mysql -v -uroot -p"$MYSQL_ROOT_PASSWORD"
fi

# 4. Configure extensions.

do_set "DO_CONFIG_EXTENSIONS" "configure extensions"
if [ "$?" = "0" ]
then

	_EXTENSIONS_DIR="$_LINGCLOUD_HOME/extensions"
	if [ -d "$_EXTENSIONS_DIR" ]
	then
	
		echo "Extensions directory is: $_EXTENSIONS_DIR"
		for _EXTENSION_NAME in `ls "$_EXTENSIONS_DIR"`
		do
			_EXTENSION_DIR="$_EXTENSIONS_DIR/$_EXTENSION_NAME"
			echo "Found an extension: $_EXTENSION_NAME"
			
			echo "Create configuration files for the extension"
			if [ -d "$_EXTENSION_DIR/conf" ]
			then
				pushd "$_EXTENSION_DIR/conf"
				ls *.tmpl &> /dev/null
				if [ "$?" = "0" ]
				then
					for _FILE_IN in *.tmpl
					do
						_FILE_OUT="${_FILE_IN/%.tmpl/}"
						> "$_FILE_OUT" || onerror "failed to create configuration file"
						sub_file "$_FILE_IN" "$_FILE_OUT" || onerror "failed to do variables substitution"
					done
				fi
				popd
			else
				echo "No \"conf\" found in the extension directory, ignore."
			fi
			
			echo "Call the configure script of the extension"
			if [ -e "$_EXTENSION_DIR/configure.sh" ]
			then
				bash "$_EXTENSION_DIR/configure.sh"
			else
				echo "No \"configure.sh\" found in the extension directory, ignore."
			fi
		done
		
	else
		echo "No extensions directory found, ignore."
	fi
fi

# Done

popd

echo "Done."

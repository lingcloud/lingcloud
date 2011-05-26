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
# Script for setting up OpenNebula runtime environment
# Usage: source opennebula-env.sh (It must be sourced in its located directory)

# Environment

LINGCLOUD_HOME="$PWD/../../.."
LINGCLOUD_CONF_DIR="$LINGCLOUD_HOME/conf"
LINGCLOUD_CONF_MOLVA="$LINGCLOUD_CONF_DIR/molva.conf"
NFS_MOUNT_DIR=`grep "nfsMountDir=" "$LINGCLOUD_CONF_MOLVA" | egrep -v "^[[:blank:]]*#" | head -1 | cut -d= -f2-`
export ONE_LOCATION="$NFS_MOUNT_DIR/opennebula"
export ONE_AUTH="$ONE_LOCATION/etc/one_auth"
export ONE_XMLRPC="http://localhost:2633/RPC2"
export PATH="$ONE_LOCATION/bin:$PATH"

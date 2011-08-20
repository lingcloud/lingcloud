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
# Author: Xiaoyi Lu
# Email: cherishlxy@gmail.com
# Description: Adding Monitored Cluster to Nagios Server for XMM.
# Usage : managerMonitoredCluster.sh [ operation : create | destroy ][ masterIp : e.g. 192.168.9.1 ] [ slaveIps : e.g. 192.168.9.2,192.168.9.3 ]
#

NAGIOS_HOME=/usr/local/nagios
SERVER_CONFIG_FILE=$NAGIOS_HOME/etc/nagios.cfg

defineNewHost() {
  mccf=$1
  sip=$2
  echo "define host{" >> $mccf
  echo "use linux-box ; Inherit default values from a template" >> $mccf
  echo "host_name $sip ; The name we're giving to this server" >> $mccf
  echo "alias $sip ; A longer name for the server" >> $mccf
  echo "address $sip  ; IP address of the server" >> $mccf
  echo "active_checks_enabled       0" >> $mccf
  echo "passive_checks_enabled  1" >> $mccf
  echo "}" >> $mccf
}

defineNewService() {
  mccf=$1
  ips=$2
  # CPU Load Service
  echo "define service{" >> $mccf
  echo "use                             generic-service         ; Name of service template to use" >> $mccf
  echo "host_name                       $ips" >> $mccf
  echo "service_description             CPU Load" >> $mccf
  echo "check_command                   service-is-stale" >> $mccf
  echo "notifications_enabled           0" >> $mccf
  echo "active_checks_enabled           0" >> $mccf
  echo "check_freshness  1" >> $mccf
  echo "freshness_threshold 1200" >> $mccf
  echo "}" >> $mccf

  # PING Service
  echo "define service{" >> $mccf
  echo "use                             generic-service         ; Name of service template to use" >> $mccf
  echo "host_name                       $ips" >> $mccf
  echo "service_description             PING" >> $mccf
  echo "check_command                   service-is-stale" >> $mccf
  echo "notifications_enabled           0" >> $mccf
  echo "active_checks_enabled           0" >> $mccf
  echo "check_freshness  1" >> $mccf
  echo "freshness_threshold 1200" >> $mccf
  echo "}" >> $mccf
}

createMonitoredClusterInfo() {
  mccf=$1
  masterip=$2
  slaveips=$3
  # 1st step: Add config file name to server config file.
  echo "cfg_file=$mccf" >> $SERVER_CONFIG_FILE

  # 2nd step: generate config file for master host.
  echo "define host{" > $mccf
  echo "use linux-box ; Inherit default values from a template" >> $mccf
  echo "host_name $masterip ; The name we're giving to this server" >> $mccf
  echo "alias $masterip ; A longer name for the server" >> $mccf
  echo "address $masterip  ; IP address of the server" >> $mccf
  echo "active_checks_enabled       0" >> $mccf
  echo "passive_checks_enabled  1" >> $mccf
  echo "}" >> $mccf

  # 3rd step: generate config info for slave hosts.
  if [ "x$slaveips" != "x" ]; then
     SLAVES_NUM=`echo $slaveips | awk -F',' '{print NF-1}'`
     SLAVES_NUM=`expr $SLAVES_NUM + 1`
     echo "Total $SLAVES_NUM Slave Nodes"
     for ((j=1;j<=$SLAVES_NUM;j++))
     do
      sip=`echo $slaveips | cut -d"," -f$j`
      echo "get a slave ip : $sip"
      defineNewHost $mccf $sip 
     done
  fi

  #4th step: generate service info for all nodes.
  if [ "x$slaveips" != "x" ]; then
     ips=$masterip,$slaveips
     echo "all nodes ips as : $ips"
     defineNewService $mccf $ips
  else
     echo "all nodes ips as : $masterip"
     defineNewService $mccf $masterip
  fi
}

destroyMonitoredClusterInfo() {
  mccf=$1
  \rm $mccf
  mccf=`echo $mccf | sed 's/\//\\\\\\//g'`
  sed -i "/cfg_file=$mccf.*/d" $SERVER_CONFIG_FILE
}

operation=$1
masterIp=$2
slaveIps=$3

if [ "x$operation" == "x" -o "x$masterIp" == "x" ]; then
  echo " Usage : managerMonitoredCluster.sh [ operation : create | destroy ][ masterIp : e.g. 192.168.9.1 ] [ slaveIps : e.g. 192.168.9.2,192.168.9.3 ]"
  exit 1;
fi 

MonitoredClusterConfigFile=$NAGIOS_HOME/etc/objects/$masterIp.cfg

if [ "x$operation" == "xcreate" ]; then
  createMonitoredClusterInfo $MonitoredClusterConfigFile $masterIp $slaveIps
else
  destroyMonitoredClusterInfo $MonitoredClusterConfigFile
fi

#Restart nagios service
/etc/init.d/nagios restart

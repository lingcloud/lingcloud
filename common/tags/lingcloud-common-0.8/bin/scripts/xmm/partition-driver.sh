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

function add_machine_partition
{
	case $2 in
#	xen)            ssh -o  StrictHostKeyChecking=no $1     nohup si_updateclient  --server $3  --image my_imagexen --yes  --reboot >/dev/null & ;;
#	hadoop)         ssh -o  StrictHostKeyChecking=no $1     nohup si_updateclient  --server $3  --image my_imagehadoop --yes  --reboot  >/dev/null & ;;
#	torque)         ssh -o  StrictHostKeyChecking=no $1     nohup si_updateclient  --server $3  --image my_imagehpc --yes  --reboot >/dev/null & ;;
#	ori)            ssh -o  StrictHostKeyChecking=no $1     nohup si_updateclient  --server $3  --image my_imageori --yes  --reboot >/dev/null & ;;
#	bit)            ssh -o  StrictHostKeyChecking=no $1     nohup si_updateclient  --server $3  --image my_imagebit --yes -reboot  >/dev/null & ;;
#	init)           ssh -o  StrictHostKeyChecking=no $1     nohup si_updateclient  --server $3  --image my_imageInit --yes -reboot  >/dev/null & ;;
	esac

}

function do_partition
{
	#echo "" >/root/.ssh/known_hosts
	ssh -o PasswordAuthentication=no $1 "exit"
    if [ $? -ne "0" ]; then
        echo "false"
        exit 2;
    fi
	
	dummyip=`echo "$1" | sed "s/\./\\\\\./g"`
       	sed -i "/$dummyip.*/d" /root/.ssh/known_hosts
	add_machine_partition  $1 $2 $3  2>/tmp/warning$i
	echo true;
}

function getstatus
{
                ping -c 5 $1 >/dev/null  2>/dev/null
                status3=$?
                if [ $status3 != "0" ]
                        then { echo rebooting;  }
                else {
                        ps aux|grep "si_updateclient"|grep "$1" |grep -v "grep"  >/dev/null  2>/dev/null
                        status1=$?
                        grep  "Probing"  /tmp/warning$1  >/dev/null  2>/dev/null
                        status2=$?
                        if [ $status1 == "0"  -a $status2 != "0" ]
                                then echo "cloning";
                        else {
                                if [ $status3 == "0"  -a $status2 == "0" ]
                                        then echo "cloned";
                                      #  else echo " $1  error";
                                fi
                                }
                        fi
                     }
                fi


}
#################################
##   Main Module Begins Here   ##
################################# 


case $4 in
        -p)  for i in $1
        	do
		do_partition  $i $2 $3 ;
		done
  		;;
        *)  
		for i in $1
                do
		getstatus  $i;
		done
		;;
esac


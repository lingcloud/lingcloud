
                             LingCloud Get Started

               Copyright (C) 2008-2011, LingCloud Team, ICT, CAS
                  http://lingcloud.org  support@lingcloud.org

================================================================================
0. System requirements
================================================================================

[REQUIRED] Main server (the server for LingCloud installation):
  * CentOS 5.3-5.6 x86_64
  * Yum software source available
  * Firewall allowing 22, 5901 .. 59XX, and 8080 ports

[REQUIRED] NFS server (the server for virtual machine images storage):
  * CentOS 5.3-5.6 x86_64
  * Yum software source available
  * Firewall allowing 22, 111, and 2049 ports
  * An NFS directory exported and allowing to be mounted from all the servers
  * Regardless of performance, can be the same server as the main server

[REQUIRED] Appliance server (the server for virtual appliances making):
  * CentOS 5.3-5.6 x86_64
  * Yum software source available
  * Firewall allowing 22, 3306, 5901 .. 59XX, and 5981 .. 59XX ports
  * Regardless of performance, can be the same server as the main server

[OPTIONAL] Xen servers (the servers for running virtual machines):
  * CentOS 5.3-5.6 x86_64 with Xen 3.1 kernel enabled
  * Yum software source available
  * Firewall allowing 22, and 5901 .. 59XX ports
  * For testing purpose, can be the same server as the main server

[OPTIONAL] General servers (the physical machines managed by LingCloud):
  * Any Linux distribution
  * Firewall allowing 22, and 5901 .. 59XX ports
  * For testing purpose, can be the same server as the main server

================================================================================
1. Prepare the main server, the NFS server, and the appliance server
================================================================================

This step is necessary only for the first time when LingCloud is deployed. It is
not necessary to repeat preparation after redeploying LingCloud on the same 
servers.
We use $LINGCLOUD_HOME_DIR for the extracted directory of LingCloud, which is 
also the runtime directory of LingCloud.
  * Login the main server as root
  * Change directory to $LINGCLOUD_HOME_DIR/bin/scripts/preparation
  * Copy preparation-main.info.example to preparation-main.info
  * Edit preparation-main.info according to the comments in the file
    - Notice that $INSTALL_DEPENDENCIES_SCRIPT_ARGS_OPENNEBULA_DIR should be in
      an NFS directory
  * Execute ./preparation.sh preparation-main.info

It is not necessary to copy all the $LINGCLOUD_HOME_DIR to the NFS server and 
the appliance server. only the preparation directory is necessary.
For the NFS server and the appliance server, analogously, copy 
preparation-{nfs,app}.info.example, edit preparation-{nfs,app}.info, and execute
./preparation.sh preparation-{nfs,app}.info on the corresponding server. It is 
feasible to use the same server playing two or three roles without regard to 
performance.

================================================================================
2.  Prepare the Xen servers and general servers
================================================================================

This step is necessary only for the first time when the servers are prepared to 
be Xen hypervisors or to be managed by LingCloud. It is not necessary to repeat 
preparation after redeploying LingCloud.
It is not necessary to copy all the $LINGCLOUD_HOME_DIR to the Xen servers and 
the general servers. only the preparation directory is necessary.
  * Login the Xen server as root
  * Change directory to $LINGCLOUD_HOME_DIR/bin/scripts/preparation
  * Copy preparation-xen.info.example to preparation-xen.info
  * Edit preparation-xen.info according to the comments in the file
    - $INSTALL_DEPENDENCIES_SCRIPT_ARGS_OPENNEBULA_DIR should be ignored
  * Execute ./preparation.sh preparation-xen.info

For the general servers (physical machines managed by LingCloud), analogously, 
copy preparation-general.info.example, edit preparation-general.info, and 
execute ./preparation.sh preparation-general.info on each server.

================================================================================
3. Build LingCloud
================================================================================

This step is necessary only for the source code package of LingCloud. If you
have got the binary package (which has a dist directory in the extracted 
directory), you can ignore this step.
If you have modified the source code of LingCloud, you can rebuild it by
this step.
  * Login the main server as root
  * Install Oracle JDK (>= 1.5), Apache Ant (>= 1.8), and prepare their runtime 
    environment
  * Change directory to $LINGCLOUD_HOME_DIR
  * Execute ant clean if you have build before
  * Execute ant

================================================================================
4. Configure LingCloud
================================================================================

This step is necessary only for the first time when LingCloud is build (or a 
binary package is extracted). It is not necessary to repeat configuration after
rebuilding LingCloud on the same server.
  * Login the main server as root
  * Change directory to $LINGCLOUD_HOME_DIR/bin/scripts/configure
  * Copy configure.info.example to configure.info
  * Edit configure.info according to the comments in the file
  * Execute ./configure.sh configure.info

================================================================================
5. Start/stop/restart LingCloud
================================================================================

After building and configuring LingCloud, you can start it.
  * Login the main server as root
  * Change directory to $LINGCLOUD_HOME_DIR/bin
  * Execute ./lingcloud.sh start

To stop LingCloud, you can use the following command.
  * Execute ./lingcloud.sh stop

To restart LingCloud, you can use the following command.
  * Execute ./lingcloud.sh restart

To check the status of LingCloud, you can use the following command.
  * Execute ./lingcloud.sh status

================================================================================
6. Access LingCloud
================================================================================

Now you can access LingCloud via web portal by opening the following URL.
  * http://$LINGCLOUD_SERVER:8080/lingcloud

For more information on how to use LingCloud, please read the documents in the 
following directory.
  * $LINGCLOUD_HOME_DIR/docs

Enjoy it!

================================================================================
Appendix A. Execute the commands from OpenNebula
================================================================================

To execute the commands from OpenNebula, you can use the following commands to 
export its runtime environment.
  * Login the main server as root
  * Change directory to $LINGCLOUD_HOME_DIR/bin/scripts/opennebula-env
  * Execute source opennebula-env.sh or . opennebula-env.sh (it must be sourced 
    in its located directory, not be executed as a new process)

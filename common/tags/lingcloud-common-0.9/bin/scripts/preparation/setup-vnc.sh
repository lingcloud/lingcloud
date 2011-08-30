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
# Setup VNC autostart
# Usage: setup-vnc.sh [VNC_USER]

CLOUD_VNC_USER="$1"
if [ "$CLOUD_VNC_USER" = "" ]
then
	CLOUD_VNC_USER="root"
fi

echo "VNC user is $CLOUD_VNC_USER."

if [ "$CLOUD_VNC_USER" = "root" ]
then
	CLOUD_VNC_HOME="/root"
else
	CLOUD_VNC_HOME="/home/$CLOUD_VNC_USER"
fi

if [ ! -e "$CLOUD_VNC_HOME" ]
then
	mkdir -p "$CLOUD_VNC_HOME"
	chmod 755 "$CLOUD_VNC_HOME"
	chown "$CLOUD_VNC_USER:$CLOUD_VNC_USER" "$CLOUD_VNC_HOME"
fi

if [ ! -e "$CLOUD_VNC_HOME/.vnc" ]
then
	mkdir -p "$CLOUD_VNC_HOME/.vnc"
	chmod 755 "$CLOUD_VNC_HOME/.vnc"
	chown "$CLOUD_VNC_USER:$CLOUD_VNC_USER" "$CLOUD_VNC_HOME/.vnc"
fi

if [ ! -e "$CLOUD_VNC_HOME/.vnc/passwd" ]
then
	# Password: 123456
	echo "Note: VNC password is \"123456\", you can use \"vncpasswd\" to modify it."
	echo -n $'\x49\x40\x15\xf9\xa3\x5e\x8b\x22' > "$CLOUD_VNC_HOME/.vnc/passwd"
	chmod 600 "$CLOUD_VNC_HOME/.vnc/passwd"
	chown "$CLOUD_VNC_USER:$CLOUD_VNC_USER" "$CLOUD_VNC_HOME/.vnc/passwd"
fi

if [ ! -e "$CLOUD_VNC_HOME/.vnc/xstartup" ]
then

	grep -q "Ubuntu" /etc/issue
	if [ "$?" = "0" ];
	then

cat << _EOF_ > "$CLOUD_VNC_HOME/.vnc/xstartup"
#!/bin/sh

unset DBUS_SESSION_BUS_ADDRESS
export XKL_XMODMAP_DISABLE=1

# Uncomment the following two lines for normal desktop:
# unset SESSION_MANAGER
# exec /etc/X11/xinit/xinitrc

[ -x /etc/vnc/xstartup ] && exec /etc/vnc/xstartup
[ -r \$HOME/.Xresources ] && xrdb \$HOME/.Xresources
xsetroot -solid grey
vncconfig -iconic &
xterm -geometry 80x24+10+10 -ls -title "\$VNCDESKTOP Desktop" &
gnome-session &
_EOF_

	else

cat << _EOF_ > "$CLOUD_VNC_HOME/.vnc/xstartup"
#!/bin/sh

# Uncomment the following two lines for normal desktop:
unset SESSION_MANAGER
exec /etc/X11/xinit/xinitrc

[ -x /etc/vnc/xstartup ] && exec /etc/vnc/xstartup
[ -r \$HOME/.Xresources ] && xrdb \$HOME/.Xresources
xsetroot -solid grey
vncconfig -iconic &
xterm -geometry 80x24+10+10 -ls -title "\$VNCDESKTOP Desktop" &
twm &
_EOF_

	fi

        chmod 755 "$CLOUD_VNC_HOME/.vnc/xstartup"
	chown "$CLOUD_VNC_USER:$CLOUD_VNC_USER" "$CLOUD_VNC_HOME/.vnc/xstartup"

fi

echo "(Re)start VNC server."
export PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH"

CLOUD_VNC_PORT=":1"
vncserver -kill $CLOUD_VNC_PORT
ps aux | grep "Xvnc" | grep -q "$CLOUD_VNC_PORT\ "
if [ "$?" != "0" ]
then
	CLOUD_VNC_PORT_NUM="${CLOUD_VNC_PORT:1}"
	[ -e "/tmp/.X${CLOUD_VNC_PORT_NUM}-lock" ] && rm -f "/tmp/.X${CLOUD_VNC_PORT_NUM}-lock"
	[ -e "/tmp/.X11-unix/X${CLOUD_VNC_PORT_NUM}" ] && rm -rf "/tmp/.X11-unix/X${CLOUD_VNC_PORT_NUM}"
fi

sudo -u "$CLOUD_VNC_USER" PATH="$PATH" HOME="$CLOUD_VNC_HOME" vncserver $CLOUD_VNC_PORT > "/tmp/vnc-lingcloud.$CLOUD_VNC_USER.1" 2> "/tmp/vnc-lingcloud.$CLOUD_VNC_USER.2"
if [ "$?" != "0" ]
then
	echo "Failed to start VNC server. Please check \"/tmp/vnc-lingcloud.$CLOUD_VNC_USER.2\" for detailed information."
	exit 1
fi

RC_LINE1="export PATH=\"/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:\$PATH\""
RC_LINE2="rm -rf /tmp/.X*-lock /tmp/.X11-unix/X*"
RC_LINE3="sudo -u \"$CLOUD_VNC_USER\" PATH=\"\$PATH\" HOME=\"$CLOUD_VNC_HOME\" vncserver $CLOUD_VNC_PORT > \"/tmp/vnc-lingcloud.$CLOUD_VNC_USER.1\" 2> \"/tmp/vnc-lingcloud.$CLOUD_VNC_USER.2\""
egrep -v "^[[:blank:]]*#" /etc/rc.local | grep -q "vncserver"
if [ "$?" = "0" ]
then
	echo "A vncserver command is already in /etc/rc.local. If it does not work, please add the following lines in {...} manually."
	echo "{"
	echo "$RC_LINE1"
	echo "$RC_LINE2"
	echo "$RC_LINE3"
	echo "}"
else
	echo "Modify /etc/rc.local."
	echo "$RC_LINE1" >> /etc/rc.local
	echo "$RC_LINE2" >> /etc/rc.local
	echo "$RC_LINE3" >> /etc/rc.local
fi

exit 0


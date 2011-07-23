    <%
    //get the AccessControl object which is saved in session,the object includes user's status
    AccessControl ac = (AccessControl)session.getAttribute("ACobject");
    
    //if the object do not exist,we think that the user comes for first time and 
    //build a new object of the user 
    if (ac == null)
    {
    	ac = new AccessControl();
        //if the AccessControl service if used, the login dialoge is shown
    	if (ac.isAccessControlEnabled() == true)
    	{
    %>
            <b><font size="3px"><bean:message key="org.lingcloud.molva.portal.login" /></font></b>
            <form action="login.do" method="post">
                <bean:message key="org.lingcloud.molva.portal.username" /><br><input type="text" name="username"><br>
				<bean:message key="org.lingcloud.molva.portal.password" /><br><input type="password" name="password"><br>
				<input type="submit" value="<bean:message key="org.lingcloud.molva.portal.login" />">
			</form>
    <%
     	}
     	//if the AccessControl service if closed, the page shows that we welcome anyone
     	//and save AccessControl object into session.
     	else
     	{
     	    ac.setStatus(AccessControl.accessControlStatus.UNUSED);
     		session.setAttribute("ACobject",ac);	
        }
    }
    
    //if the AccessControl object has existed,the operation is decided by the user's status
    else
    {
        //if the user is admin, there shows "welcome admin"
    	if (ac.getStatus() == AccessControl.accessControlStatus.ADMIN)
    	{
    %>
    <bean:message key="org.lingcloud.molva.portal.welcomeadmin" /><%=ac.username%>
    <form action="logout.do" method="post">
		<input type="submit" value="<bean:message key="org.lingcloud.molva.portal.logout" />">
	</form>
    <%
    	}
       //if the AccessControl is not opened, there shows "welcome lingcloud user"
       if (ac.getStatus() == AccessControl.accessControlStatus.UNUSED)
       {
    %>
    <bean:message key="org.lingcloud.molva.portal.welcomeuser" />
    <% 
       }
    }
    %>

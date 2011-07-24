    <%
    //get the AccessControl object which is saved in session,the object includes user's status
    AccessControl ac = (AccessControl)session.getAttribute("ACobject");
    
    //if the object do not exist,we think that the user comes for first time and 
    //build a new object of the user 
    if (ac == null)
    {
    	ac = new AccessControl();
        //if the AccessControl service if used, the login dialog is shown
    	if (ac.isAccessControlEnabled() == true)
    	{
    %>
            <h3><b><bean:message key="org.lingcloud.molva.portal.welcome" /></b></h3>
            <form action="login.do" method="post">
                <bean:message key="org.lingcloud.molva.portal.username" /> <input type="text" name="username" style="width:160px"><br />
				<bean:message key="org.lingcloud.molva.portal.password" /> <input type="password" name="password" style="width:160px"><br />
				<input type="submit" value="<bean:message key="org.lingcloud.molva.portal.login" />">
			</form>
    <%
     	}
     	//if the AccessControl service if closed, the page shows that we welcome anyone
     	//and save AccessControl object into session.
     	else
     	{
     	    ac.setStatus(AccessControl.accessControlStatus.UNUSED);
     		session.setAttribute("ACobject", ac);	
        }
    }
    
    //if the AccessControl object has existed,the operation is decided by the user's status
    else
    {
        //if the user is admin, there shows "welcome admin"
    	if (ac.getStatus() == AccessControl.accessControlStatus.ADMIN)
    	{
    %>
		    <h3><b><bean:message key="org.lingcloud.molva.portal.welcome" /></b></h3>
		    <bean:message key="org.lingcloud.molva.portal.welcomeadmin" /><br />
		    <bean:message key="org.lingcloud.molva.portal.username" /> <%=ac.getUsername()%><br />
		    <form action="logout.do" method="post">
				<input type="submit" value="<bean:message key="org.lingcloud.molva.portal.logout" />">
			</form>
    <%
    	}
       //if the AccessControl is not opened, there shows "welcome lingcloud user"
       if (ac.getStatus() == AccessControl.accessControlStatus.UNUSED)
       {
    %>
		    <h3><b><bean:message key="org.lingcloud.molva.portal.welcome" /></b></h3>
		    <bean:message key="org.lingcloud.molva.portal.welcomeuser" />
    <% 
       }
    }
    %>

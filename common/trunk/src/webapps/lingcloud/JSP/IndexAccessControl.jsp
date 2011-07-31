    <%
    //get the AccessControl object which is saved in session,the object includes user's status
    AccessControl ac = (AccessControl)session.getAttribute("ACobject");
    AccessControl actest = new AccessControl();
    //if the accessControl is open
    if (actest.isAccessControlEnabled() == true)
    {
        //if there is not a ac object
    	if (ac == null)
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
		//if there is a ac object
     	else
     	{
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
        }
    }
    
    //if the AccessControl is closed
    else
    {
    %>
		    <h3><b><bean:message key="org.lingcloud.molva.portal.welcome" /></b></h3>
		    <bean:message key="org.lingcloud.molva.portal.welcomeuser" />
    <% 
    }
    %>

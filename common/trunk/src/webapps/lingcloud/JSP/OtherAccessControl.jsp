    <%
    //get the AccessControl object which is saved in session,the object includes user's status
    AccessControl ac = (AccessControl)session.getAttribute("ACobject");
    if (AccessControl.isAccessControlEnabled() == true) {
    	if (ac == null) {
    		response.sendRedirect(basePath + "JSP/LoginErrorJump.jsp");
            return;
    	}
    }
    %>

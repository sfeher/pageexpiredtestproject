/*
 * Blue System Kft 2017  
 * Minden jog fenntartva!
 *
 */
package com.mycompany.pageexpiredtestproject;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.joda.time.DateTime;

/**
 *
 * @author sfeher
 */
public class TestSession extends AuthenticatedWebSession {

    private DateTime   timeOut;
    public TestSession(Request request) {
        super(request);
    }

    @Override
    protected boolean authenticate(String string, String string1) {
        if (string.equals("test")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Roles getRoles() {
        Roles r=new Roles();
        r.add("ROLE_USER");
        return r;
    }
    public static TestSession getSession() {
        return (TestSession) Session.get();
    }

    public DateTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(DateTime timeOut) {
        this.timeOut = timeOut;
    }
    
}

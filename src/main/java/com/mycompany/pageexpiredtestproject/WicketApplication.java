/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.pageexpiredtestproject;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Component;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.joda.time.DateTime;

public class WicketApplication extends AuthenticatedWebApplication {

    private static final Logger LOG = Logger.getLogger(WicketApplication.class.getName());

//    ApplicationContext appContext;
    

    public static enum eventType {

        INFO("I"), WARN("W"), ERROR("E");
        private final String type;

        public String getType() {
            return type;
        }

        private eventType(String type) {
            this.type = type;
        }

    }
    boolean isInitialized = false;

    public WicketApplication() {

    }

    @Override
    protected void init() {
        if (!isInitialized) {
            super.init();
            if (getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
                LOG.info("deployment config.");
                getMarkupSettings().setStripWicketTags(true);
                getMarkupSettings().setStripComments(true);
                getMarkupSettings().setCompressWhitespace(true);
            }
            getApplicationSettings().setUploadProgressUpdatesEnabled(true);
            setListeners();
            isInitialized = true;
            setCustomErrorPages();            
       }
    }


    private void setListeners() {
        
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {

            @Override
            public void onEndRequest(RequestCycle cycle) {
                HttpServletRequest req = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
                Integer st=req.getSession().getMaxInactiveInterval();
                DateTime now=new DateTime();
                
                StringValue t = cycle.getRequest().getRequestParameters().getParameterValue("extra");
                if (!t.isNull()) {                    
                    if (TestSession.getSession().getTimeOut().isBefore(now)) {
                        TestSession.getSession().invalidateNow();
                    }
                } else {                    
                    TestSession.getSession().setTimeOut(now.plusSeconds(st));
                }

                super.onEndRequest(cycle); 
            }

        });

    }

    private void setCustomErrorPages() {
        getApplicationSettings().setPageExpiredErrorPage(LoginPage.class);
    }

    @Override
    public Class getHomePage() {
        if (TestSession.get().isSignedIn()) {
            return HomePage.class;
        }
        return LoginPage.class;
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return TestSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    
    @Override
    protected void onUnauthorizedPage(Component page) {
        if (!TestSession.get().isSignedIn()) {
            getSignInPageClass();
        } else {
            super.onUnauthorizedPage(page);
        }
    }


}

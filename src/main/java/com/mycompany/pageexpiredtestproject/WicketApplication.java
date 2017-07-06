/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.pageexpiredtestproject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Component;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.core.request.handler.ComponentNotFoundException;
import org.apache.wicket.core.request.mapper.StalePageException;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.PageExpiredException;
import static org.apache.wicket.protocol.http.WebApplication.get;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.protocol.https.Scheme;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.settings.ResourceSettings;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;
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
            //appContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            

            if (getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
                LOG.info("deployment config.");
                getMarkupSettings().setStripWicketTags(true);
                getMarkupSettings().setStripComments(true);
                getMarkupSettings().setCompressWhitespace(true);
            }
            getApplicationSettings().setUploadProgressUpdatesEnabled(true);

            
            //mountPage("/test",TestPage.class);
            

            //getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
            setListeners();
            isInitialized = true;
            ServletContext servletContext = WicketApplication.get().getServletContext();
            String contextPath = servletContext.getContextPath();
            ResourceSettings resourceSettings = getResourceSettings();
            resourceSettings.getResourceFinders().add(new WebApplicationPath(getServletContext(), "src/main/resources")); //this path should be changed                                       
            //mountResource("images/${name}", new ImagesResourceReference());
            String realpath = get().getServletContext().getRealPath(".");
            //mountResource("css/mail.css", new TextTemplateResourceReference(WicketApplication.class, "css/mail.css", "text/css", "utf8", new Model(new HashMap<String, Object>())));
            //LOG.info("loading companyresources: " + customBo.getProperties().get("application.companyresources").toString());
            try {
                String currentworkdir = (new java.io.File(".")).getCanonicalPath();
                LOG.info("current workdir: " + currentworkdir);
            } catch (IOException e) {
                //LOG.info(e);
            }

            //getSharedResources().add("companyresources", new FolderContentResource(new File(customBo.getProperties().get("application.companyresources").toString())));
            mountResource("companyresources", new SharedResourceReference("companyresources"));
            resourceSettings.setResourcePollFrequency(Duration.ONE_SECOND);
            resourceSettings.setThrowExceptionOnMissingResource(false);
            storeappwideparams();

            setCustomErrorPages();            

            // secure mode
            setRootRequestMapper(new HttpsMapper(getRootRequestMapper(), new HttpsConfig()) {

                @Override
                protected Scheme getDesiredSchemeFor(Class<? extends IRequestablePage> pageClass) {
                    if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
                        //                      LOG.info("Dev mode, always use HTTP");
                        return Scheme.HTTP;
                    } else {
                        Scheme sm = super.getDesiredSchemeFor(pageClass);
            /*            if (customBo.getProperties().get("application.scheme.https") != null && customBo.getProperties().get("application.scheme.https").toString().equals("true")) {
                            sm = Scheme.HTTPS;
                        }*/
//                        LOG.info("not in development mode "+sm.name());
                        return sm;
                    }
                }

            });

        }
    }

    static class FolderContentResource implements IResource {

        private final File rootFolder;

        public FolderContentResource(File rootFolder) {
            this.rootFolder = rootFolder;
        }

        @Override
        public void respond(Attributes attributes) {
            PageParameters parameters = attributes.getParameters();
            String fileName = parameters.get(0).toString();
            File file = new File(rootFolder, fileName);
            FileResourceStream fileResourceStream = new FileResourceStream(file);
            ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
            resource.respond(attributes);
        }
    }

    private void setListeners() {
        ///addComponentInstantiationListener(new SpringComponentInjector(this));
        //getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        /*    JQContributionConfig config = new JQContributionConfig().withDefaultJQueryUi();
         getComponentPreOnBeforeRenderListeners().add(new JQComponentOnBeforeRenderListener(config));*/
        SecurePackageResourceGuard guard = (SecurePackageResourceGuard) getResourceSettings().getPackageResourceGuard();
        guard.addPattern("+*.htm");
        guard.addPattern("+*.woff2");
        guard.addPattern("+*.woff");
        //getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
        //getExceptionSettings().setAjaxErrorHandlingStrategy(IExceptionSettings.AjaxErrorStrategy.INVOKE_FAILURE_HANDLER);
        
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
        //getRequestCycleListeners().add(new SessionTimeoutHandlerListener()); 

    }

    private void setCustomErrorPages() {
        //getApplicationSettings().setAccessDeniedPage(Error_AccessDenied.class);
        getApplicationSettings().setPageExpiredErrorPage(LoginPage.class);
        ///getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);

        getRequestCycleListeners().add(new AbstractRequestCycleListener() {

            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                if (getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
                    if (!(ex instanceof PageExpiredException)
                            && !(ex instanceof ComponentNotFoundException)
                            && !(ex instanceof UnauthorizedInstantiationException)
          //                  && !(ex instanceof UsernameNotFoundException)
                            && !(ex instanceof IOException)
                            && !(ex instanceof IllegalStateException)
                            && !(ex instanceof StalePageException)                            
                            && !(ex instanceof SocketTimeoutException)                            
                            && ex != null) {
           //             return new RenderPageRequestHandler(new PageProvider(new ExceptionHandlerPage(ex)));
                        return super.onException(cycle, ex);
                    }
                }
                return super.onException(cycle, ex);
            }

        });
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

    private void storeappwideparams() {

        ServletContext ctx = WicketApplication.get().getServletContext();


    }

}

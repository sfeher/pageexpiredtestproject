/*
 * Blue System Kft 2017  
 * Minden jog fenntartva!
 *
 */
package com.mycompany.pageexpiredtestproject;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

/**
 *
 * @author sfeher
 */
public final class LoginPage extends WebPage {


    public LoginPage() {
        super();

        AuthenticatedWebSession session = TestSession.get();
        Link login = new Link("login") {

            @Override
            public void onClick() {
                if (session.signIn("test", "test")) {
                    setResponsePage(HomePage.class);
                };
            }
        };
        add(login);
        //TODO:  process page parameters
    }
}

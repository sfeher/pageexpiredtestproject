/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pageexpiredtestproject;


import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author sfeher
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation("ROLE_USER")
public class BasePage extends WebPage {
   

   

    public BasePage() {
          Populate();
    }

    private void Populate() {
        if (!TestSession.get().isSignedIn()) {
            setResponsePage(LoginPage.class);
        }
        add(new HeaderPanel("headerpanel"));
        
        //add(new FooterPanel("footerpanel", new StringResourceModel("application.footnote", this).getString()));
    }
}

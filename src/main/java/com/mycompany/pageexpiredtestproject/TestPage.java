/*
 * Blue System Kft 2017  
 * Minden jog fenntartva!
 *
 */
package com.mycompany.pageexpiredtestproject;


import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

/**
 *
 * @author sfeher
 */

public final class TestPage extends BasePage {


    
    
    public TestPage() {
        super();
        add(new Link("inner") {

            @Override
            public void onClick() {
                  setResponsePage(new InnerPage());
            }
        });
    }      
}

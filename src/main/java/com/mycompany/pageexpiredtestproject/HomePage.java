package com.mycompany.pageexpiredtestproject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;


public class HomePage extends BasePage {
	private static final long serialVersionUID = 1L;

	public HomePage() {
		super();

		add(new AjaxFallbackLink("test") {


                    @Override
                    public void onClick(AjaxRequestTarget art) {
                         setResponsePage(new TestPage());
                    }
                });

                // TODO Add your page's components here

    }
}

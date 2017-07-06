/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pageexpiredtestproject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author sfeher
 */
public final class FooterPanel extends Panel {
    
   public FooterPanel(String id, String text) {
        super(id);
        add(new Label("footerpanel_text", text));
    }
}

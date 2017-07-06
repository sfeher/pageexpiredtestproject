/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pageexpiredtestproject;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTime;
import org.joda.time.Period;




/**
 *
 * @author sfeher
 */
public class HeaderPanel extends Panel {


    List<Integer> jvList;
    LoadableDetachableModel jvModel;
    Integer ctsz;

    public HeaderPanel(String id) {
        super(id);
        Populate();
    }

    private void Populate() {




        Label timeout = new Label("timeout", new Model() {

            @Override
            public Serializable getObject() {
                //HttpServletRequest req = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
                //Long lastAccessTime = req.getSession().getLastAccessedTime();
                //req.getSession().getMaxInactiveInterval();
                //Date d = new Date(lastAccessTime);
                DateTime d1 = new DateTime();
                DateTime d = TestSession.getSession().getTimeOut();
                Period p = new Period(d1, d);
                int m = p.getMinutes();
                int s = p.getSeconds();
                //SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm.ss");
                return String.format("%d:%d", m, s); //fm.format(d); //To change body of generated methods, choose Tools | Templates.
            }

        });
        timeout.add(new SessionExpirationAwareAjaxSelfUpdatingBehaviour(Duration.ONE_SECOND)); 
        Label timeoutlabel = new Label("timeoutlabel", "timeout");
        add(timeoutlabel);
        add(timeout);
        //add(companylogo);

    }

}

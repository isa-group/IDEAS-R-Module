package es.us.isa.ideas.controller.R.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import es.us.isa.ideas.controller.R.RDelegate;



public class RSessionListener implements HttpSessionListener{

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
         HttpSession session=se.getSession();
	RDelegate rdelegate= (RDelegate) session.getAttribute("RDelegate");
	rdelegate.deleteTemp();
    }


    /*@Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof HttpSessionDestroyedEvent){
            HttpSession session=((HttpSessionDestroyedEvent)event).getSession();
	RDelegate rdelegate= (RDelegate) session.getAttribute("RDelegate");
	rdelegate.deleteTemp();
        }*/
   

}

/*
 * 
 * Each line should be prefixed with  * 
 */
package uk.ac.surrey.ee.iot.s2w.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author te0003
 */
@WebListener()
public class GeneralConfig implements ServletContextListener {
    
    public static ServletContext servletContext;    
    public static String iotLiteOntUri = "http://iot.ee.surrey.ac.uk/fiware/ontologies/iot-lite";
//    public static String sparqlDescribeRes = "C:/Users/te0003/Documents/NetBeansProjects/OntologyEvaluator/src/main/webapp/query/describe_sensors.rq";
    public static String sparqlDescribeRes = "/query/describe_sensors.rq";    
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        sparqlDescribeRes = servletContext.getRealPath(sparqlDescribeRes);
        
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

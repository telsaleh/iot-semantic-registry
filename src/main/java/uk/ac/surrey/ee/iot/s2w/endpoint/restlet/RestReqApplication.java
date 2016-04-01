/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.iot.s2w.endpoint.restlet;


import uk.ac.surrey.ee.iot.s2w.endpoint.restlet.resource.SparqlHandler;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import uk.ac.surrey.ee.iot.s2w.endpoint.restlet.resource.RegistryHandler;
import uk.ac.surrey.ee.iot.s2w.endpoint.restlet.resource.SanityCheck;

public class RestReqApplication extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    
//    public static final String restletPath = "/repo";
//    public static final String restletPath = "/v2";
    
//    public static final String registerPrefix = "/register";//POST
//    public static final String lookupPrefix = "/lookup";    //GET
//    public static final String updatePrefix = "/update";    //UPDATE
//    public static final String deletePrefix = "/remove";    //DELETE
//    public static final String sparqlPrefix = "/sparql";     //GET, POST    discover via SPARQL
//    public static final String searchPrefix = "/search";    //GET, POST     discover via Prob Engine
    
    public static final String registryPrefix = "/registry"; //POST
    public static final String sparqlPrefix = "/sparql"; //POST
    public static final String sanityCheckPrefix = "/version"; //POST
    
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
        Router router = new Router(getContext());
        
        router.attach(sanityCheckPrefix, SanityCheck.class);
        
        router.attach(registryPrefix+"/{repository_id}", RegistryHandler.class); //POST
        router.attach(registryPrefix+"/{repository_id}/{resource_id}", RegistryHandler.class);  //GET, UPDATE, DELETE
        router.attach(sparqlPrefix+"/{repository_id}", SparqlHandler.class);    //GET, POST
        
        return router;
    }

}

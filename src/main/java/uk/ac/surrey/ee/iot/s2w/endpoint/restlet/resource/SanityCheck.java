/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.iot.s2w.endpoint.restlet.resource;

import java.io.IOException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Resource which has only one representation.
 */
public class SanityCheck extends ServerResource {

    

    @Get
    public Representation getSanityChkStatus() {

        //get servlet context for getting files
        String result = "<sanityCheck>\n"
                + "  <name>IoT Discovery</name>\n"
                + "  <type>Sanity Check</type>\n"
                + "  <version>Version: 3.2.3.SNAPSHOT</version>\n"
                + "  </sanityCheck>";      
        
        return new StringRepresentation(result, MediaType.TEXT_XML);
    }
    
    @Post
    public Representation postTest(Representation entity) throws ResourceException, IOException {
        
        String message = entity.getText();
        
        return new StringRepresentation(message);
    }
}
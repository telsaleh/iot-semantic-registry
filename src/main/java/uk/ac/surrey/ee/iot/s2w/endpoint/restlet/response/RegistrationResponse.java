/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.iot.s2w.endpoint.restlet.response;

/**
 *
 * @author te0003
 */
public class RegistrationResponse {
    
//    protected String resId ="";
//    String type ="";
    protected String repoId ="";
    protected boolean stored=false;
//    boolean indexed=false;
//    String association ="";
    protected String responseStatus="";
    
    public RegistrationResponse(){}
    
    public RegistrationResponse(String resId, String repoId, boolean stored, String errorMessage){
    
//        this.resId=id;
//        this.type=type;
        this.repoId= repoId;
        this.stored=stored;
//        this.indexed=indexed;
//        this.association=association;
        this.responseStatus=errorMessage;
    
    }
    
    
}

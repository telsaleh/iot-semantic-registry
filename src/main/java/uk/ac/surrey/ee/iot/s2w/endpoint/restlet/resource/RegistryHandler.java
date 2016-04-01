/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.iot.s2w.endpoint.restlet.resource;

import com.google.gson.Gson;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.iot.s2w.common.GeneralConfig;
import uk.ac.surrey.ee.iot.s2w.store.TdbAccessHandler;
import uk.ac.surrey.ee.iot.s2w.endpoint.restlet.response.RegistrationResponse;
import uk.ac.surrey.ee.iot.s2w.store.TripleStoreStartup;

/**
 * Resource which has only one representation.
 */
public class RegistryHandler extends ServerResource {

    @Post
    public Representation handleRegister(Representation entity) throws ResourceException, IOException {

        String repoId = (String) getRequest().getAttributes().get("repository_id");
        String reqBody = entity.getText();
//        InputStream reqBody = entity.getStream();

        String errorMessage = "";
        boolean parseError = false;
        RegistrationResponse rr = new RegistrationResponse();
        StringReader srToRead = new StringReader("");
        Gson gson = new Gson();
        StringRepresentation result = new StringRepresentation("");

        try {
            srToRead = new StringReader(reqBody);
        } catch (NullPointerException npe) {
            errorMessage = npe.getLocalizedMessage();
            rr = new RegistrationResponse("", repoId, parseError, errorMessage);
            result = new StringRepresentation(gson.toJson(rr));
            result.setMediaType(MediaType.APPLICATION_JSON);
            return result;
        }

        OntModel modelToRegister = ModelFactory.createOntologyModel();
        try {
            modelToRegister.read(srToRead, null, RDFLanguages.strLangTurtle);
            parseError = false;
        } catch (RiotException re) {
            System.out.println("Error caught: Turtle");
            System.out.println(re.getMessage());
            parseError = true;
        }
        if (parseError) {
            srToRead = new StringReader(reqBody);
            try {
                modelToRegister.read(srToRead, null, RDFLanguages.strLangJSONLD);
                parseError = false;
            } catch (RiotException re) {
                System.out.println("Error caught: JSON-LD");
                System.out.println(re.getMessage());
                parseError = true;
            }
        }
        if (parseError) {
            srToRead = new StringReader(reqBody);
            try {
                modelToRegister.read(srToRead, null, RDFLanguages.strLangRDFXML);
                parseError = false;
            } catch (RiotException re) {
                System.out.println("Error caught: RDF/XML");
                System.out.println(re.getMessage());
                parseError = true;
            }
        }
        if (parseError) {
            srToRead = new StringReader(reqBody);
            try {
                modelToRegister.read(srToRead, null, RDFLanguages.strLangN3);
                parseError = false;
            } catch (RiotException re) {
                System.out.println("Error caught: N3");
                System.out.println(re.getMessage());
                parseError = true;
            }
        } 

        boolean stored = false;
        if (!parseError) {
            String storePath = TripleStoreStartup.db_connection; //+"/"+ repoId;
            TdbAccessHandler tah = new TdbAccessHandler(storePath);

            modelToRegister.write(System.out, "TURTLE");

            try {
                tah.storeModel(modelToRegister);
                stored = true;
                rr = new RegistrationResponse("", repoId, stored, errorMessage);
            } catch (Exception e) {
                e.printStackTrace();
                stored = false;
            }
        }else {
            errorMessage = "";
            rr = new RegistrationResponse("", repoId, parseError, errorMessage);
        }
        
        result = new StringRepresentation(gson.toJson(rr));
        result.setMediaType(MediaType.APPLICATION_JSON);

        return result;
    }

    @Get
    public Representation handleLookup() {

        String repoId = (String) getRequest().getAttributes().get("repository_id");
//        String resourceId = (String) getRequest().getAttributes().get("resource_id");

        String resId = "";
        String resURI = getRequest().getResourceRef().toUri().toString();

//        int ind = resURI.lastIndexOf("/");
//        resURI = new StringBuilder(resURI).replace(ind, ind + 1, "#").toString();
//        System.out.println(resURI);
        System.out.println("HELLO: " + resURI);

        //repoId = "sdr";
        StringRepresentation resultInFormat = new StringRepresentation("");
        String queryString = "";

        Query query;
        try {
            queryString = new String(Files.readAllBytes(Paths.get(GeneralConfig.sparqlDescribeRes)));
            queryString = queryString + resURI + ">";
            query = QueryFactory.create(queryString);
        } catch (QueryParseException | IOException ex) {
            return new StringRepresentation(ex.getMessage());
        }

        String format = "XML";

        int acceptTypes = getClientInfo().getAcceptedMediaTypes().size();
        if (acceptTypes > 0) {

            boolean atMatch = false;
            for (int i = 0; i < acceptTypes; i++) {

                String acceptType = getClientInfo().getAcceptedMediaTypes().get(i).getMetadata().getName();
                System.out.println("Request Accepts is: " + acceptType);

                if (query.isDescribeType() || query.isConstructType()) {
                    switch (acceptType) {
                        case "application/rdf+xml":
                        case "application/owl+xml":
                            format = "RDF/XML-ABBREV";
                            atMatch = true;
                            break;
                        case "text/turtle":
                        case "application/x-turtle":
                        case "application/rdf+turtle":
                        case "application/turtle":
                            format = "TURTLE";
                            atMatch = true;
                            break;
                        case "application/ld+json":
                            format = "JSON-LD";
                            atMatch = true;
                            break;
                        case "text/n3":
                            format = "N3";
                            atMatch = true;
                            break;
                        case "application/n-triples":
                            format = "N-TRIPLE";
                            atMatch = true;
                            break;
                        default:
                            format = "RDF/XML-ABBREV";
                            resultInFormat.setMediaType(MediaType.APPLICATION_RDF_XML);
                    }
                } else {
                    switch (acceptType) {
                        case "text/plain":
                            format = "TXT";
                            atMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
                            break;
                        case "application/xml":
                            format = "XML";
                            atMatch = true;
                            resultInFormat.setMediaType(MediaType.APPLICATION_XML);
                            break;
                        case "text/xml":
                            format = "XML";
                            atMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_XML);
                            break;
                        case "application/json":
                            format = "JSON";
                            atMatch = true;
                            resultInFormat.setMediaType(MediaType.APPLICATION_JSON);
                            break;
                        case "text/csv":
                            format = "CSV";
                            atMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_CSV);
                            break;
                        case "text/tab-separated-values":
                            format = "TSV";
                            atMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_TSV);
                            break;
                        default:
                            format = "TXT";
                            resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
                    }
                }
                if (atMatch) {
                    break;
                }
            }

            String queryResult = "";

            String storePath = TripleStoreStartup.db_connection; //+"/"+ repoId;
            TdbAccessHandler tah = new TdbAccessHandler(storePath);
            queryResult = tah.queryModel(query, format);

            resultInFormat = new StringRepresentation(queryResult);
            return resultInFormat;
        }

        return null;
    }

    @Put
    public Representation handleUpdate(Representation entity) throws ResourceException, IOException {

        Representation result;
        String reqBody = entity.getText();
        String repoId = (String) getRequest().getAttributes().get("repository_id");
        String resourceId = (String) getRequest().getAttributes().get("resource_id");

        String storePath = TripleStoreStartup.db_connection;// +"/" + repoId;
        TdbAccessHandler tah = new TdbAccessHandler(storePath);

        StringReader srToStore = new StringReader(reqBody);

        OntModel modelToRegister = ModelFactory.createOntologyModel();
        Model ontologyModel = FileManager.get().loadModel(GeneralConfig.iotLiteOntUri);
        modelToRegister.add(ontologyModel);
        modelToRegister.read(srToStore, null);

        boolean updSuccess = tah.updateModel(resourceId, modelToRegister);

        Gson gson = new Gson();
        result = new StringRepresentation(gson.toJson(updSuccess));
        result.setMediaType(MediaType.APPLICATION_JSON);

        return result;

    }

    @Delete
    public Representation handleRemove() throws ResourceException, IOException {

        Representation result;
        String repoId = (String) getRequest().getAttributes().get("repository_id");
        String resourceId = (String) getRequest().getAttributes().get("resource_id");

        String storePath = TripleStoreStartup.db_connection; //+ "/" + repoId;
        TdbAccessHandler tah = new TdbAccessHandler(storePath);

        boolean delResult = tah.deleteModel(resourceId);
        if (!delResult) {
            result = new StringRepresentation("deleted");
        } else {
            result = new StringRepresentation("could not find resource");
        }
        return result;

    }

}

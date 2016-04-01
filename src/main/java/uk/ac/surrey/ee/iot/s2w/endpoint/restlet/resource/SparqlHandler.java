/*
 * 
 * Each line should be prefixed with  * 
 */
package uk.ac.surrey.ee.iot.s2w.endpoint.restlet.resource;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import java.io.File;
import java.io.IOException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.iot.s2w.store.TdbAccessHandler;
import uk.ac.surrey.ee.iot.s2w.store.TripleStoreStartup;

/**
 *
 * @author te0003
 */
public class SparqlHandler extends ServerResource {

    protected String WEB_DATASET_PATH = "/WEB-INF/dataset/ds";
//    protected String ABSOLUTE_DATASET_PATH = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/dataset/ds";
    protected String ABSOLUTE_DATASET_PATH = System.getProperty("user.dir") + "/target/OntologyEvaluator-1.0-SNAPSHOT/WEB-INF/dataset/ds";

    @Get
    public Representation sparqlGet() {

        String queryString = (String) getQuery().getFirstValue("query");
        Representation response = handleRequest(queryString);
        return response;
    }

    @Post
    public Representation sparqlPost(Representation entity) throws ResourceException, IOException {

        String queryString = entity.getText();
        Representation response = handleRequest(queryString);
        
        System.out.println("final reponse content type is: "+ response.getMediaType());
        return response;
    }

    public Representation handleRequest(String queryString) {

        String repoId = (String) getRequest().getAttributes().get("repository_id");

        String format = "XML";
        StringRepresentation resultInFormat = new StringRepresentation("");
//        System.out.println(queryString);

        Query query;
        try {
            query = QueryFactory.create(queryString);
        } catch (QueryParseException qpe) {
            return new StringRepresentation(qpe.getMessage());
        }

        int acceptTypes = getClientInfo().getAcceptedMediaTypes().size();
        if (acceptTypes > 0) {

            boolean acceptTypeMatch = false;
            for (int i = 0; i < acceptTypes; i++) {

                String acceptType = getClientInfo().getAcceptedMediaTypes().get(i).getMetadata().getName();
                System.out.println("Request Accepts is: " + acceptType);

                if (query.isDescribeType() || query.isConstructType()) {
                    switch (acceptType) {
                        case "application/rdf+xml":
                        case "application/owl+xml":
                            format = "RDF/XML-ABBREV";
                            acceptTypeMatch = true;
                            break;
                        case "text/turtle":
                        case "application/x-turtle":
                        case "application/rdf+turtle":
                        case "application/turtle":
                            format = "TURTLE";
                            acceptTypeMatch = true;
                            break;
                        default:
                            format = "RDF/XML-ABBREV";
                            resultInFormat.setMediaType(MediaType.APPLICATION_RDF_XML);
                            break;
                    }
                } else {
                    switch (acceptType) {
                        case "text/plain":
                            format = "TXT";
                            acceptTypeMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
                            break;
                        case "application/xml":
                            format = "XML";
                            acceptTypeMatch = true;
                            resultInFormat.setMediaType(MediaType.APPLICATION_XML);
                            break;
                        case "text/xml":
                            format = "XML";
                            acceptTypeMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_XML);
                            break;
                        case "application/json":
                            format = "JSON";
                            acceptTypeMatch = true;
                            resultInFormat.setMediaType(MediaType.APPLICATION_JSON);
//                            System.out.println("reponse content type is: "+ resultInFormat.getMediaType());
                            break;
                        case "text/csv":
                            format = "CSV";
                            acceptTypeMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_CSV);
                            break;
                        case "text/tab-separated-values":
                            format = "TSV";
                            acceptTypeMatch = true;
                            resultInFormat.setMediaType(MediaType.TEXT_TSV);
                            break;
                        default:
                            format = "TXT";
                            resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
//                            System.out.println("reponse content type is: "+ resultInFormat.getMediaType());
                            break;
                    }
                }
                if (acceptTypeMatch) {
                    break;
                }
            }
        }
        String queryResult = "";
        queryResult = executeQuery(query, format, repoId);

        resultInFormat.setText(queryResult);        
        return resultInFormat;
    }

    public String executeQuery(Query query, String format, String repoId) {

        String queryResult = "";
        String storePath = TripleStoreStartup.db_connection; //+"/"+ repoId;
        TdbAccessHandler tah = new TdbAccessHandler(storePath);
        queryResult = tah.queryModel(query, format);

        return queryResult;
    }

    public static void main(String[] args) {

        String SPARQL_QUERY1 = "C:/Users/te0003/Documents/NetBeansProjects/OntologyEvaluator/src/main/webapp/query/sensors_declared.rq";
        String queryString = "";
        SparqlHandler qd = new SparqlHandler();

        try {
            queryString = org.apache.commons.io.FileUtils.readFileToString(new File(SPARQL_QUERY1));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Query query = QueryFactory.create(queryString);

        String queryResult = "";
        queryResult = qd.executeQuery(query, "XML", "200");
        System.out.println("Result is: \n" + queryResult);
    }
}

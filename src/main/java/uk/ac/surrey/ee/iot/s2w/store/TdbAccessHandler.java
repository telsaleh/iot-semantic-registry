/*
 * 
 * Each line should be prefixed with  * 
 */
package uk.ac.surrey.ee.iot.s2w.store;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.jena.tdb.TDBFactory;

/**
 *
 * @author te0003
 */
public class TdbAccessHandler {
    
    public Dataset dataset;
    public Model model;

    public TdbAccessHandler(String storePath) {
        
//        String storePath = TripleStoreStartup.db_connection + "/" + repoId;
        
        this.dataset= TDBFactory.createDataset(storePath);
        dataset.begin(ReadWrite.READ);
        
    
    }
    
    public void storeModel(OntModel ontModel){//, String datasetPath) {
        
//        System.out.println("\n" + dataset.getContext().toString());
        dataset.end();
        dataset.begin(ReadWrite.WRITE);
//        Model model = dataset.getDefaultModel();

        try {
            this.model= this.dataset.getDefaultModel();
            // API calls to a model in the dataset
            model.add(ontModel);
            // Finally, commit the transaction. 
            dataset.commit();
            // Or call .abort()

        } finally {
            dataset.end();
            model.close();
            dataset.close();
            System.out.println("\nmodel added to store at: "+TripleStoreStartup.db_connection);
        }
        
    }
    
    public boolean deleteModel(String resId) {
         
//        Dataset dataset = TDBFactory.createDataset(TripleStoreStartup.db_connection);
        dataset.begin(ReadWrite.WRITE);
        
//         Model model = dataset.getDefaultModel();
//         boolean success = false;
//
//        try { 
//            // API calls to a model in the dataset
//            success = deleteResource(model, resId);            
//            // Finally, commit the transaction. 
//            dataset.commit();
//            // Or call .abort()
//        } finally {
//            dataset.end();
//            model.close();
//            dataset.close();
//        }
//         
//         return success;
        this.model= this.dataset.getDefaultModel();
        System.out.println("check if available");
        Resource res = model.getResource(resId);
        if (model.contains(res, null) == true) {
            res.removeProperties();
            System.out.println("Properties deleted for instance: " + resId);
            return true;
        } else {
            System.out.println("Properties not found for instance: " + resId);
        }
        return false;
         
    }
     
//     public boolean deleteResource(Model m, String resId) {
//        // TODO Auto-generated method stub
//        System.out.println("check if available");
//        Resource res = m.getResource(resId);
//        if (m.contains(res, null) == true) {
//            res.removeProperties();
//            System.out.println("Properties deleted for instance: " + resId);
//            return true;
//        } else {
//            System.out.println("Properties not found for instance: " + resId);
//        }
//        return false;
//    }
     
     public boolean updateModel(String resId, Model modelUpdate) {
         
//        Dataset dataset = TDBFactory.createDataset(TripleStoreStartup.db_connection);
        dataset.begin(ReadWrite.WRITE);
        this.model= this.dataset.getDefaultModel();
        boolean delete = false;
//        Model model = dataset.getDefaultModel();

        try {
            // API calls to a model in the dataset
            delete = deleteModel(resId);
            if (delete){
            model.add(modelUpdate);
            }
            // Finally, commit the transaction. 
            dataset.commit();
            // Or call .abort()
        } finally {
            dataset.end();
        }         
         return delete;         
    }
     
    public String queryModel(Query query, String format) {

//        Dataset dataset = TDBFactory.createDataset(TripleStoreStartup.db_connection);
//        dataset.begin(ReadWrite.READ);
        QueryExecution qExec = QueryExecutionFactory.create(query, dataset);
        
//        String directory = "";//ABSOLUTE_DATASET_PATH+dsSize;
//        try {
//            directory = context.getRealPath(WEB_DATASET_PATH + "/ds" + dsSize);
//            System.out.println("dataset directory: "+directory);//      "/WEB-INF/dataset/Dataset1"
//        } catch (NullPointerException e) {
//            System.out.println("no context...set to absolute path");
//            directory = ABSOLUTE_DATASET_PATH + dsSize;
//        }
//        Dataset dataset = TDBFactory.createDataset(directory);
//        dataset.begin(ReadWrite.READ);
//        QueryExecution qExec = QueryExecutionFactory.create(query, dataset);
        
        String queryResult = "";

        if (qExec.getQuery().isSelectType()) {
            try {
                ResultSet rs = qExec.execSelect();
                //ResultSetFormatter.out(rs);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                switch (format) {
                    case "TXT":
                        queryResult = ResultSetFormatter.asText(rs);
                        break;
                    case "JSON":
                        ResultSetFormatter.outputAsJSON(bos, rs);
                        queryResult = bos.toString();
                        break;
                    case "CSV":
                        ResultSetFormatter.outputAsCSV(bos, rs);
                        queryResult = bos.toString();
                        break;
                    case "TSV":
                        ResultSetFormatter.outputAsTSV(bos, rs);
                        queryResult = bos.toString();
                        break;
                    default:
                        queryResult = ResultSetFormatter.asXMLString(rs);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println(e.getMessage());
                return e.getMessage();                
            } finally {
                qExec.close();
                dataset.close();
            }
        } else if (qExec.getQuery().isConstructType()) {
            try {
                Model results = qExec.execConstruct();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                results.write(bos, format);
                queryResult = bos.toString();
            } catch (Exception e) {
                return e.getMessage();
            } finally {
                qExec.close();
                dataset.close();
            }
        } else if (qExec.getQuery().isDescribeType()) {
            try {
                Model resultModel = qExec.execDescribe();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                resultModel.write(bos, format);
                queryResult = bos.toString();
            } catch (Exception e) {
                return e.getMessage();
            } finally {
                qExec.close();
                dataset.close();
            }
        } else if (qExec.getQuery().isAskType()) {
            try {
                boolean result = qExec.execAsk();
                queryResult = String.valueOf(result);
            } catch (Exception e) {
                return e.getMessage();
            } finally {
                qExec.close();
                dataset.close();
            }
        }
        return queryResult;
    }
    
    
    public static void main(String[] args) {

        String queryString = "";
        TdbAccessHandler tah = new TdbAccessHandler("");

        String SPARQL_QUERY1 = "C:/Users/te0003/Documents/NetBeansProjects/OntologyEvaluator/src/main/webapp/query/sensors_declared.rq";

        try {
            queryString = org.apache.commons.io.FileUtils.readFileToString(new File(SPARQL_QUERY1));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Query query = QueryFactory.create(queryString);

        String queryResult = "";
        queryResult = tah.queryModel(query, "XML");
        System.out.println("Result is: \n" + queryResult);
    }
    
}

package uk.ac.surrey.ee.iot.s2w.store;

import org.apache.jena.graph.GraphEvents;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sdb.SDBFactory;
import org.apache.jena.sdb.Store;
import org.apache.jena.sdb.StoreDesc;
import org.apache.jena.sdb.sql.SDBConnectionDesc;
import org.apache.jena.sdb.store.DatabaseType;
import org.apache.jena.sdb.store.LayoutType;
import java.io.InputStream;
import java.io.StringReader;

public class SdbAccessHandler {

    /**
     * @param args
     */
    public Store store;

    public Store getStore() {
        return store;
    }

    public SdbAccessHandler() {

        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL);
        if (TripleStoreStartup.storageMode==TripleStoreStartup.select_SDB_H2) {
            storeDesc.setDbType(DatabaseType.H2);
        }
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        //storeDesc.setLayout(LayoutType.LayoutTripleNodesHash);
        String jdbcURL = TripleStoreStartup.db_connection;
        String dbUsername = TripleStoreStartup.db_username;
        String dbpassword = TripleStoreStartup.db_password;

        //CONNECT THIS WAY
        SDBConnectionDesc sdbConnDesc = SDBConnectionDesc.blank();
        sdbConnDesc.setJdbcURL(jdbcURL);
        sdbConnDesc.setUser(dbUsername);
        sdbConnDesc.setPassword(dbpassword);
        storeDesc.connDesc = sdbConnDesc;
        this.store = SDBFactory.connectStore(TripleStoreStartup.conn, storeDesc);//connectStore(storeDesc);
    }

    public synchronized Model loadAllModelInstancesFromSdb() {
        // TODO Auto-generated method stub
        System.out.println("load from SDB using: " + TripleStoreStartup.db_connection);        //Store store = getSdbStore();
        Model storeModel = SDBFactory.connectDefaultModel(store);
        store.close();

        return storeModel;
    }

    public synchronized void storeModel(Object model) {
        // TODO Auto-generated method stub

        store.getLoader().setChunkSize(5000); //
        store.getLoader().setUseThreading(false); // Don't thread
        store.getLoader().startBulkUpdate();

        Model storeModel = SDBFactory.connectDefaultModel(store);
        if (model instanceof InputStream){// || model instanceof StringReader){
        storeModel.read( (InputStream) model, null);//cp.getOntoUri());
        }else if (model instanceof StringReader){// || model instanceof StringReader){
        storeModel.read( (StringReader) model, null);//cp.getOntoUri());
        }else if (model instanceof Model){// || model instanceof StringReader){
        storeModel.add((Model) model);//cp.getOntoUri());
        }
        
        storeModel.notifyEvent(GraphEvents.startRead);
        try {
            store.getLoader().finishBulkUpdate();
            store.getLoader().close();
            store.close();
        } catch (Exception e) {
            System.err.print(e.toString());
        } finally {
            storeModel.notifyEvent(GraphEvents.finishRead);
        }

    }
    
     public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

//    public synchronized boolean storeModel(StringReader model) {
//        // TODO Auto-generated method stub
//
//        store.getLoader().setChunkSize(5000); //
//        store.getLoader().setUseThreading(false); // Don't thread
//        store.getLoader().startBulkUpdate();
//
//        Model storeModel = SDBFactory.connectDefaultModel(store);
//        storeModel.read(model, null);//cp.getOntoUri());
//        storeModel.notifyEvent(GraphEvents.startRead);
//        try {
//            store.getLoader().finishBulkUpdate();
//            store.getLoader().close();
//            store.close();
//            return true;
//        } catch (Exception e) {
//            System.err.print(e.toString());
//            return false;
//        } finally {
//            storeModel.notifyEvent(GraphEvents.finishRead);
//            storeModel.close();
//            store.close(); //check!
//        }
//
//    }
//
//    public synchronized boolean storeModel(Model model) {
//        // TODO Auto-generated method stub
//
//        store.getLoader().setChunkSize(5000); //
//        store.getLoader().setUseThreading(false); // Don't thread
//        store.getLoader().startBulkUpdate();
//
//        Model storeModel = SDBFactory.connectDefaultModel(store);
//        storeModel.add(model);
//        storeModel.notifyEvent(GraphEvents.startRead);
//        try {
//            store.getLoader().finishBulkUpdate();
//            store.getLoader().close();
//            store.close();
//            return true;
//        } catch (Exception e) {
//            System.err.print(e.toString());
//            return false;
//        } finally {
//            storeModel.notifyEvent(GraphEvents.finishRead);
//
//        }
//
//    }
    
}

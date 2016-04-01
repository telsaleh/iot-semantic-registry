/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.iot.s2w.store;

import org.apache.jena.query.Dataset;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import uk.ac.surrey.ee.iot.s2w.common.GeneralConfig;

/**
 * Web application lifecycle listener.
 *
 * @author te0003
 */
public class TripleStoreStartup implements ServletContextListener {

    protected static String db_hostname = "";
    protected static String db_port = "";
    protected static String db_path = "";

    protected String TDB_DEFAULT_PATH = "/WEB-INF/triple-store";

    public static final int select_TDB = 1;
    public static final int select_SDB_H2 = 2;
    public static final int select_SDB_MYSQL = 3;

    protected static int storageMode = 1;

    protected static String db_username = "";
    protected static String db_password = "";
    public static String db_connection = "";
    protected final String trplStrListName = "db_list";
    protected static String tripleStoreScript = "/WEB-INF/sql_scripts/create-triple-store.sql";
//    protected static String trplStrListScript = "";

    protected static Connection conn = null;
    private Server server;
    private final String  IOT_LITE_ONT_NS = "http://purl.oclc.org/NET/UNIS/fiware/iot-lite#";
    private final String SSN_ONT_NS = "http://purl.oclc.org/NET/ssnx/ssn#";
    
    private Dataset dataset;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext context = GeneralConfig.servletContext;
        tripleStoreScript = context.getRealPath(tripleStoreScript);

        Properties dbProp = new Properties();
        try {
            String path = context.getInitParameter("db_access_properties");
            final InputStream is = context.getResourceAsStream(path);
            dbProp.load(is);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        db_hostname = dbProp.getProperty("hostname");
        db_port = dbProp.getProperty("port");
        db_path = dbProp.getProperty("path");

        db_username = dbProp.getProperty("username");
        db_password = dbProp.getProperty("password");

        storageMode = Integer.parseInt(dbProp.getProperty("storageMode"));

        if (storageMode == 2) {
            try {
                System.out.println("SQL Database selected: H2 Embedded");
                db_connection = "jdbc:h2:tcp://" + db_hostname + ":" + db_port + db_path;
                server = Server.createTcpServer("-tcpPort", db_port, "-tcpAllowOthers").start();
                System.out.println("SQL Database: H2 Embedded");
                // Create Sql Connection
                Class.forName("org.h2.Driver");
                System.out.println("h2 db_url is: " + db_connection);//.split("INIT")[0]);
                conn = DriverManager.getConnection(db_connection, db_username, db_password);
                System.out.println("store location: " + db_connection);
                runSqlScript();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(TripleStoreStartup.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (storageMode == 3) {
            try {
                System.out.println("SQL Database selected: MySQL server");
                // Create Sql Connection
                Class.forName("com.mysql.jdbc.Driver");
                db_connection = "jdbc:mysql://" + db_hostname + ":" + db_port + db_path;
                conn = DriverManager.getConnection(db_connection, db_username, db_password);
                conn.setClientInfo("sql_mode", "ANSI");
                System.out.println("store location: " + db_connection);
                runSqlScript();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(TripleStoreStartup.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Triplestore selected: Jena TDB");
//            Dataset dataset;
            if (!db_path.isEmpty()) {
                db_connection = db_path;
                dataset = TDBFactory.createDataset(db_connection);
            } else {
                db_connection = GeneralConfig.servletContext.getRealPath(TDB_DEFAULT_PATH);
                dataset = TDBFactory.createDataset(db_connection);
            }

            TdbAccessHandler tah = new TdbAccessHandler(db_connection);
            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
//            ontModel.setStrictMode(true);
//            ontModel.add(FileManager.get().loadModel(IOT_LITE_ONT_NS));
//            ontModel.add(FileManager.get().loadModel(SSN_ONT_NS));
            tah.storeModel(ontModel);

            System.out.println("store location: " + TDB_DEFAULT_PATH);

        }

    }

    public void runSqlScript() {

        try {
            // Initialize object for ScripRunner
            ScriptRunner sr = new ScriptRunner(conn);
            // Give the input file to Reader
            Reader reader = new BufferedReader(new FileReader(tripleStoreScript));
            // Execute script
            //sr.runScript(reader);
            RunScript.execute(conn, reader);

        } catch (FileNotFoundException e) {
            System.err.println("Failed to Execute" + tripleStoreScript
                    + " The error is " + e.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(TripleStoreStartup.class.getName()).log(Level.SEVERE, null, ex);
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM employees");
        } catch (SQLException ex) {
            Logger.getLogger(TripleStoreStartup.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

//        try {
//            Statement stat = TripleStoreSetup.conn.createStatement();
//            stat.execute("SHUTDOWN");
//            stat.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(TripleStoreSetup.class.getName()).log(Level.SEVERE, null, ex);
//        }        
        if (storageMode == 2) {
            try {
                server.stop();
                if (!conn.isClosed()) {
                    conn.close();
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            } catch (SQLException | NullPointerException ex) {
                Logger.getLogger(TripleStoreStartup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else {
        dataset.end(); 
        
        }

        System.out.println("Context Listener for Triple Store Setup Destroyed");
    }
}

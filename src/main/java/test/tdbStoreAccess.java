/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;

/**
 *
 * @author te0003
 */
public class tdbStoreAccess {

    public static void main(String[] args) {

        String directory = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/dataset/Dataset2";
//        Dataset dataset = TDBFactory.createDataset(directory);
        Dataset dataset = DatasetFactory.createMem();
        System.out.println("\n"+dataset.getContext().toString());
        
        

    }

}

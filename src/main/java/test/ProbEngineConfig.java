/*
 * 
 * Each line should be prefixed with  * 
 */
package test;

/**
 *
 * @author te0003
 */
public class ProbEngineConfig {
    
    public int training_set_min = 1;
    public String STM_1 = "";
    public String STM_2 = "";
    public String LDA_model = "";
    public String unseen_concepts = "";
    public String index = "";        
    public Thread retrainEngineThread;	// = new Thread(this);
    public Thread retrainAfterTrain;	// = new Thread(this); used for dataset registration.
    
}

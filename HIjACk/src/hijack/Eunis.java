package hijack;

/**
 *
 * @author diegues
 */
public class Eunis {
    String classification;
    String level1;
    String level2;
    String level3;
    String level4;
    String level5;
    String level6;
    
    public Eunis(String c){
        this.level1 = "";
        this.level2 = "";
        this.level3 = "";
        this.level4 = "";
        this.level5 = "";
        this.level6 = "";   
        this.classification = c;
        this.getClassByLevels(c);
    }   

    private void getClassByLevels(String c) {
        if(c.equals("-1") || c.isEmpty()){
            return;
        }
        int len = c.length();
        if(len > 0){
            this.level1 = c.substring(0, 1);
        }
        if(len > 1){
            this.level2 = c.substring(0,2);
        }
        if(len > 3){
            this.level3 = c.substring(0, 4);
        }
        if(len > 4){
            this.level4 = c.substring(0, 5);
        }
        if(len > 5){
            this.level5 = c.substring(0, 6);
        }
        if(len > 6)
            this.level6 = c;
    }    

    @Override
    public String toString() {
        return this.classification + "," + this.level1 + "," + this.level2 + "," + this.level3 + "," + this.level4 + "," + this.level5 + "," + this.level6;
    }

    public String getClassification() {
        return classification;
    }
    
    public boolean equals(Eunis obj) {
        return this.classification.equals(obj.classification); 
    }
    
}

package hijack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author diegues
 */
public class DataRecord {
    String filename;
    String date;
    String longitude;
    String latitude;
    String depth;
    Eunis eunis;
    List<String> species;

    public DataRecord(String filename) {
        this.filename = filename;
        this.date = "";
        this.longitude = "";
        this.latitude = "";
        this.depth = "";
        this.eunis = new Eunis("", "");
        this.species = new ArrayList<>();
    }
    
    public DataRecord(String filename, String date, String longitude, String latitude, String depth, Eunis eunis, List<String> species) {
        this.filename = filename;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
        this.eunis = eunis;
        this.species = species;
    }
    
    @Override
    public String toString() {
        String row = "";
        if(species.isEmpty()){
            return filename + "," + date + "," + longitude + "," + latitude + "," + depth + "," + eunis.toString() + ",,\n";
        }
        for (int i = 0; i < species.size(); i++) {
            String get = species.get(i);
            String[] split = get.split(" - ");
            if(split.length > 1){
                row += filename + "," + date + "," + longitude + "," + latitude + "," + depth + "," + eunis.toString() + "," + split[1] + "," + split[0] + '\n';
            }
            else{
                row += filename + "," + date + "," + longitude + "," + latitude + "," + depth + "," + eunis.toString() + "," + get + ",\n";
            }
        }
        return row;
    }

}

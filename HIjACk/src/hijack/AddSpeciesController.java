package hijack;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author diegues
 */
public class AddSpeciesController implements Initializable {
    String newSpeciesName;
    
    @FXML
    private TextField speciesName;

    @FXML
    void handleAddNewSpecies(ActionEvent event) {
        newSpeciesName = speciesName.getText();
        HIjACk.getCurrentStage().close();
    }
    @FXML
    void handleCancelAction(ActionEvent event){
        HIjACk.getCurrentStage().close();
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        newSpeciesName = new String();
        assert speciesName != null : "fx:id=\"speciesName\" was not injected: check your FXML file 'addSpecies.fxml'.";
    }    

    public String getSpeciesName() {
        return newSpeciesName;
    }
    
}

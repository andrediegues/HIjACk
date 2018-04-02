/*
 * Copyright (C) 2018 diegues
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hijack;

/**
 *
 * @author diegues
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditController {
    
    Stage stage;
    EunisClassification classification;
    String filename;

    @FXML
    private TextField eunisTextField;

    @FXML
    void handleCancelButtonAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void handleOkButtonAction(ActionEvent event) {
        classification = new EunisClassification(filename, eunisTextField.getText());
        if(!classification.isValid()){
            classification = null;
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please insert a valid classification.", ButtonType.OK);
            alert.setTitle("Not a valid classification!");
            alert.showAndWait();
        }
        else{
            stage.close();
        }
    }

    @FXML
    void initialize() {
        assert eunisTextField != null : "fx:id=\"eunisTextField\" was not injected: check your FXML file 'editEUNIS.fxml'.";
    }
    
    /**
     *
     * @param editStage
     * @param filename
     * @return
     */
    public EunisClassification handleEdition(Stage editStage, String filename) {
        stage = editStage;
        classification = null;
        this.filename = filename;
        stage.showAndWait();
        return classification;
    }
}

package hijack;

/**
 *
 * @author diegues
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class ShortcutController {
    
    @FXML
    private VBox root;
    
    @FXML
    private TableView<Shortcut> table;
    
    @FXML
    private TableColumn shortcut;

    @FXML
    private TableColumn description;
    
    @FXML
    void handleCloseAction(ActionEvent event) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
    }
    
    private ObservableList<Shortcut> data;

    @FXML
    void initialize() {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'shortcut.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'shortcut.fxml'.";
        assert shortcut != null : "fx:id=\"shortcut\" was not injected: check your FXML file 'shortcut.fxml'.";
        assert description != null : "fx:id=\"description\" was not injected: check your FXML file 'shortcut.fxml'.";
        
        final Shortcut save = new Shortcut("Ctrl + s", "Save the file.");
        final Shortcut nextPhoto = new Shortcut("\u2193", "Display the next photo.");
        final Shortcut previousPhoto = new Shortcut("\u2191", "Display the previous photo.");
        final Shortcut tag = new Shortcut("Enter", "Tag the current photo with the annotation written.");
        final Shortcut next10Photos = new Shortcut("Ctrl + \u2193", "Display the photo 10 positions ahead.");
        final Shortcut previous10Photos = new Shortcut("Ctrl + \u2191", "Display the photo 10 positions behind.");
        
        shortcut.setCellValueFactory(new PropertyValueFactory("shortCut"));        
        description.setCellValueFactory(new PropertyValueFactory("descr"));
        
        data = FXCollections.observableArrayList(save, next10Photos, nextPhoto, previous10Photos, previousPhoto, tag);
        table.setItems(data);
        
    }
    public class Shortcut{
        private final SimpleStringProperty shortCut;
        private final SimpleStringProperty descr;
        
        public Shortcut(String sc, String des) {
            this.shortCut = new SimpleStringProperty(sc);
            this.descr = new SimpleStringProperty(des);
        }

        public String getShortCut() {
            return shortCut.get();
        }

        public String getDescr() {
            return descr.get();
        }
    }
}

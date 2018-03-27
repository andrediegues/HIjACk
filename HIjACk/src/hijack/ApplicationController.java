package hijack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ApplicationController {

    @FXML // fx:id="listView"
    private ListView<?> listView; // Value injected by FXMLLoader

    @FXML // fx:id="previousButton"
    private Button previousButton; // Value injected by FXMLLoader

    @FXML // fx:id="imageView"
    private ImageView imageView; // Value injected by FXMLLoader

    @FXML // fx:id="nextButton"
    private Button nextButton; // Value injected by FXMLLoader

    @FXML // fx:id="EUNISClass"
    private TextField EUNISClass; // Value injected by FXMLLoader

    @FXML // fx:id="editButton"
    private Button editButton; // Value injected by FXMLLoader

    @FXML // fx:id="saveButton"
    private Button saveButton; // Value injected by FXMLLoader

    @FXML // fx:id="zoomInButton"
    private Button zoomInButton; // Value injected by FXMLLoader

    @FXML // fx:id="zoomOutButton"
    private Button zoomOutButton; // Value injected by FXMLLoader

    @FXML // fx:id="fullScreenButton"
    private Button fullScreenButton; // Value injected by FXMLLoader

    @FXML // fx:id="fileName"
    private Label fileName; // Value injected by FXMLLoader

    @FXML // fx:id="observationsTextArea"
    private TextArea observationsTextArea; // Value injected by FXMLLoader

    @FXML
    void handleAboutAction(ActionEvent event) {

    }

    @FXML
    void handleCloseAction(ActionEvent event) {

    }

    @FXML
    void handleCopyAction(ActionEvent event) {

    }

    @FXML
    void handleCutAction(ActionEvent event) {

    }

    @FXML
    void handleDeleteAction(ActionEvent event) {

    }

    @FXML
    void handleEditAction(ActionEvent event) {

    }

    @FXML
    void handleExitAction(ActionEvent event) {

    }

    @FXML
    void handleFilesClick(MouseEvent event) {

    }

    @FXML
    void handleFilesKeyPress(KeyEvent event) {

    }

    @FXML
    void handleFullScreenAction(ActionEvent event) {

    }

    @FXML
    void handleImageDoubleClickAction(MouseEvent event) {

    }

    @FXML
    void handleImageScrollAction(ScrollEvent event) {

    }

    @FXML
    void handleNextAction(ActionEvent event) {

    }

    @FXML
    void handleNextKeyPress(KeyEvent event) {

    }

    @FXML
    void handleOpenAction(ActionEvent event) {

    }

    @FXML
    void handlePasteAction(ActionEvent event) {

    }

    @FXML
    void handlePreviousAction(ActionEvent event) {

    }

    @FXML
    void handlePreviousKeyPress(KeyEvent event) {

    }

    @FXML
    void handleSaveAction(ActionEvent event) {

    }

    @FXML
    void handleTextAreaKeyPress(KeyEvent event) {

    }

    @FXML
    void handleZoomInAction(ActionEvent event) {

    }

    @FXML
    void handleZoomOutAction(ActionEvent event) {

    }
   
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        System.out.println("Application initialized");
        assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'application.fxml'.";
        assert previousButton != null : "fx:id=\"previousButton\" was not injected: check your FXML file 'application.fxml'.";
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'application.fxml'.";
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'application.fxml'.";
        assert EUNISClass != null : "fx:id=\"EUNISClass\" was not injected: check your FXML file 'application.fxml'.";
        assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'application.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'application.fxml'.";
        assert zoomInButton != null : "fx:id=\"zoomInButton\" was not injected: check your FXML file 'application.fxml'.";
        assert zoomOutButton != null : "fx:id=\"zoomOutButton\" was not injected: check your FXML file 'application.fxml'.";
        assert fullScreenButton != null : "fx:id=\"fullScreenButton\" was not injected: check your FXML file 'application.fxml'.";
        assert fileName != null : "fx:id=\"fileName\" was not injected: check your FXML file 'application.fxml'.";
        assert observationsTextArea != null : "fx:id=\"observationsTextArea\" was not injected: check your FXML file 'application.fxml'.";

    }

    void initData(File choice) throws FileNotFoundException {
        if(!checkExistingLog(choice)){
            File out = new File(choice.getAbsolutePath() + choice.getName() + ".csv");
        }
        else{
            BufferedReader br = new BufferedReader(new FileReader(choice.getAbsolutePath() + choice.getName() + ".csv"));
        }
        /*ObservableList<String> items = new ObservableList<String>
        for(int i = 0; i < choice.listFiles().length; i++){
            items.add(i, choice.listFiles()[i].getName());
        }
        listView.setItems(items);*/
    }
     
    boolean checkExistingLog(File file){
        File[] files = file.listFiles();
        for(File f: files){
            if(f.getName().equals(file.getName() + ".csv")){
                return true;
            }
        }
        return false;
    }
}

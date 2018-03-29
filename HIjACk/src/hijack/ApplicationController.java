package hijack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationController {
    
    private File logFile;
    private File currentDir;
    private List<String> imageList;

    @FXML
    private VBox root;
    
    @FXML // fx:id="listView"
    private ListView<String> listView; // Value injected by FXMLLoader

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
        try {
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
            Parent appLauncher = FXMLLoader.load(getClass().getResource("appLauncher.fxml"));
            Stage newStage = new Stage();
            Scene launcher = new Scene(appLauncher);
            newStage.setScene(launcher);
            newStage.setTitle("HIjACk");
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void handleFilesClick(MouseEvent event) {
        System.out.println(listView.getSelectionModel().getSelectedItem());
        if(listView.getSelectionModel().getSelectedItem() == null){
            return;
        }
        loadImage(currentDir + "/" + listView.getSelectionModel().getSelectedItem());
        fileName.setText(listView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void handleFilesKeyPress(KeyEvent event) {
        String name = listView.getSelectionModel().getSelectedItem();
        KeyCode character = event.getCode();
        int index = imageList.indexOf(name);
        if(character.equals(KeyCode.RIGHT) && index < imageList.size() - 1){
            listView.getSelectionModel().select(index + 1);
            name = listView.getSelectionModel().getSelectedItem();
        }
        else if(character.equals(KeyCode.DOWN) && index < imageList.size() - 1){
            name = imageList.get(index + 1);
        }
        else if(character.equals(KeyCode.LEFT) && index > 0){
            listView.getSelectionModel().select(index - 1);
            name = listView.getSelectionModel().getSelectedItem();
        }
        else if(character.equals(KeyCode.UP) && index > 0){
            name = imageList.get(index - 1);
        }
        else{// falta caso para o enter
            return;
        }
        fileName.setText(name);
        String pathOfImage = currentDir.getAbsolutePath() + "/" + name;
        loadImage(pathOfImage);

    }

    @FXML
    void handleFullScreenAction(ActionEvent event) {

    }

    @FXML
    void handleImageScrollAction(ScrollEvent event) {

    }

    @FXML
    void handleNextAction(ActionEvent event) {
        handleFilesKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, true, true, true, true));
    }

    @FXML
    void handleNextKeyPress(KeyEvent event) {

    }

    @FXML
    void handleOpenAction(ActionEvent event) { // not working need to send stage to work
        try {
            Stage currentStage = (Stage) root.getScene().getWindow();
            FXMLLoader appLauncher = new FXMLLoader(getClass().getResource("appLauncher.fxml"));
            appLauncher.load();
            AppLauncherController appLauncherController = appLauncher.getController();
            appLauncherController.getFolderFromStage(currentStage);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    void handlePasteAction(ActionEvent event) {

    }

    @FXML
    void handlePreviousAction(ActionEvent event) {
        handleFilesKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, true, true, true, true));
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

    void initData(File choice, List<String> listOfNames) throws FileNotFoundException {
        currentDir = choice;
        logFile = checkExistingLog(choice);
        imageList = listOfNames;
        if(logFile == null){
            logFile = new File(choice.getAbsolutePath() + choice.getName() + ".csv");
        }        
        //System.out.println(logFile.length()); podemos ver se o ficheiro ja existia pelo tamanho do ficheiro, ie, se > 0 ja existia
        listView.getItems().addAll(listOfNames);
    }
     
    private File checkExistingLog(File file){
        File[] files = file.listFiles();
        for(File f: files){
            if(f.getName().equals(file.getName() + ".csv")){
                return f;
            }
        }
        return null;
    }

    private void loadImage(String pathOfImage) {
        try {
            File imagefile = new File(pathOfImage);
            Image image = new Image(imagefile.toURI().toURL().toString());
            imageView.setImage(image);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

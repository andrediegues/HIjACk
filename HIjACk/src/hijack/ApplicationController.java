package hijack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class ApplicationController implements Initializable{
    
    private File logFile;
    private File currentDir;
    private List<String> imageList;
    private boolean isModified;
    private HashMap<String, String> data; 

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

    @FXML // fx:id="fullScreenButton"
    private Button fullScreenButton; // Value injected by FXMLLoader

    @FXML // fx:id="fileName"
    private Label fileName; // Value injected by FXMLLoader

    @FXML
    void handleAboutAction(ActionEvent event) throws MalformedURLException {
        Alert about = new Alert(Alert.AlertType.NONE);
        about.setTitle("About HIjACK");
        about.getButtonTypes().add(ButtonType.OK);
        about.setGraphic(new Hyperlink("https://github.com/andrediegues/HIjACk"));        
        about.showAndWait();
    }

    @FXML
    void handleOpenAction(ActionEvent event) { 
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
    void handleCloseAction(ActionEvent event) {
        try {            
            if(!exitWithUnsavedModifications()){
                return;
            }
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
    void handleExitAction(ActionEvent event) {
        if(!exitWithUnsavedModifications()){
            return;
        }        
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void handleFilesClick(MouseEvent event) {
        editButton.setDisable(false);
        if(listView.getSelectionModel().getSelectedItem() == null){
            editButton.setDisable(true);
            return;
        }
        loadImage(currentDir + "/" + listView.getSelectionModel().getSelectedItem());
        fileName.setText(listView.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    void handleKeyPress(KeyEvent event) {
        editButton.setDisable(false);
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
        else if(character.equals(KeyCode.ENTER)){
            handleEditAction(new ActionEvent());
        }
        else{
            return;
        }
        if(!listView.isVisible()){
            listView.scrollTo(index);
        }
        fileName.setText(name);
        String pathOfImage = currentDir.getAbsolutePath() + "/" + name;
        loadImage(pathOfImage);
    }

    @FXML
    void handleNextAction(ActionEvent event) {
        handleKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, true, true, true, true));
    }
    
    @FXML
    void handlePreviousAction(ActionEvent event) {
        handleKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, true, true, true, true));
    }   
    
    @FXML
    void handleEditAction(ActionEvent event) {
        String filename = listView.getSelectionModel().getSelectedItem();
        if(filename == null){
            return;
        }
        String classification = EUNISClass.getText();
        if(!isValid(classification)){
            classification = null;
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please insert a valid classification.", ButtonType.OK);
            alert.setTitle("Not a valid classification!");
            alert.showAndWait();
        }
        else if(classification != null){
            if(!classification.equals(data.get(filename))){
                isModified = true;
                data.put(filename, classification);
                Notifications editNotification = Notifications.create()
                                        .text("Edited " + filename + " successfully")
                                        .hideAfter(Duration.seconds(3))
                                        .title("Edit")
                                        .position(Pos.TOP_RIGHT);
                editNotification.showInformation();
            }
        }
    }

    @FXML
    void handleSaveAction(ActionEvent event) {
        try {
            if(isModified){
                isModified = false;
                BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
                bw.write("filename, classificationValue\n");
                data.forEach((String key, String value) -> {
                    try {
                        bw.write(key + "," + value + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                bw.close();
                Notifications saveNotification = Notifications.create()
                                .text("File was saved in " + logFile.getAbsolutePath())
                                .hideAfter(Duration.seconds(3))
                                .title("Save")
                                .position(Pos.TOP_RIGHT);
                saveNotification.showInformation();
            }
            else{
                Notifications saveNotification = Notifications.create()
                                .text("Nothing to save")
                                .hideAfter(Duration.seconds(3))
                                .title("Save")
                                .position(Pos.TOP_RIGHT);
                saveNotification.showInformation();
            }
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @FXML
    void handleFullScreenAction(ActionEvent event) {
        try {
            FXMLLoader fsImage = new FXMLLoader(getClass().getResource("fullscreen.fxml"));
            Parent fsRoot = fsImage.load();
            FullscreenController fsController = fsImage.getController();
            fsController.addImageToImageView(currentDir + "/" + listView.getSelectionModel().getSelectedItem());
            Stage fsStage = new Stage();
            fsStage.setScene(new Scene(fsRoot));
            fsStage.initModality(Modality.APPLICATION_MODAL);
            fsStage.setTitle(listView.getSelectionModel().getSelectedItem());
            fsStage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void initData(Stage appStage, File choice, List<String> listOfNames) throws FileNotFoundException {
        currentDir = choice;
        logFile = checkExistingLog(choice);
        imageList = listOfNames;
        isModified = false;
        editButton.setDisable(true);
        data = new HashMap<>();
        if(logFile == null){
            logFile = new File(choice.getAbsolutePath() + "/" + choice.getName() + ".csv");
            listOfNames.forEach((name) -> data.put(name, ""));
        }        
        else{
            try {
                BufferedReader br = new BufferedReader(new FileReader(logFile));
                String line = br.readLine();
                while((line = br.readLine()) != null){
                    if(line.split(",").length > 1){
                        data.put(line.split(",")[0], line.split(",")[1]);
                    }
                    else{
                        data.put(line.split(",")[0], "");
                    }
                }   
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listView.getItems().addAll(listOfNames);
        appStage.setOnCloseRequest((WindowEvent event) -> {
            exitWithUnsavedModifications();
        });
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
            String[] names = pathOfImage.split("/");
            if(data.containsKey(names[names.length-1])){
                EUNISClass.setText(data.get(names[names.length-1]));
            } else {
                EUNISClass.setText("");
            }
            File imagefile = new File(pathOfImage);
            Image image = new Image(imagefile.toURI().toURL().toString());
            imageView.setImage(image);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isValid(String classification){
        if(classification.isEmpty()){
            return true;
        }
        char level0 = classification.charAt(0);
        if(!Character.isAlphabetic(level0)){
            return false;
        }
        int length = classification.length();
        for(int i = 1; i < length; i++){
            if(!Character.isDigit(classification.charAt(i)) && classification.charAt(i) != '.'){
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Application initialized");
        assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'application.fxml'.";
        assert previousButton != null : "fx:id=\"previousButton\" was not injected: check your FXML file 'application.fxml'.";
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'application.fxml'.";
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'application.fxml'.";
        assert EUNISClass != null : "fx:id=\"EUNISClass\" was not injected: check your FXML file 'application.fxml'.";
        assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'application.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'application.fxml'.";
        assert fullScreenButton != null : "fx:id=\"fullScreenButton\" was not injected: check your FXML file 'application.fxml'.";
        assert fileName != null : "fx:id=\"fileName\" was not injected: check your FXML file 'application.fxml'.";    
    }

    private boolean exitWithUnsavedModifications() {
        if(!isModified){
            return true;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save before leaving?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("You have unsaved changes");
        alert.setTitle("Unsaved Changes");
        alert.showAndWait();
        ButtonType result = alert.getResult();
        
        if(result.equals(ButtonType.YES)){
            handleSaveAction(new ActionEvent());
            return true;
        }
        else{
            return false;
        }
    }
    
    
}

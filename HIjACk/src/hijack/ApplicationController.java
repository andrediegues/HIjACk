package hijack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ApplicationController implements Initializable{
    
    private File logFile;
    private File currentDir;
    private List<String> imageList;
    private boolean isModified;
    EunisClassification currentImageClassification;
    private ArrayList<EunisClassification> data; 

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
    void handleImageScrollAction(ScrollEvent event) {

    }    
    
    @FXML
    void handleEditAction(ActionEvent event) {
        if(listView.getSelectionModel().getSelectedItem() == null){
            return;
        }
        try {
            FXMLLoader edit                      = new FXMLLoader(getClass().getResource("editEUNIS.fxml"));
            Parent editRoot                      = edit.load();
            EditController editController = edit.getController();
            
            Stage editStage = new Stage();
            editStage.initModality(Modality.APPLICATION_MODAL);
            Scene editScene = new Scene(editRoot);
            editStage.setScene(editScene);
            currentImageClassification = editController.handleEdition(editStage, listView.getSelectionModel().getSelectedItem());
            if(currentImageClassification != null){
                EUNISClass.setText(currentImageClassification.getClassValue());
                data.add(currentImageClassification);
            }
            editStage.close();
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleSaveAction(ActionEvent event) {
        
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

    void initData(File choice, List<String> listOfNames) throws FileNotFoundException {
        currentDir = choice;
        logFile = checkExistingLog(choice);
        imageList = listOfNames;
        isModified = false;
        data = new ArrayList<>();
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
            EUNISClass.setText("");
            // falta ir buscar de novo a classificaçao quando se volta a mesma imagem
            File imagefile = new File(pathOfImage);
            Image image = new Image(imagefile.toURI().toURL().toString());
            imageView.setImage(image);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        assert fileName != null : "fx:id=\"fileName\" was not injected: check your FXML file 'application.fxml'.";    }
}

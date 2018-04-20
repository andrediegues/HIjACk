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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TreeMap;
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
import javafx.scene.control.ButtonBar;
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
import javafx.util.Pair;
import org.controlsfx.control.Notifications;

public class ApplicationController implements Initializable{
    
    private File logFile;
    private File currentDir;
    private List<String> imageList;
    private boolean isModified;
    private Map<String, Pair<String, String>> data; 
    private String lastEdition;
    private String firstUnlabeledImage;

    @FXML // fx:id="root"
    private VBox root; // Value injected by FXMLLoader

    @FXML // fx:id="listView"
    private ListView<String> listView; // Value injected by FXMLLoader

    @FXML // fx:id="labelsButtonBar"
    private ButtonBar labelsButtonBar; // Value injected by FXMLLoader

    @FXML // fx:id="previousButton"
    private Button previousButton; // Value injected by FXMLLoader

    @FXML // fx:id="imageView"
    private ImageView imageView; // Value injected by FXMLLoader

    @FXML // fx:id="nextButton"
    private Button nextButton; // Value injected by FXMLLoader

    @FXML // fx:id="EUNISClass"
    private TextField EUNISClass; // Value injected by FXMLLoader

    @FXML
    private ImageView statusIcon;

    @FXML // fx:id="obsTextField"
    private TextField obsTextField; // Value injected by FXMLLoader

    @FXML // fx:id="editButton"
    private Button editButton; // Value injected by FXMLLoader

    @FXML // fx:id="saveButton"
    private Button saveButton; // Value injected by FXMLLoader

    @FXML // fx:id="markerButton"
    private Button markerButton; // Value injected by FXMLLoader

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
            listView.scrollTo(index + 1);
            name = listView.getSelectionModel().getSelectedItem();
        }
        else if(character.equals(KeyCode.DOWN)){
            event.consume();
        }
        else if(character.equals(KeyCode.LEFT) && index > 0){
            listView.getSelectionModel().select(index - 1);
            listView.scrollTo(index - 1);            
            name = listView.getSelectionModel().getSelectedItem();
        }
        else if(character.equals(KeyCode.UP)){
            event.consume();
        }
        else if(character.equals(KeyCode.ENTER)){
            handleEditAction(new ActionEvent());
            listView.requestFocus();
        }
        else if(event.isControlDown() && character.equals(KeyCode.S)){
            handleSaveAction(new ActionEvent());
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
        String classification = EUNISClass.getText().toUpperCase();
        if(classification.contains(" ")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please remove all blank spaces.", ButtonType.OK);
            alert.setTitle(classification + " contains blank spaces!");
            alert.setGraphic(new ImageView("images/ic_error_black_48dp.png"));
            classification = null;
            alert.showAndWait();
        }
        else if(!isValid(classification)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please insert a valid classification.", ButtonType.OK);
            alert.setTitle(classification + " is not a valid classification!");
            alert.setGraphic(new ImageView("images/ic_error_black_48dp.png"));
            classification = null;
            alert.showAndWait();
        }
        else{
            if(!classification.equals("")){
                lastEdition = classification;
            }
            String obs = obsTextField.getText();
            EUNISClass.setText(classification);
            obsTextField.setText(obs);            
            if(data.get(filename).getKey().equals(classification) && data.get(filename).getValue().equals(obs)){
            } 
            else {
                isModified = true;
                obsTextField.setText(obs);
                data.put(filename, new Pair(classification, obs));
                Image img = new Image("images/ic_done_black_48dp.png", true);
                Notifications editNotification = Notifications.create()
                        .text("Edited " + filename + " successfully")
                        .hideAfter(Duration.seconds(3))
                        .title("Edit")
                        .graphic(new ImageView(img))
                        .position(Pos.TOP_RIGHT);
                editNotification.show();
            }
        }
    }

    @FXML
    void handleSaveAction(ActionEvent event) {
        try {
            if(isModified){
                isModified = false;
                BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
                bw.write("filename, classification, observations\n");
                data.forEach((String key, Pair value) -> {
                    try {
                        System.out.println(key + " " + value.toString());
                        bw.write(key + "," + value.getKey() + "," + value.getValue() + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                bw.close();
                Image img = new Image("images/ic_done_black_48dp.png", true);
                Notifications saveNotification = Notifications.create()
                                .text("File was saved in " + logFile.getAbsolutePath())
                                .hideAfter(Duration.seconds(3))
                                .title("Save")
                                .graphic(new ImageView(img))
                                .position(Pos.TOP_RIGHT);
                saveNotification.show();
            }
            else{
                Image img = new Image("images/ic_info_black_48dp.png", true);
                Notifications saveNotification = Notifications.create()
                                .text("Nothing to save")
                                .hideAfter(Duration.seconds(3))
                                .title("Save")
                                .graphic(new ImageView(img))
                                .position(Pos.TOP_RIGHT);
                saveNotification.show();
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
        data = new TreeMap<>();
        if(logFile == null){
            logFile = new File(choice.getAbsolutePath() + "/" + choice.getName() + ".csv");
            listOfNames.forEach((name) -> {
                data.put(name, new Pair("", ""));
                if(firstUnlabeledImage == null){
                    firstUnlabeledImage = name;
                }
            });
        }        
        else{
            try {
                BufferedReader br = new BufferedReader(new FileReader(logFile));
                String line = br.readLine();
                while((line = br.readLine()) != null){
                    if(line.split(",").length <= 1){
                        data.put(line.split(",")[0], new Pair("", ""));
                        if(firstUnlabeledImage == null){
                            firstUnlabeledImage = line.split(",")[0];
                        }
                    }
                    else if(line.split(",").length <= 2){
                        data.put(line.split(",")[0], new Pair(line.split(",")[1], ""));
                    }
                    else{
                        data.put(line.split(",")[0], new Pair(line.split(",")[1], line.split(",")[2]));
                    }
                }   
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listView.getItems().addAll(listOfNames);
        listView.getSelectionModel().select(firstUnlabeledImage);
        listView.scrollTo(firstUnlabeledImage);
        loadImage(currentDir.getAbsolutePath() + "/" + firstUnlabeledImage);
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
                EUNISClass.setText(data.get(names[names.length-1]).getKey());
                obsTextField.setText(data.get(names[names.length-1]).getValue());
                EUNISClass.setStyle("-fx-text-fill: black;");
            } 
            if(EUNISClass.getText().equals("")){
                EUNISClass.setStyle("-fx-text-fill: red;");
                Image img = new Image("images/ic_clear_black_18dp.png");
                statusIcon.setImage(img);
                if(lastEdition == null){
                    listView.requestFocus();
                }
                else{                    
                    EUNISClass.setText(lastEdition);
                    EUNISClass.requestFocus();
                    EUNISClass.selectAll();
                }
            }
            else{
                Image img = new Image("images/ic_done_black_18dp.png");
                statusIcon.setImage(img);
            }
            File imagefile = new File(pathOfImage);
            Image image = new Image(imagefile.toURI().toURL().toString());
            imageView.setImage(image);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isValid(String classification){
        if(classification.isEmpty() || classification.equals("-1")){
            return true;
        }
        String[] classes = classification.split("/");
        if(classification.contains("/") && classes.length < 2){
            return false;
        }
        for(String cl: classes){
            Scanner scanner = new Scanner(cl);
            if(!scanner.hasNext()){
                return false;
            }
            String c = scanner.next();
            if(c.equals("")){
                return false;
            }
            char level1 = c.charAt(0);
            if(!Character.isAlphabetic(level1)){
                return false;
            }
            int length = c.length();
            for(int i = 1; i < length; i++){
                if(!Character.isDigit(c.charAt(i)) && c.charAt(i) != '.'){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Application initialized");
    }

    private boolean exitWithUnsavedModifications() {
        if(!isModified){
            return true;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save before leaving?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("You have unsaved changes");
        alert.setTitle("Unsaved Changes");
        alert.setGraphic(new ImageView("images/ic_report_problem_black_48dp.png"));
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

package hijack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.Notifications;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ApplicationController implements Initializable{
    
    private File logFile;
    private File targetsFile;
    private File currentDir;
    private List<String> imageList;
    private boolean isModified;
    private Map<String, Pair<String, String>> data; 
    private String lastEdition;
    private String firstImage;
    private CSVParser csvParser;
    private List<CSVRecord> records;
    private int index;

    @FXML
    private VBox root;

    @FXML
    private Button nextButton;

    @FXML
    private TextField EUNISClass;

    @FXML
    private TextField obsTextField;

    @FXML
    private ImageView statusIcon;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button fullScreenButton;

    @FXML
    private Button previousButton;

    @FXML
    private Label fileName;

    @FXML
    private Label longitude;

    @FXML
    private Label latitude;

    @FXML
    private Label depth;
    
    @FXML
    private Region imageRegion;
    
    @FXML
    private Label imageIndex;
    
    @FXML
    void handleAboutAction(ActionEvent event) throws MalformedURLException {
        Alert about = new Alert(Alert.AlertType.NONE);
        about.setTitle("About HIjACK");
        about.getButtonTypes().add(ButtonType.OK);
        about.setGraphic(new Hyperlink("https://github.com/andrediegues/HIjACk"));        
        about.showAndWait();
    }
    
    @FXML
    void handleShortcutsAction(ActionEvent event) {
        FXMLLoader shortcuts = new FXMLLoader(getClass().getResource("shortcut.fxml"));
        try {
            Parent sc = shortcuts.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(sc));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Help - Shortcuts");
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(ShortcutController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    void handleKeyPress(KeyEvent event) {
        editButton.setDisable(false);
        String name = imageList.get(index);
        KeyCode character = event.getCode();
        //int index = imageList.indexOf(name);
        if(event.isControlDown()){
            switch (character) {
                case S:
                    handleSaveAction(new ActionEvent());
                    break;
                case LEFT:
                    if(index > 10){
                        name = imageList.get(index - 10);
                        index -= 10;
                    }
                    else{
                        name = imageList.get(0);
                        index = 0;
                    }   
                    break;
                case RIGHT:
                    if(index < imageList.size() - 10){
                        name = imageList.get(index + 10);
                        index += 10;
                    }
                    else{                        
                        name = imageList.get(imageList.size() - 1);
                        index = imageList.size() - 1;
                    }   
                    break;
                default:
                    break;
            }
        }
        else if(character.equals(KeyCode.RIGHT) && index < imageList.size() - 1){
            name = imageList.get(index + 1);
            index++;
        }
        else if(character.equals(KeyCode.LEFT) && index > 0){          
            name = imageList.get(index - 1);
            index--;
        }
        else if(character.equals(KeyCode.ENTER)){
            handleEditAction(new ActionEvent());
        }
        else{
            return;
        }
        String pathOfImage = currentDir.getAbsolutePath() + "/" + name;
        loadImage(pathOfImage, name);
    }

    @FXML
    void handleNextAction(ActionEvent event) {
        handleKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false));
    }
    
    @FXML
    void handlePreviousAction(ActionEvent event) {
        handleKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false));
    }   
    
    @FXML
    void handleEditAction(ActionEvent event) {
        String filename = imageList.get(index);
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
            lastEdition = classification;
            String obs = obsTextField.getText();           
            if(!data.get(filename).getKey().equals(classification) || !data.get(filename).getValue().equals(obs)){
                isModified = true;
                EUNISClass.setText(classification);
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
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetsFile));
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
                                .text("File was saved in " + targetsFile.getAbsolutePath())
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
            fsController.addImageToImageView(currentDir + "/" + imageList.get(index));
            Stage fsStage = new Stage();
            fsStage.setScene(new Scene(fsRoot));
            fsStage.initModality(Modality.APPLICATION_MODAL);
            fsStage.setTitle(imageList.get(index));
            fsStage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void initData(Stage appStage, File choice, List<String> listOfNames) throws FileNotFoundException, IOException {
        currentDir = choice;
        logFile = checkExistingLog(choice, choice.getName() + ".csv");
        targetsFile = checkExistingLog(choice, choice.getName() + "-targets.csv");
        imageList = listOfNames;
        isModified = false;
        editButton.setDisable(true);
        data = new TreeMap<>();
        if(logFile != null){
            Reader in = new FileReader(logFile);
            csvParser = new CSVParser(new BufferedReader(in), CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withIgnoreHeaderCase()
            .withTrim());
            records = csvParser.getRecords();
        }
        if(targetsFile == null){
            targetsFile = new File(choice.getAbsolutePath() + "/" + choice.getName() + "-targets.csv");
            listOfNames.forEach((name) -> {
                data.put(name, new Pair("", ""));
                if(firstImage == null){
                    firstImage = name;
                    index = imageList.indexOf(firstImage);
                }
            });
        }        
        else{
            try {
                BufferedReader br = new BufferedReader(new FileReader(targetsFile));
                String line = br.readLine();
                while((line = br.readLine()) != null){
                    if(line.split(",").length <= 1){
                        data.put(line.split(",")[0], new Pair("", ""));
                        if(firstImage == null){
                            firstImage = line.split(",")[0];
                            index = imageList.indexOf(firstImage);
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
        if(firstImage == null){
            index = 0;
            firstImage = imageList.get(index);
        }
        System.out.println("first is null? " +  firstImage);
        loadImage(currentDir.getAbsolutePath() + "/" + firstImage, firstImage);
        appStage.setOnCloseRequest((WindowEvent event) -> {
            exitWithUnsavedModifications();
        });
    }
     
    private File checkExistingLog(File file, String name){
        File[] files = file.listFiles();
        for(File f: files){
            if(f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }

    private void loadImage(String pathOfImage, String filename) {
        try {
            if(data.containsKey(filename)){
                EUNISClass.setText(data.get(filename).getKey());
                obsTextField.setText(data.get(filename).getValue());
                EUNISClass.setStyle("-fx-text-fill: black;");
            } 
            if(EUNISClass.getText().equals("")){
                EUNISClass.setStyle("-fx-text-fill: red;");
                Image img = new Image("images/ic_clear_black_18dp.png");
                statusIcon.setImage(img);
                if(lastEdition != null){                    
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
            imageRegion.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false))));
            fileName.setText("Image: " + filename);
            if(logFile != null){
                CSVRecord record = records.get(imageList.indexOf(filename));
                //System.out.println(filename + " ---- with record: " + record.get("filename"));
                longitude.setText("Lon: " + record.get("longitude") + "\u00B0");
                latitude.setText("Lat: " + record.get("latitude") + "\u00B0");
                depth.setText("Target Depth: " + Double.toString(Double.valueOf(record.get("depth")) + Double.valueOf(record.get("altitude"))) + "m");
            }
            imageIndex.setText((index + 1) + "/" + imageList.size());
            EUNISClass.requestFocus();
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
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'application.fxml'.";
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'application.fxml'.";
        assert EUNISClass != null : "fx:id=\"EUNISClass\" was not injected: check your FXML file 'application.fxml'.";
        assert obsTextField != null : "fx:id=\"obsTextField\" was not injected: check your FXML file 'application.fxml'.";
        assert statusIcon != null : "fx:id=\"statusIcon\" was not injected: check your FXML file 'application.fxml'.";
        assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'application.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'application.fxml'.";
        assert fullScreenButton != null : "fx:id=\"fullScreenButton\" was not injected: check your FXML file 'application.fxml'.";
        assert previousButton != null : "fx:id=\"previousButton\" was not injected: check your FXML file 'application.fxml'.";
        assert fileName != null : "fx:id=\"fileName\" was not injected: check your FXML file 'application.fxml'.";
        assert longitude != null : "fx:id=\"longitude\" was not injected: check your FXML file 'application.fxml'.";
        assert latitude != null : "fx:id=\"latitude\" was not injected: check your FXML file 'application.fxml'.";
        assert depth != null : "fx:id=\"depth\" was not injected: check your FXML file 'application.fxml'.";
        assert imageRegion != null : "fx:id=\"imageRegion\" was not injected: check your FXML file 'application.fxml'.";
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

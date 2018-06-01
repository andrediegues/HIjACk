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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
    private Map<String, Pair<Eunis, List<String>>> data; 
    private String lastEdition;
    private String firstImage;
    private CSVParser csvParser;
    private List<CSVRecord> records;
    private int index;
    private List<String> species;

    @FXML
    private Button nextButton;

    @FXML
    private TextField EUNISClass;

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
    private MenuButton speciesMenuButton;
    
    @FXML
    void handleAboutAction(ActionEvent event) throws MalformedURLException {
        Alert about = HIjACk.createAlert(Alert.AlertType.NONE, "About HIjACK", "", new Hyperlink("https://github.com/andrediegues/HIjACk"), new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
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
            Stage currentStage = HIjACk.getCurrentStage();
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
        if(!exitWithUnsavedModifications()){
            return;
        }  
        try {       
            Stage currentStage = HIjACk.getCurrentStage();
            currentStage.close();
            Parent appLauncher = FXMLLoader.load(getClass().getResource("appLauncher.fxml"));
            Stage newStage = new Stage();
            Scene launcher = new Scene(appLauncher);
            newStage.setScene(launcher);
            newStage.setTitle("HIjACk");
            newStage.show();
            HIjACk.setCurrentStage(newStage);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void handleExitAction(ActionEvent event) {
        if(!exitWithUnsavedModifications()){
            return;
        }        
        Stage currentStage = HIjACk.getCurrentStage();
        currentStage.close();
    }
    
    @FXML
    void handleKeyPress(KeyEvent event) {
        editButton.setDisable(false);
        String name = imageList.get(index);
        KeyCode character = event.getCode();
        if(event.isControlDown()){
            switch (character) {
                case S:
                    handleSaveAction(new ActionEvent());
                    break;
                case UP:
                    if(index > 10){
                        name = imageList.get(index - 10);
                        index -= 10;
                    }
                    else{
                        name = imageList.get(0);
                        index = 0;
                    }   
                    event.consume();
                    break;
                case DOWN:
                    if(index < imageList.size() - 10){
                        name = imageList.get(index + 10);
                        index += 10;
                    }
                    else{                        
                        name = imageList.get(imageList.size() - 1);
                        index = imageList.size() - 1;
                    }   
                    event.consume();
                    break;
                default:
                    break;
            }
        }
        else if(character.equals(KeyCode.DOWN) && index < imageList.size() - 1){
            name = imageList.get(index + 1);
            index++;
            event.consume();
        }
        else if(character.equals(KeyCode.UP) && index > 0){          
            name = imageList.get(index - 1);
            index--;
            event.consume();
        }
        else if(character.equals(KeyCode.ENTER)){
            handleEditAction(new ActionEvent());
            EUNISClass.setStyle("-fx-text-fill: black;");
            Image img = new Image("images/ic_done_black_18dp.png");
            statusIcon.setImage(img);
            return;
        }
        else{
            return;
        }
        String pathOfImage = currentDir.getAbsolutePath() + "/" + name;
        loadImage(pathOfImage, name);
    }

    @FXML
    void handleNextAction(ActionEvent event) {
        handleKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false));
    }
    
    @FXML
    void handlePreviousAction(ActionEvent event) {
        handleKeyPress(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false));
    }   
    
    @FXML
    void handleEditAction(ActionEvent event) {
        String filename = imageList.get(index);
        String classification = EUNISClass.getText().toUpperCase();
        if(classification.contains(" ")){
            Alert whiteSpaces = HIjACk.createAlert(Alert.AlertType.NONE, classification + " contains blank spaces!", "Please remove all blank spaces.", 
                    new ImageView("images/ic_error_black_48dp.png"), new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
            whiteSpaces.showAndWait();
            classification = null;
        }
        else if(!isValid(classification)){
            Alert invalidClass = HIjACk.createAlert(Alert.AlertType.NONE, classification + " is not a valid classification!", "Please insert a valid classification.", 
                    new ImageView("images/ic_error_black_48dp.png"), new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
            invalidClass.showAndWait();
            classification = null;
        }
        else{
            Eunis eunis = new Eunis(classification);
            if(!classification.isEmpty()){
                lastEdition = classification;
            }
            EUNISClass.setText(classification);
            if(!data.get(filename).getKey().equals(eunis) || !data.get(filename).getValue().equals(species)){
                isModified = true;
                data.put(filename, new Pair(classification, species));
                Image img = new Image("images/ic_done_black_48dp.png");
                Notifications editNotification = HIjACk.createNotification("Edited " + filename + " successfully", Duration.seconds(3), "Edit", new ImageView(img), Pos.TOP_RIGHT);
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
                if(logFile != null){
                    bw.write("filename, date, longitude, latitude, depth, EunisCode, EunisName, level1, level2, level3, level4, level5, level6, species, AphiaID\n");
                    data.forEach((String key, Pair value) -> {
                        try {
                            // for loop to iterate all species; put lon, lat, depth, date
                            bw.write(key + "," + value.getKey().toString() + "," + value.getValue() + "\n");
                        } catch (IOException ex) {
                            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
                else{
                    bw.write("filename, EunisCode, EunisName, level1, level2, level3, level4, level5, level6, species, AphiaID\n");
                }
                data.forEach((String key, Pair value) -> {
                    try {
                        bw.write(key + "," + value.getKey() + "," + value.getValue() + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                bw.close();
                Image img = new Image("images/ic_done_black_48dp.png");
                Notifications saveNotification = HIjACk.createNotification("File was saved in " + targetsFile.getAbsolutePath(), Duration.seconds(3), "Save", new ImageView(img), Pos.TOP_RIGHT);
                saveNotification.show();
            }
            else{
                Image img = new Image("images/ic_info_black_48dp.png");
                Notifications saveNotification = HIjACk.createNotification("Nothing to save", Duration.seconds(3), "Save", new ImageView(img), Pos.TOP_RIGHT);
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
    
    @FXML
    void handleNewEntry(ActionEvent event) {
        try {
            FXMLLoader newEntry = new FXMLLoader(getClass().getResource("addSpecies.fxml"));
            Parent neRoot = newEntry.load();
            AddSpeciesController controller = newEntry.getController();
            Stage currentStage = HIjACk.getCurrentStage();
            Stage stage = new Stage();
            stage.setScene(new Scene(neRoot));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Adding new species...");
            HIjACk.setCurrentStage(stage);
            stage.showAndWait();
            HIjACk.setCurrentStage(currentStage);
            
            String newItem = controller.getSpeciesName();
            if(newItem.isEmpty() || species.contains(newItem)){
                return;
            }
            Button remove = new Button("", new ImageView(new Image("images/ic_clear_black_18dp.png")));
            MenuItem newSpeciesItem = new MenuItem(newItem, remove);
            speciesMenuButton.getItems().add(0, newSpeciesItem);
            species.add(newItem);
            newSpeciesItem.setStyle("-fx-text-fill: black;");
            remove.setOnAction((ActionEvent event1) -> {
                speciesMenuButton.getItems().remove(newSpeciesItem);
                species.remove(newItem);
            });
        } catch (IOException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void initData(File choice, List<String> listOfNames) throws FileNotFoundException, IOException {
        Stage appStage = HIjACk.getCurrentStage();
        currentDir = choice;
        logFile = checkExistingLog(choice, choice.getName() + ".csv");
        targetsFile = checkExistingLog(choice, choice.getName() + "-targets.csv");
        imageList = listOfNames;
        isModified = false;
        editButton.setDisable(true);
        data = new TreeMap<>();
        species = new ArrayList<>();
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
                data.put(name, new Pair(new Eunis(""), new ArrayList<>()));
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
                        data.put(line.split(",")[0], new Pair(new Eunis(""), new ArrayList<>()));
                        if(firstImage == null){
                            firstImage = line.split(",")[0];
                            index = imageList.indexOf(firstImage);
                        }
                    }
                    else if(line.split(",").length <= 2){
                        data.put(line.split(",")[0], new Pair(line.split(",")[1], ""));
                    }
                    else{
                        data.put(line.split(",")[0], new Pair(new Eunis(line.split(",")[2]), new ArrayList<>()));
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
        loadImage(currentDir.getAbsolutePath() + "/" + firstImage, firstImage);
        appStage.setOnCloseRequest((WindowEvent event) -> {
            if(!exitWithUnsavedModifications()){
                event.consume();
            }
        });
        
        Timeline autosave5min = new Timeline(new KeyFrame(Duration.seconds(300), (ActionEvent event) -> {
            handleSaveAction(new ActionEvent());
        }));
        autosave5min.setCycleCount(Timeline.INDEFINITE);
        autosave5min.play();
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
                EUNISClass.setText(data.get(filename).getKey().getClassification());
                // load list of species
                EUNISClass.setStyle("-fx-text-fill: black;");
            } 
            if(EUNISClass.getText().equals("")){
                EUNISClass.setStyle("-fx-text-fill: red;");
                Image img = new Image("images/ic_clear_black_18dp.png");
                statusIcon.setImage(img);
                if(lastEdition != null){                    
                    EUNISClass.setText(lastEdition);
                }
                EUNISClass.requestFocus();
                EUNISClass.selectAll();
            }
            File imagefile = new File(pathOfImage);
            Image image = new Image(imagefile.toURI().toURL().toString());
            imageRegion.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false))));
            fileName.setText("Image: " + filename);
            if(logFile != null){
                CSVRecord record = records.get(imageList.indexOf(filename));
                longitude.setText("Lon: " + record.get("longitude"));
                latitude.setText("Lat: " + record.get("latitude"));
                depth.setText("Target Depth: " + Double.toString(Double.valueOf(record.get("depth")) + Double.valueOf(record.get("altitude"))) + "m");
            }
            imageIndex.setText((index + 1) + "/" + imageList.size());
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Application initialized");
        assert nextButton != null : "fx:id=\"nextButton\" was not injected: check your FXML file 'application.fxml'.";
        assert EUNISClass != null : "fx:id=\"EUNISClass\" was not injected: check your FXML file 'application.fxml'.";
        assert speciesMenuButton != null : "fx:id=\"speciesMenuButton\" was not injected: check your FXML file 'application.fxml'.";
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
        Alert alert = HIjACk.createAlert(Alert.AlertType.NONE, "Unsaved changes", "Do you want to save before leaving?", 
                new ImageView("images/ic_report_problem_black_48dp.png"), new ButtonType("Yes", ButtonBar.ButtonData.YES), new ButtonType("No", ButtonBar.ButtonData.NO), new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.showAndWait();
        ButtonType result = alert.getResult();
        
        if(!result.getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)){
            if(result.getButtonData().equals(ButtonBar.ButtonData.YES)){
                handleSaveAction(new ActionEvent());
            }
            return true;
        }
        return false;
    }
    private boolean isValid(String classification){
        if(classification.isEmpty() || classification.equals("-1")){
            return true;
        }
        Scanner scanner = new Scanner(classification);
        if(!scanner.hasNext()){
            return false;
        }
        String c = scanner.next();
        char type = c.charAt(0);
        if(!Character.isAlphabetic(type)){
            return false;
        }
        int length = c.length();
        for(int i = 1; i < length; i++){
            if(!Character.isDigit(c.charAt(i)) && c.charAt(i) != '.'){
                return false;
            }
        }
        return true;
    }
}

package hijack;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

public class LoadingController {

    @FXML
    private ProgressBar progressBar;
    
    @FXML
    void initialize() {
        System.out.println("hijack.LoadingController.initialize()");
        progressBar = new ProgressBar(0);
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'loading.fxml'.";
    }
    
    void handleProgression(File file) {
        int numberOfFiles = file.listFiles().length;
        for(int i = 0; i < numberOfFiles; i++){
            double percentage = i / numberOfFiles;
            System.out.println("i: " + i + " - " + (i * 100) / numberOfFiles);
            if(percentage * 100 % 5 == 0){
                progressBar.setProgress(percentage);
            }
        }
    }
}

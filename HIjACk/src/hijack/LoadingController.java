package hijack;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

public class LoadingController {

    @FXML
    private ProgressBar progressBar;
    
    @FXML
    void initialize() {
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'loading.fxml'.";
    }
}

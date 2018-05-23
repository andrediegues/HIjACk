package hijack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;

/**
 * FXML Controller class
 *
 * @author diegues
 */
public class FullscreenController implements Initializable {

    
    @FXML
    private ImageView fullScreenImageView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert fullScreenImageView != null : "fx:id=\"fullScreenImageView\" was not injected: check your FXML file 'fullscreen.fxml'.";
    }    

    void addImageToImageView(String string) {
        try {
            File imagefile = new File(string);
            Image image = new Image(imagefile.toURI().toURL().toString());
            double width = image.getWidth();
            double height = image.getHeight();
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            if(bounds.getHeight() < height){
                height = bounds.getHeight();
            }
            if(bounds.getWidth()< width){
                width = bounds.getWidth();
            }
            fullScreenImageView.setFitWidth(width);
            fullScreenImageView.setFitHeight(height);
            fullScreenImageView.setImage(image);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FullscreenController.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
}

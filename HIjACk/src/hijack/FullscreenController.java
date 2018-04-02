/*
 * Copyright (C) 2018 diegues
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hijack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author diegues
 */
public class FullscreenController implements Initializable {

    
    @FXML
    private ImageView fullScreenImageView;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
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
            fullScreenImageView.setFitWidth(width);
            fullScreenImageView.setFitHeight(height);
            fullScreenImageView.setImage(image);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FullscreenController.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
}

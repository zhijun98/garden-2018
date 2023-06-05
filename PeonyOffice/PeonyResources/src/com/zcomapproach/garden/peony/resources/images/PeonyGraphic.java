/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.peony.resources.images;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.ImageIcon;

/**
 *
 * @author zhijun98
 */
public class PeonyGraphic {

    public static ImageView getImageView(String imageFileName){
        return new ImageView(getJavaFxImage(imageFileName));
    }
    
    public static Image getJavaFxImage(String imageFileName){
        return new Image(getImageInputStream(imageFileName));
    }
    
    public static InputStream getImageInputStream(String imageFileName){
        return PeonyGraphic.class.getResourceAsStream(imageFileName);
    }
    
    public static java.awt.Image getAwtImage(String imageFileName){
        return getSwingImageIcon(imageFileName).getImage();
    }
    
    public static ImageIcon getSwingImageIcon(String imageFileName){
        return new ImageIcon(PeonyGraphic.class.getResource(imageFileName));
    }
}

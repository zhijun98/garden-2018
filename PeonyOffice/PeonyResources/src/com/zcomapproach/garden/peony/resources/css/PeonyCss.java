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

package com.zcomapproach.garden.peony.resources.css;

import java.io.InputStream;
import javafx.scene.paint.Color;

/**
 *
 * @author zhijun98
 */
public class PeonyCss {
    
    public static final Color BLACK = Color.web("#000000");
    public static final Color WHITE = Color.web("#000000");
    public static final Color DARKRED = Color.web("#980000");
    public static final Color DARKBLUE = Color.web("#000098");
    
    /**
     * Global CSS for Peony
     * @return 
     */
    public static String getPeonyGlobalCss(){
        return PeonyCss.class.getResource("PeonyCss.css").toExternalForm();
    }
    
    /**
     * 
     * @param aPeonyFontFileName - if it is NULL, the default is PeonyFontFileName.MingLiU
     * @return 
     */
    public static InputStream getFontInputStream(PeonyFontFileName aPeonyFontFileName){
        if ((aPeonyFontFileName == null) || (PeonyFontFileName.UNKNOWN.equals(aPeonyFontFileName))){
            aPeonyFontFileName = PeonyFontFileName.MingLiU;
        }
        return PeonyCss.class.getResourceAsStream(aPeonyFontFileName.value());
    }
}

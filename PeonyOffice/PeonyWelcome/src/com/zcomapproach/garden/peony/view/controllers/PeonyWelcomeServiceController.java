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

package com.zcomapproach.garden.peony.view.controllers;

import com.zcomapproach.garden.peony.view.PeonyFaceController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyWelcomeServiceController extends PeonyFaceController{
    
    /**
     * Help load the FXML file for controllers
     * @param aFXMLLoader
     * @return
     * @throws IOException 
     */
    @Override
    protected Pane loadFxmlHelper(FXMLLoader aFXMLLoader) throws IOException{
        return (Pane)aFXMLLoader.load();
    }

}

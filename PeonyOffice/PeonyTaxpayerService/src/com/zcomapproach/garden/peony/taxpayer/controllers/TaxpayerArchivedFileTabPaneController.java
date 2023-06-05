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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.view.controllers.PeonyArchivedFilePaneController;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class TaxpayerArchivedFileTabPaneController extends PeonyTaxpayerCaseTabPaneController{
    @FXML
    private Label pageTitleLabel;
    @FXML
    private VBox tabRootVBox;
    @FXML
    private GridPane topTitleGridPane;
    @FXML
    private HBox topButtonsHBox;
    
    private PeonyArchivedFilePaneController peonyArchivedDocumentPaneController;

    /**
     * 
     * @param targetPeonyTaxpayerCase - cannot be NULL, and taxpayer's UUID, tax-return 
     * deadline and customer-instance should be ready
     */
    public TaxpayerArchivedFileTabPaneController(PeonyTaxpayerCase targetPeonyTaxpayerCase, Tab ownerTab) {
        super(targetPeonyTaxpayerCase, ownerTab);
    }

    @Override
    protected void setPageTitle(final String pageTitle) {
        if (Platform.isFxApplicationThread()){
            pageTitleLabel.setText(pageTitle);
        }else{
            Platform.runLater(() -> {
                pageTitleLabel.setText(pageTitle);
            });
        }
    }

    @Override
    protected void decoratePeonyFaceAfterLoadingHelper(Scene scene) {
        if ((peonyArchivedDocumentPaneController != null) && (peonyArchivedDocumentPaneController.getRootPane() != null)){
            peonyArchivedDocumentPaneController.getRootPane().prefHeightProperty().bind(tabRootVBox.heightProperty()
                    .subtract(topTitleGridPane.heightProperty()).subtract(50));
            peonyArchivedDocumentPaneController.decoratePeonyFaceAfterLoading(scene);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //topButtonsHBox
        topButtonsHBox.getChildren().addAll(constructCommonTaxpayerCaseFunctionalButtons());
        
        if (peonyArchivedDocumentPaneController == null){
            Path archivedFileRoot = Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder());
            try {
                if (Files.notExists(archivedFileRoot)){
                    /**
                     * todo zzj: potentially here it may produce empty folder for 
                     * the brand-new taxpayer case. It may need garbage folder collection 
                     * in the future.
                     */
                    ZcaNio.createFolder(archivedFileRoot);
                }
                peonyArchivedDocumentPaneController = new PeonyArchivedFilePaneController(archivedFileRoot, 
                                                            getTargetPeonyTaxpayerCase().getPeonyArchivedFileList(), 
                                                            getTargetPeonyTaxpayerCase());
                peonyArchivedDocumentPaneController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
                tabRootVBox.getChildren().add(peonyArchivedDocumentPaneController.loadFxml());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected void loadTargetTaxpayerCaseFromTab() throws ZcaEntityValidationException {
        //do nothing here since all the save-operation are done locally
    }

    @Override
    protected void resetDataEntryStyleFromTab() {
        //do nothing here
    }
}

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

package com.zcomapproach.garden.peony.utils;

import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.IPeonyTopComponent;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.commons.view.ZcaFaceUtils;
import static com.zcomapproach.commons.view.ZcaFaceUtils.displayMessageDialog;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.awt.Desktop;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.Toolbar;
import org.openide.awt.ToolbarPool;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
public class PeonyFaceUtils extends ZcaFaceUtils{
    
    public static final String FILL_DEFUALT_COLOR = "-fx-text-fill: black;";
    public static final String FILL_HIGHLIGHT_COLOR = "-fx-text-fill: #0000ff;";
    public static final String FONT_ITALIC_STYLE = "-fx-font-style: italic;";
    public static final String FONT_NORMAL_STYLE = "-fx-font-style: normal;";
    public static final String WARNING_STYLE = "-fx-border-color: #0000ff;";

    /**
     * anchorPane containts the content presented by peonyFaceController;
     * @param peonyFaceController
     * @param anchorPane 
     */
    public static void populateOntoAnchorPane(final PeonyFaceController peonyFaceController, final AnchorPane anchorPane){
        publishPaneOntoAnchorPane(peonyFaceController.getRootPane(), anchorPane);
    }
    
    public static void populateOntoScrollPane(final PeonyFaceController peonyFaceController, final ScrollPane scrollPane){
        publishPaneOntoScrollPane(peonyFaceController.getRootPane(), scrollPane);
    }
    
    /**
     * find the top component and dispaly it in EDT. This method does not demand EDT
     * @param topComponentSingletonName 
     */
    public static void openExistingTopComponentSingleton(final String topComponentSingletonName){
        if (SwingUtilities.isEventDispatchThread()){
            openExistingTopComponentSingletonHelper(topComponentSingletonName);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openExistingTopComponentSingletonHelper(topComponentSingletonName);
                }
            });
        }
    }

    /**
     * This method demands EDT: find the top component, dispaly it and return it
     * @param topComponentSingletonName 
     */
    private static TopComponent openExistingTopComponentSingletonHelper(String topComponentSingletonName) {
        TopComponent aTopComponent = getExistingTopComponentSingleton(topComponentSingletonName);
        if (aTopComponent != null){
            aTopComponent.open();
            aTopComponent.requestActive();
        }
        return aTopComponent;
    }
    
    /**
     * Get the initialized topComponentSingletonName instance. if it was not there, NULL returns
     * @param topComponentSingletonName
     * @return 
     */
    public static TopComponent getExistingTopComponentSingleton(String topComponentSingletonName) {
        return WindowManager.getDefault().findTopComponent(topComponentSingletonName);
    }
    
    public static void publishMessageOntoOutputWindowWithErrorPopup(final String message){
        publishMessageOntoOutputWindow(message);
        displayErrorMessageDialog(message);
    }
    
    public static void publishMessageOntoOutputWindowWithInformationPopup(final String message){
        publishMessageOntoOutputWindow(message);
        displayInformationMessageDialog(message);
    }
    
    /**
     * Publish message onto the output window
     * @param message 
     */
    public static void publishMessageOntoOutputWindow(final String message){
        if (SwingUtilities.isEventDispatchThread()){
            publishMessageOntoOutputWindowHelper(message);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    publishMessageOntoOutputWindowHelper(message);
                }
            });
        }
    }
    private static void publishMessageOntoOutputWindowHelper(final String message){
        StatusDisplayer.getDefault().setStatusText(message);
        InputOutput io = IOProvider.getDefault().getIO("Logging Status", false);
        
        DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        io.getOut().println(timestamp + " > " + message);
        io.getOut().close();
    }
    
    public static Node createFontAwesomeNode(FontAwesome.Glyph aGlyph, Color aColor){
        return GlyphFontRegistry.font("FontAwesome").create(aGlyph.getChar()).color(aColor);
    }

    public static void blurOpenedTopComponentRootsBeforeLogin() {
        /**
         * todo zzj: how to blur/clear JComponent? Temp-solution is to close any non-Peony TopComponent before blur
         */
        if (SwingUtilities.isEventDispatchThread()){
            closeNonPeonyTopComponentsHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    closeNonPeonyTopComponentsHelper();
                }
            });
        }
        
        if (Platform.isFxApplicationThread()){
            blurOpenedTopComponentRootsBeforeLoginHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    blurOpenedTopComponentRootsBeforeLoginHelper();
                }
            });
        }
    }
    
    private static void closeNonPeonyTopComponentsHelper(){
        WindowManager aWindowManager = WindowManager.getDefault(); 
        Set<? extends Mode> modeSet = aWindowManager.getModes();
        TopComponent[] topComponentArray;
        for (Mode mode : modeSet){
            topComponentArray = aWindowManager.getOpenedTopComponents(mode);
            for (TopComponent aTopComponent : topComponentArray){
                if (aTopComponent instanceof IPeonyTopComponent){
                    //do blurry after this method executed
                }else{
                    if (aTopComponent != null){
                        aTopComponent.close();
                    }
                }
            }//for
        }//for
    }
    
    private static void blurOpenedTopComponentRootsBeforeLoginHelper(){
        WindowManager aWindowManager = WindowManager.getDefault(); 
        Set<? extends Mode> modeSet = aWindowManager.getModes();
        TopComponent[] topComponentArray;
        for (Mode mode : modeSet){
            topComponentArray = aWindowManager.getOpenedTopComponents(mode);
            for (TopComponent aTopComponent : topComponentArray){
                if (aTopComponent instanceof IPeonyTopComponent){
                    ((IPeonyTopComponent)aTopComponent).makeJfxPanelRootBlurry();
                }
            }//for
        }//for
    }

    public static void clearOpenedTopComponentRootsAfterLogin() {
        if (Platform.isFxApplicationThread()){
            clearOpenedTopComponentRootsAfterLoginHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    clearOpenedTopComponentRootsAfterLoginHelper();
                }
            });
        }
    }

    private static void clearOpenedTopComponentRootsAfterLoginHelper() {
        WindowManager aWindowManager = WindowManager.getDefault(); 
        Set<? extends Mode> modeSet = aWindowManager.getModes();
        TopComponent[] topComponentArray;
        for (Mode mode : modeSet){
            topComponentArray = aWindowManager.getOpenedTopComponents(mode);
            for (TopComponent aTopComponent : topComponentArray){
                if (aTopComponent instanceof IPeonyTopComponent){
                    ((IPeonyTopComponent)aTopComponent).makeJfxPanelRootClear();
                }
            }//for
        }//for
    }
    
    public static FlowPane constructFlowPaneSeparator(){
        FlowPane aFlowPane = new FlowPane();
        
        aFlowPane.setAlignment(Pos.CENTER_LEFT);
        aFlowPane.prefHeightProperty().setValue(2.0);
        FlowPane.setMargin(aFlowPane, new Insets(2,0,2,0));
        aFlowPane.setStyle("-fx-background-color: #e8e8ff;");
        
        return aFlowPane;
    }
    /**
     * Only disable the top menus in the menubar but not its sub-menus and/or their menu-items
     * @param aMenuBar
     * @param enabled 
     */
    public static void setMenuBarEnabled(JMenuBar aMenuBar, boolean enabled){
        if (aMenuBar != null){
            JMenu aMenu;
            for (int i = 0; i < aMenuBar.getMenuCount(); i++){
                aMenu = aMenuBar.getMenu(i);
                aMenu.setEnabled(enabled);
            }
        }
    }
    
    /**
     * Only disable the toolbar but not its items
     * @param visible 
     */
    public static void setToolbarPoolVisible(boolean visible){
        Toolbar[] toolbarArray = ToolbarPool.getDefault().getToolbars();
        if (toolbarArray != null){
            for (Toolbar aToolbar : toolbarArray){
                aToolbar.setVisible(visible);
                //Component[] components = aToolbar.getComponents();
                //for (Component component : components){
                //    component.setEnabled(enabled);
                //}
            }
        }
    }

    public static void makeViewRootBlurry(Node root, double radius) {
        if (root == null){
            return;
        }
        //Instantiating the ColorAdjust class 
        ColorAdjust colorAdjust = new ColorAdjust();
        GaussianBlur blur = new GaussianBlur(radius);
        colorAdjust.setInput(blur);
        root.setEffect(colorAdjust);
    }

    public static void makeViewRootClear(Node root) {
        if (root == null){
            return;
        }
        root.setEffect(new ColorAdjust());
    }

    public static File getFolderByFileChooser(JFileChooser fileChooser, Frame frame, String lastSelectedPath){
        File result;
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (ZcaValidator.isNotNullEmpty(lastSelectedPath)){
            File existingDirectoryOrFile = new File(lastSelectedPath);
            if (existingDirectoryOrFile.isDirectory()){
                fileChooser.setCurrentDirectory(existingDirectoryOrFile);
            }
        }

        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            result = fileChooser.getSelectedFile();
        }else{
            result = fileChooser.getCurrentDirectory();
        }

        return result;
    }
    
    /**
     * 
     * @param fileChooser
     * @param frame
     * @param lastSelectedPath - the file path which is used to display n fileChooser
     * @return
     */
    public static String getFileFullPathByFileChooser(JFileChooser fileChooser, Frame frame, String lastSelectedPath){
        String result = "";
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (ZcaValidator.isNotNullEmpty(lastSelectedPath)){
            File existingDirectoryOrFile = new File(lastSelectedPath);
            if (existingDirectoryOrFile.isDirectory()){
                fileChooser.setCurrentDirectory(existingDirectoryOrFile);
            }else if (existingDirectoryOrFile.isFile()){
                fileChooser.setSelectedFile(existingDirectoryOrFile);
            }
        }

        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            result = fileChooser.getSelectedFile().getAbsolutePath();
        }

        return result;
    }
    
    /**
     * Display a confirm dialog with "Confirm" as title and JOptionPane.YES_NO_OPTION
     * @param msg
     * @return - e.g. JOptionPane.YES_OPTION
     */
    public static int displayConfirmDialog(final Frame frame, final String msg) {
        return displayConfirmDialog(frame, msg, "Confirm", JOptionPane.YES_NO_OPTION);
    }
    
    /**
     * 
     * @param msg
     * @param title
     * @return - e.g. JOptionPane.YES_OPTION
     */
    public static int displayConfirmDialog(final Frame frame, final String msg, final String title) {
        return displayConfirmDialog(frame, msg, title, JOptionPane.YES_NO_OPTION);
    }
    
    /**
     * This method is executed in EDT and only check if the number of opened TopComponents 
     * are too many for performance consideration. If too many, warning dialog will pop up.
     * 
     */
    public static void checkOpenedEditorTopComponentThreshold(){
        if (SwingUtilities.isEventDispatchThread()){
            checkOpenedEditorTopComponentThresholdHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    checkOpenedEditorTopComponentThresholdHelper();
                }
            });
        }
    }
    private static void checkOpenedEditorTopComponentThresholdHelper(){
        WindowManager wm = WindowManager.getDefault();
        Mode editor = wm.findMode("editor");
        if (wm.getOpenedTopComponents(editor).length > PeonyProperties.getSingleton().getOpenedEditorWindowsThreshold()){
            PeonyFaceUtils.displayWarningMessageDialog("Too many windows are opened. Please close some of them for better performance.");
        }
    }
    
    /**
     * This method demands EDT: it searchs all the opened top-components which stays 
     * in the "editor" zone
     * @param targetTopComponentUuid
     * @return 
     */
    public static PeonyTopComponent findPeonyTopComponentFromEditorZone(String targetTopComponentUuid) {
        if (ZcaValidator.isNullEmpty(targetTopComponentUuid)){
            return null;
        }
        WindowManager wm = WindowManager.getDefault();
        Mode editor = wm.findMode("editor");
        PeonyTopComponent result;
        for (TopComponent aTopComponent : wm.getOpenedTopComponents(editor)){
            if (aTopComponent instanceof PeonyTopComponent){
                result = (PeonyTopComponent)aTopComponent;
                if (targetTopComponentUuid.equalsIgnoreCase(result.getTargetTopComponetUuid())){
                    return result;
                }
            }
        }//for
        
        return null;
    }

    /**
     * 
     * This method will be executed in the AWT/Swing-thread
     * 
     * @param targetTopComponentUuid 
     */
    public static void closePeonyTopComponent(final String targetTopComponentUuid) {
        if (SwingUtilities.isEventDispatchThread()){
            closePeonyTopComponentHelper(targetTopComponentUuid);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    closePeonyTopComponentHelper(targetTopComponentUuid);
                }
            });
        }
    }
    private static void closePeonyTopComponentHelper(final String targetTopComponentUuid) {
        PeonyTopComponent aPeonyTopComponent = PeonyFaceUtils.findPeonyTopComponentFromEditorZone(targetTopComponentUuid);
        if (aPeonyTopComponent != null){
            aPeonyTopComponent.peonyFaceEventHappened(new CloseTopComponentRequest());
        }
    }

    public static void openFile(File aFile) throws IOException {
        if (aFile == null){
            throw new IOException("The file for open is not provided.");
        }
        if (aFile.exists()){
            String localFilePath = PeonyProperties.getSingleton().getLocalUserTempPath()
                    .resolve(FilenameUtils.getName(aFile.getAbsolutePath())).toAbsolutePath().toString();
            File localFile = new File(localFilePath);
            if (!localFilePath.equalsIgnoreCase(aFile.getAbsolutePath())){
                ZcaNio.copyFile(aFile, localFile);
            }
            //open the copy of aFile
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(localFile);
            } else {
                PeonyFaceUtils.displayErrorMessageDialog("Your selected file is not supported.");
            }
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("Your selected file does not exist.");
        }
    }

    public static void displayPrivilegeErrorMessageDialog() {
        displayErrorMessageDialog("You are not authorized to do this operation. Please contact your supervisor for operation privliges."); 
    }

    public static void displayJfxTaskFailedErrorMessageDialog(String exMsg) {
        displayErrorMessageDialog("This operation failed due to tachnical error. " + exMsg);
    }
    
    public static void displayErrorMessageDialog(final String msg){
        if (SwingUtilities.isEventDispatchThread()){
            displayMessageDialog(PeonyLauncher.mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayMessageDialog(PeonyLauncher.mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
    
    public static void displayWarningMessageDialog(final String msg){
        if (SwingUtilities.isEventDispatchThread()){
            displayMessageDialog(PeonyLauncher.mainFrame, msg, "Warning", JOptionPane.WARNING_MESSAGE);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayMessageDialog(PeonyLauncher.mainFrame, msg, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }
    
    public static void displayInformationMessageDialog(final String msg){
        if (SwingUtilities.isEventDispatchThread()){
            displayMessageDialog(PeonyLauncher.mainFrame, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayMessageDialog(PeonyLauncher.mainFrame, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }
    
    /**
     * 
     * @param aComboBox
     * @param valueList
     * @param value
     * @param defaultValue
     * @param tooltip
     * @param aPeonyFaceController - optionally, it's used for listening to the data entry change
     */
    public static void initializeComboBox(ComboBox<String> aComboBox, 
                                          List<String> valueList, 
                                          String value, 
                                          String defaultValue, 
                                          String tooltip,
                                          final PeonyFaceController aPeonyFaceController)
    {
        initializeComboBox(aComboBox, valueList, value, defaultValue, tooltip, false, aPeonyFaceController);
    }
    
    public static void initializeComboBox(ComboBox<String> aComboBox, 
                                          List<String> valueList, 
                                          String value, 
                                          String defaultValue, 
                                          String tooltip,
                                          boolean cssRequired,
                                          final PeonyFaceController aPeonyFaceController)
    {
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aComboBox.setTooltip(new Tooltip(tooltip));
        }
        aComboBox.getItems().addAll(valueList);
        if (value == null){
            aComboBox.getSelectionModel().select(defaultValue);
        }else{
            aComboBox.getSelectionModel().select(value);
        }
        if (aPeonyFaceController != null){
            aComboBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override 
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (cssRequired){
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aComboBox.setStyle(WARNING_STYLE);
                    }
                }    
            });
        }
    }
    
    public static void initializeCheckComboBox(CheckComboBox<String> aComboBox, 
                                          List<String> valueList, 
                                          String value, 
                                          String defaultValue, 
                                          String tooltip,
                                          final PeonyFaceController aPeonyFaceController)
    {
        initializeCheckComboBox(aComboBox, valueList, value, defaultValue, tooltip, false, aPeonyFaceController);
    }
    
    public static void initializeCheckComboBox(CheckComboBox<String> aCheckComboBox, 
                                          List<String> valueList, 
                                          String value, 
                                          String defaultValue, 
                                          String tooltip,
                                          boolean cssRequired,
                                          final PeonyFaceController aPeonyFaceController)
    {
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aCheckComboBox.setTooltip(new Tooltip(tooltip));
        }
        aCheckComboBox.getItems().addAll(valueList);
        if (value == null){
            aCheckComboBox.getCheckModel().check(defaultValue);
        }else{
            aCheckComboBox.getCheckModel().check(value);
        }
        if (aPeonyFaceController != null){
            aCheckComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                @Override 
                public void onChanged(ListChangeListener.Change<? extends String> c) {
                    if (cssRequired){
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aCheckComboBox.setStyle(WARNING_STYLE);
                    }
                }    
            });
        }
    }
    
    public static void initializeDatePicker(DatePicker aDatePicker, 
                                            LocalDate date, 
                                            LocalDate defaultDate, 
                                            String tooltip,
                                            final PeonyFaceController aPeonyFaceController)
    {
        initializeDatePicker(aDatePicker, date, defaultDate, tooltip, false, aPeonyFaceController);
    }
    
    public static void initializeDatePicker(DatePicker aDatePicker, 
                                            LocalDate date, 
                                            LocalDate defaultDate, 
                                            String tooltip,
                                            boolean cssRequired,
                                            final PeonyFaceController aPeonyFaceController)
    {
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aDatePicker.setTooltip(new Tooltip(tooltip));
////            aDatePicker.setPromptText(tooltip);
        }
        if (date == null){
            aDatePicker.setValue(defaultDate);
        }else{
            aDatePicker.setValue(date);
        }
        if (aPeonyFaceController != null){
            aDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
                @Override 
                public void changed(ObservableValue ov, LocalDate oldValue, LocalDate newValue) {
                    if (cssRequired){                
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aDatePicker.setStyle(WARNING_STYLE);
                    }
                }    
            });
        }
    }

    public static void initializeCheckBox(CheckBox aCheckBox, 
                                           boolean value, 
                                           String tooltip,
                                           final PeonyFaceController aPeonyFaceController) 
    {
        initializeCheckBox(aCheckBox, value, tooltip, false, aPeonyFaceController);
    }

    public static void initializeCheckBox(CheckBox aCheckBox, 
                                           boolean value, 
                                           String tooltip,
                                           boolean cssRequired,
                                           final PeonyFaceController aPeonyFaceController) 
    {
        aCheckBox.setSelected(value);
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aCheckBox.setTooltip(new Tooltip(tooltip));
        }
        if (aPeonyFaceController != null){
            
            aCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override 
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    if (cssRequired){
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aCheckBox.setStyle(WARNING_STYLE);
                    }
                }    
            });
        }
    }
    
    public static void initializeJfxTextField(JFXTextField aTextField, 
                                           String value, 
                                           String defaultValue, 
                                           String tooltip,
                                           final PeonyFaceController aPeonyFaceController)
    {
        initializeJfxTextField(aTextField, value, defaultValue, tooltip, false, aPeonyFaceController);
    }
    
    public static void initializeJfxTextField(JFXTextField aTextField, 
                                           String value, 
                                           String defaultValue, 
                                           String tooltip,
                                           boolean cssRequired,
                                           final PeonyFaceController aPeonyFaceController)
    {
        if (ZcaValidator.isNullEmpty(value)){
            aTextField.textProperty().setValue(defaultValue);
        }else{
            aTextField.textProperty().setValue(value);
        }
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aTextField.setTooltip(new Tooltip(tooltip));
////            aTextField.setPromptText(tooltip);
        }
        if (aPeonyFaceController != null){
            aTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override 
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (cssRequired){
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aTextField.setStyle(WARNING_STYLE);
                    }
                    
                }    
            });
        }
    }
    
    public static void initializeTextField(TextField aTextField, 
                                           String value, 
                                           String defaultValue, 
                                           String tooltip,
                                           final PeonyFaceController aPeonyFaceController)
    {
        initializeTextField(aTextField, value, defaultValue, tooltip, false, aPeonyFaceController);
    }
    
    public static void initializeTextField(TextField aTextField, 
                                           String value, 
                                           String defaultValue, 
                                           String tooltip,
                                           boolean cssRequired,
                                           final PeonyFaceController aPeonyFaceController)
    {
        if (ZcaValidator.isNullEmpty(value)){
            aTextField.textProperty().setValue(defaultValue);
        }else{
            aTextField.textProperty().setValue(value);
        }
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aTextField.setTooltip(new Tooltip(tooltip));
        }
        if (aPeonyFaceController != null){
            aTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override 
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (cssRequired){
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aTextField.setStyle(WARNING_STYLE);
                    }
                    
                }    
            });
        }
    }
    
    public static void initializeTextArea(TextArea aTextArea, 
                                          String value, 
                                          String defaultValue, 
                                          String tooltip,
                                          final PeonyFaceController aPeonyFaceController)
    {
        initializeTextArea(aTextArea, value, defaultValue, tooltip, false, aPeonyFaceController);
    }
    
    public static void initializeTextArea(TextArea aTextArea, 
                                          String value, 
                                          String defaultValue, 
                                          String tooltip,
                                          boolean cssRequired,
                                          final PeonyFaceController aPeonyFaceController)
    {
        if (ZcaValidator.isNullEmpty(value)){
            aTextArea.textProperty().setValue(defaultValue);
        }else{
            aTextArea.textProperty().setValue(value);
        }
        if (ZcaValidator.isNotNullEmpty(tooltip)){
            aTextArea.setTooltip(new Tooltip(tooltip));
        }
        if (aPeonyFaceController != null){
            aTextArea.textProperty().addListener(new ChangeListener<String>() {
                @Override 
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (cssRequired){
                        aPeonyFaceController.publishPeonyDataEntrySaveDemandingStatus(true);
                        aTextArea.setStyle(WARNING_STYLE);
                    }
                }    
            });
        }
    }

    public static void displayNoImplementationDialog() {
        PeonyFaceUtils.displayWarningMessageDialog("No implementation yet. It's coming soon");
    }

}

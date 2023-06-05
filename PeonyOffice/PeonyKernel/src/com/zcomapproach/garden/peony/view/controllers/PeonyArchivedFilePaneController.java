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

import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.view.data.PeonyArchivedFileTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyDocumentTagTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.peony.view.dialogs.PeonyQuestionAnswerDialog;
import com.zcomapproach.garden.peony.view.events.PeonyArchivedFileAttentionRequest;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForSave;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForDelete;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyQuestionAnswered;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02ArchivedFile;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFile;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFileList;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenHttpClientUtils;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyArchivedFilePaneController extends PeonyEntityOwnerFaceController implements PeonyFaceEventListener{
    /**
     * targetRootPath
     */
    @FXML
    private TreeView<PeonyArchivedFileTreeItemData> fileSystemTreeView;
    /**
     * Archived File Attribute Editor
     */
    @FXML
    private VBox fileAttributeEditorVBox;
    @FXML
    private Label fileAttributesLabel;
    @FXML
    private TextField fileNameTextField;
    @FXML
    private ComboBox<String> folderComboBox;
    @FXML
    private DatePicker fileTimeDatePicker;
    @FXML
    private TextArea fileMemoTextArea;
    @FXML
    private ListView<PeonyDocumentTag> serviceTagListView;
    @FXML
    private ImageView addTagImageView;
    /**
     * Drag-and-drop zone
     */
    @FXML
    private VBox fileDragTargetVBox;
    @FXML
    private CheckBox attentionCheckBox;
    @FXML
    private Button archiveButton;
    @FXML
    private Button resetButton;
    
    /**
     * Current file which is dragged and dropped into the file attribute editors 
     * for archive. If it is NULL, it means the editor has no any file for archive
     */
    private PeonyArchivedFileTreeItemData targetPeonyArchivedFileTreeItemData = null;
    
    /**
     * Historical tagged or archived files
     */
    private final List<PeonyArchivedFile> targetPeonyArchivedFileList;
    /**
     * The location where all the historical archived files for targetEntityUuid stay.
     */
    private final Path targetArchivedFileRootPath;
    
    /**
     * targetPeonyArchivedFileList came from the caller which contains tagged or 
     * archived files stored in the system database. However, the targetRootPath
     * is a physical folder which contains many files from the system. Those files
     * might not be guarrantteed to be tagged or archived in the database before 
     * for some reason.
     * 
     * @param targetArchivedFileRootPath - the caller has to make sure this path valid.
     * @param targetPeonyArchivedFileList - the caller should make sure that all the
     * files in the targetPeonyArchivedFileList are supposed to be tagged/archived before.
     * @param targetOwner
     */
    public PeonyArchivedFilePaneController(Path targetArchivedFileRootPath, 
                                           List<PeonyArchivedFile> targetPeonyArchivedFileList, 
                                           Object targetOwner) 
    {
        super(targetOwner);
        this.targetArchivedFileRootPath = targetArchivedFileRootPath;
        this.targetPeonyArchivedFileList = targetPeonyArchivedFileList;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyDocumentTagReadyForSave){
            handlePeonyDocumentTagSaved((PeonyDocumentTagReadyForSave)event);
        }else if (event instanceof PeonyDocumentTagReadyForDelete){
            handlePeonyDocumentTagDeleted((PeonyDocumentTagReadyForDelete)event);
        }else if (event instanceof PeonyQuestionAnswered){
            handlePeonyQuestionAnswered((PeonyQuestionAnswered)event);
        }
    }

    private void handlePeonyDocumentTagSaved(final PeonyDocumentTagReadyForSave event) {
        if (Platform.isFxApplicationThread()){
            handlePeonyDocumentTagSavedHelper(event);
        }else{
            Platform.runLater(() -> {
                handlePeonyDocumentTagSavedHelper(event);
            });
        }
    }

    private void handlePeonyDocumentTagSavedHelper(PeonyDocumentTagReadyForSave event) {
        if (event.getPeonyDocumentTag() != null){
            PeonyDocumentTag aPeonyDocumentTag = event.getPeonyDocumentTag();
            serviceTagListView.getItems().remove(aPeonyDocumentTag);
            serviceTagListView.getItems().add(aPeonyDocumentTag);
            targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getPeonyDocumentTagList().remove(aPeonyDocumentTag);
            targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getPeonyDocumentTagList().add(aPeonyDocumentTag);
            
            serviceTagListView.setStyle("-fx-border-color: #0000ff;");
            this.setDataEntryChanged(true);
        }
    }

    private void handlePeonyDocumentTagDeleted(final PeonyDocumentTagReadyForDelete event) {
        if (Platform.isFxApplicationThread()){
            handlePeonyDocumentTagDeletedHelper(event);
        }else{
            Platform.runLater(() -> {
                handlePeonyDocumentTagDeletedHelper(event);
            });
        }
    }

    private void handlePeonyDocumentTagDeletedHelper(PeonyDocumentTagReadyForDelete event) {
        if (event.getPeonyDocumentTag() != null){
            PeonyDocumentTag aPeonyDocumentTag = event.getPeonyDocumentTag();
            Task<G02DocumentTag> removeBusinessContactorTask = new Task<G02DocumentTag>(){
                @Override
                protected G02DocumentTag call() throws Exception {
                    G02DocumentTag result;
                    try{
                        result = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                            .deleteEntity_XML(G02DocumentTag.class, 
                                            GardenRestParams.Business.deletePeonyDocumentTagRestParams(aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid()));
                    }catch (Exception ex){
                        updateMessage("Deletion failed. " + ex.getMessage());
                        return null;
                    }
                    return result;
                }

                @Override
                protected void succeeded() {
                    try {
                        G02DocumentTag result = get();
                        if (result == null){
                            String msg = getMessage();
                            if (ZcaValidator.isNotNullEmpty(msg)){
                                PeonyFaceUtils.displayErrorMessageDialog(msg);
                            }
                        }else{
                            serviceTagListView.getItems().remove(aPeonyDocumentTag);
                            targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getPeonyDocumentTagList().remove(aPeonyDocumentTag);
                            removePeonyDocumentTag(fileSystemTreeView.getRoot(), targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile(), aPeonyDocumentTag);
                            //logging
                            G02Log log = createNewG02LogInstance(PeonyLogName.DELETE_FILE_TAG);
                            if (log != null){
                                log.setLoggedEntityType(GardenEntityType.DOCUMENT_TAG.name());
                                log.setLoggedEntityUuid(result.getDocumentTagUuid());
                                PeonyProperties.getSingleton().log(log);
                            }
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(removeBusinessContactorTask);
        }
    }

    private void removePeonyDocumentTag(TreeItem<PeonyArchivedFileTreeItemData> root, PeonyArchivedFile aPeonyArchivedFile, PeonyDocumentTag aPeonyDocumentTag) {
        TreeItem<PeonyArchivedFileTreeItemData> fileItemData = findFileItem(root, aPeonyArchivedFile);//PeonyDocumentTagTreeItemData
        if (fileItemData != null){
            ObservableList<TreeItem<PeonyArchivedFileTreeItemData>> children = fileItemData.getChildren();
            TreeItem<PeonyArchivedFileTreeItemData> tagItemData = null;
            for (TreeItem<PeonyArchivedFileTreeItemData> child : children){
                if (child.getValue() instanceof PeonyDocumentTagTreeItemData){
                    if (aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid()
                            .equalsIgnoreCase(((PeonyDocumentTagTreeItemData)(child.getValue())).getTargetPeonyDocumentTag().getDocumentTag().getDocumentTagUuid()))
                    {
                        tagItemData = child;
                        break;
                    }
                }
            }//for-loop
            fileItemData.getChildren().remove(tagItemData);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeFileSystemTreeView();
        initializeFileDragAndDropControl();
        initializeFileAttributeEditor();
    }

    private void initializeFileSystemTreeView() {
        fileSystemTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        constructFileSystemTreeView();
    }

    /**
     * key: folder-name of PeonyArchivedFile<br>
     * value: list of PeonyArchivedFiles in the folder<br>
     */
//    private final HashMap<String, List<PeonyArchivedFile>> archivedFileStorage = new HashMap<>();
//    /**
//     * initialize archivedFileStorage data structures
//     */
//    private void initializeArchivedFileStorage(){
//        archivedFileStorage.clear();
//        List<PeonyArchivedFile> aPeonyArchivedFileList;
//        String folderName;
//        for (PeonyArchivedFile aPeonyArchivedFile : targetPeonyArchivedFileList){
//            folderName = aPeonyArchivedFile.getArchivedFile().getFileFaceFolder();
//            aPeonyArchivedFileList = archivedFileStorage.get(folderName);
//            if (aPeonyArchivedFileList == null){
//                aPeonyArchivedFileList = new ArrayList<>();
//                archivedFileStorage.put(folderName, aPeonyArchivedFileList);
//            }
//            aPeonyArchivedFileList.add(aPeonyArchivedFile);
//        }//for-loop
//    }
    
    private PeonyArchivedFile findCorrespondingPeonyArchivedFile(Path filePath){
        if (Files.isRegularFile(filePath)){
            return findCorrespondingPeonyArchivedFile(FilenameUtils.getBaseName(filePath.toAbsolutePath().toString()));
        }
        return null;
    }
    
    private PeonyArchivedFile findCorrespondingPeonyArchivedFile(String fileUuid){
        for (PeonyArchivedFile aPeonyArchivedFile : targetPeonyArchivedFileList){
            if (aPeonyArchivedFile.getArchivedFile().getFileUuid().equalsIgnoreCase(fileUuid)){
                return aPeonyArchivedFile;
            }
        }//for-loop
        return null;
    }

    /**
     * Construct archived-file-tree-items based on the physical system files in the root path, i.e. targetRootPath. 
     * Notice that every system file got have a corresponding instance in the ArchivedFileStorage data structure. 
     * So, it may be some synchronization between database entity records and the file-system's files in the future.
     * <p>
     * Go though all the system files in the archived root folder in which all the so-called "folder"s 
     * are virtual, which is stored in the database but not in the file system. And it sets up the data 
     * structure to help build the archived file tree. 
     * <p>
     * Notice that in the system folder, there is no any sub-folders but only physical archived files. 
     * The folders are virtual, which is stored in the corresponding entity, e.g. G**ArchivedFile:file_face_folder. 
     * Similiarly, all the readable file names are virtual, which is stored in the corresponding entity, 
     * <p>
     * In Garden's archived-file-system, virtually it may have at most one level folder!
     * @deprecated - this method depends on the physical files in the folder.
     */
    private void constructArchivedFileTreeItemsByTargetArchivedFileRootPath(TreeItem<PeonyArchivedFileTreeItemData> treeRootItem) {
        //construct archived files' TreeItems
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(targetArchivedFileRootPath)) {
            TreeItem<PeonyArchivedFileTreeItemData> newItem;    //for folder or file
            TreeItem<PeonyArchivedFileTreeItemData> subNewItem; //for file
            PeonyArchivedFileTreeItemData aPeonyArchivedFileTreeItemData;
            PeonyArchivedFile aPeonyArchivedFile;
            for (Path path : directoryStream) {
                aPeonyArchivedFile = findCorrespondingPeonyArchivedFile(path);
                //if cannot find its corresponding entity, it does not show it at all but still stay in the folder. todo zzj: how to deal with it?
                if (aPeonyArchivedFile != null) {
                    if (ZcaValidator.isNullEmpty(aPeonyArchivedFile.getArchivedFile().getFileFaceFolder())){
                        newItem = new TreeItem<>();
                        newItem.setGraphic(PeonyGraphic.getImageView("document_valid.png"));
                        aPeonyArchivedFileTreeItemData = new PeonyArchivedFileTreeItemData(null, aPeonyArchivedFile, null);
                        newItem.setValue(aPeonyArchivedFileTreeItemData);
                        newItem.setExpanded(true);
                    }else{
                        //folder
                        newItem = constructFolderItem(treeRootItem, aPeonyArchivedFile);
                        //file
                        subNewItem = new TreeItem<>();
                        subNewItem.setGraphic(PeonyGraphic.getImageView("document_valid.png"));
                        aPeonyArchivedFileTreeItemData = new PeonyArchivedFileTreeItemData(null, aPeonyArchivedFile, null);
                        subNewItem.setValue(aPeonyArchivedFileTreeItemData);
                        subNewItem.setExpanded(true);
                        //hook file-item onto folder-item
                        newItem.getChildren().add(subNewItem);
                    }
                    treeRootItem.getChildren().remove(newItem); //possibly it was added if it is a folder
                    treeRootItem.getChildren().add(newItem);
                }//if
            }//for-loop
            treeRootItem.setExpanded(true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void constructArchivedFileTreeItems(TreeItem<PeonyArchivedFileTreeItemData> treeRootItem) {
        //construct archived files' TreeItems according to List<PeonyArchivedFile> targetPeonyArchivedFileList
        TreeItem<PeonyArchivedFileTreeItemData> newItem;    //for folder or file
        TreeItem<PeonyArchivedFileTreeItemData> subNewItem; //for file or tags
        for (PeonyArchivedFile aPeonyArchivedFile : targetPeonyArchivedFileList){
            if (ZcaValidator.isNullEmpty(aPeonyArchivedFile.getArchivedFile().getFileFaceFolder())){
                newItem = constructFileItem(aPeonyArchivedFile);
            }else{
                //folder
                newItem = constructFolderItem(treeRootItem, aPeonyArchivedFile);
                //file
                subNewItem = constructFileItem(aPeonyArchivedFile);
                //hook file-item onto folder-item
                newItem.getChildren().add(subNewItem);
            }
            treeRootItem.getChildren().remove(newItem); //possibly it was added if it is a folder
            treeRootItem.getChildren().add(newItem);
        }//for-loop
        treeRootItem.setExpanded(true);
    }
    private TreeItem<PeonyArchivedFileTreeItemData> constructFileItem(PeonyArchivedFile aPeonyArchivedFile) {
        TreeItem fileItem = new TreeItem<>();
        fileItem.setGraphic(PeonyGraphic.getImageView("document_valid.png"));
        PeonyArchivedFileTreeItemData aPeonyArchivedFileTreeItemData = new PeonyArchivedFileTreeItemData(null, aPeonyArchivedFile, null);
        fileItem.setValue(aPeonyArchivedFileTreeItemData);
        fileItem.setExpanded(true);
        List<PeonyDocumentTag> aPeonyDocumentTagList = aPeonyArchivedFile.getPeonyDocumentTagList();
        if (aPeonyDocumentTagList != null){
            PeonyDocumentTagTreeItemData aPeonyDocumentTagTreeItemData;
            TreeItem tagItem;
            for (PeonyDocumentTag aPeonyDocumentTag : aPeonyDocumentTagList){
                tagItem = new TreeItem<>();
                tagItem.setGraphic(PeonyGraphic.getImageView("pin_circle.png"));
                aPeonyDocumentTagTreeItemData = new PeonyDocumentTagTreeItemData(aPeonyDocumentTag, aPeonyArchivedFile, null);
                tagItem.setValue(aPeonyDocumentTagTreeItemData);
                tagItem.setExpanded(true);
                fileItem.getChildren().add(tagItem);
            }
        }
        return fileItem;
    }
    
    private TreeItem<PeonyArchivedFileTreeItemData> constructFolderItem(TreeItem<PeonyArchivedFileTreeItemData> treeRootItem, PeonyArchivedFile aPeonyArchivedFile) {
        String folderName = aPeonyArchivedFile.getArchivedFile().getFileFaceFolder();
        TreeItem<PeonyArchivedFileTreeItemData> folderItem = findFolderItem(treeRootItem, folderName);
        if (folderItem != null){    //find it in the existing items
            return folderItem;
        }
        //create a new folder-item if cannot find it in the existing items
        return createFolderItem(folderName);
    }

    private TreeItem<PeonyArchivedFileTreeItemData> findFolderItem(TreeItem<PeonyArchivedFileTreeItemData> treeRootItem, String folderName){
        TreeItem<PeonyArchivedFileTreeItemData> folderItem = null;
        ObservableList<TreeItem<PeonyArchivedFileTreeItemData>> items = treeRootItem.getChildren();
        if (items != null){
            for (TreeItem<PeonyArchivedFileTreeItemData> item : items){
                if (item.getValue().getPeonyArchivedFile() == null){    //folder
                    if (folderName.equalsIgnoreCase(item.getValue().getTreeItemRepresentativeText())){
                        folderItem = item;
                        break;
                    }
                }
            }//for
        }
        return folderItem;
    }

    private TreeItem<PeonyArchivedFileTreeItemData> findFileItem(TreeItem<PeonyArchivedFileTreeItemData> treeFolderItem, PeonyArchivedFile aPeonyArchivedFile){
        TreeItem<PeonyArchivedFileTreeItemData> fileItem = null;
        ObservableList<TreeItem<PeonyArchivedFileTreeItemData>> items = treeFolderItem.getChildren();
        if (items != null){
            for (TreeItem<PeonyArchivedFileTreeItemData> item : items){
                if (item.getValue().getPeonyArchivedFile() == null){    //sub-folder or tags
                    if ((item.getChildren() != null) && (!item.getChildren().isEmpty())){   //folder
                        fileItem = findFileItem(item, aPeonyArchivedFile);
                        if (fileItem != null){
                            break;
                        }
                    }
                }else{
                    if (aPeonyArchivedFile.getArchivedFile().getFileUuid().equalsIgnoreCase(item.getValue().getPeonyArchivedFile().getArchivedFile().getFileUuid())){
                        fileItem = item;
                        break;
                    }
                }
            }//for
        }
        return fileItem;
    }
    
    private TreeItem<PeonyArchivedFileTreeItemData> createFolderItem(String folderName){
        TreeItem<PeonyArchivedFileTreeItemData> folderItem = new TreeItem<>();
        folderItem.setGraphic(PeonyGraphic.getImageView("folder.png"));
        PeonyArchivedFileTreeItemData aPeonyArchivedFileTreeItemData = new PeonyArchivedFileTreeItemData(folderName, null, null);
        folderItem.setValue(aPeonyArchivedFileTreeItemData);
        folderItem.setExpanded(true);
        return folderItem;
    
    }
    
    private void constructFileSystemTreeView() {
        Task<TreeItem<PeonyArchivedFileTreeItemData>> constructFileSystemTreeViewTask = new Task<TreeItem<PeonyArchivedFileTreeItemData>>(){
            @Override
            protected TreeItem<PeonyArchivedFileTreeItemData> call() throws Exception {
                // create root-data
                PeonyArchivedFileTreeItemData rootTreeItemData = new PeonyArchivedFileTreeItemData(null, null, null);
                //tree root-item
                TreeItem<PeonyArchivedFileTreeItemData> treeRootItem = new TreeItem<>();
                treeRootItem.setValue(rootTreeItemData);
                treeRootItem.setExpanded(true);
                treeRootItem.setGraphic(PeonyGraphic.getImageView("folders.png"));
                constructArchivedFileTreeItems(treeRootItem);
                return treeRootItem;
            }

            @Override
            protected void succeeded() {
                try {
                    fileSystemTreeView.setRoot(get(5, TimeUnit.SECONDS));
                    ContextMenu aContextMenu = new ContextMenu();
                    
                    MenuItem aMenuItem = new MenuItem("View Archived File");
                    aMenuItem.setOnAction((ActionEvent e) -> {
                        viewFileFromTreeItemData(fileSystemTreeView.getSelectionModel().getSelectedItem());
                    });
                    aContextMenu.getItems().add(aMenuItem);
                    
                    aMenuItem = new MenuItem("Update File Attributes");
                    aMenuItem.setOnAction((ActionEvent e) -> {
                        displayPeonyArchivedFileTreeItemData(fileSystemTreeView.getSelectionModel().getSelectedItem());
                    });
                    aContextMenu.getItems().add(aMenuItem);
                    
                    aMenuItem = new MenuItem("Delete Archive File");
                    aMenuItem.setOnAction((ActionEvent e) -> {
                        deleteArchivedFile(fileSystemTreeView.getSelectionModel().getSelectedItem());
                    });
                    aContextMenu.getItems().add(aMenuItem);
                    
                    aMenuItem = new MenuItem("Create New Folder");
                    aMenuItem.setOnAction((ActionEvent e) -> {
                        createNewArchivedFolder();
                    });
                    aContextMenu.getItems().add(aMenuItem);
                    
                    aMenuItem = new MenuItem("Delete Folder");
                    aMenuItem.setOnAction((ActionEvent e) -> {
                        deleteArchivedFolder(fileSystemTreeView.getSelectionModel().getSelectedItem());
                    });
                    aContextMenu.getItems().add(aMenuItem);
                    
                    /**
                     * folderComboBox and fileSystemTreeView share this context menu
                     */
                    folderComboBox.setContextMenu(aContextMenu);
                    fileSystemTreeView.setContextMenu(aContextMenu);
                    
                    fileSystemTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                        if (event.getClickCount() == 2){
                            //viewFileFromTreeItemData(fileSystemTreeView.getSelectionModel().getSelectedItem());
                            displayPeonyArchivedFileTreeItemData(fileSystemTreeView.getSelectionModel().getSelectedItem());
                        }
                    });
                    fileSystemTreeView.refresh();
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    if (ex instanceof TimeoutException){
                        cancel();
                    }
                    Exceptions.printStackTrace(ex);
                }
            }

            private void viewFileFromTreeItemData(TreeItem<PeonyArchivedFileTreeItemData> selectedItem) {
                if ((selectedItem == null) || (selectedItem.getValue() == null) 
                        || (selectedItem.getValue().getPeonyArchivedFile() == null))
                {
                    PeonyFaceUtils.displayWarningMessageDialog("Please select a file to view.");
                    return;
                }
                try {
                    G02ArchivedFile aG02ArchivedFile = selectedItem.getValue()
                                                        .getPeonyArchivedFile().getArchivedFile();
                    PeonyFaceUtils.openFile(new File (targetArchivedFileRootPath.resolve(
                            Paths.get(aG02ArchivedFile.getFileUuid() + "." + aG02ArchivedFile.getFileExtension())).toAbsolutePath().toString()));
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Your selected file does not exist.");
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(constructFileSystemTreeViewTask);
    }
    

    private void displayPeonyArchivedFileTreeItemData(TreeItem<PeonyArchivedFileTreeItemData> selectedItem) {
        if ((selectedItem == null) || (selectedItem.getValue() == null)){
            PeonyFaceUtils.displayWarningMessageDialog("Please select a file.");
            return;
        }
        if (selectedItem.getValue() instanceof PeonyDocumentTagTreeItemData){
            targetPeonyArchivedFileTreeItemData = (PeonyArchivedFileTreeItemData)(selectedItem.getParent().getValue());
            displayServiceTagEditorHelper(((PeonyDocumentTagTreeItemData)selectedItem.getValue()).getTargetPeonyDocumentTag(), 
                    this, "Tag for " + targetPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
        }else if (selectedItem.getValue() instanceof PeonyArchivedFileTreeItemData){
            loadFileAttributeEditor((PeonyArchivedFileTreeItemData)selectedItem.getValue());
        }
    }

    private void initializeFileAttributeEditor(){
        
        /**
         * Disable the editor in the beginning and only enabled after users Dnd a file
         */
        fileAttributeEditorVBox.setDisable(true);
        
        PeonyFaceUtils.initializeTextField(fileNameTextField, null, null, 
                "You may assign a new file name that is more user-friendly than its original file name", this);
        
        PeonyFaceUtils.initializeDatePicker(fileTimeDatePicker, null, null, 
                "Its default date is the file's last modified timestamp.", this);
        
        //folderComboBox
        folderComboBox.getItems().clear();
        folderComboBox.getItems().add("");  //default "" empty string
        List<String> folderNames = new ArrayList<>();
        String folderName;
        for (PeonyArchivedFile aPeonyArchivedFile : targetPeonyArchivedFileList){
            if (ZcaValidator.isNotNullEmpty(aPeonyArchivedFile.getArchivedFile().getFileFaceFolder())){
                folderName = aPeonyArchivedFile.getArchivedFile().getFileFaceFolder();
                folderNames.remove(folderName);
                folderNames.add(folderName);
            }
        }
        PeonyFaceUtils.initializeComboBox(folderComboBox, folderNames, null, "", "For a new folder, right-click on the file system viewer to create one.", this);
        
        PeonyFaceUtils.initializeTextArea(fileMemoTextArea, null, null, "Description (at most 450 characters) on this file.", this);
        
        addTagImageView.setImage(PeonyGraphic.getJavaFxImage("tag_add32.png"));
        addTagImageView.setCursor(Cursor.HAND);
        Tooltip.install(addTagImageView, new Tooltip("Click to add new service tag..."));
        addTagImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getClickCount() == 2){
                displayServiceTagEditorHelper(new PeonyDocumentTag(), this, "Tag for " + targetPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
            }
        });
        serviceTagListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        serviceTagListView.setTooltip(new Tooltip("Right-click or select-double-click to view its memo and add/edit/delete service tags..."));
        ContextMenu aContextMenu = new ContextMenu();
        MenuItem aMenuItem = new MenuItem("Add Service Tag");
        aMenuItem.setOnAction((ActionEvent e) -> {
            displayServiceTagEditorHelper(new PeonyDocumentTag(), this, "Tag for " + targetPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
        });
        aContextMenu.getItems().add(aMenuItem);
        aMenuItem = new MenuItem("Edit Service Tag");
        aMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                displayServiceTagEditorHelper(serviceTagListView.getSelectionModel().getSelectedItem(), 
                        PeonyArchivedFilePaneController.this, "Tag for " + targetPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
            }
        });
        aContextMenu.getItems().add(aMenuItem);
        aMenuItem = new MenuItem("Delete Service Tag");
        aMenuItem.setOnAction((ActionEvent e) -> {
            displayServiceTagEditorHelper(serviceTagListView.getSelectionModel().getSelectedItem(), 
                    PeonyArchivedFilePaneController.this, "Tag for " + targetPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
        });
        aContextMenu.getItems().add(aMenuItem);
        
        serviceTagListView.setContextMenu(aContextMenu);
        
        serviceTagListView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getClickCount() == 2){
                PeonyDocumentTag aPeonyDocumentTag = serviceTagListView.getSelectionModel().getSelectedItem();
                if (aPeonyDocumentTag == null){
                    aPeonyDocumentTag = new PeonyDocumentTag();
                }
                displayServiceTagEditorHelper(aPeonyDocumentTag, this, "Tag for " + targetPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
            }
        });
        
        archiveButton.setOnAction((ActionEvent e) -> {
            if (targetPeonyArchivedFileTreeItemData == null){
                return; //no any target 
            }
            runArchiveFileTask();
        });
        
        resetButton.setOnAction((ActionEvent e) -> {
            resetFileAttributeEditorHelper();
            this.setDataEntryChanged(false);
        });
    }
    
    private void initializeFileDragAndDropControl() {
        
        Tooltip.install(attentionCheckBox, new Tooltip("Publish the memo for others' attention."));
        
        fileDragTargetVBox.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != fileDragTargetVBox
                    && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        fileDragTargetVBox.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = initializeTargetFileTreeItemDataForDnD(db.getFiles());
                if (success){
                    loadFileAttributeEditor(targetPeonyArchivedFileTreeItemData);
                }else{
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot archive this dragged file");
                }
            }
            /* let the source know whether the string was successfully transferred and used */
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * set PeonyArchivedFileTreeItemData according to the physical file
     * @param files
     * @return 
     */
    private boolean initializeTargetFileTreeItemDataForDnD(List<File> files) {
        if ((files == null) || (files.isEmpty())){
            return false;
        }
        if (files.size() > 1){
            PeonyFaceUtils.displayWarningMessageDialog("Only one file can be dragged and dropped here. The first file will be processed.");
        }
        
        boolean result = false;
        
        targetPeonyArchivedFileTreeItemData = new PeonyArchivedFileTreeItemData(null, new PeonyArchivedFile(), PeonyTreeItemData.Status.CREATE);
        PeonyArchivedFile aPeonyArchivedFile = targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile();
        G02ArchivedFile aG02ArchivedFile = aPeonyArchivedFile.getArchivedFile();
        for (File file : files){
            if (file.isFile()){ //it has to be a file
                if (ZcaValidator.isNullEmpty(aG02ArchivedFile.getFileUuid())){
                    aG02ArchivedFile.setFileUuid(ZcaUtils.generateUUIDString());
                }
                //use original file name as the face-file-name
                aG02ArchivedFile.setFileFaceName(FilenameUtils.getName(file.getAbsolutePath().toLowerCase()));
                aG02ArchivedFile.setFileExtension(FilenameUtils.getExtension(file.getAbsolutePath().toLowerCase()));
                aG02ArchivedFile.setFileFaceFolder(null);   //this file's root folder
                
                aG02ArchivedFile.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                try {
                    aPeonyArchivedFile.getArchivedFile().setFileTimestamp(ZcaNio.getFileLastModifiedTimestamp(file));
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                    aPeonyArchivedFile.getArchivedFile().setFileTimestamp(new Date());
                }
                aG02ArchivedFile.setEntityUuid(getTargetEntityUuid());
                aG02ArchivedFile.setEntityType(getTargetEntityType().name());
                aG02ArchivedFile.setEntityStatus(null);
                targetPeonyArchivedFileTreeItemData.setOriginalFileObjectForArchive(file);
                result = true;
                break;
            }else{
                return false;
            }
        }//for-loop
        return result;
    }
    
//    /**
//     * targetPeonyArchivedFileList came from the caller which contains such tagged 
//     * or archived files stored in the database. However, the filePath is a physical 
//     * file which came from the folder containing files from the system. Those files
//     * might be not tagged or archived in the database before for some reason. This 
//     * method is used to find PeonyArchivedFile instance from targetPeonyArchivedFileList 
//     * for the file (i.e. filePath) that was tagged or archived. For not tagged or 
//     * archived, NULL returns. All the files in the targetPeonyArchivedFileList 
//     * are supposed to be tagged/archived before.
//     * 
//     * @param filePath
//     * @return 
//     */
//    private PeonyArchivedFile findPeonyArchivedFileFromTarget(Path filePath){
//        for (PeonyArchivedFile aPeonyArchivedFile : targetPeonyArchivedFileList){
//            if (aPeonyArchivedFile.getArchivedFile().getFileUuid().equalsIgnoreCase(FilenameUtils.getBaseName(filePath.toAbsolutePath().toString()))){
//                return aPeonyArchivedFile;
//            }
//        }
//        return null;
//    }
//    
//    private String findPeonyArchivedFileNameFromTarget(String fileName){
//        for (PeonyArchivedFile aPeonyArchivedFile : targetPeonyArchivedFileList){
//            if (aPeonyArchivedFile.getArchivedFile().getFileCustomName().equalsIgnoreCase(fileName)){
//                return fileName;
//            }
//        }
//        return null;
//    }

    private void resetFileAttributeEditorBorderStyleHelper() {
        fileNameTextField.setStyle(null);
        folderComboBox.setStyle(null);
        fileTimeDatePicker.setStyle(null);
        fileMemoTextArea.setStyle(null);
        serviceTagListView.setStyle(null);
        
        this.setDataEntryChanged(false);
    }
    
    private void resetFileAttributeEditorHelper() {
        fileAttributeEditorVBox.setDisable(true);
        
        fileAttributesLabel.setText("Archive File Attributes:");
        folderComboBox.setValue(null);
        fileNameTextField.setText(null);
        fileTimeDatePicker.setValue(null);
        fileMemoTextArea.setText(null);
        
        resetFileAttributeEditorBorderStyleHelper();
        
        serviceTagListView.getItems().clear();
        targetPeonyArchivedFileTreeItemData = null;
    }

    /**
     * Load all the information from aPeonyArchivedFileTreeItemData into the file attribute editor
     * @param aPeonyArchivedFileTreeItemData 
     */
    private void loadFileAttributeEditor(final PeonyArchivedFileTreeItemData aPeonyArchivedFileTreeItemData) {
        if ((aPeonyArchivedFileTreeItemData == null) || (aPeonyArchivedFileTreeItemData.getPeonyArchivedFile() == null)){
            PeonyFaceUtils.displayWarningMessageDialog("Please select a file.");
            return;
        }
        if (Platform.isFxApplicationThread()){
            loadFileAttributeEditorHelper(aPeonyArchivedFileTreeItemData);
        }else{
            Platform.runLater(() -> {
                loadFileAttributeEditorHelper(aPeonyArchivedFileTreeItemData);
            });
        }
    }

    private void loadFileAttributeEditorHelper(PeonyArchivedFileTreeItemData aPeonyArchivedFileTreeItemData) {
        fileAttributeEditorVBox.setDisable(false);
        
        fileAttributesLabel.setText("Archive: " + aPeonyArchivedFileTreeItemData.getTreeItemRepresentativeText());
        
        G02ArchivedFile aG02ArchivedFile = aPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getArchivedFile();
        fileNameTextField.setText(aG02ArchivedFile.getFileFaceName());
        
        if (findCorrespondingPeonyArchivedFile(aG02ArchivedFile.getFileUuid()) != null){
            if (ZcaValidator.isNotNullEmpty(aG02ArchivedFile.getFileFaceFolder())){
                folderComboBox.setValue(aG02ArchivedFile.getFileFaceFolder());
            }
        }
        fileTimeDatePicker.setValue(ZcaCalendar.convertToLocalDate(aG02ArchivedFile.getFileTimestamp()));
        fileMemoTextArea.setText(aG02ArchivedFile.getFileMemo());
        
        List<PeonyDocumentTag> aPeonyDocumentTagList = aPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getPeonyDocumentTagList();
        serviceTagListView.getItems().clear();
        aPeonyDocumentTagList.forEach((aPeonyDocumentTag) -> {
            serviceTagListView.getItems().add(aPeonyDocumentTag);
        });
        
        targetPeonyArchivedFileTreeItemData = aPeonyArchivedFileTreeItemData;
        
        resetFileAttributeEditorBorderStyleHelper();
        this.setDataEntryChanged(false);
    }

    /**
     * Archive the target file from the file attribute editor into the persistent storage
     * 
     */
    private void runArchiveFileTask() {
        Task<PeonyArchivedFile> task = new Task<PeonyArchivedFile>(){
            @Override
            protected PeonyArchivedFile call() throws Exception {
                PeonyArchivedFile aPeonyArchivedFile = loadTargetPeonyArchivedFileTreeItemData();
                if (aPeonyArchivedFile == null){
                    //in this case, there is something wrong in data loading
                    return null;
                }
                //PeonyArchivedFile
                try{
                    aPeonyArchivedFile.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
                    //archive it into the file system if it does not exist in the system yet
                    Path dstFileFullPath = targetArchivedFileRootPath
                            .resolve(aPeonyArchivedFile.getArchivedFile().getFileUuid()+"."+aPeonyArchivedFile.getArchivedFile().getFileExtension()).toAbsolutePath();
                    if ((targetPeonyArchivedFileTreeItemData.getOriginalFileObjectForArchive() != null)){
                        Path srcFileFullPath = targetPeonyArchivedFileTreeItemData.getOriginalFileObjectForArchive().toPath().toAbsolutePath();
                        if (Files.exists(srcFileFullPath) && (!Files.exists(dstFileFullPath))) {
                            ZcaNio.copyFile(srcFileFullPath.toString(), dstFileFullPath.toString());
                            //upload it to the server as backup...
                            GardenHttpClientUtils.uploadFileToRoseWeb(PeonyProperties.getSingleton().getRoseUploadPostUrl(),
                                                                    PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getAccountUuid(),
                                                                    dstFileFullPath.toString());
                        }
                    }
                    //Save it into the database
                    aPeonyArchivedFile = Lookup.getDefault().lookup(PeonyBusinessService.class)
                        .getPeonyBusinessRestClient().storeEntity_XML(PeonyArchivedFile.class, 
                                                                      GardenRestParams.Business.storePeonyArchivedFileRestParams(), 
                                                                      aPeonyArchivedFile);
                    if (aPeonyArchivedFile == null){
                        updateMessage("Garden system did not execute this operation. Please contact techical department. ");
                        return null;
                    }else{
                        updateMessage("Archived and stored the file into Garden storage system.");
                    }
                    //publish memo for attention if necessary
                    if (attentionCheckBox.isSelected()){
                        PeonyMemo aPeonyMemo = convertToPeonyMemo(aPeonyArchivedFile);
                        aPeonyMemo = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(PeonyMemo.class, GardenRestParams.Business.storePeonyMemoRestParams(), aPeonyMemo);
                        broadcastPeonyFaceEventHappened(new PeonyArchivedFileAttentionRequest(aPeonyMemo));
                        
                        attentionCheckBox.setSelected(false);
                    }
                }catch (Exception ex){
                    updateMessage("Failed to archived the file into Garden storage system.");
                    return null;
                }
                return aPeonyArchivedFile;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyArchivedFile result = get();
                    if (result != null){
                        //log it before GUI change
                        G02Log log = null;
                        if (PeonyArchivedFileTreeItemData.Status.CREATE.equals(targetPeonyArchivedFileTreeItemData.getTreeItemDataStatus())){
                            log = createNewG02LogInstance(PeonyLogName.CREATE_ARCHIVED_FILE);
                        }else if (isDataEntryChanged()){
                            log = createNewG02LogInstance(PeonyLogName.UPDATE_ARCHIVED_FILE);
                        }
                        if (log != null){
                            log.setLoggedEntityType(GardenEntityType.ARCHIVE_DOCUMENTS.name());
                            log.setLoggedEntityUuid(result.getArchivedFile().getFileUuid());
                            PeonyProperties.getSingleton().log(log);
                        }
                        if ((targetPeonyArchivedFileTreeItemData.getOriginalFileObjectForArchive() != null)){
                            targetPeonyArchivedFileList.remove(result);
                            targetPeonyArchivedFileList.add(result);
                        }
                        //refresh archived file system
                        constructFileSystemTreeView();
                        //reset the editor
                        resetFileAttributeEditorHelper();
                        
                        setDataEntryChanged(false);
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            private PeonyArchivedFile loadTargetPeonyArchivedFileTreeItemData() {
                if ((targetPeonyArchivedFileTreeItemData == null) 
                        || (targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile() == null))
                {
                    return null;
                }
                G02ArchivedFile aG02ArchivedFile = targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getArchivedFile();
                aG02ArchivedFile.setFilePath(targetArchivedFileRootPath.toString().toLowerCase());
                aG02ArchivedFile.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                //fileNameTextField
                if (targetPeonyArchivedFileTreeItemData.getOriginalFileObjectForArchive() != null){
                    if (ZcaValidator.isNullEmpty(aG02ArchivedFile.getFileUuid())){
                        aG02ArchivedFile.setFileUuid(ZcaUtils.generateUUIDString());
                    }
                    String originalFilePath = targetPeonyArchivedFileTreeItemData.getOriginalFileObjectForArchive().getAbsolutePath().toLowerCase();
                    if (ZcaValidator.isNullEmpty(fileNameTextField.getText())){
                        aG02ArchivedFile.setFileFaceName(FilenameUtils.getName(originalFilePath));
                    }else{
                        aG02ArchivedFile.setFileFaceName(fileNameTextField.getText());
                    }
                    aG02ArchivedFile.setFileExtension(FilenameUtils.getExtension(originalFilePath));
                }else{
                    //in this case, this file is already archived in the file system
                    if (ZcaValidator.isNotNullEmpty(fileNameTextField.getText())){
                        aG02ArchivedFile.setFileFaceName(fileNameTextField.getText());
                    }
                }
                if (aG02ArchivedFile.getFileFaceName().length() > 45){
                    updateMessage("The file name is too long, at most 45 characters");
                    return null;
                }
                //folderComboBox
                aG02ArchivedFile.setFileFaceFolder(folderComboBox.getSelectionModel().getSelectedItem());
                //fileTimeDatePicker
                if (fileTimeDatePicker.getValue() == null){
                    aG02ArchivedFile.setFileTimestamp(new Date());
                }else{
                    aG02ArchivedFile.setFileTimestamp(ZcaCalendar.convertToDate(fileTimeDatePicker.getValue()));
                }
                
                //make sure the file name (UUID which is always lowercase) and extension is lowercase
                if (ZcaValidator.isNotNullEmpty(aG02ArchivedFile.getFileExtension())){
                    aG02ArchivedFile.setFileExtension(aG02ArchivedFile.getFileExtension().toLowerCase());
                }
                
                //serviceTagListView
                ObservableList<PeonyDocumentTag> itemDataList = serviceTagListView.getItems();
//                if (itemDataList.isEmpty()){
//                    serviceTagListView.setStyle("-fx-border-color: #ff0000;");
//                    updateMessage("Please add tags to this file so as to help search engine find out this archived digital file.");
//                    return null;
//                }
                List<PeonyDocumentTag> aPeonyDocumentTagList = targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile().getPeonyDocumentTagList();
                aPeonyDocumentTagList.clear();
                for (PeonyDocumentTag aPeonyDocumentTag : itemDataList){
                    if (ZcaValidator.isNullEmpty(aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid())){
                        aPeonyDocumentTag.getDocumentTag().setDocumentTagUuid(ZcaUtils.generateUUIDString());
                    }
                    aPeonyDocumentTag.getDocumentTag().setFileUuid(aG02ArchivedFile.getFileUuid());
                    aPeonyDocumentTagList.add(aPeonyDocumentTag);
                }
                //fileMemoTextArea
                String memo = fileMemoTextArea.getText();
                if (ZcaValidator.isNotNullEmpty(memo) && (memo.length() > 450)){
                    updateMessage("Memo-field can only contains at most 450 characters.");
                    return null;
                }else{
                    aG02ArchivedFile.setFileMemo(memo);
                }
                return targetPeonyArchivedFileTreeItemData.getPeonyArchivedFile();
            }

            private PeonyMemo convertToPeonyMemo(PeonyArchivedFile aPeonyArchivedFile) {
                G02ArchivedFile aG02ArchivedFile = aPeonyArchivedFile.getArchivedFile();
                PeonyMemo aPeonyMemo = new PeonyMemo();
                G02Memo memo = new G02Memo();
                memo.setEntityComboUuid("");
                memo.setEntityStatus("");
                memo.setEntityStatus(aG02ArchivedFile.getEntityStatus());
                memo.setEntityType(aG02ArchivedFile.getEntityType());
                memo.setEntityUuid(aG02ArchivedFile.getEntityUuid());
                memo.setInitialMemoUuid(null);
                memo.setMemoUuid(ZcaUtils.generateUUIDString());
                memo.setMemo("[File Memo: " + aG02ArchivedFile.getFileFaceFolder() + "/" + aG02ArchivedFile.getFileFaceName() + "] " 
                        + aPeonyArchivedFile.getOperator().getPeonyUserFullName() + ": " + aG02ArchivedFile.getFileMemo());
                memo.setOperatorAccountUuid(aPeonyArchivedFile.getOperator().getAccount().getAccountUuid());
                memo.setTimestamp(new Date());
                aPeonyMemo.setMemo(memo);
                aPeonyMemo.setOperator(aPeonyArchivedFile.getOperator());
                return aPeonyMemo;
            }
        };
        getCachedThreadPoolExecutorService().submit(task);
    }

    private void handlePeonyQuestionAnswered(PeonyQuestionAnswered peonyQuestionAnswered) {
        final String folderName = peonyQuestionAnswered.getAnswer();
        if (PeonyDataUtils.isSystemFolderNameFormat(folderName)){
            if (folderName.length() > 45) {
                PeonyFaceUtils.displayErrorMessageDialog("The folder name's length is longer than 45 characters. Please do it again.");
                return;
            }
            if (findFolderItem(fileSystemTreeView.getRoot(), folderName) != null){
                PeonyFaceUtils.displayErrorMessageDialog("The folder name has been used. Please do it again.");
                return;
            }
            if (Platform.isFxApplicationThread()){
                createNewArchivedFolderHelper(folderName);
            }else{
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        createNewArchivedFolderHelper(folderName);
                    }
                });
            }
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("The folder name is illegal. Please do it again.");
        }
    }
    
    private void createNewArchivedFolderHelper(String folderName){
        TreeItem<PeonyArchivedFileTreeItemData> folderItem = createFolderItem(folderName);
        fileSystemTreeView.getRoot().getChildren().remove(folderItem);
        fileSystemTreeView.getRoot().getChildren().add(folderItem);
        folderComboBox.getItems().remove(folderName);
        folderComboBox.getItems().add(folderName);
        folderComboBox.setValue(folderName);
    }

    private void createNewArchivedFolder() {
        if (SwingUtilities.isEventDispatchThread()){
            createNewArchivedFolderHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    createNewArchivedFolderHelper();
                }
            });
        }
    }
    private void createNewArchivedFolderHelper(){
        PeonyQuestionAnswerDialog aPeonyQuestionAnswerDialog = new PeonyQuestionAnswerDialog(null, true);
        aPeonyQuestionAnswerDialog.launchMemoDataEntryDialog("Create New Archive Folder", 
                "Create New Archive Folder", "Please give a new archive folder name (at most 45 characters):", null);
        aPeonyQuestionAnswerDialog.addPeonyFaceEventListener(this);
    }

    private void deleteArchivedFile(TreeItem<PeonyArchivedFileTreeItemData> fileItem) {
        if ((!(fileItem.getValue() instanceof PeonyDocumentTagTreeItemData)) 
                && (fileItem.getValue().getPeonyArchivedFile() != null)
                && (ZcaValidator.isNotNullEmpty(fileItem.getValue().getPeonyArchivedFile().getArchivedFile().getFileUuid())))
        {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this archive file's record?") == JOptionPane.YES_OPTION){
                Task<TreeItem<PeonyArchivedFileTreeItemData>> deleteFileTask = new Task<TreeItem<PeonyArchivedFileTreeItemData>>(){
                    @Override
                    protected TreeItem<PeonyArchivedFileTreeItemData> call() throws Exception {
                        String fileUuid = fileItem.getValue().getPeonyArchivedFile().getArchivedFile().getFileUuid();
                        if (ZcaValidator.isNullEmpty(fileUuid)){
                            updateMessage("Cannot find the file record.");
                            return null;
                        }
                        try{
                            G02ArchivedFile file = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .deleteEntity_XML(G02ArchivedFile.class, 
                                                GardenRestParams.Business.deleteArchivedFileRestParams(fileUuid));
                            if (file == null){
                                updateMessage("Deletion failed on the server-side.");
                                return null;
                            }
                        }catch (Exception ex){
                            updateMessage("Deletion failed. " + ex.getMessage());
                            return null;
                        }
                        return fileItem;
                    }

                    @Override
                    protected void succeeded() {
                        try {
                            TreeItem<PeonyArchivedFileTreeItemData> fileItem = get();
                            if (fileItem == null){
                                PeonyFaceUtils.displayErrorMessageDialog("Failed. " + getMessage());
                            }else{
                                TreeItem<PeonyArchivedFileTreeItemData> parent = fileItem.getParent();
                                if (parent != null){
                                    parent.getChildren().remove(fileItem);
                                }
                                targetPeonyArchivedFileList.remove(fileItem.getValue().getPeonyArchivedFile());
                                G02Log log = createNewG02LogInstance(PeonyLogName.DELETE_ARCHIVED_FILE);
                                if (log != null){
                                    log.setLoggedEntityType(GardenEntityType.ARCHIVE_DOCUMENTS.name());
                                    log.setLoggedEntityUuid(fileItem.getValue().getPeonyArchivedFile().getArchivedFile().getFileUuid());
                                    PeonyProperties.getSingleton().log(log);
                                }
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            //Exceptions.printStackTrace(ex);
                            PeonyFaceUtils.displayErrorMessageDialog("Failed. " + getMessage());
                        }
                    }
                };
                getCachedThreadPoolExecutorService().submit(deleteFileTask);
            }
        }
    }

    /**
     * Note: folder removal does not affect the archived-files in such a folder. All the archived-files can be only deleted with privillege.
     * @param folderItem 
     */
    private void deleteArchivedFolder(final TreeItem<PeonyArchivedFileTreeItemData> folderItem) {
        if ((folderItem != fileSystemTreeView.getRoot()) && (folderItem.getValue() != null) && (folderItem.getValue().getPeonyArchivedFile() == null)){
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to remove this folder?") == JOptionPane.YES_OPTION){
                Task<TreeItem<PeonyArchivedFileTreeItemData>> removeFolderTask = new Task<TreeItem<PeonyArchivedFileTreeItemData>>(){
                    @Override
                    protected TreeItem<PeonyArchivedFileTreeItemData> call() throws Exception {
                        String folderName = folderItem.getValue().getTreeItemRepresentativeText();
                        if (ZcaValidator.isNullEmpty(folderName)){
                            updateMessage("Cannot find the folder name");
                            return null;
                        }
                        //update archived files
                        ObservableList<TreeItem<PeonyArchivedFileTreeItemData>> fileItems = folderItem.getChildren();
                        if (fileItems != null){
                            PeonyArchivedFileList aPeonyArchivedFileList = new PeonyArchivedFileList();
                            PeonyArchivedFile aPeonyArchivedFile;
                            for (TreeItem<PeonyArchivedFileTreeItemData> fileItem : fileItems){
                                aPeonyArchivedFile = fileItem.getValue().getPeonyArchivedFile();
                                aPeonyArchivedFile.getArchivedFile().setFileFaceFolder(null);
                                aPeonyArchivedFileList.getPeonyArchivedFileList().add(aPeonyArchivedFile);
                            }
                            if (!aPeonyArchivedFileList.getPeonyArchivedFileList().isEmpty()){
                                Lookup.getDefault().lookup(PeonyBusinessService.class)
                                    .getPeonyBusinessRestClient().storeEntity_XML(PeonyArchivedFileList.class, 
                                                                                  GardenRestParams.Business.storePeonyArchivedFileListRestParams(), 
                                                                                  aPeonyArchivedFileList);
                            }
                        }
                        return folderItem;
                    }

                    @Override
                    protected void succeeded() {
                        try {
                            TreeItem<PeonyArchivedFileTreeItemData> folderItem = get();
                            String folderName = folderItem.getValue().getTreeItemRepresentativeText();
                            fileSystemTreeView.getRoot().getChildren().remove(folderItem);
                            fileSystemTreeView.getRoot().getChildren().addAll(folderItem.getChildren());
                            folderComboBox.getItems().remove(folderName);
                        } catch (InterruptedException | ExecutionException ex) {
                            //Exceptions.printStackTrace(ex);
                            PeonyFaceUtils.displayErrorMessageDialog("Failed. " + getMessage());
                        }
                    }
                };
                getCachedThreadPoolExecutorService().submit(removeFolderTask);
            }
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("Please select a folder");
        }
    }
}

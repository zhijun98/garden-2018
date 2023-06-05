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
package com.zcomapproach.garden.peony;

import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.resources.css.PeonyCss;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.JFXPanelLoadedEvent;
import com.zcomapproach.garden.peony.view.events.PeonyDataEntrySaveDemanding;
import com.zcomapproach.garden.peony.view.events.RequestBusyMouseCursor;
import com.zcomapproach.garden.peony.view.events.RequestDefaultMouseCursor;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import java.awt.Dimension;
import java.awt.Image;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.Cursor;
import javax.swing.JOptionPane;

/**
 * This class cannot be used directly with "new" operator. It helps to construct
 * all the concrete PeonyTopComponents in the Peony. Notice there is a protected
 * -method, i.e., peonyFaceController.getRootPane(),  has to be overriden in the 
 * implementation.
 * 
 * @author zhijun98
 */
@TopComponent.Description(
        preferredID = "PeonyTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/peony.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_PeonyTopComponent=Peony Window",
    "HINT_PeonyTopComponent=This is abstract Peony window"
})
public abstract class PeonyTopComponent extends TopComponent implements IPeonyTopComponent, PeonyFaceEventListener{
    /**
     * This single thread keep the task-order
     */
    private final ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
    
    /**
     * Listeners to this TopComponent
     */
    private final List<PeonyFaceEventListener> peonyFaceEventListenerList = new ArrayList<>();

    private Scene scene;
    
    private final JFXPanel jfxPanel = new JFXPanel();
    private PeonyFaceController peonyFaceController;
    private final AtomicBoolean lockInitialization = new AtomicBoolean(false);
    
    public PeonyTopComponent() {
        initComponents();
        setName(Bundle.CTL_PeonyTopComponent());
        setToolTipText(Bundle.HINT_PeonyTopComponent());
        /**
         * Notice this helps to control the number of opened windows for performance
         */
        PeonyFaceUtils.checkOpenedEditorTopComponentThreshold();
    }

    public Scene getScene() {
        return scene;
    }
 
    /**
     * Whether or not the data entry on this window was changed by users. 
     * This is used for reminding users of changes before they leave this window
     */
    private boolean dataEntrySaveDemanding;
    public synchronized boolean isDataEntrySaveDemanding() {
        return dataEntrySaveDemanding;
    }
    public synchronized void setDataEntrySaveDemanding(boolean dataEntrySaveDemanding) {
        this.dataEntrySaveDemanding = dataEntrySaveDemanding;
    }
    
    /**
     * an instance of Executors.newSingleThreadExecutor()
     * @return - Notice that NOT every controll has a ExecutorService instance 
     */
    public final ExecutorService getSingleExecutorService(){
        return singleExecutorService;
    }
    
    public void addPeonyFaceEventListenerList(List<PeonyFaceEventListener> listeners){
        synchronized(peonyFaceEventListenerList){
            for (PeonyFaceEventListener listener : listeners){
                addPeonyFaceEventListener(listener);
            }
        }
    }
    
    public List<PeonyFaceEventListener> getPeonyFaceEventListenerList(){
        synchronized(peonyFaceEventListenerList){
            return new ArrayList<>(peonyFaceEventListenerList);
        }
    }
    
    public void addPeonyFaceEventListener(PeonyFaceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
            peonyFaceEventListenerList.add(listener);
        }
    }
    
    public void removePeonyFaceEventListener(PeonyFaceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
        }
    }
    
    protected void broadcastPeonyFaceEventHappened(PeonyFaceEvent event){
        synchronized(peonyFaceEventListenerList){
            for (PeonyFaceEventListener aPeonyFaceControllerEventListener : peonyFaceEventListenerList){
                singleExecutorService.submit(new Runnable(){
                    @Override
                    public void run() {
                        aPeonyFaceControllerEventListener.peonyFaceEventHappened(event);
                    }
                });
            }
        }
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof CloseTopComponentRequest){
            handleCloseTopComponentRequest((CloseTopComponentRequest)event);
        }else if (event instanceof RequestDefaultMouseCursor){
            handleRequestDefaultMouseCursor();
        }else if (event instanceof RequestBusyMouseCursor){
            handleRequestBusyMouseCursor((RequestBusyMouseCursor)event);
        }else if (event instanceof JFXPanelLoadedEvent){
            handleJFXPanelLoaded((JFXPanelLoadedEvent)event);
        }else if (event instanceof PeonyDataEntrySaveDemanding){
            setDataEntrySaveDemanding(((PeonyDataEntrySaveDemanding)event).isSaveDemanding());
        }
    }

    private void handleJFXPanelLoaded(final JFXPanelLoadedEvent aJFXPanelLoadedEvent) {
        openTopComponent();
        if (getPeonyFaceController() != null){
            getPeonyFaceController().decoratePeonyFaceAfterLoading(scene);
        }
        makeBusy(false);  //stop displaying busy icon for this top-component
    }
    /**
     * After this event raised, this TopComponent is ready for display
     */
    public void openTopComponent() {
        if (SwingUtilities.isEventDispatchThread()){
            openTopComponentHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openTopComponentHelper();
                }
            });
        }
    }
    private void openTopComponentHelper(){
        open();
        requestActive();
    }
    
    private void handleCloseTopComponentRequest(CloseTopComponentRequest event){
        closeTopComponent();
    }

    private Image activatedIcon;
    private Image deactivated;

    @Override
    protected void componentActivated() {
        if (deactivated == null){
            deactivated = getIcon();
        }
        if (activatedIcon == null){
            activatedIcon = PeonyGraphic.getAwtImage("red_star.png");
        }
        setIcon(activatedIcon);
    }
    
    @Override
    protected void componentDeactivated() {
        if (deactivated != null){
            setIcon(deactivated);
        }
    }
    
    /**
     * Call TopComponent.close
     */
    public void closeTopComponent(){
        if (SwingUtilities.isEventDispatchThread()){
            close();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    close();
                }
            });
        }
    }

    @Override
    public boolean canClose() {
        boolean ok = super.canClose(); //To change body of generated methods, choose Tools | Templates.
        if (ok){
            if (isDataEntrySaveDemanding()){
                ok = PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Some data entries are changed without saved yet. Are you sure to close this window?") == JOptionPane.YES_OPTION;
            }
        }
        return ok;
    }
    
    /**
     * This is used to identify the TopComponent instance
     * @return 
     */
    public String getTargetTopComponetUuid(){
        return getName();
    }
    
    protected JFXPanel getJfxPanel() {
        return jfxPanel;
    }

    public PeonyFaceController getPeonyFaceController() {
        return peonyFaceController;
    }
    
    /**
     * This method is triggerred by constructTopComponent, which is triggered by 
 launchPeonyTopComponent in turn. It does the followings: (1)the concrete 
     * implementation createPeonyFaceController is triggered; (2)this top-component 
     * listens to the just-created controller; (3)load the controller's FXML file; 
     * (4)hook the root pane in the controller with its default pane-size; (5)layout 
     * with scene loading into jsfPanel; (6)handle JFXPanelLoadedEvent to open this 
     * top component instance.
     * @return 
     */
    protected abstract PeonyFaceController createPeonyFaceController();

    protected void constructTopComponent() {
        try {
            peonyFaceController = createPeonyFaceController();
            if (peonyFaceController == null){
                throw new IOException("No controller is provided for loading its FXML file.");
            }
            peonyFaceController.addPeonyFaceEventListener(this);
            Pane rootPane = peonyFaceController.loadFxml();
            if (rootPane != null){
                peonyFaceController.setDefaultRootPaneSize(new Dimension((int)rootPane.getPrefWidth()+8, (int)rootPane.getPrefHeight()+16));
                
                scene = new Scene(rootPane);
                rootPane.prefHeightProperty().bind(scene.heightProperty());
                rootPane.prefWidthProperty().bind(scene.widthProperty().subtract(10));

                scene.getStylesheets().add(PeonyCss.getPeonyGlobalCss());
                
                jfxPanel.setScene(scene);

                peonyFaceEventHappened(new JFXPanelLoadedEvent());
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void displayPeonyTopComponent(){
        if (SwingUtilities.isEventDispatchThread()){
            displayPeonyTopComponentHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayPeonyTopComponentHelper();
                }
            });
        }
    }
    
    private void displayPeonyTopComponentHelper(){
        peonyFaceEventHappened(new JFXPanelLoadedEvent());
        this.open();
        this.requestActive();
    }

    /**
     * This method has to be called when a concrete window is demanded to be constructed 
     * and display. This method can be invoked only once. This method does nothing for any 
     * other calls but display it
     * 
     * @param windowName 
     */
    public void launchPeonyTopComponent(final String windowName) {
        synchronized(lockInitialization){
            if (lockInitialization.get()){
                displayPeonyTopComponent();
                return;
            }
            lockInitialization.set(true);
        }
        if (SwingUtilities.isEventDispatchThread()){
            initializePeonyTopComponentHelper(windowName);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    initializePeonyTopComponentHelper(windowName);
                }
            });
        }
    }
    
    private void initializePeonyTopComponentHelper(String windowName){
        
        makeBusy(true); //display busy icon for this top-component
        
        windowName = ZcaText.denullize(windowName);
        /**
         * BorderLayout
         */
        this.setLayout(new BorderLayout());
        
        this.setName(windowName);

        this.add(jfxPanel, BorderLayout.CENTER);
        /**
         * Construct JFX scene
         */
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                constructTopComponent();
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    @Override
    public void makeJfxPanelRootBlurry() {
        if (peonyFaceController == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            PeonyFaceUtils.makeViewRootBlurry(peonyFaceController.getRootPane(), 10);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    PeonyFaceUtils.makeViewRootBlurry(peonyFaceController.getRootPane(), 10);
                }
            });
        }
    }

    @Override
    public void makeJfxPanelRootClear() {
        if (peonyFaceController == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            PeonyFaceUtils.makeViewRootClear(peonyFaceController.getRootPane());
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    PeonyFaceUtils.makeViewRootClear(peonyFaceController.getRootPane());
                }
            });
        }
    }

    private void handleRequestDefaultMouseCursor() {
        if (peonyFaceController == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            getScene().setCursor(Cursor.DEFAULT);
            makeJfxPanelRootClear();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    getScene().setCursor(Cursor.DEFAULT);
                    makeJfxPanelRootClear();
                }
            });
        }
    }

    private void handleRequestBusyMouseCursor(final RequestBusyMouseCursor event) {
        if (peonyFaceController == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            getScene().setCursor(Cursor.WAIT);
            PeonyFaceUtils.makeViewRootBlurry(peonyFaceController.getRootPane(), event.getRadius());
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    getScene().setCursor(Cursor.WAIT);
                    PeonyFaceUtils.makeViewRootBlurry(peonyFaceController.getRootPane(), event.getRadius());
                }
            });
        }
    }
}

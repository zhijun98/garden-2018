/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.view.dialogs;

import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.resources.css.PeonyCss;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.JFXPanelLoadedEvent;
import com.zcomapproach.garden.peony.view.events.PeonyDataEntrySaveDemanding;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import java.awt.Dimension;
import javax.swing.JOptionPane;

/**
 * This abstract dialog is to help construct "concrete" dialogs used in Peony.
 * @author zhijun98
 */
public abstract class PeonyFaceDialog extends javax.swing.JDialog implements PeonyFaceEventListener{

    /**
     * This single thread keep the task-order
     */
    private final ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
    
    /**
     * Listeners to this dialog
     */
    private final List<PeonyFaceEventListener> peonyFaceEventListenerList = new ArrayList<>();
    
    private Scene scene;
    
    private final JFXPanel jfxPanel = new JFXPanel();
    
    /**
     * if this is not null, createPeonyFaceController() will be ignored even if it was overridden 
     */
    private PeonyFaceController peonyFaceController;
    
    private final AtomicBoolean lockInitialization = new AtomicBoolean(false);

    private final int defaultCloseOperation;
    
    /**
     * Creates new form PeonyFaceDialog with JDialog.DISPOSE_ON_CLOSE as defaultCloseOperation
     * @param parent
     * @param modal
     */
    public PeonyFaceDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE;
    }
    
    public PeonyFaceDialog(java.awt.Frame parent, boolean modal, int defaultCloseOperation) {
        super(parent, modal);
        initComponents();
        this.defaultCloseOperation = defaultCloseOperation;
    }

    /**
     * This method defined how to handle PeonyFaceEvent (in a single thread) when it happened
     * @return 
     */
    public ExecutorService getSingleExecutorService() {
        return singleExecutorService;
    }
    
    public void addPeonyFaceEventListenerList(List<PeonyFaceEventListener> listeners){
        synchronized(peonyFaceEventListenerList){
            for (PeonyFaceEventListener listener : listeners){
                addPeonyFaceEventListener(listener);
            }
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

    public List<PeonyFaceEventListener> getPeonyFaceEventListenerList() {
        return peonyFaceEventListenerList;
    }
    
    /**
     * Broadcase event to listners sequentially in a single thread service 
     * @param event 
     */
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

    /**
     * Whether or not the data entry on the dialog was changed by users. 
     * This is used for reminding users of changes before they leave this dialog
     */
    private boolean dataEntrySaveDemanding;
    public synchronized boolean isDataEntrySaveDemanding() {
        return dataEntrySaveDemanding;
    }
    public synchronized void setDataEntrySaveDemanding(boolean dataEntrySaveDemanding) {
        this.dataEntrySaveDemanding = dataEntrySaveDemanding;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof CloseDialogRequest){
            handleCloseDialogRequest((CloseDialogRequest)event);
        }else if (event instanceof JFXPanelLoadedEvent){
            handleJFXPanelLoaded((JFXPanelLoadedEvent)event);
        }else if (event instanceof PeonyDataEntrySaveDemanding){
            setDataEntrySaveDemanding(((PeonyDataEntrySaveDemanding)event).isSaveDemanding());
        }else{
            broadcastPeonyFaceEventHappened(event);
        }
    }

    /**
     * After this event raised, this dialog is ready for display
     * @param aJFXPanelLoadedEvent 
     */
    private void handleJFXPanelLoaded(final JFXPanelLoadedEvent aJFXPanelLoadedEvent) {
        decorateDialog();
        openDialog();
        getPeonyFaceController().decoratePeonyFaceAfterLoading(scene);
    }
    
    public final void decorateDialog() {
        if (SwingUtilities.isEventDispatchThread()){
            decorateDialogHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    decorateDialogHelper();
                }
            });
        }
    }
    
    /**
     * This method is triggered after JFXPanelLoadedEvent was raised and it will
     * do nothing...implemented-dialog may override it to customize this behavior. 
     * This method will be executed in EDT thread
     */
    protected void decorateDialogHelper(){
        //do nothing...implemented-dialog may override it to customize this behavior
    }
    
    public void openDialog() {
        if (SwingUtilities.isEventDispatchThread()){
            openDialogHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openDialogHelper();
                }
            });
        }
    }
    
    private void openDialogHelper(){
        /**
         * Prohibit users from clicking the close button on the right upper corner
         */
        setDefaultCloseOperation(defaultCloseOperation);
        setPreferredSize(peonyFaceController.getDefaultRootPaneSize());
        
        pack();
        
        setLocationRelativeTo(PeonyLauncher.mainFrame);
        setVisible(true);
    }

    private void handleCloseDialogRequest(final CloseDialogRequest closeDialogRequest) {
        if (closeDialogRequest == null){
            return;
        }
        if (this.getPeonyFaceController().isDataEntryChanged()){
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Some data entry was changed. Are you sure to close this dialog?") != JOptionPane.YES_OPTION){
                return;
            }
        }
        closeDialog(closeDialogRequest.isDisposeDialog());
    }
    
    public void closeDialog(final boolean disposeDialog) {
        if (SwingUtilities.isEventDispatchThread()){
            closeDialogHelper(disposeDialog);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    closeDialogHelper(disposeDialog);
                }
            });
        }
    }
    private void closeDialogHelper(boolean disposeDialog) {
        setVisible(false);
        if (disposeDialog){
            dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    protected JFXPanel getJfxPanel() {
        return jfxPanel;
    }

    public PeonyFaceController getPeonyFaceController() {
        return peonyFaceController;
    }
    
    /**
     * if launchPeonyDialog(final String dialogTitle, PeonyFaceController peonyFaceController) 
     * is called, this dialog will use the pass-in peonyFaceController.
     * @return 
     */
    protected abstract PeonyFaceController createPeonyFaceController();
    
    private void constructDialogContent() {
        try {
            if (peonyFaceController == null){
                peonyFaceController = createPeonyFaceController();
            }
            if (peonyFaceController == null){
                throw new IOException("No controller is provided for loading its FXML file.");
            }
            peonyFaceController.addPeonyFaceEventListener(this);
            Pane rootDialogPane = peonyFaceController.loadFxml();
            if (rootDialogPane != null){
                peonyFaceController.setDefaultRootPaneSize(new Dimension((int)rootDialogPane.getPrefWidth()+20, (int)rootDialogPane.getPrefHeight()+40));
                scene = new Scene(rootDialogPane);
                rootDialogPane.prefHeightProperty().bind(scene.heightProperty());
                rootDialogPane.prefWidthProperty().bind(scene.widthProperty().subtract(10));

                scene.getStylesheets().add(PeonyCss.getPeonyGlobalCss());
                
                getJfxPanel().setScene(scene);

                peonyFaceEventHappened(new JFXPanelLoadedEvent());
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * This method has to be called when a concrete dialog is demanded to be constructed 
     * and display. This methid can be invoked only once. This method does nothing for any 
     * other calls but display it
     * 
     * @param dialogTitle 
     */
    public void launchPeonyDialog(final String dialogTitle) {
        launchPeonyDialog(dialogTitle, null);
    }
    
    public void launchPeonyDialog(final String dialogTitle, PeonyFaceController peonyFaceController) {
        synchronized(lockInitialization){
            if (lockInitialization.get()){
                peonyFaceEventHappened(new JFXPanelLoadedEvent());
                return;
            }
            lockInitialization.set(true);
        }
        if (this.peonyFaceController == null){
            this.peonyFaceController = peonyFaceController;
        }
        initializePeonyDialog(dialogTitle);
    }
    
    private void initializePeonyDialog(final String dialogTitle) {
        if (SwingUtilities.isEventDispatchThread()){
            initializePeonyDialogHelper(dialogTitle);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    initializePeonyDialogHelper(dialogTitle);
                }
            });
        }
    }
    
    private void initializePeonyDialogHelper(String dialogTitle){
        dialogTitle = ZcaText.denullize(dialogTitle);
        /**
         * BorderLayout
         */
        this.setLayout(new BorderLayout());
        
        this.setTitle(dialogTitle);

        this.add(getJfxPanel(), BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(defaultCloseOperation);    //default
        
        /**
         * Construct JFX scene
         */
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                constructDialogContent();
            }
        });
    }
}

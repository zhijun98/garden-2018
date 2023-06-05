/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster;

import com.zcomapproach.zcadocmaster.face.ZcaDocFrame;
import com.zcomapproach.zcaglobals.commons.ZSwingGlobal;
import javax.swing.SwingUtilities;

/**
 *
 * @author Zhijun Zhang
 */
public class ZcaDocMaster {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                
                ZSwingGlobal.setWindowsLookAndFeel();
                
                ZcaDocFrame aZcaDocFrame = new ZcaDocFrame();
                aZcaDocFrame.pack();
                aZcaDocFrame.setLocation(ZSwingGlobal.getScreenCenterPoint(aZcaDocFrame));
                aZcaDocFrame.setVisible(true);
            }
        });
    }
    
}

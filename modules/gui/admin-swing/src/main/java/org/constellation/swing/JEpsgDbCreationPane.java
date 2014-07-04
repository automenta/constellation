/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.constellation.swing;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;
import org.opengis.util.FactoryException;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author guilhem
 */
public class JEpsgDbCreationPane extends javax.swing.JPanel {

    protected static final Logger LOGGER = Logging.getLogger(JEpsgDbCreationPane.class);
    
    /**
     * Creates new form JEpsgDbCreationPane
     */
    public JEpsgDbCreationPane() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        installDbButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        guiDriver = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        guiDbURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        guiUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        guiPwd = new javax.swing.JPasswordField();

        org.openide.awt.Mnemonics.setLocalizedText(installDbButton, org.openide.util.NbBundle.getMessage(JEpsgDbCreationPane.class, "installMDWDatabase")); // NOI18N
        installDbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installDbButtonActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/constellation/swing/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, bundle.getString("driverClass")); // NOI18N

        guiDriver.setText("org.postgresql.Driver");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, bundle.getString("dbUrl")); // NOI18N

        guiDbURL.setText("jdbc:postgresql://<host>:5432/<database name>");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, bundle.getString("dbUser")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, bundle.getString("dbPwd")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(installDbButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(guiDriver)
                            .addComponent(guiDbURL, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                            .addComponent(guiUser)
                            .addComponent(guiPwd))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(guiDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(guiUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(guiPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(installDbButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void installDbButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installDbButtonActionPerformed
        EpsgInstaller installer = new EpsgInstaller();
        
        final String className  = guiDriver.getText();
        final String connectURL = guiDbURL.getText();
        final String user       = guiUser.getText();
        final String password   = new String(guiPwd.getPassword());
        if (className.isEmpty()  ||
            connectURL.isEmpty() ||
            user.isEmpty()       ||
            password.isEmpty()) {
            JOptionPane.showMessageDialog(null, LayerRowModel.BUNDLE.getString("missingParameter"), 
                        LayerRowModel.BUNDLE.getString("missingParameterTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // try to connect first
        try {
            final Connection c = DriverManager.getConnection(connectURL, user, password);
            c.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, LayerRowModel.BUNDLE.getString("invalidConnection"), 
                        LayerRowModel.BUNDLE.getString("invalidConnectionTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        installer.setDatabase(connectURL, user, password);
        
        
        try {
            // look for existing database
            boolean exist = installer.exists();
        
            // build the database if not exist
            if (!exist) {
                installer.call();
            } else {
                JOptionPane.showMessageDialog(null, LayerRowModel.BUNDLE.getString("alreadyExistingDatabase"), 
                            LayerRowModel.BUNDLE.getString("alreadyExistingDatabaseTitle"), JOptionPane.ERROR_MESSAGE);
            }
        } catch (FactoryException ex) {
            JOptionPane.showMessageDialog(null, LayerRowModel.BUNDLE.getString("errorEPSGDatabase"), 
                            LayerRowModel.BUNDLE.getString("errorEPSGDatabaseTitle"), JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Unable to create the EPSG database.", ex);
        }
        
    }//GEN-LAST:event_installDbButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField guiDbURL;
    private javax.swing.JTextField guiDriver;
    private javax.swing.JPasswordField guiPwd;
    private javax.swing.JTextField guiUser;
    private javax.swing.JButton installDbButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}

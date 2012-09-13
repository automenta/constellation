/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.constellation.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.constellation.admin.service.ConstellationServer;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JServiceCreationPane extends javax.swing.JPanel {

    private final ConstellationServer server;
    private final Map<String,List<String>> services;
    
    public JServiceCreationPane(final ConstellationServer server) {
        initComponents();
        this.server = server;
        
        services = server.services.getAvailableService();        
        guiType.setModel(new ListComboBoxModel(new ArrayList(services.keySet())));
    }
    
    public String getServiceType(){
        return (String) guiType.getSelectedItem();
    }
    
    public String getInstanceName(){
        return guiName.getText();
    }
    
    private void correctName(){
        int pos = guiName.getCaretPosition();
        guiName.setText(guiName.getText().replace(' ', '_'));
        guiName.setCaretPosition(pos);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        guiType = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        guiName = new javax.swing.JTextField();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/constellation/swing/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("type")); // NOI18N

        jLabel2.setText(bundle.getString("name")); // NOI18N

        guiName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                guiNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                guiNameKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiType, 0, 164, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiName)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(guiName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_guiNameKeyPressed
        correctName();
    }//GEN-LAST:event_guiNameKeyPressed

    private void guiNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_guiNameKeyTyped
        correctName();
    }//GEN-LAST:event_guiNameKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField guiName;
    private javax.swing.JComboBox guiType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables

    
    /**
     * 
     * @param server
     * @return 
     */
    public static String[] showDialog(final ConstellationServer server){
        
        final JServiceCreationPane pane = new JServiceCreationPane(server);
                
        final int res = JOptionPane.showOptionDialog(null, new Object[]{pane}, 
                LayerRowModel.BUNDLE.getString("createServiceMsg"), 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 
                null);
                
        pane.correctName();
        return new String[]{pane.getServiceType(),pane.getInstanceName()};
    }

}
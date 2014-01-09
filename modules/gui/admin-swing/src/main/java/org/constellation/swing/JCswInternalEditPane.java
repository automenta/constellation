/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2014, Geomatys
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

import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.constellation.generic.database.Automatic;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JCswInternalEditPane extends JServiceEditionPane {

    /**
     * Creates new form JCswInternalEditPane
     */
    public JCswInternalEditPane(final Automatic configuration) {
        initComponents();
    }

    @Override
    public Automatic getConfiguration() {
        final String nulll = null;
        final Automatic configuration = new Automatic("internal", nulll);
        configuration.setProfile((String)guiProfileCombo.getSelectedItem());
        return configuration;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new JLabel();
        guiProfileCombo = new JComboBox();

        ResourceBundle bundle = ResourceBundle.getBundle("org/constellation/swing/Bundle"); // NOI18N
        jLabel2.setText(bundle.getString("profil")); // NOI18N

        guiProfileCombo.setModel(new DefaultComboBoxModel(new String[] { "discovery", "transactional" }));
        guiProfileCombo.setEnabled(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(guiProfileCombo, 0, 310, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(guiProfileCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox guiProfileCombo;
    private JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}

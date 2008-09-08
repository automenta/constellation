/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2008, Geomatys
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
package net.seagis.sie.type.bbox;

// J2SE dependencies
import javax.swing.event.DocumentListener;


/**
 * La partie visuelle propre à la première page de l'assistant.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
final class WizardStep1 extends WizardStep {
    /**
     * Construit la composante visuelle.
     *
     * @param listener Objet à informer chaque fois que l'utilisateur a modifié le contenu d'un
     *                 champ obligatoire.
     */
    public WizardStep1(final DocumentListener listener) {
        initComponents();
        filenameField.getDocument().addDocumentListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void readSettings(final BoundingBox settings) {
        filenameField.setText(settings.getName());
    }

    /**
     * {@inheritDoc}
     */
    public void storeSettings(final BoundingBox settings) {
        settings.setName(filenameField.getText().trim());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JLabel filenameLabel;
        java.awt.GridBagConstraints gridBagConstraints;
        javax.swing.JLabel serverLabel;
        javax.swing.ButtonGroup servers;
        javax.swing.JPanel serversPanel;

        servers = new javax.swing.ButtonGroup();
        filenameLabel = new javax.swing.JLabel();
        filenameField = new javax.swing.JTextField();
        serverLabel = new javax.swing.JLabel();
        serversPanel = new javax.swing.JPanel();
        defaultServer = new javax.swing.JRadioButton();
        specifiedServer = new javax.swing.JRadioButton();

        setLayout(new java.awt.GridBagLayout());

        filenameLabel.setLabelFor(filenameField);
        filenameLabel.setText("Nom de fichier:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 6);
        add(filenameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
        add(filenameField, gridBagConstraints);

        serverLabel.setLabelFor(serversPanel);
        serverLabel.setText("Serveur:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        add(serverLabel, gridBagConstraints);

        serversPanel.setLayout(new java.awt.GridLayout(2, 0));

        servers.add(defaultServer);
        defaultServer.setSelected(true);
        defaultServer.setText("Par d\u00e9faut");
        serversPanel.add(defaultServer);

        servers.add(specifiedServer);
        specifiedServer.setText("Personnalis\u00e9");
        specifiedServer.setEnabled(false);
        serversPanel.add(specifiedServer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(serversPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton defaultServer;
    private javax.swing.JTextField filenameField;
    private javax.swing.JRadioButton specifiedServer;
    // End of variables declaration//GEN-END:variables
}

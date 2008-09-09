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
package org.constellation.sie.window.mosaic;

// J2SE dependencies
import java.util.Map;
import java.util.HashMap;
import java.awt.Image;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// OpenIDE dependencies
import org.openide.ErrorManager;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

// Geotools dependencies
import org.geotools.gui.swing.ImagePane;
import org.geotools.gui.swing.ExceptionMonitor;

// Sicade dependencies
import org.constellation.observation.Observations;
import org.constellation.observation.CatalogException;
import org.constellation.observation.coverage.Series;
import org.constellation.observation.coverage.CoverageReference;
import org.constellation.observation.coverage.CoverageTableModel;
import org.constellation.sie.window.coverages.CoveragesWindow;


/**
 * Fenêtre qui affichera une mosaïque d'images d'une série. Cette fenêtre peut être affichée
 * par {@link ViewAction}, une action qui sera proposée dans le menu "Window".
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
final class MosaicWindow extends TopComponent implements Runnable, ListSelectionListener {
    /**
     * Pour compatibilité avec différentes versions de cette classe.
     */
    private static final long serialVersionUID = -5719523277771255514L;

    /**
     * Chemin vers l'icône utilisée pour cette fenêtre ainsi que pour l'action
     * {@link ViewAction} qui l'ouvrira.
     */
    static final String ICON_PATH = "org/constellation/sie/window/mosaic/Icon.gif";

    /**
     * L'icone pour cette fenêtre. Ne sera lue que la première fois où elle sera nécessaire.
     */
    private static Image icon;

    /**
     * Une chaîne de caractères qui représentera cette fenêtre au sein du {@linkplain WindowManager
     * gestionnaire des fenêtres}. Cet ID sert à obtenir une instance unique de cette fenêtre par
     * un appel à la méthode {@link #findInstance}.
     */
    private static final String PREFERRED_ID = "MosaicWindow";

    /**
     * Ensemble des fenêtres déjà créées. Utilisé pour l'implémentation de {@link #findInstance}.
     */
    private static final Map<Series,MosaicWindow> POOL = new HashMap<Series,MosaicWindow>();

    /**
     * La séries d'images affichées dans cette mosaïque. Cette séries sera donnée à un objet
     * {@link CoverageTableModel} (propre à cette série) qui pourra ensuite être affiché dans
     * une instance unique de {@link CoveragesWindow} (en fonction de l'objet {@code MosaicWindow}
     * actif).
     */
    private final Series series;

    /**
     * Liste des images pour la {@linkplain #series séries affichée par cette fenêtre}.
     * Cette table sera remplie en arrière plan par la méthode {@link #run}.
     */
    private final CoverageTableModel table = new CoverageTableModel();

    /**
     * Paneau affichange l'image.
     *
     * @todo Temporaire en attendant d'avoir un vrai afficheur.
     */
    private final ImagePane imagePane = new ImagePane(8192*2);

    /**
     * Construit une fenêtre contenant une mosaïque initialement vide.
     */
    private MosaicWindow(final Series series) {
        this.series = series;
        initComponents();
        setDisplayName(series.getName());
        setName(getName(series));
        setIcon(getSharedIcon());
        add(imagePane.createScrollPane());
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Retourne le nom de la fenêtre pour la série spécifiée.
     */
    private static String getName(final Series series) {
        return PREFERRED_ID + ':' + series.getName();
    }

    /**
     * Retourne un icone partagé pour cette action.
     */
    private static synchronized Image getSharedIcon() {
        if (icon == null) {
            icon = Utilities.loadImage(ICON_PATH, true);
        }
        return icon;
    }

    /**
     * Obtient une instance unique d'une fenêtre de cette classe pour la série spécifiée.
     */
    public static MosaicWindow findInstance(final Series series) {
        synchronized (POOL) {
            MosaicWindow window = POOL.get(series);
            if (window == null) {
                window = new MosaicWindow(series);
                POOL.put(series, window);
            }
            return window;
        }
    }

    /**
     * Obtient la liste de toutes les images de la série donnée au constructeur de cet objet,
     * et stocke cette liste sous forme d'objet {@link CoverageTableModel}. Cette méthode est
     * exécutée dans un thread en arrière-plan. En cas d'échec, un message est affichée pour
     * l'utilisateur et aucune liste d'images ne sera affichée.
     */
    public void run() {
        try {
            table.setSeries(series);
        } catch (CatalogException e) {
            ExceptionMonitor.show(this, e);
        }
    }

    /**
     * Retourne l'identifiant des fenêtres de type {@code MosaicWindow} dans le
     * gestionnaire des fenêtres.
     */
    @Override
    protected String preferredID() {
        return getName();
    }

    /**
     * Appelée automatiquement lorsque cette fenêtre est ouverte. Cette méthode démarre
     * l'interrogation de la base de données en arrière-plan. Le contenu du tableau de
     * {@link CoveragesWindow} sera mise à jour dès que la liste des images sera prête
     * (si cette fenêtre est la fenêtre active).
     */
    @Override
    protected void componentOpened() {
        super.componentOpened();
        synchronized (POOL) {
            POOL.put(series, this);
        }
        final Thread filler = new Thread(this, getName());
        filler.setPriority(Thread.MIN_PRIORITY + 1);
        filler.setDaemon(true);
        filler.start();
    }

    /**
     * Appelée automatiquement lorsque cette fenêtre est fermée. Cette méthode vide le tableau
     * {@link CoveragesWindow} de son contenu (si cette fenêtre est la fenêtre active).
     */
    @Override
    protected void componentClosed() {
        synchronized (POOL) {
            POOL.remove(this);
        }
        try {
            table.setSeries(null);
        } catch (CatalogException e) {
            // Ne devrait jamais se produire.
            ErrorManager.getDefault().notify(e);
        }
        super.componentClosed();
    }

    /**
     * Appelée automatiquement lorsque cette fenêtre devient la fenêtre active. Cette méthode
     * signale à {@link CoveragesWindow} qu'il devrait afficher la liste d'images de cette
     * fenêtre.
     */
    @Override
    protected void componentActivated() {
        super.componentActivated();
        CoveragesWindow.findInstance().setCoverageTableModel(table, this);
    }

    /**
     * Appelée automatiquement lorsque la sélection de l'utilisateur dans {@link CoveragesWindow}
     * change. Cette méthode obtient les lignes modifiées et affiche les images qui y correspondent.
     *
     * @todo L'implémentation ci-dessous est grossière.
     */
    public void valueChanged(final ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            final CoverageReference[] coverages = CoveragesWindow.findInstance().getSelectedEntries();
            if (coverages.length != 0) try {
                imagePane.setImage(coverages[0].getCoverage(null).geophysics(false).getRenderedImage());
            } catch (Exception e) {
                ExceptionMonitor.show(this, e);
            } catch (OutOfMemoryError e) {
                ExceptionMonitor.show(this, e);
            }
        }
    }

    /**
     * Indique que les informations sur cette fenêtre ne doivent être enregistrées que si elle
     * était ouverte.
     */
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    /**
     * Lors de l'écriture en binaire de cette fenêtre, écrit une classe sentinelle
     * à la place de la totalité de {@code MosaicWindow}.
     */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper(series.getName());
    }

    /**
     * Les classes qui seront enregistrées en binaire à la place de {@link MosaicWindow}.
     * Lors de la lecture, cette classe appelera {@link MosaicWindow#getDefault} afin de
     * reconstruire une fenêtre qui apparaîtra dans l'application de l'utilisateur.
     *
     * @author Martin Desruisseaux
     * @version $Id$
     */
    final static class ResolvableHelper implements Serializable {
        /**
         * Pour compatibilité avec différentes versions de cette classe.
         */
        private static final long serialVersionUID = -9126363576866407661L;

        /**
         * Le nom de la séries.
         */
        private final String series;

        /**
         * Construit un objet à enregistrer à la place de {@link MosaicWindow}.
         */
        public ResolvableHelper(final String series) {
            this.series = series;
        }

        /**
         * Lors de la lecture binaire, remplace cet objet par une instance de la fenêtre
         * {@link CoveragesWindow}.
         */
        public Object readResolve() throws ObjectStreamException {
            try {
                return findInstance(Observations.DEFAULT.getSeries(series));
            } catch (CatalogException cause) {
                final InvalidObjectException e = new InvalidObjectException(cause.getLocalizedMessage());
                e.initCause(cause);
                throw e;
            }
        }
    }
}

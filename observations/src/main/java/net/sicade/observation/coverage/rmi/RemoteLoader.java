/*
 * Sicade - Syst�mes int�gr�s de connaissances pour l'aide � la d�cision en environnement
 * (C) 2005, Institut de Recherche pour le D�veloppement
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sicade.observation.coverage.rmi;

// J2SE and JAI dependencies
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.awt.image.RenderedImage;
import javax.media.jai.RenderedOp;

// Geotools dependencies
import org.geotools.coverage.grid.GridCoverage2D;

// Sicade dependencies
import net.sicade.observation.coverage.sql.GridCoverageEntry;  // Pour javadoc
import static net.sicade.observation.coverage.CoverageBuilder.FACTORY;


/**
 * Un d�codeur d'image qui sera ex�cut� sur un serveur distant. Une instance de {@code RemoteLoader} est
 * r�f�renc�e par chaque {@link GridCoverageEntry} qui a �t� {@linkplain GridCoverageEntry#export export�}
 * comme service RMI. Lors de la serialization du {@code GridCoverageEntry} dans le contexte des RMI, la
 * r�f�rence vers {@code RemoteLoader} sera automatiquement remplac�e par une connexion vers le serveur
 * d'origine.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public class RemoteLoader extends UnicastRemoteObject implements CoverageLoader {
    /**
     * Pour compatibilit�s entre les enregistrements binaires de diff�rentes versions.
     */
    private static final long serialVersionUID = -2228058795492073485L;

    /**
     * D�codeur d'images sur lequel d�l�guer le travail.
     */
    private final CoverageLoader loader;

    /**
     * Construit une instance de {@code RemoteLoader} qui d�l�guera sont travail au d�codeur
     * sp�cifi�.
     */
    public RemoteLoader(final CoverageLoader loader) throws RemoteException {
        this.loader = loader;
    }

    /**
     * Proc�de � la lecture de l'image. Cette m�thode ne retourne pas directement l'objet lu,
     * mais l'enveloppe plut�t dans un nouveau {@link GridCoverage2D} qui ne conservera aucune
     * r�f�rence vers ses sources. Si son image est le r�sultat d'une cha�ne d'op�ration, seul
     * le r�sultat sera conserv�e plut�t que la cha�ne. Enfin, seule la version non-g�ophysique
     * de l'image sera envoy�e sur le r�seau pour un transfert plus rapide. Il en r�sulte une
     * image qui peut �tre de qualit� d�grad�e par rapport � une image qui aurait �t� g�n�r�e
     * localement.
     *
     * @todo Essayer d'�viter d'appeller {@code geophysics(false)} si le format "naturel"
     *       des donn�es est g�ophysique. Les tests effectu�es pour l'instant provoquent
     *       un EOFException lors de la deserialisation.
     */
    public GridCoverage2D getCoverage() throws IOException {
        GridCoverage2D coverage = loader.getCoverage();
        coverage = coverage.geophysics(false);
        RenderedImage image = coverage.getRenderedImage();
        if (image instanceof RenderedOp) {
            image = ((RenderedOp) image).getRendering();
        }
        coverage = FACTORY.create(coverage.getName(), image,
                                  coverage.getCoordinateReferenceSystem(),
                                  coverage.getGridGeometry().getGridToCoordinateSystem(),
                                  coverage.getSampleDimensions(),
                                  null, null);
        return coverage;
    }
}

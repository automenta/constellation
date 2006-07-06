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
package net.sicade.observation.coverage.sql;

// J2SE dependencies
import java.util.List;

// OpenGIS dependencies
import org.opengis.coverage.Coverage;

// Geotoops dependencies
import org.geotools.resources.Utilities;

// Sicade dependencies
import net.sicade.observation.sql.Entry;
import net.sicade.observation.Distribution;
import net.sicade.observation.CatalogException;
import net.sicade.observation.coverage.Model;
import net.sicade.observation.coverage.Series;
import net.sicade.observation.coverage.Descriptor;
import net.sicade.observation.coverage.ModelCoverage;
import net.sicade.resources.XArray;


/**
 * Classe de base d'une entr�e repr�sentant une {@linkplain Model mod�le}, pas obligatoirement
 * lin�aire. Il peut s'agir par exemple d'un r�seau de neurones artificiels.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public abstract class ModelEntry extends Entry implements Model {
    /**
     * Pour compatibilit�s entre les enregistrements binaires de diff�rentes versions.
     */
    private static final long serialVersionUID = 5614887885038525651L;

    /**
     * La s�rie dans laquelle seront stock�es les valeurs de la variable d�pendante <var>y</var>.
     */
    private final Series target;

    /**
     * Les distributions de chaque descripteurs. Ne sera construite que la premi�re
     * fois o� elle sera n�cessaire.
     */
    private transient Distribution[] distributions;

    /**
     * Index des valeurs pour lesquelles une normalisation autre que la transformation identit�e
     * est n�cessaire.
     */
    private transient int[] index;

    /**
     * Construit un mod�le.
     *
     * @param target La s�rie dans laquelle seront stock�es les valeurs de la variable d�pendante.
     */
    public ModelEntry(final Series target) {
        super(target.getName());
        this.target = target;
    }

    /**
     * {inheritDoc}
     */
    public Series getTarget() {
        return target;
    }

    /**
     * {inheritDoc}
     */
    public void normalize(final double[] values) {
        Distribution[] distributions = this.distributions;
        int[]          index         = this.index;
        if (index == null) {
            // Pas besoin de synchroniser. Ce n'est pas grave si le tableau est construit 2 fois.
            int count = 0;
            final List<Descriptor> descriptors = getDescriptors();
            distributions = new Distribution[descriptors.size()];
            index = new int[distributions.length];
            for (int i=0; i<distributions.length; i++) {
                final Distribution candidate = descriptors.get(i).getDistribution();
                if (!candidate.isIdentity()) {
                    distributions[count] = candidate;
                    index[count++] = i;
                }
            }
            this.distributions = distributions = XArray.resize(distributions, count);
            this.index         = index         = XArray.resize(index,         count);
        }
        for (int i=0; i<index.length; i++) {
            final int n = index[i];
            values[n] = distributions[i].normalize(values[n]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Coverage asCoverage() throws CatalogException {
        return new ModelCoverage(this);
    }

    /**
     * V�rifie si cet objet est �gal � l'objet sp�cifi�.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            final ModelEntry that = (ModelEntry) object;
            return Utilities.equals(this.target, that.target);
        }
        return false;
    }
}

/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2006, Institut de Recherche pour le Développement
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
package net.sicade.observation.fishery.sql;

// J2SE dependencies
import java.util.Locale;
import static java.util.Locale.*;

// Sicade dependencies
import net.sicade.observation.fishery.Species;
import net.sicade.observation.sql.PropertyTypeEntry;


/**
 * Implémentation d'une entrée représentant une {@linkplain Species espèce}.
 * 
 * @version $Id$
 * @author Martin Desruisseaux
 */
public class SpeciesEntry extends PropertyTypeEntry implements Species {
    /**
     * Pour compatibilités entre les enregistrements binaires de différentes versions.
     */
    private static final long serialVersionUID = 6568964374209025692L;

    /**
     * Conventions locales pré-définies.
     */
    private static final Locale[] LOCALES = {
        ENGLISH, FRENCH, LATIN, FAO
    };

    /**
     * Les noms de cette espèce selon les différentes langues.
     */
    private final String english, french, latin;

    /**
     * Crée une nouvelle entrée du nom spécifié.
     */
    public SpeciesEntry(final String name,
                        final String english,
                        final String french,
                        final String latin,
                        final String remarks)
    {
        super(name, remarks);
        this.english = english;
        this.french  = french;
        this.latin   = latin;
    }

    /**
     * {@inheritDoc}
     */
    public Locale[] getLocales() {
        return (Locale[]) LOCALES.clone();
    }

    /**
     * {@inheritDoc}
     */
    public String getName(final Locale locale) {
        if (ENGLISH.equals(locale)) return english;
        if (FRENCH .equals(locale)) return french;
        if (LATIN  .equals(locale)) return latin;
        if (FAO    .equals(locale)) return super.getName();
        return null;
    }
    
    /**
     * {@inheritDoc}
     *
     * @deprecated Pas encore implémenté...
     */
    public Icon getIcon() {
        throw new UnsupportedOperationException();
    }
}

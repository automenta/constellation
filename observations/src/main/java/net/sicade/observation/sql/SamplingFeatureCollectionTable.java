/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
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
 */
package net.sicade.observation.sql;

// J2SE dependencies
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import net.sicade.catalog.BoundedSingletonTable;
import net.sicade.catalog.CRS;
import net.sicade.catalog.Database;
import net.sicade.catalog.Query;
import net.sicade.catalog.QueryType;

// Geotools dependencies
import org.geotools.resources.Utilities;

// Sicade dependencies
import net.sicade.catalog.ConfigurationKey;

// openGis dependencies
import org.opengis.observation.sampling.SamplingFeatureCollection;


/**
 * Connexion vers la table des {@linkplain Platform plateformes}
 * (bateaux, campagnes d'échantillonages...).
 * 
 * @version $Id$
 * @author Martin Desruisseaux
 */
@Deprecated
public class SamplingFeatureCollectionTable extends BoundedSingletonTable<SamplingFeatureCollection> {
    /**
     * Requête SQL pour obtenir les limites géographiques des plateformes dans une région.
     */
    private static final ConfigurationKey BOUNDING_BOX = null; // new ConfigurationKey("Platforms:BBOX",
//            "SELECT MIN(date), MAX(date), MIN(x), MAX(x), MIN(y), MAX(y)\n"          +
//            "  FROM \"Locations\"\n"                                                 +
//            "  JOIN \"Stations\" ON station=identifier\n"                            +
//            " WHERE (date>=? AND date<=?) AND (x>=? AND x<=?) AND (y>=? AND y<=?)\n" +
//            "   AND (provider LIKE ?)");

    /**
     * Requête SQL pour obtenir la liste des plateformes dans une région.
     */
    private static final ConfigurationKey LIST = null; // new ConfigurationKey("Platforms:LIST",
//            "SELECT DISTINCT platform\n"                                             +
//            "  FROM \"Stations\"\n"                                                  +
//            "  JOIN \"Locations\" ON station=identifier\n"                           +
//            " WHERE (date>=? AND date<=?) AND (x>=? AND x<=?) AND (y>=? AND y<=?)\n" +
//            "   AND (provider LIKE ?)\n" +
//            " ORDER BY date");

    /**
     * Requête SQL pour obtenir des informations sur une plateforme.
     */
    private static final ConfigurationKey SELECT = null; // new ConfigurationKey("Platforms:SELECT",
//            "SELECT DISTINCT platform\n" +
//            "  FROM \"Stations\"\n"      +
//            " WHERE platform=?");

    /** Numéro d'argument. */ private static final int ARGUMENT_PROVIDER = 7;
    /** Numéro de colonne. */ private static final int NAME              = 1;

    
    /**
     * Connexion vers la table des {@linkplain Station stations}. Une table par défaut sera
     * construite la première fois où elle sera nécessaire.
     * <p>
     * <strong>IMPORTANT:</strong> La table des stations peut faire elle-même appel à cette
     * table {@code PlatformTable}.  En conséquence, toutes méthodes qui peut faire appel à
     * la table des stations doit se synchroniser sur {@code stations} <em>avant</em> de se
     * synchroniser sur {@code this}, afin d'éviter des situations de <cite>thread lock</cite>.
     */
    private SamplingFeatureTable stations;

    /**
     * Le fournisseur des stations recherchées, ou {@code null} si on accepte tous les fournisseurs.
     */
    private String provider;

    /**
     * Construit une connexion vers la table des plateformes qui utilisera la base de données
     * spécifiée.
     */
    public SamplingFeatureCollectionTable(final Database database) {
        super(new Query(database), CRS.XYZT); // TODO
    }

    /**
     * Définie la table des stations à utiliser. Cette méthode peut être appelée par
     * {@link StationTable} avant toute première utilisation de {@code PlatformTable}.
     *
     * @param  stations Table des stations à utiliser.
     * @throws IllegalStateException si cette instance utilise déjà une autre table des stations.
     */
    protected synchronized void setStationTable(final SamplingFeatureTable stations)
            throws IllegalStateException
    {
        if (this.stations != stations) {
            if (this.stations != null) {
                throw new IllegalStateException();
            }
            this.stations = stations; // Doit être avant tout appel de setTable(this).
            stations.setPlatformTable(this);
        }
    }

    /**
     * Retourne la table des stations à utiliser pour la création des objets {@link StationEntry}.
     */
    final SamplingFeatureTable getStationTable() {
        assert Thread.holdsLock(this);
        if (stations == null) {
            setStationTable(getDatabase().getTable(SamplingFeatureTable.class));
        }
        return stations;
    }

    
    /**
     * Retourne le fournisseur des plateformes désirées, ou {@code null} pour obtenir toutes
     * les plateformes.
     */
    public final String getProvider() {
        return provider;
    }

    /**
     * Définit le fournisseur des plateformes désirées. Les prochains appels à la méthode
     * {@link #getEntries() getEntries()} ne retourneront que les plateformes de ce fournisseur.
     * La valeur {@code null} sélectionne toutes les plateformes.
     */
    public synchronized void setProvider(final String provider) {
        if (!Utilities.equals(provider, this.provider)) {
            this.provider = provider;
            fireStateChanged("Provider");
        }
    }

    /**
     * Configure la requête SQL spécifiée en fonction du {@linkplain #getProvider provider}
     * des données de cette table. Cette méthode est appelée automatiquement lorsque cette
     * table a {@linkplain #fireStateChanged changé d'état}.
     */
    @Override
    protected void configure(final QueryType type, final PreparedStatement statement) throws SQLException {
        super.configure(type, statement);
        switch (type) {
            case LIST: // Fall through
            case BOUNDING_BOX: {
                statement.setString(ARGUMENT_PROVIDER, provider);
                break;
            }
        }
    }

    /**
     * Construit une plateforme pour l'enregistrement courant.
     */
    protected SamplingFeatureCollection createEntry(final ResultSet results) throws SQLException {
        final String name = results.getString(NAME);
        return new SamplingFeatureCollectionEntry(this, name);
    }
}

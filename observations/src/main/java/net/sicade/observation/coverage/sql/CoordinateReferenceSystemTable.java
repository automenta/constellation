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
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// OpenGIS dependencies
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperationFactory;

// Geotools dependencies
import org.geotools.referencing.crs.DefaultCompoundCRS;
import org.geotools.referencing.FactoryFinder;
import org.geotools.resources.CRSUtilities;

// Sicade dependencies
import net.sicade.observation.sql.Use;
import net.sicade.observation.sql.UsedBy;
import net.sicade.observation.sql.CRS;
import net.sicade.observation.sql.Table;
import net.sicade.observation.sql.Database;
import net.sicade.observation.sql.Shareable;
import net.sicade.observation.ServerException;
import net.sicade.observation.ConfigurationKey;
import net.sicade.observation.CatalogException;
import net.sicade.observation.NoSuchRecordException;
import net.sicade.observation.IllegalRecordException;
import net.sicade.resources.seagis.ResourceKeys;
import net.sicade.resources.seagis.Resources;


/**
 * Connexion vers la table des {@linkplain CoordinateReferenceSystem syst�mes de ref�rence des
 * coordonn�es} utilis�s par les images. Si une entr�e de la table ne contient pas de dimension
 * temporelle, une dimension par d�faut sera automatiquement ajout�e.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
@UsedBy(GridCoverageTable.class)
public class CoordinateReferenceSystemTable extends Table implements Shareable {
    /**
     * Requ�te SQL utilis�e par cette classe pour obtenir les CRS. L'ordre des colonnes est
     * essentiel. Ces colonnes sont r�f�renc�es par les constantes {@link #WKT} et compagnie.
     */
    private static final ConfigurationKey SELECT = new ConfigurationKey("CoordinateReferenceSystems:SELECT",
            "SELECT \"WKT\"\n"                        +
            "  FROM \"CoordinateReferenceSystems\"\n" +
            " WHERE name=?");

    /** Num�ro d'argument. */ private static final int ARGUMENT_NAME = 1;
    /** Num�ro de colonne. */ private static final int WKT           = 1;

    /**
     * Fabrique � utiliser pour construire des transformations de coordonn�es.
     * Ce champ est acc�d� par {@link Parameters}.
     */
    static final CoordinateOperationFactory TRANSFORMS = FactoryFinder.getCoordinateOperationFactory(FACTORY_HINTS);

    /**
     * La fabrique de CRS � utiliser. Ne sera cr��e que la premi�re fois o� elle sera n�cessaire.
     */
    private CRSFactory factory;

    /**
     * Ensemble des syst�mes de r�f�rences qui ont d�j� �t� cr��s.
     */
    private final Map<String,CoordinateReferenceSystem> pool = new HashMap<String,CoordinateReferenceSystem>();

    /**
     * Construit une table des syst�mes de r�f�rences des coordonn�es.
     * 
     * @param  database Connexion vers la base de donn�es.
     */
    public CoordinateReferenceSystemTable(final Database database) {
        super(database);
    }

    /**
     * Retourne le syst�me de coordonn�es spatio-temporel pour le nom sp�cifi�.
     *
     * @param  name Le nom de l'entr�e d�sir�e.
     * @return L'entr�e demand�e, ou {@code null} si {@code name} �tait nul.
     * @throws CatalogException si aucun enregistrement ne correspond au nom demand�,
     *         ou si un enregistrement est invalide.
     * @throws SQLException si l'interrogation de la base de donn�es a �chou� pour une autre raison.
     */
    public synchronized CoordinateReferenceSystem getEntry(final String name)
            throws CatalogException, SQLException
    {
        if (name.equalsIgnoreCase("G�ographique")) {
            return CRS.XYT.getCoordinateReferenceSystem();
        }
        CoordinateReferenceSystem entry = pool.get(name);
        if (entry != null) {
            return entry;
        }
        final PreparedStatement statement = getStatement(SELECT);
        statement.setString(ARGUMENT_NAME, name);
        final ResultSet results = statement.executeQuery();
        while (results.next()) {
            final String wkt = results.getString(WKT);
            if (factory == null) {
                factory = FactoryFinder.getCRSFactory(FACTORY_HINTS);
            }
            final CoordinateReferenceSystem candidate;
            try {
                candidate = factory.createFromWKT(wkt);
            } catch (FactoryException e) {
                throw new ServerException(e);
            }
            if (entry == null) {
                entry = candidate;
            } else if (!entry.equals(candidate)) {
                final String table = results.getMetaData().getTableName(1);
                results.close();
                throw new IllegalRecordException(table, Resources.format(
                          ResourceKeys.ERROR_DUPLICATED_RECORD_$1, name));
            }
        }
        if (entry == null) {
            final String table = results.getMetaData().getTableName(1);
            results.close();
            throw new NoSuchRecordException(Resources.format(
                      ResourceKeys.ERROR_KEY_NOT_FOUND_$2, table, name), table);
        }
        /*
         * Ajoute une dimension temporelle (s'il n'y en avait pas d�j�) et sauvegarde le
         * r�sultat dans la cache pour r�utilisation.
         */
        TemporalCRS temporal = CRSUtilities.getTemporalCRS(entry);
        if (temporal == null) {
            temporal = CRSUtilities.getTemporalCRS(CRS.XYT.getCoordinateReferenceSystem());
            entry = new DefaultCompoundCRS(name, entry, temporal);
        }
        if (pool.put(name, entry) != null) {
            throw new AssertionError(name); // Should never happen.
        }
        return entry;
    }
}

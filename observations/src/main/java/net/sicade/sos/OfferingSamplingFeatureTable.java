
package net.sicade.sos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sicade.catalog.CatalogException;
import net.sicade.catalog.Database;
import net.sicade.catalog.QueryType;
import net.sicade.catalog.SingletonTable;
import net.sicade.observation.ProcessEntry;
import net.sicade.observation.ProcessTable;
import net.sicade.observation.SamplingFeatureEntry;
import net.sicade.observation.SamplingFeatureTable;
import org.geotools.resources.Utilities;

/**
 *
 * @author legal
 */
public class OfferingSamplingFeatureTable extends SingletonTable<OfferingSamplingFeatureEntry>{

        
    /**
     * identifiant secondaire de la table.
     */
    private String idOffering;
    
    /**
     * un lien vers la table des process.
     */
    private SamplingFeatureTable samplingFeatures;
    
    /**
     * Construit une table des phenomene composé.
     *
     * @param  database Connexion vers la base de données.
     */
    public OfferingSamplingFeatureTable(final Database database) {
        this(new OfferingSamplingFeatureQuery(database));
    }
    
    /**
     * Construit une nouvelle table non partagée
     */
    public OfferingSamplingFeatureTable(final OfferingSamplingFeatureTable table) {
        super(table);
    }
    
    /**
     * Initialise l'identifiant de la table.
     */
    private OfferingSamplingFeatureTable(final OfferingSamplingFeatureQuery query) {
        super(query);
        setIdentifierParameters(query.bySamplingFeature, null);
    }
    
    
    @Override
    protected OfferingSamplingFeatureEntry createEntry(final ResultSet results) throws CatalogException, SQLException {
        final OfferingSamplingFeatureQuery query = (OfferingSamplingFeatureQuery) super.query;
        
        if (samplingFeatures == null) {
            samplingFeatures = getDatabase().getTable(SamplingFeatureTable.class);
        }
        SamplingFeatureEntry samplingFeature = samplingFeatures.getEntry(results.getString(indexOf(query.samplingFeature)));
        
        return new OfferingSamplingFeatureEntry(results.getString(indexOf(query.idOffering)), samplingFeature);
    }
    
    /**
     * Specifie les parametres a utiliser dans la requetes de type "type".
     */
    @Override
    protected void configure(final QueryType type, final PreparedStatement statement) throws SQLException {
        super.configure(type, statement);
        final OfferingSamplingFeatureQuery query = (OfferingSamplingFeatureQuery) super.query;
        if (! type.equals(QueryType.INSERT))
            statement.setString(indexOf(query.byOffering), idOffering);
        
    }
    
    
    public String getIdOffering() {
        return idOffering;
    }
    
    public void setIdOffering(String idOffering) {
        if (!Utilities.equals(this.idOffering, idOffering)) {
            this.idOffering = idOffering;
            fireStateChanged("idOffering");
        }
        
    }
    
}

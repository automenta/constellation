/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
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
package org.constellation.observation;

import org.constellation.catalog.Column;
import org.constellation.catalog.Database;
import org.constellation.catalog.Parameter;
import org.constellation.catalog.Query;
import org.constellation.catalog.QueryType;
import static org.constellation.catalog.QueryType.*;

/**
 * The query to execute for a {@link MeasurementTable}.
 *
 * @author Guilhem Legal
 */
public class MeasurementQuery extends Query{
    
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    protected final Column name, featureOfInterest, featureOfInterestPoint, procedure, observedProperty, observedPropertyComposite,
            distribution, samplingTimeBegin, samplingTimeEnd, result, resultDefinition, description;
    // quality, , observationMetadata, procedureTime, procedureParameter,
 
    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    protected final Parameter byName;
    
    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public MeasurementQuery(final Database database) {
        super(database, "measurements");
        final QueryType[] si  = {SELECT, INSERT};
        final QueryType[] sie = {SELECT, INSERT, EXISTS};
        
        name                      = addColumn("name",                        sie);
        description               = addColumn("description",                 si);
        featureOfInterest         = addColumn("feature_of_interest",         si);
        featureOfInterestPoint    = addColumn("feature_of_interest_point",   si);
        procedure                 = addColumn("procedure",                   si);
        observedProperty          = addColumn("observed_property",           si);
        observedPropertyComposite = addColumn("observed_property_composite", si);
        distribution              = addColumn("distribution",                si);
        samplingTimeBegin         = addColumn("sampling_time_begin",         si);
        samplingTimeEnd           = addColumn("sampling_time_end",           si);
        result                    = addColumn("result",                      si);
        resultDefinition          = addColumn("result_definition",           si);
/*
        observationMetadata       = addColumn("observationMetadata",         SI);
        quality                   = addColumn("quality",                     SI);
        result                    = addColumn("result",                      SI);
        procedureTime             = addColumn("procedureTime",               SI);
        procedureParameter        = addColumn("procedureParameter",          SI);*/
                
        
        byName = addParameter(name, SELECT, EXISTS);
    }
    
}

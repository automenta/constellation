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

package org.constellation.sos.factory;

import java.util.Map;
import org.constellation.configuration.DataSourceType;
import org.constellation.generic.database.Automatic;
import org.constellation.sos.io.om2.OM2ObservationFilter;
import org.constellation.sos.io.om2.OM2ObservationReader;
import org.constellation.sos.io.om2.OM2ObservationWriter;
import org.constellation.sos.io.ObservationFilter;
import org.constellation.sos.io.ObservationReader;
import org.constellation.sos.io.ObservationWriter;
import org.constellation.ws.CstlServiceException;

import static org.constellation.configuration.DataSourceType.*;

/**
  * A postgrid implementation of the SOS factory.
 * it provide various reader / writer / filter for observations datasource.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OM2SOSFactory implements OMFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean factoryMatchType(DataSourceType type) {
        if (type.equals(OM2)) {
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ObservationFilter getObservationFilter(final DataSourceType type, final Automatic configuration, final Map<String, Object> properties) throws CstlServiceException {
       return new OM2ObservationFilter(configuration, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservationFilter cloneObservationFilter(final ObservationFilter omFilter) throws CstlServiceException {
       return new OM2ObservationFilter((OM2ObservationFilter)omFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservationReader getObservationReader(final DataSourceType type, final Automatic configuration, final Map<String, Object> properties) throws CstlServiceException {
        return new OM2ObservationReader(configuration, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservationWriter getObservationWriter(final DataSourceType type, final Automatic configuration, final Map<String, Object> properties) throws CstlServiceException {
        return new OM2ObservationWriter(configuration, properties);
    }

}

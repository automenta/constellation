/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.constellation.provider.coveragesgroup;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.constellation.ServiceDef.Query;
import org.constellation.provider.AbstractLayerDetails;
import org.constellation.provider.coveragesgroup.util.ConvertersJaxbToGeotk;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Cédric Briançon (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class CoveragesGroupLayerDetails extends AbstractLayerDetails {
    private static final Logger LOGGER = Logging.getLogger(CoveragesGroupLayerDetails.class);

    private MapContext ctxt;

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;

    public CoveragesGroupLayerDetails(final Name name, final File file) {
        this(name, file, null, null);
    }

    /**
     * hacked method to pass the login/pass to WebMapServer
     */
    public CoveragesGroupLayerDetails(final Name name, final File file, final String login, final String password) {
        super(name, Collections.EMPTY_LIST);

        // Parsing ctxt : MapBuilder.createContext
        try {
            ctxt = createMapContextForFile(file, login, password);
        } catch (JAXBException e) {
            LOGGER.log(Level.INFO, "Unable to convert map context file into a valid object", e);
        }
    }

    private MapContext createMapContextForFile(final File file, final String login, final String password) throws JAXBException {
        pool = new MarshallerPool(JAXBContext.newInstance(org.geotoolkit.providers.xml.MapContext.class, org.apache.sis.internal.jaxb.geometry.ObjectFactory.class), null);
        unmarshaller = pool.acquireUnmarshaller();
        final Object result = unmarshaller.unmarshal(file);
        if (!(result instanceof org.geotoolkit.providers.xml.MapContext)) {
            throw new JAXBException("Wrong response for the unmarshalling");
        }
        final org.geotoolkit.providers.xml.MapContext mapContext = (org.geotoolkit.providers.xml.MapContext)result;
        return ConvertersJaxbToGeotk.convertsMapContext(mapContext,login, password);
    }

    @Override
    public SortedSet<Date> getAvailableTimes() throws DataStoreException {
        return new TreeSet<Date>();
    }

    @Override
    public SortedSet<Number> getAvailableElevations() throws DataStoreException {
        return new TreeSet<Number>();
    }

    @Override
    public GridCoverage2D getCoverage(Envelope envelope, Dimension dimension, Double elevation, Date time) throws DataStoreException, IOException {
        return null;
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        try {
            return ctxt.getBounds();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public MapItem getMapLayer(MutableStyle style, Map<String, Object> params) throws PortrayalException {
        if (params != null) {
            final Map<String,?> extras = (Map<String, ?>) params.get(KEY_EXTRA_PARAMETERS);
            if (extras != null) {
                Filter filter = null;
                for (String key : extras.keySet()) {
                    if (key.equalsIgnoreCase("cql_filter")) {
                        final String cqlFilter = ((List) extras.get(key)).get(0).toString();
                        if (cqlFilter != null) {
                            filter = buildCQLFilter(cqlFilter, filter);
                        }
                    } else if (key.startsWith("dim_") || key.startsWith("DIM_")) {
                        final String dimValue = ((List) extras.get(key)).get(0).toString();
                        final String dimName = key.substring(4);
                        filter = buildDimFilter(dimName, dimValue, filter);
                    }
                }
                if (filter != null) {
                    setFilter(ctxt, filter);
                }
            }
        }

        setSelectableAndVisible(ctxt);
        return ctxt;
    }


    /**
     * Set selectable and visible to {@code true} recursively for all map items.
     *
     * @param item A {@link MapItem} that could embed others, if it is a {@link MapContext}
     *             for example.
     */
    private static void setSelectableAndVisible(final MapItem item) {
        if (item instanceof MapLayer) {
            ((MapLayer) item).setSelectable(true);
        }
        item.setVisible(true);

        for (MapItem it : item.items()) {
            setSelectableAndVisible(it);
        }
    }

    /**
     * Apply filter for all {@link MapItem} recursively.
     *
     * @param item
     * @param filter
     */
    private void setFilter(final MapItem item, final Filter filter) {
        if (item instanceof FeatureMapLayer) {
            final FeatureMapLayer fml = (FeatureMapLayer) item;
            fml.setQuery(QueryBuilder.filtered(fml.getCollection().getFeatureType().getName(), filter));
            return;
        }

        for (final MapItem m : item.items()) {
            setFilter(m, filter);
        }
    }

    @Override
    public MeasurementRange<?>[] getSampleValueRanges() {
        return new MeasurementRange<?>[0];
    }

    @Override
    public boolean isQueryable(Query query) {
        return false;
    }

    @Override
    public TYPE getType() {
        return TYPE.COVERAGE;
    }

}

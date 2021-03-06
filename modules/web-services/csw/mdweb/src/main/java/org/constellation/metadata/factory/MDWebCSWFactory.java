/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.constellation.metadata.factory;

import org.constellation.configuration.DataSourceType;
import org.constellation.filter.FilterParser;
import org.constellation.filter.LuceneFilterParser;
import org.constellation.filter.SQLFilterParser;
import org.constellation.generic.database.Automatic;
import org.constellation.metadata.harvest.ByIDHarvester;
import org.constellation.metadata.harvest.CatalogueHarvester;
import org.constellation.metadata.harvest.DefaultCatalogueHarvester;
import org.constellation.metadata.harvest.FileSystemHarvester;
import org.constellation.metadata.index.mdweb.MDWebIndexer;
import org.constellation.metadata.io.CSWMetadataReader;
import org.constellation.metadata.io.MetadataIoException;
import org.constellation.metadata.io.MetadataReader;
import org.constellation.metadata.io.MetadataWriter;
import org.constellation.metadata.io.mdweb.MDWebCSWMetadataReader;
import org.constellation.metadata.io.mdweb.MDWebCSWMetadataWriter;
import org.constellation.metadata.security.MetadataSecurityFilter;
import org.constellation.metadata.security.NoMetadataSecurityFilter;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.index.AbstractIndexer;
import org.geotoolkit.lucene.index.LuceneIndexSearcher;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.constellation.generic.database.Automatic.*;

// Constellation dependencies

/**
 * A default implementation of the CSW factory.
 * it provide various reader / writer and  lucene indexer / searcher.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MDWebCSWFactory implements AbstractCSWFactory {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataSourceType> availableType() {
        return Arrays.asList(DataSourceType.MDWEB);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean factoryMatchType(final DataSourceType type) {
        return DataSourceType.MDWEB.equals(type);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CSWMetadataReader getMetadataReader(final Automatic configuration, final String serviceID) throws MetadataIoException {
        return new MDWebCSWMetadataReader(configuration);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataWriter getMetadataWriter(final Automatic configuration, final AbstractIndexer indexer, final String serviceID) throws MetadataIoException {
        return new MDWebCSWMetadataWriter(configuration, indexer);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractIndexer getIndexer(final Automatic configuration, final MetadataReader reader, final String serviceID, final Map<String, List<String>> additionalQueryable) throws IndexingException {
        return new MDWebIndexer(configuration, serviceID, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LuceneIndexSearcher getIndexSearcher(final File configDirectory, final String serviceID) throws IndexingException {
        return new LuceneIndexSearcher(configDirectory, serviceID, null, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CatalogueHarvester getCatalogueHarvester(Automatic configuration, MetadataWriter writer) throws MetadataIoException {
        int type = -1;
        if (configuration != null) {
            type = configuration.getHarvestType();
        }
        switch (type) {
            case DEFAULT:
                return new DefaultCatalogueHarvester(writer);
            case FILESYSTEM:
                return new FileSystemHarvester(writer);
            case BYID:
                return new ByIDHarvester(writer, configuration.getIdentifierDirectory());
            default:
                throw new IllegalArgumentException("Unknow harvester type: " + type + " In Default CSW Factory.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FilterParser getLuceneFilterParser() {
        return new LuceneFilterParser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterParser getSQLFilterParser() {
        return new SQLFilterParser();
    }

    @Override
    public MetadataSecurityFilter getSecurityFilter() {
        return new NoMetadataSecurityFilter();
    }
}

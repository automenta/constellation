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

package org.constellation.metadata.configuration;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import org.constellation.ServiceDef.Specification;
import org.constellation.configuration.*;
import org.constellation.dto.Service;
import org.constellation.generic.database.Automatic;
import org.constellation.generic.database.BDD;
import org.constellation.ogc.configuration.OGCConfigurer;
import org.w3c.dom.Node;

/**
 * {@link org.constellation.configuration.ServiceConfigurer} implementation for CSW service.
 *
 * TODO: implement specific configuration methods
 *
 * @author Fabien Bernard (Geomatys).
 * @author Cédric Briançon (Geomatys)
 * @version 0.9
 * @since 0.9
 */
public class CSWConfigurer extends OGCConfigurer {

    /**
     * Create a new {@link CSWConfigurer} instance.
     */
    public CSWConfigurer() {
        super(Specification.CSW, Automatic.class, "config.xml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createInstance(final String identifier, final Service metadata, Object configuration) throws ConfigurationException {
        if (configuration == null) {
            configuration = new Automatic("filesystem", new BDD());
        }
        super.createInstance(identifier, metadata, configuration);
    }

    public AcknowlegementType refreshIndex(final String id, final boolean asynchrone, final boolean forced) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().refreshIndex(id, asynchrone, forced);
    }

    public AcknowlegementType addToIndex(final String id, final String identifierList) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().addToIndex(id, identifierList);
    }

    public AcknowlegementType removeFromIndex(final String id, final String identifierList) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().removeFromIndex(id, identifierList);
    }

    public AcknowlegementType stopIndexation(final String id) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().stopIndexation(id);
    }

    public AcknowlegementType importRecords(final String id, final File f, final String fileName) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().importRecords(id, f, fileName);
    }

    public AcknowlegementType removeRecords(final String id, final String identifierList) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().deleteMetadata(id, identifierList);
    }

    public AcknowlegementType metadataExist(final String id, final String identifier) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().metadataExist(id, identifier);
    }

    public List<BriefNode> getMetadataList(final String id, final int count, final int startIndex) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().getMetadataList(id, count, startIndex);
    }

    public Node getMetadata(final String id, final String identifier) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().getMetadata(id, identifier);
    }
    
    public int getMetadataCount(final String id) throws ConfigurationException {
        return CSWConfigurationManager.getInstance().getMetadataCount(id);
    }

    public StringList getAvailableCSWDataSourceType() {
        return CSWConfigurationManager.getInstance().getAvailableCSWDataSourceType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instance getInstance(final String identifier) throws ConfigurationException {
        final Instance instance = super.getInstance(identifier);
        try {
            instance.setLayersNumber(getMetadataCount(identifier));
        } catch (ConfigurationException ex) {
            LOGGER.log(Level.WARNING, "Error while getting metadata count on CSW instance:" + identifier, ex);
        }
        return instance;
    }
}

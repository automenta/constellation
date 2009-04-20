/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
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
package org.constellation.provider.sld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.constellation.provider.AbstractProviderService;
import org.constellation.provider.StyleProviderService;
import org.constellation.provider.configuration.ProviderConfig;
import org.constellation.provider.configuration.ProviderSource;

import org.geotoolkit.style.MutableStyle;

import static org.constellation.provider.sld.SLDProvider.*;


/**
 *
 * @version $Id$
 *
 * @author Johann Sorel (Geomatys)
 */
public class SLDProviderService extends AbstractProviderService<String,MutableStyle> implements StyleProviderService {

    /**
     * Default logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SLDProviderService.class.getName());
    private static final String NAME = "sld";

    private static final Collection<SLDProvider> PROVIDERS = new ArrayList<SLDProvider>();
    private static final Collection<SLDProvider> IMMUTABLE = Collections.unmodifiableCollection(PROVIDERS);

    @Override
    public Collection<SLDProvider> getProviders() {
        return IMMUTABLE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init(ProviderConfig config) {
        PROVIDERS.clear();
        for (final ProviderSource ps : config.sources) {
            try {
                SLDProvider provider = new SLDProvider(ps);
                PROVIDERS.add(provider);
                LOGGER.log(Level.INFO, "[PROVIDER]> SLD provider created : " + provider.getSource().parameters.get(KEY_FOLDER_PATH));
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.WARNING, "Invalide SLD provider config", ex);
            }
        }

    }

}

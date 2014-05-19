/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2014, Geomatys
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
package org.constellation.utils;

import org.apache.sis.util.Static;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FileFeatureStoreFactory;

import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * utility class to know files extensions availables on Geotoolkit.
 *
 * @author Benjamin Garcia (Geomatys)
 * @version 0.9
 * @since 0.9
 */
public final class GeotoolkitFileExtensionAvailable extends Static {


    /**
     * Give a extension list managed by geotoolkit
     * @return an {@link String} {@link List} which contain file extension.
     */
    public static Map<String, String> getAvailableFileExtension() {
        final Map<String, String> extensions = new HashMap<>(0);

        //get coverage file extension and add on list
        final String[] coverageExtension = ImageIO.getReaderFileSuffixes();
        for (String extension : coverageExtension) {
            extensions.put(extension.toLowerCase(), "raster");
        }

        //access to features file factories
        final Iterator<FileFeatureStoreFactory> ite = FeatureStoreFinder.getAllFactories(FileFeatureStoreFactory.class).iterator();
        while (ite.hasNext()) {

            final FileFeatureStoreFactory factory = ite.next();
            //display general informations about this factory
            String[] tempExtensions = factory.getFileExtensions();
            for (int i = 0; i < tempExtensions.length; i++) {
                String extension = tempExtensions[i];

                //remove point before extension
                extensions.put(extension.replace(".", "").toLowerCase(), "vector");
            }
        }
        
        // for O&M files
        extensions.put("xml", "observation");
        
        return extensions;
    }

}

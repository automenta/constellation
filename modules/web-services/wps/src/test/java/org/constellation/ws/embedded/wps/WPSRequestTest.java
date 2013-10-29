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
package org.constellation.ws.embedded.wps;

// JUnit dependencies
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.WPSCapabilitiesType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.constellation.admin.ConfigurationEngine;
import org.constellation.configuration.ConfigDirectory;
import org.constellation.configuration.ProcessContext;
import org.constellation.configuration.ProcessFactory;
import org.constellation.configuration.Processes;
import org.constellation.wps.ws.soap.WPSService;
import org.constellation.ws.embedded.AbstractGrizzlyServer;
import org.geotoolkit.util.FileUtilities;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @since 0.9
 */
public class WPSRequestTest  extends AbstractGrizzlyServer {

    private static final File configDirectory = new File("WPSRequestTest");

    private static final String WPS_GETCAPABILITIES ="request=GetCapabilities&service=WPS&version=1.0.0";

    private static final String WPS_GETCAPABILITIES2 ="request=GetCapabilities&service=WpS&version=1.0.0";

    @BeforeClass
    public static void initLayerList() throws Exception {
        if (configDirectory.exists()) {
            FileUtilities.deleteDirectory(configDirectory);
        }
        configDirectory.mkdir();
        ConfigDirectory.setConfigDirectory(configDirectory);

        final List<ProcessFactory> process = Arrays.asList(new ProcessFactory("jts", true));
        final Processes processes = new Processes(process);
        final ProcessContext config = new ProcessContext(processes);
        config.getCustomParameters().put("shiroAccessible", "false");

        ConfigurationEngine.storeConfiguration("WPS", "default", config);
        ConfigurationEngine.storeConfiguration("WPS", "test", config);

        final Map<String, Object> map = new HashMap<>();
        map.put("wps", new WPSService());
        initServer(new String[] {
            "org.constellation.wps.ws.rs",
            "org.constellation.configuration.ws.rs",
            "org.constellation.ws.rs.provider"}, map);
        pool = WPSMarshallerPool.getInstance();
    }

    @AfterClass
    public static void shutDown() {
        ConfigurationEngine.clearDatabase();
        FileUtilities.deleteDirectory(configDirectory);
        finish();
    }

    /**
     * Ensures that a valid GetCapabilities request returns indeed a valid GetCapabilities
     */
    @Test
    public void testWPSGetCapabilities() throws Exception {

        waitForStart();
        // Creates a valid GetCapabilities url.
        URL getCapsUrl;
        try {
            getCapsUrl = new URL("http://localhost:"+ grizzly.getCurrentPort() +"/wps/default?" + WPS_GETCAPABILITIES);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WPSCapabilitiesType.
        Object obj = unmarshallResponse(getCapsUrl);
        assertTrue("was " + obj, obj instanceof WPSCapabilitiesType);

        WPSCapabilitiesType responseCaps = (WPSCapabilitiesType)obj;


        String currentUrl = responseCaps.getOperationsMetadata().getOperation("getCapabilities").getDCP().get(0).getHTTP().getGetOrPost().get(0).getHref();
        assertEquals("http://localhost:"+ grizzly.getCurrentPort() +"/wps/default?", currentUrl);

        try {
            getCapsUrl = new URL("http://localhost:"+ grizzly.getCurrentPort() +"/wps/test?" + WPS_GETCAPABILITIES2);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WPSCapabilitiesType.
        obj = unmarshallResponse(getCapsUrl);
        assertTrue(obj instanceof WPSCapabilitiesType);

        responseCaps = (WPSCapabilitiesType)obj;

        currentUrl = responseCaps.getOperationsMetadata().getOperation("getCapabilities").getDCP().get(0).getHTTP().getGetOrPost().get(0).getHref();
        assertEquals("http://localhost:"+ grizzly.getCurrentPort() +"/wps/test?", currentUrl);


        try {
            getCapsUrl = new URL(WPS_GETCAPABILITIES);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WPSCapabilitiesType.
        obj = unmarshallResponse(getCapsUrl);
        assertTrue(obj instanceof WPSCapabilitiesType);

        responseCaps = (WPSCapabilitiesType)obj;

        currentUrl = responseCaps.getOperationsMetadata().getOperation("getCapabilities").getDCP().get(0).getHTTP().getGetOrPost().get(0).getHref();
        assertEquals("http://localhost:"+ grizzly.getCurrentPort() +"/wps/default?", currentUrl);
    }

}

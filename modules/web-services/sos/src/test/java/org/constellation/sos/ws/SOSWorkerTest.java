/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
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

package org.constellation.sos.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javax.xml.namespace.QName;
import org.constellation.configuration.DataSourceType;
import org.constellation.configuration.ObservationFilterType;
import org.constellation.configuration.ObservationReaderType;
import org.constellation.configuration.ObservationWriterType;
import org.constellation.configuration.SOSConfiguration;
import org.constellation.generic.database.Automatic;
import org.geotoolkit.ows.xml.v110.AcceptFormatsType;
import org.geotoolkit.ows.xml.v110.AcceptVersionsType;
import org.geotoolkit.ows.xml.v110.SectionsType;
import org.geotoolkit.sos.xml.v100.Capabilities;
import org.geotoolkit.sos.xml.v100.DescribeSensor;
import org.geotoolkit.sos.xml.v100.GetCapabilities;
import org.constellation.util.Util;
import org.constellation.ws.CstlServiceException;
import org.constellation.ws.MimeType;
import org.geotoolkit.gml.xml.v311.TimeIndeterminateValueType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.observation.xml.v100.ObservationCollectionEntry;
import org.geotoolkit.observation.xml.v100.ObservationEntry;
import org.geotoolkit.ogc.xml.v110.BinaryTemporalOpType;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sos.xml.v100.EventTime;
import org.geotoolkit.sos.xml.v100.GetObservation;
import org.geotoolkit.sos.xml.v100.GetResult;
import org.geotoolkit.sos.xml.v100.GetResultResponse;
import org.geotoolkit.sos.xml.v100.ResponseModeType;
import org.geotoolkit.swe.xml.v101.DataArrayEntry;
import org.geotoolkit.swe.xml.v101.DataArrayPropertyType;
import org.geotoolkit.xml.MarshallerPool;
import static org.geotoolkit.ows.xml.OWSExceptionCode.*;

// JUnit dependencies
import org.junit.*;
import org.opengis.observation.Observation;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SOSWorkerTest {

    private SOSworker worker;

    private MarshallerPool marshallerPool;

    private static final String URL = "http://pulsar.geomatys.fr/SOServer/SOService";

    @BeforeClass
    public static void setUpClass() throws Exception {
        deleteTemporaryFile();

        MarshallerPool pool   = new MarshallerPool(org.constellation.configuration.ObjectFactory.class);
        Marshaller marshaller =  pool.acquireMarshaller();
        
        
        File configDir = new File("SOSWorkerTest");
        if (!configDir.exists()) {
            configDir.mkdir();

            //we write the data files
            File offeringDirectory = new File(configDir, "offerings");
            offeringDirectory.mkdir();
            writeDataFile(offeringDirectory, "offering-1.xml", "offering-allSensor");

            File phenomenonDirectory = new File(configDir, "phenomenons");
            phenomenonDirectory.mkdir();
            writeDataFile(phenomenonDirectory, "phenomenon-depth.xml", "depth");
            writeDataFile(phenomenonDirectory, "phenomenon-temp.xml",  "temperature");
            writeDataFile(phenomenonDirectory, "phenomenon-depth-temp.xml",  "aggregatePhenomenon");

            File sensorDirectory = new File(configDir, "sensors");
            sensorDirectory.mkdir();
            writeDataFile(sensorDirectory, "system.xml",    "urn:ogc:object:sensor:GEOM:1");
            writeDataFile(sensorDirectory, "component.xml", "urn:ogc:object:sensor:GEOM:2");

            File featureDirectory = new File(configDir, "features");
            featureDirectory.mkdir();
            writeDataFile(featureDirectory, "feature1.xml", "10972X0137-PONT");
            writeDataFile(featureDirectory, "feature2.xml", "10972X0137-PLOUF");

            File observationsDirectory = new File(configDir, "observations");
            observationsDirectory.mkdir();
            writeDataFile(observationsDirectory, "observation1.xml", "urn:ogc:object:observation:GEOM:304");
            writeDataFile(observationsDirectory, "observation2.xml", "urn:ogc:object:observation:GEOM:305");
            writeDataFile(observationsDirectory, "observation3.xml", "urn:ogc:object:observation:GEOM:406");
            writeDataFile(observationsDirectory, "observation4.xml", "urn:ogc:object:observation:GEOM:307");
            writeDataFile(observationsDirectory, "observation5.xml", "urn:ogc:object:observation:GEOM:507");

            File observationTemplatesDirectory = new File(configDir, "observationTemplates");
            observationTemplatesDirectory.mkdir();
            writeDataFile(observationTemplatesDirectory, "observationTemplate-3.xml", "urn:ogc:object:observation:template:GEOM:3");
            writeDataFile(observationTemplatesDirectory, "observationTemplate-4.xml", "urn:ogc:object:observation:template:GEOM:4");
            writeDataFile(observationTemplatesDirectory, "observationTemplate-5.xml", "urn:ogc:object:observation:template:GEOM:5");

            //we write the configuration file
            File configFile = new File(configDir, "config.xml");
            Automatic SMLConfiguration = new Automatic();
            SMLConfiguration.setDataDirectory("SOSWorkerTest/sensors");
            Automatic OMConfiguration  = new Automatic();
            OMConfiguration.setDataDirectory("SOSWorkerTest");
            SOSConfiguration configuration = new SOSConfiguration(SMLConfiguration, OMConfiguration);
            configuration.setObservationReaderType(ObservationReaderType.FILESYSTEM);
            configuration.setObservationWriterType(ObservationWriterType.FILESYSTEM);
            configuration.setSMLType(DataSourceType.FILE_SYSTEM);
            configuration.setObservationFilterType(ObservationFilterType.LUCENE);
            configuration.setPhenomenonIdBase("urn:ogc:def:phenomenon:GEOM:");
            configuration.setProfile("transactional");
            configuration.setObservationTemplateIdBase("urn:ogc:object:observation:template:GEOM:");
            marshaller.marshal(configuration, configFile);

        }
        pool.release(marshaller);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteTemporaryFile();
    }

    public static void deleteTemporaryFile() {
        File configDirectory = new File("SOSWorkerTest");
        if (configDirectory.exists()) {
            File dataDirectory = new File(configDirectory, "sensors");
            if (dataDirectory.exists()) {
                for (File f : dataDirectory.listFiles()) {
                    f.delete();
                }
                dataDirectory.delete();
            }
            File indexDirectory = new File(configDirectory, "index");
            if (indexDirectory.exists()) {
                for (File f : indexDirectory.listFiles()) {
                    f.delete();
                }
                indexDirectory.delete();
            }
            dataDirectory = new File(configDirectory, "offerings");
            if (dataDirectory.exists()) {
                for (File f : dataDirectory.listFiles()) {
                    f.delete();
                }
                dataDirectory.delete();
            }
            dataDirectory = new File(configDirectory, "observations");
            if (dataDirectory.exists()) {
                for (File f : dataDirectory.listFiles()) {
                    f.delete();
                }
                dataDirectory.delete();
            }
            dataDirectory = new File(configDirectory, "observationTemplates");
            if (dataDirectory.exists()) {
                for (File f : dataDirectory.listFiles()) {
                    f.delete();
                }
                dataDirectory.delete();
            }
            dataDirectory = new File(configDirectory, "features");
            if (dataDirectory.exists()) {
                for (File f : dataDirectory.listFiles()) {
                    f.delete();
                }
                dataDirectory.delete();
            }
            dataDirectory = new File(configDirectory, "phenomenons");
            if (dataDirectory.exists()) {
                for (File f : dataDirectory.listFiles()) {
                    f.delete();
                }
                dataDirectory.delete();
            }
            File conf = new File(configDirectory, "config.xml");
            conf.delete();
            File map = new File(configDirectory, "mapping.properties");
            map.delete();
            configDirectory.delete();
        }
    }

    @Before
    public void setUp() throws Exception {

        marshallerPool = new MarshallerPool("org.geotoolkit.sos.xml.v100:org.geotoolkit.observation.xml.v100:org.geotoolkit.sml.xml.v100:org.geotoolkit.sampling.xml.v100:org.geotoolkit.swe.xml.v101");
        
        File configDir = new File("SOSWorkerTest");
        worker = new SOSworker(configDir);
        Unmarshaller unmarshaller = marshallerPool.acquireUnmarshaller();
        Capabilities stcapa = (Capabilities) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/SOSCapabilities1.0.0.xml"));
        worker.setSkeletonCapabilities(stcapa);
        worker.setServiceURL(URL);
        marshallerPool.release(unmarshaller);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests the getcapabilities method
     *
     * @throws java.lang.Exception
     */
    @Test
    public void getCapabilitiesTest() throws Exception {

        /*
         *  TEST 1 : minimal getCapabilities
         */
        GetCapabilities request = new GetCapabilities();
        Capabilities result = worker.getCapabilities(request);

        assertTrue(result != null);
        assertTrue(result.getVersion().equals("1.0.0"));
        assertTrue(result.getFilterCapabilities() != null);
        assertTrue(result.getOperationsMetadata() != null);
        assertTrue(result.getServiceIdentification() != null);
        assertTrue(result.getServiceProvider() != null);

        assertTrue(result.getContents() != null);
        assertTrue(result.getContents().getObservationOfferingList() != null);
        assertTrue(result.getContents().getObservationOfferingList().getObservationOffering() != null);
        assertTrue(result.getContents().getObservationOfferingList().getObservationOffering().size() == 1);

        /*
         *  TEST 2 : full get capabilities
         */
        AcceptVersionsType acceptVersions = new AcceptVersionsType("1.0.0");
        SectionsType sections             = new SectionsType("All");
        AcceptFormatsType acceptFormats   = new AcceptFormatsType(MimeType.APP_XML);
        request = new GetCapabilities(acceptVersions, sections, acceptFormats, "", "SOS");

        result = worker.getCapabilities(request);

        assertTrue(result.getVersion().equals("1.0.0"));
        assertTrue(result.getFilterCapabilities() != null);
        assertTrue(result.getOperationsMetadata() != null);
        assertTrue(result.getServiceIdentification() != null);
        assertTrue(result.getServiceProvider() != null);
        assertTrue(result.getContents() != null);
        assertTrue(result.getContents().getObservationOfferingList() != null);
        assertTrue(result.getContents().getObservationOfferingList().getObservationOffering() != null);
        assertTrue(result.getContents().getObservationOfferingList().getObservationOffering().size() == 1);
        assertTrue(result != null);

        /*
         *  TEST 3 : get capabilities section Operation metadata
         */
        acceptVersions = new AcceptVersionsType("1.0.0");
        sections       = new SectionsType("OperationsMetadata");
        acceptFormats  = new AcceptFormatsType(MimeType.APP_XML);
        request = new GetCapabilities(acceptVersions, sections, acceptFormats, "", "SOS");

        result = worker.getCapabilities(request);

        assertTrue(result.getVersion().equals("1.0.0"));
        assertTrue(result.getFilterCapabilities() == null);
        assertTrue(result.getOperationsMetadata() != null);
        assertTrue(result.getServiceIdentification() == null);
        assertTrue(result.getServiceProvider() == null);
        assertTrue(result.getContents() == null);
        assertTrue(result != null);

        /*
         *  TEST 4 : get capabilities section Service provider
         */
        acceptVersions = new AcceptVersionsType("1.0.0");
        sections       = new SectionsType("ServiceProvider");
        acceptFormats  = new AcceptFormatsType(MimeType.APP_XML);
        request = new GetCapabilities(acceptVersions, sections, acceptFormats, "", "SOS");

        result = worker.getCapabilities(request);

        assertTrue(result.getVersion().equals("1.0.0"));
        assertTrue(result.getFilterCapabilities() == null);
        assertTrue(result.getOperationsMetadata() == null);
        assertTrue(result.getServiceIdentification() == null);
        assertTrue(result.getServiceProvider() != null);
        assertTrue(result.getContents() == null);
        assertTrue(result != null);

        /*
         *  TEST 5 : get capabilities section Service Identification
         */
        acceptVersions = new AcceptVersionsType("1.0.0");
        sections       = new SectionsType("ServiceIdentification");
        acceptFormats  = new AcceptFormatsType(MimeType.APP_XML);
        request = new GetCapabilities(acceptVersions, sections, acceptFormats, "", "SOS");

        result = worker.getCapabilities(request);

        assertTrue(result.getVersion().equals("1.0.0"));
        assertTrue(result.getFilterCapabilities() == null);
        assertTrue(result.getOperationsMetadata() == null);
        assertTrue(result.getServiceIdentification() != null);
        assertTrue(result.getServiceProvider() == null);
        assertTrue(result.getContents() == null);
        assertTrue(result != null);

        /*
         *  TEST 6 : get capabilities section Contents
         */
        acceptVersions = new AcceptVersionsType("1.0.0");
        sections       = new SectionsType("Contents");
        acceptFormats  = new AcceptFormatsType(MimeType.APP_XML);
        request = new GetCapabilities(acceptVersions, sections, acceptFormats, "", "SOS");

        result = worker.getCapabilities(request);

        assertTrue(result.getVersion().equals("1.0.0"));
        assertTrue(result.getFilterCapabilities() == null);
        assertTrue(result.getOperationsMetadata() == null);
        assertTrue(result.getServiceIdentification() == null);
        assertTrue(result.getServiceProvider() == null);
        assertTrue(result.getContents() != null);
        assertTrue(result.getContents().getObservationOfferingList() != null);
        assertTrue(result.getContents().getObservationOfferingList().getObservationOffering() != null);
        assertTrue(result.getContents().getObservationOfferingList().getObservationOffering().size() == 1);
        assertTrue(result != null);

        /*
         *  TEST 7 : get capabilities with wrong version (waiting for an exception)
         */
        acceptVersions = new AcceptVersionsType("2.0.0");
        sections       = new SectionsType("All");
        acceptFormats  = new AcceptFormatsType(MimeType.TEXT_XML);
        request = new GetCapabilities(acceptVersions, sections, acceptFormats, "", "SOS");

        boolean exLaunched = false;
        try {
            worker.getCapabilities(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), VERSION_NEGOTIATION_FAILED);
            assertEquals(ex.getLocator(), "acceptVersion");
        }

        assertTrue(exLaunched);

         /*
         *  TEST 8 : : get capabilities with wrong formats (waiting for an exception)
         */
        request = new GetCapabilities("1.0.0", "ploup/xml");

        exLaunched = false;
        try {
            worker.getCapabilities(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "acceptFormats");
        }

        /*
         *  LAST TEST : get capabilities no skeleton capabilities
         */

        worker.setSkeletonCapabilities(null);
        request = new GetCapabilities();

        exLaunched = false;
        try {
            worker.getCapabilities(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), NO_APPLICABLE_CODE);
        }

        assertTrue(exLaunched);
    
    }

    
    
    /**
     * Tests the DescribeSensor method
     *
     * @throws java.lang.Exception
     */
    @Test
    public void DescribeSensorTest() throws Exception {
        Unmarshaller unmarshaller = marshallerPool.acquireUnmarshaller();

        /**
         * Test 1 bad outputFormat
         */
        boolean exLaunched = false;
        DescribeSensor request  = new DescribeSensor("urn:ogc:object:sensor:GEOM:1", "text/xml; subtype=\"SensorML/1.0.0\"");
        try {
            worker.describeSensor(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "outputFormat");
        }
        assertTrue(exLaunched);

        /**
         * Test 2 missing outputFormat
         */
        exLaunched = false;
        request  = new DescribeSensor("urn:ogc:object:sensor:GEOM:1", null);
        try {
            worker.describeSensor(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), MISSING_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "outputFormat");
        }
        assertTrue(exLaunched);

        /**
         * Test 3 missing sensorID
         */
        exLaunched = false;
        request  = new DescribeSensor(null, "text/xml;subtype=\"SensorML/1.0.0\"");
        try {
            worker.describeSensor(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), MISSING_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), Parameters.PROCEDURE);
        }
        assertTrue(exLaunched);

        /**
         * Test 4 system sensor
         */
        request  = new DescribeSensor("urn:ogc:object:sensor:GEOM:1", "text/xml;subtype=\"SensorML/1.0.0\"");
        AbstractSensorML result = (AbstractSensorML) worker.describeSensor(request);

        AbstractSensorML expResult = (AbstractSensorML) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/system.xml"));

        assertEquals(expResult, result);

        /**
         * Test 5 component sensor
         */
        request  = new DescribeSensor("urn:ogc:object:sensor:GEOM:2", "text/xml;subtype=\"SensorML/1.0.0\"");
        result = (AbstractSensorML) worker.describeSensor(request);

        expResult = (AbstractSensorML) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/component.xml"));

        assertEquals(expResult, result);

        marshallerPool.release(unmarshaller);
    }

    /**
     * Tests the GetObservation method
     *
     * @throws java.lang.Exception
     */
    @Test
    public void GetObservationTest() throws Exception {
        Unmarshaller unmarshaller = marshallerPool.acquireUnmarshaller();

         /**
         *  Test 1: getObservation with bad response format
         */
        GetObservation request  = new GetObservation("1.0.0",
                                                     "offering-allSensor",
                                                     null,
                                                     Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                                     null,
                                                     null,
                                                     null,
                                                     "text/xml;subtype=\"om/2.0.0\"",
                                                     Parameters.OBSERVATION_QNAME,
                                                     ResponseModeType.INLINE,
                                                     null);
        boolean exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), Parameters.RESPONSE_FORMAT);
        }
        assertTrue(exLaunched);

        /**
         *  Test 2: getObservation with bad response format
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      null,
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), MISSING_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), Parameters.RESPONSE_FORMAT);
        }
        assertTrue(exLaunched);


        /**
         *  Test 3: getObservation with procedure urn:ogc:object:sensor:GEOM:4 and no resultModel
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,           
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      null,
                                      ResponseModeType.INLINE,
                                      null);
        ObservationCollectionEntry result = (ObservationCollectionEntry) worker.getObservation(request);

        JAXBElement obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        ObservationEntry expResult = (ObservationEntry)obj.getValue();

        assertEquals(result.getMember().size(), 1);

        ObservationEntry obsResult = (ObservationEntry) result.getMember().iterator().next();


        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 4: getObservation with procedure urn:ogc:object:sensor:GEOM:4 avec responseMode null
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      null,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();

        assertEquals(result.getMember().size(), 1);

        obsResult = (ObservationEntry) result.getMember().iterator().next();

        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);


        /**
         *  Test 5: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();

        assertEquals(result.getMember().size(), 1);

        obsResult = (ObservationEntry) result.getMember().iterator().next();


        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 6: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        assertEquals(result.getMember().size(), 3);

        /**
         *  Test 7: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TBefore
         */
        List<EventTime> times = new ArrayList<EventTime>();
        TimeInstantType instant = new TimeInstantType(new TimePositionType("2007-05-01T03:00:00.0"));
        BinaryTemporalOpType filter = new BinaryTemporalOpType(instant);
        EventTime before            = new EventTime(null, filter, null);
        times.add(before);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        assertEquals(result.getMember().size(), 1);

        assertEquals(result.getMember().iterator().next().getName(), "urn:ogc:object:observation:GEOM:304");

        /**
         *  Test 8: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TAFter
         */
        times = new ArrayList<EventTime>();
        EventTime after            = new EventTime(filter,null, null);
        times.add(after);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        assertEquals(result.getMember().size(), 3);

        Iterator<Observation> i = result.getMember().iterator();
        List<String> results = new ArrayList<String>();
        results.add(i.next().getName());
        results.add(i.next().getName());
        results.add(i.next().getName());

        assertTrue(results.contains("urn:ogc:object:observation:GEOM:304"));
        assertTrue(results.contains("urn:ogc:object:observation:GEOM:307"));
        assertTrue(results.contains("urn:ogc:object:observation:GEOM:305"));

        /**
         *  Test 9: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TDuring
         */
        times = new ArrayList<EventTime>();
        TimePeriodType period = new TimePeriodType(new TimePositionType("2007-05-01T03:00:00.0"), new TimePositionType("2007-05-01T08:00:00.0"));
        filter = new BinaryTemporalOpType(period);
        EventTime during = new EventTime(null, null, filter);
        times.add(during);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        assertEquals(result.getMember().size(), 2);

        i = result.getMember().iterator();
        results = new ArrayList<String>();
        results.add(i.next().getName());
        results.add(i.next().getName());

        assertTrue(results.contains("urn:ogc:object:observation:GEOM:304"));
        assertTrue(results.contains("urn:ogc:object:observation:GEOM:305"));

        /**
         *  Test 10: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        EventTime equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        assertEquals(result.getMember().size(), 1);

        i = result.getMember().iterator();
        results = new ArrayList<String>();
        results.add(i.next().getName());

        assertTrue(results.contains("urn:ogc:object:observation:GEOM:304"));

        /**
         *  Test 11: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         *
         * with unsupported Response mode
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.OUT_OF_BAND,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), NO_APPLICABLE_CODE);
            assertEquals(ex.getLocator(), Parameters.RESPONSE_MODE);
        }
        assertTrue(exLaunched);

        /**
         *  Test 12: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         *
         * with unsupported Response mode
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.ATTACHED,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), OPERATION_NOT_SUPPORTED);
            assertEquals(ex.getLocator(), Parameters.RESPONSE_MODE);
        }
        assertTrue(exLaunched);

        /**
         *  Test 13: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         *
         * with no offering
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      null,
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), MISSING_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), Parameters.OFFERING);
        }
        assertTrue(exLaunched);

        /**
         *  Test 14: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         *
         * with wrong offering
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "inexistant-offering",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), Parameters.OFFERING);
        }
        assertTrue(exLaunched);

        /**
         *  Test 15: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         *
         * with wrong srsName
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      "EPSG:3333");
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "srsName");
        }
        assertTrue(exLaunched);


        /**
         *  Test 16: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *          + Time filter TEquals
         *
         * with wrong resultModel
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      new QName("some_namespace", "some_localPart"),
                                      ResponseModeType.INLINE,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "resultModel");
        }
        assertTrue(exLaunched);

        /**
         *  Test 17: getObservation with unexisting procedure
         *          + Time filter TEquals
         *
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:36"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), Parameters.PROCEDURE);
        }
        assertTrue(exLaunched);

        /**
         *  Test 18: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *           with resultTemplate mode
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.RESULT_TEMPLATE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();

        //for template the sampling time is 1970 to now
        period = new TimePeriodType(new TimePositionType("1900-01-01T00:00:00"));
        expResult.setSamplingTime(period);

        // and we empty the result object
        DataArrayPropertyType arrayP = (DataArrayPropertyType) expResult.getResult();
        DataArrayEntry array = arrayP.getDataArray();
        array.setElementCount(0);
        array.setValues("");

        expResult.setName("urn:ogc:object:observation:template:GEOM:4-0");

        assertEquals(result.getMember().size(), 1);

        obsResult = (ObservationEntry) result.getMember().iterator().next();


        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 19: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *           with resultTemplate mode
         *           with timeFilter TEquals
         */
        times = new ArrayList<EventTime>();
        period = new TimePeriodType(new TimePositionType("2007-05-01T02:59:00.0"), new TimePositionType("2007-05-01T06:59:00.0"));
        filter = new BinaryTemporalOpType(period);
        equals = new EventTime(filter);
        times.add(equals);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.RESULT_TEMPLATE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();

        //for template the sampling time is 1970 to now
        expResult.setSamplingTime(period);

        // and we empty the result object
        arrayP = (DataArrayPropertyType) expResult.getResult();
        array = arrayP.getDataArray();
        array.setElementCount(0);
        array.setValues("");

        expResult.setName("urn:ogc:object:observation:template:GEOM:4-1");

        assertEquals(result.getMember().size(), 1);

        obsResult = (ObservationEntry) result.getMember().iterator().next();


        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 20: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *           with resultTemplate mode
         *           with timeFilter Tafter
         */
        times = new ArrayList<EventTime>();
        instant = new TimeInstantType(new TimePositionType("2007-05-01T17:58:00.0"));
        filter = new BinaryTemporalOpType(instant);
        after = new EventTime(filter,null, null);
        times.add(after);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.RESULT_TEMPLATE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();

        //for template the sampling time is 1970 to now
        period = new TimePeriodType(instant.getTimePosition());
        expResult.setSamplingTime(period);

        expResult.setName("urn:ogc:object:observation:template:GEOM:4-2");

        // and we empty the result object
        arrayP = (DataArrayPropertyType) expResult.getResult();
        array = arrayP.getDataArray();
        array.setElementCount(0);
        array.setValues("");

        assertEquals(result.getMember().size(), 1);

        obsResult = (ObservationEntry) result.getMember().iterator().next();


        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 21: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *           with resultTemplate mode
         *           with timeFilter Tbefore
         */
        times = new ArrayList<EventTime>();
        instant = new TimeInstantType(new TimePositionType("2007-05-01T17:58:00.0"));
        filter = new BinaryTemporalOpType(instant);
        before = new EventTime(null, filter, null);
        times.add(before);
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      times,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.RESULT_TEMPLATE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();

        //for template the sampling time is 1970 to now
        period = new TimePeriodType(TimeIndeterminateValueType.BEFORE, instant.getTimePosition());
        expResult.setSamplingTime(period);

        expResult.setName("urn:ogc:object:observation:template:GEOM:4-3");

        // and we empty the result object
        arrayP = (DataArrayPropertyType) expResult.getResult();
        array = arrayP.getDataArray();
        array.setElementCount(0);
        array.setValues("");

        assertEquals(result.getMember().size(), 1);

        obsResult = (ObservationEntry) result.getMember().iterator().next();


        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 22: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *           with observedproperties = urn:ogc:def:phenomenon:GEOM:depth
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      Arrays.asList("urn:ogc:def:phenomenon:GEOM:depth"),
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation3.xml"));

        expResult = (ObservationEntry)obj.getValue();
        assertEquals(result.getMember().size(), 1);


        obsResult = (ObservationEntry) result.getMember().iterator().next();
        assertTrue(obsResult != null);

        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 23: getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *          and with wrong observed prop
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      Arrays.asList("urn:ogc:def:phenomenon:GEOM:hotness"),
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);

        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "observedProperty");
        }
        assertTrue(exLaunched);

        /**
         *  Test 24: getObservation with procedure urn:ogc:object:sensor:GEOM:5
         *           with observedproperties = urn:ogc:def:phenomenon:GEOM:aggreagtePhenomenon
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:5"),
                                      Arrays.asList("urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon"),
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation5.xml"));

        expResult = (ObservationEntry)obj.getValue();
        assertEquals(result.getMember().size(), 1);


        obsResult = (ObservationEntry) result.getMember().iterator().next();
        assertTrue(obsResult != null);

        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 25: getObservation with procedure urn:ogc:object:sensor:GEOM:5
         *           with observedproperties = urn:ogc:def:phenomenon:GEOM:aggreagtePhenomenon
         *           with foi                =  10972X0137-PLOUF
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:5"),
                                      Arrays.asList("urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon"),
                                      new GetObservation.FeatureOfInterest(Arrays.asList("10972X0137-PLOUF")),
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);

        obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation5.xml"));

        expResult = (ObservationEntry)obj.getValue();
        assertEquals(result.getMember().size(), 1);


        obsResult = (ObservationEntry) result.getMember().iterator().next();
        assertTrue(obsResult != null);

        assertEquals(expResult.getName(), obsResult.getName());
        assertEquals(expResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(expResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(expResult.getProcedure(), obsResult.getProcedure());
        assertEquals(expResult.getResult(), obsResult.getResult());
        assertEquals(expResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(expResult, obsResult);

        /**
         *  Test 26: getObservation with procedure urn:ogc:object:sensor:GEOM:5
         *          and with wrong foi
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:4"),
                                      null,
                                      new GetObservation.FeatureOfInterest(Arrays.asList("NIMP")),
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);

        exLaunched = false;
        try {
            worker.getObservation(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "featureOfInterest");
        }
        assertTrue(exLaunched);

        /**
         *  Test 27: getObservation with procedure urn:ogc:object:sensor:GEOM:3
         *           with observedproperties = urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon
         *           => no error but no result
         */
        request  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      Arrays.asList("urn:ogc:def:phenomenon:GEOM:aggregatePhenomenon"),
                                      new GetObservation.FeatureOfInterest(Arrays.asList("10972X0137-PLOUF")),
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.INLINE,
                                      null);
        result = (ObservationCollectionEntry) worker.getObservation(request);


        ObservationCollectionEntry collExpResult = new ObservationCollectionEntry("urn:ogc:def:nil:OGC:inapplicable");
        assertEquals(collExpResult, result);

        marshallerPool.release(unmarshaller);
    }

    /**
     * Tests the GetResult method
     *
     * @throws java.lang.Exception
     */
    @Test
    public void GetResultTest() throws Exception {
        Unmarshaller unmarshaller = marshallerPool.acquireUnmarshaller();
        
        /**
         * Test 1: bad version number + null template ID
         */
        String templateId = null;
        GetResult request = new GetResult(templateId, null, "2.0.0");
        boolean exLaunched = false;
        try {
            worker.getResult(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), VERSION_NEGOTIATION_FAILED);
        }
        assertTrue(exLaunched);

        /**
         * Test 2:  null template ID
         */
        templateId = null;
        request = new GetResult(templateId, null, "1.0.0");
        exLaunched = false;
        try {
            worker.getResult(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), MISSING_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "ObservationTemplateId");
        }
        assertTrue(exLaunched);

        /**
         * Test 3:  bad template ID
         */
        templateId = "some id";
        request = new GetResult(templateId, null, "1.0.0");
        exLaunched = false;
        try {
            worker.getResult(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), INVALID_PARAMETER_VALUE);
            assertEquals(ex.getLocator(), "ObservationTemplateId");
        }
        assertTrue(exLaunched);

        // we make a getObservation request in order to get a template

        /**
         *   getObservation with procedure urn:ogc:object:sensor:GEOM:4
         *           with resultTemplate mode
         */
        GetObservation GOrequest  = new GetObservation("1.0.0",
                                      "offering-allSensor",
                                      null,
                                      Arrays.asList("urn:ogc:object:sensor:GEOM:3"),
                                      null,
                                      null,
                                      null,
                                      "text/xml; subtype=\"om/1.0.0\"",
                                      Parameters.OBSERVATION_QNAME,
                                      ResponseModeType.RESULT_TEMPLATE,
                                      null);
        ObservationCollectionEntry obsCollResult = (ObservationCollectionEntry) worker.getObservation(GOrequest);

        JAXBElement obj =  (JAXBElement) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/observation1.xml"));

        ObservationEntry templateExpResult = (ObservationEntry)obj.getValue();

        //for template the sampling time is 1970 to now
        TimePeriodType period = new TimePeriodType(new TimePositionType("1900-01-01T00:00:00"));
        templateExpResult.setSamplingTime(period);

        // and we empty the result object
        DataArrayPropertyType arrayP = (DataArrayPropertyType) templateExpResult.getResult();
        DataArrayEntry array = arrayP.getDataArray();
        array.setElementCount(0);
        array.setValues("");

        templateExpResult.setName("urn:ogc:object:observation:template:GEOM:3-0");

        assertEquals(obsCollResult.getMember().size(), 1);

        ObservationEntry obsResult = (ObservationEntry) obsCollResult.getMember().iterator().next();

        assertEquals(templateExpResult.getName(), obsResult.getName());
        assertEquals(templateExpResult.getFeatureOfInterest(), obsResult.getFeatureOfInterest());
        assertEquals(templateExpResult.getObservedProperty(), obsResult.getObservedProperty());
        assertEquals(templateExpResult.getProcedure(), obsResult.getProcedure());
        assertEquals(templateExpResult.getResult(), obsResult.getResult());
        assertEquals(templateExpResult.getSamplingTime(), obsResult.getSamplingTime());
        assertEquals(templateExpResult, obsResult);

        /**
         * Test 3:  getResult with no TimeFilter
         */
        templateId = "urn:ogc:object:observation:template:GEOM:3-0";
        request = new GetResult(templateId, null, "1.0.0");
        GetResultResponse result = worker.getResult(request);

        String value = "2007-05-01T02:59:00,6.560@@2007-05-01T03:59:00,6.560@@2007-05-01T04:59:00,6.560@@2007-05-01T05:59:00,6.560@@2007-05-01T06:59:00,6.560@@" + '\n' +
                       "2007-05-01T07:59:00,6.560@@2007-05-01T08:59:00,6.560@@2007-05-01T09:59:00,6.560@@2007-05-01T10:59:00,6.560@@2007-05-01T11:59:00,6.560@@" + '\n' +
                       "2007-05-01T17:59:00,6.560@@2007-05-01T18:59:00,6.550@@2007-05-01T19:59:00,6.550@@2007-05-01T20:59:00,6.550@@2007-05-01T21:59:00,6.550@@" + '\n';
        GetResultResponse expResult = new GetResultResponse(new GetResultResponse.Result(value, URL + '/' + templateId));

        assertEquals(expResult.getResult().getRS(), result.getResult().getRS());
        assertEquals(expResult.getResult().getValue(), result.getResult().getValue());
        assertEquals(expResult.getResult(), result.getResult());
        assertEquals(expResult, result);
        marshallerPool.release(unmarshaller);
    }

    /**
     * Tests the GetResult method
     *
     * @throws java.lang.Exception
     */
    @Ignore
    public void destroyTest() throws Exception {
        worker.destroy();
        GetCapabilities request = new GetCapabilities();

        boolean exLaunched = false;
        try {
            worker.getCapabilities(request);
        } catch (CstlServiceException ex) {
            exLaunched = true;
            assertEquals(ex.getExceptionCode(), NO_APPLICABLE_CODE);
            assertEquals(ex.getMessage(), "The service is not running!");
        }

        assertTrue(exLaunched);
    }



    public static void writeDataFile(File dataDirectory, String resourceName, String identifier) throws IOException {

        File dataFile = new File(dataDirectory, identifier + ".xml");
        FileWriter fw = new FileWriter(dataFile);
        InputStream in = Util.getResourceAsStream("org/constellation/sos/" + resourceName);

        byte[] buffer = new byte[1024];
        int size;

        while ((size = in.read(buffer, 0, 1024)) > 0) {
            fw.write(new String(buffer, 0, size));
        }
        in.close();
        fw.close();
    }
}

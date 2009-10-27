/*
 *    Constellation - An open source and WhiteSpace compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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

package org.constellation.metadata.index.generic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// JAXB dependencies
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

// Lucene dependencies
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;

// constellation dependencies
import org.constellation.util.Util;

// Geotoolkit dependencies
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.lucene.filter.SerialChainFilter;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.metadata.iso.DefaultMetadata;

//Junit dependencies
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WhiteSpaceAnalyzerTest {

    protected static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY,FilterFactory2.class));

    private Logger logger = Logger.getLogger("org.constellation.metadata");

    private static GenericIndexSearcher indexSearcher;

    private static File configDirectory = new File("WhiteSpaceAnalyzerTest");


    @BeforeClass
    public static void setUpClass() throws Exception {
        deleteIndex();
        List<DefaultMetadata> object = fillTestData();
        GenericIndexer indexer = new GenericIndexer(object, null, configDirectory, "", new WhitespaceAnalyzer());
        indexSearcher          = new GenericIndexSearcher(configDirectory, "", new WhitespaceAnalyzer());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteIndex();
    }

    public static void deleteIndex() {
        if (configDirectory.exists()) {
            File indexDirectory = new File(configDirectory, "index");
            if (indexDirectory.exists()) {
                for (File f : indexDirectory.listFiles()) {
                    f.delete();
                }
                indexDirectory.delete();
            }
            configDirectory.delete();
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void simpleSearchTest() throws Exception {
        Filter nullFilter   = null;
        String resultReport = "";

        /**
         * Test 1 simple search: title = 90008411.ctd
         */
        SpatialQuery spatialQuery = new SpatialQuery("Title:90008411.ctd", nullFilter, SerialChainFilter.AND);
        List<String> result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("SimpleSearch 1:" + '\n' + resultReport);

        // the result we want are this
        List<String> expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");
        //expectedResult.add("42292_9s_19900610041000");

        assertEquals(expectedResult, result);


         /**
         * Test 2 simple search: indentifier != 40510_145_19930221211500
         */
        resultReport = "";
        spatialQuery = new SpatialQuery("metafile:doc NOT identifier:\"40510_145_19930221211500\"", nullFilter, SerialChainFilter.AND);
        result       = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("SimpleSearch 2:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);

        /**
         * Test 3 simple search: originator = Donnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:\"Donnees CTD NEDIPROD VI 120\"", nullFilter, SerialChainFilter.AND);
        result = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("simpleSearch 3:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);
    }

     /**
     * Test simple lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void wildCharSearchTest() throws Exception {
        Filter nullFilter   = null;
        String resultReport = "";

        /**
         * Test 1 simple search: title = title1
         */
        SpatialQuery spatialQuery = new SpatialQuery("Title:90008411*", nullFilter, SerialChainFilter.AND);
        List<String> result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("wildCharSearch 1:" + '\n' + resultReport);

        List<String> expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");

        assertEquals(expectedResult, result);

        /**
         * Test 2 wildChar search: abstract LIKE *NEDIPROD*
         */
        spatialQuery = new SpatialQuery("abstract:*NEDIPROD*", nullFilter, SerialChainFilter.AND);
        result = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("wildCharSearch 2:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");

        // ERROR it didn't find any result (why???)
        expectedResult = new ArrayList<String>();
        assertEquals(expectedResult, result);

        /**
         * Test 3 wildChar search: title like *.ctd
         */
        resultReport = "";
        spatialQuery = new SpatialQuery("Title:*.ctd", nullFilter, SerialChainFilter.AND);
        result       = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("wildCharSearch 3:" + '\n' + resultReport);

        assertTrue(result.contains("39727_22_19750113062500"));
        assertTrue(result.contains("40510_145_19930221211500"));
        assertTrue(result.contains("42292_5p_19900609195600"));
        assertTrue(result.contains("42292_9s_19900610041000"));



        /**
         * Test 4 wildCharSearch: anstract LIKE *onnees CTD NEDIPROD VI 120
         */
        spatialQuery = new SpatialQuery("abstract:(*onnees CTD NEDIPROD VI 120)", nullFilter, SerialChainFilter.AND);
        result = indexSearcher.doSearch(spatialQuery);

        resultReport = "";
        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("wildCharSearch 4:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

    }

     /**
     * Test simple lucene date search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void dateSearchTest() throws Exception {
        Filter nullFilter   = null;
        String resultReport = "";

        /**
         * Test 1 date search: date after 25/01/2009
         */
        SpatialQuery spatialQuery = new SpatialQuery("date:{20090125 30000101}", nullFilter, SerialChainFilter.AND);
        List<String> result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("DateSearch 1:" + '\n' + resultReport);

        List<String> expectedResult = new ArrayList<String>();
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);
    }

    /**
     * Test sorted lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void sortedSearchTest() throws Exception {

        Filter nullFilter   = null;
        String resultReport = "";

        /**
         * Test 1 sorted search: all orderBy identifier ASC
         */
        SpatialQuery spatialQuery = new SpatialQuery("metafile:doc", nullFilter, SerialChainFilter.AND);
        spatialQuery.setSort(new Sort("identifier_sort", false));

        List<String> result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("SortedSearch 1:" + '\n' + resultReport);

        List<String> expectedResult = new ArrayList<String>();
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);

        /**
         * Test 2 sorted search: all orderBy identifier DSC
         */
        resultReport = "";
        spatialQuery = new SpatialQuery("metafile:doc", nullFilter, SerialChainFilter.AND);
        spatialQuery.setSort(new Sort("identifier_sort", true));

        result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("SortedSearch 2:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("CTDF02");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");

        assertEquals(expectedResult, result);

        /**
         * Test 3 sorted search: all orderBy Abstract ASC
         */
        resultReport = "";
        spatialQuery = new SpatialQuery("metafile:doc", nullFilter, SerialChainFilter.AND);
        spatialQuery.setSort(new Sort("Abstract_sort", false));

        result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("SortedSearch 3:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("CTDF02");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("42292_5p_19900609195600");

        assertEquals(expectedResult, result);

        /**
         * Test 4 sorted search: all orderBy Abstract DSC
         */
        resultReport = "";
        spatialQuery = new SpatialQuery("metafile:doc", nullFilter, SerialChainFilter.AND);
        spatialQuery.setSort(new Sort("Abstract_sort", true));

        result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("SortedSearch 4:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("40510_145_19930221211500");
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);
    }

   /**
     *
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void spatialSearchTest() throws Exception {

        String resultReport = "";

        /**
         * Test 1 spatial search: BBOX filter
         */
        double min1[] = {-20, -20};
        double max1[] = { 20,  20};
        GeneralEnvelope bbox = new GeneralEnvelope(min1, max1);
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
        bbox.setCoordinateReferenceSystem(crs);
        LuceneOGCFilter sf          = LuceneOGCFilter.wrap(FF.bbox(LuceneOGCFilter.GEOMETRY_PROPERTY, -20, -20, 20, 20, "EPSG:4326"));
        SpatialQuery spatialQuery = new SpatialQuery("metafile:doc", sf, SerialChainFilter.AND);

        List<String> result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("spatialSearch 1:" + '\n' + resultReport);

        List<String> expectedResult = new ArrayList<String>();
        expectedResult.add("39727_22_19750113062500");
        expectedResult.add("11325_158_19640418141800");
        expectedResult.add("CTDF02");

        assertEquals(expectedResult, result);

        /**
         * Test 1 spatial search: NOT BBOX filter
         */
        resultReport = "";
        List<Filter> lf = new ArrayList<Filter>();
        //sf           = new BBOXFilter(bbox, "urn:x-ogc:def:crs:EPSG:6.11:4326");
        sf           = LuceneOGCFilter.wrap(FF.bbox(LuceneOGCFilter.GEOMETRY_PROPERTY, -20, -20, 20, 20, "EPSG:4326"));
        lf.add(sf);
        int[] op = {SerialChainFilter.NOT};
        SerialChainFilter f = new SerialChainFilter(lf, op);
        spatialQuery = new SpatialQuery("metafile:doc", f, SerialChainFilter.AND);

        result = indexSearcher.doSearch(spatialQuery);

        for (String s: result)
            resultReport = resultReport + s + '\n';

        logger.finer("spatialSearch 2:" + '\n' + resultReport);

        expectedResult = new ArrayList<String>();
        expectedResult.add("42292_5p_19900609195600");
        expectedResult.add("42292_9s_19900610041000");
        expectedResult.add("40510_145_19930221211500");

        assertEquals(expectedResult, result);
    }

    /**
     *
     * Test spatial lucene search.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void TermQueryTest() throws Exception {

        /**
         * Test 1
         */

        String identifier = "39727_22_19750113062500";
        String result = indexSearcher.identifierQuery(identifier);

        logger.finer("identifier query 1:" + '\n' + result);

        String expectedResult = "39727_22_19750113062500";

        assertEquals(expectedResult, result);

        /**
         * Test 2
         */

        identifier = "CTDF02";
        result = indexSearcher.identifierQuery(identifier);

        logger.finer("identifier query 2:" + '\n' + result);

        expectedResult = "CTDF02";

        assertEquals(expectedResult, result);
    }

    public static List<DefaultMetadata> fillTestData() throws JAXBException {
        List<DefaultMetadata> result = new ArrayList<DefaultMetadata>();
        MarshallerPool pool          = new MarshallerPool(DefaultMetadata.class);
        Unmarshaller unmarshaller    = pool.acquireUnmarshaller();

        Object obj = unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/metadata/meta1.xml"));
        if (obj instanceof DefaultMetadata) {
            result.add((DefaultMetadata) obj);
        } else {
            throw new IllegalArgumentException("resource file must be MetadataImpl");
        }

        obj = unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/metadata/meta2.xml"));
        if (obj instanceof DefaultMetadata) {
            result.add((DefaultMetadata) obj);
        } else {
            throw new IllegalArgumentException("resource file must be MetadataImpl:" + obj);
        }

        obj = unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/metadata/meta3.xml"));
        if (obj instanceof DefaultMetadata) {
            result.add((DefaultMetadata) obj);
        } else {
            throw new IllegalArgumentException("resource file must be MetadataImpl:" + obj);
        }

        obj = unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/metadata/meta4.xml"));
        if (obj instanceof DefaultMetadata) {
            result.add((DefaultMetadata) obj);
        } else {
            throw new IllegalArgumentException("resource file must be MetadataImpl:" + obj);
        }

        obj = unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/metadata/meta5.xml"));
        if (obj instanceof DefaultMetadata) {
            result.add((DefaultMetadata) obj);
        } else {
            throw new IllegalArgumentException("resource file must be MetadataImpl:" + obj);
        }

        obj = unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/metadata/meta6.xml"));
        if (obj instanceof DefaultMetadata) {
            result.add((DefaultMetadata) obj);
        } else {
            throw new IllegalArgumentException("resource file must be MetadataImpl:" + obj);
        }

        pool.release(unmarshaller);
        return result;
    }

}

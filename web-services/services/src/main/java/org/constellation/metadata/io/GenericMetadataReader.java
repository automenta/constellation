/*
 *    Constellation - An open source and standard compliant SDI
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

package org.constellation.metadata.io;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.namespace.QName;

import org.constellation.cat.csw.v202.ElementSetType;
import org.constellation.coverage.web.WebServiceException;
import org.constellation.generic.database.Automatic;
import org.geotools.metadata.iso.IdentifierImpl;
import static org.constellation.generic.database.Automatic.*;

import org.geotools.metadata.iso.MetaDataImpl;
import org.geotools.metadata.iso.citation.AddressImpl;
import org.geotools.metadata.iso.citation.CitationDateImpl;
import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.metadata.iso.citation.ContactImpl;
import org.geotools.metadata.iso.citation.OnLineResourceImpl;
import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.geotools.metadata.iso.citation.TelephoneImpl;
import org.geotools.metadata.iso.constraint.LegalConstraintsImpl;
import org.geotools.metadata.iso.distribution.DigitalTransferOptionsImpl;
import org.geotools.metadata.iso.distribution.DistributionImpl;
import org.geotools.metadata.iso.distribution.DistributorImpl;
import org.geotools.metadata.iso.distribution.FormatImpl;
import org.geotools.metadata.iso.extent.ExtentImpl;
import org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl;
import org.geotools.metadata.iso.extent.GeographicDescriptionImpl;
import org.geotools.metadata.iso.extent.GeographicExtentImpl;
import org.geotools.metadata.iso.extent.TemporalExtentImpl;
import org.geotools.metadata.iso.extent.VerticalExtentImpl;
import org.geotools.metadata.iso.identification.AggregateInformationImpl;
import org.geotools.metadata.iso.identification.BrowseGraphicImpl;
import org.geotools.metadata.iso.identification.DataIdentificationImpl;
import org.geotools.metadata.iso.identification.KeywordsImpl;
import org.geotools.metadata.iso.spatial.GeometricObjectsImpl;
import org.geotools.metadata.iso.spatial.VectorSpatialRepresentationImpl;
import org.geotools.util.SimpleInternationalString;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.identification.AssociationType;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.InitiativeType;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.util.InternationalString;

/**
 * 
 * TODO regarder les cardinalité est mettre des null la ou 0-...
 * TODO getVariables
 *
 * @author Guilhem Legal
 */
public class GenericMetadataReader extends MetadataReader {
    
    /**
     * A configuration object used in Generic database mode.
     */
    private Automatic genericConfiguration;
    
    private DateFormat dateFormat;
    
    
    public GenericMetadataReader(Automatic genericConfiguration) {
        super();
        this.genericConfiguration = genericConfiguration;
    }
    
    /**
     * Return a new Metadata object read from the database for the specified identifier.
     *  
     * @param identifier An unique identifier
     * @param mode An output schema mode: ISO_19115 and DUBLINCORE supported.
     * @param type An elementSet: FULL, SUMMARY and BRIEF. (implies elementName == null)
     * @param elementName A list of QName describing the requested fields. (implies type == null)
     * @return A metadata Object (dublin core Record / geotools metadata)
     * 
     * @throws java.sql.SQLException
     * @throws org.constellation.ows.v100.OWSWebServiceException
     */
    public Object getMetadata(String identifier, int mode, ElementSetType type, List<QName> elementName) throws SQLException, WebServiceException {
        Object result = null;
        
        if (mode == ISO_19115) {
            
            result = getMetadataObject(identifier, type, elementName);
            
        } else if (mode == DUBLINCORE) {
            
        } else {
            throw new IllegalArgumentException("Unknow or unAuthorized standard mode: " + mode);
        }
        return result;
    }
    
    private MetaDataImpl getMetadataObject(String identifier, ElementSetType type, List<QName> elementName) throws SQLException {
        MetaDataImpl result = null;
        
        //TODO we verify that the identifier exists
        
        switch (genericConfiguration.getType()) {
            
            case CDI: 
                result = getCDIMetadata(identifier);
                break;
                
            case CSR: 
                result = getCSRMetadata(identifier);
                break;
                
            case EDMED: 
                result = getEDMEDMetadata(identifier);
                break;
            
        }
        
        return result;
    }

    /**
     * Extract a metadata from a CDI database.
     * 
     * @param identifier
     * 
     * @return
     * @throws java.sql.SQLException
     */
    private MetaDataImpl getCDIMetadata(String identifier) throws SQLException {
        
        MetaDataImpl result     = new MetaDataImpl();
        
        /*
         * static part
         */
        result.setFileIdentifier(identifier);
        result.setLanguage(Locale.ENGLISH);
        result.setCharacterSet(CharacterSet.UTF_8);
        result.setHierarchyLevels(Arrays.asList(ScopeCode.DATASET));
        result.setHierarchyLevelNames(Arrays.asList("Common Data Index record"));
        /*
         * contact parts
         */
        ResponsibleParty contact   = createContact(getVariables("var01"), Role.AUTHOR);
        result.setContacts(Arrays.asList(contact));
        
        /*
         * creation date TODO
         */ 
        
        /*
         * Spatial representation info
         */  
        VectorSpatialRepresentationImpl spatialRep = new VectorSpatialRepresentationImpl();
        GeometricObjectsImpl geoObj = new GeometricObjectsImpl(GeometricObjectType.valueOf(getVariable("var02")));
        spatialRep.setGeometricObjects(Arrays.asList(geoObj));
        
        result.setSpatialRepresentationInfo(Arrays.asList(spatialRep));
        
        /*
         * Reference system info TODO (issues referencing unmarshallable)
         */ 
        
        /*
         * extension information TODO
         */
       
        /*
         * Data indentification
         */ 
        DataIdentificationImpl dataIdentification = new DataIdentificationImpl();
        
        CitationImpl citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(getVariable("var04")));
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString(getVariable("var05"))));
        
        CitationDateImpl revisionDate = createRevisionDate(getVariable("var06"));
        citation.setDates(Arrays.asList(revisionDate));
        
        contact   = createContact(getVariables("var07"), Role.ORIGINATOR);
        citation.setCitedResponsibleParties(Arrays.asList(contact));
        
        dataIdentification.setCitation(citation);
        
        dataIdentification.setAbstract(new SimpleInternationalString(getVariable("var08")));
        
        contact   = createContact(getVariables("var09"), Role.CUSTODIAN);
        
        dataIdentification.setPointOfContacts(Arrays.asList(contact));

        /*
         * keywords
         */  
        List<KeywordsImpl> keywords = new ArrayList<KeywordsImpl>();
        
        //parameter
        ResultSet res = getVariables("var10");
        KeywordsImpl keyword = new KeywordsImpl();
        List<InternationalString> kws = new ArrayList<InternationalString>();
        while (res.next()) {
            kws.add(new SimpleInternationalString(res.getString("TODO")));
        }
        keyword.setKeywords(kws);
        keyword.setType(KeywordType.valueOf("parameter"));

        citation = createKeywordCitation("BODC Parameter Discovery Vocabulary", "P021", "2007-09-25T02:00:02", "19");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);

        //instrument
        String key = getVariable("var11");
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(key)));
        keyword.setType(KeywordType.valueOf("instrument"));

        citation = createKeywordCitation("SeaDataNet device categories", "L05", "2007-09-06T15:03:00", "1");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        //platform
        key = getVariable("var12");
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(key)));
        keyword.setType(KeywordType.valueOf("platform_class"));

        citation = createKeywordCitation("SeaDataNet Platform Classes", "L061", "2007-01-13T06:42:58", "4");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        //projects
        res = getVariables("var13");
        keyword = new KeywordsImpl();
        kws = new ArrayList<InternationalString>();
        while (res.next()) {
            kws.add(new SimpleInternationalString(res.getString("TODO")));
        }
        keyword.setKeywords(kws);
        keyword.setType(KeywordType.valueOf("platform_class"));

        citation = createKeywordCitation("European Directory of Marine Environmental Research Projects", "EDMERP", null, null);
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        dataIdentification.setDescriptiveKeywords(keywords);

        /*
         * resource constraint
         */  
        res = getVariables("var14");
        LegalConstraintsImpl constraint = new LegalConstraintsImpl();
        while (res.next()) {
            constraint.setAccessConstraints(Arrays.asList(Restriction.valueOf(res.getString("TODO"))));
        }
        dataIdentification.setResourceConstraints(Arrays.asList(constraint));
        
        /*
         * Aggregate info
         */
        List<AggregateInformationImpl> aggregateInfos = new ArrayList<AggregateInformationImpl>();
        
        //cruise
        AggregateInformationImpl aggregateInfo = new AggregateInformationImpl();
        citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(getVariable("var15")));
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString(getVariable("var16"))));
        revisionDate = createRevisionDate(getVariable("var17"));
        citation.setDates(Arrays.asList(revisionDate));
        aggregateInfo.setAggregateDataSetName(citation);
        aggregateInfo.setInitiativeType(InitiativeType.CAMPAIGN);
        aggregateInfo.setAssociationType(AssociationType.LARGER_WORD_CITATION);
        aggregateInfos.add(aggregateInfo);
        
        //station
        aggregateInfo = new AggregateInformationImpl();
        citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(getVariable("var18")));
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString(getVariable("var19"))));
        revisionDate = createRevisionDate(getVariable("var20"));
        citation.setDates(Arrays.asList(revisionDate));
        aggregateInfo.setAggregateDataSetName(citation);
        aggregateInfo.setInitiativeType(InitiativeType.CAMPAIGN);
        aggregateInfo.setAssociationType(AssociationType.LARGER_WORD_CITATION);
        aggregateInfos.add(aggregateInfo);
        
        dataIdentification.setAggregationInfo(aggregateInfos);
        
        /*
         * data scale TODO
         */
        //dataIdentification.set ????
        
        //static part        
        dataIdentification.setLanguage(Arrays.asList(Locale.ENGLISH));
        dataIdentification.setTopicCategories(Arrays.asList(TopicCategory.OCEANS));
        
        /*
         * Extent TODO multiple box
         */
        ExtentImpl extent = new ExtentImpl();
        
        // geographic extent
        double west  = 0;double east  = 0;double south = 0;double north = 0;
        try {
            west  = Double.parseDouble(getVariable("var24"));
            east  = Double.parseDouble(getVariable("var25"));
            south = Double.parseDouble(getVariable("var26"));
            north = Double.parseDouble(getVariable("var27"));
        } catch (NumberFormatException ex) {
            logger.severe("Number format exception while parsing boundingBox");
        }
        GeographicExtentImpl geoExtent = new GeographicBoundingBoxImpl(west, east, south, north);
        extent.setGeographicElements(Arrays.asList(geoExtent));
        
        //temporal extent
        TemporalExtentImpl tempExtent = new TemporalExtentImpl();
        try {
            Date start = dateFormat.parse(getVariable("var28"));
            tempExtent.setStartTime(start);
            Date stop  = dateFormat.parse(getVariable("var29"));
            tempExtent.setEndTime(stop);
        } catch (ParseException ex) {
            logger.severe("parse exception while parsing temporal extent date");
        }
        extent.setTemporalElements(Arrays.asList(tempExtent));
        
        //vertical extent
        VerticalExtentImpl vertExtent = new VerticalExtentImpl();
        try{
            vertExtent.setMinimumValue(Double.parseDouble(getVariable("var30")));
            vertExtent.setMaximumValue(Double.parseDouble(getVariable("var31")));
        } catch (NumberFormatException ex) {
            logger.severe("Number format exception while parsing boundingBox");
        }
        // TODO DefaultVerticalCRS verticalCRS = new DefaultVerticalCRS(key, arg1, arg2)
        extent.setVerticalElements(Arrays.asList(vertExtent));
        
        dataIdentification.setExtent(Arrays.asList(extent));
        
        result.setIdentificationInfo(Arrays.asList(dataIdentification));
        
        /*
         * Distribution info
         */ 
        DistributionImpl distributionInfo = new DistributionImpl();
        
        //distributor
        DistributorImpl distributor       = new DistributorImpl();
        
        contact   = createContact(getVariables("var36"), Role.DISTRIBUTOR);
        distributor.setDistributorContact(contact);
                
        distributionInfo.setDistributors(Arrays.asList(distributor));
        
        //format
        List<Format> formats = new ArrayList<Format>();
        res = getVariables("var37", "var38");
        while (res.next()) {
            FormatImpl format = new FormatImpl();
            format.setName(new SimpleInternationalString(res.getString("TODO")));
            format.setVersion(new SimpleInternationalString(res.getString("TODO")));
            formats.add(format);
        }
        distributionInfo.setDistributionFormats(formats);
        
        //transfert options
        DigitalTransferOptionsImpl digiTrans = new DigitalTransferOptionsImpl();
        try {
            digiTrans.setTransferSize(Double.parseDouble(getVariable("var39")));
        } catch (NumberFormatException ex) {
            logger.severe("Number format exception while parsing transfer size");
        }
        OnLineResourceImpl onlines = new OnLineResourceImpl();
        try {
            onlines.setLinkage(new URI(getVariable("var40")));
        } catch (URISyntaxException ex) {
            logger.severe("URI Syntax exception in contact online resource");
        }
        onlines.setDescription(new SimpleInternationalString(getVariable("var41")));
        onlines.setFunction(OnLineFunction.DOWNLOAD);
        digiTrans.setOnLines(Arrays.asList(onlines));
        
        distributionInfo.setTransferOptions(Arrays.asList(digiTrans));
        
        result.setDistributionInfo(distributionInfo);
        
        return result;
    }
    
    /**
     * Extract a metadata from a CSR database.
     * 
     * @param identifier
     * 
     * @return
     * @throws java.sql.SQLException
     */
    private MetaDataImpl getCSRMetadata(String identifier) throws SQLException {
        
        MetaDataImpl result     = new MetaDataImpl();
        
        /*
         * static part
         */
        result.setFileIdentifier(identifier);
        result.setLanguage(Locale.ENGLISH);
        result.setCharacterSet(CharacterSet.UTF_8);
        result.setHierarchyLevels(Arrays.asList(ScopeCode.SERIES));
        result.setHierarchyLevelNames(Arrays.asList("Cruise Summary record"));
                
        /*
         * contact parts
         */
        ResponsiblePartyImpl contact   = createContact(getVariables("var01"), Role.AUTHOR);
        result.setContacts(Arrays.asList(contact));
        
        /*
         * creation date TODO
         */ 
        
        /*
         * extension information TODO
         */
        
        List<DataIdentificationImpl> dataIdentifications = new ArrayList<DataIdentificationImpl>();
        /*
         * Data indentification 1
         */ 
        DataIdentificationImpl dataIdentification = new DataIdentificationImpl();
        
        CitationImpl citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(getVariable("var02")));
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString(getVariable("var03"))));
        
        CitationDateImpl revisionDate = createRevisionDate(getVariable("var04"));
        citation.setDates(Arrays.asList(revisionDate));
        List<ResponsibleParty> chiefs = new ArrayList<ResponsibleParty>();
        
        //first chief
        contact   = createContact(getVariables("var05"), Role.ORIGINATOR);
        chiefs.add(contact);
        
        //second and other chief
        ResultSet res = getVariables("var06");
        while (res.next()) {
            contact   = createContact(res, Role.POINT_OF_CONTACT);
            chiefs.add(contact);
        }
        
        //labo
        res = getVariables("var07");
        while (res.next()) {
            contact   = createContact(res, Role.ORIGINATOR);
            chiefs.add(contact);
        }
        
        
        citation.setCitedResponsibleParties(chiefs);
        dataIdentification.setCitation(citation);
        
        dataIdentification.setPurpose(new SimpleInternationalString(getVariable("var08")));
        
        BrowseGraphicImpl graphOverview = new BrowseGraphicImpl();
        try {
            graphOverview.setFileName(new URI(getVariable("var09")));
        } catch (URISyntaxException ex) {
            logger.severe("URI Syntax exception in graph overview");
        }
        
        graphOverview.setFileDescription(new SimpleInternationalString(getVariable("var10")));
        graphOverview.setFileType(getVariable("var11"));
        
        dataIdentification.setGraphicOverviews(Arrays.asList(graphOverview));
        
        /*
         * keywords
         */  
        List<KeywordsImpl> keywords = new ArrayList<KeywordsImpl>();
        
        //port of departure
        KeywordsImpl keyword = new KeywordsImpl();
        
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var12"))));
        keyword.setType(KeywordType.valueOf("departure_place"));

        citation = createKeywordCitation("Ports Gazetteer", "C381", "2007-09-20T02:00:02", "2");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);

        //port of arrival
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var13"))));
        keyword.setType(KeywordType.valueOf("arrival_place"));

        citation = createKeywordCitation("Ports Gazetteer", "C381", "2007-09-20T02:00:02", "2");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        //country of departure
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var14"))));
        keyword.setType(KeywordType.valueOf("departure_contry"));

        citation = createKeywordCitation("International Standards Organisation countries", "C320", "2007-08-22T16:37:58", "1");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        // country of arrival
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var15"))));
        keyword.setType(KeywordType.valueOf("arrival_country"));

        citation =  createKeywordCitation("International Standards Organisation countries", "C320", "2007-08-22T16:37:58", "1");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        // ship
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var16"))));
        keyword.setType(KeywordType.valueOf("platform"));

        citation = createKeywordCitation("SeaDataNet Cruise Summary Report ship metadata", "C174", "2007-05-14T15:45:00", "0");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        // platform class
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var17"))));
        keyword.setType(KeywordType.valueOf("platform_class"));

        citation = createKeywordCitation("SeaDataNet Platform Classes", "L061", "2007-01-13T06:42:58", "4");
        keyword.setThesaurusName(citation);
        
        // projects
        res = getVariables("var18");
        keyword = new KeywordsImpl();
        List<InternationalString> kws = new ArrayList<InternationalString>();
        while (res.next()) {
            kws.add(new SimpleInternationalString(res.getString("TODO")));
        }
        keyword.setKeywords(kws);
        keyword.setType(KeywordType.valueOf("platform_class"));

        citation = createKeywordCitation("European Directory of Marine Environmental Research Projects", "EDMERP", null, null);
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        // general oceans area
        res = getVariables("var19");
        keyword = new KeywordsImpl();
        kws = new ArrayList<InternationalString>();
        while (res.next()) {
            kws.add(new SimpleInternationalString(res.getString("TODO")));
        }
        keyword.setKeywords(kws);
        keyword.setType(KeywordType.PLACE);

        citation = createKeywordCitation("SeaDataNet Sea Areas", "C16", "2007-03-01T12:00:00", "0");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        // geographic coverage
        res = getVariables("var20");
        keyword = new KeywordsImpl();
        kws = new ArrayList<InternationalString>();
        while (res.next()) {
            kws.add(new SimpleInternationalString(res.getString("TODO")));
        }
        keyword.setKeywords(kws);
        keyword.setType(KeywordType.valueOf("marsden_square"));

        citation = createKeywordCitation("Ten-degree Marsden Squares", "C371", "2007-08-03T02:00:02", "1");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
         //parameter
        res = getVariables("var20");
        keyword = new KeywordsImpl();
        kws = new ArrayList<InternationalString>();
        while (res.next()) {
            kws.add(new SimpleInternationalString(res.getString("TODO")));
        }
        keyword.setKeywords(kws);
        keyword.setType(KeywordType.valueOf("parameter"));

        citation = createKeywordCitation("BODC Parameter Discovery Vocabulary", "P021", "2007-09-25T02:00:02", "19");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);
        
        // instrument
        String key = getVariable("var21");
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(key)));
        keyword.setType(KeywordType.valueOf("instrument"));

        citation = createKeywordCitation("SeaDataNet device categories", "L05", "2007-03-01T08:16:13", "0");
        keyword.setThesaurusName(citation);

        keywords.add(keyword);

        dataIdentification.setDescriptiveKeywords(keywords);
        
        /*
         * resource constraint
         */  
        res = getVariables("var23");
        LegalConstraintsImpl constraint = new LegalConstraintsImpl();
        while (res.next()) {
            constraint.setAccessConstraints(Arrays.asList(Restriction.valueOf(res.getString("TODO"))));
        }
        dataIdentification.setResourceConstraints(Arrays.asList(constraint));
        
        //static part        
        dataIdentification.setLanguage(Arrays.asList(Locale.ENGLISH));
        dataIdentification.setTopicCategories(Arrays.asList(TopicCategory.OCEANS));
        
        /*
         * Extent 
         */
        ExtentImpl extent = new ExtentImpl();
        
        //temporal extent
        TemporalExtentImpl tempExtent = new TemporalExtentImpl();
        try {
            Date start = dateFormat.parse(getVariable("var24"));
            tempExtent.setStartTime(start);
            Date stop  = dateFormat.parse(getVariable("var25"));
            tempExtent.setEndTime(stop);
        } catch (ParseException ex) {
            logger.severe("parse exception while parsing temporal extent date");
        }
        extent.setTemporalElements(Arrays.asList(tempExtent));
        
        List<GeographicExtentImpl> geoElements = new ArrayList<GeographicExtentImpl>();
        //geographic areas
        res = getVariables("var26");
        while (res.next()) {
            IdentifierImpl id  = new IdentifierImpl(res.getString("TODO"));
            GeographicDescriptionImpl geoDesc = new GeographicDescriptionImpl();
            geoElements.add(geoDesc);
        }
        
        // geographic extent TODO multiple box
        double west  = 0;double east  = 0;double south = 0;double north = 0;
        try {
            west  = Double.parseDouble(getVariable("var27"));
            east  = Double.parseDouble(getVariable("var28"));
            south = Double.parseDouble(getVariable("var29"));
            north = Double.parseDouble(getVariable("var30"));
        } catch (NumberFormatException ex) {
            logger.severe("Number format exception while parsing boundingBox");
        }
        GeographicExtentImpl geoExtent = new GeographicBoundingBoxImpl(west, east, south, north);
        geoElements.add(geoExtent);
        extent.setGeographicElements(geoElements);
        
        dataIdentification.setExtent(Arrays.asList(extent));
        dataIdentifications.add(dataIdentification);
        
        /**
         * dataIdentification MOORING  todo multiple
         */
        dataIdentification = new DataIdentificationImpl();
        
        citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(getVariable("var31")));
        
        revisionDate = createRevisionDate(getVariable("var32"));
        citation.setDates(Arrays.asList(revisionDate));

        //principal investigator
        contact   = createContact(getVariables("var34"), Role.PRINCIPAL_INVESTIGATOR);
        contact.setIndividualName(getVariable("var33"));
        citation.setCitedResponsibleParties(Arrays.asList(contact));
        dataIdentification.setCitation(citation);
        
        dataIdentification.setAbstract(new SimpleInternationalString(getVariable("var35")));
        
        /*
         * Aggregate info
         */
        AggregateInformationImpl aggregateInfo = new AggregateInformationImpl();
        aggregateInfo.setInitiativeType(InitiativeType.PLATFORM);
        aggregateInfo.setAssociationType(AssociationType.LARGER_WORD_CITATION);
        
        dataIdentification.setAggregationInfo(Arrays.asList(aggregateInfo));
        
        dataIdentification.setLanguage(Arrays.asList(Locale.ENGLISH));
        dataIdentification.setTopicCategories(Arrays.asList(TopicCategory.OCEANS));
        
        /**
         * TODO extent with GM_point var36
         */
        
        dataIdentifications.add(dataIdentification);
        
        /**
         * data Identification : samples TODO multiple
         */
        dataIdentification = new DataIdentificationImpl();
        
        citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(getVariable("var37")));
        
        revisionDate = createRevisionDate(getVariable("var38"));
        citation.setDates(Arrays.asList(revisionDate));
        
        //principal investigator
        contact   = createContact(getVariables("var40"), Role.PRINCIPAL_INVESTIGATOR);
        contact.setIndividualName(getVariable("var39"));
        citation.setCitedResponsibleParties(Arrays.asList(contact));
        
        dataIdentification.setCitation(citation);
        
        dataIdentification.setAbstract(new SimpleInternationalString(getVariable("var41")));
        
        keyword = new KeywordsImpl();
        keyword.setKeywords(Arrays.asList(new SimpleInternationalString(getVariable("var42"))));
        keyword.setType(KeywordType.valueOf("counting_unit"));

        citation = createKeywordCitation("ROSCOP sample quantification units", "L181", "2007-06-29T13:23:00", "0");
        keyword.setThesaurusName(citation);

        aggregateInfo = new AggregateInformationImpl();
        aggregateInfo.setInitiativeType(InitiativeType.OPERATION);
        aggregateInfo.setAssociationType(AssociationType.LARGER_WORD_CITATION);
        
        dataIdentification.setAggregationInfo(Arrays.asList(aggregateInfo));
        
        dataIdentification.setLanguage(Arrays.asList(Locale.ENGLISH));
        dataIdentification.setTopicCategories(Arrays.asList(TopicCategory.OCEANS));
        
        dataIdentification.setSupplementalInformation(new SimpleInternationalString(getVariable("var13")));
        result.setIdentificationInfo(dataIdentifications);
        
        
        return result;
    }
    
     /**
     * Extract a metadata from a EDMED database.
     * 
     * @param identifier
     * 
     * @return
     * @throws java.sql.SQLException
     */
    private MetaDataImpl getEDMEDMetadata(String identifier) throws SQLException {
        MetaDataImpl result     = new MetaDataImpl();
        
        /*
         * static part
         */
        result.setFileIdentifier(identifier);
        result.setLanguage(Locale.ENGLISH);
        result.setCharacterSet(CharacterSet.UTF_8);
        result.setHierarchyLevels(Arrays.asList(ScopeCode.SERIES));
        result.setHierarchyLevelNames(Arrays.asList("EDMED record"));
        return result;
    }
    /**
     * TODO 
     * 
     * @param variables
     * @return
     */
    private ResultSet getVariables(String... variables) {
        return null;
    }
    
    /**
     * TODO
     * 
     * @param variable
     * @return
     */
    private String getVariable(String variable) {
        return null;
    }
    
    /**
     * Build a new Responsible party with the specified resultSet and Role.
     * 
     * @param res
     * @param role
     * @return
     * @throws java.sql.SQLException
     */
    private ResponsiblePartyImpl createContact(ResultSet res, Role role) throws SQLException {
        
        ResponsiblePartyImpl contact = new ResponsiblePartyImpl();
        contact.setOrganisationName(new SimpleInternationalString(res.getString("TODO")));
        contact.setRole(role);
        
        ContactImpl contactInfo = new ContactImpl();
        TelephoneImpl phone     = new TelephoneImpl();
        AddressImpl address     = new AddressImpl();
        OnLineResourceImpl or   = new OnLineResourceImpl();
                
        phone.setFacsimiles(Arrays.asList(res.getString("TODO")));
        phone.setVoices(Arrays.asList(res.getString("TODO")));
        contactInfo.setPhone(phone);
        
        address.setDeliveryPoints(Arrays.asList(res.getString("TODO")));
        address.setCity(new SimpleInternationalString(res.getString("TODO")));
        address.setAdministrativeArea(new SimpleInternationalString(res.getString("TODO")));
        address.setPostalCode(res.getString("TODO"));
        address.setCountry(new SimpleInternationalString(res.getString("TODO")));
        address.setElectronicMailAddresses(Arrays.asList(res.getString("TODO")));
        contactInfo.setAddress(address);
        
        try {
            or.setLinkage(new URI(res.getString("TODO")));
        } catch (URISyntaxException ex) {
            logger.severe("URI Syntax exception in contact online resource");
        }
        contactInfo.setOnLineResource(or);
        contact.setContactInfo(contactInfo);
        return contact;
    }
    
    /**
     * Parse the specified date and return a CitationDate with the dateType code REVISION.
     * 
     * @param date
     * @return
     */
    private CitationDateImpl createRevisionDate(String date) {
        CitationDateImpl revisionDate = new CitationDateImpl();
        try {
            Date d = dateFormat.parse(date);
            revisionDate.setDate(d);
            revisionDate.setDateType(DateType.REVISION);
        } catch (ParseException ex) {
            logger.severe("parse exception while parsing revision date");
            return null;
        }
        return revisionDate;
    }
    
    private CitationImpl createKeywordCitation(String title, String altTitle, String revDate, String editionNumber) {
        CitationImpl citation = new CitationImpl();
        citation.setTitle(new SimpleInternationalString(title));
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString(altTitle)));
        CitationDateImpl revisionDate;
        if (revDate != null) {
            revisionDate = createRevisionDate(revDate);
        } else {
            revisionDate = new CitationDateImpl(null, DateType.REVISION); 
        }
        citation.setDates(Arrays.asList(revisionDate));
        citation.setEdition(new SimpleInternationalString(editionNumber));
        citation.setIdentifiers(Arrays.asList(new IdentifierImpl("http://www.seadatanet.org/urnurl/")));
        return citation;
    }
}

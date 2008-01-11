/*
 * Sicade - SystÃ¨mes intÃ©grÃ©s de connaissances pour l'aide Ã  la dÃ©cision en environnement
 * (C) 2005, Institut de Recherche pour le DÃ©veloppement
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package net.seagis.wcs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Full description of one coverage available from a WCS instance. 
 * WCS version 1.0.0
 * 
 * <p>Java class for CoverageOfferingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageOfferingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs}CoverageOfferingBriefType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}domainSet"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}rangeSet"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}supportedCRSs"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}supportedFormats"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}supportedInterpolations" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageOfferingType")
public class CoverageOfferingType extends CoverageOfferingBriefType {
    
    private DomainSetType domainSet;
    
    private RangeSetType rangeSet;
    
    private SupportedCRSsType supportedCRSs;
    
    private SupportedFormatsType supportedFormats;
    
    private SupportedInterpolationsType supportedInterpolations;
    /**
     * An empty constructor used by JAXB.
     */
    CoverageOfferingType(){
    }
    
    /**
     * build a new CoverageOffering type used in describeCoverage response.
     */
    public CoverageOfferingType(List<MetadataLinkType> metadataLink, String name, String label, String description,
            LonLatEnvelopeType lonLatEnvelope, Keywords keywords, DomainSetType domainSet, RangeSetType rangeSet,
            SupportedCRSsType supportedCRSs, SupportedFormatsType supportedFormats, SupportedInterpolationsType supportedInterpolations){
        super(metadataLink, name, label, description, lonLatEnvelope, keywords);
        this.domainSet               = domainSet;
        this.rangeSet                = rangeSet;
        this.supportedCRSs           = supportedCRSs;
        this.supportedFormats        = supportedFormats;
        this.supportedInterpolations = supportedInterpolations;
        
    }

}

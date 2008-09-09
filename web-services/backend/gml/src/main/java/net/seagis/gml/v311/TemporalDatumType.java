/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
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
package net.seagis.gml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Defines the origin of a temporal coordinate reference system. 
 * This type extends the TemporalDatumRestrictionType to add the "origin" element with the dateTime type. 
 * 
 * <p>Java class for TemporalDatumType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TemporalDatumType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}TemporalDatumBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}origin"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalDatumType", propOrder = {
    "origin"
})
public class TemporalDatumType extends TemporalDatumBaseType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar origin;

    /**
     * Gets the value of the origin property.
     * 
     */
    public XMLGregorianCalendar getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     * 
     */
    public void setOrigin(XMLGregorianCalendar value) {
        this.origin = value;
    }

}

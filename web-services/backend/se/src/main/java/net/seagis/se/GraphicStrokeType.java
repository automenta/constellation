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
package net.seagis.se;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GraphicStrokeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GraphicStrokeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Graphic"/>
 *         &lt;element ref="{http://www.opengis.net/se}InitialGap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Gap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GraphicStrokeType", propOrder = {
    "graphic",
    "initialGap",
    "gap"
})
public class GraphicStrokeType {

    @XmlElement(name = "Graphic", required = true)
    private GraphicType graphic;
    @XmlElement(name = "InitialGap")
    private ParameterValueType initialGap;
    @XmlElement(name = "Gap")
    private ParameterValueType gap;

    /**
     * Empty Constructor used by JAXB.
     */
    GraphicStrokeType() {
        
    }
    
    /**
     * Build a new Graphic stroke.
     */
    public GraphicStrokeType(GraphicType graphic, ParameterValueType initialGap,
            ParameterValueType gap) {
        this.graphic    = graphic;
        this.initialGap = initialGap;
        this.gap        = gap;
    }
    
    /**
     * Gets the value of the graphic property.
     */
    public GraphicType getGraphic() {
        return graphic;
    }

    /**
     * Gets the value of the initialGap property.
     */
    public ParameterValueType getInitialGap() {
        return initialGap;
    }

    /**
     * Gets the value of the gap property.
     */
    public ParameterValueType getGap() {
        return gap;
    }
}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.01.07 at 03:26:15 PM CET 
//


package net.seagis.sld.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/sld}PointPlacement"/>
 *         &lt;element ref="{http://www.opengis.net/sld}LinePlacement"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pointPlacement",
    "linePlacement"
})
@XmlRootElement(name = "LabelPlacement")
public class LabelPlacement {

    @XmlElement(name = "PointPlacement")
    protected PointPlacement pointPlacement;
    @XmlElement(name = "LinePlacement")
    protected LinePlacement linePlacement;

    /**
     * Gets the value of the pointPlacement property.
     * 
     * @return
     *     possible object is
     *     {@link PointPlacement }
     *     
     */
    public PointPlacement getPointPlacement() {
        return pointPlacement;
    }

    /**
     * Sets the value of the pointPlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointPlacement }
     *     
     */
    public void setPointPlacement(PointPlacement value) {
        this.pointPlacement = value;
    }

    /**
     * Gets the value of the linePlacement property.
     * 
     * @return
     *     possible object is
     *     {@link LinePlacement }
     *     
     */
    public LinePlacement getLinePlacement() {
        return linePlacement;
    }

    /**
     * Sets the value of the linePlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinePlacement }
     *     
     */
    public void setLinePlacement(LinePlacement value) {
        this.linePlacement = value;
    }

}

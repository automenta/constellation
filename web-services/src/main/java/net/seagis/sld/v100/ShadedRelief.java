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
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sld}BrightnessOnly" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}ReliefFactor" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "brightnessOnly",
    "reliefFactor"
})
@XmlRootElement(name = "ShadedRelief")
public class ShadedRelief {

    @XmlElement(name = "BrightnessOnly")
    protected Boolean brightnessOnly;
    @XmlElement(name = "ReliefFactor")
    protected Double reliefFactor;

    /**
     * Gets the value of the brightnessOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBrightnessOnly() {
        return brightnessOnly;
    }

    /**
     * Sets the value of the brightnessOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBrightnessOnly(Boolean value) {
        this.brightnessOnly = value;
    }

    /**
     * Gets the value of the reliefFactor property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getReliefFactor() {
        return reliefFactor;
    }

    /**
     * Sets the value of the reliefFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setReliefFactor(Double value) {
        this.reliefFactor = value;
    }

}

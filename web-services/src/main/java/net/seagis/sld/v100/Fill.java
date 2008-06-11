//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.01.07 at 03:26:15 PM CET 
//


package net.seagis.sld.v100;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{http://www.opengis.net/sld}GraphicFill" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}CssParameter" maxOccurs="unbounded" minOccurs="0"/>
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
    "graphicFill",
    "cssParameter"
})
@XmlRootElement(name = "Fill")
public class Fill {

    @XmlElement(name = "GraphicFill")
    protected GraphicFill graphicFill;
    @XmlElement(name = "CssParameter")
    protected List<CssParameter> cssParameter;

    /**
     * Gets the value of the graphicFill property.
     * 
     * @return
     *     possible object is
     *     {@link GraphicFill }
     *     
     */
    public GraphicFill getGraphicFill() {
        return graphicFill;
    }

    /**
     * Sets the value of the graphicFill property.
     * 
     * @param value
     *     allowed object is
     *     {@link GraphicFill }
     *     
     */
    public void setGraphicFill(GraphicFill value) {
        this.graphicFill = value;
    }

    /**
     * Gets the value of the cssParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cssParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCssParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CssParameter }
     * 
     * 
     */
    public List<CssParameter> getCssParameter() {
        if (cssParameter == null) {
            cssParameter = new ArrayList<CssParameter>();
        }
        return this.cssParameter;
    }

}

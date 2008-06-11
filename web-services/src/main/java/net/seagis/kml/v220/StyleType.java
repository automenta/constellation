//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.06.10 at 06:06:35 PM CEST 
//


package net.seagis.kml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StyleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractStyleSelectorType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}IconStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LabelStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LineStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PolyStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}BalloonStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ListStyle" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}StyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}StyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StyleType", propOrder = {
    "iconStyle",
    "labelStyle",
    "lineStyle",
    "polyStyle",
    "balloonStyle",
    "listStyle",
    "styleSimpleExtensionGroup",
    "styleObjectExtensionGroup"
})
public class StyleType
    extends AbstractStyleSelectorType
{

    @XmlElement(name = "IconStyle")
    protected IconStyleType iconStyle;
    @XmlElement(name = "LabelStyle")
    protected LabelStyleType labelStyle;
    @XmlElement(name = "LineStyle")
    protected LineStyleType lineStyle;
    @XmlElement(name = "PolyStyle")
    protected PolyStyleType polyStyle;
    @XmlElement(name = "BalloonStyle")
    protected BalloonStyleType balloonStyle;
    @XmlElement(name = "ListStyle")
    protected ListStyleType listStyle;
    @XmlElement(name = "StyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> styleSimpleExtensionGroup;
    @XmlElement(name = "StyleObjectExtensionGroup")
    protected List<AbstractObjectType> styleObjectExtensionGroup;

    /**
     * Gets the value of the iconStyle property.
     * 
     * @return
     *     possible object is
     *     {@link IconStyleType }
     *     
     */
    public IconStyleType getIconStyle() {
        return iconStyle;
    }

    /**
     * Sets the value of the iconStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link IconStyleType }
     *     
     */
    public void setIconStyle(IconStyleType value) {
        this.iconStyle = value;
    }

    /**
     * Gets the value of the labelStyle property.
     * 
     * @return
     *     possible object is
     *     {@link LabelStyleType }
     *     
     */
    public LabelStyleType getLabelStyle() {
        return labelStyle;
    }

    /**
     * Sets the value of the labelStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelStyleType }
     *     
     */
    public void setLabelStyle(LabelStyleType value) {
        this.labelStyle = value;
    }

    /**
     * Gets the value of the lineStyle property.
     * 
     * @return
     *     possible object is
     *     {@link LineStyleType }
     *     
     */
    public LineStyleType getLineStyle() {
        return lineStyle;
    }

    /**
     * Sets the value of the lineStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineStyleType }
     *     
     */
    public void setLineStyle(LineStyleType value) {
        this.lineStyle = value;
    }

    /**
     * Gets the value of the polyStyle property.
     * 
     * @return
     *     possible object is
     *     {@link PolyStyleType }
     *     
     */
    public PolyStyleType getPolyStyle() {
        return polyStyle;
    }

    /**
     * Sets the value of the polyStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolyStyleType }
     *     
     */
    public void setPolyStyle(PolyStyleType value) {
        this.polyStyle = value;
    }

    /**
     * Gets the value of the balloonStyle property.
     * 
     * @return
     *     possible object is
     *     {@link BalloonStyleType }
     *     
     */
    public BalloonStyleType getBalloonStyle() {
        return balloonStyle;
    }

    /**
     * Sets the value of the balloonStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BalloonStyleType }
     *     
     */
    public void setBalloonStyle(BalloonStyleType value) {
        this.balloonStyle = value;
    }

    /**
     * Gets the value of the listStyle property.
     * 
     * @return
     *     possible object is
     *     {@link ListStyleType }
     *     
     */
    public ListStyleType getListStyle() {
        return listStyle;
    }

    /**
     * Sets the value of the listStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListStyleType }
     *     
     */
    public void setListStyle(ListStyleType value) {
        this.listStyle = value;
    }

    /**
     * Gets the value of the styleSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the styleSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getStyleSimpleExtensionGroup() {
        if (styleSimpleExtensionGroup == null) {
            styleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.styleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the styleObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the styleObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyleObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getStyleObjectExtensionGroup() {
        if (styleObjectExtensionGroup == null) {
            styleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.styleObjectExtensionGroup;
    }

}

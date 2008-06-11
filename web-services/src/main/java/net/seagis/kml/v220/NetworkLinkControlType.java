//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.06.10 at 06:06:35 PM CEST 
//


package net.seagis.kml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkLinkControlType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetworkLinkControlType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}minRefreshPeriod" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxSessionLength" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}cookie" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}message" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}linkName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}linkDescription" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}linkSnippet" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}expires" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Update" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractViewGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}NetworkLinkControlSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}NetworkLinkControlObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkLinkControlType", propOrder = {
    "minRefreshPeriod",
    "maxSessionLength",
    "cookie",
    "message",
    "linkName",
    "linkDescription",
    "linkSnippet",
    "expires",
    "update",
    "abstractViewGroup",
    "networkLinkControlSimpleExtensionGroup",
    "networkLinkControlObjectExtensionGroup"
})
public class NetworkLinkControlType {

    @XmlElement(defaultValue = "0.0")
    protected Double minRefreshPeriod;
    @XmlElement(defaultValue = "-1.0")
    protected Double maxSessionLength;
    protected String cookie;
    protected String message;
    protected String linkName;
    protected String linkDescription;
    protected SnippetType linkSnippet;
    protected String expires;
    @XmlElement(name = "Update")
    protected UpdateType update;
    @XmlElementRef(name = "AbstractViewGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class)
    protected JAXBElement<? extends AbstractViewType> abstractViewGroup;
    @XmlElement(name = "NetworkLinkControlSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> networkLinkControlSimpleExtensionGroup;
    @XmlElement(name = "NetworkLinkControlObjectExtensionGroup")
    protected List<AbstractObjectType> networkLinkControlObjectExtensionGroup;

    /**
     * Gets the value of the minRefreshPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinRefreshPeriod() {
        return minRefreshPeriod;
    }

    /**
     * Sets the value of the minRefreshPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinRefreshPeriod(Double value) {
        this.minRefreshPeriod = value;
    }

    /**
     * Gets the value of the maxSessionLength property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMaxSessionLength() {
        return maxSessionLength;
    }

    /**
     * Sets the value of the maxSessionLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMaxSessionLength(Double value) {
        this.maxSessionLength = value;
    }

    /**
     * Gets the value of the cookie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * Sets the value of the cookie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCookie(String value) {
        this.cookie = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the linkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * Sets the value of the linkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkName(String value) {
        this.linkName = value;
    }

    /**
     * Gets the value of the linkDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkDescription() {
        return linkDescription;
    }

    /**
     * Sets the value of the linkDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkDescription(String value) {
        this.linkDescription = value;
    }

    /**
     * Gets the value of the linkSnippet property.
     * 
     * @return
     *     possible object is
     *     {@link SnippetType }
     *     
     */
    public SnippetType getLinkSnippet() {
        return linkSnippet;
    }

    /**
     * Sets the value of the linkSnippet property.
     * 
     * @param value
     *     allowed object is
     *     {@link SnippetType }
     *     
     */
    public void setLinkSnippet(SnippetType value) {
        this.linkSnippet = value;
    }

    /**
     * Gets the value of the expires property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpires() {
        return expires;
    }

    /**
     * Sets the value of the expires property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpires(String value) {
        this.expires = value;
    }

    /**
     * Gets the value of the update property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateType }
     *     
     */
    public UpdateType getUpdate() {
        return update;
    }

    /**
     * Sets the value of the update property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateType }
     *     
     */
    public void setUpdate(UpdateType value) {
        this.update = value;
    }

    /**
     * Gets the value of the abstractViewGroup property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractViewType> getAbstractViewGroup() {
        return abstractViewGroup;
    }

    /**
     * Sets the value of the abstractViewGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     
     */
    public void setAbstractViewGroup(JAXBElement<? extends AbstractViewType> value) {
        this.abstractViewGroup = ((JAXBElement<? extends AbstractViewType> ) value);
    }

    /**
     * Gets the value of the networkLinkControlSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkLinkControlSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkLinkControlSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getNetworkLinkControlSimpleExtensionGroup() {
        if (networkLinkControlSimpleExtensionGroup == null) {
            networkLinkControlSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.networkLinkControlSimpleExtensionGroup;
    }

    /**
     * Gets the value of the networkLinkControlObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkLinkControlObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkLinkControlObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getNetworkLinkControlObjectExtensionGroup() {
        if (networkLinkControlObjectExtensionGroup == null) {
            networkLinkControlObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.networkLinkControlObjectExtensionGroup;
    }

}

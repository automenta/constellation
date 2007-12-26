//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.11 at 08:02:10 PM CET 
//


package net.seagis.wcs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import net.seagis.gml.AbstractCoordinateOperationType;
import net.seagis.gml.PolygonType;
import net.seagis.ows.BoundingBoxType;
import net.seagis.ows.WGS84BoundingBoxType;


/**
 * Definition of the spatial domain of a coverage. 
 * 
 * <p>Java class for SpatialDomainType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpatialDomainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}BoundingBox" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/1.1.1}GridCRS" minOccurs="0"/>
 *         &lt;element name="Transformation" type="{http://www.opengis.net/gml}AbstractCoordinateOperationType" minOccurs="0"/>
 *         &lt;element name="ImageCRS" type="{http://www.opengis.net/wcs/1.1.1}ImageCRSRefType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Polygon" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialDomainType", propOrder = {
    "boundingBox",
    "gridCRS",
    "transformation",
    "imageCRS",
    "polygon"
})
public class SpatialDomainType {

    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class)
    protected List<JAXBElement<? extends BoundingBoxType>> boundingBox;
    @XmlElement(name = "GridCRS")
    protected GridCrsType gridCRS;
    @XmlElement(name = "Transformation")
    protected AbstractCoordinateOperationType transformation;
    @XmlElement(name = "ImageCRS")
    protected ImageCRSRefType imageCRS;
    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    protected List<PolygonType> polygon;

    /**
     * The first bounding box shall exactly specify the spatial domain of the offered coverage in the CRS of that offered coverage, thus specifying the available grid row and column indices. For a georectified coverage (that has a GridCRS), this bounding box shall specify the spatial domain in that GridCRS. For an image that is not georectified, this bounding box shall specify the spatial domain in the ImageCRS of that image, whether or not that image is georeferenced. 
     * 					Additional bounding boxes, if any, shall specify the spatial domain in other CRSs. One bounding box could simply duplicate the information in the ows:WGS84BoundingBox; but the intent is to describe the spatial domain in more detail (e.g., in several different CRSs, or several rectangular areas instead of one overall bounding box). Multiple bounding boxes with the same CRS shall be interpreted as an unordered list of bounding boxes whose union covers spatial domain of this coverage. Notice that WCS use of this BoundingBox is further specified in specification Subclause 7.5. Gets the value of the boundingBox property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boundingBox property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoundingBox().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     * {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        }
        return this.boundingBox;
    }

    /**
     * Definition of GridCRS of the stored coverage. This GridCRS shall be included when this coverage is georectified and is thus stored in a GridCRS. This GridCRS applies to this offered coverage, and specifies its spatial resolution. The definition is included to inform clients of this GridCRS, for possible use in a GetCoverage operation request. 
     * 
     * @return
     *     possible object is
     *     {@link GridCrsType }
     *     
     */
    public GridCrsType getGridCRS() {
        return gridCRS;
    }

    /**
     * Sets the value of the gridCRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link GridCrsType }
     *     
     */
    public void setGridCRS(GridCrsType value) {
        this.gridCRS = value;
    }

    /**
     * Gets the value of the transformation property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractCoordinateOperationType }
     *     
     */
    public AbstractCoordinateOperationType getTransformation() {
        return transformation;
    }

    /**
     * Sets the value of the transformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractCoordinateOperationType }
     *     
     */
    public void setTransformation(AbstractCoordinateOperationType value) {
        this.transformation = value;
    }

    /**
     * Gets the value of the imageCRS property.
     * 
     * @return
     *     possible object is
     *     {@link ImageCRSRefType }
     *     
     */
    public ImageCRSRefType getImageCRS() {
        return imageCRS;
    }

    /**
     * Sets the value of the imageCRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImageCRSRefType }
     *     
     */
    public void setImageCRS(ImageCRSRefType value) {
        this.imageCRS = value;
    }

    /**
     * Unordered list of polygons whose union (combined areas) covers the spatial domain of this coverage. Polygons are particularly useful for areas that are poorly approximated by a BoundingBox (such as satellite image swaths, island groups, other non-convex areas). Gets the value of the polygon property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polygon property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolygon().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolygonType }
     * 
     * 
     */
    public List<PolygonType> getPolygon() {
        if (polygon == null) {
            polygon = new ArrayList<PolygonType>();
        }
        return this.polygon;
    }

}

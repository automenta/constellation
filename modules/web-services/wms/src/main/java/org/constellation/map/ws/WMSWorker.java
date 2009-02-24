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
package org.constellation.map.ws;

//J2SE dependencies
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//Constellation dependencies
import org.constellation.Cstl;
import org.constellation.ServiceDef;
import org.constellation.catalog.CatalogException;
import org.constellation.map.ws.rs.CSVGraphicVisitor;
import org.constellation.map.ws.rs.GMLGraphicVisitor;
import org.constellation.map.ws.rs.HTMLGraphicVisitor;
import org.constellation.map.ws.rs.TextGraphicVisitor;
import org.constellation.portrayal.Portrayal;
import org.constellation.provider.LayerDetails;
import org.constellation.query.wms.DescribeLayer;
import org.constellation.query.wms.GetCapabilities;
import org.constellation.query.wms.GetFeatureInfo;
import org.constellation.query.wms.GetLegendGraphic;
import org.constellation.query.wms.GetMap;
import org.constellation.query.wms.WMSQuery;
import org.constellation.register.RegisterException;
import org.constellation.util.PeriodUtilities;
import org.constellation.util.Util;
import org.constellation.wms.AbstractDCP;
import org.constellation.wms.AbstractDimension;
import org.constellation.wms.AbstractHTTP;
import org.constellation.wms.AbstractLayer;
import org.constellation.wms.AbstractOperation;
import org.constellation.wms.AbstractProtocol;
import org.constellation.wms.AbstractRequest;
import org.constellation.wms.AbstractWMSCapabilities;
import org.constellation.wms.v111.LatLonBoundingBox;
import org.constellation.wms.v130.EXGeographicBoundingBox;
import org.constellation.wms.v130.OperationType;
import org.constellation.ws.ServiceType;
import org.constellation.ws.ServiceVersion;
import org.constellation.ws.CstlServiceException;
import org.constellation.ws.rs.WebService;

//Geotools dependencies
import org.geotools.display.exception.PortrayalException;
import org.geotools.display.canvas.AbstractGraphicVisitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.internal.jaxb.v110.se.OnlineResourceType;
import org.geotools.internal.jaxb.v110.sld.DescribeLayerResponseType;
import org.geotools.internal.jaxb.v110.sld.LayerDescriptionType;
import org.geotools.internal.jaxb.v110.sld.TypeNameType;
import org.geotools.sld.MutableLayer;
import org.geotools.sld.MutableLayerStyle;
import org.geotools.sld.MutableNamedLayer;
import org.geotools.sld.MutableNamedStyle;
import org.geotools.sld.MutableStyledLayerDescriptor;
import org.geotools.style.MutableStyle;
import org.geotools.util.MeasurementRange;

//Geoapi dependencies
import org.opengis.metadata.extent.GeographicBoundingBox;

import static org.constellation.ws.ExceptionCode.*;
import static org.constellation.query.wms.WMSQuery.*;


/**
 * A WMS worker for a local WMS service which handles requests from either REST 
 * or SOAP facades and issues appropriate responses.
 * <p>
 * The classes implementing the REST or SOAP facades to this service will have 
 * processed the requests sufficiently to ensure that all the information 
 * conveyed by the HTTP request is either in the method call parameters or is 
 * in one of the fields of the parent class which holds instances of the 
 * injectible interface {@code Context} objects created by the JEE container.
 * </p>
 *
 * @version $Id$
 * 
 * @author Cédric Briançon (Geomatys)
 * @since 0.3
 */
public class WMSWorker extends AbstractWMSWorker {
    
    /**
     * The default debugging logger for the WMS service.
     */
    private static final Logger LOGGER = Logger.getLogger("org.constellation.map.ws");

    /**
     * A map containing the Capabilities Object already loaded from file.
     */
    private Map<String,Object> capabilities = new HashMap<String,Object>();

    /**
     * The web service marshaller, which will use the web service name space.
     */
    @SuppressWarnings("unused")
    private final Marshaller marshaller;
    
    private ServiceVersion actingVersion;

    /**
     * The web service unmarshaller, which will use the web service name space.
     */
    private final Unmarshaller unmarshaller;

    public WMSWorker(final Marshaller marshaller, final Unmarshaller unmarshaller, ServiceVersion actingVersion) {
        this.marshaller   = marshaller;
        this.unmarshaller = unmarshaller;
        this.actingVersion = actingVersion;
    }

    /**
     * Return a description of layers specified in the user's request.
     * 
     * TODO: Does this actually do anything? why does this never access LayerDetails?
     * TODO: Is this broken?
     *
     * @param descLayer The {@linkplain DescribeLayer describe layer} request.
     *
     * @throws CstlServiceException
     */
    @Override
    public DescribeLayerResponseType describeLayer(final DescribeLayer descLayer) throws CstlServiceException {
        this.actingVersion = descLayer.getVersion();
        final OnlineResourceType or = new OnlineResourceType();
        or.setHref(uriContext.getBaseUri().toString() + "wcs?");

        final List<LayerDescriptionType> layerDescriptions = new ArrayList<LayerDescriptionType>();
        final List<String> layers = descLayer.getLayers();
        for (String layer : layers) {
            final TypeNameType t = new TypeNameType(layer.trim());
            final LayerDescriptionType outputLayer = new LayerDescriptionType(or, t);
            layerDescriptions.add(outputLayer);
        }
        return new DescribeLayerResponseType("1.1.0", layerDescriptions);
    }

    /**
     * Describe the capabilities and the layers available of this service.
     *
     * @param getCapab       The {@linkplain GetCapabilities get capabilities} request.
     * @return a WMSCapabilities XML document describing the capabilities of the service.
     *
     * @throws CstlServiceException
     */
    @Override
    public AbstractWMSCapabilities getCapabilities(final GetCapabilities getCapab) throws CstlServiceException {
        
        final ServiceVersion queryVersion = getCapab.getVersion();
        this.actingVersion = queryVersion;
        
        //Add accepted CRS codes
        final List<String> crs = new ArrayList<String>();
        crs.add("EPSG:4326");  
        crs.add("CRS:84");     
        crs.add("EPSG:3395");
        crs.add("EPSG:27571"); 
        crs.add("EPSG:27572"); 
        crs.add("EPSG:27573"); 
        crs.add("EPSG:27574");
        
        
        //Generate the correct URL in the static part. ?TODO: clarify this.
        final AbstractWMSCapabilities inCapabilities;
        try {
            inCapabilities = (AbstractWMSCapabilities) getStaticCapabilitiesObject(
                    servletContext.getRealPath("WEB-INF"));
        } catch (IOException e) {
            throw new CstlServiceException("IO exception while getting Services Metadata:" +
                    e.getMessage(), NO_APPLICABLE_CODE, getCapab.getVersion());
        } catch (JAXBException ex) {
            throw new CstlServiceException("IO exception while getting Services Metadata:" +
                    ex.getMessage(), NO_APPLICABLE_CODE, getCapab.getVersion());
        }
        final String url = uriContext.getBaseUri().toString();
        inCapabilities.getService().getOnlineResource().setHref(url + "wms");
        final AbstractRequest request = inCapabilities.getCapability().getRequest();

        updateURL(request.getGetCapabilities().getDCPType(), url);
        updateURL(request.getGetFeatureInfo().getDCPType(), url);
        updateURL(request.getGetMap().getDCPType(), url);
        updateExtendedOperationURL(request, queryVersion, url);
        
        
        
//        /* ****************************************************************** * 
//         *   TODO: make this call Cstl.*
//         * ****************************************************************** */
////        final List<LayerDetails> layerRefs = Cstl.REGISTER.getLayerReferencesForWMS();
//        final NamedLayerDP dp = NamedLayerDP.getInstance();
//        final Set<String> keys = dp.getKeys();
//        final List<LayerDetails> layerRefs = new ArrayList<LayerDetails>();
//        for (String key : keys) {
//            final LayerDetails layer = dp.get(key);
//            if (layer == null) {
//                LOGGER.warning("Missing layer : " + key);
//                continue;
//            }
//            if (!layer.isQueryable(ServiceType.WMS)) {
//                LOGGER.info("layer" + layer.getName() + " not queryable by WMS");
//                continue;
//            }
//            layerRefs.add(layer);
//        }
//        /* ****************************************************************** * 
//         *   TODO: make this call Cstl.                                       *
//         * ****************************************************************** */
        final List<LayerDetails> layerRefs = getAllLayerReferences();

        //Build the list of layers
        final List<AbstractLayer> layers = new ArrayList<AbstractLayer>();
        for (LayerDetails layer : layerRefs){
            /*
             *  TODO
             * code = CRS.lookupEpsgCode(inputLayer.getCoverageReference().getCoordinateReferenceSystem(), false);
             */
            final GeographicBoundingBox inputGeoBox;
            try {
                inputGeoBox = layer.getGeographicBoundingBox();
            } catch (CatalogException exception) {
                throw new CstlServiceException(exception, NO_APPLICABLE_CODE, queryVersion);
            }

            // List of elevations, times and dim_range values.
            final List<AbstractDimension> dimensions = new ArrayList<AbstractDimension>();

            //the available date
            String defaut = null;
            AbstractDimension dim;
            SortedSet<Date> dates = null;
            try {
                dates = layer.getAvailableTimes();
            } catch (CatalogException ex) {
                LOGGER.log(Level.INFO, "Error retrieving dates values for the layer :"+ layer.getName(), ex);
                dates = null;
            }
            if (dates != null && !(dates.isEmpty())) {
                final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                final PeriodUtilities periodFormatter = new PeriodUtilities(df);
                defaut = df.format(dates.last());
                dim = (queryVersion.toString().equals("1.1.1")) ?
                    new org.constellation.wms.v111.Dimension("time", "ISO8601", defaut, null) :
                    new org.constellation.wms.v130.Dimension("time", "ISO8601", defaut, null);
                dim.setValue(periodFormatter.getDatesRespresentation(dates));
                dimensions.add(dim);
            }

            //the available elevation
            defaut = null;
            SortedSet<Number> elevations = null;
            try {
                elevations = layer.getAvailableElevations();
            } catch (CatalogException ex) {
                LOGGER.log(Level.INFO, "Error retrieving elevation values for the layer :"+ layer.getName(), ex);
                elevations = null;
            }
            if (elevations != null && !(elevations.isEmpty())) {
                defaut = elevations.first().toString();
                dim = (queryVersion.toString().equals("1.1.1")) ?
                    new org.constellation.wms.v111.Dimension("elevation", "EPSG:5030", defaut, null) :
                    new org.constellation.wms.v130.Dimension("elevation", "EPSG:5030", defaut, null);
                final StringBuilder elevs = new StringBuilder();
                for (Iterator<Number> it = elevations.iterator(); it.hasNext();) {
                    final Number n = it.next();
                    elevs.append(n.toString());
                    if (it.hasNext()) {
                        elevs.append(',');
                    }
                }
                dim.setValue(elevs.toString());
                dimensions.add(dim);
            }

            //the dimension range
            defaut = null;
            final MeasurementRange<?>[] ranges = layer.getSampleValueRanges();
            /* If the layer has only one sample dimension, then we can apply the dim_range
             * parameter. Otherwise it can be a multiple sample dimensions layer, and we
             * don't apply the dim_range.
             */
            if (ranges != null && ranges.length == 1 && ranges[0] != null) {
                final MeasurementRange<?> firstRange = ranges[0];
                final double minRange = firstRange.getMinimum();
                final double maxRange = firstRange.getMaximum();
                defaut = minRange + "," + maxRange;
                final Unit<?> u = firstRange.getUnits();
                final String unit = (u != null) ? u.toString() : null;
                dim = (queryVersion.toString().equals("1.1.1")) ?
                    new org.constellation.wms.v111.Dimension("dim_range", unit, defaut,
                                                           minRange + "," + maxRange) :
                    new org.constellation.wms.v130.Dimension("dim_range", unit, defaut,
                                                           minRange + "," + maxRange);
                dimensions.add(dim);
            }

            // LegendUrl generation
            //TODO: Use a StringBuilder or two
            final String layerName = layer.getName();
            final String beginLegendUrl = url + "wms?REQUEST=GetLegendGraphic&" +
                                                    "VERSION=1.1.0&" +
                                                    "FORMAT=";
            final String legendUrlGif = beginLegendUrl + IMAGE_GIF + "&LAYER=" + layerName;
            final String legendUrlPng = beginLegendUrl + IMAGE_PNG + "&LAYER=" + layerName;
            final int queryable = (layer.isQueryable(ServiceType.GETINFO) == true) ? 1 : 0;
            final AbstractLayer outputLayer;
            if (queryVersion.toString().equals("1.1.1")) {
                /*
                 * TODO
                 * Envelope inputBox = inputLayer.getCoverage().getEnvelope();
                 */
                final org.constellation.wms.v111.BoundingBox outputBBox = (inputGeoBox != null) ?
                    new org.constellation.wms.v111.BoundingBox("EPSG:4326",
                            inputGeoBox.getWestBoundLongitude(),
                            inputGeoBox.getSouthBoundLatitude(), inputGeoBox.getEastBoundLongitude(),
                            inputGeoBox.getNorthBoundLatitude(), 0.0, 0.0, queryVersion.toString()) :
                    null;

                // we build The Style part
                org.constellation.wms.v111.OnlineResource or =
                        new org.constellation.wms.v111.OnlineResource(legendUrlPng);
                org.constellation.wms.v111.LegendURL legendURL1 =
                        new org.constellation.wms.v111.LegendURL(IMAGE_PNG, or);

                or = new org.constellation.wms.v111.OnlineResource(legendUrlGif);
                org.constellation.wms.v111.LegendURL legendURL2 =
                        new org.constellation.wms.v111.LegendURL(IMAGE_GIF, or);

                List<String> stylesName = layer.getFavoriteStyles();
                List<org.constellation.wms.v111.Style> styles = new ArrayList<org.constellation.wms.v111.Style>();
                if (stylesName != null && stylesName.size() != 0) {
                    for (String styleName : stylesName) {
                        org.constellation.wms.v111.Style style = new org.constellation.wms.v111.Style(
                                styleName, styleName, null, null, null, legendURL1, legendURL2);
                        styles.add(style);
                    }
                } else {
                    org.constellation.wms.v111.Style style = new org.constellation.wms.v111.Style(
                                "Style1", "defaultStyle", null, null, null, legendURL1, legendURL2);
                    styles.add(style);
                }

                //we build the complete layer object
                outputLayer = new org.constellation.wms.v111.Layer(layerName,
                        Util.cleanSpecialCharacter(layer.getRemarks()),
                        Util.cleanSpecialCharacter(layer.getThematic()), crs,
                        new LatLonBoundingBox(inputGeoBox.getWestBoundLongitude(),
                                              inputGeoBox.getSouthBoundLatitude(),
                                              inputGeoBox.getEastBoundLongitude(),
                                              inputGeoBox.getNorthBoundLatitude()),
                        outputBBox, queryable, dimensions, styles);
            } else {
                /*
                 * TODO
                 * Envelope inputBox = inputLayer.getCoverage().getEnvelope();
                 */
                final org.constellation.wms.v130.BoundingBox outputBBox = (inputGeoBox != null) ?
                    new org.constellation.wms.v130.BoundingBox("EPSG:4326",
                            inputGeoBox.getWestBoundLongitude(),
                            inputGeoBox.getSouthBoundLatitude(),
                            inputGeoBox.getEastBoundLongitude(),
                            inputGeoBox.getNorthBoundLatitude(), 0.0, 0.0,
                            queryVersion.toString()) :
                    null;

                // we build a Style Object
                org.constellation.wms.v130.OnlineResource or =
                        new org.constellation.wms.v130.OnlineResource(legendUrlPng);
                org.constellation.wms.v130.LegendURL legendURL1 =
                        new org.constellation.wms.v130.LegendURL(IMAGE_PNG, or);

                or = new org.constellation.wms.v130.OnlineResource(legendUrlGif);
                org.constellation.wms.v130.LegendURL legendURL2 =
                        new org.constellation.wms.v130.LegendURL(IMAGE_GIF, or);

                List<String> stylesName = layer.getFavoriteStyles();
                List<org.constellation.wms.v130.Style> styles = new ArrayList<org.constellation.wms.v130.Style>();
                if (stylesName != null && stylesName.size() != 0) {
                    for (String styleName : stylesName) {
                        org.constellation.wms.v130.Style style = new org.constellation.wms.v130.Style(
                        styleName, styleName, null, null, null, legendURL1, legendURL2);
                        styles.add(style);
                    }
                } else {
                    org.constellation.wms.v130.Style style = new org.constellation.wms.v130.Style(
                        "Style1", "default Style", null, null, null, legendURL1, legendURL2);
                    styles.add(style);
                }

                outputLayer = new org.constellation.wms.v130.Layer(layerName,
                        Util.cleanSpecialCharacter(layer.getRemarks()),
                        Util.cleanSpecialCharacter(layer.getThematic()), crs,
                        new EXGeographicBoundingBox(inputGeoBox.getWestBoundLongitude(),
                                                    inputGeoBox.getSouthBoundLatitude(),
                                                    inputGeoBox.getEastBoundLongitude(),
                                                    inputGeoBox.getNorthBoundLatitude()),
                        outputBBox, queryable, dimensions, styles);
            }
            layers.add(outputLayer);
        }

        //we build the general layer and add it to the document
        final AbstractLayer mainLayer = (queryVersion.toString().equals("1.1.1")) ?
            new org.constellation.wms.v111.Layer("Constellation Web Map Layer",
                    "description of the service(need to be fill)", crs, null, layers) :
            new org.constellation.wms.v130.Layer("Constellation Web Map Layer",
                    "description of the service(need to be fill)", crs, null, layers);

        inCapabilities.getCapability().setLayer(mainLayer);
        return inCapabilities;
    }

    /**
     * Returns the file where to read the capabilities document for each service.
     * If no such file is found, then this method returns {@code null}.
     *
     * @param home    The home directory, where to search for configuration files.
     * @return The capabilities Object, or {@code null} if none.
     *
     * @throws JAXBException
     * @throws IOException
     */
    private Object getStaticCapabilitiesObject(final String home) throws JAXBException, IOException {
        final String fileName = "WMSCapabilities" + actingVersion.toString() + ".xml";
        final File changeFile = getFile("change.properties", home);
        Properties p = new Properties();

        // if the flag file is present we load the properties
        if (changeFile != null && changeFile.exists()) {
            FileInputStream in = new FileInputStream(changeFile);
            p.load(in);
            in.close();
        } else {
            p.put("update", "false");
        }

        //Look if the template capabilities is already in cache.
        Object response = capabilities.get(fileName);
        boolean update = p.getProperty("update").equals("true");

        if (response == null || update) {
            if (update) {
                LOGGER.info("updating metadata");
            }

            File f = getFile(fileName, home);
            response = unmarshaller.unmarshal(f);
            capabilities.put(fileName, response);
            //this.setLastUpdateSequence(System.currentTimeMillis());
            p.put("update", "false");

            // if the flag file is present we store the properties
            if (changeFile != null && changeFile.exists()) {
                FileOutputStream out = new FileOutputStream(changeFile);
                p.store(out, "updated from WebService");
                out.close();
            }
        }

        return response;
    }

    /**
     * Return a file located in the home directory. In this implementation, it should be
     * the WEB-INF directory of the deployed service.
     *
     * @param fileName The name of the file requested.
     * @return The specified file.
     */
    private File getFile(final String fileName, final String home) {
         File path;
         if (home == null || !(path = new File(home)).isDirectory()) {
            path = WebService.getSicadeDirectory();
         }
         if (fileName != null)
            return new File(path, fileName);
         else return path;
    }

    /**
     * update The URL in capabilities document with the service actual URL.
     */
    private void updateURL(final List<? extends AbstractDCP> dcpList, final String url) {
        for(AbstractDCP dcp: dcpList) {
            final AbstractHTTP http = dcp.getHTTP();
            final AbstractProtocol getMethod = http.getGet();
            if (getMethod != null) {
                getMethod.getOnlineResource().setHref(url + "wms?SERVICE=WMS&");
            }
            final AbstractProtocol postMethod = http.getPost();
            if (postMethod != null) {
                postMethod.getOnlineResource().setHref(url + "wms?SERVICE=WMS&");
            }
        }
    }

    /**
     * update The URL in capabilities document for the extended operation.
     */
    private void updateExtendedOperationURL(final AbstractRequest request, final ServiceVersion version,
                                                                           final String url)
    {

        if (version.toString().equals("1.3.0")) {
            org.constellation.wms.v130.Request r = (org.constellation.wms.v130.Request) request;
            List<JAXBElement<OperationType>> extendedOperations = r.getExtendedOperation();
            for(JAXBElement<OperationType> extOp: extendedOperations) {
                updateURL(extOp.getValue().getDCPType(), url);
            }

        // version 1.1.1
        } else {
           org.constellation.wms.v111.Request r = (org.constellation.wms.v111.Request) request;
           AbstractOperation op = r.getDescribeLayer();
           if (op != null)
                updateURL(op.getDCPType(), url);
           op = r.getGetLegendGraphic();
           if (op != null)
                updateURL(op.getDCPType(), url);
           op = r.getGetStyles();
           if (op != null)
                updateURL(op.getDCPType(), url);
           op = r.getPutStyles();
           if (op != null)
                updateURL(op.getDCPType(), url);
        }
    }

    /**
     * Return the value of a point in a map.
     *
     * @param gfi The {@linkplain GetFeatureInfo get feature info} request.
     * @return text, HTML , XML or GML code.
     *
     * @throws CstlServiceException
     */
    @Override
    public synchronized String getFeatureInfo(final GetFeatureInfo getFI) throws CstlServiceException {
    	
    	//
    	// Note this is almost the same logic as in getMap
    	//
    	// 0. FIX WORKER VERSION
        this.actingVersion = getFI.getVersion();

        // 1. SCENE
        //       -- get the List of layer references
        final List<String> layerNames = getFI.getLayers();
        final List<LayerDetails> layerRefs;
        layerRefs = getLayerReferences(layerNames);
        
        //       -- build an equivalent style List
        //TODO: clean up the SLD vs. style logic
        final List<String> styleNames          = getFI.getStyles();
        final MutableStyledLayerDescriptor sld = getFI.getSld();
        
        final List<Object> styles = new ArrayList<Object>();
        for (int i=0; i<layerRefs.size(); i++) {

            final Object style;
            if (sld != null) {
                //try to use the provided SLD
                style = extractStyle(layerRefs.get(i).getName(),sld);
            } else if (styleNames != null && styles.size() > i) {
                //try to grab the style if provided
                //a style has been given for this layer, try to use it
                style = styleNames.get(i);
            } else {
                //no defined styles, use the favorite one, let the layer get it himself.
                style = null;
            }
            styles.add(style);
        }
        //       -- create the rendering parameter Map
        final Double elevation                 = getFI.getElevation();
        final Date time                        = getFI.getTime();
        final MeasurementRange<?> dimRange     = getFI.getDimRange();
        final Map<String, Object> params       = new HashMap<String, Object>();
        params.put(WMSQuery.KEY_ELEVATION, elevation);
        params.put(WMSQuery.KEY_DIM_RANGE, dimRange);
        params.put(WMSQuery.KEY_TIME, time);
        Portrayal.SceneDef sdef = new Portrayal.SceneDef(layerRefs,styles,params);
        
        
        // 2. VIEW
        final ReferencedEnvelope refEnv        = new ReferencedEnvelope(getFI.getEnvelope());
        final double azimuth                   = getFI.getAzimuth();
        Portrayal.ViewDef vdef = new Portrayal.ViewDef(refEnv,azimuth);
        
        
        // 3. CANVAS
        final Dimension canvasDimension        = getFI.getSize();
        final Color background;
        if (getFI.getTransparent()) {
            background = null;
        } else {
            final Color color = getFI.getBackground();
            background = (color == null) ? Color.WHITE : color;
        }
        Portrayal.CanvasDef cdef = new Portrayal.CanvasDef(canvasDimension,background);
    	
        // 4. SHAPE
        //     a 
        final int PIXEL_TOLERANCE = 3;
        final Rectangle selectionArea = new Rectangle( getFI.getX()-PIXEL_TOLERANCE, 
        		                                      getFI.getY()-PIXEL_TOLERANCE, 
        		                                      PIXEL_TOLERANCE*2, 
        		                                      PIXEL_TOLERANCE*2);
        
        // 5. VISITOR
        String infoFormat = getFI.getInfoFormat();
        if (infoFormat == null) {
            //Should not happen since the info format parameter is mandatory for the GetFeatureInfo request.
            infoFormat = TEXT_PLAIN;
        }
        final TextGraphicVisitor visitor;
        if (infoFormat.equalsIgnoreCase(TEXT_PLAIN)) {
            // TEXT / PLAIN
            visitor = new CSVGraphicVisitor(getFI);
        } else if (infoFormat.equalsIgnoreCase(TEXT_HTML)) {
            // TEXT / HTML
            visitor = new HTMLGraphicVisitor(getFI);
        } else if (infoFormat.equalsIgnoreCase(APP_GML) || infoFormat.equalsIgnoreCase(TEXT_XML) ||
                   infoFormat.equalsIgnoreCase(APP_XML) || infoFormat.equalsIgnoreCase(XML) ||
                   infoFormat.equalsIgnoreCase(GML))
        {
            // GML
            visitor = new GMLGraphicVisitor(getFI);
        } else {
            throw new CstlServiceException("MIME type " + infoFormat + " is not accepted by the service.\n" +
                    "You have to choose between: "+ TEXT_PLAIN +", "+ TEXT_HTML +", "+ APP_GML +", "+ GML +
                    ", "+ APP_XML +", "+ XML+", "+ TEXT_XML,
                    INVALID_PARAMETER_VALUE, getFI.getVersion(), "info_format");
        }

        // We now build the response, according to the format chosen.
        try {
        	Cstl.getPortrayalService().visit(sdef,vdef,cdef,selectionArea,visitor);
        } catch (PortrayalException ex) {
            throw new CstlServiceException(ex, NO_APPLICABLE_CODE, getFI.getVersion());
        }

        return visitor.getResult();
    }

    /**
     * Return the legend graphic for the current layer.
     * <p>If no width or height have been specified, a default output
     * size is adopted (140x15 pixels).</p>
     *
     * @param getLegend The {@linkplain GetLegendGraphic get legend graphic} request.
     * @return a file containing the legend graphic image.
     *
     * @throws CstlServiceException
     */
    @Override
    public BufferedImage getLegendGraphic(final GetLegendGraphic getLegend) throws CstlServiceException {
    	this.actingVersion = getLegend.getVersion();
        final LayerDetails layer = getLayerReference( getLegend.getLayer());
        
        final Integer width  = getLegend.getWidth();
        final Integer height = getLegend.getHeight();
        final Dimension dims = new Dimension((width == null) ? 140 : width,
                                             (height == null) ? 15 : height);
        return layer.getLegendGraphic(dims);
    }

    /**
     * Return a map for the specified parameters in the query.
     *
     * @param getMap The {@linkplain GetMap get map} request.
     * @return The map requested, or an error.
     *
     * @throws CstlServiceException
     */
    @Override
    public synchronized BufferedImage getMap(final GetMap getMap) throws CstlServiceException {
        
    	//
    	// Note this is almost the same logic as in getFeatureInfo
    	//

        // 0. FIX THE ACTING VERSION
        final ServiceVersion queryVersion = getMap.getVersion();
        this.actingVersion = queryVersion;
        final String errorType = getMap.getExceptionFormat();
        final boolean errorInImage = EXCEPTIONS_INIMAGE.equalsIgnoreCase(errorType);
        
        
        // 1. SCENE
        //       -- get the List of layer references
        final List<String> layerNames = getMap.getLayers();
        final List<LayerDetails> layerRefs;
        try{
        	layerRefs = getLayerReferences(layerNames);
        } catch (CstlServiceException ex) {
        	//TODO: distinguish
            if (errorInImage) {
                return Cstl.getPortrayalService().writeInImage(ex, getMap.getSize());
            } else {
                throw new CstlServiceException(ex, LAYER_NOT_DEFINED, queryVersion);
            }
        }
        //       -- build an equivalent style List
        //TODO: clean up the SLD vs. style logic
        final List<String> styleNames          = getMap.getStyles();
        final MutableStyledLayerDescriptor sld = getMap.getSld();
        
        final List<Object> styles = new ArrayList<Object>();
        for (int i=0; i<layerRefs.size(); i++) {

            final Object style;
            if (sld != null) {
                //try to use the provided SLD
                style = extractStyle(layerRefs.get(i).getName(),sld);
            } else if (styleNames != null && styles.size() > i) {
                //try to grab the style if provided
                //a style has been given for this layer, try to use it
                style = styleNames.get(i);
            } else {
                //no defined styles, use the favorite one, let the layer get it himself.
                style = null;
            }
            styles.add(style);
        }
        //       -- create the rendering parameter Map
        final Double elevation                 = getMap.getElevation();
        final Date time                        = getMap.getTime();
        final MeasurementRange<?> dimRange     = getMap.getDimRange();
        final Map<String, Object> params       = new HashMap<String, Object>();
        params.put(WMSQuery.KEY_ELEVATION, elevation);
        params.put(WMSQuery.KEY_DIM_RANGE, dimRange);
        params.put(WMSQuery.KEY_TIME, time);
        Portrayal.SceneDef sdef = new Portrayal.SceneDef(layerRefs,styles,params);
        
        
        // 2. VIEW
        final ReferencedEnvelope refEnv        = new ReferencedEnvelope(getMap.getEnvelope());
        final double azimuth                   = getMap.getAzimuth();
        Portrayal.ViewDef vdef = new Portrayal.ViewDef(refEnv,azimuth);
        
        
        // 3. CANVAS
        final Dimension canvasDimension        = getMap.getSize();
        final Color background;
        if (getMap.getTransparent()) {
            background = null;
        } else {
            final Color color = getMap.getBackground();
            background = (color == null) ? Color.WHITE : color;
        }
        Portrayal.CanvasDef cdef = new Portrayal.CanvasDef(canvasDimension,background);
        
        // 4. IMAGE
        BufferedImage image;
        try {
            image = Cstl.getPortrayalService().portray(sdef, vdef, cdef);
        } catch (PortrayalException ex) {
            if (errorInImage) {
                return Cstl.getPortrayalService().writeInImage(ex, getMap.getSize() );
            } else {
                throw new CstlServiceException(ex, NO_APPLICABLE_CODE, queryVersion);
            }
        }
        
        return image;
    }
    
    
    
    //TODO: handle the null value in the exception.
    //TODO: harmonize with the method getLayerReference().
    private List<LayerDetails> getAllLayerReferences() throws CstlServiceException {

    	List<LayerDetails> layerRefs = new ArrayList<LayerDetails>();
    	try { // WE catch the exception from either service version
    		String version = actingVersion.toString();
	        if (  version.equals("1.1.1") ) {
	        	layerRefs = Cstl.getRegister().getAllLayerReferences(ServiceDef.WMS_1_1_1_SLD );
	        } else if ( version.equals("1.3.0") ) {
	        	layerRefs = Cstl.getRegister().getAllLayerReferences(ServiceDef.WMS_1_3_0 );
	        } else {
	        	throw new CstlServiceException("WMS acting according to no known version.",
                        VERSION_NEGOTIATION_FAILED);
	        }
        } catch (RegisterException regex ){
        	throw new CstlServiceException(regex, INVALID_PARAMETER_VALUE, actingVersion);
        }
        return layerRefs;
    }
    
    //TODO: handle the null value in the exception.
    //TODO: harmonize with the method getLayerReference().
    private List<LayerDetails> getLayerReferences(List<String> layerNames) throws CstlServiceException {

    	List<LayerDetails> layerRefs = new ArrayList<LayerDetails>();
    	try { // WE catch the exception from either service version
    		String version = actingVersion.toString();
	        if (  version.equals("1.1.1") ) {
	        	layerRefs = Cstl.getRegister().getLayerReferences(ServiceDef.WMS_1_1_1_SLD, layerNames );
	        } else if ( version.equals("1.3.0") ) {
	        	layerRefs = Cstl.getRegister().getLayerReferences(ServiceDef.WMS_1_3_0, layerNames );
	        } else {
	        	throw new CstlServiceException("WMS acting according to no known version.",
                        VERSION_NEGOTIATION_FAILED);
	        }
        } catch (RegisterException regex ){
        	throw new CstlServiceException(regex, INVALID_PARAMETER_VALUE, actingVersion);
        }
        return layerRefs;
    }
    
    //TODO: handle the null value in the exception.
    //TODO: harmonize with the method getLayerReference().
    private LayerDetails getLayerReference(String layerName) throws CstlServiceException {

        LayerDetails layerRef;
    	try { // WE catch the exception from either service version
    		String version = actingVersion.toString();
	        if (  version.equals("1.1.1") ) {
	        	layerRef = Cstl.getRegister().getLayerReference(ServiceDef.WMS_1_1_1_SLD, layerName );
	        } else if ( version.equals("1.3.0") ) {
	        	layerRef = Cstl.getRegister().getLayerReference(ServiceDef.WMS_1_3_0, layerName );
	        } else {
	        	throw new CstlServiceException("WMS acting according to no known version.",
                        VERSION_NEGOTIATION_FAILED);
	        }
        } catch (RegisterException regex ){
        	throw new CstlServiceException(regex, INVALID_PARAMETER_VALUE, actingVersion);
        }
        return layerRef;
    }
    


    private Object extractStyle(final String layerName, final MutableStyledLayerDescriptor sld){
        if(sld == null){
            throw new NullPointerException("SLD should not be null");
        }

        for(final MutableLayer layer : sld.layers()){

            if(layer instanceof MutableNamedLayer && layerName.equals(layer.getName()) ){
                //we can only extract style from a NamedLayer that has the same name
                final MutableNamedLayer mnl = (MutableNamedLayer) layer;

                for(final MutableLayerStyle mls : mnl.styles()){
                    if(mls instanceof MutableNamedStyle){
                        final MutableNamedStyle mns = (MutableNamedStyle) mls;
                        return mns.getName();
                    }else if(mls instanceof MutableStyle){
                        return mls;
                    }

                }
            }
        }

        //no valid style found
        return null;
    }

}

/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
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

package org.constellation.wfs.ws.rs;

// J2SE dependencies
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

// JAXB dependencies
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;


// jersey dependencies
import com.sun.jersey.spi.resource.Singleton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// constellation dependencies
import javax.xml.bind.UnmarshalException;
import org.constellation.ServiceDef;
import org.constellation.util.StringUtilities;
import org.constellation.util.Util;
import org.constellation.wfs.ws.DefaultWFSWorker;
import org.constellation.wfs.ws.WFSWorker;
import org.constellation.ws.CstlServiceException;
//import org.constellation.ws.ExceptionReport;
//import org.constellation.ws.ExceptionType;
import org.constellation.ws.MimeType;
import org.constellation.ws.rs.OGCWebService;

// Geotoolkit dependencies
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.ogc.xml.v110.BBOXType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.GmlObjectIdType;
import org.geotoolkit.ogc.xml.v110.SortByType;
import org.geotoolkit.ogc.xml.v110.SortPropertyType;
import org.geotoolkit.ows.xml.v100.AcceptFormatsType;
import org.geotoolkit.ows.xml.v100.AcceptVersionsType;
import org.geotoolkit.ows.xml.v100.ExceptionReport;
import org.geotoolkit.ows.xml.v100.SectionsType;
import org.geotoolkit.util.Versioned;
import org.geotoolkit.wfs.xml.v110.AllSomeType;
import org.geotoolkit.wfs.xml.v110.DeleteElementType;
import org.geotoolkit.wfs.xml.v110.DescribeFeatureTypeType;
import org.geotoolkit.wfs.xml.v110.GetCapabilitiesType;
import org.geotoolkit.wfs.xml.v110.GetFeatureType;
import org.geotoolkit.wfs.xml.v110.GetGmlObjectType;
import org.geotoolkit.wfs.xml.v110.InsertElementType;
import org.geotoolkit.wfs.xml.v110.LockFeatureType;
import org.geotoolkit.wfs.xml.v110.LockType;
import org.geotoolkit.wfs.xml.v110.QueryType;
import org.geotoolkit.wfs.xml.v110.ResultTypeType;
import org.geotoolkit.wfs.xml.v110.TransactionType;

import org.geotoolkit.xml.MarshallerPool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.sort.SortOrder;
import static org.geotoolkit.ows.xml.OWSExceptionCode.*;
import static org.constellation.query.wfs.WFSQuery.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@Path("wfs")
@Singleton
public class WFSService extends OGCWebService {

    private final WFSWorker worker;

    private static Map<String, String> schemaLocations;

    /**
     * Build a new Restfull WFS service.
     */
    public WFSService() {
        super(ServiceDef.WFS_1_1_0);

        WFSWorker candidate              = null;
        try {
            setXMLContext("org.geotoolkit.wfs.xml.v110"   +
            		  ":org.geotoolkit.ogc.xml.v110"  +
            		  ":org.geotoolkit.gml.xml.v311"  +
                          ":org.geotoolkit.xsd.xml.v2001" +
                          ":org.constellation.ws"
                          , "");
            candidate       = new DefaultWFSWorker(getMarshallerPool());

        } catch (JAXBException ex){
            LOGGER.severe("The WFS service is not running."       + '\n' +
                          " cause  : Error creating XML context." + '\n' +
                          " error  : " + ex.getMessage()          + '\n' +
                          " details: " + ex.toString());
        } 
        this.worker        = candidate;

        //activateRequestValidation("http://schemas.opengis.net/wfs/1.1.0/wfs.xsd");
        if (worker != null) {
            LOGGER.info("WFS Service running");
        }
    }

    /**
     * Treat the incomming request and call the right function.
     *
     * @param objectRequest if the server receive a POST request in XML,
     *        this object contain the request. Else for a GET or a POST kvp
     *        request this param is {@code null}
     *
     * @return an image or xml response.
     * @throw JAXBException
     */
    @Override
    public Response treatIncomingRequest(Object objectRequest) throws JAXBException {

        Marshaller marshaller = null;
        ServiceDef version    = null;

        worker.initHTTPContext(getHttpContext());
        worker.initSecurityContext(null);
        worker.initServletContext(getServletContext());
        worker.initUriContext(getUriContext());

        try {
            marshaller = getMarshallerPool().acquireMarshaller();

            if (objectRequest instanceof JAXBElement) {
                objectRequest = ((JAXBElement<?>)objectRequest).getValue();
            }

            final String request = (objectRequest == null) ? getParameter(KEY_REQUEST, true) : null;
            logParameters();

            if (STR_GETCAPABILITIES.equalsIgnoreCase(request) || (objectRequest instanceof GetCapabilitiesType)) {
                final GetCapabilitiesType model = adaptGetCapabilities(objectRequest);
                final StringWriter sw = new StringWriter();
                marshaller.marshal(worker.getCapabilities(model), sw);
                return Response.ok(sw.toString(), getOutputFormat()).build();

            } else if (STR_DESCRIBEFEATURETYPE.equalsIgnoreCase(request) || (objectRequest instanceof DescribeFeatureTypeType)) {
                final DescribeFeatureTypeType model = adaptDescripbeFeatureType(objectRequest);
                final StringWriter sw = new StringWriter();
                marshaller.marshal(worker.describeFeatureType(model), sw);
                return Response.ok(sw.toString(), getOutputFormat()).build();

            } else if (STR_GETFEATURE.equalsIgnoreCase(request) || (objectRequest instanceof GetFeatureType)) {
                final GetFeatureType model = adaptGetFeatureType(objectRequest);
                version = getVersionFromNumber(model.getVersion());
                Object response = worker.getFeature(model);
                if (response instanceof FeatureCollection) {
                    schemaLocations = worker.getSchemaLocations();
                    return Response.ok(response, getOutputFormat()).build();
                } else {
                    final StringWriter sw = new StringWriter();
                    marshaller.marshal(response, sw);
                    return Response.ok(sw.toString(), getOutputFormat()).build();
                }

            } else if (STR_GETGMLOBJECT.equalsIgnoreCase(request) || (objectRequest instanceof GetGmlObjectType)) {
                final GetGmlObjectType model = adaptGetGMLObject(objectRequest);
                version = getVersionFromNumber(model.getVersion());
                final StringWriter sw = new StringWriter();
                marshaller.marshal(worker.getGMLObject(model), sw);
                return Response.ok(sw.toString(), getOutputFormat()).build();

            } else if (STR_LOCKFEATURE.equalsIgnoreCase(request) || (objectRequest instanceof LockFeatureType)) {
                final LockFeatureType model = adaptLockFeature(objectRequest);
                version = getVersionFromNumber(model.getVersion());
                final StringWriter sw = new StringWriter();
                marshaller.marshal(worker.lockFeature(model), sw);
                return Response.ok(sw.toString(), getOutputFormat()).build();

            } else if (STR_TRANSACTION.equalsIgnoreCase(request) || (objectRequest instanceof TransactionType)) {
                final TransactionType model = adaptTransaction(objectRequest);
                version = getVersionFromNumber(model.getVersion());
                final StringWriter sw = new StringWriter();
                marshaller.marshal(worker.transaction(model), sw);
                return Response.ok(sw.toString(), getOutputFormat()).build();
            }

            //unvalid request, throw an error
            final String invalidRequest;
            if ( request == null && objectRequest != null){
                invalidRequest = objectRequest.getClass().getName();
            }else if (request == null && objectRequest == null){
                invalidRequest = "undefined request";
            } else {
                invalidRequest = request;
            }

            throw new CstlServiceException("The operation " + invalidRequest + " is not supported by the service",
                                          INVALID_PARAMETER_VALUE, "request");

        } catch (CstlServiceException ex) {
            return processExceptionResponse(ex, version);
        } finally {
            if (marshaller != null) {
                getMarshallerPool().release(marshaller);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Response processExceptionResponse(final CstlServiceException ex, ServiceDef serviceDef) throws JAXBException {
        /* We don't print the stack trace:
         * - if the user have forget a mandatory parameter.
         * - if the version number is wrong.
         * - if the user have send a wrong request parameter
         */
        if (!ex.getExceptionCode().equals(MISSING_PARAMETER_VALUE) &&
                !ex.getExceptionCode().equals(VERSION_NEGOTIATION_FAILED) &&
                !ex.getExceptionCode().equals(INVALID_PARAMETER_VALUE) &&
                !ex.getExceptionCode().equals(OPERATION_NOT_SUPPORTED)) {
            LOGGER.log(Level.INFO, ex.getLocalizedMessage(), ex);
        } else {
            LOGGER.severe("SENDING EXCEPTION: " + ex.getExceptionCode().name() + " " + ex.getMessage() + '\n');
        }
        if (isJaxBContextValid()) {
            if (serviceDef == null) {
                serviceDef = getBestVersion(null);
            }
            final String version = serviceDef.exceptionVersion.toString();
            String exceptionCode;
            if (ex.getExceptionCode() instanceof org.constellation.ws.ExceptionCode) {
                exceptionCode = Util.transformCodeName(ex.getExceptionCode().name());
            } else {
                exceptionCode = ex.getExceptionCode().name();
            }
            ExceptionReport report = new ExceptionReport(ex.getMessage(), exceptionCode, ex.getLocator(), version);
            StringWriter sw = new StringWriter();
            Marshaller marshaller = null;
            try {
                marshaller = getMarshallerPool().acquireMarshaller();
                marshaller.marshal(report, sw);
            } finally {
                if (marshaller != null) {
                    getMarshallerPool().release(marshaller);
                }
            }
            return Response.ok(Util.cleanSpecialCharacter(sw.toString()), "text/xml").build();
        } else {
            return Response.ok("The WFS server is not running cause: unable to create JAXB context!", "text/plain").build();
        }
    }

    /**
     * Treat the incoming POST request encoded in xml.
     *
     * @return an image or xml response.
     * @throw JAXBException
     */
    @POST
    @Consumes("*/xml")
    @Override
    public Response doPOSTXml(InputStream is) throws JAXBException  {
        final MarshallerPool marshallerPool = getMarshallerPool();
        if (marshallerPool != null) {
            Object request = null;
            Unmarshaller unmarshaller = null;
            InputStream in = null;
            File temp = null;
            try {
                unmarshaller = marshallerPool.acquireUnmarshaller();
                
                //copy the stream in a temp file, we will have to read it twice
                temp = File.createTempFile("temp", "xml");
                //For Overwrite the file.
                OutputStream out = new FileOutputStream(temp);

                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                is.close();
                out.close();


                //we made a pre-reading to extract the feature to insert in transaction request.
                
                List<SimpleFeature> featuresToInsert = null;
                try {
                    XmlFeatureReader featureReader = new JAXPStreamFeatureReader(worker.getFeatureTypes());
                    in = new FileInputStream(temp);
                    featuresToInsert =  (List<SimpleFeature>) featureReader.read(in);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(),ex);
                    return launchException("Error while pre-reading the request.\nCause:" + ex.getMessage(), "NO_APPLICABLE_CODE", null);
                } finally{
                    if(in != null){
                        in.close();
                    }
                }

                in = new FileInputStream(temp);
                request = unmarshaller.unmarshal(in);
                if (request instanceof JAXBElement) {
                    request = ((JAXBElement<?>)request).getValue();
                }

                // we replace the feature to insert unmarshalled by JAXB with the feature read by JAXP.
                if (request instanceof TransactionType && featuresToInsert != null) {
                    TransactionType transaction = (TransactionType) request;
                    for (Object obj : transaction.getInsertOrUpdateOrDelete()) {
                        if (obj instanceof InsertElementType) {
                            InsertElementType insert = (InsertElementType) obj;
                            insert.getFeature().clear();
                            insert.getFeature().addAll(featuresToInsert);
                        }
                    }
                }
            }catch(IOException ex){
                return launchException("Error while pre-reading the request.\nCause:" + ex.getMessage(), "NO_APPLICABLE_CODE", null);
            }catch (UnmarshalException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    if (e.getCause() != null && e.getCause().getMessage() != null) {
                        errorMsg = e.getCause().getMessage();
                    } else if (e.getLinkedException() != null && e.getLinkedException().getMessage() != null) {
                        errorMsg = e.getLinkedException().getMessage();
                    }
                }
                String codeName;
                if (errorMsg != null && errorMsg.startsWith("unexpected element")) {
                    codeName = OPERATION_NOT_SUPPORTED.name();
                } else {
                    codeName = INVALID_REQUEST.name();
                }

                return launchException("The XML request is not valid.\nCause:" + errorMsg, codeName, null);
            } finally {
                if (unmarshaller != null)  {
                    marshallerPool.release(unmarshaller);
                }
                
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WFSService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(temp != null){
                    temp.delete();
                }

            }

            if (request instanceof Versioned) {
                final Versioned ar = (Versioned) request;
                if (ar.getVersion() != null)
                    getUriContext().getQueryParameters().add("VERSION", ar.getVersion().toString());
            } if (request != null) {
                String type = "";
                if (request instanceof JAXBElement) {
                    type = ((JAXBElement)request).getDeclaredType().getName();
                } else {
                    type = request.getClass().getName();
                }
                LOGGER.finer("request type:" + type);
            }
            return treatIncomingRequest(request);
        } else {
            return Response.ok("This service is not running", MimeType.TEXT_PLAIN).build();
        }
    }

    private GetCapabilitiesType adaptGetCapabilities(Object objectRequest) throws CstlServiceException {
        if (objectRequest instanceof GetCapabilitiesType){
            return (GetCapabilitiesType)objectRequest;
        } else {
            //use a default getCapabilities request
            return createNewGetCapabilitiesRequest();
        }
    }

    private DescribeFeatureTypeType adaptDescripbeFeatureType(Object objectRequest) throws CstlServiceException {
        if (objectRequest instanceof DescribeFeatureTypeType){
            return (DescribeFeatureTypeType)objectRequest;
        } else {
            //build a simple describe request
            //todo we must handle the query parameters here
            return createNewDescribeFeatureTypeRequest();
        }
    }

    private GetFeatureType adaptGetFeatureType(Object objectRequest) throws CstlServiceException {
        if (objectRequest instanceof GetFeatureType){
            return (GetFeatureType)objectRequest;
        } else {
            //build a simple get feature type request
            //todo we must handle the query parameters here
            return createNewGetFeatureRequest();
        }
    }

    private GetGmlObjectType adaptGetGMLObject(Object objectRequest) throws CstlServiceException {
        if (objectRequest instanceof GetGmlObjectType){
            return (GetGmlObjectType)objectRequest;
        } else {
            //build a simple get gml object request
            //todo we must handle the query parameters here
            return createNewGetGmlObjectRequest();
        }
    }

    private LockFeatureType adaptLockFeature(Object objectRequest) throws CstlServiceException {
        if (objectRequest instanceof LockFeatureType){
            return (LockFeatureType)objectRequest;
        } else {
            //build a simple lock feature request
            //todo we must handle the query parameters here
            return createNewLockFeatureRequest();
        }
    }

    private TransactionType adaptTransaction(Object objectRequest) throws CstlServiceException {
        if (objectRequest instanceof TransactionType){
            return (TransactionType)objectRequest;
        } else {
            //build a simple transaction request
            //todo we must handle the query parameters here
            return createNewTransactionRequest();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void destroy() {
        // do something
    }

    private DescribeFeatureTypeType createNewDescribeFeatureTypeRequest() throws CstlServiceException {
        String outputFormat = getParameter("outputFormat", false);
        String handle       = getParameter("handle", false);
        String service      = getParameter("service", true);
        String version      = getParameter("version", true);

        if (outputFormat == null)
            outputFormat = "text/xml; subtype=gml/3.1.1";
        String namespace = getParameter("namespace", false);
        Map<String, String> mapping = new HashMap<String, String>();
        if (namespace != null) {
            final StringTokenizer tokens = new StringTokenizer(namespace, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf("xmlns(") != -1 && token.indexOf(')') != -1 && token.indexOf('=') != -1) {
                    String tmp = token.substring(token.indexOf("xmlns(") + 6, token.indexOf(')'));
                    String prefix = tmp.substring(0, tmp.indexOf('='));
                    String namesp = tmp.substring(tmp.indexOf('=') + 1);
                    mapping.put(prefix, namesp);

                } else {
                    throw new CstlServiceException("The namespace parameter is malformed : [" + token + "] the good pattern is xmlns(ns1=http://my_ns1.com)",
                                                  INVALID_PARAMETER_VALUE, "namespace");
                }
            }
        }
        List<QName> typeNames = new ArrayList<QName>();
        String typeName = getParameter("typeName", false);
        if (typeName != null) {
            final StringTokenizer tokens = new StringTokenizer(typeName, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf(':') != -1) {
                    String prefix    = token.substring(0, token.indexOf(':'));
                    String localPart = token.substring(token.indexOf(':') + 1);
                    String namesp = mapping.get(prefix);
                    if (namesp != null) {
                        typeNames.add(new QName(namesp, localPart, prefix));
                    } else {
                        throw new CstlServiceException("The typeName parameter is malformed : the prefix [" + prefix + "] is not bounded with a namespace",
                                                  INVALID_PARAMETER_VALUE, "typeName");
                    }
                } else {
                    throw new CstlServiceException("The typeName parameter is malformed : [" + token + "] the good pattern is ns1:feature",
                                                  INVALID_PARAMETER_VALUE, "typeName");
                }
            }
        }

        return new DescribeFeatureTypeType(service, version, handle, typeNames, outputFormat);
    }

    private GetCapabilitiesType createNewGetCapabilitiesRequest() throws CstlServiceException {
        String version = getParameter("acceptVersions", false);
        AcceptVersionsType versions;
        if (version != null) {
            if (version.indexOf(',') != -1) {
                version = version.substring(0, version.indexOf(','));
            }
            versions = new AcceptVersionsType(version);
        } else {
             versions = new AcceptVersionsType("1.1.0");
        }

        AcceptFormatsType formats = new AcceptFormatsType(getParameter("AcceptFormats", false));

        //We transform the String of sections in a list.
        //In the same time we verify that the requested sections are valid.
        SectionsType sections;
        String section = getParameter("Sections", false);
        if (section != null && !section.equalsIgnoreCase("All")) {
            List<String> requestedSections = new ArrayList<String>();
            final StringTokenizer tokens = new StringTokenizer(section, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (SectionsType.getExistingSections().contains(token)){
                    requestedSections.add(token);
                } else {
                    throw new CstlServiceException("The section " + token + " does not exist",
                                                  INVALID_PARAMETER_VALUE, "Sections");
                }
            }
            sections = new SectionsType(requestedSections);
        } else {
            sections = null;
            
        }
        
        return new GetCapabilitiesType(versions,
                                       sections,
                                       formats,
                                       null,
                                       getParameter("SERVICE", true));

    }

    private GetFeatureType createNewGetFeatureRequest() throws CstlServiceException {
        Integer maxFeature = null;
        String max = getParameter("maxfeatures", false);
        if (max != null) {
            try {
                maxFeature = Integer.parseInt(max);
            } catch (NumberFormatException ex) {
                throw new CstlServiceException("Unable to parse the integer maxfeatures parameter" + max + "",
                                                  INVALID_PARAMETER_VALUE, "MaxFeatures");
            }

        }
        String service      = getParameter("service", true);
        String version      = getParameter("version", true);
        String handle       = getParameter("handle",  false);
        String outputFormat = getParameter("outputFormat", false);

        if (outputFormat == null)
            outputFormat = "text/xml; subtype=gml/3.1.1";
        String namespace = getParameter("namespace", false);
        Map<String, String> mapping = new HashMap<String, String>();
        if (namespace != null) {
            final StringTokenizer tokens = new StringTokenizer(namespace, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf("xmlns(") != -1 && token.indexOf(')') != -1 && token.indexOf('=') != -1) {
                    String tmp    = token.substring(token.indexOf("xmlns(") + 6, token.indexOf(')'));
                    String prefix = tmp.substring(0, tmp.indexOf('='));
                    String namesp = tmp.substring(tmp.indexOf('=') + 1);
                    mapping.put(prefix, namesp);

                } else {
                    throw new CstlServiceException("The namespace parameter is malformed : [" + token + "] the good pattern is xmlns(ns1=http://my_ns1.com)",
                                                  INVALID_PARAMETER_VALUE, "namespace");
                }
            }
        }
        List<QName> typeNames = new ArrayList<QName>();
        String typeName = getParameter("typeName", true);
        if (typeName != null) {
            final StringTokenizer tokens = new StringTokenizer(typeName, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf(':') != -1) {
                    String prefix    = token.substring(0, token.lastIndexOf(':'));
                    String localPart = token.substring(token.lastIndexOf(':') + 1);
                    String namesp    = mapping.get(prefix);
                    if (namesp != null) {
                        typeNames.add(new QName(namesp, localPart, prefix));
                    } else {
                        // we assume that the query was typeName=namespace:localPart

                        typeNames.add(new QName(prefix, localPart));
                        /*throw new CstlServiceException("The typeName parameter is malformed : the prefix [" + prefix + "] is not bounded with a namespace",
                                                  INVALID_PARAMETER_VALUE, "typeName");*/
                    }
                } else {
                    typeNames.add(new QName(token));
                    /*throw new CstlServiceException("The typeName parameter is malformed : [" + token + "] the good pattern is ns1:feature",
                                                  INVALID_PARAMETER_VALUE, "typeName");*/
                }
            }
        }

        String xmlFilter = getParameter("filter", false);
        FilterType filter = null;
        if (xmlFilter != null) {
            Unmarshaller unmarshaller = null;
            try {
                unmarshaller = getMarshallerPool().acquireUnmarshaller();
                Object obj = unmarshaller.unmarshal(new StringReader(xmlFilter));
                if (obj instanceof JAXBElement) {
                    obj = ((JAXBElement)obj).getValue();
                }
                if (!(obj instanceof FilterType)) {
                    String type = "null";
                    if (obj != null) {
                        type = obj.getClass().getName();
                    }
                    throw new CstlServiceException("The xml filter does not have the good type:" + type,
                                                  INVALID_PARAMETER_VALUE, "filter");
                } else {
                    filter = (FilterType) obj;
                }
            } catch (JAXBException ex) {
                throw new CstlServiceException("The service was unable to read the xml filter:" + ex.getMessage(),
                                                  INVALID_PARAMETER_VALUE, "filter");
            } finally {
                if (unmarshaller != null) {
                    getMarshallerPool().release(unmarshaller);
                }
            }
        }

        String bbox = getParameter("bbox", false);
        if (bbox != null) {
            final double[] coodinates = new double[4];

            final StringTokenizer tokens = new StringTokenizer(bbox, ",;");
            int index = 0;
            while (tokens.hasMoreTokens() && index < 4) {
                final double value = StringUtilities.toDouble(tokens.nextToken());
                coodinates[index] = value;
                index++;
            }
            String crs = null;
            if (tokens.hasMoreTokens()) {
                crs = tokens.nextToken();
            }
            
            if (coodinates != null) {
                BBOXType bboxFilter = new BBOXType("", coodinates[0], coodinates[1], coodinates[2], coodinates[3], null);
                if (filter == null) {
                    filter = new FilterType(bboxFilter);
                } else {
                    LOGGER.info("unexpected case --> filter + bbox TODO");
                }
            }
        }

        String featureVersion = getParameter("featureVersion", false);
        QueryType query = new QueryType(filter, typeNames, featureVersion);


        // TODO handle multiple properties and handle prefixed properties
        String sortByParam = getParameter("sortBy", false);
        if (sortByParam != null) {
            if (sortByParam.indexOf(':') != -1) {
                sortByParam = sortByParam.substring(sortByParam.indexOf(':') + 1);
            }
            //we get the order
            SortOrder order;
            if (sortByParam.indexOf(' ') != -1) {
                char cOrder = sortByParam.charAt(sortByParam.length() -1);
                sortByParam = sortByParam.substring(0, sortByParam.indexOf(' '));
                if (cOrder == 'D') {
                    order = SortOrder.DESCENDING;
                } else {
                    order = SortOrder.ASCENDING;
                }
            } else {
                order = SortOrder.ASCENDING;
            }
            List<SortPropertyType> sortProperties = new ArrayList<SortPropertyType>();
            sortProperties.add(new SortPropertyType(sortByParam, order));
            SortByType sortBy = new SortByType(sortProperties);
            query.setSortBy(sortBy);
        }

        String propertyNameParam = getParameter("propertyName", false);
        if (propertyNameParam != null) {
            List<String> propertyNames = new ArrayList<String>();
            final StringTokenizer tokens = new StringTokenizer(propertyNameParam, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                propertyNames.add(token);
            }
            query.getPropertyNameOrXlinkPropertyNameOrFunction().addAll(propertyNames);
        }
        

        String result = getParameter("resultType", false);
        ResultTypeType resultType = null;
        if (result != null) {
            resultType = ResultTypeType.fromValue(result.toLowerCase());
        }
        return new GetFeatureType(service, version, handle, maxFeature, Arrays.asList(query), resultType, outputFormat);


    }

    private GetGmlObjectType createNewGetGmlObjectRequest() throws CstlServiceException {
        String service      = getParameter("service", true);
        String version      = getParameter("version", true);
        String handle       = getParameter("handle",  false);
        String outputFormat = getParameter("outputFormat", false);
        String id           = getParameter("gmlobjectid", true);

        GmlObjectIdType gmlObjectId = new GmlObjectIdType(id);
        return new GetGmlObjectType(service, version, handle, gmlObjectId, outputFormat);
    }

    private LockFeatureType createNewLockFeatureRequest() throws CstlServiceException {
        String service      = getParameter("service", true);
        String version      = getParameter("version", true);
        String handle       = getParameter("handle",  false);

        String lockAct      = getParameter("lockAction",  false);
        AllSomeType lockAction = null;
        if (lockAct != null) {
            lockAction = AllSomeType.fromValue(lockAct);
        }
        String exp         = getParameter("expiry",  false);
        Integer expiry     = null;
        if (exp != null) {
            try {
                expiry = Integer.parseInt(exp);
            } catch (NumberFormatException ex) {
                throw new CstlServiceException("The service was to parse the expiry value :" + exp,
                                                  INVALID_PARAMETER_VALUE, "expiry");
            }
        }

        String namespace = getParameter("namespace", false);
        Map<String, String> mapping = new HashMap<String, String>();
        if (namespace != null) {
            final StringTokenizer tokens = new StringTokenizer(namespace, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf("xmlns(") != -1 && token.indexOf(')') != -1 && token.indexOf('=') != -1) {
                    String tmp = token.substring(token.indexOf("xmlns(") + 6, token.indexOf(')'));
                    String prefix = tmp.substring(0, tmp.indexOf('='));
                    String namesp = tmp.substring(tmp.indexOf('=') + 1);
                    mapping.put(prefix, namesp);

                } else {
                    throw new CstlServiceException("The namespace parameter is malformed : [" + token + "] the good pattern is xmlns(ns1=http://my_ns1.com)",
                                                  INVALID_PARAMETER_VALUE, "namespace");
                }
            }
        }
        List<QName> typeNames = new ArrayList<QName>();
        String typeName = getParameter("typeName", true);
        if (typeName != null) {
            final StringTokenizer tokens = new StringTokenizer(typeName, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf(':') != -1) {
                    String prefix    = token.substring(0, token.indexOf(':'));
                    String localPart = token.substring(token.indexOf(':') + 1);
                    String namesp = mapping.get(prefix);
                    if (namesp != null) {
                        typeNames.add(new QName(namesp, localPart, prefix));
                    } else {
                        throw new CstlServiceException("The typeName parameter is malformed : the prefix [" + prefix + "] is not bounded with a namespace",
                                                  INVALID_PARAMETER_VALUE, "typeName");
                    }
                } else {
                    throw new CstlServiceException("The typeName parameter is malformed : [" + token + "] the good pattern is ns1:feature",
                                                  INVALID_PARAMETER_VALUE, "typeName");
                }
            }
        }

        String xmlFilter = getParameter("filter", false);
        FilterType filter = null;
        if (xmlFilter != null) {
            Unmarshaller unmarshaller = null;
            try {
                unmarshaller = getMarshallerPool().acquireUnmarshaller();
                Object obj = unmarshaller.unmarshal(new StringReader(xmlFilter));
                if (obj instanceof JAXBElement) {
                    obj = ((JAXBElement)obj).getValue();
                }
                if (!(obj instanceof FilterType)) {
                    String type = "null";
                    if (obj != null) {
                        type = obj.getClass().getName();
                    }
                    throw new CstlServiceException("The xml filter does not have the good type:" + type,
                                                  INVALID_PARAMETER_VALUE, "filter");
                }
            } catch (JAXBException ex) {
                throw new CstlServiceException("The service was unable to read the xml filter:" + ex.getMessage(),
                                                  INVALID_PARAMETER_VALUE, "filter");
            } finally {
                if (unmarshaller != null) {
                    getMarshallerPool().release(unmarshaller);
                }
            }
        }
        // TODO
        QName typeNamee = typeNames.get(0);
        LockType lock = new LockType(filter, handle, typeNamee);

        return new LockFeatureType(service, version, handle, Arrays.asList(lock), expiry, lockAction);
    }

    private TransactionType createNewTransactionRequest() throws CstlServiceException {
        String service      = getParameter("service", true);
        String version      = getParameter("version", true);
        String handle       = getParameter("handle",  false);
        String relAct       = getParameter("releaseAction",  false);
        AllSomeType releaseAction = null;
        if (relAct != null) {
            releaseAction = AllSomeType.fromValue(relAct);
        }

         String namespace = getParameter("namespace", false);
        Map<String, String> mapping = new HashMap<String, String>();
        if (namespace != null) {
            final StringTokenizer tokens = new StringTokenizer(namespace, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf("xmlns(") != -1 && token.indexOf(')') != -1 && token.indexOf('=') != -1) {
                    String tmp = token.substring(token.indexOf("xmlns(") + 6, token.indexOf(')'));
                    String prefix = tmp.substring(0, tmp.indexOf('='));
                    String namesp = tmp.substring(tmp.indexOf('=') + 1);
                    mapping.put(prefix, namesp);

                } else {
                    throw new CstlServiceException("The namespace parameter is malformed : [" + token + "] the good pattern is xmlns(ns1=http://my_ns1.com)",
                                                  INVALID_PARAMETER_VALUE, "namespace");
                }
            }
        }
        List<QName> typeNames = new ArrayList<QName>();
        String typeName = getParameter("typeName", true);
        if (typeName != null) {
            final StringTokenizer tokens = new StringTokenizer(typeName, ",;");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken().trim();
                if (token.indexOf(':') != -1) {
                    String prefix    = token.substring(0, token.indexOf(':'));
                    String localPart = token.substring(token.indexOf(':') + 1);
                    String namesp = mapping.get(prefix);
                    if (namesp != null) {
                        typeNames.add(new QName(namesp, localPart, prefix));
                    } else {
                        throw new CstlServiceException("The typeName parameter is malformed : the prefix [" + prefix + "] is not bounded with a namespace",
                                                  INVALID_PARAMETER_VALUE, "typeName");
                    }
                } else {
                    throw new CstlServiceException("The typeName parameter is malformed : [" + token + "] the good pattern is ns1:feature",
                                                  INVALID_PARAMETER_VALUE, "typeName");
                }
            }
        }

        String xmlFilter = getParameter("filter", false);
        FilterType filter = null;
        if (xmlFilter != null) {
            Unmarshaller unmarshaller = null;
            try {
                unmarshaller = getMarshallerPool().acquireUnmarshaller();
                Object obj = unmarshaller.unmarshal(new StringReader(xmlFilter));
                if (obj instanceof JAXBElement) {
                    obj = ((JAXBElement)obj).getValue();
                }
                if (!(obj instanceof FilterType)) {
                    String type = "null";
                    if (obj != null) {
                        type = obj.getClass().getName();
                    }
                    throw new CstlServiceException("The xml filter does not have the good type:" + type,
                                                  INVALID_PARAMETER_VALUE, "filter");
                }
            } catch (JAXBException ex) {
                throw new CstlServiceException("The service was unable to read the xml filter:" + ex.getMessage(),
                                                  INVALID_PARAMETER_VALUE, "filter");
            } finally {
                if (unmarshaller != null) {
                    getMarshallerPool().release(unmarshaller);
                }
            }
        }
        // TODO
        QName typeNamee = typeNames.get(0);
        DeleteElementType delete = new DeleteElementType(filter, handle, typeNamee);
        return new TransactionType(service, version, handle, releaseAction, delete);
    }

    public static Map<String, String> getSchemaLocations() {
        return schemaLocations;
    }

    public MediaType getOutputFormat() {
        String format = worker.getOutputFormat();
        if (format.equals("text/xml; subtype=gml/3.1.1")) {
            return new MediaType("text", "xml; subtype=gml/3.1.1");
        } else {
            return MediaType.valueOf(format);
        }
    }
}

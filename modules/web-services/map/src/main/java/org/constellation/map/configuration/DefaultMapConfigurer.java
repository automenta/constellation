/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
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

package org.constellation.map.configuration;

import org.constellation.configuration.Provider;
import java.util.ArrayList;
import java.util.List;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.GeneralParameterValue;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.stream.XMLStreamException;
import org.constellation.configuration.AbstractConfigurer;
import org.constellation.configuration.AcknowlegementType;
import org.constellation.configuration.ProviderReport;
import org.constellation.provider.LayerProvider;
import org.constellation.provider.LayerProviderProxy;
import org.constellation.provider.LayerProviderService;
import org.constellation.provider.StyleProviderProxy;
import org.constellation.provider.configuration.ProviderParameters;
import org.constellation.ws.CstlServiceException;
import org.geotoolkit.xml.parameter.ParameterValueReader;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import static org.constellation.ws.ExceptionCode.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DefaultMapConfigurer extends AbstractConfigurer {

    private Map<String, LayerProviderService> services = new HashMap<String, LayerProviderService>();
    
    public DefaultMapConfigurer() {
        final Collection<LayerProviderService> availableServices = LayerProviderProxy.getInstance().getServices();
        for (LayerProviderService service: availableServices) {
            this.services.put(service.getName(), service);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object treatRequest(final String request, final MultivaluedMap<String, String> parameters, final Object objectRequest) throws CstlServiceException {
        if ("addSource".equalsIgnoreCase(request)) {
            return addSource(parameters, objectRequest);
        } else if ("modifySource".equalsIgnoreCase(request)) {
            return modifySource(parameters, objectRequest);
        } else if ("getSource".equalsIgnoreCase(request)) {
            return getSource(parameters);
        } else if ("removeSource".equalsIgnoreCase(request)) {
            return removeSource(parameters);
        } else if ("addLayer".equalsIgnoreCase(request)) {
            return addLayer(parameters, objectRequest);
        } else if ("removeLayer".equalsIgnoreCase(request)) {
            return removeLayer(parameters);
        } else if ("modifyLayer".equalsIgnoreCase(request)) {
            return modifyLayer(parameters, objectRequest);        
        } else if ("getDescriptor".equalsIgnoreCase(request)) {
            return getDescriptor(parameters);
        } else if ("listProviders".equalsIgnoreCase(request)) {
            return listProviders();
        }
        return null;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void beforeRestart() {
        StyleProviderProxy.getInstance().dispose();
        LayerProviderProxy.getInstance().dispose();
    }
    
    /**
     * Add a new source to the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType addSource(final MultivaluedMap<String, String> parameters,
            final Object objectRequest) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final LayerProviderService service = this.services.get(serviceName);
        if (service != null) {

            final ParameterValueReader reader = new ParameterValueReader(service.getDescriptor());
            try {
                // we read the soruce parameter to add
                reader.setInput(objectRequest);
                final ParameterValueGroup sourceToAdd = (ParameterValueGroup) reader.read();
                reader.dispose();
                LayerProviderProxy.getInstance().createProvider(service, sourceToAdd);

                return new AcknowlegementType("Success", "The source has been added");
            } catch (XMLStreamException ex) {
                throw new CstlServiceException(ex);
            } catch (IOException ex) {
                throw new CstlServiceException(ex);
            }
        } else {
            throw new CstlServiceException("No provider service for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
        }
    }
    
    /**
     * Modify a source in the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType modifySource(final MultivaluedMap<String, String> parameters,
            final Object objectRequest) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final String currentId = getParameter("id", true, parameters);
        final LayerProviderService service = services.get(serviceName);
        if (service != null) {

            final ParameterValueReader reader = new ParameterValueReader(service.getDescriptor());
            try {
                // we read the soruce parameter to add
                reader.setInput(objectRequest);
                final ParameterValueGroup sourceToModify = (ParameterValueGroup) reader.read();
                reader.dispose();

                final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
                for (LayerProvider p : providers) {
                    if (p.getId().equals(currentId)) {
                        p.updateSource(sourceToModify);
                        return new AcknowlegementType("Success", "The source has been modified");
                    }
                }
                return new AcknowlegementType("Failure", "Unable to find a source named:" + currentId);   

            } catch (XMLStreamException ex) {
                throw new CstlServiceException(ex);
            } catch (IOException ex) {
                throw new CstlServiceException(ex);
            }
        } else {
            throw new CstlServiceException("No descriptor for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
        }
    }
    
    /**
     * Return the configuration object  of the specified source.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return The configuration object  of the specified source.
     * @throws CstlServiceException 
     */
    private Object getSource(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String id = getParameter("id", true, parameters);
            
        Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(id)) {
                return p.getSource();
            }
        }

        return new AcknowlegementType("Failure", "Unable to find a source named:" + id);
    }
    
    /**
     * Remove a source in the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType removeSource(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String sourceId = getParameter("id", true, parameters);
        Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(sourceId)) {
                LayerProviderProxy.getInstance().removeProvider(p);
                return new AcknowlegementType("Success", "The source has been deleted");
            }
        }
        return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);
    }
    
    /**
     * Add a layer to the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType addLayer(final MultivaluedMap<String, String> parameters, 
            final Object objectRequest) throws CstlServiceException{
        
        final String sourceId = getParameter("id", true, parameters);            
        final ParameterValueReader reader = new ParameterValueReader(ProviderParameters.LAYER_DESCRIPTOR);
        try {
            // we read the soruce parameter to add
            reader.setInput(objectRequest);
            final ParameterValueGroup newLayer = (ParameterValueGroup) reader.read();
            reader.dispose();

            final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
            for (LayerProvider p : providers) {
                if (p.getId().equals(sourceId)) {
                    p.getSource().values().add(newLayer);
                    p.updateSource(p.getSource());
                    return new AcknowlegementType("Success", "The layer has been added");
                }
            }
            return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);


        } catch (XMLStreamException ex) {
            throw new CstlServiceException(ex);
        } catch (IOException ex) {
            throw new CstlServiceException(ex);
        }
    }
    
    /**
     * Remove a layer in the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType removeLayer(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String sourceId = getParameter("id", true, parameters);
        final String layerName = getParameter("layerName", true, parameters);

        final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(sourceId)) {
                for (GeneralParameterValue param : p.getSource().values()) {
                    if (param instanceof ParameterValueGroup) {
                        final ParameterValueGroup pvg = (ParameterValueGroup)param;
                        if (param.getDescriptor().equals(ProviderParameters.LAYER_DESCRIPTOR)) {
                            final ParameterValue value = pvg.parameter("name");
                            if (value.stringValue().equals(layerName)) {
                                p.getSource().values().remove(pvg);
                                break;
                            }
                        }
                    }
                }
                p.updateSource(p.getSource());
                return new AcknowlegementType("Success", "The layer has been removed");
            }
        }
        return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);
    }
    
    /**
     * Modify a layer to the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType modifyLayer(final MultivaluedMap<String, String> parameters, 
            final Object objectRequest) throws CstlServiceException{
        
        final String sourceId = getParameter("id", true, parameters);
        final String layerName = getParameter("layerName", true, parameters);

        final ParameterValueReader reader = new ParameterValueReader(ProviderParameters.LAYER_DESCRIPTOR);
        try {
            // we read the source parameter to add
            reader.setInput(objectRequest);
            ParameterValueGroup newLayer = (ParameterValueGroup) reader.read();
            reader.dispose();

            Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
            for (LayerProvider p : providers) {
                if (p.getId().equals(sourceId)) {
                    for (GeneralParameterValue param : p.getSource().values()) {
                        if (param instanceof ParameterValueGroup) {
                            ParameterValueGroup pvg = (ParameterValueGroup)param;
                            if (param.getDescriptor().equals(ProviderParameters.LAYER_DESCRIPTOR)) {
                                ParameterValue value = pvg.parameter("name");
                                if (value.stringValue().equals(layerName)) {
                                    p.getSource().values().remove(pvg);
                                    p.getSource().values().add(newLayer);
                                    break;
                                }
                            }
                        }
                    }
                    p.updateSource(p.getSource());
                    return new AcknowlegementType("Success", "The layer has been modified");
                }
            }
        } catch (XMLStreamException ex) {
            throw new CstlServiceException(ex);
        } catch (IOException ex) {
            throw new CstlServiceException(ex);
        }
        return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);
    }
    
    /**
     * Return the descriptor of the specified provider type.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return The descriptor of the specified provider type.
     * @throws CstlServiceException 
     */
    private ParameterDescriptorGroup getDescriptor(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final LayerProviderService service = services.get(serviceName);
        if (service != null) {
            return service.getDescriptor();
        }
        throw new CstlServiceException("No provider service for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
    }
    
    /**
     * Return a description of the available providers.
     * 
     * @return A description of the available providers.
     */
    private ProviderReport listProviders(){
        final List<Provider> providerDesc = new ArrayList<Provider>();
        for (LayerProviderService service: services.values()) {
            final List<String> sources = new ArrayList<String>();
            final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
            for (LayerProvider p : providers) {
                if (p.getService().equals(service)) {
                    sources.add(p.getId());
                }
            }
            providerDesc.add(new Provider(service.getName(), sources));
        }
        return new ProviderReport(providerDesc);
    }
    
}

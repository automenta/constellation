/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.constellation.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import org.apache.sis.util.logging.Logging;
import org.constellation.ServiceDef.Specification;
import org.constellation.admin.ConfigurationEngine;
import org.constellation.configuration.*;
import org.constellation.dto.AddLayer;
import org.constellation.dto.ParameterValues;
import org.constellation.dto.SimpleValue;
import org.constellation.map.configuration.MapConfigurer;
import static org.constellation.utils.RESTfulUtilities.ok;

/**
 * RESTful API for map services configuration.
 *
 * @author Bernard Fabien (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 0.9
 * @since 0.9
 */
@Path("/1/MAP/{spec}")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public final class MapServices {
    private static final Logger LOGGER = Logging.getLogger(MapServices.class);

    /**
     * @see MapConfigurer#getLayers(String)
     */
    @GET
    @Path("{id}/layer/all")
    public Response getLayers(final @PathParam("spec") String spec, final @PathParam("id") String id) throws Exception {
        try {
            return ok(new LayerList(getConfigurer(spec).getLayers(id)));
        } catch (ConfigurationException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @GET
    @Path("{id}/layersummary/all")
    public Response getLayersSummary(final @PathParam("spec") String spec, final @PathParam("id") String id) throws Exception {
        final List<Layer> layers;
        try {
            layers = getConfigurer(spec).getLayers(id);
        } catch (ConfigurationException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            throw e;
        }
        final List<LayerSummary> sumLayers = new ArrayList<>();
        for (final Layer lay : layers) {
            final DataBrief db = ConfigurationEngine.getData(lay.getName(), lay.getProviderID());
            sumLayers.add(new LayerSummary(lay,db));
        }
        return ok(sumLayers);
    }

    /**
     * @see MapConfigurer#addLayer(AddLayer)
     */
    @PUT
    @Path("{id}/layer")
    public Response addLayer(final @PathParam("spec") String spec, final @PathParam("id") String id, final AddLayer layer) throws Exception {
        try {
            getConfigurer(spec).addLayer(layer);
        } catch (ConfigurationException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            throw e;
        }
        return ok(AcknowlegementType.success("Layer \"" + layer.getLayerId() + "\" successfully added to " + spec + " service \"" + id + "\"."));
    }

    /**
     * @see org.constellation.map.configuration.MapConfigurer#removeLayer(String, javax.xml.namespace.QName)
     */
    @POST
    @Path("{id}/delete/{layerid}")
    public Response deleteLayer(final @PathParam("spec") String spec, final @PathParam("id") String serviceId, final @PathParam("layerid") String layerid, final SimpleValue layernmsp) throws Exception {
        try {
            final QName layerName;
            if (layernmsp != null) {
                layerName = new QName(layernmsp.getValue(), layerid);
            } else {
                layerName = new QName(layerid);
            }
            getConfigurer(spec).removeLayer(serviceId, layerName);
        } catch (ConfigurationException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            throw e;
        }
        return Response.ok().build();
    }

    @POST
    @Path("{id}/updatestyle")
    public Response updateLayerStyleForService(final @PathParam("spec") String spec, final @PathParam("id") String serviceId, final ParameterValues params) throws Exception {
        try {
            getConfigurer(spec).updateLayerStyle(serviceId, params.get("layerId"), params.get("spId"), params.get("styleName"));
        } catch (ConfigurationException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            throw e;
        }
        return Response.ok().build();
    }

    @POST
    @Path("{id}/removestyle")
    public Response removeLayerStyleForService(final @PathParam("spec") String spec, final @PathParam("id") String serviceId, final ParameterValues params) throws Exception {
        try {
            getConfigurer(spec).removeLayerStyle(serviceId, params.get("layerId"), params.get("spId"), params.get("styleName"));
        } catch (ConfigurationException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
            throw e;
        }
        return Response.ok().build();
    }

    /**
     * Returns the {@link MapConfigurer} instance from its {@link Specification}.
     *
     * @throws NotRunningServiceException if the service is not activated or if an error
     * occurred during its startup
     */
    private static MapConfigurer getConfigurer(final String specification) throws NotRunningServiceException {
        final Specification spec = Specification.fromShortName(specification);
        if (!spec.supportedWXS()) {
            throw new IllegalArgumentException(specification + " is not a valid OGC service.");
        }
        return (MapConfigurer) ServiceConfigurer.newInstance(spec);
    }


}

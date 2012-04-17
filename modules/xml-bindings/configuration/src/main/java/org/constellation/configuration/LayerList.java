/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2010, Geomatys
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
package org.constellation.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @since 0.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LayerList {

    @XmlElement(name="Layer")
    private List<Layer> layer;

    public LayerList() {

    }

    public LayerList(List<Layer> layer) {
        this.layer = layer;
    }

    /**
     * @return the layer
     */
    public List<Layer> getLayer() {
        if (layer == null) {
            layer = new ArrayList<Layer>();
        }
        return layer;
    }

    /**
     * @param layer the layer to set
     */
    public void setLayer(List<Layer> layer) {
        this.layer = layer;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LayerList) {
            final LayerList that = (LayerList) obj;
            return Utilities.equals(this.layer, that.layer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.layer != null ? this.layer.hashCode() : 0);
        return hash;
    }
}

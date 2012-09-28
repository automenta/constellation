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
package org.constellation.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
@XmlRootElement(name ="ProvidersReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProvidersReport {

    @XmlElement(name = "service")
    private List<ProviderServiceReport> providerServices;

    public ProvidersReport() {

    }

    public ProvidersReport(final List<ProviderServiceReport> services) {
        this.providerServices = services;
    }

    /**
     * @return the provider services
     */
    public List<ProviderServiceReport> getProviderServices() {
        if(providerServices == null){
            providerServices = new ArrayList<ProviderServiceReport>();
        }
        return providerServices;
    }

    /**
     * @param providerServices the provider services to set
     */
    public void setProviderServices(List<ProviderServiceReport> providerServices) {
        this.providerServices = providerServices;
    }

    /**
     * @param type The type (Postgis, shapefile, ...) of the wanted provider service.
     * @return ProviderServiceReport or null
     */
    public ProviderServiceReport getProviderService(final String type){
        for(ProviderServiceReport report : getProviderServices()){
            if(type.equals(report.getType())){
                return report;
            }
        }
        return null;
    }

    /**
     * @param id of the wanted provider.
     * @return ProviderReport or null
     */
    public ProviderReport getProvider(final String id){
        for(ProviderServiceReport report : getProviderServices()){
            final ProviderReport p = report.getProvider(id);
            if (p !=  null) {
                return p;
            }
        }
        return null;
    }

}

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
package org.constellation.process.service;

import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.constellation.ServiceDef;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class ServiceProcessCommon {

    private ServiceProcessCommon() {
    }

    public static final List<String> SUPPORTED_SERVICE_TYPE = UnmodifiableArrayList.wrap(new String[] {"WMS", "WFS", "WMTS", "WCS", "CSW", "SOS", "WPS", "WEBDAV"});
    
    public static String[] servicesAvaible() {

        final List<String> validValues = new ArrayList<>();

        final ServiceDef.Specification[] spec = ServiceDef.Specification.values();
         for (ServiceDef.Specification specification : spec) {
             if (!"NONE".equals(specification.name())) {
                 validValues.add(specification.name());
             }
         }

        return validValues.toArray(new String[validValues.size()]);
    }

}

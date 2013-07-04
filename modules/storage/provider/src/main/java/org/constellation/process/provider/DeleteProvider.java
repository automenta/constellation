/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.constellation.process.provider;

import java.util.Collection;
import org.constellation.process.AbstractCstlProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import static org.constellation.process.provider.DeleteProviderDescriptor.*;
import org.constellation.provider.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.ProcessDescriptor;

/**
 * Remove a provider from constellation. Throw an ProcessException if Provider is not found.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class DeleteProvider extends AbstractCstlProcess{

    public DeleteProvider( final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }


    @Override
    protected void execute() throws ProcessException {
        final String providerID = value(PROVIDER_ID, inputParameters);
        final Boolean deleteData = value(DELETE_DATA, inputParameters);

        boolean found = false;

        Collection<? extends Provider> providers = LayerProviderProxy.getInstance().getProviders();
        for (final Provider p : providers) {
            if (p.getId().equals(providerID)) {
                if (deleteData != null && deleteData) {
                    p.removeAll();
                }
                LayerProviderProxy.getInstance().removeProvider((LayerProvider) p);
                found = true;
                break;
            }
        }

        if (!found) {
            providers = StyleProviderProxy.getInstance().getProviders();
            for (final Provider p : providers) {
                if (p.getId().equals(providerID)) {
                    StyleProviderProxy.getInstance().removeProvider((StyleProvider) p);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            throw new ProcessException("Provider to remove not found.", this, null);
        }
    }

}

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
package org.constellation.process.provider.style;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.constellation.process.ConstellationProcessFactory;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.style.MutableStyle;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * Add a style to an existing style provider.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class SetStyleToStyleProviderDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "style_provider.set_style";
    public static final InternationalString ABSTRACT = new SimpleInternationalString("Add a style to an exising StyleProvider."
            + "If style name already exist, style will be update.");

    /*
     * StyleProvider identifier
     */
    public static final String PROVIDER_ID_NAME = "provider_id";
    private static final String PROVIDER_ID_REMARKS = "Identifier of the StyleProvider where the new style will be added.";
    public static final ParameterDescriptor<String> PROVIDER_ID =
            new DefaultParameterDescriptor(PROVIDER_ID_NAME, PROVIDER_ID_REMARKS, String.class, null, true);

    /*
     * Style name.
     */
    public static final String STYLE_ID_NAME = "style_Name";
    private static final String STYLE_ID_REMARKS = "Name/Identifier of the new style.";
    public static final ParameterDescriptor<String> STYLE_ID =
            new DefaultParameterDescriptor(STYLE_ID_NAME, STYLE_ID_REMARKS, String.class, null, false);

    /*
     * Style
     */
    public static final String STYLE_NAME = "style";
    private static final String STYLE_REMARKS = "Style to add.";
    public static final ParameterDescriptor<MutableStyle> STYLE =
            new DefaultParameterDescriptor(STYLE_NAME, STYLE_REMARKS, MutableStyle.class, null, true);


    /*
     * Owner
     */
    public static final String OWNER_NAME = "owner";
    private static final String OWNER_REMARKS = "The style owner login. Can be null.";
    public static final ParameterDescriptor<String> OWNER =
            new DefaultParameterDescriptor<String>(OWNER_NAME,OWNER_REMARKS, String.class, null, false);

    /**
     * Input parameters
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters", new GeneralParameterDescriptor[]{PROVIDER_ID, STYLE_ID, STYLE, OWNER});
    /**
     * Output parameters
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new DefaultParameterDescriptorGroup("OutputParameters");

    /**
     * Public constructor use by the ServiceRegistry to find and instantiate all ProcessDescriptor.
     */
    public SetStyleToStyleProviderDescriptor() {
        super(NAME, ConstellationProcessFactory.IDENTIFICATION, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public org.geotoolkit.process.Process createProcess(ParameterValueGroup input) {
        return new SetStyleToStyleProvider(this, input);
    }
}

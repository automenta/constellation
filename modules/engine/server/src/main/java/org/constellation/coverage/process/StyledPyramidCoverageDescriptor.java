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

package org.constellation.coverage.process;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.constellation.process.AbstractCstlProcess;
import org.constellation.util.StyleReference;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class StyledPyramidCoverageDescriptor extends AbstractPyramidCoverageDescriptor {
    
    public static final String NAME = "coverage.pyramid.styled";
    public static final InternationalString ABSTRACT = new SimpleInternationalString("Build a styled pyramid from the specified file.");

    public static final String STYLE_NAME = "pyramid_style";
    private static final String STYLE_REMARKS = "The style to apply to the pyramid.";
    public static final ParameterDescriptor<StyleReference> STYLE =
            new DefaultParameterDescriptor<>(STYLE_NAME, STYLE_REMARKS, StyleReference.class, null, true);

    public static final ParameterDescriptorGroup INPUT_DESC = new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{IN_COVERAGE_REF, ORIGINAL_DATA, PYRAMID_NAME, PYRAMID_FOLDER, STYLE,
                    PROVIDER_OUT_ID, DOMAIN, PYRAMID_DATASET, PYRAMID_CRS});
    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC = new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{OUT_PYRAMID_PROVIDER_CONF});

    /**
     * Public constructor use by the ServiceRegistry to find and instantiate all ProcessDescriptor.
     */
    public StyledPyramidCoverageDescriptor() {
        super(NAME, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    public static final StyledPyramidCoverageDescriptor INSTANCE = new StyledPyramidCoverageDescriptor();

    @Override
    public AbstractCstlProcess buildProcess(ParameterValueGroup input) {
        return new StyledPyramidCoverageProcess(this, input);
    }
}

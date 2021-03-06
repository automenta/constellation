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

package org.constellation.json.metadata.binding;

import java.io.Serializable;
import java.util.List;

/**
 * Pojo class used for Jackson that represents the binding for root block
 * in metadata template json.
 *
 * @author Mehdi Sidhoum (Geomatys).
 * @since 0.9
 */
public class RootBlock implements Serializable {

    private String name;
    private int multiplicity;
    private List<SuperBlockObj> children;

    public RootBlock(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public List<SuperBlockObj> getChildren() {
        return children;
    }

    public void setChildren(List<SuperBlockObj> children) {
        this.children = children;
    }
}

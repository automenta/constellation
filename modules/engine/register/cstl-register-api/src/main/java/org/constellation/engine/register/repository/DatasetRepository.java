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

package org.constellation.engine.register.repository;

import java.util.List;
import org.constellation.engine.register.Dataset;
import org.constellation.engine.register.DatasetXCsw;

/**
 *
 * @author Guilhem Legal
 */
public interface DatasetRepository {
    
    List<Dataset> findAll();
    
    Dataset insert(Dataset dataset);
    
    int update(Dataset dataset);
            
    Dataset findByMetadataId(String metadataId);
    
    Dataset findByIdentifierAndDomainId(String datasetIdentifier, Integer domainId);
    
    Dataset findByIdentifier(String datasetIdentifier);
    
    Dataset findByIdentifierWithEmptyMetadata(String datasetIdentifier);

    Dataset findById(int datasetId);
    
    void remove(int id);
    
    List<DatasetXCsw> getCswLinkedDataset(final int cswId);
    
    DatasetXCsw addDatasetToCSW(final int serviceID, final int datasetID, final boolean allData);
    
    void removeDatasetFromCSW(final int serviceID, final int datasetID);
    
    void removeDatasetFromAllCSW(final int datasetID);
    
    void removeAllDatasetFromCSW(final int serviceID);
}

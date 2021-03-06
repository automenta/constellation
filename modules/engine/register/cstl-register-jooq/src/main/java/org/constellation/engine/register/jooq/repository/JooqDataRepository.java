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
package org.constellation.engine.register.jooq.repository;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.constellation.engine.register.*;
import org.constellation.engine.register.helper.DataHelper;
import org.constellation.engine.register.i18n.DataWithI18N;
import org.constellation.engine.register.jooq.Tables;
import org.constellation.engine.register.jooq.tables.records.DataRecord;
import org.constellation.engine.register.jooq.tables.records.DataXDataRecord;
import org.constellation.engine.register.repository.DataRepository;
import org.constellation.engine.register.repository.DomainRepository;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.constellation.engine.register.jooq.tables.records.DataXCswRecord;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.constellation.engine.register.jooq.Tables.*;

@Component
public class JooqDataRepository extends AbstractJooqRespository<DataRecord, Data> implements DataRepository {

    @Autowired
    private DomainRepository domainRepository;

    public JooqDataRepository() {
        super(Data.class, DATA);
    }

    @Override
    public Data findById(int id) {
        return dsl.select().from(DATA).where(DATA.ID.eq(id)).fetchOneInto(Data.class);
    }

    @Override
    public Data fromLayer(String layerAlias, String providerId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Data create(Data data) {
        DataRecord newRecord = DataHelper.copy(data, dsl.newRecord(DATA));
        newRecord.store();
        return newRecord.into(Data.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public int delete(int id) {
        return dsl.delete(DATA).where(DATA.ID.eq(id)).execute();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public int delete(String namespaceURI, String localPart, int providerId) {
        Condition whereClause = buildWhereClause(namespaceURI, localPart, providerId);
        return dsl.delete(DATA).where(whereClause).execute();

    }

    private Condition buildWhereClause(String namespaceURI, String localPart, int providerId) {
        Condition whereClause = DATA.NAME.eq(localPart).and(DATA.PROVIDER.eq(providerId));
        if (namespaceURI != null) {
            return whereClause.and(DATA.NAMESPACE.eq(namespaceURI));
        }
        return whereClause;
    }

    private Condition buildWhereClause(String namespaceURI, String localPart, String providerId) {
        Condition whereClause = Tables.PROVIDER.IDENTIFIER.eq(providerId).and(DATA.NAME.eq(localPart));
        if (namespaceURI != null && ! namespaceURI.isEmpty()) {
            return whereClause.and(DATA.NAMESPACE.eq(namespaceURI));
        }
        return whereClause;
    }

    @Override
    public Data findDataFromProvider(String namespaceURI, String localPart, String providerId) {
        final Condition whereClause = buildWhereClause(namespaceURI, localPart, providerId);
        return dsl.select(DATA.fields()).from(DATA).join(Tables.PROVIDER).onKey().where(whereClause).fetchOneInto(Data.class);
    }

    @Override
    public Data findByMetadataId(String metadataId) {
        return dsl.select().from(DATA).where(DATA.METADATA_ID.eq(metadataId)).fetchOneInto(Data.class);
    }

    @Override
    public List<Data> findByProviderId(Integer id) {
        return dsl.select().from(DATA).where(DATA.PROVIDER.eq(id)).fetchInto(Data.class);
    }
    
    @Override
    public List<Data> findByDatasetId(Integer id) {
        return dsl.select().from(DATA)
                .where(DATA.DATASET_ID.eq(id))
                .and(DATA.INCLUDED.eq(Boolean.TRUE))
                .and(DATA.HIDDEN.isNull().or(DATA.HIDDEN.isFalse())).fetchInto(Data.class);
    }
    
    @Override
    public List<Data> findAllByDatasetId(Integer id) {
        return dsl.select().from(DATA).where(DATA.DATASET_ID.eq(id)).fetchInto(Data.class);
    }

    @Override
    public DataWithI18N getDescription(Data data) {
        Result<Record> fetch = dsl.select().from(DATA_I18N).where(DATA_I18N.DATA_ID.eq(data.getId())).fetch();
        ImmutableMap<String, DataI18n> dataI18ns = Maps.uniqueIndex(fetch.into(DataI18n.class), new Function<DataI18n, String>() {
            @Override
            public String apply(DataI18n input) {
                return input.getLang();
            }
        });
        return new DataWithI18N(data, dataI18ns);
    }

    @Override
    public Data findByNameAndNamespaceAndProviderId(String localPart, String namespaceURI, Integer providerId) {
        return dsl.select().from(DATA).where(DATA.PROVIDER.eq(providerId)).and(DATA.NAME.eq(localPart))
                .and(DATA.NAMESPACE.eq(namespaceURI)).fetchOneInto(Data.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(Data data) {

        dsl.update(DATA)
                .set(DATA.DATE, data.getDate())
                .set(DATA.ISO_METADATA, data.getIsoMetadata())
                .set(DATA.METADATA, data.getMetadata())
                .set(DATA.METADATA_ID, data.getMetadataId())
                .set(DATA.NAME, data.getName())
                .set(DATA.NAMESPACE, data.getNamespace())
                .set(DATA.OWNER, data.getOwner())
                .set(DATA.PROVIDER, data.getProvider())
                .set(DATA.SENSORABLE, data.isSensorable())
                .set(DATA.SUBTYPE, data.getSubtype())
                .set(DATA.TYPE, data.getType())
                .set(DATA.INCLUDED, data.isIncluded())
                .set(DATA.DATASET_ID, data.getDatasetId())
                .set(DATA.FEATURE_CATALOG, data.getFeatureCatalog())
                .set(DATA.STATS_RESULT, data.getStatsResult())
                .set(DATA.STATS_STATE, data.getStatsState())
                .set(DATA.RENDERED, data.isRendered())
                .set(DATA.HIDDEN, data.isHidden())
                .set(DATA.MD_COMPLETION,data.getMdCompletion())
                .where(DATA.ID.eq(data.getId()))
                .execute();

    }

    @Override
    public Map<Domain, Boolean> getLinkedDomains(int dataId) {

        List<Integer> domainIds = dsl.select(DATA_X_DOMAIN.DOMAIN_ID).from(DATA_X_DOMAIN).where(DATA_X_DOMAIN.DATA_ID.eq(dataId))
                .fetch(DATA_X_DOMAIN.DOMAIN_ID);
        Map<Domain, Boolean> result = new LinkedHashMap<>();
        for (Domain domain : domainRepository.findByIds(domainIds)) {
            result.put(domain, true);
        }

        for (Domain domain : domainRepository.findByIdsNotIn(domainIds)) {
            result.put(domain, false);
        }
        return result;
    }

    @Override
    public Data findByIdentifierWithEmptyMetadata(String localPart) {
        return dsl.select().from(DATA).where(DATA.NAME.eq(localPart)).and(DATA.METADATA_ID.isNull()).and(DATA.ISO_METADATA.isNull()).fetchOneInto(Data.class);
    }

    @Override
    public List<Data> getCswLinkedData(final int cswId) {
        return dsl.select(DATA.fields()).from(DATA).join(DATA_X_CSW).onKey(DATA_X_CSW.DATA_ID).where(DATA_X_CSW.CSW_ID.eq(cswId)).fetchInto(Data.class);
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public DataXCsw addDataToCSW(final int serviceID, final int dataID) {
        final DataXCsw dxc = dsl.select().from(DATA_X_CSW).where(DATA_X_CSW.CSW_ID.eq(serviceID)).and(DATA_X_CSW.DATA_ID.eq(dataID)).fetchOneInto(DataXCsw.class);
        if (dxc == null) {
            DataXCswRecord newRecord = dsl.newRecord(DATA_X_CSW);
            newRecord.setCswId(serviceID);
            newRecord.setDataId(dataID);
            newRecord.store();
            return newRecord.into(DataXCsw.class);
        }
        return dxc;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void removeDataFromCSW(int serviceID, int dataID) {
        dsl.delete(DATA_X_CSW).where(DATA_X_CSW.CSW_ID.eq(serviceID)).and(DATA_X_CSW.DATA_ID.eq(dataID)).execute();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void removeDataFromAllCSW(int dataID) {
        dsl.delete(DATA_X_CSW).where(DATA_X_CSW.DATA_ID.eq(dataID)).execute();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void removeAllDataFromCSW(int serviceID) {
        dsl.delete(DATA_X_CSW).where(DATA_X_CSW.CSW_ID.eq(serviceID)).execute();
    }

    @Override
    public void linkDataToData(final int dataId, final int childId) {
        final DataXData dxd = dsl.select().from(DATA_X_DATA).where(DATA_X_DATA.DATA_ID.eq(dataId)).and(DATA_X_DATA.CHILD_ID.eq(childId)).fetchOneInto(DataXData.class);
        if (dxd == null) {
            DataXDataRecord newRecord = dsl.newRecord(DATA_X_DATA);
            newRecord.setDataId(dataId);
            newRecord.setChildId(childId);
            newRecord.store();
        }
    }

    @Override
    public List<Data> getDataLinkedData(final int dataId) {
        return dsl.select(DATA.fields()).from(DATA).join(DATA_X_DATA).onKey(DATA_X_DATA.CHILD_ID).where(DATA_X_DATA.DATA_ID.eq(dataId)).fetchInto(Data.class);
    }

    @Override
    public List<Data> findStatisticLess() {
        return dsl.select().from(DATA)
                .where(DATA.TYPE.eq("COVERAGE"))
                .and(DATA.RENDERED.isNull().or(DATA.RENDERED.isFalse())).fetchInto(Data.class);
    }
}

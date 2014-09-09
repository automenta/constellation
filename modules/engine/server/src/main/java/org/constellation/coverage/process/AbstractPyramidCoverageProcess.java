package org.constellation.coverage.process;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArraysExt;
import org.constellation.process.AbstractCstlProcess;
import org.constellation.provider.DataProvider;
import org.constellation.provider.DataProviderFactory;
import org.constellation.provider.DataProviders;
import org.constellation.provider.ProviderFactoryType;
import org.constellation.provider.configuration.ProviderParameters;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.filestore.FileCoverageStoreFactory;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.xmlstore.XMLCoverageReference;
import org.geotoolkit.coverage.xmlstore.XMLCoverageStoreFactory;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.parameter.Parameters.value;

/**
 * @author Quentin Boileau (Geomatys)
 */
public abstract class AbstractPyramidCoverageProcess extends AbstractCstlProcess {

    private static final String TILE_FORMAT = "PNG";
    protected static final int TILE_SIZE = 256;

    public AbstractPyramidCoverageProcess(ProcessDescriptor desc, ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    protected CoverageStore createXMLCoverageStore(final File pyramidFolder, final Name layerName)
            throws DataStoreException, ProcessException, MalformedURLException {

        final ParameterValueGroup storeParams = XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        getOrCreate(XMLCoverageStoreFactory.NAMESPACE, storeParams).setValue("no namespace");
        getOrCreate(XMLCoverageStoreFactory.PATH, storeParams).setValue(pyramidFolder.toURI().toURL());

        final CoverageStore store = CoverageStoreFinder.open(storeParams);
        if (store == null) {
            throw new ProcessException("Can't initialize XMLCoverageStore.", this, null);
        }

        final XMLCoverageReference covRef = (XMLCoverageReference)store.create(layerName);
        covRef.setPackMode(ViewType.RENDERED);
        covRef.setPreferredFormat(TILE_FORMAT);
        return store;
    }

    protected CoordinateReferenceSystem getPyramidCRS() {
        return CommonCRS.defaultGeographic();
    }

    protected CoverageStore getCoverageStoreFromInput(final String imageFile, final String imageType)
            throws ProcessException, MalformedURLException {
        final ParameterValueGroup params = FileCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        getOrCreate(FileCoverageStoreFactory.PATH, params).setValue(new File(imageFile).toURI().toURL());
        getOrCreate(FileCoverageStoreFactory.TYPE, params).setValue(imageType);

        try {
            return CoverageStoreFinder.open(params);
        } catch (DataStoreException ex) {
            throw new ProcessException("Error while opening output datastore", this, ex);
        }
    }

    protected CoverageReference getOrCreateCRef(CoverageStore coverageStore, Name coverageName) throws DataStoreException {
        CoverageReference cv = null;
        for (Name n : coverageStore.getNames()) {
            if (n.getLocalPart().equals(coverageName.getLocalPart())) {
                cv = coverageStore.getCoverageReference(n);
            }
        }
        if (cv == null) {
            cv = coverageStore.create(coverageName);
        }
        return cv;
    }

    /**
     * Compute bounds of pyramid CRS
     * @return
     */
    protected Envelope getPyramidWorldEnvelope(){
        final CoordinateReferenceSystem pyramidCRS = getPyramidCRS();
        return CRS.getEnvelope(pyramidCRS);
    }

    /**
     * Re-use previous pyramid scales or compute new scales from input coverage.
     * @param inputCoverage
     * @param pyramidRef
     * @return scales array
     * @throws DataStoreException
     * @throws FactoryException
     */
    protected double[] getPyramidScales(GridCoverage2D inputCoverage, PyramidalCoverageReference pyramidRef)
            throws DataStoreException, FactoryException {
        final CoordinateReferenceSystem pyramidCRS = getPyramidCRS();

        final PyramidSet pyramidSet = pyramidRef.getPyramidSet();
        final Collection<Pyramid> pyramids = pyramidSet.getPyramids();

        Pyramid pyramid = null;
        for (Pyramid p : pyramids) {
            if (CRS.equalsIgnoreMetadata(p.getCoordinateReferenceSystem(), pyramidCRS)) {
                pyramid = p;
                break;
            }
        }
        double[] scales;
        if (pyramid != null) {
            scales = pyramid.getScales();
        } else {
            scales = computeScales(inputCoverage);
        }
        return scales;
    }

    private double[] computeScales(GridCoverage2D coverage) {
        final Envelope coverageEnv = coverage.getEnvelope();
        final GridGeometry2D gg = coverage.getGridGeometry();
        int gridspan = gg.getExtent2D().getSpan(0);

        //calculate scales
        final double spanX = coverageEnv.getSpan(0);
        final double baseScale = spanX / gridspan;
        double scale = spanX / TILE_SIZE;
        double[] scales = new double[0];
        while (true) {
            if (scale <= baseScale) {
                //fit to exact match to preserve base quality.
                scale = baseScale;
            }
            scales = ArraysExt.insert(scales, scales.length, 1);
            scales[scales.length - 1] = scale;

            if (scale <= baseScale) {
                break;
            }
            scale = scale / 2;
        }
        return scales;
    }

    protected DataProvider createProvider(String providerID, CoverageStore store, Integer domainId) throws ProcessException {
        final DataProvider outProvider;
        try {
            //get store configuration
            final ParameterValueGroup storeConf = store.getConfiguration();
            final String namespace = value(AbstractCoverageStoreFactory.NAMESPACE, storeConf);
            final URL pyramidFolder = value(XMLCoverageStoreFactory.PATH, storeConf);

            //create provider configuration
            final String factoryName = ProviderFactoryType.COVERAGE_STORE.getType();
            final DataProviderFactory factory = DataProviders.getInstance().getFactory(factoryName);
            final ParameterValueGroup pparams = factory.getProviderDescriptor().createValue();
            ParametersExt.getOrCreateValue(pparams, ProviderParameters.SOURCE_ID_DESCRIPTOR.getName().getCode()).setValue(providerID);
            ParametersExt.getOrCreateValue(pparams, ProviderParameters.SOURCE_TYPE_DESCRIPTOR.getName().getCode()).setValue(factoryName);
            final ParameterValueGroup choiceparams = ParametersExt.getOrCreateGroup(pparams, factory.getStoreDescriptor().getName().getCode());

            final ParameterValueGroup xmlpyramidparams = ParametersExt.getOrCreateGroup(choiceparams, XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.getName().getCode());
            ParametersExt.getOrCreateValue(xmlpyramidparams, XMLCoverageStoreFactory.PATH.getName().getCode()).setValue(pyramidFolder);
            ParametersExt.getOrCreateValue(xmlpyramidparams, XMLCoverageStoreFactory.NAMESPACE.getName().getCode()).setValue(namespace);
            outProvider = DataProviders.getInstance().createProvider(providerID, factory, pparams);

            if (domainId != null) {
                int count = domainRepository.addProviderDataToDomain(providerID, domainId);
                LOGGER.info("Added " + count + " data to domain " + domainId);
            }

        } catch (Exception ex) {
            throw new ProcessException("Failed to create pyramid provider "+ex.getMessage(), this, ex);
        }
        return outProvider;
    }
}
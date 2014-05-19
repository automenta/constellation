/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2014, Geomatys
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
package org.constellation.gui;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.Route;
import juzu.View;
import juzu.impl.request.Request;
import juzu.request.RequestParameter;
import org.apache.commons.io.FilenameUtils;
import org.apache.sis.util.logging.Logging;
import org.constellation.dto.DataInformation;
import org.constellation.dto.DataMetadata;
import org.constellation.dto.MetadataLists;
import org.constellation.dto.ParameterValues;
import org.constellation.gui.service.ProviderManager;

import javax.inject.Inject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Benjamin Garcia (Geomatys).
 * @author Fabien Bernard (Geomatys).
 */
public class RasterController {

    private static final Logger LOGGER = Logging.getLogger(RasterController.class);

        private static final String[] SUFFIXES = new String[] {"nc", "ncml", "cdf", "grib", "grib1", "grib2", "grb", "grb1", "grb2", "grd"};

    /**
     * Manager used to call constellation server side.
     */
    @Inject
    protected ProviderManager providerManager;

    @Inject
    @Path("raster_description.gtmpl")
    org.constellation.gui.templates.raster_description rasterDescription;

    @Inject
    @Path("netcdf_coverage_listing.gtmpl")
    org.constellation.gui.templates.netcdf_coverage_listing netcdf_coverageListing;

    @View
    @Route("/raster/description")
    public Response showRaster(final String returnURL, final String metadataUploaded, final String creationMode) throws IOException {
        final Locale userLocale = Request.getCurrent().getUserContext().getLocale();
        final MetadataLists codeLists = providerManager.getMetadataCodeLists(userLocale.toString());
        return rasterDescription.with().datainformation(DataInformationContainer.getInformation())
                .returnURL(returnURL)
                .dateTypes(codeLists.getDateTypes())
                .locales(codeLists.getLocales())
                .roles(codeLists.getRoles())
                .topics(codeLists.getCategories())
                .userLocale(userLocale.getLanguage())
                .metadataUploaded(metadataUploaded)
                .creationMode(creationMode)
                .ok().withMimeType("text/html");
    }

    @Action
    @Route("/raster/create")
    public Response createProvider(String returnURL, DataMetadata metadataToSave, String date, String keywords, String metadataUploaded) {
        final DataInformation information = DataInformationContainer.getInformation();
        final String extension = FilenameUtils.getExtension(information.getPath());
        final List<String> suffixes = new ArrayList<>(0);

        Response rep;
        Collections.addAll(suffixes, SUFFIXES);

        providerManager.createProvider("coverage-store", information.getName(), information.getPath(), information.getDataType(), null, "coverage-file");

        //if it's netCDF, we don't pyramid data and go on a specific page.
        if(suffixes.contains(extension)){
            rep = RasterController_.getNetCDFListing(returnURL, information.getName());
        }else{
            providerManager.pyramidData(information.getName(), information.getPath());
            rep = Response.redirect(returnURL);
        }

        boolean metadataUpload = Boolean.parseBoolean(metadataUploaded);
        if(!metadataUpload){
            saveEditedMetadata(metadataToSave, date, keywords, information);
        }

        return rep;
    }

    @View
    @Route("/netcdf/listing")
    public Response getNetCDFListing(final String returnUrl, final String providerId){
        ParameterValues coveragesPV = providerManager.getCoverageList(providerId);
        Map<String, String> coveragesMap = coveragesPV.getValues();
        return netcdf_coverageListing.with().returnURL(returnUrl).coveragesMap(coveragesMap).providerId(providerId).ok().withMimeType("text/html");
    }

    /**
     * Save Metadata if it was edited (not given by user)
     * @param metadataToSave {@link org.constellation.dto.DataMetadata} which define global metadata except date and keywords
     * @param date data date
     * @param keywords data keywords
     * @param information information receive from server.
     */
    private void saveEditedMetadata(final DataMetadata metadataToSave, final String date, final String keywords, final DataInformation information) {
        final Locale userLocale = Request.getCurrent().getUserContext().getLocale();
        DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT, userLocale);
        Date metadataDate = null;
        try {
            metadataDate = formatDate.parse(date);
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "can't parse data", e);
        }
        metadataToSave.setLocaleMetadata(userLocale.toString());
        metadataToSave.setDate(metadataDate);


        metadataToSave.setDataName(information.getName());
        metadataToSave.setDataPath(information.getPath());
        metadataToSave.setType(information.getDataType());

        //split keywords
        String[] keywordArray = keywords.split(",");
        List<String> keywordList = new ArrayList<>(0);
        for (int i = 0; i < keywordArray.length; i++) {
            String keyword = keywordArray[i];
            keywordList.add(keyword);
        }
        metadataToSave.setKeywords(keywordList);

        //create pyramid, provider and metadata
        providerManager.saveISO19115Metadata(metadataToSave);
    }

    @Action
    @Route("/netcdf/save")
    public Response saveCRSModification(final String returnUrl, final String providerId){
        final Map<String, RequestParameter> parameters = Request.getCurrent().getParameters();

        final Map<String, String> dataCRSModified = new HashMap<>(0);

        ///remove url & providerId from parameters to loop without test
        parameters.remove("returnUrl");
        parameters.remove("providerId");
        if (parameters.size()>0){
            for (String key : parameters.keySet()) {
                dataCRSModified.put(key, parameters.get(key).getValue());
            }
        }

        providerManager.saveCRSModifications(dataCRSModified, providerId);
        return Response.redirect(returnUrl);
    }
}

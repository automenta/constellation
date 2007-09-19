/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.sicade.observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Geotools dependencies
import org.geotools.resources.Utilities;
import net.sicade.catalog.Entry;

// openGis dependencies
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeatureRelation;
import org.opengis.observation.sampling.SurveyProcedure;

/**
 * Implémentation d'une entrée représentant une {@link SamplingFeature station}.
 *
 * @version $Id$
 * @author Antoine Hnawia
 * @author Martin Desruisseaux
 *
 * @todo L'implémentation actuelle n'est pas <cite>serializable</cite> du fait qu'elle nécessite
 *       une connexion à la base de données. Une version future devrait rétablir la connexion au
 *       moment de la <cite>deserialization</cite>.
 */
public class SamplingFeatureEntry extends Entry implements SamplingFeature {
    /**
     * Pour compatibilités entre les enregistrements binaires de différentes versions.
     */
    private static final long serialVersionUID = 8822736167506306189L;

    /**
     * L'identifiant alphanumérique de la station.
     */
    private String identifier;
    
    
    /**
     * La description de la station.
     */
    private String description;
    
    /**
     * 
     */
    private Collection<SamplingFeatureRelation> relatedSamplingFeature;
    
    /**
     * Les Observations
     */
    private Collection<Observation> relatedObservation;
    
    /**
     * Les features designé
     */
    private Collection<Object> sampledFeature; 
    
    /**
     * Connexion vers la table des "survey details"
     * Optionnel peut etre {@code null}
     */
    private SurveyProcedure surveyDetail;
    

    /**
     * Connexion vers la table des observations. Contrairement à la plupart des autres
     * entrées du paquet {@code net.sicade.observation}, les observations ne seront pas
     * conservées dans une cache car elle sont potentiellement très nombreuses. Il nous
     * faudra donc conserver la connexion en permanence.
     */
    private final ObservationTable<? extends Observation> observations = null;
    

    /**
     * Constructeur vide utilisé par JAXB.
     */
    private SamplingFeatureEntry(){}
    
    /** 
     * Construit une entrée pour l'identifiant de station spécifié.
     * adapté au modele de BRGM.
     *
     * @param identifier  L'identifiant numérique de la station.
     * @param name        Le nom de la station.
     * @param description Une description de la station.
     * @param le 
     */
    public SamplingFeatureEntry(   final String            identifier,
                                   final String            name,
                                   final String            description,
                                   final String            sampledFeature)
    {
        super(name, description);
        this.identifier             = identifier;
        this.description            = description;
        this.sampledFeature = new ArrayList<Object>();
        this.sampledFeature.add(sampledFeature);
        this.surveyDetail           = null;
        this.relatedSamplingFeature = null;
        this.relatedObservation     = null;
        this.sampledFeature         = null;
    }
    
    public SamplingFeatureEntry(   final String            identifier,
                                   final String            name,
                                   final String            description,
                                   final List<SamplingFeatureRelation> relatedSamplingFeature,
                                   final List<Observation> relatedObservation,
                                   final List<Object>      sampledFeature,
                                   final SurveyProcedure   surveyDetail)
    {
        super(name, description);
        this.identifier             = identifier;
        this.description            = description;
        this.surveyDetail           = surveyDetail;
        this.relatedSamplingFeature = relatedSamplingFeature;
        this.relatedObservation     = relatedObservation;
        this.sampledFeature         = sampledFeature;
    }

    /**
     * retourne l'identifiant de la station.
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * retourne la description de la station.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized Collection<SamplingFeatureRelation> getRelatedSamplingFeatures() {
    
        return relatedSamplingFeature;
    }
  
    
    /**
     * {@inheritDoc}
     */
    public synchronized Collection<Observation> getRelatedObservations() {
       
        return relatedObservation;
    }
    
     /**
     * {@inheritDoc}
     */
    public synchronized Collection<Object> getSampledFeatures() {
        
        return sampledFeature;
    }

    public SurveyProcedure getSurveyDetail() {
        return this.surveyDetail;
    }
    

    /**
     * Retourne le code numérique identifiant cette entrée.
     */
    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    /**
     * Vérifie que cette station est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final SamplingFeatureEntry that = (SamplingFeatureEntry) object;
            return Utilities.equals(this.identifier,             that.identifier) &&
                   Utilities.equals(this.surveyDetail,           that.surveyDetail)   &&
                   Utilities.equals(this.description,            that.description)   && 
                   Utilities.equals(this.relatedObservation,     that.relatedObservation) &&
                   Utilities.equals(this.relatedSamplingFeature, that.relatedSamplingFeature) &&
                   Utilities.equals(this.sampledFeature,         that.sampledFeature);
        }
        return false;
    }

  

   
}

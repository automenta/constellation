/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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
package org.constellation.gml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.constellation.catalog.Entry;
import org.geotools.util.Utilities;

/**
 * Unité de mesure.
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseUnit")
public class UnitOfMeasureEntry extends Entry { //implements BaseUnit {
    /**
     * l'identifiant de l'unité ( exemple cm, és, ...)
     */
    @XmlAttribute
    private String id;
    
    /**
     * Le nom de l'unité.
     */
    private String name;
    
    /**
     * le type de l'unité de mesure (longueur, temporelle, ...).
     */
    private String quantityType;
    
    /**
     * Le system qui definit cette unité de mesure.
     */
    private String unitsSystem;
    
    /**
     * constructeur videé utilisé par JAXB
     */
    protected UnitOfMeasureEntry() {}
    
    /**
     * Créé une nouvelle unité de mesure.
     */
    public UnitOfMeasureEntry(String id, String name, String quantityType, String unitsSystem) {
        super(name);
        this.id           = id;
        this.name         = name;
        this.quantityType = quantityType;
        this.unitsSystem  = unitsSystem;
    }
    
    /**
     * Retourne l'identifiant.
     */
    public String getId() {
        return id;
    }
    
    /**
     * retourne le type de l'unité de mesure.
     */
    public String getQuantityType() {
        return quantityType;
    }
    
    /**
     * retourne le nom du systeme qui definit cette unité.
     */
    public String getUnitsSystem() {
        return unitsSystem;
    }
    
    /**
     * Vérifie si cette entrée est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        final UnitOfMeasureEntry that = (UnitOfMeasureEntry) object;
        return Utilities.equals(this.name,  that.name) &&
               Utilities.equals(this.id,   that.id) &&
               Utilities.equals(this.quantityType, that.quantityType) &&
               Utilities.equals(this.unitsSystem, that.unitsSystem);
    }
    
    /**
     * Retourne une representation de l'objet.
     */
     @Override
     public String toString() {
         StringBuilder s = new StringBuilder();
         s.append(" id= ").append(id).append(" name=").append(name).append(" quantity type=")
                 .append(quantityType).append(" unitSystem=").append(unitsSystem);
         return s.toString();
     }
}

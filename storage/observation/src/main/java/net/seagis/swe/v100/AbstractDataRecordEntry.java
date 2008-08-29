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

package net.seagis.swe.v100;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlSeeAlso({SimpleDataRecordEntry.class})
@XmlType(name="AbstractDataRecord")
public class AbstractDataRecordEntry extends AbstractDataComponentEntry {
    
    /**
     * constructeur utilisé par jaxB.
     */
    public AbstractDataRecordEntry() {}
            
    /**
     * super-constructeur appellé par les sous-classes.
     */
    public AbstractDataRecordEntry(final String id, final String definition, boolean fixed) {
        super(id, definition, fixed);
    }
    
}

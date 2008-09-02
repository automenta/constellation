/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008, Institut de Recherche pour le Développement
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

package net.seagis.cat.csw;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Mehdi Sidhoum
 */
public interface SearchResults {
    
    /**
     * Gets the value of the any property.
     * (unModifiable)
     */
    public List<Object> getAny();

    /**
     * Gets the value of the resultSetId property.
     * 
     */
    public String getResultSetId();

    /**
     * Sets the value of the resultSetId property.
     * 
    */
    public void setResultSetId(String value);

    /**
     * Gets the value of the elementSet property.
     * 
     */
    public ElementSet getElementSet();

    /**
     * Gets the value of the recordSchema property.
     * 
     */
    public String getRecordSchema();

    /**
     * Sets the value of the recordSchema property.
     * 
     */
    public void setRecordSchema(String value);

    /**
     * Gets the value of the numberOfRecordsMatched property.
     * 
     */
    public int getNumberOfRecordsMatched();
    
    /**
     * Sets the value of the numberOfRecordsMatched property.
     * 
     */
    public void setNumberOfRecordsMatched(int value);

    /**
     * Gets the value of the numberOfRecordsReturned property.
     * 
     */
    public int getNumberOfRecordsReturned();

    /**
     * Sets the value of the numberOfRecordsReturned property.
     * 
     */
    public void setNumberOfRecordsReturned(int value);

    /**
     * Gets the value of the nextRecord property.
     * 
     */
    public int getNextRecord();

    /**
     * Sets the value of the nextRecord property.
     * 
     */
    public void setNextRecord(int value);

    /**
     * Gets the value of the expires property.
     * 
     */
    public XMLGregorianCalendar getExpires();

    /**
     * Sets the value of the expires property.
     * 
     */
    public void setExpires(XMLGregorianCalendar value);

}

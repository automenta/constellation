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
package net.seagis.ows.v110;

import net.seagis.coverage.web.ServiceVersion;
import net.seagis.coverage.web.WebServiceException;
import net.seagis.ows.OWSExceptionCode;

/**
 *
 * @author Guilhem Legal
 */
public class OWSWebServiceException extends WebServiceException {
    
    /**
     * An OWS Web Service exception report
     */
    private ExceptionReport exception;
    
    OWSWebServiceException() {
        super();
    }
            
    public OWSWebServiceException(String message, OWSExceptionCode code, String locator, ServiceVersion v) {
        super(message);
        this.exception = new ExceptionReport(message, code.name(), locator, v);
    }
    
    public ExceptionReport getExceptionReport() {
        return exception;
    }
    
    /**
     * Returns the code of the first exception in the report.
     * or {@code null} if there is no exception report.
     */
    public OWSExceptionCode getExceptionCode() {
        if (exception != null && !exception.getException().isEmpty()) {
            String code = exception.getException().get(0).getExceptionCode();
            return OWSExceptionCode.valueOf(code);
        } else {
            return null;
        }
    }
    
    /**
     * Return the version number
     */
    public String getVersion() {
        if (exception != null) {
            return exception.getVersion();
        }
        return null;
    }

}

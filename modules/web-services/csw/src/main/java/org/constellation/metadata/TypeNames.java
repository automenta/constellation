/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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
package org.constellation.metadata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author Guilhem Legal
 */
public class TypeNames {
    
    /**
     * a QName for csw:Record type
     */
    public final static QName _Record_QNAME = new QName("http://www.opengis.net/cat/csw/2.0.2", "Record");
    
    /**
     * a QName for gmd:MD_Metadata type
     */
    public final static QName _Metadata_QNAME = new QName("http://www.isotc211.org/2005/gmd", "MD_Metadata");
    
    /**
     * a QName for csw:Capabilities type
     */
    public final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/cat/csw/2.0.2", "Capabilities");
    
    /**
     * some QName for ebrim 3.0 types
     */
    protected final static QName _ExtrinsicObject_QNAME      = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ExtrinsicObject");
    protected final static QName _RegistryPackage_QNAME      = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "RegistryPackage");
    protected final static QName _SpecificationLink_QNAME    = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "SpecificationLink");
    protected final static QName _RegistryObject_QNAME       = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "RegistryObject");
    protected final static QName _Association_QNAME          = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Association");
    protected final static QName _AdhocQuery_QNAME           = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "AdhocQuery");
    protected final static QName _User_QNAME                 = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "User");
    protected final static QName _ClassificationNode_QNAME   = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ClassificationNode");
    protected final static QName _AuditableEvent_QNAME       = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "AuditableEvent");
    protected final static QName _Federation_QNAME           = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Federation");
    protected final static QName _Subscription_QNAME         = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Subscription");
    protected final static QName _ObjectRefList_QNAME        = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ObjectRefList");
    protected final static QName _Classification_QNAME       = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Classification");
    protected final static QName _Person_QNAME               = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Person");
    protected final static QName _ServiceBinding_QNAME       = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ServiceBinding");
    protected final static QName _Notification_QNAME         = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Notification");
    protected final static QName _ClassificationScheme_QNAME = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ClassificationScheme");
    protected final static QName _Service_QNAME              = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Service");
    protected final static QName _ExternalIdentifier_QNAME   = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ExternalIdentifier");
    protected final static QName _Registry_QNAME             = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Registry");
    protected final static QName _Organization_QNAME         = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Organization");
    protected final static QName _ExternalLink_QNAME         = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ExternalLink");
    protected final static QName _WRSExtrinsicObject_QNAME   = new QName("http://www.opengis.net/cat/wrs/1.0",          "ExtrinsicObject");
    
    /**
     * some QName for ebrim 2.5 types
     */
    protected final static QName _ExtrinsicObject25_QNAME       = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "ExtrinsicObject");
    protected final static QName _Federation25_QNAME            = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Federation");
    protected final static QName _ExternalLink25_QNAME          = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "ExternalLink");
    protected final static QName _ClassificationNode25_QNAME    = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "ClassificationNode");
    protected final static QName _User25_QNAME                  = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "User");
    protected final static QName _Classification25_QNAME        = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Classification");
    protected final static QName _RegistryPackage25_QNAME       = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "RegistryPackage");
    protected final static QName _RegistryObject25_QNAME        = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "RegistryObject");
    protected final static QName _Association25_QNAME           = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Association");
    protected final static QName _RegistryEntry25_QNAME         = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "RegistryEntry");
    protected final static QName _ClassificationScheme25_QNAME  = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "ClassificationScheme");
    protected final static QName _Organization25_QNAME          = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Organization");
    protected final static QName _ExternalIdentifier25_QNAME    = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "ExternalIdentifier");
    protected final static QName _SpecificationLink25_QNAME     = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "SpecificationLink");
    protected final static QName _Registry25_QNAME              = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Registry");
    protected final static QName _ServiceBinding25_QNAME        = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "ServiceBinding");
    protected final static QName _Service25_QNAME               = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Service");
    protected final static QName _AuditableEvent25_QNAME        = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "AuditableEvent");
    protected final static QName _Subscription25_QNAME          = new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5", "Subscription");
    protected final static QName _Geometry09_QNAME              = new QName("http://www.opengis.net/cat/wrs",              "Geometry");
    protected final static QName _ApplicationModule09_QNAME     = new QName("http://www.opengis.net/cat/wrs",              "ApplicationModule");
    protected final static QName _WRSExtrinsicObject09_QNAME    = new QName("http://www.opengis.net/cat/wrs",              "WRSExtrinsicObject");

    
    //iso 19115 typeNames
    public static List<QName> ISO_TYPE_NAMES = new ArrayList<QName>();
    static {
        ISO_TYPE_NAMES.add(_Metadata_QNAME);
    }
    
    //dublin core typeNames
    public static List<QName> DC_TYPE_NAMES = new ArrayList<QName>();
    static {
        DC_TYPE_NAMES.add(_Record_QNAME);
    }
    
    //ebrim v3.0 typeNames
    public static List<QName> EBRIM30_TYPE_NAMES = new ArrayList<QName>();
    static {
        EBRIM30_TYPE_NAMES.add(_AdhocQuery_QNAME);
        EBRIM30_TYPE_NAMES.add(_Association_QNAME);
        EBRIM30_TYPE_NAMES.add(_AuditableEvent_QNAME);
        EBRIM30_TYPE_NAMES.add(_ClassificationNode_QNAME);
        EBRIM30_TYPE_NAMES.add(_ClassificationScheme_QNAME);
        EBRIM30_TYPE_NAMES.add(_Classification_QNAME);
        EBRIM30_TYPE_NAMES.add(_ExternalIdentifier_QNAME);
        EBRIM30_TYPE_NAMES.add(_ExternalLink_QNAME);
        EBRIM30_TYPE_NAMES.add(_ExtrinsicObject_QNAME);
        EBRIM30_TYPE_NAMES.add(_Federation_QNAME);
        EBRIM30_TYPE_NAMES.add(_Notification_QNAME);
        EBRIM30_TYPE_NAMES.add(_ObjectRefList_QNAME);
        EBRIM30_TYPE_NAMES.add(_Person_QNAME);
        EBRIM30_TYPE_NAMES.add(_Organization_QNAME);
        EBRIM30_TYPE_NAMES.add(_RegistryObject_QNAME);
        EBRIM30_TYPE_NAMES.add(_RegistryPackage_QNAME);
        EBRIM30_TYPE_NAMES.add(_Registry_QNAME);
        EBRIM30_TYPE_NAMES.add(_ServiceBinding_QNAME);
        EBRIM30_TYPE_NAMES.add(_Service_QNAME);
        EBRIM30_TYPE_NAMES.add(_SpecificationLink_QNAME);
        EBRIM30_TYPE_NAMES.add(_Subscription_QNAME);
        EBRIM30_TYPE_NAMES.add(_User_QNAME);
        EBRIM30_TYPE_NAMES.add(_WRSExtrinsicObject_QNAME);
    }
    
    //ebrim v2.5 typenames
    public static List<QName> EBRIM25_TYPE_NAMES = new ArrayList<QName>();
    static {
        EBRIM25_TYPE_NAMES.add(_ExtrinsicObject25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Federation25_QNAME);
        EBRIM25_TYPE_NAMES.add(_ExternalLink25_QNAME);
        EBRIM25_TYPE_NAMES.add(_ClassificationNode25_QNAME);
        EBRIM25_TYPE_NAMES.add(_User25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Classification25_QNAME);
        EBRIM25_TYPE_NAMES.add(_RegistryPackage25_QNAME);
        EBRIM25_TYPE_NAMES.add(_RegistryObject25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Association25_QNAME);
        EBRIM25_TYPE_NAMES.add(_RegistryEntry25_QNAME);
        EBRIM25_TYPE_NAMES.add(_ClassificationScheme25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Organization25_QNAME);
        EBRIM25_TYPE_NAMES.add(_ExternalIdentifier25_QNAME);
        EBRIM25_TYPE_NAMES.add(_SpecificationLink25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Registry25_QNAME);
        EBRIM25_TYPE_NAMES.add(_ServiceBinding25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Service25_QNAME);
        EBRIM25_TYPE_NAMES.add(_AuditableEvent25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Subscription25_QNAME);
        EBRIM25_TYPE_NAMES.add(_Geometry09_QNAME);
        EBRIM25_TYPE_NAMES.add(_ApplicationModule09_QNAME);
        EBRIM25_TYPE_NAMES.add(_WRSExtrinsicObject09_QNAME);
    }

    /**
     * Return true if the specified list of QNames contains an ebrim V2.5 QName.
     *
     * @param qnames A list of QNames.
     * @return true if the list contains at least one ebrim V2.5 QName.
     */
    public static boolean containsOneOfEbrim25(final List<QName> qnames) {

        if (qnames.contains(_ExtrinsicObject25_QNAME)
         || qnames.contains(_Federation25_QNAME)
         || qnames.contains(_ExternalLink25_QNAME)
         || qnames.contains(_ClassificationNode25_QNAME)
         || qnames.contains(_User25_QNAME)
         || qnames.contains(_Classification25_QNAME)
         || qnames.contains(_RegistryPackage25_QNAME)
         || qnames.contains(_RegistryObject25_QNAME)
         || qnames.contains(_Association25_QNAME)
         || qnames.contains(_RegistryEntry25_QNAME)
         || qnames.contains(_ClassificationScheme25_QNAME)
         || qnames.contains(_Organization25_QNAME)
         || qnames.contains(_ExternalIdentifier25_QNAME)
         || qnames.contains(_SpecificationLink25_QNAME)
         || qnames.contains(_Registry25_QNAME)
         || qnames.contains(_ServiceBinding25_QNAME)
         || qnames.contains(_Service25_QNAME)
         || qnames.contains(_AuditableEvent25_QNAME)
         || qnames.contains(_Subscription25_QNAME)
         || qnames.contains(_Geometry09_QNAME)
         || qnames.contains(_ApplicationModule09_QNAME)
         || qnames.contains(_WRSExtrinsicObject09_QNAME))
            return true;
        return false;
    }

    /**
     * Return true if the specified list of QNames contains an ebrim V3.0 QName.
     *
     * @param qnames A list of QNames.
     * @return true if the list contains at least one ebrim V3.0 QName.
     */
    public static boolean containsOneOfEbrim30(final List<QName> qnames) {

        if (qnames.contains(_AdhocQuery_QNAME)
         || qnames.contains(_Association_QNAME)
         || qnames.contains(_AuditableEvent_QNAME)
         || qnames.contains(_ClassificationNode_QNAME)
         || qnames.contains(_ClassificationScheme_QNAME)
         || qnames.contains(_Classification_QNAME)
         || qnames.contains(_ExternalIdentifier_QNAME)
         || qnames.contains(_ExternalLink_QNAME)
         || qnames.contains(_ExtrinsicObject_QNAME)
         || qnames.contains(_Federation_QNAME)
         || qnames.contains(_Notification_QNAME)
         || qnames.contains(_ObjectRefList_QNAME)
         || qnames.contains(_Person_QNAME)
         || qnames.contains(_Organization_QNAME)
         || qnames.contains(_RegistryObject_QNAME)
         || qnames.contains(_RegistryPackage_QNAME)
         || qnames.contains(_Registry_QNAME)
         || qnames.contains(_ServiceBinding_QNAME)
         || qnames.contains(_Service_QNAME)
         || qnames.contains(_SpecificationLink_QNAME)
         || qnames.contains(_Subscription_QNAME)
         || qnames.contains(_User_QNAME)
         || qnames.contains(_WRSExtrinsicObject_QNAME))
            return true;
        return false;
    }
}

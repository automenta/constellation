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
package org.constellation.metadata.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.constellation.cat.csw.v202.RecordType;
import org.constellation.dublincore.AbstractSimpleLiteral;
import org.constellation.ebrim.v300.InternationalStringType;
import org.constellation.ebrim.v250.RegistryObjectType;
import org.constellation.ebrim.v300.IdentifiableType;
import org.constellation.metadata.Utils;
import org.constellation.metadata.index.IndexLucene;
import org.constellation.ws.ServiceVersion;
import org.constellation.ws.WebServiceException;
import org.geotools.metadata.iso.MetaDataImpl;
import org.mdweb.model.profiles.Profile;
import org.mdweb.model.schemas.Classe;
import org.mdweb.model.schemas.CodeList;
import org.mdweb.model.schemas.CodeListElement;
import org.mdweb.model.schemas.Path;
import org.mdweb.model.schemas.Property;
import org.mdweb.model.schemas.Standard;
import org.mdweb.model.storage.Catalog;
import org.mdweb.model.storage.Form;
import org.mdweb.model.storage.LinkedValue;
import org.mdweb.model.storage.TextValue;
import org.mdweb.model.storage.Value;
import org.mdweb.model.users.User;
import org.mdweb.sql.Reader;
import org.mdweb.sql.v20.Writer20;
import org.opengis.metadata.identification.Identification;
import static org.constellation.ows.OWSExceptionCode.*;

/**
 *
 * @author Guilhem Legal
 */
public class MetadataWriter {

    /**
     * A debugging logger.
     */
    private static Logger LOGGER = Logger.getLogger("org.constellation.metadata.io");
    
    /**
     * A MDWeb catalogs where write the form.
     */
    private Catalog MDCatalog;
    
    /**
     * The MDWeb user who owe the inserted form.
     */
    private final User user;
    
    /**
     * A reader to the MDWeb database.
     */
    private Reader MDReader;
    
    /**
     * A writer to the MDWeb database.
     */
    private Writer20 MDWriter;
    
    /**
     * The current main standard of the Object to create
     */
    private Standard mainStandard;
    
    /**
     * Record the date format in the metadata.
     */
    private DateFormat dateFormat; 
    
    /**
     * A map recording the binding between java Class and MDWeb classe 
     */
    private Map<Class, Classe> classBinding;
    
    /**
     * A List of the already see object for the current metadata readed
     * (in order to avoid infinite loop)
     */
    private Map<Object, Value> alreadyWrite;
    
    /**
     * The current version of the csw
     */
    private ServiceVersion version;
    
    /**
     * An indexer lucene to add object into the index.
     */
    private final IndexLucene index;
    
    /**
     * Build a new metadata writer.
     * 
     * @param MDReader an MDWeb database reader.
     */
    public MetadataWriter(Reader MDReader, Writer20 MDWriter, IndexLucene index, ServiceVersion version) throws SQLException {
        
        MDCatalog         = MDReader.getCatalog("CSWCat");
        if (MDCatalog == null) {
            MDCatalog = new Catalog("CSWCat", "CSW Data Catalog");
            MDWriter.writeCatalog(MDCatalog);
        }
        user              = MDReader.getUser("admin");
        this.MDReader     = MDReader;  
        this.MDWriter     = MDWriter;
        this.index        = index;
        this.version      = version;
        this.dateFormat   = new SimpleDateFormat("yyyy-MM-dd");
        this.classBinding = new HashMap<Class, Classe>();
        this.alreadyWrite = new HashMap<Object, Value>();
    }

    /**
     * Return an MDWeb formular from an object.
     * 
     * @param object The object to transform in form.
     * @return an MDWeb form representing the metadata object.
     */
    public Form getFormFromObject(Object object) throws SQLException {

        
        if (object != null) {
            //we try to find a title for the from
            String title = findName(object);
            if (title.equals("unknow title")) {
                title = getAvailableTitle();
            }
            
            Date creationDate = new Date(System.currentTimeMillis());
            String className = object.getClass().getSimpleName();
            
            // ISO 19115 types
            if (className.equals("MetaDataImpl")         || 
            
            // ISO 19110 types        
                className.equals("FeatureCatalogueImpl") ||
                className.equals("FeatureOperationImpl") ||
                className.equals("FeatureAssociationImpl")
            ) {
                mainStandard   = Standard.ISO_19115;
            
            // Ebrim Types    
            } else if (object instanceof IdentifiableType) {
                mainStandard = Standard.EBRIM_V3;
           
            } else if (object instanceof RegistryObjectType) {
                mainStandard = Standard.EBRIM_V2_5;
            
            // CSW Types    
            } else if (className.equals("RecordType")) {
                mainStandard = Standard.CSW;
            
            // unkow types
            } else {
                String msg = "Can't register ths kind of object:" + object.getClass().getName();
                LOGGER.severe(msg);
                throw new IllegalArgumentException(msg);
            }
            
            Profile defaultProfile = null;
            if  (className.equals("MetaDataImpl")) {
                defaultProfile = MDReader.getProfile("ISO_19115");
            }
            Form form = new Form(-1, MDCatalog, title, user, null, defaultProfile, creationDate);
            
            Classe rootClasse = getClasseFromObject(object);
            if (rootClasse != null) {
                alreadyWrite.clear();
                Path rootPath = new Path(rootClasse.getStandard(), rootClasse);
                addValueFromObject(form, object, rootPath, null);
                return form;
            } else {
                LOGGER.severe("unable to find the root class:" + object.getClass().getSimpleName());
                return null;
            }
        } else {
            LOGGER.severe("unable to create form object is null");
            return null;
        }
    }
    
    /**
     * Ask to the mdweb reader an available title for a form.
     */
    private String getAvailableTitle() throws SQLException {
        
        return MDReader.getAvailableTitle();
    }
    
    /**
     * This method try to find a title to this object.
     * if the object is a ISO19115:Metadata or CSW:Record we know were to search the title,
     * else we try to find a getName() method.
     * 
     * @param obj the object for wich we want a title
     * 
     * @return the founded title or "Unknow title"
     */
    private String findName(Object obj) {
        
        //here we try to get the title
        AbstractSimpleLiteral titleSL = null;
        String title = "unknow title";
        if (obj instanceof RecordType) {
            titleSL = ((RecordType) obj).getTitle();
            if (titleSL == null) {
                titleSL = ((RecordType) obj).getIdentifier();
            }
                               
            if (titleSL == null) {
                title = "unknow title";
            } else {
                if (titleSL.getContent().size() > 0)
                    title = titleSL.getContent().get(0);
            }
                            
        } else if (obj instanceof MetaDataImpl) {
            Collection<Identification> idents = ((MetaDataImpl) obj).getIdentificationInfo();
            if (idents.size() != 0) {
                Identification ident = idents.iterator().next();
                if (ident != null && ident.getCitation() != null && ident.getCitation().getTitle() != null) {
                    title = ident.getCitation().getTitle().toString();
                } 
            }
        } else if (obj instanceof org.constellation.ebrim.v300.RegistryObjectType) {
            InternationalStringType ident = ((org.constellation.ebrim.v300.RegistryObjectType) obj).getName();
            if (ident != null && ident.getLocalizedString().size() > 0) {
                title = ident.getLocalizedString().get(0).getValue();
            } else {
                title = ((RegistryObjectType) obj).getId();
            } 
        
        } else if (obj instanceof org.constellation.ebrim.v250.RegistryObjectType) {
            org.constellation.ebrim.v250.InternationalStringType ident = ((org.constellation.ebrim.v250.RegistryObjectType) obj).getName();
            if (ident != null && ident.getLocalizedString().size() > 0) {
                title = ident.getLocalizedString().get(0).getValue();
            } else {
                title = ((org.constellation.ebrim.v250.RegistryObjectType) obj).getId();
            } 
            
        } else {
            Method nameGetter = null;
            String methodName = "";
            int i = 0;
            while (i < 3) {
                try {
                    switch (i) {
                        case 0: methodName = "getTitle";
                                nameGetter = obj.getClass().getMethod(methodName);
                                break;
                                 
                        case 1: methodName = "getName";
                                nameGetter = obj.getClass().getMethod(methodName);
                                break;
                                
                        case 2: methodName = "getId";
                                nameGetter = obj.getClass().getMethod(methodName);
                                break;
                    }
                
                
                } catch (NoSuchMethodException ex) {
                    LOGGER.finer("not " + methodName + " method in " + obj.getClass().getSimpleName());
                } catch (SecurityException ex) {
                    LOGGER.severe(" security exception while getting the title of the object.");
                }
                if (nameGetter != null) {
                    i = 3;
                } else {
                    i++;
                }
            }
            
            if (nameGetter != null) {
                try {
                    Object objT = nameGetter.invoke(obj);
                    if (objT instanceof String) {
                        title = (String) obj;
                    
                    } else if (objT instanceof AbstractSimpleLiteral) {
                        titleSL = (AbstractSimpleLiteral) objT;
                        if (titleSL.getContent().size() > 0)
                            title = titleSL.getContent().get(0);
                        else title = "unknow title";
                    
                    } else {
                        title = "unknow title";
                    }
                    
                    if (title == null)
                        title = "unknow title";
                } catch (IllegalAccessException ex) {
                    LOGGER.severe("illegal access for method " + methodName + " in " + obj.getClass().getSimpleName() + '\n' + 
                                  "cause: " + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    LOGGER.severe("illegal argument for method " + methodName + " in " + obj.getClass().getSimpleName()  +'\n' +
                                  "cause: " + ex.getMessage());
                } catch (InvocationTargetException ex) {
                    LOGGER.severe("invocation target exception for " + methodName + " in " + obj.getClass().getSimpleName() +'\n' +
                                  "cause: " + ex.getMessage());
                }
            }
            
            if (title.equals("unknow title"))
                LOGGER.severe("unknow type: " + obj.getClass().getName() + " unable to find a title");
        }
        return title;
    }
    
    /**
     * Add a MDWeb value (and his children)to the specified form.
     * 
     * @param form The created form.
     * 
     */
    private void addValueFromObject(Form form, Object object, Path path, Value parentValue) throws SQLException {
        //if the path is not already in the database we write it
        if (MDReader.getPath(path.getId()) == null) {
           MDWriter.writePath(path);
        } 
        if (object == null) {
            return;
        }             
        
        //if the object is a collection we call the method on each child
        Classe classe;
        if (object instanceof Collection) {
            Collection c = (Collection) object;
            for (Object obj: c) {
                addValueFromObject(form, obj, path, parentValue);
                
            }
            return;
            
        //if the object is a JAXBElement we desencapsulate it    
        } else {
            if (object instanceof JAXBElement) {
                JAXBElement jb = (JAXBElement) object;
                object = jb.getValue();
            } 
            classe = getClasseFromObject(object);
        }
        
        //if we don't have found the class we stop here
        if (classe == null) {
            return;
        }
        
        //we try to find the good ordinal
        int ordinal;
        if (parentValue == null) {
            ordinal = 1;
        } else {
            ordinal  = form.getNewOrdinal(parentValue.getIdValue() + ':' + path.getName());
        }
        
        //we look if the object have been already write
        Value linkedValue = alreadyWrite.get(object);
        
        // if its a primitive type we create a TextValue
        if (isPrimitive(classe)) {
            if (classe instanceof CodeList) {
                CodeList cl = (CodeList) classe;
                String codelistElement;
                if (classe.getName().equals("LanguageCode")) {
                    codelistElement =  ((Locale) object).getISO3Language();
                } else {
                    if (object instanceof org.opengis.util.CodeList) {
                        codelistElement =  ((org.opengis.util.CodeList) object).identifier();
                        if (codelistElement == null) {
                            codelistElement = ((org.opengis.util.CodeList) object).name();
                        }
                        
                    } else if (object.getClass().isEnum()) {
                        
                        codelistElement = getElementNameFromEnum(object);
                        
                    } else {
                        LOGGER.severe (object.getClass().getName() + " is not a codelist!");
                        codelistElement = null;
                    }
                }
                CodeListElement cle = (CodeListElement) cl.getPropertyByName(codelistElement);
                if (cle != null && cle instanceof org.mdweb.model.schemas.Locale) {
                    object = cle.getShortName();
                } else if (cle != null) {
                    object = cle.getCode();
                } else {
                    String values = "";
                    for (Property p: classe.getProperties()) {
                        values += p.getName() +'\n';
                    }
                    LOGGER.severe("unable to find a codeListElement named " + codelistElement + " in the codelist " + classe.getName() + '\n' +
                                  "allowed values are: " + '\n' +  values);
                }
            }
            String value;
            if (object instanceof java.util.Date) {
                value = dateFormat.format(object);
            } else {
                value = object + "";
            }
            
            TextValue textValue = new TextValue(path, form , ordinal, value, classe, parentValue);
            LOGGER.finer("new TextValue: " + path.toString() + " classe:" + classe.getName() + " value=" + object + " ordinal=" + ordinal);
        
        // if we have already see this object we build a Linked Value.
        } else if (linkedValue != null) {
            
            LinkedValue value = new LinkedValue(path, form, ordinal, form, linkedValue, classe, parentValue);
            LOGGER.finer("new LinkedValue: " + path.toString() + " classe:" + classe.getName() + " linkedValue=" + linkedValue.getIdValue() + " ordinal=" + ordinal);
        
        // else we build a Value node.
        } else {
        
            Value value = new Value(path, form, ordinal, classe, parentValue);
            LOGGER.finer("new Value: " + path.toString() + " classe:" + classe.getName() + " ordinal=" + ordinal);
            //we add this object to the listed of already write element
            alreadyWrite.put(object, value);
            
            do {
                for (Property prop: classe.getProperties()) {
                    // TODO remove when fix in MDweb2
                    if (prop.getName().equals("geographicElement3") ||  prop.getName().equals("geographicElement4"))
                        continue;
                
                    Method getter = getGetterFromName(prop.getName(), object.getClass());
                    if (getter != null) {
                        try {
                            Object propertyValue = getter.invoke(object);
                            if (propertyValue != null) {
                                Path childPath = new Path(path, prop); 
                            
                                //if the path is not already in the database we write it
                                if (MDReader.getPath(childPath.getId()) == null) {
                                    MDWriter.writePath(childPath);
                                }
                                addValueFromObject(form, propertyValue, childPath, value);
                            } 
                    
                        } catch (IllegalAccessException e) {
                            LOGGER.severe("The class is not accessible");
                            return;
                        } catch (java.lang.reflect.InvocationTargetException e) {
                            LOGGER.severe("Exception throw in the invokated getter: " + getter.toGenericString() + '\n' +
                                          "Cause: " + e.getMessage());
                            return;
                        }   
                    }
                }
                classe = classe.getSuperClass();
                if (classe != null) {
                    LOGGER.finer("searching in superclasse " + classe.getName());
                }
            } while (classe != null);
        }
    }

    /**
     * 
     * @param enumeration
     * @return
     */
    private String getElementNameFromEnum(Object enumeration) {
        String value = "";
        try {
            Method getValue = enumeration.getClass().getDeclaredMethod("value");
            value = (String) getValue.invoke(enumeration);
        } catch (IllegalAccessException ex) {
            LOGGER.severe("The class is not accessible");
        } catch (IllegalArgumentException ex) {
            LOGGER.severe("IllegalArguement exeption in value()");
        } catch (InvocationTargetException ex) {
            LOGGER.severe("Exception throw in the invokated getter value() " + '\n' +
                       "Cause: " + ex.getMessage());
        } catch (NoSuchMethodException ex) {
           LOGGER.severe("no such method value() in " + enumeration.getClass().getSimpleName());
        } catch (SecurityException ex) {
           LOGGER.severe("security Exception while getting the codelistElement in value() method");
        }
        return value;
    }
    
    /**
     * Return a getter Method for the specified attribute (propertyName) 
     * 
     * @param propertyName The attribute name.
     * @param rootClass    The class whitch owe this attribute
     * 
     * @return a setter to this attribute.
     */
    public static Method getGetterFromName(String propertyName, Class rootClass) {
        Logger.getLogger(propertyName).finer("search for a getter in " + rootClass.getName() + " of name :" + propertyName);
        
        //special case and corrections
        if (propertyName.equals("beginPosition")) {
            propertyName = "begining";
        } else if (propertyName.equals("endPosition")) {
            propertyName = "ending";
        } else if (propertyName.equals("onlineResource")) {
            propertyName = "onLineResource";
        } else if (propertyName.equals("dataSetURI")) {
            propertyName = "dataSetUri";
        } else if (propertyName.equals("extentTypeCode")) {
            propertyName = "inclusion";    
        // TODO remove when this issue will be fix in MDWeb    
        } else if (propertyName.indexOf("geographicElement") != -1) {
            propertyName = "geographicElement";
        }
        
        String methodName = "get" + Utils.firstToUpper(propertyName);
        int occurenceType = 0;
        
        while (occurenceType < 4) {

            try {
                Method getter = null;
                switch (occurenceType) {

                    case 0: {
                        getter = rootClass.getMethod(methodName);
                        break;
                    }
                    case 1: {
                        getter = rootClass.getMethod(methodName + "s");
                        break;
                    }
                    case 2: {
                        getter = rootClass.getMethod(methodName + "es");
                        break;
                    }
                    case 3: {
                        if (methodName.endsWith("y")) {
                            methodName = methodName.substring(0, methodName.length() - 1) + 'i';
                        }
                        getter = rootClass.getMethod(methodName + "es");
                        break;
                    }
                   
                }
                if (getter != null) {
                    LOGGER.finer("getter found: " + getter.toGenericString());
                }
                return getter;

            } catch (NoSuchMethodException e) {

                switch (occurenceType) {

                    case 0: {
                        LOGGER.finer("The getter " + methodName + "() does not exist");
                        occurenceType = 1;
                        break;
                    }

                    case 1: {
                        LOGGER.finer("The getter " + methodName + "s() does not exist");
                        occurenceType = 2;
                        break;
                    }
                    case 2: {
                        LOGGER.finer("The getter " + methodName + "es() does not exist");
                        occurenceType = 3;
                        break;
                    }
                    case 3: {
                        LOGGER.finer("The getter " + methodName + "es() does not exist");
                        occurenceType = 4;
                        break;
                    }
                    default:
                        occurenceType = 5;
                }
            }
        }
        LOGGER.severe("No getter have been found for attribute " + propertyName + " in the class " + rootClass.getName());
        return null;
    }
    
    /**
     * Return true if the MDWeb classe is primitive (i.e. if its a CodeList or if it has no properties).
     * 
     * @param classe an MDWeb classe Object
     */
    private boolean isPrimitive(Classe classe) {
        if (classe != null) {
            int nbProperties = classe.getProperties().size();
            Classe superClass = classe.getSuperClass();
            while (superClass != null) {
                nbProperties = nbProperties + superClass.getProperties().size();
                superClass = superClass.getSuperClass();
            }
            
            return (nbProperties == 0 || classe instanceof CodeList);
        }
            
        return false;
    }
    
    /**
     * Return an MDWeb classe object for the specified java object.
     * 
     * @param object the object to identify
     *
     * @throws java.sql.SQLException
     */
    private Classe getClasseFromObject(Object object) throws SQLException {
        
        String className;
        String packageName;
        Classe result;
        if (object != null) {
            
            result = classBinding.get(object.getClass());
            if (result != null) {
                return result;
            }
            
            className   = object.getClass().getSimpleName();
            packageName = object.getClass().getPackage().getName();
            LOGGER.finer("searche for classe " + className);
            
        } else {
            return null;
        }
        //for the primitive type we return ISO primitive type
        result = getPrimitiveTypeFromName(className);
        if (result != null) {
            classBinding.put(object.getClass(), result);
            return result;
        }

        //special case TODO delete when geotools/api will be updated.
        if (className.equals("MetaDataImpl")) {
            className = "Metadata";
        } else if (className.equals("OnLineResourceImpl")) {
            className = "OnlineResource";
        } else if (className.equals("CitationDate") || className.equals("CitationDateImpl")) {
            className = "CI_Date";
        } else if (className.equals("ScopeImpl")) {
            className = "DQ_Scope";
        } 
        
        //we remove the Impl suffix
        int i = className.indexOf("Impl");
        if (i != -1) {
            className = className.substring(0, i);
        }
        
        //We replace The FRA prefix by FRA_
        if (className.startsWith("FRA"))
            className = "FRA_" + className.substring(3);
        
        //we remove the Type suffix
        if (className.endsWith("Type") && !className.equals("CouplingType") 
                                       && !className.equals("DateType") 
                                       && !className.equals("KeywordType")
                                       && !className.equals("FeatureType")
                                       && !className.equals("GeometricObjectType")
                                       && !className.equals("SpatialRepresentationType")) {
            className = className.substring(0, className.length() - 4);
        }
        
        List<Standard> availableStandards = new ArrayList<Standard>();
        
        // ISO 19115 and its sub standard (ISO 19119, 19110)
        if (mainStandard.equals(Standard.ISO_19115)) {
            availableStandards.add(Standard.ISO_19115_FRA);
            availableStandards.add(mainStandard);
            availableStandards.add(Standard.ISO_19108);
            availableStandards.add(Standard.ISO_19103);
            availableStandards.add(MDReader.getStandard("ISO 19119"));
            availableStandards.add(MDReader.getStandard("ISO 19110"));
        
        // CSW standard    
        } else if (mainStandard.equals(Standard.CSW)) {
            availableStandards.add(Standard.CSW);
            availableStandards.add(Standard.DUBLINCORE);
            availableStandards.add(Standard.DUBLINCORE_TERMS);
            availableStandards.add(Standard.OWS);
        
        // Ebrim v3 standard    
        } else if (mainStandard.equals(Standard.EBRIM_V3)) {
            availableStandards.add(Standard.EBRIM_V3);
            availableStandards.add(Standard.CSW);
            availableStandards.add(Standard.OGC_FILTER);
            availableStandards.add(Standard.MDWEB);
            
        // Ebrim v2.5 tandard    
        } else if (mainStandard.equals(Standard.EBRIM_V2_5)) {
            availableStandards.add(Standard.EBRIM_V2_5);
            availableStandards.add(Standard.CSW);
            availableStandards.add(Standard.OGC_FILTER);
            availableStandards.add(Standard.MDWEB);
        
        } else {
            throw new IllegalArgumentException("Unexpected Main standard: " + mainStandard);
        }
        
        String availableStandardLabel = "";
        for (Standard standard : availableStandards) {
            
            availableStandardLabel = availableStandardLabel + standard.getName() + ',';
            /* to avoid some confusion between to classes with the same name
             * we affect the standard in some special case
             */
            if (packageName.equals("org.geotools.service")) {
                standard = MDReader.getStandard("ISO 19119");
            } else if (packageName.equals("org.constellation.metadata.fra")) {
                standard = Standard.ISO_19115_FRA;
            }
                
            String name = className;
            int nameType = 0;
            while (nameType < 11) {
                
                LOGGER.finer("searching: " + standard.getName() + ":" + name);
                result = MDReader.getClasse(name, standard);
                if (result != null) {
                    LOGGER.info("class found:" + standard.getName() + ":" + name);
                    classBinding.put(object.getClass(), result);
                    return result;
                } 
                
                switch (nameType) {

                        //we add the prefix MD_
                        case 0: {
                            nameType = 1;
                            name = "MD_" + className;    
                            break;
                        }
                        //we add the prefix MD_ + the suffix "Code"
                        case 1: {
                            nameType = 2;
                            name = "MD_" + className + "Code";    
                            break;
                        }
                        //we add the prefix CI_
                        case 2: {
                            nameType = 3;
                            name = "CI_" + className;    
                            break;
                        }
                        //we add the prefix CI_ + the suffix "Code"
                        case 3: {
                            nameType = 4;
                            name = "CI_" + className + "Code";    
                            break;
                        }
                        //we add the prefix EX_
                        case 4: {
                            nameType = 5;
                            name = "EX_" + className;    
                            break;
                        }
                        //we add the prefix SV_
                        case 5: {
                            nameType = 6;
                            name = "SV_" + className;    
                            break;
                        }
                        //we add the prefix FC_
                        case 6: {
                            nameType = 7;
                            name = "FC_" + className;    
                            break;
                        }
                        //we add the prefix DQ_
                        case 7: {
                            nameType = 8;
                            name = "DQ_" + className;    
                            break;
                        }
                        //we add the prefix LI_
                        case 8: {
                            nameType = 9;
                            name = "LI_" + className;    
                            break;
                        }
                        //for the code list we add the "code" suffix
                        case 9: {
                            if (name.indexOf("Code") != -1) {
                                name += "Code";
                            }
                            nameType = 10;
                            break;
                        }
                         //for the code list we add the "code" suffix
                        //for the temporal element we remove add prefix
                        case 10: {
                            name = "Time" + name;
                            nameType = 11;
                            break;
                        }
                        default:
                            nameType = 11;
                            break;
                    }

                }
            }
        
        availableStandardLabel = availableStandardLabel.substring(0, availableStandardLabel.length() - 1);
        LOGGER.severe("class no found: " + className + " in the following standards: " + availableStandardLabel);
        return null;
    }
    
    /**
     * Return a class (java primitive type) from a class name.
     * 
     * @param className the standard name of a class. 
     * @return a primitive class.
     */
    private Classe getPrimitiveTypeFromName(String className) throws SQLException {
        
        if (className.equals("String") || className.equals("SimpleInternationalString")) {
            return MDReader.getClasse("CharacterString", Standard.ISO_19103);
        } else if (className.equalsIgnoreCase("Date")) {
            return MDReader.getClasse(className, Standard.ISO_19103);
        }  else if (className.equalsIgnoreCase("Integer")) {
            return MDReader.getClasse(className, Standard.ISO_19103);
        }  else if (className.equalsIgnoreCase("Long")) {
            return MDReader.getClasse("Integer", Standard.ISO_19103);
        } else if (className.equalsIgnoreCase("Boolean")) {
            return MDReader.getClasse(className, Standard.ISO_19103);
        }  else if (className.equalsIgnoreCase("URL")) {
            return MDReader.getClasse(className, Standard.ISO_19115);
        //special case for locale codeList.
        } else if (className.equals("Locale")) {
            return MDReader.getClasse("LanguageCode", Standard.ISO_19115);
        //special case for Role codeList.
        } else if (className.equals("Role")) {
            return MDReader.getClasse("CI_RoleCode", Standard.ISO_19115);
        } else if (className.equals("Double")) {
            return MDReader.getClasse("Real", Standard.ISO_19103);
        } else {
            return null;
        }
    }
    
    /**
     * Record an object in the metadata database.
     * 
     * @param obj The object to store in the database.
     * @return true if the storage succeed, false else.
     */
    public boolean storeMetadata(Object obj) throws SQLException, WebServiceException {
        // profiling operation
        long start     = System.currentTimeMillis();
        long transTime = 0;
        long writeTime = 0;
        
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement)obj).getValue();
        }
        
        // we create a MDWeb form form the object
        Form f = null;
        try {
            long start_trans = System.currentTimeMillis();
            f = getFormFromObject(obj);
            transTime = System.currentTimeMillis() - start_trans;
            
        } catch (IllegalArgumentException e) {
             throw new WebServiceException("This kind of resource cannot be parsed by the service: " + obj.getClass().getSimpleName() +'\n' +
                                           "cause: " + e.getMessage(),NO_APPLICABLE_CODE, version);
        }
        
        // and we store it in the database
        if (f != null) {
            try {
                long startWrite = System.currentTimeMillis();
                MDWriter.writeForm(f, false);
                writeTime = System.currentTimeMillis() - startWrite;
            } catch (IllegalArgumentException e) {
                //TODO restore catching at this point
                throw e;
                //return false;
            }
            
            long time = System.currentTimeMillis() - start; 
            LOGGER.info("inserted new Form: " + f.getTitle() + " in " + time + " ms (transformation: " + transTime + " DB write: " +  writeTime + ")");
            index.indexDocument(f);
            return true;
        }
        return false;
    }
}

/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2009, Geomatys
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

package org.constellation.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.internal.io.IOUtilities;

/**
 * Utility methods of general use.
 * <p>
 * TODO: this class needs review.
 *   * methods should be re-ordered for coherence
 *       -- String
 *       -- Reflection
 *       -- ...
 * </p>
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @author Legal Guilhem (Geomatys)
 * @author Adrian Custer (Geomatys)
 * 
 * @since 0.2
 */
public final class Util {
	
    private static final Logger LOGGER = Logger.getLogger("org.constellation.util");
    
    private Util() {}
    
    /**
     * Generate a {@code Date} object from a {@code String} which represents an 
     * instant encoded in the ISO-8601 format, e.g. {@code 2009.01.20T17:04Z}.
     * <p>
     * The {@code String} can be defined in the pattern 
     *   yyyy-MM-dd'T'HH:mm:ss.SSSZ 
     * or 
     *   yyyy-MM-dd.
     * </p>
     * @param dateString An instant encoded in the ISO-8601 format.
     * @return A {@code java.util.Date} object representing the same instant.
     * @see TimeParser
     */
    public static Date getDateFromString(String dateString) throws ParseException {
        final String dateFormat     = "yyyy-MM-dd'T'HH:mm:ssZ";
        final String dateFormat2    = "yyyy-MM-dd";
        final String dateFormat3    = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        final SimpleDateFormat sdf  = new java.text.SimpleDateFormat(dateFormat);
        final SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(dateFormat2);
        final SimpleDateFormat sdf3 = new java.text.SimpleDateFormat(dateFormat3);

        if (dateString.contains("T")) {
            String timezoneStr;
            int index = dateString.lastIndexOf('+');
            if (index == -1) {
                index = dateString.lastIndexOf('-');
            }
            if (index > dateString.indexOf('T')) {
                timezoneStr = dateString.substring(index + 1);

                if (timezoneStr.contains(":")) {
                    //e.g : 1985-04-12T10:15:30+04:00
                    timezoneStr = timezoneStr.replace(":", "");
                    dateString = dateString.substring(0, index + 1).concat(timezoneStr);
                } else if (timezoneStr.length() == 2) {
                    //e.g : 1985-04-12T10:15:30-04
                    dateString = dateString.concat("00");
                }
            } else if (dateString.endsWith("Z")) {
                //e.g : 1985-04-12T10:15:30Z
                dateString = dateString.substring(0, dateString.length() - 1).concat("+0000");
            }else {
                //e.g : 1985-04-12T10:15:30
                dateString = dateString + "+0000";
            }
            final String timezone = getTimeZone(dateString);
            sdf.setTimeZone(TimeZone.getTimeZone(timezone));

            if (dateString.contains(".")) {
                return sdf3.parse(dateString);
            }
            return sdf.parse(dateString);
        }
        if (dateString.contains("-")) {
            return sdf2.parse(dateString);
        }
        return null;
    }
    
    /**
     * Extract a description of the offset from GMT for an instant encoded in 
     * ISO-8601 format (e.g. an input of {@code 2009-01-20T12:04:00Z-05} would
     * yield the {@code String} "GMT-05").
     * 
     * @param dateString An instant encoded in ISO-8601 format, must not be 
     *                     {@code null}.
     * @return The offset from GMT for the parameter.
     */
    public static String getTimeZone(final String dateString) {
    	
        if (dateString.endsWith("Z")) {
            return "GMT+" + 0;
        }
        int index = dateString.lastIndexOf('+');
        if (index == -1) {
            index = dateString.lastIndexOf('-');
        }
        if (index > dateString.indexOf('T')) {
            return "GMT" + dateString.substring(index);
        }
        return TimeZone.getDefault().getID();
    }
    
    /**
     * Create a {@link Date} object from a date represented in a {@String} 
     * object and a formatting rule described in a {@link DateFormat} object.
     * <p>
     * The input date parameter can have any of several different formats.
     * </p>
     * <p>
     * TODO: Explain these formats.
     * TODO: This is out of date since we no longer use the {@code DateFormat}
     * objects to format the {@code Date}.
     * </p>
     * 
     * @param date A date representation, e.g. 2002, 02-2007, 2004-03-04, which 
     *               may be null.
     * @return A formated date (example 2002 -> 01-01-2002,  2004-03-04 -> 04-03-2004, ...) 
     */
    public static Date createDate(String date, final DateFormat dateFormat) throws ParseException {
        
        final Map<String, String> pool = new HashMap<String, String>();
        pool.put("janvier",   "01");
        pool.put("février",   "02");
        pool.put("mars",      "03");
        pool.put("avril",     "04");
        pool.put("mai",       "05");
        pool.put("juin",      "06");
        pool.put("juillet",   "07");
        pool.put("août",      "08");
        pool.put("septembre", "09");
        pool.put("octobre",   "10");
        pool.put("novembre",  "11");
        pool.put("décembre",  "12");

        final Map<String, String> poolCase = new HashMap<String, String>();
        poolCase.put("Janvier",   "01");
        poolCase.put("Février",   "02");
        poolCase.put("Mars",      "03");
        poolCase.put("Avril",     "04");
        poolCase.put("Mai",       "05");
        poolCase.put("Juin",      "06");
        poolCase.put("Juillet",   "07");
        poolCase.put("Août",      "08");
        poolCase.put("Septembre", "09");
        poolCase.put("Octobre",   "10");
        poolCase.put("Novembre",  "11");
        poolCase.put("Décembre",  "12");

        String year;
        String month;
        String day;
        Date tmp = getDateFromString("1900" + "-" + "01" + "-" + "01");
        if (date != null) {
            if (date.contains("/")) {
                if (getOccurenceFrequency(date, "/") == 2) {
                    day = date.substring(0, date.indexOf('/'));
                    date = date.substring(date.indexOf('/') + 1);
                    month = date.substring(0, date.indexOf('/'));
                    year = date.substring(date.indexOf('/') + 1);

                    tmp = getDateFromString(year + "-" + month + "-" + day);
                } else {
                    if (getOccurenceFrequency(date, "/") == 1) {
                        month = date.substring(0, date.indexOf('/'));
                        year = date.substring(date.indexOf('/') + 1);
                        tmp = getDateFromString(year + "-" + month + "-" + "01");
                    }
                }
            } else if (getOccurenceFrequency(date, " ") == 2) {
                if (!date.contains("?")) {

                    day = date.substring(0, date.indexOf(' '));
                    date = date.substring(date.indexOf(' ') + 1);
                    month = pool.get(date.substring(0, date.indexOf(' ')));
                    year = date.substring(date.indexOf(' ') + 1);

                    tmp = getDateFromString(year + "-" + month + "-" + day);
                } else {
                    tmp = getDateFromString("2000" + "-" + "01" + "-" + "01");
                }
            } else if (getOccurenceFrequency(date, " ") == 1) {
                try {
                    Date d;
                    synchronized(dateFormat) {
                        d = dateFormat.parse(date);
                    }
                    return new Date(d.getTime());
                } catch (ParseException ex) {
                    LOGGER.info("parse exception for input:" + date);
                }
                month = poolCase.get(date.substring(0, date.indexOf(' ')));
                year = date.substring(date.indexOf(' ') + 1);
                tmp = getDateFromString(year + "-" + month + "-" + "01");


            } else if (getOccurenceFrequency(date, "-") == 1) {

                month = date.substring(0, date.indexOf('-'));
                year = date.substring(date.indexOf('-') + 1);

                tmp = getDateFromString(year + "-" + month + "-" + "01");

            } else if (getOccurenceFrequency(date, "-") == 2) {

                //if date is in format yyyy-mm-dd
                if (date.substring(0, date.indexOf('-')).length() == 4) {
                    year = date.substring(0, date.indexOf('-'));
                    date = date.substring(date.indexOf('-') + 1); //mm-ddZ
                    month = date.substring(0, date.indexOf('-'));
                    date = date.substring(date.indexOf('-') + 1); // ddZ
                    if (date.contains("Z")) {
                        date = date.substring(0, date.indexOf('Z'));
                    }
                    day = date;

                    tmp = getDateFromString(year + "-" + month + "-" + day);
                } else {
                    day = date.substring(0, date.indexOf('-'));
                    date = date.substring(date.indexOf('-') + 1);
                    month = date.substring(0, date.indexOf('-'));
                    year = date.substring(date.indexOf('-') + 1);

                    tmp = getDateFromString(year + "-" + month + "-" + day);
                }

            } else {
                year = date;
                tmp = getDateFromString(year + "-" + "01" + "-" + "01");
            }
        }
        return tmp;
    }
    
        
    /**
     * Counts the number of occurences of the second parameter within the first.
     * For example, {@code getOccurenceFrequency("Constellation","ll") yields 
     * {@code 1}.
     * 
     * @param s   The {@code String} against which matches should be made, must 
     *              not be null.
     * @param occ The {@code String} whose frequency of occurrence in the first 
     *              parameter should be counted, must not be null.
     * @return The frequency of occurrences of the second parameter characters 
     *           in the character sequence of the first.
     */
    public static int getOccurenceFrequency (String s, final String occ){
        if (! s.contains(occ))
            return 0;
        else {
            int nbocc = 0;
            while(s.indexOf(occ) != -1){
                s = s.substring(s.indexOf(occ)+1);
                nbocc++;
            }
            return nbocc;
        }
    }
    
    /**
     * Searches in the Context ClassLoader for the named directory and returns it.
     *
     * @param packagee The name of package.
     *
     * @return A directory if it exist.
     */
    public static File getDirectoryFromResource(final String packagee) {
        File result = null;
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String fileP = packagee.replace('.', '/');
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result  = scanDir(uri, fileP);
                } catch (URISyntaxException e) {
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
        }


        return result;
    }

    /**
     * Scan a resource file (a JAR or a directory) and return it as a File.
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static File scanDir(final URI u, final String filePackageName) throws IOException {
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            final File f = new File(u.getPath());
            if (f.isDirectory()) {
                return f;
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            final File f = new File(System.getProperty("java.io.tmpdir") + "/Constellation");
            if (f != null && f.exists()) {
                try {
                    String cleanedUri = u.getSchemeSpecificPart();
                    if (cleanedUri.indexOf('!') != -1)
                        cleanedUri = cleanedUri.substring(0, cleanedUri.indexOf('!'));
                    URI newUri = new URI(cleanedUri);
                    InputStream i = newUri.toURL().openStream();
                    IOUtilities.unzip(i, f);
                } catch(URISyntaxException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
                final File fConfig = new File(f, filePackageName);
                if (fConfig.exists() && fConfig.isDirectory()) {
                    return fConfig;
                } else {
                    LOGGER.info("The configuration directory was not found in the temporary folder.");
                }
            } else {
                LOGGER.info("The Constellation directory was not present in the temporary folder.");
            }
        }
        return null;
    }

    /**
     * Searches in the Context ClassLoader for the named file and returns it.
     *
     * @param packagee The name of package.
     *
     * @return A directory if it exist.
     */
    public static File getFileFromResource(String packagee) {
        File result = null;
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String extension = packagee.substring(packagee.lastIndexOf('.'), packagee.length());
            packagee               = packagee.substring(0, packagee.lastIndexOf('.'));
            final String fileP = packagee.replace('.', '/') + extension;
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result  = scanFile(uri, fileP);
                } catch (URISyntaxException e) {
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
        }


        return result;
    }

    /**
     * Scan a resource file (a JAR or a directory) and return it as a File.
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static File scanFile(final URI u, final String filePackageName) throws IOException {
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            return new File(u.getPath());
        }
        if (scheme.equals("jar") || scheme.equals("zip")) {
            final File f = new File(System.getProperty("java.io.tmpdir") + "/Constellation");
            if (f != null && f.exists()) {
                final File fConfig = new File(f, filePackageName);
                if (fConfig.exists() && fConfig.isDirectory()) {
                    return fConfig;
                } else {
                    LOGGER.info("The configuration directory was not found in the temporary folder.");
                }
            } else {
                LOGGER.info("The Constellation directory was not present in the temporary folder.");
            }
        }
        return null;
    }

/**
     * Searches in the Context ClassLoader for the named files and returns a
     * {@code List<String>} with, for each named package,
     *
     * @param packages The names of the packages to scan in Java format, i.e.
     *                   using the "." separator, may be null.
     *
     * @return A list of package names.
     */
    public static List<String> searchSubFiles(final String packagee) {
        final List<String> result     = new ArrayList<String>();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String fileP          = packagee.replace('.', '/');
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result.addAll(scan(uri, fileP, false));
                } catch (URISyntaxException e) {
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
        }
        return result;
    }

    /**
     * Searches in the Context ClassLoader for the named packages and returns a
     * {@code List<String>} with, for each named package,
     *
     * @param packages The names of the packages to scan in Java format, i.e.
     *                   using the "." separator, may be null.
     *
     * @return A list of package names.
     */
    public static List<String> searchSubPackage(final String... packages) {
        final List<String> result     = new ArrayList<String>();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        for (String p : packages) {
            try {
                final String fileP          = p.replace('.', '/');
                final Enumeration<URL> urls = classloader.getResources(fileP);
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    try {
                        final URI uri = url.toURI();
                        result.addAll(scan(uri, fileP, true));
                    } catch (URISyntaxException e) {
                        LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                    }
                }
            } catch (IOException ex) {
                LOGGER.severe("The resources for the package" + p + ", could not be obtained");
            }
        }
        return result;
    }

    /**
     * Scan a resource file (a JAR or a directory) to find the sub-package names of
     * the specified "filePackageName"
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static List<String> scan(final URI u, final String filePackageName, boolean directory) throws IOException {
        final List<String> result = new ArrayList<String>();
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            final File f = new File(u.getPath());
            if (f.isDirectory()) {
                result.addAll(scanDirectory(f, filePackageName, directory));
            } else if (!directory) {
                LOGGER.info("added :" + f.getPath());
                result.add(f.getPath());
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            try {
                final URI jarUri = URI.create(u.getSchemeSpecificPart());
                String jarFile   = jarUri.getPath();
                jarFile          = jarFile.substring(0, jarFile.indexOf('!'));
                result.addAll(scanJar(new File(jarFile), filePackageName, directory));

            } catch (IllegalArgumentException ex) {
                LOGGER.warning("unable to scan jar file: " +u.getSchemeSpecificPart());
            }
        }
        return result;
    }

    /**
     * Scan a directory to find the sub-package names of
     * the specified "parent" package
     *
     * @param root The root file (directory) of the package to scan.
     * @param parent the package name.
     *
     * @return a list of package names.
     */
    public static List<String> scanDirectory(final File root, final String parent, boolean directory) {
        final List<String> result = new ArrayList<String>();
        for (File child : root.listFiles()) {
            if (child.isDirectory()) {
                if (directory) {
                    result.add(parent.replace('/', '.') + '.' + child.getName());
                }
                result.addAll(scanDirectory(child, parent, directory));
            } else if (!directory) {
                LOGGER.info("added :" + child.getPath());
                result.add(child.getPath());
            }
        }
        return result;
    }

    /**
     * Scan a jar to find the sub-package names of
     * the specified "parent" package
     *
     * @param file the jar file containing the package to scan
     * @param parent the package name.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static List<String> scanJar(final File file, final String parent, boolean directory) throws IOException {
        final List<String> result = new ArrayList<String>();
        final JarFile jar         = new JarFile(file);
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry e = entries.nextElement();
            if (e.isDirectory() && e.getName().startsWith(parent) && directory) {
                String s = e.getName().replace('/', '.');
                s = s.substring(0, s.length() - 1);
                result.add(s);
            } else if (!e.isDirectory() && e.getName().startsWith(parent) && !directory) {
                String s = e.getName().replace('/', '.');
                s = s.substring(0, s.length() - 1);
                result.add(s);
            }
        }
        return result;
    }
    
    /**
     * Encode the specified string with MD5 algorithm.
     * 
     * @param key :  the string to encode.
     * @return the value (string) hexadecimal on 32 bits
     */
    public static String md5encode(final String key) {

        final byte[] uniqueKey = key.getBytes();
        byte[] hash = null;
        try {
            // we get an object allowing to crypt the string
            hash = MessageDigest.getInstance("MD5").digest(uniqueKey);

        } catch (NoSuchAlgorithmException e) {
            throw new Error("no MD5 support in this VM");
        }
        final StringBuffer hashString = new StringBuffer();
        for (int i = 0; i < hash.length; ++i) {
            final String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length() - 1));
            } else {
                hashString.append(hex.substring(hex.length() - 2));
            }
        }
        return hashString.toString();
    }

    /**
     * Return an marshallable Object from an url
     */
    public static Object getUrlContent(final String url, final Unmarshaller unmarshaller) throws MalformedURLException, IOException {
        final URL source         = new URL(url);
        final URLConnection conec = source.openConnection();
        Object response = null;
        
        try {
        
            // we get the response document
            final InputStream in   = conec.getInputStream();
            final StringWriter out = new StringWriter();
            final byte[] buffer    = new byte[1024];
            int size;

            while ((size = in.read(buffer, 0, 1024)) > 0) {
                out.write(new String(buffer, 0, size));
            }

            //we convert the brut String value into UTF-8 encoding
            String brutString = out.toString();

            //we need to replace % character by "percent because they are reserved char for url encoding
            brutString = brutString.replaceAll("%", "percent");
            final String decodedString = java.net.URLDecoder.decode(brutString, "UTF-8");
            
            try {
                response = unmarshaller.unmarshal(new StringReader(decodedString));
                if (response instanceof JAXBElement) {
                    response = ((JAXBElement<?>) response).getValue();
                }
            } catch (JAXBException ex) {
                LOGGER.severe("The distant service does not respond correctly: unable to unmarshall response document." + '\n' +
                        "cause: " + ex.getMessage());
            }
        } catch (IOException ex) {
            LOGGER.severe("The Distant service have made an error");
            return null;
        }
        return response;
    }
    
    /**
     * 
     * @param enumeration
     * @return
     */
    public static String getElementNameFromEnum(final Object enumeration) {
        String value = "";
        try {
            final Method getValue = enumeration.getClass().getDeclaredMethod("value");
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
     * Obtain the Thread Context ClassLoader.
     */
    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    /**
     * Return an input stream of the specified resource. 
     */
    public static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }

    /**
     * Load the properties from a properies file. 
     * 
     * If the file does not exist it will be created and an empty Properties object will be return.
     * 
     * @param f a properties file.
     * 
     * @return a Properties Object.
     */
    public static Properties getPropertiesFromFile(final File f) throws IOException {
        if (f != null) {
            final Properties prop = new Properties();
            if (f.exists()) {
                final FileInputStream in = new FileInputStream(f);
                prop.load(in);
                in.close();
            } else {
                f.createNewFile();
            }
            return prop;
        } else {
            throw new IllegalArgumentException(" the properties file can't be null");
        }
    }
    
    /**
     * store an Properties object "prop" into the specified File
     * 
     * @param prop A properties Object.
     * @param f    A file.
     * @throws org.constellation.coverage.web.WebServiceException
     */
    public static void storeProperties(final Properties prop, final File f) throws IOException {
        if (prop == null || f == null) {
            throw new IllegalArgumentException(" the properties or file can't be null");
        } else {
            final FileOutputStream out = new FileOutputStream(f);
            prop.store(out, "");
            out.close();
        }
    }

    /**
     * Returns the mime type matching the extension of an image file.
     * For example, for a file "my_image.png" it will return "image/png", in most cases.
     *
     * @param extension The extension of an image file.
     * @return The mime type for the extension specified.
     *
     * @throws IIOException if no image reader are able to handle the extension given.
     */
    public static String fileExtensionToMimeType(final String extension) throws IIOException {
        final Iterator<ImageReaderSpi> readers = IIORegistry.lookupProviders(ImageReaderSpi.class);
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] suffixes = reader.getFileSuffixes();
            for (String suffixe : suffixes) {
                if (extension.equalsIgnoreCase(suffixe)) {
                    final String[] mimeTypes = reader.getMIMETypes();
                    if (mimeTypes != null && mimeTypes.length > 0) {
                        return mimeTypes[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the extension specified: "+ extension);
    }

    /**
     * Returns the mime type matching the format name of an image file.
     * For example, for a format name "png" it will return "image/png", in most cases.
     *
     * @param format name The format name of an image file.
     * @return The mime type for the format name specified.
     *
     * @throws IIOException if no image reader are able to handle the format name given.
     */
    public static String formatNameToMimeType(final String formatName) throws IIOException {
        final Iterator<ImageReaderSpi> readers = IIORegistry.lookupProviders(ImageReaderSpi.class);
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] formats = reader.getFormatNames();
            for (String format : formats) {
                if (formatName.equalsIgnoreCase(format)) {
                    final String[] mimeTypes = reader.getMIMETypes();
                    if (mimeTypes != null && mimeTypes.length > 0) {
                        return mimeTypes[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the format name specified: "+ formatName);
    }

    /**
     * Returns the format name matching the mime type of an image file.
     * For example, for a mime type "image/png" it will return "png", in most cases.
     *
     * @param mimeType The mime type of an image file.
     * @return The format name for the mime type specified.
     *
     * @throws IIOException if no image reader are able to handle the mime type given.
     */
    public static String mimeTypeToFormatName(final String mimeType) throws IIOException {
        final Iterator<ImageReaderSpi> readers = IIORegistry.lookupProviders(ImageReaderSpi.class);
        while (readers.hasNext()) {
            final ImageReaderSpi reader = readers.next();
            final String[] mimes = reader.getMIMETypes();
            for (String mime : mimes) {
                if (mimeType.equalsIgnoreCase(mime)) {
                    final String[] formats = reader.getFormatNames();
                    if (formats != null && formats.length > 0) {
                        return formats[0];
                    }
                }
            }
        }
        throw new IIOException("No available image reader able to handle the mime type specified: "+ mimeType);
    }

    /**
     * Transform an exception code into the OWS specification.
     * Example : MISSING_PARAMETER_VALUE become MissingParameterValue.
     *
     * @param code
     * @return
     */
    public static String transformCodeName(String code) {
        final StringBuilder result = new StringBuilder();
        while (code.indexOf('_') != -1) {
            final String tmp = code.substring(0, code.indexOf('_')).toLowerCase();
            result.append(StringUtilities.firstToUpper(tmp));
            code = code.substring(code.indexOf('_') + 1, code.length());
        }
        code = code.toLowerCase();
        result.append(StringUtilities.firstToUpper(code));
        return result.toString();
    }
    
    /**
     * Read the contents of a file into string.
     * 
     * @param f the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String stringFromFile(File f) throws IOException {

        if (f != null) {
            final StringBuilder sb  = new StringBuilder();
            final BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null){
                sb.append(line).append('\n');
            }
            br.close();

            return sb.toString();
        }
        return null;
    }
    
    /**
     * Write the contents of a file into string.
     * 
     * @param f the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void stringToFile(File f, String s) throws IOException {
        
        final BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(s);
        bw.close();
    }

    /**
     * Execute a SQL script located into the resources.
     *
     * @param path the path to the name of the file example : org.constellation.sos.sql in Resources folder.
     * @param connection.
     */
    public static void executeSQLScript(String path, Connection connection) {
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(getResourceAsStream(path), "UTF-8"));
            final StringWriter sw   = new StringWriter();
            final char[] buffer     = new char[1024];
            int size;
            while ((size = in.read(buffer, 0, 1024)) > 0) {
                sw.append(new String(buffer, 0, size));
            }
            in.close();

            final Statement stmt  = connection.createStatement();
            String SqlQuery       = sw.toString();
            int end               = SqlQuery.indexOf(';');
            int nbQuery           = 0;
            while (end != -1) {
                final String singleQuery = SqlQuery.substring(0, end);
                try {
                    stmt.execute(singleQuery);
                    nbQuery++;
                } catch (SQLException ex) {
                    LOGGER.severe("SQLException while executing: " + singleQuery + '\n' + ex.getMessage() + '\n' + " in file:" + path + " instruction n° " + nbQuery);
                }
                SqlQuery = SqlQuery.substring(end + 1);
                end      = SqlQuery.indexOf(';');
            }
        } catch (IOException ex) {
            LOGGER.severe("IOException creating statement:" + '\n' + ex.getMessage());
        } catch (SQLException ex) {
            LOGGER.severe("SQLException creating statement:" + '\n' + ex.getMessage());
        }
    }
}

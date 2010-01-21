/* Copyright 2009-2010 Tracy Flynn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.olioinfo.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Find all the property files in the specified trees, and load them all into a single Properties instance
 *
 * @author Tracy Flynn
 * @since Jan 21, 2010
 */
public class CombinedPropertyFileLoader {

    private  org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CombinedPropertyFileLoader.class);

    private Properties combinedProperties = new Properties();

    private ArrayList<String> searchPaths = new ArrayList<String>();

    private String propertyFileName = null;

    /**
     * Load and combined property files found in directory tree rooted at path
     *
     * @param path Top of tree
     * @param propertyFileName Name of property file to load
     * @return Properties instance with contents of all properties files combined
     * 
     */
    public static Properties loadAndCombineProperties(String path, String propertyFileName) {
        ArrayList<String> pathList = new ArrayList<String>();
        pathList.add(path);
        return CombinedPropertyFileLoader.loadAndCombineProperties(pathList,propertyFileName);
    } 

    /**
     * Load and combined property files found in directory trees rooted at specified paths
     *
     * @param paths List of paths to search
     * @param propertyFileName Name of property file to load
     * @return Properties instance with contents of all properties files combined
     *
     */
    public static Properties loadAndCombineProperties(ArrayList<String> paths, String propertyFileName) {
        CombinedPropertyFileLoader propertyFileLoader = new CombinedPropertyFileLoader();
        propertyFileLoader.setSearchPaths(paths);
        propertyFileLoader.setPropertyFileName(propertyFileName);
        return propertyFileLoader.loadAll();
    }

    /**
     * Instantiate a new CombinedPropertyFileLoader instance
     *
     * Initialize logging
     */
    public CombinedPropertyFileLoader(){

        org.apache.log4j.Level loggerLevel = org.apache.log4j.Level.WARN;

        String overrideLogLevel = System.getProperty("net.olioinfo.fileutils.CombinedPropertyFileLoader.logLevel");
        if (overrideLogLevel != null) {
            try {
                loggerLevel = org.apache.log4j.Level.toLevel(overrideLogLevel);
            }
            catch (Exception ex) {
                loggerLevel = org.apache.log4j.Level.WARN;
            }
        }
        logger.setLevel(loggerLevel);
        org.apache.log4j.PatternLayout defaultLayout = new org.apache.log4j.PatternLayout();
        org.apache.log4j.ConsoleAppender appender = new org.apache.log4j.ConsoleAppender(defaultLayout);
        logger.addAppender(appender);
    }

    /**
     * Get the list of search paths for this CombinedPropertyFileLoader
     *
     * @return List of search paths
     */
    public ArrayList<String> getSearchPaths() {
        return searchPaths;
    }

    /**
     * Set the list of search paths for this CombinedPropertyFileLoader
     * @param searchPaths
     */
    public void setSearchPaths(ArrayList<String> searchPaths) {
        this.searchPaths = searchPaths;
    }

    /**
     * Get the name of the property file being searched for
     * @return Name of property file
     */
    public String getPropertyFileName() {
        return propertyFileName;
    }

    /**
     * Set the name of the property file being searched for
     * 
     * @param propertyFileName
     */
    public void setPropertyFileName(String propertyFileName) {
        this.propertyFileName = propertyFileName;
    }

    /**
     * Load all the properties in the search paths listed
     *
     * @return Properties instance with contents of all properties files combined
     */
    public Properties loadAll() {

        class PropertiesFileTraverser extends AbstractFileAndJarTraverser {

            public boolean includeFile(VirtualFileEntry virtualFileEntry) {
                if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR ) {
                    if (virtualFileEntry.getRelativeFilePath().endsWith(propertyFileName)) {
                        if (logger.isTraceEnabled()) logger.trace("Property file " + propertyFileName + " matched in Jar file " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                        return true;
                    }
                }
                else if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_FILE ) {
                    if (virtualFileEntry.getAbsoluteFilePath().endsWith(propertyFileName)) {
                        if (logger.isTraceEnabled()) logger.trace("Property file " + propertyFileName + " matched by " + virtualFileEntry.getAbsoluteFilePath());
                        return true;
                    }
                }
                return false;
            }

           public boolean includeDirectory(VirtualFileEntry virtualFileEntry) {
               return false;
           }

        }


        ArrayList<VirtualFileEntry> allPropertyFileEntries = new ArrayList<VirtualFileEntry>();

        Iterator<String> pathsItr = searchPaths.iterator();

        while (pathsItr.hasNext()) {
            String path = pathsItr.next();
            if (logger.isTraceEnabled()) logger.trace("About to find properties from path " + path);
            try {
                PropertiesFileTraverser propertiesFileTraverser = new PropertiesFileTraverser();
                propertiesFileTraverser.traverse(new File(path));
                allPropertyFileEntries.addAll(propertiesFileTraverser.getFileList());
            }
            catch (Exception ex) {
                logger.warn("Error finding property files to load. Ignoring ..",ex);
            }
        }


        Iterator<VirtualFileEntry> virtualFileEntryItr =  allPropertyFileEntries.iterator();
        while (virtualFileEntryItr.hasNext()) {
            VirtualFileEntry virtualFileEntry = virtualFileEntryItr.next();
            try {
                if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR) {
                    JarFile jarFile = new JarFile(virtualFileEntry.getAbsoluteFilePath());
                    JarEntry jarEntry = jarFile.getJarEntry(virtualFileEntry.getRelativeFilePath());
                    InputStream jarEntryInputStream = jarFile.getInputStream(jarEntry);
                    if (logger.isTraceEnabled()) logger.trace("About to load properties file from jar " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                    if (virtualFileEntry.getRelativeFilePath().endsWith(".xml")) {
                        combinedProperties.loadFromXML(jarEntryInputStream);
                    }
                    else {
                        combinedProperties.load(jarEntryInputStream);
                    }
                    jarEntryInputStream.close();
                    jarFile.close();;
                }
                else if (virtualFileEntry.getFileType() == virtualFileEntry.TYPE_FILE) {
                    if (logger.isTraceEnabled()) logger.trace("About to load properties file " + virtualFileEntry.getAbsoluteFilePath());
                    if (virtualFileEntry.getAbsoluteFilePath().endsWith(".xml")) {
                        combinedProperties.loadFromXML(new FileInputStream(virtualFileEntry.getAbsoluteFilePath()));
                    }
                    else {
                        combinedProperties.load(new FileInputStream(virtualFileEntry.getAbsoluteFilePath()));
                    }
                }
            }
            catch (Exception ex) {
                logger.warn("Error while loading properties files. Ignoring ..",ex);

            }
        }

        // Hand back whatever was found. Errors are logged but don't get loaded (obviously!)
        return combinedProperties;

    }

    /**
     * Merge two sets of properties, returnning the original set (now merged)
     * 
     * @param original
     * @param fresh
     * @return Merged set
     */
    public static Properties mergeProperties(Properties original, Properties fresh) {
        Enumeration freshPropsEnum = fresh.keys();
        while (freshPropsEnum.hasMoreElements()) {
            String key = (String) freshPropsEnum.nextElement();
            String value = fresh.getProperty(key);
            original.setProperty(key,value);
        }
        return original;
    }
}
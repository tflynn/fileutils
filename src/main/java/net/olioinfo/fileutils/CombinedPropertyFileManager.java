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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>Find or find and load all property files in the directory trees rooted in specified paths.</p>
 *
 * <p>For example:</p>
 * <pre>
 * Properties combinedProps = CombinedPropertyFileManager.loadAndCombineProperties(System.getProperty("user.dir"),"test-props.properties");
 * </pre>
 * <p>will find load all the property files named 'test-props.properties' in the tree rooted at
 *  'System.getProperty("user.dir")' ('user.dir' is usually the directory active when the JVM is started).</p>
 *
 * @author Tracy Flynn
 * @version 0.6
 * @since 0.1
 */
public class CombinedPropertyFileManager {

    public  static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CombinedPropertyFileManager.class);

    private Properties combinedProperties = new Properties();

    private ArrayList<String> searchPaths = new ArrayList<String>();

    private String propertyFileName = null;

    private Pattern compiledPropertyFilenameRegex = null;

    protected boolean consoleTracing = false;

    /**
     * <p>Load and combined property files found in directory tree rooted at path.</p>
     *
     * <p><strong>Note that the regex implementation currently has problems and should not be used.</strong></p>
     *
     * @param path Top of tree
     * @param propertyFileNameRegex Regex for Name of property file to load
     * @return Properties instance with contents of all properties files combined
     * 
     */
    public static Properties loadAndCombineProperties(String path, String propertyFileNameRegex) {
        ArrayList<String> pathList = new ArrayList<String>();
        pathList.add(path);
        return CombinedPropertyFileManager.loadAndCombineProperties(pathList,propertyFileNameRegex);
    } 

    /**
     * <p>Load and combined property files found in directory trees rooted at specified paths</p>
     *
     * <p><strong>Note that the regex implementation currently has problems and should not be used.</strong></p>
     *
     * @param paths List of paths to search
     * @param propertyFileNameRegex Regex for Name of property file to load
     * @return Properties instance with contents of all properties files combined
     *
     */
    public static Properties loadAndCombineProperties(ArrayList<String> paths, String propertyFileNameRegex) {
        CombinedPropertyFileManager propertyFileLoader = new CombinedPropertyFileManager();
        propertyFileLoader.setSearchPaths(paths);
        propertyFileLoader.setPropertyFileName(propertyFileNameRegex);
        return propertyFileLoader.loadAll();
    }

    /**
     * <p>Create an instance of CombinedPropertyFileManager.</p>
     *
     */
    public CombinedPropertyFileManager(){
        if (System.getProperty("net.olioinfo.fileutils.consoleTracing") != null) {
            consoleTracing = true;
        }
    }

    /**
     * Get the list of search paths for this CombinedPropertyFileManager
     *
     * @return List of search paths
     */
    public ArrayList<String> getSearchPaths() {
        return searchPaths;
    }

    /**
     * Set the list of search paths for this CombinedPropertyFileManager
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
        try {
            this.compiledPropertyFilenameRegex = Pattern.compile(propertyFileName);
            if (logger.isTraceEnabled()) logger.trace("Regex compilation succeeded for \"" + propertyFileName + "\"");
            if (consoleTracing) System.out.println("Regex compilation succeeded for \"" + propertyFileName + "\"");
        }
        catch (Exception ex) {
            logger.info("Unable to treat filename as regular expression. Defaulting to matching at end of path.",ex);
            this.compiledPropertyFilenameRegex = null;
        }

    }

    /**
     * Find all the properties in the search paths listed
     *
     * @return List of all matching properties files
     */
    public ArrayList<VirtualFileEntry> findAll() {

        class PropertiesFileTraverser extends AbstractFileAndJarTraverser {

            public boolean includeFile(VirtualFileEntry virtualFileEntry) {

                if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR ) {
                    if (compiledPropertyFilenameRegex != null) {
                        Matcher matcher = compiledPropertyFilenameRegex.matcher(virtualFileEntry.getRelativeFilePath());
                        if (matcher.matches()) {
                            if (logger.isTraceEnabled()) logger.trace("Property file " + propertyFileName + " regex matched in Jar file " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                            if (consoleTracing) System.out.println("Property file " + propertyFileName + " regex matched in Jar file " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                            return true;
                        }
                    }
                    // Deliberately fall through to simple casd if match doesn't work - that way we don't have to specify a regex to get a match
                    if (virtualFileEntry.getRelativeFilePath().endsWith(propertyFileName)) {
                        if (logger.isTraceEnabled()) logger.trace("Property file " + propertyFileName + " matched in Jar file " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                        if (consoleTracing) System.out.println("Property file " + propertyFileName + " matched in Jar file " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                        return true;
                    }
                }
                else if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_FILE ) {
                    if (compiledPropertyFilenameRegex != null) {
                        Matcher matcher = compiledPropertyFilenameRegex.matcher(virtualFileEntry.getAbsoluteFilePath());
                        if (matcher.matches()) {
                            if (logger.isTraceEnabled()) logger.trace("Property file " + propertyFileName + " regex matched by " + virtualFileEntry.getAbsoluteFilePath());
                            if (consoleTracing) System.out.println("Property file " + propertyFileName + " regex matched by " + virtualFileEntry.getAbsoluteFilePath());
                            return true;
                        }
                    }
                    // Deliberately fall through to simple casd if match doesn't work - that way we don't have to specify a regex to get a match
                    if (virtualFileEntry.getAbsoluteFilePath().endsWith(propertyFileName)) {
                        if (logger.isTraceEnabled()) logger.trace("Property file " + propertyFileName + " matched by " + virtualFileEntry.getAbsoluteFilePath());
                        if (consoleTracing) System.out.println("Property file " + propertyFileName + " matched by " + virtualFileEntry.getAbsoluteFilePath());
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
            if (consoleTracing) System.out.println("About to find properties from path " + path);
            try {
                PropertiesFileTraverser propertiesFileTraverser = new PropertiesFileTraverser();
                propertiesFileTraverser.traverse(new File(path));
                allPropertyFileEntries.addAll(propertiesFileTraverser.getFileList());
            }
            catch (Exception ex) {
                logger.warn("Error finding property files to load. Ignoring ..",ex);
            }
        }

        return allPropertyFileEntries;

    }


    /**
     * Load all the properties in the search paths listed
     *
     * @return Properties instance with contents of all properties files combined
     */
    public Properties loadAll() {

        ArrayList<VirtualFileEntry> allPropertyFileEntries = findAll();

        Iterator<VirtualFileEntry> virtualFileEntryItr =  allPropertyFileEntries.iterator();
        while (virtualFileEntryItr.hasNext()) {
            VirtualFileEntry virtualFileEntry = virtualFileEntryItr.next();
            loadSingle(virtualFileEntry,combinedProperties);
        }

        // Hand back whatever was found. Errors are logged but don't get loaded (obviously!)
        return combinedProperties;

    }

    /**
     * Load a single file from a supplied virtual file entry
     *
     * @param virtualFileEntry
     * @param properties Properties instance, if null a new properties instance is created and returned
     * @return Properties file instance with the specified file loaded, or empty instance if any errors
     */
    public Properties loadSingle(VirtualFileEntry virtualFileEntry,Properties properties){

        if (properties == null) {
            properties = new Properties();
        }

        try {
            if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR) {
                JarFile jarFile = new JarFile(virtualFileEntry.getAbsoluteFilePath());
                JarEntry jarEntry = jarFile.getJarEntry(virtualFileEntry.getRelativeFilePath());
                InputStream jarEntryInputStream = jarFile.getInputStream(jarEntry);
                if (logger.isTraceEnabled()) logger.trace("About to load properties file from jar " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                if (consoleTracing) System.out.println("About to load properties file from jar " + virtualFileEntry.getAbsoluteFilePath() + ":" + virtualFileEntry.getRelativeFilePath());
                if (virtualFileEntry.getRelativeFilePath().endsWith(".xml")) {
                    properties.loadFromXML(jarEntryInputStream);
                }
                else {
                    properties.load(jarEntryInputStream);
                }
                jarEntryInputStream.close();
                jarFile.close();;
            }
            else if (virtualFileEntry.getFileType() == virtualFileEntry.TYPE_FILE) {
                if (logger.isTraceEnabled()) logger.trace("About to load properties file " + virtualFileEntry.getAbsoluteFilePath());
                if (consoleTracing) System.out.println("About to load properties file " + virtualFileEntry.getAbsoluteFilePath());
                if (virtualFileEntry.getAbsoluteFilePath().endsWith(".xml")) {
                    properties.loadFromXML(new FileInputStream(virtualFileEntry.getAbsoluteFilePath()));
                }
                else {
                    properties.load(new FileInputStream(virtualFileEntry.getAbsoluteFilePath()));
                }
            }
        }
        catch (Exception ex) {
            logger.warn("Error while loading properties files. Ignoring ..",ex);

        }

        return properties;

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
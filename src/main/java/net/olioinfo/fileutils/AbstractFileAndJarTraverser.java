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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * <p>An abstract class that implements an AbstractFileTraverser for all Jar files and regular files in a given tree.</p>
 *
 * <p>To make a concrete class, the includeFile and includeDirectory methods must be implemented
 * to define selection criteria for inclusion in the final list of matching files and directories.</p>
 *
 * @author Tracy Flynn
 * @since Jan 20, 2010
 */
public abstract class AbstractFileAndJarTraverser extends AbstractFileTraverser {

    private  org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstractFileAndJarTraverser.class);

    private static final String JAR_FILE_EXTENSION = ".jar";

    private ArrayList<VirtualFileEntry> fileList = new ArrayList<VirtualFileEntry>();
    private ArrayList<VirtualFileEntry> directoryList = new ArrayList<VirtualFileEntry>();

    /**
     * <p>Create an instance of AbstractFileAndJarTraverser.</p>
     *
     * <p>Initialize logging using log4j. The default 'WARN' logging level can be overridden by specifying
     * -Dnet.olioinfo.fileutils.AbstractFileAndJarTraverser.logLevel=TRACE (or other level) when starting the JVM.
     * Configuration using the standard log4j.properties approach also works.</p>
     */
    public AbstractFileAndJarTraverser() {
        super();
        org.apache.log4j.Level loggerLevel = org.apache.log4j.Level.WARN;

        String overrideLogLevel = System.getProperty("net.olioinfo.fileutils.AbstractFileAndJarTraverser.logLevel");
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
     * Implementation of the AbstractFileTraverser.onFile method for Jar and regular files
     *
     * @param f File object representing the file to be processed
     * 
     */
    public void onFile( final File f ) {

        String path = f.getAbsolutePath();
        if (path.endsWith(AbstractFileAndJarTraverser.JAR_FILE_EXTENSION)) {
            try {
                JarFile jarFile = new JarFile(path);
                Enumeration<JarEntry> entries = jarFile.entries();
                while(entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();

                    VirtualFileEntry virtualFileEntry = new VirtualFileEntry();
                    virtualFileEntry.setAbsoluteFilePath(path);
                    virtualFileEntry.setFileType(VirtualFileEntry.TYPE_JAR);
                    virtualFileEntry.setRelativeFilePath(jarEntry.getName());

                    if (includeFile(virtualFileEntry)) {
                        if (logger.isTraceEnabled()) logger.trace("Adding Jar entry to virtual file list: " + path + ":" + jarEntry.getName());
                        fileList.add(virtualFileEntry);
                    }
                }
                jarFile.close();
            }
            catch (Exception ex) {
                logger.warn("Error during onFile processing " + path + " generated an error. This file will be ignored.",ex);
            }

        }
        else {

            VirtualFileEntry virtualFileEntry = new VirtualFileEntry();
            virtualFileEntry.setAbsoluteFilePath(path);
            virtualFileEntry.setFileType(VirtualFileEntry.TYPE_FILE);

            if (includeFile(virtualFileEntry)) {
                if (logger.isTraceEnabled()) logger.trace("Adding regular file entry to virtual file list: " + path );
                fileList.add(virtualFileEntry);
            }
        }

    }

    /**
     * Implementation of the AbstractFileTraverser.onDirectory method for Jar and regular files
     *
     * @param f File object representing the file to be processed
     *
     */
    public void onDirectory( final File f ) {
        //Do nothing
    }


    /**
     * Get a list of the matching file entries
     *
     * @return List of file entries
     */
    public ArrayList<VirtualFileEntry> getFileList() {
        return fileList;
    }

    /**
     * Get a list of the matching directory entries
     *
     * @return List of matching directory entries
     */
    public ArrayList<VirtualFileEntry> getDirectoryList() {
        return directoryList; 
    }


    /**
     * Indicate whether file should be included in the list of matching files
     *
     * @param virtualFileEntry
     * @return true if the file should be included in the list, false otherwise
     */
    public abstract boolean includeFile(VirtualFileEntry virtualFileEntry);


    /**
     * Indicate whether directory should be included in the list of matching files
     *
     * @param virtualFileEntry
     * @return true if the  directory should be included in the list, false otherwise
     */
    public abstract boolean includeDirectory(VirtualFileEntry virtualFileEntry);



}
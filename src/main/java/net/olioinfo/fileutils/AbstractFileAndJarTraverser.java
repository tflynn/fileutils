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
 * Abstract class that implements a AbstractFileTraverser for all Jar files and regular files in a given tree.
 *
 * The includeFile and includeDirectory methods must be implemented to defined selection criteria for
 * inclusion in the final list of matching files and directories.
 *
 * @author Tracy Flynn
 * @since Jan 20, 2010
 */
public abstract class AbstractFileAndJarTraverser extends AbstractFileTraverser {

    private static final String JAR_FILE_EXTENSION = ".jar";

    private ArrayList<VirtualFileEntry> fileList = new ArrayList<VirtualFileEntry>();
    private ArrayList<VirtualFileEntry> directoryList = new ArrayList<VirtualFileEntry>();

    
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
                        logger.trace("Adding to file list path: " + path );
                        fileList.add(virtualFileEntry);
                    }
                }
                jarFile.close();
            }
            catch (Exception ex) {
                logger.warn("net.olioinfo.fileutils.AbstractFileAndJarTraverser.onFile processing " + path + " generated an error. This file will be ignored.",ex);
            }

        }
        else {

            VirtualFileEntry virtualFileEntry = new VirtualFileEntry();
            virtualFileEntry.setAbsoluteFilePath(path);
            virtualFileEntry.setFileType(VirtualFileEntry.TYPE_FILE);

            if (includeFile(virtualFileEntry)) {
                logger.trace("Adding to file list path: " + path );
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
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
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Match files in a tree
 *
 * @author Tracy Flynn
 * @version 0.6
 * @since 0.4
 */
public class MatchingFileTraverser extends AbstractFileTraverser {

    public  static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MatchingFileTraverser.class);

    private String filePatternMatch;

    private Pattern filePatternMatchRegex;


    private ArrayList<String> fileList = new ArrayList<String>();
    private ArrayList<String> directoryList = new ArrayList<String>();
    
    /**
     * <p>Create an instance of MatchingFileTraverser.</p>
     *
     */
    public MatchingFileTraverser() {
        super();
    }

    /**
     * Reset the file and directory lists
     */
    public void resetList() {
        this.fileList = new ArrayList<String>();
        this.directoryList = new ArrayList<String>();
    }

    /**
     * Get the matching file pattern
     *
     * @return Matching file pattern
     */
    public String getFilePatternMatch() {
        return this.filePatternMatch;
    }

    public void setFilePatternMatch(String filePatternMatch) {
        this.filePatternMatch = filePatternMatch;
        try {
            this.filePatternMatchRegex = Pattern.compile(this.filePatternMatch);
        }
        catch (Exception ex) {
            logger.error("Exception raised while trying to compile supplied file name regular expression \"" + this.filePatternMatch + "\"");
            this.filePatternMatchRegex = null;
        }
    }


    public ArrayList<String> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<String> fileList) {
        this.fileList = fileList;
    }

    public ArrayList<String> getDirectoryList() {
        return directoryList;
    }

    public void setDirectoryList(ArrayList<String> directoryList) {
        this.directoryList = directoryList;
    }

    

    /**
     * Implementation of the AbstractFileTraverser.onFile method for Jar and regular files
     *
     * @param f File object representing the file to be processed
     *
     */
    public void onFile( final File f ) {
        String path = f.getAbsolutePath();
        Matcher matcher = this.filePatternMatchRegex.matcher(path);
        if (matcher.matches()) {
            this.fileList.add(path);
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
     * <p>Find all the files in the directory trees rooted in the given paths that are in a directory corresponding
     * to the package of the specified ciass and match the file name specified.</p>
     *
     * @param klass Class for package to search
     * @param paths List of fully-qualified path names to search
     * @param matchingFilename name for files to match
     * @return Array of fully-qualitifed matching file names
     */
    public static ArrayList<String> findFilesByPackageAndPaths(Class klass, ArrayList<String> paths, String matchingFilename) {

        ArrayList<String> matchingPaths = new ArrayList<String>();

        String packageName = klass.getPackage().getName();
        for (String currentPath : paths ) {
            String fullPathName = null;
            try {
                fullPathName  = currentPath + "/" + packageName.replaceAll("\\.","/") + "/" + matchingFilename;
                if ( (new File(fullPathName)).exists()) {
                    matchingPaths.add(fullPathName);
                }
            }
            catch (Exception ex) {
                logger.error("Ignoring error matching path " + fullPathName,ex);
            }
        }
        return matchingPaths;
    }
}



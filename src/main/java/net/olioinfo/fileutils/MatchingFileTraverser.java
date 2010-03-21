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

    private ArrayList<String> fileList = new ArrayList<String>();
    private ArrayList<String> directoryList = new ArrayList<String>();

    private String matchingString = null;

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

    public void setMatchingString(String matchingString) {
        this.matchingString = matchingString;

    }

    public String getMatchingString() {
        return this.matchingString;
    }

    
    /**
     * Implementation of the AbstractFileTraverser.onFile method for Jar and regular files
     *
     * @param f File object representing the file to be processed
     *
     */
    public void onFile( final File f ) {
        String fileName = f.getAbsolutePath();
        Pattern matchingPattern = Pattern.compile(this.matchingString);
        Matcher matcher = matchingPattern.matcher(fileName);
        if (matcher.matches()) {
            this.fileList.add(f.getAbsolutePath());
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
    public static ArrayList<String> findFilesFromPackageAndPaths(Class klass, ArrayList<String> paths, String matchingFilename) {

        boolean  consoleTracing = false;
        if (System.getProperty("net.olioinfo.fileutils.consoleTracing") != null) {
            if (System.getProperty("net.olioinfo.fileutils.consoleTracing").equalsIgnoreCase("true")) {
                consoleTracing = true;
            }
            else {
                consoleTracing = false;
            }
        }



        ArrayList<String> matchingPaths = new ArrayList<String>();

        String packageName = klass.getPackage().getName();
        for (String currentPath : paths ) {
            String fullPathName = null;
            try {
                fullPathName  = String.format("%s/%s",currentPath,convertPackageNameToDirectoriesSegment(packageName));
                MatchingFileTraverser matchingFileTraverser = new MatchingFileTraverser();
                matchingFileTraverser.setMatchingString(matchingFilename);
                try {
                    matchingFileTraverser.traverse(new File(fullPathName));
                }
                catch (Exception ex) {
                    if (consoleTracing) {
                        System.out.format("MatchingFileTravers.findFilesFromPackageAndPaths ignoring exception %s\n",ex.toString());
                        ex.printStackTrace(System.out);
                    }
                }

                ArrayList<String> matchingFileList = matchingFileTraverser.getFileList();
                for (String matchingFile : matchingFileList ) {
                    matchingPaths.add(matchingFile);
                    if (consoleTracing) {
                        System.out.format("MatchingFileTraverser.findFilesFromyPackageAndPaths adding %s\n", matchingFile);
                    }
                }
            }
            catch (Exception ex) {
                if (consoleTracing) {
                    System.out.format("MatchingFileTraverser.findFilesFromyPackageAndPaths Ignoring error %s which matching path %s\n", ex.toString(), fullPathName);
                    ex.printStackTrace(System.out);
                }
            }
        }
        return matchingPaths;
    }

    /**
     * Convert a package name into a directory tree
     *
     * @param packageName A String containing a Java package name that uses standard '.' notation to separate namespace elements
     * @return A string  representing the directory tree segment implied by the package name
     */
    public static String convertPackageNameToDirectoriesSegment(String packageName) {
        return packageName.replaceAll("\\.","/");
    }
}



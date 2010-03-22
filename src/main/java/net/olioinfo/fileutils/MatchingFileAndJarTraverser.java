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
 * Find files matching a specified pattern in JARS or as regular files
 *
 * @author Tracy Flynn
 * @since Mar 21, 2010
 */
public class MatchingFileAndJarTraverser {


    private String matchingString = null;

    
    public void setMatchingString(String matchingString) {
        this.matchingString = matchingString;

    }

    public String getMatchingString() {
        return this.matchingString;
    }

    
    /**
     * <p>Find all the files in the directory trees rooted in the given paths that match the file name specified.</p>
     *
     * @param paths List of fully-qualified path names to search
     * @param fileNameRegex name for files to match
     * @return Array of fully-qualitifed matching file names
     */
    public static ArrayList<VirtualFileEntry> findFilesFromPaths(ArrayList<String> paths, String fileNameRegex) {

        boolean  consoleTracing = false;
        if (System.getProperty("net.olioinfo.fileutils.consoleTracing") != null) {
            if (System.getProperty("net.olioinfo.fileutils.consoleTracing").equalsIgnoreCase("true")) {
                consoleTracing = true;
            }
            else {
                consoleTracing = false;
            }
        }


        final String finalFileNameRegex = fileNameRegex;

        class FileAndJarTraverser extends AbstractFileAndJarTraverser {


            @Override
            public boolean includeFile(VirtualFileEntry virtualFileEntry) {
                Pattern matchingPattern = Pattern.compile(finalFileNameRegex);

                if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR) {
                    Matcher matcher = matchingPattern.matcher(virtualFileEntry.getRelativeFilePath());
                    if (matcher.matches()) {
                        if (consoleTracing) {
                            System.out.format("MatchingFileAndJarTraverser.findFilesFromPaths adding JAR file %s with entry %s to matching list\n", virtualFileEntry.getAbsoluteFilePath(),virtualFileEntry.getRelativeFilePath());
                        }
                        return true;
                    }
                }
                else if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_FILE) {
                    Matcher matcher = matchingPattern.matcher(virtualFileEntry.getRelativeFilePath());
                    if (matcher.matches()) {
                        if (consoleTracing) {
                            System.out.format("MatchingFileAndJarTraverser.findFilesFromPaths adding reqular file %s with entry %s to matching list\n", virtualFileEntry.getAbsoluteFilePath(),virtualFileEntry.getRelativeFilePath());
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean includeDirectory(VirtualFileEntry virtualFileEntry) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

        }

        try {

            ArrayList<VirtualFileEntry> allFileEntries = new  ArrayList<VirtualFileEntry>();
            for (String path : paths) {
                FileAndJarTraverser fileAndJarTraverser = new FileAndJarTraverser();
                fileAndJarTraverser.traverse(new File(path));
                ArrayList<VirtualFileEntry> fileList = fileAndJarTraverser.getFileList();
                allFileEntries.addAll(fileList);                        
            }
            return allFileEntries;
        }
        catch (Exception ex) {
            if (consoleTracing) {
                System.out.format("MatchingFileAndJarTraverser.findFilesFromPaths exception %s\n",ex.toString());
                ex.printStackTrace(System.out);
            }
        }

        return null;

    }


    /**
     * <p>Find all the files in the directory trees rooted in the given paths that are in a directory corresponding
     * to the package of the specified ciass and match the file name specified.</p>
     *
     * @param klass Class for package to search
     * @param paths List of fully-qualified path names to search
     * @param fileNameRegex name for files to match
     * @return Array of fully-qualitifed matching file entries
     */
    public static ArrayList<VirtualFileEntry> findFilesFromPackageAndPaths(Class klass, ArrayList<String> paths, String fileNameRegex) {

        boolean  consoleTracing = false;
        if (System.getProperty("net.olioinfo.fileutils.consoleTracing") != null) {
            if (System.getProperty("net.olioinfo.fileutils.consoleTracing").equalsIgnoreCase("true")) {
                consoleTracing = true;
            }
            else {
                consoleTracing = false;
            }
        }

        String packageFileNameRegex = null;
        if (klass.getPackage() == null) {
            packageFileNameRegex = fileNameRegex;
        }
        else {
            String packageName = klass.getPackage().getName();
            String packagePath = convertPackageNameToDirectoriesSegment(packageName);
            packageFileNameRegex = String.format(".*%s/%s$", packagePath , fileNameRegex);
        }

        if (consoleTracing) {
            System.out.format("MatchingFileAndJarTraverser.findFilesFromPackageAndPaths calling MatchingFileAndJarTraverser.findFilesFromPaths with file name pattern %s\n",packageFileNameRegex);
        }

        return MatchingFileAndJarTraverser.findFilesFromPaths(paths,packageFileNameRegex);

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
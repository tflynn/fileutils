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

    
    public static ArrayList<VirtualFileEntry> findPropertiesFiles(ArrayList<String> paths, String propertyFileNameRegex) {

        boolean  consoleTracing = false;
        if (System.getProperty("net.olioinfo.fileutils.consoleTracing") != null) {
            if (System.getProperty("net.olioinfo.fileutils.consoleTracing").equalsIgnoreCase("true")) {
                consoleTracing = true;
            }
            else {
                consoleTracing = false;
            }
        }


        final String finalPropertyFileNameRegex = propertyFileNameRegex;

        class FileAndJarTraverser extends AbstractFileAndJarTraverser {


            @Override
            public boolean includeFile(VirtualFileEntry virtualFileEntry) {
                Pattern matchingPattern = Pattern.compile(finalPropertyFileNameRegex);

                if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR) {
                    Matcher matcher = matchingPattern.matcher(virtualFileEntry.getRelativeFilePath());
                    if (matcher.matches()) {
                        if (consoleTracing) {
                            System.out.format("MatchingFileAndJarTraverser.findPropertiesFiles adding JAR file %s with entry %s to matching list\n", virtualFileEntry.getAbsoluteFilePath(),virtualFileEntry.getRelativeFilePath());
                        }
                        return true;
                    }
                }
                else if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_FILE) {
                    Matcher matcher = matchingPattern.matcher(virtualFileEntry.getRelativeFilePath());
                    if (matcher.matches()) {
                        if (consoleTracing) {
                            System.out.format("MatchingFileAndJarTraverser.findPropertiesFiles adding reqular file %s with entry %s to matching list\n", virtualFileEntry.getAbsoluteFilePath(),virtualFileEntry.getRelativeFilePath());
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
                System.out.format("testAbstractFileAndJarTraverser exception %s\n",ex.toString());
                ex.printStackTrace(System.out);
            }
        }

        return null;

    }




}
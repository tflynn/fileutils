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
package net.olioinfo.test.fileutils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.olioinfo.fileutils.MatchingFileAndJarTraverser;
import net.olioinfo.fileutils.MatchingFileTraverser;
import net.olioinfo.fileutils.VirtualFileEntry;

import java.io.File;
import java.util.ArrayList;


/**
 * Class description
 *
 * @author Tracy Flynn
 * @since Mar 21, 2010
 */
public class MatchingFileAnJarTraverserTest extends TestCase {

    /**
      * Create the test case
      *
      * @param testName name of the test case
      */
     public MatchingFileAnJarTraverserTest( String testName )
     {
         super( testName );
     }

     /**
      * @return the suite of tests being tested
      */
     public static Test suite()
     {
         return new TestSuite( MatchingFileAnJarTraverserTest.class );
     }


    public void testFileAndMatch() {

        ArrayList<String> paths = new ArrayList<String>();
        String userDir = System.getProperty("user.dir");
        paths.add(String.format("%s/%s",userDir,"src/test/java"));
        ArrayList<VirtualFileEntry> matchingFileList = MatchingFileAndJarTraverser.findFilesFromPaths(paths,".*properties$");

        for (VirtualFileEntry virtualFileEntry : matchingFileList) {
            System.out.format("Matching file found %s:%s\n", virtualFileEntry.getAbsoluteFilePath(),virtualFileEntry.getRelativeFilePath());
        }
        assertTrue("There should be eleven matching files",matchingFileList.size() == 11);

        
    }

}
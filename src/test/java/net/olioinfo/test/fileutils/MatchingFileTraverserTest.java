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
import net.olioinfo.fileutils.MatchingFileTraverser;

import java.util.ArrayList;


/**
 * Class description
 *
 * @author Tracy Flynn
 * @since Jan 31, 2010
 */
public class MatchingFileTraverserTest extends TestCase {


    /**
      * Create the test case
      *
      * @param testName name of the test case
      */
     public MatchingFileTraverserTest( String testName )
     {
         super( testName );
     }

     /**
      * @return the suite of tests being tested
      */
     public static Test suite()
     {
         return new TestSuite( MatchingFileTraverserTest.class );
     }

    public void testFindFilesByPackageAndPaths() {

        ArrayList<String> paths = new ArrayList<String>();
        String userDir = System.getProperty("user.dir");
        paths.add(userDir + "/" + "src/test/java");

        ArrayList<String> matchingPaths;
        matchingPaths = MatchingFileTraverser.findFilesByPackageAndPaths(MatchingFileTraverserTest.class, paths, "resources1/test-props.properties");
        assertEquals(matchingPaths.get(0),"/Users/tracy/Everything/Activities/net-olioinfo-fileutils/src/test/java/net/olioinfo/test/fileutils/resources1/test-props.properties");

        matchingPaths = MatchingFileTraverser.findFilesByPackageAndPaths(MatchingFileTraverserTest.class, paths, "resources2/test-props.properties");
        assertEquals(matchingPaths.get(0),"/Users/tracy/Everything/Activities/net-olioinfo-fileutils/src/test/java/net/olioinfo/test/fileutils/resources2/test-props.properties");


        
    }

}
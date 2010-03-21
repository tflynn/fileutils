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

import net.olioinfo.fileutils.CombinedPropertyFileManager;

import java.util.Properties;


/**
 * Class description
 *
 * @author Tracy Flynn
 * @since Jan 21, 2010
 */
public class CombinedPropertyFileLoaderTest extends TestCase {

    /**
      * Create the test case
      *
      * @param testName name of the test case
      */
     public CombinedPropertyFileLoaderTest( String testName )
     {
         super( testName );
     }

     /**
      * @return the suite of tests being tested
      */
     public static Test suite()
     {
         return new TestSuite( CombinedPropertyFileLoaderTest.class );
     }

     /**
      * Test load with simple file name
      */
     public void testCombinedPropertyFileLoaderTest()
     {
         Properties combinedProps = CombinedPropertyFileManager.loadAndCombineProperties(System.getProperty("user.dir"),".*fileutils-test-defaults.properties$");

         combinedProps.list(System.out);
//         assertEquals("second unique" , combinedProps.getProperty("test.props.2"));
//         assertEquals("first unique" , combinedProps.getProperty("test.props.1"));
//         assertEquals("second" , combinedProps.getProperty("test.props.common"));

     }

//    /**
//     * Test load with file name as regex
//     */
//    public void testCombinedPropertyFileLoaderTestWithRegex()
//    {
//        //Properties combinedProps = CombinedPropertyFileManager.loadAndCombineProperties(System.getProperty("user.dir"),"test-props.properties$");
//        Properties combinedProps = CombinedPropertyFileManager.loadAndCombineProperties(System.getProperty("user.dir") + "/src/test/java/net/olioinfo/test/fileutils","test-props.properties\\$");
//
//        assertEquals("second unique" , combinedProps.getProperty("test.props.2"));
//        assertEquals("first unique" , combinedProps.getProperty("test.props.1"));
//        assertEquals("second" , combinedProps.getProperty("test.props.common"));
//
//    }

}
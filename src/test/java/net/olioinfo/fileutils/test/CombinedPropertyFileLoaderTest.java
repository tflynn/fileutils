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
package net.olioinfo.fileutils.test;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.olioinfo.fileutils.CombinedPropertyFileLoader;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
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
      * Rigourous Test :-)
      */
     public void testCombinedPropertyFileLoaderTest()
     {
         Properties combinedProps = CombinedPropertyFileLoader.loadAndCombineProperties(System.getProperty("user.dir"),"test-props.properties");
//         Enumeration combinedPropsEnum = combinedProps.keys();
//         while (combinedPropsEnum.hasMoreElements()) {
//             String key = (String) combinedPropsEnum.nextElement();
//             String value = combinedProps.getProperty(key);
//             System.out.println(key + " = " + value);
//         }

         assertEquals("second unique" , combinedProps.getProperty("test.props.2"));
         assertEquals("first unique" , combinedProps.getProperty("test.props.1"));
         assertEquals("second" , combinedProps.getProperty("test.props.common"));
         
     }

}
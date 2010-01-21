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
import net.olioinfo.fileutils.VirtualFileEntry;

import java.io.File;
import java.util.Iterator;


/**
 * Class description
 *
 * @author Tracy Flynn
 * @since Jan 20, 2010
 */
public class AbstractFileTraverserTest  extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AbstractFileTraverserTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AbstractFileTraverserTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAbstractFileTraverser()
    {
        System.out.println("Testing recursion in directory " + System.getProperty("user.dir"));

        FileAndJarTraverser traverser = new FileAndJarTraverser();
        try {
            traverser.traverse(new File(System.getProperty("user.dir")));
        }
        catch (Exception ex) {
            //
        }

        Iterator<VirtualFileEntry> traverserItr = traverser.getFileList().iterator();
        while (traverserItr.hasNext()) {
            VirtualFileEntry virtualFileEntry = traverserItr.next();
            System.out.println(virtualFileEntry.getAbsoluteFilePath());
        }
        assertTrue( true );
    }

}




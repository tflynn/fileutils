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
import net.olioinfo.fileutils.AbstractFileTraverser;

import java.io.File;
import java.util.ArrayList;



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
        final ArrayList<String> fileList = new ArrayList<String>();


        class FileTraverser extends AbstractFileTraverser {


            @Override
            public void onDirectory(File d) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onFile(File f) {
                if (f.getAbsolutePath().endsWith("properties")) {
                    if (f.getName().startsWith("fileutils")) {
                        fileList.add(f.getAbsolutePath());
                    }
                }
            }
        }

        try {
            FileTraverser fileTraverser = new FileTraverser();
            fileTraverser.traverse(new File(String.format("%s/src",System.getProperty("user.dir"))));
        }
        catch (Exception ex) {
            System.out.format("testAbstractFileTraverser exception %s\n",ex.toString());
            ex.printStackTrace(System.out);
        }
        assertTrue("There should be four matching files", fileList.size() == 4);

    }


}




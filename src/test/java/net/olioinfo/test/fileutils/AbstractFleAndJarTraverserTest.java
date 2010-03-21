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
import net.olioinfo.fileutils.AbstractFileAndJarTraverser;
import net.olioinfo.fileutils.VirtualFileEntry;

import java.io.File;
import java.util.ArrayList;



/**
 * Class description
 *
 * @author Tracy Flynn
 * @since Jan 20, 2010
 */
public class AbstractFleAndJarTraverserTest  extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AbstractFleAndJarTraverserTest ( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AbstractFleAndJarTraverserTest .class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAbstractFileAndJarTraverser()
    {

        ArrayList<VirtualFileEntry> fileList = null;

        class FileAndJarTraverser extends AbstractFileAndJarTraverser {


            @Override
            public boolean includeFile(VirtualFileEntry virtualFileEntry) {
                if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_JAR) {
                    if (virtualFileEntry.getRelativeFilePath().endsWith("properties")) {
                        if (virtualFileEntry.getRelativeFilePath().startsWith("fileutils")) {
                            return true;
                        }
                    }
                }
                else if (virtualFileEntry.getFileType() == VirtualFileEntry.TYPE_FILE) {
                    if (virtualFileEntry.getAbsoluteFilePath().endsWith("properties")) {
                        if (virtualFileEntry.getRelativeFilePath().startsWith("fileutils")) {
                            return true;
                        }
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
            FileAndJarTraverser fileAndJarTraverser = new FileAndJarTraverser();
            fileAndJarTraverser.traverse(new File(String.format("%s/src",System.getProperty("user.dir"))));
            fileList = fileAndJarTraverser.getFileList();
        }
        catch (Exception ex) {
            System.out.format("testAbstractFileAndJarTraverser exception %s\n",ex.toString());
            ex.printStackTrace(System.out);
        }
        assertTrue("There should be eight matching files", fileList.size() == 8);
//        for(VirtualFileEntry virtualFileEntry : fileList) {
//            System.out.format("%s:%s\n",virtualFileEntry.getAbsoluteFilePath(),virtualFileEntry.getRelativeFilePath());
//        }

    }


}




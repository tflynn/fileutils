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
import java.io.IOException;


/**
 * Traverses a directory tree starting at a given point
 *
 * @author Tracy Flynn
 * @since Jan 20, 2010
 */
public abstract class AbstractFileTraverser {

    private  org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstractFileTraverser.class);


    public AbstractFileTraverser(){
        
        org.apache.log4j.Level loggerLevel = org.apache.log4j.Level.WARN;

        String overrideLogLevel = System.getProperty("net.olioinfo.fileutils.AbstractFileTraverser.logLevel");
        if (overrideLogLevel != null) {
            try {
                loggerLevel = org.apache.log4j.Level.toLevel(overrideLogLevel);
            }
            catch (Exception ex) {
                loggerLevel = org.apache.log4j.Level.WARN;
            }
        }
        logger.setLevel(loggerLevel);
        org.apache.log4j.PatternLayout defaultLayout = new org.apache.log4j.PatternLayout();
        org.apache.log4j.ConsoleAppender appender = new org.apache.log4j.ConsoleAppender(defaultLayout);
        logger.addAppender(appender);
    }

    /**
     * Traverse a tree from a given starting point
     *
     * @param f File object indicating starting point
     * @throws IOException
     */
    public final void traverse( final File f ) throws IOException {
        if (logger.isTraceEnabled()) logger.trace("traverse: file " + f.getAbsolutePath());
        if (f.exists()) {
            if (logger.isTraceEnabled()) logger.trace("traverse: file exists " + f.getAbsolutePath());
            if (f.isDirectory()) {
                if (logger.isTraceEnabled()) logger.trace("traverse: file is a directory " + f.getAbsolutePath());
                onDirectory(f);
                final File[] children = f.listFiles();
                for( File child : children ) {
                    traverse(child);
                }
                return;
            }
            onFile(f);
        }
    }

    /**
     * Perform this processing on each directory. This method should be overridden by implementations
     *
     * @param d File object representing the directory to be processed
     */
    public abstract void onDirectory( final File d );

    /**
     * Perform this processing on each file. This method should be overridden by implementations
     *
     * @param f File object representing the file to be processed
     */
    public abstract void onFile( final File f );
}

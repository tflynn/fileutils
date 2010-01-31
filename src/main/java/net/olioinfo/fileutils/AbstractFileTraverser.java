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


import org.apache.log4j.Appender;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;


/**
 * <p>An abstract cleas that traverses a directory tree starting at a given point.</p>
 *
 * <p>To make a concrete class, extend and implement onDirectory() and onFile() methods. These methods are called once
 * per directory or file encountered while traversing the tree. Call traverse(somePath) on the root of the tree to
 * start the traversal.</p>
 *
 * @author Tracy Flynn
 * @version 0.3
 * @since 0.1
 */
public abstract class AbstractFileTraverser {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstractFileTraverser.class);

    private static final String CONSOLE_APPENDER_NAME = "net.olioinfo.fileutils.AbstractFileTraverser.CONSOLE_APPENDER";

    /**
     * <p>Create an instance of AbstractFileTraverser.</p>
     *
     * <p>Initialize logging using log4j. The default 'WARN' logging level can be overridden by specifying
     * -Dnet.olioinfo.fileutils.logLevel=TRACE (or other level) when starting the JVM.
     * </p>
     *
     * <p>To change the logging for this module in a log4j / log4j-ext properties file use the class names</p>
     * <ul>
     *   <li>net.olioinfo.fileutils.AbstractFileTraverser</li>
     *   <li>net.olioinfo.fileutils.AbstractFileAndJarTraverser</li>
     *   <li>net.olioinfo.fileutils.CombinedPropertyFileManager</li>
     * </ul>
     */
    public AbstractFileTraverser(){
        configureLogging();
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


    /*
     * Configure internal logging. This method may be called safely multiple times
     */
    private static void configureLogging() {

        Enumeration<Appender> appenders = AbstractFileTraverser.logger.getAllAppenders();
        // Only add a console appender if there isn't an appender of any kind there already
        if (! appenders.hasMoreElements()) {
            org.apache.log4j.Level level = org.apache.log4j.Level.WARN;
            String overrideLogLevel = System.getProperty("net.olioinfo.fileutils.logLevel");
            if (overrideLogLevel != null) {
                try {
                    level = org.apache.log4j.Level.toLevel(overrideLogLevel);
                }
                catch (Exception ex) {
                    level = org.apache.log4j.Level.WARN;
                }
            }

            org.apache.log4j.PatternLayout patternLayout = new org.apache.log4j.PatternLayout("%5p [%t] (%F:%L) - %m%n");
            org.apache.log4j.ConsoleAppender appender = new org.apache.log4j.ConsoleAppender(patternLayout,"System.out");
            appender.setName(AbstractFileTraverser.CONSOLE_APPENDER_NAME);

            // Set log levels and appenders for everyone
            AbstractFileTraverser.logger.setLevel(level);
            AbstractFileTraverser.logger.addAppender(appender);
            AbstractFileAndJarTraverser.logger.setLevel(level);
            AbstractFileAndJarTraverser.logger.addAppender(appender);
            CombinedPropertyFileManager.logger.setLevel(level);
            CombinedPropertyFileManager.logger.addAppender(appender);
            MatchingFileTraverser.logger.setLevel(level);
            MatchingFileTraverser.logger.addAppender(appender);


        }
    }

}

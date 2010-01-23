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


/**
 * A representation of a directory, file or JAR file
 *
 * @author Tracy Flynn
 * @version 0.3
 * @since 0.1
 */
public class VirtualFileEntry {

    /**
     * Entry represents a JAR file
     */
    public static final Integer TYPE_JAR = 1;

    /**
     * Entry represents a Directory
     */
    public static final Integer TYPE_DIR = 2;


    /**
     * Entry represents a File
     */
    public static final Integer TYPE_FILE = 3;

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }

    public String getRelativeFilePath() {
        return relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
    }

    /**
     * Type of this virtual file entry
     *
     * Legal values 'dir', 'jar', 'tree', 'file' 'jarEntry'
     */
    private Integer fileType;

    /**
     * Absolute file path to this file entry
     */
    private String absoluteFilePath;

    /**
     * Relative file path inside a Jar file
     */
    private String relativeFilePath;


    
}
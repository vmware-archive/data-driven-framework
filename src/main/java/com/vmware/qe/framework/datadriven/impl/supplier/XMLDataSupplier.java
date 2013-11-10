/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vmware.qe.framework.datadriven.impl.supplier;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.DataSupplier;
import com.vmware.qe.framework.datadriven.utils.DDUtils;

/**
 * Represents XML file based data supplier.<br>
 */
public class XMLDataSupplier implements DataSupplier {
    private static final Logger log = LoggerFactory.getLogger(XMLDataSupplier.class);
    /**
     * Cache containing the test case name and corresponding data.<br>
     * Ex: {test.Pos001=datas, test.Pos002=datas}
     */
    private static final Map<String, HierarchicalConfiguration> cache = new HashMap<String, HierarchicalConfiguration>();
    /** loaded files list, {test.Pos001.xml,..} */
    private static final Set<String> loadedFiles = new HashSet<String>();

    /**
     * Get's data from XML file in classpath in specified format.<br>
     * First search the
     * 
     * @param className ClassName
     * @param context The context.
     */
    public HierarchicalConfiguration getData(final String className,
            HierarchicalConfiguration context) {
        HierarchicalConfiguration dataForTestCase = null;
        try {
            Class<?> clazz = Class.forName(className);
            String dataFilePath = null;
            URL dataFileURL = null;
            boolean packageFile = false;
            Map<String, HierarchicalConfiguration> dataMap = null;
            String dataFileName = context.getString("supplier.dataFile", null);
            log.debug("Checking the data file in argument...");
            if (dataFileName == null || dataFileName.equals("")) {
                log.debug("Data file not given in argument..Using DataFileFinder..");
                dataFilePath = DDUtils.findDataFile(className, ".xml", context);
            } else {
                log.debug("Got data file in argument");
                dataFilePath = dataFileName;
            }
            log.debug("Data file path: " + dataFilePath);
            if (dataFilePath == null) {
                return null;// No data found, hence it's a normal test case.
            }
            dataFileURL = clazz.getResource(dataFilePath);
            if (packageFile) {
                // The data file is from package file name so check the cache.
                log.debug("Cache: " + cache.size());
                synchronized (XMLDataSupplier.class) {
                    if (loadedFiles.contains(dataFilePath)) { // get it from cache.
                        log.info("File was loaded before !!!");
                        dataForTestCase = cache.get(clazz.getName());
                    } else {// not in cache, so load and put it to cache.
                        log.info("File was not loaded before, loading now...");
                        if (dataFileURL != null) {
                            cache.putAll(XMLDataParser.load(dataFileURL, clazz));
                        } else {
                            cache.putAll(XMLDataParser.load(dataFilePath, clazz));
                        }
                        dataForTestCase = cache.get(clazz.getName());
                        loadedFiles.add(dataFilePath);
                    }
                }
                if ((dataForTestCase == null) || dataForTestCase.isEmpty()) {
                    log.info("Data for '{}' is not available!", className);
                    return null;
                }
            } else { // data file not from package file so go ahead and load.
                log.debug("Loading the xml file...");
                if (dataFileURL != null) {
                    dataMap = XMLDataParser.load(dataFileURL, clazz);
                } else {
                    dataMap = XMLDataParser.load(dataFilePath, clazz);
                }
                dataForTestCase = dataMap.get(clazz.getName());
            }
        } catch (Exception ex) {
            throw new DDException("Error in loading the data file", ex);
        }
        return dataForTestCase;
    }
}

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
package com.vmware.qe.framework.datadriven.config;

import static com.vmware.qe.framework.datadriven.core.DDConstants.DD_CONFIG_FILE_NAME;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.core.DDException;

/**
 * Holds configuration info of DD framework.<br>
 */
public class DDConfig {
    private static final Logger log = LoggerFactory.getLogger(DDConfig.class);
    private static final DDConfig ddConfig = new DDConfig();
    public static final String TESTINPUT_DATAFILE = "dataFile";
    public static final String TESTINPUT_CONFIG_PATH = "configPath";
    private Configuration data;

    private DDConfig() {
    }

    public static DDConfig getSingleton() {
        return ddConfig;
    }

    /**
     * Returns the configuration data. If configuration data is null, method first attempts to
     * create a configuration data from JVM's system properties.<br>
     * 
     * @return test configuration data
     */
    public synchronized Configuration getData() {
        if (data != null) {
            return data;
        }
        final HierarchicalConfiguration cfg = new HierarchicalConfiguration();
        cfg.copy(new EnvironmentConfiguration());// copy the environment variables.
        cfg.copy(new SystemConfiguration());// JVM args
        log.debug("Configuration data from Env:\n" + ConfigurationUtils.toString(cfg));
        return data = prepareData(cfg);
    }

    /**
     * Uses the current configuration data passed in as argument and does the following: <br>
     * 1. Look for config.properties file on class path and load it if present.<br>
     * 2. Look for config.path in CLI params. If present, load it and overwrite any existing
     * properties.<br>
     * 3. Overwrite existing data with whatever was specified via cli. <br>
     * 
     * @param testData test configuration data
     * @return processed test configuration data
     */
    private synchronized Configuration prepareData(Configuration testData) {
        Configuration resultData = null;
        // step 1. config.properties on classpath
        URL cfgFile = this.getClass().getResource(DD_CONFIG_FILE_NAME);
        if (cfgFile != null) {
            log.info("Loading Configuration File: {}", cfgFile);
            resultData = getConfigFileData(cfgFile.getFile());
        } else {
            log.warn("Config file not found! " + DD_CONFIG_FILE_NAME);
        }
        if (resultData != null) {
            log.debug("Loaded data from " + DD_CONFIG_FILE_NAME + " on classpath");
        }
        // step 2. config file specified on cli
        if (testData.containsKey(TESTINPUT_CONFIG_PATH)) {
            String filePath = testData.getString(TESTINPUT_CONFIG_PATH);
            if (checkFilePath(filePath)) {
                Configuration tmpData = getConfigFileData(filePath);
                resultData = overrideConfigProperties(resultData, tmpData);
                log.debug("Loaded data from config file '{}'", filePath);
            }
        }
        log.debug("Overriding using properties specified via commandline arguments");
        resultData = overrideConfigProperties(resultData, testData);
        if (resultData == null) {
            String error = "Configuration data can not be null. Please specify test "
                    + "configuration information via config file on classpath or filesystem or via cli";
            log.error(error);
            throw new DDException(error);
        }
        log.debug("DDConfig: {}", ConfigurationUtils.toString(resultData));
        return resultData;
    }

    /**
     * Overrides the properties that exist in original configuration with the properties specified
     * in new configuration, if they already exist. Otherwise, they are added.
     * 
     * @param orgConfig original configuration
     * @param newConfig new configuration
     */
    private static Configuration overrideConfigProperties(Configuration orgConfig,
            Configuration newConfig) {
        if (newConfig == null) {
            return orgConfig;
        }
        if (orgConfig == null) {
            return newConfig;
        }
        Iterator<String> itr = newConfig.getKeys();
        while (itr.hasNext()) {
            String key = itr.next();
            orgConfig.setProperty(key, newConfig.getProperty(key));
        }
        return orgConfig;
    }

    /**
     * Gets the configuration based on the specified property file.<br>
     * 
     * @param filePath file containing configuration information.
     * @return configuration Configuration if found else returns null.
     */
    private static Configuration getConfigFileData(String filePath) {
        PropertiesConfiguration flatConfig = new PropertiesConfiguration();
        try {
            flatConfig.load(filePath);
            return ConfigurationUtils.convertToHierarchical(flatConfig);
        } catch (ConfigurationException e) {
            log.warn("Failed to load configuration from File {}", filePath, e);
            return null;
        }
    }

    /**
     * Checks to see if the file path points to an existing file and tries to locate it
     * 
     * @param filePath file path
     * @return true if file path is valid, false otherwise
     */
    private synchronized boolean checkFilePath(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            return true;
        } else {
            if (f.isAbsolute()) {
                log.error("Config file doesn't exist. Check value for '" + filePath + "'");
            } else {
                log.error("Can not locate config file. Check value for '" + filePath
                        + "' (Please use absolute file path)");
            }
        }
        return false;
    }
}

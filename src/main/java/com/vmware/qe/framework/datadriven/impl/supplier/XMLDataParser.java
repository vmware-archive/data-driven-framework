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
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.utils.XMLUtil;

/**
 * Helper class which parses the XML in required format.<br>
 */
public class XMLDataParser {
    private static final Logger log = LoggerFactory.getLogger(XMLDataParser.class);
    public static final String XSD_FILE_NAME = "/data-driven.xsd";
    /** Name of the argument to specify the data file. */
    public static final String TAG_NAMESPACE = "namespace";
    public static final String TAG_DATA = "data";
    public static final String TAG_COMMON_DATA = "common-data";
    public static final String TAG_CLASS_NAME = "class-name";

    /**
     * Load the data file and convert them in to a list of data's.
     * 
     * @throws Exception
     */
    public static Map<String, HierarchicalConfiguration> load(URL dataFileURL, Class<?> clazz)
            throws Exception {
        log.debug("Validating against schema file.");
        validateAgainstSchema(dataFileURL);
        log.debug("Reading the data file: " + dataFileURL);
        HierarchicalConfiguration dataFromFile = new XMLConfiguration(dataFileURL);
        return segregate(dataFromFile, clazz);
    }

    /**
     * Load the data file and convert them in to a list of data's.
     * 
     * @throws Exception
     */
    public static Map<String, HierarchicalConfiguration> load(String dataFilePath, Class<?> clazz)
            throws Exception {
        HierarchicalConfiguration dataFromFile = null;
        URL dataFileURL = clazz.getResource(dataFilePath);
        log.info("Validating against schema file:");
        validateAgainstSchema(dataFileURL);
        log.info("Reading the data file: " + dataFileURL);
        dataFromFile = new XMLConfiguration(dataFileURL);
        return segregate(dataFromFile, clazz);
    }

    /**
     * Segregate the data based on the namespace.<br>
     * 
     * @param dataFromFile HierarchicalConfiguration data loaded from file.
     * @param clazz the test class.
     * @return Map of namespace/test vs the testData.
     * @throws Exception
     */
    private static Map<String, HierarchicalConfiguration> segregate(
            HierarchicalConfiguration dataFromFile, Class<?> clazz) throws Exception {
        Map<String, HierarchicalConfiguration> dataMap = new HashMap<String, HierarchicalConfiguration>();
        log.debug("Data : " + ConfigurationUtils.toString(dataFromFile));
        List<HierarchicalConfiguration> allNameSpaces = null;
        String[] classNames = null;
        allNameSpaces = dataFromFile.configurationsAt(TAG_NAMESPACE);
        log.debug(allNameSpaces.size() + " namespaces given.");
        for (int i = 0; i < allNameSpaces.size(); i++) {
            HierarchicalConfiguration aNameSpaceData = allNameSpaces.get(i);
            classNames = aNameSpaceData.getStringArray(TAG_CLASS_NAME);
            // data is added to individual testData in the same namespace is added
            // to the dataMap using testClassName as key
            for (String testClassName : classNames) {
                log.debug(" ** Namespace: " + i + " > " + testClassName);
                if (!dataMap.containsKey(testClassName)) {
                    dataMap.put(testClassName, aNameSpaceData);
                } else {
                    append(dataMap.get(testClassName), aNameSpaceData);
                }
            }
        }
        log.debug("Data keys: " + dataMap.keySet());
        log.debug("Load size: " + dataMap.size());
        return dataMap;
    }

    /**
     * This method appends config2 with config1 This method is required since Configuration.append()
     * will not maintain hierarchical structure
     * 
     * @param config1
     * @param config2
     */
    public static void append(HierarchicalConfiguration config1, HierarchicalConfiguration config2)
            throws Exception {
        HierarchicalConfiguration clonedConfig2 = (HierarchicalConfiguration) config2.clone();
        List<ConfigurationNode> nodes = clonedConfig2.getRootNode().getChildren();
        for (ConfigurationNode configurationNode : nodes) {
            config1.getRoot().addChild(configurationNode);
        }
    }

    private static void validateAgainstSchema(URL dataFileURL) throws Exception {
        URL xsdURL = XMLDataParser.class.getResource(XSD_FILE_NAME);
        if (xsdURL == null) {
            log.error("Schema file not found!");
        } else {
            XMLUtil.validateXML(dataFileURL.openStream(), xsdURL.openStream());
        }
    }
}

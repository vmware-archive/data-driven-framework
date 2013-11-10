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

import static com.vmware.qe.framework.datadriven.core.DDConstants.DD_COMPONENT_CONFIG_FILE_NAME;
import static com.vmware.qe.framework.datadriven.core.DDConstants.KEY_DEFAULT;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_CLASS;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_FILTER;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_GENERATOR;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_INJECTOR;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_SUPPLIER;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DEFAULT_ATTR;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_INSTANCE_CREATOR;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_NAME;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.core.DDConstants;
import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.DataFilter;
import com.vmware.qe.framework.datadriven.core.DataGenerator;
import com.vmware.qe.framework.datadriven.core.DataInjector;
import com.vmware.qe.framework.datadriven.core.DataSupplier;
import com.vmware.qe.framework.datadriven.core.TestInstanceCreator;

/**
 * Represents the configuration of various components in DD framework.<br>
 * This looks for file with name {@link DDConstants#CONFIG_FILE_NAME} and loads it from class path.<br>
 */
public class DDComponentsConfig {
    private static final Logger log = LoggerFactory.getLogger(DDComponentsConfig.class);
    private final Map<String, DataSupplier> dataSupplierMap = new HashMap<>();
    private final Map<String, DataGenerator> dataGeneratorMap = new HashMap<>();
    private final Map<String, DataFilter> dataFilterMap = new HashMap<>();
    private final Map<String, TestInstanceCreator> instanceCreatorMap = new HashMap<>();
    private final Map<String, DataInjector> dataInjectorMap = new HashMap<>();
    private static final DDComponentsConfig instance = new DDComponentsConfig();

    public static DDComponentsConfig getInstance() {
        return instance;
    }

    private DDComponentsConfig() throws DDException {
        log.info("Initializing Components...");
        final XMLConfiguration ddconfig;
        try {
            log.info("Config file '{}'", DD_COMPONENT_CONFIG_FILE_NAME);
            URL cfgFile = this.getClass().getResource(DD_COMPONENT_CONFIG_FILE_NAME);
            log.info("Loading Components from: {}", cfgFile);
            ddconfig = new XMLConfiguration(cfgFile.getFile());
        } catch (Exception e) {
            throw new DDException("Error loading File: " + DD_COMPONENT_CONFIG_FILE_NAME, e);
        }
        List<HierarchicalConfiguration> suppliers = ddconfig.configurationsAt(TAG_DATA_SUPPLIER);
        for (HierarchicalConfiguration supplier : suppliers) {
            String name = supplier.getString(TAG_NAME);
            String className = supplier.getString(TAG_CLASS);
            Class<?> clazz = registerClass(className);
            DataSupplier dataSupplier;
            try {
                dataSupplier = (DataSupplier) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DDException("Error in creating supplier instance", e);
            }
            dataSupplierMap.put(name, dataSupplier);
            if (supplier.getBoolean(TAG_DEFAULT_ATTR, false)) {
                if (dataSupplierMap.containsKey(KEY_DEFAULT)) {
                    throw new DDException("multiple default supplier configuration found!!!");
                } else {
                    dataSupplierMap.put(KEY_DEFAULT, dataSupplier);
                }
            }
        }
        log.info("Data Suppliers: {}", dataSupplierMap.keySet());
        List<HierarchicalConfiguration> generators = ddconfig.configurationsAt(TAG_DATA_GENERATOR);
        for (HierarchicalConfiguration generator : generators) {
            String name = generator.getString(TAG_NAME);
            String className = generator.getString(TAG_CLASS);
            Class<?> clazz = registerClass(className);
            DataGenerator dataGenerator;
            try {
                dataGenerator = (DataGenerator) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DDException("Error in creating generator instance", e);
            }
            dataGeneratorMap.put(name, dataGenerator);
            if (generator.getBoolean(TAG_DEFAULT_ATTR, false)) {
                if (dataGeneratorMap.containsKey(KEY_DEFAULT)) {
                    throw new DDException("multiple default supplier configuration found!!!");
                } else {
                    dataGeneratorMap.put(KEY_DEFAULT, dataGenerator);
                }
            }
        }
        log.info("Data Generators: {}", dataGeneratorMap.keySet());
        List<HierarchicalConfiguration> filters = ddconfig.configurationsAt(TAG_DATA_FILTER);
        for (HierarchicalConfiguration filter : filters) {
            String name = filter.getString(TAG_NAME);
            String className = filter.getString(TAG_CLASS);
            Class<?> clazz = registerClass(className);
            DataFilter dataFilter;
            try {
                dataFilter = (DataFilter) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DDException("Error in creating filter instance", e);
            }
            dataFilterMap.put(name, dataFilter);
        }
        log.info("Data Filters {}", dataFilterMap.keySet());
        // No default for filters, all will be applied...
        List<HierarchicalConfiguration> instanceCreators = ddconfig
                .configurationsAt(TAG_INSTANCE_CREATOR);
        for (HierarchicalConfiguration instanceCreator : instanceCreators) {
            String name = instanceCreator.getString(TAG_NAME);
            String className = instanceCreator.getString(TAG_CLASS);
            Class<?> clazz = registerClass(className);
            TestInstanceCreator testInstanceCreator;
            try {
                testInstanceCreator = (TestInstanceCreator) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DDException("Error in creating testInstanceCreator instance", e);
            }
            instanceCreatorMap.put(name, testInstanceCreator);
            if (instanceCreator.getBoolean(TAG_DEFAULT_ATTR, false)) {
                instanceCreatorMap.put(KEY_DEFAULT, testInstanceCreator);
            }
        }
        log.info("Instance Creators: {}", instanceCreatorMap.keySet());
        List<HierarchicalConfiguration> dataInjectors = ddconfig
                .configurationsAt(TAG_DATA_INJECTOR);
        for (HierarchicalConfiguration dataInjector : dataInjectors) {
            String name = dataInjector.getString(TAG_NAME);
            String className = dataInjector.getString(TAG_CLASS);
            Class<?> clazz = registerClass(className);
            DataInjector dataInjectorObj;
            try {
                dataInjectorObj = (DataInjector) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DDException("Error in creating DataInjector instance", e);
            }
            dataInjectorMap.put(name, dataInjectorObj);
            if (dataInjector.getBoolean(TAG_DEFAULT_ATTR, false)) {
                dataInjectorMap.put(KEY_DEFAULT, dataInjectorObj);
            }
        }
        log.info("Data Injectors  : {}", dataInjectorMap.keySet());
    }

    private Class<?> registerClass(String className) throws DDException {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new DDException("Class not found: " + className, e);
        }
        return clazz;
    }

    public Collection<DataSupplier> getDataSuppliers() {
        return dataSupplierMap.values();
    }

    public Collection<DataGenerator> getDataGenerators() {
        return dataGeneratorMap.values();
    }

    public Collection<DataFilter> getDataFilters() {
        return dataFilterMap.values();
    }

    public Collection<TestInstanceCreator> getTestInstanceCreators() {
        return instanceCreatorMap.values();
    }

    public Collection<DataInjector> getDataInjectors() {
        return dataInjectorMap.values();
    }

    public Map<String, DataSupplier> getDataSupplierMap() {
        return dataSupplierMap;
    }

    public Map<String, DataGenerator> getDataGeneratorMap() {
        return dataGeneratorMap;
    }

    public Map<String, DataFilter> getDataFilterMap() {
        return dataFilterMap;
    }

    public Map<String, TestInstanceCreator> getInstanceCreatorMap() {
        return instanceCreatorMap;
    }

    public Map<String, DataInjector> getDataInjectorMap() {
        return dataInjectorMap;
    }
}

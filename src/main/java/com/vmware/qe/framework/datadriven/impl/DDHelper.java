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
package com.vmware.qe.framework.datadriven.impl;

import static com.vmware.qe.framework.datadriven.core.DDConstants.KEY_DEFAULT;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_AUTOGEN_ATTR;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_INJECTOR_DEFAULT;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_INJECTOR_TYPE;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_GENERATOR_DEFAULT;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_GENERATOR_DYNAMIC;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_GENERATOR_TYPE;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_INSTANCE_CREATOR_DEFAULT;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_INSTANCE_CREATOR_TYPE;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_SUPPLIER_DEFAULT;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_SUPPLIER_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.config.DDComponentsConfig;
import com.vmware.qe.framework.datadriven.config.DDConfig;
import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.DataFilter;
import com.vmware.qe.framework.datadriven.core.DataGenerator;
import com.vmware.qe.framework.datadriven.core.DataInjector;
import com.vmware.qe.framework.datadriven.core.DataSupplier;
import com.vmware.qe.framework.datadriven.core.TestInstanceCreator;

/**
 * The Main Class which helps to get the relevant data based on given configuration and info.<br>
 */
public final class DDHelper {
    private static final Logger log = LoggerFactory.getLogger(DDHelper.class);
    private static final DDComponentsConfig ddCoreConfig = DDComponentsConfig.getInstance();

    private DDHelper() {
    }

    private static final List<HierarchicalConfiguration> getData(String className,
            HierarchicalConfiguration context) throws DDException {
        log.info("Getting data for '{}'", className);
        // selecting supplier and getting data
        String supplierType = context.getString(TAG_SUPPLIER_TYPE, null);
        supplierType = supplierType == null ? DDConfig.getSingleton().getData()
                .getString(TAG_SUPPLIER_DEFAULT, null) : supplierType;
        DataSupplier supplier = null;
        if (supplierType != null) {
            if (ddCoreConfig.getDataSupplierMap().containsKey(supplierType)) {
                supplier = ddCoreConfig.getDataSupplierMap().get(supplierType);
            } else {
                throw new IllegalArgumentException("Given supplier with name = " + supplierType
                        + " not found");
            }
        } else {
            if (ddCoreConfig.getDataSupplierMap().containsKey(KEY_DEFAULT)) {
                supplier = ddCoreConfig.getDataSupplierMap().get(KEY_DEFAULT);
            } else {
                log.warn("no supplier selected. could not find default supplier. Using the first avaliable supplier");
                supplier = ddCoreConfig.getDataSuppliers().iterator().next();
            }
        }
        log.info("Supplier used: {}", supplier.getClass().getName());
        final HierarchicalConfiguration testData;
        testData = supplier.getData(className, context);
        if (testData == null) {
            log.warn("no test data found for the given test class = " + className);
            return null;
        }
        // selecting generator and generating dynamic data along with static data.
        List<HierarchicalConfiguration> datas = null;
        boolean dynamicGeneration = context.getBoolean(TAG_GENERATOR_DYNAMIC, false);
        dynamicGeneration = dynamicGeneration ? dynamicGeneration : DDConfig.getSingleton()
                .getData().getBoolean(TAG_GENERATOR_DYNAMIC, false);
        if (dynamicGeneration) {
            log.info("Dynamic data generation selected!");
            String generatorType = context.getString(TAG_GENERATOR_TYPE, null);
            generatorType = generatorType == null ? DDConfig.getSingleton().getData()
                    .getString(TAG_GENERATOR_DEFAULT, null) : generatorType;
            DataGenerator dataGenerator = null;
            
            if (generatorType != null) {
                if (ddCoreConfig.getDataGeneratorMap().containsKey(generatorType)) {
                    dataGenerator = ddCoreConfig.getDataGeneratorMap().get(generatorType);
                } else {
                    throw new IllegalArgumentException("Given generator with name = "
                            + generatorType + " not found");
                }
            } else {
                if (ddCoreConfig.getDataGeneratorMap().containsKey(KEY_DEFAULT)) {
                    dataGenerator = ddCoreConfig.getDataGeneratorMap().get(KEY_DEFAULT);
                } else {
                    log.warn("Could not find default generator, using the first avaliable generator");
                    dataGenerator = ddCoreConfig.getDataGenerators().iterator().next();
                }
            }
            log.info("Generator Used: {}", dataGenerator.getClass().getName());
            datas = dataGenerator.generate(testData, context);
            List<HierarchicalConfiguration> staticData = testData.configurationsAt(TAG_DATA);
            for (HierarchicalConfiguration aStaticData : staticData) {
                if (!aStaticData.getBoolean(TAG_AUTOGEN_ATTR, false)) {
                    datas.add(aStaticData);
                }
            }
        } else {
            log.info("No Dynamic data generation.");
            datas = testData.configurationsAt(TAG_DATA);
        }
        log.info("Applying filters...");
        List<HierarchicalConfiguration> filteredData = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {// for each data we create
            // new test instance.
            HierarchicalConfiguration aTestData = datas.get(i);
            boolean canRun = true;
            for (DataFilter filter : ddCoreConfig.getDataFilters()) {
                if (!filter.canRun(className, aTestData, context)) {
                    canRun = false;
                    break;
                }
            }
            if (canRun) {
                filteredData.add(aTestData);
            }
        }
        return filteredData;
    }

    /**
     * Method to get the test instances of given test with data.<br>
     * 
     * @param className The test class name.
     * @param context context containing any relevant info.
     * @return Array of test instances.
     * @throws DDException on any issues when getting data.
     */
    public static Object[] getTests(String className, HierarchicalConfiguration context)
            throws DDException {
        List<HierarchicalConfiguration> datas = getData(className, context);
        if (datas == null || datas.isEmpty()) { // not a DD test
            throw new DDException("No test data found for class = " + className);
        }
        String instanceCreatorType = context.getString(TAG_INSTANCE_CREATOR_TYPE, null);
        instanceCreatorType = instanceCreatorType == null ? DDConfig.getSingleton().getData()
                .getString(TAG_INSTANCE_CREATOR_DEFAULT, null) : instanceCreatorType;
        TestInstanceCreator instanceCreator = null;
        if (instanceCreatorType != null) {
            if (ddCoreConfig.getInstanceCreatorMap().containsKey(instanceCreatorType)) {
                instanceCreator = ddCoreConfig.getInstanceCreatorMap().get(instanceCreatorType);
            } else {
                throw new IllegalArgumentException("Given instance creator with name = "
                        + instanceCreatorType + " not found");
            }
        } else {
            if (ddCoreConfig.getInstanceCreatorMap().containsKey(KEY_DEFAULT)) {
                instanceCreator = ddCoreConfig.getInstanceCreatorMap().get(KEY_DEFAULT);
            } else {
                log.warn("no instance creator selected. could not find default instance creator. Using the first avaliable instance creator");
                instanceCreator = ddCoreConfig.getTestInstanceCreators().iterator().next();
            }
        }
        log.info("InstanceCreator used: {}", instanceCreator.getClass().getName());
        String dataInjectorType = context.getString(TAG_DATA_INJECTOR_TYPE, null);
        dataInjectorType = dataInjectorType == null ? DDConfig.getSingleton().getData()
                .getString(TAG_DATA_INJECTOR_DEFAULT, null) : dataInjectorType;
        DataInjector dataInjector;
        if (dataInjectorType != null) {
            log.info("Injector: {} ", dataInjectorType);
            if (ddCoreConfig.getDataInjectorMap().containsKey(dataInjectorType)) {
                dataInjector = ddCoreConfig.getDataInjectorMap().get(dataInjectorType);
            } else {
                throw new IllegalArgumentException("Given data injector with name = "
                        + dataInjectorType + " not found");
            }
        } else {
            if (ddCoreConfig.getDataInjectorMap().containsKey(KEY_DEFAULT)) {
                dataInjector = ddCoreConfig.getDataInjectorMap().get(KEY_DEFAULT);
                log.info("Injector used: {}", dataInjector.getClass().getName());
            } else {
                log.warn("no data injector selected. could not find default data injector. Using the first avaliable data injector");
                dataInjector = ddCoreConfig.getDataInjectors().iterator().next();
            }
        }
        final List<Object> tests = new ArrayList<Object>();
        for (HierarchicalConfiguration aTestData : datas) {
            Object testObj = instanceCreator.newInstance(className, aTestData, context);
            dataInjector.inject(testObj, aTestData, context);
            tests.add(testObj);
        }
        log.info("Number of tests: {}", tests.size());
        return tests.toArray();
    }
}

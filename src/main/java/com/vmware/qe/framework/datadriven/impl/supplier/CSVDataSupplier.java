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

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.DataSupplier;
import com.vmware.qe.framework.datadriven.utils.DDUtils;

public class CSVDataSupplier implements DataSupplier {
    private static final Logger log = LoggerFactory.getLogger(CSVDataSupplier.class);

    public HierarchicalConfiguration getData(final String className,
            HierarchicalConfiguration context) {
        HierarchicalConfiguration testData = null;
        try {
            Class<?> clazz = Class.forName(className);
            String dataFilePath = null;
            URL dataFileURL = null;
            String dataFileName = context.getString("supplier.dataFile", null);
            log.debug("Checking the data file in argument...");
            if (dataFileName == null || dataFileName.equals("")) {
                log.debug("Data file not given in argument..Using DataFileFinder..");
                dataFilePath = DDUtils.findDataFile(className, ".csv", context);
            } else {
                log.debug("Got data file in argument");
                dataFilePath = dataFileName;
            }
            log.debug("Data file path: " + dataFilePath);
            if (dataFilePath == null) {
                return null;// No data found, hence it's a normal test case.
            }
            dataFileURL = clazz.getResource(dataFilePath);
            CsvMapReader reader = new CsvMapReader(new InputStreamReader(dataFileURL.openStream()),
                    CsvPreference.STANDARD_PREFERENCE);
            String list[] = reader.getHeader(true);
            Map<String, String> map = null;
            testData = new HierarchicalConfiguration();
            int i = 0;
            while ((map = reader.read(list)) != null) {
                String testId = null;
                HierarchicalConfiguration newData = new HierarchicalConfiguration();
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    if (entry.getKey().equals("test-id")) {
                        newData.addProperty("[@test-id]", entry.getValue());
                        testId = entry.getValue();
                        continue;
                    }
                    newData.addProperty(entry.getKey(), entry.getValue());
                }
                testData.addNodes("data(" + i + ")", newData.getRootNode().getChildren());
                if (testId != null) {
                    testData.addProperty("data(" + i + ")[@test-id]", testId);
                }
                i++;
            }
            reader.close();
        } catch (Exception ex) {
            throw new DDException("Error in loading data file", ex);
        }
        return testData;
    }
}

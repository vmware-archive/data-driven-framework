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
package com.vmware.qe.framework.datadriven;

import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_TESTID_ATTR;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vmware.qe.framework.datadriven.core.DDConstants;
import com.vmware.qe.framework.datadriven.impl.DDHelper;

public class DynamicUsage implements ITest {
    private static int testId = 1;
    private final Logger log = LoggerFactory.getLogger(DynamicUsage.class);
    private HierarchicalConfiguration data; // Will be injected.
    private List<String> testList;
    private int capacity;
    private int itemsToAdd;
    private int itemsToRemove;

    // private Random random = new Random();
    @BeforeClass(alwaysRun = true)
    public void setup() {
        capacity = data.getInt("capacity");
        itemsToAdd = data.getInt("items-to-add");
        itemsToRemove = data.getInt("items-to-remove");
        log.info("Capacity : {} ToAdd: {}  ToRemove: {}", capacity, itemsToAdd, itemsToRemove);
        testList = new ArrayList<>(capacity);
    }

    @Test
    public void test() {
        for (int i = 0; i < itemsToAdd; i++) {
            testList.add("String-" + i);
        }
        Assert.assertEquals(testList.size(), itemsToAdd);
        log.info("Done adding  :{}", itemsToAdd);
        for (int i = 0; i < itemsToRemove; i++) {
            testList.remove("String-" + i);
        }
        Assert.assertEquals(testList.size(), itemsToAdd - itemsToRemove);
        log.info("Done removing: {}", itemsToRemove);
    }

    @Factory
    @Parameters({ "dataFile" })
    public Object[] getTests(@Optional("") String dataFile) throws Exception {
        HierarchicalConfiguration context = new HierarchicalConfiguration();
//        context.addProperty(DDConstants.TAG_GENERATOR_TYPE, "Pairwise");
        return DDHelper.getTests(this.getClass().getName(), context);
    }

    /**
     * Overriding this methods helps to give meaningful names to each test instance.<br>
     */
    @Override
    public String getTestName() {
        String name = data.getString(TAG_TESTID_ATTR);
        name = name != null ? getClass().getName() + "-" + name
                : (getClass().getName() + "-" + testId++);
        return name;
    }
}

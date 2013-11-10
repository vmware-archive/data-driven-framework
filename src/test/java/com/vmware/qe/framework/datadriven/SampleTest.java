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

import java.util.Random;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.vmware.qe.framework.datadriven.impl.DDHelper;
import com.vmware.qe.framework.datadriven.impl.injector.Data;

public class SampleTest implements ITest {
    private final Logger log = LoggerFactory.getLogger(SampleTest.class);
    private Random random = new Random();
    @Data(name = "")
    HierarchicalConfiguration data; // data injection here.
    @Data(name = "pass")
    String str;

    @Test
    public void test() {
        log.info("Testing...");
        log.info(" service = " + data.getString("service"));
        log.info(" user    = " + data.getString("user"));
        log.info(" pass    = " + str);
    }

    @Factory
    @Parameters({ "dataFile" })
    public Object[] getTests(@Optional("") String dataFile) throws Exception {
        HierarchicalConfiguration context = new HierarchicalConfiguration();
        // context.addProperty(TAG_SUPPLIER_FILE_PATH, dataFile);
        return DDHelper.getTests(this.getClass().getName(), context);
    }

    @Override
    public String getTestName() {
        return data != null ? data.getString(TAG_TESTID_ATTR) : getClass().getName()
                + random.nextInt();
    }
}

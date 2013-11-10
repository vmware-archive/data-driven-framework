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
package com.vmware.qe.framework.datadriven.impl.filter;

import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_TESTID_ATTR;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.config.DDConfig;
import com.vmware.qe.framework.datadriven.core.DataFilter;

/**
 * Filters the data based on test-id.<br>
 */
public class DataFilterBasedOnTestId implements DataFilter {
    private static final Logger log = LoggerFactory.getLogger(DataFilterBasedOnTestId.class);

    @Override
    public boolean canRun(String className, HierarchicalConfiguration dataToFilter,
            HierarchicalConfiguration context) {
        String testIdKey = className + "-testids";
        String testIdArray[];
        testIdArray = context.getStringArray(testIdKey);
        testIdArray = testIdArray == null ? DDConfig.getSingleton().getData()
                .getStringArray(testIdKey) : testIdArray;
        List<String> testIds = Arrays.asList(testIdArray);
        if (testIds.isEmpty()) {
            return true;
        }
        String testId = dataToFilter.getString(TAG_TESTID_ATTR, "");
        boolean canRun = false;
        // Ignoring testIds if they are not in the selected test-ids to run
        if (testIds != null && !testIds.isEmpty()) {
            canRun = testIds.contains(testId);
        }
        log.debug("CanRun: {}", canRun);
        return canRun;
    }
}

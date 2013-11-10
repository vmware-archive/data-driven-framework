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

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.vmware.qe.framework.datadriven.config.DDConfig;
import com.vmware.qe.framework.datadriven.core.DataFilter;

public class DataFilterBasedOnProperty implements DataFilter {
    public static final String ARG_FILTER_KEY = "filter.filterkey";
    public static final String ARG_FILTER_VALUE = "filter.filtervalue";

    @Override
    public boolean canRun(String className, HierarchicalConfiguration dataToFilter,
            HierarchicalConfiguration context) {
        return checkAgainstFilterProperties(dataToFilter, context);
    }

    /**
     * filters test based on given filterKey/filterValue. If the any of the filter key/value pairs
     * found from data object, then returns true.
     * 
     * @param data test data
     * @return true if test is selected after applying filter, false otherwise
     */
    private boolean checkAgainstFilterProperties(HierarchicalConfiguration data,
            HierarchicalConfiguration context) {
        boolean included = true;
        String filterKeys[] = null;
        String filterValues[] = null;
        if (context.containsKey(ARG_FILTER_KEY)
                && !context.getString(ARG_FILTER_KEY).trim().equals("")) {
            filterKeys = context.getStringArray(ARG_FILTER_KEY);
            filterValues = context.getStringArray(ARG_FILTER_VALUE);
        } else if (DDConfig.getSingleton().getData().containsKey(ARG_FILTER_KEY)
                && !DDConfig.getSingleton().getData().getString(ARG_FILTER_KEY).trim().equals("")) {
            filterKeys = DDConfig.getSingleton().getData().getStringArray(ARG_FILTER_KEY);
            filterValues = DDConfig.getSingleton().getData().getStringArray(ARG_FILTER_VALUE);
        }
        if (filterKeys != null && filterValues != null) {
            included = false;
            for (int index = 0; index < filterKeys.length; index++) {
                String filterKey = filterKeys[index];
                String filterValue = null;
                if (index >= filterValues.length) {
                    filterValue = "";
                } else {
                    filterValue = filterValues[index];
                }
                if (data.containsKey(filterKey)
                        && data.getString(filterKey, "").equals(filterValue)) {
                    included = true;
                    break;
                }
            }
        }
        return included;
    }
}

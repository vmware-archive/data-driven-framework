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
package com.vmware.qe.framework.datadriven.core;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * DataFilter enables filtering of data based on some criteria.<br>
 * Users can implement their own filters by implementing the
 * {@link #canRun(String, HierarchicalConfiguration, HierarchicalConfiguration)} method.<br>
 * After getting the data from DataSupplier data is passed through all registered filters for their
 * usability.<br>
 * 
 * @see DataSupplier
 */
public interface DataFilter {
    /**
     * Filters the given data and returns true/false based on whether the data can be used or not.<br>
     * 
     * @param className name of the class to which the data is associated with.
     * @param dataToFilter the data to be filtered.
     * @param context Context information.
     * @return 'true' if the data can be used else 'false'.
     */
    boolean canRun(String className, HierarchicalConfiguration dataToFilter,
            HierarchicalConfiguration context);
}

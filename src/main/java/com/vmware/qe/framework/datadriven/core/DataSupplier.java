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
 * Represents a Data supplier of any form.<br>
 * Responsibility being supplying the data requested for given namespace by using context
 * information if required.<br>
 */
public interface DataSupplier {
    /**
     * Gets the data from the supplier based on the namespace and given file name.<br>
     * Typically the namespace will be the FQCN of the test for which data needs to be fetched.<br>
     * One can provide extra info required for fetching the data using second argument.
     * 
     * @param nameSpace namespace for which data needs to be supplied. Generally it's FQCN of test.
     * @param context The context of the application.
     * @return List containing the data sets in the form of HierarchicalConfiguration
     * @throws DDException If data can't be fetched for any reason.
     * @see {@link HierarchicalConfiguration}
     */
    HierarchicalConfiguration getData(String nameSpace, HierarchicalConfiguration context);
}

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
 * Instance creator allows creation of test instances based on given data and other info in context.<br>
 */
public interface TestInstanceCreator {
    /**
     * Creates instance of given class and applies given data to it.<br>
     * 
     * @param className Name of the class for which instance needs to be created.
     * @param testData the data to be applied on the created instance.
     * @param context Context information.
     * @return Instance of given class.
     * @throws DDException If instance could not be created.
     */
    Object newInstance(String className, HierarchicalConfiguration testData,
            HierarchicalConfiguration context) throws DDException;
}

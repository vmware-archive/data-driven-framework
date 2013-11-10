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
package com.vmware.qe.framework.datadriven.impl.creator;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.TestInstanceCreator;

/**
 * Creates instance of given class by calling default constructor.<br>
 */
public class SimpleTestInstanceCreator implements TestInstanceCreator {
    @Override
    public Object newInstance(String className, HierarchicalConfiguration testData,
            HierarchicalConfiguration context) {
        Class<?> testClass;
        Object testInstance = null;
        try {
            testClass = Class.forName(className);
            testInstance = testClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new DDException(
                    "Error in creating test instance. Test class name = " + className, e);
        }
        return testInstance;
    }
}

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
 * DataInjector is used to Inject applicable data acquired in to the test object which is created
 * using {@link TestInstanceCreator} <br>
 * Ideally the test object will adhares to convention which a Injector imposes.<br>
 */
public interface DataInjector {
    /**
     * Inject given data in to given test object.<br>
     * 
     * @param test the object of test to which data is injected.
     * @param data data to inject.
     * @param context context containing configuration info.
     */
    void inject(Object test, HierarchicalConfiguration data, HierarchicalConfiguration context);
}

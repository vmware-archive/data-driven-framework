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

/**
 * Constants used across DD framework.<br>
 */
public class DDConstants {
    public final static String DD_COMPONENT_CONFIG_FILE_NAME = "/dd-components-config.xml";
    public final static String DD_CONFIG_FILE_NAME = "/dd-config.properties";
    public final static String TAG_DATA_SUPPLIER = "data-supplier";
    public final static String TAG_DATA_GENERATOR = "data-generator";
    public final static String TAG_DATA_FILTER = "data-filter";
    public final static String TAG_INSTANCE_CREATOR = "instance-creator";
    public final static String TAG_DATA_INJECTOR = "data-injector";
    public final static String TAG_NAME = "name";
    public final static String TAG_CLASS = "class";
    public final static String TAG_DEFAULT_ATTR = "[@default]";
    public final static String TAG_TESTID_ATTR = "[@test-id]";
    public final static String TAG_AUTOGEN_ATTR = "[@auto-gen]";
    public final static String KEY_DEFAULT = "default";
    public final static String TAG_SUPPLIER_TYPE = "supplier.type";
    public final static String TAG_SUPPLIER_DEFAULT = "supplier.default";
    public final static String TAG_SUPPLIER_FILE_PATH = "supplier.file.path";
    public final static String TAG_GENERATOR_DYNAMIC = "generator.dynamic.enabled";
    public final static String TAG_GENERATOR_TYPE = "generator.type";
    public final static String TAG_GENERATOR_DEFAULT = "generator.default";
    public static final String TAG_DATA = "data";
    public final static String TAG_INSTANCE_CREATOR_TYPE = "instancecreator.type";
    public final static String TAG_INSTANCE_CREATOR_DEFAULT = "instancecreator.default";
    public final static String TAG_DATA_INJECTOR_TYPE = "datainjector.type";
    public final static String TAG_DATA_INJECTOR_DEFAULT = "datainjector.default";
    public final static String DATA_INJECTOR_DEFAULT_PROPERTY_NAME = "data";
    public final static String TAG_DATA_INJECTOR_PROPERTY = "datainjector.property";
}

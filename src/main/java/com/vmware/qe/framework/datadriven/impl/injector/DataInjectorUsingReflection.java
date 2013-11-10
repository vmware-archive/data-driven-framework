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
package com.vmware.qe.framework.datadriven.impl.injector;

import static com.vmware.qe.framework.datadriven.core.DDConstants.DATA_INJECTOR_DEFAULT_PROPERTY_NAME;
import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_DATA_INJECTOR_PROPERTY;

import java.lang.reflect.Field;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.vmware.qe.framework.datadriven.config.DDConfig;
import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.DataInjector;

public class DataInjectorUsingReflection implements DataInjector {
    @Override
    public void inject(Object test, HierarchicalConfiguration data,
            HierarchicalConfiguration context) {
        String propertyName = context.getString(TAG_DATA_INJECTOR_PROPERTY, null);
        propertyName = propertyName == null ? DDConfig.getSingleton().getData()
                .getString(TAG_DATA_INJECTOR_PROPERTY, DATA_INJECTOR_DEFAULT_PROPERTY_NAME)
                : propertyName;
        Class<? extends Object> testClass = test.getClass();
        Field field;
        try {
            field = getField(testClass, propertyName);
            field.setAccessible(true);
            field.set(test, data);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new DDException("Failed to inject data to property with name =" + propertyName, e);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        Class<?> tmpClass = clazz;
        do {
            for (Field field : tmpClass.getDeclaredFields()) {
                String candidateName = field.getName();
                if (!candidateName.equals(fieldName)) {
                    continue;
                }
                field.setAccessible(true);
                return field;
            }
            tmpClass = tmpClass.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Field '" + fieldName + "' not found on class " + clazz);
    }
}

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.vmware.qe.framework.datadriven.core.DDException;
import com.vmware.qe.framework.datadriven.core.DataInjector;

public class DataInjectorUsingAnnotations implements DataInjector {
    @Override
    public void inject(Object test, HierarchicalConfiguration data,
            HierarchicalConfiguration context) {
        Class<? extends Object> testClass = test.getClass();
        for (Field field : testClass.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().isAssignableFrom(Data.class)) {
                }
                Data dataAnnotation = (Data) annotation;
                field.setAccessible(true);
                try {
                    if (dataAnnotation.name().equals("")) {
                        field.set(test, data);
                    } else {
                        String value = data.getString(dataAnnotation.name());
                        field.set(test, value);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new DDException("Error in injecting data to field with name = "
                            + field.getName(), e);
                }
            }
        }
    }
}

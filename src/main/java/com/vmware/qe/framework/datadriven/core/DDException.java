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
 * DDException is used to report any exceptions coming from DD Framework.<br>
 */
public class DDException extends RuntimeException {
    private static final long serialVersionUID = -7851147301277835548L;

    public DDException(String message, Throwable cause) {
        super(message, cause);
    }

    public DDException(String message) {
        super(message);
    }
}

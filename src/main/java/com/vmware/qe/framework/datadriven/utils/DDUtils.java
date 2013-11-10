package com.vmware.qe.framework.datadriven.utils;

import java.net.URL;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.core.DDException;

public class DDUtils {
    private static final Logger log = LoggerFactory.getLogger(DDUtils.class);
    
    public static String findDataFile(String className, String fileExtn,
            HierarchicalConfiguration context) {
        String dataFilePath = null;
        try {
            final Class<?> clazz = Class.forName(className);
            URL dataFileURL = null;
            log.debug("Checking for file in classpath of '{}'", className);
            String fileName = clazz.getName() + fileExtn;
            log.debug("Checking classpath for data file with name: " + fileName);
            dataFileURL = clazz.getResource(fileName);
            if (dataFileURL == null) {
                log.debug("Data file with class name not found.");
                fileName = clazz.getPackage().getName() + fileExtn;
                log.debug("Checking classpath for data file with name: " + fileName);
                dataFileURL = clazz.getResource(fileName);
                if (dataFileURL == null) {
                    log.debug("Data file with package name is not found, "
                            + "So it's not a data driven test case.");
                    dataFilePath = null;
                } else {
                    log.debug("Got data file with package name.");
                    dataFilePath = fileName;
                }
            } else {
                log.debug("Got data file with class name.");
                dataFilePath = fileName;
            }
            log.debug("Data file path: " + dataFilePath);
            if (dataFilePath == null) {
                return null;// No data found, hence it's a normal test case.
            }
            log.debug("Data file path from classpath: " + dataFilePath);
        } catch (Exception e) {
            throw new DDException("Could not find file in classpath!", e);
        }
        return dataFilePath;
    }
}

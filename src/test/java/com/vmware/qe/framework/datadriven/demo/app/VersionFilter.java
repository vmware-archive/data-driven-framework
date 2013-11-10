package com.vmware.qe.framework.datadriven.demo.app;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.config.DDConfig;
import com.vmware.qe.framework.datadriven.core.DataFilter;

/**
 * Filters the data based on product filter<br>
 */
public class VersionFilter implements DataFilter {
    private static final Logger log = LoggerFactory.getLogger(VersionFilter.class);

    /**
     * Filters the data based on product version<br>
     * 
     * @param className FQCN of the class.
     * @param dataToFilter data which needs to be filtered.
     * @param context The context containing more info.
     */
    public boolean canRun(String className, HierarchicalConfiguration dataToFilter,
            HierarchicalConfiguration context) {
        boolean canRun = false;
        int prodVersion = 0;
        String version = DDConfig.getSingleton().getData().getString("prod.version");
        if (version != null) {
            prodVersion = Integer.parseInt(version);
        } else {
            log.debug("No version found in product.");
        }
        int dataVersion = dataToFilter.getInt("[@above-version]", 0);
        canRun = prodVersion >= dataVersion;
        log.debug("Prod version: {}  Data version: {} Can run= {}", prodVersion, dataVersion,
                canRun);
        return canRun;
    }
}

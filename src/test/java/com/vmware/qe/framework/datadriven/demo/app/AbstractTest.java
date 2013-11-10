package com.vmware.qe.framework.datadriven.demo.app;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.testng.ITest;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.vmware.qe.framework.datadriven.core.DDConstants;
import com.vmware.qe.framework.datadriven.impl.DDHelper;

public abstract class AbstractTest implements ITest {
    protected HierarchicalConfiguration data; // Will be injected.
    
    @Factory
    @Parameters({ "dataFile" })
    public Object[] getTests(@Optional("") String dataFile) throws Exception {
        HierarchicalConfiguration context = new HierarchicalConfiguration();
        context.addProperty(DDConstants.TAG_GENERATOR_TYPE, "Pairwise");
        return DDHelper.getTests(this.getClass().getName(), context);
    }
}

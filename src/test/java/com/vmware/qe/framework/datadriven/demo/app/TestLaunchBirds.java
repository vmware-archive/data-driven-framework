package com.vmware.qe.framework.datadriven.demo.app;

import static com.vmware.qe.framework.datadriven.core.DDConstants.TAG_TESTID_ATTR;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.qe.framework.datadriven.config.DDConfig;

public class TestLaunchBirds extends AbstractTest {
    private final Logger log = LoggerFactory.getLogger(TestLaunchBirds.class);
    private AngryBirds ab;
    private List<?> birds;
    private List<?> hits;
    private List<?> expectedHits;
    private int version;
    private Random random = new Random();
    @BeforeClass(alwaysRun = true)
    public void setup() {
        version = DDConfig.getSingleton().getData().getInt("prod.version", 0);
        Assert.assertTrue(version > 0, "Version should be a greaterthan zero.");
        log.info("############################");
        birds = data.getList("birds");
        hits = data.getList("hit");
        expectedHits = data.getList("expectedHits");
        log.info("Given birds: {}", birds);
        log.info("Given hits: {}", hits);
        log.info("Given expectedHits: {}", expectedHits);
        Assert.assertEquals(birds.size(), hits.size());
        Assert.assertEquals(birds.size(), expectedHits.size());
        ab = new AngryBirds(version);
    }

    @Test
    public void test() {
        log.info(">>>>>>>>>>>>");
        for (int i = 0; i < birds.size(); i++) {
            String bird = (String) birds.get(i);
            final boolean hit = Boolean.parseBoolean((String) hits.get(i));
            final int expHits = Integer.parseInt((String) expectedHits.get(i));
            log.info("Launching {} {} Exp hits= {}", bird, hit, expHits);
            ab.launch(bird, hit);
            int pigHits = ab.getPigsHit();
            log.info("Actual hits: {}", pigHits);
            Assert.assertEquals(pigHits, expHits);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        log.info("Cleanup");
    }

    @Override
    public String getTestName() {
        return data != null ? data.getString(TAG_TESTID_ATTR) : getClass().getName()
                + random.nextInt();
    }
}

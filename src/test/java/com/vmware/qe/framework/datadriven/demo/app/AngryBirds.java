package com.vmware.qe.framework.datadriven.demo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngryBirds {
    private static final Logger log = LoggerFactory.getLogger(AngryBirds.class);
    private final int version;
    private final int levels;
    public static final String BIRD_RED = "RED";
    public static final String BIRD_BLACK = "BLACK";
    public static final String BIRD_YELLOW = "YELLOW";
    public static final String BIRD_WHITE = "WHITE";
    private final List<String> birds;
    private int currentLevel = 1;
    private int pigsHit = 0;
    private static final Map<Integer, Integer> map = new HashMap<>();
    static {
        map.put(1, 3);
        map.put(2, 4);
        map.put(3, 5);
    }

    public AngryBirds(int version) {
        this.version = version;
        birds = new ArrayList<>();
        addBirds();
        levels = map.get(version);
        log.info("Creating with version= {} levels= {}", version, levels);
    }

    public void launch(String bird, boolean hit) {
        log.info("In air  : " + bird);
        if (!birds.remove(bird)) { // remove the launched bird.
            throw new RuntimeException("Level '" + currentLevel + "' Dont have " + bird + " Now");
        }
        if (hit) {
            if (bird.equals(BIRD_BLACK)) {
                pigsHit = (pigsHit + 2);
            } else if (bird.equals(BIRD_WHITE)) {
                pigsHit = (pigsHit + 3);
            } else {
                pigsHit++;
            }
        } else {
            log.info("I missed: " + bird);
        }
        if (pigsHit > 2 && currentLevel <= 3) {
            levelAdvance();
        } else if (pigsHit > 3 && currentLevel >= 4) {
            levelAdvance();
        } else if (birds.isEmpty()) {
            throw new RuntimeException("Level '" + currentLevel + "' Failed!!!  No birds");
        }
        log.info("Hits: {}", pigsHit);
    }

    private void levelAdvance() {
        if (currentLevel == levels) {
            log.info("!! GAME COMPLETE !! Score:" + birds.size());
            System.exit(0);
        }
        currentLevel++;
        pigsHit = 0;
        addBirds();
        log.info("Promoted to next level: " + currentLevel);
        log.info("Remaining Birds: " + birds);
    }

    private void addBirds() {
        birds.add(BIRD_RED);
        birds.add(BIRD_BLACK);
        birds.add(BIRD_YELLOW);
        if (version >= 2) {
            birds.add(BIRD_WHITE);
        }
    }

    public int version() {
        return version;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getPigsHit() {
        return pigsHit;
    }

    public static void main(String[] args) {
        AngryBirds ab = new AngryBirds(2);
        for (int i = 0; i < 10; i++) {
            ab.launch(BIRD_RED, true);
            ab.launch(BIRD_BLACK, true);
            ab.launch(BIRD_YELLOW, false);
            ab.launch(BIRD_WHITE, true);
        }
    }
}

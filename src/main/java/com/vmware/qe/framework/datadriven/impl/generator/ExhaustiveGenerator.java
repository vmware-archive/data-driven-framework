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
package com.vmware.qe.framework.datadriven.impl.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates all possible data combinations for given data sets.<br>
 */
public class ExhaustiveGenerator extends AbstractDataGenerator {
    private static final Logger log = LoggerFactory.getLogger(ExhaustiveGenerator.class);

    /**
     * Generate Exhaustively.<br>
     */
    public List<List<Integer>> generateCombinations(List<List<Integer>> props) {
        return generatePairs(props, 0);
    }

    private List<List<Integer>> generatePairs(List<List<Integer>> props, int index) {
        if (index == props.size() - 1) {
            List<Integer> prop = props.get(index);
            List<List<Integer>> combinations = new ArrayList<>();
            for (Integer value : prop) {
                List<Integer> comb = new ArrayList<>();
                comb.add(value);
                combinations.add(comb);
            }
            return combinations;
        }
        List<List<Integer>> tempCombinations = generatePairs(props, index + 1);
        List<List<Integer>> combinations = new ArrayList<>();
        List<Integer> prop = props.get(index);
        for (Integer value : prop) {
            for (List<Integer> comb : tempCombinations) {
                List<Integer> newComb = new ArrayList<>();
                newComb.add(value);
                newComb.addAll(comb);
                combinations.add(newComb);
            }
        }
        log.debug("Generated:" + combinations);
        return combinations;
    }
}

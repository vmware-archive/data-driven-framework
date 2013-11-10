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

public class PairwiseDataGenerator extends AbstractDataGenerator {
    final Logger log = LoggerFactory.getLogger(PairwiseDataGenerator.class);

    @Override
    public List<List<Integer>> generateCombinations(List<List<Integer>> prop) {
        List<List<Integer>> data = generateCombinations(null, prop.get(0), null);
        for (int i = 1; i < prop.size(); i++) {
            data = generateCombinations(data, prop.get(i), prop.get(i - 1));
        }
        log.info("Combinations generated: '{}'", data.size());
        log.info("Combinations: {}", data);
        return data;
    }

    public List<List<Integer>> generateCombinations(List<List<Integer>> data, List<Integer> prop,
            List<Integer> prevProp) {
        if (prop == null) {
            return data;
        }
        List<List<Integer>> combinations = new ArrayList<>();
        if (data == null) {
            for (Integer value : prop) {
                List<Integer> comb = new ArrayList<>();
                comb.add(value);
                combinations.add(comb);
            }
            return combinations;
        }
        if (data.get(0).size() == 1) {
            for (Integer prevValue : prevProp) {
                for (Integer value : prop) {
                    List<Integer> newComb = new ArrayList<>();
                    newComb.add(prevValue);
                    newComb.add(value);
                    combinations.add(newComb);
                }
            }
        } else {
            List<List<Integer>> tempCombinations = new ArrayList<>();
            for (Integer prevValue : prevProp) {
                for (Integer value : prop) {
                    List<Integer> newComb = new ArrayList<>();
                    newComb.add(prevValue);
                    newComb.add(value);
                    tempCombinations.add(newComb);
                }
            }
            int index = -1;
            boolean fillup = false;
            for (int i = 0; i < data.size(); i++) {
                List<Integer> comb = data.get(i);
                index++;
                if (index == tempCombinations.size()) {
                    if (fillup) {
                        break;
                    }
                    index = 0;
                }
                List<Integer> tempComb = tempCombinations.get(index);
                List<Integer> newComb = new ArrayList<>();
                newComb.addAll(comb);
                newComb.add(tempComb.get(1));
                combinations.add(newComb);
                if (tempComb.get(0) != comb.get(comb.size() - 1)) {
                    newComb = new ArrayList<>();
                    newComb.addAll(comb);
                    newComb.remove(newComb.size() - 1);
                    newComb.add(tempComb.get(0));
                    newComb.add(tempComb.get(1));
                    combinations.add(newComb);
                }
                if (i == data.size() - 1 && data.size() < tempCombinations.size()) {
                    i--;
                    fillup = true;
                }
            }
            log.debug("temp= {}", tempCombinations);
        }
        return combinations;
    }
}

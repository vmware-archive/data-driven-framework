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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.qe.framework.datadriven.core.DataGenerator;

/**
 * This class helps to convert the standard HC format data in to index based data sets so that one
 * can apply any data generation techniques based on indexes rather than working with raw data.<br>
 * 
 * @see ExhaustiveGenerator
 * @see PairwiseDataGenerator
 */
public abstract class AbstractDataGenerator implements DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(AbstractDataGenerator.class);

    @Override
    public List<HierarchicalConfiguration> generate(HierarchicalConfiguration data,
            HierarchicalConfiguration context) {
        List<HierarchicalConfiguration> combConfig = new ArrayList<>();
        List<HierarchicalConfiguration> dataCombs = data.configurationsAt("data-combination");
        for (HierarchicalConfiguration dataComb : dataCombs) {
            List<HierarchicalConfiguration> sets = dataComb.configurationsAt("set");
            List<List<HierarchicalConfiguration>> combData = new ArrayList<List<HierarchicalConfiguration>>();
            for (HierarchicalConfiguration set : sets) {
                List<HierarchicalConfiguration> elements = set.configurationsAt("element");
                combData.add(elements);
            }
            List<List<Integer>> setIndexData = new ArrayList<>();
            for (List<HierarchicalConfiguration> set : combData) {
                List<Integer> elements = new ArrayList<>();
                for (int i = 0; i < set.size(); i++) {
                    elements.add(i);
                }
                setIndexData.add(elements);
            }
            int id = 1;
            List<List<Integer>> combinations = generateCombinations(setIndexData);
            final NumberFormat format = new DecimalFormat("###000");
            for (List<Integer> comb : combinations) {
                HierarchicalConfiguration combination = new HierarchicalConfiguration();
                combination.addProperty("[@test-id]", format.format(id++));
                for (int i = 0; i < comb.size(); i++) {
                    HierarchicalConfiguration element = combData.get(i).get(comb.get(i));
                    combination.append(element);
                }
                combConfig.add(combination);
            }
        }
        log.debug("Combinations: " + combConfig.size());
        return combConfig;
    }
    /**
     * This method exposes a easy way to generate combinations based on indexes.<br>
     * @param indexData 
     * @return
     */
    public abstract List<List<Integer>> generateCombinations(List<List<Integer>> indexData);
}

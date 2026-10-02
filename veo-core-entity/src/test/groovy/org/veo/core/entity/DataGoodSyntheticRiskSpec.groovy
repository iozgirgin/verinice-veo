/*
 * verinice.veo
 * Copyright (C) 2026 DataGood contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.veo.core.entity

import org.veo.core.entity.risk.ImpactRef
import org.veo.core.entity.risk.ProbabilityRef
import org.veo.core.entity.riskdefinition.CategoryDefinition
import org.veo.core.entity.riskdefinition.CategoryLevel
import org.veo.core.entity.riskdefinition.ProbabilityLevel
import org.veo.core.entity.riskdefinition.RiskValue

import groovy.json.JsonSlurper
import spock.lang.Specification

class DataGoodSyntheticRiskSpec extends Specification {
    def 'native category lookup reproduces all demo-calibrated CIA cases'() {
        given:
        def fixture = new JsonSlurper().parse(getClass().getResourceAsStream('/datagood/iso-risk-cases.json'))
        def levels = fixture.impacts.withIndex().collect { label, index -> new CategoryLevel(index, '#ffffff', null) }
        def matrix = fixture.matrix.collect { row -> row.collect { ordinal -> new RiskValue(ordinal, '#ffffff', fixture.riskLabels[ordinal]) } }
        def category = new CategoryDefinition('TEST-ISORA', matrix, levels)
        int comparisons = 0

        expect:
        fixture.cases.each { testCase ->
            def probability = ProbabilityRef.from(new ProbabilityLevel(fixture.probabilities.indexOf(testCase.probability), '#ffffff', null))
            testCase.impacts.each { dimension, impact ->
                def actual = category.getRiskValue(probability, ImpactRef.from(levels[fixture.impacts.indexOf(impact)]))
                assert actual.symbolicRisk == testCase.expectedInherent[dimension]: "${testCase.caseId}/${dimension}"
                comparisons++
            }
        }
        comparisons == 51
    }
}

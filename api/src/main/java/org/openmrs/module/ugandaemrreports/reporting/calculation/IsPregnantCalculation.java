/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.ugandaemrreports.reporting.calculation;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.ugandaemrreports.reporting.metadata.Dictionary;
import org.openmrs.module.ugandaemrreports.reporting.cohort.Filters;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the recorded pregnancy status of patients
 */
public class IsPregnantCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    @Override
    public String getFlagMessage() {
        return "Pregnant";
    }

    /**
     * Evaluates the calculation
     * @should calculate null for deceased patients
     * @should calculate null for patients with no recorded status
     * @should calculate last recorded pregnancy status for all patients
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        Concept yes = Dictionary.getConcept(Dictionary.YES);
        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANT), aliveAndFemale, context);
        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            boolean result = false;

            Obs pregStatusObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);

            if (pregStatusObs != null && pregStatusObs.getValueCoded().equals(yes)) {
                result = true;
            }

            ret.put(ptId, new BooleanResult(result, this));
        }

        return ret;
    }
}
/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ugandaemrreports.reporting.calculation.smc;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.ugandaemrreports.reporting.calculation.AbstractPatientCalculation;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Nicholas Ingosi on 8/1/17.
 */
public class SmcReturnFollowUpCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        return ret;
    }
}

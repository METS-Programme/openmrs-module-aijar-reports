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
package org.openmrs.module.ugandaemrreports.data.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.ugandaemrreports.reporting.metadata.Dictionary;

/**
 */
public class FSGEnrollmentDataConverter implements DataConverter {
    @Override
    public Object convert(Object obj) {
        Concept concept = ((Obs) obj).getValueCoded();

        if (concept == null) {
            return null;
        }
            if (concept.equals(Dictionary.getConcept("3af0aae4-4ea7-489d-a5be-c5339f7c5a77"))) {
                return "FSG";
            } else if (concept.equals(Dictionary.getConcept("0c0edbd7-ce81-48fd-8f56-ab3aa4406f8d"))) {
                return "FSGK";
            } else if (concept.equals(Dictionary.getConcept("680f7f8d-eac6-44b4-8899-101fa2c4f873"))) {
                return "FSG✔";
            }

        return concept;
    }



    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }
}
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

import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.ugandaemrreports.reporting.metadata.Dictionary;

/**
 */
public class TFVDataConverter implements DataConverter {
    @Override
    public Object convert(Object obj) {

        Obs obs = ((Obs) obj);
        if (obj == null) {
            return "";
        }

        if (obs.getValueCoded().equals(Dictionary.getConcept("25c448ff-5fe4-4a3a-8c0a-b5aaea9d5465"))) {
            return "TRR";
        }
        if (obs.getValueCoded().equals(Dictionary.getConcept("8dcaefaa-aa91-4c24-aaeb-122cff549ab3"))) {
            return "TRR+";
        }
        return obs;
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

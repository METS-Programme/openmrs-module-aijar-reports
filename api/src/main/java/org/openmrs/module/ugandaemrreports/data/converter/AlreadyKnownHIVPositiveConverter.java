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
public class AlreadyKnownHIVPositiveConverter implements DataConverter {
    @Override
    public Object convert(Object obj) {
        if (obj == null) {
            return "NO";
        }

        Obs obs = ((Obs) obj);

        if (obs.getValueCoded() != null) {

            if(obs.getValueCoded().equals(obs.getValueCoded().equals(Dictionary.getConcept("dcdf4241-30ab-102d-86b0-7a5022ba4115")))){
                return "YES";
            }else{
                return "NO";
            }
        }

        return null;
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

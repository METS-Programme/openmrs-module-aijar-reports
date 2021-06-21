package org.openmrs.module.ugandaemrreports.metadata;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.springframework.stereotype.Component;

/**
 *
 * This class contains common metadata that is used within UgandaEMR
 *
 */
@Component("commonReportMetadata")
public class CommonReportMetadata extends ReportMetadata {

	public PatientIdentifierType getARTClinicNumber() {
		return getPatientIdentifierType("e1731641-30ab-102d-86b0-7a5022ba4115");
	}
	public PersonAttributeType getTelephone(){
		return getPersonAttributeType("14d4f066-15f5-102d-96e4-000c29c2a5d7");
	}
}

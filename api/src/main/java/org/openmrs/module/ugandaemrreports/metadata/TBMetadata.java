package org.openmrs.module.ugandaemrreports.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata definitions for the HIV related reporting
 */
@Component("tbMetadata")
public class TBMetadata extends ReportMetadata {


    public Concept getUnitTBNo() {
        return getConcept("304df0d0-afe4-4a61-a917-d684b100a65a");
    }

    public Concept getHSDTBNo() {
        return getConcept("d1cda288-4853-4450-afbc-76bd4e65ea70");
    }

    public Concept getDistrictTBNo() {
        return getConcept("67e9ec2f-4c72-408b-8122-3706909d77ec");
    }

    public Concept getContactPerson() {
        return getConcept("163258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public Concept getContactPhoneNumber() {
        return getConcept("159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public Concept getRelationshipOfContactPerson() {
        return getConcept("dce138f8-30ab-102d-86b0-7a5022ba4115");
    }

    public Concept getIsPatientAHealthWorker() {
        return getConcept("5619AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public Concept getCadreOfHealthWorker() {
        return getConcept("1783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public Concept getCurrentTBRegimen() {
        return getConcept("16fd7307-0b26-4c8b-afa3-8362baff4042");
    }

    public Concept getDiseaseClassification() {
        return getConcept("d45871ee-62d6-4d4d-b905-f7b75a3fd3bb");
    }
    
    public Concept getTransferInLocation() {
        return getConcept("88e07e7c-f7e4-4ccf-8068-b770a3e3957b");
    }
    

    public PatientIdentifierType getTBIdentifier() {
        return getPatientIdentifierType("8110f2d2-1f98-4c38-aef3-11b19bb0a589");
    }

    public List<EncounterType> getTBFormEncounterType() {
        List<EncounterType> l = new ArrayList<EncounterType>();
        l.add(MetadataUtils.existing(EncounterType.class, "334bf97e-28e2-4a27-8727-a5ce31c7cd66"));
        return l;
    }

    public Concept getTBStartDate() {
        return getConcept("dce02eca-30ab-102d-86b0-7a5022ba4115");
    }

    public Concept getTBStopDate() {
        return getConcept("dd2adde2-30ab-102d-86b0-7a5022ba4115");
    }


    public Concept getTBStatus() {
        return getConcept("dce02aa1-30ab-102d-86b0-7a5022ba4115");
    }

    public Concept getTBStatusDiagnosed() {
        return getConcept("dcdac38b-30ab-102d-86b0-7a5022ba4115");
    }

    public Concept getTBStatusRx() {
        return getConcept("dcdaa6b4-30ab-102d-86b0-7a5022ba4115");
    }

    public Concept getNewTBPatient() {
        return getConcept("b3c43c5e-1987-42c1-a7b3-2c71dc58c126");
    }

    public Concept getRelapsedTBPatient() {
        return getConcept("8ad53c8c-e136-41e3-aab8-eace935a3bbe");
    }

    public Concept getFailedTBPatient() {
        return getConcept("8ad53c8c-e136-41e3-aab8-eace935a3bbe");
    }

    public Concept getLostToFollowupTBPatient() {
        return getConcept("a37462b6-1f47-4efb-8df5-2bdc742efc17");
    }

    public Concept getTBPatientWithUnknownTreatmentHistory() {
        return getConcept("1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public Concept getTypeOfPatient(){
        return getConcept("e077f196-c19a-417f-adc6-b175a3343bfd");
    }

    public Concept getHIVCounsellingAndTesting(){
        return getConcept("4395c8b9-2aaa-46b2-a228-5968e42bf085");
    }


    public List<Concept> getNewAndRelapsedTBPatients() {
        return getConceptList("b3c43c5e-1987-42c1-a7b3-2c71dc58c126,8ad53c8c-e136-41e3-aab8-eace935a3bbe");
    }

    public List<Concept> getTBPatientTypes() {
        return getConceptList("b3c43c5e-1987-42c1-a7b3-2c71dc58c126, 8ad53c8c-e136-41e3-aab8-eace935a3bbe, 7f5420a6-2c1a-4bb9-b6cf-8ed565933669,a37462b6-1f47-4efb-8df5-2bdc742efc17, 1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }
}

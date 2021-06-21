package org.openmrs.module.ugandaemrreports.reports;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ugandaemrreports.definition.data.converter.BirthDateConverter;
import org.openmrs.module.ugandaemrreports.library.ARTClinicCohortDefinitionLibrary;
import org.openmrs.module.ugandaemrreports.library.BasePatientDataLibrary;
import org.openmrs.module.ugandaemrreports.library.CommonDimensionLibrary;
import org.openmrs.module.ugandaemrreports.library.DataFactory;
import org.openmrs.module.ugandaemrreports.library.HIVCohortDefinitionLibrary;
import org.openmrs.module.ugandaemrreports.library.HIVPatientDataLibrary;
import org.openmrs.module.ugandaemrreports.metadata.HIVMetadata;
import org.openmrs.module.ugandaemrreports.reporting.dataset.definition.SharedDataDefintion;
import org.openmrs.module.ugandaemrreports.reporting.library.cohort.CommonCohortLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Patients on Second Line Regimens
 */
@Component
public class SetUpPatientsOnSecondLineRegimenReport extends UgandaEMRDataExportManager {

    @Autowired
    private DataFactory df;

    @Autowired
    ARTClinicCohortDefinitionLibrary hivCohorts;

    @Autowired
    private BuiltInPatientDataLibrary builtInPatientData;

    @Autowired
    HIVCohortDefinitionLibrary hivCohortDefinitionLibrary;

    @Autowired
    SharedDataDefintion sdd;

    @Autowired
    private HIVPatientDataLibrary hivPatientData;

    @Autowired
    private HIVMetadata hivMetadata;

    /**
     * @return the uuid for the report design for exporting to Excel
     */
    @Override
    public String getExcelDesignUuid() {
        return "a8fa38a2-921c-474e-b899-f323bb29f471";
    }

    public String getCSVDesignUuid() {
        return "40c4b347-954a-4630-9747-fd2c2169de62";
    }

    @Override
    public String getUuid() {
        return "864b1e53-eb4c-4d74-bfbd-331f95554150";
    }

    @Override
    public String getName() {
        return "Patients on Second Line Regimens";
    }

    @Override
    public String getDescription() {
        return "Patients on Second Line Regimens";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(df.getStartDateParameter());
        l.add(df.getEndDateParameter());
        return l;
    }

    public ReportDesign buildCSVReportDesign(ReportDefinition reportDefinition) {
        ReportDesign rd = createCSVDesign(getCSVDesignUuid(), reportDefinition);
        return rd;
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        l.add(buildReportDesign(reportDefinition));
        l.add(buildCSVReportDesign(reportDefinition));
        return l;
    }

    /**
     * Build the report design for the specified report, this allows a user to override the report design by adding
     * properties and other metadata to the report design
     *
     * @param reportDefinition
     * @return The report design
     */
    @Override

    public ReportDesign buildReportDesign(ReportDefinition reportDefinition) {
        ReportDesign rd = createExcelTemplateDesign(getExcelDesignUuid(), reportDefinition, "PatientARTRegimenStatusReport.xls");
        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:7,dataset:ARTREGIMENSTATUS");
        props.put("sortWeight", "5000");
        rd.setProperties(props);
        return rd;
    }

    @Override
    public ReportDefinition constructReportDefinition() {

        ReportDefinition rd = new ReportDefinition();

        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.setParameters(getParameters());

        PatientDataSetDefinition dsd = new PatientDataSetDefinition();

        CohortDefinition patientsWithoutRegimenLinesCohortDefinition = df.getWorkFlowStateCohortDefinition(hivMetadata.getSecondLineRegimenState());
        dsd.setName(getName());
        dsd.setParameters(getParameters());
        dsd.addRowFilter(Mapped.mapStraightThrough(patientsWithoutRegimenLinesCohortDefinition));
        addColumn(dsd, "Clinic number", hivPatientData.getClinicNumber());
        addColumn(dsd, "Middle Name", builtInPatientData.getPreferredMiddleName());
        addColumn(dsd, "Surname", builtInPatientData.getPreferredFamilyName());
        addColumn(dsd, "Given Name", builtInPatientData.getPreferredGivenName());
        addColumn(dsd, "Sex", builtInPatientData.getGender());
        dsd.addColumn("Birth Date", builtInPatientData.getBirthdate(), "", new BirthDateConverter());
        addColumn(dsd, "Age", hivPatientData.getAgeDuringPeriod());
        addColumn(dsd, "HIV Enrolled Date", hivPatientData.getSummaryPageDate());
        addColumn(dsd, "Art Start Date", hivPatientData.getARTStartDate());
        addColumn(dsd,  "Regimen Start Date", hivPatientData.getFirstRegimenPickupDate());
        addColumn(dsd, "VL Date", hivPatientData.getViralLoadDate());
        addColumn(dsd, "Clinic Stage", hivPatientData.getWHOClinicStage());
        addColumn(dsd, "VL Quantitative",  hivPatientData.getCurrentViralLoad());
        addColumn(dsd, "Current Regimen", hivPatientData.getCurrentRegimen());
        addColumn(dsd,"RegimenLine", hivPatientData.getRegimenLine());
        addColumn(dsd,"RegimenLineStartDate", hivPatientData.getRegimenLineStartDate());
        rd.addDataSetDefinition("ARTREGIMENSTATUS", Mapped.mapStraightThrough(dsd));
        rd.setBaseCohortDefinition(Mapped.mapStraightThrough(patientsWithoutRegimenLinesCohortDefinition));

        return rd;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}

package org.openmrs.module.ugandaemrreports.definition.data.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDate;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.evaluator.SqlPatientDataEvaluator;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ugandaemrreports.common.Enums;
import org.openmrs.module.ugandaemrreports.common.PatientData;
import org.openmrs.module.ugandaemrreports.common.Periods;
import org.openmrs.module.ugandaemrreports.common.StubDate;
import org.openmrs.module.ugandaemrreports.definition.data.definition.FUStatusPatientDataDefinition;
import org.openmrs.module.ugandaemrreports.library.HIVPatientDataLibrary;
import org.openmrs.module.ugandaemrreports.library.PatientDatasets;
import org.openmrs.module.ugandaemrreports.metadata.HIVMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Handler(supports = FUStatusPatientDataDefinition.class, order = 50)
public class FUStatusPatientDataDefinitionEvaluator implements PatientDataEvaluator {
    protected static final Log log = LogFactory.getLog(FUStatusPatientDataDefinition.class);

    @Autowired
    private HIVPatientDataLibrary hivLibrary;

    @Autowired
    private PatientDataService patientDataService;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private SqlPatientDataEvaluator sqlPatientDataEvaluator;

    @Autowired
    private HIVMetadata hivMetadata;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
        FUStatusPatientDataDefinition def = (FUStatusPatientDataDefinition) definition;

        EvaluatedPatientData c = new EvaluatedPatientData(def, context);

        if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
            return c;
        }

        Enums.Period period = def.getPeriod();

        Map<Integer, Date> m = new HashMap<Integer, Date>();

        LocalDate workingDate = StubDate.dateOf(DateUtil.formatDate(def.getStartDate(), "yyyy-MM-dd"));

        List<LocalDate> periods = Periods.getDatesDuringPeriods(workingDate, def.getPeriodToAdd(), period);

        LocalDate localStartDate = periods.get(0);
        LocalDate localEndDate = periods.get(1);

        HqlQueryBuilder artStartQuery = new HqlQueryBuilder();
        artStartQuery.select("o.personId", "MIN(o.valueDatetime)");
        artStartQuery.from(Obs.class, "o");
        artStartQuery.wherePersonIn("o.personId", context);
        artStartQuery.whereIn("o.concept", hivMetadata.getConceptList("99161"));
        artStartQuery.groupBy("o.personId");


        if (period == Enums.Period.QUARTERLY) {
            m = getPatientDateMap(artStartQuery, context);
        }


        SqlPatientDataDefinition sqlPatientDataDefinition = PatientDatasets.getFUStatus(localStartDate.toDate(), localEndDate.toDate());
        EvaluatedPatientData data = sqlPatientDataEvaluator.evaluate(sqlPatientDataDefinition, context);


        Map<Integer, Object> evaluatedData = data.getData();

        for (Integer pId : evaluatedData.keySet()) {
            Object o = evaluatedData.get(pId);
            if (o != null) {
                String dt = (String) o;

                if (dt != null) {
                    String[] splitString = dt.split(",");
                    String s0 = splitString[0];
                    String s1 = splitString[1];
                    String s2 = splitString[2];
                    String s3 = splitString[3];
                    String s4 = splitString[4];
                    String s5 = splitString[5];

                    PatientData patientData = new PatientData();
                    patientData.setPeriod(period);
                    patientData.setPeriodDate(localEndDate.toDate());

                    if (m.containsKey(pId)) {
                        patientData.setArtStartDate(m.get(pId));
                    }

                    if (!s0.equalsIgnoreCase("-")) {
                        Date encounterDate = DateUtil.parseDate(s0, "yyyy-MM-dd");
                        patientData.setEncounterDate(encounterDate);
                    }

                    if (!s1.equalsIgnoreCase("-")) {
                        Integer numberOfSinceLastVisit = Integer.valueOf(s1);
                        patientData.setNumberOfSinceLastVisit(numberOfSinceLastVisit);
                    }

                    if (!s2.equalsIgnoreCase("-")) {
                        Date deathDate = DateUtil.parseDate(s2, "yyyy-MM-dd");
                        patientData.setDeathDate(deathDate);
                    }

                    if (!s3.equalsIgnoreCase("-")) {
                        boolean transferredOut = true;
                        patientData.setTransferredOut(transferredOut);
                    }

                    if (!s4.equalsIgnoreCase("-")) {
                        Date nextVisitDate = DateUtil.parseDate(s4, "yyyy-MM-dd");
                        patientData.setNextVisitDate(nextVisitDate);
                    }

                    if (!s5.equalsIgnoreCase("-")) {
                        Date artStartDate = DateUtil.parseDate(s5, "yyyy-MM-dd");
                        patientData.setArtStartDate(artStartDate);
                    }

                    c.addData(pId, patientData);
                } else {
                    c.addData(pId, new PatientData());
                }
            }
        }
        return c;
    }

    protected Map<Integer, Date> getPatientDateMap(HqlQueryBuilder query, EvaluationContext context) {
        Map<Integer, Date> m = new HashMap<Integer, Date>();
        List<Object[]> queryResults = evaluationService.evaluateToList(query, context);
        for (Object[] row : queryResults) {
            m.put((Integer) row[0], (Date) row[1]);
        }
        return m;
    }
}

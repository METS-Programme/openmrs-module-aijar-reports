package org.openmrs.module.ugandaemrreports.definition.dataset.evaluator;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ugandaemrreports.common.PatientDataHelper;
import org.openmrs.module.ugandaemrreports.common.StubDate;
import org.openmrs.module.ugandaemrreports.definition.dataset.definition.MaternityDatasetDefinition;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { MaternityDatasetDefinition.class })
public class MaternityDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	
	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		MaternityDatasetDefinition definition = (MaternityDatasetDefinition) dataSetDefinition;
		
		String date = DateUtil.formatDate(definition.getStartDate(), "yyyy-MM-dd");
		
		LocalDate workingDate = StubDate.dateOf(date);
		
		int beginningMonth = workingDate.getMonthOfYear();
		int beginningYear = workingDate.getYear();
		
		context = ObjectUtil.nvl(context, new EvaluationContext());	

		String sql = "SELECT\r\n" +
			"  A.encounter_datetime											AS doa,\r\n" +
			"  IFNULL(IPD.value_text,\"\")										AS ipd_no,\r\n" +
			"  PI.identifier												AS anc_no,\r\n" +
			"  PN.family_name,\r\n" +
			"  PN.given_name,\r\n" +
			"  IFNULL(PA.address5,\"\")										AS village,\r\n" +
			"  IFNULL(PA.address4,\"\")										AS Parish,\r\n" +
			"  IFNULL(PAT.value,\"\")											AS phone_number,\r\n" +
			"  IFNULL(REF.value_text,\"\")										AS ref_no,\r\n" +
			"  IFNULL(GRAVIDA.value_numeric,\"\")									AS gravida,\r\n" +
			"  IFNULL(PARITY.value_numeric,\"\")									AS parity,\r\n" +
			"  IFNULL(GESTATION.value_numeric,\"\")									AS gestation,\r\n" +
			"  CASE DELIVERY.value_coded \r\n" +
			"	WHEN 1395 THEN 'T'\r\n" +
			"	WHEN 129218 THEN 'P'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS delivery,\r\n" +
			"  IFNULL(D.diagnoses,\"\")										AS final_diagnosis,\r\n" +
			"  IFNULL(WCS.name,\"\")											AS who_clinical_stage,\r\n" +
			"  IFNULL(CD4.value_numeric,\"\")										AS cd4_count,\r\n" +
			"  IFNULL(VL.value_numeric,\"\")										AS viral_load,\r\n" +
			"  IFNULL(MD.name,\"\")											AS mode_of_delivery,\r\n" +
			"  IFNULL(DATE(DOD.value_datetime),\"\")									AS date_of_delivery,\r\n" +
			"  IFNULL(TIME(DOD.value_datetime),\"\")									AS time_of_delivery,\r\n" +
			"  IFNULL(TSL.name,\"\")											AS third_stage_labour,\r\n" +
			"  IFNULL(OTHER_TREATMENT.value_text,\"\")									AS other_treatment,\r\n" +
			"  CASE EMTCT_W.value_coded\r\n" +
			"	WHEN 99313 THEN 'C'\r\n" +
			"	WHEN 99314 THEN 'TR'\r\n" +
			"	WHEN 99315 THEN 'TRR'\r\n" +
			"	ELSE ''		\r\n" +
			"  END													AS emtct_code_woman,\r\n" +
			"  CASE EMTCT_P.value_coded\r\n" +
			"	WHEN 99313 THEN 'C'\r\n" +
			"	WHEN 99314 THEN 'TR'\r\n" +
			"	WHEN 99315 THEN 'TRR'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS emtct_code_partner,\r\n" +
			"  IFNULL(ATM.name,\"\")											AS arv_to_mother,\r\n" +
			"  IFNULL(PAN.identifier,\"\")										AS pre_art_no,\r\n" +
			"  IFNULL(AGS.value_numeric,\"\")										AS agpar_score,\r\n" +
			"  CASE BS.value_coded\r\n" +
			"	WHEN 1534 THEN 'M'\r\n" +
			"	WHEN 1535 THEN 'F'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS baby_sex,\r\n" +
			"  CASE NBB.value_coded\r\n" +
			"	WHEN 162940 THEN 'SS'\r\n" +
			"	WHEN 162941 THEN 'BM'\r\n" +
			"	WHEN 162942 THEN 'BMD'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS not_breathing_at_birth,\r\n" +
			"  CASE ISSC.value_coded\r\n" +
			"	WHEN 1065 THEN 'Y'\r\n" +
			"	WHEN 1066 THEN 'N'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS skin_to_skin_contact,\r\n" +
			"  CASE BF.value_coded\r\n" +
			"	WHEN 1065 THEN 'Y'\r\n" +
			"	WHEN 1066 THEN 'N'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS breast_fed,\r\n" +
			"  IFNULL(RM.medications,\"\")										AS routine_medications,\r\n" +
			"  CASE CAD.value_coded\r\n" +
			"	WHEN 162936 THEN 'C'\r\n" +
			"	WHEN 162937 THEN 'NC'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS counselling_at_discharge,\r\n" +
			"  CASE MNC.value_coded\r\n" +
			"	WHEN 1065 THEN 'Y'\r\n" +
			"	WHEN 1066 THEN 'N'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS maternity_nutritional_counselling,\r\n" +
			"  CASE IYCF.value_coded\r\n" +
			"	WHEN 1065 THEN 'Y'\r\n" +
			"	WHEN 1066 THEN 'N'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS iycf,\r\n" +
			"  CASE IFO.value_coded\r\n" +
			"	WHEN 5526 THEN 'EBF'\r\n" +
			"	WHEN 99089 THEN 'RF'\r\n" +
			"	WHEN 6046 THEN 'MF'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS infant_feeding_programme,\r\n" +
			"  IFNULL(AAB.name,\"\")											AS arvs_administered_to_baby,\r\n" +
			"  IFNULL(BW.value_numeric,\"\")										AS baby_weight,\r\n" +
			"  IFNULL(IM.immunizations,\"\")										AS immunizations,\r\n" +
			"  IFNULL(FP.fp_methods,\"\")										AS family_planning,\r\n" +
			"  CASE CMD.value_coded\r\n" +
			"	WHEN 99327 THEN 'D'\r\n" +
			"	WHEN 99328 THEN 'DD'\r\n" +
			"	WHEN 99329 THEN 'R'\r\n" +
			"	WHEN 90306 THEN 'T'\r\n" +
			"	WHEN 162949 THEN 'DF'\r\n" +
			"	WHEN 162950 THEN 'DDF'\r\n" +
			"	WHEN 162951 THEN 'RF'\r\n" +
			"	WHEN 162952 THEN 'TF'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS condition_mother_discharge,\r\n" +
			"  CASE CBD.value_coded\r\n" +
			"	WHEN 99331 THEN 'MSB'\r\n" +
			"	WHEN 162953 THEN 'FSB'\r\n" +
			"	WHEN 99332 THEN 'NND'\r\n" +
			"	WHEN 99333 THEN 'AB'\r\n" +
			"	WHEN 162954 THEN 'BDF'\r\n" +
			"	ELSE ''\r\n" +
			"  END													AS condition_baby_discharge,\r\n" +
			"  IFNULL(DATE(P6M.value_datetime),\"\")										AS pnc_at6_mother,\r\n" +
			"  IFNULL(DATE(P6B.value_datetime),\"\")										AS pnc_at6_baby,\r\n" +
			"  IFNULL(DATE(PD.value_text),\"\")										AS person_discharging,\r\n" +
			"  IFNULL(DATE(DD.value_datetime),\"\")										AS discharge_date,\r\n" +
			"  YEAR(A.encounter_datetime) - YEAR(P.birthdate) - (RIGHT(A.encounter_datetime, 5) < RIGHT(P.birthdate, 5)) AS age,\r\n" +
			"  IFNULL(EP.provider_name,\"\")										AS provider \r\n" +
			"FROM\r\n" +
			"  (SELECT\r\n" +
			"     e.encounter_id,\r\n" +
			"     e.patient_id,\r\n" +
			"     e.encounter_datetime\r\n" +
			"   FROM encounter e\r\n" +
			"   INNER JOIN encounter_type et ON et.encounter_type_id = e.encounter_type AND et.uuid = 'a9f11592-22e7-45fc-904d-dfe24cb1fc67' \r\n" +
		        String.format(
		            "   WHERE YEAR(e.encounter_datetime) = %s AND MONTH(e.encounter_datetime) = %s AND e.voided = 0) A",
		            beginningYear, beginningMonth) +
			"  INNER JOIN person P\r\n" +
			"    ON (P.person_id = A.patient_id)\r\n" +
			"  LEFT JOIN person_name PN ON (P.person_id = PN.person_id)\r\n" +
			"  LEFT JOIN person_address PA ON (P.person_id = PA.person_id AND PA.preferred = 1 AND PA.voided = 0)\r\n" +
			"  LEFT JOIN person_attribute PAT ON (P.person_id = PAT.person_id AND PAT.person_attribute_type_id = 8 AND PAT.voided = 0)\r\n" +
			"  LEFT JOIN patient_identifier PI ON (P.person_id = PI.patient_id AND PI.identifier_type = 7 AND PI.voided = 0)\r\n" +
			"  LEFT JOIN obs IPD ON IPD.encounter_id = A.encounter_id AND IPD.concept_id = 1646 AND IPD.voided = 0 \r\n" +
			"  LEFT JOIN obs REF ON REF.encounter_id = A.encounter_id AND REF.concept_id = 99767 AND REF.voided = 0\r\n" +
			"  LEFT JOIN obs GRAVIDA ON GRAVIDA.encounter_id = A.encounter_id AND GRAVIDA.concept_id = 5624 AND GRAVIDA.voided = 0\r\n" +
			"  LEFT JOIN obs PARITY ON PARITY.encounter_id = A.encounter_id AND PARITY.concept_id = 1053 AND PARITY.voided = 0\r\n" +
			"  LEFT JOIN obs GESTATION ON GESTATION.encounter_id = A.encounter_id AND GESTATION.concept_id = 162929 AND GESTATION.voided = 0\r\n" +
			"  LEFT JOIN obs DELIVERY ON DELIVERY.encounter_id = A.encounter_id AND DELIVERY.concept_id = 161033 AND DELIVERY.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id,REPLACE(GROUP_CONCAT(CONCAT('\"', cn.name , '\"\\n')),\",\",\"\") diagnoses\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 1284 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) D ON D.encounter_id = A.encounter_id\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id, cn.name\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 90203 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) WCS ON WCS.encounter_id = A.encounter_id	     \r\n" +
			"  LEFT JOIN obs CD4 ON CD4.encounter_id = A.encounter_id AND CD4.concept_id = 5497 AND CD4.voided = 0\r\n" +
			"  LEFT JOIN obs VL ON VL.encounter_id = A.encounter_id AND VL.concept_id = 856 AND VL.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id, cn.name\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 5630 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) MD ON MD.encounter_id = A.encounter_id\r\n" +
			"  LEFT JOIN obs DOD ON DOD.encounter_id = A.encounter_id AND DOD.concept_id = 99340 AND DOD.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id, cn.name\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 99336 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) TSL ON TSL.encounter_id = A.encounter_id\r\n" +
			"  LEFT JOIN obs OTHER_TREATMENT ON OTHER_TREATMENT.encounter_id = A.encounter_id AND OTHER_TREATMENT.concept_id = 99341 AND OTHER_TREATMENT.voided = 0\r\n" +
			"  LEFT JOIN obs EMTCT_W ON EMTCT_W.encounter_id = A.encounter_id AND EMTCT_W.concept_id = 99317 AND EMTCT_W.voided = 0\r\n" +
			"  LEFT JOIN obs EMTCT_P ON EMTCT_P.encounter_id = A.encounter_id AND EMTCT_P.concept_id = 99342 AND EMTCT_P.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id, cn.name\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 99344 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) ATM ON ATM.encounter_id = A.encounter_id\r\n" +
			"  LEFT JOIN patient_identifier PAN ON (P.person_id = PAN.patient_id AND PAN.identifier_type = 4 AND PAN.voided = 0)\r\n" +
			"  LEFT JOIN obs AGS ON AGS.encounter_id = A.encounter_id AND AGS.concept_id = 99326 AND AGS.voided = 0\r\n" +
			"  LEFT JOIN obs BS ON BS.encounter_id = A.encounter_id AND BS.concept_id = 1587 AND BS.voided = 0\r\n" +
			"  LEFT JOIN obs NBB ON NBB.encounter_id = A.encounter_id AND NBB.concept_id = 162943 AND NBB.voided = 0\r\n" +
			"  LEFT JOIN obs ISSC ON ISSC.encounter_id = A.encounter_id AND ISSC.concept_id = 162944 AND ISSC.voided = 0\r\n" +
			"  LEFT JOIN obs BF ON BF.encounter_id = A.encounter_id AND BF.concept_id = 162945 AND BF.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id,REPLACE(GROUP_CONCAT(CONCAT('\"', cn.name , '\"\\n')),\",\",\"\") medications\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 162948 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) RM ON RM.encounter_id = A.encounter_id\r\n" +
			"  LEFT JOIN obs CAD ON CAD.encounter_id = A.encounter_id AND CAD.concept_id = 162938 AND CAD.voided = 0\r\n" +
			"  LEFT JOIN obs MNC ON MNC.encounter_id = A.encounter_id AND MNC.concept_id = 99750 AND MNC.voided = 0\r\n" +
			"  LEFT JOIN obs IYCF ON IYCF.encounter_id = A.encounter_id AND IYCF.concept_id = 99749 AND IYCF.voided = 0\r\n" +
			"  LEFT JOIN obs IFO ON IFO.encounter_id = A.encounter_id AND IFO.concept_id = 1151 AND IFO.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id, cn.name\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 162935 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) AAB ON AAB.encounter_id = A.encounter_id\r\n" +
			"  LEFT JOIN obs BW ON BW.encounter_id = A.encounter_id AND BW.concept_id = 5916 AND BW.voided = 0\r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id,REPLACE(GROUP_CONCAT(CONCAT('\"', cn.name , '\"\\n')),\",\",\"\") immunizations\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 984 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) IM ON IM.encounter_id = A.encounter_id	     \r\n" +
			"  LEFT JOIN (SELECT \r\n" +
			"		o.encounter_id,REPLACE(GROUP_CONCAT(CONCAT('\"', cn.name , '\"\\n')),\",\",\"\") fp_methods\r\n" +
			"	     FROM obs o \r\n" +
			"	     INNER JOIN concept_name cn ON o.value_coded = cn.concept_id AND cn.locale = 'en' AND cn.concept_name_type = 'FULLY_SPECIFIED' AND cn.voided = 0 WHERE o.concept_id = 374 AND o.voided = 0\r\n" +
			"	     GROUP BY o.encounter_id ) FP ON FP.encounter_id = A.encounter_id	     \r\n" +
			"  LEFT JOIN obs CMD ON CMD.encounter_id = A.encounter_id AND CMD.concept_id = 99330 AND CMD.voided = 0\r\n" +
			"  LEFT JOIN obs CBD ON CBD.encounter_id = A.encounter_id AND CBD.concept_id = 99334 AND CBD.voided = 0\r\n" +
			"  LEFT JOIN obs P6M ON P6M.encounter_id = A.encounter_id AND P6M.concept_id = 162955 AND P6M.voided = 0\r\n" +
			"  LEFT JOIN obs P6B ON P6B.encounter_id = A.encounter_id AND P6B.concept_id = 162956 AND P6B.voided = 0\r\n" +
			"  LEFT JOIN obs PD ON PD.encounter_id = A.encounter_id AND PD.concept_id = 1473 AND PD.voided = 0\r\n" +
			"  LEFT JOIN obs DD ON DD.encounter_id = A.encounter_id AND DD.concept_id = 162958 AND DD.voided = 0\r\n" +
			"  LEFT JOIN (SELECT CONCAT(PN.given_name, \" \", PN.family_name) AS provider_name, E.encounter_id  FROM encounter E \r\n" +
			"	INNER JOIN encounter_provider EP ON E.encounter_id = EP.encounter_id AND E.voided = 0 AND EP.voided = 0\r\n" +
			"	INNER JOIN provider P ON P.provider_id = EP.provider_id\r\n" +
			"	INNER JOIN users U ON U.person_id = P.person_id\r\n" +
			"	INNER JOIN user_role UR ON U.user_id = UR.user_id\r\n" +
			"	INNER JOIN role R ON UR.role = R.role AND R.uuid  = '28de5e93-5462-4e25-8a6a-a980b637dc61'\r\n" +
			"	INNER JOIN person_name PN ON P.person_id = PN.person_id AND PN.voided = 0 AND PN.preferred = 1\r\n" +
			"	) EP ON EP.encounter_id = A.encounter_id\r\n";
		
		SqlQueryBuilder q = new SqlQueryBuilder(sql);
		
		List<Object[]> results = evaluationService.evaluateToList(q, context);
		
		PatientDataHelper pdh = new PatientDataHelper();
				
		for (Object[] r : results) {
			DataSetRow row = new DataSetRow();
			
			Timestamp doa = (Timestamp) r[0];

			String ipdNo = (String) r[1];

			String ancAndRefNo = new StringBuilder()
					.append(r[2] + "\n")
					.append(r[8] + "\n")
					.toString();

			String name = new StringBuilder()
					.append(r[3] + "\n")
					.append(r[4])
					.toString();
						
			String address = new StringBuilder()
					.append(r[5] + "\n")
					.append(r[6] + "\n")
					.toString();
			
			String phoneNo = (String) r[7];
			
			Long age = (Long) r[46];
			
			String age10To19 = age >= 10 && age <=19 ? "Y":"";
			
			String age20To24 = age >= 20 && age <=24 ? "Y":"";;
			
			String ageGTE25 = age >= 25 ? "Y":"";
			
			String gravidaAndParity = new StringBuilder()
					.append(r[9] + "\n")
					.append(r[10] + "\n")
					.toString();

			String weeksOfGestation = (String) r[11];

			String delivery = (String) r[12];

			String finalDiagnosis = (String) r[13];
			
			String wCSAndCD4AndVL = new StringBuilder()
					.append(r[14] + "\n")
					.append(r[15] + "\n")
					.append(r[16] + "\n")
			        .toString();

			String modeOfDelivery = (String) r[17];

			String dateOfDelivery = (String) r[18];

			String timeOfDelivery = (String) r[19];

			String managementOfThirdStageLabour = (String) r[20];

			String otherTreatment = (String) r[21];

			String emtctWoman = (String) r[22];
			
			String emtctPartner = (String) r[23];

			String arvToMotherAndPreArtNo = new StringBuilder()
					.append(r[24] + "\n")
					.append(r[25] + "\n")
					.toString();
			
			String vitAAndMuacAndInrNo = "";
			
			String agparScore = (String) r[26];
			
			String babySex = (String) r[27];
			
			String notBreathingAtBirth = (String) r[28];
			
			String immediateSkinToSkinCOntact = (String) r[29];
			
			String breastfedLessThanOneHour = (String) r[30];
			
			String routineMedication = (String) r[31];
			
			String counsellingAtDischarge = new StringBuilder()
					.append(r[32] + "\n")
					.append(r[33] + "\n")
					.toString();
			
			String iycfAndIfo = new StringBuilder()
					.append(r[34] + "\n")
					.append(r[35] + "\n")
					.toString();
			
			String babyWeight = (String) r[37];
			
			String arvsAdministeredToBaby = (String) r[36];
			
			String immunization = (String) r[38];
			
			String familyPlanningMethod = (String) r[39];
			
			String conditionOfMotherAtDischarge = (String) r[40];
			
			String conditionOfBabyAtDischarge = (String) r[41];
						
			String pncAtSixMother = (String) r[42];
			
			String pncAtSixBaby = (String) r[43];
			
			String dateOfDischargeAndNameOfPersonDischarging = new StringBuilder()
					.append(r[44] + "\n")
					.append(r[45])
					.toString();

			String deliveredBy = (String) r[47];
			
			pdh.addCol(row, "DOA", doa);
			pdh.addCol(row, "IPD No", ipdNo);
			pdh.addCol(row, "ANC and Ref No", ancAndRefNo);
			pdh.addCol(row, "Name", name);
			pdh.addCol(row, "Village and Parish", address);
			pdh.addCol(row, "Phone No", phoneNo);
			pdh.addCol(row, "Age 10-19", age10To19);
			pdh.addCol(row, "Age 20-24", age20To24);
			pdh.addCol(row, "Age >=25", ageGTE25);
			pdh.addCol(row, "Gravida and Parity", gravidaAndParity);
			pdh.addCol(row, "Weeks of Gestation", weeksOfGestation);
			pdh.addCol(row, "T and P", delivery);
			pdh.addCol(row, "Final Diagnosis", finalDiagnosis);
			pdh.addCol(row, "WCS and CD4 and VL", wCSAndCD4AndVL);
			pdh.addCol(row, "Mode of Delivery", modeOfDelivery);
			pdh.addCol(row, "Date of Delivery", dateOfDelivery);
			pdh.addCol(row, "Time of Delivery", timeOfDelivery);
			pdh.addCol(row, "Management of 3rd Stage", managementOfThirdStageLabour);
			pdh.addCol(row, "Other Treatment", otherTreatment);
			pdh.addCol(row, "eMTCT W", emtctWoman);
			pdh.addCol(row, "eMTCT P", emtctPartner);
			pdh.addCol(row, "ARV to Mother and Pre-ART No", arvToMotherAndPreArtNo);
			pdh.addCol(row, "Vit A and MUAC and INR No", vitAAndMuacAndInrNo);
			pdh.addCol(row, "Agpar Score", agparScore);
			pdh.addCol(row, "Sex", babySex);
			pdh.addCol(row, "Not Breathing at Birth", notBreathingAtBirth);
			pdh.addCol(row, "Immediate Skin to Skin Contact", immediateSkinToSkinCOntact);
			pdh.addCol(row, "Breastfed <=1hr", breastfedLessThanOneHour);
			pdh.addCol(row, "Routine Medication", routineMedication);
			pdh.addCol(row, "Counselling at Discharge", counsellingAtDischarge);
			pdh.addCol(row, "IYCF and Infant Feeding Option", iycfAndIfo);
			pdh.addCol(row, "Baby Weight", babyWeight);
			pdh.addCol(row, "ARVs Administered to Baby", arvsAdministeredToBaby);
			pdh.addCol(row, "Immunization BCG and Polio", immunization);
			pdh.addCol(row, "Family Planning Method", familyPlanningMethod);
			pdh.addCol(row, "Condition of Mother at Discharge", conditionOfMotherAtDischarge);
			pdh.addCol(row, "Condition of Baby at Discharge", conditionOfBabyAtDischarge);
			pdh.addCol(row, "Delivered By", deliveredBy);
			pdh.addCol(row, "PNC at 6 Mother", pncAtSixMother);
			pdh.addCol(row, "PNC at 6 Baby", pncAtSixBaby);
			pdh.addCol(row, "Date of Discharge and Name of person Discharging", dateOfDischargeAndNameOfPersonDischarging);
			
			dataSet.addRow(row);
		}
		return dataSet;
		
	}
	
}

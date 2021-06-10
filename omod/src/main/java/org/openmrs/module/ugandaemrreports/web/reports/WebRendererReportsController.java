/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrreports.web.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.*;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class WebRendererReportsController {

	protected static Log log = LogFactory.getLog(WebRendererReportsController.class);
	
	/**
	 * Default Constructor
	 */
	public WebRendererReportsController() { }

    @RequestMapping("/module/ugandaemrreports/reports/renderDataSet")
    public void renderDataSet(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(required=false, value="uuid") String uuid,
                              @RequestParam(required=false, value="name") String name,
                              @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type) throws IOException {
        try {
            DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
            DataSetDefinition dataSetDefinition = service.getDefinition(uuid, type);
            dataSetDefinition.setName(name);
            dataSetDefinition.getParameters().clear();

            ReportDefinition rd = new ReportDefinition();
            rd.setName(name);
            rd.getParameters().clear();



            CsvReportRenderer renderer = new CsvReportRenderer();

            EvaluationContext context = new EvaluationContext();

            for (Property p : DefinitionUtil.getConfigurationProperties(dataSetDefinition)) {
                String fieldName = p.getField().getName();
                Object valToSet = WidgetUtil.getFromRequest(request, fieldName, p.getField());

                 ReflectionUtil.setPropertyValue(dataSetDefinition, p.getField(), valToSet);

            }

            String fileName = dataSetDefinition.getName();
            rd.addDataSetDefinition("renderDatasetParsed", Mapped.mapStraightThrough(dataSetDefinition));
            response.setHeader("Content-Type", "text/csv");
            ReportData reportData = getReportDefinitionService().evaluate(rd, context);

            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +".csv" +"\"");
            renderer.render(reportData, "", response.getOutputStream());
        }
        catch (Exception e) {
            e.printStackTrace(response.getWriter());
        }
    }

    private ReportDefinitionService getReportDefinitionService() {
        return Context.getService(ReportDefinitionService.class);
    }
}

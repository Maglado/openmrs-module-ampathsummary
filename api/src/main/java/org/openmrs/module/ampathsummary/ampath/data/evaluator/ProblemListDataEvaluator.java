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
package org.openmrs.module.ampathsummary.ampath.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ampathsummary.ampath.data.definition.ProblemListDataDefinition;
import org.openmrs.module.ampathsummary.reporting.data.definition.MultiConceptObsDataDefinition;
import org.openmrs.module.ampathsummary.util.ConceptConstants;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 */
@Handler(supports = ProblemListDataDefinition.class, order = 50)
public class ProblemListDataEvaluator implements PersonDataEvaluator {

    /**
     * @should return the obs that match the passed definition configuration
     * @see PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {

        ProblemListDataDefinition dataDefinition = (ProblemListDataDefinition) definition;
        EvaluatedPersonData data = new EvaluatedPersonData(dataDefinition, context);
        Cohort cohort = context.getBaseCohort();

        if (cohort != null && cohort.isEmpty())
            return data;

        MultiConceptObsDataDefinition d = new MultiConceptObsDataDefinition();
        d.addQuestion(ConceptConstants.PROBLEM_ADDED);
        d.addQuestion(ConceptConstants.PROBLEM_RESOLVED);

        EvaluatedPersonData personData = Context.getService(PersonDataService.class).evaluate(d, context);
        for (Map.Entry<Integer, Object> entry : personData.getData().entrySet()) {
            data.addData(entry.getKey(), consolidate(entry.getValue()));
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    private Object consolidate(final Object object) {
        // consolidate the entries for every patient and only return
        List<Obs> consolidatedList = new ArrayList<Obs>();
        if (object != null) {
            List<Obs> list = (List<Obs>) object;
            Iterator<Obs> iterator = list.iterator();
            while (iterator.hasNext()) {
                Obs obs = iterator.next();
                Concept concept = obs.getConcept();
                if (concept.equals(ConceptConstants.PROBLEM_ADDED)) {
                    // just add all problem added observations
                    consolidatedList.add(obs);
                } else {
                    Concept valueCoded = obs.getValueCoded();
                    Iterator<Obs> consolidatedIterator = consolidatedList.iterator();
                    while (consolidatedIterator.hasNext()) {
                        Obs consolidatedObs = consolidatedIterator.next();
                        Concept consolidatedValueCoded = consolidatedObs.getValueCoded();
                        if (consolidatedValueCoded.equals(valueCoded)
                                && !ConceptConstants.UNRESOLVABLE_PROBLEMS.contains(valueCoded)) {
                            // remove all previously added problem list because they are now marked as resolved
                            // unless they are marked as unresolvable problem list!
                            consolidatedIterator.remove();
                        }
                    }
                }

            }
        }
        return consolidatedList;
    }
}

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
package org.openmrs.module.ampathsummary.reporting.data.definition;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
@Localized("ampathsummary.CodedObsDataDefinition")
public class CodedObsDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
    @ConfigurationProperty
    private TimeQualifier which;

    @ConfigurationProperty(required = true)
    private Concept question;

    @ConfigurationProperty
    private List<Concept> answers;

    @ConfigurationProperty
    private Date onOrAfter;

    @ConfigurationProperty
    private Date onOrBefore;

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        if (which == TimeQualifier.LAST || which == TimeQualifier.FIRST) {
            return Obs.class;
        }
        return List.class;
    }

    public TimeQualifier getWhich() {
        return which;
    }

    public void setWhich(final TimeQualifier which) {
        this.which = which;
    }

    public Concept getQuestion() {
        return question;
    }

    public void setQuestion(final Concept question) {
        this.question = question;
    }

    public void addAnswer(final Concept answer) {
        getAnswers().add(answer);
    }

    public List<Concept> getAnswers() {
        if (answers == null)
            answers = new ArrayList<Concept>();
        return answers;
    }

    public void setAnswers(final List<Concept> answers) {
        this.answers = answers;
    }

    public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(final Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(final Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }
}
package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment.r4;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment.AbstractEnrollmentToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilter;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchParamType;
import org.hl7.fhir.r4.model.CarePlan;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * R4 specific version of {@link AbstractEnrollmentToFhirDataProvider}.
 *
 * @author volsch
 */
@Component
public class R4EnrollmentToFhirDataProvider extends AbstractEnrollmentToFhirDataProvider
{
    public R4EnrollmentToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull EnrollmentService enrollmentService )
    {
        super( scriptExecutor, enrollmentService );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected void initSearchFilter( @Nonnull FhirVersion fhirVersion, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull SearchFilter searchFilter )
    {
        searchFilter.add( CarePlan.SP_INSTANTIATES_CANONICAL, SearchParamType.REFERENCE, "program" );
        searchFilter.add( CarePlan.SP_INSTANTIATES_URI, SearchParamType.REFERENCE, "program" );
        searchFilter.add( CarePlan.SP_PATIENT, SearchParamType.REFERENCE, "trackedEntityInstance" );
        searchFilter.add( CarePlan.SP_SUBJECT, SearchParamType.REFERENCE, "trackedEntityInstance" );
        searchFilter.add( LocationExtensionUtils.LOCATION_SEARCH_PARAM, SearchParamType.REFERENCE, "ou" );
    }
}

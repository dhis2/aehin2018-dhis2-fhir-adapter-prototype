package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.program;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Utility class to extract tracked entity FHIR resource type from program.
 *
 * @author volsch
 */
public abstract class ProgramTrackedEntityTypeUtils
{
    @Nonnull
    public static Optional<TrackedEntityRule> getTrackedEntityRule(
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository,
        @Nonnull Program program )
    {
        if ( program.getTrackedEntityTypeId() == null )
        {
            return Optional.empty();
        }

        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference(
            new Reference( program.getTrackedEntityTypeId(), ReferenceType.ID ) ).orElse( null );

        if ( trackedEntityType == null )
        {
            return Optional.empty();
        }

        return trackedEntityRuleRepository.findByTypeRefs( trackedEntityType.getAllReferences() ).stream().min( ( o1, o2 ) -> o2.getEvaluationOrder() - o1.getEvaluationOrder() );
    }

    @Nullable
    public static FhirResourceType getTrackedEntityFhirResourceType(
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository,
        @Nonnull Program program )
    {
        return getTrackedEntityRule( trackedEntityMetadataService, trackedEntityRuleRepository, program ).map( AbstractRule::getFhirResourceType ).orElse( null );
    }

    private ProgramTrackedEntityTypeUtils()
    {
        super();
    }
}

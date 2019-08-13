package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.aggregate;

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

import org.dhis2.fhir.adapter.dhis.aggregate.DataValueSet;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataValueSetRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractFhirMeasureReportToDataValueSetTranformer extends AbstractFhirToDhisTransformer<DataValueSet, DataValueSetRule>
{
    public AbstractFhirMeasureReportToDataValueSetTranformer( @Nonnull final ScriptExecutor scriptExecutor,
        @Nonnull final TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull final OrganizationUnitService organizationUnitService,
        @Nonnull final TrackedEntityService trackedEntityService,
        @Nonnull final FhirDhisAssignmentRepository fhirDhisAssignmentRepository,
        @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ValueConverter valueConverter)
    {
        super( scriptExecutor, organizationUnitService, new StaticObjectProvider<>( trackedEntityMetadataService ),
            new StaticObjectProvider<>( trackedEntityService ), fhirDhisAssignmentRepository, scriptExecutionContext,
            valueConverter );
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.DATA_VALUE_SET;
    }

    @Nonnull
    @Override
    public Class<DataValueSet> getDhisResourceClass()
    {
        return DataValueSet.class;
    }

    @Nonnull
    @Override
    public Class<DataValueSetRule> getRuleClass()
    {
        return DataValueSetRule.class;
    }

    @Nonnull
    @Override
    protected Optional<DataValueSet> getResourceById( @Nullable final String id )
        throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<DataValueSet> getActiveResource( @Nonnull final FhirToDhisTransformerContext context,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nonnull final Map<String, Object> scriptVariables,
        final boolean sync, final boolean refreshed ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<DataValueSet> findResourceById( @Nonnull final FhirToDhisTransformerContext context,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nonnull final String id,
        @Nonnull final Map<String, Object> scriptVariables )
    {
        return Optional.empty();
    }

    @Override
    protected boolean isAlwaysActiveResource( @Nonnull final RuleInfo<DataValueSetRule> ruleInfo )
    {
        return false;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull final FhirToDhisTransformerContext context,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nonnull final Map<String, Object> scriptVariables )
        throws TransformerException
    {
        return context.getFhirRequest().isSync();
    }

    @Nullable
    @Override
    public FhirToDhisDeleteTransformOutcome<DataValueSet> transformDeletion(
        @Nonnull final FhirClientResource fhirClientResource, @Nonnull final RuleInfo<DataValueSetRule> ruleInfo,
        @Nonnull final DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        return null;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<DataValueSet> transform( @Nonnull final FhirClientResource fhirClientResource,
        @Nonnull final FhirToDhisTransformerContext context, @Nonnull final IBaseResource input,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nonnull final Map<String, Object> scriptVariables )
        throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        final DataValueSet dataValueSet = getResource( fhirClientResource, context, ruleInfo, scriptVariables ).orElse( null );
        if ( dataValueSet == null )
        {
            return null;
        }

        final Optional<OrganizationUnit> organizationUnit;
        if ( ruleInfo.getRule().getOrgUnitLookupScript() == null )
        {
            logger.info( "Rule does not define an organization unit lookup script and data value set does not yet include one." );
            return null;
        }
        else
        {
            organizationUnit = getOrgUnit( context, ruleInfo, ruleInfo.getRule().getOrgUnitLookupScript(), variables );
            organizationUnit.ifPresent( ou -> dataValueSet.setOrgUnitId( ou.getId() ) );
        }

        if ( !organizationUnit.isPresent() )
        {
            throw new TransformerDataException( "Organization unit ID cannot be decided." );
        }

        return transformInternal( fhirClientResource, context, input, ruleInfo, scriptVariables, dataValueSet );
    }

    abstract protected FhirToDhisTransformOutcome<DataValueSet> transformInternal( @Nonnull final FhirClientResource fhirClientResource,
        @Nonnull final FhirToDhisTransformerContext context, @Nonnull final IBaseResource input,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nonnull final Map<String, Object> scriptVariables,
        @Nonnull final DataValueSet dataValueSet ) throws TransformerException;

    @Nonnull
    protected Optional<String> getDataSetId( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<DataValueSetRule> ruleInfo, @Nonnull ExecutableScript lookupScript, @Nonnull Map<String, Object> scriptVariables )
    {
        //TODO: I need to figure out how to obtain DataSetId -> It should be done in similar way as it is done for OrgUnits
        final Reference orgUnitReference = executeScript( context, ruleInfo, lookupScript, scriptVariables, Reference.class );
//        if ( orgUnitReference == null )
//        {
//            logger.info( "Could not extract organization unit reference." );
//            return Optional.empty();
//        }
        return getDataSetId( context, orgUnitReference, scriptVariables );
    }

    @Nonnull
    protected Optional<String> getDataSetId( @Nonnull FhirToDhisTransformerContext context, @Nonnull Reference orgUnitReference, @Nonnull Map<String, Object> scriptVariables )
    {
//        final Optional<OrganizationUnit> organisationUnit = organizationUnitService.findMetadataByReference( orgUnitReference );
//        if ( !organisationUnit.isPresent() )
//        {
//            logger.info( "Organization unit of reference does not exist: " + orgUnitReference );
//        }
//        return organisationUnit;

        return Optional.empty();
    }
}

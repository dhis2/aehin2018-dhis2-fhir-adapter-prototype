package org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.RequiredValueType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AbstractFhirToDhisTransformer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class FhirToTrackedEntityTransformer extends AbstractFhirToDhisTransformer<TrackedEntityInstance, TrackedEntityRule>
{
    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final TrackedEntityService trackedEntityService;

    private final OrganisationUnitService organisationUnitService;

    private final ValueConverter valueConverter;

    public FhirToTrackedEntityTransformer( @Nonnull ScriptExecutor scriptExecutor,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull OrganisationUnitService organisationUnitService, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor );
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityService = trackedEntityService;
        this.organisationUnitService = organisationUnitService;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityInstance> getDhisResourceClass()
    {
        return TrackedEntityInstance.class;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityRule> getRuleClass()
    {
        return TrackedEntityRule.class;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<TrackedEntityInstance> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        if ( !addScriptVariables( variables, rule ) )
        {
            return null;
        }

        final TrackedEntityType trackedEntityType = getTrackedEntityType( variables );
        final TrackedEntityInstance trackedEntityInstance = getResource( context, rule, variables )
            .orElseThrow( () -> new FatalTransformerException( "Tracked entity instance could neither be retrieved nor created." ) );
        final WritableScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance(
            trackedEntityType, trackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedTrackedEntityInstance );

        final Optional<OrganisationUnit> organisationUnit = getOrganizationUnit( context, rule, variables );
        if ( !organisationUnit.isPresent() )
        {
            return null;
        }
        scriptedTrackedEntityInstance.setOrganizationUnitId( organisationUnit.get().getId() );

        if ( trackedEntityInstance.isNewResource() )
        {
            final String identifier = getIdentifier( context, rule, scriptVariables );
            if ( identifier == null )
            {
                return null;
            }

            final String attributeId = trackedEntityType.getOptionalTypeAttribute( rule.getTrackedEntityIdentifierReference() ).orElseThrow( () ->
                new TransformerMappingException( "Rule \"" + rule + "\" expects that tracked entity type \"" + trackedEntityType.getName() +
                    "\" contains undefined attribute \"" + rule.getTrackedEntityIdentifierReference() + "\"." ) ).getAttributeId();
            trackedEntityInstance.getAttribute( attributeId ).setValue( identifier );
        }

        if ( !transform( context, rule, variables ) )
        {
            return null;
        }
        trackedEntityService.updateGeneratedValues( trackedEntityInstance, trackedEntityType,
            Collections.singletonMap( RequiredValueType.ORG_UNIT_CODE, organisationUnit.get().getCode() ) );
        scriptedTrackedEntityInstance.validate();

        return new FhirToDhisTransformOutcome<>( trackedEntityInstance.getId(), trackedEntityInstance );
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> arguments, @Nonnull TrackedEntityRule rule ) throws TransformerException
    {
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.getType( rule.getTrackedEntityReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type in rule " + rule + " could not be found: " + rule.getTrackedEntityReference() ) );
        arguments.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );

        // is applicable for further processing
        return true;
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getResourceById( @Nullable String id ) throws TransformerException
    {
        return (id == null) ? Optional.empty() : trackedEntityService.getById( id );
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getResourceByIdentifier(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final String identifier = getIdentifier( context, rule, scriptVariables );
        if ( identifier == null )
        {
            return Optional.empty();
        }

        final TrackedEntityType trackedEntityType = getTrackedEntityType( scriptVariables );
        final TrackedEntityTypeAttribute identifierAttribute = trackedEntityType.getOptionalTypeAttribute( rule.getTrackedEntityIdentifierReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type \"" + rule.getTrackedEntityReference() +
                "\" does not include identifier attribute \"" + rule.getTrackedEntityIdentifierReference() + "\"." ) );

        final Collection<TrackedEntityInstance> result = trackedEntityService.findByAttrValue(
            trackedEntityType.getId(), identifierAttribute.getAttributeId(), Objects.requireNonNull( identifier ), 2 );
        if ( result.size() > 1 )
        {
            throw new TransformerMappingException( "Filtering with identifier of rule " + rule +
                " returned more than one tracked entity instance: " + identifier );
        }
        return result.stream().findFirst();
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getActiveResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected TrackedEntityInstance createResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return new TrackedEntityInstance( getTrackedEntityType( scriptVariables ).getId(), id, true );
    }

    @Nullable
    protected String getIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables )
    {
        final IBaseResource baseResource = getInput( scriptVariables );
        if ( !(baseResource instanceof IDomainResource) )
        {
            throw new TransformerDataException( "Resource " + context.getFhirRequest().getResourceType() +
                " is no domain resource and does not include the required identifier." );
        }

        final FhirResourceType resourceType = context.getFhirRequest().getResourceType();
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( resourceType )
            .orElseThrow( () -> new TransformerDataException( "No system has been defined for resource type " + resourceType + "." ) );

        final String identifier = getIdentifierUtils( scriptVariables ).getResourceIdentifier(
            (IDomainResource) baseResource, context.getFhirRequest().getResourceType(), resourceSystem.getSystem() );
        if ( identifier == null )
        {
            logger.info( "Resource " + context.getFhirRequest().getResourceType() + " does not include the required identifier with system: " +
                resourceSystem.getSystem() );
            return null;
        }

        if ( rule.isTrackedEntityIdentifierFq() )
        {
            throw new TransformerDataException( "Tracked entity rule " + rule.getId() + " requires full qualified identifiers. " +
                "Full qualified identifiers are currently not supported." );
        }
        return identifier;
    }

    @Nonnull
    protected Optional<OrganisationUnit> getOrganizationUnit( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables )
    {
        final Reference organizationUnitReference = getScriptExecutor().execute( rule.getOrgUnitLookupScript(), context.getFhirRequest().getVersion(), scriptVariables, Reference.class );
        if ( organizationUnitReference == null )
        {
            logger.info( "Could not extract organization unit reference." );
            return Optional.empty();
        }

        final Optional<OrganisationUnit> organisationUnit = organisationUnitService.get( organizationUnitReference );
        if ( !organisationUnit.isPresent() )
        {
            logger.info( "Organization unit of extracted reference does not exist: " + organizationUnitReference );
        }
        return organisationUnit;
    }

    @Nonnull
    protected TrackedEntityType getTrackedEntityType( @Nonnull Map<String, Object> scriptVariables ) throws FatalTransformerException
    {
        final TrackedEntityType trackedEntityType = (TrackedEntityType) scriptVariables.get( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName() );
        if ( trackedEntityType == null )
        {
            throw new FatalTransformerException( "Tracked entity type is not included as script variables." );
        }
        return trackedEntityType;
    }
}

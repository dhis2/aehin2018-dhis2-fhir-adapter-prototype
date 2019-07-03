package org.dhis2.fhir.adapter.dhis.sync.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link DhisResourceRepository}.
 *
 * @author volsch
 * @author Charles Chigoriwa (ITINORDIC)
 */
@Component
public class DhisResourceRepositoryImpl implements DhisResourceRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final OrganizationUnitService organizationUnitService;

    private final TrackedEntityService trackedEntityService;

    private final EnrollmentService enrollmentService;

    private final EventService eventService;

    public DhisResourceRepositoryImpl( @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull TrackedEntityService trackedEntityService, @Nonnull EnrollmentService enrollmentService, @Nonnull EventService eventService )
    {
        this.organizationUnitService = organizationUnitService;
        this.trackedEntityService = trackedEntityService;
        this.enrollmentService = enrollmentService;
        this.eventService = eventService;
    }

    @Nonnull
    @Override
    public Optional<? extends DhisResource> findRefreshed( @Nonnull DhisResourceId dhisResourceId )
    {
        switch ( dhisResourceId.getType() )
        {
            case ORGANIZATION_UNIT:
                return organizationUnitService.findOneByReference( new Reference( dhisResourceId.getId(), ReferenceType.ID ) );
            case TRACKED_ENTITY:
                return trackedEntityService.findOneByIdRefreshed( dhisResourceId.getId() );
            case PROGRAM_STAGE_EVENT:
                return eventService.findOneById( dhisResourceId.getId() );
            case ENROLLMENT:
                return enrollmentService.findOneById( dhisResourceId.getId() );
            default:
                throw new AssertionError( "Unhandled DHIS resource type: " + dhisResourceId.getType() );
        }
    }

    @Nonnull
    @Override
    public Optional<? extends DhisResource> findRefreshedDeleted( @Nonnull DhisResourceId dhisResourceId )
    {
        switch ( dhisResourceId.getType() )
        {
            case PROGRAM_STAGE_EVENT:
                return eventService.findOneDeletedById( dhisResourceId.getId() );
            case ORGANIZATION_UNIT:
            case TRACKED_ENTITY:
            case ENROLLMENT:
                throw new UnsupportedOperationException( "Retrieving deleted " + dhisResourceId.getType() + " DHIS resource items is not supported." );
            default:
                throw new AssertionError( "Unhandled DHIS resource type: " + dhisResourceId.getType() );
        }
    }

    @Nonnull
    @Override
    public Optional<? extends DhisResource> find( @Nonnull DhisResourceId dhisResourceId )
    {
        // caching is not supported currently
        return findRefreshed( dhisResourceId );
    }

    @Override
    @Nonnull
    public DhisResource save( @Nonnull DhisResource resource )
    {
        switch ( resource.getResourceType() )
        {
            case TRACKED_ENTITY:
                saveTrackedEntityInstance( (TrackedEntityInstance) resource );
                break;
            case ENROLLMENT:
                saveEnrollment( (Enrollment) resource );
                break;
            case PROGRAM_STAGE_EVENT:
                saveEvent( (Event) resource );
                break;
            default:
                throw new AssertionError( "Unhandled DHIS resource type: " + resource.getResourceType() );
        }
        return resource;
    }

    @Override
    public boolean delete( @Nonnull DhisResource resource )
    {
        switch ( resource.getResourceType() )
        {
            case TRACKED_ENTITY:
                return trackedEntityService.delete( resource.getId() );
            case ENROLLMENT:
                return enrollmentService.delete( resource.getId() );
            case PROGRAM_STAGE_EVENT:
                return eventService.delete( resource.getId() );
            default:
                throw new AssertionError( "Unhandled DHIS resource type: " + resource.getResourceType() );
        }
    }

    private boolean saveTrackedEntityInstance( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        if ( trackedEntityInstance.isNewResource() || trackedEntityInstance.isModified() )
        {
            logger.debug( "Persisting tracked entity instance." );
            final DhisResource dhisResource = trackedEntityService.createOrUpdate( trackedEntityInstance );
            logger.info( "Persisted tracked entity instance {}.", dhisResource.getId() );
            return true;
        }

        logger.info( "Tracked entity instance {} has not been modified.", trackedEntityInstance.getId() );
        return false;
    }

    private boolean saveEvent( @Nonnull Event event )
    {
        boolean updated = false;

        if ( (event.getTrackedEntityInstance() != null) && event.getTrackedEntityInstance().isModified() )
        {
            logger.debug( "Persisting tracked entity instance." );
            trackedEntityService.createOrUpdate( event.getTrackedEntityInstance() );
            logger.info( "Persisted tracked entity instance {}.", event.getTrackedEntityInstance().getId() );
            updated = true;
        }

        if ( event.getEnrollment().isNewResource() )
        {
            logger.info( "Creating new enrollment." );
            event.getEnrollment().setEvents( Collections.singletonList( event ) );
            enrollmentService.createOrUpdate( event.getEnrollment() );
            logger.info( "Created new enrollment {} with new event {}.", event.getEnrollment().getId(), event.getId() );
            updated = true;
        }
        else
        {
            final List<Event> events = event.getEnrollment().getEvents();
            if ( event.getEnrollment().isModified() )
            {
                logger.info( "Updating existing enrollment." );
                event.setEnrollment( enrollmentService.createOrUpdate( event.getEnrollment() ) );
                logger.info( "Updated existing enrollment {}.", event.getEnrollment().getId() );
                updated = true;
            }

            for ( final Event e : events )
            {
                if ( e.isModified() || e.isAnyDataValueModified() )
                {
                    logger.debug( "Persisting event." );
                    final DhisResource dhisResource = eventService.createOrMinimalUpdate( event );
                    logger.info( "Persisted event {}.", dhisResource.getId() );
                    updated = true;
                }
            }
        }

        return updated;
    }

    private boolean saveEnrollment( @Nonnull Enrollment enrollment )
    {
        boolean updated = false;

        if ( enrollment.getTrackedEntityInstance() != null && enrollment.getTrackedEntityInstance().isModified() )
        {
            logger.debug( "Persisting enrollment." );
            trackedEntityService.createOrUpdate( enrollment.getTrackedEntityInstance() );
            logger.info( "Persisted enrollment {}.", enrollment.getTrackedEntityInstance().getId() );
            updated = true;
        }

        if ( enrollment.isNewResource() )
        {
            logger.info( "Creating new enrollment." );
            enrollmentService.createOrUpdate( enrollment );
            logger.info( "Created new enrollment {}.", enrollment.getId() );
            updated = true;
        }
        else if ( enrollment.isModified() )
        {
            logger.info( "Updating existing enrollment." );
            enrollmentService.createOrUpdate( enrollment );
            logger.info( "Updated existing enrollment {}.", enrollment.getId() );
            updated = true;
        }


        return updated;
    }
}

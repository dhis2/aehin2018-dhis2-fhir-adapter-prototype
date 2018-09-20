package org.dhis2.fhir.adapter.fhir.transform.scripted.program;

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

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.util.CastUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Scriptable
public class WritableScriptedEnrollment implements ScriptedEnrollment, Serializable
{
    private static final long serialVersionUID = -9043373621936561310L;

    private final Enrollment enrollment;

    private final ValueConverter valueConverter;

    public WritableScriptedEnrollment( @Nonnull Enrollment enrollment, @Nonnull ValueConverter valueConverter )
    {
        this.enrollment = enrollment;
        this.valueConverter = valueConverter;
    }

    @Override
    public boolean isNewResource()
    {
        return enrollment.isNewResource();
    }

    @Nullable
    @Override
    public String getId()
    {
        return enrollment.getId();
    }

    @Nullable
    @Override
    public String getOrganizationUnitId()
    {
        return enrollment.getOrgUnitId();
    }

    public void setOrganizationUnitId( String organizationUnitId )
    {
        enrollment.setOrgUnitId( organizationUnitId );
    }

    @Nullable
    @Override
    public ZonedDateTime getEnrollmentDate()
    {
        return enrollment.getEnrollmentDate();
    }

    public void setEnrollmentDate( ZonedDateTime enrollmentDate )
    {
        enrollment.setEnrollmentDate( enrollmentDate );
    }

    public void setEnrollmentDate( Object enrollmentDate )
    {
        enrollment.setEnrollmentDate( CastUtils.cast( enrollmentDate, ZonedDateTime.class, ed -> ed, Object.class, ed -> valueConverter.convert( ed, ValueType.DATETIME, ZonedDateTime.class ) ) );
    }

    @Nullable
    @Override
    public ZonedDateTime getIncidentDate()
    {
        return enrollment.getIncidentDate();
    }

    public void setIncidentDate( Object incidentDate )
    {
        enrollment.setIncidentDate( CastUtils.cast( incidentDate, ZonedDateTime.class, id -> id, Object.class, id -> valueConverter.convert( id, ValueType.DATETIME, ZonedDateTime.class ) ) );
    }

    @Override
    public void validate() throws TransformerException
    {
        if ( enrollment.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of enrollment has not been specified." );
        }
        if ( enrollment.getEnrollmentDate() == null )
        {
            throw new TransformerMappingException( "MappedEnrollment date of enrollment has not been specified." );
        }
        if ( enrollment.getIncidentDate() == null )
        {
            throw new TransformerMappingException( "Incident date of enrollment has not been specified." );
        }
    }
}

package org.dhis2.fhir.adapter.dhis.tracker.program;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.model.Id;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WritableProgram implements Program, Serializable
{
    private static final long serialVersionUID = -4906529875383953995L;

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    private String name;

    private String code;

    @JsonProperty
    private Id trackedEntityType;

    @JsonProperty( "programStages" )
    private List<WritableProgramStage> stages;

    private transient volatile Map<String, ProgramStage> stagesByName;

    private transient volatile Map<String, ProgramStage> stagesByCode;

    private transient volatile Map<String, ProgramStage> stagesById;

    @Override
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @JsonIgnore
    @Override
    public String getTrackedEntityTypeId()
    {
        return (trackedEntityType == null) ? null : trackedEntityType.getId();
    }

    public void setTrackedEntityTypeId( String trackedEntityTypeId )
    {
        this.trackedEntityType = (trackedEntityTypeId == null) ? null : new Id( trackedEntityTypeId );
    }

    @Override
    public List<WritableProgramStage> getStages()
    {
        return stages;
    }

    public void setStages( List<WritableProgramStage> stages )
    {
        this.stages = stages;
    }

    @Nonnull
    public Optional<ProgramStage> getOptionalStage( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case NAME:
                return Optional.ofNullable( getStageByName( reference.getValue() ) );
            case CODE:
                return Optional.ofNullable( getStageByCode( reference.getValue() ) );
            case ID:
                return Optional.ofNullable( getStageById( reference.getValue() ) );
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }

    @Nullable
    public ProgramStage getStageByName( @Nonnull String name )
    {
        Map<String, ProgramStage> stagesByName = this.stagesByName;
        if ( stagesByName == null )
        {
            this.stagesByName = stagesByName = stages.stream().collect( Collectors.toMap( WritableProgramStage::getName, s -> s ) );
        }
        return stagesByName.get( name );
    }

    @Nullable
    public ProgramStage getStageByCode( @Nonnull String name )
    {
        Map<String, ProgramStage> stagesByCode = this.stagesByCode;
        if ( stagesByCode == null )
        {
            this.stagesByCode = stagesByCode = stages.stream().collect( Collectors.toMap( WritableProgramStage::getCode, s -> s ) );
        }
        return stagesByCode.get( name );
    }

    @Nullable
    public ProgramStage getStageById( @Nonnull String id )
    {
        Map<String, ProgramStage> stagesById = this.stagesById;
        if ( stagesById == null )
        {
            this.stagesById = stagesById = stages.stream().collect( Collectors.toMap( WritableProgramStage::getId, s -> s ) );
        }
        return stagesById.get( id );
    }

}
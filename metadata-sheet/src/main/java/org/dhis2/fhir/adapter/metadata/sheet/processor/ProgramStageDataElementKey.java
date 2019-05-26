package org.dhis2.fhir.adapter.metadata.sheet.processor;

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

import org.dhis2.fhir.adapter.dhis.model.DataElement;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Key combination of a program stage and a single data element.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
final class ProgramStageDataElementKey implements Serializable
{
    private static final long serialVersionUID = 7613695189701608811L;

    private final ProgramStage programStage;

    private final DataElement dataElement;

    public ProgramStageDataElementKey( @Nonnull ProgramStage programStage, @Nonnull DataElement dataElement )
    {
        this.programStage = programStage;
        this.dataElement = dataElement;
    }

    @Nonnull
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    @Nonnull
    public DataElement getDataElement()
    {
        return dataElement;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ProgramStageDataElementKey that = (ProgramStageDataElementKey) o;

        return programStage.getId().equals( that.programStage.getId() ) && dataElement.getId().equals( that.dataElement.getId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( programStage.getId(), dataElement.getId() );
    }
}

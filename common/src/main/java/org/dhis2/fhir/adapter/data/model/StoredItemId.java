package org.dhis2.fhir.adapter.data.model;

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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the reference (as an ID) of an item that has already been
 * stored recently and must not be processed again currently
 * (e.g. avoid endless synchronizations). The included
 * {@linkplain #getStoredId() stored ID}  must have the same format like
 * the correponding {@linkplain ProcessedItemId processed item ID}.
 *
 * @param <G> the concrete type of the group of the ID.
 * @author volsch
 */
@MappedSuperclass
public abstract class StoredItemId<G extends DataGroup> implements Serializable
{
    private static final long serialVersionUID = -2744716962486660280L;

    private String storedId;

    public StoredItemId()
    {
        super();
    }

    public StoredItemId( @Nonnull String storedId )
    {
        this.storedId = storedId;
    }

    @Transient
    public abstract G getGroup();

    public abstract void setGroup( G group );

    @Column( name = "stored_id", nullable = false )
    public String getStoredId()
    {
        return storedId;
    }

    public void setStoredId( String storedId )
    {
        this.storedId = storedId;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        StoredItemId that = (StoredItemId) o;
        return Objects.equals( storedId, that.storedId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( storedId );
    }
}

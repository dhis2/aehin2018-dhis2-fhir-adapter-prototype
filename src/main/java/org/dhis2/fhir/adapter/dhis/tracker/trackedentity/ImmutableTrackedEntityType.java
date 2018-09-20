package org.dhis2.fhir.adapter.dhis.tracker.trackedentity;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImmutableTrackedEntityType implements TrackedEntityType, Serializable
{
    private static final long serialVersionUID = 797154293863611491L;

    private final TrackedEntityType delegate;

    public ImmutableTrackedEntityType( @Nonnull TrackedEntityType delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public String getId()
    {
        return delegate.getId();
    }

    @Override
    public String getName()
    {
        return delegate.getName();
    }

    @Override
    public List<TrackedEntityTypeAttribute> getAttributes()
    {
        return (delegate.getAttributes() == null) ? null : delegate.getAttributes().stream().map( ImmutableTrackedEntityTypeAttribute::new ).collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public Optional<? extends TrackedEntityTypeAttribute> getOptionalTypeAttribute( @Nonnull Reference reference )
    {
        return delegate.getOptionalTypeAttribute( reference );
    }

    @Nonnull
    @Override
    public Optional<TrackedEntityTypeAttribute> getOptionalTypeAttributeByCode( @Nonnull String code )
    {
        return delegate.getOptionalTypeAttributeByCode( code ).map( ImmutableTrackedEntityTypeAttribute::new );
    }

    @Nullable
    @Override
    public TrackedEntityTypeAttribute getTypeAttributeByCode( @Nonnull String code )
    {
        return getOptionalTypeAttributeByCode( code ).orElse( null );
    }

    @Nonnull
    @Override
    public Optional<TrackedEntityTypeAttribute> getOptionalTypeAttributeByName( @Nonnull String name )
    {
        return delegate.getOptionalTypeAttributeByName( name ).map( ImmutableTrackedEntityTypeAttribute::new );
    }

    @Nullable
    @Override
    public TrackedEntityTypeAttribute getTypeAttributeByName( @Nonnull String name )
    {
        return getOptionalTypeAttributeByName( name ).orElse( null );
    }
}

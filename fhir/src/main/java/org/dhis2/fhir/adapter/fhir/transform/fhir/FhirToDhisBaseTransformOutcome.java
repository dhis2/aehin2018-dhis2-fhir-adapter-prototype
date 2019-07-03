package org.dhis2.fhir.adapter.fhir.transform.fhir;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * The base outcome of the transformation between a FHIR resource and a DHIS 2 resource.
 *
 * @param <R> the concrete type of the returned DHIS 2 resource.
 * @author volsch
 */
public class FhirToDhisBaseTransformOutcome<R extends DhisResource> implements Serializable
{
    private static final long serialVersionUID = -1022009827965716982L;

    private final AbstractRule rule;

    private final R resource;

    private final boolean created;

    public FhirToDhisBaseTransformOutcome( @Nonnull AbstractRule rule, @Nonnull R resource, boolean created )
    {
        this.rule = rule;
        this.resource = resource;
        this.created = created;
    }

    @Nonnull
    public AbstractRule getRule()
    {
        return rule;
    }

    @Nonnull
    public R getResource()
    {
        return resource;
    }

    public boolean isCreated()
    {
        return created;
    }
}

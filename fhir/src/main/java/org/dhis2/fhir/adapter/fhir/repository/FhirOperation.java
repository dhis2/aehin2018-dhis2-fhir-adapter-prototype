package org.dhis2.fhir.adapter.fhir.repository;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URI;

/**
 * An operation on a FHIR resource.
 *
 * @author volsch
 */
public class FhirOperation implements Serializable
{
    private static final long serialVersionUID = -983303057790883471L;

    private final FhirOperationType operationType;

    private final FhirResourceType fhirResourceType;

    private final String resourceId;

    private final IBaseResource resource;

    private final URI uri;

    private final FhirOperationResult result = new FhirOperationResult();

    public FhirOperation( @Nonnull FhirOperationType operationType, @Nullable FhirResourceType fhirResourceType, @Nullable String resourceId, @Nullable IBaseResource resource, @Nullable URI uri )
    {
        this.operationType = operationType;
        this.fhirResourceType = fhirResourceType;
        this.resourceId = resourceId;
        this.resource = resource;
        this.uri = uri;
    }

    @Nonnull
    public FhirOperationType getOperationType()
    {
        return operationType;
    }

    public boolean isProcessed()
    {
        return result.getStatusCode() != FhirOperationResult.UNDEFINED_STATUS_CODE;
    }

    public FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
    }

    public String getResourceId()
    {
        return resourceId;
    }

    public IBaseResource getResource()
    {
        return resource;
    }

    public URI getUri()
    {
        return uri;
    }

    @Nonnull
    public FhirOperationResult getResult()
    {
        return result;
    }
}

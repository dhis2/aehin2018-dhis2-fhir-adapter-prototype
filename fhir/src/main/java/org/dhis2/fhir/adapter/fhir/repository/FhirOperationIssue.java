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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Contains information about an issue that occured during processing of a
 * FHIR operation.
 *
 * @author volsch
 */
public class FhirOperationIssue implements Serializable
{
    private static final long serialVersionUID = -2950621257399928582L;

    private final FhirOperationIssueSeverity severity;

    private final FhirOperationIssueType type;

    private final String diagnostics;

    public FhirOperationIssue( @Nonnull FhirOperationIssueSeverity severity, @Nonnull FhirOperationIssueType type, @Nullable String diagnostics )
    {
        this.severity = severity;
        this.type = type;
        this.diagnostics = diagnostics;
    }

    @Nonnull
    public FhirOperationIssueSeverity getSeverity()
    {
        return severity;
    }

    @Nonnull
    public FhirOperationIssueType getType()
    {
        return type;
    }

    @Nullable
    public String getDiagnostics()
    {
        return diagnostics;
    }

    @Override
    public String toString()
    {
        return "FhirOperationIssue{severity=" + severity + ", type=" + type + ", diagnostics='" + diagnostics + "\'}";
    }
}

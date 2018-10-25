package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Defines the supported FHIR resource types. For every FHIR Resource type the name of
 * the simple name of the HAPI FHIR class that implements this FHIR Resource type can
 * be specified. This enabled version independent lookups. Since HAPI FHIR Resource
 * class maty have been extended, also the super class hierarchy of that class is
 * checked.
 *
 * @author volsch
 */
public enum FhirResourceType
{
    DIAGNOSTIC_REPORT( "DiagnosticReport", "DiagnosticReport" ),
    IMMUNIZATION( "Immunization", "Immunization" ),
    LOCATION( "Location", "Location" ),
    MEDICATION_REQUEST( "MedicationRequest", "MedicationRequest" ),
    OBSERVATION( "Observation", "Observation" ),
    ORGANIZATION( "Organization", "Organization" ),
    PATIENT( "Patient", "Patient" );

    private static final Map<String, FhirResourceType> resourcesBySimpleClassName = Arrays.stream( values() ).flatMap( v -> v.getSimpleClassNames().stream().map( scn -> new SimpleEntry<>( scn, v ) ) )
        .collect( Collectors.toMap( SimpleEntry::getKey, SimpleEntry::getValue ) );

    @Nullable
    public static FhirResourceType getByResource( @Nullable IBaseResource resource )
    {
        if ( resource == null )
        {
            return null;
        }
        FhirResourceType frt;
        Class<?> c = resource.getClass();
        do
        {
            frt = resourcesBySimpleClassName.get( c.getSimpleName() );
            if ( frt == null )
            {
                c = c.getSuperclass();
            }
        }
        while ( (frt == null) && (c != null) && (c != Object.class) );
        return frt;
    }

    private final String resourceTypeName;

    private final Set<String> simpleClassNames;

    FhirResourceType( String resourceTypeName, String... simpleClassNames )
    {
        this.resourceTypeName = resourceTypeName;
        this.simpleClassNames = Collections.unmodifiableSet( new HashSet<>( Arrays.asList( simpleClassNames ) ) );
    }

    @Nonnull
    public String getResourceTypeName()
    {
        return resourceTypeName;
    }

    @Nonnull
    public Set<String> getSimpleClassNames()
    {
        return simpleClassNames;
    }
}

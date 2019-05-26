package org.dhis2.fhir.adapter.util;

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

import com.google.common.base.CaseFormat;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Name utilities for converting to enum constants and class names.
 *
 * @author volsch
 */
public abstract class NameUtils
{
    @Nullable
    public static String toClassName( @Nullable Object value )
    {
        final String stringValue = toEnumName( value );
        if ( stringValue == null )
        {
            return null;
        }
        return CaseFormat.UPPER_UNDERSCORE.to( CaseFormat.UPPER_CAMEL, stringValue );
    }

    @Nonnull
    public static <T extends Enum<T>> T toEnumValue( @Nonnull Class<T> enumClass, @Nullable Object value ) throws IllegalArgumentException
    {
        final String stringValue = toEnumName( value );
        if ( stringValue == null )
        {
            throw new IllegalArgumentException( "Null enum values are not allowed" );
        }
        return Enum.valueOf( enumClass, stringValue );
    }

    @Nullable
    public static <T extends Enum<T>> T toEnumValue( @Nonnull Class<T> enumClass, @Nullable String value ) throws IllegalArgumentException
    {
        if ( value == null )
        {
            return null;
        }
        return Enum.valueOf( enumClass, Objects.requireNonNull( toEnumName( value ) ) );
    }

    @Nullable
    public static String toEnumName( @Nullable Object value )
    {
        if ( value == null )
        {
            return null;
        }
        final String stringValue = value.toString();
        if ( stringValue == null )
        {
            return null;
        }
        if ( stringValue.length() == 0 )
        {
            return stringValue;
        }
        if ( containsNoLowerCase( stringValue ) )
        {
            return stringValue.replace( '-', '_' );
        }

        return Arrays.stream( StringUtils.splitByCharacterTypeCamelCase( stringValue.replace( '-', '_' ) ) )
            .filter( v -> !v.equals( "_" ) ).collect( Collectors.joining( "_" ) ).toUpperCase();
    }

    private static boolean containsNoLowerCase( @Nonnull String value )
    {
        final int length = value.length();

        for ( int i = 0; i < length; i++ )
        {
            if ( Character.isLowerCase( value.charAt( i ) ) )
            {
                return false;
            }
        }

        return true;
    }

    private NameUtils()
    {
        super();
    }
}

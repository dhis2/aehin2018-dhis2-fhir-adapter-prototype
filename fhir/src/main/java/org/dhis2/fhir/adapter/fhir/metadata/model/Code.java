package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import com.fasterxml.jackson.annotation.JsonFilter;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.jackson.JsonCacheIgnore;
import org.dhis2.fhir.adapter.jackson.RestIgnore;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * FHIR allows to use code definitions of one or more systems. This code defines a unique code for the codes that are
 * defined in a different way on other systems. The code must be unique und should be prefixed with its domain.
 * <br><br>
 * E.g. for the vaccine BCG a code <code>VACCINE_BCG</code> can be defined that is referenced by rules and
 * transformations. This code maps then to several system specific vaccine codes. If another system with a different
 * code is added to the adapter, rules and transformations need not to be changed normally.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_code" )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class Code extends VersionedBaseMetadata implements CodeCategoryAware, Serializable
{
    private static final long serialVersionUID = 6376382638316116368L;

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 50;

    public static final int MAX_MAPPED_CODE_LENGTH = 50;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    @NotBlank
    @Size( max = MAX_CODE_LENGTH )
    private String code;

    @Size( max = MAX_MAPPED_CODE_LENGTH )
    private String mappedCode;

    private String description;

    private boolean enabled = true;

    @NotNull
    private CodeCategory codeCategory;

    private List<SystemCode> systemCodes;

    @Basic
    @Column( name = "name", nullable = false, length = 230 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "code", nullable = false, length = 50, unique = true )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "mapped_code", length = MAX_MAPPED_CODE_LENGTH )
    public String getMappedCode()
    {
        return mappedCode;
    }

    public void setMappedCode( String mappedCode )
    {
        this.mappedCode = mappedCode;
    }

    @Basic
    @Column( name = "description", columnDefinition = "TEXT" )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic
    @Column( name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL" )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "code_category_id", referencedColumnName = "id", nullable = false )
    public CodeCategory getCodeCategory()
    {
        return codeCategory;
    }

    public void setCodeCategory( CodeCategory codeCategory )
    {
        this.codeCategory = codeCategory;
    }

    @RestIgnore
    @RestResource( exported = false )
    @JsonCacheIgnore
    @OneToMany( mappedBy = "code" )
    @OrderBy( "id" )
    @BatchSize( size = 100 )
    public List<SystemCode> getSystemCodes()
    {
        return systemCodes;
    }

    public void setSystemCodes( List<SystemCode> systemCodes )
    {
        this.systemCodes = systemCodes;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Code that = (Code) o;
        return Objects.equals( getId(), that.getId() ) && Objects.equals( name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), name );
    }
}

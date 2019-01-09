/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.ampt2d.registry.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@EntityListeners({AuditingEntityListener.class, EntityEventListener.class})
public class Property implements IdentifiableEntity<String> {

    public enum Type {

        BOOLEAN,

        INTEGER,

        FLOAT,

        DOUBLE,

        STRING

    }

    public enum Meaning {

        NONE,

        CALL_RATE,

        P_VALUE,

        MAF,

        MAC

    }

    @ApiModelProperty(position = 1)
    @JsonProperty
    @NotNull
    @Size(min = 1, max = 255)
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private String id;

    @ApiModelProperty(position = 2)
    @JsonProperty
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @ApiModelProperty(position = 3)
    @JsonProperty
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Meaning meaning;

    @ApiModelProperty(position = 4)
    @JsonProperty
    @NotNull
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(updatable = false)
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime lastModifiedDate;

    public String getId() {
        return id;
    }
}
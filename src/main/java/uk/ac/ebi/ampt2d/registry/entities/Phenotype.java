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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@EntityListeners({AuditingEntityListener.class, EntityEventListener.class})
public class Phenotype implements IdentifiableEntity<String> {

    public enum Group {
        ANTHROPOMETRIC,
        CARDIOVASCULAR,
        GLYCEMIC,
        LIPIDS,
        RENAL,
        HEPATIC
    }

    public enum Type {
        DICHOTOMOUS,
        MULTICHOTOMOUS,
        CONTINUOUS
    }

    @JsonProperty
    @NotNull
    @Size(min = 1, max = 255)
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private String id;

    @JsonProperty
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Group phenotypeGroup;

    @NotNull
    @NotBlank
    @JsonProperty
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @JsonProperty
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @JsonProperty
    @NotNull
    @Column(nullable = false)
    private String allowedValues;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public Phenotype() {
    }

    public Phenotype(String id, Group phenotypeGroup, String description, Type type, String allowedValues) {
        this.id = id;
        this.phenotypeGroup = phenotypeGroup;
        this.description = description;
        this.type = type;
        this.allowedValues = allowedValues;
    }

    public String getId() {
        return id;
    }
}

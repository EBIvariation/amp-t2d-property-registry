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
package uk.ac.ebi.ampt2d.registry.config;
/*
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.WildcardType;
import java.util.Arrays;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
@Import({SpringDataRestConfiguration.class, BeanValidatorPluginsConfiguration.class})
public class SwaggerConfiguration {

    @Autowired
    private TypeResolver typeResolver;

    private SecurityReference securityReference = SecurityReference.builder()
            .reference("Authorization").scopes(new AuthorizationScope[0]).build();

    private SecurityContext securityContext = SecurityContext.builder()
            .securityReferences(Arrays.asList(securityReference)).build();

    @Bean
    public Docket propertyRegistryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getScanRestServicesPathPredicate())
                .build()
                .apiInfo(getApiInfo())
                .pathMapping("/")
                .tags(
                        new Tag("Property Entity", "Property definition"),
                        new Tag("Phenotype Entity", "Phenotype definition")
                )
                .genericModelSubstitutes(ResponseEntity.class)
                .securitySchemes(Arrays.asList(new ApiKey("Authorization", "Authorization", "header")))
                .securityContexts(Arrays.asList(securityContext))
                .alternateTypeRules(getSubstitutionRules());

    }

    private Predicate<String> getScanRestServicesPathPredicate() {
        return Predicates.and(
                Predicates.not(PathSelectors.regex("/actuator.*")), // Hide spring-actuator
                Predicates.not(PathSelectors.regex("/error.*")), // Hide spring-data error
                Predicates.not(PathSelectors.regex("/profile.*")),// Hide spring-data profile
                Predicates.not(PathSelectors.regex("/users.*")) // Hide user-profile
        );
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .contact(new Contact("Europe Phenome Genome Archive - EMBL-EBI", "https://www.ebi.ac.uk/ega/", null))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .termsOfServiceUrl(null)
                .title("AMP T2D Property registry API")
                .description("Property registry API for the project AMP-T2D")
                .version("1.0")
                .build();
    }

    private AlternateTypeRule getSubstitutionRules() {
        return newRule(typeResolver.resolve(DeferredResult.class,
                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                typeResolver.resolve(WildcardType.class));
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayRequestDuration(true)
                .filter(false)
                .validatorUrl("")
                .build();
    }

}
*/
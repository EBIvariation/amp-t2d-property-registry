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
package uk.ac.ebi.ampt2d.registry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.ac.ebi.ampt2d.registry.entities.Phenotype;
import uk.ac.ebi.ampt2d.registry.entities.Property;
import uk.ac.ebi.ampt2d.registry.repositories.PhenotypeRepository;
import uk.ac.ebi.ampt2d.registry.repositories.PropertyRepository;
import uk.ac.ebi.ampt2d.registry.service.mail.MailService;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"security.enabled=true", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext(classMode = AFTER_CLASS)
public class PropertyRegistryServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthHelper oAuthHelper;

    @Autowired
    private PhenotypeRepository phenotypeRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private JacksonTester<Phenotype> phenotypeJacksonTester;

    @Autowired
    private JacksonTester<Property> propertyJacksonTester;

    @MockBean
    private MailService mailService;

    @Before
    public void setUp() throws Exception {
        doNothing().when(mailService).send(anyString());
        phenotypeRepository.deleteAll();
        propertyRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.properties").exists());
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.phenotypes").exists());
    }

    private String postTestEntity(String uri, String content) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(uri).with(oAuthHelper.bearerToken("testEditor@gmail.com")).content
                (content))
                .andExpect(status().isCreated())
                .andReturn();

        return mvcResult.getResponse().getHeader("Location");
    }

    private String postTestPhenotype() throws Exception {
        Phenotype phenotype = new Phenotype("BMI", Phenotype.Group.ANTHROPOMETRIC, "Body Mass Index",
                Phenotype.Type.CONTINUOUS, "nn.nn");
        return postTestEntity("/phenotypes", phenotypeJacksonTester.write(phenotype).getJson());
    }

    @Test
    public void shouldCreatePhenotype() throws Exception {
        String location = postTestPhenotype();

        assert location.contains("/phenotypes/");
    }

    @Test
    public void shouldRetrievePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testEditor@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phenotypeGroup").value("ANTHROPOMETRIC"));
    }

    @Test
    public void shouldQueryPhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(get("/phenotypes/search/findByPhenotypeGroup?phenotypeGroup=ANTHROPOMETRIC")
                .with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..phenotypes").isArray())
                .andExpect(jsonPath("$..phenotypes.length()").value(1))
                .andExpect(jsonPath("$..phenotypes[0]..phenotype.href").value(location));
    }

    @Test
    public void shouldUpdatePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(patch(location).with(oAuthHelper.bearerToken("testEditor@gmail.com"))
                .content("{\"id\":\"BMI\"," + "\"phenotypeGroup\":\"RENAL\"," + "\"allowedValues\":\"nn.nnnn\"}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phenotypeGroup").value("RENAL"))
                .andExpect(jsonPath("$.allowedValues").value("nn.nnnn"));
    }

    @Test
    public void shouldPartiallyUpdatePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(patch(location).with(oAuthHelper.bearerToken("testEditor@gmail.com"))
                .content("{\"phenotypeGroup\":\"RENAL\"}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phenotypeGroup").value("RENAL"));
    }

    @Test
    public void shouldDeletePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(delete(location).with(oAuthHelper.bearerToken("testEditor@gmail.com")))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isNotFound());
    }

    private String postTestProperty() throws Exception {
        Property property = new Property("CALL_RATE", Property.Type.FLOAT, Property.Meaning.CALL_RATE, "calling rate");

        return postTestEntity("/properties", propertyJacksonTester.write(property).getJson());
    }

    @Test
    public void shouldCreateProperty() throws Exception {
        String location = postTestProperty();

        assert location.contains("/properties/");
    }

    @Test
    public void shouldRetrieveProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FLOAT"))
                .andExpect(jsonPath("$.meaning").value("CALL_RATE"))
                .andExpect(jsonPath("$.description").value("calling rate"));
    }

    @Test
    public void shouldQueryProperties() throws Exception {
        postTestProperty();

        mockMvc.perform(
                get("/properties/search/findByType?type={type}", "FLOAT")
                        .with(oAuthHelper.bearerToken("testUser@gmail.com"))).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.properties[0].type").value("FLOAT"))
                .andExpect(jsonPath("$._embedded.properties[0].meaning").value("CALL_RATE"))
                .andExpect(jsonPath("$._embedded.properties[0].description").value("calling rate"));
    }

    @Test
    public void shouldUpdateProperty() throws Exception {
        String location = postTestProperty();

        Property property = new Property("CALL_RATE", Property.Type.DOUBLE, Property.Meaning.CALL_RATE, "calling rate");

        mockMvc.perform(put(location).with(oAuthHelper.bearerToken("testEditor@gmail.com"))
                .content(propertyJacksonTester.write(property).getJson())).andExpect(status().isNoContent());

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DOUBLE"))
                .andExpect(jsonPath("$.meaning").value("CALL_RATE"))
                .andExpect(jsonPath("$.description").value("calling rate"));
    }

    @Test

    public void shouldPartiallyUpdateProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(
                patch(location).with(oAuthHelper.bearerToken("testEditor@gmail.com")).content("{\"type\": " +
                        "\"DOUBLE\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DOUBLE"))
                .andExpect(jsonPath("$.meaning").value("CALL_RATE"))
                .andExpect(jsonPath("$.description").value("calling rate"));
    }

    @Test
    public void shouldDeleteProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(delete(location).with(oAuthHelper.bearerToken("testEditor@gmail.com")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location).with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPaging() throws Exception {

        Property property1 = new Property("CALL_RATE", Property.Type.DOUBLE, Property.Meaning.CALL_RATE, "calling rate");
        Property property2 = new Property("MAF", Property.Type.FLOAT, Property.Meaning.MAF, "MAF");

        postTestEntity("/properties", propertyJacksonTester.write(property1).getJson());

        postTestEntity("/properties", propertyJacksonTester.write(property2).getJson());

        mockMvc.perform(get("/properties?size=1").with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.properties.length()").value(1));
        mockMvc.perform(get("/properties?size=2").with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.properties.length()").value(2));

    }

    @Test
    public void testAuthorization() throws Exception {
        // Any url other than root and swagger is Secured
        mockMvc.perform(get("/phenotypes")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/properties")).andExpect(status().isUnauthorized());

        // AUTH_WHITELIST URLs not secured
        mockMvc.perform(get("/")).andExpect(status().isOk());

        Property property = new Property("CALL_RATE", Property.Type.DOUBLE, Property.Meaning.CALL_RATE, "calling rate");
        Phenotype phenotype1 = new Phenotype("BMI", Phenotype.Group.ANTHROPOMETRIC, "Body Mass Index",
                Phenotype.Type.CONTINUOUS, "nn.nn");
        Phenotype phenotype2 = new Phenotype("BMI", Phenotype.Group.RENAL, "Body Mass Index",
                Phenotype.Type.CONTINUOUS, "nn.nn");

        String propertyContent = propertyJacksonTester.write(property).getJson();
        String phenotypeContent = phenotypeJacksonTester.write(phenotype1).getJson();

        //POST can be performed by EDITOR or ADMIN only
        mockMvc.perform(post("/properties").with(oAuthHelper.bearerToken("testUser@gmail.com"))
                .content(propertyContent)).andExpect(status().isForbidden());
        mockMvc.perform(post("/properties").with(oAuthHelper.bearerToken("testEditor@gmail.com"))
                .content(propertyContent)).andExpect(status().isCreated());
        mockMvc.perform(post("/phenotypes").with(oAuthHelper.bearerToken("testEditor@gmail.com"))
                .content(phenotypeContent)).andExpect(status().isCreated());

        //GET can be performed by any authenticated user
        mockMvc.perform(get("/properties").with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.properties.length()").value(1));
        mockMvc.perform(get("/phenotypes").with(oAuthHelper.bearerToken("testEditor@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.phenotypes.length()").value(1));
        mockMvc.perform(get("/phenotypes/BMI").with(oAuthHelper.bearerToken("testAdmin@gmail.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("BMI"));

        //PUT/PATCH/DELETE can be performed by EDITOR or ADMIN only
        mockMvc.perform(patch("/phenotypes/BMI").with(oAuthHelper.bearerToken("testUser@gmail.com"))
                .content("{\"phenotypeGroup\": \"GLYCEMIC\"}")).andExpect(status().isForbidden());
        mockMvc.perform(patch("/phenotypes/BMI").with(oAuthHelper.bearerToken("testEditor@gmail.com"))
                .content("{\"phenotypeGroup\": \"GLYCEMIC\"}")).andExpect(status().isNoContent());
        mockMvc.perform(put("/phenotypes/BMI").with(oAuthHelper.bearerToken("testAdmin@gmail.com"))
                .content(phenotypeJacksonTester.write(phenotype2).getJson())).andExpect(status().isNoContent());
        mockMvc.perform(delete("/properties/CALL_RATE").with(oAuthHelper.bearerToken("testUser@gmail.com")))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/properties/CALL_RATE").with(oAuthHelper.bearerToken("testEditor@gmail.com")))
                .andExpect(status().isNoContent());

        //Change of Role can be performed by ADMIN only
        mockMvc.perform(put("/users/testUser@gmail.com")
                .content("{\"role\": \"ROLE_EDITOR\"}").with(oAuthHelper.bearerToken("testEditor@gmail.com")))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/users/testUser@gmail.com")
                .content("{\"role\": \"ROLE_EDITOR\"}").with(oAuthHelper.bearerToken("testAdmin@gmail.com")))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/properties").with(oAuthHelper.bearerToken("testUser@gmail.com"))
                .content(propertyContent)).andExpect(status().isCreated());
    }

}

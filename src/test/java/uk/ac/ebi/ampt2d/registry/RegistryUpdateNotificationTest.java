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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ebi.ampt2d.registry.service.mail.MailService;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_CLASS)
public class RegistryUpdateNotificationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailService mailService;

    @Before
    public void setUp() throws Exception {
        doNothing().when(mailService).send(anyString());
    }

    @Test
    public void testPhenotypeEvent() throws Exception {
        String phenotypeContent = "{\"id\":\"BMI\"," + "\"phenotypeGroup\":\"ANTHROPOMETRIC\"}";
        mockMvc.perform(post("/phenotypes").content(phenotypeContent))
                .andExpect(status().isCreated());
        verify(mailService, times(1)).send("Phenotype BMI CREATED");

        mockMvc.perform(patch("/phenotypes/BMI").content("{\"phenotypeGroup\":\"RENAL\"}"))
                .andExpect(status().is2xxSuccessful());
        verify(mailService, times(1)).send("Phenotype BMI UPDATED");

        mockMvc.perform(delete("/phenotypes/BMI"))
                .andExpect(status().isNoContent());
        verify(mailService, times(1)).send("Phenotype BMI REMOVED");
    }

    @Test
    public void testPropertyEvent() throws Exception {
        String propertiesContent = "{\"id\":\"AF\"," +
                "\"type\":\"FLOAT\"," +
                "\"meaning\":\"NONE\"," +
                "\"description\":\"AF\"}";
        mockMvc.perform(post("/properties").content(propertiesContent))
                .andExpect(status().isCreated());
        verify(mailService, times(1)).send("Property AF CREATED");

        mockMvc.perform(patch("/properties/AF").content("{\"description\":\"Allele Freq\"}"))
                .andExpect(status().is2xxSuccessful());
        verify(mailService, times(1)).send("Property AF UPDATED");

        mockMvc.perform(delete("/properties/AF"))
                .andExpect(status().isNoContent());
        verify(mailService, times(1)).send("Property AF REMOVED");
    }

}

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"mail.notify=true", "spring.mail.host=invalid_host"})
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_CLASS)
public class RegistryUpdateNotificationFailure {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPhenotypeEvent() throws Exception {
        String phenotypeContent = "{\"id\":\"BMI\"," + "\"phenotypeGroup\":\"ANTHROPOMETRIC\"}";
        mockMvc.perform(post("/phenotypes").content(phenotypeContent))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An automated email could not be sent, please contact user@domain"));
    }

}

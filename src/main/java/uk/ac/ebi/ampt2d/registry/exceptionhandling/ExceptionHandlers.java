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
package uk.ac.ebi.ampt2d.registry.exceptionhandling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class ExceptionHandlers {

    private static final Logger exceptionLogger = Logger.getLogger(ExceptionHandlers.class.getSimpleName());

    @Value("${mail.from}")
    private String mailFrom;

    @ExceptionHandler(value = TransactionSystemException.class)
    public ResponseEntity<String> handleMailSendException(TransactionSystemException ex) {
        if (ex.getOriginalException().getCause().getClass().equals(MailSendException.class)) {
            exceptionLogger.log(Level.SEVERE, ex.getOriginalException().getCause().getMessage());
            return new ResponseEntity("An automated email could not be sent, please contact " + mailFrom,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        throw ex;
    }
}

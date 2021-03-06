/** 
*    Copyright 2014 BUPT. 
**/ 
/**
*    Copyright 2011,2012 Big Switch Networks, Inc. 
*    Originally created by David Erickson, Stanford University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package com.sds.securitycontroller.core.web.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serialize a short value as an unsigned short
 */
public class UShortSerializer extends JsonSerializer<Short> {

    @Override
    public void serialize(Short s, JsonGenerator jGen,
                          SerializerProvider serializer) throws IOException,
                                                  JsonProcessingException {
        if (s == null) jGen.writeNull();
        else jGen.writeNumber(s.shortValue() & 0xffff);
    }

}

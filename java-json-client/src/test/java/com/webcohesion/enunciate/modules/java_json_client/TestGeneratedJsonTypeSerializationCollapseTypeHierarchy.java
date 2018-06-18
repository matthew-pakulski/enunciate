/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.modules.java_json_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import junit.framework.TestCase;
import com.webcohesion.enunciate.examples.java_json_client.schema.subtypes.FileReport;
import com.webcohesion.enunciate.examples.java_json_client.schema.subtypes.Report;
import com.webcohesion.enunciate.examples.java_json_client.schema.subtypes.TextReport;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.webcohesion.enunciate.examples.java_json_client.schema.*;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class TestGeneratedJsonTypeSerialization extends TestCase {

  /**
   * When collapse-type-hierarchy="false", Report.class is preserved.
   * JsonTypeInfo requires the subtype be a child of the parent type.
   */
  public void testSubtypesExtendParent() throws Exception {
  	FileReport fileReport = new FileReport();
  	TextReport textReport = new TextReport();
  	
  	assertTrue(fileReport instanceof Report.class);
  	assertTrue(textReport instanceof Report.class);
  	
  	assertEquals("FILE", fileReport.getType());
  	assertEquals("TEXT", textReport.getType());
  }
  
  public void testFileReportSubtype() throws Exception {
  	
  	byte[] content = "the content".getBytes();
  	FileReport fileReport = new FileReport();
  	fileReport.setId("someid");
  	fileReport.setMimeType("somemimetype");
  	fileReport.setContent(content);
  	fileReport.setType("FILE"); // Ideally this would be derived instead
  	
    JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
    ObjectMapper fileReportMapper = provider.locateMapper(FileReport.class, MediaType.APPLICATION_JSON_TYPE);
    ObjectMapper clientMapper = new ObjectMapper();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    circleMapper.writeValue(out, fileReport);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

    shapes.json.subtypes.FileReport clientFileReport = clientMapper.readValue(in, shapes.json.subtypes.FileReport.class);
    assertEquals("someid", clientFileReport.getId());
    assertEquals("somemimetype", clientFileReport.getMimeType());
    assertEquals("FILE", clientFileReport.getType());
    assertEquals(content, clientFileReport.getContent());

    out = new ByteArrayOutputStream();
    clientMapper.writeValue(out, clientFileReport);
    in = new ByteArrayInputStream(out.toByteArray());

    fileReport = fileReportMapper.readValue(in, Report.class);
    assertEquals("someid", fileReport.getId());
    assertEquals("somemimetype", fileReport.getMimeType());
    assertEquals("FILE", fileReport.getType());
    assertEquals(content, fileReport.getContent());

  }
  
  public void testTextReportSubtype() throws Exception {
  	
  	String content = "the content";
  	TextReport textReport = new TextReport();
  	textReport.setId("someid");
  	textReport.setContent(content);
  	textReport.setType("TEXT"); // Ideally this would be derived instead
  	
    JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
    ObjectMapper textReportMapper = provider.locateMapper(TextReport.class, MediaType.APPLICATION_JSON_TYPE);
    ObjectMapper clientMapper = new ObjectMapper();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    circleMapper.writeValue(out, textReport);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

    shapes.json.subtypes.TextReport clientTextReport = clientMapper.readValue(in, shapes.json.subtypes.FileReport.class);
    assertEquals("someid", clientTextReport.getId());
    assertEquals("TEXT", clientTextReport.getType());
    assertEquals(content, clientTextReport.getContent());

    out = new ByteArrayOutputStream();
    clientMapper.writeValue(out, clientTextReport);
    in = new ByteArrayInputStream(out.toByteArray());

    textReport = textReportMapper.readValue(in, Report.class);
    assertEquals("someid", textReport.getId());
    assertEquals("TEXT", textReport.getType());
    assertEquals(content, textReport.getContent());

  }
  
}

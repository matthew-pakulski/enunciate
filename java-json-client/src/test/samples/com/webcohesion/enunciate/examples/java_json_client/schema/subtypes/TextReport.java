package com.webcohesion.enunciate.examples.java_json_client.schema.subtypes;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TEXT")
public class TextReport extends Report {

  private String content;
  
  public String getContent() {
  	return content;
  }
  
  public void setContent(String content) {
  	this.content = content;
  }
 
}

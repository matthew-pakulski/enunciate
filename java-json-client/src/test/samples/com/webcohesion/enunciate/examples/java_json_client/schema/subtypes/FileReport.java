package com.webcohesion.enunciate.examples.java_json_client.schema.subtypes;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("FILE")
public class FileReport extends Report {

  private byte[] content;

  private String mimeType;
  
  public byte[] getContent() {
  	return content;
  }
  
  public void setContent(byte[] content) {
  	this.content = content;
  }
  
  public String getMimeType() {
  	return mimeType;
  }
  
  public void setMimeType(String mimeType) {
  	this.mimeType = mimeType;
  }
}

package com.webcohesion.enunciate.examples.java_json_client.schema.subtypes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	property = "type",
	visible = true
)
@JsonSubTypes({
	@Type(value = TextReport.class, name "TEXT"),
	@Type(value = FileReport.class, name "FILE")
})
public abstract class Report {

  private int id;
  
  private String type;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public String getType() {
  	return type;
  }
  
  public void setType(String type) {
  	this.type = type;
  }
}

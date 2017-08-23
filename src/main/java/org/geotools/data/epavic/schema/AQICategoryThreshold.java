package org.geotools.data.epavic.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AQICategoryThreshold {
  @JsonProperty("AQIBackgroundColour")
  private String aQIBackgroundColour;

  @JsonProperty("AQICategoryAbbreviation")
  private String aQICategoryAbbreviation;

  @JsonProperty("AQICategoryDescription")
  private String aQICategoryDescription;

  @JsonProperty("AQIForegroundColour")
  private String aQIForegroundColour;

  public AQICategoryThreshold() {
  }

  public String getaQIBackgroundColour() {
    return aQIBackgroundColour;
  }

  public void setaQIBackgroundColour(String aQIBackgroundColour) {
    this.aQIBackgroundColour = aQIBackgroundColour;
  }

  public String getaQICategoryAbbreviation() {
    return aQICategoryAbbreviation;
  }

  public void setaQICategoryAbbreviation(String aQICategoryAbbreviation) {
    this.aQICategoryAbbreviation = aQICategoryAbbreviation;
  }

  public String getaQICategoryDescription() {
    return aQICategoryDescription;
  }

  public void setaQICategoryDescription(String aQICategoryDescription) {
    this.aQICategoryDescription = aQICategoryDescription;
  }

  public String getaQIForegroundColour() {
    return aQIForegroundColour;
  }

  public void setaQIForegroundColour(String aQIForegroundColour) {
    this.aQIForegroundColour = aQIForegroundColour;
  }
}
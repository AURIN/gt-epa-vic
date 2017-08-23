package org.geotools.data.epavic.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthCategoryThreshold {
  @JsonProperty("HealthCategoryBackgroundColour")
  private String healthCategoryBackgroundColour;

  @JsonProperty("HealthCategoryDescription")
  private String healthCategoryDescription;

  @JsonProperty("HealthCategoryForegroundColour")
  private String healthCategoryForegroundColour;

  @JsonProperty("HealthCategoryLevel")
  private Integer healthCategoryLevel;

  @JsonProperty("HealthCategoryMessage")
  private String healthCategoryMessage;

  @JsonProperty("HealthCategoryValueRangeText")
  private String healthCategoryValueRangeText;

  @JsonProperty("HealthCategoryVisibilityText")
  private String healthCategoryVisibilityText;

  public HealthCategoryThreshold() {
  }

  public String getHealthCategoryBackgroundColour() {
    return healthCategoryBackgroundColour;
  }

  public void setHealthCategoryBackgroundColour(String healthCategoryBackgroundColour) {
    this.healthCategoryBackgroundColour = healthCategoryBackgroundColour;
  }

  public String getHealthCategoryDescription() {
    return healthCategoryDescription;
  }

  public void setHealthCategoryDescription(String healthCategoryDescription) {
    this.healthCategoryDescription = healthCategoryDescription;
  }

  public String getHealthCategoryForegroundColour() {
    return healthCategoryForegroundColour;
  }

  public void setHealthCategoryForegroundColour(String healthCategoryForegroundColour) {
    this.healthCategoryForegroundColour = healthCategoryForegroundColour;
  }

  public Integer getHealthCategoryLevel() {
    return healthCategoryLevel;
  }

  public void setHealthCategoryLevel(Integer healthCategoryLevel) {
    this.healthCategoryLevel = healthCategoryLevel;
  }

  public String getHealthCategoryMessage() {
    return healthCategoryMessage;
  }

  public void setHealthCategoryMessage(String healthCategoryMessage) {
    this.healthCategoryMessage = healthCategoryMessage;
  }

  public String getHealthCategoryValueRangeText() {
    return healthCategoryValueRangeText;
  }

  public void setHealthCategoryValueRangeText(String healthCategoryValueRangeText) {
    this.healthCategoryValueRangeText = healthCategoryValueRangeText;
  }

  public String getHealthCategoryVisibilityText() {
    return healthCategoryVisibilityText;
  }

  public void setHealthCategoryVisibilityText(String healthCategoryVisibilityText) {
    this.healthCategoryVisibilityText = healthCategoryVisibilityText;
  }
}

package org.geotools.data.epavic.schema;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "CommonName", "EPADescriptionURL", "EquipmentType", "MonitorId", "PresentationPrecision",
    "ShortName", "SiteId", "UnitOfMeasure" })
public class Monitor {

  @JsonProperty("CommonName")
  private String commonName;

  @JsonProperty("EPADescriptionURL")
  private String ePADescriptionURL;

  @JsonProperty("EquipmentType")
  private EquipmentType equipmentType;

  @JsonProperty("MonitorId")
  private String monitorId;

  @JsonProperty("PresentationPrecision")
  private Integer presentationPrecision;

  @JsonProperty("ShortName")
  private String shortName;

  @JsonProperty("SiteId")
  private Integer siteId;

  @JsonProperty("UnitOfMeasure")
  private String unitOfMeasure;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("CommonName")
  public String getCommonName() {
    return commonName;
  }

  @JsonProperty("CommonName")
  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  @JsonProperty("EPADescriptionURL")
  public String getEPADescriptionURL() {
    return ePADescriptionURL;
  }

  @JsonProperty("EPADescriptionURL")
  public void setEPADescriptionURL(String ePADescriptionURL) {
    this.ePADescriptionURL = ePADescriptionURL;
  }

  @JsonProperty("EquipmentType")
  public EquipmentType getEquipmentType() {
    return equipmentType;
  }

  @JsonProperty("EquipmentType")
  public void setEquipmentType(EquipmentType equipmentType) {
    this.equipmentType = equipmentType;
  }

  @JsonProperty("MonitorId")
  public String getMonitorId() {
    return monitorId;
  }

  @JsonProperty("MonitorId")
  public void setMonitorId(String monitorId) {
    this.monitorId = monitorId;
  }

  @JsonProperty("PresentationPrecision")
  public Integer getPresentationPrecision() {
    return presentationPrecision;
  }

  @JsonProperty("PresentationPrecision")
  public void setPresentationPrecision(Integer presentationPrecision) {
    this.presentationPrecision = presentationPrecision;
  }

  @JsonProperty("ShortName")
  public String getShortName() {
    return shortName;
  }

  @JsonProperty("ShortName")
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @JsonProperty("SiteId")
  public Integer getSiteId() {
    return siteId;
  }

  @JsonProperty("SiteId")
  public void setSiteId(Integer siteId) {
    this.siteId = siteId;
  }

  @JsonProperty("UnitOfMeasure")
  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }

  @JsonProperty("UnitOfMeasure")
  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(commonName).append(ePADescriptionURL).append(equipmentType).append(monitorId)
        .append(presentationPrecision).append(shortName).append(siteId).append(unitOfMeasure)
        .append(additionalProperties).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Monitor) == false) {
      return false;
    }
    Monitor rhs = ((Monitor) other);
    return new EqualsBuilder().append(commonName, rhs.commonName).append(ePADescriptionURL, rhs.ePADescriptionURL)
        .append(equipmentType, rhs.equipmentType).append(monitorId, rhs.monitorId)
        .append(presentationPrecision, rhs.presentationPrecision).append(shortName, rhs.shortName)
        .append(siteId, rhs.siteId).append(unitOfMeasure, rhs.unitOfMeasure)
        .append(additionalProperties, rhs.additionalProperties).isEquals();
  }

}

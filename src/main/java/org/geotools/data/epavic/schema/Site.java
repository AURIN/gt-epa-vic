
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
@JsonPropertyOrder({ "FireHazardCategory", "HasIncident", "IncidentSite", "IncidentType", "IsStationOffline",
    "Latitude", "Longitude", "Name", "SiteId", "SiteList" })
public class Site {

  @JsonProperty("FireHazardCategory")
  private Integer fireHazardCategory;

  @JsonProperty("HasIncident")
  private Boolean hasIncident;

  @JsonProperty("IncidentSite")
  private IncidentSite incidentSite;

  @JsonProperty("IncidentType")
  private String incidentType;

  @JsonProperty("IsStationOffline")
  private Boolean isStationOffline;

  @JsonProperty("Latitude")
  private Double latitude;

  @JsonProperty("Longitude")
  private Double longitude;

  @JsonProperty("Name")
  private String name;

  @JsonProperty("SiteId")
  private Integer siteId;

  @JsonProperty("SiteList")
  private SiteList siteList;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("FireHazardCategory")
  public Integer getFireHazardCategory() {
    return fireHazardCategory;
  }

  @JsonProperty("FireHazardCategory")
  public void setFireHazardCategory(Integer fireHazardCategory) {
    this.fireHazardCategory = fireHazardCategory;
  }

  @JsonProperty("HasIncident")
  public Boolean getHasIncident() {
    return hasIncident;
  }

  @JsonProperty("HasIncident")
  public void setHasIncident(Boolean hasIncident) {
    this.hasIncident = hasIncident;
  }

  @JsonProperty("IncidentSite")
  public IncidentSite getIncidentSite() {
    return incidentSite;
  }

  @JsonProperty("IncidentSite")
  public void setIncidentSite(IncidentSite incidentSite) {
    this.incidentSite = incidentSite;
  }

  @JsonProperty("IncidentType")
  public String getIncidentType() {
    return incidentType;
  }

  @JsonProperty("IncidentType")
  public void setIncidentType(String incidentType) {
    this.incidentType = incidentType;
  }

  @JsonProperty("IsStationOffline")
  public Boolean getIsStationOffline() {
    return isStationOffline;
  }

  @JsonProperty("IsStationOffline")
  public void setIsStationOffline(Boolean isStationOffline) {
    this.isStationOffline = isStationOffline;
  }

  @JsonProperty("Latitude")
  public Double getLatitude() {
    return latitude;
  }

  @JsonProperty("Latitude")
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  @JsonProperty("Longitude")
  public Double getLongitude() {
    return longitude;
  }

  @JsonProperty("Longitude")
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  @JsonProperty("Name")
  public String getName() {
    return name;
  }

  @JsonProperty("Name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("SiteId")
  public Integer getSiteId() {
    return siteId;
  }

  @JsonProperty("SiteId")
  public void setSiteId(Integer siteId) {
    this.siteId = siteId;
  }

  @JsonProperty("SiteList")
  public SiteList getSiteList() {
    return siteList;
  }

  @JsonProperty("SiteList")
  public void setSiteList(SiteList siteList) {
    this.siteList = siteList;
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
    return new HashCodeBuilder().append(fireHazardCategory).append(hasIncident).append(incidentSite)
        .append(incidentType).append(isStationOffline).append(latitude).append(longitude).append(name).append(siteId)
        .append(siteList).append(additionalProperties).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Site) == false) {
      return false;
    }
    Site rhs = ((Site) other);
    return new EqualsBuilder().append(fireHazardCategory, rhs.fireHazardCategory).append(hasIncident, rhs.hasIncident)
        .append(incidentSite, rhs.incidentSite).append(incidentType, rhs.incidentType)
        .append(isStationOffline, rhs.isStationOffline).append(latitude, rhs.latitude).append(longitude, rhs.longitude)
        .append(name, rhs.name).append(siteId, rhs.siteId).append(siteList, rhs.siteList)
        .append(additionalProperties, rhs.additionalProperties).isEquals();
  }

}

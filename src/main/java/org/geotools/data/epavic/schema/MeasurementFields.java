package org.geotools.data.epavic.schema;

public enum MeasurementFields {

  VALUE(Measurement.VALUE, String.class),
  TIME_BASE_ID(Measurement.TIME_BASE_ID, String.class),
  SITE_ID(Measurement.SITE_ID, Integer.class),
  QUALITY_STATUS(Measurement.QUALITY_STATUS, Integer.class),
  MONITOR_ID(Measurement.MONITOR_ID, String.class),
  MONITOR_NAME(Measurement.MONITOR_NAME, String.class),
  MONITOR_SHORT_NAME(Measurement.MONITOR_SHORT_NAME, String.class),
  MONITOR_TIME_BASIS(Measurement.MONITOR_TIME_BASIS, String.class),
  LONG(Measurement.LONG, Double.class),
  LAT(Measurement.LAT, Double.class),
  IS_STATION_OFFLINE(Measurement.IS_STATION_OFFLINE, Boolean.class),
  EQUIPMENT_TYPE(Measurement.MONITOR_ID, String.class),
  DATE_TIME_START(Measurement.MONITOR_ID, String.class),
  DATE_TIME_RECORDED(Measurement.MONITOR_ID, String.class),
  AQI_INDEX(Measurement.MONITOR_ID, Integer.class);

  private final Class<?> type;

  private final String fieldName;

  private MeasurementFields(String fieldName, Class<?> type) {
    this.fieldName = fieldName;
    this.type = type;
  }

  public Class<?> getType() {
    return this.type;
  }

  public String getFieldName() {
    return this.fieldName;
  }
}
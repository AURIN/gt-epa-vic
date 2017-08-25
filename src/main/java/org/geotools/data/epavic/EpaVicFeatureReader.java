/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2016, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */

package org.geotools.data.epavic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.UUID;

import org.geotools.data.FeatureReader;
import org.geotools.data.epavic.schema.AQICategoryThreshold;
import org.geotools.data.epavic.schema.HealthCategoryThreshold;
import org.geotools.data.epavic.schema.Measurement;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;

/**
 * Feature reader of the GeoJSON features
 * 
 * @author lmorandini
 * @author William Voorsluys
 *
 */
public class EpaVicFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

  protected SimpleFeatureType featureType;

  protected int featIndex = 0;

  private JsonParser jParser;

  private Queue<InputStream> siteStreams;

  private JsonFactory jfactory = new JsonFactory();

  private ObjectMapper om = new ObjectMapper();

  public EpaVicFeatureReader(SimpleFeatureType featureTypeIn, InputStream stream) throws IOException {
    this(featureTypeIn, new LinkedList<>(Arrays.asList(stream)));
  }

  public EpaVicFeatureReader(SimpleFeatureType featureTypeIn, Queue<InputStream> siteStreams) throws IOException {
    this.featureType = featureTypeIn;
    this.siteStreams = siteStreams;
    this.featIndex = 0;

    if (siteStreams.isEmpty()) {
      throw new IllegalArgumentException("Reader requires an input stream");
    }

    initJParser();
  }

  private void initJParser() throws IOException, JsonParseException {
    this.jParser = jfactory.createParser(siteStreams.poll());
    this.jParser.setCodec(om);
    JsonToken nextToken = jParser.nextToken();
    if (nextToken == null) {
      throw new IOException("Input stream does not contain valid JSON");
    }
    while (nextToken != null && nextToken != JsonToken.END_OBJECT) {
      String fieldname = jParser.getCurrentName();
      if ("Measurements".equals(fieldname)) {
        jParser.nextToken();
        jParser.nextToken();
        break;
      }
      nextToken = jParser.nextToken();
    }
  }

  /**
   * @see FeatureReader#getFeatureType()
   */
  @Override
  public SimpleFeatureType getFeatureType() {
    if (this.featureType == null) {
      throw new IllegalStateException("No features were retrieved, shouldn't be calling getFeatureType()");
    }
    return this.featureType;
  }

  /**
   * @throws IOException
   * @see FeatureReader#hasNext()
   */
  @Override
  public boolean hasNext() throws IOException {
    if (isParserCurrent(jParser)) {
      return true;
    }
    this.jParser.nextToken();
    if (!isParserCurrent(jParser) && !siteStreams.isEmpty()) {
      System.out.println("Creating new parser");
      initJParser();
    }
    return isParserCurrent(jParser);
  }

  private boolean isParserCurrent(JsonParser parser) {
    return parser.hasCurrentToken() && parser.getCurrentToken() != JsonToken.END_ARRAY
        && parser.getCurrentToken() != JsonToken.END_OBJECT;
  }

  /**
   * @throws IOException
   * @see FeatureReader#next()
   */
  @Override
  public SimpleFeature next() throws NoSuchElementException, IOException {
    if (jParser.hasCurrentToken() && jParser.getCurrentToken() != JsonToken.END_ARRAY
        && jParser.getCurrentToken() != JsonToken.END_OBJECT) {
      Measurement val = jParser.readValueAs(Measurement.class);

      SimpleFeatureBuilder b = new SimpleFeatureBuilder(getFeatureType());

      b.set(Measurement.VALUE, val.getValue());
      b.set(Measurement.TIME_BASE_ID, val.getTimeBaseId());
      b.set(Measurement.SITE_ID, val.getSiteId());
      b.set(Measurement.QUALITY_STATUS, val.getQualityStatus());
      b.set(Measurement.MONITOR_TIME_BASIS, val.getMonitorTimeBasis());
      b.set(Measurement.MONITOR_SHORT_NAME, val.getMonitorShortName());
      b.set(Measurement.MONITOR_NAME, val.getMonitorName());
      b.set(Measurement.MONITOR_ID, val.getMonitorId());
      b.set(Measurement.LONG, val.getLongitude());
      b.set(Measurement.LAT, val.getLatitude());
      b.set(Measurement.IS_STATION_OFFLINE, val.getIsStationOffline());
      b.set(Measurement.DATE_TIME_START, val.getValue());
      b.set(Measurement.DATE_TIME_RECORDED, val.getValue());
      b.set(Measurement.AQI_INDEX, val.getValue());
      b.set(Measurement.EQUIPMENT_TYPE,
          val.getEquipmentType() == null ? null : val.getEquipmentType().getDescription());
      b.set(AQICategoryThreshold.AQI_BACKGROUND_COLOUR, val.getaQICategoryThreshold().getaQIBackgroundColour());
      b.set(AQICategoryThreshold.AQI_CATEGORY_ABBREVIATION, val.getaQICategoryThreshold().getaQICategoryAbbreviation());
      b.set(AQICategoryThreshold.AQI_CATEGORY_DESCRIPTION, val.getaQICategoryThreshold().getaQICategoryDescription());
      b.set(AQICategoryThreshold.AQI_FOREGROUND_COLOUR, val.getaQICategoryThreshold().getaQIForegroundColour());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_BACKGROUND_COLOUR,
          val.getHealthCategoryThreshold().getHealthCategoryBackgroundColour());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_DESCRIPTION,
          val.getHealthCategoryThreshold().getHealthCategoryDescription());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_FOREGROUND_COLOUR,
          val.getHealthCategoryThreshold().getHealthCategoryForegroundColour());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_LEVEL, val.getHealthCategoryThreshold().getHealthCategoryLevel());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_MESSAGE,
          val.getHealthCategoryThreshold().getHealthCategoryMessage());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_VALUE_RANGE_TEXT,
          val.getHealthCategoryThreshold().getHealthCategoryValueRangeText());
      b.set(HealthCategoryThreshold.HEALTH_CATEGORY_VISIBILITY_TEXT,
          val.getHealthCategoryThreshold().getHealthCategoryVisibilityText());

      GeometryBuilder builder = new GeometryBuilder();
      Point point = builder.point(val.getLongitude(), val.getLatitude());
      b.set(EpaVicDatastore.GEOMETRY_ATTR, point);

      return b.buildFeature(UUID.randomUUID().toString());
    }
    throw new NoSuchElementException();
  }

  @Override
  public void close() {
    try {
      this.jParser.close();
    } catch (IOException e) {
    }
  }
}

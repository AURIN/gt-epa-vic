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
import java.util.NoSuchElementException;
import java.util.UUID;

import org.geotools.data.FeatureReader;
import org.geotools.data.epavic.schema.Measurement;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

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

  public EpaVicFeatureReader(SimpleFeatureType featureTypeIn, InputStream iStream) throws IOException {
    this.featureType = featureTypeIn;
    this.featIndex = 0;

    JsonFactory jfactory = new JsonFactory();
    jParser = jfactory.createParser(iStream);
    jParser.setCodec(new ObjectMapper());
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
    if (this.jParser.hasCurrentToken() && jParser.getCurrentToken() != JsonToken.END_ARRAY
        && jParser.getCurrentToken() != JsonToken.END_OBJECT) {
      return true;
    }
    this.jParser.nextToken();
    return this.jParser.hasCurrentToken() && jParser.getCurrentToken() != JsonToken.END_ARRAY
        && jParser.getCurrentToken() != JsonToken.END_OBJECT;
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

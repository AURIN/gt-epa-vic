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

import org.geotools.data.FeatureReader;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

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

  private FeatureIterator<SimpleFeature> featureIterator;

  public EpaVicFeatureReader(SimpleFeatureType featureTypeIn, InputStream iStream) throws IOException {
    this.featureType = featureTypeIn;
    this.featIndex = 0;

    FeatureJSON parser = new FeatureJSON(new GeometryJSON(14));
    featureIterator = parser.streamFeatureCollection(iStream);
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
   * @see FeatureReader#hasNext()
   */
  @Override
  public boolean hasNext() {
    return this.featureIterator.hasNext();
  }

  /**
   * @throws IOException
   * @see FeatureReader#next()
   */
  @Override
  public SimpleFeature next() throws NoSuchElementException, IOException {
    return this.featureIterator.next();
  }

  @Override
  public void close() {
    this.featureIterator.close();
  }
}

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.logging.Level;

import org.geotools.data.DefaultResourceInfo;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.ResourceInfo;
import org.geotools.data.epavic.schema.MeasurementFields;
import org.geotools.data.store.ContentDataStore;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.And;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.referencing.FactoryException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.vividsolutions.jts.geom.Point;

/**
 * Source of features for the ArcGIS ReST API
 * 
 * @author lmorandini
 *
 */
public class EpaVicFeatureSource extends ContentFeatureSource {

  // Request parameters
  public static String SITEID = "SiteId";
  public static String MONITORID = "MonitorId";
  public static String TIMEBASISID = "TimeBasisId";
  public static String FROMDATE = "FromDate";
  public static String TODATE = "ToDate";

  protected EpaVicDatastore dataStore;
  protected DefaultResourceInfo resInfo;
  protected String objectIdField;

  /**
   * Inner class used to build the Reuqest parameters. Only "And" logical
   * connectors with equaility can be used
   * 
   * @author lmorandini
   *
   */
  protected final class VisitFilter extends DefaultFilterVisitor {

    public Object visit(And expr, Object data) {
      Map<String, String> map = (Map<String, String>) data;

      expr.getChildren().forEach((eqExpr) -> {
        if (eqExpr instanceof And) {
          this.visit((And) eqExpr, map);
        }
        if (eqExpr instanceof PropertyIsEqualTo) {
          this.visit((PropertyIsEqualTo) eqExpr, map);
        }
      });

      return map;
    }

    public Object visit(PropertyIsEqualTo expr, Object data) {
      Map<String, String> map = (Map<String, String>) data;
      System.out.println(">>>> EXPR: " + expr.getExpression1().toString() + " "
          + expr.getExpression2().toString());
      ; // XXX
      map.put(expr.getExpression1().toString(),
          expr.getExpression2().toString());
      return map;
    }

  }

  public EpaVicFeatureSource(ContentEntry entry, Query query) {

    super(entry, query);
    this.dataStore = (EpaVicDatastore) entry.getDataStore();
  }

  @Override
  protected SimpleFeatureType buildFeatureType() throws IOException {

    // Sets the information about the resource
    this.resInfo = new DefaultResourceInfo();
    try {
      this.resInfo
          .setSchema(new URI(this.dataStore.getNamespace().toExternalForm()));
    } catch (URISyntaxException e) {
      // Re-packages the exception to be compatible with method signature
      throw new IOException(e.getMessage(), e.fillInStackTrace());
    }
    try {
      this.resInfo.setCRS(CRS.decode("EPSG:4283"));
    } catch (FactoryException e) {
      throw new IllegalStateException(e);
    }

    this.resInfo.setName(EpaVicDatastore.MEASUREMENT);

    // Builds the feature type
    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    builder.setCRS(this.resInfo.getCRS());
    builder.setName(this.entry.getName());

    for (MeasurementFields fld : MeasurementFields.values()) {
      builder.add(fld.getFieldName(), fld.getType());
    }

    builder.add(EpaVicDatastore.GEOMETRY_ATTR, Point.class);

    this.schema = builder.buildFeatureType();

    return this.schema;
  }

  @Override
  public ResourceInfo getInfo() {
    if (this.resInfo == null) {
      try {
        this.buildFeatureType();
      } catch (IOException e) {
        this.getDataStore().getLogger().log(Level.SEVERE, e.getMessage(), e);
        return null;
      }
    }
    return this.resInfo;
  }

  @Override
  public ContentDataStore getDataStore() {
    return this.dataStore;
  }

  @Override
  public Name getName() {
    return this.entry.getName();
  }

  @Override
  protected ReferencedEnvelope getBoundsInternal(Query arg0)
      throws IOException {
    return this.getInfo().getBounds();
  }

  @Override
  protected int getCountInternal(Query query) throws IOException {

    JsonFactory jfactory = new JsonFactory();
    try (JsonParser jParser = jfactory
        .createParser(dataStore.retrieveJSON(null))) {
      while (jParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldname = jParser.getCurrentName();
        if ("NumberOfMeasurements".equals(fieldname)) {
          jParser.nextToken();
          return jParser.getIntValue();
        }
      }
    }
    return 0;
  }

  @Override
  protected FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(
      Query query) throws IOException {

    Map<String, Object> params = new HashMap<String, Object>(
        EpaVicDatastore.DEFAULT_PARAMS);

    // params.put(EpaVicDatastore.GEOMETRY_PARAM,
    // this.composeExtent(this.getBounds(query)));

    // params.put(EpaVicDatastore.GEOMETRY_PARAM,
    // this.composeExtent(this.getBounds(query)));

    // Sets the atttributes to return
    params.put(EpaVicDatastore.ATTRIBUTES_PARAM, this.composeAttributes(query));

    // Sets the outpout to GeoJSON
    params.put(EpaVicDatastore.FORMAT_PARAM, EpaVicDatastore.FORMAT_GEOJSON);

    // Executes the request
    InputStream result = this.dataStore.retrieveJSON(params);

    // Returns a reader for the result
    return new EpaVicFeatureReader(this.schema, result);
  }

  /**
   * Helper method to return an attribute list as the API expects it
   * 
   * @param query
   *          Query to build the attributes for
   */
  protected String composeAttributes(Query query) {

    StringJoiner joiner = new StringJoiner(",");

    // The Object ID is always in to ensure the GeoJSON is correctly processed
    // by the parser,
    // For instance, when the GeoJSON properties is null (i.e., only the
    // geometry is
    // returned), WMS GetMap requests return an empty image
    joiner.add(this.objectIdField);

    if (query.retrieveAllProperties()) {
      Iterator<AttributeDescriptor> iter = this.schema.getAttributeDescriptors()
          .iterator();
      while (iter.hasNext()) {
        AttributeDescriptor attr = iter.next();
        // Skips ID and geometry field
        if (!attr.getLocalName().equalsIgnoreCase(this.objectIdField)
            && !attr.getLocalName().equalsIgnoreCase(
                this.schema.getGeometryDescriptor().getLocalName())) {
          joiner.add(iter.next().getLocalName());
        }
      }
    } else {
      for (String attr : query.getPropertyNames()) {
        // Skips ID and geometry field
        if (!attr.equalsIgnoreCase(this.objectIdField)
            && !attr.equalsIgnoreCase(
                this.schema.getGeometryDescriptor().getLocalName())) {
          joiner.add(attr);
        }
      }
    }

    return joiner.toString();
  }

  /**
   * Returns a map of KVP parameters extracted from the ECQL Filter
   *
   * @param filter
   *          ECQL Filter
   * @return Map of parameters and values
   */
  public Map<String, String> composeRequestParameters(Filter filter)
      throws CQLException {

    Map<String, String> requestParams = null;

    try {
      requestParams = (Map<String, String>) filter.accept(
          new EpaVicFeatureSource.VisitFilter(),
          new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER));
    } catch (Exception e) {
      CQLException ce = new CQLException("The " + filter.toString()
          + " CQL expression is incorrect: " + e.getMessage());
      ce.setStackTrace(e.getStackTrace());
    }

    if (requestParams.size() != 4 || !requestParams.containsKey(MONITORID)
        || !requestParams.containsKey(TIMEBASISID)
        || !requestParams.containsKey(FROMDATE)
        || !requestParams.containsKey(TODATE)) {
      throw new CQLException("The " + filter.toString()
          + " CQL expression is incorrect: it has to have "
          + "MonitorID, TimeBasisID, FromDate and ToDate euqaility expression tied by 'And' logical predicate.");
    }

    return requestParams;
  }

  /**
   * Compose the query URL of the instance's dataset
   * 
   * @return query URL
   */
  protected String composeQueryURL() {
    return this.schema.getUserData().get("serviceUrl") + "/query";
  }

}

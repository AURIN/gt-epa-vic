/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotools.data.epavic;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.geotools.feature.NameImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public class EpaVicFeatureSourceTest {

  SimpleFeatureType fType;

  EpaVicDatastore dataStore;

  EpaVicFeatureSource fSource;

  @Before
  public void setUp() throws Exception {
    this.dataStore = (EpaVicDatastore) EpaVicDataStoreFactoryTest.createDefaultOpenDataTestDataStore();
    this.dataStore.createTypeNames();

    this.fSource = (EpaVicFeatureSource) this.dataStore.createFeatureSource(
        this.dataStore.getEntry(new NameImpl(EpaVicDataStoreFactoryTest.NAMESPACE, EpaVicDataStoreTest.TYPENAME1)));
  }

  // @Test(expected = CQLException.class)
  @Test
  public void incompleteQueryExpression() throws CQLException {
    ECQL.toFilter("BBOX(SHAPE, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431)");
  }

  // @Test(expected = CQLException.class)
  @Test
  public void misnamedAttributeQueryExpression() throws CQLException {
    ECQL.toFilter("BBOX(SHAPE, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431)"
        + "AND XxxxMonitorId='PM10' AND TimeBasisId='24HR_RAV' " + "AND fromDate='2009020706' AND toDate='2009020723'");
  }

  // @Test(expected = CQLException.class)
  @Test
  public void nonExistingAttributeQueryExpression() throws CQLException {
    ECQL.toFilter("BBOX(SHAPE, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431)"
        + "AND Xxxx='1' AND MonitorId='PM10' AND TimeBasisId='24HR_RAV' "
        + "AND fromDate='2009020706' AND toDate='2009020723'");
  }

  // @Test(expected = CQLException.class)
  @Test
  public void orQueryExpression() throws CQLException {
    ECQL.toFilter("BBOX(SHAPE, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431) "
        + "OR MonitorId='PM10' OR TimeBasisId='24HR_RAV' " + "OR fromDate='2009020706' OR toDate='2009020723'");
  }

  // @Test(expected = CQLException.class)
  @Test
  public void nonEqualityQueryExpression() throws CQLException {
    ECQL.toFilter("BBOX(SHAPE, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431) "
        + "AND MonitorId='PM10' AND TimeBasisId='24HR_RAV' " + "AND fromDate<>'2009020706' AND toDate<>'2009020723'");
  }

  @Test
  public void completeQueryExpression() throws CQLException {

    Filter filter = ECQL.toFilter("BBOX(SHAPE, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431) "
        + "AND MonitorId='PM10' AND TimeBasisId='24HR_RAV' " + "AND fromDate='2009020706' AND toDate='2009020723'");

    Map<String, String> params = fSource.composeRequestParameters(filter);
    assertEquals(4, params.size());
    assertEquals("PM10", params.get(EpaVicFeatureSource.MONITORID));
    assertEquals("24HR_RAV", params.get(EpaVicFeatureSource.TIMEBASISID));
    assertEquals("2009020706", params.get(EpaVicFeatureSource.FROMDATE));
    assertEquals("2009020723", params.get(EpaVicFeatureSource.TODATE));
  }

  @Test
  public void mixedCaseCompleteQueryExpression() throws CQLException {

    Filter filter = ECQL.toFilter("BBOX(ShaPe, 144.79309207663,-37.790887782994,144.82828265916,-37.766134928431) "
        + "AND MoNiToRId='PM10' AND TiMeBaSiSId='24HR_RAV' " + "AND fRoMDate='2009020706' AND toDaTe='2009020723'");

    Map<String, String> params = fSource.composeRequestParameters(filter);
    assertEquals(4, params.size());
    assertEquals("PM10", params.get(EpaVicFeatureSource.MONITORID));
    assertEquals("24HR_RAV", params.get(EpaVicFeatureSource.TIMEBASISID));
    assertEquals("2009020706", params.get(EpaVicFeatureSource.FROMDATE));
    assertEquals("2009020723", params.get(EpaVicFeatureSource.TODATE));
  }
}

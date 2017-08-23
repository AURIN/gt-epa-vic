/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008-2016, Open Source Geospatial Foundation (OSGeo)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.referencing.CRS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vividsolutions.jts.geom.Geometry;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpMethod.class, EpaVicDatastore.class })
public class EpaVicDataStoreTest {

  public static String TYPENAME1 = "measurement";

  private EpaVicDatastore dataStore;

  private HttpClient clientMock;

  private GetMethod getMock;

  private PostMethod postMock;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHTTPError() throws Exception {

    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(clientMock);
    this.getMock = PowerMockito.mock(GetMethod.class);
    PowerMockito.whenNew(GetMethod.class).withNoArguments().thenReturn(getMock);
    when(clientMock.executeMethod(getMock)).thenReturn(HttpStatus.SC_NOT_FOUND);

    try {
      this.dataStore = (EpaVicDatastore) EpaVicDataStoreFactoryTest.createDefaultOpenDataTestDataStore();
      List<Name> names = this.dataStore.createTypeNames();
    } catch (IOException e) {
      assertTrue(e.getMessage().contains("404"));
    }

  }

  @Test
  public void testServiceError() throws Exception {

    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(clientMock).thenReturn(clientMock);
    this.getMock = PowerMockito.mock(GetMethod.class);
    PowerMockito.whenNew(GetMethod.class).withNoArguments().thenReturn(getMock).thenReturn(getMock);
    when(clientMock.executeMethod(getMock)).thenReturn(HttpStatus.SC_OK).thenReturn(HttpStatus.SC_OK);
    when(getMock.getResponseBodyAsStream())
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/measurements.json"));

    try {
      this.dataStore = (EpaVicDatastore) EpaVicDataStoreFactoryTest.createDefaultOpenDataTestDataStore();
      List<Name> names = this.dataStore.createTypeNames();
    } catch (IOException e) {
      assertTrue(e.getMessage().contains("400 Cannot perform query"));
    }
  }

  @Test
  public void testCreateTypeNamesMeasurements() throws Exception {

    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(clientMock).thenReturn(clientMock);
    this.getMock = PowerMockito.mock(GetMethod.class);
    PowerMockito.whenNew(GetMethod.class).withNoArguments().thenReturn(getMock).thenReturn(getMock);
    when(clientMock.executeMethod(getMock)).thenReturn(HttpStatus.SC_OK).thenReturn(HttpStatus.SC_OK);
    when(getMock.getResponseBodyAsStream())
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/measurements.json"));

    this.dataStore = (EpaVicDatastore) EpaVicDataStoreFactoryTest.createDefaultOpenDataTestDataStore();
    List<Name> names = this.dataStore.createTypeNames();

    assertEquals(1, names.size());
    assertEquals(TYPENAME1, names.get(0).getLocalPart());
    assertEquals(EpaVicDataStoreFactoryTest.NAMESPACE, names.get(0).getNamespaceURI());

    assertNotNull(this.dataStore.getEntry(new NameImpl(EpaVicDataStoreFactoryTest.NAMESPACE, TYPENAME1)));
  }

  @Test
  public void testCreateFeatureSourceAndCountFeature() throws Exception {

    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(clientMock).thenReturn(clientMock);
    this.getMock = PowerMockito.mock(GetMethod.class);
    PowerMockito.whenNew(GetMethod.class).withNoArguments().thenReturn(getMock).thenReturn(getMock);
    when(clientMock.executeMethod(getMock)).thenReturn(HttpStatus.SC_OK).thenReturn(HttpStatus.SC_OK)
        .thenReturn(HttpStatus.SC_OK);
    when(getMock.getResponseBodyAsStream())
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/catalog.json"))
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/lgaDataset.json"))
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/lgaDataset.json"));

    this.dataStore = (EpaVicDatastore) EpaVicDataStoreFactoryTest.createDefaultOpenDataTestDataStore();
    this.dataStore.createTypeNames();

    FeatureSource<SimpleFeatureType, SimpleFeature> src = this.dataStore
        .createFeatureSource(this.dataStore.getEntry(new NameImpl(EpaVicDataStoreFactoryTest.NAMESPACE, TYPENAME1)));
    src.getSchema();
    assertTrue(src instanceof EpaVicFeatureSource);
    assertEquals("LGAProfiles2014Beta", src.getInfo().getName());
    assertEquals(EpaVicDataStoreFactoryTest.NAMESPACE, src.getInfo().getSchema().toString());
    assertEquals(CRS.decode("EPSG:3857"), src.getInfo().getCRS());
    assertEquals("LGA Profile 2014 (beta)", src.getInfo().getTitle());
    assertEquals(15661191, src.getInfo().getBounds().getMinX(), 1);
    assertEquals(-4742385, src.getInfo().getBounds().getMinY(), 1);
    assertEquals(16706777, src.getInfo().getBounds().getMaxX(), 1);
    assertEquals(-4022464, src.getInfo().getBounds().getMaxY(), 1);
    assertEquals("[Health and Human Services, LGA, LGA Profiles]", src.getInfo().getKeywords().toString());
    assertEquals(
        "<div>2014 Local Government Area Profiles</div><div><br /></div>https://www2.health.vic.gov.au/about/reporting-planning-data/gis-and-planning-products/geographical-profiles<div>&gt; Please read the data definistions at the link above</div><div>&gt; xls and pdf documents area available at the link above</div><div>&gt; This is a beta release of the 2014 LGA profiles in this format. Field names and types may change during the beta phase. </div><div><br /></div><div>Last updated : 24 May 2016</div><div>Owning agency : Department of Health and Human Services, Victoria</div><div>Copyright statement : https://www.health.vic.gov.au/copyright</div><div>Licence name : https://www.health.vic.gov.au/data-license</div><div>Disclaimer: https://www.health.vic.gov.au/data-disclaimer</div><div>Attribution statement: https://www.health.vic.gov.au/data-attribution</div><div><br /></div><div>Off-line access : Department of Health and Human Services, GPO Box 4057, Melbourne Victoria, 3001</div><div><br /></div><div>Geographic coverage-jurisdiction : Victoria</div>",
        src.getInfo().getDescription());

    // Feature count test
    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(this.clientMock);

    this.postMock = PowerMockito.mock(PostMethod.class);
    PowerMockito.whenNew(PostMethod.class).withNoArguments().thenReturn(this.postMock);
    when(this.clientMock.executeMethod(postMock)).thenReturn(HttpStatus.SC_OK);
    when(this.postMock.getResponseBodyAsStream())
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/count.json"));

    assertEquals(79, src.getCount(new Query()));
  }

  @Test
  public void testFeatures() throws Exception {

    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(clientMock).thenReturn(clientMock);
    this.getMock = PowerMockito.mock(GetMethod.class);
    PowerMockito.whenNew(GetMethod.class).withNoArguments().thenReturn(getMock).thenReturn(getMock);
    when(clientMock.executeMethod(getMock)).thenReturn(HttpStatus.SC_OK).thenReturn(HttpStatus.SC_OK)
        .thenReturn(HttpStatus.SC_OK);
    when(getMock.getResponseBodyAsStream())
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/catalog.json"))
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/lgaDataset.json"))
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/lgaDataset.json"));

    this.dataStore = (EpaVicDatastore) EpaVicDataStoreFactoryTest.createDefaultOpenDataTestDataStore();
    this.dataStore.createTypeNames();

    FeatureSource<SimpleFeatureType, SimpleFeature> src = this.dataStore
        .createFeatureSource(this.dataStore.getEntry(new NameImpl(EpaVicDataStoreFactoryTest.NAMESPACE, TYPENAME1)));
    src.getSchema();

    // Test feature iteration
    this.clientMock = PowerMockito.mock(HttpClient.class);
    PowerMockito.whenNew(HttpClient.class).withNoArguments().thenReturn(this.clientMock);

    this.postMock = PowerMockito.mock(PostMethod.class);
    PowerMockito.whenNew(PostMethod.class).withNoArguments().thenReturn(this.postMock);
    when(this.clientMock.executeMethod(postMock)).thenReturn(HttpStatus.SC_OK);
    when(this.postMock.getResponseBodyAsStream())
        .thenReturn(EpaVicDataStoreFactoryTest.readJSONAsStream("test-data/lgaFeatures.geo.json"));

    FeatureCollection<SimpleFeatureType, SimpleFeature> fc = src.getFeatures(new Query());
    FeatureIterator iter = fc.features();

    assertEquals(CRS.decode("EPSG:3857"), fc.getSchema().getCoordinateReferenceSystem());
    assertEquals(true, iter.hasNext());
    SimpleFeature sf = (SimpleFeature) iter.next();
    assertEquals(true, iter.hasNext());
    sf = (SimpleFeature) iter.next();
    assertEquals("POINT (16421261.466298774 -4592239.022226746)",
        ((Geometry) (sf.getAttribute("geometry"))).getCentroid().toString());
    assertEquals("Wellington (S)", sf.getAttribute("LGA"));
    assertEquals(false, iter.hasNext());
    assertEquals(false, iter.hasNext());
  }

}

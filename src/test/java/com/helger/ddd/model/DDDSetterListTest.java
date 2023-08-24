package com.helger.ddd.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsMap;

/**
 * Test class for class {@link DDDValueProviderList}.
 *
 * @author Philip Helger
 */
public final class DDDSetterListTest
{
  @Test
  public void testBasic ()
  {
    final DDDValueProviderList aList = DDDValueProviderList.read (DDDValueProviderList.DEFAULT_VALUE_PROVIDER_LIST_RES);
    assertNotNull (aList);

    assertNotNull (aList.getLastModification ());

    final ICommonsMap <String, DDDValueProviderPerSyntax> aMap = aList.getAllValueProvidersPerSyntaxes ();
    assertEquals (3, aMap.size ());
    assertNotNull (aMap.get ("ubl21-invoice"));
    assertNotNull (aMap.get ("ubl21-creditnote"));
    assertNotNull (aMap.get ("cii-d16b"));
  }
}

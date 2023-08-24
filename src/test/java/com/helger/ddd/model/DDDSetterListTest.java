/*
 * Copyright (C) 2023 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

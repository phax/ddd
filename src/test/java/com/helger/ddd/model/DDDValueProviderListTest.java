/*
 * Copyright (C) 2023-2024 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsMap;

/**
 * Test class for class {@link DDDValueProviderList}.
 *
 * @author Philip Helger
 */
public final class DDDValueProviderListTest
{
  private static void _recursiveTest (@Nonnull final String sSyntaxID,
                                      @Nonnull final Map <EDDDSourceField, VPSelect> aSelects)
  {
    for (final Map.Entry <EDDDSourceField, VPSelect> e1 : aSelects.entrySet ())
      for (final Map.Entry <String, VPIf> e2 : e1.getValue ())
      {
        final VPIf aIf = e2.getValue ();
        if (aIf.determinedValues ().isNotEmpty ())
        {
          // Make sure, for each value providers at least the VESID is mapped
          final String sVESID = aIf.determinedValues ().get (EDDDDeterminedField.VESID);
          assertNotNull ("VESID missing for " + sSyntaxID + " - " + e1.getKey (), sVESID);
        }
        else
        {
          // Nested selects
          assertTrue (aIf.hasNestedSelects ());
          _recursiveTest (sSyntaxID, aIf.getAllNestedSelects ());
        }
      }
  }

  @Test
  public void testBasic ()
  {
    final DDDValueProviderList aList = DDDValueProviderList.getDefaultValueProviderList ();
    assertNotNull (aList);

    assertNotNull (aList.getLastModification ());

    final ICommonsMap <String, DDDValueProviderPerSyntax> aMap = aList.getAllValueProvidersPerSyntaxes ();
    assertEquals (DDDSyntaxList.getDefaultSyntaxList ().getAllSyntaxes ().size (), aMap.size ());

    for (final DDDValueProviderPerSyntax aVPS : aMap.values ())
      _recursiveTest (aVPS.getSyntaxID (), aVPS.getAllSelectors ());
  }
}

/*
 * Copyright (C) 2023-2025 Philip Helger (www.helger.com)
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
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.io.resource.ClassPathResource;

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
          // Make sure, for each value providers at least the profile name is
          // mapped
          final String sProfileName = aIf.determinedValues ().get (EDDDDeterminedField.PROFILE_NAME);
          assertNotNull ("Profile Name missing for " + sSyntaxID + " - " + e1.getKey (), sProfileName);
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

  @Test
  public void testMerging ()
  {
    final DDDValueProviderList aList1 = DDDValueProviderList.readFromXML (new ClassPathResource ("value-provider/vp1.xml"));
    assertNotNull (aList1);
    assertEquals (1, aList1.getAllValueProvidersPerSyntaxes ().size ());
    assertEquals (1,
                  aList1.getAllValueProvidersPerSyntaxes ().values ().iterator ().next ().getAllSelectors ().size ());

    final DDDValueProviderList aList2 = DDDValueProviderList.readFromXML (new ClassPathResource ("value-provider/vp2.xml"));
    assertNotNull (aList2);
    assertEquals (1, aList2.getAllValueProvidersPerSyntaxes ().size ());
    assertEquals (1,
                  aList2.getAllValueProvidersPerSyntaxes ().values ().iterator ().next ().getAllSelectors ().size ());

    final DDDValueProviderList aList3 = DDDValueProviderList.readFromXML (new ClassPathResource ("value-provider/vp3.xml"));
    assertNotNull (aList3);
    assertEquals (1, aList3.getAllValueProvidersPerSyntaxes ().size ());
    assertEquals (1,
                  aList3.getAllValueProvidersPerSyntaxes ().values ().iterator ().next ().getAllSelectors ().size ());

    final DDDValueProviderList aList4 = DDDValueProviderList.readFromXML (new ClassPathResource ("value-provider/vp4.xml"));
    assertNotNull (aList4);
    assertEquals (1, aList4.getAllValueProvidersPerSyntaxes ().size ());
    assertEquals (1,
                  aList4.getAllValueProvidersPerSyntaxes ().values ().iterator ().next ().getAllSelectors ().size ());

    {
      final DDDValueProviderList aList12 = DDDValueProviderList.createMergedValueProviderList (aList1, aList2);
      final ICommonsMap <String, DDDValueProviderPerSyntax> aVPSs = aList12.getAllValueProvidersPerSyntaxes ();
      assertEquals (1, aVPSs.size ());
      final DDDValueProviderPerSyntax aVPS = aVPSs.getFirstValue ();
      final ICommonsMap <EDDDSourceField, VPSelect> aSelectors = aVPS.getAllSelectors ();
      assertEquals (1, aSelectors.size ());
      assertEquals (EDDDSourceField.CUSTOMIZATION_ID, aSelectors.getFirstKey ());
      final VPSelect aSelect = aSelectors.getFirstValue ();
      assertEquals (2, aSelect.getIfCount ());
      final ICommonsSortedMap <String, VPIf> aAllIfs = aSelect.getAllIfs ();
      assertTrue (aAllIfs.containsKey ("C1"));
      assertTrue (aAllIfs.containsKey ("C2"));
      assertEquals (1, aAllIfs.get ("C1").nestedSelects ().size ());
      assertEquals (1, aAllIfs.get ("C2").nestedSelects ().size ());
    }

    {
      final DDDValueProviderList aList13 = DDDValueProviderList.createMergedValueProviderList (aList1, aList3);
      final ICommonsMap <String, DDDValueProviderPerSyntax> aVPSs = aList13.getAllValueProvidersPerSyntaxes ();
      assertEquals (1, aVPSs.size ());
      final DDDValueProviderPerSyntax aVPS = aVPSs.getFirstValue ();
      final ICommonsMap <EDDDSourceField, VPSelect> aSelectors = aVPS.getAllSelectors ();
      assertEquals (1, aSelectors.size ());
      assertEquals (EDDDSourceField.CUSTOMIZATION_ID, aSelectors.getFirstKey ());
      final VPSelect aSelect = aSelectors.getFirstValue ();
      assertEquals (1, aSelect.getIfCount ());
      final ICommonsSortedMap <String, VPIf> aAllIfs = aSelect.getAllIfs ();
      assertTrue (aAllIfs.containsKey ("C1"));
      final VPIf aIf = aAllIfs.get ("C1");
      assertNotNull (aIf);
      assertTrue (aIf.hasNestedSelects ());
      assertEquals (1, aIf.nestedSelects ().size ());
      final VPSelect aNestedSelect = aIf.nestedSelects ().getFirstValue ();
      assertNotNull (aNestedSelect);
      assertEquals (4, aNestedSelect.getIfCount ());
      assertNotNull (aNestedSelect.getIf ("P1"));
      assertNotNull (aNestedSelect.getIf ("P2"));
      assertNotNull (aNestedSelect.getIf ("P3"));
      assertNotNull (aNestedSelect.getIf ("P4"));
    }

    {
      final DDDValueProviderList aList14 = DDDValueProviderList.createMergedValueProviderList (aList1, aList4);
      final ICommonsMap <String, DDDValueProviderPerSyntax> aVPSs = aList14.getAllValueProvidersPerSyntaxes ();
      assertEquals (1, aVPSs.size ());
      final DDDValueProviderPerSyntax aVPS = aVPSs.getFirstValue ();
      final ICommonsMap <EDDDSourceField, VPSelect> aSelectors = aVPS.getAllSelectors ();
      assertEquals (2, aSelectors.size ());
      assertTrue (aSelectors.containsKey (EDDDSourceField.CUSTOMIZATION_ID));
      assertTrue (aSelectors.containsKey (EDDDSourceField.PROCESS_ID));

      assertEquals (1, aSelectors.get (EDDDSourceField.CUSTOMIZATION_ID).getIfCount ());
      assertEquals (2, aSelectors.get (EDDDSourceField.PROCESS_ID).getIfCount ());
    }
  }
}

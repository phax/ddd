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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsMap;

/**
 * Test class for class {@link DDDValueProviderList}.
 *
 * @author Philip Helger
 */
public final class DDDValueProviderListTest
{
  @Test
  public void testBasic ()
  {
    final DDDValueProviderList aList = DDDValueProviderList.getDefaultValueProviderList ();
    assertNotNull (aList);

    assertNotNull (aList.getLastModification ());

    final ICommonsMap <String, DDDValueProviderPerSyntax> aMap = aList.getAllValueProvidersPerSyntaxes ();
    assertEquals (12, aMap.size ());
    assertNotNull (aMap.get ("cii-d16b"));
    assertNotNull (aMap.get ("peppol-eusr"));
    assertNotNull (aMap.get ("peppol-tsr"));
    assertNotNull (aMap.get ("ubl2-applicationresponse"));
    assertNotNull (aMap.get ("ubl2-catalogue"));
    assertNotNull (aMap.get ("ubl2-creditnote"));
    assertNotNull (aMap.get ("ubl2-despatchadvice"));
    assertNotNull (aMap.get ("ubl2-invoice"));
    assertNotNull (aMap.get ("ubl2-order"));
    assertNotNull (aMap.get ("ubl2-ordercancellation"));
    assertNotNull (aMap.get ("ubl2-orderchange"));
    assertNotNull (aMap.get ("ubl2-orderresponse"));

    final Set <String> aAllVESIDs = new HashSet <> ();
    for (final DDDValueProviderPerSyntax aVPS : aMap.values ())
      for (final var e1 : aVPS.getAllSelectors ().entrySet ())
        for (final var e2 : e1.getValue ().entrySet ())
        {
          // Make sure, for each value providers at least the VESID is mapped
          final String sVESID = e2.getValue ().get (EDDDDeterminedField.VESID);
          assertNotNull ("VESID missing for " + aVPS.getSyntaxID () + " - " + e1.getKey (), sVESID);

          // E.g. true for XRechnung UBL CreditNote 2.2
          if (false)
            assertTrue ("VESID '" + sVESID + "' is contained more then once", aAllVESIDs.add (sVESID));
        }
  }
}

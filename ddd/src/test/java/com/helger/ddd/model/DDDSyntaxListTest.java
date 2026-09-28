/*
 * Copyright (C) 2023-2026 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.string.StringHelper;
import com.helger.collection.commons.ICommonsMap;

/**
 * Test class for class {@link DDDSyntaxList}
 *
 * @author Philip Helger
 */
public final class DDDSyntaxListTest
{
  @Test
  public void testDefault ()
  {
    final DDDSyntaxList aSL = DDDSyntaxList.getDefaultSyntaxList ();
    assertNotNull (aSL);

    assertNotNull (aSL.getLastModification ());

    // Creates a copy
    final ICommonsMap <String, DDDSyntax> aMap = aSL.getAllSyntaxes ();
    assertNotNull (aMap.remove ("cdar"));
    assertNotNull (aMap.remove ("cii"));
    assertNotNull (aMap.remove ("ebinterface-3p0"));
    assertNotNull (aMap.remove ("ebinterface-3p02"));
    assertNotNull (aMap.remove ("ebinterface-4p0"));
    assertNotNull (aMap.remove ("ebinterface-4p1"));
    assertNotNull (aMap.remove ("ebinterface-4p2"));
    assertNotNull (aMap.remove ("ebinterface-4p3"));
    assertNotNull (aMap.remove ("ebinterface-5p0"));
    assertNotNull (aMap.remove ("ebinterface-6p0"));
    assertNotNull (aMap.remove ("ebinterface-6p1"));
    assertNotNull (aMap.remove ("fatturapa-12"));
    assertNotNull (aMap.remove ("ksef-fa1"));
    assertNotNull (aMap.remove ("ksef-fa2"));
    assertNotNull (aMap.remove ("ksef-fa3"));
    assertNotNull (aMap.remove ("osa-invoice-annulment-2"));
    assertNotNull (aMap.remove ("osa-invoice-annulment-3"));
    assertNotNull (aMap.remove ("osa-invoice-data-2"));
    assertNotNull (aMap.remove ("osa-invoice-data-3"));
    assertNotNull (aMap.remove ("peppol-eusr"));
    assertNotNull (aMap.remove ("peppol-tdd"));
    assertNotNull (aMap.remove ("peppol-om-tdd"));
    assertNotNull (aMap.remove ("peppol-sk-tdd"));
    assertNotNull (aMap.remove ("peppol-vida-tdd"));
    assertNotNull (aMap.remove ("peppol-tsr"));
    // assertNotNull (aMap.remove ("sbd"));
    // assertNotNull (aMap.remove ("sbdh"));
    assertNotNull (aMap.remove ("ubl2-applicationresponse"));
    assertNotNull (aMap.remove ("ubl2-catalogue"));
    assertNotNull (aMap.remove ("ubl2-cataloguedeletion"));
    assertNotNull (aMap.remove ("ubl2-catalogueitemspecificationupdate"));
    assertNotNull (aMap.remove ("ubl2-cataloguepricingupdate"));
    assertNotNull (aMap.remove ("ubl2-cataloguerequest"));
    assertNotNull (aMap.remove ("ubl2-creditnote"));
    assertNotNull (aMap.remove ("ubl2-despatchadvice"));
    assertNotNull (aMap.remove ("ubl2-invoice"));
    assertNotNull (aMap.remove ("ubl2-order"));
    assertNotNull (aMap.remove ("ubl2-ordercancellation"));
    assertNotNull (aMap.remove ("ubl2-orderchange"));
    assertNotNull (aMap.remove ("ubl2-orderresponse"));
    assertNotNull (aMap.remove ("ubl2-orderresponsesimple"));
    assertNotNull (aMap.remove ("ubl2-reminder"));
    assertNotNull (aMap.remove ("ubl2-statement"));
    assertNotNull (aMap.remove ("ubl2-utilitystatement"));
    assertNotNull (aMap.remove ("zugferd1"));
    assertEquals ("Left: " + aMap.toString (), 0, aMap.size ());
  }

  @Test
  public void testAllSyntaxesWithNamespaceUri ()
  {
    for (final var e : DDDSyntaxList.getDefaultSyntaxList ().getAllSyntaxes ().values ())
      assertTrue (StringHelper.isNotEmpty (e.getRootElementNamespaceURI ()));
  }

  @Test
  public void testFindMatchingSyntaxByNamespaceAndLocalName ()
  {
    final DDDSyntaxList aSL = DDDSyntaxList.getDefaultSyntaxList ();

    // Positive match against a known syntax
    final DDDSyntax aMatch = aSL.findMatchingSyntax ("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2",
                                                     "Invoice");
    assertNotNull (aMatch);
    assertEquals ("ubl2-invoice", aMatch.getID ());

    // Unknown namespace / local name combination
    assertNull (aSL.findMatchingSyntax ("urn:does:not:exist", "Invoice"));
    assertNull (aSL.findMatchingSyntax ("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Unknown"));

    // Null parameters return null (no registered syntax has a null namespace
    // URI or local name)
    assertNull (aSL.findMatchingSyntax (null, null));
    assertNull (aSL.findMatchingSyntax (null, "Invoice"));
    assertNull (aSL.findMatchingSyntax ("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", null));
  }
}

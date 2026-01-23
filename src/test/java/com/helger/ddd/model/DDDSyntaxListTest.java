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

import org.junit.Test;

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

    final ICommonsMap <String, DDDSyntax> aMap = aSL.getAllSyntaxes ();
    assertNotNull (aMap.remove ("cii-d16b"));
    assertNotNull (aMap.remove ("fatturapa-12"));
    assertNotNull (aMap.remove ("peppol-eusr"));
    assertNotNull (aMap.remove ("peppol-tdd"));
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
}

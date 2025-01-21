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

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsMap;

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
    assertEquals (22, aMap.size ());
    assertTrue (aMap.containsKey ("cii-d16b"));
    assertTrue (aMap.containsKey ("fatturapa-12"));
    assertTrue (aMap.containsKey ("peppol-eusr"));
    assertTrue (aMap.containsKey ("peppol-tsr"));
    assertTrue (aMap.containsKey ("ubl2-applicationresponse"));
    assertTrue (aMap.containsKey ("ubl2-catalogue"));
    assertTrue (aMap.containsKey ("ubl2-cataloguedeletion"));
    assertTrue (aMap.containsKey ("ubl2-catalogueitemspecificationupdate"));
    assertTrue (aMap.containsKey ("ubl2-cataloguepricingupdate"));
    assertTrue (aMap.containsKey ("ubl2-cataloguerequest"));
    assertTrue (aMap.containsKey ("ubl2-creditnote"));
    assertTrue (aMap.containsKey ("ubl2-despatchadvice"));
    assertTrue (aMap.containsKey ("ubl2-invoice"));
    assertTrue (aMap.containsKey ("ubl2-order"));
    assertTrue (aMap.containsKey ("ubl2-ordercancellation"));
    assertTrue (aMap.containsKey ("ubl2-orderchange"));
    assertTrue (aMap.containsKey ("ubl2-orderresponse"));
    assertTrue (aMap.containsKey ("ubl2-orderresponsesimple"));
    assertTrue (aMap.containsKey ("ubl2-statement"));
    assertTrue (aMap.containsKey ("ubl2-utilitystatement"));
    assertTrue (aMap.containsKey ("zugferd1"));
  }
}

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
import org.w3c.dom.Document;

import com.helger.diagnostics.error.list.ErrorList;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Test class for class {@link DDDGetterXPath}
 *
 * @author Philip Helger
 */
public final class DDDGetterXPathTest
{
  private static final String XML = "<root a='attr'><simple>value</simple><complex><c>1</c><c>2</c></complex></root>";

  @Test
  public void testNodeTypes ()
  {
    final Document aDoc = DOMReader.readXMLDOM (XML);
    assertNotNull (aDoc);

    ErrorList aErrorList = new ErrorList ();
    assertEquals ("attr", new DDDGetterXPath ("/root/@a").getValue (aDoc, aErrorList));
    assertTrue (aErrorList.isEmpty ());

    aErrorList = new ErrorList ();
    assertEquals ("value", new DDDGetterXPath ("/root/simple/text()").getValue (aDoc, aErrorList));
    assertTrue (aErrorList.isEmpty ());

    // Element without child elements
    aErrorList = new ErrorList ();
    assertEquals ("value", new DDDGetterXPath ("/root/simple").getValue (aDoc, aErrorList));
    assertTrue (aErrorList.isEmpty ());

    // Element with child elements
    aErrorList = new ErrorList ();
    assertNull (new DDDGetterXPath ("/root/complex").getValue (aDoc, aErrorList));
    assertEquals (1, aErrorList.size ());

    // String literal
    aErrorList = new ErrorList ();
    assertEquals ("constant", new DDDGetterXPath ("\"constant\"").getValue (aDoc, aErrorList));
    assertTrue (aErrorList.isEmpty ());
  }
}

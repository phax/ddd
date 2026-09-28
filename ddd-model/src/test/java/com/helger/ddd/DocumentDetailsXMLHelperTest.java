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
package com.helger.ddd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.w3c.dom.Element;

import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.xml.XMLFactory;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Test class for class {@link DocumentDetailsXMLHelper}.
 *
 * @author Philip Helger
 */
public final class DocumentDetailsXMLHelperTest
{
  @Test
  public void testMicroElement ()
  {
    // Test null
    assertNull (DocumentDetailsXMLHelper.getAsDocumentDetails ((IMicroElement) null, SimpleIdentifierFactory.INSTANCE));

    // Test empty object
    final DocumentDetails aDDEmpty = DocumentDetails.builder ().build ();
    final IMicroElement e = new MicroElement ("x");
    aDDEmpty.appendToMicroElement (e);
    final DocumentDetails aDD2 = DocumentDetailsXMLHelper.getAsDocumentDetails (e, SimpleIdentifierFactory.INSTANCE);
    assertEquals (aDDEmpty, aDD2);
  }

  @Test
  public void testDOMElement ()
  {
    // Test null
    assertNull (DocumentDetailsXMLHelper.getAsDocumentDetails ((Element) null, SimpleIdentifierFactory.INSTANCE));

    // Test empty object
    final DocumentDetails aDDEmpty = DocumentDetails.builder ().build ();
    final var aDoc = XMLFactory.newDocument ();
    final var e = (Element) aDoc.appendChild (aDoc.createElement ("root"));
    aDDEmpty.appendToDOMElement (e);
    final DocumentDetails aDD2 = DocumentDetailsXMLHelper.getAsDocumentDetails (e, SimpleIdentifierFactory.INSTANCE);
    assertEquals (aDDEmpty, aDD2);
  }
}

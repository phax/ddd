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
package com.helger.ddd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Test class for class {@link DocumentDetailsDeterminator}
 *
 * @author Philip Helger
 */
public final class DocumentDetailsDeterminatorTest
{
  @Test
  public void testDiscovery ()
  {
    // The main determinator
    final DocumentDetailsDeterminator aDDD = new DocumentDetailsDeterminator (DDDSyntaxList.createDefaultSyntaxList (),
                                                                              DDDValueProviderList.createDefaultValueProviderList ());

    // Read the document to be identified
    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("external/ubl21-invoice/good/base-example.xml"));
    assertNotNull (aDoc);

    // Main determination
    final DocumentDetails aDD = aDDD.findDocumentDetails (aDoc.getDocumentElement (), null, null);
    assertNotNull (aDD);

    assertEquals ("Snippet1", aDD.getBusinessDocumentID ());
    assertEquals ("0088:9482348239847239874", aDD.getSenderID ().getValue ());
    assertEquals ("0002:FR23342", aDD.getReceiverID ().getValue ());
    assertEquals ("SupplierTradingName Ltd.", aDD.getSenderName ());
    assertEquals ("BuyerTradingName AS", aDD.getReceiverName ());
    assertEquals ("eu.peppol.bis3:invoice:latest", aDD.getVESID ());
  }
}

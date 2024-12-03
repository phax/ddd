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
package com.helger.ddd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.error.list.ErrorList;
import com.helger.commons.io.file.FileSystemIterator;
import com.helger.commons.io.file.IFileFilter;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.ddd.model.DDDSyntax;
import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.EDDDSourceField;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Test class for class {@link DocumentDetailsDeterminator}
 *
 * @author Philip Helger
 */
public final class DocumentDetailsDeterminatorTest
{
  // The main determinator
  private static final DocumentDetailsDeterminator DDD = new DocumentDetailsDeterminator (DDDSyntaxList.getDefaultSyntaxList (),
                                                                                          DDDValueProviderList.getDefaultValueProviderList ());
  private static final Logger LOGGER = LoggerFactory.getLogger (DocumentDetailsDeterminatorTest.class);

  @Test
  public void testDiscoveryInvoice ()
  {
    // Read the document to be identified
    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("external/ubl2-invoice/good/base-example.xml"));
    assertNotNull (aDoc);

    // Main determination
    final DocumentDetails aDD = DDD.findDocumentDetails (aDoc.getDocumentElement ());
    assertNotNull (aDD);

    assertTrue (aDD.hasSyntaxID ());
    assertEquals ("ubl2-invoice", aDD.getSyntaxID ());

    assertNotNull (aDD.getSenderID ());
    assertEquals ("0088:9482348239847239874", aDD.getSenderID ().getValue ());

    assertNotNull (aDD.getReceiverID ());
    assertEquals ("0002:FR23342", aDD.getReceiverID ().getValue ());

    assertNotNull (aDD.getDocumentTypeID ());
    assertEquals ("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1",
                  aDD.getDocumentTypeID ().getValue ());

    assertNotNull (aDD.getProcessID ());
    assertEquals ("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0", aDD.getProcessID ().getValue ());

    assertEquals ("Snippet1", aDD.getBusinessDocumentID ());
    assertEquals ("SupplierTradingName Ltd.", aDD.getSenderName ());
    assertEquals ("GB", aDD.getSenderCountryCode ());
    assertEquals ("BuyerTradingName AS", aDD.getReceiverName ());
    assertEquals ("SE", aDD.getReceiverCountryCode ());
    assertEquals ("eu.peppol.bis3:invoice:latest-active", aDD.getVESID ());
    assertEquals ("Peppol BIS Billing UBL Invoice V3", aDD.getProfileName ());
  }

  @Test
  public void testDiscoveryMultipleConditions ()
  {
    {
      // Read the document to be identified
      final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("external/ubl2-order/good/Order_Example-ehf.xml"));
      assertNotNull (aDoc);

      // Main determination
      final DocumentDetails aDD = DDD.findDocumentDetails (aDoc.getDocumentElement ());
      assertNotNull (aDD);

      assertEquals ("no.ehf.g3:order:latest", aDD.getVESID ());
      assertEquals ("EHF Order V3", aDD.getProfileName ());
    }

    {
      // Read the document to be identified
      final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("external/ubl2-order/good/Advanced_Order_Example-ehf.xml"));
      assertNotNull (aDoc);

      // Main determination
      final DocumentDetails aDD = DDD.findDocumentDetails (aDoc.getDocumentElement ());
      assertNotNull (aDD);

      assertEquals ("no.ehf.g3:advanced-order-initiation:latest", aDD.getVESID ());
      assertEquals ("EHF Advanced Order Initiation V3", aDD.getProfileName ());
    }

    {
      // Read the document to be identified
      final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("external/ubl2-order/unknown/Order_Example-ehf-unknownProcessID.xml"));
      assertNotNull (aDoc);

      // Main determination
      final DocumentDetails aDD = DDD.findDocumentDetails (aDoc.getDocumentElement ());
      assertNotNull (aDD);

      assertNull (aDD.getVESID ());
      assertNull (aDD.getProfileName ());
    }
  }

  @Test
  public void testReadAllTestfiles ()
  {
    final DDDSyntaxList aSL = DDDSyntaxList.getDefaultSyntaxList ();

    int nFilesRead = 0;

    // For all syntaxes
    for (final Map.Entry <String, DDDSyntax> aSyntaxEntry : aSL.getAllSyntaxes ().entrySet ())
    {
      final DDDSyntax aSyntax = aSyntaxEntry.getValue ();

      // Search for positive cases for the current syntax
      for (final File f : new FileSystemIterator ("src/test/resources/external/" + aSyntaxEntry.getKey () + "/good")
                                                                                                                    .withFilter (IFileFilter.filenameEndsWith (".xml")))
      {
        LOGGER.info ("Reading as [" + aSyntax.getID () + "] " + f.toString ());
        nFilesRead++;

        // Read as XML
        final Document aDoc = DOMReader.readXMLDOM (f);
        assertNotNull (aDoc);

        // Test all getters
        final ErrorList aErrorList = new ErrorList ();
        for (final EDDDSourceField eGetter : EDDDSourceField.values ())
        {
          final String sValue = aSyntax.getValue (eGetter, aDoc.getDocumentElement (), aErrorList);

          if (false)
            LOGGER.info ("  " + eGetter + " --> " + sValue);
        }

        final DocumentDetails aDetails = DDD.findDocumentDetails (aDoc.getDocumentElement ());
        assertNotNull (aDetails);
        assertNotNull (aDetails.getVESID ());
      }
    }

    assertTrue ("At least the testfiles must have been read", nFilesRead >= 3);
  }

  @Test
  public void testAllBadCases ()
  {
    final DDDSyntaxList aSL = DDDSyntaxList.getDefaultSyntaxList ();

    // For all syntaxes
    for (final Map.Entry <String, DDDSyntax> aSyntaxEntry : aSL.getAllSyntaxes ().entrySet ())
    {
      final DDDSyntax aSyntax = aSyntaxEntry.getValue ();

      // Search for positive cases for the current syntax
      for (final File f : new FileSystemIterator ("src/test/resources/external/" + aSyntaxEntry.getKey () + "/bad")
                                                                                                                   .withFilter (IFileFilter.filenameEndsWith (".xml")))
      {
        LOGGER.info ("Reading as [" + aSyntax.getID () + "] " + f.toString ());

        // Read as XML
        final Document aDoc = DOMReader.readXMLDOM (f);
        assertNotNull (aDoc);

        final DocumentDetails aDetails = DDD.findDocumentDetails (aDoc.getDocumentElement ());
        assertNull (aDetails);
      }
    }
  }
}

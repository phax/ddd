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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.unwrap.DDDDocumentUnwrapperSBDH;
import com.helger.ddd.unwrap.DDDDocumentUnwrapperXHE;
import com.helger.io.resource.ClassPathResource;
import com.helger.xml.serialize.read.DOMReader;

/**
 * Test class for {@link IDDDDocumentUnwrapper}, {@link DDDDocumentUnwrapperSBDH} and
 * {@link DDDDocumentUnwrapperXHE}.
 *
 * @author Philip Helger
 */
public final class DDDDocumentUnwrapperTest
{
  private static final String SBDH_NS = DDDDocumentUnwrapperSBDH.NAMESPACE_URI;
  private static final String XHE_NS = DDDDocumentUnwrapperXHE.NAMESPACE_URI_XHE;
  private static final String XHE_AC_NS = DDDDocumentUnwrapperXHE.NAMESPACE_URI_AC;

  /**
   * Read the base UBL invoice test document.
   *
   * @return The parsed invoice element, never <code>null</code>.
   */
  @NonNull
  private static Element _readBaseInvoice ()
  {
    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("external/ubl2-invoice/good/base-example.xml"));
    assertNotNull (aDoc);
    return aDoc.getDocumentElement ();
  }

  /**
   * Wrap a business document element in an SBDH envelope.
   *
   * @param aBusinessDoc
   *        The business document element to wrap.
   * @return The SBDH root element containing the business document.
   */
  @NonNull
  private static Element _wrapInSBDH (@NonNull final Element aBusinessDoc)
  {
    final Document aDoc = aBusinessDoc.getOwnerDocument ();
    final Element aSBD = aDoc.createElementNS (SBDH_NS, "StandardBusinessDocument");

    // Add a StandardBusinessDocumentHeader child
    final Element aSBDH = aDoc.createElementNS (SBDH_NS, "StandardBusinessDocumentHeader");
    final Element aHeaderVersion = aDoc.createElementNS (SBDH_NS, "HeaderVersion");
    aHeaderVersion.setTextContent ("1.0");
    aSBDH.appendChild (aHeaderVersion);
    aSBD.appendChild (aSBDH);

    // Add the business document as the second child
    aSBD.appendChild (aBusinessDoc);
    return aSBD;
  }

  /**
   * Wrap a business document element in an XHE envelope.
   *
   * @param aBusinessDoc
   *        The business document element to wrap.
   * @return The XHE root element containing the business document.
   */
  @NonNull
  private static Element _wrapInXHE (@NonNull final Element aBusinessDoc)
  {
    final Document aDoc = aBusinessDoc.getOwnerDocument ();
    final Element aXHE = aDoc.createElementNS (XHE_NS, "XHE");

    // Add Payloads -> Payload -> PayloadContent -> business doc
    final Element aPayloads = aDoc.createElementNS (XHE_AC_NS, "xha:Payloads");
    final Element aPayload = aDoc.createElementNS (XHE_AC_NS, "xha:Payload");
    final Element aPayloadContent = aDoc.createElementNS (XHE_AC_NS, "xha:PayloadContent");

    aPayloadContent.appendChild (aBusinessDoc);
    aPayload.appendChild (aPayloadContent);
    aPayloads.appendChild (aPayload);
    aXHE.appendChild (aPayloads);
    return aXHE;
  }

  @NonNull
  private static DocumentDetailsDeterminator _createDDD ()
  {
    return new DocumentDetailsDeterminator (DDDSyntaxList.getDefaultSyntaxList (),
                                            DDDValueProviderList.getDefaultValueProviderList ());
  }

  @Test
  public void testSBDHUnwrapping ()
  {
    final Element aInvoice = _readBaseInvoice ();
    final Element aSBD = _wrapInSBDH (aInvoice);

    final DocumentDetailsDeterminator aDDD = _createDDD ().addDefaultUnwrappers ();
    final DocumentDetails aDD = aDDD.findDocumentDetails (aSBD);
    assertNotNull (aDD);

    assertEquals ("ubl2-invoice", aDD.getSyntaxID ());
    assertNotNull (aDD.getSenderID ());
    assertNotNull (aDD.getReceiverID ());
    assertNotNull (aDD.getDocumentTypeID ());
    assertTrue (aDD.wrappers ().contains ("SBDH"));
    assertFalse (aDD.wrappers ().contains ("XHE"));
  }

  @Test
  public void testXHEUnwrapping ()
  {
    final Element aInvoice = _readBaseInvoice ();
    final Element aXHE = _wrapInXHE (aInvoice);

    final DocumentDetailsDeterminator aDDD = _createDDD ().addDefaultUnwrappers ();
    final DocumentDetails aDD = aDDD.findDocumentDetails (aXHE);
    assertNotNull (aDD);

    assertEquals ("ubl2-invoice", aDD.getSyntaxID ());
    assertNotNull (aDD.getSenderID ());
    assertNotNull (aDD.getReceiverID ());
    assertNotNull (aDD.getDocumentTypeID ());
    assertFalse (aDD.wrappers ().contains ("SBDH"));
    assertTrue (aDD.wrappers ().contains ("XHE"));
  }

  @Test
  public void testRecursiveUnwrapping ()
  {
    final Element aInvoice = _readBaseInvoice ();
    // Wrap invoice in XHE, then wrap XHE in SBDH
    final Element aXHE = _wrapInXHE (aInvoice);
    final Element aSBD = _wrapInSBDH (aXHE);

    final DocumentDetailsDeterminator aDDD = _createDDD ().addDefaultUnwrappers ();
    final DocumentDetails aDD = aDDD.findDocumentDetails (aSBD);
    assertNotNull (aDD);

    assertEquals ("ubl2-invoice", aDD.getSyntaxID ());
    assertTrue (aDD.wrappers ().contains ("SBDH"));
    assertTrue (aDD.wrappers ().contains ("XHE"));
  }

  @Test
  public void testNoUnwrappersRegistered ()
  {
    final Element aInvoice = _readBaseInvoice ();
    final Element aSBD = _wrapInSBDH (aInvoice);

    // No unwrappers registered - should not recognize the SBDH root
    final DocumentDetailsDeterminator aDDD = _createDDD ();
    final DocumentDetails aDD = aDDD.findDocumentDetails (aSBD);
    assertNull (aDD);
  }

  @Test
  public void testPlainDocumentWithUnwrappers ()
  {
    final Element aInvoice = _readBaseInvoice ();

    // Unwrappers registered but input is a plain business document
    final DocumentDetailsDeterminator aDDD = _createDDD ().addDefaultUnwrappers ();
    final DocumentDetails aDD = aDDD.findDocumentDetails (aInvoice);
    assertNotNull (aDD);

    assertEquals ("ubl2-invoice", aDD.getSyntaxID ());
    assertFalse (aDD.wrappers ().contains ("SBDH"));
    assertFalse (aDD.wrappers ().contains ("XHE"));
  }

  @Test
  public void testSBDHNoPayload ()
  {
    final Element aInvoice = _readBaseInvoice ();
    final Document aDoc = aInvoice.getOwnerDocument ();
    final Element aSBD = aDoc.createElementNS (SBDH_NS, "StandardBusinessDocument");
    // Only add header, no payload
    final Element aSBDH = aDoc.createElementNS (SBDH_NS, "StandardBusinessDocumentHeader");
    aSBD.appendChild (aSBDH);

    assertNull (DDDDocumentUnwrapperSBDH.INSTANCE.unwrap (aSBD));
  }

  @Test
  public void testXHENoPayloadContent ()
  {
    final Element aInvoice = _readBaseInvoice ();
    final Document aDoc = aInvoice.getOwnerDocument ();
    final Element aXHE = aDoc.createElementNS (XHE_NS, "XHE");
    // Add empty Payloads
    final Element aPayloads = aDoc.createElementNS (XHE_AC_NS, "xha:Payloads");
    aXHE.appendChild (aPayloads);

    assertNull (DDDDocumentUnwrapperXHE.INSTANCE.unwrap (aXHE));
  }

  @Test
  public void testUnwrapperDoesNotMatchWrongDocument ()
  {
    final Element aInvoice = _readBaseInvoice ();

    // SBDH unwrapper should return null for a non-SBDH document
    assertNull (DDDDocumentUnwrapperSBDH.INSTANCE.unwrap (aInvoice));

    // XHE unwrapper should return null for a non-XHE document
    assertNull (DDDDocumentUnwrapperXHE.INSTANCE.unwrap (aInvoice));
  }

  @Test
  public void testWrappingTypes ()
  {
    assertEquals ("SBDH", DDDDocumentUnwrapperSBDH.INSTANCE.getWrappingType ());
    assertEquals ("XHE", DDDDocumentUnwrapperXHE.INSTANCE.getWrappingType ());
  }
}

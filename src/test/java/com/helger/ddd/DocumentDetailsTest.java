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

import javax.annotation.Nonnull;

import org.junit.Test;
import org.w3c.dom.Element;

import com.helger.commons.system.ENewLineMode;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.xml.XMLFactory;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

/**
 * Test class for class {@link DocumentDetails}
 *
 * @author Philip Helger
 */
public final class DocumentDetailsTest
{
  @Nonnull
  private static DocumentDetails _createDD ()
  {
    final IIdentifierFactory aIF = SimpleIdentifierFactory.INSTANCE;
    return DocumentDetails.builder ()
                          .senderID (aIF.parseParticipantIdentifier ("a::b"))
                          .receiverID (aIF.parseParticipantIdentifier ("c::def"))
                          .documentTypeID (aIF.parseDocumentTypeIdentifier ("bla::fo:o"))
                          .processID (aIF.parseProcessIdentifier ("pro::cess"))
                          .businessDocumentID ("id")
                          .senderName ("sn")
                          .senderCountryCode ("sc")
                          .receiverName ("rn")
                          .receiverCountryCode ("rc")
                          .vesid ("ves")
                          .profileName ("pn")
                          .build ();
  }

  @Test
  public void testBuilderFilled ()
  {
    final DocumentDetails aDD = _createDD ();

    assertNotNull (aDD.getSenderID ());
    assertEquals ("a", aDD.getSenderID ().getScheme ());
    assertEquals ("b", aDD.getSenderID ().getValue ());

    assertNotNull (aDD.getReceiverID ());
    assertEquals ("c", aDD.getReceiverID ().getScheme ());
    assertEquals ("def", aDD.getReceiverID ().getValue ());

    assertNotNull (aDD.getDocumentTypeID ());
    assertEquals ("bla", aDD.getDocumentTypeID ().getScheme ());
    assertEquals ("fo:o", aDD.getDocumentTypeID ().getValue ());

    assertNotNull (aDD.getProcessID ());
    assertEquals ("pro", aDD.getProcessID ().getScheme ());
    assertEquals ("cess", aDD.getProcessID ().getValue ());

    assertEquals ("id", aDD.getBusinessDocumentID ());
    assertEquals ("sn", aDD.getSenderName ());
    assertEquals ("sc", aDD.getSenderCountryCode ());
    assertEquals ("rn", aDD.getReceiverName ());
    assertEquals ("rc", aDD.getReceiverCountryCode ());
    assertEquals ("ves", aDD.getVESID ());
    assertEquals ("pn", aDD.getProfileName ());
  }

  @Test
  public void testBuilderEmpty ()
  {
    final DocumentDetails aDD = DocumentDetails.builder ().build ();

    assertNull (aDD.getSenderID ());
    assertNull (aDD.getReceiverID ());
    assertNull (aDD.getDocumentTypeID ());
    assertNull (aDD.getProcessID ());
    assertNull (aDD.getBusinessDocumentID ());
    assertNull (aDD.getSenderName ());
    assertNull (aDD.getSenderCountryCode ());
    assertNull (aDD.getReceiverName ());
    assertNull (aDD.getReceiverCountryCode ());
    assertNull (aDD.getVESID ());
    assertNull (aDD.getProfileName ());
  }

  @Test
  public void testGetAsJson ()
  {
    final DocumentDetails aDD = _createDD ();
    final var aJson = aDD.getAsJson ();
    assertEquals ("{\"sender\":\"a::b\"," +
                  "\"receiver\":\"c::def\"," +
                  "\"doctype\":\"bla::fo:o\"," +
                  "\"process\":\"pro::cess\"," +
                  "\"bdid\":\"id\"," +
                  "\"senderName\":\"sn\"," +
                  "\"senderCountryCode\":\"sc\"," +
                  "\"receiverName\":\"rn\"," +
                  "\"receiverCountryCode\":\"rc\"," +
                  "\"vesid\":\"ves\"," +
                  "\"profileName\":\"pn\"}",
                  aJson.getAsJsonString ());
  }

  @Test
  public void testAppendToMicroElement ()
  {
    final DocumentDetails aDD = _createDD ();
    final IMicroElement eRoot = new MicroElement ("root");
    aDD.appendToMicroElement (eRoot);
    assertEquals ("<root>\r\n" +
                  "  <SenderID>a::b</SenderID>\r\n" +
                  "  <ReceiverID>c::def</ReceiverID>\r\n" +
                  "  <DocTypeID>bla::fo:o</DocTypeID>\r\n" +
                  "  <ProcessID>pro::cess</ProcessID>\r\n" +
                  "  <BusinessDocumentID>id</BusinessDocumentID>\r\n" +
                  "  <SenderName>sn</SenderName>\r\n" +
                  "  <SenderCountryCode>sc</SenderCountryCode>\r\n" +
                  "  <ReceiverName>rn</ReceiverName>\r\n" +
                  "  <ReceiverCountryCode>rc</ReceiverCountryCode>\r\n" +
                  "  <VESID>ves</VESID>\r\n" +
                  "  <ProfileName>pn</ProfileName>\r\n" +
                  "</root>\r\n",
                  MicroWriter.getNodeAsString (eRoot, new XMLWriterSettings ().setNewLineMode (ENewLineMode.WINDOWS)));
  }

  @Test
  public void testAppendToMicroElementNS ()
  {
    final DocumentDetails aDD = _createDD ();
    final IMicroElement eRoot = new MicroElement ("urn:helger:test", "root");
    aDD.appendToMicroElement (eRoot);
    assertEquals ("<root xmlns=\"urn:helger:test\">\r\n" +
                  "  <SenderID>a::b</SenderID>\r\n" +
                  "  <ReceiverID>c::def</ReceiverID>\r\n" +
                  "  <DocTypeID>bla::fo:o</DocTypeID>\r\n" +
                  "  <ProcessID>pro::cess</ProcessID>\r\n" +
                  "  <BusinessDocumentID>id</BusinessDocumentID>\r\n" +
                  "  <SenderName>sn</SenderName>\r\n" +
                  "  <SenderCountryCode>sc</SenderCountryCode>\r\n" +
                  "  <ReceiverName>rn</ReceiverName>\r\n" +
                  "  <ReceiverCountryCode>rc</ReceiverCountryCode>\r\n" +
                  "  <VESID>ves</VESID>\r\n" +
                  "  <ProfileName>pn</ProfileName>\r\n" +
                  "</root>\r\n",
                  MicroWriter.getNodeAsString (eRoot, new XMLWriterSettings ().setNewLineMode (ENewLineMode.WINDOWS)));
  }

  @Test
  public void testAppendToDOMElement ()
  {
    final DocumentDetails aDD = _createDD ();
    final var aDoc = XMLFactory.newDocument ();
    final var eRoot = (Element) aDoc.appendChild (aDoc.createElement ("root"));
    aDD.appendToDOMElement (eRoot);
    assertEquals ("<root>\r\n" +
                  "  <SenderID>a::b</SenderID>\r\n" +
                  "  <ReceiverID>c::def</ReceiverID>\r\n" +
                  "  <DocTypeID>bla::fo:o</DocTypeID>\r\n" +
                  "  <ProcessID>pro::cess</ProcessID>\r\n" +
                  "  <BusinessDocumentID>id</BusinessDocumentID>\r\n" +
                  "  <SenderName>sn</SenderName>\r\n" +
                  "  <SenderCountryCode>sc</SenderCountryCode>\r\n" +
                  "  <ReceiverName>rn</ReceiverName>\r\n" +
                  "  <ReceiverCountryCode>rc</ReceiverCountryCode>\r\n" +
                  "  <VESID>ves</VESID>\r\n" +
                  "  <ProfileName>pn</ProfileName>\r\n" +
                  "</root>\r\n",
                  XMLWriter.getNodeAsString (eRoot, new XMLWriterSettings ().setNewLineMode (ENewLineMode.WINDOWS)));
  }

  @Test
  public void testAppendToDOMElementNS ()
  {
    final DocumentDetails aDD = _createDD ();
    final var aDoc = XMLFactory.newDocument ();
    final var eRoot = (Element) aDoc.appendChild (aDoc.createElementNS ("urn:helger:test", "root"));
    aDD.appendToDOMElement (eRoot);
    assertEquals ("<root xmlns=\"urn:helger:test\">\r\n" +
                  "  <SenderID>a::b</SenderID>\r\n" +
                  "  <ReceiverID>c::def</ReceiverID>\r\n" +
                  "  <DocTypeID>bla::fo:o</DocTypeID>\r\n" +
                  "  <ProcessID>pro::cess</ProcessID>\r\n" +
                  "  <BusinessDocumentID>id</BusinessDocumentID>\r\n" +
                  "  <SenderName>sn</SenderName>\r\n" +
                  "  <SenderCountryCode>sc</SenderCountryCode>\r\n" +
                  "  <ReceiverName>rn</ReceiverName>\r\n" +
                  "  <ReceiverCountryCode>rc</ReceiverCountryCode>\r\n" +
                  "  <VESID>ves</VESID>\r\n" +
                  "  <ProfileName>pn</ProfileName>\r\n" +
                  "</root>\r\n",
                  XMLWriter.getNodeAsString (eRoot, new XMLWriterSettings ().setNewLineMode (ENewLineMode.WINDOWS)));
  }
}

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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.SimpleIdentifierFactory;

/**
 * Test class for class {@link DocumentDetails}
 *
 * @author Philip Helger
 */
public final class DocumentDetailsTest
{
  @Test
  public void testBuilderFilled ()
  {
    final IIdentifierFactory aIF = SimpleIdentifierFactory.INSTANCE;

    final DocumentDetails aDD = DocumentDetails.builder ()
                                               .senderID (aIF.parseParticipantIdentifier ("a::b"))
                                               .receiverID (aIF.parseParticipantIdentifier ("c::def"))
                                               .documentTypeID (aIF.parseDocumentTypeIdentifier ("bla::fo:o"))
                                               .processID (aIF.parseProcessIdentifier ("pro::cess"))
                                               .businessDocumentID ("id")
                                               .senderName ("sn")
                                               .receiverName ("rn")
                                               .vesid ("ves")
                                               .profileName ("pn")
                                               .build ();

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
    assertEquals ("rn", aDD.getReceiverName ());
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
    assertNull (aDD.getReceiverName ());
    assertNull (aDD.getVESID ());
    assertNull (aDD.getProfileName ());
  }
}

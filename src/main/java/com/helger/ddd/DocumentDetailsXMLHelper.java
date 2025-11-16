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
package com.helger.ddd;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.xml.XMLHelper;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.util.MicroHelper;

/**
 * Helper class to work with the XML binding.
 *
 * @author Philip Helger
 * @since 0.5.0
 */
@Immutable
public final class DocumentDetailsXMLHelper
{
  public static final String XML_SYNTAX_ID = "SyntaxID";
  public static final String XML_SYNTAX_VERSION = "SyntaxVersion";
  public static final String XML_SENDER_ID = "SenderID";
  public static final String XML_RECEIVER_ID = "ReceiverID";
  public static final String XML_DOC_TYPE_ID = "DocTypeID";
  public static final String XML_PROCESS_ID = "ProcessID";
  public static final String XML_CUSTOMIZATION_ID = "CustomizationID";
  public static final String XML_BUSINESS_DOCUMENT_ID = "BusinessDocumentID";
  public static final String XML_SENDER_NAME = "SenderName";
  public static final String XML_SENDER_COUNTRY_CODE = "SenderCountryCode";
  public static final String XML_RECEIVER_NAME = "ReceiverName";
  public static final String XML_RECEIVER_COUNTRY_CODE = "ReceiverCountryCode";
  public static final String XML_VESID = "VESID";
  public static final String XML_PROFILE_NAME = "ProfileName";
  public static final String XML_FLAG = "Flag";

  private DocumentDetailsXMLHelper ()
  {}

  /**
   * Append all document details to an existing IMicroElement.
   *
   * @param aDD
   *        The Document Details to be converted. May not be <code>null</code>
   * @param aTarget
   *        The micro element to append to. May not be <code>null</code>.
   */
  public static void appendToMicroElement (@NonNull final DocumentDetails aDD, @NonNull final IMicroElement aTarget)
  {
    ValueEnforcer.notNull (aDD, "DocumentDetails");
    ValueEnforcer.notNull (aTarget, "Target");

    final String sNamespaceURI = aTarget.getNamespaceURI ();
    if (aDD.hasSyntaxID ())
      aTarget.addElementNS (sNamespaceURI, XML_SYNTAX_ID).addText (aDD.getSyntaxID ());
    if (aDD.hasSyntaxVersion ())
      aTarget.addElementNS (sNamespaceURI, XML_SYNTAX_VERSION).addText (aDD.getSyntaxVersion ());
    if (aDD.hasSenderID ())
      aTarget.addElementNS (sNamespaceURI, XML_SENDER_ID).addText (aDD.getSenderID ().getURIEncoded ());
    if (aDD.hasReceiverID ())
      aTarget.addElementNS (sNamespaceURI, XML_RECEIVER_ID).addText (aDD.getReceiverID ().getURIEncoded ());
    if (aDD.hasDocumentTypeID ())
      aTarget.addElementNS (sNamespaceURI, XML_DOC_TYPE_ID).addText (aDD.getDocumentTypeID ().getURIEncoded ());
    if (aDD.hasProcessID ())
      aTarget.addElementNS (sNamespaceURI, XML_PROCESS_ID).addText (aDD.getProcessID ().getURIEncoded ());
    if (aDD.hasCustomizationID ())
      aTarget.addElementNS (sNamespaceURI, XML_CUSTOMIZATION_ID).addText (aDD.getCustomizationID ());
    if (aDD.hasBusinessDocumentID ())
      aTarget.addElementNS (sNamespaceURI, XML_BUSINESS_DOCUMENT_ID).addText (aDD.getBusinessDocumentID ());
    if (aDD.hasSenderName ())
      aTarget.addElementNS (sNamespaceURI, XML_SENDER_NAME).addText (aDD.getSenderName ());
    if (aDD.hasSenderCountryCode ())
      aTarget.addElementNS (sNamespaceURI, XML_SENDER_COUNTRY_CODE).addText (aDD.getSenderCountryCode ());
    if (aDD.hasReceiverName ())
      aTarget.addElementNS (sNamespaceURI, XML_RECEIVER_NAME).addText (aDD.getReceiverName ());
    if (aDD.hasReceiverCountryCode ())
      aTarget.addElementNS (sNamespaceURI, XML_RECEIVER_COUNTRY_CODE).addText (aDD.getReceiverCountryCode ());
    if (aDD.hasVESID ())
      aTarget.addElementNS (sNamespaceURI, XML_VESID).addText (aDD.getVESID ());
    if (aDD.hasProfileName ())
      aTarget.addElementNS (sNamespaceURI, XML_PROFILE_NAME).addText (aDD.getProfileName ());
    if (aDD.hasFlags ())
      for (final String sFlag : aDD.flags ())
        aTarget.addElementNS (sNamespaceURI, XML_FLAG).addText (sFlag);
  }

  /**
   * Convert a XML element back to document details
   *
   * @param aObj
   *        The XML element to be converted back. May be <code>null</code>
   * @param aIF
   *        The identifier factory that should be used to parse the participant, document type and
   *        process identifiers. May not be <code>null</code>.
   * @return <code>null</code> if the source object is <code>null</code>.
   */
  @Nullable
  public static DocumentDetails getAsDocumentDetails (@Nullable final IMicroElement aObj,
                                                      @NonNull final IIdentifierFactory aIF)
  {
    ValueEnforcer.notNull (aIF, "IdentifierFactory");

    if (aObj == null)
      return null;

    final ICommonsList <IMicroElement> aFlags = aObj.getAllChildElements (XML_FLAG);
    return DocumentDetails.builder ()
                          .syntaxID (MicroHelper.getChildTextContent (aObj, XML_SYNTAX_ID))
                          .syntaxVersion (MicroHelper.getChildTextContent (aObj, XML_SYNTAX_VERSION))
                          .senderID (aIF.parseParticipantIdentifier (MicroHelper.getChildTextContent (aObj,
                                                                                                      XML_SENDER_ID)))
                          .receiverID (aIF.parseParticipantIdentifier (MicroHelper.getChildTextContent (aObj,
                                                                                                        XML_RECEIVER_ID)))
                          .documentTypeID (aIF.parseDocumentTypeIdentifier (MicroHelper.getChildTextContent (aObj,
                                                                                                             XML_DOC_TYPE_ID)))
                          .processID (aIF.parseProcessIdentifier (MicroHelper.getChildTextContent (aObj,
                                                                                                   XML_PROCESS_ID)))
                          .customizationID (MicroHelper.getChildTextContent (aObj, XML_CUSTOMIZATION_ID))
                          .businessDocumentID (MicroHelper.getChildTextContent (aObj, XML_BUSINESS_DOCUMENT_ID))
                          .senderName (MicroHelper.getChildTextContent (aObj, XML_SENDER_NAME))
                          .senderCountryCode (MicroHelper.getChildTextContent (aObj, XML_SENDER_COUNTRY_CODE))
                          .receiverName (MicroHelper.getChildTextContent (aObj, XML_RECEIVER_NAME))
                          .receiverCountryCode (MicroHelper.getChildTextContent (aObj, XML_RECEIVER_COUNTRY_CODE))
                          .vesid (MicroHelper.getChildTextContent (aObj, XML_VESID))
                          .profileName (MicroHelper.getChildTextContent (aObj, XML_PROFILE_NAME))
                          .flags (aFlags == null ? null : aFlags.getAllMapped (IMicroElement::getTextContentTrimmed))
                          .build ();
  }

  /**
   * Append all document details to an existing DOM element.
   *
   * @param aDD
   *        The Document Details to be converted. May not be <code>null</code>
   * @param aTarget
   *        The DOM element to append to. May not be <code>null</code>.
   */
  public static void appendToDOMElement (@NonNull final DocumentDetails aDD, @NonNull final Element aTarget)
  {
    ValueEnforcer.notNull (aDD, "DocumentDetails");
    ValueEnforcer.notNull (aTarget, "Target");

    final String sNamespaceURI = aTarget.getNamespaceURI ();
    final Document aDoc = aTarget.getOwnerDocument ();
    final Function <String, Node> fCreate = sNamespaceURI == null ? x -> aDoc.createElement (x) : x -> aDoc
                                                                                                           .createElementNS (sNamespaceURI,
                                                                                                                             x);
    final BiConsumer <String, String> fAppend = (name, val) -> aTarget.appendChild (fCreate.apply (name))
                                                                      .appendChild (aDoc.createTextNode (val));

    if (aDD.hasSyntaxID ())
      fAppend.accept (XML_SYNTAX_ID, aDD.getSyntaxID ());
    if (aDD.hasSyntaxVersion ())
      fAppend.accept (XML_SYNTAX_VERSION, aDD.getSyntaxVersion ());
    if (aDD.hasSenderID ())
      fAppend.accept (XML_SENDER_ID, aDD.getSenderID ().getURIEncoded ());
    if (aDD.hasReceiverID ())
      fAppend.accept (XML_RECEIVER_ID, aDD.getReceiverID ().getURIEncoded ());
    if (aDD.hasDocumentTypeID ())
      fAppend.accept (XML_DOC_TYPE_ID, aDD.getDocumentTypeID ().getURIEncoded ());
    if (aDD.hasProcessID ())
      fAppend.accept (XML_PROCESS_ID, aDD.getProcessID ().getURIEncoded ());
    if (aDD.hasCustomizationID ())
      fAppend.accept (XML_CUSTOMIZATION_ID, aDD.getCustomizationID ());
    if (aDD.hasBusinessDocumentID ())
      fAppend.accept (XML_BUSINESS_DOCUMENT_ID, aDD.getBusinessDocumentID ());
    if (aDD.hasSenderName ())
      fAppend.accept (XML_SENDER_NAME, aDD.getSenderName ());
    if (aDD.hasSenderCountryCode ())
      fAppend.accept (XML_SENDER_COUNTRY_CODE, aDD.getSenderCountryCode ());
    if (aDD.hasReceiverName ())
      fAppend.accept (XML_RECEIVER_NAME, aDD.getReceiverName ());
    if (aDD.hasReceiverCountryCode ())
      fAppend.accept (XML_RECEIVER_COUNTRY_CODE, aDD.getReceiverCountryCode ());
    if (aDD.hasVESID ())
      fAppend.accept (XML_VESID, aDD.getVESID ());
    if (aDD.hasProfileName ())
      fAppend.accept (XML_PROFILE_NAME, aDD.getProfileName ());
    if (aDD.hasFlags ())
      for (final String sFlag : aDD.flags ())
        fAppend.accept (XML_FLAG, sFlag);
  }

  /**
   * Convert a XML element back to document details
   *
   * @param aObj
   *        The XML element to be converted back. May be <code>null</code>
   * @param aIF
   *        The identifier factory that should be used to parse the participant, document type and
   *        process identifiers. May not be <code>null</code>.
   * @return <code>null</code> if the source object is <code>null</code>.
   */
  @Nullable
  public static DocumentDetails getAsDocumentDetails (@Nullable final Element aObj,
                                                      @NonNull final IIdentifierFactory aIF)
  {
    ValueEnforcer.notNull (aIF, "IdentifierFactory");

    if (aObj == null)
      return null;

    final BiFunction <Node, String, String> fGet = (node, name) -> {
      final Element e = XMLHelper.getFirstChildElementOfName (node, name);
      return e == null ? null : e.getTextContent ();
    };

    final ICommonsList <Element> aFlags = new CommonsArrayList <> (XMLHelper.getChildElementIterator (aObj, XML_FLAG));
    return DocumentDetails.builder ()
                          .syntaxID (fGet.apply (aObj, XML_SYNTAX_ID))
                          .syntaxVersion (fGet.apply (aObj, XML_SYNTAX_VERSION))
                          .senderID (aIF.parseParticipantIdentifier (fGet.apply (aObj, XML_SENDER_ID)))
                          .receiverID (aIF.parseParticipantIdentifier (fGet.apply (aObj, XML_RECEIVER_ID)))
                          .documentTypeID (aIF.parseDocumentTypeIdentifier (fGet.apply (aObj, XML_DOC_TYPE_ID)))
                          .processID (aIF.parseProcessIdentifier (fGet.apply (aObj, XML_PROCESS_ID)))
                          .customizationID (fGet.apply (aObj, XML_CUSTOMIZATION_ID))
                          .businessDocumentID (fGet.apply (aObj, XML_BUSINESS_DOCUMENT_ID))
                          .senderName (fGet.apply (aObj, XML_SENDER_NAME))
                          .senderCountryCode (fGet.apply (aObj, XML_SENDER_COUNTRY_CODE))
                          .receiverName (fGet.apply (aObj, XML_RECEIVER_NAME))
                          .receiverCountryCode (fGet.apply (aObj, XML_RECEIVER_COUNTRY_CODE))
                          .vesid (fGet.apply (aObj, XML_VESID))
                          .profileName (fGet.apply (aObj, XML_PROFILE_NAME))
                          .flags (aFlags.getAllMapped (Element::getTextContent))
                          .build ();
  }
}

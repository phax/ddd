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

import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppolid.factory.IIdentifierFactory;

/**
 * Helper class to work with the JSON binding.
 *
 * @author Philip Helger
 * @since 0.5.0
 */
@Immutable
public final class DocumentDetailsJsonHelper
{
  private static final String JSON_SYNTAX_ID = "syntaxID";
  private static final String JSON_SYNTAX_VERSION = "syntaxVersion";
  private static final String JSON_SENDER_ID = "sender";
  private static final String JSON_RECEIVER_ID = "receiver";
  private static final String JSON_DOC_TYPE_ID = "doctype";
  private static final String JSON_PROCESS_ID = "process";
  private static final String JSON_CUSTOMIZATION_ID = "customizationID";
  private static final String JSON_BUSINESS_DOCUMENT_ID = "bdid";
  private static final String JSON_SENDER_NAME = "senderName";
  private static final String JSON_SENDER_COUNTRY_CODE = "senderCountryCode";
  private static final String JSON_RECEIVER_NAME = "receiverName";
  private static final String JSON_RECEIVER_COUNTRY_CODE = "receiverCountryCode";
  private static final String JSON_VESID = "vesid";
  private static final String JSON_PROFILE_NAME = "profileName";
  private static final String JSON_FLAGS = "flags";

  private DocumentDetailsJsonHelper ()
  {}

  /**
   * Convert all document details as a JSON object.
   *
   * @param aDD
   *        The Document Details to be converted. May not be <code>null</code>
   * @return Never <code>null</code>.
   */
  @NonNull
  public static IJsonObject getAsJson (@NonNull final DocumentDetails aDD)
  {
    ValueEnforcer.notNull (aDD, "DocumentDetails");

    final IJsonObject ret = new JsonObject ();
    if (aDD.hasSyntaxID ())
      ret.add (JSON_SYNTAX_ID, aDD.getSyntaxID ());
    if (aDD.hasSyntaxVersion ())
      ret.add (JSON_SYNTAX_VERSION, aDD.getSyntaxVersion ());
    if (aDD.hasSenderID ())
      ret.add (JSON_SENDER_ID, aDD.getSenderID ().getURIEncoded ());
    if (aDD.getReceiverID () != null)
      ret.add (JSON_RECEIVER_ID, aDD.getReceiverID ().getURIEncoded ());
    if (aDD.hasDocumentTypeID ())
      ret.add (JSON_DOC_TYPE_ID, aDD.getDocumentTypeID ().getURIEncoded ());
    if (aDD.hasProcessID ())
      ret.add (JSON_PROCESS_ID, aDD.getProcessID ().getURIEncoded ());
    if (aDD.hasCustomizationID ())
      ret.add (JSON_CUSTOMIZATION_ID, aDD.getCustomizationID ());
    if (aDD.hasBusinessDocumentID ())
      ret.add (JSON_BUSINESS_DOCUMENT_ID, aDD.getBusinessDocumentID ());
    if (aDD.hasSenderName ())
      ret.add (JSON_SENDER_NAME, aDD.getSenderName ());
    if (aDD.hasSenderCountryCode ())
      ret.add (JSON_SENDER_COUNTRY_CODE, aDD.getSenderCountryCode ());
    if (aDD.hasReceiverName ())
      ret.add (JSON_RECEIVER_NAME, aDD.getReceiverName ());
    if (aDD.hasReceiverCountryCode ())
      ret.add (JSON_RECEIVER_COUNTRY_CODE, aDD.getReceiverCountryCode ());
    if (aDD.hasVESID ())
      ret.add (JSON_VESID, aDD.getVESID ());
    if (aDD.hasProfileName ())
      ret.add (JSON_PROFILE_NAME, aDD.getProfileName ());
    if (aDD.hasFlags ())
      ret.add (JSON_FLAGS, new JsonArray ().addAll (aDD.flags ()));
    return ret;
  }

  /**
   * Convert a JSON object back to document details
   *
   * @param aObj
   *        The JSON object to be converted back. May be <code>null</code>
   * @param aIF
   *        The identifier factory that should be used to parse the participant, document type and
   *        process identifiers. May not be <code>null</code>.
   * @return <code>null</code> if the source object is <code>null</code>.
   */
  @Nullable
  public static DocumentDetails getAsDocumentDetails (@Nullable final IJsonObject aObj,
                                                      @NonNull final IIdentifierFactory aIF)
  {
    ValueEnforcer.notNull (aIF, "IdentifierFactory");

    if (aObj == null)
      return null;

    final IJsonArray aFlags = aObj.getAsArray (JSON_FLAGS);
    return DocumentDetails.builder ()
                          .syntaxID (aObj.getAsString (JSON_SYNTAX_ID))
                          .syntaxVersion (aObj.getAsString (JSON_SYNTAX_VERSION))
                          .senderID (aIF.parseParticipantIdentifier (aObj.getAsString (JSON_SENDER_ID)))
                          .receiverID (aIF.parseParticipantIdentifier (aObj.getAsString (JSON_RECEIVER_ID)))
                          .documentTypeID (aIF.parseDocumentTypeIdentifier (aObj.getAsString (JSON_DOC_TYPE_ID)))
                          .processID (aIF.parseProcessIdentifier (aObj.getAsString (JSON_PROCESS_ID)))
                          .customizationID (aObj.getAsString (JSON_CUSTOMIZATION_ID))
                          .businessDocumentID (aObj.getAsString (JSON_BUSINESS_DOCUMENT_ID))
                          .senderName (aObj.getAsString (JSON_SENDER_NAME))
                          .senderCountryCode (aObj.getAsString (JSON_SENDER_COUNTRY_CODE))
                          .receiverName (aObj.getAsString (JSON_RECEIVER_NAME))
                          .receiverCountryCode (aObj.getAsString (JSON_RECEIVER_COUNTRY_CODE))
                          .vesid (aObj.getAsString (JSON_VESID))
                          .profileName (aObj.getAsString (JSON_PROFILE_NAME))
                          .flags (aFlags == null ? null : aFlags.getAll ()
                                                                .getAllMapped ((Predicate <? super IJson>) IJson::isValue,
                                                                               x -> x.getAsValue ().getAsString ()))
                          .build ();
  }
}

package com.helger.ddd;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;

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
  private static final String JSON_DOCTYPE_ID = "doctype";
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
  @Nonnull
  public static IJsonObject getAsJson (@Nonnull final DocumentDetails aDD)
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
      ret.add (JSON_DOCTYPE_ID, aDD.getDocumentTypeID ().getURIEncoded ());
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
      ret.addJson (JSON_FLAGS, new JsonArray ().addAll (aDD.flags ()));
    return ret;
  }
}

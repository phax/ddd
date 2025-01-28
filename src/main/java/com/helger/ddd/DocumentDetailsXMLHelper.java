package com.helger.ddd;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.xml.microdom.IMicroElement;

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
  public static void appendToMicroElement (@Nonnull final DocumentDetails aDD, @Nonnull final IMicroElement aTarget)
  {
    ValueEnforcer.notNull (aDD, "DocumentDetails");
    ValueEnforcer.notNull (aTarget, "Target");

    final String sNamespaceURI = aTarget.getNamespaceURI ();
    if (aDD.hasSyntaxID ())
      aTarget.appendElement (sNamespaceURI, XML_SYNTAX_ID).appendText (aDD.getSyntaxID ());
    if (aDD.hasSyntaxVersion ())
      aTarget.appendElement (sNamespaceURI, XML_SYNTAX_VERSION).appendText (aDD.getSyntaxVersion ());
    if (aDD.hasSenderID ())
      aTarget.appendElement (sNamespaceURI, XML_SENDER_ID).appendText (aDD.getSenderID ().getURIEncoded ());
    if (aDD.hasReceiverID ())
      aTarget.appendElement (sNamespaceURI, XML_RECEIVER_ID).appendText (aDD.getReceiverID ().getURIEncoded ());
    if (aDD.hasDocumentTypeID ())
      aTarget.appendElement (sNamespaceURI, XML_DOC_TYPE_ID).appendText (aDD.getDocumentTypeID ().getURIEncoded ());
    if (aDD.hasProcessID ())
      aTarget.appendElement (sNamespaceURI, XML_PROCESS_ID).appendText (aDD.getProcessID ().getURIEncoded ());
    if (aDD.hasCustomizationID ())
      aTarget.appendElement (sNamespaceURI, XML_CUSTOMIZATION_ID).appendText (aDD.getCustomizationID ());
    if (aDD.hasBusinessDocumentID ())
      aTarget.appendElement (sNamespaceURI, XML_BUSINESS_DOCUMENT_ID).appendText (aDD.getBusinessDocumentID ());
    if (aDD.hasSenderName ())
      aTarget.appendElement (sNamespaceURI, XML_SENDER_NAME).appendText (aDD.getSenderName ());
    if (aDD.hasSenderCountryCode ())
      aTarget.appendElement (sNamespaceURI, XML_SENDER_COUNTRY_CODE).appendText (aDD.getSenderCountryCode ());
    if (aDD.hasReceiverName ())
      aTarget.appendElement (sNamespaceURI, XML_RECEIVER_NAME).appendText (aDD.getReceiverName ());
    if (aDD.hasReceiverCountryCode ())
      aTarget.appendElement (sNamespaceURI, XML_RECEIVER_COUNTRY_CODE).appendText (aDD.getReceiverCountryCode ());
    if (aDD.hasVESID ())
      aTarget.appendElement (sNamespaceURI, XML_VESID).appendText (aDD.getVESID ());
    if (aDD.hasProfileName ())
      aTarget.appendElement (sNamespaceURI, XML_PROFILE_NAME).appendText (aDD.getProfileName ());
    if (aDD.hasFlags ())
      for (final String sFlag : aDD.flags ())
        aTarget.appendElement (sNamespaceURI, XML_FLAG).appendText (sFlag);
  }

  /**
   * Append all document details to an existing DOM element.
   *
   * @param aDD
   *        The Document Details to be converted. May not be <code>null</code>
   * @param aTarget
   *        The DOM element to append to. May not be <code>null</code>.
   */
  public static void appendToDOMElement (@Nonnull final DocumentDetails aDD, @Nonnull final Element aTarget)
  {
    ValueEnforcer.notNull (aDD, "DocumentDetails");
    ValueEnforcer.notNull (aTarget, "Target");

    final String sNamespaceURI = aTarget.getNamespaceURI ();
    final Document aDoc = aTarget.getOwnerDocument ();
    final Function <String, Node> fCreate = sNamespaceURI == null ? x -> aDoc.createElement (x)
                                                                  : x -> aDoc.createElementNS (sNamespaceURI, x);
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
}

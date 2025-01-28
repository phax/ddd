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

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.microdom.IMicroElement;

/**
 * Document details determined from the payload, with all fields optional.
 *
 * @author Philip Helger
 */
@Immutable
public class DocumentDetails
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

  private final String m_sSyntaxID;
  private final String m_sSyntaxVersion;
  private final IParticipantIdentifier m_aSenderID;
  private final IParticipantIdentifier m_aReceiverID;
  private final IDocumentTypeIdentifier m_aDocTypeID;
  private final IProcessIdentifier m_aProcessID;
  private final String m_sCustomizationID;
  private final String m_sBusinessDocumentID;
  private final String m_sSenderName;
  private final String m_sSenderCountryCode;
  private final String m_sReceiverName;
  private final String m_sReceiverCountryCode;
  private final String m_sVESID;
  private final String m_sProfileName;
  private final ICommonsOrderedSet <String> m_aFlags;

  /**
   * Internal constructor. All fields are optional. Don't use this ctor
   * directly, use {@link #builder()} instead. <br>
   * Note: reordered parameters in 0.4.1
   *
   * @param sSyntaxID
   *        DDD syntax ID. Added in 0.3.2.
   * @param sSyntaxVersion
   *        Syntax version from the Document Type ID.
   * @param aSenderID
   *        Sender ID.
   * @param aReceiverID
   *        Receiver ID.
   * @param aDocTypeID
   *        Document Type ID.
   * @param aProcessID
   *        Process ID.
   * @param sCustomizationID
   *        Customization ID from the Document Type ID.
   * @param sBusinessDocumentID
   *        Business document ID
   * @param sSenderName
   *        Sender name
   * @param sSenderCountryCode
   *        Sender country code
   * @param sReceiverName
   *        Receiver name
   * @param sReceiverCountryCode
   *        Receiver country code
   * @param sVESID
   *        VESID to use
   * @param sProfileName
   *        Profile filename
   * @param aFlags
   *        The determined flags. Never <code>null</code> but maybe empty.
   */
  protected DocumentDetails (@Nullable final String sSyntaxID,
                             @Nullable final String sSyntaxVersion,
                             @Nullable final IParticipantIdentifier aSenderID,
                             @Nullable final IParticipantIdentifier aReceiverID,
                             @Nullable final IDocumentTypeIdentifier aDocTypeID,
                             @Nullable final IProcessIdentifier aProcessID,
                             @Nullable final String sCustomizationID,
                             @Nullable final String sBusinessDocumentID,
                             @Nullable final String sSenderName,
                             @Nullable final String sSenderCountryCode,
                             @Nullable final String sReceiverName,
                             @Nullable final String sReceiverCountryCode,
                             @Nullable final String sVESID,
                             @Nullable final String sProfileName,
                             @Nonnull final ICommonsOrderedSet <String> aFlags)
  {
    m_sSyntaxID = sSyntaxID;
    m_aSenderID = aSenderID;
    m_aReceiverID = aReceiverID;
    m_aDocTypeID = aDocTypeID;
    m_sCustomizationID = sCustomizationID;
    m_sSyntaxVersion = sSyntaxVersion;
    m_aProcessID = aProcessID;
    m_sBusinessDocumentID = sBusinessDocumentID;
    m_sSenderName = sSenderName;
    m_sSenderCountryCode = sSenderCountryCode;
    m_sReceiverName = sReceiverName;
    m_sReceiverCountryCode = sReceiverCountryCode;
    m_sVESID = sVESID;
    m_sProfileName = sProfileName;
    m_aFlags = aFlags.getClone ();
  }

  public final boolean hasSyntaxID ()
  {
    return StringHelper.hasText (m_sSyntaxID);
  }

  @Nullable
  public final String getSyntaxID ()
  {
    return m_sSyntaxID;
  }

  public final boolean hasSyntaxVersion ()
  {
    return StringHelper.hasText (m_sSyntaxVersion);
  }

  /**
   * @return The syntax version contained in the Document Type ID. May be
   *         <code>null</code>.
   * @since 0.2.2
   */
  @Nullable
  public final String getSyntaxVersion ()
  {
    return m_sSyntaxVersion;
  }

  public final boolean hasSenderID ()
  {
    return m_aSenderID != null;
  }

  /**
   * @return The C1 sender ID. May be <code>null</code>.
   */
  @Nullable
  public final IParticipantIdentifier getSenderID ()
  {
    return m_aSenderID;
  }

  public final boolean hasReceiverID ()
  {
    return m_aReceiverID != null;
  }

  /**
   * @return The C4 receiver ID. May be <code>null</code>.
   */
  @Nullable
  public final IParticipantIdentifier getReceiverID ()
  {
    return m_aReceiverID;
  }

  public final boolean hasDocumentTypeID ()
  {
    return m_aDocTypeID != null;
  }

  /**
   * @return The document type ID. May be <code>null</code>.
   */
  @Nullable
  public final IDocumentTypeIdentifier getDocumentTypeID ()
  {
    return m_aDocTypeID;
  }

  public final boolean hasProcessID ()
  {
    return m_aProcessID != null;
  }

  /**
   * @return The process ID. May be <code>null</code>.
   */
  @Nullable
  public final IProcessIdentifier getProcessID ()
  {
    return m_aProcessID;
  }

  public final boolean hasCustomizationID ()
  {
    return StringHelper.hasText (m_sCustomizationID);
  }

  /**
   * @return The customization ID contained in the Document Type ID. May be
   *         <code>null</code>.
   * @since 0.2.2
   */
  @Nullable
  public final String getCustomizationID ()
  {
    return m_sCustomizationID;
  }

  /**
   * @return <code>true</code> if all key fields are present
   * @deprecated Because the definition of "key field" is too vague
   */
  @Deprecated (forRemoval = true, since = "0.1.4")
  public final boolean areAllKeyFieldsPresent ()
  {
    return hasSenderID () && hasReceiverID () && hasDocumentTypeID () && hasProcessID () && hasVESID ();
  }

  public final boolean hasBusinessDocumentID ()
  {
    return StringHelper.hasText (m_sBusinessDocumentID);
  }

  /**
   * @return The business document ID (e.g. Invoice number). May be
   *         <code>null</code>.
   */
  @Nullable
  public final String getBusinessDocumentID ()
  {
    return m_sBusinessDocumentID;
  }

  public final boolean hasSenderName ()
  {
    return StringHelper.hasText (m_sSenderName);
  }

  /**
   * @return The human readable sender name. May be <code>null</code>.
   */
  @Nullable
  public final String getSenderName ()
  {
    return m_sSenderName;
  }

  public final boolean hasSenderCountryCode ()
  {
    return StringHelper.hasText (m_sSenderCountryCode);
  }

  /**
   * @return The sender country code. May be <code>null</code>.
   */
  @Nullable
  public final String getSenderCountryCode ()
  {
    return m_sSenderCountryCode;
  }

  public final boolean hasReceiverName ()
  {
    return StringHelper.hasText (m_sReceiverName);
  }

  /**
   * @return The human readable receiver name. May be <code>null</code>.
   */
  @Nullable
  public final String getReceiverName ()
  {
    return m_sReceiverName;
  }

  public final boolean hasReceiverCountryCode ()
  {
    return StringHelper.hasText (m_sReceiverCountryCode);
  }

  /**
   * @return The receiver country code. May be <code>null</code>.
   */
  @Nullable
  public final String getReceiverCountryCode ()
  {
    return m_sReceiverCountryCode;
  }

  public final boolean hasVESID ()
  {
    return StringHelper.hasText (m_sVESID);
  }

  /**
   * @return The VESID for validation. May be <code>null</code>.
   */
  @Nullable
  public final String getVESID ()
  {
    return m_sVESID;
  }

  public final boolean hasProfileName ()
  {
    return StringHelper.hasText (m_sProfileName);
  }

  /**
   * @return The human readable name of the profile / document type found. May
   *         be <code>null</code>.
   */
  @Nullable
  public final String getProfileName ()
  {
    return m_sProfileName;
  }

  public final boolean hasFlags ()
  {
    return m_aFlags.isNotEmpty ();
  }

  /**
   * @return A copy of the contained flags. Never <code>null</code> but maybe
   *         empty.
   * @since 0.5.0
   */
  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsOrderedSet <String> getAllFlags ()
  {
    return m_aFlags.getClone ();
  }

  /**
   * @return A copy of the contained flags. Never <code>null</code> but maybe
   *         empty.
   * @since 0.5.0
   */
  @Nonnull
  @ReturnsMutableObject
  public final ICommonsOrderedSet <String> flags ()
  {
    return m_aFlags;
  }

  /**
   * Convert all document details as a JSON object.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public final IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    if (hasSyntaxID ())
      ret.add ("syntaxID", m_sSyntaxID);
    if (hasSyntaxVersion ())
      ret.add ("syntaxVersion", m_sSyntaxVersion);
    if (m_aSenderID != null)
      ret.add ("sender", m_aSenderID.getURIEncoded ());
    if (m_aReceiverID != null)
      ret.add ("receiver", m_aReceiverID.getURIEncoded ());
    if (m_aDocTypeID != null)
      ret.add ("doctype", m_aDocTypeID.getURIEncoded ());
    if (m_aProcessID != null)
      ret.add ("process", m_aProcessID.getURIEncoded ());
    if (hasCustomizationID ())
      ret.add ("customizationID", m_sCustomizationID);
    if (hasBusinessDocumentID ())
      ret.add ("bdid", m_sBusinessDocumentID);
    if (hasSenderName ())
      ret.add ("senderName", m_sSenderName);
    if (hasSenderCountryCode ())
      ret.add ("senderCountryCode", m_sSenderCountryCode);
    if (hasReceiverName ())
      ret.add ("receiverName", m_sReceiverName);
    if (hasReceiverCountryCode ())
      ret.add ("receiverCountryCode", m_sReceiverCountryCode);
    if (hasVESID ())
      ret.add ("vesid", m_sVESID);
    if (hasProfileName ())
      ret.add ("profileName", m_sProfileName);
    if (hasFlags ())
      ret.addJson ("flags", new JsonArray ().addAll (m_aFlags));
    return ret;
  }

  /**
   * Append all document details to an existing IMicroElement.
   *
   * @param aTarget
   *        The micro element to append to. May not be <code>null</code>.
   * @since 0.4.1
   */
  public void appendToMicroElement (@Nonnull final IMicroElement aTarget)
  {
    ValueEnforcer.notNull (aTarget, "Target");

    final String sNamespaceURI = aTarget.getNamespaceURI ();
    if (hasSyntaxID ())
      aTarget.appendElement (sNamespaceURI, XML_SYNTAX_ID).appendText (m_sSyntaxID);
    if (hasSyntaxVersion ())
      aTarget.appendElement (sNamespaceURI, XML_SYNTAX_VERSION).appendText (m_sSyntaxVersion);
    if (m_aSenderID != null)
      aTarget.appendElement (sNamespaceURI, XML_SENDER_ID).appendText (m_aSenderID.getURIEncoded ());
    if (m_aReceiverID != null)
      aTarget.appendElement (sNamespaceURI, XML_RECEIVER_ID).appendText (m_aReceiverID.getURIEncoded ());
    if (m_aDocTypeID != null)
      aTarget.appendElement (sNamespaceURI, XML_DOC_TYPE_ID).appendText (m_aDocTypeID.getURIEncoded ());
    if (m_aProcessID != null)
      aTarget.appendElement (sNamespaceURI, XML_PROCESS_ID).appendText (m_aProcessID.getURIEncoded ());
    if (hasCustomizationID ())
      aTarget.appendElement (sNamespaceURI, XML_CUSTOMIZATION_ID).appendText (m_sCustomizationID);
    if (hasBusinessDocumentID ())
      aTarget.appendElement (sNamespaceURI, XML_BUSINESS_DOCUMENT_ID).appendText (m_sBusinessDocumentID);
    if (hasSenderName ())
      aTarget.appendElement (sNamespaceURI, XML_SENDER_NAME).appendText (m_sSenderName);
    if (hasSenderCountryCode ())
      aTarget.appendElement (sNamespaceURI, XML_SENDER_COUNTRY_CODE).appendText (m_sSenderCountryCode);
    if (hasReceiverName ())
      aTarget.appendElement (sNamespaceURI, XML_RECEIVER_NAME).appendText (m_sReceiverName);
    if (hasReceiverCountryCode ())
      aTarget.appendElement (sNamespaceURI, XML_RECEIVER_COUNTRY_CODE).appendText (m_sReceiverCountryCode);
    if (hasVESID ())
      aTarget.appendElement (sNamespaceURI, XML_VESID).appendText (m_sVESID);
    if (hasProfileName ())
      aTarget.appendElement (sNamespaceURI, XML_PROFILE_NAME).appendText (m_sProfileName);
    if (hasFlags ())
      for (final String sFlag : m_aFlags)
        aTarget.appendElement (sNamespaceURI, XML_FLAG).appendText (sFlag);
  }

  /**
   * Append all document details to an existing DOM element.
   *
   * @param aTarget
   *        The DOM element to append to. May not be <code>null</code>.
   * @since 0.4.1
   */
  public void appendToDOMElement (@Nonnull final Element aTarget)
  {
    ValueEnforcer.notNull (aTarget, "Target");

    final String sNamespaceURI = aTarget.getNamespaceURI ();
    final Document aDoc = aTarget.getOwnerDocument ();
    final Function <String, Node> fCreate = sNamespaceURI == null ? x -> aDoc.createElement (x)
                                                                  : x -> aDoc.createElementNS (sNamespaceURI, x);
    final BiConsumer <String, String> fAppend = (name, val) -> aTarget.appendChild (fCreate.apply (name))
                                                                      .appendChild (aDoc.createTextNode (val));

    if (hasSyntaxID ())
      fAppend.accept (XML_SYNTAX_ID, m_sSyntaxID);
    if (hasSyntaxVersion ())
      fAppend.accept (XML_SYNTAX_VERSION, m_sSyntaxVersion);
    if (m_aSenderID != null)
      fAppend.accept (XML_SENDER_ID, m_aSenderID.getURIEncoded ());
    if (m_aReceiverID != null)
      fAppend.accept (XML_RECEIVER_ID, m_aReceiverID.getURIEncoded ());
    if (m_aDocTypeID != null)
      fAppend.accept (XML_DOC_TYPE_ID, m_aDocTypeID.getURIEncoded ());
    if (m_aProcessID != null)
      fAppend.accept (XML_PROCESS_ID, m_aProcessID.getURIEncoded ());
    if (hasCustomizationID ())
      fAppend.accept (XML_CUSTOMIZATION_ID, m_sCustomizationID);
    if (hasBusinessDocumentID ())
      fAppend.accept (XML_BUSINESS_DOCUMENT_ID, m_sBusinessDocumentID);
    if (hasSenderName ())
      fAppend.accept (XML_SENDER_NAME, m_sSenderName);
    if (hasSenderCountryCode ())
      fAppend.accept (XML_SENDER_COUNTRY_CODE, m_sSenderCountryCode);
    if (hasReceiverName ())
      fAppend.accept (XML_RECEIVER_NAME, m_sReceiverName);
    if (hasReceiverCountryCode ())
      fAppend.accept (XML_RECEIVER_COUNTRY_CODE, m_sReceiverCountryCode);
    if (hasVESID ())
      fAppend.accept (XML_VESID, m_sVESID);
    if (hasProfileName ())
      fAppend.accept (XML_PROFILE_NAME, m_sProfileName);
    if (hasFlags ())
      for (final String sFlag : m_aFlags)
        fAppend.accept (XML_FLAG, sFlag);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SyntaxID", m_sSyntaxID)
                                       .append ("SyntaxVersion", m_sSyntaxVersion)
                                       .append ("SenderID", m_aSenderID)
                                       .append ("ReceiverID", m_aReceiverID)
                                       .append ("DocTypeID", m_aDocTypeID)
                                       .append ("ProcessID", m_aProcessID)
                                       .append ("CustomizationID", m_sCustomizationID)
                                       .append ("BusinessDocumentID", m_sBusinessDocumentID)
                                       .append ("SenderName", m_sSenderName)
                                       .append ("SenderCountryCode", m_sSenderCountryCode)
                                       .append ("ReceiverName", m_sReceiverName)
                                       .append ("ReceiverCountryCode", m_sReceiverCountryCode)
                                       .append ("VESID", m_sVESID)
                                       .append ("ProfileName", m_sProfileName)
                                       .append ("Flags", m_aFlags)
                                       .getToString ();
  }

  /**
   * @return A new empty Builder. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builder ()
  {
    return new Builder ();
  }

  /**
   * @param aSource
   *        The document details source to use. May not be <code>null</code>.
   * @return A new Builder pre-filled with the provided document details. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static Builder builder (@Nonnull final DocumentDetails aSource)
  {
    return new Builder (aSource);
  }

  /**
   * Builder for {@link DocumentDetails}
   *
   * @author Philip Helger
   * @since 0.1.2
   */
  public static class Builder implements IBuilder <DocumentDetails>
  {
    private String m_sSyntaxID;
    private String m_sSyntaxVersion;
    private IParticipantIdentifier m_aSenderID;
    private IParticipantIdentifier m_aReceiverID;
    private IDocumentTypeIdentifier m_aDocTypeID;
    private IProcessIdentifier m_aProcessID;
    private String m_sCustomizationID;
    private String m_sBusinessDocumentID;
    private String m_sSenderName;
    private String m_sSenderCountryCode;
    private String m_sReceiverName;
    private String m_sReceiverCountryCode;
    private String m_sVESID;
    private String m_sProfileName;
    private final ICommonsOrderedSet <String> m_aFlags = new CommonsLinkedHashSet <> ();

    /**
     * Builder constructor with all fields empty.
     */
    public Builder ()
    {}

    /**
     * Builder constructor using an existing {@link DocumentDetails} object.
     *
     * @param aSource
     *        The source object to fill from. May not be <code>null</code>.
     */
    public Builder (@Nonnull final DocumentDetails aSource)
    {
      ValueEnforcer.notNull (aSource, "Source");
      syntaxID (aSource.getSyntaxID ()).syntaxVersion (aSource.getSyntaxVersion ())
                                       .senderID (aSource.getSenderID ())
                                       .receiverID (aSource.getReceiverID ())
                                       .documentTypeID (aSource.getDocumentTypeID ())
                                       .processID (aSource.getProcessID ())
                                       .businessDocumentID (aSource.getBusinessDocumentID ())
                                       .customizationID (aSource.getCustomizationID ())
                                       .senderName (aSource.getSenderName ())
                                       .senderCountryCode (aSource.getSenderCountryCode ())
                                       .receiverName (aSource.getReceiverName ())
                                       .receiverCountryCode (aSource.getReceiverCountryCode ())
                                       .vesid (aSource.getVESID ())
                                       .profileName (aSource.getProfileName ())
                                       .flags (aSource.flags ());
    }

    @Nonnull
    public final Builder syntaxID (@Nullable final String s)
    {
      m_sSyntaxID = s;
      return this;
    }

    @Nonnull
    public final Builder syntaxVersion (@Nullable final String s)
    {
      m_sSyntaxVersion = s;
      return this;
    }

    @Nonnull
    public final Builder senderID (@Nullable final IParticipantIdentifier a)
    {
      m_aSenderID = a;
      return this;
    }

    @Nonnull
    public final Builder receiverID (@Nullable final IParticipantIdentifier a)
    {
      m_aReceiverID = a;
      return this;
    }

    @Nonnull
    public final Builder documentTypeID (@Nullable final IDocumentTypeIdentifier a)
    {
      m_aDocTypeID = a;
      return this;
    }

    @Nonnull
    public final Builder processID (@Nullable final IProcessIdentifier a)
    {
      m_aProcessID = a;
      return this;
    }

    @Nonnull
    public final Builder customizationID (@Nullable final String s)
    {
      m_sCustomizationID = s;
      return this;
    }

    @Nonnull
    public final Builder businessDocumentID (@Nullable final String s)
    {
      m_sBusinessDocumentID = s;
      return this;
    }

    @Nonnull
    public final Builder senderName (@Nullable final String s)
    {
      m_sSenderName = s;
      return this;
    }

    @Nonnull
    public final Builder senderCountryCode (@Nullable final String s)
    {
      m_sSenderCountryCode = s;
      return this;
    }

    @Nonnull
    public final Builder receiverName (@Nullable final String s)
    {
      m_sReceiverName = s;
      return this;
    }

    @Nonnull
    public final Builder receiverCountryCode (@Nullable final String s)
    {
      m_sReceiverCountryCode = s;
      return this;
    }

    @Nonnull
    public final Builder vesid (@Nullable final String s)
    {
      m_sVESID = s;
      return this;
    }

    @Nonnull
    public final Builder profileName (@Nullable final String s)
    {
      m_sProfileName = s;
      return this;
    }

    @Nonnull
    public final Builder flags (@Nullable final String... a)
    {
      if (a == null)
        m_aFlags.clear ();
      else
        m_aFlags.addAll (a);
      return this;
    }

    @Nonnull
    public final Builder flags (@Nullable final Set <String> a)
    {
      if (a == null)
        m_aFlags.clear ();
      else
        m_aFlags.addAll (a);
      return this;
    }

    @Nonnull
    public DocumentDetails build ()
    {
      // All fields are optional
      return new DocumentDetails (m_sSyntaxID,
                                  m_sSyntaxVersion,
                                  m_aSenderID,
                                  m_aReceiverID,
                                  m_aDocTypeID,
                                  m_aProcessID,
                                  m_sCustomizationID,
                                  m_sBusinessDocumentID,
                                  m_sSenderName,
                                  m_sSenderCountryCode,
                                  m_sReceiverName,
                                  m_sReceiverCountryCode,
                                  m_sVESID,
                                  m_sProfileName,
                                  m_aFlags);
    }
  }
}

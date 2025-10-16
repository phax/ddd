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

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.ddd.model.DDDSyntax;
import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.DDDValueProviderPerSyntax;
import com.helger.ddd.model.EDDDDeterminedField;
import com.helger.ddd.model.EDDDSourceField;
import com.helger.ddd.model.VPDeterminedFlags;
import com.helger.ddd.model.VPDeterminedValues;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.doctype.PeppolDocumentTypeIdentifierParts;
import com.helger.xml.XMLHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Determine the document details from the payload.
 *
 * @author Philip Helger
 */
public final class DocumentDetailsDeterminator
{
  public static final String DEFAULT_PARTICIPANT_ID_SCHEME = PeppolIdentifierHelper.PARTICIPANT_SCHEME_ISO6523_ACTORID_UPIS;

  private static final Logger LOGGER = LoggerFactory.getLogger (DocumentDetailsDeterminator.class);

  private final DDDSyntaxList m_aSyntaxList;
  private final DDDValueProviderList m_aValueProviderList;
  private IIdentifierFactory m_aIF = SimpleIdentifierFactory.INSTANCE;
  private IParticipantIdentifier m_aFallbackSenderID;
  private IParticipantIdentifier m_aFallbackReceiverID;
  private String m_sParticipantIDScheme = DEFAULT_PARTICIPANT_ID_SCHEME;
  private Function <String, String> m_aDocTypeIDSchemeDeterminator = PeppolIdentifierFactory.INSTANCE::getDefaultDocumentTypeIdentifierScheme;
  private Function <String, String> m_aProcessIDSchemeDeterminator = x -> PeppolIdentifierHelper.DEFAULT_PROCESS_SCHEME;
  private Consumer <String> m_aInfoHdl = LOGGER::info;
  private Consumer <String> m_aWarnHdl = LOGGER::warn;
  private Consumer <String> m_aErrorHdl = LOGGER::error;

  public DocumentDetailsDeterminator (@Nonnull final DDDSyntaxList aSyntaxList,
                                      @Nonnull final DDDValueProviderList aValueProviderList)
  {
    ValueEnforcer.notNull (aSyntaxList, "SyntaxList");
    ValueEnforcer.notNull (aValueProviderList, "ValueProviderList");
    m_aSyntaxList = aSyntaxList;
    m_aValueProviderList = aValueProviderList;
  }

  /**
   * @return The syntax list provided in the constructor. Never <code>null</code>.
   * @since 0.3.3
   */
  @Nonnull
  public DDDSyntaxList getSyntaxList ()
  {
    return m_aSyntaxList;
  }

  /**
   * @return The value provider list provided in the constructor. Never <code>null</code>.
   * @since 0.3.3
   */
  @Nonnull
  public DDDValueProviderList getValueProviderList ()
  {
    return m_aValueProviderList;
  }

  /**
   * @return The Identifier Factory used internally to created structured IDs. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IIdentifierFactory getIdentifierFactory ()
  {
    return m_aIF;
  }

  /**
   * Set the Identifier Factory to be used. By default the {@link SimpleIdentifierFactory} is used.
   *
   * @param aIF
   *        The Identifier Factory to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public DocumentDetailsDeterminator setIdentifierFactory (@Nullable final IIdentifierFactory aIF)
  {
    ValueEnforcer.notNull (aIF, "IdentifierFactory");
    m_aIF = aIF;
    return this;
  }

  /**
   * @return The fallback sending participant ID in case non could be determined from the payload.
   *         May be <code>null</code>.
   */
  @Nullable
  public IParticipantIdentifier getFallbackSenderID ()
  {
    return m_aFallbackSenderID;
  }

  @Nonnull
  public DocumentDetailsDeterminator setFallbackSenderID (@Nullable final IParticipantIdentifier aFallbackSenderID)
  {
    m_aFallbackSenderID = aFallbackSenderID;
    return this;
  }

  /**
   * @return The fallback receiving participant ID in case non could be determined from the payload.
   *         May be <code>null</code>.
   */
  @Nullable
  public IParticipantIdentifier getFallbackReceiverID ()
  {
    return m_aFallbackReceiverID;
  }

  @Nonnull
  public DocumentDetailsDeterminator setFallbackReceiverID (@Nullable final IParticipantIdentifier aFallbackReceiverID)
  {
    m_aFallbackReceiverID = aFallbackReceiverID;
    return this;
  }

  /**
   * @return The Participant Identifier Scheme to be used. Defaults to the Peppol one. May be
   *         <code>null</code>.
   * @since 0.1.3
   */
  @Nullable
  public String getParticipantIDScheme ()
  {
    return m_sParticipantIDScheme;
  }

  @Nonnull
  public DocumentDetailsDeterminator setParticipantIDScheme (@Nullable final String sParticipantIDScheme)
  {
    m_sParticipantIDScheme = sParticipantIDScheme;
    return this;
  }

  /**
   * @return The Document Type Identifier Scheme determinator to be used. Defaults to using the
   *         Peppol schemes. Never <code>null</code>.
   */
  @Nonnull
  public Function <String, String> getDocTypeIDSchemeDeterminator ()
  {
    return m_aDocTypeIDSchemeDeterminator;
  }

  @Nonnull
  public DocumentDetailsDeterminator setDocTypeIDSchemeDeterminator (@Nonnull final Function <String, String> a)
  {
    ValueEnforcer.notNull (a, "DocTypeIDSchemeDeterminator");
    m_aDocTypeIDSchemeDeterminator = a;
    return this;
  }

  /**
   * @return The Process Identifier Scheme determinator to be used. Defaults to using the Peppol
   *         scheme. Never <code>null</code>.
   */
  @Nonnull
  public Function <String, String> getProcessIDSchemeDeterminator ()
  {
    return m_aProcessIDSchemeDeterminator;
  }

  @Nonnull
  public DocumentDetailsDeterminator setProcessIDSchemeDeterminator (@Nonnull final Function <String, String> a)
  {
    ValueEnforcer.notNull (a, "ProcessIDSchemeDeterminator");
    m_aProcessIDSchemeDeterminator = a;
    return this;
  }

  /**
   * @return The handler for "information" messages that occurred during document detail
   *         determination. Default is to log with info level. Never <code>null</code>.
   */
  @Nonnull
  public Consumer <String> getInfoHdl ()
  {
    return m_aInfoHdl;
  }

  @Nonnull
  public DocumentDetailsDeterminator setInfoHdl (@Nonnull final Consumer <String> aInfoHdl)
  {
    ValueEnforcer.notNull (aInfoHdl, "InfoHdl");
    m_aInfoHdl = aInfoHdl;
    return this;
  }

  /**
   * @return The handler for "warning" messages that occurred during document detail determination.
   *         Default is to log with warning level. Never <code>null</code>.
   */
  @Nonnull
  public Consumer <String> getWarnHdl ()
  {
    return m_aWarnHdl;
  }

  @Nonnull
  public DocumentDetailsDeterminator setWarnHdl (@Nonnull final Consumer <String> aWarnHdl)
  {
    ValueEnforcer.notNull (aWarnHdl, "WarnHdl");
    m_aWarnHdl = aWarnHdl;
    return this;
  }

  /**
   * @return The handler for "error" messages that occurred during document detail determination.
   *         Default is to log with error level. Never <code>null</code>.
   */
  @Nonnull
  public Consumer <String> getErrorHdl ()
  {
    return m_aErrorHdl;
  }

  @Nonnull
  public DocumentDetailsDeterminator setErrorHdl (@Nonnull final Consumer <String> aErrorHdl)
  {
    ValueEnforcer.notNull (aErrorHdl, "ErrorHdl");
    m_aErrorHdl = aErrorHdl;
    return this;
  }

  @Nullable
  private IParticipantIdentifier _createPID (@Nullable final String sSchemeID, @Nullable final String sValue)
  {
    // Participant ID Scheme: iso6523-actorid-upis
    // Scheme is e.g. "0088"
    return m_aIF.createParticipantIdentifier (m_sParticipantIDScheme,
                                              StringHelper.trim (sSchemeID) + ":" + StringHelper.trim (sValue));
  }

  @Nullable
  public DocumentDetails findDocumentDetails (@Nonnull final Element aRootElement)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    // Get the qualified name of the root element
    final QName aQName = XMLHelper.getQName (aRootElement);
    m_aInfoHdl.accept ("Searching document details for " + aQName.toString ());

    // First find the matching syntax from the root element
    final DDDSyntax aSyntax = m_aSyntaxList.findMatchingSyntax (aRootElement);
    if (aSyntax == null)
    {
      m_aErrorHdl.accept ("Unsupported Document Type syntax " + aQName.toString ());
      return null;
    }

    // Find the value provider for the selected syntax
    final DDDValueProviderPerSyntax aValueProvider = m_aValueProviderList.getValueProviderPerSyntax (aSyntax.getID ());
    if (aValueProvider == null)
    {
      m_aErrorHdl.accept ("The value provider has no mapping for syntax with ID '" + aSyntax.getID () + "'");
      return null;
    }

    // Get all the values from the source XML
    final ErrorList aErrorList = new ErrorList ();
    final String sCustomizationID = aSyntax.getValue (EDDDSourceField.CUSTOMIZATION_ID, aRootElement, aErrorList);
    // optional
    String sProcessID = aSyntax.getValue (EDDDSourceField.PROCESS_ID, aRootElement, aErrorList);
    final String sSenderIDScheme = aSyntax.getValue (EDDDSourceField.SENDER_ID_SCHEME, aRootElement, aErrorList);
    final String sSenderIDValue = aSyntax.getValue (EDDDSourceField.SENDER_ID_VALUE, aRootElement, aErrorList);
    IParticipantIdentifier aSenderID = _createPID (sSenderIDScheme, sSenderIDValue);
    final String sReceiverIDScheme = aSyntax.getValue (EDDDSourceField.RECEIVER_ID_SCHEME, aRootElement, aErrorList);
    final String sReceiverIDValue = aSyntax.getValue (EDDDSourceField.RECEIVER_ID_VALUE, aRootElement, aErrorList);
    IParticipantIdentifier aReceiverID = _createPID (sReceiverIDScheme, sReceiverIDValue);
    final String sBusinessDocumentID = aSyntax.getValue (EDDDSourceField.BUSINESS_DOCUMENT_ID,
                                                         aRootElement,
                                                         aErrorList);
    final String sSenderName = aSyntax.getValue (EDDDSourceField.SENDER_NAME, aRootElement, aErrorList);
    final String sSenderCountryCode = aSyntax.getValue (EDDDSourceField.SENDER_COUNTRY_CODE, aRootElement, aErrorList);
    final String sReceiverName = aSyntax.getValue (EDDDSourceField.RECEIVER_NAME, aRootElement, aErrorList);
    final String sReceiverCountryCode = aSyntax.getValue (EDDDSourceField.RECEIVER_COUNTRY_CODE,
                                                          aRootElement,
                                                          aErrorList);
    // optional value
    String sSyntaxVersion = aSyntax.getVersion ();
    String sVESID = null;

    // Debug log specific value found while retrieving certain values
    if (LOGGER.isDebugEnabled ())
      aErrorList.getAllFailures ().forEach (x -> LOGGER.debug (x.getAsString (Locale.US)));

    // Handle fallbacks (if any)
    if (aSenderID == null && m_aFallbackSenderID != null)
    {
      m_aWarnHdl.accept ("Falling back to the default sender ID '" + m_aFallbackSenderID.getURIEncoded () + "'");
      aSenderID = m_aFallbackSenderID;
    }
    if (aReceiverID == null && m_aFallbackReceiverID != null)
    {
      m_aWarnHdl.accept ("Falling back to the default receiver ID '" + m_aFallbackReceiverID.getURIEncoded () + "'");
      aReceiverID = m_aFallbackReceiverID;
    }

    // Source value provider
    final String sSourceProcessID = sProcessID;
    final Function <EDDDSourceField, String> fctFieldProvider = field -> (switch (field)
    {
      case CUSTOMIZATION_ID -> sCustomizationID;
      case PROCESS_ID -> sSourceProcessID;
      case BUSINESS_DOCUMENT_ID -> sBusinessDocumentID;
      case SENDER_ID_SCHEME -> sSenderIDScheme;
      case SENDER_ID_VALUE -> sSenderIDValue;
      case SENDER_NAME -> sSenderName;
      case SENDER_COUNTRY_CODE -> sSenderCountryCode;
      case RECEIVER_ID_SCHEME -> sReceiverIDScheme;
      case RECEIVER_ID_VALUE -> sReceiverIDValue;
      case RECEIVER_NAME -> sReceiverName;
      case RECEIVER_COUNTRY_CODE -> sReceiverCountryCode;
      default -> throw new IllegalArgumentException ("Unsupported field " + field);
    });

    // Target value setter
    final VPDeterminedValues aDeterminedMatches = new VPDeterminedValues ();
    final VPDeterminedFlags aDeterminedFlags = new VPDeterminedFlags ();
    aValueProvider.forAllDeducedValues (fctFieldProvider, aDeterminedMatches, aDeterminedFlags);

    String sProfileName = null;
    for (final Map.Entry <EDDDDeterminedField, String> aEntry : aDeterminedMatches)
    {
      final String sNewValue = aEntry.getValue ();
      switch (aEntry.getKey ())
      {
        case PROCESS_ID:
          sProcessID = sNewValue;
          break;
        case SYNTAX_VERSION:
          sSyntaxVersion = sNewValue;
          break;
        case VESID:
          sVESID = sNewValue;
          break;
        case PROFILE_NAME:
          sProfileName = sNewValue;
          break;
        default:
          throw new IllegalStateException ("The field " + aEntry.getKey () + " is unknown");
      }
    }

    // Assemble Document Type ID
    final IDocumentTypeIdentifier aDocTypeID;
    if (StringHelper.isNotEmpty (sCustomizationID) && StringHelper.isNotEmpty (sSyntaxVersion))
    {
      final String sDocTypeIDValue = new PeppolDocumentTypeIdentifierParts (aRootElement.getNamespaceURI (),
                                                                            aRootElement.getLocalName (),
                                                                            sCustomizationID,
                                                                            sSyntaxVersion).getAsDocumentTypeIdentifierValue ();
      final String sDocTypeIDScheme = m_aDocTypeIDSchemeDeterminator.apply (sDocTypeIDValue);
      aDocTypeID = m_aIF.createDocumentTypeIdentifier (sDocTypeIDScheme, sDocTypeIDValue);
    }
    else
      aDocTypeID = null;

    // Assemble Process ID
    final IProcessIdentifier aProcessID;
    if (StringHelper.isNotEmpty (sProcessID))
    {
      String sProcessIDScheme = m_aProcessIDSchemeDeterminator.apply (sProcessID);
      aProcessID = m_aIF.createProcessIdentifier (sProcessIDScheme, sProcessID);
    }
    else
      aProcessID = null;

    // Swap sender and receiver for self-billing?
    // Don't keep this action in the resulting flags
    boolean bSwapSenderAndReceiver = aDeterminedFlags.remove ("Action-SwapSenderAndReceiver").isChanged ();

    // All elements are optional
    return DocumentDetails.builder ()
                          .syntaxID (aSyntax.getID ())
                          .senderID (bSwapSenderAndReceiver ? aReceiverID : aSenderID)
                          .receiverID (bSwapSenderAndReceiver ? aSenderID : aReceiverID)
                          .documentTypeID (aDocTypeID)
                          .customizationID (sCustomizationID)
                          .syntaxVersion (sSyntaxVersion)
                          .processID (aProcessID)
                          .businessDocumentID (sBusinessDocumentID)
                          .senderName (bSwapSenderAndReceiver ? sReceiverName : sSenderName)
                          .senderCountryCode (bSwapSenderAndReceiver ? sReceiverCountryCode : sSenderCountryCode)
                          .receiverName (bSwapSenderAndReceiver ? sSenderName : sReceiverName)
                          .receiverCountryCode (bSwapSenderAndReceiver ? sSenderCountryCode : sReceiverCountryCode)
                          .vesid (sVESID)
                          .profileName (sProfileName)
                          .flags (aDeterminedFlags.getAsSet ())
                          .build ();
  }
}

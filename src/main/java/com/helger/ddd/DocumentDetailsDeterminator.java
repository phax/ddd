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

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.string.StringHelper;
import com.helger.ddd.model.DDDSyntax;
import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.DDDValueProviderPerSyntax;
import com.helger.ddd.model.EDDDField;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.doctype.PeppolDocumentTypeIdentifierParts;
import com.helger.xml.XMLHelper;

/**
 * Determine the document details from the payload. Heuristics.
 *
 * @author Philip Helger
 */
public final class DocumentDetailsDeterminator
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DocumentDetailsDeterminator.class);

  private final DDDSyntaxList m_aSyntaxList;
  private final DDDValueProviderList m_aValueProviderList;
  private IParticipantIdentifier m_aFallbackSenderID;
  private IParticipantIdentifier m_aFallbackReceiverID;

  public DocumentDetailsDeterminator (@Nonnull final DDDSyntaxList aSyntaxList,
                                      @Nonnull final DDDValueProviderList aValueProviderList)
  {
    ValueEnforcer.notNull (aSyntaxList, "SyntaxList");
    ValueEnforcer.notNull (aValueProviderList, "ValueProviderList");
    m_aSyntaxList = aSyntaxList;
    m_aValueProviderList = aValueProviderList;
  }

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

  @Nonnull
  private static IParticipantIdentifier _createPID (final String sSchemeID, final String sValue)
  {
    // Scheme is e.g. "0088"
    return SimpleIdentifierFactory.INSTANCE.createParticipantIdentifier (PeppolIdentifierHelper.PARTICIPANT_SCHEME_ISO6523_ACTORID_UPIS,
                                                                         StringHelper.trim (sSchemeID) +
                                                                                                                                         ":" +
                                                                                                                                         StringHelper.trim (sValue));
  }

  @Nullable
  public DocumentDetails findDocumentDetails (@Nonnull final Element aRootElement)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    LOGGER.info ("Searching document details for " + XMLHelper.getQName (aRootElement).toString ());

    final DDDSyntax aSyntax = m_aSyntaxList.findMatchingSyntax (aRootElement);
    if (aSyntax == null)
    {
      LOGGER.error ("Unsupported Document Type syntax");
      return null;
    }

    // Get all the values from the source XML
    final ErrorList aErrorList = new ErrorList ();
    final String sCustomizationID = aSyntax.getValue (EDDDField.CUSTOMIZATION_ID, aRootElement, aErrorList);
    // optional
    String sProcessID = aSyntax.getValue (EDDDField.PROCESS_ID, aRootElement, aErrorList);
    final String sSenderScheme = aSyntax.getValue (EDDDField.SENDER_ID_SCHEME, aRootElement, aErrorList);
    final String sSenderValue = aSyntax.getValue (EDDDField.SENDER_ID_VALUE, aRootElement, aErrorList);
    IParticipantIdentifier aSenderID = _createPID (sSenderScheme, sSenderValue);
    final String sReceiverScheme = aSyntax.getValue (EDDDField.RECEIVER_ID_SCHEME, aRootElement, aErrorList);
    final String sReceiverValue = aSyntax.getValue (EDDDField.RECEIVER_ID_VALUE, aRootElement, aErrorList);
    IParticipantIdentifier aReceiverID = _createPID (sReceiverScheme, sReceiverValue);
    final String sBusinessDocumentID = aSyntax.getValue (EDDDField.BUSINESS_DOCUMENT_ID, aRootElement, aErrorList);
    final String sSenderName = aSyntax.getValue (EDDDField.SENDER_NAME, aRootElement, aErrorList);
    final String sReceiverName = aSyntax.getValue (EDDDField.RECEIVER_NAME, aRootElement, aErrorList);
    String sVESID = null;

    if (true)
      aErrorList.getAllFailures ().forEach (x -> LOGGER.warn (x.getAsString (Locale.US)));

    // Handle fallbacks (if any)
    if (aSenderID == null && m_aFallbackSenderID != null)
    {
      LOGGER.warn ("Falling back to the default sender ID " + m_aFallbackSenderID);
      aSenderID = m_aFallbackSenderID;
    }
    if (aReceiverID == null && m_aFallbackReceiverID != null)
    {
      LOGGER.warn ("Falling back to the default receiver ID " + m_aFallbackReceiverID);
      aReceiverID = m_aFallbackReceiverID;
    }

    // Source value provider
    final String sSourceProcessID = sProcessID;
    final Function <EDDDField, String> fctFieldProvider = field -> {
      switch (field)
      {
        case CUSTOMIZATION_ID:
          return sCustomizationID;
        case PROCESS_ID:
          return sSourceProcessID;
        case SENDER_ID_SCHEME:
          return sSenderScheme;
        case SENDER_ID_VALUE:
          return sSenderValue;
        case RECEIVER_ID_SCHEME:
          return sReceiverScheme;
        case RECEIVER_ID_VALUE:
          return sReceiverValue;
        case BUSINESS_DOCUMENT_ID:
          return sBusinessDocumentID;
        case SENDER_NAME:
          return sSenderName;
        case RECEIVER_NAME:
          return sReceiverName;
        default:
          throw new IllegalArgumentException ("Unsupported field " + field);
      }
    };

    // Find all setters for the missing values
    final DDDValueProviderPerSyntax aValueProvider = m_aValueProviderList.getValueProviderPerSyntax (aSyntax.getID ());
    if (aValueProvider == null)
    {
      LOGGER.error ("The value provider has no mapping for syntax with ID '" + aSyntax.getID () + "'");
      return null;
    }

    final ICommonsMap <EDDDField, String> aMatches = aValueProvider.getAllDeducedValues (fctFieldProvider);
    for (final Map.Entry <EDDDField, String> aEntry : aMatches.entrySet ())
    {
      final String sNewValue = aEntry.getValue ();
      switch (aEntry.getKey ())
      {
        case PROCESS_ID:
          sProcessID = sNewValue;
          break;
        case VESID:
          sVESID = sNewValue;
          break;
        default:
          throw new IllegalStateException ("The field " + aEntry.getKey () + " cannot be set atm");
      }
    }

    final IDocumentTypeIdentifier aDocTypeID;
    if (StringHelper.hasText (sCustomizationID))
    {
      final String sDocTypeIDValue = new PeppolDocumentTypeIdentifierParts (aRootElement.getNamespaceURI (),
                                                                            aRootElement.getLocalName (),
                                                                            sCustomizationID,
                                                                            aSyntax.getVersion ()).getAsDocumentTypeIdentifierValue ();
      aDocTypeID = SimpleIdentifierFactory.INSTANCE.createDocumentTypeIdentifier (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS,
                                                                                  sDocTypeIDValue);
    }
    else
      aDocTypeID = null;

    final IProcessIdentifier aProcessID;
    if (StringHelper.hasText (sProcessID))
      aProcessID = SimpleIdentifierFactory.INSTANCE.createProcessIdentifier (PeppolIdentifierHelper.PROCESS_SCHEME_CENBII_PROCID_UBL,
                                                                             sProcessID);
    else
      aProcessID = null;

    // All elements are optional
    return new DocumentDetails (aSenderID,
                                aReceiverID,
                                aDocTypeID,
                                aProcessID,
                                sVESID,
                                sBusinessDocumentID,
                                sSenderName,
                                sReceiverName);
  }
}

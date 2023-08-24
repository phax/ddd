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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.string.StringHelper;
import com.helger.ddd.model.DDDSyntax;
import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.EDDDField;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
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

  public DocumentDetailsDeterminator (@Nonnull final DDDSyntaxList aSyntaxList)
  {
    ValueEnforcer.notNull (aSyntaxList, "SyntaxList");
    m_aSyntaxList = aSyntaxList;
  }

  @Nullable
  private static String _getChildText (@Nonnull final Element a, @Nonnull final String... aTagNames)
  {
    final Element aChild = XMLHelper.getChildElementOfNames (a, aTagNames);
    return aChild == null ? null : StringHelper.trim (aChild.getTextContent ());
  }

  @Nonnull
  private static IParticipantIdentifier _createPID (final String sSchemeID, final String sValue)
  {
    // Scheme is e.g. "0088"
    return SimpleIdentifierFactory.INSTANCE.createParticipantIdentifier (StringHelper.trim (sSchemeID),
                                                                         StringHelper.trim (sValue));
  }

  @Nullable
  public DocumentDetails findDocumentDetails (@Nonnull final Element aRootElement,
                                              @Nullable final IParticipantIdentifier aFallbackSenderID,
                                              @Nullable final IParticipantIdentifier aFallbackReceiverID)
  {
    ValueEnforcer.notNull (aRootElement, "RootElement");

    LOGGER.info ("Searching document details for " + XMLHelper.getQName (aRootElement).toString ());

    final DDDSyntax aSyntax = m_aSyntaxList.findMatchingSyntax (aRootElement);
    if (aSyntax == null)
    {
      LOGGER.error ("Unsupported Document Type syntax");
      return null;
    }

    final ErrorList aErrorList = new ErrorList ();
    final String sCustomizationID = aSyntax.getValue (EDDDField.CUSTOMIZATION_ID, aRootElement, aErrorList);
    // optional
    final String sProcessID = aSyntax.getValue (EDDDField.PROCESS_ID, aRootElement, aErrorList);
    final String sBusinessDocumentID = aSyntax.getValue (EDDDField.BUSINESS_DOCUMENT_ID, aRootElement, aErrorList);
    final String sSenderScheme = aSyntax.getValue (EDDDField.SENDER_ID_SCHEME, aRootElement, aErrorList);
    final String sSenderValue = aSyntax.getValue (EDDDField.SENDER_ID_VALUE, aRootElement, aErrorList);
    IParticipantIdentifier aSenderID = _createPID (sSenderScheme, sSenderValue);
    final String sReceiverScheme = aSyntax.getValue (EDDDField.RECEIVER_ID_SCHEME, aRootElement, aErrorList);
    final String sReceiverValue = aSyntax.getValue (EDDDField.RECEIVER_ID_VALUE, aRootElement, aErrorList);
    IParticipantIdentifier aReceiverID = _createPID (sReceiverScheme, sReceiverValue);
    final String sSenderName = aSyntax.getValue (EDDDField.SENDER_NAME, aRootElement, aErrorList);
    final String sReceiverName = aSyntax.getValue (EDDDField.RECEIVER_NAME, aRootElement, aErrorList);

    if (aSenderID == null)
    {
      LOGGER.warn ("Falling back to the default sender ID " + aFallbackSenderID);
      aSenderID = aFallbackSenderID;
    }
    if (aReceiverID == null)
    {
      LOGGER.warn ("Falling back to the default receiver ID " + aFallbackSenderID);
      aReceiverID = aFallbackReceiverID;
    }

    // TODO stuff
    final IDocumentTypeIdentifier aDocTypeID = null;
    final IProcessIdentifier aProcessID = null;
    final String sVESID = null;

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

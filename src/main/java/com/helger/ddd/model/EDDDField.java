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
package com.helger.ddd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.state.EMandatory;

/**
 * Defines the supported fields for document determination. They are mapped to
 * different source elements per syntax.
 *
 * @author Philip Helger
 */
public enum EDDDField implements IHasID <String>
{
  CUSTOMIZATION_ID ("CustomizationID", EMandatory.MANDATORY),
  PROCESS_ID ("ProcessID", EMandatory.OPTIONAL),
  SENDER_ID_SCHEME ("SenderIDScheme", EMandatory.MANDATORY),
  SENDER_ID_VALUE ("SenderIDValue", EMandatory.MANDATORY),
  RECEIVER_ID_SCHEME ("ReceiverIDScheme", EMandatory.MANDATORY),
  RECEIVER_ID_VALUE ("ReceiverIDValue", EMandatory.MANDATORY),
  BUSINESS_DOCUMENT_ID ("BusinessDocumentID", EMandatory.MANDATORY),
  SENDER_NAME ("SenderName", EMandatory.MANDATORY),
  RECEIVER_NAME ("ReceiverName", EMandatory.MANDATORY),
  SYNTAX_VERSION ("SyntaxVersion", EMandatory.OPTIONAL),
  VESID ("VESID", EMandatory.OPTIONAL),
  PROFILE_NAME ("ProfileName", EMandatory.OPTIONAL);

  private final String m_sID;
  private final EMandatory m_eSyntaxDefinitionMandatory;

  EDDDField (@Nonnull @Nonempty final String sID, @Nonnull final EMandatory eSourceMandatory)
  {
    m_sID = sID;
    m_eSyntaxDefinitionMandatory = eSourceMandatory;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public boolean isSyntaxDefinitionMandatory ()
  {
    return m_eSyntaxDefinitionMandatory.isMandatory ();
  }

  @Nullable
  public static EDDDField getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EDDDField.class, sID);
  }
}

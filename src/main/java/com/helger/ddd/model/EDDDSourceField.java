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
public enum EDDDSourceField implements IHasID <String>
{
  CUSTOMIZATION_ID ("CustomizationID", EMandatory.MANDATORY),
  // Special case - missing in CII based syntaxes
  PROCESS_ID ("ProcessID", EMandatory.OPTIONAL),
  BUSINESS_DOCUMENT_ID ("BusinessDocumentID", EMandatory.MANDATORY),

  SENDER_ID_SCHEME ("SenderIDScheme", EMandatory.MANDATORY),
  SENDER_ID_VALUE ("SenderIDValue", EMandatory.MANDATORY),
  SENDER_NAME ("SenderName", EMandatory.MANDATORY),
  SENDER_COUNTRY_CODE ("SenderCountryCode", EMandatory.MANDATORY),

  RECEIVER_ID_SCHEME ("ReceiverIDScheme", EMandatory.MANDATORY),
  RECEIVER_ID_VALUE ("ReceiverIDValue", EMandatory.MANDATORY),
  RECEIVER_NAME ("ReceiverName", EMandatory.MANDATORY),
  RECEIVER_COUNTRY_CODE ("ReceiverCountryCode", EMandatory.MANDATORY);

  private final String m_sID;
  private final EMandatory m_eSyntaxDefinitionMandatory;

  EDDDSourceField (@Nonnull @Nonempty final String sID, @Nonnull final EMandatory eSyntaxDefinitionMandatory)
  {
    m_sID = sID;
    m_eSyntaxDefinitionMandatory = eSyntaxDefinitionMandatory;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if this field needs to be present in all syntax
   *         definitions. If this field is mandatory, it doesn't mean that the
   *         field needs to be present in all instances-
   */
  public boolean isSyntaxDefinitionMandatory ()
  {
    return m_eSyntaxDefinitionMandatory.isMandatory ();
  }

  @Nullable
  public static EDDDSourceField getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EDDDSourceField.class, sID);
  }
}

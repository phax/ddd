/*
 * Copyright (C) 2023-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;
import com.helger.base.state.EMandatory;

/**
 * Defines the supported fields for document determination. They are mapped to different source
 * elements per syntax.
 *
 * @author Philip Helger
 */
public enum EDDDSourceField implements IHasID <String>
{
  CUSTOMIZATION_ID ("CustomizationID", EMandatory.MANDATORY),
  // Special case - missing in CII based syntaxes
  PROCESS_ID ("ProcessID", EMandatory.OPTIONAL),
  BUSINESS_DOCUMENT_ID ("BusinessDocumentID", EMandatory.OPTIONAL),

  SENDER_ID_SCHEME ("SenderIDScheme", EMandatory.OPTIONAL),
  SENDER_ID_VALUE ("SenderIDValue", EMandatory.OPTIONAL),
  SENDER_NAME ("SenderName", EMandatory.OPTIONAL),
  SENDER_COUNTRY_CODE ("SenderCountryCode", EMandatory.OPTIONAL),

  RECEIVER_ID_SCHEME ("ReceiverIDScheme", EMandatory.OPTIONAL),
  RECEIVER_ID_VALUE ("ReceiverIDValue", EMandatory.OPTIONAL),
  RECEIVER_NAME ("ReceiverName", EMandatory.OPTIONAL),
  RECEIVER_COUNTRY_CODE ("ReceiverCountryCode", EMandatory.OPTIONAL);

  private final String m_sID;
  private final EMandatory m_eSyntaxDefinitionMandatory;

  EDDDSourceField (@NonNull @Nonempty final String sID, @NonNull final EMandatory eSyntaxDefinitionMandatory)
  {
    m_sID = sID;
    m_eSyntaxDefinitionMandatory = eSyntaxDefinitionMandatory;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if this field needs to be present in all syntax definitions. If this
   *         field is mandatory, it doesn't mean that the field needs to be present in all
   *         instances.
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

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

/**
 * Defines the all the fields that can be determined based on source fields.
 *
 * @author Philip Helger
 */
public enum EDDDDeterminedField implements IHasID <String>
{
  PROCESS_ID ("ProcessID", EDDDSourceField.PROCESS_ID),
  SYNTAX_VERSION ("SyntaxVersion", null),
  VESID ("VESID", null),
  PROFILE_NAME ("ProfileName", null);

  private final String m_sID;
  private final EDDDSourceField m_eSourceField;

  EDDDDeterminedField (@NonNull @Nonempty final String sID, @Nullable final EDDDSourceField eSourceField)
  {
    m_sID = sID;
    m_eSourceField = eSourceField;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public EDDDSourceField getSourceField ()
  {
    return m_eSourceField;
  }

  @Nullable
  public static EDDDDeterminedField getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EDDDDeterminedField.class, sID);
  }
}

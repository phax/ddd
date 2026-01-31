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

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This class is a combination of a source field and a source value.
 *
 * @author Philip Helger
 * @since 0.2.2
 */
@Immutable
public final class VPSourceValue
{
  private final EDDDSourceField m_eSourceField;
  private final String m_sSourceValue;

  public VPSourceValue (@NonNull final EDDDSourceField eSourceField, @NonNull @Nonempty final String sSourceValue)
  {
    ValueEnforcer.notNull (eSourceField, "SourceField");
    ValueEnforcer.notEmpty (sSourceValue, "SourceValue");

    m_eSourceField = eSourceField;
    m_sSourceValue = sSourceValue;
  }

  @NonNull
  public EDDDSourceField getSourceField ()
  {
    return m_eSourceField;
  }

  @NonNull
  @Nonempty
  public String getSourceValue ()
  {
    return m_sSourceValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final VPSourceValue rhs = (VPSourceValue) o;
    return m_eSourceField.equals (rhs.m_eSourceField) && m_sSourceValue.equals (rhs.m_sSourceValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eSourceField).append (m_sSourceValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SourceField", m_eSourceField)
                                       .append ("SourceValue", m_sSourceValue)
                                       .getToString ();
  }
}

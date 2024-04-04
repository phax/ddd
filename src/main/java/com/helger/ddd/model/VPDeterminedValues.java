/*
 * Copyright (C) 2023-2024 Philip Helger (www.helger.com)
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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a set of values in the Value Provider scope
 *
 * @author Philip Helger
 * @since 0.2.2
 */
@NotThreadSafe
public final class VPDeterminedValues implements ICloneable <VPDeterminedValues>, Iterable <Map.Entry <EDDDDeterminedField, String>>
{
  private final ICommonsMap <EDDDDeterminedField, String> m_aMap;

  public VPDeterminedValues ()
  {
    m_aMap = new CommonsHashMap <> ();
  }

  private VPDeterminedValues (@Nonnull final ICommonsMap <EDDDDeterminedField, String> aMap)
  {
    m_aMap = aMap.getClone ();
  }

  public boolean containsKey (@Nonnull final EDDDDeterminedField eField)
  {
    return m_aMap.containsKey (eField);
  }

  @Nullable
  public String get (final EDDDDeterminedField eField)
  {
    return m_aMap.get (eField);
  }

  public void put (@Nonnull final EDDDDeterminedField eField, @Nonnull final String sValue)
  {
    m_aMap.put (eField, sValue);
  }

  public void putAll (@Nonnull final VPDeterminedValues aSetters)
  {
    m_aMap.putAll (aSetters.m_aMap);
  }

  @Nonnull
  public Iterator <Entry <EDDDDeterminedField, String>> iterator ()
  {
    return m_aMap.entrySet ().iterator ();
  }

  public boolean isNotEmpty ()
  {
    return m_aMap.isNotEmpty ();
  }

  @Nonnull
  public VPDeterminedValues getClone ()
  {
    return new VPDeterminedValues (m_aMap);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Map", m_aMap).getToString ();
  }
}

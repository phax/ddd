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
package com.helger.ddd.model;

import java.util.Iterator;
import java.util.Map;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsSortedMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Represents a single source field selection with different conditions attached
 * to it.
 *
 * @author Philip Helger
 * @since 0.2.2
 */
public class VPSelect implements ICloneable <VPSelect>, Iterable <Map.Entry <String, VPIf>>
{
  private final EDDDSourceField m_eSourceField;
  private final ICommonsSortedMap <String, VPIf> m_aIfs;

  public VPSelect (@Nonnull final EDDDSourceField eSourceField)
  {
    ValueEnforcer.notNull (eSourceField, "SourceField");

    m_eSourceField = eSourceField;
    m_aIfs = new CommonsTreeMap <> ();
  }

  public VPSelect (@Nonnull final EDDDSourceField eSourceField, @Nonnull final ICommonsSortedMap <String, VPIf> aIfs)
  {
    ValueEnforcer.notNull (eSourceField, "SourceField");
    ValueEnforcer.notNull (aIfs, "Ifs");

    m_eSourceField = eSourceField;
    m_aIfs = aIfs.getClone ();
  }

  // Cloning constructor
  private VPSelect (@Nonnull final EDDDSourceField eSourceField,
                    @Nonnull final ICommonsSortedMap <String, VPIf> aIfs,
                    final boolean bClone)
  {
    ValueEnforcer.notNull (eSourceField, "SourceField");
    ValueEnforcer.notNull (aIfs, "Ifs");

    m_eSourceField = eSourceField;
    m_aIfs = bClone ? aIfs.getClone () : aIfs;
  }

  @Nonnull
  public EDDDSourceField getSourceField ()
  {
    return m_eSourceField;
  }

  public boolean containsIf (@Nullable final String sConditionValue)
  {
    return m_aIfs.containsKey (sConditionValue);
  }

  @Nullable
  public VPIf getIf (@Nullable final String sConditionValue)
  {
    return m_aIfs.get (sConditionValue);
  }

  @Nonnegative
  public int getIfCount ()
  {
    return m_aIfs.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSortedMap <String, VPIf> getAllIfs ()
  {
    return m_aIfs.getClone ();
  }

  @Nonnull
  public Iterator <Map.Entry <String, VPIf>> iterator ()
  {
    return m_aIfs.entrySet ().iterator ();
  }

  public void addIf (@Nonnull final VPIf aIf)
  {
    ValueEnforcer.notNull (aIf, "If");

    m_aIfs.put (aIf.getConditionValue (), aIf);
  }

  @Nonnull
  public VPSelect getClone ()
  {
    // Clone here
    final ICommonsSortedMap <String, VPIf> aIfs = new CommonsTreeMap <> ();
    for (final var e : m_aIfs.entrySet ())
      aIfs.put (e.getKey (), e.getValue ().getClone ());

    // Already cloned
    return new VPSelect (m_eSourceField, aIfs, false);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SourceField", m_eSourceField).append ("Ifs", m_aIfs).getToString ();
  }
}

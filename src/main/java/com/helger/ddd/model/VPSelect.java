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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.ToStringGenerator;

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

  private VPSelect (@Nonnull final EDDDSourceField eSourceField, @Nonnull final ICommonsMap <String, VPIf> aIfs)
  {
    ValueEnforcer.notNull (eSourceField, "SourceField");

    m_eSourceField = eSourceField;
    // Deep clone
    m_aIfs = new CommonsTreeMap <> ();
    for (final Map.Entry <String, VPIf> e : aIfs.entrySet ())
      m_aIfs.put (e.getKey (), e.getValue ().getClone ());
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
    return new VPSelect (m_eSourceField, m_aIfs);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SourceField", m_eSourceField).append ("Ifs", m_aIfs).getToString ();
  }
}

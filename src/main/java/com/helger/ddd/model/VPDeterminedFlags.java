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

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class VPDeterminedFlags implements ICloneable <VPDeterminedFlags>, Iterable <String>
{
  private final ICommonsOrderedSet <String> m_aSet;

  /**
   * Public no-argument constructor.
   */
  public VPDeterminedFlags ()
  {
    m_aSet = new CommonsLinkedHashSet <> ();
  }

  /**
   * Private copy constructor for cloning
   *
   * @param aSet
   *        The set to be copied. May not be <code>null</code>.
   */
  private VPDeterminedFlags (@Nonnull final ICommonsOrderedSet <String> aSet)
  {
    m_aSet = aSet.getClone ();
  }

  public boolean contains (@Nullable final String sFlag)
  {
    return sFlag != null && m_aSet.contains (sFlag);
  }

  @Nonnull
  public EChange add (@Nonnull @Nonempty final String sFlag)
  {
    ValueEnforcer.notEmpty (sFlag, "Flag");
    return m_aSet.addObject (sFlag);
  }

  @Nonnull
  public EChange addAll (@Nonnull final VPDeterminedFlags aFlags)
  {
    ValueEnforcer.notNull (aFlags, "Flags");
    return EChange.valueOf (m_aSet.addAll (aFlags.m_aSet));
  }

  @Nonnull
  public Iterator <String> iterator ()
  {
    return m_aSet.iterator ();
  }

  public boolean isNotEmpty ()
  {
    return m_aSet.isNotEmpty ();
  }

  @Nonnegative
  public int getCount ()
  {
    return m_aSet.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAsSet ()
  {
    return m_aSet.getClone ();
  }

  @Nonnull
  public EChange remove (@Nullable final String sFlag)
  {
    return m_aSet.removeObject (sFlag);
  }

  @Nonnull
  public VPDeterminedFlags getClone ()
  {
    return new VPDeterminedFlags (m_aSet);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Set", m_aSet).getToString ();
  }
}

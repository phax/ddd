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

import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single condition in the Value Provider scope
 *
 * @author Philip Helger
 * @since 0.2.2
 */
public final class VPIf implements ICloneable <VPIf>
{
  private final String m_sConditionValue;

  // The following 2 fields are mutually exclusive
  private final VPDeterminedValues m_aDeterminedValues;
  private final VPDeterminedFlags m_aDeterminedFlags;
  private final ICommonsSortedMap <EDDDSourceField, VPSelect> m_aNestedSelects;

  /**
   * Constructor
   *
   * @param sConditionValue
   *        The condition value to use. May neither be <code>null</code> nor
   *        empty.
   */
  public VPIf (@Nonnull @Nonempty final String sConditionValue)
  {
    ValueEnforcer.notEmpty (sConditionValue, "ConditionValue");

    m_sConditionValue = sConditionValue;
    m_aDeterminedValues = new VPDeterminedValues ();
    m_aDeterminedFlags = new VPDeterminedFlags ();
    m_aNestedSelects = new CommonsTreeMap <> ();
  }

  /**
   * Copy constructor
   *
   * @param sConditionValue
   *        The condition value
   * @param aDeterminedValues
   *        The determined values
   * @param aFlags
   *        The determined flags
   * @param aSelects
   *        The nested selects
   */
  private VPIf (@Nonnull @Nonempty final String sConditionValue,
                @Nonnull final VPDeterminedValues aDeterminedValues,
                @Nonnull final VPDeterminedFlags aFlags,
                @Nonnull final ICommonsMap <EDDDSourceField, VPSelect> aSelects)
  {
    m_sConditionValue = sConditionValue;
    m_aDeterminedValues = aDeterminedValues.getClone ();
    m_aDeterminedFlags = aFlags.getClone ();
    // Deep clone
    m_aNestedSelects = new CommonsTreeMap <> ();
    for (final Map.Entry <EDDDSourceField, VPSelect> e : aSelects.entrySet ())
      m_aNestedSelects.put (e.getKey (), e.getValue ().getClone ());
  }

  /**
   * @return The condition value as provided in the constructor.
   */
  @Nonnull
  @Nonempty
  public String getConditionValue ()
  {
    return m_sConditionValue;
  }

  public boolean hasDeterminedValues ()
  {
    return m_aDeterminedValues.isNotEmpty ();
  }

  @Nonnull
  @ReturnsMutableObject
  public VPDeterminedValues determinedValues ()
  {
    return m_aDeterminedValues;
  }

  /**
   * @return <code>true</code> if at least one flag is present,
   *         <code>false</code> if not.
   * @since 0.5.0
   */
  public boolean hasDeterminedFlags ()
  {
    return m_aDeterminedFlags.isNotEmpty ();
  }

  /**
   * @return A set of specific flag that apply to a specific document type.
   *         Never <code>null</code>.
   * @since 0.5.0
   */
  @Nonnull
  @ReturnsMutableObject
  public VPDeterminedFlags determinedFlags ()
  {
    return m_aDeterminedFlags;
  }

  /**
   * @return <code>true</code> if either a determined value or a flag is
   *         present.
   * @since 0.5.0
   */
  public boolean hasDeterminedValuesOrFlags ()
  {
    return hasDeterminedValues () || hasDeterminedFlags ();
  }

  public boolean hasNestedSelects ()
  {
    return m_aNestedSelects.isNotEmpty ();
  }

  @Nonnull
  @ReturnsMutableObject
  public ICommonsMap <EDDDSourceField, VPSelect> nestedSelects ()
  {
    return m_aNestedSelects;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <EDDDSourceField, VPSelect> getAllNestedSelects ()
  {
    return m_aNestedSelects.getClone ();
  }

  public void addNestedSelect (@Nonnull final VPSelect aSelect)
  {
    ValueEnforcer.notNull (aSelect, "Select");

    if (hasDeterminedValuesOrFlags ())
      throw new IllegalStateException ("An If can either have Determined Values and Flags or Nested Selects but not both");

    final EDDDSourceField eSelector = aSelect.getSourceField ();
    if (m_aNestedSelects.containsKey (eSelector))
      throw new IllegalStateException ("The Selector with ID '" + eSelector.getID () + "' is already contained");
    m_aNestedSelects.put (eSelector, aSelect);
  }

  @Nonnull
  public VPIf getClone ()
  {
    return new VPIf (m_sConditionValue, m_aDeterminedValues, m_aDeterminedFlags, m_aNestedSelects);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("ConditionValue", m_sConditionValue)
                                       .append ("DeterminedValues", m_aDeterminedValues)
                                       .append ("DeterminedFlags", m_aDeterminedFlags)
                                       .append ("NestedSelects", m_aNestedSelects)
                                       .getToString ();
  }
}

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
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.ddd.model.jaxb.vp1.VPIfType;
import com.helger.ddd.model.jaxb.vp1.VPSelectType;
import com.helger.ddd.model.jaxb.vp1.VPSetType;
import com.helger.ddd.model.jaxb.vp1.VPSyntaxType;

/**
 * This class contains the logic to identify missing fields based on existing
 * values. This class is specific per syntax.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class DDDValueProviderPerSyntax
{
  private final String m_sSyntaxID;
  private final ICommonsMap <EDDDSourceField, VPSelect> m_aSelects;

  /**
   * Constructor
   *
   * @param sSyntaxID
   *        The syntax this object works on. May neither be <code>null</code>
   *        nor empty.
   * @param aSelects
   *        Map from (SelectorField) to (Map from (SelectorValue) to (Map from
   *        (TargetField) to (TargetValue)))
   */
  public DDDValueProviderPerSyntax (@Nonnull @Nonempty final String sSyntaxID,
                                    @Nonnull @Nonempty final ICommonsMap <EDDDSourceField, VPSelect> aSelects)
  {
    ValueEnforcer.notEmpty (sSyntaxID, "SyntaxID");
    ValueEnforcer.notEmpty (aSelects, "Selectors");
    m_sSyntaxID = sSyntaxID;
    m_aSelects = aSelects;
  }

  @Nonnull
  @Nonempty
  public final String getSyntaxID ()
  {
    return m_sSyntaxID;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsMap <EDDDSourceField, VPSelect> getAllSelects ()
  {
    return m_aSelects.getClone ();
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableObject
  ICommonsMap <EDDDSourceField, VPSelect> selects ()
  {
    return m_aSelects;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public final ICommonsMap <EDDDSourceField, VPSelect> getAllSelectors ()
  {
    // Deep clone
    final ICommonsMap <EDDDSourceField, VPSelect> ret = new CommonsHashMap <> ();
    for (final Map.Entry <EDDDSourceField, VPSelect> e : m_aSelects.entrySet ())
      ret.put (e.getKey (), e.getValue ().getClone ());
    return ret;
  }

  public interface ISelectorCallback
  {
    /**
     * Selector callback
     *
     * @param aSourceValues
     *        List of all source values used for selection. Never
     *        <code>null</code> nor empty.
     * @param eDeterminedField
     *        The determined field. Never <code>null</code>.
     * @param sDeterminedValue
     *        The determined values. Never <code>null</code>.
     */
    void accept (@Nonnull @Nonempty ICommonsList <VPSourceValue> aSourceValues,
                 @Nonnull EDDDDeterminedField eDeterminedField,
                 @Nonnull String sDeterminedValue);
  }

  public static void _forEachSelectorRecursive (@Nonnull final ICommonsMap <EDDDSourceField, VPSelect> aSelects,
                                                @Nonnull final ICommonsList <VPSourceValue> aSourceValues,
                                                @Nonnull final ISelectorCallback aConsumer)
  {
    for (final Map.Entry <EDDDSourceField, VPSelect> e : aSelects.entrySet ())
    {
      final EDDDSourceField eSourceField = e.getKey ();
      for (final Map.Entry <String, VPIf> e2 : e.getValue ())
      {
        final VPSourceValue aSourceValue = new VPSourceValue (eSourceField, e2.getKey ());
        aSourceValues.add (aSourceValue);

        final VPIf aIf = e2.getValue ();

        if (aIf.hasDeterminedValues ())
        {
          for (final Map.Entry <EDDDDeterminedField, String> e3 : aIf.determinedValues ())
          {
            final EDDDDeterminedField eDeterminedField = e3.getKey ();
            final String sDeterminedValue = e3.getValue ();
            aConsumer.accept (aSourceValues, eDeterminedField, sDeterminedValue);
          }
        }
        else
        {
          // Nested select - call recursively
          _forEachSelectorRecursive (aIf.nestedSelects (), aSourceValues, aConsumer);
        }

        // Remove from state list
        aSourceValues.removeLastOrNull ();
      }
    }
  }

  public final void forEachSelector (@Nonnull final ISelectorCallback aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    // List for keeping state
    final ICommonsList <VPSourceValue> aSourceValues = new CommonsArrayList <> ();
    _forEachSelectorRecursive (m_aSelects, aSourceValues, aConsumer);
  }

  @Nonnull
  private static ESuccess _getAllDeducedValuesRecursive (@Nonnull final Function <EDDDSourceField, String> aSourceProvider,
                                                         @Nonnull final ICommonsMap <EDDDSourceField, VPSelect> aSelects,
                                                         @Nonnull final VPDeterminedValues aTargetValues)
  {
    for (final Map.Entry <EDDDSourceField, VPSelect> aEntry : aSelects.entrySet ())
    {
      final EDDDSourceField eSourceField = aEntry.getKey ();
      final VPSelect aSelect = aEntry.getValue ();

      // Get the source value from the document
      final String sSourceValue = aSourceProvider.apply (eSourceField);
      if (sSourceValue != null)
      {
        // Find all new values, based on source value (e.g. CustomizationID)
        final VPIf aIf = aSelect.getIf (sSourceValue);
        if (aIf != null)
        {
          // Is it the last condition
          if (aIf.hasDeterminedValues ())
          {
            aTargetValues.putAll (aIf.determinedValues ());
            return ESuccess.SUCCESS;
          }

          // Nested selects instead
          if (_getAllDeducedValuesRecursive (aSourceProvider, aIf.nestedSelects (), aTargetValues).isSuccess ())
          {
            // We found something in the nested value
            return ESuccess.SUCCESS;
          }
        }
      }
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  @ReturnsMutableCopy
  public VPDeterminedValues getAllDeducedValues (@Nonnull final Function <EDDDSourceField, String> aSourceProvider)
  {
    ValueEnforcer.notNull (aSourceProvider, "SourceProvider");

    final VPDeterminedValues ret = new VPDeterminedValues ();
    _getAllDeducedValuesRecursive (aSourceProvider, m_aSelects, ret);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SyntaxID", m_sSyntaxID)
                                       .append ("Selectors", m_aSelects)
                                       .getToString ();
  }

  private static void _addSetFromJaxb (@Nonnull final EDDDSourceField eOuterSelector,
                                       @Nonnull final VPSetType aJaxbSet,
                                       @Nonnull final VPDeterminedValues aSetters)
  {
    final String sSetterID = aJaxbSet.getId ();
    final EDDDDeterminedField eSetter = EDDDDeterminedField.getFromIDOrNull (sSetterID);
    if (eSetter == null)
      throw new IllegalStateException ("The selector field '" +
                                       eOuterSelector.getID () +
                                       "' contains the unknown field '" +
                                       sSetterID +
                                       "'. Valid values are: " +
                                       StringHelper.imploder ()
                                                   .source (EDDDDeterminedField.values (), x -> "'" + x.getID () + "'")
                                                   .separator (", ")
                                                   .build ());
    if (eSetter.getSourceField () == eOuterSelector)
      throw new IllegalStateException ("The selector field '" +
                                       eOuterSelector.getID () +
                                       "' cannot be used as a setter field too");

    if (aSetters.containsKey (eSetter))
      throw new IllegalStateException ("The selector with ID '" +
                                       eOuterSelector.getID () +
                                       "' already contains a setter for '" +
                                       sSetterID +
                                       "'");
    aSetters.put (eSetter, aJaxbSet.getValue ().trim ());
  }

  @Nonnull
  private static VPIf _createIfFromJaxb (@Nonnull final EDDDSourceField eOuterSelector, @Nonnull final VPIfType aJaxbIf)
  {
    final String sConditionValue = aJaxbIf.getValue ();
    if (StringHelper.hasNoText (sConditionValue))
      throw new IllegalStateException ("The selector '" +
                                       eOuterSelector.getID () +
                                       "' contains a condition with an empty value");

    // Read all setters
    final VPIf aIf = new VPIf (sConditionValue);
    for (final VPSetType aJaxbSet : aJaxbIf.getSet ())
      _addSetFromJaxb (eOuterSelector, aJaxbSet, aIf.determinedValues ());

    // Read additional selectors
    for (final VPSelectType aJaxbSelect : aJaxbIf.getSelect ())
    {
      final VPSelect aSelect = _createSelectFromJaxb (aJaxbSelect);
      aIf.addNestedSelect (aSelect);
    }

    if (aIf.hasDeterminedValues () && aIf.hasNestedSelects ())
      throw new IllegalStateException ("The selector '" +
                                       eOuterSelector.getID () +
                                       "' contains a condition with an values and nested selects. This is not allowed");

    return aIf;
  }

  @Nonnull
  private static VPSelect _createSelectFromJaxb (@Nonnull final VPSelectType aJaxbSelect)
  {
    // Selector field
    final String sSelectorID = aJaxbSelect.getId ();
    final EDDDSourceField eSelector = EDDDSourceField.getFromIDOrNull (sSelectorID);
    if (eSelector == null)
      throw new IllegalStateException ("The selector field '" +
                                       sSelectorID +
                                       "' is unknown. Valid values are: " +
                                       StringHelper.imploder ()
                                                   .source (EDDDSourceField.values (), x -> "'" + x.getID () + "'")
                                                   .separator (", ")
                                                   .build ());

    final VPSelect aSelect = new VPSelect (eSelector);

    // Read all "if"s
    for (final VPIfType aJaxbIf : aJaxbSelect.getIf ())
    {
      final VPIf aIf = _createIfFromJaxb (eSelector, aJaxbIf);
      final String sConditionValue = aIf.getConditionValue ();
      if (aSelect.containsIf (sConditionValue))
        throw new IllegalStateException ("The selector '" +
                                         eSelector.getID () +
                                         "' already has a condition with value '" +
                                         sConditionValue +
                                         "'");
      aSelect.addIf (aIf);
    }

    return aSelect;
  }

  /**
   * Create a new {@link DDDValueProviderPerSyntax} by reading it from the
   * provided JAXB object.
   *
   * @param aJaxbSyntax
   *        The XML object to parse. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link DDDValueProviderList} contained
   *         the read data.
   */
  @Nonnull
  public static DDDValueProviderPerSyntax createFromJaxb (@Nonnull final VPSyntaxType aJaxbSyntax)
  {
    ValueEnforcer.notNull (aJaxbSyntax, "Syntax");

    final String sSyntaxID = aJaxbSyntax.getId ();

    // Read all selects
    final ICommonsMap <EDDDSourceField, VPSelect> aSelects = new CommonsHashMap <> ();
    for (final VPSelectType aJaxbSelect : aJaxbSyntax.getSelect ())
    {
      final VPSelect aSelect = _createSelectFromJaxb (aJaxbSelect);
      final EDDDSourceField eSelector = aSelect.getSourceField ();
      if (aSelects.containsKey (eSelector))
        throw new IllegalStateException ("The selector with ID '" + eSelector.getID () + "' is already contained");
      aSelects.put (eSelector, aSelect);
    }

    return new DDDValueProviderPerSyntax (sSyntaxID, aSelects);
  }
}

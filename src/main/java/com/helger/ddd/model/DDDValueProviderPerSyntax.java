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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ESuccess;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.ddd.model.jaxb.vp1.VPIfType;
import com.helger.ddd.model.jaxb.vp1.VPSelectType;
import com.helger.ddd.model.jaxb.vp1.VPSetType;
import com.helger.ddd.model.jaxb.vp1.VPSyntaxType;

/**
 * This class contains the logic to identify missing fields based on existing values. This class is
 * specific per syntax.
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
   *        The syntax this object works on. May neither be <code>null</code> nor empty.
   * @param aSelects
   *        Map from (SelectorField) to (Map from (SelectorValue) to (Map from (TargetField) to
   *        (TargetValue)))
   */
  public DDDValueProviderPerSyntax (@NonNull @Nonempty final String sSyntaxID,
                                    @NonNull @Nonempty final ICommonsMap <EDDDSourceField, VPSelect> aSelects)
  {
    ValueEnforcer.notEmpty (sSyntaxID, "SyntaxID");
    ValueEnforcer.notEmpty (aSelects, "Selectors");
    m_sSyntaxID = sSyntaxID;
    m_aSelects = aSelects;
  }

  @NonNull
  @Nonempty
  public final String getSyntaxID ()
  {
    return m_sSyntaxID;
  }

  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsMap <EDDDSourceField, VPSelect> getAllSelects ()
  {
    return m_aSelects.getClone ();
  }

  @NonNull
  @Nonempty
  @ReturnsMutableObject
  ICommonsMap <EDDDSourceField, VPSelect> selects ()
  {
    return m_aSelects;
  }

  @NonNull
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
     * Selector callback for a determined value
     *
     * @param aSourceValues
     *        List of all source values used for selection. Never <code>null</code> nor empty.
     * @param eDeterminedField
     *        The determined field. Never <code>null</code>.
     * @param sDeterminedValue
     *        The determined values. Never <code>null</code>.
     * @since 0.5.0 the name changed from <code>accept</code> to <code>acceptDeterminedValue</code>
     */
    void acceptDeterminedValue (@NonNull @Nonempty ICommonsList <VPSourceValue> aSourceValues,
                                @NonNull EDDDDeterminedField eDeterminedField,
                                @NonNull String sDeterminedValue);

    /**
     * Selector callback for a flag
     *
     * @param aSourceValues
     *        List of all source values used for selection. Never <code>null</code> nor empty.
     * @param sFlag
     *        The flag value Neither <code>null</code> nor empty.
     * @since 0.5.0
     */
    void acceptFlag (@NonNull @Nonempty ICommonsList <VPSourceValue> aSourceValues, @NonNull @Nonempty String sFlag);
  }

  private static void _forEachSelectorRecursive (@NonNull final ICommonsMap <EDDDSourceField, VPSelect> aSelects,
                                                 @NonNull final ICommonsList <VPSourceValue> aSourceValues,
                                                 @NonNull final ISelectorCallback aConsumer)
  {
    for (final Map.Entry <EDDDSourceField, VPSelect> e : aSelects.entrySet ())
    {
      final EDDDSourceField eSourceField = e.getKey ();
      for (final Map.Entry <String, VPIf> e2 : e.getValue ())
      {
        final VPSourceValue aSourceValue = new VPSourceValue (eSourceField, e2.getKey ());
        aSourceValues.add (aSourceValue);

        final VPIf aIf = e2.getValue ();

        if (aIf.hasDeterminedValuesOrFlags ())
        {
          // For all determined values (0-n)
          for (final Map.Entry <EDDDDeterminedField, String> e3 : aIf.determinedValues ())
          {
            final EDDDDeterminedField eDeterminedField = e3.getKey ();
            final String sDeterminedValue = e3.getValue ();
            aConsumer.acceptDeterminedValue (aSourceValues, eDeterminedField, sDeterminedValue);
          }

          // For all flags (0-n)
          for (final String sFlag : aIf.determinedFlags ())
            aConsumer.acceptFlag (aSourceValues, sFlag);
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

  public final void forEachSelector (@NonNull final ISelectorCallback aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    // List for keeping state
    final ICommonsList <VPSourceValue> aSourceValues = new CommonsArrayList <> ();
    _forEachSelectorRecursive (m_aSelects, aSourceValues, aConsumer);
  }

  @NonNull
  private static ESuccess _getAllDeducedValuesRecursive (@NonNull final Function <EDDDSourceField, String> aSourceProvider,
                                                         @NonNull final ICommonsMap <EDDDSourceField, VPSelect> aSelects,
                                                         @NonNull final VPDeterminedValues aTargetDeterminedValues,
                                                         @NonNull final VPDeterminedFlags aTargetDeterminedFlags)
  {
    for (final var aEntry : aSelects.entrySet ())
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
          // Is it the last condition with a value or flags?
          if (aIf.hasDeterminedValuesOrFlags ())
          {
            aTargetDeterminedValues.putAll (aIf.determinedValues ());
            aTargetDeterminedFlags.addAll (aIf.determinedFlags ());
            return ESuccess.SUCCESS;
          }

          // Nested selects instead
          if (_getAllDeducedValuesRecursive (aSourceProvider,
                                             aIf.nestedSelects (),
                                             aTargetDeterminedValues,
                                             aTargetDeterminedFlags).isSuccess ())
          {
            // We found something in the nested value
            return ESuccess.SUCCESS;
          }
        }
      }
    }
    return ESuccess.FAILURE;
  }

  public void forAllDeducedValues (@NonNull final Function <EDDDSourceField, String> aSourceProvider,
                                   @NonNull final VPDeterminedValues aDeterminedValues,
                                   @NonNull final VPDeterminedFlags aDeterminedFlags)
  {
    ValueEnforcer.notNull (aSourceProvider, "SourceProvider");
    ValueEnforcer.notNull (aDeterminedValues, "DeterminedValues");
    ValueEnforcer.notNull (aDeterminedFlags, "DeterminedFlags");

    _getAllDeducedValuesRecursive (aSourceProvider, m_aSelects, aDeterminedValues, aDeterminedFlags);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SyntaxID", m_sSyntaxID).append ("Selects", m_aSelects).getToString ();
  }

  private static void _addSetFromJaxb (@NonNull final EDDDSourceField eOuterSelector,
                                       @NonNull final VPSetType aJaxbSet,
                                       @NonNull final VPDeterminedValues aTargetSetters)
  {
    final String sSetterID = aJaxbSet.getId ();

    // Check if the setter is known. Only specific setters are allowed
    final EDDDDeterminedField eSetter = EDDDDeterminedField.getFromIDOrNull (sSetterID);
    if (eSetter == null)
    {
      throw new IllegalStateException ("The selector field '" +
                                       eOuterSelector.getID () +
                                       "' contains the unknown field '" +
                                       sSetterID +
                                       "'. Valid values are: " +
                                       StringImplode.imploder ()
                                                    .source (EDDDDeterminedField.values (), x -> "'" + x.getID () + "'")
                                                    .separator (", ")
                                                    .build ());
    }

    // Special case: the selector cannot be identical to the setter. Simply
    // makes no sense
    if (eSetter.getSourceField () == eOuterSelector)
    {
      throw new IllegalStateException ("The selector field '" +
                                       eOuterSelector.getID () +
                                       "' cannot be used as a setter field too");
    }

    // And finally make sure, each setter is contained only once per set
    if (aTargetSetters.containsKey (eSetter))
    {
      throw new IllegalStateException ("The selector with ID '" +
                                       eOuterSelector.getID () +
                                       "' already contains a setter for '" +
                                       sSetterID +
                                       "'");
    }

    // Store :)
    aTargetSetters.put (eSetter, aJaxbSet.getValue ().trim ());
  }

  private static void _addFlagFromJaxb (@NonNull final EDDDSourceField eOuterSelector,
                                        @NonNull final String sJaxbFlag,
                                        @NonNull final VPDeterminedFlags aTargetFlags)
  {
    final String sUnifiedFlag = sJaxbFlag.trim ();

    // Check it is not empty
    if (StringHelper.isEmpty (sUnifiedFlag))
    {
      throw new IllegalStateException ("The selector with ID '" + eOuterSelector.getID () + "' contains an empty flag");
    }

    // And finally make sure, each setter is contained only once per set
    if (aTargetFlags.contains (sUnifiedFlag))
    {
      throw new IllegalStateException ("The selector with ID '" +
                                       eOuterSelector.getID () +
                                       "' already contains the flag '" +
                                       sUnifiedFlag +
                                       "'");
    }

    // Store :)
    aTargetFlags.add (sUnifiedFlag);
  }

  @NonNull
  private static VPIf _createIfFromJaxb (@NonNull final EDDDSourceField eOuterSelector, @NonNull final VPIfType aJaxbIf)
  {
    final String sConditionValue = aJaxbIf.getValue ();
    if (StringHelper.isEmpty (sConditionValue))
      throw new IllegalStateException ("The selector '" +
                                       eOuterSelector.getID () +
                                       "' contains a condition with an empty Condition Value");

    // Read all setters
    final VPIf aIf = new VPIf (sConditionValue);
    for (final VPSetType aJaxbSet : aJaxbIf.getSet ())
      _addSetFromJaxb (eOuterSelector, aJaxbSet, aIf.determinedValues ());

    for (final String sFlag : aJaxbIf.getFlag ())
      _addFlagFromJaxb (eOuterSelector, sFlag, aIf.determinedFlags ());

    // Read additional selectors
    for (final VPSelectType aJaxbSelect : aJaxbIf.getSelect ())
    {
      final VPSelect aSelect = _createSelectFromJaxb (aJaxbSelect);
      aIf.addNestedSelect (aSelect);
    }

    if (!aIf.hasDeterminedValuesOrFlags () && !aIf.hasNestedSelects ())
    {
      throw new IllegalStateException ("The selector '" +
                                       eOuterSelector.getID () +
                                       "' contains a condition with neither Determined Values, Flags nor Nested Selects. This is not allowed.");
    }

    if (aIf.hasDeterminedValuesOrFlags () && aIf.hasNestedSelects ())
    {
      throw new IllegalStateException ("The selector '" +
                                       eOuterSelector.getID () +
                                       "' contains a condition with Determined Values or Flags and Nested Selects. This is not allowed.");
    }

    return aIf;
  }

  /**
   * Convert a JAXB select to a domain select. This method is called indirectly recursive from
   * <code>_createIfFromJaxb</code>.
   *
   * @param aJaxbSelect
   *        The JAXB select. May not be <code>null</code>.
   * @return The domain select. Never <code>null</code>.
   */
  @NonNull
  private static VPSelect _createSelectFromJaxb (@NonNull final VPSelectType aJaxbSelect)
  {
    // Selector field
    final String sSelectorID = aJaxbSelect.getId ();
    final EDDDSourceField eSelector = EDDDSourceField.getFromIDOrNull (sSelectorID);
    if (eSelector == null)
      throw new IllegalStateException ("The selector field '" +
                                       sSelectorID +
                                       "' is unknown. Valid values are: " +
                                       StringImplode.imploder ()
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
   * Create a new {@link DDDValueProviderPerSyntax} by reading it from the provided JAXB object.
   *
   * @param aJaxbSyntax
   *        The XML object to parse. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link DDDValueProviderList} contained the read data.
   */
  @NonNull
  public static DDDValueProviderPerSyntax createFromJaxb (@NonNull final VPSyntaxType aJaxbSyntax)
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

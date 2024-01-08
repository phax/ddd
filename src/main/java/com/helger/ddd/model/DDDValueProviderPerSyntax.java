/*
 * Copyright (C) 2023 Philip Helger (www.helger.com)
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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.microdom.IMicroElement;

/**
 * This class contains the logic to identify missing fields based on existing
 * values. This class is specific per syntax.
 *
 * @author Philip Helger
 */
@Immutable
public class DDDValueProviderPerSyntax
{
  private final String m_sSyntaxID;
  private final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> m_aSelectors;

  /**
   * Constructor
   *
   * @param sSyntaxID
   *        The syntax this object works on. May neither be <code>null</code>
   *        nor empty.
   * @param aSelectors
   *        Map from (SelectorField) to (Map from (SelectorValue) to (Map from
   *        (TargetField) to (TargetValue)))
   */
  public DDDValueProviderPerSyntax (@Nonnull @Nonempty final String sSyntaxID,
                                    @Nonnull @Nonempty final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> aSelectors)
  {
    ValueEnforcer.notEmpty (sSyntaxID, "SyntaxID");
    ValueEnforcer.notEmpty (aSelectors, "Selectors");
    m_sSyntaxID = sSyntaxID;
    m_aSelectors = aSelectors;
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
  public final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> getAllSelectors ()
  {
    // Deep clone
    final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> ret = new CommonsHashMap <> ();
    for (final Map.Entry <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> e : m_aSelectors.entrySet ())
    {
      final ICommonsMap <String, ICommonsMap <EDDDField, String>> ret2 = new CommonsHashMap <> ();
      for (final Map.Entry <String, ICommonsMap <EDDDField, String>> e2 : e.getValue ().entrySet ())
        ret2.put (e2.getKey (), e2.getValue ().getClone ());
      ret.put (e.getKey (), ret2);
    }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <EDDDField, String> getAllDeducedValues (@Nonnull final Function <EDDDField, String> aSourceProvider)
  {
    ValueEnforcer.notNull (aSourceProvider, "SourceProvider");

    final ICommonsMap <EDDDField, String> ret = new CommonsHashMap <> ();
    for (final Map.Entry <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> aEntry : m_aSelectors.entrySet ())
    {
      // Get the source value
      final EDDDField eSelector = aEntry.getKey ();
      final String sSourceValue = aSourceProvider.apply (eSelector);
      if (sSourceValue != null)
      {
        // Find all new values, based on source value (e.g. CustomizationID)
        final ICommonsMap <EDDDField, String> aSetters = aEntry.getValue ().get (sSourceValue);
        if (aSetters != null)
        {
          ret.putAll (aSetters);
          break;
        }
      }
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("SyntaxID", m_sSyntaxID)
                                       .append ("Selectors", m_aSelectors)
                                       .getToString ();
  }

  @Nonnull
  public static DDDValueProviderPerSyntax readFromXML (@Nonnull final IMicroElement eVesid)
  {
    final String sSyntaxID = eVesid.getAttributeValue ("id");
    if (StringHelper.hasNoText (sSyntaxID))
      throw new IllegalStateException ("Failed to read a syntax ID");

    final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> aMap = new CommonsHashMap <> ();
    for (final IMicroElement eSelect : eVesid.getAllChildElements ("select"))
    {
      // Selector field
      final String sSelectorID = eSelect.getAttributeValue ("id");
      final EDDDField eSelector = EDDDField.getFromIDOrNull (sSelectorID);
      if (eSelector == null)
        throw new IllegalStateException ("The selector field '" +
                                         sSelectorID +
                                         "' is unknown. Valid values are: " +
                                         StringHelper.imploder ()
                                                     .source (EDDDField.values (), x -> "'" + x.getID () + "'")
                                                     .separator (", ")
                                                     .build ());

      if (aMap.containsKey (eSelector))
        throw new IllegalStateException ("The selector with ID '" + sSelectorID + "' is already contained");

      // Read all "if"s
      final ICommonsMap <String, ICommonsMap <EDDDField, String>> aSelectors = new CommonsHashMap <> ();
      for (final IMicroElement eIf : eSelect.getAllChildElements ("if"))
      {
        final String sConditionValue = eIf.getAttributeValue ("value");

        if (aSelectors.containsKey (sConditionValue))
          throw new IllegalStateException ("The selector '" +
                                           sSelectorID +
                                           "' already has a condition with value '" +
                                           sConditionValue +
                                           "'");

        // Read all setters
        final ICommonsMap <EDDDField, String> aSetters = new CommonsHashMap <> ();
        for (final IMicroElement eSet : eIf.getAllChildElements ("set"))
        {
          final String sSetterID = eSet.getAttributeValue ("id");
          final EDDDField eSetter = EDDDField.getFromIDOrNull (sSetterID);
          if (eSetter == null)
            throw new IllegalStateException ("The selector field '" +
                                             sSelectorID +
                                             "' contains the unknown field '" +
                                             sSetterID +
                                             "'. Valid values are: " +
                                             StringHelper.imploder ()
                                                         .source (EDDDField.values (), x -> "'" + x.getID () + "'")
                                                         .separator (", ")
                                                         .build ());
          if (eSetter == eSelector)
            throw new IllegalStateException ("The selector field '" +
                                             sSelectorID +
                                             "' cannot be used as a setter field too");

          if (aSetters.containsKey (eSetter))
            throw new IllegalStateException ("The selector with ID '" +
                                             sSelectorID +
                                             "' already contains a setter for '" +
                                             sSetterID +
                                             "'");
          aSetters.put (eSetter, eSet.getTextContentTrimmed ());
        }
        aSelectors.put (sConditionValue, aSetters);
      }
      aMap.put (eSelector, aSelectors);
    }

    return new DDDValueProviderPerSyntax (sSyntaxID, aMap);
  }
}

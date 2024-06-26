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

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * This class manages a list of {@link DDDValueProviderPerSyntax} objects. The
 * key is the syntax ID. A default mapping is provided as part of the library.
 * See {@link #getDefaultValueProviderList()}.
 *
 * @author Philip Helger
 */
public class DDDValueProviderList
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DDDValueProviderList.class);

  /** The resource that contains the default value provide list */
  public static final IReadableResource DEFAULT_VALUE_PROVIDER_LIST_RES = new ClassPathResource ("ddd/value-providers.xml",
                                                                                                 DDDValueProviderList.class.getClassLoader ());

  private static class SingletonHolder
  {
    static final DDDValueProviderList INSTANCE = readFromXML (DEFAULT_VALUE_PROVIDER_LIST_RES);
  }

  private final LocalDate m_aLastMod;
  private final ICommonsMap <String, DDDValueProviderPerSyntax> m_aVPPerSyntaxes;

  public DDDValueProviderList (@Nonnull final LocalDate aLastMod,
                               @Nonnull final ICommonsMap <String, DDDValueProviderPerSyntax> aSyntaxes)
  {
    ValueEnforcer.notNull (aLastMod, "LastMod");
    ValueEnforcer.notNull (aSyntaxes, "Syntaxes");
    m_aLastMod = aLastMod;
    m_aVPPerSyntaxes = aSyntaxes;
  }

  @Nonnull
  public final LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  @Nonnull
  @ReturnsMutableObject
  public final ICommonsMap <String, DDDValueProviderPerSyntax> valueProvidersPerSyntaxes ()
  {
    return m_aVPPerSyntaxes;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsMap <String, DDDValueProviderPerSyntax> getAllValueProvidersPerSyntaxes ()
  {
    return m_aVPPerSyntaxes.getClone ();
  }

  @Nullable
  public DDDValueProviderPerSyntax getValueProviderPerSyntax (@Nullable final String sSyntaxID)
  {
    return m_aVPPerSyntaxes.get (sSyntaxID);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("LastModification", m_aLastMod)
                                       .append ("VPPerSyntaxes", m_aVPPerSyntaxes)
                                       .getToString ();
  }

  /**
   * @return The value provider list from the default file. Never
   *         <code>null</code>.
   * @see #readFromXML(IReadableResource)
   * @see #DEFAULT_VALUE_PROVIDER_LIST_RES
   */
  @Nonnull
  public static DDDValueProviderList getDefaultValueProviderList ()
  {
    return SingletonHolder.INSTANCE;
  }

  @Nonnull
  public static DDDValueProviderList readFromXML (@Nonnull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    LOGGER.info ("Reading DDDValueProviderList from '" + aRes.getPath () + "'");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    if (aDoc == null)
      throw new IllegalArgumentException ("Failed to read DDD syntax list");

    final LocalDate aLastMod = aDoc.getDocumentElement ().getAttributeValueWithConversion ("lastmod", LocalDate.class);
    if (aLastMod == null)
      throw new IllegalArgumentException ("The DDD syntax list is missing a valid 'lastmod' attribute");

    final ICommonsMap <String, DDDValueProviderPerSyntax> aSyntaxes = new CommonsHashMap <> ();
    for (final IMicroElement eSyntax : aDoc.getDocumentElement ().getAllChildElements ("syntax"))
    {
      final DDDValueProviderPerSyntax aVesidPerSyntax = DDDValueProviderPerSyntax.readFromXML (eSyntax);
      if (aSyntaxes.containsKey (aVesidPerSyntax.getSyntaxID ()))
        throw new IllegalStateException ("Another DDD syntax with ID '" +
                                         aVesidPerSyntax.getSyntaxID () +
                                         "' is already contained");

      aSyntaxes.put (aVesidPerSyntax.getSyntaxID (), aVesidPerSyntax);
    }
    return new DDDValueProviderList (aLastMod, aSyntaxes);
  }

  @Nonnull
  @ReturnsMutableCopy
  private static ICommonsMap <EDDDSourceField, VPSelect> _recursiveMergeSelects (@Nonnull final ICommonsMap <EDDDSourceField, VPSelect> aSelects1,
                                                                                 @Nonnull final ICommonsMap <EDDDSourceField, VPSelect> aSelects2)
  {
    final ICommonsMap <EDDDSourceField, VPSelect> aMergedSelects = new CommonsHashMap <> ();
    for (final var aEntrySourceField : aSelects1.entrySet ())
    {
      final EDDDSourceField eSourceField = aEntrySourceField.getKey ();

      final VPSelect aSelect1 = aEntrySourceField.getValue ();
      final VPSelect aSelect2 = aSelects2.get (eSourceField);
      if (aSelect2 == null)
      {
        // Source field only present in VPL1 source field
        aMergedSelects.put (eSourceField, aSelect1);
      }
      else
      {
        // Source field present in VPL1 and VPL2
        final ICommonsSortedMap <String, VPIf> aIfs1 = aSelect1.getAllIfs ();
        final ICommonsSortedMap <String, VPIf> aIfs2 = aSelect2.getAllIfs ();

        final ICommonsSortedMap <String, VPIf> aMergedIfs = new CommonsTreeMap <> ();
        for (final var aEntryIf : aIfs1.entrySet ())
        {
          final String sConditionValue = aEntryIf.getKey ();

          final VPIf aIf1 = aEntryIf.getValue ();
          final VPIf aIf2 = aIfs2.get (sConditionValue);

          if (aIf2 == null)
          {
            // If only contained in VPL1
            aMergedIfs.put (sConditionValue, aIf1);
          }
          else
          {
            final VPIf aMergedIf = new VPIf (sConditionValue);

            // If contained in VPL1 and VPL2
            if (aIf1.hasDeterminedValues ())
            {
              if (aIf2.hasDeterminedValues ())
              {
                for (final var aEntryValue : aIf1.determinedValues ())
                {
                  final EDDDDeterminedField eDeterminedField = aEntryValue.getKey ();
                  final String sDeterminedValue1 = aEntryValue.getValue ();
                  final String sDeterminedValue2 = aIf2.determinedValues ().get (eDeterminedField);
                  if (sDeterminedValue2 == null)
                  {
                    // Only contained in VPL1
                    aMergedIf.determinedValues ().put (eDeterminedField, sDeterminedValue1);
                  }
                  else
                  {
                    if (sDeterminedValue1.equals (sDeterminedValue2))
                    {
                      LOGGER.info ("Merged <If>-values '" + sDeterminedValue1 + "' are identical.");
                      aMergedIf.determinedValues ().put (eDeterminedField, sDeterminedValue1);
                    }
                    else
                    {
                      // In the future we might provide a parameter that allows
                      // for overwriting old values. Currently this is not
                      // foreseen
                      throw new IllegalStateException ("Cannot merge two <Ifs> for because determined field " +
                                                       eDeterminedField +
                                                       " has 2 different values ('" +
                                                       sDeterminedValue1 +
                                                       "' vs. '" +
                                                       sDeterminedValue2 +
                                                       "')");
                    }
                  }
                }

                // Add all values only contained in If2
                for (final var aEntryValue : aIf2.determinedValues ())
                  if (!aIf1.determinedValues ().containsKey (aEntryValue.getKey ()))
                    aMergedIf.determinedValues ().put (aEntryValue.getKey (), aEntryValue.getValue ());
              }
              else
                throw new IllegalStateException ("Cannot merge two <Ifs> where one has a value and the other one has nested selects");
            }
            else
            {
              if (aIf2.hasDeterminedValues ())
              {
                throw new IllegalStateException ("Cannot merge two <Ifs> where one has nested selects and the other one has a value");
              }

              // Recursive call
              final var aMergedNestedSelects = _recursiveMergeSelects (aIf1.nestedSelects (), aIf2.nestedSelects ());
              for (final var aMergedNestedSelect : aMergedNestedSelects.values ())
                aMergedIf.addNestedSelect (aMergedNestedSelect);
            }

            aMergedIfs.put (sConditionValue, aMergedIf);
          }
        }

        // add all if only contained in VPL2
        for (final var aEntryIf : aIfs2.entrySet ())
          if (!aIfs1.containsKey (aEntryIf.getKey ()))
            aMergedIfs.put (aEntryIf.getKey (), aEntryIf.getValue ());

        aMergedSelects.put (eSourceField, new VPSelect (eSourceField, aMergedIfs));
      }
    }

    // add all source fields only contained in VPL2
    for (final var aEntrySourceField : aSelects2.entrySet ())
      if (!aSelects1.containsKey (aEntrySourceField.getKey ()))
        aMergedSelects.put (aEntrySourceField.getKey (), aEntrySourceField.getValue ());

    return aMergedSelects;
  }

  @Nonnull
  public static DDDValueProviderList createMergedValueProviderList (@Nonnull final DDDValueProviderList aVPL1,
                                                                    @Nonnull final DDDValueProviderList aVPL2)
  {
    ValueEnforcer.notNull (aVPL1, "ValueProviderList1");
    ValueEnforcer.notNull (aVPL2, "ValueProviderList2");

    // find latest last modification
    final LocalDate aLastMod = aVPL1.getLastModification ().isAfter (aVPL2.getLastModification ()) ? aVPL1
                                                                                                          .getLastModification ()
                                                                                                   : aVPL2.getLastModification ();

    // Merge on syntax level
    final ICommonsMap <String, DDDValueProviderPerSyntax> aMergedVPsPerSyntax = new CommonsHashMap <> ();
    for (final var aEntrySyntax : aVPL1.m_aVPPerSyntaxes.entrySet ())
    {
      final String sSyntaxKey = aEntrySyntax.getKey ();

      final DDDValueProviderPerSyntax aVPPS1 = aEntrySyntax.getValue ();
      final DDDValueProviderPerSyntax aVPPS2 = aVPL2.m_aVPPerSyntaxes.get (sSyntaxKey);
      if (aVPPS2 == null)
      {
        // Syntax only present in VPL1
        aMergedVPsPerSyntax.put (sSyntaxKey, aVPPS1);
      }
      else
      {
        // Syntax present in VPL1 and VPL2
        // Merge by source field (e.g. "Customization ID")
        final ICommonsMap <EDDDSourceField, VPSelect> aSelects1 = aVPPS1.selects ();
        final ICommonsMap <EDDDSourceField, VPSelect> aSelects2 = aVPPS2.selects ();

        final ICommonsMap <EDDDSourceField, VPSelect> aMergedSelects = _recursiveMergeSelects (aSelects1, aSelects2);

        aMergedVPsPerSyntax.put (sSyntaxKey, new DDDValueProviderPerSyntax (sSyntaxKey, aMergedSelects));
      }
    }

    // add all syntaxes only contained in VPL2
    for (final var aEntrySyntax : aVPL2.m_aVPPerSyntaxes.entrySet ())
      if (!aVPL1.m_aVPPerSyntaxes.containsKey (aEntrySyntax.getKey ()))
        aMergedVPsPerSyntax.put (aEntrySyntax.getKey (), aEntrySyntax.getValue ());

    return new DDDValueProviderList (aLastMod, aMergedVPsPerSyntax);
  }
}

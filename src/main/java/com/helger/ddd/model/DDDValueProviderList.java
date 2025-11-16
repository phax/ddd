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

import java.time.LocalDate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.ddd.model.jaxb.ValueProviderListMarshaller;
import com.helger.ddd.model.jaxb.vp1.VPSyntaxType;
import com.helger.ddd.model.jaxb.vp1.ValueProvidersType;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.IReadableResource;

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

  public DDDValueProviderList (@NonNull final LocalDate aLastMod,
                               @NonNull final ICommonsMap <String, DDDValueProviderPerSyntax> aSyntaxes)
  {
    ValueEnforcer.notNull (aLastMod, "LastMod");
    ValueEnforcer.notNull (aSyntaxes, "Syntaxes");
    m_aLastMod = aLastMod;
    m_aVPPerSyntaxes = aSyntaxes;
  }

  @NonNull
  public final LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  @NonNull
  @ReturnsMutableObject
  public final ICommonsMap <String, DDDValueProviderPerSyntax> valueProvidersPerSyntaxes ()
  {
    return m_aVPPerSyntaxes;
  }

  @NonNull
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
  @NonNull
  public static DDDValueProviderList getDefaultValueProviderList ()
  {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Create a new {@link DDDValueProviderList} by reading it from the provided
   * readable resource.
   *
   * @param aRes
   *        The XML resource to read from. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link DDDValueProviderList} contained
   *         the read data.
   * @throws IllegalArgumentException
   *         If the XML has the wrong layout
   * @see #createFromJaxb(ValueProvidersType)
   */
  @NonNull
  public static DDDValueProviderList readFromXML (@NonNull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    LOGGER.info ("Reading DDDValueProviderList from '" + aRes.getPath () + "'");

    final ValueProvidersType aJaxbVps = new ValueProviderListMarshaller ().read (aRes);
    if (aJaxbVps == null)
      throw new IllegalArgumentException ("Failed to read DDD syntax list as XML");

    return createFromJaxb (aJaxbVps);
  }

  /**
   * Create a new {@link DDDValueProviderList} by reading it from the provided
   * readable resource.
   *
   * @param aJaxbVps
   *        The JAXB object to read. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link DDDValueProviderList} contained
   *         the read data.
   * @see DDDValueProviderPerSyntax#createFromJaxb(VPSyntaxType)
   */
  @NonNull
  public static DDDValueProviderList createFromJaxb (@NonNull final ValueProvidersType aJaxbVps)
  {
    ValueEnforcer.notNull (aJaxbVps, "JaxbVps");
    // Last modification
    final LocalDate aLastMod = aJaxbVps.getLastmod ().toLocalDate ();

    // Read all syntaxes
    final ICommonsMap <String, DDDValueProviderPerSyntax> aSyntaxes = new CommonsHashMap <> ();
    for (final VPSyntaxType aJaxbSyntax : aJaxbVps.getSyntax ())
    {
      final DDDValueProviderPerSyntax aVesidPerSyntax = DDDValueProviderPerSyntax.createFromJaxb (aJaxbSyntax);
      if (aSyntaxes.containsKey (aVesidPerSyntax.getSyntaxID ()))
        throw new IllegalStateException ("Another DDD syntax with ID '" +
                                         aVesidPerSyntax.getSyntaxID () +
                                         "' is already contained");

      aSyntaxes.put (aVesidPerSyntax.getSyntaxID (), aVesidPerSyntax);
    }
    return new DDDValueProviderList (aLastMod, aSyntaxes);
  }

  @NonNull
  @ReturnsMutableCopy
  private static ICommonsMap <EDDDSourceField, VPSelect> _recursiveMergeSelects (@NonNull final ICommonsMap <EDDDSourceField, VPSelect> aSelects1,
                                                                                 @NonNull final ICommonsMap <EDDDSourceField, VPSelect> aSelects2)
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
            if (aIf1.hasDeterminedValuesOrFlags ())
            {
              if (aIf2.hasDeterminedValuesOrFlags ())
              {
                // Merge all determined values
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
                      throw new IllegalStateException ("Cannot merge two <Ifs> for because Determined Value field " +
                                                       eDeterminedField +
                                                       " has 2 different values ('" +
                                                       sDeterminedValue1 +
                                                       "' vs. '" +
                                                       sDeterminedValue2 +
                                                       "')");
                    }
                  }
                }

                // Add all determined values only contained in If2
                for (final var aEntryValue : aIf2.determinedValues ())
                  if (!aIf1.determinedValues ().containsKey (aEntryValue.getKey ()))
                    aMergedIf.determinedValues ().put (aEntryValue.getKey (), aEntryValue.getValue ());

                // Merge all flags
                aMergedIf.determinedFlags ().addAll (aIf1.determinedFlags ());
                aMergedIf.determinedFlags ().addAll (aIf2.determinedFlags ());
              }
              else
                throw new IllegalStateException ("Cannot merge two <Ifs> where one has Determined Value or Flags and the other one has Nested Selects");
            }
            else
            {
              if (aIf2.hasDeterminedValuesOrFlags ())
              {
                throw new IllegalStateException ("Cannot merge two <Ifs> where one has Nested Selects and the other one has Determined Values or Flags");
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

  /**
   * Merge the two provided {@link DDDValueProviderList} objects to a new one.
   * This is a real recursive merge on all layers. So either new syntaxes might
   * be added as well as existing syntaxes might be extended. However, and
   * overwrite of existing data is not possible and will lead to an exception.
   *
   * @param aVPL1
   *        The first {@link DDDValueProviderList} to merge. Must not be
   *        <code>null</code>.
   * @param aVPL2
   *        The second {@link DDDValueProviderList} to merge. Must not be
   *        <code>null</code>.
   * @return The merged {@link DDDValueProviderList} containing the data of both
   *         source objects and never <code>null</code>.
   * @throws IllegalStateException
   *         If the two objects cannot be merged.
   * @since 0.3.1
   */
  @NonNull
  public static DDDValueProviderList createMergedValueProviderList (@NonNull final DDDValueProviderList aVPL1,
                                                                    @NonNull final DDDValueProviderList aVPL2)
  {
    ValueEnforcer.notNull (aVPL1, "ValueProviderList1");
    ValueEnforcer.notNull (aVPL2, "ValueProviderList2");

    // find latest last modification
    final LocalDate aLastMod = aVPL1.getLastModification ()
                                    .isAfter (aVPL2.getLastModification ()) ? aVPL1.getLastModification ()
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

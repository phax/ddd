/*
 * Copyright (C) 2023-2026 Philip Helger (www.helger.com)
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
package com.helger.ddd.supplementary.tools;

import java.util.Comparator;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.numeric.mutable.MutableInt;
import com.helger.base.string.StringImplode;
import com.helger.collection.commons.ICommonsList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.DDDValueProviderPerSyntax.ISelectorCallback;
import com.helger.ddd.model.EDDDDeterminedField;
import com.helger.ddd.model.VPSourceValue;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.rules.all.PhiveRulesValidation;
import com.helger.phive.xml.source.IValidationSourceXML;

/**
 * Check that the determined VESIDs are correct and can be validated
 *
 * @author Philip Helger
 */
public final class DDDConsistencyFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DDDConsistencyFuncTest.class);

  @Test
  public void testIfAllVESIDsAreCorrect ()
  {
    final MutableInt aChecks = new MutableInt (0);
    final ValidationExecutorSetRegistry <IValidationSourceXML> aVesRegistry = new ValidationExecutorSetRegistry <> ();
    PhiveRulesValidation.initPhiveRules (aVesRegistry);

    // For each value provider of each syntax
    final DDDValueProviderList aVPL = DDDValueProviderList.getDefaultValueProviderList ();
    for (final var e1 : aVPL.valueProvidersPerSyntaxes ().getSortedByKey (Comparator.naturalOrder ()).entrySet ())
    {
      LOGGER.info ("Syntax " + e1.getKey ());

      // Check each VESID selector
      e1.getValue ().forEachSelector (new ISelectorCallback ()
      {
        public void acceptFlag (final ICommonsList <VPSourceValue> aSourceValues, final String sFlag)
        {}

        public void acceptDeterminedValue (@NonNull @Nonempty final ICommonsList <VPSourceValue> aSourceValues,
                                           @NonNull final EDDDDeterminedField eDeterminedField,
                                           @NonNull final String sDeterminedValue)
        {
          // We only care about the VESID
          if (eDeterminedField == EDDDDeterminedField.VESID)
          {
            aChecks.inc ();

            final String sSrcString = StringImplode.imploder ()
                                                   .source (aSourceValues,
                                                            x -> '[' +
                                                                 x.getSourceField ().name () +
                                                                 '=' +
                                                                 x.getSourceValue () +
                                                                 ']')
                                                   .separator ("; ")
                                                   .build ();
            final String sVESID = sDeterminedValue;
            LOGGER.info ("  " + sSrcString + " -- " + sVESID);
            if (aVesRegistry.getOfID (DVRCoordinate.parseOrNull (sVESID)) == null)
              throw new IllegalStateException ("VES ID '" + sVESID + "' is unknown");
          }
        }
      });
    }
    LOGGER.info (aChecks.intValue () + " checks done");
  }
}

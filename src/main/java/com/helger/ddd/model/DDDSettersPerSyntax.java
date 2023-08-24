package com.helger.ddd.model;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.string.StringHelper;
import com.helger.xml.microdom.IMicroElement;

public class DDDSettersPerSyntax
{
  private final String m_sSyntaxID;
  private final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> m_aSelectors;

  public DDDSettersPerSyntax (@Nonnull @Nonempty final String sSyntaxID,
                              @Nonnull final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> aSelectors)
  {
    ValueEnforcer.notEmpty (sSyntaxID, "SyntaxID");
    ValueEnforcer.notEmpty (aSelectors, "Selectors");
    m_sSyntaxID = sSyntaxID;
    m_aSelectors = aSelectors;
  }

  @Nonnull
  @Nonempty
  public String getSyntaxID ()
  {
    return m_sSyntaxID;
  }

  @Nonnull
  public ICommonsMap <EDDDField, String> findAllMatches (@Nonnull final Function <EDDDField, String> fctFieldProvider)
  {
    final ICommonsMap <EDDDField, String> ret = new CommonsHashMap <> ();
    for (final Map.Entry <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> aEntry : m_aSelectors.entrySet ())
    {
      // Get the source value
      final String sSourceValue = fctFieldProvider.apply (aEntry.getKey ());
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

  @Nonnull
  public static DDDSettersPerSyntax readFromXML (@Nonnull final IMicroElement eVesid)
  {
    final String sSyntaxID = eVesid.getAttributeValue ("id");
    if (StringHelper.hasNoText (sSyntaxID))
      throw new IllegalStateException ("Failed to read a syntax ID");

    final ICommonsMap <EDDDField, ICommonsMap <String, ICommonsMap <EDDDField, String>>> aMap = new CommonsHashMap <> ();
    for (final IMicroElement eSelect : eVesid.getAllChildElements ("select"))
    {
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

    return new DDDSettersPerSyntax (sSyntaxID, aMap);
  }
}

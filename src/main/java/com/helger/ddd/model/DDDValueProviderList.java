package com.helger.ddd.model;

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

public class DDDValueProviderList
{
  public static final IReadableResource DEFAULT_VALUE_PROVIDER_LIST_RES = new ClassPathResource ("ddd/value-providers.xml",
                                                                                                 DDDValueProviderList.class.getClassLoader ());

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
  public LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  @Nonnull
  public ICommonsMap <String, DDDValueProviderPerSyntax> getAllValueProvidersPerSyntaxes ()
  {
    return m_aVPPerSyntaxes.getClone ();
  }

  @Nullable
  public DDDValueProviderPerSyntax getValueProviderPerSyntax (@Nullable final String sSyntaxID)
  {
    return m_aVPPerSyntaxes.get (sSyntaxID);
  }

  @Nonnull
  public static DDDValueProviderList read (@Nonnull final IReadableResource aRes)
  {
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
}

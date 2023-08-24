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

public class DDDSetterList
{
  public static final IReadableResource DEFAULT_VESID_LIST_RES = new ClassPathResource ("ddd/vesids.xml",
                                                                                        DDDSetterList.class.getClassLoader ());

  private final LocalDate m_aLastMod;
  private final ICommonsMap <String, DDDSettersPerSyntax> m_aSettersPerSyntaxes;

  public DDDSetterList (@Nonnull final LocalDate aLastMod,
                        @Nonnull final ICommonsMap <String, DDDSettersPerSyntax> aSyntaxes)
  {
    ValueEnforcer.notNull (aLastMod, "LastMod");
    ValueEnforcer.notNull (aSyntaxes, "Syntaxes");
    m_aLastMod = aLastMod;
    m_aSettersPerSyntaxes = aSyntaxes;
  }

  @Nonnull
  public LocalDate getLastModification ()
  {
    return m_aLastMod;
  }

  @Nonnull
  public ICommonsMap <String, DDDSettersPerSyntax> getAllSettersPerSyntaxes ()
  {
    return m_aSettersPerSyntaxes.getClone ();
  }

  @Nullable
  public DDDSettersPerSyntax getSettersPerSyntax (@Nullable final String sSyntaxID)
  {
    return m_aSettersPerSyntaxes.get (sSyntaxID);
  }

  @Nonnull
  public static DDDSetterList read (@Nonnull final IReadableResource aRes)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    if (aDoc == null)
      throw new IllegalArgumentException ("Failed to read DDD syntax list");

    final LocalDate aLastMod = aDoc.getDocumentElement ().getAttributeValueWithConversion ("lastmod", LocalDate.class);
    if (aLastMod == null)
      throw new IllegalArgumentException ("The DDD syntax list is missing a valid 'lastmod' attribute");

    final ICommonsMap <String, DDDSettersPerSyntax> aSyntaxes = new CommonsHashMap <> ();
    for (final IMicroElement eSyntax : aDoc.getDocumentElement ().getAllChildElements ("syntax"))
    {
      final DDDSettersPerSyntax aVesidPerSyntax = DDDSettersPerSyntax.readFromXML (eSyntax);
      if (aSyntaxes.containsKey (aVesidPerSyntax.getSyntaxID ()))
        throw new IllegalStateException ("Another DDD syntax with ID '" +
                                         aVesidPerSyntax.getSyntaxID () +
                                         "' is already contained");

      aSyntaxes.put (aVesidPerSyntax.getSyntaxID (), aVesidPerSyntax);
    }
    return new DDDSetterList (aLastMod, aSyntaxes);
  }
}

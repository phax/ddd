package com.helger.ddd.model;

import java.util.Iterator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

public class VPDeterminedFlags implements ICloneable <VPDeterminedFlags>, Iterable <String>
{
  private final ICommonsOrderedSet <String> m_aSet;

  /**
   * Public no-argument constructor.
   */
  public VPDeterminedFlags ()
  {
    m_aSet = new CommonsLinkedHashSet <> ();
  }

  /**
   * Private copy constructor for cloning
   *
   * @param aSet
   *        The set to be copied. May not be <code>null</code>.
   */
  private VPDeterminedFlags (@Nonnull final ICommonsOrderedSet <String> aSet)
  {
    m_aSet = aSet.getClone ();
  }

  public boolean contains (@Nullable final String sFlag)
  {
    return sFlag != null && m_aSet.contains (sFlag);
  }

  @Nonnull
  public EChange add (@Nullable final String sFlag)
  {
    ValueEnforcer.notEmpty (sFlag, "Flag");
    return EChange.valueOf (m_aSet.add (sFlag));
  }

  @Nonnull
  public EChange addAll (@Nullable final VPDeterminedFlags aFlags)
  {
    ValueEnforcer.notNull (aFlags, "Flags");
    return EChange.valueOf (m_aSet.addAll (aFlags.m_aSet));
  }

  @Nonnull
  public Iterator <String> iterator ()
  {
    return m_aSet.iterator ();
  }

  public boolean isNotEmpty ()
  {
    return m_aSet.isNotEmpty ();
  }

  @Nonnegative
  public int getCount ()
  {
    return m_aSet.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAsSet ()
  {
    return m_aSet.getClone ();
  }

  @Nonnull
  public VPDeterminedFlags getClone ()
  {
    return new VPDeterminedFlags (m_aSet);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Set", m_aSet).getToString ();
  }
}

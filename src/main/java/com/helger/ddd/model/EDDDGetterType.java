package com.helger.ddd.model;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.state.EMandatory;
import com.helger.commons.state.IMandatoryIndicator;

/**
 * Defines the supported getters for {@link DDDSyntax}
 *
 * @author Philip Helger
 */
public enum EDDDGetterType implements IHasID <String>, IMandatoryIndicator
{
  CUSTOMIZATION_ID ("CustomizationID", EMandatory.MANDATORY),
  PROCESS_ID ("ProcessID", EMandatory.OPTIONAL),
  SENDER_ID_SCHEME ("SenderIDScheme", EMandatory.MANDATORY),
  SENDER_ID_VALUE ("SenderIDValue", EMandatory.MANDATORY),
  RECEIVER_ID_SCHEME ("ReceiverIDScheme", EMandatory.MANDATORY),
  RECEIVER_ID_VALUE ("ReceiverIDValue", EMandatory.MANDATORY),
  BUSINESS_DOCUMENT_ID ("BusinessDocumentID", EMandatory.MANDATORY),
  SENDER_NAME ("SenderName", EMandatory.MANDATORY),
  RECEIVER_NAME ("ReceiverName", EMandatory.MANDATORY);

  private final String m_sID;
  private final EMandatory m_eMandatory;

  EDDDGetterType (@Nonnull @Nonempty final String sID, @Nonnull final EMandatory eMandatory)
  {
    m_sID = sID;
    m_eMandatory = eMandatory;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public boolean isMandatory ()
  {
    return m_eMandatory.isMandatory ();
  }
}

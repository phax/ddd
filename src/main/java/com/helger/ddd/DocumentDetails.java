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
package com.helger.ddd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;

/**
 * Document details determined from the payload, with all fields optional.
 *
 * @author Philip Helger
 */
public class DocumentDetails
{
  private final IParticipantIdentifier m_aSenderID;
  private final IParticipantIdentifier m_aReceiverID;
  private final IDocumentTypeIdentifier m_aDocTypeID;
  private final IProcessIdentifier m_aProcessID;
  private final String m_sVESID;
  private final String m_sBusinessDocumentID;
  private final String m_sSenderName;
  private final String m_sReceiverName;

  public DocumentDetails (@Nullable final IParticipantIdentifier aSenderID,
                          @Nullable final IParticipantIdentifier aReceiverID,
                          @Nullable final IDocumentTypeIdentifier aDocTypeID,
                          @Nullable final IProcessIdentifier aProcessID,
                          @Nullable final String sVESID,
                          @Nullable final String sBusinessDocumentID,
                          @Nullable final String sSenderName,
                          @Nullable final String sReceiverName)
  {
    m_aSenderID = aSenderID;
    m_aReceiverID = aReceiverID;
    m_aDocTypeID = aDocTypeID;
    m_aProcessID = aProcessID;
    m_sVESID = sVESID;
    m_sBusinessDocumentID = sBusinessDocumentID;
    m_sSenderName = sSenderName;
    m_sReceiverName = sReceiverName;
  }

  @Nullable
  public final IParticipantIdentifier getSenderID ()
  {
    return m_aSenderID;
  }

  @Nullable
  public final IParticipantIdentifier getReceiverID ()
  {
    return m_aReceiverID;
  }

  @Nullable
  public final IDocumentTypeIdentifier getDocumentTypeID ()
  {
    return m_aDocTypeID;
  }

  @Nullable
  public final IProcessIdentifier getProcessID ()
  {
    return m_aProcessID;
  }

  @Nullable
  public final String getVESID ()
  {
    return m_sVESID;
  }

  public final boolean areAllKeyFieldsPresent ()
  {
    return m_aSenderID != null &&
           m_aReceiverID != null &&
           m_aDocTypeID != null &&
           m_aProcessID != null &&
           m_sVESID != null;
  }

  @Nullable
  public final String getBusinessDocumentID ()
  {
    return m_sBusinessDocumentID;
  }

  @Nullable
  public final String getSenderName ()
  {
    return m_sSenderName;
  }

  @Nullable
  public final String getReceiverName ()
  {
    return m_sReceiverName;
  }

  @Nonnull
  public final IJsonObject getAsJson ()
  {
    final IJsonObject ret = new JsonObject ();
    ret.add ("sender", m_aSenderID == null ? null : m_aSenderID.getURIEncoded ());
    ret.add ("receiver", m_aReceiverID == null ? null : m_aReceiverID.getURIEncoded ());
    ret.add ("doctype", m_aDocTypeID == null ? null : m_aDocTypeID.getURIEncoded ());
    ret.add ("process", m_aProcessID == null ? null : m_aProcessID.getURIEncoded ());
    ret.add ("vesid", m_sVESID);
    ret.add ("bdid", m_sBusinessDocumentID);
    ret.add ("sendername", m_sSenderName);
    ret.add ("receivername", m_sReceiverName);
    return ret;
  }
}

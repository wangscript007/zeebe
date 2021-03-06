/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.engine.state.mutable;

import io.zeebe.engine.state.immutable.MessageState;
import io.zeebe.engine.state.message.Message;
import org.agrona.DirectBuffer;

public interface MutableMessageState extends MessageState {

  void put(Message message);

  void putMessageCorrelation(long messageKey, DirectBuffer bpmnProcessId);

  void removeMessageCorrelation(long messageKey, DirectBuffer bpmnProcessId);

  void putActiveWorkflowInstance(DirectBuffer bpmnProcessId, DirectBuffer correlationKey);

  void removeActiveWorkflowInstance(DirectBuffer bpmnProcessId, DirectBuffer correlationKey);

  void putWorkflowInstanceCorrelationKey(long workflowInstanceKey, DirectBuffer correlationKey);

  void removeWorkflowInstanceCorrelationKey(long workflowInstanceKey);

  void remove(long key);
}

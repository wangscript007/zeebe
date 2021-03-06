/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.test.util.bpmn.random;

import io.zeebe.model.bpmn.Bpmn;
import io.zeebe.model.bpmn.BpmnModelInstance;
import io.zeebe.model.bpmn.builder.AbstractFlowNodeBuilder;
import io.zeebe.test.util.bpmn.random.blocks.BlockSequenceBuilder.BlockSequenceBuilderFactory;
import java.io.File;
import java.util.Optional;
import java.util.Random;

/** Class to generate random workflows and execution paths for those workflows */
public final class RandomWorkflowGenerator {

  private static final BlockSequenceBuilderFactory FACTORY = new BlockSequenceBuilderFactory();

  private final BlockBuilder blockBuilder;

  private final String startEventId;
  private final String endEventId;
  private final String processId;

  /**
   * Creates the random workflow generator
   *
   * @param seed seed for random noise generator
   * @param maxBlocks maximum number of blocks in a sequence (defaults to {@code 5})
   * @param maxDepth maximum level of depth for nested elements (defaults to {@code 3})
   * @param maxBranches maximum number of outgoing branches for a forking node (defaults to {@code
   *     3})
   */
  public RandomWorkflowGenerator(
      final long seed, final Integer maxBlocks, final Integer maxDepth, final Integer maxBranches) {
    final Random random = new Random(seed);

    final IDGenerator idGenerator = new IDGenerator(0);

    processId = "process_" + idGenerator.nextId();

    startEventId = idGenerator.nextId();
    endEventId = idGenerator.nextId();

    final ConstructionContext context =
        new ConstructionContext(
            random,
            idGenerator,
            FACTORY,
            Optional.ofNullable(maxBlocks).orElse(5),
            Optional.ofNullable(maxDepth).orElse(3),
            Optional.ofNullable(maxBranches).orElse(3),
            0);
    blockBuilder = FACTORY.createBlockSequenceBuilder(context);
  }

  public BpmnModelInstance buildWorkflow() {

    AbstractFlowNodeBuilder<?, ?> workflowWorkInProgress =
        Bpmn.createExecutableProcess(processId).startEvent(startEventId);

    workflowWorkInProgress = blockBuilder.buildFlowNodes(workflowWorkInProgress);

    return workflowWorkInProgress.endEvent(endEventId).done();
  }

  public ExecutionPath findRandomExecutionPath(final long seed) {
    return new ExecutionPath(blockBuilder.findRandomExecutionPath(new Random(seed)));
  }

  // main method to test and debug this class
  public static void main(final String[] args) {
    final Random random = new Random();

    for (int i = 0; i < 100; i++) {
      System.out.println("Generating process " + i);

      final String id = "process" + i;

      final RandomWorkflowGenerator builder =
          new RandomWorkflowGenerator(random.nextLong(), 5, 3, 3);

      Bpmn.writeModelToFile(new File(id + ".bpmn"), builder.buildWorkflow());

      for (int p = 0; p < 10; p++) {
        final ExecutionPath path = builder.findRandomExecutionPath(random.nextLong());

        System.out.println("Execution path " + p + " :" + path);
      }
    }
  }
}

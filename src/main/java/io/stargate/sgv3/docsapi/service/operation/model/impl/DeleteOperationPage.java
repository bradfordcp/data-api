package io.stargate.sgv3.docsapi.service.operation.model.impl;

import io.stargate.sgv3.docsapi.api.model.command.CommandResult;
import io.stargate.sgv3.docsapi.api.model.command.CommandStatus;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This represents the response for a delete operation. .
 *
 * @param deletedIds - document ids deleted
 */
public record DeleteOperationPage(List<String> deletedIds) implements Supplier<CommandResult> {
  @Override
  public CommandResult get() {
    return new CommandResult(Map.of(CommandStatus.DELETED_IDS, deletedIds));
  }
}
package io.stargate.sgv3.docsapi.service.operation.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.stargate.sgv3.docsapi.api.model.command.CommandResult;
import io.stargate.sgv3.docsapi.api.model.command.CommandStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record UpdateOperationPage(
    List<ReadAndUpdateOperation.UpdatedDocument> updatedDocuments, boolean returnDocs)
    implements Supplier<CommandResult> {
  @Override
  public CommandResult get() {
    List<String> updatedIds = new ArrayList<>(updatedDocuments().size());
    List<JsonNode> updatedDocs = new ArrayList<>(updatedDocuments().size());
    updatedDocuments.forEach(
        update -> {
          updatedIds.add(update.id());
          updatedDocs.add(update.document());
        });
    if (returnDocs) {
      return new CommandResult(
          new CommandResult.ResponseData(updatedDocs),
          Map.of(CommandStatus.UPDATED_IDS, updatedIds));
    } else {
      return new CommandResult(Map.of(CommandStatus.UPDATED_IDS, updatedIds));
    }
  }
}
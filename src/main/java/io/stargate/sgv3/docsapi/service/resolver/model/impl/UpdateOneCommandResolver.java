package io.stargate.sgv3.docsapi.service.resolver.model.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stargate.sgv3.docsapi.api.model.command.CommandContext;
import io.stargate.sgv3.docsapi.api.model.command.impl.UpdateOneCommand;
import io.stargate.sgv3.docsapi.service.operation.model.Operation;
import io.stargate.sgv3.docsapi.service.operation.model.ReadOperation;
import io.stargate.sgv3.docsapi.service.operation.model.impl.ReadAndUpdateOperation;
import io.stargate.sgv3.docsapi.service.resolver.model.CommandResolver;
import io.stargate.sgv3.docsapi.service.resolver.model.impl.matcher.FilterableResolver;
import io.stargate.sgv3.docsapi.service.shredding.Shredder;
import io.stargate.sgv3.docsapi.service.updater.DocumentUpdater;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/** Resolves the {@link UpdateOneCommand } */
@ApplicationScoped
public class UpdateOneCommandResolver extends FilterableResolver<UpdateOneCommand>
    implements CommandResolver<UpdateOneCommand> {
  private Shredder shredder;

  @Inject
  public UpdateOneCommandResolver(ObjectMapper objectMapper, Shredder shredder) {
    super(objectMapper, true, true);
    this.shredder = shredder;
  }

  @Override
  public Class<UpdateOneCommand> getCommandClass() {
    return UpdateOneCommand.class;
  }

  @Override
  public Operation resolveCommand(CommandContext ctx, UpdateOneCommand command) {
    ReadOperation readOperation = resolve(ctx, command);
    DocumentUpdater documentUpdater = new DocumentUpdater(command.updateClause());
    return new ReadAndUpdateOperation(ctx, readOperation, documentUpdater, false, shredder);
  }

  @Override
  protected FilteringOptions getFilteringOption(UpdateOneCommand command) {
    return new FilteringOptions(1, null, 1);
  }
}
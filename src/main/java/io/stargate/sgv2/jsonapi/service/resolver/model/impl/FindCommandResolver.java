package io.stargate.sgv2.jsonapi.service.resolver.model.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stargate.sgv2.jsonapi.api.model.command.CommandContext;
import io.stargate.sgv2.jsonapi.api.model.command.clause.sort.SortClause;
import io.stargate.sgv2.jsonapi.api.model.command.impl.FindCommand;
import io.stargate.sgv2.jsonapi.api.model.command.impl.FindOneCommand;
import io.stargate.sgv2.jsonapi.config.OperationsConfig;
import io.stargate.sgv2.jsonapi.service.operation.model.Operation;
import io.stargate.sgv2.jsonapi.service.operation.model.ReadType;
import io.stargate.sgv2.jsonapi.service.operation.model.impl.DBFilterBase;
import io.stargate.sgv2.jsonapi.service.operation.model.impl.FindOperation;
import io.stargate.sgv2.jsonapi.service.resolver.model.CommandResolver;
import io.stargate.sgv2.jsonapi.service.resolver.model.impl.matcher.FilterableResolver;
import io.stargate.sgv2.jsonapi.util.SortClauseUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/** Resolves the {@link FindOneCommand } */
@ApplicationScoped
public class FindCommandResolver extends FilterableResolver<FindCommand>
    implements CommandResolver<FindCommand> {

  private final OperationsConfig operationsConfig;
  private final ObjectMapper objectMapper;

  @Inject
  public FindCommandResolver(OperationsConfig operationsConfig, ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
    this.operationsConfig = operationsConfig;
  }

  @Override
  public Class<FindCommand> getCommandClass() {
    return FindCommand.class;
  }

  @Override
  public Operation resolveCommand(CommandContext commandContext, FindCommand command) {
    List<DBFilterBase> filters = resolve(commandContext, command);

    // limit and paging state defaults
    int limit = Integer.MAX_VALUE;
    int skip = 0;
    String pagingState = null;

    // update if options provided
    FindCommand.Options options = command.options();
    if (options != null) {
      if (null != options.limit()) {
        limit = options.limit();
      }
      if (null != options.skip()) {
        skip = options.skip();
      }
      pagingState = options.pagingState();
    }

    // resolve sort clause
    SortClause sortClause = command.sortClause();
    List<FindOperation.OrderBy> orderBy = SortClauseUtil.resolveOrderBy(sortClause);

    // if orderBy present
    if (orderBy != null) {
      return FindOperation.sorted(
          commandContext,
          filters,
          command.buildProjector(),
          pagingState,
          // For in memory sorting if no limit provided in the request will use
          // documentConfig.defaultPageSize() as limit
          Math.min(limit, operationsConfig.defaultPageSize()),
          // For in memory sorting we read more data than needed, so defaultSortPageSize like 100
          operationsConfig.defaultSortPageSize(),
          ReadType.SORTED_DOCUMENT,
          objectMapper,
          orderBy,
          skip,
          operationsConfig.maxDocumentSortCount());
    } else {
      return FindOperation.unsorted(
          commandContext,
          filters,
          command.buildProjector(),
          pagingState,
          limit,
          operationsConfig.defaultPageSize(),
          ReadType.DOCUMENT,
          objectMapper);
    }
  }
}

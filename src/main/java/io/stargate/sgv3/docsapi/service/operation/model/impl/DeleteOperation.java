package io.stargate.sgv3.docsapi.service.operation.model.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.stargate.bridge.grpc.Values;
import io.stargate.bridge.proto.QueryOuterClass;
import io.stargate.sgv3.docsapi.api.model.command.CommandContext;
import io.stargate.sgv3.docsapi.api.model.command.CommandResult;
import io.stargate.sgv3.docsapi.service.bridge.executor.QueryExecutor;
import io.stargate.sgv3.docsapi.service.operation.model.ModifyOperation;
import io.stargate.sgv3.docsapi.service.operation.model.ReadOperation;
import io.stargate.sgv3.docsapi.service.operation.model.ReadOperation.FindResponse;
import java.util.List;
import java.util.function.Supplier;

/**
 * Executes readOperation to get the documents ids based on filter condition. All the ids are
 * deleted as LWT based on the id and tx_id.
 */
public record DeleteOperation(CommandContext commandContext, ReadOperation readOperation)
    implements ModifyOperation {
  @Override
  public Uni<Supplier<CommandResult>> execute(QueryExecutor queryExecutor) {
    Uni<FindResponse> docsToDelete = readOperation().getDocuments(queryExecutor);
    final QueryOuterClass.Query delete = buildDeleteQuery();
    final Uni<List<String>> ids =
        docsToDelete
            .onItem()
            .transformToMulti(
                findResponse -> Multi.createFrom().items(findResponse.docs().stream()))
            .onItem()
            .transformToUniAndConcatenate(
                readDocument -> deleteDocument(queryExecutor, delete, readDocument))
            .collect()
            .asList();
    return ids.onItem().transform(DeleteOperationPage::new);
  }

  private QueryOuterClass.Query buildDeleteQuery() {
    String delete = "DELETE FROM \"%s\".\"%s\" WHERE key = ? IF tx_id = ?";
    return QueryOuterClass.Query.newBuilder()
        .setCql(String.format(delete, commandContext.database(), commandContext.collection()))
        .build();
  }

  /**
   * When delete is run with LWT, applied field is always the first field and in case the
   * transaction id mismatch the latest transaction id is returned as second field Eg:
   * cassandra@cqlsh:docsapi> delete from docsapi.test1 where key = 'doc2' IF tx_id =
   * 13659a90-9361-11ed-92df-515ba7f99655 ;
   *
   * <p>[applied] | tx_id -----------+-------------------------------------- False |
   * 13659a90-9361-11ed-92df-515ba7f99654
   *
   * <p>cassandra@cqlsh:docsapi> delete from docsapi.test1 where key = 'doc2' IF tx_id =
   * 13659a90-9361-11ed-92df-515ba7f99654 ;
   *
   * <p>[applied] ----------- True
   *
   * @param queryExecutor
   * @param query
   * @param doc
   * @return
   */
  private static Uni<String> deleteDocument(
      QueryExecutor queryExecutor, QueryOuterClass.Query query, ReadDocument doc) {
    query = bindDeleteQuery(query, doc);
    return queryExecutor
        .executeWrite(query)
        .onItem()
        .transformToUni(
            result -> {
              if (result.getRows(0).getValues(0).getBoolean()) {
                return Uni.createFrom().item(doc.id());
              } else {
                return Uni.createFrom().nothing();
              }
            });
  }

  private static QueryOuterClass.Query bindDeleteQuery(
      QueryOuterClass.Query builtQuery, ReadDocument doc) {
    QueryOuterClass.Values.Builder values =
        QueryOuterClass.Values.newBuilder()
            .addValues(Values.of(doc.id()))
            .addValues(Values.of(doc.txnId()));
    return QueryOuterClass.Query.newBuilder(builtQuery).setValues(values).build();
  }
}
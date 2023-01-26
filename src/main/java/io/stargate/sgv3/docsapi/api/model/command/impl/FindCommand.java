package io.stargate.sgv3.docsapi.api.model.command.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.stargate.sgv3.docsapi.api.model.command.Filterable;
import io.stargate.sgv3.docsapi.api.model.command.ReadCommand;
import io.stargate.sgv3.docsapi.api.model.command.clause.filter.FilterClause;
import io.stargate.sgv3.docsapi.api.model.command.clause.sort.SortClause;
import javax.annotation.Nullable;
import javax.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Command that finds a single JSON document from a collection.")
@JsonTypeName("find")
public record FindCommand(
    @Valid @JsonProperty("filter") FilterClause filterClause,
    @Valid @JsonProperty("sort") SortClause sortClause,
    @Valid @Nullable @JsonProperty("options") Options options)
    implements ReadCommand, Filterable {

  public record Options(
      @Valid
          @Schema(
              description = "Maximum number of document that can be fetched for the command.",
              type = SchemaType.INTEGER,
              implementation = Integer.class)
          Integer limit,
      @Valid
          @Schema(
              description = "Next page state for pagination.",
              type = SchemaType.STRING,
              implementation = String.class)
          String pagingState,
      @Valid
          @Schema(
              description = "Number of document needed per page.",
              type = SchemaType.INTEGER,
              implementation = Integer.class)
          Integer pageSize) {}
}
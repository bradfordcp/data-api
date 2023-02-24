package io.stargate.sgv2.jsonapi.api.model.command.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.stargate.sgv2.jsonapi.api.model.command.Command;
import io.stargate.sgv2.jsonapi.api.model.command.Filterable;
import io.stargate.sgv2.jsonapi.api.model.command.ModifyCommand;
import io.stargate.sgv2.jsonapi.api.model.command.clause.filter.FilterClause;
import javax.annotation.Nullable;
import javax.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Representation of the deleteMany API {@link Command}.
 *
 * @param filterClause {@link FilterClause} used to identify the document.
 */
@Schema(
    description =
        "Command that finds documents based on the filter and deletes it from a collection")
@JsonTypeName("deleteMany")
public record DeleteManyCommand(
    @Schema(
            description = "Filter clause based on which document is identified",
            implementation = FilterClause.class)
        @Valid
        @JsonProperty("filter")
        FilterClause filterClause,
    @Nullable Options options)
    implements ModifyCommand, Filterable {
  public record Options() {}
}
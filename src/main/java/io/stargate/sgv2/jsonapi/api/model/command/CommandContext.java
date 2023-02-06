package io.stargate.sgv2.jsonapi.api.model.command;

/**
 * Defines the context in which to execute the command.
 *
 * @param database The name of the database.
 * @param collection The name of the collection.
 */
public record CommandContext(String database, String collection) {}
package io.stargate.sgv2.jsonapi.api.model.command.clause.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Implementation of {@code $set} update operation used to assign values to document fields. */
public class SetOperation extends UpdateOperation {
  private final List<SetAction> additions;

  private SetOperation(List<SetAction> additions) {
    this.additions = additions;
  }

  public static SetOperation construct(ObjectNode args) {
    List<SetAction> additions = new ArrayList<>();
    var it = args.fields();
    while (it.hasNext()) {
      var entry = it.next();
      String path = validateSetPath(UpdateOperator.SET, entry.getKey());
      additions.add(new SetAction(path, entry.getValue()));
    }
    return new SetOperation(additions);
  }

  @Override
  public boolean updateDocument(ObjectNode doc) {
    boolean modified = false;
    for (SetAction addition : additions) {
      JsonNode prev = doc.replace(addition.path, addition.value);
      modified |= !Objects.equals(prev, addition.value);
    }
    return modified;
  }

  public Set<String> paths() {
    return additions.stream().map(SetAction::path).collect(Collectors.toSet());
  }

  // Just needed for tests
  @Override
  public boolean equals(Object o) {
    return (o instanceof SetOperation)
        && Objects.equals(this.additions, ((SetOperation) o).additions);
  }

  private record SetAction(String path, JsonNode value) {}
}

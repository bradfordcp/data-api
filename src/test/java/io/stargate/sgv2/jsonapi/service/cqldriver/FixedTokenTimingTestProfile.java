package io.stargate.sgv2.jsonapi.service.cqldriver;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

public class FixedTokenTimingTestProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return ImmutableMap.<String, String>builder()
        .put("stargate.jsonapi.operations.database-config.fixed-token", "test-token")
        .put("stargate.jsonapi.operations.database-config.session-cache-ttl-seconds", "10")
        .build();
  }
}

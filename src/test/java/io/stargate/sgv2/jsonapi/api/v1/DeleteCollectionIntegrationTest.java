package io.stargate.sgv2.jsonapi.api.v1;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.stargate.sgv2.jsonapi.testresource.DseTestResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

@QuarkusIntegrationTest
@QuarkusTestResource(DseTestResource.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class DeleteCollectionIntegrationTest extends AbstractNamespaceIntegrationTestBase {

  @Nested
  @Order(1)
  class DeleteCollection {

    @Test
    public void happyPath() {
      String collection = RandomStringUtils.randomAlphabetic(16);

      // first create
      String createJson =
          """
          {
            "createCollection": {
              "name": "%s"
            }
          }
          """
              .formatted(collection);

      given()
          .headers(getHeaders())
          .contentType(ContentType.JSON)
          .body(createJson)
          .when()
          .post(NamespaceResource.BASE_PATH, namespaceName)
          .then()
          .statusCode(200)
          .body("status.ok", is(1));

      // then delete
      String json =
          """
          {
            "deleteCollection": {
              "name": "%s"
            }
          }
          """
              .formatted(collection);

      given()
          .headers(getHeaders())
          .contentType(ContentType.JSON)
          .body(json)
          .when()
          .post(NamespaceResource.BASE_PATH, namespaceName)
          .then()
          .statusCode(200)
          .body("status.ok", is(1));
    }

    @Test
    public void notExisting() {
      String collection = RandomStringUtils.randomAlphabetic(16);

      // delete not existing
      String json =
          """
          {
            "deleteCollection": {
              "name": "%s"
            }
          }
          """
              .formatted(collection);

      given()
          .headers(getHeaders())
          .contentType(ContentType.JSON)
          .body(json)
          .when()
          .post(NamespaceResource.BASE_PATH, namespaceName)
          .then()
          .statusCode(200)
          .body("status.ok", is(1));
    }

    @Test
    public void invalidCommand() {
      String json =
          """
          {
            "deleteCollection": {
            }
          }
          """;

      given()
          .headers(getHeaders())
          .contentType(ContentType.JSON)
          .body(json)
          .when()
          .post(NamespaceResource.BASE_PATH, namespaceName)
          .then()
          .statusCode(200)
          .body("errors[0].errorCode", is("COMMAND_FIELD_INVALID"))
          .body("errors[0].exceptionClass", is("JsonApiException"))
          .body(
              "errors[0].message",
              is(
                  "Request invalid: field 'command.name' value `null` not valid. Problem: must not be null."));
    }
  }

  @Nested
  @Order(2)
  class Metrics {
    @Test
    public void checkMetrics() {
      DeleteCollectionIntegrationTest.super.checkMetrics("DeleteCollectionCommand");
      DeleteCollectionIntegrationTest.super.checkDriverMetricsTenantId();
    }
  }
}

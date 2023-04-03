package io.stargate.sgv2.jsonapi.api.model.command.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.stargate.sgv2.jsonapi.config.DocumentLimitsConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Limits the maximum amount of documents to insert, as defined in the {@link DocumentLimitsConfig}.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {MaxInsertManyDocumentsValidation.class})
public @interface MaxInsertManyDocuments {

  String message() default "amount of documents to insert is over the max limit";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
package io.stargate.sgv2.jsonapi.api.model.command.clause.filter;

import io.stargate.sgv2.jsonapi.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;

/** This interface will be implemented by Operator enum. */
public interface FilterOperator {
  String getOperator();

  /**
   * Flip comparison operator when `$not` is pushed down
   *
   * @return
   */
  FilterOperator flip();

  class FilterOperatorUtils {
    private static Map<String, FilterOperator> operatorMap = new HashMap<>();

    static {
      for (FilterOperator filterOperator : ValueComparisonOperator.values()) {
        addComparisonOperator(filterOperator);
      }
      for (FilterOperator filterOperator : ElementComparisonOperator.values()) {
        addComparisonOperator(filterOperator);
      }
      for (FilterOperator filterOperator : ArrayComparisonOperator.values()) {
        addComparisonOperator(filterOperator);
      }
      // This should not be supported from outside
      operatorMap.remove(ArrayComparisonOperator.NOTANY.getOperator());
    }

    private static void addComparisonOperator(FilterOperator filterOperator) {
      operatorMap.put(filterOperator.getOperator(), filterOperator);
    }

    public static FilterOperator findComparisonOperator(String operator) {
      return operatorMap.get(operator);
    }

    @Deprecated // since 1.0.3 use "findComparisonOperator()" instead
    public static FilterOperator getComparisonOperator(String operator) {
      final FilterOperator filterOperator = findComparisonOperator(operator);
      if (filterOperator == null) {
        throw ErrorCode.UNSUPPORTED_FILTER_OPERATION.toApiException(operator);
      }
      return filterOperator;
    }
  }
}

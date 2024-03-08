package org.springframework.ai.vectorstore.filter.converter;

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionConverter;

public class MongoDBAtlasFilterExpressionConverter extends AbstractFilterExpressionConverter {

    @Override
    protected void doExpression(Filter.Expression expression, StringBuilder context) {

    }

    private String getOperationSymbol(Filter.Expression exp) {
        switch (exp.type()) {
            case AND:
                return "$and";
            case OR:
                return "$or";
            case EQ:
                return "$eq";
            case NE:
                return "$ne";
            case LT:
                return "$lt";
            case LTE:
                return "$lte";
            case GT:
                return "$gt";
            case GTE:
                return "$gte";
            case IN:
                return "$in";
            case NIN:
                return "$nin ";
            default:
                throw new RuntimeException("Not supported expression type:" + exp.type());
        }
    }
    @Override
    protected void doKey(Filter.Key filterKey, StringBuilder context) {
        var identifier = (hasOuterQuotes(filterKey.key())) ? removeOuterQuotes(filterKey.key()) : filterKey.key();
        context.append("\"metadata." + identifier + "\"");
    }
}

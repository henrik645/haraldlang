package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.vm.Word;

public class BinaryOperators {
    public static final BinaryOperator plus = (left, right) -> new Word(left.value() + right.value());
    public static final BinaryOperator minus = (left, right) -> new Word(left.value() - right.value());
    public static final BinaryOperator greaterThan = (left, right) -> Word.ofBoolean(left.value() > right.value());
    public static final BinaryOperator greaterThanOrEqual = (left, right) -> Word.ofBoolean(left.value() >= right.value());
    public static final BinaryOperator lesserThan = (left, right) -> Word.ofBoolean(left.value() < right.value());
    public static final BinaryOperator lesserThanOrEqual = (left, right) -> Word.ofBoolean(left.value() <= right.value());
    public static final BinaryOperator equal = (left, right) -> Word.ofBoolean(left.value() == right.value());
    public static final BinaryOperator notEqual = (left, right) -> Word.ofBoolean(left.value() != right.value());
    
    public static BinaryOperator fromTokenType(TokenType tokenType) {
        switch (tokenType) {
            case PLUS -> {
                return plus;
            }
            case MINUS -> {
                return minus;
            }
            case EQUALS -> {
                return equal;
            }
            case NOT_EQUALS -> {
                return notEqual;
            }
            case GREATER_THAN -> {
                return greaterThan;
            }
            case GREATER_THAN_OR_EQUAL -> {
                return greaterThanOrEqual;
            }
            case LESSER_THAN -> {
                return lesserThan;
            }
            case LESSER_THAN_OR_EQUAL -> {
                return lesserThanOrEqual;
            }
            default -> throw new IllegalArgumentException("Unsupported token type for binary operator: " + tokenType);
        }
    }

    public static boolean tokenTypeIsOperator(TokenType type) {
        return switch (type) {
            case PLUS, MINUS, EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESSER_THAN, LESSER_THAN_OR_EQUAL -> true;
            default -> false;
        };
    }
}

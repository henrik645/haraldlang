package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.ast.types.HLBool;
import nu.henrikvester.haraldlang.ast.types.HLInt;
import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.vm.Word;

public class BinaryOperators {
    public static final BinaryOperator plus = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return new Word(left.value() + right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLInt();
        }

        @Override
        public String symbol() {
            return "+";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static final BinaryOperator minus = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return new Word(left.value() - right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLInt();
        }

        @Override
        public String symbol() {
            return "-";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static final BinaryOperator greaterThan = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return Word.ofBoolean(left.value() > right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLBool();
        }

        @Override
        public String symbol() {
            return ">";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static final BinaryOperator greaterThanOrEqual = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return Word.ofBoolean(left.value() >= right.value());
        }

        @Override
        public String symbol() {
            return ">=";
        }

        @Override
        public String toString() {
            return symbol();
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLBool();
        }
    };

    public static final BinaryOperator lesserThan = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return Word.ofBoolean(left.value() < right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLBool();
        }

        @Override
        public String symbol() {
            return "<";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static final BinaryOperator lesserThanOrEqual = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return Word.ofBoolean(left.value() <= right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLBool();
        }


        @Override
        public String symbol() {
            return "<=";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static final BinaryOperator equal = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return Word.ofBoolean(left.value() == right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLBool();
        }

        @Override
        public String symbol() {
            return "==";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static final BinaryOperator notEqual = new BinaryOperator() {
        @Override
        public Word apply(Word left, Word right) {
            return Word.ofBoolean(left.value() != right.value());
        }

        @Override
        public HLType leftType() {
            return new HLInt();
        }

        @Override
        public HLType rightType() {
            return new HLInt();
        }

        @Override
        public HLType resultType() {
            return new HLBool();
        }

        @Override
        public String symbol() {
            return "!=";
        }

        @Override
        public String toString() {
            return symbol();
        }
    };

    public static BinaryOperator fromTokenType(TokenType tokenType) {
        switch (tokenType) {
            case PLUS -> {
                return plus;
            }
            case MINUS -> {
                return minus;
            }
            case DOUBLE_EQUALS -> {
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
            case PLUS, MINUS, DOUBLE_EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESSER_THAN,
                 LESSER_THAN_OR_EQUAL -> true;
            default -> false;
        };
    }
}

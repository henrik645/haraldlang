package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface ExpressionVisitor<R> {
    R visitAddressOfExpression(AddressOfExpression expr);

    R visitLiteralExpression(LiteralExpression expr);

    R visitBinaryExpression(BinaryExpression expr) throws HaraldLangException;

    R visitVar(Var expr) throws HaraldLangException;
}

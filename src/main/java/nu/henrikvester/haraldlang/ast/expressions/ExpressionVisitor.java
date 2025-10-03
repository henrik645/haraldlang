package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public interface ExpressionVisitor<R> {
    R visitAddressOfExpression(AddressOfExpression expr);

    R visitLiteralExpression(LiteralExpression expr);

    R visitBinaryExpression(BinaryExpression expr) throws HaraldMachineException;

    R visitIdentifierExpression(IdentifierExpression expr) throws HaraldMachineException;
}

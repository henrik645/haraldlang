package nu.henrikvester.haraldlang.analysis;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.LiteralExpression;
import nu.henrikvester.haraldlang.ast.expressions.TypeUse;
import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.ast.statements.Assignment;
import nu.henrikvester.haraldlang.ast.statements.BlockStatement;
import nu.henrikvester.haraldlang.ast.statements.Declaration;
import nu.henrikvester.haraldlang.ast.statements.PrintStatement;
import nu.henrikvester.haraldlang.codegen.ir.VarSlot;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NameResolverTest {
    private NameResolver nr;

    private FunctionDefinition getFirst(String input) throws HaraldLangException {
        var ast = new Parser(input).parse();
        return ast.functions().getFirst();
    }

    @BeforeEach
    void setUp() {
        nr = new NameResolver();
    }

    private boolean varSlotsContain(List<VarSlot> slots, String name, int occurences) {
        var matches = slots.stream().filter(s -> s.name().equals(name)).toList();
        return occurences == matches.size();
    }

    @Test
    void nameResolver_handlesSimpleCase() throws HaraldLangException {
        var main = getFirst("""
                fun main() {
                    declare x = 5;
                }
                """);
        var b = nr.resolve(main);

        assertEquals(1, b.locals(main).size());
        assertTrue(varSlotsContain(b.locals(main), "x", 1));
    }

    @Test
    void nameResolver_canRedeclareLocal() throws HaraldLangException {
        var main = getFirst("""
                fun main(x) {
                    declare x = 5;
                }
                """);
        var b = nr.resolve(main);

        assertEquals(2, b.locals(main).size());
        assertTrue(varSlotsContain(b.locals(main), "x", 2));
    }

    @Test
    void nameResolver_letDoesNotRedeclare() throws HaraldLangException {
        var main = getFirst("""
                fun main(x) {
                    let x = 5;
                }
                """);
        var b = nr.resolve(main);

        assertEquals(1, b.locals(main).size());
        assertTrue(varSlotsContain(b.locals(main), "x", 1));
    }

    @Test
    void nameResolver_shadowingOnlyAffectsScope() throws HaraldLangException {
        var firstDeclaration = new Declaration(new TypeUse("int", SourceLocation.NONE), "x", new LiteralExpression(5, SourceLocation.NONE), SourceLocation.NONE);
        var secondDeclaration = new Declaration(new TypeUse("int", SourceLocation.NONE), "x", new LiteralExpression(10, SourceLocation.NONE), SourceLocation.NONE);

        var firstDeclarationUse = new Var("x", SourceLocation.NONE);
        var secondDeclarationUse = new Var("x", SourceLocation.NONE);

        var main = new FunctionDefinition("main", List.of(), new BlockStatement(List.of(
                firstDeclaration,
                new BlockStatement(List.of(
                        secondDeclaration,
                        new PrintStatement(secondDeclarationUse)
                ), SourceLocation.NONE),
                new PrintStatement(firstDeclarationUse)
        ), SourceLocation.NONE), SourceLocation.NONE);

        var b = nr.resolve(main);

        assertEquals(2, b.locals(main).size());
        assertTrue(varSlotsContain(b.locals(main), "x", 2));

        assertNotEquals(b.slot(firstDeclarationUse), b.slot(secondDeclarationUse));
        assertNotEquals(b.slot(firstDeclaration), b.slot(secondDeclaration));

        assertEquals(b.slot(firstDeclarationUse), b.slot(firstDeclaration));
        assertEquals(b.slot(secondDeclarationUse), b.slot(secondDeclaration));
    }

    @Test
    void nameResolver_letDoesNotIntroduceShadowing() throws HaraldLangException {
        var firstUse = new Var("x", SourceLocation.NONE);
        var secondUse = new Var("x", SourceLocation.NONE);

        var declaration = new Declaration(new TypeUse("int", SourceLocation.NONE), "x", new LiteralExpression(5, SourceLocation.NONE), SourceLocation.NONE);
        var reassignment = new Assignment(firstUse, new LiteralExpression(10, SourceLocation.NONE));

        var main = new FunctionDefinition("main", List.of(), new BlockStatement(List.of(
                declaration,
                new BlockStatement(List.of(
                        reassignment,
                        new PrintStatement(secondUse)
                ), SourceLocation.NONE),
                new PrintStatement(firstUse)
        ), SourceLocation.NONE), SourceLocation.NONE);

        var b = nr.resolve(main);

        assertEquals(1, b.locals(main).size());
        assertTrue(varSlotsContain(b.locals(main), "x", 1));

        assertEquals(b.slot(firstUse), b.slot(secondUse));

        assertEquals(b.slot(firstUse), b.slot(declaration));
        assertEquals(b.slot(secondUse), b.slot(declaration));
    }
}
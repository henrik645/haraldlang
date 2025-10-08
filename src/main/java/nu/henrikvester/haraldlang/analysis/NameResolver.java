package nu.henrikvester.haraldlang.analysis;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.lvalue.LValueVisitor;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.ast.types.BaseTypes;
import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.codegen.ir.VarSlot;
import nu.henrikvester.haraldlang.exceptions.CompilerException;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

import java.util.*;

public class NameResolver implements ExpressionVisitor<Void>, StatementVisitor<Void>, LValueVisitor<Void> {
    // Ephemeral -- during resolution, we pop and push scopes as we enter and exit blocks
    private final Deque<Map<String, VarSlot>> scopes = new ArrayDeque<>();
    private final Deque<Map<String, HLType>> typeScopes = new ArrayDeque<>();
    
    // Maps from **exact** identifier expressions to the variable slots they refer to
    private final IdentityHashMap<Var, VarSlot> use2slot = new IdentityHashMap<>();
    // Maps from **exact** declarations to the variable slots they introduce
    private final IdentityHashMap<Declaration, VarSlot> decl2slot = new IdentityHashMap<>();

    // Maps from a function definition to a list of var slots for the function's parameters and variables declared in the function body
    private final Map<FunctionDefinition, List<VarSlot>> function2locals = new HashMap<>();
    private int nextId = 0;

    public Bindings resolve(FunctionDefinition functionDefinition) throws HaraldLangException {
        enter();

        // Add base types to the type scope.
        assert this.typeScopes.peek() != null;
        this.typeScopes.peek().putAll(BaseTypes.getBaseTypes());
        
        for (var param : functionDefinition.parameters()) {
            newVarSlot(param);
        }
        functionDefinition.body().accept(this);
        var locals = new LinkedHashSet<>(decl2slot.values());
        function2locals.put(functionDefinition, List.copyOf(locals));
        exit();

        return new Bindings(use2slot, decl2slot, function2locals);
    }

    private void enter() {
        scopes.push(new HashMap<>());
        typeScopes.push(new HashMap<>());
    }

    private void exit() {
        scopes.pop();
        typeScopes.pop();
    }

    private VarSlot lookup(String identifier) {
        for (Map<String, VarSlot> scope : scopes) {
            if (scope.containsKey(identifier)) {
                return scope.get(identifier);
            }
        }
        return null;
    }

    private HLType lookupType(String identifier) {
        for (var typeScope : typeScopes) {
            if (typeScope.containsKey(identifier)) {
                return typeScope.get(identifier);
            }
        }
        return null;
    }

    private Map<String, VarSlot> currentScope() {
        Map<String, VarSlot> ret = scopes.peek();
        if (ret == null) {
            throw new IllegalStateException("No current scope");
        }
        return ret;
    }

    @Override
    public Void visitAddressOfExpression(AddressOfExpression expr) {
        throw new NotImplementedException();
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression expr) {
        // do nothing
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        expr.left().accept(this);
        expr.right().accept(this);
        return null;
    }

    @Override
    public Void visitVar(Var expr) throws CompilerException {
        // get the slot for this identifier **at the current scope**
        VarSlot slot = lookup(expr.identifier());
        if (slot == null) throw CompilerException.undeclaredVariable(expr);
        // store the mapping from this **exact expression** to the slot
        use2slot.put(expr, slot);
        return null;
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        enter();
        stmt.initial().accept(this);
        stmt.condition().accept(this);
        stmt.body().accept(this);
        stmt.update().accept(this); // make sure update comes after so we don't get away with declaring variables in the update
        exit();
        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement stmt) throws HaraldLangException {
        enter();
        for (var s : stmt.statements()) {
            s.accept(this);
        }
        exit();
        return null;
    }

    private void newVarSlot(Declaration declaration) throws CompilerException {
        var type = lookupType(declaration.type().typeName());
        if (type == null) {
            throw CompilerException.unknownType(declaration.type().typeName(), declaration.type().location());
        }
        // create a new var slot to represent this variable
        var slot = new VarSlot(nextId++, declaration.identifier(), type);
        // add it to the current scope so we can find it **in the current scope**
        currentScope().put(declaration.identifier(), slot);
        // add the mapping from this **exact declaration** to the slot so we can find it all the time
        decl2slot.put(declaration, slot);
    }

    @Override
    public Void visitDeclaration(Declaration declaration) throws HaraldLangException {
        var initializer = declaration.expression();
        if (initializer != null) {
            initializer.accept(this);
        }
        newVarSlot(declaration); // in the initializer, the variable is not in scope yet

        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldLangException {
        // no name resolution here, but we need to visit the value expression
        stmt.lvalue().accept(this);
        stmt.value().accept(this);
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldLangException {
        stmt.condition().accept(this);
        enter();
        stmt.thenBody().accept(this);
        exit();
        if (stmt.elseBody() != null) {
            enter();
            stmt.elseBody().accept(this);
            exit();
        }
        return null;
    }

    @Override
    public Void visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldLangException {
        stmt.expression().accept(this);
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement stmt) throws HaraldLangException {
        stmt.expr().accept(this);
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldLangException {
        stmt.condition().accept(this);
        enter();
        stmt.body().accept(this);
        exit();
        return null;
    }
}

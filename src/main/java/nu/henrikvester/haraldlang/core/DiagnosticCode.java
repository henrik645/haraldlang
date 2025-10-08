package nu.henrikvester.haraldlang.core;

import lombok.Getter;

public enum DiagnosticCode {

    SUSPICIOUS_FOR_LOOP_UPDATE_STATEMENT("the update statement in the for loop is an expression, and will never update the loop variable"),
    SUSPICIOUS_FOR_LOOP_INITIAL_STATEMENT("the initial statement in the for loop is an expression, and will not initialize the loop variable");

    @Getter
    private final String message;


    DiagnosticCode(String message) {
        this.message = message;
    }
}

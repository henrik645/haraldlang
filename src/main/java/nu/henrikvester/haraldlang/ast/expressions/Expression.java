package nu.henrikvester.haraldlang.ast.expressions;

import com.sun.java.accessibility.util.Translator;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.Environment;
import nu.henrikvester.haraldlang.vm.Word;

public interface Expression {
    Word evaluate(Environment env) throws HaraldMachineException;

    void lower(Translator tr);
}


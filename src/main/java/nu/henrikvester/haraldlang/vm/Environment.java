package nu.henrikvester.haraldlang.vm;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Word> variables = new HashMap<>();
    
    public Word get(String identifier) {
        return variables.get(identifier);
    }
    
    public void set(String identifier, Word value) {
        variables.put(identifier, value);
    }
}

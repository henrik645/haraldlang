package nu.henrikvester.haraldlang.exceptions;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
    }
   
    public NotImplementedException(String message) {
        super(message);
    }
}

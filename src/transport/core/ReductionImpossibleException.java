package transport.core;

public class ReductionImpossibleException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public ReductionImpossibleException(String message) {
        super(message);
    }
}

package transport.core;

public enum TypeReclamation {
    TECHNIQUE("Problème technique"), 
    SERVICE("Problème de service"),
    PAIEMENT("Problème de paiement");
    
    private String description;
    
    TypeReclamation(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}

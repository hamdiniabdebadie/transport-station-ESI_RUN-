package transport.core;

public enum MethodePaiement {
    ESPECES("Espèces"),
    DAHABIA("Carte Dahabia"),
    BARIDIMOB("BaridiMob");
    
    private String description;
    
    MethodePaiement(String description) {
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

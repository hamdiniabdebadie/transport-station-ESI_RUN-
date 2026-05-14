package transport.core;

import java.io.Serializable;
import java.time.LocalDate;

public class Ticket extends TitreTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double PRIX_TICKET = 50.0;
    private String modePaiement;
    
    public Ticket(Personne personne, String modePaiement) {
        super(personne);
        this.modePaiement = modePaiement;
        this.prix = PRIX_TICKET;
    }
    
    @Override
    public boolean estValide(LocalDate dateValidation) throws TitreNonValideException {
        if (dateValidation == null) {
            throw new TitreNonValideException("Date de validation non spécifiée");
        }
        
        // Ticket is valid only on the day of purchase
        if (dateValidation.equals(dateAchat)) {
            return true;
        } else {
            throw new TitreNonValideException("Ticket expiré. Valable uniquement le " + dateAchat);
        }
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
}
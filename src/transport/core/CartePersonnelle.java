package transport.core;

import java.io.Serializable;
import java.time.LocalDate;

public class CartePersonnelle extends TitreTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double PRIX_BASE = 5000.0;
    private String type;
    
    public CartePersonnelle(Personne personne) throws ReductionImpossibleException {
        super(personne);
        calculerPrixEtType();
    }
    
    private void calculerPrixEtType() throws ReductionImpossibleException {
        Personne personne = getPersonne();
        
        if (personne == null) {
            throw new ReductionImpossibleException("Personne non spécifiée");
        }
        
        // Calculate price based on person type
        if (personne instanceof Usager) {
            Usager usager = (Usager) personne;
            int age = usager.getAge();
            boolean handicap = usager.estHandicape();
            
            if (handicap) {
                // Carte solidarité: 50% reduction for people with disabilities
                this.prix = PRIX_BASE * 0.5;
                this.type = "Solidarité";
            } else if (age < 25) {
                // Carte junior: 30% reduction for users under 25
                this.prix = PRIX_BASE * 0.7;
                this.type = "Junior";
            } else if (age > 65) {
                // Carte senior: 25% reduction for users over 65
                this.prix = PRIX_BASE * 0.75;
                this.type = "Senior";
            } else {
                this.prix = PRIX_BASE;
                this.type = "Standard";
            }
        } else if (personne instanceof Employe) {
            // Carte partenaire: 40% reduction for employees
            this.prix = PRIX_BASE * 0.6;
            this.type = "Partenaire";
        } else {
            throw new ReductionImpossibleException("Type de personne non reconnu");
        }
    }
    
    @Override
    public boolean estValide(LocalDate dateValidation) throws TitreNonValideException {
        if (dateValidation == null) {
            throw new TitreNonValideException("Date de validation non spécifiée");
        }
        
        // Card is valid for one year from purchase date
        LocalDate dateExpiration = dateAchat.plusYears(1);
        if (!dateValidation.isAfter(dateExpiration)) {
            return true;
        } else {
            throw new TitreNonValideException("Carte expirée depuis le " + dateExpiration);
        }
    }
    
    public String getType() {
        return type;
    }
}
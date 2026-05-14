package transport.core;

import java.time.LocalDate;

// Employee class
public class Employe extends Personne {
    private final String matricule;
    private final Fonction fonction;

    public Employe(String nom, String prenom, LocalDate dateNaissance, boolean handicap, 
                  String matricule, Fonction fonction) {
        super(nom, prenom, dateNaissance, handicap);
        this.matricule = matricule;
        this.fonction = fonction;
    }

    public String getMatricule() { return matricule; }
    public Fonction getFonction() { return fonction; }
    
    @Override
    public String toString() {
        return super.toString() + " (Employé #" + matricule + ", " + fonction + ")";
    }
}

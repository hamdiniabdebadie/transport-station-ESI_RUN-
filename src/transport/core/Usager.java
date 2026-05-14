package transport.core;

import java.time.LocalDate;

// Regular user class
public class Usager extends Personne {
    private static int compteur = 0;
    private int id;

    public Usager(String nom, String prenom, LocalDate dateNaissance, boolean handicap) {
        super(nom, prenom, dateNaissance, handicap);
        this.id = ++compteur;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return super.toString() + " (Usager #" + id + ")";
    }
}

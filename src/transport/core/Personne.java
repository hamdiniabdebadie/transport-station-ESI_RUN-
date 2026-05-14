package transport.core;

import java.time.LocalDate;
import java.time.Period;

// Abstract base class for all people
public abstract class Personne {
    protected String nom;
    protected String prenom;
    protected LocalDate dateNaissance;
    protected boolean handicap;

    public Personne(String nom, String prenom, LocalDate dateNaissance, boolean handicap) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.handicap = handicap;
    }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public boolean estHandicape() { return handicap; }
    
    @Override
    public String toString() {
        return prenom + " " + nom;
    }
    
    public int getAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
}

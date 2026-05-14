package transport.core;

import java.io.Serializable;
import java.time.LocalDate;

// Complaint class
public class Reclamation implements Serializable, Comparable<Reclamation> {
    private static int compteur = 1;
    private int numero;
    private LocalDate date;
    private Personne personne;
    private TypeReclamation type;
    private Suspendable cible;
    private String description;

    public Reclamation(Personne personne, TypeReclamation type, Suspendable cible, String description) {
        this.personne = personne;
        this.type = type;
        this.cible = cible;
        this.description = description;
        this.date = LocalDate.now();
        this.numero = compteur++;
    }

    public Suspendable getCible() { return cible; }
    public int getNumero() { return numero; }
    public LocalDate getDate() { return date; }
    public Personne getPersonne() { return personne; }
    public String getDescription() { return description; }
    public TypeReclamation getType() { return type; }

    @Override
    public String toString() {
        return "Réclamation #" + numero + " | Date: " + date + " | Type: " + type + " | " +
                "Cible: " + cible + " | "+
                "Description: " + description+" | "+
                "Soumise par: " + personne;
    }
    
    @Override
    public int compareTo(Reclamation other) {
        return Integer.compare(this.numero, other.numero);
    }
}

package transport.core;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TitreTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger compteur = new AtomicInteger(1000);
    
    protected int id;
    protected LocalDate dateAchat;
    protected double prix;
    protected Personne personne;
    
    public TitreTransport(Personne personne) {
        this.id = compteur.incrementAndGet();
        this.dateAchat = LocalDate.now();
        this.personne = personne;
    }
    
    public abstract boolean estValide(LocalDate dateValidation) throws TitreNonValideException;
    
    public int getId() {
        return id;
    }
    
    public LocalDate getDateAchat() {
        return dateAchat;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public Personne getPersonne() {
        return personne;
    }
}

package transport.core;

public class Station implements Suspendable {
    private String nom;
    private boolean suspendu;

    public Station(String nom) {
        this.nom = nom;
        suspendu = false;
    }

    public String getNom() { return nom; }

    @Override
    public String toString() { return "Station de " + nom; }

    @Override
    public void suspendre() {
        this.suspendu = true;
    }

    @Override
    public void reactiver() {
        this.suspendu = false;
    }

    @Override
    public boolean estSuspendu() {
        return this.suspendu;
    }

    @Override
    public String getEtat() {
        return this.suspendu ? "suspendu" : "actif";
    }
}

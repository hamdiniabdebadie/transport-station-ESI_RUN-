package transport.core;

public class MoyenTransport implements Suspendable {
    private String identifiant;
    private boolean suspendu;

    public MoyenTransport(String identifiant) {
        this.identifiant = identifiant;
        this.suspendu = false;
    }

    public String getIdentifiant() { return identifiant; }
    
    @Override
    public String toString() { return identifiant; }

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

package transport.core;

import java.io.Serializable;
import java.util.*;

// Complaint service class
public class ServiceReclamation implements Serializable {
    private final int SEUIL = 3;
    private Map<TypeReclamation, TreeSet<Reclamation>> reclamationsParType = new TreeMap<>();
    private Map<Personne, TreeSet<Reclamation>> reclamationsParPersonne = new HashMap<>();
    private Map<Suspendable, TreeSet<Reclamation>> reclamationsParSuspendable = new HashMap<>();
    private List<Reclamation> toutesReclamations = new ArrayList<>();

    // Serializable comparators to replace lambdas
    private static class NumeroComparator implements Comparator<Reclamation>, Serializable {
        @Override
        public int compare(Reclamation r1, Reclamation r2) {
            return Integer.compare(r1.getNumero(), r2.getNumero());
        }
    }
    
    private static class TypeNumeroComparator implements Comparator<Reclamation>, Serializable {
        @Override
        public int compare(Reclamation r1, Reclamation r2) {
            int typeCompare = r1.getType().compareTo(r2.getType());
            if (typeCompare != 0) {
                return typeCompare;
            }
            return Integer.compare(r1.getNumero(), r2.getNumero());
        }
    }

    public ServiceReclamation() {
        for (TypeReclamation type : TypeReclamation.values()) {
            reclamationsParType.put(type, new TreeSet<>(new NumeroComparator()));
        }
    }

    public void soumettre(Reclamation reclamation) {
        // Add to all collections
        toutesReclamations.add(reclamation);
        
        // Add to type map
        TypeReclamation type = reclamation.getType();
        reclamationsParType.get(type).add(reclamation);

        // Add to person map
        Personne personne = reclamation.getPersonne();
        if (!reclamationsParPersonne.containsKey(personne)) {
            reclamationsParPersonne.put(personne, new TreeSet<>(new TypeNumeroComparator()));
        }
        reclamationsParPersonne.get(personne).add(reclamation);

        // Add to suspendable map
        Suspendable cible = reclamation.getCible();
        if (!reclamationsParSuspendable.containsKey(cible)) {
            reclamationsParSuspendable.put(cible, new TreeSet<>(new TypeNumeroComparator()));
        }
        reclamationsParSuspendable.get(cible).add(reclamation);

        verifierEtSuspendre(cible);
    }

    public void resoudre(Reclamation reclamation) {
        // Remove from all collections
        toutesReclamations.remove(reclamation);
        
        TypeReclamation type = reclamation.getType();
        reclamationsParType.get(type).remove(reclamation);

        Personne personne = reclamation.getPersonne();
        if (reclamationsParPersonne.containsKey(personne)) {
            reclamationsParPersonne.get(personne).remove(reclamation);
        }

        Suspendable cible = reclamation.getCible();
        if (reclamationsParSuspendable.containsKey(cible)) {
            TreeSet<Reclamation> recs = reclamationsParSuspendable.get(cible);
            recs.remove(reclamation);

            if (recs.size() < SEUIL) {
                cible.reactiver();
            }
        }
    }

    private void verifierEtSuspendre(Suspendable cible) {
        if (reclamationsParSuspendable.containsKey(cible)) {
            if (reclamationsParSuspendable.get(cible).size() >= SEUIL) {
                cible.suspendre();
            }
        }
    }
    
    public List<Reclamation> getToutesReclamations() {
        return new ArrayList<>(toutesReclamations);
    }
    
    public int getNombreReclamations(Suspendable cible) {
        if (reclamationsParSuspendable.containsKey(cible)) {
            return reclamationsParSuspendable.get(cible).size();
        }
        return 0;
    }
}

package transport.core;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransportService implements Serializable {
    private List<Personne> personnes = new ArrayList<>();
    private List<TitreTransport> titres = new ArrayList<>();
    private List<Station> stations = new ArrayList<>();
    private List<MoyenTransport> moyensTransport = new ArrayList<>();
    private ServiceReclamation serviceReclamation = new ServiceReclamation();
    private List<ActivityLog> activityLogs = new ArrayList<>();
    
    // Singleton pattern
    private static TransportService instance;
    
    private TransportService() {
        // Initialize with some default data
        initDefaultData();
    }
    
    public static TransportService getInstance() {
        if (instance == null) {
            instance = loadFromFile();
            if (instance == null) {
                instance = new TransportService();
            }
        }
        return instance;
    }
    
    private void initDefaultData() {
        // Add some default stations
        stations.add(new Station("Alger Centre"));
        stations.add(new Station("Bab Ezzouar"));
        stations.add(new Station("Hussein Dey"));
        stations.add(new Station("El Harrach"));
        stations.add(new Station("Dar El Beida"));
        
        // Add some default transport means
        moyensTransport.add(new MoyenTransport("Bus #1"));
        moyensTransport.add(new MoyenTransport("Bus #2"));
        moyensTransport.add(new MoyenTransport("Tramway #1"));
        moyensTransport.add(new MoyenTransport("Metro #1"));
        moyensTransport.add(new MoyenTransport("Metro #2"));
        
        // Log initialization
        logActivity("Système", "Initialisation", "Système initialisé avec les données par défaut");
    }
    
    // User management
    public void ajouterPersonne(Personne personne) {
        personnes.add(personne);
        logActivity("Utilisateur", "Ajout", "Ajout de l'utilisateur: " + personne.getPrenom() + " " + personne.getNom());
        saveToFile();
    }
    
    public List<Personne> getPersonnes() {
        return new ArrayList<>(personnes);
    }
    
    public List<Usager> getUsagers() {
        List<Usager> usagers = new ArrayList<>();
        for (Personne p : personnes) {
            if (p instanceof Usager) {
                usagers.add((Usager) p);
            }
        }
        return usagers;
    }
    
    public List<Employe> getEmployes() {
        List<Employe> employes = new ArrayList<>();
        for (Personne p : personnes) {
            if (p instanceof Employe) {
                employes.add((Employe) p);
            }
        }
        return employes;
    }
    
    public Personne getPersonneById(int id) {
        for (Personne p : personnes) {
            if (p instanceof Usager && ((Usager) p).getId() == id) {
                return p;
            } else if (p instanceof Employe && ((Employe) p).getMatricule().equals(String.valueOf(id))) {
                return p;
            }
        }
        return null;
    }
    
    // Fare media management
    public Ticket acheterTicket(Personne personne, String modePaiement) {
        Ticket ticket = new Ticket(personne, modePaiement);
        titres.add(ticket);
        logActivity("Titre", "Achat", "Achat d'un ticket #" + ticket.getId() + 
                   " pour " + personne.getPrenom() + " " + personne.getNom() + " à 50 DA");
        saveToFile();
        return ticket;
    }

    public CartePersonnelle acheterCarte(Personne personne) throws ReductionImpossibleException {
        CartePersonnelle carte = new CartePersonnelle(personne);
        titres.add(carte);
        logActivity("Titre", "Achat", "Achat d'une carte " + carte.getType() + " #" + carte.getId() + 
                   " pour " + personne.getPrenom() + " " + personne.getNom() + " à " + carte.getPrix() + " DA");
        saveToFile();
        return carte;
    }
    
    public List<TitreTransport> getTitres() {
        // Sort by purchase date, most recent first
        List<TitreTransport> sortedTitres = new ArrayList<>(titres);
        sortedTitres.sort(Comparator.comparing(TitreTransport::getDateAchat).reversed());
        return sortedTitres;
    }
    
    public TitreTransport getTitreById(int id) {
        for (TitreTransport titre : titres) {
            if (titre.getId() == id) {
                return titre;
            }
        }
        return null;
    }
    
    // Validation
    public boolean validerTitre(int id, LocalDate date) throws TitreNonValideException {
        TitreTransport titre = getTitreById(id);
        if (titre == null) {
            logActivity("Titre", "Validation", "Échec de validation: Titre #" + id + " non trouvé");
            throw new TitreNonValideException("Titre non trouvé");
        }
        
        boolean result = false;
        try {
            if (titre instanceof Ticket) {
                result = ((Ticket) titre).estValide(date);
                logActivity("Titre", "Validation", "Validation du ticket #" + id + ": Succès");
            } else if (titre instanceof CartePersonnelle) {
                result = ((CartePersonnelle) titre).estValide(date);
                logActivity("Titre", "Validation", "Validation de la carte #" + id + ": Succès");
            }
            return result;
        } catch (TitreNonValideException e) {
            logActivity("Titre", "Validation", "Échec de validation: Titre #" + id + " - " + e.getMessage());
            throw e;
        }
    }
    
    // Station and transport means management
    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }
    
    public List<MoyenTransport> getMoyensTransport() {
        return new ArrayList<>(moyensTransport);
    }
    
    public void ajouterStation(Station station) {
        stations.add(station);
        logActivity("Station", "Ajout", "Ajout de la station: " + station.getNom());
        saveToFile();
    }
    
    public void ajouterMoyenTransport(MoyenTransport moyen) {
        moyensTransport.add(moyen);
        logActivity("Transport", "Ajout", "Ajout du moyen de transport: " + moyen.getIdentifiant());
        saveToFile();
    }
    
    // Complaint management
    public void soumettreReclamation(Reclamation reclamation) {
        serviceReclamation.soumettre(reclamation);
        logActivity("Réclamation", "Soumission", "Réclamation #" + reclamation.getNumero() + 
                   " soumise par " + reclamation.getPersonne().getPrenom() + " " + reclamation.getPersonne().getNom());
        saveToFile();
    }
    
    public List<Reclamation> getReclamations() {
        return serviceReclamation.getToutesReclamations();
    }
    
    public int getNombreReclamations(Suspendable cible) {
        return serviceReclamation.getNombreReclamations(cible);
    }
    
    // Activity logging
    public void logActivity(String category, String action, String details) {
        ActivityLog log = new ActivityLog(category, action, details);
        activityLogs.add(log);
        
        // Keep only the last 100 logs
        if (activityLogs.size() > 100) {
            activityLogs = activityLogs.subList(activityLogs.size() - 100, activityLogs.size());
        }
    }
    
    public List<ActivityLog> getRecentActivity() {
        List<ActivityLog> sortedLogs = new ArrayList<>(activityLogs);
        sortedLogs.sort(Comparator.comparing(ActivityLog::getTimestamp).reversed());
        return sortedLogs;
    }
    
    // Statistics
    public int getUserCount() {
        return personnes.size();
    }
    
    public int getTicketCount() {
        int count = 0;
        for (TitreTransport titre : titres) {
            if (titre instanceof Ticket) {
                count++;
            }
        }
        return count;
    }
    
    public int getCardCount() {
        int count = 0;
        for (TitreTransport titre : titres) {
            if (titre instanceof CartePersonnelle) {
                count++;
            }
        }
        return count;
    }
    
    public int getComplaintCount() {
        return getReclamations().size();
    }
    
    // Persistence
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("transport_data.ser"))) {
            oos.writeObject(this);
            
            // Also save to text files for readability
            savePersonnesToText();
            saveTitresToText();
            saveReclamationsToText();
            saveActivityToText();
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }
    
    private void savePersonnesToText() {
        try (PrintWriter writer = new PrintWriter("users.txt")) {
            writer.println("=== LISTE DES UTILISATEURS ===");
            writer.println("Date d'export: " + LocalDateTime.now());
            writer.println("Nombre total: " + personnes.size());
            writer.println();
            
            for (Personne p : personnes) {
                if (p instanceof Usager) {
                    Usager u = (Usager) p;
                    writer.println("USAGER #" + u.getId());
                    writer.println("Nom: " + u.getNom());
                    writer.println("Prénom: " + u.getPrenom());
                    writer.println("Date de naissance: " + u.getDateNaissance());
                    writer.println("Âge: " + u.getAge() + " ans");
                    writer.println("Handicap: " + (u.estHandicape() ? "Oui" : "Non"));
                    writer.println();
                } else if (p instanceof Employe) {
                    Employe e = (Employe) p;
                    writer.println("EMPLOYÉ #" + e.getMatricule());
                    writer.println("Nom: " + e.getNom());
                    writer.println("Prénom: " + e.getPrenom());
                    writer.println("Date de naissance: " + e.getDateNaissance());
                    writer.println("Âge: " + e.getAge() + " ans");
                    writer.println("Handicap: " + (e.estHandicape() ? "Oui" : "Non"));
                    writer.println("Fonction: " + e.getFonction());
                    writer.println();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs: " + e.getMessage());
        }
    }
    
    private void saveTitresToText() {
        try (PrintWriter writer = new PrintWriter("media.txt")) {
            writer.println("=== LISTE DES TITRES DE TRANSPORT ===");
            writer.println("Date d'export: " + LocalDateTime.now());
            writer.println("Nombre total: " + titres.size());
            writer.println();
            
            for (TitreTransport t : titres) {
                if (t instanceof Ticket) {
                    Ticket ticket = (Ticket) t;
                    writer.println("TICKET #" + ticket.getId());
                    writer.println("Date d'achat: " + ticket.getDateAchat());
                    writer.println("Prix: " + ticket.getPrix() + " DA");
                    writer.println("Validité: Jour d'achat uniquement");
                    writer.println();
                } else if (t instanceof CartePersonnelle) {
                    CartePersonnelle carte = (CartePersonnelle) t;
                    writer.println("CARTE #" + carte.getId() + " - " + carte.getType());
                    writer.println("Date d'achat: " + carte.getDateAchat());
                    writer.println("Date d'expiration: " + carte.getDateAchat().plusYears(1));
                    writer.println("Prix: " + carte.getPrix() + " DA");
                    writer.println("Propriétaire: " + carte.getPersonne().getPrenom() + " " + carte.getPersonne().getNom());
                    writer.println();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des titres: " + e.getMessage());
        }
    }
    
    private void saveReclamationsToText() {
        try (PrintWriter writer = new PrintWriter("complaints.txt")) {
            List<Reclamation> reclamations = getReclamations();
            
            writer.println("=== LISTE DES RÉCLAMATIONS ===");
            writer.println("Date d'export: " + LocalDateTime.now());
            writer.println("Nombre total: " + reclamations.size());
            writer.println();
            
            for (Reclamation r : reclamations) {
                writer.println("RÉCLAMATION #" + r.getNumero());
                writer.println("Date: " + r.getDate());
                writer.println("Type: " + r.getType());
                writer.println("Cible: " + r.getCible());
                writer.println("Soumise par: " + r.getPersonne().getPrenom() + " " + r.getPersonne().getNom());
                writer.println("Description: " + r.getDescription());
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des réclamations: " + e.getMessage());
        }
    }
    
    private void saveActivityToText() {
        try (PrintWriter writer = new PrintWriter("activity.txt")) {
            writer.println("=== JOURNAL D'ACTIVITÉ ===");
            writer.println("Date d'export: " + LocalDateTime.now());
            writer.println("Nombre d'entrées: " + activityLogs.size());
            writer.println();
            
            List<ActivityLog> sortedLogs = getRecentActivity();
            for (ActivityLog log : sortedLogs) {
                writer.println(log.getTimestamp() + " | " + log.getCategory() + " | " + 
                               log.getAction() + " | " + log.getDetails());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du journal d'activité: " + e.getMessage());
        }
    }
    
    private static TransportService loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("transport_data.ser"))) {
            return (TransportService) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Aucune sauvegarde trouvée ou erreur de chargement: " + e.getMessage());
            return null;
        }
    }
}

import Exception.*;
import java.util.*;

public class Blog {

    /*
     *   Overview:
     *       Classe astratta di Blog rappresenta la piattaforma che gestisce gli utenti e i loro post.
     *       Infatti, abbiamo le set ListaUtenti e ListaUtentiVeri per indicare gli Utenti che si sono iscritti alla piattaforma,
     *       che viene rappresentata dalla Map dei following.
     *       Attraverso i metodi del Blog riesco a gestire gli utenti(con post annessi) e filtrare i post attraverso vari parametri.
     *       In più è presente un metodo particolare per ottenere la Map dei followers e gli utenti più seguiti anche chiamati influencer.
     *        
     *       
     *
     *    Typical Element:
     *       <ListaUtenti ,following,ListaUtentiVeri>, dove:
     *           ListaUtenti (Set<String>)              la lista degli username degli utenti registrati
     *           following (Map<String, Set<String>>)   rappresenta la relazione tra i vari utenti e gli utenti che seguono o vengono seguiti
     *           ListaUtentiVeri (Set<Utenti>)          la lista degli oggetti che rappresentano degli utenti registrati
     *          
     *    Abstraction function:
     *          v:<ListaUtenti> -> <Username> | v:LU -> Us | forall v:(ListaUtenti) -> username
     *          t:<ListaUtentiVeri> -> <Utente> | t:LUV -> U | forall t:(ListaUtentiVeri) -> utente | per ogni utente.Username appartiene a ListaUtenti 
     *          following:<ListaUtenti1,Username> -> <ListaUtenti2> | following:<LU1,Us> -> LU2 | 
     *                    forall following:(listautenti1,Username) -> listautenti2 |
     *                    | dove ListaUtenti1 != ListaUtenti2 && Username appartine(ListaUtenti1) && Username non appartine(ListaUtenti2) 
     *             
     * 
     * 
     *      
     *     Representation Invariant:
     *       ListaUtenti != null  
     *       && following != null
     *       && ListaUtentiVeri != null
     *       && for each(Utente) in ListaUtenti : name(Utente) != "" && unique
     *       && for each(Utente) in (for each(ListaUtenti) in following : name(following.ListaUtente.Utente) != "" && unique)
     * 
     */

    private Set<String> ListaUtenti; // a è un utente, Map[a] = lista di tutti i following(persone che segue a)
                                     // List<E> following
    private Map<String, Set<String>> following;
    private Set<Utente> ListaUtentiVeri; // è una Set di Utenti da cui posso prelevare molte info

    /**
     * Costruttore del Blog
     */
    public Blog() {
        this.ListaUtenti = new HashSet<>();
        this.following = new HashMap<>();
        this.ListaUtentiVeri = new HashSet<>();
    }

    /**
     * Getter following
     *
     * @return Map<String, Set<String>> following, cioè ritorna la lista dei following, cioè la gente che segue un utente
     */
    public Map<String, Set<String>> getFollowing() {
        return following;
    }

    /**
     * Getter ListaUtentiVeri
     *
     * @return Set<Utente> ListaUtentiVeri, cioè ritorna la lista degli oggetti che rappresentano degli utenti registrati
     */
    public Set<Utente> getUtentiVero() {
        return ListaUtentiVeri;
    }

    /**
     * Getter ListaUtenti
     *
     * @return Set<Utente> ListaUtenti, cioè ritorna la lista degli Username che rappresentano degli utenti registrati
     */
    public Set<String> getListaUtentiFalsi() {
        return ListaUtenti;
    }

    /**
     * Aggiunge l'utente al blog
     *
     * @param U                           rappresenta l'oggetto Utente
     * @throws NullPointerException       se il puntatore all'oggetto U non è valido
     * @throws SameUserException          se l'utente è stato gia aggiunto
     * @modifies this.ListaUtenti
     * @modifies this.following
     * @modifies this.ListaUtentiVeri
     * @effects post(this.ListaUtenti) = pre(this.ListaUtenti) U {U.Username}
     * @effects post(this.following) = pre(this.following) U {U.Username}
     * @effects post(this.ListaUtentiVeri) = pre(this.ListaUtentiVeri) U {U}
     * 
     */
    public void AddUtente(Utente U) throws NullPointerException,SameUserException { // appena creo l'utente, allora lo inserisco nella SET
                                                                                    // e gli creo una posizione nella MAP
        if (U == null)
            throw new NullPointerException();
        if(this.getListaUtentiFalsi().contains(U.getUsername())) throw new SameUserException();
        else
        {
            ListaUtenti.add(U.getUsername()); // Controlla che l'utente non sia stato gia inserito nella lista grazie alla
            following.put(U.getUsername(), new HashSet<>());
            ListaUtentiVeri.add(U);
        }
        
    }

    /**
     * Rimuove l'utente al blog
     *
     * @param U                           rappresenta l'oggetto Utente
     * @throws NullPointerException       se il puntatore all'oggetto U non è valido
     * @throws UserNotFoundExeception     se l'utente U non è presente nel blog
     * @modifies this.ListaUtenti
     * @modifies this.following
     * @modifies this.ListaUtentiVeri
     * @effects post(this.ListaUtenti) = pre(this.ListaUtenti) / {U.Username}
     * @effects post(this.following) = pre(this.following) / {U.Username}
     * @effects post(this.ListaUtentiVeri) = pre(this.ListaUtentiVeri) / {U}
     * 
     */
    public void RemoveUtente(Utente U) throws NullPointerException, UserNotFoundExeception {
        if (U == null)
            throw new NullPointerException();

        if (this.ListaUtenti.contains(U.getUsername())) // mi cerca l'username dell'utente U nella SET
        { // va eliminato dalla Map
            ListaUtenti.remove(U.getUsername());
            ListaUtentiVeri.remove(U);
            for (String s : ListaUtenti)
                following.remove(s, U.getUsername());

            following.remove(U.getUsername());
        } else
            throw new UserNotFoundExeception();
    }

    /**
     * Aggiunge un utente ai seguaci di un altro utente
     *
     * @param segue                           rappresenta l'oggetto Utente
     * @param seguito                         rappresenta l'oggetto Utente
     * @throws NullPointerException           se segue o seguito sono non è valido
     * @throws SameUserException              se segue e seguito sono lo stesso Utente
     * @throws UserNotFoundExeception         se l'utente segue non è presente nel blog
     * @modifies this.following.segue
     * @effects post(this.following.segue) = pre(this.following.segue) U {seguito.Username}
     * 
     */
    public void AddFollowing(Utente segue, Utente seguito)
            throws NullPointerException, SameUserException, UserNotFoundExeception { // va cercato il nome degli utenti
                                                                                     // e poi va aggiunto segue alla
                                                                                     // lista
                                                                                     // dell' hash map con key =
                                                                                     // username di seguito
        if (segue == null || seguito == null)
            throw new NullPointerException();
        if (segue.getUsername().equals(seguito.getUsername()))
            throw new SameUserException();

        if (this.following.containsKey(segue.getUsername())) {
            this.following.get(segue.getUsername()).add(seguito.getUsername());
        } else
            throw new UserNotFoundExeception();
    }

    /**
     * Rimuove un utente dai seguaci di un altro utente
     *
     * @param segue                           rappresenta l'oggetto Utente
     * @param seguito                         rappresenta l'oggetto Utente
     * @throws NullPointerException           se segue o seguito sono non è valido
     * @throws SameUserException              se segue e seguito sono lo stesso Utente
     * @throws UserNotFoundExeception         se l'utente seguito non è presente nella lista dei following dell'utente segue
     *                                        e se l'utente segue non è presente nel blog
     * @modifies this.following.segue
     * @effects post(this.following.segue) = pre(this.following.segue) / {seguito.Username}
     * 
     */
    public void RemoveFollowing(Utente segue, Utente seguito)
            throws NullPointerException, SameUserException, UserNotFoundExeception {
        if (segue == null || seguito == null)
            throw new NullPointerException();
        if (segue.getUsername().equals(seguito.getUsername()))
            throw new SameUserException();

        if (this.following.containsKey(segue.getUsername()) && this.following.get(segue.getUsername()).contains(seguito.getUsername())) {
            this.following.get(segue.getUsername()).remove(seguito.getUsername());
        } else
            throw new UserNotFoundExeception();
    }

    /**
     * Getter ListaFollowing di un Utente
     * 
     * @param segue                           rappresenta l'oggetto Utente
     * @throws NullPointerException           se segue non è valido
     * @throws UserNotFoundExeception         se l'utente segue non è presente nel blog
     * @return Set<String> ListaFollowing, cioè ritorna la lista degli Username che segue un utente
     */
    public Set<String> returnListFollowing(Utente segue) throws NullPointerException, UserNotFoundExeception { // restituisco
                                                                                                               // la
                                                                                                               // lista
                                                                                                               // delle
                                                                                                               // persone
                                                                                                               // che
                                                                                                               // seguo
        if (segue == null)
            throw new NullPointerException();

        if (this.following.containsKey(segue.getUsername()))
            return this.following.get(segue.getUsername());
        else
            throw new UserNotFoundExeception();
    }

    /**
     * Getter ListaMentioned di una lista di Post
     * 
     * @param ps                              rappresenta una lista di Post
     * @throws NullPointerException           se la lista di Post non è valida
     * @return Set<String> ListaMentioned, cioè ritorna la lista degli Username che sono menzionati nella lista di Post
     */
    public Set<String> getMentionedUsers(List<Post> ps) throws NullPointerException {
        if (ps == null)
            throw new NullPointerException();

        Set<String> Menzionati = new HashSet<>(); // lista dei menzionati
        for (Post p : ps) {
            for (String M : p.getMenzionatiPost()) {
                if (ListaUtenti.contains(M)) // controlliamo se l'utente menzionato esiste
                    Menzionati.add(M);
            }
        }
        return Menzionati;
    }

    /**
     * Getter ListaMentioned
     * 
     * @throws NullPointerException           se la lista di Post di tutto il blog non è valida
     * @return Set<String> ListaMentioned, cioè ritorna la lista degli Username che sono menzionati nella lista di Post
     */
    public Set<String> getMentionedUsers() throws NullPointerException {
        List<Post> psNuova = new ArrayList<Post>();

        for (Utente elem : ListaUtentiVeri) // voglio unire tutte le liste di tutto il blog in una lista sola
        {
            psNuova.addAll(elem.getPostList());
        }

        return getMentionedUsers(psNuova);
    }

    /**
     * Getter Utente
     * 
     * @param Username                        rappresenta l'username di un Utente
     * @throws NullPointerException           se l'username non è valido
     * @throws VoidTextException              se l'username è vuoto
     * @throws UserNotFoundExeception         se non esiste un utente con l'username del parametro
     * @return Utente elem, cioè ritorna l'utente elem con l'username del parametro
     * @return null, se non trova l'oggetto
     * 
     */
    public Utente GetUserForUsername(String UserName)
            throws NullPointerException, VoidTextException, UserNotFoundExeception { // mi restutisce l'oggetto utente
                                                                                     // dandogli il suo username se
                                                                                     // esiste
        if (UserName == null)
            throw new NullPointerException();
        if (UserName.length() == 0)
            throw new VoidTextException();

        if (ListaUtenti.contains(UserName)) {
            for (Utente elem : ListaUtentiVeri) {
                if (UserName.equals(elem.getUsername())) {
                    return elem;
                }
            }
        } else
            throw new UserNotFoundExeception();

        return null; // in caso che non trovi l'oggetto restituisce NULL
    }

    /**
     * Getter ListaPost attraverso un username
     * 
     * @param Username                        rappresenta l'username di un Utente
     * @throws NullPointerException           se l'username non è valido
     * @throws VoidTextException              se l'username è vuoto
     * @throws UserNotFoundExeception         se non esiste un utente con l'username del parametro
     * @return ListaPost di un Utente
     * 
     */
    public List<Post> writtenBy(String UserName)
            throws NullPointerException, VoidTextException, UserNotFoundExeception {
        return GetUserForUsername(UserName).getPostList(); // ottengo tutti i post di un utente attraverso il suo
                                                           // username
    }

    /**
     * Getter ListaPost attraverso un username e una lista di Post
     * 
     * @param Username                        rappresenta l'username di un Utente
     * @param ps                              rappresenta una Lista di Post
     * @throws NullPointerException           se l'username o la lista di Post non sono validi
     * @throws VoidTextException              se l'username è vuoto
     * @throws UserNotFoundExeception         se non esiste un utente con l'username del parametro
     * @return ListaPost di un Utente         filtro la lista di post per un singolo Utente
     * 
     */
    public List<Post> writtenBy(List<Post> ps, String UserName)
            throws NullPointerException, VoidTextException, UserNotFoundExeception {
        if (ps == null)
            throw new NullPointerException();
        if (UserName == null)
            throw new NullPointerException();
        if (UserName.length() == 0)
            throw new VoidTextException();
        if (!ListaUtenti.contains(UserName))
            throw new UserNotFoundExeception();

        List<Post> RisultatoPs = new ArrayList<>();
        for (Post elem : ps) {
            if (elem.getAutore().equals(UserName)) // prendeva un lista di post e un username
            { // ottengo tutti i post che appartengono al mio username che ho nel parametro
                RisultatoPs.add(elem);
            }
        }

        return RisultatoPs;
    }

    /**
     * Getter ListaPost attraverso una lista di Words
     * 
     * @param Words                           rappresenta una lista di parole
     * @throws NullPointerException           se la lista di parole non è valida
     * @return RisultatoPs                    filtro la lista di post per una lista di Words 
     * 
     */
    public List<Post> containing(List<String> Words) throws NullPointerException {
        if (Words == null)
            throw new NullPointerException();

        List<Post> RisultatoPs = new ArrayList<>();
        for (Utente elem : ListaUtentiVeri) // cerco in tutto il mio blog tutti i post per filtrarli attraverso la lista
                                            // di Words
        {
            for (Post p : elem.getPostList()) {
                if (p.ContainWords(Words)) // controlla se è presente una parola della lista Words nel testo del post p
                { // in caso affermativo aggiunge il post alla lista
                    RisultatoPs.add(p);
                }
            }
        }
        return RisultatoPs;
    }

    /**
     * Getter Map followers
     * 
     * @param Ps                              rappresenta una lista di Post
     * @throws NullPointerException           se la lista di Post non è valida
     * @return RisultatoPs                    creo una map dalla lista di post dove la relazione è tra utenti e followers 
     * 
     */
    // PUNTO 1
    public Map<String, Set<String>> guessFollowers(List<Post> Ps) throws NullPointerException { // si deve trovare
                                                                                                // attraverso la lista
                                                                                                // dei post gli autori e
                                                                                                // poi i follower degli
                                                                                                // Autori
        if (Ps == null)
            throw new NullPointerException();

        Set<String> Autori = new HashSet<>();
        for (Post p : Ps) // cerco tutti gli Autori della lista di post ottenuta
        {
            Autori.add(p.getAutore());
        }

        Map<String, Set<String>> follower = new HashMap<>();

        for (String s : Autori) // scorro tutti gli autori della lista di post ottenuta
        {
            follower.put(s, new HashSet<>()); // key : s -> set nuova di follower
            for (String seguace : ListaUtenti) // dovrebbe trovare tutti i follower di s attreaverso la Map dei
                                                   // following
            {
                for (String f : following.get(seguace)) // scansiona tutte le persone che il seguace segue
                {
                    if (f.equals(s)) {
                        follower.get(s).add(seguace);
                    }
                }
            }
        }

        return follower;
    }

    /**
     * Set numFollowers
     * 
     * @param Followers                       rappresenta una mappa di followers 
     * @throws NullPointerException           se la mappa followers o una stringa s(Username) non sono validi
     * @throws VoidTextException              se trovo una stringa s(Username) vuota
     * @throws UserNotFoundExeception         se non trovo un utente presente nel blog
     * @throws NegativeValueException         se il sistema prova a mettere un numero di followers n<0
     * @modifies this.U.NumberOfFollower
     * @effects post(this.U.NumberOfFollower) = followers.get(s).size()
     * 
     */
    public void ContaFollower(Map<String, Set<String>> followers)
            throws NullPointerException, VoidTextException, UserNotFoundExeception, NegativeValueException { // questa
                                                                                                             // funzione
                                                                                                             // conta i
                                                                                                             // followers
                                                                                                             // che sono
                                                                                                             // presenti
                                                                                                             // nella
                                                                                                             // MAP
                                                                                                             // fatta
                                                                                                             // nel
                                                                                                             // punto 1
        if (followers == null)
            throw new NullPointerException();

        Utente U;
        for (String s : ListaUtenti) {
            U = this.GetUserForUsername(s);
            if (followers.containsKey(s)) {
                U.setNumberOfFollower(followers.get(s).size());
            }
        }
    }

    /**
     * Get ListaInfluencer
     * 
     * @param follower                        rappresenta la mappa dei followers                     
     * @throws NullPointerException           se la mappa followers o una stringa s(Username) non sono validi
     * @throws VoidTextException              se trovo una stringa s(Username) vuota
     * @throws UserNotFoundExeception         se non trovo un utente presente nel blog
     * @throws NegativeValueException         se il sistema prova a mettere un numero di followers n<0
     * @return risultato                      rappresenta la lista degli influencer, cioè le persone che hanno il numero maggiore di followers 
     * 
     */
    // PUNTO 2
    public List<String> influencers(Map<String, Set<String>> followers)
            throws NullPointerException, VoidTextException, UserNotFoundExeception, NegativeValueException {
        if (followers == null)
            throw new NullPointerException();
        // si deve creare una "lista" che associa
        List<String> risultato = new ArrayList<String>(followers.size()); // creiamo la lista vuota per il risultato
        risultato.addAll(followers.keySet()); // risultato aggiungiamo tutte le key

        ContaFollower(followers); // conto i followers attraverso la map che gli passo e li salvo negli utenti

        // per ovviare alle eccezzioni non accettate dalla funzione lambda si potrebbe
        // fare una try chach oppure eliminare le eccezzioni dal metodo
        // visto che è un metodo di sistema

        // questa parte ordina il risultato secondo il valore delle funzione che
        // definisco dopo la ->

        risultato.sort((String LeftUsername, String RightUsername) -> {
            int LeftCountFollow = 0;
            try {
                LeftCountFollow = GetUserForUsername(LeftUsername).getNumberOfFollower(); 
            } catch (NullPointerException e) { // nei due casi si fanno delle try catch per controllare se le eccezioni si verificano
                e.printStackTrace();
            } catch (VoidTextException e) {
                e.printStackTrace();
            } catch (UserNotFoundExeception e) {
                e.printStackTrace();
            }
            int RightCountFollow = 0;
            try {
                RightCountFollow = GetUserForUsername(RightUsername).getNumberOfFollower();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (VoidTextException e) {
                e.printStackTrace();
            } catch (UserNotFoundExeception e) {
                e.printStackTrace();
            }
                return -Integer.compare(LeftCountFollow, RightCountFollow);
            });
            // è come se scrivessi una qsort con la sua compare in C, infatti la compare mi restitisce 1,0 o -1 a seconda dei count     
            return risultato;
    }
}

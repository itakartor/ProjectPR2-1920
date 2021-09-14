import Exception.*;
import java.util.*;

public class Utente2 extends Utente { // estensione della classe Utente per gestire gli Utenti che non rispettano le regole

    /*
     *   Overview:
     *       Classe astratta di Utente2 estenzione della classe Utente.
     *       Infatti, l'Utente2 ha tutti i metodi e gli attributi dell'Utente, 
     *       che permette la creazione di una struttura dati contenete varie informazioni, tra cui
     *       l'username dell'utente e id, che sono unici, nome dell'utente, il numero dei suoi Followers,
     *       la lista dei post, numSegnalazione e reportedU.  
     *       In più rispetto alla classe Utente si possono sengnalare i post con dei contenuti poco idonei alla piattaforma.
     *        
     *       
     *
     *    Typical Element:
     *       <Id ,Username, Nome, NumberOfFollower, ListaPost, numSegnalazioni, reportedU>, dove:
     *           Id (int)                    è il codice identificativo dell'Utente
     *           Username (string)           è il sopranomme del proprietario del profilo
     *           NumberOfFollower (int)      è il numero delle persone che seguono l'utente
     *           ListaPost (List<Post>)      è l'insieme dei Post che vengono creati dall'utente
     *           numSegnalazioni (int)       è il numero delle segnalazioni che riceve l'utente
     *           reportedU (Set<String>)     è la lista degli Username che hanno segnalato l'utente
     * 
     *    Abstraction function:
     *          <Utente2> -> <Id ,Username, Nome, NumberOfFollower, ListaPost,numSegnalazioni,reportedU>
     *          f:<ListaPost> -> <Post> | f:LP -> P | forall f:(ListaPost) -> p
     *          s:<reportedU> -> <Utente2> | s:RU -> U2 | forall s:(reportedU) -> Utente2
     *          
     *
     *   Representation Invariant:
     *       Username != null && Username != "" 
     *       && Nome != null && Nome != ""
     *       && ListaPost != null
     *       && reportedU != null
     *       && numSegnalazioni >= 0
     */
    private int numSegnalazioni;
    private Set<String> reportedU; //sono gli Username delle persone che hanno segnalato l'utente X

    /**
     * Costruttore dell'Utente2
     *
     * @param Username                    username valido, diverso dalla stringa vuota
     * @param Nome                        nome valido, diverso dalla stringa vuota
     * @throws VoidTextException          se l'Username o il Nome sono vuoti
     * @throws NullPointerException       se l'Username o il Nome non sono validi
     * 
     */
    public Utente2(String Username, String Nome) throws VoidTextException, NullPointerException
    {
        super(Username,Nome);
        this.numSegnalazioni = 0;
        reportedU = new HashSet<>();
    }

    /**
     * Aggiunge il nome dell'utente che ha segnalato un altro utente
     *
     * @param Username                    l'username dell'utente che ha segnalato un altro utente
     * @throws NullPointerException       se l'Username non sono validi
     * @throws VoidTextException          se l'Username sono vuoti
     * @modifies this.reportedU
     * @effects post(this.reportedU) = pre(this.reportedU) U {Username}
     */
    public void addSet(String Username) throws VoidTextException, NullPointerException
    {
        if(Username == null) throw new NullPointerException();
        if(Username.length() == 0) throw new VoidTextException();

        reportedU.add(Username);
    }
    // aggiungo una persona alla Set delle persone che mi hanno segnalato
    // reportedU è una set quindi controlla in modo automatico se ci sono dei duplicati

    /**
     * Getter reportedU
     *
     * @return Set<String> reportedU, cioè ritorna la lista degli username degli utenti che hanno segnalato un altro utente
     */
    public Set<String> GetSetReport() // restituisco la lista delle persone che mi hanno segnalato
    {
        return reportedU;
    }

    /**
     * Incrementa il numero delle segnalazioni
     * 
     * @modifies this.numSegnalazioni
     * @effects post(this.numSegnalazioni = pre(this.numSegnalazioni) + 1
     */
    public void addSegnalazione()
    {
        this.numSegnalazioni++;
    }

    /**
     * Manda una segnalazione al sistema del blog
     *
     * @param ps               rappresenta il Post che l'utente ha segnalato
     * @param b                rappresenta la piattaforma del blog
     * @throws NullPointerException       se il post o il blog non sono validi
     * @throws SameUserException          se il proprietario del post sta segnalando un post di cui autore
     * @throws VoidTextException          se l'Username è vuoto
     * @throws UserNotFoundExeception     se l'Username non esiste
     * @effects manda un messaggio di report al sistema del blog 
     */
    public void report(Post ps, Blog2 b) throws NullPointerException,SameUserException,VoidTextException,UserNotFoundExeception
    {
        if(ps == null || b == null) throw new NullPointerException();
        if(ps.getAutore().equals(this.getUsername())) throw new SameUserException();
                                                                                     //controllo se non mi sto auto segnalando
        b.Segnala(ps, this.getUsername());
    }

    /**
     * Getter numeSegnalazioni
     *
     * @return int numSegnalazioni, cioè ritorna il numero di segnalazioni
     */
    public int getNumSegnalazioni()
    {
        return this.numSegnalazioni;
    }
}

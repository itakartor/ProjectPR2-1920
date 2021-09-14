import Exception.*;
import java.util.*;

public class Utente {

    /*
     *   Overview:
     *       Classe astratta di Utente che permette la creazione di una struttura dati contenete varie informazioni, tra cui
     *       l'username dell'utente e id, che sono unici, nome dell'utente, il numero dei suoi Followers e la lista dei post. 
     *       Utilizzando dei metodi di sistema per ottenere l'id, che viene assegnato all'utente. 
     *       In più grazie ad alcuni metodi l'utente può creare un Post.
     *        
     *          
     *
     *   Typical Element:
     *       <Id ,Username, Nome, NumberOfFollower, ListaPost>, dove:
     *           Id (int)                    è il codice identificativo dell'Utente
     *           Username (string)           è il sopranomme del proprietario del profilo
     *           NumberOfFollower (int)      è il numero delle persone che seguono l'utente
     *           ListaPost (List<Post>)      è l'insieme dei Post che vengono creati dall'utente
     *
     *    Abstraction function:
     *          <Utente> -> <Id ,Username, Nome, NumberOfFollower, ListaPost>
     *          f:<ListaPost> -> <Post> | f:LP -> P | forall f:(ListaPost) -> p
     *    
     *
     *   Representation Invariant:
     *       Username != null && Username != "" 
     *       && Nome != null && Nome != ""
     *       && ListaPost != null
     *       
     *
     */
    private static int base_id = 0; 
    private int Id;
    private String Username;
    private String Nome;
    private int NumberOfFollower;

    private List<Post> ListaPost;

    /**
     * Costruttore dell'Utente
     *
     * @param Username                    username valido, diverso dalla stringa vuota
     * @param Nome                        nome valido, diverso dalla stringa vuota
     * @throws NullPointerException       se l'Username o il Nome non sono validi
     * @throws VoidTextException          se l'Username o il Nome sono vuoti
     * 
     */
    public Utente(String Username, String Nome) throws NullPointerException, VoidTextException
    {
        if(Username == null || Nome == null) throw new NullPointerException();
        if(Username.length() == 0  || Nome.length() == 0) throw new VoidTextException();
        this.Id = base_id++; 
        this.Username = Username;
        this.Nome = Nome;
        this.ListaPost = new ArrayList<Post>();
        this.NumberOfFollower = 0;

    }

    /**
     * Getter Username
     *
     * @return String Username, cioè ritorna il sopranome dell'utente
     */
    public String getUsername()
    {
        return this.Username;
    }
    /**
     * Getter Id
     *
     * @return int Id, cioè ritorna l'id dell'Utente
     */
    public int getId()
    {
        return this.Id;
    }

     /**
     * Crea un nuovo Post
     *
     * @param Testo                 il testo del Post scritto dall'utente
     * @throws NullPointerException     se il testo o l'autore non sono validi
     * @throws VoidTextException        se il testo o l'autore sono vuoti
     * @throws LongTextException        se il testo ha più di 140 caratteri
     * @modifies this.ListaPost
     * @effects post(this.ListaPost) = pre(this.ListaPost) U {Post}
     */
    public void CreaPost(String Testo) throws LongTextException, VoidTextException, NullPointerException
    {
        Post a = new Post(this.Username, Testo);
        ListaPost.add(a);
    } //crea un post richiamando il costruttore Post e poi lo aggiunge alla lista dell'utente

    /**
     * Getter ListaPost
     *
     * @return List<Post> ListaPost, cioè ritorna la lista dei Post creati dall'utente
     */
    public List<Post> getPostList()
    {   
        return ListaPost; // restituisce una copia della lista dei post dell'utente
    }

    /**
     * Getter NumberOfFollower
     *
     * @return int NumberOfFollower, cioè ritorna il numero dei follower dell'utente
     */
    public int getNumberOfFollower()
    {
        return this.NumberOfFollower;
    }

    /**
     * Set del NumberOfFollower
     *
     * @param n                         il nuovo numero dei follower dell'utente
     * @throws NegativeValueException   se il numero n<0
     * @modifies this.NumberOfFollower
     * @effects post(this.NumberOfFollower) = n
     */
    public void setNumberOfFollower(int n) throws NegativeValueException
    {
        if(n < 0) throw new NegativeValueException();

        this.NumberOfFollower = n;
    }

}

import Exception.*;

import java.time.LocalDateTime;
import java.util.*;

public class Post{


     /*
     *   Overview:
     *       Classe astratta immutable di Post che permette la creazione di una struttura dati contenete varie informazioni, tra cui
     *       l'username dell'autore, il testo e l'Id. Utilizzando dei metodi di sistema per ottenere data, ora e Menzionati,
     *       che vengono assegnati al Post. 
     *       In più grazie ad alcuni metodi specifici che può richiamare, sia il sistema e l'utente, si riesce ad ottenere il testo,
     *       l'autore e la lista delle persone Menzionate.
     *        
     *       
     *
     *   Typical Element:
     *       <Id ,Autore, Testo, TimeStamp, Menzionati>, dove:
     *           Id (int)            è il codice identificativo del Post
     *           Autore (string)     è l'Username del proprietario del Post
     *           Testo (string)      è l'informazione scritta dall'Utente 
     *           TimeStamp (string)  è l'insieme delle informazioni che descrivono la data e ora di creazione Post
     *           Menzionati (List<String>) è l'insieme degli Utenti esistenti che vengono citati dentro al Testo del Post
     *          
     *
     *   Representation Invariant:
     *       Testo != null && Testo != "" && Testo.length() <= 140
     *       && Autore != null && Autore != ""
     *       && Menzionati != null
     *       
     *
     */

    private static int base_id = 0; // si incrementa ogni volta che creo un post nel blog
    private int Id; //id sequenziale 
    private String Testo; // max 140 caretti
    private String Autore; // L'autore descrive l'username dell'utente

    private Set<String> MenzionatiPost; //creando un post, salvo in una lista apposita le persone che vengono menzionate
                                        //nel testo del post
    private int Giorno;
    private int Mese;
    private int Anno;
    private int Ora;
    private int Minuti;

    private String TimeStamp; //rappresenta l'unione di tutte le informazioni interenti al tempo
    
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());      
  
    /**
     * Costruttore di Post
     *
     * @param Autore                    username valido, diverso dalla stringa vuota
     * @param Testo                     testo valido, diverso dalla stringa vuota, e con meno di 140 caratteri
     * @throws NullPointerException     se il testo o l'autore non sono validi
     * @throws VoidTextException        se il testo o l'autore sono vuoti
     * @throws LongTextException        se il testo ha più di 140 caratteri
     * 
     */

    // è il costruttore principale che ci autogenera id, giorno, mese, anno, ora, minuti
    public Post(String Autore, String Testo) throws NullPointerException, VoidTextException, LongTextException
    {
         
        if(Testo == null || Autore == null) throw new NullPointerException();
        if(Testo.length() == 0) throw new VoidTextException();
        if(Testo.length() >= 140) throw new LongTextException();

        int Ora = LocalDateTime.now().getHour();
        int Minuti = LocalDateTime.now().getMinute();
        int Giorno = calendar.get(Calendar.DAY_OF_MONTH);
        int Mese = calendar.get(Calendar.MONTH) + 1; //Note: +1 the month for current month
        int Anno = calendar.get(Calendar.YEAR);
        
        this.Id = base_id++; // grazie alla variabile statica generiamo i post in modo sequenziale in base a tutto il blog
        this.Autore = Autore;
        this.Testo = Testo;
        this.Giorno = Giorno;
        this.Mese = Mese;
        this.Anno = Anno;
        this.Ora = Ora;
        this.Minuti = Minuti;
        this.TimeStamp = new String(Giorno + "/" + Mese + "/" + Anno + "-" + Ora + ":" + Minuti);
        this.MenzionatiPost = new HashSet<>(); // vogliamo fare un analisi del testo per le menzioni ogni volta che viene creato
                                               // il post. Attraverso una @ indichiamo i menzionati nel testo
        //pars del testo
        //splittiamo le stringhe del testo come un array di parole e poi le analizziamo.
        String[] Parts = Testo.split(" ");//splitta il testo in tante parole divise da uno " " e ogni parola del testo
                                          //è una posizione dell'array di stringhe
        for(int i=0;i<Parts.length;i++)
        {
            if(Parts[i].startsWith("@")) // cerco tutti i nomi preceduti da una @
            {
                this.MenzionatiPost.add(Parts[i].substring(1)); //aggiungiamo il menzionato senza la @ partendo la carattere 1 
            }
        }
        
    }
    
    /**
     * Verifica controllo parole
     *
     * @param Words                     lista di parole valida
     * @throws NullPointerException     se la lista delle Words non è valida
     * @return (true)                   se almeno una parola della lista Words è contenuta nel testo del Post
     * @return (false)                  se nessuna parola della lista Words è contenuta nel testo del Post
     */
    public boolean ContainWords(List<String> Words) throws NullPointerException
    {                                               // controlla se nel testo del post è presente almeno 1 parola della lista Words
        if(Words == null) throw new NullPointerException();
        for(String s : Words)
        {
            if(this.Testo.contains(s))
                return true;
        }
        return false;       
    }

    /**
     * Getter Menzionati
     *
     * @return Set<String> Menzionati, cioè utenti menzionati nel testo del Post
     */
    public Set<String> getMenzionatiPost()
    {
        return MenzionatiPost;
    }

    /**
     * Getter Testo
     *
     * @return String Testo, restituisce ul testo del Post
     */
    public String getTesto()
    {
        return this.Testo;
    }

    /**
     * Getter Autore
     *
     * @return String Autore, restituisce l'username del proprietario
     */
    public String getAutore()
    {
        return this.Autore;
    }
}

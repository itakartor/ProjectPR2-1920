import Exception.*;

import java.util.*;

public class Blog2 extends Blog{ //è un altro insieme di comandi derivati da Bolg per la gestione di materiale offensivo 
    
    /*
     *   Overview:
     *       Classe astratta di Blog2 estensione della classe Blog.
     *       Infatti, rappresenta la piattaforma che gestisce gli utenti e i loro post, con una feature in più, dal momento che
     *       si può gestire i contenuti dei post se contengono dei contenuti inappropriati per il blog o per un utente.
     *       Attraverso i metodi del Blog riesco a gestire gli utenti(con post annessi) e filtrare i post attraverso vari parametri.
     *       In più è presente un metodo particolare per ottenere la Map dei followers e gli utenti più seguiti anche chiamati influencer,
     *       infine posso trattare nel modo più corretto gli utenti che non riespettano le regole della piattaforma.
     *       Infatti, se necessario la piattaforma sospende l'utente non idoneo e cancella momentanemanete cancella i suoi contenuti.
     *        
     *       
     *
     *    Typical Element:
     *       <ListaUtenti ,following,ListaUtentiVeri, LimiteS, Revisionati>, dove:
     *           ListaUtenti (Set<String>)              la lista degli username degli utenti registrati
     *           following (Map<String, Set<String>>)   rappresenta la relazione tra i vari utenti e gli utenti che seguono o vengono seguiti
     *           ListaUtentiVeri (Set<Utenti>)          la lista degli oggetti che rappresentano degli utenti registrati
     *           LimiteS (int)                          rappresenta il limite di segnalazioni che può ricevere l'utente
     *           Revisionati (Set<Utente>)              rappresenta l'insieme degli utenti che hanno superato il Limite di segnalazioni consentite
     *          
     *    Abstraction function:
     *          v:<ListaUtenti> -> <Username> | v:LU -> Us | forall v:(ListaUtenti) -> username
     *          t:<ListaUtentiVeri> -> <Utente> | t:LUV -> U | forall t:(ListaUtentiVeri) -> utente | per ogni utente.Username appartiene a ListaUtenti 
     *          following:<ListaUtenti1,Username> -> <ListaUtenti2> | following:<LU1,Us> -> LU2 | 
     *                    forall following:(listautenti1,Username) -> listautenti2 |
     *                    | dove ListaUtenti1 != ListaUtenti2 && Username appartine(ListaUtenti1) && Username non appartine(ListaUtenti2)
     *          z:<Revisionati> -> <Utente> | z:R -> U | forall z:(Revisionati) -> utente | per ogni utente.Username appartiene a ListaUtenti 
     * 
     *      
     *     Representation Invariant:
     *       ListaUtenti != null  
     *       && following != null
     *       && ListaUtentiVeri != null
     *       && for each(Utente) in ListaUtenti : name(Utente) != "" && unique
     *       && for each(Utente) in (for each(ListaUtenti) in following : name(following.ListaUtente.Utente) != "" && unique)
     *       && Revisionati != null 
     *       && for each(Utente) in Revisionati : name(Utente) != "" && unique
     * 
     */
    private int LimiteS;
    private Set<Utente> Revisionati; // utenti che andranno bannati

    /**
     * Costruttore del Blog2
     * @param LimiteS                                           rappresenta il numero massimo di segnalazioni che un utente può subire
     * @throws NegativeValueException                           se il limite di segnalazioni è negativo       
     */
    public Blog2(int LimiteS) throws NegativeValueException
    {
        super();
        if(LimiteS <= 0) throw new NegativeValueException();

        this.Revisionati = new HashSet<>();
        this.LimiteS = LimiteS;
    }
    
    /**
     * Set LimiteS
     * @param LimiteS                                           rappresenta il numero massimo di segnalazioni che un utente può subire
     * @throws NegativeValueException                           se il limite di segnalazioni è negativo
     * @modifies this.LimiteS
     * @effects post(this.LimiteS) = LimiteS    
     */
    public void SetLimiteS(int LimiteS) throws NegativeValueException
    {
        if(LimiteS <= 0) throw new NegativeValueException();
        this.LimiteS = LimiteS;
    }
    
    /**
     * Get LimiteS
     * @param Username                                          rappresenta l'username di un Utente
     * @throws NullPointerException                             se username non è valido
     * @throws VoidTextException                                se username è vuoto
     * @throws UserNotFoundExeception                           se l'utente con username non è presente nel blog
     * @return Utente2                                          restituisce un oggetto Utente2 con username   
     */
    public Utente2 GetUserForUsername(String Username) throws NullPointerException,VoidTextException,UserNotFoundExeception
    {
        return (Utente2)super.GetUserForUsername(Username);
    }

    /**
     * Get LimiteS
     * @param ps                                                rappresenta un oggetto Post
     * @param UsernameUtenteCheSegnala                          rappresenta l'username di un Utente che ha mandato una segnalazione
     * @throws NullPointerException                             se UsernameUtenteCheSegnala e il puntatore del Post non sono validi
     * @throws VoidTextException                                se UsernameUtenteCheSegnala è vuoto
     * @throws UserNotFoundExeception                           se l'utente con UsernameUtenteCheSegnala non è presente nel blog
     * @throws SameUserException                                se l'utente che ha mandato il messaggio di segnalazione ha gia
     *                                                          segnalato una volta un qualsiasi post dell'utente segnalato
     * @modifies U.reportedU
     * @modifies U.numSegnalazioni
     * @modifies this.Revisionati
     * @modifies super.ListaUtenti
     * @modifies super.following
     * @modifies super.ListaUtentiVeri
     * @effects post(super.ListaUtenti) = pre(super.ListaUtenti) / {U.Username}
     * @effects post(super.following) = pre(super.following) / {U.Username}
     * @effects post(super.ListaUtentiVeri) = pre(super.ListaUtentiVeri) / {U}
     * @effects post(U.reportedU) = pre(U.reportedU) U {UsernameUtenteCheSegnala}
     * @effects post(U.numSegnalazioni) = pre(U.numSegnalazioni) + 1
     * @effects post(this.Revisionati) = pre(this.Revisionati) U {U}
     */
    public void Segnala(Post ps, String UsernameUtenteCheSegnala) throws NullPointerException,VoidTextException,UserNotFoundExeception,SameUserException
    {   // è un metodo che prende un messaggio di report di un Utente e lo esegue se passa i controlli
        // nella peggiore delle ipotesi banna l'utente
        if(ps == null) throw new NullPointerException();
        if(UsernameUtenteCheSegnala == null) throw new NullPointerException();
        if(UsernameUtenteCheSegnala.length() == 0) throw new VoidTextException();

        Utente2 U = this.GetUserForUsername(ps.getAutore());
        if(U.GetSetReport().contains(UsernameUtenteCheSegnala)) throw new UserNotFoundExeception();

        if(!U.GetSetReport().contains(UsernameUtenteCheSegnala))
        {
            U.addSegnalazione();
        }
        else throw new SameUserException();
        U.addSet(UsernameUtenteCheSegnala); // controllo automatico sul duplice 

        if(U.getNumSegnalazioni() > this.LimiteS) // se l'utente supera il massimo delle segnalazioni viene sospeso
        {                                         // infatti non cancello l'utente dalla MAP following perchè potrebbe riottenere l'account
            this.Revisionati.add(U);
            super.RemoveUtente(U);
        }
    }
    
    /**
     * Get Revisionati
     * @return Revisionati                                         restituisce la lista dei revisionati  
     */
    public Set<Utente> getListaRevisionati()
    {
        return Revisionati;
    }
}

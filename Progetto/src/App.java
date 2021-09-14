import Exception.*;
import java.util.*;

public class App {
    static String TestoVuoto = "";
    static String TestoLungo = "Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao Ciao. sono più di 140 caratteri ";
    static String Damiao = "Damiao";

    public static void main(String[] args) throws Exception {
        
        //Creare il Blog
        Blog blog = new Blog(); // non ci sono eccezzioni quindi non va fatta una try cach

        //Creare gli Utenti
        Utente Irene = new Utente("Ire", "Irene");
        Utente Damiano = new Utente(Damiao, "Damiano");
        Utente Giacomo = new Utente("jake", "Giacomo");
        Utente Alice = new Utente("Alic", "Alice");
        Utente Bob = new Utente("Bob", "Bibbo");
        
        // creazione utete -> VoidTextException
        try {
            System.out.println("Creazione Utente errato Username vuoto");
            Utente Pino = new Utente(TestoVuoto, "Pino");
        } catch (VoidTextException e) {
            System.out.println("[FATAL ERROR] Username vuoto");
        }

        // creazione utete -> VoidTextException
        try {
            System.out.println("Creazione Utente errato nome vuoto");
            Utente Pino = new Utente("Pino", TestoVuoto);
        } catch (VoidTextException e) {
            System.out.println("[FATAL ERROR] nome vuoto");
        }

        //Aggiungere un utente al Blog
        blog.AddUtente(Irene);
        blog.AddUtente(Damiano);
        blog.AddUtente(Alice);
        blog.AddUtente(Bob);
        blog.AddUtente(Giacomo);
        //Aggiungere un utente al Blog -> SameUserException 
        try {
            System.out.println("Aggiungo un Utente gia presente");
            blog.AddUtente(Irene);
        } catch (SameUserException e) {
            System.out.println("[FATAL ERROR] Utente gia presente");
        }
        
        //Creazione di alcuni Post
        Irene.CreaPost("ciao @Damiao belli");
        Damiano.CreaPost("@Ire Attenti non sbagliare");
        Damiano.CreaPost("ciao @Bob belli");
        Damiano.CreaPost("sono @Alic");
        Bob.CreaPost("sono @Carmine");

        //Creazione Post -> VoidTextException
        try {
            System.out.println("Creazione Post errato Testo Vuoto");
            Irene.CreaPost(TestoVuoto);
        } catch (VoidTextException e) {
            System.out.println("[FATAL ERROR] Testo vuoto");
        }

        //Creazione Post -> LongTextException 
        try {
            System.out.println("Creazione Post errato Testo Lungo");
            Irene.CreaPost(TestoLungo);
        } catch (LongTextException  e) {
            System.out.println("[FATAL ERROR] Testo Troppo Lungo");
        }
        
        //Aggiunta dei following alla MAP
        blog.AddFollowing(Irene, Damiano); // Damiano è seguito da Irene , Giangi, Alice e Bob
        blog.AddFollowing(Giacomo, Damiano); //giangi segue damiano
        blog.AddFollowing(Alice, Damiano);
        blog.AddFollowing(Bob, Damiano);

        blog.AddFollowing(Damiano,Irene);   // Damiano segue , Irene, Giangi, Alice e Bob
        blog.AddFollowing(Damiano,Giacomo); //giangi segue damiano
        blog.AddFollowing(Damiano,Alice);
        blog.AddFollowing(Damiano,Bob);

        blog.AddFollowing(Giacomo, Irene);
        blog.AddFollowing(Giacomo, Bob);

        //Aggiunta dei following alla MAP -> SameUserException 
        try {
            System.out.println("Aggiunta following alla MAP stesso utente");
            blog.AddFollowing(Giacomo, Giacomo);  
        } catch (SameUserException   e) {
            System.out.println("[FATAL ERROR] Utente non può seguire se stesso");
        }

        Utente NonUtente = new Utente("NonUtente","NOPE"); // questo è un Utente non presente nel Blog
        //Aggiunta dei following alla MAP -> UserNotFoundExeception 
        try {
            System.out.println("Aggiunta following alla MAP Utente da seguire non trovato");
            blog.AddFollowing(NonUtente,Giacomo);
        } catch (UserNotFoundExeception  e) {
            System.out.println("[FATAL ERROR] Utente non presente nel blog");
        }

// fino a qui va bene

        //Rimuovere il following 
        blog.RemoveFollowing(Damiano, Bob);

        //Rimuovere il following -> SameUserException 
        try {
            System.out.println("Rimuovere se stessi dai following");
            blog.RemoveFollowing(Damiano, Damiano);
        } catch (SameUserException e) {
            System.out.println("[FATAL ERROR] Utente non può rimuovere se stesso");
        }
        //Rimuovere il following -> UserNotFoundExeception
        try {
            System.out.println("Rimuovere un following che non è nella lista dei seguiti oppure un utente segue che non è presente nel blog ");
            blog.RemoveFollowing(Damiano, Bob);
        } catch (UserNotFoundExeception e) {
            System.out.println("[FATAL ERROR] Utente non trovato nella lista");
        }

        //Test sulla get della following list di un utente
        System.out.println("[TEST] lista di Following");
        Set<String> ListFollowing = blog.returnListFollowing(Damiano);
        for(String s : ListFollowing)
        {
            System.out.println(s);
        }
        //get della following list di un utente -> UserNotFoundExeception 
        try {
            System.out.println("Richiedo la lista di following di un utente non presente nel blog ");
            ListFollowing = blog.returnListFollowing(NonUtente);
        } catch (UserNotFoundExeception e) {
            System.out.println("[FATAL ERROR] L'utente non esiste nel blog");
        }

        //Ricevere i menzionati di una lista di Post
        List<Post> ListaPost = new ArrayList<>();
        ListaPost.add(Irene.getPostList().get(0));
        ListaPost.add(Damiano.getPostList().get(0));
        ListaPost.add(Bob.getPostList().get(0));
        Set<String> ListaMenzionati = blog.getMentionedUsers(ListaPost);
        System.out.println("[TEST] LISTA MENZIONATI");
        for(String elem : ListaMenzionati)
        {
            System.out.println(elem);
        }

        //Provo ad ottenere un oggetto Utente dal Username
        System.out.println("[TEST] GetUserForUsername");
        Utente DaTrovare = blog.GetUserForUsername("Bob");
        System.out.println("Questo è l'id dell'Utente da trovare:" + DaTrovare.getId());

        //Ottengo un utente dal suo Username ->VoidTextException 
        try {
            System.out.println("Cerco un utente con un Username vuoto");
            DaTrovare = blog.GetUserForUsername("");
        } catch (VoidTextException e) {
            System.out.println("[FATAL ERROR] Username Vuoto");
        }
        //Ottengo un utente dal suo Username ->UserNotFoundExeception 
        try {
            System.out.println("Cerco un utente con un Username che non è presente nel blog");
            DaTrovare = blog.GetUserForUsername("NonUtente");
        } catch (UserNotFoundExeception e) {
            System.out.println("[FATAL ERROR] Utente non trovato");
        }

        //Provo il metodo writtenBy che restutisce i post scritti da un Utente con uno specifico Username
        System.out.println("[TEST] DELLA writtenBy");
        List<Post> ListaPost2 = blog.writtenBy(Damiao);
        for(Post elem : ListaPost2)
        {
            System.out.println(elem.getTesto());
        }
        //Cerco una lista di post attraverso l'username -> VoidTextException
        try {
            System.out.println("Cerco una lista di post attraverso l'username vuoto");
            ListaPost2 = blog.writtenBy("");
        } catch (VoidTextException e) {
            System.out.println("[FATAL ERROR] Username vuoto");
        }
        //Cerco una lista di post attraverso l'username -> UserNotFoundExeception
        try {
            System.out.println("Cerco una lista di post attraverso l'username che non è presente nel blog");
            ListaPost2 = blog.writtenBy("NonUtente");
        } catch (UserNotFoundExeception e) {
            System.out.println("[FATAL ERROR] L'utente non trovato");
        }

        //Cerco di filtrare una lista tutti i Post del mio blog attraverso una lista di Parole
        List<String> parole = new ArrayList<>();
        parole.add("sono");
        parole.add("belli");
        System.out.println("[TEST] DELLA containing");
        ListaPost = blog.containing(parole); //RISULTATO LOCALE: Post di Irene,Damiano e Bob
        for(Post elem : ListaPost)
        {
            System.out.println(elem.getTesto());
        }

        //Costruisco la mappa dei follower attraverso una lista di Post generica
        System.out.println("[TEST] MAPPA FOLLOWER");
        Map<String, Set<String>> follower = blog.guessFollowers(ListaPost); // se gli do la stringa Damiano ottengo le persone che lo seguono
        Set<String> r = follower.get("Damiao");
        System.out.println("[TEST] Stampo i follower dell'utente con l'username Damiao");
        for(String s : r)
        {
            System.out.println(s); //RISULTATO LOCALE: Irene,Damiano e Bob solo questi 3 utenti avranno i couter dei follower aggiornato 
        }

        //Conto tutti i follower attraverso una mappa di follower
        System.out.println("[TEST] Conta dei follower");
        blog.ContaFollower(follower); // in questo caso conta i following perchè gli ho passato la map dei Following
        Set<Utente> persone = blog.getUtentiVero();
        for(Utente s : persone)
        {
            System.out.println(s.getUsername() + " " + s.getNumberOfFollower());
        }

        //Cerco le persone più influenti nel blog
        System.out.println("[TEST] INFLUENCER");
        List<String> influencer = blog.influencers(follower);
        for(String s : influencer)
        {
            System.out.println(s);
        }

        //Test per l'estensione di blog 2 e utente 2
        int limiteS = 2;                //inizializzazione di LimiteS locale
        Blog2 blog2 = new Blog2(limiteS);   //creazione del Blog2
        //creazione di un blog di tipologia 2 -> NegativeValueException
        try {
            System.out.println("Creo una piattaforma Blog2 con un limite di segnalazioni negative");
            Blog2 NonBlog = new Blog2(-2);
        } catch (NegativeValueException e) {
            System.out.println("[FATAL ERROR] Numero negativo");
        }

        //Test creazione di un Utente tipologia 2 con post annessi
        Utente2 Mario = new Utente2("SuperMario", "Mario");
        Utente2 Luigi = new Utente2("Gigi", "Luigi");
        Utente2 Peach = new Utente2("Pricipessa", "Peach");
        Utente2 Yoshi = new Utente2("Yoshi", "serpente");
        //aggiunta degli utenti al blog2
        blog2.AddUtente(Mario);
        blog2.AddUtente(Luigi);
        blog2.AddUtente(Peach);
        blog2.AddUtente(Yoshi);
        //Creazione di alcuni Post
        Mario.CreaPost("ciao @Damiao");
        Luigi.CreaPost("sono Luigi");

        //Prova report Post 
        Mario.report(Luigi.getPostList().get(0), blog2);
        System.out.println("[TEST] stampo quante segnalazioni ha ricevuto l'utente Luigi -> " + Luigi.getNumSegnalazioni());
        //Prova report Post -> SameUserException
        try {
            System.out.println("Segnala un Post con contunuti offensivi");
            Mario.report(Mario.getPostList().get(0), blog2);
        } catch (SameUserException e) {
            System.out.println("[FATAL ERROR] Stai segnalando un tuo Post");
        }

        //Prova un utente che viene bannato
        Yoshi.report(Luigi.getPostList().get(0), blog2);
        Peach.report(Luigi.getPostList().get(0), blog2);
        System.out.println("[TEST] stampo quante segnalazioni ha ricevuto l'utente Luigi per essere rimosso -> " + Luigi.getNumSegnalazioni());
        System.out.println("[TEST] Verifico che Luigi sia stato rimosso dal Blog2 ed inserito nella BlackList");
        //Controllo se è stato rimosso l'utente Luigi
        if(!(blog2.getListaRevisionati().contains(Luigi) )&& blog2.getUtentiVero().contains(Luigi) && blog2.getListaUtentiFalsi().contains(Luigi.getUsername()) && blog2.getFollowing().containsKey(Luigi.getUsername())) // controllo se Luigi è stato bannato e rimosso dal blog 2
        {
            System.out.println("Luigi è ancora presente nel blog2");
        }
        else
        {
            System.out.println("Luigi è stato rimosso con successo a seguito di " + Luigi.getNumSegnalazioni());
        }

        //Provo la get revisionati
        System.out.println("[TEST] Verifico che Luigi è nella Lista Revisionati");
        if(blog2.getListaRevisionati().contains(Luigi))
        {
            System.out.println("Luigi è presente nella lista revisionati");
        }
        System.out.println("FINE");
    }
}

(*progetto ocaml - implementazione di insiemi*)

type ide = string;;

(*i tipi che posso prendere gli insiemi*)
type typeSet= Int | Bool | String;;

type exp = CstInt of int
         | CstTrue
         | CstFalse
         | Den of ide
         | CstString of string
         | Empty of typeSet		(*estensione per insiemi*)
         | Singleton of exp
         | Of of typeSet * collection
         | Union of exp * exp
         | Intersection of exp * exp
         | Difference of exp * exp
         | Push of exp * exp
         | RemoveFrom of exp * exp
         | IsEmpty of exp
         | Contains of exp * exp
         | IsSubset of exp * exp
         | MaxOf of exp
         | MinOf of exp
         | For_all of exp * exp
         | Exists of exp * exp
         | Filter of exp * exp
         | Map of exp * exp		(*estensione per insiemi*)
         | Sum of exp * exp
         | Sub of exp * exp
         | Times of exp * exp
		 | Mod of exp * exp
         | Ifthenelse of exp * exp * exp
         | Eq of exp * exp
         | And of exp * exp
         | Or of exp * exp
         | Not of exp
         | Let of ide * exp * exp
         | Fun of ide * exp
         | Letrec of ide * ide * exp * exp
         | Apply of exp * exp

and collection = Empty | Value of exp * collection;;

type 'v env = (string * 'v) list;; (*definizione di ambiente*)

(*tipi esprimibili*)
type evT = 	Int of int
			| Bool of bool
			| String of string
			| Set of (evT list) * typeSet
			| Closure of ide * exp * evT env 			(*Una chiusura è una coppia di puntatori. Uno punta all'ambiente di definizione e uno punta al codice*)
			| RecClosure of ide * ide * exp * evT env 	(*uguale a Closure ma per le funzioni ricorsive*)
			| Unbound;;

(*funzioni dell'ambiente*)
let emptyEnv  = [ ("", Unbound)] ;;
let bind (s: evT env) (i:string) (x:evT) = ( i, x ) :: s;;

let rec lookup (s:evT env) (i:string) = match s with
  | [] ->  Unbound
  | (j,v)::sl when j = i -> v
  | _::sl -> lookup sl i;;

(*type checker dinamico*)
let typecheck (x, (y: evT)) = match x with
  | "int" ->
      (match y with
       | Int(u) -> true
       | _ -> false)

  | "bool" ->
      (match y with
       | Bool(u) -> true
       | _ -> false)

  | "string" ->
      (match y with
       | String(u) -> true
       | _ -> false)

  | "set" ->
      (match y with
       | Set(l1,_type_) -> true
       | _ -> false)
  | _ -> failwith ("not a valid type");;

(*funzioni primitive*)
let rec getFunType (x) : typeSet =
  match x with
  | CstTrue | CstFalse | Eq(_, _) | And(_, _) | Or(_, _) | Not(_) -> Bool
  | CstString(_) -> String
  | CstInt(_) | Sum(_) | Sub(_) | Times(_) -> Int
  | Ifthenelse(cond, _then_, _else_) -> let ret = getFunType(_then_) in if ret = getFunType(_else_) then ret else failwith("if type error")
  | Let(_, _, fbody) -> getFunType(fbody)
  | Fun(_, fbody) -> getFunType(fbody)
  | _ -> failwith("run-time error");;

(*	Verifica la tipologia di un elemento v per poi restituire il tipo corrispondente della typeset
	@params: 	v è un elemento esprimibile
	@fail:		se l'elemento v non ha un tipo presente nella typeset
	@return: 	la funzione ritorna il tipo dell'elemento v se presente nella typeset, cioè un gruppo di tipi per le set
*)
let typeof (v : evT)=
  match v with
  |Int(x) -> (Int : typeSet)
  |String(x) -> (String : typeSet)
  |Bool(x) -> Bool
  |_ -> failwith("runtime-error");;

let int_eq(x,y) =
  match (typecheck("int",x), typecheck("int",y), x, y) with
  | (true, true, Int(v), Int(w)) -> Bool(v = w)
  | (_,_,_,_) -> failwith("run-time error ");;

let int_plus(x, y) =
  match(typecheck("int",x), typecheck("int",y), x, y) with
  | (true, true, Int(v), Int(w)) -> Int(v + w)
  | (_,_,_,_) -> failwith("run-time error ");;

let int_sub(x, y) =
  match(typecheck("int",x), typecheck("int",y), x, y) with
  | (true, true, Int(v), Int(w)) -> Int(v - w)
  | (_,_,_,_) -> failwith("run-time error ");;

let int_times(x, y) =
  match(typecheck("int",x), typecheck("int",y), x, y) with
  | (true, true, Int(v), Int(w)) -> Int(v * w)
  | (_,_,_,_) -> failwith("run-time error ");;

let int_mod(x, y) =
  match(typecheck("int",x), typecheck("int",y), x, y) with
  | (true, true, Int(v), Int(w)) -> Int(v mod w)
  | (_,_,_,_) -> failwith("run-time error ");;

let bool_and(x, y) =
  match (x, y) with
  | (Bool(i), Bool(j)) -> Bool(i && j);
  | (_, _) -> failwith("nonboolean expression");;

let bool_or(x, y) =
  match (x, y) with
  | (Bool(i), Bool(j)) -> Bool(i || j);
  | (_, _) -> failwith("nonboolean expression");;

let bool_not(x) =
  match x with
  | Bool(i) -> Bool(not i);
  | _ -> failwith("nonboolean expression");;

(*
	La funzione controlla se un elemento v appartiene ad un insieme s
	@params:
				s è un insieme 
				v è un elemento che sto cercando in s
	@fail:		se sto cercando un elemento v che ha un tipo differente da quello dell'insieme
	@fail:		se s non è una set, quindi non ha una definizione valida
	@return:	ritorna un valore di verità
*)
let is_inside ((s : evT),(v : evT))= 
  match s with
  |Set([],_type_)->Bool(false)
  |Set(x::xs,_type_)->if typeof v != _type_ then failwith("tipi non corretti")
      else Bool(List.mem v (x::xs)) (*List.mem serve a controllare se v è contenuto nella lista x::xs*)
  |_ -> failwith("not a set");;

(*
	La funzione prova ad inserire un elemento v in una set 
	@params:
				set è un insieme 
				v è un elemento che sto cercando di inserire nella set
	@fail:		se la set non ha una definizione valida
	@fail:		se sto provando a inserire un elemento v che ha una tipologia diversa dall'insieme
	@fail:		se si verifica un problema a tempo di esecuzione
	@return:	ritorna un valore di verità
*)
  let push ((set : evT),(v : evT)) =
    if not (typecheck("set",set)) then failwith("not a set") else
    if is_inside(set,v)=Bool(true) then set else
      match set with
      |Set ([],_type_) -> if typeof v != _type_ then failwith("tipi non corretti")
          else Set ([v],_type_)
      |Set (l1,_type_) -> if typeof v != _type_ then failwith("tipi non corretti")
          else
            Set (v::l1,_type_)
      |_ -> failwith("runtime-error");;

(*
	La funzione restituisce la cardinalità dell'insieme
	@params:	s è un insieme
	@fail:		se s non è una set
	@return:	il numero degli elementi di s
*)
let set_lenght(s : evT)=
  match s with
  |Set(l1,_type_) -> List.length l1
  |_ -> failwith("not a set");;

(*Creazione di insiemi*)
(*
	Costruttore di un insieme vuoto
	@params:	t è il tipo dell'insieme che genero
	@return:	Set è un insieme vuoto
*)
let empty_set (t : typeSet)=Set([],t);;

(*
	Costruttore di un insieme con un elemento iniziale
	@params:	v è il primo elemento dell'insieme da cui prendo il tipo dell'insieme
	@return:	Set è un insieme con l'elemento v 
*)
let singleton (v : evT)=Set([v],typeof v);;

(*Operazioni primitive tra insiemi*)
(*
	Verifico che un insieme set1 è sottoinsieme di una set2
	@params:	
				set1 è un insieme
				set2 è un insieme
	@fail:		in caso che una delle due Set non sia un insieme valido
	@return:	ritorna un valore di verità
*)
let is_subset ((set1 : evT),(set2 : evT))=
 if set_lenght(set1)=0 then Bool(true) else
    match (set1,set2) with
    |(Set(l1,_type1_),Set([],_type2_))->Bool(false)
    |(Set(l1,_type1_),Set(l2,_type2_))->let rec aux l1 l2 ret=
                                        match l1 with
                                        |[]->ret
                                        |x::xs->if is_inside ((Set(l2,_type2_)),x)=Bool(true) then aux xs l2 (Bool(true))
                                            else Bool(false)
      in aux l1 l2 (Bool(true))
    |_ -> failwith("not a set");;

(*
	é una funzione che unisce due insiemi set1 e set2
	@params:
				set1 è un insieme
				set2 è un insieme
	@fail:		se i due insiemi hanno dei tipi diversi
	@fail:		in caso che una delle due Set non sia un insieme valido
	@return:	ritorna una nuova set che rappresenta l'unione
*)
let rec union ((set1 : evT),(set2 : evT))=
  if set_lenght(set1) > set_lenght(set2) then union(set2,set1) else
    match (set1,set2) with
    |(Set(l1,_type1_),Set(l2,_type2_)) -> if _type1_!=_type2_ then failwith("tipi non corretti")
        else (let rec aux l1 s2=
                match l1 with
                |[]->s2
                |x::xs -> aux xs (push(s2,x))
              in aux l1 (Set(l2,_type2_)))
    |(_,_)->failwith("not a set");;
	
(*
	é una funzione che interseca due insiemi set1 e set2
	@params:
				set1 è un insieme
				set2 è un insieme
	@fail:		se i due insiemi hanno dei tipi diversi
	@fail:		in caso che una delle due Set non sia un insieme valido
	@return:	ritorna una nuova set che rappresenta l'intersezione
*)
let rec intersection ((set1 : evT),(set2 : evT))=
  if set_lenght(set1) > set_lenght(set2) then intersection(set2,set1) else
    match (set1,set2) with
    |(Set(l1,_type1_),Set(l2,_type2_))-> if _type1_!=_type2_ then failwith("tipi non corretti") else
          let rec aux l1 l2 ret=
            match l1 with
            |[]->Set(ret,_type1_)
            |x::xs -> if is_inside ((Set(l2,_type1_)),x)=Bool(true) then aux xs l2 (x::ret)
					  else aux xs l2 ret
          in aux l1 l2 []
    |(_,_) -> failwith("not a set");;

(*
	é una funzione che crea la differenza di due insiemi set1 e set2
	@params:
				set1 è un insieme
				set2 è un insieme
	@fail:		se i due insiemi hanno dei tipi diversi
	@fail:		in caso che una delle due Set non sia un insieme valido
	@return:	ritorna una nuova set 
*)
let rec difference ((set1 : evT),(set2 : evT))=
  match (set1,set2) with
  |(Set([],t1),set)->empty_set t1
  |(set,Set([],t2))->set
  |(Set(l1,_type1_),Set(l2,_type2_))-> if _type1_!=_type2_ then failwith("tipi non corretti") else
        let rec aux l1 l2 ret=
          match l1 with
          |[]->empty_set _type1_
          |[x]->if is_inside ((Set(l2,_type1_)),x)=Bool(false) then Set(x::ret,_type1_)
              else Set(ret,_type1_)
          |x::xs -> if is_inside ((Set(l2,_type1_)),x)=Bool(false) then aux xs l2 (x::ret)
              else aux xs l2 ret
        in aux l1 l2 []
  |(_,_) -> failwith("not a set");;

(*Operazioni aggiuntive*)
(*
	é una funzione rimuove un elemento da una set
	@params:
				set è un insieme
				v è un elemento
	@fail:		in caso che la Set non sia un insieme valido
	@return:	ritorna una nuova set senza l'elemento v
*)
let remove_from ((set : evT),(v : evT))=
  let rec aux set v (set1_temp : evT)=
    match set with
    |Set([],_type_)->set1_temp
    |Set(x::xs,_type_)->let check=_type_=(typeof v)
        in if check && x=v then aux (Set(xs,_type_)) v set1_temp
        else aux (Set(xs,_type_)) v (push (set1_temp,x))
    |_ -> failwith("not a set")
  in aux set v (Set([],typeof v));;

(*
	é una funzione verifica se un insieme è vuoto
	@params:
				s è un insieme
	@fail:		in caso che la Set non sia un insieme valido
	@return:	ritorna un valore di verità
*)
let is_empty ((s : evT))=
  match s with
  |Set([],_type_)->Bool(true)
  |Set(l1,_type_)->Bool(false)
  |_ -> failwith("not a set");;

(*
	é una funzione cerca il valore più grande
	@params:	s è un insieme
	@fail:		se ho una set di tipo diverso da Int
	@fail:		in caso che la Set non sia un insieme valido
	@return:	ritorna un elemento di s
*)
let maxOf ((s : evT)) = match s with
  Set([],_type_) -> if _type_ != Int then failwith("tipi non corretti") else Unbound
  | Set(list,_type_) -> if _type_ != Int then failwith("tipi non corretti") else let rec aux l max b = match l with
        [] -> max
        | x::xs -> if b = false then aux xs x true else if x > max then aux xs x b else aux xs max b
  in aux list (Unbound) false 						(*il primo valore viene preso alla prima iterazione*)
  |_ -> failwith("not a set");;

(*
	é una funzione cerca il valore più piccolo
	@params:	s è un insieme
	@fail:		se ho una set di tipo diverso da Int
	@fail:		in caso che la Set non sia un insieme valido
	@return:	ritorna un elemento di s
*)
let minOf ((s : evT)) = match s with
  Set([],_type_) -> if _type_ != Int then failwith("tipi non corretti") else Unbound
  | Set(list,_type_) -> if _type_ != Int then failwith("tipi non corretti") else let rec aux l min b = match l with
  [] -> min
  | x::xs -> if b = false then aux xs x true else if x < min then aux xs x b else aux xs min b
  in aux list (Unbound) false 						(*il primo valore viene preso alla prima iterazione*)
  |_ -> failwith("not a set");;

(*interprete*)
let rec eval  (e:exp) (s:evT env) : evT = match e with
  | CstInt(n) -> Int(n)
  | CstTrue -> Bool(true)
  | CstFalse -> Bool(false)
  | CstString(s) -> String(s)
  | Empty(i) -> empty_set i
  | Singleton(v) -> singleton (eval v s)
  | Of(t, values) -> let rec getSetFrom v s1= 									(*v è una lista di espressioni*)
					(*
					  v è una Value (lista di espressioni)
                      s1 è una set (alla prima iterazione è vuota)
                      primo pattern matching: x è una espressione, e non abbiamo altre cose da valutare
                      secondo pattern matching: x è una espressione, ma ho altre cose da valutare: quindi metto x (valutato)
                                                nella set e richiamo la getSetFrom ricorsiva sui Value (lista di espressioni) che mi mancano
                      terzo pattern matching: se non ho più niente restituisco la set che ho formato
					*)
                        match v with
                       |Value(x,Empty) -> if typeof (eval x s) != t then failwith("tipi non corretti")
                           else (push(s1,(eval x s)))
                       |Value(x,Value(y,z))->if typeof (eval x s) != t then failwith("tipi non corretti")
                           else getSetFrom (Value(y,z)) (push(s1,(eval x s)))
                       |Empty -> s1
      in getSetFrom values (empty_set(t))
  | Union(e1, e2) -> union((eval e1 s),(eval e2 s))
  | Intersection(e1, e2) -> intersection((eval e1 s),(eval e2 s))
  | Difference(e1, e2) -> difference((eval e1 s),(eval e2 s))
  | Push(e1, e2) -> push((eval e1 s),(eval e2 s))
  | RemoveFrom(e1, e2) -> remove_from((eval e1 s), (eval e2 s))
  | IsEmpty(e) -> is_empty((eval e s))
  | Contains(e1, e2) -> is_inside((eval e1 s), (eval e2 s))
  | IsSubset(e1, e2) -> is_subset((eval e1 s), (eval e2 s))
  | MaxOf(e) -> maxOf((eval e s))
  | MinOf(e) -> minOf((eval e s))
  | For_all(f, e) -> for_all((eval f s), (eval e s))
  | Exists(f, e) -> exist((eval f s), (eval e s))
  | Filter(f, e) -> filter((eval f s), (eval e s))
  | Map(f, e) -> map((eval f s), (eval e s))
  | Eq(e1, e2) -> int_eq((eval e1 s), (eval e2 s))
  | And(e1, e2) -> bool_and((eval e1 s), (eval e2 s))
  | Or(e1, e2) -> bool_or((eval e1 s), (eval e2 s))
  | Not(e) -> bool_not(eval e s)
  | Times(e1,e2) -> int_times((eval e1 s), (eval e2 s))
  | Sum(e1, e2) -> int_plus((eval e1 s), (eval e2 s))
  | Sub(e1, e2) -> int_sub((eval e1 s), (eval e2 s))
  | Mod(e1, e2) -> int_mod((eval e1 s), (eval e2 s))
  | Ifthenelse(e1,e2,e3) -> let g = eval e1 s in
		(*
		il primo bool è per vedere se la guardia è un booleano
            il secondo bool è la valutazione della guardia nell'ambiente
            il terzo bool è per vedere se i risultati restituiti sono dello stesso tipo (cioè getFunType verifica se valutando e2 ed e3 mi viene restituito lo stesso tipo)
		*)

      (match (typecheck("bool", g), g, getFunType(e2)=getFunType(e3)) with
       | (true, Bool(true), true) -> eval e2 s
       | (true, Bool(false), true) -> eval e3 s
       | (_, _, _) -> failwith ("nonboolean guard"))
  | Den(i) -> lookup s i
  | Let(i, e, ebody) -> eval ebody (bind s i (eval e s))
  | Fun(arg, ebody) -> Closure(arg,ebody,s)
  | Letrec(f, arg, fBody, letBody) ->
      let benv = bind (s) (f) (RecClosure(f, arg, fBody,s)) in eval letBody benv
  | Apply(eF, eArg) ->
      let fclosure = eval eF s in
      (match fclosure with
       | Closure(arg, fbody, fDecEnv) ->
           let aVal = eval eArg s in
           let aenv = bind fDecEnv arg aVal in
           eval fbody aenv
       | RecClosure(f, arg, fbody, fDecEnv) ->
           let aVal = eval eArg s in
           let rEnv = bind fDecEnv f fclosure in
           let aenv = bind rEnv arg aVal in
           eval fbody aenv
       | _ -> failwith("non functional value"))

(*
	é una funzione che verifica un predicato per tutti gli elementi dell'insieme
	@params:
				set è un insieme
				pred è un predicato
	@fail:		se pred non ha una definizione valida
	@fail:		se si verifica un errore a tempo di esecuzione
	@return:	ritorna un valore di verità
*)
and for_all (pred,set)=
  match (pred,set) with
  |(Closure(func_var,fbody,env),Set(l1,_type_)) ->let rec aux(var,body, env, l1, ret)= 		
                                                    match l1 with
                                                    |[]->ret
                                                    | x::xs -> let varEnv= bind env var x
																in let ret=eval fbody varEnv
																	in if ret=Bool(true)
																		then aux (var,body,env,xs, ret)
																	   else
																		if ret=Bool(false) then ret
																		else failwith("not a predicate")
      in aux (func_var,fbody,emptyEnv,l1,(Bool(false)))
  |(_,_) -> failwith("runtime-error")

(*
	é una funzione si ferma quando un elemento verifica il predicato
	@params:
				set è un insieme
				pred è un predicato
	@fail:		se pred non ha una definizione valida
	@fail:		se si verifica un errore a tempo di esecuzione
	@return:	ritorna un valore di verità
*)
and exist (pred,set)=
  match (pred,set) with
  |(Closure(func_var,fbody,env),Set(l1,_type_)) ->let rec aux(var,body, env, l1, ret)=
                                                    match l1 with
                                                    |[]->ret
                                                    | x::xs -> let varEnv= bind env var x
																			in let ret=eval fbody varEnv
																				in if ret=Bool(false)
																					then aux (var,body,env,xs, ret)
																				   else
																					if ret=Bool(true) then ret
																					else failwith("not a predicate")
      in aux (func_var,fbody,emptyEnv,l1,(Bool(false)))
  |(_,_) -> failwith("runtime-error")

(*
	é una funzione che verifica un predicato per tutti gli elementi dell'insieme e crea una set con gli elementi che verificano il predicato
	@params:
				set è un insieme
				pred è un predicato
	@fail:		se pred non ha una definizione valida
	@fail:		se si verifica un errore a tempo di esecuzione
	@return:	ritorna una set con i valori che verificavano il predicato
*)
and filter (pred,set)=
  match (pred,set) with
  |(Closure(func_var,fbody,env),Set(l1,_type_)) ->let rec aux(var,body, env, l1, ret_set, _type_)=
                                                    match l1 with
                                                    |[]-> Set(ret_set,_type_)
                                                    | x::xs -> let varEnv= bind env var x
																			in let ret=eval fbody varEnv
																			in if ret=Bool(true) then aux (var,body,env,xs, (x::ret_set),_type_)
																			   else
																				if ret=Bool(false) then aux (var,body,env,xs, ret_set,_type_)
																				else failwith("not a predicate")
      in aux (func_var,fbody,emptyEnv,l1,[],_type_)
  |(_,_) -> failwith("runtime-error")

(*
	é una funzione che applica una funzione agli elementi di una set
	@params:
				set è un insieme
				func è una funzione
	@fail:		se si verifica un errore a tempo di esecuzione
	@return:	ritorna una set
*)
and map (func,set)=
  match (func,set) with
  |(Closure(func_var,fbody,env),Set(l1,_type_)) ->let rec aux(var,body, env, l1, ret_set,_type_)=
                                                    match l1 with
                                                    |[]->ret_set
                                                    | x::xs -> let varEnv= bind env var x
                                                        in aux (var,body,env,xs, (push(ret_set,(eval fbody varEnv))),_type_)
      in aux (func_var,fbody,emptyEnv,l1,empty_set(getFunType(fbody)),_type_)
  |(_,_) -> failwith("runtime-error");;

(*TEST-CASE*)

let myEnv = emptyEnv;;

(*Test Singleton*)
let set1=Singleton(CstInt(3));;(*{ 3 }*)
let set1=Singleton(CstString("ciao"));;
let set1=Singleton(CstTrue);;

eval set1 myEnv;;

(*Test Of*)
(*Of è una funzione che crea un insieme prendendo in input un tipo t e un insieme di valori dello stesso tipo*)
let set2=Of(Int,Value(CstInt(3),Value(CstInt(1),Value(CstInt(4),Empty))));;(*{3, 1, 4}*)
let set2=Of(String,Value(CstString("a"),Value(CstString("ciao"),Value(CstString("b"),Empty))));;(*{a , ciao, b}*)
let set2=Of(Bool,Value(CstTrue,Value(CstTrue,Value(CstFalse,Empty))));;(*{True, True, False}*)
let set2=Of(Int,Value(CstInt(3),Value(CstInt(1),Value(CstString("ciao"),Empty))));;(*"tipi non corretti"*)

(*nella valutazione dell'ultima definizione della set2 verifica che i tipi non siano correti dandomi come messaggio di sistema "tipi non corretti"*)
try eval set2 myEnv												
with Failure(msg) -> Printf.printf "hai un problema %s\n" msg;Set([],Int);;

let emptyset=Empty(String);; (*{empty}*)

(*Test Union*)
let set1=Singleton(CstInt(3));; (*{ 3 }*)
let set2=Of(Int,Value(CstInt(1),Value(CstInt(4),Empty)));; (*{1, 4}*)
let union=Union(set1,set2);; (*{3, 4, 1}*)
eval union myEnv;;

let set1=Singleton(CstString("ciao"));;
let set2=Of(String,Value(CstString("a"),Value(CstString("b"),Empty)));; (*{a , b}*)
let union=Union(set1,set2);; (*{a , ciao, b}*)
eval union myEnv;;

let set1=Of(Int,Value(CstInt(1),Value(CstInt(4),Empty)));; (*{1, 4}*)
let set2=Of(String,Value(CstString("a"),Value(CstString("b"),Empty)));; (*{a , b}*)
let union=Union(set1,set2);; (*"tipi non corretti"*)
try eval union myEnv
with Failure(msg) -> Printf.printf "hai un problema %s\n" msg;Set([],Int);;

(*Test Intersection*)
let set1=Singleton(CstInt(4));; (*{ 4 }*)
let set2=Of(Int,Value(CstInt(1),Value(CstInt(4),Empty)));; (*{1, 4}*)
let inters=Intersection(set1,set2);; (*{4}*)
eval inters myEnv;;

let set1=Singleton(CstString("ciao"));;
let set2=Of(String,Value(CstString("a"),Value(CstString("ciao"),Empty)));; (*{a , ciao}*)
let inters=Intersection(set1,set2);; (*{ciao}*)
eval inters myEnv;;

let set1=Of(Int,Value(CstInt(1),Value(CstInt(4),Empty)));; (*{1, 4}*)
let set2=Of(String,Value(CstString("a"),Value(CstString("b"),Empty)));; (*{a , b}*)
let inters=Intersection(set1,set2);; (*"tipi non corretti"*)
try eval inters myEnv
with Failure(msg) -> Printf.printf "hai un problema %s\n" msg;Set([],Int);;

(*Test IsSubset*)
let set1=Singleton(CstInt(4));; (*{ 4 }*)
let set2=Of(Int,Value(CstInt(1),Value(CstInt(4),Empty)));; (*{1, 4}*)
let sub=IsSubset(set1,set2);; (*True*)
eval sub myEnv;;

let set1=Singleton(CstString("ciao"));;
let set2=Of(String,Value(CstString("a"),Value(CstString("ciao"),Empty)));; (*{a , ciao}*)
let sub=IsSubset(set1,set2);; (*True*)
eval sub myEnv;;

let set1=Of(Int,Value(CstInt(1),Value(CstInt(4),Empty)));; (*{1, 4}*)
let set2=Of(String,Value(CstString("a"),Value(CstString("b"),Empty)));; (*{a , b}*)
let sub=IsSubset(set1,set2);; (*"tipi non corretti"*)
try eval sub myEnv
with Failure(msg) -> Printf.printf "hai un problema %s\n" msg;Set([],Int);;

(*Test Push*)
let set1=Singleton(CstInt(3));; (*3*)
let set1=Push(set1,CstInt(4));; (*3, 4*)
let set1=Push(set1,CstInt(7));; (*3, 4, 7*)
let set1=Push(set1,CstInt(5));; (*3, 4, 7, 5*)
eval set1 myEnv;;

let set2=Singleton(CstString("a"));; (*{a}*)
let set2=Push(set2,CstString("ciao"));; (*{a, ciao}*)
let set2=Push(set2,CstString("mondo"));; (*{a, ciao,mondo}*)
eval set2 myEnv;;

let set2=Push(set2,CstInt(1));; (*"tipi non corretti"*)
try eval set2 myEnv
with Failure(msg) -> Printf.printf "hai un problema %s\n" msg;Set([],Int);;

(*Test RemoveFrom*)
let set1=RemoveFrom(set1,CstInt(7));;(*3, 4, 5*)
eval set1 myEnv;;

let set3=Singleton(CstString("a"));; (*{a}*)
let set3=Push(set3,CstString("ciao"));; (*{a, ciao}*)
let set3=Push(set3,CstString("mondo"));; (*{a, ciao,mondo}*)
let set3=RemoveFrom(set3,CstString("ciao"));; (*{a,mondo}*)
eval set3 myEnv;;

let ismaybeEmpty=IsEmpty(emptyset);;
eval ismaybeEmpty myEnv;;

(*Test Contains*)
let cont=Contains(set1,CstInt(7));; (*False*)
eval cont myEnv;;

let cont=Contains(set3,CstString("a"));; (*True*)
eval cont myEnv;;

(*Test MaxOf/MinOf*)
let max=MaxOf(set1);; (*3, 4, 5* -> max = 5*)
let min=MinOf(set1);; (*3, 4, 5* -> min = 3*)
eval max myEnv;;
eval min myEnv;;

let set4=Of(Int,Value(CstInt(1),Value(CstInt(4),Value(CstInt(3),Value(CstInt(2),Empty)))));; (*{4, 1, 3, 2}*)
let pred=Fun("x",Ifthenelse(Eq(Den("x"),CstInt(2)),CstTrue,CstFalse));; (*predicato x=2*)

(*Test For_all*)
let forall=For_all(pred,set4);; (*False*)
eval forall myEnv;;

(*Test Exists*)
let exists=Exists(pred,set4);; 
eval exists myEnv;; (*True*)

(*Test Filter*)
let filter=Filter(pred,set4);; 
eval filter myEnv;; (*{2}*)

(*Test Map*)
let map=Map(pred,set4);; 
eval map myEnv;; (*{False,True}*)
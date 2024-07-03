![[Pasted image 20240610090600.png]]

# Thread

Un Thread présente les méthodes suivantes :
- start : débute l'exécution du code de la méthode `run()`
- sleep : met en pause le thread
- join : patiente jusqu'à ce que son exécution soit terminée (rejoint le thread actuel)

On crée un thread avec une nouvelle classe héritant de Thread :
```java
public class MyThread extends Thread {
    public void run() {
        System.out.println("START");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("STOP");
    }
}
```

On instancie puis démarre ensuite ce thread
```java
public class Main {
    public static void main(String[] args) {

        var t = new MyThread();
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("End");
    }
}
```

Quel sera l'affichage console ?

Note : on ne peut pas arrêter directement un thread avec une méthode `.stop()`.
A la place mieux vaut créer une variable représentant l'état du thread (actif/inactif) et faire en sorte que le thread la consulte régulièrement.
Au besoin, le thread va alors terminer son exécution (simple `return` par exemple)


Autre syntaxe, au lieu de créer une classe on peut directement instancier un Thread avec un lambda, celui-ci sera utilisé comme
corps de la méthode run :

```java
public class Main {
    public static void main(String[] args) {

        var t = new Thread(() -> {...});
        t.start();

        ...
    }
}
```

# Variables propres aux threads

Il est souvent utile de partager des variables au sein d'un thread mais de faire en sorte que ces variables ne sont pas partagées avec les autres threads.
Par exemple, si nous créons un pool de threads pour nos connexions SQL (1 thread par connexion), chaque thread va avoir besoin de sa propre connexion.
Une instance de ThreadLocal est une variable qui est disponible dans tous les threads enfants, mais dont la valeur est propre au thread.
C'est donc un candidat idéal pour y placer une instance de connexion SQL par exemple.

```java
public class Main {

    public static ThreadLocal<String> CONTEXT = ThreadLocal.withInitial(() -> "OUTSIDE");
    
    public static void main(String[] args) {

        Thread parentThread = new Thread(() -> {
            CONTEXT.set("INSIDE");
            System.out.println(CONTEXT.get());
        });
        parentThread.start();
        parentThread.join();
        System.out.println(CONTEXT.get());
    }
}
```

Si chaque thread possède une version spécifique de ThreadLocal, quel sera l'affichage console pour cet exemple ?

Note : attention, cela veut aussi dire que pour chaque thread créé à partir du thread principal,
toutes les variables ThreadLocal seront "dupliquées" en mémoire. Le coût d'instanciation d'un Thread augmente donc avec le volume de ThreadLocal

Les ThreadLocal ne sont accessible qu'aux threads enfants.
Si ces threads instancient eux-même des threads, ils n'auront pas accès aux ThreadLocal.

InheritableThreadLocal est une alternative qui règle ce problème.

# Runnable et Executor

Créer des classes pour chaque nouveau thread est trop verbeux. Il existe d'autres approches plsu pratiques.

L'interface fonctionnelle Runnable représente une opération qui ne retroune pas de résultat, avec une méthode `run()`

Elle permet de déclarer un block exécutable comme argument :
```java
public class MaClasse {
	void maMethode(Runnable command) {
		command.run();
	}
}

...

public static void main(String[] args) {  
    MaClass maClasse = new MaCalsse();
    maClasse.maMethode(() -> { ... })
}

```

Executor est une interface fonctionnelle qui sert à centraliser la façon d'exécuter un Runnable.

Prenons l'exemple suivant :

```java

public static void main(String[] args) {  
    System.out.println("BEGIN");  
    Executor executor = new Invoker();  
    executor.execute(() -> {  
        try {  
            Thread.sleep(5000);  
            System.out.println(1);  
        } catch (InterruptedException e) {  
            throw new RuntimeException(e);  
        }  
    });  
  
    executor.execute(() -> {  
        System.out.println(2);  
    });  
    System.out.println("END");  
}

```

Et créons un Executor qui se contente d'exécuter chaque Runnable :

```java
public class Invoker implements Executor {  
    public void execute(Runnable r) {  
        r.run();  
    }  
}
```

Quel sera l'affichage console ?

Réponse :
`BEGIN 1 2 END`

# Exemple de Multithreading

Notre Invoker va désormais démarrer un nouveau Thread pour chaque Runnable :

```java
public class Invoker implements Executor {  
    public void execute(Runnable r) {  
        new Thread(r).start();  
    }  
}
```

Quel sera le résultat console ?

Réponse :
`BEGIN END 2 1`

# Fixer le nombre de Threads

Créer systématiquement de nouveaux threads peut avoir un effet délétère. 
RAPPEL : les threads ne sont pas exécutés en parallèle, le CPU alloue alternativement du temps à chaque thread. L'existence d'un thread 

Créer un thread nest considéré comme "coûteux". A chaque nouveau thread :
- un large block mémoire est alloué et initialisé (thread stack)
- des appels système sont réalisés pour créer/enregistrer le thread au niveau du système d'exploitation (coût fonction de l'OS)
- des descripteurs doivent être créés, initialisés et ajoutés aux structures internes de la JVM

Par exemple si nous planifions plusieurs milliers d'opérations, il peut être plus intérresant de créer une queue (liste des opérations à traiter dans un ordre précis) et quelques threads qui vont consommer cette queue. On a alors un "thread pool" qui contient des threads qui seront réutilisés plutôt que systématiquement supprimés et recréés.

Pour cela on peut utiliser un ExecutorService

Exemple (avec 1 seul thread **supplémentaire** pour l'instant) : 

```java
public class Count {  
  
    public int val = 0;  
  
    public void addOne() {  
        val++;  
    }  
}
```

```java
  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
  
public class Main {  
    public static void main(String[] args) {  
        ExecutorService service = Executors.newSingleThreadExecutor();  
        Count count = new Count();  
  
        for (int i = 0; i < 1000; i++) {  
            service.execute(count::addOne);  
        }  
        
        System.out.println(count.val);  
    }  
}
```

2 problèmes :
- La somme ne fait pas 1000 car on affiche le total sans même avoir attendu la fin d'exécution des 1000 opérations
- le programme ne se termine jamais car on n'a pas arrêté les threads ouverts

```java
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.TimeUnit;  
  
public class Main {  
    public static void main(String[] args) {  
        ExecutorService service = Executors.newSingleThreadExecutor();  
        Count count = new Count();  
  
        for (int i = 0; i < 1000; i++) {  
            service.execute(count::addOne);  
        }  
  
        try {  
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);  
        } catch (InterruptedException e) {  
            throw new RuntimeException(e);  
        }  
  
        System.out.println(count.val);  
        service.shutdown();  
    }  
}
```
Désormais on autorise un maximum de 1 sec d'exécution du thread avant de passer à la suite. Mais cela ne clôture pas pour autant le thread, c'est shutdown qui y met fin.

Essayons maintenant avec 10 threads

```java
ExecutorService service = Executors.newFixedThreadPool(10);
```

Pourquoi n'obtient-on pas le bon résultat ?

Nos 10 threads peuvent déclencher de façon simultanée la méthode `addOne`. Il en résulte que les opérations se téléscopent. On parle de "race condition".
Ex : les threads A et B débutent en simultané les opérations 501 et 502. Au moment d'exécuter `addOne`, `val` vaut 500 pour les 2 threads, il en résulte que à l'issue des 2 oéprations, `val` ne vaudra que 501

## Thread-safe

On dit qu'un code est thread-safe si plusieurs threads peuvent accéder à une même ressource sans risque de comportement erroné et de résultat imprédictible.

Ici la ressource qui n'est pas thread-safe est notre variable `val` de la classe `Count` car elle est accédée et modifiée par plusieurs threads à la fois.

Une solution simple pour rendre cette variable thread-safe est de la déclarée comme synchronisée avec le mot clé `synchronized`. Cela aura pour effet d'empêcher 2 threads d'accéder en même temps à `val`. Nous aurions également pu déclarer la méthode `addOne` comme `synchronized`. Une variable ou une méthode `synchronized` présente alors un `lock` dès qu'un thread s'en empare et ce `lock` est levé lorsque le thread le libère.

Quel problème va induire `synchronized` dans notre code ?

Nos threads vont devoir attendre leur tour pour exécuter `addOne`. Il n'y a donc plus aucun intérêt au multithreading dans notre exemple (voire une pénaité pour la création de ces 10 threads).

`synchronized` peut être utile dans un scénario où :
- la majorité de l'opération réalisée par le thread n'implique pas de variable "partagée"
- il n'y a que quelques variables qui nécessitent d'être lues ou alimentées de façon synchronisée
Ex : nos thread effectuent un calcul complexe puis stockent à la fin cette information dans une variable synchronisée. Seule l'écriture dans cette variable pourra être bloquante pour les autres threads.

Note : il existe aussi le mot clé `volatile` qui n'induit pas un `lock` des threads mais qui indique plutôt qu'un thread ne devrait pas mettre en cache cette variable car elle est susceptible d'être modifiée par plusieurs threads. C'est donc une "synchronisation implicite" de la variable entre nos threads. Peu de cas où cela est utile

Comment pourrait-on repenser notre code afin de rendre ce calcul concurrentiel ? Ex : avec 10 thread sans que ceux-ci soient obligés d'attendre que `val` soit accessible.

La solution peut consister à demander aux 10 threads de réaliser 100 itérations chacun puis de faire la somme des résultats des 10 threads. Cette approche s'appelle map-reduce : 
- entrée : 10 lots de 100 itérations
- map : pour chaque lot, exécution des 10 itérations par 1 thread = 10 threads
- reduce : agrégation des 10 résultats en 1 seul via l'opération somme

Cette approche a l'avantage de rendre notre code totalement thread-safe sans induire de lock potentiellement bloquant (= latence)

Mais pour y parvenir, il faut pouvoir sortir un résultat d'un thread

# Récupérer le résultat d'un Runnable

La méthode `execute()` ne permet pas d'obtenir le résultat d'un `Runnable`. Jusque là notre Runnable a soit écrit en console, soit enregsitré le résultat dans une mémoire partagée.
On peut aussi récupérer le résultat via un `Future`
`Future` est une classe générique qui indique qu'il renfermera dans un certain futur un résultat

Pour créer un `Future` on utilisera `submit()` au lieu de `execute()`
`Future` dispose de la méthode  `isDone()` qui retourne un booléen selon s'il a achevé son exécution.
On utilisera sa méthode `get()` pour récupérer le résultat. L'appel à `get` est bloquant, c'est-à-dire que l'exécution du code ne se poursuivra que lorsque le résultat sera disponible (lorsque `isDone()` renvoie `true`)


```java
import java.util.concurrent.ExecutionException;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.Future;  
  
public class Main {  
    public static void main(String[] args) {  
        System.out.println("BEGIN");  
  
  
        ExecutorService service = Executors.newSingleThreadExecutor();  
        Future<String> result = service.submit(() -> {  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException ex) {  
                throw new RuntimeException(ex);  
            }  
  
            return "OK";  
        });  
  
  
        try {  
	        System.out.println(result.isDone());  //A
            String str = result.get();  
            System.out.println(result.isDone());  //B
            System.out.println(str);  
        } catch (InterruptedException | ExecutionException e) {  
            throw new RuntimeException(e);  
        }  
  
  
        System.out.println("END");  
  
        service.shutdown();  
    }  
}
```

Question : qu'affichent les lignes A et B ?

Réponse : A renvoie `false` mais de façon non reproductible (si pour une raison quelconque notre machine met plus d'1 seconde à atteindre cette ligne, nous aurons `true`)
B renvoie forcément `true` car `get()` implique d'attendre la disponibilité du résultat

On peut également préciser un timeout à `get` (cela laisse la possibilité de traiter le timeout dans le catch):

```java
try {  
    String str = result.get(10, TimeUnit.SECONDS);  
    System.out.println(str);  
} catch (InterruptedException | ExecutionException | TimeoutException e) {  
    throw new RuntimeException(e);  
}
```

# Exercice 01

Modifiez le code précédent (1000 itérations) afin que celui-ci s'exécute via 2 threads (chacun 500 itérations). Le résultat final (total = 1000) doit être issu de la somme des résultats des 2 threads

# Exercice 1

Vous souhaitez faire appel à 3 API web dans une méthode et attendre d'obtenir les 3 résultats avant de retourner une valeur.

Modifier le code dans `MaClasse` afin de réduire le temps d'exécution tout en conservant le bon résultat



# Le futur de `Future`

`Future` a été introduit par Java 5. Des améliorations ont été apportées par Java 8 avec `CompletableFuture`.

L'équivalent de ceci avec `Future`
```java
ExecutorService service = Executors.newSingleThreadExecutor();
Future<String> result = service.submit(() -> {  
    try {  
        Thread.sleep(1000);  
    } catch (InterruptedException ex) {  
        throw new RuntimeException(ex);  
    }  
  
    return "OK";  
});

```

Peut s'écrire comme ceci avec `CompletableFuture`
```java
ExecutorService service = Executors.newSingleThreadExecutor();
CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {  
    try {  
        Thread.sleep(1000);  
    } catch (InterruptedException ex) {  
        throw new RuntimeException(ex);  
    }  
  
    return "OK";  
}, service);
```

Note : `CompletableFuture.supplyAsync()` admet une interface fonctionnelle `Supplier` et non plus un `Runnable`. Les 2 étant des interfaces foncitonnelles, cela ne change en rien la syntaxe si on utilise un lambda.

`CompletableFuture` permet des opérations supplémentaires :

Le chaînage des opérations
```java
result.thenApply(res -> {  
    return "["+res+"]";  
});

// ou de façon asynchrone :

result.thenApplyAsync(res -> {  
    return "["+res+"]";  
});
```
Attendre que toutes les opérations soient terminées, ou qu'au moins l'une d'entre elles le soient :

```java
CompletableFuture.allOf(f1, f2, f3).join()  
CompletableFuture.anyOf(f1, f2, f3).join()
```

# Exercice 2

Nos 3 web services sont finalement considérés comme redondants. On considère que les 3 retournent la même valeur et on souhaite les mettre en concurrence pour récupérer au plus vite l'information. Dès que l'un d'entre eux a répondu, on souhaite afficher cette valeur et ne pas attendre la réponse des 2 autres.

Faites les modifications nécessaires au code précédent.

# Exercice 3

La création d'un thread est coûteuse (plusieurs Mo de RAM) et est limitée en nombre. 
Certains langages ont réduit ce problème en réinventant la notion de thread à l'échelle logicielle.
Kotlin a par exemple introduit les Coroutines.
Java 19 puis 21 amène les threads virtuels. Leur coût est réduit à quelques octets de RAM et le nombre maximum de threads virtuels n'est pas limité. 
A la différence des threads systèmes, ceux-ci n'impliquent pas d'appels coûteux au système.
C'est la JVM elle-même qui va gérer l'allocation CPU aux différents threads virtuels.

Un thread virtuel s'instancie de la façon suivante :

```java
var myThread = Thread.ofVirtual().start(() -> ...)
```

On peut ensuite traiter ce thread virtuel comme un thread système avec `sleep`, `join`, etc.

Pour plus de facilité, on peut également faire appel à un Executor comme pour nos threads système :

```java
var executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> ...)
```

Rédigez un programme créant 1000 threads système.
Ce thread doit avoir pour seul effet de patienter 1 seconde puis de s'arrêter.
Vous êtes libre de choisir la taille du thread pool.
Cherchez le meilleur compromis pour réduire la durée d'exécution du programme.
Essayez avec 1000, 10000, 100000, etc threads --> quelle est la limite de votre système ?

Essayez ensuite avec des threads virtuels. Cette limite est-elle différente ?

Note : on ne crée pas de pool avec des threads virtuels. Ce n'est pas utile car très peu coûteux à instancier.

# Exercice 4

Lorsque nous démarrons plusieurs threads, il se peut que l'un d'eux rencontre une erreur.
Un try catch pourra permettre d'intercepter cette erreur, mais que faisons-nous des autres threads encore en cours qui ne sont pas en erreur ?
En l'absence d'action ils peuvent être "perdus" (thread leak). Les conséquences sont d'autant plus lourdes s'il s'agit de threads système

Une solution peut consister à mettre en place des timeout sur nos threads, mais peu pratique.
Depuis Java 21 il existe un preview un solution pour structurer nos threads au sein de scopes : StructuredTaskScope
Ainsi les threads au sein d'un scope peuvent tous être clôturés à la sortie d'un bloc de code pour éviter tout thread leak.

Note : StructuredTaskScope crée des threads virtuels

Pour bénéficier des fonctionnalités en preview sous IntelliJ :
- "Settings" > "Build, Execution, Deployment" > "Compiler" > "Java Compiler"
- "Target bytecode version" -> "21"
- "Override compiler parameters per module" > "Compilation options" -> "--enable-preview" ou "--release 21 --enable-preview"

Création d'un scope :

```java
try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    // ...code ici
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
```

ShutdownOnFailure : arrête tous les threads s'il y a uen erreur, sinon attend qu'ils aient tous fini
ShutdownOnSuccess : arrête tous les threads s'il y a un succès = stop au 1er thread terminé

Création d'une tache (thread) au sein du scope :

```java
Subtask<T> myTask = scope.fork(() -> doSomething(data));
Subtask<T> otherTask = scope.fork(() -> doSomethingElse(data));
```

Résolution du scope

```java
scope.join();
```

Récupérer les données des différentes taches :

```java
myTask.get();
othertask.get();
```

Faire exercice sur ce point

## Scoped values

IMPORTANT : il s'agit d'une feature déclarée comme PREVIEW dans Java 21, donc susceptible d'e^tre retirée de Java à l'avenir.

Nous avons vu le type ThreadLocal qui permettait d'avoir 1 version d'une variable par thread système.
Cette approche ne fonctionnera pas avec les threads virtuels, puisqu'ils tournent au sein d'un même thread.

```java
public static ScopedValue<String> CONTEXT = ScopedValue.newInstance();

public static void main(String[] args) throws InterruptedException {
    ScopedValue.runWhere(CONTEXT, "Test Value", () -> {

        System.out.println(CONTEXT.get());

        ScopedValue.runWhere(CONTEXT, "Changed Value", () -> {
            System.out.println(CONTEXT.get());
        });

        System.out.println(CONTEXT.get());
    });
}
```

Les scopes déclarés dans les blocs "runWhere" auront ainsi tous les même version de la ScopedValue :

```java
public static ScopedValue<String> CONTEXT = ScopedValue.newInstance();

public static void main(String[] args) throws InterruptedException {
    ScopedValue.runWhere(CONTEXT, "Test Value", () -> {

        System.out.println(CONTEXT.get());

        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                System.out.println("From SCOPE: "+CONTEXT.get());
                return null;
            });
            scope.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ScopedValue.runWhere(CONTEXT, "Changed Value", () -> {
            System.out.println(CONTEXT.get());
        });

        System.out.println(CONTEXT.get());
    });
}
```

Avantage : par rapport au ThreadLocal, il n'y a pas de duplication de l'information entre les threads

Les ScopedValue ne fonctionnent que pour les threads virtuels

La JVM répartit les éléments en mémoire dans 2 espaces distincts :
- Stack
- Heap

## Stack

La Stack (pile) représente la pile d'exécution de notre code.
En cas d'erreur, la `StackTrace` affichée représente l'état de cette pile.

Par exemple si nous exécutons  :
```java
public static void main(String[] args) {
	var obj = new MaClasse();
	obj.methodA()
}

...

class MaClassse {
	private void methodA() {
		this.methodB();
	}
	
	private void methodB() {
		this.methodC();
	}
	
	private void methodC() {
		...
	}
}
```
- un espace mémoire est créé dans la stack pour l'exécution de main
- puis un espace mémoire est ajouté pour l'exécution de methodA
- puis pour methodB
- et enfin methodC

Lorsque l'exécution de methodC se termine, on désalloue son espace mémoire et on redonne la main à la méthode B, et ainsi de suite.
C'est donc le dernier espace créé dans la stack qui est le premier à être évacué :
Last-In-First-Out -> LIFO

Note : par opposition au mécanisme FIFO (Firs-in-First-Out)

Ce mécanisme assure que les variables allouées dans une méthode sont automatiquement désallouées lorsque la méthode se termine.

La stack stocke les valeurs des primitives et les **références** aux instances d'objet.
Ces données sont thread-safe

En cas de débordement de la stack, l'erreur `java.lang.StackOverFlowError` est déclenchée

## Heap

Le heap stocke les nouveaux objets (la référence est stockée par la stack)
Contrairement à la stack, ces données ne sont pas thread-safe : elles sont accessibles globalement.

La déallocation des objets dans le heap est beaucoup plus complexe. Elle implique de déclencher le Garbage Collector. 
Les objets placés dans le heap se répartissent en 3 sous espaces :

- Young Generation : les objets tout juste créés sont placés ici. Si cet espace devient saturé, le Garbage Collector est déclenché avec une stratégie efficiente pour effacer de grands volumes d'objets
- Old or Tenured Generation : les objets qui ont survénu un certain temps migrent dans cet espace. Si cet espace est saturé, le GC est déclenché avec une stratégie efficiente pour effacer un nombre restreint d'objets mais avec un impact restreint sur les ressources
- Permanent Generation : comprend les objets et métadonnées de la JVM qui doivent toujours être conservés

En cas de saturation du heap, une erreur `java.lang.OutOfMemoryError` est déclenchée

## Garbage Collection

Le déclenchement du GC peut être très coûteux.

Le GC est donc déclenché lorsque l'une des générations du heap est saturée. Les autre situations pouvant déclencher un GC sont :

- System.gc() : demande à déclencher le GC, mais son exécution n'est pas garantie
- Time-based : la JVM peut déclencher à interavlle fixe le GC (1/h, 1/jour)

Le comportement exact du GC dépend de l'implémentation et de la configuration de la JVM.

## Changer la taille du heap et de la stack

```bash
-Xms<size>        set initial Java heap size (ex : -Xms6g -> 6 Go of RAM)
-Xmx<size>        set maximum Java heap size
-Xss<size>        set java thread stack size (default ~ 1M)
-Xmn<size>        set java Youg Generation size (rest allocated to other Generations)
```

Note : la taille du stack s'entend par thread.
## Quand changer ces valeurs ?

De façon "naïve" :

Mon programme rencontre fréquemment des erreurs du type `OutOfMemoryError`.

-> Augmenter la taille maximale du heap via `-Xmx`

Mon programme rencontre fréquemment des erreurs du type `StackOverFlowError`.

-> Augmenter la taille maximale du stack via `-Xss`

Mais ça ne résoudra pas toutes les situations. Voici des exemples plus concrets :

### Exercice 1

Exécutez le code

1) Quelle est l'erreur déclenchée ?
2) `StackOverFlowError` car la pile augment à chaque nouvelle méthode et en cas de récursivité le chaînage des méthodes peut être très important
3) Comment résoudre cette erreur ?
4) On peut tenter d'augmenter la taille maximale de la stack
5) Malgré une augmentation de la stack, l'erreur persiste. Quelle peut en être la raison ?
6) La récursivité de mon code est potentiellement infinie
7) Comment puis-je me protéger contre ce scénario ?
8) Dans ce cas précis : la récursivité n'a aucun intérêt, on peut réécrire ce code avec une simple boucle infinie (while(true)). Ou encore mieux : définir d'avance les bornes souhaitées pour générer ce nombre aléatoire
9) S'il s'agissait d'un cas d'usage impliquant obligatoirement une récursivité potentiellement infinie, quelle stratégie puis-je adopter pour prévenir un stack overflow ?
10) Limiter la profondeur de récursivité


### Exercice 2
Exécutez le programme en ajoutant une contrainte pour le heap : `Xms1000M`


1) Quel est l'erreur déclenchée ? 
2) `OutOfMemoryError`
3) Partons du principe que le code contenu dans la classe BlackBox est une boîte noire : on ne sait pas comment cela fonctionne. Comment peut-on y voir clair et comprendre quelle part du code est à l'origine du problème ?
4) Je fais appel à un outil de profilage.

Sous IntelliJ :
![[Pasted image 20240618111816.png]]

Sinon via l'outil gratuit VisualVM :
1) https://visualvm.github.io/download.html
2) Démarrer l'exécutable dans le dossier bin
3) Au besoin, préciser le jdkhome via `./visualvm --jdkhome /home/user/.jdks/corretto-21.0.3` par exemple
4) Documentation : https://visualvm.github.io/gettingstarted.html

Lorsque l'outil de profilage est intégré à l'IDE, une configuration spécifique est soumise à la JVM afin de générer les données nécessaires au profilage.

Cela ne couvre pas 2 situations :
- si le profilage est réalisé avec un outil externe comme VisualVM, celui-ci devra déclencher lui-même la création d'un heap-dump. Si le programme crash avant que l'on ait le temps de lancer cette opération, nous ne pourrons rien explorer
- en production, il peut être très coûteux de générer des logs en temps réel ou à la demande. En cas de crash lié au heap, nous ne savons pas quel était l'état du heap

Pour ces 2 cas de figure il peut être utile de configurer la JVM afin qu'elle génère un Heap Dump en cas de crash. Un heap dump est une copie de l'état du heap mise dans un fichier. Attention, en cas de débordement du heap, le fichier généré sera de taille similaire à la taille maximale du heap (prévoir l'espace disque en conséquence)
On active cette option via `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=<file-or-dir-path>`

1) Ajoutez cette configuration à votre JVM après avoir spécifié un emplacement de sauvegarde du dump
2) Ouvrez ensuite ce dump avec VisualVM

On constate que, contrairement à un outil de profilage temps réel, les informations sont bien plus restreintes : elles ne concernent que l'état du heap à l'instant précédant le crash

Que peut-on observer d'intéressant ?
- on connait l'id du thread ayant amené au débordement du heap
- on voit qu'une majorité d'instances sont des byte[] et des String
- bien qu'aussi nombreux, les bytes occupent bien plus d'espace mémoire (99%)
- le détail instance par instance affiche des byte[] 52Mo
- au moment du crash, 19 instances de byte[] occupant chacune 5,3% du heap, soit la quasi totalité du heap
- on compte 8179 byte[] instanciés. Seuls 19 d'entre eux sont de taille imposante. Les autres 

Le heap dump en cas de débordement du heap permet donc de récupérer des informations précieuses au moment d'un crash, mais ne raconte pas l'histoire ayant mené au crash. Sa génération en cas de crash n'a pas de surcoût et peut donc être mise en place en production.

Pour explorer la succession d'événements, nous pouvons faire appel au Java Flight Record (JFR). Là encore il est possible de démarrer une application directement avec la génération de logs, ou de déclencher en direct cette fonctionnalité.

Le système de profilage d'IntelliJ utilise en arrière plan JFR. Lorsque vous déclenchez un profilage, IntelliJ stocke par défuat un fichier .jfr dans le répertoire `<home>/IdeaSnapshots`. Ces fichiers peuvent être ouverts via VisualVM ou IntelliJ.

Pour démarrer JFR, ajoutez la configuration à la JVM : `-XX:StartFlightRecording=duration=200s,filename=flight.jfr`

Exécutez le code puis analysez le fichier avec VisualVM. Quels sont les éléments d'intérêt ?

- Overview : 12 threads ont pu être dumpés
- Monitor :
	- le CPU ne sembel pas être un facteur limitant
	- le Heap a rapidement été saturé. C'est donc que le GC a fait son possible pour maintenir l'exécution en libérant de la RAM mais cela n'a pas suffit. Mieux vaut avoir une certaine marge de manoeuvre (par exemple 80% occupée) pour survivre aux pics sur des applis web. Jouer avec la limite du Heap est à risque de débordement + coût CPU car implique de déclencher plus fréquemment le GC
	- l'instanciation de threads est quasi linéaire jusqu'au crash
- Threads :
	- on constate à nouveau l'instanciation séquentielle des threads
	- la grande majorité des threads est en mode Park, le multithreading de cette ampleur n'est peut être pas adapté à la situation, ou un autre élément ne permet pas d'en bénéficier pleinement
- File / Socket IO :
	- aucun élément. A vérifier +++ car si threads en attente de lecture/écriture d'un fichier par ex ou d'une connexion web, cela peut représenter un goulot d'étranglement majeur
- Exception :
	- généralement peu d'intérêt, en prod veiller à mettre en place un système de log systématique des erreurs. En dev, IDE bien mieux armés pour étudier ces aspects
- GC : Peut éventuellement mettre en évidence de grandes latences liées au déclenchement du GC. Plus grande pause constatée = 88ms. Sévérité à rapporter au contexte de l'application. Ex : pour un jeu vidéo c'est un problème majeur (représente 5 FPS). Pour un serveur web avec fort trafiic, cela n'est acceptable que si peu fréquent
- Sampler : intérêt ++
	- Peu d'info utile dans noter contexte dans la partie CPU
	- La partie mémoire nous indique qu'un grand nombre de threads alloue 52Mo de Heap
	- Une autre part des threads demeure à 1Ko
	- Hypothèse : seule une partie des threads a pu allouer ces 52Mo, les autres n'ont pas eu le temps du fait du débordement du heap intervenu en amont
- Browser : détail des différents événements, pas d'intérêt ici
- Environment : détail de l'environnement système
- Recording : métadonnées de l'enregistrement des logs

A la vue de ces logs, quelles sont les stratégies envisageables pour traiter le problème du dépassement de heap ? Rappel : vous ne pouvez pas toucher à la classe BlackBox et vous devez obligatoirement exécuter les 1000 appels à la méthode blackBox()

- augmenter la taille du Heap. Nous savons qu'un thread va occuper environ 50Mo de Heap et que 1000 threads vont être instanciés. Il faut donc au maximum 50Go de RAM. Cela ne fonctionnera pas car même avec la RAM nécessaire, nous ne parviendrons pas à instancier et exécuter des 1000 threads de façon efficiente. Rappel : le coût de création d'un thread n'est pas anodin. Travailler avec un pool de thread est souvent plus efficient
- réduire le nombre de threads. Actuellement notre VM autorise un Heap max de 1Go. Sachant qu'un thread occupe 50Mo, nous devrions pouvoir au maximum instancier 20 threads. Mieux vaut prendre une marge de sécurité (ex : 10%) et se limiter à 18 threads
- employer les 2 stratégies pour trouver la meilleure combinaison
- et/ou migrer vers des threads virtuels

Testez les différentes combinaisons, mais n'autorisez pas plus de 2Go de Heap.
Quelle est votre meilleure durée d'exécution ?


Note : le profilage JFR peut dégrader les performances si activé

| Stratégie        | Durée     | Interprétation                                                                |
| ---------------- | --------- | ----------------------------------------------------------------------------- |
| 16 threads       | 14 171 ms |                                                                               |
| 17 threads       | 13 385 ms |                                                                               |
| 18 threads       | 13 176 ms | Meilleur résultat                                                             |
| 19 threads       | 16 714 ms | Heap atteint : GC ? ou nb threads devenu contre productif (coût > bénéfice) ? |
| 20 threads       | 17 611 ms | Heap atteint : GC ? ou nb threads devenu contre productif (coût > bénéfice) ? |
| Threads virtuels | 17 838 ms | ?                                                                             |

Les threads virtuels ne sont pas forcément plus performants, ils présentent avant tout une meilleure scalabilité. Rappel : on n'utilise pas de pool pour les threads virtuels. Ce sont donc 1000 threads virtuels qui ont été créés ici là où nous ne parvenions pas à créer 1000 threads système.

Explorer le profil du heap via un log JFR ou le profilage d'IntelliJ et comparez les 2 situations :
- 16 threads système
- threads virtuels

Résultat :
- 16 threads système
	- Profilage ajoute 4sec à l'exécution ! -> 18 sec
	- CPU : 78-80%
	- Heap ~ 800Mo sauf démarrage à 1300Mo (potentiellement lié à `-Xms`)
- threads virtuels
	- Profilage a réduit l'exécution de 2,5 sec ??? -> 15 sec
	- CPU constamment entre 85-90%
	- Heap ~ 1Go avec un pic à 1,7Go

Conclusion :
- ne comparer que ce qui est comparable. L'ajout du profilage modifie le comportement de la JVM sur de nombreux aspects
- ici la taille du Heap n'est plus le facteur limitant, donc pas de latence engeandrée par un GC trop agressif
- threads système ici plus efficients, plusieurs causes possibles :
	- nature des opérations menées au sein des threads
	- cas particulier où le pooling est plus efficient (instanciation de 16 threads systèmes coûteux < instanciation de 1000 threads virtuels peu coûteux ?)
- pour autant ne pas abandonner threads virtuels : si contraintes fortes sur les ressources (script intégré dans un programme plus vaste), le surcoût des threads virtuels peut largement valoir le gain en scalabilité et l'assurance de ne pas atteindre le débordement du heap
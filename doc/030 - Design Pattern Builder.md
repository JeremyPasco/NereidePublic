Dans l'exercice précédent, nos factories produisaient toutes des instances de classes héritant de Etiquette.

Si au lieu de produire des étiquettes nous avions souhaité produire un ensemble plus hétérogène d'entités ? Par exemple des conditionnements avec chacun des méthodes et propriétés distinctes. Le pattern Factory aurait-il fonctionné ?

## Mission 1
Pour le vérifiez essayez de retirer l'interface Etiquette du code précédent, ou de retirer sa méthode `imprimer`. Peut-on adapter le code en conséquence ?

A l'inverse du pattern Factory, le pattern Builder permet de centraliser la conception d'objets sans requérir que ceux-ci partagent une même interface.
Ca n'est pas l'objectif 1er du pattern Builder, mais c'est un critère qui peut conduire à son utilisation.

**Contexte** : vous cherchez à produire un configurateur de vélo en ligne. Parmi toutes les configurations envisageables, certaines sont non valides. Par exemple sur certains vélos électriques il n'est pas possible d'ajouter une gourde car la batterie occupe déjà l'emplacement prévu.

## Mission 2
Vous souhaitez prévenir à tout prix les configurations invalides. C'est-à-dire qu'il doit être impossible d'avoir une instance de la classe Velo avec des propriétés incompatibles. Dans notre exemple, une instance de Velo ne peut pas être électrique ET avoir une gourde. Comment procédez-vous ? Comment s'appelle ce concept ?

Réponse : il faut rendre la classe immuable en omettant tous les mutateurs et en employant un constructeur comme seule solution d'hydratation. A ce stade, on s'assurera de la validation de la configuration du vélo au sein du constructeur.

## Mission 3
Dans notre exemple nous n'avons que 2 propriétés, mais en réalité la classe Velo en possède une centaine. Il sera trop laborieux de préciser à chaque instantiation la valeur de chaque argument dans un constructeur. Quelles sont les solutions possibles ?

Réponse : 
- créer autant de constructeurs que de combinaisons possibles. C'est ce qu'on appelle le "telescoping constructors problem" (on essaye de réutiliser les constructeurs les uns dans les autres). Et cela ne marchera pas si 2 constructeurs ont la même signature (même nombre d'arguments et même types)
- abadonner l'immuabilité et utiliser les mutateurs
- ou... le pattern factory

Notre classe Velo répond à nos attentes en termes d'immuabilité, nous allons donc la conserver en l'état. Ce qu'il nous manque c'est une approche pour appeler son constructeur. Une bonne façon d'entamer cette réflexion est de régiger le code tel que nous aimerions pouvoir l'utiliser (réfléchir d'abord en termes d'API puis en termes d'implémentation). Voici ce à quoi nous aimerions aboutir :

```java
Velo velo1 = new VeloBuilder()
  .motor("electric")
  .build(); //électrique sans gourde  

Velo velo2 = new VeloBuilder()
  .motor("muscular")
  .bottle(true)
  .build(); //électrique sans gourde  

Velo velo3 = new VeloBuilder()
  .bottle(true)
  .build(); //musculaire avec gourde
```

Faites les développements nécessaires afin que ce code fonctionne.

## Mission 4
Certains éléments peuvent encore être améliorés :
- L'appel au constructeur de Velo implique actuellement de lui passer tous les arguments un par un. Si les arguments sont nombreux cela devient très verbeux et à risque d'erreur (ex : décalage). Faites en sortes de régler ce problème
  PS : partez du principe que les vélos seront systématiquement instanciés grace au VeloBuilder
- On peut actuellement omettre la variable motor lors de la configuration d'un nouveau vélo. On souhaite rendre cette rendre cette information obligatoire (pas de valeur par défaut) 
- La classe VeloBuilder étant intimement liée à la classe Velo, on souhaite faire en sorte que ces 2 classes soient associées plus étroitement
- Peut-on déclarer notre classe VeloBuilder comme statique ?

Réponse à la dernière question : si notre classe VeloBuilder était restée autonome, non. Car cela voudrait dire qu'une seule instance de cette classe pourrait exister et donc que les différentes configurations viendraient se surajoutées les unes aux autres. L'ordre de conception des Velo aurait donc un résultat différent.
Cependant si la classe VeloBuilder devient une sous classe de Velo, cela est non seulement possible (car implique 1 seule instance de VeloBuilder par instance de Velo) mais nécessaire car exigé pour une déclaration de sous classe.



## Réflexion

Quels sont les inconvénients de ce pattern ?

- plus verbeux : nécessite d'écrire 2 fois plus de code environ
- empreinte mémoire : sauf à compléter cet exemple, les propriétés hydratées dans le VeloBuilder ne seront pas vidées de la mémoire

La validation des configuration doit-elle aller dans le Builder ou dans le constructeur de Velo ?

Dans ce cas précis, nos instances Velo sont immuables. Si on veut s'assurer qu'elles sont valides, il faut valider leur état dans le constructeur. Si l'on souhaite pouvoir shunter la validation, on peut donner ce rôle à une méthode du builder et appeler de façon optionnelle `validate()` avant `build()`

Note : il est possible d'utiliser un record pour créer des objets immuables


https://howtodoinjava.com/design-patterns/creational/builder-pattern-in-java/

https://refactoring.guru/design-patterns/builder/java/example
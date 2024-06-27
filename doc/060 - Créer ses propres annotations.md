La syntaxe sera la suivante :
```java
public @interface MyAnnotation {
  String  name();
  boolean isOk();
  int[]   range() default {1, 2, 3};
}
```

Les attributs d'une annotation ne puevnt être que des types suivants :
- un primitif
- un String
- une référence à une Classe
- une autre annotation
- un Enum
- un tableau à 1 dimension de l'un de ces types

Le mot clé `default` permet de donner les valeurs par défaut si non précisé.

La déclaration de l'annotation peut elle même être annotée :
- **@Documented** : l'annotation intégrera la documentation générée par javadoc (ou équivalent)
- **@Inherited** : indique que l'annotation doit être héritée par la classe fille
- **@Retention** : précise le niveau de rétention :
	- `RetentionPolicy.SOURCE` : accessible durant la compilation mais PAS intégrée au fichier class généré
	- `RetentionPolicy.CLASS` : accessible durant la compilation, intégrée au fichier class généré mais PAS chargée dans la JVM
	- `RetentionPolicy.RUNTIME` : accessible durant la compilation, intégrée au fichier class généré et chargée dans la JVM. Donc accessible à l'introspection
- **@Target** : types auxquels l'annotation peut être associée :
	- `ElementType.FIELD`, `ElementType.MODULE`, `ElementType.METHOD`, etc
- **@Repeatable** : si une même annotation peut être répétée plusieurs fois sur un même élément

## Mission 1
Nous allons créer une annotation permettant de générer un catalogue des familles de produit dans notre application. Par exemple si nous avons les classes Velo, Tente et Kayak, nous aimerions qu'on moment de la génération de la documentation ces classes soient annotées en tant que tel.
Commençons par déclarer notre annotation @ProductClass. Celle-ci doit permettre d'annoter des objets dans javadoc. 

Vous pouvez vérifier via 
- intelliJ : Tools > Generate Javadoc
- la ligne de commande suivante depuis la racine du projet:
`javadoc -d doc src/org/example/stuff/*.java`

Exemple de résultat :
![[Pasted image 20240605154937.png]]

## Mission 2
Nous aimerions désormais générer un catalogue de ces classes directement depuis le code lors de son exécution. Cela pourrait par exemple faire l'objet d'une fonctionnalité d'un CLI de développement visant à aider le développeur à connaitre les classes produit donc il dispose.

Le concept visant à analyser du code à partir du code est appelé réflexivité. Java propose un ensemble de méthodes dans ce but mais il est aisé de commettre des erreurs, notamment car la réflexivité ne peut pas être intégralement vérifiée au moment de la compilation.
Des dépendances existent pour faciliter l'usage de la réflexivité. Nous allons utiliser la dépendance Reflexion :

`implementation group: 'org.reflections', name: 'reflections', version: '0.10.2'`

Voici un exemple de code permettant de récupérer toutes les classes avec une annotation @X dans un package :
```java
Reflections reflections = new Reflections("org.example.stuff");
reflections.getTypesAnnotatedWith(X.class);
```

Un objet de type Class peut ensuite fournir la liste de ses méthodes via `Fields.of(maClasse)`

Des exemples peuvent être consultés ici : https://github.com/ronmamo/reflections

Objectif : Afficher dans la console pour chaque classe la liste de ses propriétés publiques.

Note : `getTypesAnnotatedWith(X.class, true)` devrait retourner les classes filles dont les parents sont annotés mais un bug sur la dernière version de la bibliothèque ne le permet pas.

## Mission 3
On va désormais instancier ces objets.
Nous aimerions ensuite pouvoir générer une étiquette pour un objet comme suivant ``
`creerEtiquette(monObjet)`.
Cette fonction doit afficher en console les clés-valeurs mais uniquement pour une liste de champs que nous aurons annotés via @LabelField
Cette même fonction doit déclencher une erreur si l'objet qui lui est passé ne possède pas l'annotation @ProductClass

Exemple d'affichage attendu :
```
[Kayak]
width=60
length=210
```

Quelques aides :
- Tous les objets instanciés ont la méthode `getClass()`
- On vérifie si une classe possède une annotation via sa méthode `isAnnotationPresent(MonAnnotation.class)`
- On peut accéder à la liste des champs d'une classe via `Fields.of(maClasse)` (voir exemples de la dépendance reflections)
- A partir d'un champ (classe Field) on peut récupérer la valeur correspondante dans une instance via `field.get(monInstance)`.

## Mission 4
Le nom des champs de nos objets ne sont pas adaptés à la création d'étiquettes. Faites en sorte que l'on puisse configurer une autre valeur au sein de l'annotation @LabelField. Si cette valeur n'est pas précisée, conservez le nom du champ de l'objet.

Aides :
- on déclare un paramètre dans notre annotation via la syntaxe `<type> monParametre()`
- pour rendre un paramètre optionnel, on peut ajouter dans sa déclaration `default <valeur par défaut>`
- on accède à la valeur de ce paramètre via `field.getAnnotation(MonAnnotation.class).monParametre()`
  



**Infos intéressantes :**
- Modifier/compléter le code source avec une annotation est possible. La dépendance Lombok permet par exemple de générer automatiquement des Builder à l'aide d'une simple annotation. L'approche est cependant extrêmement complexe et très "hacky". [Doc](https://projectlombok.org/features/Builder)
- Une autre approche, moins complexe mais plus restreinte est décrite ici : https://www.baeldung.com/java-annotation-processing-builder
Elle implique de configurer en conséquence Maven/Gradle afin de déclencher un scan du code et une prise en charge des annotations trouvées.


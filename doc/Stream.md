Stream : permet de chaîner des opérations sur une collection de façon fonctionnelle.

Un stream se compose de 3 blocs :
- une source de connées (collection)
- des étapes intermédiaires (transformations)
- une étape finale (ex : agrégation, collector)

## Source

La source de données est une Collection. Sa transformation en stream dépend de son type exact :

```java

// Tout type de Collection (dont list)
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();

// Array
Stream<String> streamOfArray = Stream.of("a", "b", "c");
Stream<String> streamOfArray2 = Stream.of(someArray);

// Via Stream.builder
Stream<String> streamBuilder = Stream.<String>builder().add("a").add("b").add("c").build();

// Via Stream.generate()
Stream<String> streamGenerated = Stream.generate(() -> "element").limit(10);

// Via Stream.iterate()
Stream<Integer> streamIterated = Stream.iterate(40, n -> n + 2).limit(20);

//Avec des primitifs
IntStream intStream = IntStream.range(1, 3); LongStream longStream = LongStream.rangeClosed(1, 3);

Random random = new Random();
DoubleStream doubleStream = random.doubles(3);
```

## Etapes intermédiaires

Exemples :

- `.map()`  : applique une transformation à chaque élément
- `mapToXXX()` : identique à map mais retourne un stream de type XXX où XXX est un primitif
- `.peek()`  : retourne le stream non modifié après avoir exécuté une fonction pour chaque élément
- `.distinct()` : retourne un stream avec une liste d'éléments distincts
- `.dropWhile()` : retire les éléments du stream tant que la condition est vraie
- `.filter()` : filtre les éléments sur la base d'un prédicat
- `.limit()` : ne garde que les N premiers éléments
- `.skip()` : retire les N premiers éléments
	- `.sorted()` : classe les éléments du stream selon un Comparator

## Etape finale

- `.forEach()` : exécute une fonction pour chaque élément (ne retourne rien)

- `.allMatch()` : true si tous les éléments sont conformes à un prédicat, sinon false
- `.anyMatch()` : true si au moins 1 élément est conforme à un prédicat, sinon false

- `.count()` : nombre d'éléments dans le stream
- `.findAny()` : trouve un élément conforme au prédicat (non déterministique)
- `.findFirst()` : trouve le premier élément conforme au prédicat (déterministique)

Note : findFirst et findAny retournent un type Optional (voir paragraphe dédié)

- `min()` : valeur minimale
- `max()` : valeur maximale

- `reduce()` : agrégation selon reduce, ex : 
```java
var x = Arrays.asList("a", "b", "c", "d")
x.stream().reduce("DEBUT->", (val, acc) -> val + acc);
//DEBUT->abcd
```

- `collect()` : applique un Collector. >Ex :
	- `.collect(Collectors.toList())` ou directement `toList()` depuis un stream
	- `.collect(Collectors.joining(", ", "[", "]"))`
	- `.collect(Collectors.averagingInt(Product::getPrice))`
- 


## Optional

Représente une valeur potentiellement "vide". Permet d'avoir une représentation objet des valeurs null et ainsi réduire les erreurs du type NullPointerException

Créer un Optional :
```java
Optional<String> empty = Optional.empty();
Optional<String> empty = Optional.of("text");
Optional<String> empty = Optional.ofNullable(someText); //someText peut être null
```

Un type `Optional<E>` peut renfermer une valeur ou non. On le vérifie à l'aide de l'une de ces méthodes :
- `isPresent()`
- `isEmpty()`

Sans Optional :

```java
if(something != null) {
	//...
}
```

Avec Optional

```java
opt.ifPresent(name -> System.out.println(name));
```

Valeur par défaut

```java
String text = opt.orElse("valeur par défaut");
String text2 = opt.orElseGet(() -> ...);
```

Erreur si null

```java
String text = opt.orElseThrow(IllegalArgumentException::new);
String text2 = opt.orElseThrow(); //NoSuchElementException
```

# Exercice 1

Créez un stream réalisant les étapes suivantes :

1) Source = liste d'entiers allant de 1 à 10
2) Ne gardez que les nombres strictement supérieurs à 3
3) Pour chaque nombre pair, affichez sa valeur en console
4) Triez ces nombres par ordre décroissant
5) Stockez dans une variable la somme des nombres restants dans le stream à cette étape
6) Afficher le contenu de cette variable

Si vous ne parvenez pas à réaliser une étape, passez à la suivante.

Que se passe-t-il si vous omettez les étapes 5 et 6 ? Pourquoi ?


Réponse : l'étape 3 n'est pas déclenchée. L'exécution d'un stream est dite `lazy`, c'est-à-dire qu'en l'absence de clôture du stream, rien n'est exécuté. Cela laisse potentiellement la palce à des optimisations (planifier / rassembler des étapes du stream de façon performantes une fois toutes les étapes connues)

Notes pour l'étape 4 : les IntStream sont des streams de primitifs et non d'Integer. Ces streams présentent de nombreuses limites. Il n'est par exemple pas possible de les convertir en List ou de les trier selon un comparateur. Parfois il faudra passer par un la méthode `boxed()` qui permet d'obtenir un Stream d'Integer, ou d'instancier directement un Stream de ce type.

Note pour l'étape 5 : il est possible d'appeler directement la méthode `sum` si on utilise un IntStream. Si utilisation de `boxed`, on peut revenir à un type primitif avec `mapToInt`


```java
int total = IntStream.range(1, 11)  
        .filter(i -> i > 3)  
        .peek(i -> {  
            if (i % 2 == 0) {  
                System.out.println(i);  
            }  
        })  
        .boxed()  
        .sorted(Comparator.reverseOrder())  
        .reduce(0, Integer::sum);

		// alternative
        // .mapToInt(e -> e)  
        // .sum();  
  
System.out.println(total);
```
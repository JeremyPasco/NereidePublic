Les annotations sont des métadonnées qui peuvent être associées à des portions de codes comme des classes, des méthodes, des arguments, etc.

Elles peuvent avoir 3 effets :
- donner des indications au compilateur pour déclencher des avertissements ou erreurs
- modifier le code au moment de la compilation
- modifier ou inspecter le comportement du code lors de son exécution

Java est livré avec 6 annotations mais il est possible de créer soi-même ou d'en importer.

## @Override

On l'utilise pour indiquer qu'une méthode surcharge ou remplace une méthode héritée.
Si l'annotation est utilisée sur une méthode qui s'avère ne pas rentrer dans cette définition, le compilateur nous en informera.

## @SuppressWarnings et @SafeVarargs

Ces annotations masquent des warnings

```java
@SuppressWarnings({"unchecked", "deprecation"})
```
SuppressWarnings permet surtout de masquer les warnings du compilateur pour les unchecked exceptions non traitées et pour l'usage de code déprécié.

SafeVarargs : si on utilise un argument de type varargs générique, il y a un risque potentiel (corruption de type). Le compilateur déclenche donc systématiquement un warning. Si on considère que les entrées sont fiables, on peut désactiver ce warnings via cette annotation.

## @Deprecated

Déclare que le code associé est déprécié : informer les développeurs et déclenche des warnings si le code associé est utilisé.

## @ FunctionalInterface

Déclenche une erreur si l'interface n'est pas une SAM.
Si cette annotation n'est pas utilisée, on pourra compiler mais une erreur se produira à l'exécution lorsqu'il sera fait usage de notre interface via un lambda.

## @Native

Valable uniquement pour les propriétés. Indique qu'il s'agit d'une valeur constante à un programme accédant au code natif (ex : interface entre du C++ et du Java)


# Exemple d'une annotation Lombok

En Java les exceptions peuvent être de 2 types :
- checked
- unchecked

Lorsqu'une CheckedException se produit dans une méthode nous n'avons que 2 solutions :
- la traiter avec un try/catch
- ne pas la traiter, il faut alors modifier la signature de notre méthode pour déclarer qu'elle peut engeandrer une exception de ce type. Le scope parent aura alors lui aussi les même choix

Une exception de type unchecked peut être traitée de la même façon mais sans obligation. Le compilateur compilera même si notre code suppose qu'une erreur de ce type se produira.

Pour cette raison il est conseillé de déclencher des erreurs de type checked si elles sont récupérables. Dans le cas contraire on fera appel à des erreurs de type unchecked.

## Checked Exceptions are Evil

source : https://phauer.com/2015/checked-exceptions-are-evil/

Les checked exceptions ont cependant une conséquence lourde sur le développement.

![[Pasted image 20240619144315.png]]

Si l'on souhaite traiter l'exception en dehors du scope où elle se produit, il faut modifier en cascade les signatures.

Dans certains cas c'est impossible (API externe, dépendance, etc).

Traiter systématiquement l'exception dans le scope où elle se produit n'est pas non plus possible.

Exemple classique avec les streams+lambda : 

```java
List<Path> files = // ... 
List<String> contents = files.stream()
	.map(file -> {
			try {
				return new String(Files.readAllBytes(file));
			} catch (IOException e) {
			 // Well, we can't reasonably handle the exception here...
			}
		}
	)
	.collect(Collectors.toList());
```

Impossible de traiter l'exception au sein du lambda.
Impossible de remonter l'exception au scope parent car l'interface fonctionnelle ne l'a pas prévu dans sa signature (`java.util.Function.apply()`)

## Encapsulation

Une solution consiste alors à encapsuler notre checked exception dans une unchecked exception. Par exemple :

```java
private String getContent(Path targetFile) {
	try { 
		byte[] content = Files.readAllBytes(targetFile); 
		return new String(content); 
	} catch (IOException e) { 
		throw new RepositoryException("Unable to read file content. File: " + targetFile, e); 
	}
}

public class RepositoryException extends RuntimeException { //... }
```

Note : les exceptions héritant de `RuntimeException` sont de type Unchecked

Il est alors possible de faire remonter l'erreur sans toucher aux signatures des méthodes et de l'attraper au moment voulu par un try/catch

![[Pasted image 20240619145102.png]]

Inconvénient : ni notre IDE ni notre compilateur ne va nous empêcher de compiler sans avoir traité cette exception. On peut cependant aider l'IDE à nous informer de la situation en lui donnant des incides.

Soit par javadoc : 
```java
/** 
 * @throws RepositoryException if ...
 */ 
private String getContent(Path targetFile) {
```

Soit via `throws` :
```java
private String getContent(Path targetFile) throws RepositoryException {...}
```

## SneakyThrows

Pour éviter la redondance dans ces situations, la dépendance Lombok propose une annotation dédiée `@SneakyThrows`

Elle permet de passer de :

```java
public static void main(String[] args) {  
	try {  
		Files.readAllLines(Path.of("test.txt"))  
			.forEach(System.out::println);  
	} catch (IOException e) {  
		throw new RuntimeException(e);  
	}  
}
```

à :

```java
@SneakyThrows  
public static void main(String[] args) {  
	Files.readAllLines(Path.of("test.txt"))  
		.forEach(System.out::println);  
}
```

Attention cependant, l'exception renvoyée est toujours une `IOException` et donc une exception de type checked !

L'annotation a pour effet d'empêcher le compilateur de considérer cette exception comme un type checked. C'est donc une astuce pour passer l'étape de compilation sans avoir traité une exception de type checked dans le scope où elle a lieu.
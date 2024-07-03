# Evolutions de Java (versions 17 à 21)

Version LTS = support pendant 8 ans

LTS tous les 2 ans

Nouvelle version tous les 6 mois

Versions LTS : 17 (2021) et 21 (2023)


## Java 17

- Amélioration de la génération des nombres pseudo aléatoires
- Meilleure prise en charge de macOs
- Dépréciation de l'API Applet
- Meilleure encapsulation des éléments internes au JDK : évite qu'un développeur accède à ces éléments par erreur
- pattern matching avec switch :

```java
if(obj instanceOf Velo) {
  Velo velo = (Velo) obj;
  velo.getEngine();
}

// Avec pattern matching
if(obj instanceOf Velo velo) {
  velo.getEngine();
}
```

Amélioration de switch
```java
String res;
switch(val) {
	case 10:
	case 20:
		res = "A";
		break;
	case 30:
	case 40:
		res = "B";
		break;
	default:
		res = "C";
		break;
}

String res = switch(val) {
	case 10, 20 -> "A";
	case 30, 40 -> "B";
	default -> "C";
}
```
Combinaison des 2
```java
String res = switch(obj) {
	case VeloElectrique -> "Vélo électrique";
	case VeloMusculaire -> "Vélo musculaire";
	case Velo -> "Autre typ de vélo";
	default -> "Ceci n'est pas un vélo";
}
```

- Suppression de RMI Activation
- Classes et interfaces sealed : n'autoriser l'héritage qu'à certaines classes
```java
public sealed class Velo permits VeloElectrique, VeloMusculaire {...}
public sealed interface Velo permits VeloElectrique, VeloMusculaire {...}
```
- Suppression d'AOT et JIT compiler --> se tourner vers GraalVM au besoin
- Dépréciation du Security Manager (client side Java code security)
- Foreign Function and Memory API : accéder à l'espace mémoire de Java depuis une autre application, en rempalcement de l'API JNI
- Vector API : calcul vectoriel avec prise en charge matérielle
- Context-Specific Deserialization filter ?

# Java 18
- UTF-8 par défaut (auparavent : dépendant de paramètres locaux + OS)
- Simple Web Server : serveur HTTP en qq lignes de code
- Ajout de Code Snippets dans la documentation de l'API Java
- Amélioration de Vector API
- Internet-Address-Resolution SPI : conversion nom d'hôte vers IP, auparavent utilisait le système natif prévu par l'OS, désormais configurable
- Amélioration de Foreign Function and Memory API
- Amélioration du Pattern Matching
- Dépréciation de `finalize()` : déprécié depuis Java9. Devait permettre de gérer manuellement la déallocation mémoire et réduire la charge du garbage collector mais sans succès

# Java 19
- Préallocation des HashMap : par défaut un HashMap de taille X est préalloué pour 75% de cette taille. Lorsque 75% sont réellement alloué, un HashMap double alors sa taille. Ainsi pour `HashMap.newHashMap(100)` nous obtenons une préallocation pour 75 éléments. Si l'objectif est d'avoir un un HashMap avec une préallocation de 75 éléments, on peut désormais écrire plus simplement : `new HashMap<>(75)`
- Amélioration du pattern matching de switch et de Record
- Amélioration de Foreign Function and Memory API
- Amélioration des Virtual Threads --> voir multithreading
- Amélioration de Structured Concurrency --> voir multithreading
- Amélioration de Vector API

# Java 20
- Scoped values --> voir multithreading
- Record Pattern
```java
record Point(int x, int y) {}

public static int beforeRecordPattern(Object obj) { 
	int sum = 0; 
	if(obj instanceof Point p) { 
		int x = p.x(); 
		int y = p.y(); 
		sum = x+y; 
	} 
	return sum; 
} 

public static int afterRecordPattern(Object obj) { 
	if(obj instanceof Point(int x, int y)) { 
		return x+y; 
	} 
	return 0; 
}
```
- Amélioration Pattern Matching pour les switch
- Amélioration Foreign Function and Memory API
- Virtual Threads -> voir multithreading
- Structured concurrency --> voir multithreading
- Améliration de Vector API

# Java 21

- Amélioration du Record Pattern, implications pour le switch
- String Literal (template)
```java
String label = "Produit";
String welcomeText = STR."Découvrez notre nouveau \{label}";
System.out.println(welcomeText);
```
- Amélioration des Virtual Threads
- Sequenced collections : https://www.baeldung.com/java-lts-21-new-features#5-sequenced-collections-jep-431
- Key encapsulation mechanism API : méthodes de cryptographie symmétrique
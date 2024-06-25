## Déclaration de variable

```java
int entier;
int autreEntier = 2;

Integer ent = 2;

var val = 2;
```

Possible d'utiliser `var` si le type peut être imputé

## Portée des variables

```java
public static void main(String[] args) {  
    if (true) {  
        int nombre = 5;  
    }  
    System.out.println(nombre);  
}
```

```java
public static void main(String[] args) {  
    int nombre;  
    {  
        nombre = 5;  
    }  
    System.out.println(nombre);  
}
```

## "Pass by reference"

Pour chaque exemple, quelle est la valeur affichée ?

```java
public class Main {  
    public static void main(String[] args) {  
  
        int nombre = 5;  
        ajouteCinq(nombre);  
        System.out.println(nombre);  
    }  
  
    private static void ajouteCinq(int a) {  
        a += 5;  
    }  
}
```

```java
public class MonObjet {  
    public int nombre;  
}

public class Main {  
    public static void main(String[] args) {  
  
        MonObjet monObj = new MonObjet();  
        monObj.nombre = 10;  
  
        ajouteCinq(monObj);  
        System.out.println(monObj.nombre);  
    }  
  
    private static void ajouteCinq(MonObjet a) {  
        a.nombre += 5;  
    }  
}
```

Piège avec les classes immuables :

```java
public class Main {  
    public static void main(String[] args) {  
  
        Integer nombre = 5;  
        ajouteCinq(nombre);  
        System.out.println(nombre);  
    }  
  
    private static void ajouteCinq(Integer a) {  
        a += 5;  
    }  
}
```

Autres points importants :
- l'incompatibilité int/Integer n'est pas problématique, Java fera la conversion nécessaire (ex : autoboxing)
- Les classes Integer / Float / etc sont immuables

## Héritage

Une classe fille hérite d'une classe parent via le mot-clé `extends`
On ne peut hériter que d'une classe à la fois.
Mais la classe parent peut elle même hériter d'une autre classe et ainsi de suite.

Une classe fille hérite de toutes les méthodes et champs qui ne sont pas privés.
On peut redéfinir une méthode existante chez le parent, dans ce cas on annote dans la classe fille la méthode avec `@overrite`

S'il s'agit du même nom de méthode mais d'une signature différente (autres paramètres) on parle de surcharge. Ambigu avec le terme overwrite en anglais.

Si l'on redéfinit le constructeur dans la classe fille, on peut appeler le constructeur de la classe mère avec `super()`

## Polymorphisme

Des classes héritant d'un parent commun peuvent être typée de la même façon :

```java
class Product{...}
class Bike extends Product{...}
class Bottle extends Product{...}

...
public static void main(String[] args) {
	Product p1 = new Bike();
	Product p2 = new Bottle();

	var arr = new Product[]{p1, p2};
}
```

Mais dans ce cas, seules les champs et méthodes du parent sont accessibles. Les spécificités des classes enfant sont ignorées.

Si la classe `Bike` admet une methode `getPrice()` mais pas la classe `Product`, alors il faut au préalable caster notre instance en accord :

```java

var p = arr[0];

// avant Java 17
if(p instanceOf Bike) {
  Bike bike = (Bike) p;
  bike.getPrice();
}

// Depuis Java 17
if(p instanceOf Bike bike) {
  bike.getEngine();
}
```

## Classes abstraites

On souhaite parfois déclarer un parent commun mais ne pas avoir de cas d'usage où celui-ci est instancié. Par exemple notre classe `Product` n'a probablement aucune raison d'être instanciée.

On déclare alors notre classe comme abstract : on peut en hériter mais pas l'instancier.

## Classes "finales"

A l'inverse, on peut souhaiter interdire l'héritage d'une classe. Si nos produits sont considérés comme des classes terminales (ils n'auront jamais d'enfant) ont peut les déclarer avec le mot clé `final` afin d'en interdire l'héritage.

## Visibilité des variables et méthodes

Du plus ouvert au plus fermé :

- **public** : toutes les classes
- **protected** : (classes du même package) et (classes enfants dans d'autres packages)
- **sans modifier** : classes du même package
- **private** : propre classe seulement

Règle générale : viser le plus restrictif

Quelles lignes seront responsables d'erreurs ?

```java
package org.example.a;  
  
public class ClassA {  
    public int a;  
    int b;  
    protected int c;  
    private int d;  
}
```

```java
package org.example.b;  
  
import org.example.a.ClassA;  
  
public class ClassB {  
    public ClassB() {  
        ClassA ca = new ClassA();  
        ca.a = 5;  
        ca.b = 5;  
        ca.c = 5;  
        ca.d = 5;  
    }  
}
```

```java
package org.example.a;  
  
public class ClassC {  
    public ClassC() {  
        ClassA ca = new ClassA();  
        ca.a = 5;  
        ca.b = 5;  
        ca.c = 5;  
        ca.d = 5;  
    }  
}
```

```java
package org.example.b;  
  
import org.example.a.ClassA;  
  
public class ClassD extends ClassA {  
    public ClassD() {  
        this.a = 5;  
        this.b = 5;  
        this.c = 5;  
        this.d = 5;  
    }  
}
```

**Question** : pourquoi définir des getters/setters avec une variable privée plutôt que de la laisser publique ?
**Exercice** : 
1) Créer une classe `MaClasse` avec un `public int nombre`
2) Depuis `main`, intancier `MaClasse`, définir une valeur pour `nombre` puis l'afficher
3) Modifier le code afin que toute lecture ou écriture sur ce `nombre` déclenche un log console
**Que fait-il en déduire ?**

## Autres keywords pour les variables

**final** : la valeur ne pourra pas être modifiée (constante)
Le comportement diffère entre les primitifs et les références (objets) -> une référence constante n'interdit pas de modifier le contenu de l'objet vers lequel elle pointe.

Quelles ligne(s) déclenchent des erreurs ?
```java
final int a = 5;  
a = 6;  
  
final int[] array = {1, 2, 3, 4, 5};  
array[0] = 9;  
array = new int[5];
```

## Interfaces

Les interfaces servent avant tout à décrire des méthodes qui sont implémentées dans une classe. Contrairement à l'héritage, il est possible d'implémenter plusieurs interfaces depuis une classe.

Exemple :
```java
public interface Product {
	String label = "Produit"; // constante
	float getPrice(); //méthode abstraite
	static boolean isAvailable() { //méthode statique
		return false;
	}
	default void printData() { //méthode par défaut
		System.out.println("Data");
	}
}
```

Une interface permet de déclarer 4 catégories d'éléments :
- des variables constantes
- des méthodes abstraites (+++)
- des méthodes statiques
- des méthodes avec une implémentation par défaut

Une interface peut également être vide

Une classe qui implémente cette interface devra obligatoirement implémenter une méthode `getPrice()`. Elle peut implémenter une méthode `isAvailable`, dans le cas contraire elle implémentera automatiquement la méhode par défaut.

Les méthodes par défaut permettent d'éviter les évolutions cassantes. Par exemple si vous ajoutez une méthode abstraite à une interface, vous devrez l'implémenter dans toutes les classes implémentant cette interface. Pour éviter cela, on utilisera l'implémentation par défaut.

On implémente des interfaces avec `implements`

Les mêmes règles de polymorphisme s'appliquent : une classe X héritant de A et implémentant B et C peut être castée comme un type A, B,  C ou X selon le besoin.

# Interface vs classe abstraite

Dès lors q'une interface ne peut pas être instanciée et qu'elle permet de déclarer des méthodes avec implémentation par défaut, on peut se demander dans quel cas il vaut mieux utiliser une classe abstraite et une interface.

Critères à prendre en compte :
- on ne peut hériter que d'une classe mais implémenter plusieurs interfaces
- les classes abstraites ont un état, ses méthodes peuvent y accéder. L'interface n'a pas d'état et ses méthodes ne peuvent donc pas y accéder. Les méthodes par défaut ne pourrons donc pas toujours répondre au besoin

Ex concret ici : https://www.baeldung.com/java-interface-default-method-vs-abstract-class

## Interface fonctionnelle

Dans de nombreux langages, il est possible de passer une fonction comme argument. On parle parfois de fonction anonyme dans ce cas. En javascript on peut ainsi écrire :

```js
maFonction(x, y, function(){ ... })
```

Ou encore l'équivalent sous forme de lambda :
```js
maFonction(x, y, () => { ... })
```

Un cas d'usage classique est de fournir un callback : une fonction qui sera exécutée après exécution de `maFonction`

Jusqu'à Java 8 nous étions forcés de créer des classes avec 1 méthode pour y parvenir.
Désormais il est possible de mettre en place des interfaces fonctionnelles.

Une interface fonctionnelle est une interface avec 1 seule méthode abstraite (SAM : single abstract method). Il est conseillé d'annoter ces interfaces avec `@FunctionalInterface` afin de déclencher des erreurs à la compilation si l'interface n'est pas une SAM.


Exercice : rédiger le code nécessaire pour que le bloc suivant foncitonne : 
```java
ClasseA a = new ClasseA();  
String resultat = a.ajouteUn(10, i -> "Le résultat vaut : " + i);  
System.out.println(resultat); //Le résultat vaut 11
```

Solution :
```java
@FunctionalInterface  
public interface IntToStringFunction {  
    abstract String apply(int nombre);  
}

public class ClasseA {  
    public String ajouteUn(int nombre, IntToStringFunction function) {  
        return function.apply(nombre+1);  
    }  
}
```



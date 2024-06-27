
# L'atomicité

Prenons la requête SQL suivante :

```sql
UPDATE product SET available = false WHERE created_at < DATE '2020-01-01';
```

Partons du principe que cette requête revient à modifier plusieurs centaines de milliers de lignes dans notre table `product`.

Que se passera-t-il si pour une raison quelconque notre SGBD ne parvenait pas à traiter l'intégralité de la requête (ex : bloqué à 60% des lignes) ?

- A - Seules les lignes traitées auront bénéficié de la modification. Il faut prendre en compte ce scénario dans noter code Java (ex : dans un try/catch) pour relancer la requête si elle ne s'est pas terminée correctement
- B - Notre SGBD reprendra la requête dès qu'il le pourra et finira de traiter les lignes manquantes
- C - Notre SGBD annulera la requête et revidnra à l'état initial (aucune ligne affectée) puis déclenchera une erreur que l'on pourra intercepter avec un try/catch

Les bases de données de type SQL implémentent les propriétés ACID.
A correspond à l'atomicité. C'est-à-dire qu'une action s'exécute en tout ou rien, elle est atomique. C'est donc la réponse C.

En cas d'erreur, le SGBD procède à un rollback.

# Virement bancaire

Parfois certaines opérations impliquent des requêtes successives. Prenons l'exemple d'un virement bancaire. Celui-ci pourrait se traduire par :

```sql
UPDATE bank_account SET balance = balance - 100 WHERE account_id = 456;
UPDATE bank_account SET balance = balance + 100 WHERE account_id = 789;
```

Que se passera-t-il si la 2e requête échoue ?

- A - Le compte 456 sera débité de 100€ mais le compte 789 ne sera pas crédité.
- B - Selon le principe d'atomicité, la 1e requête sera annulée (rollback) et on reviendra à l'état initial. L'erreur pourra être détectée via un try/catch
- C - L'ordre d'exécution des requêtes n'étant pas prévisible, il est possible que la 1e requête soit exécutée ou non selon si son exécution a pu se terminer avant l'erreur

L'ordre d'exécution est toujours respecté dans un bloc SQL, ce n'est donc pas la réponse C.
Par défaut l'atomicité n'est assurée que par requête, et non pour un ensemble de requête. C'est donc la réponse A.

# Transaction

Pour rendre un ensemble de requêtes "atomique" il faut déclarer une transaction.

Exemple sous Postgresql :
```sql
BEGIN
UPDATE bank_account SET balance = balance - 100 WHERE account_id = 456;
UPDATE bank_account SET balance = balance + 100 WHERE account_id = 789;
COMMIT
```
Tant qu'une transaction n'est pas terminée, il est possible de déclencher un rollback.

```sql
BEGIN
UPDATE bank_account SET balance = balance - 100 WHERE account_id = 456;
UPDATE bank_account SET balance = balance + 100 WHERE account_id = 789;
ROLLBACK
```
La transaction ci-dessus ne modifie donc aucune des 2 lignes.

En Java il existe de nombreuses couches d'abstractions pour manipuler nos données sans avoir à écrire du SQL. Ces différentes bibliothèques proposent des méthodes permettant de débuter / clôturer des transactions et effectuer des rollbacks.

Voici un exemple sous JDBC :
```java
Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
try { 
	connection.setAutoCommit(false); 
	PreparedStatement firstStatement = connection
		.prepareStatement("firstQuery"); 
	
	firstStatement.executeUpdate(); 
	PreparedStatement secondStatement = connection
		.prepareStatement("secondQuery"); 
	
	secondStatement.executeUpdate(); 
	
	connection.commit(); 
} catch (Exception e) { 
	connection.rollback(); 
}
```

Equivalent sous JPA :

```java
EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-example");
EntityManager entityManager = entityManagerFactory.createEntityManager(); try { 
	entityManager.getTransaction().begin();
	entityManager.persist(firstEntity);
	entityManager.persist(secondEntity);
	entityManager.getTransaction().commit(); 
} catch (Exception e) { 
	entityManager.getTransaction().rollback(); 
}
```

-> l'approche est similaire dans l'ensemble des bibliothèques

Point d'attention : ce principe de transaction n'est valable que pour certains SGBD (SQL). D'autres SQBD dit NOSQL peuvent ne pas implémenter l'atomicité, les transactions ou ne pas garantir l'exécution du rollback.

Note : il est possible d'imbriquer des transactions. Auquel cas un rollback réinitialisera l'état à la transaction parent.

# Transaction entre différents SGBD ou différents schémas d'un même SGBD

Une transaction SQL s'entend au sein d'un même schéma d'un même SGBD. Certains SGBD supportent les transactions au travers de différents schéma.

Cependant une transaction impliquant plusieurs instances de SGBD n'est pas possible du côté SGBD. Il existe cependant des solutions côté Java pour récupérer la gestion des transactions : voir JTA et JTS. Ces approches permettent également de développer des comportements de type transaction/rollback sur des éléments autres que des SGBD. Il faut alors coder soit même le comportement souhaité en cas de commit ou de rollback.

Des frameworks comme Spring implémentent nativement différentes approches pour gérer des transactions globales.

# Impact des transactions

En pratique, déclarer des transactions oblige notre SGBD à conserver 2 version des données pour les lignes (voire tables) concernées : les valeurs d'origine, et celles après modifications au sein de la transaction. Il y a donc un coût à cette opération qui dépend de l'ampleur de la transaction. On dit que notre SGBD effectue un FORK.

Les données modifiées par notre transactions sont sousmises à un LOCK. C'est-à-dire qu'aucune autre requête ne peut les modifier. Ceci afin d'éviter toute incohérence après exécution des 2 requêtes concurrentes.

Il faut donc veiller à réduire au maximum l'ampleur de notre transaction et à ce qu'elle soit résolue le plus vite possible.
Si notre transaction implique un très grand nombre de lignes/tables il pourra être utile d'envisager un traitement par lots (batch).
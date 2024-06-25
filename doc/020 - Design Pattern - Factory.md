**Contexte** : vous devez améliorer le code responsable de l'impression des étiquettes produit. Ce code est intégré dans un système plus vaste. L'objectif est de rendre votre code évolutif, c'est-à-dire faire en sorte que vos futures modifications aient le moins d'impact possible sur le reste de l'application.

Il existe 2 types d'étiquettes. Une décrivant le poids, l'autre décrivant les dimensions des produits. On imprime l'une ou l'autre selon les caractéristiques du produit. Par la suite des centaines d'autres formats d'étiquettes sont prévues.

## **Mission 1** : modifiez le code afin de préparer l'arrivée de ces nouvelles étiquettes.

Votre code est désormais appelé par des milliers de fonctions dans l'application. Il n'est plus possible de modifier les classes des étiquettes. De même, les classes des produits ne sont plus modifiables.
De nouvelles règles vous sont imposées pour calculer la taille et les dimensions à afficher sur l'étiquette :
- pour étiquettes affichant le poids : on rajoute 1kg pour les produits de moins de 20 kg, sinon on rajoute 2 kg
- pour les étiquettes affichant les dimensions, on rajoute 10 sur la dimension la plus petite

Partez du principe que ces règles seront à termes beaucoup plus complexes (>100 lignes de code par type d'étiquette), et qu'il y aura des règles pour chaque type d'étiquette.

On souhaite également pouvoir générer des étiquettes depuis n'importe quelle classe.

## **Mission 2** : modifiez votre code pour intégrer ces nouvelles règles avec les contraintes énoncées.


Réflexions :
- la/les classes factory peuvent implémenter une interface commune ou héiter d'une classe abstraite si logique partagée
- au lieu de procéder à 1 classe factory <=> 1 classe étiquette, on peut avoir d'autres stratégies (ex : 1 classe factory par pays et autant de méthodes que de type d'étiquette -> dans ce cas l'interface liste tous les types d'étiquettes)
- si les factories n'ont pas d'état, on peut rendre leur(s) méthodes statiques


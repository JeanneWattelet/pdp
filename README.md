# pdp
Les dernières modifications sont dans PDP/src. Je ne comprends toujours pas comment ça marche...

Résumé de ce que nous avons enregistré ici :
 - Package domain : Classe GrapheTrajet et tout ce qui concerne sa création, son utilisation pour un plus court chemin et sa sérialisaiton. Les fonctionnalités présentes pour l'instant sont : partir à, arriver à, le moins de marche, le moins d'attente et ne pas prendre tel moyen de transport. Il y a aussi quelques classes de tests et des classes inutiles comme Graphe ou Oiseaux.
 - Package transport : Classe Carte et tout ce qu'elle contient, c'est à dire un ensemble de classe structurant nos données d'une manière lisible pour nous (en tout cas pour moi). Il sert (servira, en fait) à la création puis sérialisation des graphes avant leurs utilisations.
 
 Ce sur quoi nous pourrions travailler dans l'avenir :
 - Passer des données GTFS au package transport (voir même directement aux graphes !)

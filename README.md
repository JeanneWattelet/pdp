# pdp

 - Package domain : Classe GrapheTrajet et tout ce qui concerne sa création, son utilisation pour un plus court chemin et sa sérialisaiton. Les fonctionnalités présentes pour l'instant sont : partir à, arriver à, le moins de marche, le moins d'attente et ne pas prendre tel moyen de transport. Il y a aussi quelques classes de tests et des classes inutiles comme Graphe ou Oiseaux.
 - Package transport : Classe Carte et tout ce qu'elle contient, c'est à dire un ensemble de classe structurant nos données d'une manière lisible pour nous (en tout cas pour moi). Il sert à la création puis sérialisation des graphes avant leurs utilisations.
- Le reste des package est utilisé pour la gestion des données et de l'interface. Cela prend en compte les données brutes, les doonées modifiées, les images et les classes.
- Les librairies devraient être dans le dossier pdp. Dans tous les cas, elles sont obligatoires pour que le programme fonctionne.

JGraphT peut être récupéré à l'adresse suivante : https://jgrapht.org

Le téléchargement de javafx11 peut se faire directement à travers eclise de la manière suivante :
 Help -> Eclipse MarketSpace -> Recherche : javafx11 -> installer e(fx)clipse

Liste des fichiers que nous ne pouvons pas télécharger ici à cause de leur taille :
- stop_times.txt : pour les données GTFS des bus (56Mo)
- fv_adresse_p.geojson : Adresses de Bordeaux (80Mo)
- Les listes d'ArcTrajet : les arêtes d'attente, de marche et de transport.

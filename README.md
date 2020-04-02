# pdp
Les dernières modifications sont dans PDP/src. Je ne comprends toujours pas comment ça marche...

Résumé de ce que nous avons enregistré ici :
 - Package domain : Classe GrapheTrajet et tout ce qui concerne sa création, son utilisation pour un plus court chemin et sa sérialisaiton. Il y a aussi quelques classes de tests et des classes inutiles comme Graphe ou Oiseaux.
 - Package transport : Classe Carte et tout ce qu'elle contient, c'est à dire un ensemble de classe structurant nos données d'une manière lisible pour nous (en tout cas pour moi). Il sert (servira, en fait) à la création puis sérialisation des graphes avant leurs utilisations.
 
 Ce sur quoi nous pourrions travailler dans l'avenir :
 - Passer des données GTFS à la carte (voir même directement aux graphes !)
 - Permettre l'utilisation de A* avec sérialisation 1. en sérialisant la liste des sommets (lourd) ou 2. en créant une fonction de parcours des arcs pour récupérer les sommets (long ?) (RAPPEL : la création d'une heuristique par défaut de A* demande de lui donner la liste de tous les sommets).

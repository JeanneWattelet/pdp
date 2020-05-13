# pdp

Le problème qui nous a été posé était la recheche de chemins multi-modaux et son application aux réseaux de transport d'une ville. La ville que nous avons choisi comme exemple est la métropole de Bordeaux mais le code est assez général pour être appliqué à n'importe quelle ville à partir du moment où nous avons les informations suffisantes sur celle-ci.

Notre application se decoupe en plusieurs packages :
 - Package domain : Classe GrapheTrajet et tout ce qui concerne sa création, son utilisation pour un plus court chemin et sa sérialisaiton.
 - Package transport : Classe Carte et tout ce qu'elle contient, c'est à dire un ensemble de classe structurant nos données d'une manière lisible que les données GTFS. Il sert à la création puis sérialisation des graphes avant leurs utilisations.
- Le reste des package est utilisé pour la gestion des données et de l'interface. Cela prend en compte les données brutes, les doonées modifiées, les images et les classes.
- Les librairies sont aussi présentes en partie dans ce répertoire. Elles sont obligatoires pour que le programme fonctionne.

Les libraires qui nous utilisons sont :
- JGraphT qui peut être récupéré à l'adresse suivante : https://jgrapht.org ou https://github.com/jgrapht/jgrapht
- UnfoldingMap qui peut être récupéré à l'adresse suivante : http://unfoldingmaps.org ou https://github.com/tillnagel/unfolding
- javafx11 done le téléchargement peut se faire directement à travers eclise de la manière suivante : Help -> Eclipse MarketSpace -> Recherche : javafx11 -> installer e(fx)clipse

Certains fonctionnalités que nous utilisons ne sont pas compatible avec les dernières versions de java. Il est conseillé d'utiliser java1.8.

Certains fichiers de données ne peuvent pas être téléchargés sur github à cause de leur volume. En voilà la liste exaustive :
- stop_times.txt : pour les données GTFS des bus (56Mo) que vous pouvez retrouver à l'adresse suivante : https://www.data.gouv.fr/fr/datasets/gtfs-du-reseau-de-transport-tbm-offre-de-service-bus-et-tram/
- fv_adresse_p.geojson : Adresses de Bordeaux (80Mo) que vous pouvez retrouver à l'adresse suivante : https://opendata.bordeaux-metropole.fr/explore/dataset/fv_adresse_p/information/
- Les listes d'ArcTrajet : les arêtes d'attente, de marche et de transport. Ces fichiers doivent être créés à l'aide des fonctions de notre application. Elles seront à télécharger à l'adresse : ******* et à inclure dans le fichier src.

Toutes les informations sur ce projet sont disponibles dans le rapport suivant : https://fr.overleaf.com/read/cgkwnkjcrccv.

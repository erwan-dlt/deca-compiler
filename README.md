# Compilateur Déca

Compilateur pédagogique développé en équipe dans le cadre du projet Génie Logiciel de Grenoble INP – Ensimag / Phelma (2025).

Le compilateur transforme un programme Déca en assembleur pour la machine abstraite IMA. Le pipeline comprend une analyse lexicale et syntaxique avec **ANTLR 4**, la construction d'un **arbre syntaxique abstrait**, des vérifications de types et de portée, puis la génération de code.

## Périmètre

Le socle impératif prend en charge les variables `int`, `float` et `boolean`, les expressions arithmétiques et logiques, les affectations, les conversions numériques, `if` / `else`, `while`, les lectures et les affichages. Les diagnostics indiquent le fichier et la position de l'erreur.

Les classes, champs et méthodes sont représentés dans la grammaire et l'AST. Leur analyse contextuelle reste partielle et **la génération de code objet n'est pas implémentée** : un programme contenant des classes est refusé à cette étape. La bibliothèque expérimentale `Math.decah` utilise ces fonctionnalités et ne constitue donc pas une démonstration exécutable du compilateur actuel.

## Installation et compilation

Prérequis : **JDK 17**, **Maven 3.8+** et un shell POSIX pour les scripts. Le premier build télécharge les dépendances Maven. Sous Windows, utiliser WSL pour les scripts, ou lancer le JAR directement avec Java.

```sh
mvn clean verify
./src/main/bin/decac examples/deca/factorial.deca
```

Le fichier `examples/deca/factorial.ass` est généré à côté du source. Pour l'exécuter, le simulateur **IMA**, distribué séparément dans l'environnement pédagogique, doit être présent dans le `PATH` :

```sh
ima examples/deca/factorial.ass
# Résultat attendu : 720
```

IMA n'est nécessaire ni pour construire le compilateur ni pour lancer les tests JUnit. Les instructions Docker historiques sont disponibles dans `docker/README.md` et dépendent de ressources de l'école.

Le build produit également un JAR autonome :

```sh
java -jar target/Deca-0.0.1-SNAPSHOT-jar-with-dependencies.jar examples/deca/factorial.deca
```

## Ligne de commande

| Option | Effet |
| --- | --- |
| `-p` | Analyse syntaxique et réécriture du programme sur la sortie standard ; aucun assembleur généré. |
| `-v` | Vérifications contextuelles uniquement ; silence en cas de succès. |
| `-n` | Désactive les contrôles arithmétiques et de pile générés. Les erreurs de lecture restent contrôlées. |
| `-r X` | Utilise les registres `R0` à `R{X-1}`, avec `4 ≤ X ≤ 16`. |
| `-d` | Active les traces sur la sortie d'erreur ; peut être répété. |
| `-P` | Compile plusieurs fichiers en parallèle. Avec `-p`, la sortie reste séquentielle pour préserver les programmes. |
| `-b` | Affiche les auteurs ; s'utilise seul. |

`-p` et `-v` sont incompatibles. Plusieurs fichiers `.deca` peuvent être fournis. Le code de retour est non nul si une compilation échoue.

```sh
./src/main/bin/decac -p examples/deca/factorial.deca
./src/main/bin/decac -v examples/deca/factorial.deca
./src/main/bin/decac -P examples/deca/factorial.deca examples/deca/numeric.deca
```

## Tests

```sh
mvn test
```

La suite par défaut couvre les tests unitaires existants et les régressions de la ligne de commande, des diagnostics et de l'assembleur émis. Elle ne remplace pas l'exécution sur IMA.

Le corpus historique contient 286 fichiers `.deca`, répartis entre analyse syntaxique, vérification contextuelle et génération de code. Les scripts associés peuvent être lancés individuellement après `mvn test`. Les scripts historiques inspectent parfois les diagnostics plutôt que le seul code de retour ; ils ne constituent pas à eux seuls une preuve de conformité complète.

Pour lancer aussi les scripts historiques liés au cycle Maven, dans l'environnement de l'école avec IMA :

```sh
mvn test -Pschool-tests
```

Ce profil inclut des scénarios objet et des attentes historiques ; il peut signaler les limitations décrites ci-dessous. La CI exécute la suite portable, sans dépendre d'IMA.

## Organisation

| Chemin | Contenu |
| --- | --- |
| `src/main/antlr4/` | Grammaires ANTLR du lexer et du parser. |
| `src/main/java/fr/ensimag/deca/tree/` | AST, vérification contextuelle et génération de code par nœud. |
| `src/main/java/fr/ensimag/deca/context/` | Types, définitions et environnements de portée. |
| `src/main/java/fr/ensimag/deca/codegen/` | Gestion des registres et des étiquettes. |
| `src/main/java/fr/ensimag/ima/pseudocode/` | Représentation Java des instructions IMA. |
| `src/test/` | Tests JUnit, programmes Déca et scripts historiques. |
| `examples/deca/` | Petits programmes de démonstration. |
| `docs/` | Rapports pédagogiques d'origine et notes techniques. |

## Limites connues

- La génération objet, les appels de méthodes, `instanceof` et les conversions de références restent incomplets. `-v` n'est pas une validation exhaustive du sous-langage objet.
- L'allocation de registres ne sauvegarde pas les valeurs intermédiaires sur la pile. Une expression trop imbriquée peut dépasser le nombre de registres disponibles, notamment avec `-r 4` ; le compilateur émet alors un diagnostic explicite.
- L'extension trigonométrique est conservée comme travail expérimental, avec ses documents d'analyse. Elle n'est pas couverte par les tests portables.
- Les rapports PDF décrivent le travail à sa remise initiale et peuvent différer du comportement actuel. Le présent README décrit le périmètre du code.

## Auteurs et provenance

Équipe **gl49** : Colin Dunand, Baptiste Van der Stracten, Julia Mulder, Abel Prud'Homme Lacroix et Erwan Didelot. Le projet repose sur un squelette pédagogique Ensimag ; les attributions présentes dans les sources sont conservées.

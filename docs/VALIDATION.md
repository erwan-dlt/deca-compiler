# Validation technique

Environnement de vérification : OpenJDK 17, Maven 3.9.9, Linux.

## Construction et tests portables

`mvn clean verify` termine avec **BUILD SUCCESS** : **35 tests JUnit**, zéro échec, zéro erreur, aucun test ignoré. Le JAR autonome et le lanceur de `target/package/` sont produits.

Les 28 cas de régression complètent les 7 tests unitaires d'origine. Ils couvrent notamment :

- la séparation des sorties standard et d'erreur, et le ré-analysage de la sortie de `-p` ;
- les limites de registres et les options incompatibles ;
- les affectations imbriquées, conversions numériques et expressions isolées ;
- la négation, les conditions booléennes et l'affichage hexadécimal ;
- les erreurs de types, les littéraux hors plage et leurs positions ;
- le refus explicite de la génération objet ;
- la décompilation des classes et des chaînes échappées.

Les démonstrations `factorial.deca` et `numeric.deca` compilent avec le lanceur source et le JAR, y compris en parallèle. Le lanceur empaqueté accepte aussi la vérification contextuelle.

## Corpus historique

Les scripts `basic-lex.sh`, `avance-lex.sh`, `basic-synt.sh`, `avance-synt.sh`, `basic-context.sh` et `basic-decac.sh` terminent avec succès lors de la revue.

Une compilation des 44 fichiers du répertoire `codegen/valid/` réussit pour 32 fichiers. Les 12 autres comprennent les exemples objet et des fichiers rangés dans `not_working/`, dont certains sont syntaxiquement invalides. Ce résultat porte sur la compilation, pas sur l'exécution de l'assembleur.

Le script `avance-cont.sh` ne passe pas intégralement : il s'arrête notamment sur `extends_attrib.deca`, qui utilise l'accès aux champs, encore non implémenté. Les scripts historiques fondés sur la recherche de messages peuvent mal classer les exceptions de fonctionnalités non prises en charge.

## Ce qui n'est pas validé

Le simulateur IMA n'était pas disponible. Les sorties des programmes assembleur et les scripts `basic-gencode.sh`, `avance-gencode.sh` et `common-tests.sh` n'ont donc pas été vérifiés à l'exécution. La couverture JaCoCo n'a pas été mesurée : aucun pourcentage de couverture n'est revendiqué.

Les limites objet, d'allocation de registres et de l'extension mathématique sont détaillées dans le README. Les rapports pédagogiques d'origine sont conservés comme documents historiques.

#! /bin/sh

# Auteur : gl49
# Version initiale : 01/01/2025

# Script de test de la syntaxe avancé
# On teste tous les fichiers valides et invalides.

# dans le cas du fichier valide, on teste seulement qu'il n'y a pas eu
# d'erreur. Il faudrait tester que l'arbre donné est bien le bon. Par
# exemple, en stoquant la valeur attendue quelque part, et en
# utilisant la commande unix "diff".


cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_synt_valide () {
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_synt sur $1."
        exit 1
    else
        echo "Succes attendu de test_synt sur $1."
    fi
}

test_synt_invalide () {
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_synt sur $1."
    else
        echo "Succes inattendu de test_synt sur $1."
        exit 1
    fi
}    

#tests censés être valides syntaxiquement
for cas_de_test in src/test/deca/syntax/valid/provided/*.deca
do
    test_synt_valide "$cas_de_test"
done

for cas_de_test in src/test/deca/syntax/valid/parser/*.deca
do
    test_synt_valide "$cas_de_test"
done

#tests censés être invalides syntaxiquement
for cas_de_test in src/test/deca/syntax/invalid/provided/*.deca
do
    test_synt_invalide "$cas_de_test"
done

for cas_de_test in src/test/deca/syntax/invalid/parser/*.deca
do
    test_synt_invalide "$cas_de_test"
done





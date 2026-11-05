#! /bin/sh

# Auteur : gl49
# Version initiale : 01/01/2025

# Script de test du contexte avancé
# On teste tous les fichiers valides et invalides.


cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_cont_valide () {
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_context sur $1."
        exit 1
    else
        echo "Succes attendu de test_context sur $1."
    fi
}

test_cont_invalide () {
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_context sur $1."
    else
        echo "Succes inattendu de test_context sur $1."
        exit 1
    fi
}    

#tests censés être valides contextuellement
for cas_de_test in src/test/deca/context/valid/provided/*.deca
do
    test_cont_valide "$cas_de_test"
done

for cas_de_test in src/test/deca/context/valid/not_provided/*.deca
do
    test_cont_valide "$cas_de_test"
done

#tests censés être invalides contextuellement
for cas_de_test in src/test/deca/context/invalid/provided/*.deca
do
    test_cont_invalide "$cas_de_test"
done

for cas_de_test in src/test/deca/context/invalid/not_provided/*.deca
do
    test_cont_invalide "$cas_de_test"
done





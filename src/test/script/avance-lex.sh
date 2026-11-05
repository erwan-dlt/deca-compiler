#! /bin/sh

# Auteur : gl49
# Version initiale : 01/01/2025

# Script de test de la lexicographie avancé
# On teste tous les fichiers valides et invalides.

# Ce script peut lancer test_lex sur un grand nombre 
#de fichiers à la suite.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_lex_valide () {
    # $1 = premier argument.
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec inattendu pour test_lex sur $1."
        exit 1
    else
        echo "Succes attendu de test_lex sur $1."
    fi
}

test_lex_invalide () {
    # $1 = premier argument.
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_lex sur $1."
    else
        echo "Succes inattendu de test_lex sur $1."
        exit 1
    fi
}    

for cas_de_test in src/test/deca/syntax/valid/lexer/*.deca
do 
    test_lex_valide "$cas_de_test"
done

for cas_de_test in src/test/deca/syntax/invalid/lexer/*.deca
do
    test_lex_invalide "$cas_de_test"
done


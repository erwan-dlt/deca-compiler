#! /bin/bash

# Auteur : gl49

# Description :
# Automatisation avancée de la génération de tests pour les fichiers .deca dans gencode/valid/not_provided.
# 1. Compile chaque fichier .deca avec decac.
# 2. Exécute le fichier .ass généré avec ima.
# 3. Compare le résultat obtenu avec le fichier attendu (situé dans le dossier expected).

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"
TEST_DIR="src/test/deca/codegen/valid/not_provided"
EXPECTED_DIR="$TEST_DIR/expected"

test_gencode_valide () {
    local test_file="$1"
    local base_name=$(basename "$test_file" .deca)
    local ass_file="${TEST_DIR}/${base_name}.ass"
    local expected_file="${EXPECTED_DIR}/${base_name}.expected"
    local output_file="${TEST_DIR}/${base_name}.out"

    rm -f "$ass_file" "$output_file" 2>/dev/null

    echo "Compilation de $test_file..."
    decac "$test_file" || { echo "Erreur de compilation pour $test_file"; exit 1; }

    if [ ! -f "$ass_file" ]; then
        echo "Fichier .ass non généré pour $test_file"
        exit 1
    fi

    echo "Exécution de $ass_file avec ima..."
    ima "$ass_file" > "$output_file" || { echo "Erreur d'exécution avec ima pour $test_file"; exit 1; }

    if [ ! -f "$expected_file" ]; then
        echo "Fichier attendu (.expected) manquant pour $test_file"
        exit 1
    fi

    if diff -q "$output_file" "$expected_file" >/dev/null; then
        echo "Test réussi pour $test_file"
    else
        echo "Test échoué pour $test_file"
        echo "Résultat obtenu :"
        cat "$output_file"
        echo "Résultat attendu :"
        cat "$expected_file"
        exit 1
    fi
}

for test_file in "$TEST_DIR"/*.deca; do
    test_gencode_valide "$test_file"
done

echo "Tous les tests de gencode ont été exécutés avec succès."

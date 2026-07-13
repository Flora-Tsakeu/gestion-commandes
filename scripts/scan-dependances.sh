#!/usr/bin/env bash
set -euo pipefail

LISTE_BLOCAGE="scripts/denylist-dependances.txt"
mkdir -p target
RESOLUTION=$(mvn -B dependency:list -DoutputFile=/dev/stdout -Dsort=true 2>/dev/null | grep -E '^\s+[a-zA-Z0-9._-]+:[a-zA-Z0-9._-]+:jar:' || true)

echo "$RESOLUTION" > target/dependances-resolues.txt

trouve=0
while IFS= read -r entree; do
    [ -z "$entree" ] && continue
    if grep -qF "$entree" target/dependances-resolues.txt; then
        echo "dependance vulnerable detectee dans le graphe de resolution : $entree"
        trouve=1
    fi
done < "$LISTE_BLOCAGE"

if [ "$trouve" -eq 1 ]; then
    echo "le scan de securite a identifie au moins une dependance non conforme, blocage du pipeline"
    exit 1
fi

echo "aucune dependance de la liste de blocage n'a ete trouvee, scan valide"

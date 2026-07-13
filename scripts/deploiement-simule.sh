#!/usr/bin/env bash
set -euo pipefail

JAR=$(ls target/*.jar 2>/dev/null | grep -v original | head -n1 || true)

if [ -z "$JAR" ]; then
    echo "aucun jar trouve dans target, le packaging semble avoir echoue en amont"
    exit 1
fi

echo "artefact pret pour le deploiement : $JAR"

if [ -z "${DEPLOY_TOKEN:-}" ]; then
    echo "le token d'authentification vers l'environnement cible est absent"
    exit 1
fi

if [ "${#DEPLOY_TOKEN}" -lt 16 ]; then
    echo "le token d'authentification fourni ne respecte pas le format attendu"
    exit 1
fi

echo "authentification acceptee, transfert de l'artefact vers l'environnement cible"
sleep 2
echo "deploiement simule termine avec succes"

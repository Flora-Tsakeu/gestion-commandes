# gestion-commandes

API REST de gestion de commandes et de stocks (Spring Boot 3.3, Maven,
H2). Ce depot sert de generateur controle de logs CI/CD pour un memoire
de master sur la detection d'anomalies et l'analyse des causes racines
dans les pipelines CI/CD .

## Lancer le projet en local

```bash
mvn spring-boot:run
```

L'API demarre sur `http://localhost:8080`, base H2 en memoire.

## Endpoints principaux

- `POST /api/produits`, `GET /api/produits` (pagination, recherche par
  categorie, texte libre, plage de prix), `PATCH .../reapprovisionnement`,
  `PATCH .../desactivation` et `.../reactivation`, `GET .../disponibilite`
- `POST /api/commandes`, `GET /api/commandes` (par client, statut,
  periode, pagination), `POST .../annulation`, `POST .../duplication`,
  `GET .../integrite`, `GET .../lignes`, `PATCH .../notes`
- `GET /api/commandes/statistiques`, `GET /api/commandes/resume-quotidien`
- `GET /api/produits/resume-stock`, `GET /api/produits/top-ventes`
- `GET /api/produits/export`, `GET /api/commandes/export` (CSV)
- `GET /api/info` (nom et version de l'application)

## Pipeline CI/CD

Le workflow `.github/workflows/ci-cd.yml` execute 6 jobs sequentiels :
setup et cache Maven, qualite du code (Checkstyle), tests unitaires
(`mvn test`), tests d'integration (`mvn verify`, Failsafe), scan de
securite (liste de blocage interne, deterministe), packaging et
simulation de deploiement.

## Etat du developpement

La Vague 1 (runs nominaux, commits 15 a 124) est cloturee : 110 runs
verts couvrant l'evolution complete du code sans panne injectee. Les
scenarios de pannes controlees (Vague A, F1 a F4) et les runs
d'evaluation (Vagues B et C) font l'objet d'une phase separee.

Voir les fichiers `SEQUENCE_COMMITS_*.md` et `NOTES_ARCHITECTURE.md`
pour le detail commit par commit de la construction du projet.

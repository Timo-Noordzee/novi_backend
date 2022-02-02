# Installatiehandleiding

#### Auteur: Timo Noordzee

Dit bestand bevat instructies voor het installeren en starten van de garage applicatie.

* [1. Benodigdheden](#1-benodigdheden)
    + [1.1 Docker installeren](#11-docker-installeren)
    + [1.2 PostgreSQL database](#12-postgresql-database)
* [2. Applicatie starten](#2-applicatie-starten)
    + [2.1 Environment variables opzetten](#21-environment-variables-opzetten)
    + [2.2 Docker run](#22-docker-run)

## 1. Benodigdheden

1. Docker
2. PostgreSQL database

### 1.1 Docker installeren

De garage backoffice software is beschikbaar als Docker image om de installatie te vereenvoudigen. Om deze image te
kunnen runnen dient Docker geinstalleerd te zijn.

Indien Docker nog niet geïnstalleerd is volg dan de instructies op https://docs.docker.com/get-docker/

### 1.2 PostgreSQL database

De applicatie maakt gebruikt van een PostgreSQL database voor het opslaan van gegevens. Er zijn meerdere opties om de
PostgreSQL database te draaien.

1. Gebruik maken van een cloud provider
2. Postgres lokaal installeren https://www.postgresql.org/download/
3. Postgres Docker image runnen middels onderstaande commando<br/>
   `docker run -d -p 5432:5432 --name my-postgres -e POSTGRES_PASSWORD=mysecretpassword postgres`

## 2. Applicatie starten

Als Docker geïnstalleerd is en je toegang hebt tot een PostgreSQL database kan je de applicatie draaien. Op Docker Hub
is de meeste recente versie van de applicatie te
vinden. https://hub.docker.com/repository/docker/timonoordzee/novi_backend

### 2.1 Environment variables opzetten

Om de applicatie te koppelen aan de database wordt gebruik gemaakt van environment variables. De meest eenvoudige optie
voor het zetten van de environment variables is door het aanmaken van een `.env` bestand met daarin onderstaande
content.

```
JDBC_URL=jdbc:postgresql://<host>:<port>/<database>
JDBC_USER=<user>
JDBC_PASS=<password>
```

De placeholders `<host>`, `<port>`, `<database>`, `<user>` en `<password>` dienen vervangen te worden door de PostgreSQL
database credentials. Indien de PostgreSQL database draait in een docker container dient voor `<host>` mogelijk de
waarde `host.docker.internal` gebruikt te worden i.p.v. `localhost`.

### 2.2 Docker run

Om de image te kunnen runnen dien je onderstaande commando uit te voeren. De placeholder `<path>` dient vervangen te
worden door het eerder aangemaakte `.env` bestand. De placeholder `<port>` dient vervangen te worden door de poort
waarop je de applicatie wil draaien.

`docker run --env-file <path> -p 8080:<port> timonoordzee/novi_backend:latest`

Na het uitvoeren van bovenstaande comando zou de applicatie moeten starten. De applicatie is vervolgens te benaderen op
localhost met de gespecificeerde poort.


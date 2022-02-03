# Installatiehandleiding

#### Auteur: Timo Noordzee

Dit bestand bevat instructies voor het installeren en starten van de garage applicatie. Daarnaast bevat dit document
instructies voor het importeren van testdata en een overzicht van de verschillende endpoints.

* [1. Benodigdheden](#1-benodigdheden)
    + [1.1 Docker installeren](#11-docker-installeren)
    + [1.2 PostgreSQL database](#12-postgresql-database)
* [2. Applicatie starten](#2-applicatie-starten)
    + [2.1 Environment variables opzetten](#21-environment-variables-opzetten)
    + [2.2 Docker run](#22-docker-run)
* [3. Demo data importeren](#3-demo-data-importeren)
* [4. Demo credentials](#4-demo-credentials)
* [5. Endpoints](#5-endpoints)
    + [5.1 Employee](#51-employee)
    + [5.2 Customer](#52-customer)
    + [5.3 Vehicle](#53-vehicle)
    + [5.4 VehiclePapers](#54-vehiclepapers)
    + [5.5 Shortcoming](#55-shortcoming)
    + [5.6 Part](#56-part)
    + [5.7 Action](#57-action)
    + [5.8 Repair](#58-repair)
    + [5.9 Invoice](#59-invoice)
* [6. Foutmeldingen](#6-foutmeldingen)

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

Na het uitvoeren van bovenstaande commando zou de applicatie moeten starten. De applicatie is vervolgens te benaderen op
localhost met de gespecificeerde poort.

## 3. Demo data importeren

Om de demo data te kunnen importeren is het belangrijk dat de nodige tabellen al bestaat. De tabellen worden automatisch
aangemaakt bij het starten van de applicatie. Doorloop dus eerst de stappen onder 1 en 2 voordat je verder gaat met het
importeren van demo data.

Het bestand `data.sql` bevat de demo data die gebruikt kan worden voor het testen van de database. Dit bestand is te
vinden op de volgende locatie `src/main/resources/data.sql`. Er zijn meerdere manieren om deze demo data te importeren
in de PostgreSQL database. In dit document staan instructies voor het importeren van deze data
middels [Jetbrains Datagrip](https://www.jetbrains.com/datagrip/)

1. Open Datagrip en maak een nieuw project.
2. Voeg een nieuwe PostgreSQL datasource toe aan het project middels `file -> new -> datasource -> PostgreSQL`
   ![Add project](images/add_project.png)
3. Vul de credentials van de PostgresSQL database in en klik op `Test Connection`
4. Indien de credentials kloppen klik je op `Apply` en daarna `OK` om de datasoruce op te slaan
   ![Add Datasource](images/add_datasource.png)
5. Voeg een script toe aan de Run/Debug configurations via `Run -> Edit Configurations`
   ![Edit configuration](images/edit_configurations.png)
6. Klik op het plusje links boven en selecteer de optie `Database script`
7. Geef een naam aan de configuration, bijvoorbeeld "Import demo data"
8. Klik op het plusje onder Target data source / schema en selecteer de bij stap 4 toegevoegde database
   ![Target datasource](images/select_target_datasource.png)
9. Selecteer de optie `Script files` i.p.v. `Script text`
10. Klik op het plusje en selecteer het bestand `data.sql` in `src/main/resources/data.sql`
    ![Add script file](images/script_files.png)
11. Sla de configuratie op door op `Apply` en vervolgens `OK` te klikken
12. Selecteer de configuratie en klik vervolgens op het 'run' icoontje (groene play icon)<br/>
    ![Run configuration](images/run_configuration.png)

Na het uitvoeren van bovenstaande 12 stappen is de demo data uit `data.sql` geïmporteerd in de database.

## 4. Demo credentials

De demo data bevat onder andere meerdere werknemers met verschillende rollen om de applicatie te kunnen testen. De
credentials zijn in onderstaande tabel te vinden.

| **email**                          | **wachtwoord** | **rol**                   |
|------------------------------------|----------------|---------------------------|
| `bertus.bick@novi-garage.nl`       | `hm4&sE83`     | admin                     |
| `joep.kamphuis@novi-garage.nl`     | `s69#5Lzk`     | administratief medewerker |
| `arend.de.porter@novi-garage.nl`   | `LG7Ef$tj`     | backoffice medewerker     |
| `anton.van.neste@novi-garage.nl`   | `kLQK74!?`     | monteur                   |
| `natalie.westhoven@novi-garage.nl` | `#6HD9bMH`     | kassamedewerker           |

De applicatie maakt gebruik
van [Basic authentication](https://en.wikipedia.org/wiki/Basic_access_authentication#:~:text=HTTP%20Basic%20authentication%20(BA)%20implementation,fields%20in%20the%20HTTP%20header.)
het e-mailadres wordt gebruikt als username. Om een request uit te voeren namens Bertus Bick dient dus de volgende
header toegevoegd te worden aan de request:

`Authorization: Basic YmVydHVzLmJpY2tAbm92aS1nYXJhZ2Uubmw6aG00JnNFODM=`

## 5. Endpoints

De applicatie bevat verschillende CRUD endpoints voor het lezen en schrijven van en naar de database. Hieronder staat
per endpoint beschreven welke rollen de request kunnen uitvoeren. Naast onderstaande beschrijven is er ook een
documentatie pagina beschikbaar bij het starten van de applicatie. De documentatie is middels onderstaande URL te
benaderen, waar `<host>` vervangen dient te worden door de base URL.

`<host>/v3/api/index.html`

De OpenAPI 3.0.1 specificatie is te vinden op de volgende locatie `documentation/api.json`. Dit bestand kan geïmporteerd
worden in Postman. Instructies voor het importeren van de OpenAPI specificatie zijn op de volgende pagina te
vinden https://learning.postman.com/docs/integrations/available-integrations/working-with-openAPI/

### 5.1 Employee

Deze endpoints zijn voor het beheren van werknemers

| method | path            | roles |
|--------|-----------------|-------|
| GET    | /employees      | admin |
| GET    | /employees/{id} | admin |
| POST   | /employees      | admin |
| PUT    | /employees/{id} | admin |
| DELETE | /employees/{id} | admin |

**Voorbeeld payload voor het toevoegen van een werknemer**

````json
{
  "email": "paulus-broeren@novi-garage.nl",
  "name": "Paulus",
  "password": "QZ-r6XA$",
  "role": "mechanic",
  "surname": "Broeren",
  "id": "74f61000-8193-4b75-9a50-3ced99961988"
}
````

**Voorbeeld payload voor het updaten van een werknemer**

````json
{
  "name": "Marko",
  "surname": "Valder",
  "password": "M5sj-qJE",
  "role": "admin"
}
````

### 5.2 Customer

Deze endpoints zijn voor het beheren van klanten

| method | path            | roles                            |
|--------|-----------------|----------------------------------|
| GET    | /customers      | admin, administratief medewerker |
| GET    | /customer/{id}  | admin, administratief medewerker |
| POST   | /customers      | admin, administratief medewerker |
| PUT    | /customers/{id} | admin, administratief medewerker |
| DELETE | /customers/{id} | admin, administratief medewerker |

**Voorbeeld payload voor het toevoegen van een klant**

````json
{
  "email": "gert.veldman@klant.novi-garage.nl",
  "name": "Gert",
  "phone": "+31 6 12345678",
  "surname": "Veldman",
  "id": "8102cd51-1f2f-4051-974f-b20dda3ea5ec"
}
````

**Voorbeeld payload voor het updaten van een klant**

````json
{
  "name": "Ingrid",
  "surname": "de Vries",
  "email": "ingrid.de.vries@klant.novi-garage.nl",
  "phone": "+31 6 87654321"
}
````

### 5.3 Vehicle

Deze endpoints zijn voor het beheren van voertuigen

| method | path                  | roles                                     |
|--------|-----------------------|-------------------------------------------|
| GET    | /vehicles             | admin, administratief medewerker, monteur |
| GET    | /vehicle/{id}         | admin, administratief medewerker, monteur |
| POST   | /vehicles             | admin, administratief medewerker          |
| PUT    | /vehicles/{id}        | admin, administratief medewerker          |
| DELETE | /vehicles/{id}        | admin, administratief medewerker          |
| GET    | /vehicles/{id}/papers | admin, administratief medewerker, monteur |
| POST   | /vehicles/{id}/papers | admin, administratief medewerker          |

**Voorbeeld payload voor toevoegen van een voertuig**

````json
{
  "brand": "BMW",
  "customerId": "8102cd51-1f2f-4051-974f-b20dda3ea5ec",
  "license": "G-373-LB",
  "make": "330I",
  "vin": "1FTPW14V87FA46384",
  "year": 2019
}
````

**Voorbeeld payload voor het updaten van een voertuig**

````json
{
  "license": "54-PR-VB",
  "brand": "Peugeot",
  "make": "206",
  "year": 2004,
  "customerId": "8102cd51-1f2f-4051-974f-b20dda3ea5ec"
}
````

### 5.4 VehiclePapers

Deze endpoints zijn voor het beheren van de voertuigpapieren

| method | path                | roles                                     |
|--------|---------------------|-------------------------------------------|
| GET    | /vehiclePapers      | admin, administratief medewerker, monteur |
| GET    | /vehiclePapers/{id} | admin, administratief medewerker, monteur |
| DELETE | /vehiclePapers/{id} | admin, administratief medewerker          |

### 5.5 Shortcoming

Deze endpoints zijn voor het beheren van tekortkomingen voor een voertuig

| method | path               | roles          |
|--------|--------------------|----------------|
| GET    | /shortcomings      | admin, monteur |
| GET    | /shortcomings/{id} | admin, monteur |
| POST   | /shortcomings      | admin, monteur |
| PUT    | /shortcomings/{id} | admin, monteur |
| DELETE | /shortcomings/{id} | admin, monteur |

**Voorbeeld payload voor het toevoegen van een tekortkoming**

````json
{
  "description": "De remschijven zijn versleten",
  "vehicleId": "1FTPW14V87FA46384",
  "id": "e3604bc3-8859-4e6e-874a-316370d33f5d"
}
````

**Voorbeeld payload voor het updaten van een tekortkoming**

````json
{
  "description": "De lamp links achter werkt niet meer"
}
````

### 5.6 Part

Deze endpoints zijn voor het beheren van onderdelen

| method | path        | roles                                 |
|--------|-------------|---------------------------------------|
| GET    | /parts      | admin, backoffice medewerker, monteur |
| GET    | /parts/{id} | admin, backoffice medewerker, monteur |
| POST   | /parts      | admin, backoffice medewerker          |
| PUT    | /parts/{id} | admin, backoffice medewerker          |
| DELETE | /parts/{id} | admin, backoffice medewerker          |

**Voorbeeld payload voor het toevoegen van een onderdeel**

`````json
{
  "name": "Motorblok",
  "price": 59.99,
  "id": "1bde1162-7da8-4a5b-b76c-becd8b7f9173",
  "stock": 5
}
`````

**Voorbeeld payload voor het updaten van een onderdeel**

````json
{
  "name": "Remschijf",
  "price": 79.95,
  "stock": 10
}
````

### 5.7 Action

Deze endpoints zijn voor het beheren van handelingen

| method | path          | roles                                     |
|--------|---------------|-------------------------------------------|
| GET    | /actions      | admin, administratief medewerker, monteur |
| GET    | /actions/{id} | admin, administratief medewerker, monteur |
| POST   | /actions      | admin, administratief medewerker          |
| PUT    | /actions/{id} | admin, administratief medewerker          |
| DELETE | /actions/{id} | admin, administratief medewerker          |

**Voorbeeld payload voor het toevoegen van een handeling**

````json
{
  "name": "Ruitenwisser vervangen",
  "price": 78194917.86535971,
  "id": "fc6cbf76-837a-4ea6-a20b-0e22f817e55a"
}
````

**Voorbeeld payload voor het updaten van een handeling**

````json
{
  "name": "Remschrijven vervangen",
  "price": 44.99
}
````

### 5.8 Repair

Deze endpoints zijn voor her beheren van reparaties

| method | path                         | roles                                                      |
|--------|------------------------------|------------------------------------------------------------|
| GET    | /repairs                     | admin, administratief medewerker, monteur, kassamedewerker |
| GET    | /repairs/{id}                | admin, administratief medewerker, monteur, kassamedewerker |
| POST   | /repairs                     | admin, administratief medewerker, monteur                  |
| PUT    | /repairs/{id}                | admin, administratief medewerker, monteur                  |
| DELETE | /repairs/{id}                | admin, administratief medewerker, monteur                  |
| POST   | /repairs/{id}/lines          | admin, administratief medewerker, monteur                  |
| PUT    | /repairs/{id}/lines/:lineId  | admin, administratief medewerker, monteur                  |
| DELETE | /repairs/{id}/lines/{lineId} | admin, administratief medewerker, monteur                  |

**Voorbeeld payload voor het toevoegen van een reparatie**

````json
{
  "vehicleId": "1FTPW14V87FA46384",
  "id": "21bdc260-d585-41e8-950d-56f5ea9208cc",
  "remarks": "De klant is niet akkoord gegaan met de reparatie, omdat de kosten te hoog zijn",
  "status": 1
}
````

**Voorbeeld payload voor het updaten van een reparatie**

````json
{
  "remarks": "Reparatie voltooid, alle tekortkomingen zijn opgelost",
  "status": 3
}
````

**Voorbeeld payload voor het toevoegen van regels aan een reparatie**

````json
{
  "parts": [
    {
      "id": "f71f2846-4e53-45ac-b8c0-6edb93728698",
      "amount": 2
    }
  ],
  "actions": [
    {
      "id": "dcfca82f-c4b2-4b5d-ba42-7ac1729ef4df"
    }
  ],
  "custom": [
    {
      "name": "Software updaten",
      "price": 29.99,
      "amount": 2,
      "type": 1
    }
  ]
}
````

### 5.9 Invoice

Deze endpoints zijn voor het beheren van facturen

| method | path           | roles                  |
|--------|----------------|------------------------|
| GET    | /invoices      | admin, kassamedewerker |
| GET    | /invoices/{id} | admin, kassamedewerker |
| POST   | /invoices      | admin, kassamedewerker |
| PUT    | /invoices/{id} | admin, kassamedewerker |
| DELETE | /invoices/{id} | admin, kassamedewerker |

**Voorbeeld payload voor het toevoegen van een factuur**

````json
{
  "repairId": "21bdc260-d585-41e8-950d-56f5ea9208cc",
  "id": "6f563bf0-e1ab-4e63-b721-3d2c47b28963",
  "paidAt": "1972-01-22T20:54:27.035Z",
  "status": 1
}
````

**Voorbeeld payload voor het updaten van een factuur**

````json
{
  "status": 1,
  "paidAt": "1991-04-15T20:23:48.899Z"
}
````

## 6. Foutmeldingen

Bij het gebruik van de applicatie kunnen fouten optreden. Foutmeldingen hebben altijd een vast formaat waaraan te
herkennen is wat er fout gegaan is. Elke foutmelding bestaat uit de volgende 3 velden:

1. statusCode - statusCode om de soort fout te herkennen (bad request, internal server error, etc.)
2. errorCode - type foutmelding
3. message - omschrijving van wat er fout gegaan is

Onderstaande foutmelding geeft het systeem bij het uitvoeren van een GET request naar een onbekende resource.

````json
{
  "statusCode": 404,
  "errorCode": "entity-not-found",
  "message": "entity of type InvoiceEntity with id 3f6b0479-ef88-4f63-bd65-af3aa8ad5b73 doesn't exist"
}
````

De volgende foutmeldingen zijn bekend in het systeem:

| errorCode                | omschrijving                                                                |
|--------------------------|-----------------------------------------------------------------------------|
| email-already-taken      | het opgegeven e-mailadres is al in gebruik                                  |
| entity-already-exists    | er bestaat al een resource van dit type met het opgegeven ID                |
| entity-not-found         | er bestaat geen resource met het opgegegeven ID                             |
| file-upload-exception    | er is wat fout gegaan bij het uploaden van een bestand                      |
| forbidden-file-type      | het bestandstype dat je wil uploaden is niet toegestaan                     |
| generate-invoice         | er is wat fout gegaan met het genereren van de factuur                      |
| invalid-uuid             | het opgegeven ID is geen geldig UUID                                        |
| license-already-taken    | het opgegeven kenteken is al in gebruik                                     |
| out-of-stock             | er is onvoldoende voorraad om een onderdeel toe te voegen aan een reparatie |
| unknown-repair-line-type | de waarde voor het type reparatie regel veld is onbekend                    |
| unknown-role             | de waarde voor het rol veld is een onbekend rol                             |
| unknown-status           | de waarde vor het status veld is een onbekend status                        |
# Verantwoordingsdocument

#### Auteur: Timo Noordzee

In dit document worden technisch ontwerpbeslissing voor de applicatie beschreven. Daarnaast staan er een aantal
limitaties van de applicatie beschreven.

* [1. BaseRestService](#1-baserestservice)
* [2. DTO voor Create en Update](#2-dto-voor-create-en-update)
* [3. Mappers](#3-mappers)
* [4. EntityGraphJpaRepository](#4-entitygraphjparepository)
* [5. Opslaan van bestanden](#5-opslaan-van-bestanden)
* [6. Opslaan van regels onder een reparatie](#6-opslaan-van-regels-onder-een-reparatie)
* [7. Genereren van PDF-bestanden](#7-genereren-van-pdf-bestanden)
* [8. Authenticatie](#8-authenticatie)
* [9. Afhandelen van Exceptions](#9-afhandelen-van-exceptions)
* [10. Testen](#10-testen)
* [11. Limitaties](#11-limitaties)
    + [11.1 Data archiveren](#111-data-archiveren)
    + [11.2 Multi tenancy](#112-multi-tenancy)
    + [11.3 Filter opties](#113-filter-opties)
    + [11.4 Meerdere rollen per gebruiker](#114-meerdere-rollen-per-gebruiker)

## 1. BaseRestService

De applicatie bevat voor veel soortgelijke requests voor de verschillende entities. Zo kent bijvoorbeeld elke entity wel
een `GET` met als pad `/{id}` en `DELETE` met `/{id}`

Om te voorkomen dat steeds dezelfde code geschreven moet worden is gekozen om een `BaseRestService` te maken die een
aantal basisfunctionaliteiten voor REST endpoints bevat zoals het ophalen van alle entities of een entity met een
specifiek id.

De `BaseRestService` is abstracte klasse en bevat de basislogica voor verschillende CRUD operaties.

## 2. DTO voor Create en Update

Voor vrijwel alle request dient eerst een validatie gedaan te worden op de payload. Zo mag bijvoorbeeld bij het
toevoegen van een voertuig middels de `POST` endpoint het kenteken in het veld `license` niet leeg zijn.

Het is mogelijk om de `@NotBlank` annotation toe te voegen aan het veld `license` in `VehicleEntity.java`, maar dit is
niet gewenst. In dit geval zou `VehicleEntity` dan ook als `@RequestBody` gebruikt moeten worden in
de `VehicleController`.

Het gevolg daarvan is dat de payload altijd identiek moet zijn aan de database model `VehicleEntity`. Dit is niet
wenselijk. Aanpassingen in de database model hebben dan direct invloed op de payload die gebruikt moet worden voor de
API. Daarnaast is de data model niet identiek aan de request payload. Zo bevat `InvoiceEntity` een kolom van het
type `InvoiceStatus`, maar deze dient als `int` in de request payload te staan. Het scheiden van de data model en DTO is
dus noodzakelijk.

Voor elke model in de data laag bestaat een create en update DTO. Zo bestaat er voor `VehicleEntity` bijvoorbeeld
een `CreateVehicleDto` en `UpdateVehicleDto`. Elke create DTO implement van `CreateDto` zodat deze op een generieke
manier gebruikt kan worden in de `BaseRestService`.

## 3. Mappers

Voor het omzetten van de DTO models naar data models is gebruikt gemaakt van mappers. Om boilerplate code te beperken is
hiervoor de MapStruct depedency gebruikt. Daarnaast extend elke model-specifieke mapper van `EnityMapper`. Deze extra
laag van abstractie maakt het mogelijk om de mapper te gebruiken in de abstract `BaseRestService`. Elke mapper die
extend van `EntityMapper` bevat namelijk tenminste een functie om van een create DTO naar een entity te gaan en om een
entity te updaten met een update DTO.

````java
public interface EntityMapper<E, C, U> {

    E fromCreateDto(final C createDto);

    E updateWithDto(final U updateDto, @MappingTarget final E entity);
}
````

## 4. EntityGraphJpaRepository

Elke repository extend van `EntityGraphJpaRepository` i.p.v. `JpaRepository`. De Spring Data JPA EntityGraph dependency
maakt het eenvoudig om met named EntityGraphs te werken. Elke entity heeft de `@NamedEntityGraphs` annotation met
tenminste een standaard entity graph. In het geval van de `VehicleEntity` is er ook een `GRAPH_FULL_DETAILS` met daarin
naast de standaard velden ook de `owner` en `shortcoming`. Bij het ophalen van een voeruig met deze entity graph krijg
je dan naast het voertuig ook de klant en tekortkomingen terug.

```java
@NamedEntityGraphs(value = {
        @NamedEntityGraph(name = VehicleEntity.GRAPH_DEFAULT),
        @NamedEntityGraph(name = VehicleEntity.GRAPH_FULL_DETAILS, attributeNodes = {
                @NamedAttributeNode("owner"),
                @NamedAttributeNode("shortcomings"),
        })
})
```

In de `VehicleService` wordt daarvoor i.p.v. de `findById(id)` functie de `findById(id, entityGraph)` functie gebruikt.

````java
@Override
protected Optional<VehicleEntity> findById(final String s){
        return repository.findById(s,EntityGraphs.named(VehicleEntity.GRAPH_FULL_DETAILS));
        }
````

## 5. Opslaan van bestanden

In het oorspronkelijke ontwerp zouden de bijbehorende PDF-bestanden voor de autopapieren en invoice als bestanden op de
disk worden opgeslagen. Uiteindelijk is er toch voor gekozen om de PDF-bestanden als byte array op te slaan in de
PostgreSQL database. Het voordeel hiervan is dat de integriteit van de data beter te waarborgen is aangezien het bestand
samen met de relevante metadata (bijvoorbeeld status bij een factuur) opgeslagen staan zonder verwijzing naar een
bestand. Het is hierdoor niet mogelijk dat een row in de database verwijst naar een bestand dat niet meer bestaat of dat
een bestand nog wel opgeslagen is terwijl de bijbehorende row in de database al verwijderd is.

Omdat het lezen van een byte array uit de database een relatief zware operatie is dient deze niet onnodig uitgevoerd te
worden. Voor de klassen `VehiclePapersEntity` en `InvoiceEntity` is daarom een projection gemaakt zonder de data kolom.
Bij het ophalen van alle voertuigpapieren en facturen zal de data kolom niet uitgelezen worden.

````java
@Lob
@Column(name = "data")
@Type(type = "org.hibernate.type.BinaryType")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
private byte[]data;
````

## 6. Opslaan van regels onder een reparatie

De klasse `RepairEntity` heeft een OneToMany relatie met `RepairLineEntity`. Een reparatie bestaat dus uit meerdere
regels. Elke regel heeft een type (`RepairLineType`) waar 0 een onderdeel is en 1 een handeling. Er is bewust gekozen om
geen relatie te maken met `PartEntity` en `ActionEntity`, maar om de prijs en naam als ruwe waardes op te slaan. Dit is
gedaan om de data-integriteit te waarborgen. Op het moment dat een monteur een onderdeel toevoegt aan een reparatie zijn
alleen de naam en prijs zoals die op dat moment in het systeem staan relevant. Als gekozen wordt om i.p.v. de ruwe
waardes een verwijzing op te slaan kunnen aanpassingen aan de prijs/naam van onderdelen/handelingen voor wijzigingen in
de repair lines zorgen. Als gevolg kan een onderdeel dat op 1 januari 2022 nog voor €100 op de reparatie stond een maand
later opeens als €105 op de reparatie staan waardoor was er destijds in het systeem is gezet niet terug te vinden is.

## 7. Genereren van PDF-bestanden

De applicatie bevat de mogelijkheid een factuur te genereren o.b.v. een reparatie (`RepairEntity`). Voor het generen van
de factuur PDF-bestanden is gebruik gemaakt van 2 dependencies, namelijk:

1. Thymeleaf
2. Flying Saucer

Deze 2 dependencies sluiten goed op elkaar aan. Tymeleaf wordt gebruikt om een template om te zetten naar een HTML
string. Deze HTML string wordt vervolgens door Flying Saucer gebruikt om een PDF te renderen. De PDF wordt naar
een `ByteArrayOutputStream` geschreven die omgezet wordt naar een byte array. Deze opzet maakt het eenvouding om een PDF
te generen o.b.v. een HTML template.

## 8. Authenticatie

Voor authenticatie is gekozen voor basic authentication. De applicatie zou in een productieomgeving alleen op een lokaal
netwerk beschikbaar zijn met slechts een beperkt aantal gebruikers. Voor dit scenario is basic authenticatie een prima
en eenvoudige oplossing voor authentication en authorization. In een ander scenario zou mogelijke voor een andere vorm
van authentication zoals JWT gekozen zijn.

Voor authenticatie wordt de tabel `employee` gebruikt aangezien alleen medewerkers toegang hebben tot de applicatie.
De `AuthUserService` die implement van `UserDetailsService` is verantwoordelijk voor het ophalen van de `UserDetails`
o.b.v de username die wordt misbruikt als e-mailadres.

## 9. Afhandelen van Exceptions

Voor het afhandelen van exceptions is ervoor gekozen dat elke Exception extend van `BaseHttpException`. Deze abstract
klasse bevat 3 variabelen namelijk `status`, `errorCode` en `message`. Op deze manier heeft elke exception die kan
optreden in de applicatie een vaste structuur (op validatie exceptions na). Ook hoeft alleen de `BaseHttpException`
geregistreerd te worden in `ExceptionControllerAdvice` omdat alle andere exceptions instanties zijn
van `BaseHttpException` en daardoor goed verwerkt worden.

Elke exception heeft een eigen error code waardoor een client applicatie eenvoudig kan herkennen wat er fout gegaan is.

````json
{
  "statusCode": 404,
  "errorCode": "entity-not-found",
  "message": "entity of type InvoiceEntity with id 3f6b0479-ef88-4f63-bd65-af3aa8ad5b71 doesn't exist"
}
````

## 10. Testen

Alle model klassen zowel in de data laag als de dto laag hebben de lombok `@Builder` annotation. De builder heeft in de
productiecode niet echt een toegevoegde waarde (naast dat deze door mapstruct gebruikt wordt), maar de unit tests worden
veel duidelijker door het gebruik van het builder pattern.

Voor de verschillende tests zijn utility classes geschreven om boilerplate code te verminderen. De utility classes
bevatten functies voor het genereren van mock data. Zo is er bijvoorbeeld in elke `*TestUtils` klasse
een `generateMockEntity()` functie voor het genereren van een instantie van de data model met fake data.

Er is bewust gekozen voor het gebruiken van random fake data i.p.v. hardcoded testwaardes. De tests zijn hierdoor
mogelijk minder voorspelbaar, de tests zijn niet allemaal pure functions, maar er wordt getest met een bredere set van
data. Door een bug zou bijvoorbeeld een kenteken in het formaat AA-11-BB wel verwerkt kunnen worden, maar A-123-BB niet.
Als alleen één hardcoded kenteken gebruikt wordt in de test zou deze bug veel minder snel ontdekt worden.

Voor het genereren van de random mock data is niet gebruikt gemaakt van de populaire
dependency [java-faker](https://github.com/DiUS/java-faker), maar van de
fork [data-faker](https://github.com/datafaker-net/datafaker_old) aangezien deze een vehicle categorie bevat die
java-faker niet heeft.

De tests voor de RepositoryTests zijn beperkt aangezien veel tests/validatie al gedaan wordt door het gebruiken van
infered queries.

> "...as long as we have at least one test that tries to start up the Spring application context in our code base, we do not need to write an extra test for our inferred query."

Source: [refactoring](https://reflectoring.io/spring-boot-data-jpa-test)

## 11. Limitaties

De huidige applicatie kent limitaties. Een deel van deze limitaties staan hieronder beschreven.

### 11.1 Data archiveren

Het is niet mogelijk data te archiveren, alleen verwijderen. Daarnaast zal bij het verwijderen van een klant ook alle
gerelateerde data verwijderd worden. Dit zou aangepast kunnen worden met het implementeren van een 'soft delete'.

### 11.2 Multi tenancy

De applicatie is nu gemaakt voor één garage en heeft geen ondersteuning voor multi tenancy. Dit zou aangepast kunnen
worden door voor elke table een extra tenantId kolom toe te voegen. Data kan dan voor een specifieke tentant opgeslagen
worden. Op deze manier is het niet nodig voor elke tenant een nieuwe database op te zetten, maar kan alles opgeslagen
worden opéén centrale database

### 11.3 Filter opties

De query opties zijn zeer beperkt, het is alleen mogelijk om alle rijen of één specifieke rij op te vragen. Dit zou
aangepast kunnen worden door query parameters in de URL te ondersteunen. Een request
naar `/vehicles?year=2000&brand=Opel` zou bijvoorbeeld alle voertuigen met bouwjaar 2000 en merk Opel terug kunnen
geven.

### 11.4 Meerdere rollen per gebruiker

In de huidige applicatie heeft elke gebruiker slechts één rol. Het is niet mogelijk om iemand toegang te geven tot zowel
de backoffice medewerker als de kassamedewerker endpoints. Om dit te bereiken zouden er nu meerdere employees aangemaakt
moeten worden. Dit zou aangepast kunnen worden door een many-to-many table toe te voegen tussen employee en role.
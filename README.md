# MovieApplication
Wem eine Cloud Lösung einer Film Bibliothek zu unsicher ist, weil man am Liebsten alles bei sich zuhause hosten möchte, dann ist MovieApplication eine Backend-Lösung.
Mithilfe MovieApplication kann man Filminformation von IMDB anfordern und bei sich in einer konfigurierten Datenbank konfigurieren.

### Getting Started
1. ein `mvn clean install` um dir alle für die anwendung nötigen Abhängigkeiten zu installieren
2. ein apiKey von IMDB (http://www.omdbapi.com/) der auf `imdb.apiKey` in den `application.properties` gesetzt werden muss

### Rest Calls
| URL                                            | Beschreibung                                                  |
|------------------------------------------------|---------------------------------------------------------------|
| GET localhost:8080/api/movies/                 | Gibt dir alle in der Datenbank gespeicherten Filme zurück     |
| GET localhost:8080/api/movies/{Filmtitel}      | Gibt dir den Film mit dem übergebenen Namen aus der Datenbank |
| GET localhost:8080/api/movies/{Filmtitel}/imdb | Sucht auf IMDB nach diesem Film                               |
| POST localhost:8080/api/movies                 | Speichert einen neuen Film in der Datenbank                   |

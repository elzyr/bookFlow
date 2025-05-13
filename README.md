# BookFlow 

BookFlow to aplikacja do zarządzania książkami, użytkownikami oraz historią wypożyczeń. Projekt oparty jest o Java + Spring Boot jako backend oraz frontend napisanym w React.

## Technologie

- Java 17+
- Spring Boot 3+
- Spring Security (JWT + cookie HTTP-only)
- Spring Data JPA + Hibernate
- MySQL
- Maven
- Lombok
- MapStruct

## ⚙️ Instalacja backendu krok po kroku (Spring Boot)

### 1. Klonuj repozytorium

```bash
git clone MartynaSzymanskaGitHub/bookFlow (https://github.com/MartynaSzymanskaGitHub/bookFlow.git)
cd bookFlow/backend
```

## Konfiguracja bazy danych (MySQL)

1. **Utwórz bazę oraz użytkownika**

   Zaloguj się do MySQL i uruchom:

   ```sql
   CREATE DATABASE bookflow
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
   ```

## Ustaw dane dostępowe w application.properties
## Plik znajduje się w backend/src/main/resources/


spring.datasource.url=jdbc:mysql://localhost:3306/bookflow
spring.datasource.username=bookflow_user
spring.datasource.password=strong_password

# Opcjonalnie dostosuj:
spring.jpa.hibernate.ddl-auto=update      # create / validate / none
spring.jpa.show-sql=true                  # logowanie zapytań

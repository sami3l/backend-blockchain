# ClinChain Backend API

Medical lot management system REST API built with Spring Boot 3.2.

## 📋 Overview

ClinChain is a pharmaceutical supply chain management system that tracks medical lots through different stages with role-based access control.

## 🚀 Technologies

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA**
- **H2 Database** (development)
- **PostgreSQL** (production ready)
- **Lombok**
- **Maven**

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL (optional, for production)

## ⚙️ Setup & Installation

### 1. Clone the repository

```bash
cd spring-boot-backend
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

### 4. Access H2 Console (Development)

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:clinchain_db`
- Username: `sa`
- Password: _(leave empty)_

## 🔐 Default Users

The application seeds these users automatically on startup:

| Username   | Password | Role       | Permissions                     |
| ---------- | -------- | ---------- | ------------------------------- |
| grossiste  | password | GROSSISTE  | Create lots                     |
| hopitale   | password | HOPITALE   | Validate lots                   |
| pharmacien | password | PHARMACIEN | Withdraw from lots, add history |
| infirmier  | password | INFIRMIER  | View lots, add history          |

## 📚 API Endpoints

### Authentication

#### Login

```http
POST /auth/login
Content-Type: application/json

{
  "username": "grossiste",
  "password": "password"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "uuid",
    "username": "grossiste",
    "role": "GROSSISTE"
  }
}
```

#### Get Current User

```http
GET /auth/me
Authorization: Bearer <token>
```

#### Logout

```http
POST /auth/logout
Authorization: Bearer <token>
```

### Lot Management

#### Get All Lots

```http
GET /lots
Authorization: Bearer <token>
```

#### Create Lot (GROSSISTE only)

```http
POST /lots
Authorization: Bearer <token>
Content-Type: application/json

{
  "medName": "Paracetamol 500mg",
  "quantity": 1000,
  "createdBy": "grossiste"
}
```

#### Validate Lot (HOPITALE only)

```http
POST /lots/{lotId}/validate
Authorization: Bearer <token>
Content-Type: application/json

{
  "actor": "hopitale"
}
```

#### Withdraw from Lot (PHARMACIEN only)

```http
POST /lots/{lotId}/withdraw
Authorization: Bearer <token>
Content-Type: application/json

{
  "qty": 50,
  "actor": "pharmacien"
}
```

#### Add History (PHARMACIEN, INFIRMIER)

```http
POST /lots/{lotId}/history
Authorization: Bearer <token>
Content-Type: application/json

{
  "action": "Lot dispensé au patient",
  "actor": "infirmier"
}
```

## 🧪 Testing

### Run all tests

```bash
mvn test
```

### Run with coverage

```bash
mvn test jacoco:report
```

## 🧪 Example API Usage

### 1. Login and get token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"grossiste","password":"password"}'
```

### 2. Create a lot

```bash
curl -X POST http://localhost:8080/lots \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"medName":"Paracetamol","quantity":1000,"createdBy":"grossiste"}'
```

### 3. Get all lots

```bash
curl http://localhost:8080/lots \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🗂️ Project Structure

```
src/main/java/com/clinchain/backend/
├── BackendApplication.java
├── config/
│   ├── DataSeeder.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── controller/
│   ├── AuthController.java
│   └── LotController.java
├── dto/
│   ├── AddHistoryRequest.java
│   ├── CreateLotRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── UserDto.java
│   ├── ValidateRequest.java
│   └── WithdrawRequest.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientQuantityException.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── model/
│   ├── Lot.java
│   ├── LotHistory.java
│   ├── User.java
│   └── UserRole.java
├── repository/
│   ├── LotHistoryRepository.java
│   ├── LotRepository.java
│   └── UserRepository.java
└── service/
    ├── AuthService.java
    ├── LotService.java
    └── UserService.java
```

## ⚙️ Configuration

### Switch to PostgreSQL (Production)

Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinchain_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### JWT Configuration

```properties
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000  # 24 hours in milliseconds
```

## 📝 Error Responses

All errors follow this format:

```json
{
  "timestamp": "2024-12-14T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Not enough quantity. Available: 100, Requested: 150",
  "path": "/lots/uuid/withdraw"
}
```

### HTTP Status Codes

- `200 OK` - Success
- `201 Created` - Resource created
- `204 No Content` - Success with no response body
- `400 Bad Request` - Validation error or insufficient quantity
- `401 Unauthorized` - Invalid credentials or token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 🔒 Security Features

- ✅ JWT-based stateless authentication
- ✅ BCrypt password hashing
- ✅ Role-based access control (RBAC)
- ✅ Method-level security with `@PreAuthorize`
- ✅ CORS configuration for cross-origin requests
- ✅ Global exception handling

## 📄 License

This project is for educational purposes.

## 👥 Authors

ClinChain Development Team

---

**Backend URL:** http://localhost:8080
**H2 Console:** http://localhost:8080/h2-console

## Structure du projet

Le projet est organisé selon la structure standard de Spring Boot, avec les fichiers et répertoires suivants :

- **src/main/java/com/example/backend** : Contient le code source de l'application.

  - **BackendApplication.java** : Point d'entrée de l'application.
  - **config** : Contient les fichiers de configuration pour la sécurité et les paramètres web.
  - **controller** : Contient les contrôleurs de l'API.
  - **service** : Contient les interfaces et implémentations des services.
  - **repository** : Contient les dépôts pour l'accès aux données.
  - **model** : Contient les entités JPA.
  - **dto** : Contient les objets de transfert de données pour les requêtes et réponses.
  - **exception** : Contient les classes pour la gestion des exceptions.

- **src/main/resources** : Contient les fichiers de configuration de l'application.

  - **application.properties** : Configuration principale.
  - **application-dev.properties** : Configuration pour l'environnement de développement.
  - **application-prod.properties** : Configuration pour l'environnement de production.

- **src/test/java/com/example/backend** : Contient les tests unitaires de l'application.

  - **BackendApplicationTests.java** : Tests pour l'application.
  - **controller** : Tests pour les contrôleurs.
  - **service** : Tests pour les services.

- **pom.xml** : Fichier de configuration Maven pour les dépendances et plugins.

## Instructions de configuration

1. **Cloner le projet**

   ```bash
   git clone <URL_DU_REPOSITORY>
   cd spring-boot-backend
   ```

2. **Configurer la base de données**
   Modifiez le fichier `src/main/resources/application.properties` pour configurer les paramètres de connexion à votre base de données.

3. **Construire le projet**
   Utilisez Maven pour construire le projet :

   ```bash
   mvn clean install
   ```

4. **Exécuter l'application**
   Lancez l'application avec la commande suivante :
   ```bash
   mvn spring-boot:run
   ```

## Tests

Les tests unitaires peuvent être exécutés avec Maven :

```bash
mvn test
```

## Technologies utilisées

- Spring Boot
- Spring Data JPA
- Maven
- JUnit
- Lombok (si utilisé)

## Auteurs

- [Votre Nom] - Développeur principal

## License

Ce projet est sous licence MIT. Veuillez consulter le fichier LICENSE pour plus de détails.

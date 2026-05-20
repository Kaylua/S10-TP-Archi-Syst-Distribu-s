# TP2

Passage du monolithe (TP1) vers une architecture distribuée composée de 3 services indépendants.

## Prérequis

- Java 21
- Maven 3.9+

---

## Architecture

```
distributed-library/
├── api-gateway/       port 8080 — point d'entrée unique (Spring Cloud Gateway)
├── catalog-service/   port 8081 — gestion du catalogue de livres
└── loan-service/      port 8082 — gestion des prêts (appelle catalog via Feign)
```

Chaque service possède sa propre base H2 en mémoire (pas de base partagée).

### Flux inter-services

```
Client
  │
  ▼
api-gateway :8080
  ├── /api/books/**  ──────────► catalog-service :8081
  │                                    │
  └── /api/loans/**  ──────────► loan-service :8082
                                       │
                           Feign ──────┘
                           /internal/books/{id}/availability
                           /internal/books/{id}/borrow
                           /internal/books/{id}/return
```

### Décisions architecturales clés

- **Pas de base partagée** : chaque service gère ses propres données.
- **Dénormalisation** : `loan-service` stocke `bookTitle` dans `Loan` pour éviter des appels réseau à la lecture.
- **Endpoints internes** : `/internal/books/**` est réservé aux appels inter-services, non exposé via la gateway.
- **Gestion des pannes** : les erreurs Feign sont interceptées et retournent `503 SERVICE_UNAVAILABLE` au client.

---

## Lancer les services

Chaque service se lance indépendamment. L'ordre recommandé :

```bash
# Terminal 1 — catalog-service
cd catalog-service
mvn spring-boot:run

# Terminal 2 — loan-service (nécessite catalog-service démarré)
cd loan-service
mvn spring-boot:run

# Terminal 3 — api-gateway
cd api-gateway
mvn spring-boot:run
```

Ou depuis la racine pour compiler tout le projet :

```bash
mvn clean package -DskipTests
```

---

## API REST (via la gateway — port 8080)

### Livres

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/books` | Lister tous les livres |
| `GET` | `/api/books?q={query}` | Rechercher par titre ou auteur |
| `GET` | `/api/books/{id}` | Obtenir un livre par id |
| `POST` | `/api/books` | Ajouter un livre |

#### POST /api/books — Body
```json
{
  "title": "Clean Code",
  "author": "Robert Martin",
  "isbn": "978-0132350884"
}
```

---

### Prêts

| Méthode | URL | Description |
|---|---|---|
| `POST` | `/api/loans` | Emprunter un livre |
| `POST` | `/api/loans/{id}/return` | Retourner un livre |
| `GET` | `/api/loans/student/{studentId}` | Historique d'un étudiant |

#### POST /api/loans — Body
```json
{
  "bookId": 1,
  "studentId": "etudiant42"
}
```

#### Réponse
```json
{
  "id": 1,
  "bookId": 1,
  "bookTitle": "Clean Code",
  "studentId": "etudiant42",
  "borrowedAt": "2026-05-20",
  "returnedAt": null,
  "status": "ACTIVE"
}
```

---

## Règles métier

- Un livre non disponible ne peut pas être emprunté → `400 Bad Request`
- Retourner un prêt déjà clôturé est refusé → `400 Bad Request`
- catalog-service indisponible → `503 Service Unavailable`
- Livre introuvable dans le catalogue → `404 Not Found`

---

## Consoles H2 (debug)

| Service | URL | JDBC URL |
|---|---|---|
| catalog-service | http://localhost:8081/h2-console | `jdbc:h2:mem:catalogdb` |
| loan-service | http://localhost:8082/h2-console | `jdbc:h2:mem:loandb` |

User : `sa` / Password : *(vide)*

---

## Scénario de test complet (via gateway — port 8080)

1. `POST /api/books` — ajouter "Clean Code"
2. `GET /api/books` — vérifier le livre (`available: true`)
3. `POST /api/loans` avec `bookId: 1, studentId: "etudiant42"` — emprunt
4. `GET /api/books/1` — vérifier `available: false`
5. `POST /api/loans` avec `bookId: 1` à nouveau → `400` (livre non disponible)
6. `GET /api/loans/student/etudiant42` — historique
7. `POST /api/loans/1/return` — retour du livre
8. `GET /api/books/1` — vérifier `available: true`

---

## Tests

```bash
# Tests unitaires de catalog-service et loan-service
mvn test -pl catalog-service,loan-service
```

11 tests unitaires : 5 `BookServiceTest` + 6 `LoanServiceTest` (CatalogClient mocké avec Mockito).

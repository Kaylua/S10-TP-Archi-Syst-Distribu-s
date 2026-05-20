# TP1

Application Spring Boot implémentant une base architecturale propre en couches pour une plateforme de gestion de prêts de livres universitaires.

## Prérequis

- Java 21
- Maven 3.9+

## Lancer l'application

```bash
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

La base de données H2 est en mémoire (réinitialisée à chaque démarrage).  
Console H2 accessible sur `http://localhost:8080/h2-console` :
- JDBC URL : `jdbc:h2:mem:librarydb`
- User : `sa` / Password : *(vide)*

---

## Architecture

```
com.esiea.library
├── domain/          Entités métier et exception domaine
│   ├── Book.java
│   ├── Loan.java
│   ├── LoanStatus.java
│   └── BusinessException.java
├── repository/      Interfaces Spring Data JPA
│   ├── BookRepository.java
│   └── LoanRepository.java
├── application/     Couche service — règles métier
│   ├── BookApplicationService.java
│   └── LoanApplicationService.java
└── web/             Couche HTTP — controllers et DTOs
    ├── BookController.java
    ├── LoanController.java
    ├── ApiExceptionHandler.java
    ├── BookResponse.java / CreateBookRequest.java
    └── LoanResponse.java / CreateLoanRequest.java
```

---

## API REST

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

#### Réponse
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert Martin",
  "isbn": "978-0132350884",
  "available": true
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
- Les champs obligatoires manquants retournent un `400` avec le détail des erreurs

#### Exemple d'erreur métier
```json
{ "error": "Ce livre n'est pas disponible à l'emprunt" }
```

#### Exemple d'erreur de validation
```json
{
  "error": "Validation échouée",
  "fields": { "title": "must not be blank" }
}
```

---

## Scénario de test complet (Postman)

1. `POST /api/books` — ajouter "Clean Code"
2. `POST /api/books` — ajouter "Design Patterns"
3. `GET /api/books` — vérifier les 2 livres (`available: true`)
4. `GET /api/books?q=clean` — recherche
5. `POST /api/loans` avec `bookId: 1, studentId: "etudiant42"` — emprunt
6. `GET /api/books/1` — vérifier `available: false`
7. `POST /api/loans` avec `bookId: 1` à nouveau → doit retourner `400`
8. `GET /api/loans/student/etudiant42` — historique
9. `POST /api/loans/1/return` — retour du livre
10. `GET /api/books/1` — vérifier `available: true`

# Postman operations — Oakridge Library

## Auth
POST /auth/register   { "name", "email", "password" }   -- always creates a STUDENT
POST /auth/login       { "email", "password" }           -- returns { token, id, name, email, role }

## Catalog (GET = any logged-in user, write = ADMIN only)
GET    /books?q=search-term
GET    /books/{id}
POST   /books          { "title","author","isbn","category","totalCopies" }   [ADMIN]
PUT    /books/{id}     same body                                               [ADMIN]
DELETE /books/{id}                                                             [ADMIN]

## Borrowing (self-service - no studentId in the URL, it's resolved from your token)
POST /borrow/{bookId}
POST /borrow/{recordId}/return
GET  /borrow/my
GET  /borrow/all                                                               [ADMIN]

## Students (admin user management)
GET  /students/me          -- your own profile, any logged-in user
GET  /students                                                                 [ADMIN]
POST /students/{id}/unlock                                                     [ADMIN]

## Login anomaly dashboard
GET /login-attempts                                                            [ADMIN]
GET /login-attempts/flagged                                                    [ADMIN]

## Trying the role split
1. Register a normal account -> it's a STUDENT. Try GET /students or POST /books -
   expect 403 Forbidden.
2. Log in as the seeded admin (see README_PROJECT.md for default dev credentials).
   The same endpoints now work.
3. Every request needs: Authorization: Bearer <token from login>

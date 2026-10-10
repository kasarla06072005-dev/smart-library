# Smart Library — Secure Library Management

A merged project combining library catalog/borrowing management with login-anomaly
detection, built as one system with real role-based access control:

- **Students** can browse the catalog, borrow and return books, and see their own
  borrow history. Nothing else.
- **Admins** get everything students get, plus: full CRUD on the book catalog, a view
  of every borrow record (and the ability to force a return), a student account list
  with unlock controls, and the login-anomaly dashboard.

Stack: Spring Boot 4 (Java 21) + MySQL backend, React 19 + Vite frontend, JWT auth.

## Run it locally

1. `CREATE DATABASE smart_library_db;`
2. Backend: `cd backend && ./mvnw spring-boot:run` → http://localhost:8080
   - On first run it creates one admin account automatically. Default (local dev only):
     `admin@library.local` / `ChangeMe123!` — see the startup log, and change the
     password once you've confirmed you can log in.
3. Frontend: `cd frontend && npm install && npm run dev` → http://localhost:5173

## How the pieces fit together

- **Auth** (`/auth/register`, `/auth/login`) — registration always creates a STUDENT
  account; there is no way for a client to register as ADMIN. Login reuses the
  anomaly-detection logic: 5 failed attempts in 2 minutes auto-locks the account, and
  a login from a never-seen IP is flagged for admin review without blocking it.
- **Catalog** (`/books`) — anyone logged in can browse; only ADMIN can create, edit,
  or delete.
- **Borrowing** (`/borrow/**`) — a student can only borrow/return for themselves
  (resolved from their token, not a client-supplied ID); an admin can view and force-
  return any record.
- **Students** (`/students`, `/students/*/unlock`) — admin-only account list and
  unlock action. `/students/me` is available to anyone, returning only their own
  profile.
- **Login activity** (`/login-attempts`) — admin-only. This is the login-anomaly
  dashboard from the original project, now gated behind a real role check instead of
  being open to anyone who finds the URL.

See `SECURITY.md` for what was hardened versus the original four projects, and
`README_DEPLOYMENT.md` for how to actually put this online.

## Default admin credentials

For local setup, the default administrator account is:

- Email: `admin@library.local`
- Password: `ChangeMe123!`

These values are configured in `backend/src/main/resources/application.properties`.
Change them before deploying the application publicly.

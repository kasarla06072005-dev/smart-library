# Security notes

This is an honest account of what was hardened in this merged project versus the
original four student projects, and what is deliberately out of scope. Nobody should
read "integrated and hardened" as "guaranteed vulnerability-free" — no one can promise
that for hand-written code that hasn't gone through a real audit or dependency scan.

## Fixed, compared to the original projects

- **Role self-assignment.** The original `RegisterDTO` accepted a `role` field
  straight from the client — anyone could register as ADMIN. Here, `/auth/register`
  only ever creates STUDENT accounts; the first ADMIN is created once, at startup, by
  `AdminSeeder`, from environment variables, and never through a public endpoint.
- **Trusting client-supplied IDs for "my" data.** The original ticket/attendance
  projects took a `studentId` straight from the URL for "my records" endpoints —
  any logged-in user could read anyone else's data just by changing a number. Here,
  `SecurityUtil.currentStudent()` resolves identity from the verified JWT only;
  `/borrow/my`, `/students/me`, and the ownership check in `returnBook()` never trust
  a client-supplied ID.
- **Permissive authorization.** The originals used `permitAll()` for almost every
  endpoint and did no role checks at all. Here, `SecurityConfig` explicitly lists
  what each role can do and ends with `anyRequest().denyAll()` — new endpoints are
  unreachable by default until someone deliberately opens them up.
- **Hardcoded JWT secret.** The originals signed tokens with a secret string typed
  directly into the source. Here it's read from `JWT_SECRET`, the app refuses to
  start if it's under 32 characters, and it logs a loud warning if you're still
  running on the fallback dev value.
- **Stack traces / internal errors sent to the client.** `GlobalExceptionHandler` now
  catches unhandled exceptions and returns a generic message, logging the real
  details server-side instead.
- **User enumeration via login errors.** Login now always returns "Incorrect email or
  password" instead of distinguishing "email is wrong" from "password is wrong,"
  which otherwise lets someone test which emails have accounts.
- **Unvalidated input.** Registration and book forms now enforce real constraints
  (password length, required fields, positive copy counts) via Bean Validation,
  returning field-level errors instead of letting bad data reach the database.
- **Locked accounts with a live session.** The JWT filter re-fetches the account on
  every request rather than trusting the token's claims for the whole session — a
  lock or role change takes effect on the next request, not at next login.

## Deliberately out of scope (know before you deploy)

- **Token storage.** The JWT is kept in `localStorage`, same as the original
  projects. This is simple and consistent with the rest of this project set, but it
  is readable by any JavaScript that runs on the page — if this app ever embeds
  third-party scripts or has an XSS bug, that token is exposed. An httpOnly-cookie
  session is more resistant to this but is a bigger architectural change than this
  pass covers.
- **No dependency vulnerability scan.** This build environment has no network
  access, so `mvn` / `npm` dependencies were never actually downloaded, and nothing
  like `npm audit` or `mvn dependency-check` could run. Do this yourself before
  deploying — it's a five-minute step and genuinely matters.
- **No HTTPS enforcement in the app itself.** TLS termination is left to your reverse
  proxy or hosting platform, as called out in `README_DEPLOYMENT.md`. Don't serve
  this over plain HTTP in production.
- **No rate limiting beyond the login lockout.** Other endpoints (book search,
  borrow) have no request throttling. Fine for a class project's traffic; not fine
  if this is ever exposed to the open internet at scale.
- **`ddl-auto=update`, no migration tool.** Convenient for a student project, risky
  for a long-lived production database. If this grows past a class demo, move to
  Flyway or Liquibase.
- **No automated tests.** Nothing here has unit or integration test coverage. The
  logic was reasoned through carefully and checked for syntax/brace-balance, but it
  has not been exercised by a real test suite or by actually running it (see the
  note on compiling below).

## One more honest caveat

This code was written directly, the same way the original four projects were, but
**it has not been compiled or run** — this sandbox has no network access, so Maven
and npm could never actually download dependencies. Brace-balance and structural
checks were run on every file, but that only catches certain classes of error.
Budget some real debugging time on first run, especially around the Spring Security
configuration, which is the part most sensitive to small mistakes.

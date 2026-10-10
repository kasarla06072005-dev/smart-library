# Deploying Smart Library

## Option A — Docker Compose on a single server (simplest)

Good for a VPS (DigitalOcean, a college server, etc.) or just running it reliably at home.

1. Install Docker and Docker Compose on the server.
2. Copy this whole project to the server.
3. `cp .env.example .env` and fill in real values:
   - `DB_PASSWORD` — any strong password
   - `JWT_SECRET` — run `openssl rand -base64 48` and paste the output
   - `ADMIN_EMAIL` / `ADMIN_PASSWORD` — the first admin account's real login
   - `CORS_ALLOWED_ORIGINS` — the URL people will actually use for the frontend
   - `VITE_API_BASE_URL` — the URL the frontend should call for the backend
4. `docker compose up -d --build`
5. Frontend is served on port 5173, backend API on port 8080. Put a reverse proxy
   (nginx, Caddy, or your platform's load balancer) in front with a real TLS
   certificate — do not expose this directly over plain HTTP on the public internet.

To stop: `docker compose down` (add `-v` to also wipe the database volume).

## Option B — Split hosting (backend + managed DB + static frontend)

This splits cleanly onto free/cheap tiers of typical PaaS providers:

- **Database**: any managed MySQL (Railway, PlanetScale, AWS RDS, etc.) — get its
  connection URL, username, and password.
- **Backend**: deploy `backend/` to a Java-friendly host (Railway, Render, Fly.io,
  a VM). Set these environment variables there:
  - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` — from your managed database
  - `JWT_SECRET` — a long random string (`openssl rand -base64 48`)
  - `ADMIN_EMAIL`, `ADMIN_PASSWORD` — the real first admin login
  - `CORS_ALLOWED_ORIGINS` — the exact URL your deployed frontend will be served from
  - `SPRING_PROFILES_ACTIVE=prod`
- **Frontend**: deploy `frontend/` to a static host (Vercel, Netlify, Cloudflare
  Pages). Set `VITE_API_BASE_URL` to your backend's public URL as a build-time
  environment variable, then run `npm run build` and deploy the `dist/` folder (most
  of these platforms do this automatically from the repo).

## Before you consider this "live"

- [ ] `JWT_SECRET` is a real random value, not the default
- [ ] `ADMIN_PASSWORD` has been changed from whatever you set it to initially, after
      your first successful login
- [ ] `CORS_ALLOWED_ORIGINS` is set to your actual frontend URL (not localhost)
- [ ] The site is served over HTTPS (via your reverse proxy or platform)
- [ ] You've read `SECURITY.md` and are comfortable with its "not done" list

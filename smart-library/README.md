# Smart Library — Library Management System

Smart Library is a full-stack library management platform that helps students browse, borrow, and return books while allowing administrators to manage the catalog, members, borrow records, and login activity.

## Features

- Student registration and login
- Admin authentication and dashboard
- Browse library catalog
- Search books by title or author
- Borrow and return books
- Track borrowing history and due dates
- Admin book creation, editing, and deletion
- Admin borrow-record management
- Student account management
- Login activity monitoring
- Role-based navigation menus
- Responsive desktop, tablet, and mobile UI
- Custom Smart Library book-and-bookmark logo

## Technology Stack

### Frontend

- React
- Vite
- React Router
- Axios
- Responsive CSS

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT authentication
- MySQL

## Project Structure

```text
smart-library/
├── backend/     # Spring Boot REST API
├── frontend/    # React + Vite client
├── docker-compose.yml
├── postman_collection.json
└── README.md
```

## Requirements

- Java 21 or later
- Node.js 18 or later
- npm
- MySQL 8 or later

## Database Setup

Create a MySQL database named:

```sql
CREATE DATABASE smart_library_db;
```

Update the database username, password, and connection settings in:

```text
backend/src/main/resources/application.properties
```

Do not commit real database passwords or production secrets to GitHub.

## Run the Backend

From the `backend` directory:

### Windows

```bash
mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

## Run the Frontend

From the `frontend` directory:

```bash
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

## Default Admin Login

Use these credentials:

```text
Email: admin@library.local
Password: ChangeMe123!
```

The credentials are configured in:

```text
backend/src/main/resources/application.properties
```

```properties
app.admin.email=${ADMIN_EMAIL:admin@library.local}
app.admin.password=${ADMIN_PASSWORD:ChangeMe123!}
```

Change the admin password before using the application in production.

## Production Build

```bash
cd frontend
npm run build
```

## API Overview

| Method | Endpoint | Access |
|---|---|---|
| POST | `/auth/register` | Public |
| POST | `/auth/login` | Public |
| GET | `/books` | Authenticated user |
| POST | `/books` | Admin |
| PUT | `/books/{id}` | Admin |
| DELETE | `/books/{id}` | Admin |
| POST | `/borrow/{bookId}` | Student |
| GET | `/borrow/my` | Student |
| POST | `/borrow/{recordId}/return` | Student |
| GET | `/borrow/records` | Admin |
| GET | `/students` | Admin |
| GET | `/students/me` | Authenticated user |
| GET | `/login-attempts` | Admin |

See `postman_collection.json` for additional API examples.

## Security

Before deploying this project:

- Change the default admin credentials.
- Store database credentials in environment variables or a secrets manager.
- Configure a production JWT secret.
- Restrict CORS to trusted frontend domains.
- Enable HTTPS in production.
- Review authentication and account-lockout settings.
- Review database permissions and file access.

## License

This project is intended for educational and institutional project use.

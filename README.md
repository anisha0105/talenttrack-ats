# TalentTrack ATS

A modern, production-grade Applicant Tracking System (ATS) built with Java 21, Spring Boot 3, React 19, and TypeScript. TalentTrack ATS streamlines the entire hiring lifecycle — from public careers discovery and candidate application submissions to recruiter pipeline orchestration and resume evaluation.

---

## Architecture & System Overview

TalentTrack ATS separates public candidate discovery, authenticated candidate self-service, and recruiter hiring operations through a secure, modular REST API and single-page application.

Detailed architectural documentation, system boundaries, data ownership, and sequence flows are available in the [architecture package](architecture/README.md):
- [High-Level Design](architecture/HLD.md)
- [Application & Pipeline Sequence](architecture/SEQUENCE.md)
- [System Architecture Diagram](architecture/diagrams/system-architecture.svg)

### Tech Stack

| Domain | Technologies |
|---|---|
| **Backend** | Java 21, Spring Boot 3.5, Spring Security 6, Spring Data JPA, Hibernate 6, Flyway |
| **Authentication** | Stateless JWT (HMAC-SHA256 signed sessions), BCrypt password hashing, Role-Based Access Control (`ROLE_CANDIDATE`, `ROLE_RECRUITER`) |
| **Data & Storage** | PostgreSQL 16 (authoritative store), H2 (in-memory test profile), Local/Volume File Storage (configurable for S3/Blob) |
| **Search (Optional)** | Elasticsearch 8.18 (multi-field role search weighted by title and company) |
| **Frontend** | React 19, TypeScript, React Router 7, Axios, Vite 7 |
| **Containerization** | Docker multi-stage builds, Docker Compose |

---

## Core Features & Workflows

### 1. Public Careers Discovery
- Instant faceted role search by department, location, employment type, and keyword.
- Deep job detail pages displaying compensation brackets, team descriptions, and requirements.
- Responsive, accessible interface with zero layout shift.

### 2. Candidate Portal
- User registration and authenticated candidate dashboard.
- Reusable candidate profile stored once and synchronized across multiple applications.
- Secure resume attachment supporting PDF and Word documents with server-side MIME and size validation (10 MB limit).
- Transparent real-time status tracking across recruitment pipeline stages.

### 3. Recruiter Hiring Console
- Dedicated console for recruiters to publish, manage, and close job postings.
- Custom multi-stage hiring pipelines (e.g., Applied &rarr; Screen &rarr; Interview &rarr; Offer).
- Applicant progression controls to advance candidates across pipeline stages.
- Candidate resume inspection and collaborative recruiter notes with timestamped author attribution.

### 4. Enterprise Architecture & Reliability
- **DTO Projection Only:** REST endpoints expose clean DTO records, preventing entity leaks or unintended data disclosure.
- **Strict Data Ownership:** Service layer enforces job ownership checks on every recruiter mutation.
- **Optimized Persistence:** Batch fetching, indexed database queries, and pagination prevent N+1 query bottlenecks.
- **Database Migrations:** Managed Flyway migrations (`V1__create_ats_schema.sql`) guarantee reproducible schema state.

---

## Repository Structure

```
talenttrack-ats/
├── architecture/                 # System architecture diagrams and HLD specs
│   ├── diagrams/                 # Graphviz DOT files and rendered SVGs
│   ├── HLD.md                    # High-Level Design document
│   ├── README.md                 # Architecture package guide
│   └── SEQUENCE.md               # Application lifecycle sequence flow
├── backend/                      # Spring Boot 3 REST API
│   ├── src/main/java/com/talenttrack/ats/
│   │   ├── application/          # Applications, resumes, notes, candidate endpoints
│   │   ├── auth/                 # JWT token generation, filter, auth controller
│   │   ├── common/               # Global exception handling and system health check
│   │   ├── config/               # Security, CORS, and demo data configuration
│   │   ├── job/                  # Jobs, pipeline stages, recruiter endpoints
│   │   ├── search/               # Elasticsearch job projection and indexer
│   │   └── user/                 # User entities, roles, and repository
│   ├── src/main/resources/       # application.yml and Flyway migrations
│   ├── pom.xml                   # Maven dependencies and build definition
│   └── Dockerfile                # Multi-stage production container build
├── frontend/                     # React 19 + TypeScript SPA
│   ├── src/
│   │   ├── api/                  # Axios HTTP client and TypeScript interfaces
│   │   ├── auth/                 # React Context auth provider and token management
│   │   ├── components/           # Navigation header, protected route guards
│   │   └── pages/                # Public careers, candidate, and recruiter views
│   ├── package.json              # NPM dependencies and scripts
│   ├── vite.config.ts            # Vite configuration
│   └── Dockerfile                # Multi-stage build with Nginx reverse proxy
├── compose.yaml                  # Local multi-service orchestration
├── .env.example                  # Environment configuration template
└── README.md                     # Project documentation
```

---

## Quickstart

### Option A: Run with Docker Compose (Recommended)

1. Clone your repository:
   ```bash
   git clone https://github.com/MissAnisha/talenttrack-ats.git
   cd talenttrack-ats
   ```

2. Create your local environment configuration:
   ```bash
   cp .env.example .env
   ```

3. Spin up the full system stack (PostgreSQL, Elasticsearch, Spring Boot API, and Nginx/React):
   ```bash
   docker compose up --build
   ```

4. Access the applications:
   - **Web Application:** `http://localhost:3000`
   - **API Health Check:** `http://localhost:8080/api/health`

### Demonstration Accounts

When `SEED_DEMO=true` is enabled in your environment (default in Docker Compose):
- **Recruiter Console:** `recruiter@talenttrack.dev` / `Recruiter123!`
- **Candidate Portal:** `candidate@talenttrack.dev` / `Candidate123!`

---

### Option B: Local Native Development

#### 1. Start PostgreSQL
Ensure PostgreSQL is running locally on port 5432 with database `talenttrack`:
```bash
createdb talenttrack
```

#### 2. Run the Backend API
```bash
cd backend
./mvnw spring-boot:run
```
*(On Windows PowerShell, use `.\mvnw.cmd spring-boot:run`)*

The API will start at `http://localhost:8080`.

#### 3. Run the Frontend Client
```bash
cd frontend
npm install
npm run dev
```

The Vite dev server will start at `http://localhost:5173`.

---

## Environment Configuration

| Variable | Description | Default |
|---|---|---|
| `POSTGRES_DB` | PostgreSQL database name | `talenttrack` |
| `POSTGRES_USER` | PostgreSQL username | `talenttrack` |
| `POSTGRES_PASSWORD` | PostgreSQL password | `talenttrack` |
| `DATABASE_URL` | Full JDBC database connection string | `jdbc:postgresql://localhost:5432/talenttrack` |
| `JWT_SECRET` | 256-bit cryptographically secure signing key | `${JWT_SECRET}` |
| `PORT` | API server port | `8080` |
| `CORS_ORIGIN` | Allowed CORS client origin | `http://localhost:3000` |
| `ELASTICSEARCH_ENABLED` | Toggle Elasticsearch text search | `false` |
| `SEED_DEMO` | Seed sample jobs and demo users on startup | `false` |
| `VITE_API_URL` | Base API URL consumed by the React client | `http://localhost:8080/api` |

---

## Quality & Verification

Run automated test suites and linters:

```bash
# Run backend Spring Boot unit and integration tests
cd backend
./mvnw test

# Lint and build production frontend bundle
cd ../frontend
npm run lint
npm run build
```

---

## API Overview

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `GET` | `/api/health` | Public | System status and service health |
| `POST` | `/api/auth/register` | Public | Register new candidate or recruiter account |
| `POST` | `/api/auth/login` | Public | Authenticate user and receive signed JWT token |
| `GET` | `/api/jobs` | Public | Pageable list of active jobs with filter parameters |
| `GET` | `/api/jobs/{id}` | Public | Job details, stages, and requirements |
| `GET` | `/api/candidate/me` | Candidate | Candidate profile details |
| `PUT` | `/api/candidate/profile`| Candidate | Update candidate profile |
| `GET` | `/api/candidate/applications` | Candidate | Retrieve candidate's submitted applications |
| `POST` | `/api/candidate/jobs/{id}/apply` | Candidate | Submit job application with resume upload |
| `GET` | `/api/recruiter/jobs` | Recruiter | Recruiter's managed job listings |
| `POST` | `/api/recruiter/jobs` | Recruiter | Create a new job listing with stages |
| `GET` | `/api/recruiter/jobs/{jobId}` | Recruiter | Pipeline view with applications and stages |
| `POST` | `/api/recruiter/applications/{appId}/move` | Recruiter | Transition applicant to next pipeline stage |
| `POST` | `/api/recruiter/applications/{appId}/notes` | Recruiter | Add internal interview/evaluation note |

---

## Security Practices

- **Token Lifecycle:** Secrets should be at least 32 characters (256 bits). For production, generate using `openssl rand -base64 32`.
- **Credential Storage:** User passwords are encrypted with BCrypt (strength 10).
- **Environment Isolation:** Secrets are kept out of source code and loaded strictly via `.env` or container runtime variables.
- **Upload Hardening:** Uploaded files are isolated, validated for permitted extensions (`.pdf`, `.doc`, `.docx`), and limited to 10 MB.

---

## License & Copyright Notice

All third-party dependencies remain subject to their respective open-source licenses (Spring Boot, JJWT, and Flyway under Apache-2.0; React, Vite, and Axios under MIT; PostgreSQL JDBC Driver under BSD-2-Clause).

Licensing for the application source code itself is subject to verification of author permissions. Do not apply a public open-source license without confirming rights.

# AGENTS.md

## Project overview

NotebookAnalyzer collects hardware and health data from notebooks, stores it, and
compares the results. The project currently contains:

- `bash/collector.sh` collects data from a Live Linux environment and writes JSON.
- `backend/notebook-analyzer-api/` is the main API, built with Spring Boot.
- `obsidian/` contains the project vision, workflow, technology choices, and notes
  in Portuguese.
- PostgreSQL database `notebook_analyzer` is available locally for the API.
- A FastAPI/Python analysis service and browser frontend are planned, but have not
  been created yet.

## Repository layout

- `bash/`: collection script. Its generated `result.json` is local-only because it
  may contain hardware serial numbers.
- `backend/notebook-analyzer-api/`: Maven module for the Spring Boot main API.
- `obsidian/`: product and technical documentation; preserve Portuguese unless a
  task explicitly calls for translation.

Future services belong in technology-specific top-level directories (for example,
`analysis/` or `frontend/`). Do not mix generated output with source files.

## Backend stack

The main API module uses Maven, Java 21, and Spring Boot 4.1.1. Its package root
is `com.notebookanalyzer.api`.

Current backend dependencies:

- Spring Web MVC for HTTP endpoints.
- Spring Data JPA for persistence.
- PostgreSQL JDBC driver.
- Flyway for versioned database migrations.
- Spring Validation for request validation.
- Spring Boot DevTools for local development.
- Spring Boot test starters for automated tests.

Use `application.yaml` for Spring configuration. Keep database credentials out of
version control; when datasource settings are added, read sensitive values from
environment variables or another local-only configuration source.

Create schema changes only through Flyway migrations in
`src/main/resources/db/migration/`. Use sequential names such as
`V1__create_notebooks.sql`; never create or alter project tables manually in
DataGrip.

## Working conventions

- Make small, focused changes and keep documentation aligned with behavior.
- Preserve the JSON contract produced by the collector unless coordinating a
  compatible change across the receiving API and consumers.
- Treat serial numbers and other machine identifiers as sensitive. Do not add real
  collected output containing identifiable hardware data to version control.
- Use lowercase names for directories and Java packages. Use PascalCase for Java
  classes and the Maven standard source layout.
- Prefer portable Bash compatible with the Live Linux target. Quote expansions,
  handle unavailable commands or data, and retain valid JSON when fields are
  unavailable.
- Do not make the collector install packages or perform privileged operations
  without clear user authorization. Keep collection and setup concerns separate
  when evolving the script.
- Use UTF-8 and keep existing Portuguese terminology consistent.

## Validation

- For Bash changes, run `bash -n bash/collector.sh`.
- Validate JSON fixtures or generated output with `jq empty <file>` when `jq` is
  available.
- For API changes, run `./mvnw test` from `backend/notebook-analyzer-api/`.
- Add focused automated tests alongside each new endpoint, service, repository, or
  migration-related behavior.
- Never execute hardware probing, `sudo`, package installation, or SMART commands
  merely to validate a change; they can alter the host or require real devices.

## Architecture boundaries

- The collector owns hardware discovery and the emitted JSON payload.
- Spring Boot owns API validation, persistence, Flyway migrations, and
  orchestration.
- PostgreSQL stores notebook and collection history.
- FastAPI/Python owns scoring, comparison, and ranking calculations.
- The frontend presents API data and should not duplicate analysis logic.

When adding an endpoint, schema, or score, document the data contract and update
all affected layers deliberately.

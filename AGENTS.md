# AGENTS.md

## Project overview

NotebookAnalyzer collects hardware and health data from notebooks, stores it, and
compares the results. The current repository is an early-stage prototype:

- `Bash/collector.sh` collects data from a Live Linux environment and writes JSON.
- `Obsidian/` contains the project vision, workflow, technology choices, and notes
  in Portuguese.
- The planned system adds a Spring Boot API, PostgreSQL, a FastAPI/Python analysis
  service, and a browser frontend.

## Repository layout

- `Bash/`: collection script and a representative `result.json` output.
- `Obsidian/`: product and technical documentation; preserve Portuguese unless a
  task explicitly calls for translation.

Keep implementation code in technology-specific top-level directories as those
components are introduced (for example, `backend/`, `analysis/`, or `frontend/`).
Do not mix generated output with source files.

## Working conventions

- Make small, focused changes and keep documentation aligned with behavior.
- Preserve the JSON contract produced by the collector unless coordinating a
  compatible change across the receiving API and consumers.
- Treat serial numbers and other machine identifiers as sensitive. Do not add real
  collected output containing identifiable hardware data to version control.
- Prefer portable Bash compatible with the Live Linux target. Quote expansions,
  handle unavailable commands or data, and retain valid JSON when fields are
  unavailable.
- Do not make the collector install packages or perform privileged operations
  without clear user authorization. Keep collection and setup concerns separate
  when evolving the script.
- Use UTF-8 and keep existing Portuguese terminology consistent.

## Validation

- For Bash changes, run `bash -n Bash/collector.sh`.
- Validate JSON fixtures or generated output with `jq empty <file>` when `jq` is
  available.
- Add focused automated tests alongside each new service and run that service's
  standard test and formatting commands before handoff.
- Never execute hardware probing, `sudo`, package installation, or SMART commands
  merely to validate a change; they can alter the host or require real devices.

## Architecture boundaries

- The collector owns hardware discovery and the emitted JSON payload.
- Spring Boot owns API validation, persistence, and orchestration.
- PostgreSQL stores notebook and test history.
- FastAPI/Python owns scoring, comparison, and ranking calculations.
- The frontend presents API data and should not duplicate analysis logic.

When adding an endpoint, schema, or score, document the data contract and update
all affected layers deliberately.

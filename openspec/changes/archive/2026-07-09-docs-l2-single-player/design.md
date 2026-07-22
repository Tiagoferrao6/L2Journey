## Context

L2Journey is a traditional MMORPG server emulator. Transitioning it into a Single Player experience requires redefining how the server operates, handles state, and fakes a populated world. We need to document these paradigms so that any AI agent assisting with the project has a structured, context-rich knowledge base to draw from, avoiding hallucinations and ensuring architectural consistency.

## Goals / Non-Goals

**Goals:**
- Design a structured `/docs` directory acting as the ultimate source of truth for the Single Player vision.
- Define specific Markdown files tailored for AI ingestion (using tables, clear headers, and cross-references).
- Document the overarching architecture, data models, interfaces, and glossary of terms.

**Non-Goals:**
- Implementing any server code in this specific change (this change is strictly for bootstrapping the documentation).

## Decisions

- **Documentation Format**: Use standard Markdown (`.md`) with explicit use of tables for parameters/weights to ensure AI parsers (like LLMs) can easily read and construct logic based on the docs.
- **Directory Structure**: All docs will live in `/docs` at the root of the repository.
- **File Numbering**: Files will be prefixed with numbers (`01-visao.md`, `02-requisitos.md`, etc.) to suggest a logical reading order for new developers or agents onboarding to the project.
- **SPEC.md as Master Ledger**: Use `SPEC.md` as the central tracker for the status of implemented vs planned features, serving as an entry point for agents to understand the project's current state.

## Risks / Trade-offs

- **Risk: Documentation Drift** -> Mitigation: Require any future `/opsx-apply` changes to update relevant `/docs` files as part of their tasks.
- **Trade-off: Initial overhead** -> Creating detailed docs takes time upfront but massively accelerates AI-assisted coding later by providing clear context.

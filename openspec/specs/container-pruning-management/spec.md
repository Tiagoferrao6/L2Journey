# container-pruning-management Specification

## Purpose
TBD - created by archiving change container-build-optimization. Update Purpose after archive.
## Requirements
### Requirement: Automated Container Image Pruning
The system environment SHALL configure shell shortcuts that execute image pruning immediately after building containers to prevent dangling image buildup.

#### Scenario: Rebuilding GameServer Prunes Dangling Images
- **WHEN** Developer executes `l2rebuild`
- **THEN** Only the `gameserver` container is rebuilt and `podman image prune -f` automatically purges untagged dangling images

### Requirement: Deep Clean Shortcut
The system environment SHALL provide a single command `l2clean` that purges unused containers, volumes, and dangling images.

#### Scenario: Executing l2clean
- **WHEN** Developer executes `l2clean`
- **THEN** Unused Podman system resources and dangling images are reclaimed automatically


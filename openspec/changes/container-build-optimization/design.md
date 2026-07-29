## Context

During active development, rebuilding the GameServer container creates untagged intermediate Podman layers. Without automated pruning, disk usage can exceed 20GB+ within a few build iterations.

## Goals / Non-Goals

**Goals:**
- Target `l2rebuild` to build only `gameserver` by default.
- Chain `podman image prune -f` after build commands to delete dangling untagged images automatically.
- Provide `l2rebuild-all` for full rebuilds and `l2clean` for deep system cleanup.

**Non-Goals:**
- Modifying core Podman storage configs or systemd timers.

## Decisions

- **Chain `podman image prune -f` in Aliases**: Ensures zero manual intervention required to keep storage clean after builds.
- **Single-Service Target (`gameserver`) for `l2rebuild`**: Cuts build time in half and avoids recreating untouched LoginServer containers.

## Risks / Trade-offs

- **[Risk] Loss of cached intermediate layers**: Running `podman image prune -f` removes dangling images, but keeps tagged base images cached.
  - *Mitigation*: Base images (e.g. `eclipse-temurin:24-jdk`) remain intact.

## Why

Frequent container rebuilds create dangling untagged images (`<none>:<none>`) and unused intermediate layers, which quickly consume dozens of gigabytes of disk space. Additionally, rebuilding all services when only GameServer code changes causes unnecessary build overhead.

## What Changes

- Update shell aliases in `~/.bashrc`:
  - `l2rebuild`: Rebuilds only the `gameserver` service and automatically purges dangling images (`podman image prune -f`).
  - `l2rebuild-all`: Rebuilds both `gameserver` and `loginserver` services followed by automatic image pruning.
  - `l2clean`: Dedicated disk cleanup command (`podman system prune -f && podman image prune -f`).
- Add a helper cleanup script or documented workflow for developers to prevent storage bloat.

## Capabilities

### New Capabilities
- `container-pruning-management`: Provides automated image pruning and targeted service rebuilding to eliminate disk bloat and speed up development rebuilds.

### Modified Capabilities

## Impact

- User shell configuration (`~/.bashrc`).
- Local Podman development workflow and storage management.

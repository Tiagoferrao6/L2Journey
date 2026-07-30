# Proposal: Fix Container Classpath Precedence for GameServer & LoginServer

## Why
When running `l2journey_gameserver` and `l2journey_loginserver` containers, newly compiled code (such as the Real-Time Web Dashboard `WebAPIManager`) is not loaded by the JVM, causing port 8080 connections to reset (`Connection reset by peer`).

Investigation showed that `Dockerfile.gameserver` and `Dockerfile.login` copy pre-compiled legacy JARs (`dist/libs/GameServer.jar`) into `/opt/l2journey/libs/`. When Java starts with `-cp "./../libs/*:Gameserver.jar"`, the wildcard `./../libs/*` expands to `/opt/l2journey/libs/GameServer.jar` FIRST, forcing the JVM to load outdated classes from the legacy JAR instead of the newly built `Gameserver.jar`.

## What Changes
1. **`Dockerfile.gameserver`**: Change classpath order to `-cp "Gameserver.jar:./../libs/*"` and remove/exclude outdated `GameServer.jar` from `/opt/l2journey/libs/`.
2. **`Dockerfile.login`**: Change classpath order to `-cp "Loginserver.jar:./../libs/*"` and remove/exclude outdated `LoginServer.jar` from `/opt/l2journey/libs/`.
3. **Verification**: Rebuild container image, run `podman-compose up`, verify `WebAPIManager` logs and test `http://localhost:8080/api/status`.

## Impacted Artifacts
- `Dockerfile.gameserver`
- `Dockerfile.login`

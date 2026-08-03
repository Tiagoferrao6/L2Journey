# Technical Design: Fix Container Classpath Precedence

## Problem Statement
When running containerized `GameServer` or `LoginServer`, Java loads outdated `.class` files from legacy JARs inside `/opt/l2journey/libs/` due to wildcard classpath expansion `./../libs/*` occurring before `Gameserver.jar`.

## Solution Details

### 1. Classpath Reordering
In `Dockerfile.gameserver` and `Dockerfile.login`, change the `-cp` argument:
- `Dockerfile.gameserver`: `-cp "Gameserver.jar:./../libs/*"`
- `Dockerfile.login`: `-cp "Loginserver.jar:./../libs/*"`

### 2. Cleanup of Legacy Server JARs in Docker Image
Add a cleanup command in the runtime stage of both Dockerfiles to ensure `/opt/l2journey/libs/` contains only third-party dependency libraries:
```dockerfile
RUN rm -f /opt/l2journey/libs/GameServer.jar /opt/l2journey/libs/LoginServer.jar
```

## Verification & Test Plan
1. Rebuild images with `podman-compose build`.
2. Start services with `podman-compose up -d`.
3. Verify `podman logs l2journey_gameserver_1` contains:
   `WebAPIManager: Embedded REST API Server started successfully on port 8080`
4. Confirm `curl -i http://localhost:8080/api/status` returns `HTTP/1.1 200 OK`.

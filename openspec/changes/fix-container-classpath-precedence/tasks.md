# Tasks: Fix Container Classpath Precedence

## 1. Dockerfile Modifications
- [x] **1.1 Update `Dockerfile.gameserver`**
  - Adjust `-cp` in CMD to `"Gameserver.jar:./../libs/*"`.
  - Remove legacy server JARs from `/opt/l2journey/libs/`.
- [x] **1.2 Update `Dockerfile.login`**
  - Adjust `-cp` in CMD to `"Loginserver.jar:./../libs/*"`.
  - Remove legacy server JARs from `/opt/l2journey/libs/`.

## 2. Rebuild & Verification
- [x] **2.1 Rebuild Podman/Docker Images**
  - Run container rebuild command.
- [x] **2.2 Runtime E2E Validation**
  - Confirm `WebAPIManager` initialization in container logs.
  - Verify `curl -i http://localhost:8080/api/status` returns HTTP 200 OK.

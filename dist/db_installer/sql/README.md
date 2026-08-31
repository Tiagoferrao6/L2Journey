# L2Journey Database Installer (SQL Loader)

## Overview
This directory contains all the SQL files required to initialize the L2Journey database. 
The loading process is fully automated when starting the project via Docker or running the database installation scripts.

## How it works
The `00_run_sql.sh` script (located in `docker/mysql/init/00_run_sql.sh`) scans the `login` and `game` subdirectories and executes every file that ends with `.sql`.

### Crucial Rules:
1. **Alphabetical Order**: Files are loaded **alphabetically**. If one table depends on another, it must be ordered appropriately (e.g., `00_characters.sql` runs before files starting with `z_`).
2. **Active vs Disabled**: 
   - Any file ending in `.sql` **will be executed**.
   - Any file ending in `.disabled` (e.g., `fake_shops_accounts.sql.disabled`) will be **skipped**.

## Custom Testing Configurations
- **`z_custom_test_characters_setup.sql`**: Because it starts with `z_`, it runs *last*. It guarantees our testing accounts, clans, and characters (such as `SilverTester`, `TitanTester` and the 15 Gludio dwarves/fake shops) are correctly provisioned after all basic tables exist.
- **`cleanup_old_fake_shops.sql`**: A utility script created to drop legacy fake shops. If placed in `game/` as `.sql`, it will run, but it can be renamed to `.disabled` if the base characters table is already clean.

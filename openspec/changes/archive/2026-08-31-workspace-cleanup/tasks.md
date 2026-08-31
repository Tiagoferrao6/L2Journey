## 1. Clean the Root Directory
- [x] 1.1 Create a `tools` directory in the root of the project.
- [x] 1.2 Move all Python scripts (`generate_sql.py`, `generate_full_sql.py`, `fix_client_dat.py`, `historixotxt.py`) into the `tools` directory.
- [x] 1.3 Delete unused temporary JSON and text files (`stat1.json`, `stat2.json`, `stat3.json`, `stat4.json`, `temp_out.txt`, `temp_weap.txt`, `silver_skills.txt`, `titan_skills.txt`, `scratch_explore.md`).

## 2. Document the Database Installation Process
- [x] 2.1 Create a `README.md` in `dist/db_installer/sql` explaining how `00_run_sql.sh` automatically loads `.sql` files alphabetically.
- [x] 2.2 Verify that `cleanup_old_fake_shops.sql` and `z_custom_test_characters_setup.sql` are active, and document their purpose in the new README.

## 3. Final Verification
- [x] 3.1 Validate the root structure.
- [x] 3.2 Ensure the project is ready to compile and run with the standard setup workflow without relying on loose files.

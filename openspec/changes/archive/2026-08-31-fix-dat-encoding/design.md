# Design: Fix DAT Encoding

The solution is straightforward. Python's built-in `utf-16` encoding handles UTF-16LE with BOM correctly for Windows applications. 

By replacing `utf-8-sig` with `utf-16` in all file `open()` calls, the script will output `.txt` files with the `FF FE` BOM and 2-byte characters that `l2asm.exe` natively understands.

## Changes

- Modify `fix_client_dat.py` line 111: `with open(filepath, 'r', encoding='utf-16') as f:`
- Modify `fix_client_dat.py` line 165: `with open(filepath, 'w', encoding='utf-16', newline='') as f:`
- Modify `fix_client_dat.py` line 175: `with open(filepath, 'r', encoding='utf-16') as f:`
- Modify `fix_client_dat.py` line 200: `with open(filepath, 'w', encoding='utf-16', newline='') as f:`

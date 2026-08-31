# Proposal: Fix DAT File Encoding

## Context
When modifying decrypted DAT client files (`weapongrp.txt`, `armorgrp.txt`, `itemname-e.txt`) using the `fix_client_dat.py` script, the resulting text files fail to compile back into binary `.dat` files using L2FileEdit (`l2asm.exe`). The tool outputs the error `Не удалось создать файл: dec-weapongrp.dat`.

## Problem
The Lineage 2 `l2asm.exe` compiler strictly requires input text files to be encoded in **UTF-16LE** (which contains a `FF FE` Byte Order Mark, and is often labeled just "Unicode" in Windows editors). 

The `fix_client_dat.py` script currently reads and writes files using `utf-8-sig` (UTF-8 with a BOM). This encoding mismatch causes `l2asm.exe` to fail to parse the file structure, crashing or exiting immediately without producing the expected output binary file.

## Goal
Update `fix_client_dat.py` to read and write the text files using `utf-16` encoding. This will ensure that `l2asm.exe` can properly parse the modified structure and compile the `.dat` files successfully.

## Scope
Modify `fix_client_dat.py`:
1. In `fix_grp_file()`: update `encoding='utf-8-sig'` to `encoding='utf-16'` on the `open()` calls for read and write.
2. In `fix_itemname()`: update `encoding='utf-8-sig'` to `encoding='utf-16'` on the `open()` calls for read and write.

## Out of Scope
- No changes to the actual logic of item mapping or tattoo additions.
- No changes to the L2FileEdit client tools.

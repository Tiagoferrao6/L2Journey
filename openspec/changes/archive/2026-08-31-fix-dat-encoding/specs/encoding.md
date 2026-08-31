# Encoding Specification

## 1. File Encoding Requirements

When reading and writing L2 DAT text files, the system MUST use `UTF-16LE` encoding (with BOM) to remain compatible with Lineage 2 DAT compilation tools.

- **Reader:** Must decode files using `utf-16`.
- **Writer:** Must encode files using `utf-16` to ensure the `FF FE` BOM is written at the beginning of the file.

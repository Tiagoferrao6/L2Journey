# Proposal: Simplify Fake Shops

## Context
The server currently has hundreds of fake shops spawned across all cities (Giran, Aden, Rune, Goddard, etc.) as defined in `fake_shops.xml` and initialized in the database. While this populates the world, it creates a lot of noise, makes testing isolated behaviors difficult, and clutters the database and XML files.

In Gludio, there is a specific group of 15 dwarves named after Lord of the Rings characters.

## Problem
Testing the Fake Players economy (buy, sell dynamically, sell fixed items, and craft) is currently chaotic due to the massive number of active fake shops. Furthermore, the XML is overly bloated with hundreds of shops, and the database contains dozens of offline traders that are not strictly necessary for validation.

## Goal
Isolate the fake player economy testing to only the 15 dwarf characters in Gludio. Remove the other shops from the game world to clean up the XML configuration and the database. Reorganize these 15 dwarves so they cover 100% of the required test scenarios for Fake Players.

## Scope
1. **XML Cleanup**: Remove all shops from `dist/game/data/fakeplayers/fake_shops.xml` except the 15 dwarves in Gludio.
2. **Reorganization**: Configure the 15 dwarves in `fake_shops.xml` into specific behavior groups:
    - **Dynamic Sellers**: Gimli, Thorin, Durin (reading from `city_catalogs.xml`)
    - **Fixed Sellers**: Balin, Dwalin, Fili, Kili (using `<customitem>`)
    - **Buyers**: Oin, Gloin, Bifur, Bofur (using `<customitem>`)
    - **Crafters**: Bombur, Nori, Ori, Dori (using `<customitem>`)
3. **Database Cleanup**: Delete all existing fake shop characters from the `characters` table except the 15 dwarves. Update the SQL setup script (`z_custom_test_characters_setup.sql`) to reflect this cleanup.

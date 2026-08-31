# Proposal: Equip Test Characters with Retail Items (Elegia/Vesper)

## Context
Currently, the test characters (SilverTester and TitanTester) are equipped with custom items from the Royal Dynasty set. We want to remove these custom items to test with standard retail high-end gear (Elegia armors and Vesper weapons). 
The characters should retain their custom tattoos and boss jewels.

## Goal
Replace the custom Royal Dynasty weapons and armors with retail Elegia (armors) and Vesper (weapons) items for the test characters in the setup script.

## Scope
1. Update `z_custom_test_characters_setup.sql` to remove all custom Royal Dynasty weapons and armors from both test characters' inventory and paperdoll.
2. Equip SilverTester (Moonlight Sentinel) with:
   - Elegia Leather Set (Helmet, Breastplate, Legging, Gloves, Boots)
   - Vesper Thrower (Bow)
3. Equip TitanTester (Titan) with:
   - Elegia Heavy Set (Helmet, Breastplate, Gaiter, Gauntlet, Boots)
   - Vesper Slasher (Two-Handed Sword)
4. Ensure elementals (e.g., 300 attribute for weapons, 120 for armors) are properly applied to the new retail items.
5. Keep tattoos, boss jewels, and cloaks untouched.

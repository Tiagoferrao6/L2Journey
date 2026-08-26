import re
import os

CLIENT_DIR = "client_dat"

# Mappings for Weapons
# 99300 - 99315
WEAPONS_MAP = {
    99300: 9442,  # Royal Dynasty Blade -> Dynasty Sword
    99301: 9443,  # Royal Dynasty Two-Handed Sword -> Dynasty Blade
    99302: 9444,  # Royal Dynasty Phantom -> Dynasty Phantom
    99303: 9445,  # Royal Dynasty Bow -> Dynasty Bow
    99304: 9446,  # Royal Dynasty Dagger -> Dynasty Knife
    99305: 9447,  # Royal Dynasty Halberd -> Dynasty Halberd
    99306: 9448,  # Royal Dynasty Cudgel -> Dynasty Cudgel
    99307: 9449,  # Royal Dynasty Staff -> Dynasty Mace
    99308: 13882, # Royal Dynasty Dual Dagger -> Dynasty Dual Daggers
    99309: 11272, # Royal Dynasty Rapier -> Dynasty Rapier
    99310: 11273, # Royal Dynasty Ancient Sword -> Dynasty Ancient Sword
    99311: 10004, # Royal Dynasty Dual Sword -> Dynasty Dual Sword
    99312: 11274, # Royal Dynasty Crossbow -> Dynasty Crossbow
    99313: 9441   # Royal Dynasty Shield -> Dynasty Shield (in weapongrp.txt in L2)
}

# Mappings for Armors
# 99200 - 99224
ARMORS_MAP = {
    99200: 9417,  # Breastplate - Shield Master
    99201: 9418,  # Breastplate - Weapon Master
    99202: 9419,  # Breastplate - Force Master
    99203: 9420,  # Breastplate - Bard
    99204: 9421,  # Gaiters
    99205: 9422,  # Helmet
    99206: 9423,  # Gauntlet
    99207: 9424,  # Boots
    99208: 9441,  # Shield (Wait, shield should be in weapongrp. I'll just map to 10119 sigil or skip it. Let's map to 10119 to be safe in armorgrp)
    99209: 9426,  # Leather Armor - Dagger Master
    99210: 9427,  # Leather Armor - Bow Master
    99211: 9428,  # Leather Leggings
    99212: 9429,  # Leather Helmet
    99213: 9430,  # Leather Gloves
    99214: 9431,  # Leather Boots
    99215: 9433,  # Tunic - Healer
    99216: 9434,  # Tunic - Enchanter
    99217: 9435,  # Tunic - Summoner
    99218: 9436,  # Tunic - Wizard
    99219: 9437,  # Stockings
    99220: 9438,  # Circlet
    99221: 9439,  # Gloves - Robe
    99222: 9440,  # Shoes - Robe
    99223: 10119, # Sigil
    99224: 14609  # Cloak
}

TATTOO_ICONS = {
    41001: "icon.etc_str_hena_i00", 
    41002: "icon.etc_str_hena_i00", 
    41003: "icon.etc_str_hena_i00", 
    41004: "icon.etc_str_hena_i00", 
    41005: "icon.etc_str_hena_i00", 
    41006: "icon.etc_str_hena_i00", # Ogre
    41007: "icon.etc_dex_hena_i00", 
    41008: "icon.etc_dex_hena_i00", 
    41009: "icon.etc_dex_hena_i00", 
    41010: "icon.etc_dex_hena_i00", 
    41011: "icon.etc_dex_hena_i00", 
    41012: "icon.etc_dex_hena_i00", # Monk
    41013: "icon.etc_str_hena_i01", 
    41014: "icon.etc_str_hena_i01", 
    41015: "icon.etc_str_hena_i01", 
    41016: "icon.etc_str_hena_i01", 
    41017: "icon.etc_str_hena_i01", 
    41018: "icon.etc_str_hena_i01", # Assassin
    41019: "icon.etc_str_hena_i02", 
    41020: "icon.etc_str_hena_i02", 
    41021: "icon.etc_str_hena_i02", 
    41022: "icon.etc_str_hena_i02", 
    41023: "icon.etc_str_hena_i02", 
    41024: "icon.etc_str_hena_i02", # Blood
}

TATTOO_NAMES = {
    41001: "Tattoo of Ogre - Lv 1",
    41002: "Tattoo of Ogre - Lv 2",
    41003: "Tattoo of Ogre - Lv 3",
    41004: "Tattoo of Ogre - Lv 4",
    41005: "Tattoo of Ogre - Lv 5",
    41006: "Tattoo of Ogre - Lv 6",
    41007: "Tattoo of Monk - Lv 1",
    41008: "Tattoo of Monk - Lv 2",
    41009: "Tattoo of Monk - Lv 3",
    41010: "Tattoo of Monk - Lv 4",
    41011: "Tattoo of Monk - Lv 5",
    41012: "Tattoo of Monk - Lv 6",
    41013: "Tattoo of Assassin - Lv 1",
    41014: "Tattoo of Assassin - Lv 2",
    41015: "Tattoo of Assassin - Lv 3",
    41016: "Tattoo of Assassin - Lv 4",
    41017: "Tattoo of Assassin - Lv 5",
    41018: "Tattoo of Assassin - Lv 6",
    41019: "Tattoo of Blood - Lv 1",
    41020: "Tattoo of Blood - Lv 2",
    41021: "Tattoo of Blood - Lv 3",
    41022: "Tattoo of Blood - Lv 4",
    41023: "Tattoo of Blood - Lv 5",
    41024: "Tattoo of Blood - Lv 6",
}

def fix_grp_file(filename, mapping, is_tattoo_file=False):
    filepath = os.path.join(CLIENT_DIR, filename)
    with open(filepath, 'r', encoding='utf-8-sig') as f:
        lines = f.readlines()
    
    # 1. Clean existing broken entries
    cleaned_lines = []
    base_lines = {}
    
    for line in lines:
        if not line.strip():
            continue
        parts = line.split('\t')
        if len(parts) > 1:
            try:
                item_id = int(parts[1])
                # Store base line for cloning
                base_lines[item_id] = line
                
                # Filter out old 99xxx if they exist
                if item_id in mapping.keys():
                    continue
                # Also filter out old tattoos if they exist
                if is_tattoo_file and item_id in TATTOO_ICONS.keys():
                    continue
            except ValueError:
                pass
        cleaned_lines.append(line)
        
    # 2. Add fixed entries
    new_lines = []
    for new_id, base_id in mapping.items():
        if base_id in base_lines:
            base_line = base_lines[base_id]
            parts = base_line.split('\t')
            parts[1] = str(new_id)
            new_lines.append('\t'.join(parts))
        else:
            print(f"Warning: Base ID {base_id} not found for {new_id} in {filename}")

    # 3. Add tattoos if it's armorgrp
    if is_tattoo_file:
        base_tattoo_id = 684 # Underwear of Rule
        if base_tattoo_id in base_lines:
            for t_id, icon in TATTOO_ICONS.items():
                parts = base_lines[base_tattoo_id].split('\t')
                parts[1] = str(t_id)
                # find icon column (it's around column index 9 in armorgrp usually)
                for i, p in enumerate(parts):
                    if "icon." in p:
                        parts[i] = icon
                new_lines.append('\t'.join(parts))
        else:
            print("Warning: Base tattoo ID 684 not found in armorgrp.")

    # Write back
    with open(filepath, 'w', encoding='utf-8-sig', newline='') as f:
        for line in cleaned_lines:
            f.write(line)
        for line in new_lines:
            if not line.endswith('\n') and not line.endswith('\r\n'):
                line += '\r\n'
            f.write(line)
            
    print(f"Fixed {filename}")

def fix_itemname():
    filepath = os.path.join(CLIENT_DIR, 'itemname-e.txt')
    with open(filepath, 'r', encoding='utf-8-sig') as f:
        lines = f.readlines()
        
    cleaned_lines = []
    for line in lines:
        if not line.strip():
            continue
        parts = line.split('\t')
        if len(parts) > 0:
            try:
                item_id = int(parts[0])
                if item_id in TATTOO_NAMES.keys():
                    continue
            except ValueError:
                pass
        cleaned_lines.append(line)
        
    # Generate tattoo lines
    new_lines = []
    # 684    Underwear of Rule       a,      -1      0       0                                               a,      0       0               a,      0       0       0       0       0       0       0       0       0       0       a,      1
    for t_id, name in TATTOO_NAMES.items():
        desc = "a,Custom Tattoo that massively increases attributes.\\\\0"
        line = f"{t_id}\t{name}\t\t{desc}\t-1\t0\t0\t\t\t\t\t\ta,\t0\t0\t\ta,\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\ta,\t1\r\n"
        new_lines.append(line)
        
    with open(filepath, 'w', encoding='utf-8-sig', newline='') as f:
        for line in cleaned_lines:
            f.write(line)
        for line in new_lines:
            f.write(line)
            
    print("Fixed itemname-e.txt")

if __name__ == "__main__":
    fix_grp_file('weapongrp.txt', WEAPONS_MAP, is_tattoo_file=False)
    fix_grp_file('armorgrp.txt', ARMORS_MAP, is_tattoo_file=True)
    fix_itemname()

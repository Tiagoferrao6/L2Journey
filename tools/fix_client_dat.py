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

# Mappings for EtcItems
ETCITEMS_MAP = {
    99900: 7186   # Golkonda Horn (cloned from Horn of Buffalo)
}

TATTOO_TYPES = [
    ("Ogre", "icon.etc_str_hena_i00"),
    ("Monk", "icon.etc_dex_hena_i00"),
    ("Assassin", "icon.etc_str_hena_i01"),
    ("Blood", "icon.etc_str_hena_i02"),
    ("Soul", "icon.etc_dex_hena_i02"),
    ("Flame", "icon.etc_men_hena_i00"),
    ("Absolute", "icon.etc_wit_hena_i00")
]

TATTOO_ICONS = {}
TATTOO_NAMES = {}

current_id = 41001
for side in ["Right", "Left"]:
    for name, icon in TATTOO_TYPES:
        for lvl in range(1, 7):
            TATTOO_ICONS[current_id] = icon
            TATTOO_NAMES[current_id] = f"Tattoo of {name} - Lv {lvl} ({side})"
            current_id += 1

def fix_grp_file(filename, mapping, is_tattoo_file=False):
    filepath = os.path.join(CLIENT_DIR, filename)
    with open(filepath, 'r', encoding='utf-16') as f:
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
                
                # Filter out ALL custom items (>= 99000) so we don't keep corrupted manual lines
                if item_id >= 99000:
                    continue
                # Filter out ALL custom tattoos (>= 41000) if it's armorgrp
                if is_tattoo_file and item_id >= 41000:
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
        base_right_tattoo = 684   # Underwear of Rule
        base_left_tattoo = 13740  # Agathion Bracelet (uses lbracelet slot in client)
        
        for t_id, icon in TATTOO_ICONS.items():
            base_id = base_left_tattoo if t_id >= 41043 else base_right_tattoo
            if base_id in base_lines:
                parts = base_lines[base_id].split('\t')
                parts[1] = str(t_id)
                # find icon column (usually around index 9 or 18)
                for i, p in enumerate(parts):
                    if "icon." in p:
                        parts[i] = icon
                new_lines.append('\t'.join(parts))
            else:
                print(f"Warning: Base tattoo ID {base_id} not found in armorgrp.")

    # Write back
    # To prevent L2FileEdit encoding failures, the lines MUST be sorted by ID.
    # The first line is the header and should remain first.
    header = cleaned_lines[0]
    data_lines = cleaned_lines[1:] + new_lines
    
    # Sort data_lines by item_id (column index 1)
    def get_item_id(line):
        try:
            return int(line.split('\t')[1])
        except:
            return 0
            
    data_lines.sort(key=get_item_id)
    
    with open(filepath, 'w', encoding='utf-16', newline='') as f:
        f.write(header.rstrip('\r\n') + '\r\n')
        for line in data_lines:
            f.write(line.rstrip('\r\n') + '\r\n')
            
    print(f"Fixed {filename}")

def fix_itemname():
    filepath = os.path.join(CLIENT_DIR, 'itemname-e.txt')
    with open(filepath, 'r', encoding='utf-16') as f:
        lines = f.readlines()
        
    cleaned_lines = []
    base_lines = {}
    
    for line in lines:
        if not line.strip():
            continue
        parts = line.split('\t')
        if len(parts) > 0:
            try:
                item_id = int(parts[0])
                base_lines[item_id] = line
                
                # Filter out ALL custom items (>= 99000) so we don't keep corrupted manual lines
                if item_id >= 99000:
                    continue
                # Filter out ALL custom tattoos (>= 41000)
                if item_id >= 41000:
                    continue
                    
                # 3. Rename Gold Einhasad (4356) to Raid Coin
                if item_id == 4356:
                    parts[1] = "Raid Coin"
                    line = '\t'.join(parts)
            except ValueError:
                pass
        cleaned_lines.append(line)
        
    new_lines = []
    
    # 1. Clone Weapons, Armors and EtcItems
    for custom_map in [WEAPONS_MAP, ARMORS_MAP, ETCITEMS_MAP]:
        for new_id, base_id in custom_map.items():
            if base_id in base_lines:
                parts = base_lines[base_id].split('\t')
                parts[0] = str(new_id)
                # Ensure the name gets a generic prefix or something? The user just wanted name/description.
                # Actually, the user's DB specifies "Royal Dynasty...". We can just prefix it with "Royal ", or just let it use the original name.
                # "mude o nome para raid coin" -> Wait, for weapons they are "Royal Dynasty ...". If we clone, they will just be named "Dynasty ...".
                # Let's prefix the name with "Royal " if it's in custom map, because the user explicitly called them Royal Dynasty Weapons in the DB!
                original_name = parts[1]
                if new_id == 99900:
                    parts[1] = "Golkonda Horn"
                    parts[3] = r"a,A legendary horn dropped by Golkonda. Used to unlock Cumulative Skills.\0"
                else:
                    parts[1] = f"Royal {original_name}"
                new_lines.append('\t'.join(parts))
            else:
                print(f"Warning: Base ID {base_id} not found in itemname-e for {new_id}")

    # 2. Generate Tattoos
    tattoo_stats_map = {
        "Ogre": "P. Atk.",
        "Monk": "Atk. Spd.",
        "Assassin": "Critical Rate",
        "Blood": "Max HP",
        "Soul": "Evasion",
        "Flame": "M. Atk.",
        "Absolute": "Casting Spd."
    }
    
    for t_id, name in TATTOO_NAMES.items():
        # Extact name parts, e.g. "Tattoo of Ogre - Lv 1 (Right)"
        import re
        match = re.match(r"Tattoo of (\w+) - Lv (\d+)", name)
        if match:
            t_type = match.group(1)
            lvl = int(match.group(2))
            stat_name = tattoo_stats_map.get(t_type, "Stats")
            desc = fr"a,Custom Tattoo. Increases {stat_name} by Level {lvl}.\0"
        else:
            desc = r"a,Custom Tattoo.\0"
            
        line = f"{t_id}\t{name}\t\t{desc}\t-1\t0\t0\t\t\t\t\t\ta,\t0\t0\t\ta,\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\ta,\t1\r\n"
        new_lines.append(line)
        
    header = cleaned_lines[0]
    data_lines = cleaned_lines[1:] + new_lines
    
    def get_item_id_itemname(line):
        try:
            return int(line.split('\t')[0])
        except:
            return 0
            
    data_lines.sort(key=get_item_id_itemname)
    
    with open(filepath, 'w', encoding='utf-16', newline='') as f:
        f.write(header.rstrip('\r\n') + '\r\n')
        for line in data_lines:
            f.write(line.rstrip('\r\n') + '\r\n')
            
    print("Fixed itemname-e.txt")

if __name__ == "__main__":
    fix_grp_file('weapongrp.txt', WEAPONS_MAP, is_tattoo_file=False)
    fix_grp_file('armorgrp.txt', ARMORS_MAP, is_tattoo_file=True)
    fix_grp_file('etcitemgrp.txt', ETCITEMS_MAP, is_tattoo_file=False)
    fix_itemname()

import os
import xml.etree.ElementTree as ET

sm_classes = {18, 22, 24, 102, 19, 21, 100}
titan_classes = {44, 47, 48, 113, 0, 1, 3, 89}

sm_skills = {}
titan_skills = {}

# Subclass certification skills (level 1)
cert_skills = [631, 632, 633, 650, 651]

for root_dir, _, files in os.walk('dist/game/data/skillTrees/'):
    for f in files:
        if f.endswith('.xml'):
            path = os.path.join(root_dir, f)
            try:
                tree = ET.parse(path)
                root_el = tree.getroot()
                for st in root_el.findall('.//skillTree'):
                    c_id = st.get('classId')
                    if not c_id: continue
                    c_id = int(c_id)
                    for s in st.findall('.//skill'):
                        s_id = int(s.get('skillId'))
                        s_lvl = int(s.get('skillLevel'))
                        
                        if c_id in sm_classes:
                            if s_id not in sm_skills or s_lvl > sm_skills[s_id]:
                                sm_skills[s_id] = s_lvl
                        if c_id in titan_classes:
                            if s_id not in titan_skills or s_lvl > titan_skills[s_id]:
                                titan_skills[s_id] = s_lvl
            except Exception as e:
                pass

for cs in cert_skills:
    sm_skills[cs] = 1
    titan_skills[cs] = 1
    
with open('silver_skills.txt', 'w') as f:
    for s_id, s_lvl in sm_skills.items():
        f.write(f"(300000000, {s_id}, {s_lvl}, 0),\n")

with open('titan_skills.txt', 'w') as f:
    for s_id, s_lvl in titan_skills.items():
        f.write(f"(300000001, {s_id}, {s_lvl}, 0),\n")

print(f"SM Skills: {len(sm_skills)}, Titan Skills: {len(titan_skills)}")

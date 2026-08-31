import os, json, glob, re
from datetime import datetime

BRAIN_DIR = os.path.expanduser("~/.gemini/antigravity-ide/brain")
OUTPUT_DIR = os.path.expanduser("~/historico_conversas_txt")
os.makedirs(OUTPUT_DIR, exist_ok=True)

def extract_user_request(text):
    if not text: return ""
    match = re.search(r'<USER_REQUEST>\s*(.*?)\s*</USER_REQUEST>', text, re.DOTALL)
    if match: return match.group(1).strip()
    idx = text.find("<ADDITIONAL_METADATA>")
    return text[:idx].strip() if idx != -1 else text.strip()

for conv_dir in glob.glob(os.path.join(BRAIN_DIR, "*")):
    if not os.path.isdir(conv_dir): continue
    conv_id = os.path.basename(conv_dir)
    log_file = os.path.join(conv_dir, ".system_generated", "logs", "transcript_full.jsonl")
    if not os.path.exists(log_file): continue
    
    entries, first_prompt, created_at = [], "", ""
    with open(log_file, "r", encoding="utf-8", errors="ignore") as f:
        for line in f:
            try: data = json.loads(line)
            except: continue
            msg_type, src, content, ts = data.get("type"), data.get("source"), data.get("content", ""), data.get("created_at", "")
            if not created_at and ts: created_at = ts
            
            if msg_type == "USER_INPUT":
                user_msg = extract_user_request(content)
                if user_msg:
                    if not first_prompt: first_prompt = user_msg
                    entries.append(f"--- USUÁRIO ({ts}) ---\n{user_msg}\n")
            elif msg_type == "PLANNER_RESPONSE" and src == "MODEL" and content.strip():
                entries.append(f"--- ASSISTENTE ({ts}) ---\n{content.strip()}\n")
                
    if not entries: continue
    slug = re.sub(r'[^\w\s-]', '', first_prompt[:40]).strip().replace(' ', '_') or "conversa"
    dt_str = created_at[:16].replace("T", "_").replace(":", "-") if created_at else "data_desconhecida"
    
    with open(os.path.join(OUTPUT_DIR, f"{dt_str}_{slug}_{conv_id[:8]}.txt"), "w", encoding="utf-8") as out:
        out.write(f"ID da Conversa: {conv_id}\nData: {created_at}\n" + "="*60 + "\n\n" + "\n\n".join(entries))

print("Conversas exportadas com sucesso!")

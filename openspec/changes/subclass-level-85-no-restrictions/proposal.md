# Subclasse Nível 85 Sem Restrições de Habilidades

## Summary
Permitir que todas as subclasses evoluam até o nível máximo 85 (anteriormente limitado ao nível 80) e remover qualquer restrição de aprendizado ou retenção de habilidades ao trocar de subclasse.

## Motivation
No Lineage II High Five padrão, as subclasses são travadas no nível 80 e possuem limitações no acúmulo e retenção de certas habilidades entre main e subclasses. Expandir o limite para o nível 85 e permitir a retenção completa de habilidades concede flexibilidade total aos jogadores no endgame.

## Proposed Changes
1. **Configuração `MaxSubclassLevel`**:
   - Alterar `MaxSubclassLevel = 85` em `dist/game/config/player/character.ini`.
2. **Ajuste no Core (`SubClassHolder.java`)**:
   - Corrigir a verificação `MAX_LEVEL` para permitir que subclasses alcancem o nível 85 completo sem travar no nível 84 (`ExperienceData.getInstance().getMaxLevel()`).
3. **Remoção de Restrições de Skills em Subclasse**:
   - Manter as habilidades aprendidas disponíveis e ativas independente da subclasse selecionada, se configurado.

## Verification Plan
- Subir uma subclasse até o nível 85 via comando GM `//setlevel 85` ou em jogo e verificar que a experiência e barra de nível permanecem ativas em 85.
- Verificar que o aprendizado de skills de nível 81+ funciona normalmente na subclasse.

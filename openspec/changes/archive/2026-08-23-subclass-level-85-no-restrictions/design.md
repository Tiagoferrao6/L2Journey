# Design: Subclasse Nível 85 Sem Restrições

## Architecture & Logic Changes

### 1. Configuração de Nível Máximo de Subclasse
No arquivo `dist/game/config/player/character.ini`:
```ini
MaxSubclassLevel = 85
```

### 2. Ajuste no Core Java (`SubClassHolder.java`)
Na classe `java/com/l2journey/gameserver/model/actor/holders/player/SubClassHolder.java`:
```java
// ANTES:
private static final byte MAX_LEVEL = Config.MAX_SUBCLASS_LEVEL < ExperienceData.getInstance().getMaxLevel() ? Config.MAX_SUBCLASS_LEVEL : (byte) (ExperienceData.getInstance().getMaxLevel() - 1);

// DEPOIS:
private static final byte MAX_LEVEL = Config.MAX_SUBCLASS_LEVEL <= ExperienceData.getInstance().getMaxLevel() ? Config.MAX_SUBCLASS_LEVEL : ExperienceData.getInstance().getMaxLevel();
```

Isso garante que, quando `MaxSubclassLevel` estiver configurado para `85`, a subclasse possa alcançar o nível 85 completo igual à classe principal.

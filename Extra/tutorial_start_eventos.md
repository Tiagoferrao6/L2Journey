# Configuração de Agendamento Automático de Eventos

## Visão Geral

Os eventos do servidor podem ser configurados para iniciar automaticamente em horários específicos usando o arquivo `config.xml` localizado na pasta do evento.

## Como Funciona

O sistema usa um padrão de agendamento similar ao **cron** (formato utilizado em sistemas Linux/Unix) através do elemento `<schedule>`.

---

## Formato do Padrão

```xml
<schedule pattern="minutos horas dia_do_mês mês dia_da_semana" />
```

### Campos do Padrão

| Campo | Valores Aceitos | Descrição |
|-------|-----------------|-----------|
| **Minutos** | 0-59 | Minuto da hora em que o evento iniciará |
| **Horas** | 0-23 | Hora do dia (formato 24 horas) |
| **Dia do Mês** | 1-31 | Dia do mês (1 = primeiro dia) |
| **Mês** | 1-12 | Mês do ano (1 = janeiro, 12 = dezembro) |
| **Dia da Semana** | 0-7 | Dia da semana (0 e 7 = domingo, 1 = segunda, ..., 6 = sábado) |

### Caractere Especial

- **`*` (asterisco)** = Significa "qualquer valor" ou "todos"
- Use vírgula para múltiplos valores: `1,3,5` = segunda, quarta e sexta
- Use hífen para intervalos: `1-5` = segunda a sexta

---

## Exemplos Práticos

### Exemplo 1: Evento todos os dias no mesmo horário
```xml
<!-- Executa às 20:00 (8 PM) todos os dias -->
<schedule pattern="0 20 * * *" />
```
- `0` = no minuto 0 (hora cheia)
- `20` = às 20 horas
- `* * *` = todo dia, todo mês, toda semana

### Exemplo 2: Múltiplos horários no mesmo dia
```xml
<!-- Executa às 14h, 20h e 22h todos os dias -->
<schedule pattern="0 14 * * *" />
<schedule pattern="0 20 * * *" />
<schedule pattern="0 22 * * *" />
```

### Exemplo 3: Apenas nos fins de semana
```xml
<!-- Executa às 20h apenas aos sábados e domingos -->
<schedule pattern="0 20 * * 0,6" />
```
- `0,6` = domingo (0) e sábado (6)

### Exemplo 4: Dias específicos da semana
```xml
<!-- Executa às 20h apenas segunda, quarta e sexta -->
<schedule pattern="0 20 * * 1,3,5" />
```
- `1,3,5` = segunda (1), quarta (3) e sexta (5)

### Exemplo 5: Apenas em dias específicos do mês
```xml
<!-- Executa às 20h nos dias 1 e 15 de cada mês -->
<schedule pattern="0 20 1,15 * *" />
```

### Exemplo 6: Horários diferentes por tipo de dia
```xml
<!-- Segunda a sexta às 20h -->
<schedule pattern="0 20 * * 1-5" />

<!-- Sábado e domingo às 14h e 22h -->
<schedule pattern="0 14 * * 0,6" />
<schedule pattern="0 22 * * 0,6" />
```

### Exemplo 7: A cada 30 minutos (usando múltiplas entradas)
```xml
<!-- Executa às 14:00, 14:30, 15:00, 15:30, etc. -->
<schedule pattern="0 14-23 * * *" />
<schedule pattern="30 14-23 * * *" />
```

---

## Como Ativar o Agendamento

### Passo 1: Localize o arquivo de configuração
Navegue até a pasta do evento:
```
dist/game/data/scripts/custom/events/[NomeDoEvento]/config.xml
```

### Passo 2: Edite o arquivo
Abra o `config.xml` em um editor de texto.

### Passo 3: Descomente ou adicione o schedule
O arquivo pode ter linhas comentadas (entre `<!-- -->`). Remova os comentários:

**ANTES (comentado - não funciona):**
```xml
<!-- <schedule pattern="0 20 * * *" /> -->
```

**DEPOIS (ativo):**
```xml
<schedule pattern="0 20 * * *" />
```

### Passo 4: Salve e reinicie o servidor
Após salvar as alterações, reinicie o servidor para que as configurações sejam aplicadas.

---

## Estrutura Completa do Arquivo

```xml
<?xml version="1.0" encoding="UTF-8"?>
<event name="Nome Do Evento" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../../../xsd/event.xsd">
	
	<!-- Comentário explicativo (opcional) -->
	<schedule pattern="0 20 * * *" />
	
	<!-- Você pode ter múltiplos schedules -->
	<schedule pattern="0 14 * * 6,0" />
	
</event>
```

---

## Dicas Importantes

1. **Formato 24 horas**: Use 0-23 para horas (14 = 2 PM, 20 = 8 PM, 23 = 11 PM)

2. **Minuto zero**: Para eventos em hora cheia, use `0` no campo de minutos

3. **Teste cuidadosamente**: Verifique se o horário configurado está correto antes de disponibilizar no servidor

4. **Múltiplos agendamentos**: Você pode ter quantos `<schedule>` precisar no mesmo arquivo

5. **Fuso horário**: Os horários seguem o fuso horário configurado no servidor

6. **Domingo**: Pode ser representado como `0` ou `7`

---

## Solução de Problemas

### O evento não inicia automaticamente
- ✓ Verifique se a linha `<schedule>` não está comentada
- ✓ Confirme que o padrão está no formato correto
- ✓ Certifique-se de que o servidor foi reiniciado após a alteração
- ✓ Verifique os logs do servidor para mensagens de erro

### Evento inicia em horário errado
- ✓ Confirme o fuso horário do servidor
- ✓ Lembre-se que o formato é 24 horas (20 = 8 PM, não 8 AM)
- ✓ Verifique se não há caracteres especiais inválidos no padrão

---

## Referência Rápida de Padrões Comuns

| Descrição | Padrão |
|-----------|--------|
| Todo dia às 20h | `0 20 * * *` |
| Todo dia às 14h e 20h | Use dois `<schedule>`: `0 14 * * *` e `0 20 * * *` |
| Apenas fins de semana às 20h | `0 20 * * 0,6` |
| Segunda a sexta às 20h | `0 20 * * 1-5` |
| Primeiro dia do mês às 12h | `0 12 1 * *` |
| Toda segunda-feira às 19h | `0 19 * * 1` |
| A cada hora (hora cheia) | `0 * * * *` |

---

## Suporte

Se você tiver dúvidas ou problemas com a configuração de agendamento, consulte os logs do servidor ou entre em contato com a equipe de suporte.

**Última atualização**: Outubro 2025

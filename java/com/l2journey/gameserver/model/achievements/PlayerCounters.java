package com.l2journey.gameserver.model.achievements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.gameserver.model.actor.Player;

/**
 * Manages and persists various player-specific counters for achievements.
 * @author KingHanker
 */
public class PlayerCounters
{
	private static final Logger LOGGER = Logger.getLogger(PlayerCounters.class.getName());
	
	private static final String LOAD_COUNTERS = "SELECT name, value FROM character_counters WHERE charId = ?";
	private static final String SAVE_COUNTER = "INSERT INTO character_counters (charId, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value = ?";
	
	private final Player _owner;
	private final Map<String, Long> _counters = new ConcurrentHashMap<>();
	/** Nomes de counters que foram alterados desde o último flush. */
	private final Set<String> _dirty = ConcurrentHashMap.newKeySet();
	/** Momento (ms) do último flush persistente. */
	private volatile long _lastFlush = System.currentTimeMillis();
	
	// ===== Infra global para flush periódico =====
	private static final Set<PlayerCounters> INSTANCES = new CopyOnWriteArraySet<>();
	private static final long FLUSH_INTERVAL_MS = 30_000L; // 30s
	private static final int MAX_DIRTY_BEFORE_FORCE = 25; // força flush se muitos tipos foram alterados rapidamente
	private static volatile boolean SCHEDULER_STARTED = false;
	
	// Lista de nomes de counters suportados (documentação / depuração)
	public static final String C_RAID_KILL = "raidkill";
	public static final String C_FISH_CAUGHT = "catchFish"; // segue nomenclatura do XML
	public static final String C_CRAFT = "craft";
	
	public PlayerCounters(Player owner)
	{
		_owner = owner;
		load();
		INSTANCES.add(this);
		startSchedulerIfNeeded();
	}
	
	/** Remove este registro da lista global (chamar em logout/desconexão). */
	public void dispose()
	{
		flush();
		INSTANCES.remove(this);
	}
	
	private static synchronized void startSchedulerIfNeeded()
	{
		if (SCHEDULER_STARTED)
		{
			return;
		}
		SCHEDULER_STARTED = true;
		try
		{
			// Usa ThreadPool central do projeto (mesmo utilizado em outros managers) se disponível
			com.l2journey.commons.threads.ThreadPool.scheduleAtFixedRate(() ->
			{
				for (PlayerCounters pc : INSTANCES)
				{
					try
					{
						pc.flushIfInterval();
					}
					catch (Exception e)
					{
						LOGGER.log(Level.WARNING, "Flush periódico falhou para jogador " + pc._owner.getName(), e);
					}
				}
			}, FLUSH_INTERVAL_MS, FLUSH_INTERVAL_MS);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Não foi possível iniciar scheduler de flush de counters", e);
		}
	}
	
	/**
	 * Loads all counters for the player from the database.
	 */
	private void load()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_COUNTERS))
		{
			ps.setInt(1, _owner.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					_counters.put(rs.getString("name"), rs.getLong("value"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not load counters for player " + _owner.getName(), e);
		}
	}
	
	/**
	 * Persists a single counter value to the database.
	 * @param name The name of the counter.
	 * @param value The value to save.
	 */
	/**
	 * Adiciona um counter individual ao batch preparado.
	 * @param con conexão (não usada diretamente aqui mas mantida para clareza)
	 * @param ps statement de batch preparado com {@link #SAVE_COUNTER}
	 * @param name nome do contador
	 * @param value valor a persistir
	 * @throws Exception propagada se falhar ao adicionar no batch
	 */
	private void persistOne(Connection con, PreparedStatement ps, String name, long value) throws Exception
	{
		ps.setInt(1, _owner.getObjectId());
		ps.setString(2, name);
		ps.setLong(3, value);
		ps.setLong(4, value);
		ps.addBatch();
	}
	
	/** Flush explícito (força escrita de todos os dirty). */
	public void flush()
	{
		final Set<String> snapshot = Set.copyOf(_dirty); // evita retenção longa do set mutável
		if (snapshot.isEmpty())
		{
			_lastFlush = System.currentTimeMillis();
			return;
		}
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SAVE_COUNTER))
		{
			for (String name : snapshot)
			{
				Long v = _counters.get(name);
				if (v != null)
				{
					persistOne(con, ps, name, v);
				}
			}
			ps.executeBatch();
			_dirty.removeAll(snapshot);
			_lastFlush = System.currentTimeMillis();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Falha ao flush counters de " + _owner.getName(), e);
		}
	}
	
	/** Flush condicionado a intervalo ou excesso de dirty. */
	private void flushIfInterval()
	{
		if (_dirty.isEmpty())
		{
			return;
		}
		long now = System.currentTimeMillis();
		if ((now - _lastFlush) >= FLUSH_INTERVAL_MS)
		{
			flush();
		}
	}
	
	/** Verifica se precisa flushar por volume. */
	private void flushIfNeededByVolume()
	{
		if (_dirty.size() >= MAX_DIRTY_BEFORE_FORCE)
		{
			flush();
		}
	}
	
	/**
	 * Gets the current value of a counter.
	 * @param name The name of the counter.
	 * @return The counter's value, or 0 if not present.
	 */
	public long getCounter(String name)
	{
		return _counters.getOrDefault(name, 0L);
	}
	
	/** Incrementa um contador simples em +1. */
	/**
	 * Incrementa em 1 o contador indicado.
	 * @param name nome do contador
	 * @return novo valor após incremento
	 */
	public long increment(String name)
	{
		return add(name, 1L);
	}
	
	/** Adiciona delta ao contador. */
	/**
	 * Soma um delta (positivo ou negativo) ao contador.
	 * @param name nome do contador
	 * @param delta valor a somar
	 * @return novo valor persistido
	 */
	public long add(String name, long delta)
	{
		if (delta == 0)
		{
			return getCounter(name);
		}
		long nv = _counters.merge(name, delta, Long::sum);
		_dirty.add(name);
		flushIfNeededByVolume();
		return nv;
	}
	
	/**
	 * Ajusta o contador para "value" apenas se o valor informado for maior que o atual (uso para máximos).
	 * @param name nome do contador
	 * @param value novo valor candidato
	 */
	public void setIfHigher(String name, long value)
	{
		_counters.compute(name, (k, v) ->
		{
			long current = (v == null ? 0L : v);
			if (value > current)
			{
				_dirty.add(name);
				flushIfNeededByVolume();
				return value;
			}
			return current;
		});
	}
	
	// ==== Helpers semânticos (facilitam chamadas diretas em pontos de evento) ====
	public void onRaidKill()
	{
		increment(C_RAID_KILL);
	}
	
	public void onFishCaught()
	{
		increment(C_FISH_CAUGHT);
	}
	
	public void onCraftSuccess()
	{
		increment(C_CRAFT);
	}
	/**
	 * Atualiza o maior nível de enchant já observado.
	 * @param enchantLevel nível de enchant a comparar
	 */
	
}

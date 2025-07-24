/*
 * Copyright (c) 2025 L2Journey Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * ---
 * 
 * Portions of this software are derived from the L2JMobius Project, 
 * shared under the MIT License. The original license terms are preserved where 
 * applicable..
 * 
 */
package com.l2journey.gameserver.managers.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;
import com.l2journey.gameserver.util.Broadcast;

public class LotteryManager
{
	public static final long SECOND = 1000;
	public static final long MINUTE = 60000;
	
	protected static final Logger LOGGER = Logger.getLogger(LotteryManager.class.getName());
	
	private static final String INSERT_LOTTERY = "INSERT INTO lottery(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_PRICE = "UPDATE lottery SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
	private static final String UPDATE_LOTTERY = "UPDATE lottery SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
	private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM lottery WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
	private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
	private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM lottery WHERE id = 1 and idnr = ?";
	
	protected int _number;
	protected long _prize;
	protected boolean _isSellingTickets;
	protected boolean _isStarted;
	protected long _enddate;
	
	protected LotteryManager()
	{
		_number = 1;
		_prize = Config.ALT_LOTTERY_PRIZE;
		_isSellingTickets = false;
		_isStarted = false;
		_enddate = System.currentTimeMillis();
		if (Config.ALLOW_LOTTERY)
		{
			(new startLottery()).run();
		}
	}
	
	public int getId()
	{
		return _number;
	}
	
	public long getPrize()
	{
		return _prize;
	}
	
	public long getEndDate()
	{
		return _enddate;
	}
	
	public void increasePrize(long count)
	{
		_prize += count;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_PRICE))
		{
			ps.setLong(1, _prize);
			ps.setLong(2, _prize);
			ps.setInt(3, _number);
			ps.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Lottery: Could not increase current lottery prize: " + e.getMessage(), e);
		}
	}
	
	public boolean isSellableTickets()
	{
		return _isSellingTickets;
	}
	
	public boolean isStarted()
	{
		return _isStarted;
	}
	
	private class startLottery implements Runnable
	{
		protected startLottery()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			try (Connection con = DatabaseFactory.getConnection();
				Statement statement = con.createStatement();
				ResultSet rset = statement.executeQuery(SELECT_LAST_LOTTERY))
			{
				if (rset.next())
				{
					_number = rset.getInt("idnr");
					if (rset.getInt("finished") == 1)
					{
						_number++;
						_prize = rset.getLong("newprize");
					}
					else
					{
						_prize = rset.getLong("prize");
						_enddate = rset.getLong("enddate");
						
						long currentTime = System.currentTimeMillis();
						if (_enddate < currentTime)
						{
							// Calcula quantos sorteios foram perdidos
							long missedLotteries = ((currentTime - _enddate) / 604800000) + 1;
							
							if (missedLotteries > 0)
							{
								// Atualiza para o próximo sorteio válido
								_number += missedLotteries;
								_enddate += (missedLotteries * 604800000);
								
								// Acumula o prêmio dos sorteios perdidos
								_prize += (Config.ALT_LOTTERY_PRIZE * missedLotteries);
								
								LOGGER.info("Lottery: Recovered from downtime. Skipped " + missedLotteries + " lotteries. New lottery #" + _number);
							}
							
							(new finishLottery()).run();
							return;
						}
						
						if (_enddate > System.currentTimeMillis())
						{
							_isStarted = true;
							ThreadPool.schedule(new finishLottery(), _enddate - System.currentTimeMillis());
							if (_enddate > (System.currentTimeMillis() + (12 * MINUTE)))
							{
								_isSellingTickets = true;
								ThreadPool.schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - (10 * MINUTE));
							}
							return;
						}
					}
				}
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, "Lottery: Could not restore lottery data: " + e.getMessage(), e);
			}
			
			_isSellingTickets = true;
			_isStarted = true;
			Broadcast.toAllOnlinePlayers("Lottery tickets are now available for Lucky Lottery #" + _number + ".");
			final Calendar finishtime = Calendar.getInstance();
			finishtime.setTimeInMillis(_enddate);
			finishtime.set(Calendar.MINUTE, 0);
			finishtime.set(Calendar.SECOND, 0);
			if (finishtime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			{
				finishtime.set(Calendar.HOUR_OF_DAY, 19);
				_enddate = finishtime.getTimeInMillis();
				_enddate += 604800000;
			}
			else
			{
				finishtime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				finishtime.set(Calendar.HOUR_OF_DAY, 19);
				_enddate = finishtime.getTimeInMillis();
			}
			
			final long endDate = _enddate - System.currentTimeMillis();
			ThreadPool.schedule(new stopSellingTickets(), Math.max(endDate - (10 * MINUTE), 0));
			ThreadPool.schedule(new finishLottery(), Math.max(endDate, 0));
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_LOTTERY))
			{
				ps.setInt(1, 1);
				ps.setInt(2, _number);
				ps.setLong(3, _enddate);
				ps.setLong(4, _prize);
				ps.setLong(5, _prize);
				ps.execute();
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, "Lottery: Could not store new lottery data: " + e.getMessage(), e);
			}
		}
	}
	
	private class stopSellingTickets implements Runnable
	{
		protected stopSellingTickets()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			_isSellingTickets = false;
			Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED));
		}
	}
	
	private class finishLottery implements Runnable
	{
		protected finishLottery()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			try
			{
				// 1. Gerar 5 números aleatórios distintos (1-20)
				int[] luckyNumbers = generateLuckyNumbers();
				
				// 2. Converter para representação em bits
				NumberCombo combo = convertToBitRepresentation(luckyNumbers);
				
				// 3. Contar bilhetes vencedores
				WinnerCount winners = countWinningTickets(combo.enchant, combo.type2);
				
				// 4. Calcular prêmios
				PrizeDistribution prizes = calculatePrizeDistribution(winners);
				
				// 5. Atualizar banco de dados
				updateLotteryResults(combo, prizes);
				
				// 6. Notificar jogadores
				broadcastWinners(winners);
				
				// 7. Preparar próximo sorteio
				prepareNextLottery(prizes.newprize);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Error finishing lottery #" + _number + ": " + e.getMessage(), e);
				// Tentar novamente em 1 minuto
				ThreadPool.schedule(new finishLottery(), MINUTE);
			}
		}
		
		private int[] generateLuckyNumbers()
		{
			int[] numbers = new int[5];
			for (int i = 0; i < 5; i++)
			{
				boolean unique;
				do
				{
					unique = true;
					numbers[i] = Rnd.get(20) + 1; // Gera número entre 1-20
					
					// Verifica se já foi sorteado
					for (int j = 0; j < i; j++)
					{
						if (numbers[j] == numbers[i])
						{
							unique = false;
							break;
						}
					}
				}
				while (!unique);
			}
			return numbers;
		}
		
		private NumberCombo convertToBitRepresentation(int[] numbers)
		{
			int enchant = 0;
			int type2 = 0;
			
			for (int num : numbers)
			{
				if (num <= 16)
				{
					enchant |= 1 << (num - 1); // Define o bit correspondente (0-15)
				}
				else
				{
					type2 |= 1 << (num - 17); // Define o bit correspondente (0-3)
				}
			}
			return new NumberCombo(enchant, type2);
		}
		
		private WinnerCount countWinningTickets(int enchant, int type2) throws SQLException
		{
			WinnerCount winners = new WinnerCount();
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement(SELECT_LOTTERY_ITEM))
			{
				ps.setInt(1, _number);
				
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						int matches = countMatches(rs.getInt("enchant_level") & enchant, rs.getInt("custom_type2") & type2);
						
						// Classifica os acertos
						if (matches >= 5)
						{
							winners.count1++;
						}
						else if (matches == 4)
						{
							winners.count2++;
						}
						else if (matches == 3)
						{
							winners.count3++;
						}
						else if (matches >= 1)
						{
							winners.count4++;
						}
					}
				}
			}
			return winners;
		}
		
		private int countMatches(int curenchant, int curtype2)
		{
			int count = 0;
			
			// Conta bits setados em curenchant (números 1-16)
			for (int i = 0; i < 16; i++)
			{
				if ((curenchant & (1 << i)) != 0)
				{
					count++;
				}
			}
			
			// Conta bits setados em curtype2 (números 17-20)
			for (int i = 0; i < 4; i++)
			{
				if ((curtype2 & (1 << i)) != 0)
				{
					count++;
				}
			}
			
			return count;
		}
		
		private PrizeDistribution calculatePrizeDistribution(WinnerCount winners)
		{
			PrizeDistribution prizes = new PrizeDistribution();
			
			// Prêmio para 1-2 acertos (fixo por bilhete)
			prizes.prize4 = winners.count4 * Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
			
			// Distribui o restante do prêmio
			long remainingPrize = _prize - prizes.prize4;
			
			if (winners.count1 > 0)
			{
				prizes.prize1 = (long) ((remainingPrize * Config.ALT_LOTTERY_5_NUMBER_RATE) / winners.count1);
			}
			if (winners.count2 > 0)
			{
				prizes.prize2 = (long) ((remainingPrize * Config.ALT_LOTTERY_4_NUMBER_RATE) / winners.count2);
			}
			if (winners.count3 > 0)
			{
				prizes.prize3 = (long) ((remainingPrize * Config.ALT_LOTTERY_3_NUMBER_RATE) / winners.count3);
			}
			
			// Calcula o novo prêmio acumulado
			prizes.newprize = _prize - (prizes.prize1 + prizes.prize2 + prizes.prize3 + prizes.prize4);
			return prizes;
		}
		
		private void updateLotteryResults(NumberCombo combo, PrizeDistribution prizes) throws SQLException
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_LOTTERY))
			{
				ps.setLong(1, _prize);
				ps.setLong(2, prizes.newprize);
				ps.setInt(3, combo.enchant);
				ps.setInt(4, combo.type2);
				ps.setLong(5, prizes.prize1);
				ps.setLong(6, prizes.prize2);
				ps.setLong(7, prizes.prize3);
				ps.setInt(8, _number);
				ps.execute();
			}
		}
		
		private void broadcastWinners(WinnerCount winners)
		{
			SystemMessage sm;
			if (winners.count1 > 0)
			{
				sm = new SystemMessage(SystemMessageId.THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY_S1_IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS);
				sm.addInt(_number);
				sm.addLong(_prize);
				sm.addLong(winners.count1);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY_S1_IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING);
				sm.addInt(_number);
				sm.addLong(_prize);
			}
			Broadcast.toAllOnlinePlayers(sm);
		}
		
		private void prepareNextLottery(long newprize)
		{
			_number++;
			_prize = newprize;
			_isStarted = false;
			ThreadPool.schedule(new startLottery(), MINUTE);
		}
		
		// Classes auxiliares para organização dos dados
		private static class NumberCombo
		{
			final int enchant;
			final int type2;
			
			NumberCombo(int enchant, int type2)
			{
				this.enchant = enchant;
				this.type2 = type2;
			}
		}
		
		private static class WinnerCount
		{
			int count1; // 5 acertos
			int count2; // 4 acertos
			int count3; // 3 acertos
			int count4; // 1-2 acertos
		}
		
		private static class PrizeDistribution
		{
			long prize1;
			long prize2;
			long prize3;
			long prize4;
			long newprize;
		}
	}
	
	/**
	 * Decodes the lottery numbers from their bitmask representation
	 * @param enchantValue Bitmask for numbers 1-16 (bits 0-15)
	 * @param type2Value Bitmask for numbers 17-20 (bits 0-3)
	 * @return Array with the decoded numbers (1-20)
	 */
	public int[] decodeNumbers(int enchantValue, int type2Value)
	{
		final int[] result = new int[5];
		int index = 0;
		
		// Decode numbers 1-16 from enchantValue
		for (int i = 0; (i < 16) && (index < 5); i++)
		{
			if ((enchantValue & (1 << i)) != 0)
			{
				result[index++] = i + 1;
			}
		}
		
		// Decode numbers 17-20 from type2Value
		for (int i = 0; (i < 4) && (index < 5); i++)
		{
			if ((type2Value & (1 << i)) != 0)
			{
				result[index++] = i + 17;
			}
		}
		
		return result;
	}
	
	public long[] checkTicket(Item item)
	{
		return checkTicket(item.getCustomType1(), item.getEnchantLevel(), item.getCustomType2());
	}
	
	public long[] checkTicket(int id, int enchant, int type2)
	{
		final long[] result =
		{
			0,
			0
		};
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_LOTTERY_TICKET))
		{
			ps.setInt(1, id);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					int winningEnchant = rs.getInt("number1");
					int winningType2 = rs.getInt("number2");
					
					// Conta números acertados (1-16)
					int matches = countSetBits(winningEnchant & enchant);
					// Conta números acertados (17-20)
					matches += countSetBits(winningType2 & type2);
					
					if (matches == 0)
					{
						return result; // Sem acertos
					}
					
					// Determina o prêmio baseado no número de acertos
					switch (matches)
					{
						case 5:
							result[0] = 1; // 1° prêmio (5 acertos)
							result[1] = rs.getLong("prize1");
							break;
						case 4:
							result[0] = 2; // 2° prêmio (4 acertos)
							result[1] = rs.getLong("prize2");
							break;
						case 3:
							result[0] = 3; // 3° prêmio (3 acertos)
							result[1] = rs.getLong("prize3");
							break;
						default: // 1-2 acertos
							result[0] = 4;
							result[1] = Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Lottery: Could not check lottery ticket #" + id + ": " + e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Counts the number of set bits in an integer (Hamming weight)
	 * @param value The integer to count bits
	 * @return Number of set bits
	 */
	private int countSetBits(int value)
	{
		int count = 0;
		while (value != 0)
		{
			count += value & 1; // Add 1 if LSB is set
			value >>>= 1; // Unsigned right shift
		}
		return count;
	}
	
	public static LotteryManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final LotteryManager INSTANCE = new LotteryManager();
	}
}

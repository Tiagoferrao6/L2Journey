package com.l2journey.gameserver.data.xml.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;

/**
 * Grade-based Equipment Manager for FakePlayers and Mercenary Companions.
 * Automatically equips Top Weapon, Armor Set, and Jewels based on character level/class.
 */
public class FakePlayerEquipmentData
{
	private static final Logger LOGGER = Logger.getLogger(FakePlayerEquipmentData.class.getName());

	public enum Grade
	{
		NO_GRADE,
		D_GRADE,
		C_GRADE,
		B_GRADE,
		A_GRADE,
		S_GRADE
	}

	public static Grade getGradeForLevel(int level)
	{
		if (level >= 76)
		{
			return Grade.S_GRADE;
		}
		else if (level >= 61)
		{
			return Grade.A_GRADE;
		}
		else if (level >= 52)
		{
			return Grade.B_GRADE;
		}
		else if (level >= 40)
		{
			return Grade.C_GRADE;
		}
		else if (level >= 20)
		{
			return Grade.D_GRADE;
		}
		return Grade.NO_GRADE;
	}

	public static void autoEquip(Player player)
	{
		autoEquip(player, getGradeForLevel(player.getLevel()));
	}

	public static void autoEquip(Player player, Grade grade)
	{
		if (player == null)
		{
			return;
		}

		try
		{
			// Destroy old inventory items
			player.getInventory().destroyAllItems(ItemProcessType.DESTROY, player, null);

			int classId = player.getTemplate().getPlayerClass().getId();
			boolean isMageOrHealer = player.isMageClass() || classId == 97 || classId == 16;
			boolean isArcher = classId == 92 || classId == 102 || classId == 109 || classId == 24 || classId == 37 || classId == 130;

			int weaponId = 1; // Short Sword
			int chestId = 23; // Wooden Breastplate
			int legsId = 2386; // Wooden Gaiters
			int helmId = 43; // Wooden Helmet
			int glovesId = 49; // Short Gloves
			int bootsId = 37; // Sandals
			int neckId = 906; // Necklace of Anguish
			int earId = 112; // Coral Earring
			int ringId = 875; // Ring of Knowledge

			switch (grade)
			{
				case S_GRADE:
					if (isMageOrHealer)
					{
						weaponId = 6608; // Arcana Mace
						chestId = 6383; // Major Arcana Robe
						legsId = 0;
						helmId = 6384; // Major Arcana Circlet
						glovesId = 6385; // Major Arcana Gloves
						bootsId = 6386; // Major Arcana Boots
					}
					else if (isArcher)
					{
						weaponId = 7579; // Draconic Bow
						chestId = 6379; // Draconic Leather Armor
						legsId = 0;
						helmId = 6380; // Draconic Leather Helmet
						glovesId = 6381; // Draconic Leather Gloves
						bootsId = 6382; // Draconic Leather Boots
					}
					else
					{
						weaponId = 6369; // Heaven's Divider
						chestId = 6373; // Imperial Crusader Breastplate
						legsId = 6374; // Imperial Crusader Gaiters
						helmId = 6375; // Imperial Crusader Helm
						glovesId = 6376; // Imperial Crusader Gauntlets
						bootsId = 6377; // Imperial Crusader Boots
					}
					neckId = 6657; // Tateossian Necklace
					earId = 6656; // Tateossian Earring
					ringId = 6658; // Tateossian Ring
					break;

				case A_GRADE:
					if (isMageOrHealer)
					{
						weaponId = 212; // Dasparion's Staff
						chestId = 2408; // Dark Crystal Robe
						legsId = 0;
						helmId = 549; // Dark Crystal Helm
						glovesId = 574; // Dark Crystal Gloves
						bootsId = 603; // Dark Crystal Boots
					}
					else if (isArcher)
					{
						weaponId = 289; // Soul Bow
						chestId = 2390; // Majestic Leather Mail
						legsId = 0;
						helmId = 2419; // Majestic Circlet
						glovesId = 577; // Majestic Gloves
						bootsId = 606; // Majestic Boots
					}
					else
					{
						weaponId = 80; // Tallum Blade
						chestId = 367; // Tallum Plate Armor
						legsId = 0;
						helmId = 548; // Tallum Helm
						glovesId = 573; // Tallum Gloves
						bootsId = 602; // Tallum Boots
					}
					neckId = 926; // Majestic Necklace
					earId = 864; // Majestic Earring
					ringId = 895; // Majestic Ring
					break;

				case B_GRADE:
					if (isMageOrHealer)
					{
						weaponId = 146; // Valhalla
						chestId = 2407; // Avadon Robe
						legsId = 0;
						helmId = 2417; // Avadon Circlet
						glovesId = 571; // Avadon Gloves
						bootsId = 600; // Avadon Boots
					}
					else if (isArcher)
					{
						weaponId = 288; // Bow of Peril
						chestId = 2391; // Blue Wolf Leather Mail
						legsId = 0;
						helmId = 546; // Blue Wolf Helm
						glovesId = 572; // Blue Wolf Gloves
						bootsId = 601; // Blue Wolf Boots
					}
					else
					{
						weaponId = 71; // Great Sword
						chestId = 365; // Zubei's Breastplate
						legsId = 2393; // Zubei's Gaiters
						helmId = 547; // Zubei's Helmet
						glovesId = 570; // Zubei's Gauntlets
						bootsId = 599; // Zubei's Boots
					}
					neckId = 920; // Black Ore Necklace
					earId = 858; // Black Ore Earring
					ringId = 889; // Black Ore Ring
					break;

				case C_GRADE:
					if (isMageOrHealer)
					{
						weaponId = 84; // Homunkulus's Sword
						chestId = 439; // Carmian Tunic
						legsId = 2406; // Carmian Hose
						helmId = 43; // Wooden Helmet
						glovesId = 561; // Carmian Gloves
						bootsId = 562; // Carmian Boots
					}
					else if (isArcher)
					{
						weaponId = 287; // Eminence Bow
						chestId = 396; // Plated Leather Shirt
						legsId = 2401; // Plated Leather Gaiters
						helmId = 46; // Brigandine Helmet
						glovesId = 49;
						bootsId = 37;
					}
					else
					{
						weaponId = 4814; // Berserker Blade
						chestId = 358; // Full Plate Armor
						legsId = 0;
						helmId = 2414; // Full Plate Helmet
						glovesId = 570;
						bootsId = 599;
					}
					neckId = 915; // Necklace of Seal
					earId = 853; // Earring of Binding
					ringId = 884; // Ring of Aging
					break;

				case D_GRADE:
					if (isMageOrHealer)
					{
						weaponId = 188; // Staff of Life
						chestId = 418; // Elven Mithril Tunic
						legsId = 2404; // Elven Mithril Hose
						helmId = 43;
						glovesId = 49;
						bootsId = 37;
					}
					else if (isArcher)
					{
						weaponId = 276; // Elven Bow
						chestId = 392; // Manticore Skin Shirt
						legsId = 2400; // Manticore Skin Gaiters
						helmId = 46;
						glovesId = 49;
						bootsId = 37;
					}
					else
					{
						weaponId = 75; // Tarbar
						chestId = 356; // Brigandine Shirt
						legsId = 2392; // Brigandine Gaiters
						helmId = 46; // Brigandine Helmet
						glovesId = 49;
						bootsId = 37;
					}
					neckId = 909; // Elven Necklace
					earId = 847; // Elven Earring
					ringId = 878; // Elven Ring
					break;

				case NO_GRADE:
				default:
					if (isMageOrHealer)
					{
						weaponId = 3; // Apprentice's Staff
						chestId = 423; // Devotion Tunic
						legsId = 2405; // Devotion Hose
					}
					else if (isArcher)
					{
						weaponId = 13; // Wooden Bow
					}
					break;
			}

			// Add & Equip items
			equipItemIfValid(player, weaponId);
			equipItemIfValid(player, chestId);
			if (legsId > 0)
			{
				equipItemIfValid(player, legsId);
			}
			equipItemIfValid(player, helmId);
			equipItemIfValid(player, glovesId);
			equipItemIfValid(player, bootsId);

			equipItemIfValid(player, neckId);
			equipItemIfValid(player, earId);
			equipItemIfValid(player, earId);
			equipItemIfValid(player, ringId);
			equipItemIfValid(player, ringId);

			// Refresh visual appearance and stats
			player.broadcastUserInfo();
			player.broadcastCharInfo();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerEquipmentData: Error equipping player " + player.getName(), e);
		}
	}

	private static void equipItemIfValid(Player player, int itemId)
	{
		if (itemId <= 0)
		{
			return;
		}
		Item item = player.getInventory().addItem(ItemProcessType.REWARD, itemId, 1, player, null);
		if (item != null)
		{
			player.getInventory().equipItem(item);
		}
	}
}

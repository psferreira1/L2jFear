package com.l2jfrozen.gameserver.model.base;

import com.l2jfrozen.Config;

/**
 * $ Rewrite 06.12.06 - Yesod
 */
public class SoulCrystal
{
	public static final int[][] HighSoulConvert =
	{
		{
			4639,
			5577
		}, // RED 10 - 11
		{
			5577,
			5580
		}, // RED 11 - 12
		{
			5580,
			5908
		}, // RED 12 - 13
		
		{
			4650,
			5578
		}, // GRN 10 - 11
		{
			5578,
			5581
		}, // GRN 11 - 12
		{
			5581,
			5911
		}, // GRN 12 - 13
		
		{
			4661,
			5579
		}, // BLU 10 - 11
		{
			5579,
			5582
		}, // BLU 11 - 12
		{
			5582,
			5914
		}
		// BLU 12 - 13
	};
	
	/**
	 * "First line is for Red Soul Crystals, second is Green and third is Blue Soul Crystals, ordered by ascending level, from 0 to 13..."
	 */
	public static final int[] SoulCrystalTable =
	{
		4629,
		4630,
		4631,
		4632,
		4633,
		4634,
		4635,
		4636,
		4637,
		4638,
		4639,
		5577,
		5580,
		5908,
		4640,
		4641,
		4642,
		4643,
		4644,
		4645,
		4646,
		4647,
		4648,
		4649,
		4650,
		5578,
		5581,
		5911,
		4651,
		4652,
		4653,
		4654,
		4655,
		4656,
		4657,
		4658,
		4659,
		4660,
		4661,
		5579,
		5582,
		5914
	};
	
	public static final int MAX_CRYSTALS_LEVEL = Config.SOUL_CRYSTAL_MAX_LEVEL;
	public static final int BREAK_CHANCE = Config.SOUL_CRYSTAL_BREAK_CHANCE;
	public static final int LEVEL_CHANCE = Config.SOUL_CRYSTAL_LEVEL_CHANCE;
	
	public static final int RED_BROKEN_CRYSTAL = 4662;
	public static final int GRN_BROKEN_CYRSTAL = 4663;
	public static final int BLU_BROKEN_CRYSTAL = 4664;
	
	public static final int RED_NEW_CRYSTAL = 4629;
	public static final int GRN_NEW_CYRSTAL = 4640;
	public static final int BLU_NEW_CRYSTAL = 4651;
	
}

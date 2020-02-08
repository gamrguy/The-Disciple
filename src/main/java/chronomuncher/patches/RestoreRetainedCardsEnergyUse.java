package chronomuncher.patches;

import java.util.function.Consumer;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;

import com.megacrit.cardcrawl.actions.unique.RestoreRetainedCardsAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.actions.common.MonsterStartTurnAction;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.blue.Equilibrium;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.RetainCardPower;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;

import basemod.ReflectionHacks;

import chronomuncher.ChronoMod;
import chronomuncher.actions.ChronoStreakAction;
import chronomuncher.cards.PrimeTime;
import chronomuncher.patches.RetainedForField;
import chronomuncher.relics.Chronograph;
import chronomuncher.relics.Chronometer;

public class RestoreRetainedCardsEnergyUse {

	// Patch for function called to reset card attributes at the end of the turn
	// Occurs after "end of turn" triggers and hand discarding
	@SpirePatch(clz = AbstractRoom.class, method="endTurn")
	public static class endTurn {
	
		// Reapply the cost changes from existing retain streaks, for visibility
		// This occurs right after cards have their values rest, after clicking End Turn
		@SpireInsertPatch(rloc=24)
		public static void InsertReapplyCostChange() {
			AbstractDungeon.player.hand.group.forEach((c) -> {
				c.setCostForTurn(c.costForTurn - RetainedForField.retainedFor.get(c));
			});
		}
		
		// Update the retain streaks and revert any ended streaks
		// This action occurs after turn end triggers but before the player's hand is discarded
		@SpireInsertPatch(rloc=2)
		public static void InsertUpdateCostChange() {
			AbstractDungeon.actionManager.addToBottom(new ChronoStreakAction());
		}
	}
}
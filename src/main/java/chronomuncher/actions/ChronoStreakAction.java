package chronomuncher.actions;

import java.util.function.Consumer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Equilibrium;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.RunicPyramid;

import chronomuncher.patches.RetainedForField;
import chronomuncher.relics.Chronograph;
import chronomuncher.relics.Chronometer;

public class ChronoStreakAction extends AbstractGameAction {
	
	public void update() {
		boolean hasChronograph = AbstractDungeon.player.hasRelic(Chronograph.ID);
		boolean hasChronometer = hasChronograph || AbstractDungeon.player.hasRelic(Chronometer.ID);
		if(hasChronometer) {
			Consumer<AbstractCard> endStreak = (c) -> {
				RetainedForField.retainedFor.set(c, 0);
			};
			Consumer<AbstractCard> checkCard = (c) -> {
				if(c.retain 
						|| AbstractDungeon.player.hasPower(Equilibrium.ID)
						|| AbstractDungeon.player.hasRelic(RunicPyramid.ID)) {
					// Bonuses from successfully retaining this card
					lowerCostFromRetain(c);
					if(hasChronograph) upgradeFromChronograph(c);
				} else {
					// Reset streak if this card wasn't retained
					c.setCostForTurn(c.costForTurn + RetainedForField.retainedFor.get(c));
					if(c.cost == c.costForTurn) c.isCostModifiedForTurn = false;
					endStreak.accept(c);
				}
			};
			
			AbstractDungeon.player.hand.group.forEach(checkCard);
			AbstractDungeon.player.drawPile.group.forEach(endStreak);
			AbstractDungeon.player.discardPile.group.forEach(endStreak);
			AbstractDungeon.player.exhaustPile.group.forEach(endStreak);
		}
		this.isDone = true;
	}
	
	public static void lowerCostFromRetain(AbstractCard c) {
		// Don't increase the streak count if the cost is 0
		if(c.costForTurn > 0) {
			int kept = RetainedForField.retainedFor.get(c);
			RetainedForField.retainedFor.set(c, kept + 1);
			c.setCostForTurn(c.costForTurn - 1);
		}
	}

	public static void upgradeFromChronograph(AbstractCard c) {
		if (c.canUpgrade()) {
			c.superFlash();
			c.upgrade();
			c.applyPowers();
		}
	}
}

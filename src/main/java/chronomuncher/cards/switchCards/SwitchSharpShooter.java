package chronomuncher.cards.switchCards;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.FocusPower;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;

import chronomuncher.cards.MetricsCard;
import chronomuncher.ChronoMod;
import chronomuncher.orbs.ReplicaOrb;
import chronomuncher.patches.Enum;
import chronomuncher.actions.SwitchAction;
import chronomuncher.actions.ModifyTimerAction;

import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class SwitchSharpShooter extends AbstractSelfSwitchCard {

	// switchList.add(new switchCard(String CardID, String switchID, Integer cost, Integer damage, Integer damageUp, Integer block, Integer blockUp, Integer magicNum, Integer magicNumUp, 
	// 					          CardType type, CardTarget target, boolean isMultiTarget, boolean isInnate, boolean exhaust, boolean isEthereal));

	public List<switchCard> switchListInherit = Arrays.asList(
		new AbstractSelfSwitchCard.switchCard("FastForwardS", "ClockandLoad", 2, 0, 0, 0, 0, 1, 1, 
						          		AbstractCard.CardType.SKILL, AbstractCard.CardTarget.ENEMY, false, false, false, false),

		new AbstractSelfSwitchCard.switchCard("ClockandLoad", "FastForwardS", 1, 0, 0, 0, 0, 1, 1, 
						          		AbstractCard.CardType.SKILL, AbstractCard.CardTarget.SELF, false, false, false, false) );


	public SwitchSharpShooter (String switchID) {
		super("SharpShooter", "None", null, 0, "None", AbstractCard.CardType.SKILL,
				Enum.CHRONO_GOLD, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.NONE, SwitchSharpShooter.class);

		if (switchID == null) {
			switchID = switchListInherit.get(new Random().nextInt(switchListInherit.size())).cardID;
		}
		
		this.switchList = switchListInherit;
		if (this.currentID != null) {
			this.switchTo(this.currentID);
		} else {
			this.switchTo(switchID);
		}
    	this.tags.add(Enum.SWITCH_CARD);
	}

	public SwitchSharpShooter () { this(null); }

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (this.purgeOnUse) { this.switchTo(switchID); }

		AbstractDungeon.actionManager.addToBottom(new SwitchAction(this));
		ReplicaOrb r;

		switch (this.currentID) {
			case "FastForwardS":
		        for (AbstractOrb o : p.orbs) {
		            if (o instanceof ReplicaOrb) {
		            	r = (ReplicaOrb)o;
		            	for (int i = 0; i < this.magicNumber; i++) {
		            		if (r.timer > 0) {
								AbstractDungeon.actionManager.addToBottom(new ModifyTimerAction(r, -1));
								AbstractDungeon.actionManager.addToBottom(new PlayTopCardAction(m, false));
							}		            		
		            	}
		        	}
		        }
				break;
			case "ClockandLoad":
		        for (AbstractOrb o : p.orbs) {
		            if (o instanceof ReplicaOrb) {
		            	r = (ReplicaOrb)o;
						AbstractDungeon.actionManager.addToBottom(new ModifyTimerAction(r, this.magicNumber));
		        	}
		        }
			break;
		}
	}
}
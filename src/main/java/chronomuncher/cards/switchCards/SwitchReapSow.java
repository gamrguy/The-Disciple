package chronomuncher.cards.switchCards;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;

import chronomuncher.cards.MetricsCard;
import chronomuncher.ChronoMod;
import chronomuncher.patches.Enum;
import chronomuncher.actions.SwitchAction;

import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class SwitchReapSow extends AbstractSelfSwitchCard {

	// switchList.add(new switchCard(String CardID, String switchID, Integer cost, Integer damage, Integer damageUp, Integer block, Integer blockUp, Integer magicNum, Integer magicNumUp, 
	// 					          CardType type, CardTarget target, boolean isMultiTarget, boolean isInnate, boolean exhaust, boolean isEthereal));

	public List<switchCard> switchListInherit = Arrays.asList(
		new AbstractSelfSwitchCard.switchCard("Reap", "Sow", 1, 9, 3, 0, 0, 0, 0, 
						          		AbstractCard.CardType.ATTACK, AbstractCard.CardTarget.ALL_ENEMY, true, false, false, false),

		new AbstractSelfSwitchCard.switchCard("Sow", "Reap", 1, 12, 5, 0, 0, 0, 0, 
						          		AbstractCard.CardType.ATTACK, AbstractCard.CardTarget.ENEMY, false, false, false, false) );


	public SwitchReapSow(String switchID) {
		super("ReapSow", "None", null, 0, "None", AbstractCard.CardType.ATTACK,
				Enum.CHRONO_GOLD, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE, SwitchReapSow.class);

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

	public SwitchReapSow() { this(null); }

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		switch (this.currentID) {
			case "Reap":
			    AbstractDungeon.actionManager.addToBottom(
			    	new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE)); 
			    AbstractDungeon.actionManager.addToBottom(new SFXAction("ATTACK_HEAVY"));
			    AbstractDungeon.actionManager.addToBottom(new VFXAction(p, new CleaveEffect(), 0.1F));
    			break;
			case "Sow":
				AbstractDungeon.actionManager.addToBottom(
					new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
				break;
		}

		AbstractDungeon.actionManager.addToBottom(new SwitchAction(this));
	}
}

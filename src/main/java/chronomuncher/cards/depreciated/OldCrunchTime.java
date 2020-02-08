package chronomuncher.cards.depreciated;	

import com.megacrit.cardcrawl.core.CardCrawlGame;	
import com.megacrit.cardcrawl.actions.common.GainBlockAction;	
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;	
import com.megacrit.cardcrawl.cards.AbstractCard;	
import com.megacrit.cardcrawl.characters.AbstractPlayer;	
import com.megacrit.cardcrawl.core.CardCrawlGame;	
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;	
import com.megacrit.cardcrawl.localization.CardStrings;	
import com.megacrit.cardcrawl.monsters.AbstractMonster;	
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;	
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;	
import com.megacrit.cardcrawl.actions.utility.WaitAction;	
import com.megacrit.cardcrawl.powers.RetainCardPower;	
import com.megacrit.cardcrawl.orbs.AbstractOrb;	

import chronomuncher.cards.MetricsCard;	
import chronomuncher.ChronoMod;	
import chronomuncher.patches.Enum;	
import chronomuncher.powers.RetainOncePower;	
import chronomuncher.orbs.ReplicaOrb;	
import chronomuncher.actions.ShatterAction;	

public class OldCrunchTime extends MetricsCard {	
	public static final String ID = "OldCrunchTime";	
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);	
	public static final String NAME = cardStrings.NAME;	
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;	

	private static final int COST = 1;	
	private static final int ENERGY = 2;	
	private static final int ENERGY_UPGRADE = 1;	

	public OldCrunchTime() {	
		super(ID, NAME, "chrono_images/cards/OldCrunchTime.png", COST, DESCRIPTION, AbstractCard.CardType.SKILL,	
				Enum.CHRONO_GOLD, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);	

		this.baseMagicNumber = ENERGY;	
		this.magicNumber = this.baseMagicNumber;	
	}	

	@Override	
	public void use(AbstractPlayer p, AbstractMonster m) {	
        for (AbstractOrb o : p.orbs) {	
          if (o instanceof ReplicaOrb) {	
	          AbstractDungeon.actionManager.addToBottom(new ShatterAction((ReplicaOrb)o));	
			  AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(this.magicNumber));	
	          AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2F));	
	      }	
        }	
   	}	

	@Override	
	public AbstractCard makeCopy() {	
		return new OldCrunchTime();	
	}	

	@Override	
	public void upgrade() {	
		if (!this.upgraded) {	
			upgradeName();	
			upgradeMagicNumber(ENERGY_UPGRADE);	
		}	
	}	
}
package chronomuncher.cards.depreciated;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;

import chronomuncher.cards.MetricsCard;
import chronomuncher.ChronoMod;
import chronomuncher.patches.Enum;
import chronomuncher.actions.PlayLowerBlockFromDeckAction;


public class OldResonantCall extends MetricsCard {
	public static final String ID = "OldResonantCall";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;

	private static final int COST = 1;
	private static final int CARDS_TO_PLAY = 3;
	private static final int CARDS_TO_PLAY_UP = 2;

	public OldResonantCall() {
		super(ID, NAME, "chrono_images/cards/OldResonantCall.png", COST, DESCRIPTION, AbstractCard.CardType.SKILL,
				Enum.CHRONO_GOLD, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);

		this.baseMagicNumber = CARDS_TO_PLAY;
		this.magicNumber = CARDS_TO_PLAY;

		this.exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToTop(new PlayLowerBlockFromDeckAction(m, this.magicNumber, p.currentBlock));
	}


	@Override
	public AbstractCard makeCopy() {
		return new OldResonantCall();
	}

	@Override
	public void upgrade() {
		if (!this.upgraded) {
			upgradeName();
			upgradeMagicNumber(CARDS_TO_PLAY_UP);
		}
	}
}
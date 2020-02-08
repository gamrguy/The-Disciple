package chronomuncher.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.relics.BottledFlame;
import com.megacrit.cardcrawl.relics.BottledLightning;
import com.megacrit.cardcrawl.relics.BottledTornado;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Iterator;

import chronomuncher.ChronoMod;
import chronomuncher.cards.tempoCards.AbstractSwitchCard;

public class TransformCardPermanently extends AbstractGameAction {

	private AbstractPlayer p;
	private boolean upgraded;
	private AbstractCard transformToCard;
  	private AbstractCard transformee;
  	private boolean toHand;

	public TransformCardPermanently(AbstractPlayer p, AbstractCard transformee, AbstractCard transformToCard, boolean toHand) {
		this.p = p;
		this.transformToCard = transformToCard;
		this.transformee = transformee;
		this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
		this.toHand  = toHand;
	}

	public TransformCardPermanently(AbstractPlayer p, AbstractCard transformee, AbstractCard transformToCard) {
		this(p, transformee, transformToCard, false); }

	public void update() {

		// Transformed cards transform into upgrade versions of themselves.
	    UnlockTracker.markCardAsSeen(this.transformToCard.cardID);
	    if ((this.transformee.upgraded) && (this.transformToCard.canUpgrade())) {
	     	this.transformToCard.upgrade();
	    }

		// Make the new card temporarily in the battle, and discard it
		if (this.toHand) {
 			AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(this.transformToCard)); }
		else {
  			AbstractDungeon.actionManager.addToTop(new MakeTempCardInDiscardAction(this.transformToCard, 1));
  		}

		this.transformee.exhaust = true;
		if (this.transformee.purgeOnUse) {
			this.transformToCard.purgeOnUse = true; }
		this.transformee.purgeOnUse = true;

  		// And add it to the deck
		AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.transformToCard, Settings.WIDTH / 2.0F + (AbstractCard.IMG_WIDTH + 16.0F) * Settings.scale, Settings.HEIGHT / 2.0F));

		// Update Bottles
		 if (this.transformee.inBottleFlame) { 
		 	BottledFlame bf = (BottledFlame)AbstractDungeon.player.getRelic("Bottled Flame");
		 	bf.card = this.transformToCard;
		 	bf.setDescriptionAfterLoading();
		 }
		 if (this.transformee.inBottleLightning) { 
		 	BottledLightning bl = (BottledLightning)AbstractDungeon.player.getRelic("Bottled Lightning");
		 	bl.card = this.transformToCard;
		 	bl.setDescriptionAfterLoading();
		 }
		 if (this.transformee.inBottleTornado) { 
		 	BottledTornado bt = (BottledTornado)AbstractDungeon.player.getRelic("Bottled Tornado");
		 	bt.card = this.transformToCard;
		 	bt.setDescriptionAfterLoading();
		 }

  		// Remove the original card from the deck
		this.removeFromCardGroup(AbstractDungeon.player.masterDeck, (AbstractSwitchCard)this.transformee, this.transformee.upgraded);

		this.isDone = true;
	}

	public boolean removeFromCardGroup(CardGroup group, AbstractSwitchCard card, boolean upgrade) {

	    for (Iterator<AbstractCard> i = group.group.iterator(); i.hasNext();)
	    {
	      AbstractCard e = (AbstractCard)i.next();
		  if (e instanceof AbstractSwitchCard) {
	      	AbstractSwitchCard f = (AbstractSwitchCard)e;
		    if (f.switchCardUniqueID == card.switchCardUniqueID)
		    {
		      i.remove();
		      return true;
		    }
		  }
	    }
	    return false;
   	}

	// public boolean findInCardGroup(CardGroup group, String cardID, boolean upgrade) {

	//     for (Iterator<AbstractCard> i = group.group.iterator(); i.hasNext();)
	//     {
	//       AbstractCard e = (AbstractCard)i.next();
	//       if (e.cardID.equals(cardID) && e.upgraded == upgrade)
	//       {
	//         return true;
	//       }
	//     }
	//     return false;
 //   	}
}
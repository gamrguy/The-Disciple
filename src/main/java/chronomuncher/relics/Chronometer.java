package chronomuncher.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.AbstractPower.PowerType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;

import com.badlogic.gdx.graphics.Texture;
import basemod.abstracts.CustomRelic;
import chronomuncher.ChronoMod;
import chronomuncher.patches.RestoreRetainedCardsEnergyUse;
import chronomuncher.patches.RetainedForField;

public class Chronometer extends CustomRelic {
    public static final String ID = "Chronometer";

    private AbstractPlayer p;

    public Chronometer() {
        super(ID, new Texture("chrono_images/relics/Chronometer.png"), new Texture("chrono_images/relics/outline/Chronometer.png"), RelicTier.STARTER, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void onUnequip() {
        RelicLibrary.bossList.remove("Chronograph");
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Chronometer();
    }
}
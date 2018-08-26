package chronomuncher.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.actions.common.EscapeAction;

import com.megacrit.cardcrawl.monsters.exordium.Hexaghost;

import basemod.ReflectionHacks;
import chronomuncher.ChronoMod;

public class PatternShiftAction extends AbstractGameAction {

	private AbstractPlayer p;
	private AbstractMonster m;
	private boolean upgraded;
	private AbstractCard transformToCard;
  	private AbstractCard transformee;

	public PatternShiftAction(AbstractPlayer p, AbstractMonster m) {
		this.p = p;
		this.m = m;
	}

	public void update() {
		AbstractMonster m = this.m;
		int count;
		int turnCount;
		int slashCount;
		boolean isAttacking;
		boolean firstTurn;
		boolean firstMove;

		// Special Cases
		switch(this.m.id) {

			// firstTurn only
			case "AwakenedOne":
			case "Chosen":
			case "Snecko":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstTurn", false);
				break;

			case "Dagger":
				if (m.nextMove == 2) {
      				AbstractDungeon.actionManager.addToBottom(new LoseHPAction(m, m, m.currentHealth));
      			}

			// firstMove only
			case "SnakeMage":
			case "Byrd":
			case "Shelled Parasite":
			case "Cultist":
			case "JawWorm":
			case "Sentry":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstMove", false);
				break;

			// Donu and deca need to swap
			case "Deca":
				isAttacking = (boolean)ReflectionHacks.getPrivate(m, m.getClass(), "isAttacking");
				ReflectionHacks.setPrivate(m, m.getClass(), "isAttacking", !isAttacking);
				break;
			case "Donu":
				isAttacking = (boolean)ReflectionHacks.getPrivate(m, m.getClass(), "isAttacking");
				ReflectionHacks.setPrivate(m, m.getClass(), "isAttacking", !isAttacking);
				break;

			// Exploder advances his explosion timer
			case "Exploder":
				turnCount = (int)ReflectionHacks.getPrivate(m, m.getClass(), "turnCount");
				ReflectionHacks.setPrivate(m, m.getClass(), "turnCount", turnCount + 1);
				if (turnCount+1 < 3) {
					AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(m, m, "Explosive", 1)); 
				} else {
					AbstractDungeon.actionManager.addToBottom(new VFXAction(new ExplosionSmallEffect(m.hb.cX, m.hb.cY), 0.1F));
					
					AbstractDungeon.actionManager.addToBottom(new SuicideAction(m));
					AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(AbstractDungeon.player, new int[]{30,30,30,30,30}, DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE, true));
     			}
				break;

			// Giant Head's count lowers
			case "GiantHead":
				count = (int)ReflectionHacks.getPrivate(m, m.getClass(), "count");
				ReflectionHacks.setPrivate(m, m.getClass(), "count", count - 1);
				break;

			// You can skip Time Eater hasting itself =D
			case "TimeEater":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstTurn", false);
				if (m.currentHealth < m.maxHealth / 2) { ReflectionHacks.setPrivate(m, m.getClass(), "usedHaste", true); }
				break;

			// Transient's count increases
			case "Transient":
				count = (int)ReflectionHacks.getPrivate(m, m.getClass(), "count");
				ReflectionHacks.setPrivate(m, m.getClass(), "count", count + 1);
				break;

			// There's no avoiding Hyper Beam charging... but you can avoid Hyper Beam....?!
			case "BronzeAutomaton":
				firstTurn = (boolean)ReflectionHacks.getPrivate(m, m.getClass(), "firstTurn");
				if ((m.intent == AbstractMonster.Intent.UNKNOWN) && !firstTurn) {
					this.isDone = true;
					return;
				}
				ReflectionHacks.setPrivate(m, m.getClass(), "firstTurn", false);
				break;

			// Looter is annoying, just like Mugger
			case "Looter":
			case "Mugger":
				slashCount = (int)ReflectionHacks.getPrivate(m, m.getClass(), "slashCount");

				switch (slashCount) {
					case 0:
						ReflectionHacks.setPrivate(m, m.getClass(), "slashCount", 1);
						m.rollMove();
						break;
					case 1:
						ReflectionHacks.setPrivate(m, m.getClass(), "slashCount", 2);
						m.rollMove();
						break;
					case 2:
						ReflectionHacks.setPrivate(m, m.getClass(), "slashCount", 3);
						m.setMove((byte)2, AbstractMonster.Intent.DEFEND);
						break;
					default:
						if (m.nextMove == 2) {
							ReflectionHacks.setPrivate(m, m.getClass(), "slashCount", 4);
							m.setMove((byte)3, AbstractMonster.Intent.ESCAPE);
						} else if (m.nextMove == 3) {
							AbstractDungeon.actionManager.addToBottom(new EscapeAction(m));
						}
						break;
				}

				m.createIntent();
				this.isDone = true;
				return;

			// Spheric Guardian has a secondTurn as well
			case "SphericGuardian":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstMove", false);
				firstMove = (boolean)ReflectionHacks.getPrivate(m, m.getClass(), "firstMove");
				if (!firstMove) { ReflectionHacks.setPrivate(m, m.getClass(), "secondMove", false); }
				break;

			// The Collector has all sorts of stuff, but you can skip his ult
			case "TheCollector":
				int turnsTaken = (int)ReflectionHacks.getPrivate(m, m.getClass(), "turnsTaken") + 1;
				ReflectionHacks.setPrivate(m, m.getClass(), "turnsTaken", turnsTaken);
				ReflectionHacks.setPrivate(m, m.getClass(), "initialSpawn", false);
				if (m.intent == AbstractMonster.Intent.STRONG_DEBUFF) {
					ReflectionHacks.setPrivate(m, m.getClass(), "ultUsed", true);
				}
				break;

			// Slimes get split triggered reset, so they will do another intent until you hit them again
			case "AcidSlime_L":
				ReflectionHacks.setPrivate(m, m.getClass(), "splitTriggered", false);
				break;

			// Gremlin Wizard charges more or goes back to no charge
			case "GremlinWizard":
				int currentCharge = (int)ReflectionHacks.getPrivate(m, m.getClass(), "currentCharge");
				ReflectionHacks.setPrivate(m, m.getClass(), "currentCharge", currentCharge + 1);

				if (currentCharge+1 == 3) {
					m.setMove((byte)1, AbstractMonster.Intent.ATTACK);
				}

				if ((m.intent == AbstractMonster.Intent.ATTACK)) {
					ReflectionHacks.setPrivate(m, m.getClass(), "currentCharge", 0);
					m.rollMove();
				}
				break;

			// Hexaghost, ouch
			case "Hexaghost":
      			ChronoMod.log("Move byte: " + Integer.toString(m.nextMove));
      			Hexaghost hexa;

				// If you skip the first turn, you need to activate him anyway
				if (m.intent == AbstractMonster.Intent.UNKNOWN) {
					AbstractDungeon.actionManager.addToTop(new ChangeStateAction(m, "Activate"));
      				int d = AbstractDungeon.player.currentHealth / 12 + 1;
      
      				((DamageInfo)m.damage.get(2)).base = d;
      
      				m.applyPowers();
					m.setMove((byte)1, AbstractMonster.Intent.ATTACK, d, 6, true);
					m.createIntent();
					this.isDone = true;
					return;
      			} else {
      				switch (m.nextMove) {
      					case 1:
      						hexa = (Hexaghost)m;
      						hexa.changeState("Deactivate");

  		      				// AbstractDungeon.actionManager.addToBottom(new RollMoveAction(m));
		      				hexa.setMove((byte)4, AbstractMonster.Intent.ATTACK_DEBUFF, ((DamageInfo)hexa.damage.get(1)).base);
							hexa.createIntent();
							this.isDone = true;
							return;
      					case 6:
      						hexa = (Hexaghost)m;
      						hexa.changeState("Deactivate");
      						break;
      					default:
		      				AbstractDungeon.actionManager.addToTop(new ChangeStateAction(m, "Activate Orb"));
		      				AbstractDungeon.actionManager.addToBottom(new RollMoveAction(m));
		      				break;
		      		}
      			}
      			break;

      		// Lagavulin wakes up faster, but is otherwise the same
      		case "Lagavulin":
      			switch (m.nextMove) {
      				case 5: // sleep
						int idleCount = (int)ReflectionHacks.getPrivate(m, m.getClass(), "idleCount");
						ReflectionHacks.setPrivate(m, m.getClass(), "idleCount", idleCount + 1);
						if (idleCount + 1 >= 3)
						{
							ReflectionHacks.setPrivate(m, m.getClass(), "isOutTriggered", true);
							AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(m, "OPEN"));
							AbstractDungeon.actionManager.addToBottom(new SetMoveAction(m, (byte)3, AbstractMonster.Intent.ATTACK, 
							((DamageInfo)m.damage.get(0)).base));
						}
						break;
					case 1:
						ReflectionHacks.setPrivate(m, m.getClass(), "debuffTurnCount", 0);
						break;
					case 3:
						int debuffTurnCount = (int)ReflectionHacks.getPrivate(m, m.getClass(), "debuffTurnCount");
						ReflectionHacks.setPrivate(m, m.getClass(), "debuffTurnCount", debuffTurnCount + 1);
						break;
            	}
            	break;

			// Slaver should only try to use entangle once
			case "SlaverRed":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstMove", false);
				if (m.nextMove == 2) { ReflectionHacks.setPrivate(m, m.getClass(), "usedEntangle", true); }
				break;

			// Slimes get split triggered reset, so they will do another intent until you hit them again
			case "SlimeBoss":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstTurn", false);
				switch (m.nextMove) {
					case 4:
						m.setMove((byte)2, AbstractMonster.Intent.UNKNOWN);
						break;
					case 2:
						m.setMove((byte)1, AbstractMonster.Intent.ATTACK, ((DamageInfo)m.damage.get(1)).base);
						break;
					case 1:
						m.setMove((byte)4, AbstractMonster.Intent.STRONG_DEBUFF);
						break;
					case 3:
						m.setMove((byte)4, AbstractMonster.Intent.STRONG_DEBUFF);
						break;
				}
				break;

			// Slimes get split triggered reset, so they will do another intent until you hit them again
			case "SpikeSlime_L":
				ReflectionHacks.setPrivate(m, m.getClass(), "firstTurn", false);
				ReflectionHacks.setPrivate(m, m.getClass(), "splitTriggered", false);
				break;

			// Special case guardian stuff
			case "TheGuardian":
				switch (m.nextMove) {
					case 1:
						m.setMove((byte)3, AbstractMonster.Intent.ATTACK, ((DamageInfo)m.damage.get(1)).base);
						break;
					case 2:
						m.setMove((byte)7, AbstractMonster.Intent.STRONG_DEBUFF);
						break;
					case 3:
						m.setMove((byte)4, AbstractMonster.Intent.ATTACK_BUFF, (int)ReflectionHacks.getPrivate(m, m.getClass(), "twinSlamDamage"), 2, true);
						break;
					case 4:
						m.setMove((byte)5, AbstractMonster.Intent.ATTACK, (int)ReflectionHacks.getPrivate(m, m.getClass(), "whirlwindDamage"), (int)ReflectionHacks.getPrivate(m, m.getClass(), "whirlwindCount"), true);
						break;
					case 5:
						m.setMove((byte)6, AbstractMonster.Intent.DEFEND);
						break;
					case 6:
						m.setMove((byte)2, AbstractMonster.Intent.ATTACK, ((DamageInfo)m.damage.get(0)).base);
						break;
					case 7:
						m.setMove((byte)5, AbstractMonster.Intent.ATTACK, (int)ReflectionHacks.getPrivate(m, m.getClass(), "whirlwindDamage"), (int)ReflectionHacks.getPrivate(m, m.getClass(), "whirlwindCount"), true);
						break;
				}
				break;
			
		}

		// The actual stuff
		this.m.rollMove();
		m.createIntent();

		this.isDone = true;
	}
}
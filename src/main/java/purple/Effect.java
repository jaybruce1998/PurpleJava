package purple;

import java.util.*;
public class Effect
{
	double acc=1000, modifier;
	String effect, stat;
	boolean me, up;
	int amount, variation;
	String[] effects;
	private Effect(Effect e)
	{
		acc=e.acc;
		modifier=e.modifier;
		effect=e.effect;
		stat=e.stat;
		me=e.me;
		up=e.up;
		amount=e.amount;
		variation=e.variation;
	}
	public Effect(String effect, List<Effect> e)
	{
		this(e.get(0));
		this.effect=effect;
	}
	public Effect(String[] a, List<Effect> e)
	{
		this(e.get(0));
		acc=Double.parseDouble(a[0])/Double.parseDouble(a[1]);
	}
	public Effect(boolean me, boolean up, String stat, double modifier)
	{
		effect="hp";
		this.me=me;
		this.up=up;
		this.stat=stat;
		this.modifier=modifier;
	}
	public Effect(boolean me, boolean up, String stat, String[] a)
	{
		effect="hp";
		this.me=me;
		this.up=up;
		this.stat=stat;
		modifier=Double.parseDouble(a[0]);
		acc=Double.parseDouble(a[1]);
	}
	public Effect(String effect, boolean me, boolean up, int amount)
	{
		this.effect=effect;
		this.me=me;
		this.up=up;
		this.amount=amount;
	}
	public Effect(String effect)
	{
		this.effect=effect;
	}
	public Effect(String effect, int amount)
	{
		this.effect=effect;
		this.amount=amount;
	}
	public Effect(String effect, String[] a)
	{
		this.effect=effect;
		amount=Integer.parseInt(a[0]);
		variation=Integer.parseInt(a[1])-amount+1;
	}
	public Effect(String effect, String stat, int amount, double modifier)
	{
		this.effect=effect;
		this.stat=stat;
		this.amount=amount;
		this.modifier=modifier;
	}
	public Effect(String effect, String stat, String[] a, double modifier)
	{
		this.effect=effect;
		this.stat=stat;
		amount=Integer.parseInt(a[0]);
		variation=Integer.parseInt(a[1])-amount;
		this.modifier=modifier;
	}
	public Effect(double modifier)
	{
		effect="RECOIL";
		this.modifier=modifier;
	}
	public Effect(String effect, int amount, int variation)
	{
		this.effect=effect;
		this.amount=amount;
		this.variation=variation;
	}
	public Effect(String[] effects)
	{
		this.effects=effects;
	}
	public static List<Effect> getEffects(String[] e, int i)
	{
		List<Effect> effects=new ArrayList<>();
		if(i>=e.length)
			return effects;
		String eff=e[i];
		if(eff.equals("SLEEPING")||eff.equals("MISS"))
		{
			effects.add(new Effect(eff, getEffects(e, i+1)));
			return effects;
		}
		if(eff.indexOf("/")>0)
		{
			String[] ea=eff.split("/");
			if(ea[0].matches("\\d+"))
				effects.add(new Effect(ea, getEffects(e, i+1)));
			else
			{
				effects.add(new Effect(ea));
				effects.addAll(getEffects(e, i+1));
			}
			return effects;
		}
		boolean me=!eff.startsWith("opp");
		if(!me)
			eff=eff.substring(3).toLowerCase();
		switch(eff)
		{
			case "hp":
				if(!e[i+2].matches("\\d+"))
				{
					if(e[i+3].indexOf("-")>0)
						effects.add(new Effect(me, e[i+1].equals("UP"), e[i+2], e[i+3].split("-")));
					else
						effects.add(new Effect(me, e[i+1].equals("UP"), e[i+2], Double.parseDouble(e[i+3])));
					i+=4;
					break;
				}
			case "accuracy":
			case "def":
			case "spd":
			case "spatk":
			case "spdef":
			case "evasion":
			case "atk":
			case "crit_chance":
				effects.add(new Effect(eff, me, e[i+1].equals("UP"), Integer.parseInt(e[i+2])));
				i+=3;
				break;
			case "ATTACK_FIRST":
			case "ATTACK_SECOND":
			case "BIDE":
			case "BURN":
			case "CONFUSE":
			case "FREEZE":
			case "PARALYZE":
			case "SLEEP":
			case "POISON":
			case "TOXIC":
			case "FLINCH":
			case "CONVERT":
			case "IMMUNE":
			case "RESET_STATS":
			case "RECHARGE":
			case "RANDOM_MOVE":
			case "COPY_LAST_OPPONENT_MOVE":
			case "LAST_OPPONENT_MOVE":
			case "DISABLE_STAT_CHANGES":
			case "CHARGE":
			case "SWITCH_OPPONENT":
			case "MAKE_SUBSTITUTE":
			case "SWITCH":
			case "TRANSFORM_INTO_OPPONENT":
			case "CONFUSE_ME":
			case "SCATTER_COINS":
			case "RAGING":
				effects.add(new Effect(eff));
				i++;
				break;
			case "HIT":
			case "CRIT_CHANCE":
			case "DISABLE":
			case "CONTINUE":
			case "SLEEP_FOR":
				if(e[i+1].indexOf("-")>0)
					effects.add(new Effect(eff, e[i+1].split("-")));
				else
					effects.add(new Effect(eff, Integer.parseInt(e[i+1])));
				i+=2;
				break;
			case "END_DAMAGE":
				if(e[i+2].indexOf("-")>0)
					effects.add(new Effect(eff, e[i+1], e[i+2].split("-"), Double.parseDouble(e[i+3])));
				else
					effects.add(new Effect(eff, e[i+1], Integer.parseInt(e[i+2]), Double.parseDouble(e[i+3])));
				i+=4;
				break;
			case "DEF":
			case "SPDEF":
				effects.add(new Effect(eff, Integer.parseInt(e[i+1]), Integer.parseInt(e[i+2])));
				i+=3;
				break;
			default:
				throw new RuntimeException(String.join(" ", e)+"("+eff+")");
		}
		effects.addAll(getEffects(e, i));
		return effects;
	}
	public String toString()
	{
		return "Accuracy="+acc+", Modifier="+modifier+", Name="+effect+", Stat="+stat+", Me="+me+", Up="+up+", Amount="+amount+", Variation="+variation+";";
	}
}
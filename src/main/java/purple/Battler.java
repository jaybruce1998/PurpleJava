package purple;

public class Battler extends Monster
{
	static String[] GROUPS={"Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Slow","Medium Slow","Medium Slow","Medium Fast",
		"Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Fast","Fast","Medium Fast","Medium Fast","Fast","Fast","Medium Fast","Medium Fast",
		"Medium Slow","Medium Slow","Medium Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Slow","Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow",
		"Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Medium Slow","Slow","Slow","Medium Slow","Medium Slow","Medium Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast",
		"Medium Fast","Medium Fast","Medium Fast","Slow","Slow","Medium Slow","Medium Slow","Medium Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Slow","Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast",
		"Medium Fast","Slow","Slow","Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Slow","Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Slow","Slow","Slow","Slow","Slow","Medium Fast","Medium Fast","Medium Fast","Medium Fast",
		"Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Medium Fast","Slow","Slow","Slow","Slow","Slow","Slow","Slow","Slow","Slow","Medium Slow","Slow"};
	
	String nickname, status, group;
	int level, mhp, xp, mxp, atkDv, defDv, spatkDv, spdefDv, hpDv, spdDv, atkXp, defXp, spatkXp, spdefXp, hpXp, spdXp, bAtk, bDef, bSpatk, bSpdef, bHp, bSpd, lsi;
	Move[] moves;
	int[] pp, mpp;
	boolean shiny;
	Learnset[] learnset;
	Evolution evolution;
	public int xpNeeded(int level)
	{
		switch(group)
		{
			case "Medium Slow":
				return (int)(6*Math.pow(level, 3)/5-15*level*level+100*level-140);
			case "Medium Fast":
				return level*level*level;
			case "Fast":
				return (int)(4*(Math.pow(level, 3))/5);
			case "Slow":
				return (int)(5*(Math.pow(level, 3))/4);
			default:
				throw new RuntimeException(group);
		}
	}
	private int newStat(int base, int dv, int sxp, int c)
	{
		return (int)(((base+dv)*2+Math.sqrt(sxp)/4)*level/100+c);
	}
	private void calculateStats()
	{
		hp++;
		mhp=newStat(bHp, hpDv, hpXp, level+10);
		spd=newStat(bSpd, spdDv, spdXp, 5);
		atk=newStat(bAtk, atkDv, atkXp, 5);
		def=newStat(bDef, defDv, defXp, 5);
		spatk=newStat(bSpatk, spatkDv, spatkXp, 5);
		spdef=newStat(bSpdef, spdefDv, spdefXp, 5);
	}
	private void become(Monster m)
	{
		if(name.equals(nickname))
			nickname=m.name;
		dexNum=m.dexNum;
		learnset=Learnset.LEARNSETS[dexNum];
		evolution=Evolution.EVOLUTIONS[dexNum];
		name=m.name;
		types=m.types;
		bAtk=m.atk;
		bDef=m.def;
		bSpatk=m.spatk;
		bSpdef=m.spdef;
		bHp=m.hp;
		bSpd=m.spd;
		lsi=0;
	}
	private void finishSetup()
	{
		nickname=name;
		status="";
		group=GROUPS[dexNum];
		xp=xpNeeded(level);
		mxp=xpNeeded(level+1);
		calculateStats();
		hp=mhp;
		moves=new Move[4];
		pp=new int[4];
		mpp=new int[4];
		for(int md=0; lsi<learnset.length; lsi++)
			if(learnset[lsi].level>level)
				break;
			else
			{
				moves[md]=learnset[lsi].move;
				md=(md+1)%moves.length;
			}
		for(int i=0; i<moves.length; i++)
			if(moves[i]==null)
				break;
			else
			{
				pp[i]=moves[i].pp;
				mpp[i]=pp[i];
			}
	}
	public Battler(int level, Monster m)
	{
		super();
		become(m);
		this.level=level;
		atkDv=(int)(Math.random()*16);
		defDv=(int)(Math.random()*16);
		spatkDv=(int)(Math.random()*16);
		spdefDv=(int)(Math.random()*16);
		hpDv=(int)(Math.random()*16);
		spdDv=(int)(Math.random()*16);
		shiny=(int)(Math.random()*Main.SHINY_CHANCE)==0;
		finishSetup();
	}
	public Battler(Battler b)
	{
		super();
		dexNum=b.dexNum-1;
		if(dexNum>132&&dexNum<136)
			dexNum=133;
		else if(dexNum<1||Evolution.EVOLUTIONS[dexNum]==null)
			dexNum++;
		else
		{
			dexNum--;
			if(Evolution.EVOLUTIONS[dexNum]==null)
				dexNum++;
		}
		become(Monster.MONSTERS[dexNum]);
		level=1;
		int n=0, s=Main.SHINY_CHANCE/2;
		atkDv=b.atkDv==15?15:(int)(Math.random()*16);
		if(atkDv==15)
			n++;
		defDv=b.defDv==15?15:(int)(Math.random()*16);
		if(defDv==15)
			n++;
		spatkDv=b.spatkDv==15?15:(int)(Math.random()*16);
		if(spatkDv==15)
			n++;
		spdefDv=b.spdefDv==15?15:(int)(Math.random()*16);
		if(spdefDv==15)
			n++;
		hpDv=b.hpDv==15?15:(int)(Math.random()*16);
		if(hpDv==15)
			n++;
		spdDv=b.spdDv==15?15:(int)(Math.random()*16);
		if(spdDv==15)
			n++;
		shiny=(int)(Math.random()*(b.shiny?Math.sqrt(s):s))==0;
		finishSetup();
		OverworldGui.print(n+" perfect DVs! "+(shiny?"And it's shiny!!":n==0?" Wow that sucks!":"Not too shabby..."));
	}
	public Battler(String s)
	{
		super();
		String[] a=s.split("/"), i=a[0].split(",");
		name=i[2];
		become(Monster.MONSTERS[Integer.parseInt(i[0])]);
		level=Integer.parseInt(i[1]);
		nickname=i[2];
		status=i[3];
		xp=Integer.parseInt(i[4]);
		hp=Integer.parseInt(i[5])-1;//We add one again in calculateStats
		lsi=Integer.parseInt(i[6]);
		atkDv=Integer.parseInt(i[7]);
		defDv=Integer.parseInt(i[8]);
		spatkDv=Integer.parseInt(i[9]);
		spdefDv=Integer.parseInt(i[10]);
		hpDv=Integer.parseInt(i[11]);
		spdDv=Integer.parseInt(i[12]);
		shiny=i[13].equals("1");
		atkXp=Integer.parseInt(i[14]);
		defXp=Integer.parseInt(i[15]);
		spatkXp=Integer.parseInt(i[16]);
		spdefXp=Integer.parseInt(i[17]);
		hpXp=Integer.parseInt(i[18]);
		spdXp=Integer.parseInt(i[19]);
		group=GROUPS[dexNum];
		mxp=xpNeeded(level+1);
		calculateStats();
		moves=new Move[4];
		pp=new int[4];
		mpp=new int[4];
		for(int j=a.length-2; j>=0; j--)
		{
			i=a[j+1].split(",");
			moves[j]=Move.MOVE_MAP.get(i[0]);
			String[] q=i[1].split("x");
			pp[j]=Integer.parseInt(q[0]);
			if(q.length==2)
				mpp[j]=Integer.parseInt(q[1]);
			else
				mpp[j]=moves[j].pp;
		}
	}
	public void learn(Move[] lastMoves, Move m)
	{
		OverworldGui.spaceHeld=false;
		for(int i=0; i<lastMoves.length; i++)
			OverworldGui.strArr[i]=lastMoves[i]==null?"":lastMoves[i].name;
		OverworldGui.print(nickname+" is trying to learn "+m.name+"! Select a move to replace.");
		int v=BattleState.guiChoice(3);
		if(v==-1)
			OverworldGui.print(nickname+" did not learn "+m.name+".");
		else
		{
			OverworldGui.print(nickname+" forgot how to use "+lastMoves[v].name+"...");
			lastMoves[v]=m;
			moves[v]=m;
			pp[v]=Math.min(pp[v], m.pp);
			mpp[v]=m.pp;
			OverworldGui.print(" and learned how to use "+m.name+"!");
		}
	}
	public boolean newMove(Move m)
	{
		for(int i=0; i<moves.length; i++)
			if(moves[i]==m)
				return false;
		return true;
	}
	private void learnLevelUpMoves(Move[] lastMoves)
	{
		while(lsi<learnset.length)
			if(learnset[lsi].level<=level)
			{
				if(newMove(learnset[lsi].move))
					if(lastMoves[3]==null)
					{
						for(int i=2; true; i--)
							if(lastMoves[i]!=null)
							{
								moves[++i]=learnset[lsi].move;
								lastMoves[i]=moves[i];
								pp[i]=moves[i].pp;
								mpp[i]=pp[i];
								OverworldGui.print(nickname+" learned "+moves[i].name+"!");
								break;
							}
					}
					else
						learn(lastMoves, learnset[lsi].move);
				lsi++;
			}
			else
				break;
	}
	public void gainXp(Player p, Move[] lastMoves, int xp, int atk, int def, int spatk, int spdef, int hp, int spd)
	{
		this.xp+=xp;
		atkXp+=atk;
		defXp+=def;
		spatkXp+=spatk;
		spdefXp+=spdef;
		hpXp+=hp;
		spdXp+=spd;
		OverworldGui.print(nickname+" gained "+xp+" experience points!");
		while(this.xp>=mxp)
		{
			level++;
			OverworldGui.print(nickname+" grew to level "+level+"!");
			mxp=xpNeeded(level+1);
			learnLevelUpMoves(lastMoves);
			if(evolution!=null&&evolution.stone==null&&evolution.level<=level)
			{
				String n=nickname;
				become(Monster.MONSTERS[evolution.evo]);
				OverworldGui.print(n+" evolved into "+name+"!");
				p.register(this);
				learnLevelUpMoves(moves);
			}
			calculateStats();
		}
	}
	public boolean useStone(String s)
	{
		if(dexNum==133)
		{
			switch(s)
			{
				case "Water Stone":
					evolution=new Evolution(s, 134);
					break;
				case "Thunder Stone":
					evolution=new Evolution(s, 135);
					break;
				case "Fire Stone":
					evolution=new Evolution(s, 136);
					break;
				default:
					OverworldGui.print("Neat idea, but nope, sorry! Try a different stone...");
					return false;
			}
		}
		else if(evolution==null||!s.equals(evolution.stone))
		{
			OverworldGui.print("Quit messing around!");
			return false;
		}
		String n=nickname;
		become(Monster.MONSTERS[evolution.evo]);
		OverworldGui.print(n+" evolved into "+name+"!");
		return true;
	}
	public String[] moveStrings()
	{
		String[] a=new String[moves.length];
		for(int i=0; i<moves.length; i++)
			if(moves[i]==null)
				a[i]="";
			else
				a[i]=moves[i].name+" ("+pp[i]+"/"+mpp[i]+")";
		return a;
	}
	public static String[] partyStrings(Battler[] party)
	{
		String[] r=new String[party.length];
		for(int i=0; i<r.length; i++)
			if(party[i]==null)
				r[i]=" ";
			else
				r[i]=party[i].toString();
		return r;
	}
	public void fullyHeal()
	{
		hp=mhp;
		status="";
		for(int i=0; i<moves.length; i++)
			if(moves[i]==null)
				return;
			else
				pp[i]=mpp[i];
	}
	public String[] allInformation()
	{
		return new String[]{nickname+"/"+name,
			(status.isEmpty()?"healthy":status)+"/"+(shiny?"SHINY":"happy"),
			"Level: "+level+", "+String.join("/", types),
			"XP: "+xp+"/"+mxp,
			"",
			"HP: "+hp+"/"+mhp+" DV: ("+hpDv+")",
			"Speed: "+spd+" DV: ("+spdDv+")",
			"Attack: "+atk+" DV: ("+atkDv+")",
			"Defense: "+def+" DV: ("+defDv+")",
			"Special Attack: "+spatk+" DV: ("+spatkDv+")",
			"Special Defense: "+spdef+" DV: ("+spdefDv+")",
			"",
			"Moves:",
			moves[0].name+" "+pp[0]+"/"+mpp[0],
			moves[1]==null?"":moves[1].name+" "+pp[1]+"/"+mpp[1],
			moves[2]==null?"":moves[2].name+" "+pp[2]+"/"+mpp[2],
			moves[3]==null?"":moves[3].name+" "+pp[3]+"/"+mpp[3]
		};
	}
	public void append(StringBuilder sb)
	{
		sb.append(dexNum).append(',').append(level).append(',').append(nickname).append(',').append(status).append(',').append(xp).append(',').append(hp).append(',').append(lsi)
			.append(',').append(atkDv).append(',').append(defDv).append(',').append(spatkDv).append(',').append(spdefDv).append(',').append(hpDv).append(',').append(spdDv).append(',').append(shiny?1:0)
			.append(',').append(atkXp).append(',').append(defXp).append(',').append(spatkXp).append(',').append(spdefXp).append(',').append(hpXp).append(',').append(spdXp);
		for(int i=0; i<moves.length; i++)
			if(moves[i]==null)
				break;
			else
				sb.append('/').append(moves[i].name).append(',').append(pp[i]).append('x').append(mpp[i]);
	}
	public String toString()
	{
		String s=nickname+" L"+level+", HP: "+hp+"/"+mhp;
		switch(status)
		{
			case "POISONED":
				s+=" PSN";
				break;
			case "PARALYZED":
				s+=" PRZ";
				break;
			case "SLEEPING":
				s+=" SLP";
				break;
			case "FROZEN":
				s+=" FZN";
				break;
			case "BURNED":
				s+=" BRN";
				break;
		}
		return s;
	}
}
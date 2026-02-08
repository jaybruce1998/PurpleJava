package purple;

import java.util.*;
public class BattleState
{
	static final Move STRUGGLE=Move.MOVE_MAP.get("Struggle");
	static final Move CONFUSION=Move.MOVE_MAP.get("CONFUSION");
	static final Move RECHARGE=Move.MOVE_MAP.get("RECHARGE");
	static final Move ROAR=Move.MOVE_MAP.get("Roar");
	static final Move TELEPORT=Move.MOVE_MAP.get("Teleport");
	static final Move WHIRLWIND=Move.MOVE_MAP.get("Whirlwind");
	static final int[] EXPERIENCE_TABLE={10000,64,141,208,65,142,209,66,143,210,53,72,160,52,71,159,55,113,172,57,116,58,162,62,147,82,122,93,163,59,117,194,60,118,195,68,129,63,178,76,
		109,54,171,78,132,184,70,128,75,138,81,153,69,148,80,174,74,149,91,213,77,131,185,73,145,186,88,146,193,84,151,191,105,205,86,134,177,152,192,99,164,89,169,94,96,158,
		100,176,90,157,97,203,95,126,190,108,102,165,115,206,103,150,98,212,87,124,139,140,127,114,173,135,204,255,166,175,83,155,111,170,106,207,136,187,137,156,167,200,211,
		20,214,219,61,92,196,197,198,130,120,199,119,201,202,154,215,216,217,67,144,218,220,154,1000};
	static final String[] ACTIONS={"Fight", "Pokemon", "Item", "Run"};
	static final Item LUCKY_EGG=Item.ITEM_MAP.get("Lucky Egg");
	private static int getMoveDex(BattleState b)
	{
		if(b.shouldStruggle())
			return -1;
		for(int i=0; i<4; i++)
			OverworldGui.strArr[i]=b.moves[i]==null?"":b.monster.moves[i].name+" ("+b.monster.pp[i]+"/"+b.monster.mpp[i]+")";
		while(true)
		{
			int moveDex=guiChoice(3);
			if(moveDex==-1)
				return -2;
			if(b.moves[moveDex]==null)
				OverworldGui.print("You don't know enough moves!");
			else if(b.monster.pp[moveDex]==0)
				OverworldGui.print("You don't have any PP!");
			else if(b.dTurns>0&&b.disabled==moveDex)
				OverworldGui.print("That move is disabled!");
			else
				return moveDex;
		}
	}
	private static int randomMoveDex(BattleState b)
	{
		List<Integer> l=new ArrayList<>();
		for(int i=0; i<4; i++)
			if(b.moves[i]==null)
				break;
			else if((b.dTurns==0||b.disabled!=i)&&b.monster.pp[i]>0)
				l.add(i);
		return l.isEmpty()?-1:l.get((int)(Math.random()*l.size()));
	}
	private static boolean blackedOut(Battler[] monsters)
	{
		for(int i=0; i<monsters.length; i++)
			if(monsters[i]==null)
				return true;
			else if(monsters[i].hp>0)
				return false;
		return true;
	}
	private static int coins(BattleState pState, BattleState oState)
	{
		int coins=pState.coins+oState.coins;
		if(coins>0)
			OverworldGui.print("You picked up "+coins+" scattered coins!");
		return coins;
	}
	private static boolean canSwitch(Battler[] monsters)
	{
		int r=0;
		for(int i=0; i<monsters.length; i++)
			if(monsters[i]==null)
				break;
			else if(monsters[i].hp>0)
				r++;
		return r>1;
	}
	private static int switchMon(Battler[] monsters, int i, boolean mustSwitch)
	{
		OverworldGui.usableItems=null;
		OverworldGui.longArr=Battler.partyStrings(monsters);
		while(true)
		{
			OverworldGui.choosingFromLongList=true;
			int monDex=guiChoice(5);
			OverworldGui.choosingFromLongList=false;
			if(monDex==-1)
				if(mustSwitch)
					OverworldGui.print("You must switch!");
				else
					return -1;
			else if(monDex==i)
				OverworldGui.print("No! You have to switch pokemon!");
			else if(monsters[monDex]==null)
				OverworldGui.print("You don't have enough pokemon!");
			else if(monsters[monDex].hp==0)
				OverworldGui.print("No! That pokemon has already fainted!");
			else
				return monDex;
		}
	}
	private static void seed(BattleState sucker, BattleState sucked)
	{
		sucker.spdefTurns=Math.max(0, sucker.spdefTurns-1);
		sucker.defTurns=Math.max(0, sucker.defTurns-1);
		if(!sucked.seeded||sucker.monster.hp<=0||sucked.monster.hp<=0)
			return;
		int d=Math.max(1, Math.min(sucked.monster.hp, sucked.monster.mhp/16));
		sucked.monster.hp-=d;
		OverworldGui.print(sucker.monster.nickname+" sucked "+d+" health from the seed!");
		sucker.monster.hp=Math.min(sucker.monster.mhp, sucker.monster.hp+d);
	}
	private void gainXp(Player p, Set<Battler> participants, Battler fainted, boolean trainer, int lucky)
	{
		int l=participants.size(), xp=EXPERIENCE_TABLE[fainted.dexNum]*fainted.level/7, per=xp/l,
			atk=fainted.bAtk/l, def=fainted.bDef/l, spatk=fainted.bSpatk/l, spdef=fainted.bSpdef/l, hp=fainted.bHp/l, spd=fainted.bSpd/l;
		per*=lucky;
		if(trainer)
		{
			per*=3;
			per/=2;
		}
		for(Battler b: participants)
			b.gainXp(p, b==monster?moves:b.moves, per, atk, def, spatk, spdef, hp, spd);
	}
	public static int guiChoice(int m)
	{
		synchronized (OverworldGui.choiceLock) {
			OverworldGui.clickedChoice=-1;
			while (OverworldGui.clickedChoice<0||OverworldGui.clickedChoice>m) {
				try {
					OverworldGui.choiceLock.wait(); // release lock and wait for notify
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
			int choice=OverworldGui.clickedChoice;
			OverworldGui.clickedChoice=-1;
			if(OverworldGui.rightClicked)
			{
				OverworldGui.rightClicked=false;
				return -1;
			}
			return choice;
		}
	}
	public static int wildBattle(Battler[] monsters, Battler wildMon, OverworldGui gui)
	{
		int monDex, escapeAttempts=0, moveDex, wMoveDex, lucky=gui.player.hasItem(LUCKY_EGG)?2:1;
		Move myMove, yourMove;
		for(monDex=0; monDex<monsters.length; monDex++)
			if(monsters[monDex].hp>0)
				break;
		BattleState pState=new BattleState(monsters[monDex]), wState=new BattleState(wildMon);
		OverworldGui.setBattleStates(pState, wState);
		Set<Battler> participants=new HashSet<>();
		participants.add(monsters[monDex]);
		wildMon.nickname="Wild "+wildMon.name+(gui.player.pokedex[wildMon.dexNum]?"🔴":"");
		OverworldGui.print("A wild "+wildMon.name+" appeared!");
		OverworldGui.print("Go! "+monsters[monDex].nickname+"!");
		while(true)
		{
			boolean didSomething=true;
			myMove=STRUGGLE;
			yourMove=STRUGGLE;
			for(int i=0; i<ACTIONS.length; i++)
				OverworldGui.strArr[i]=ACTIONS[i];
			switch(gui.spacebar?0:guiChoice(3))
			{
				case 0:
					if(pState.nextMove==null)
					{
						moveDex=getMoveDex(pState);
						if(moveDex==-2)
						{
							didSomething=false;
							break;
						}
						if(pState.canMove()&&moveDex>=0)
						{
							myMove=pState.moves[moveDex];
							monsters[monDex].pp[moveDex]--;
						}
					}
					else if(pState.canMove())
						myMove=pState.nextMove;
					if(wState.nextMove==null)
					{
						wMoveDex=randomMoveDex(wState);
						if(wState.canMove()&&wMoveDex>=0)
						{
							yourMove=wState.moves[wMoveDex];
							wildMon.pp[wMoveDex]--;
						}
					}
					else if(wState.canMove())
						yourMove=wState.nextMove;
					if(myMove==ROAR)
					{
						OverworldGui.print("You scared away the wild "+wildMon.name+"!");
						return coins(pState, wState);
					}
					else if(yourMove==ROAR)
					{
						OverworldGui.print(wildMon.name+" scared you away!");
						return coins(pState, wState);
					}
					else if(myMove==TELEPORT)
					{
						OverworldGui.print("You teleported away!");
						return coins(pState, wState);
					}
					else if(yourMove==TELEPORT)
					{
						OverworldGui.print(wildMon.name+" teleported away!");
						return coins(pState, wState);
					}
					else if(myMove==WHIRLWIND)
					{
						OverworldGui.print("A strong gust blew wild "+wildMon.name+" away!");
						return coins(pState, wState);
					}
					else if(yourMove==WHIRLWIND)
					{
						OverworldGui.print("A strong gust blew you away!");
						return coins(pState, wState);
					}
					if(pState.goBefore(myMove, wState, yourMove))
						if(pState.doMove(myMove, wState))
						{
							if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
							{
								OverworldGui.print("All of your pokemon fainted!");
								return -1;
							}
						}
						else
						{
							OverworldGui.print("The wild "+wildMon.name+" fainted!");
							pState.gainXp(gui.player, participants, wildMon, false, lucky);
							return coins(pState, wState);
						}
					else if(wState.doMove(yourMove, pState)&&!pState.doMove(myMove, wState))
					{
						OverworldGui.print("The wild "+wildMon.name+" fainted!");
						pState.gainXp(gui.player, participants, wildMon, false, lucky);
						return coins(pState, wState);
					}
					break;
				case 1:
					if(pState.tTurns==0&&pState.nextMove==null)
					{
						moveDex=switchMon(monsters, monDex, false);
						if(moveDex==-1)
						{
							didSomething=false;
							break;
						}
						monDex=moveDex;
						pState=new BattleState(monsters[monDex], pState);
						OverworldGui.playerState=pState;
						participants.add(monsters[monDex]);
						OverworldGui.print("Go! "+monsters[monDex].nickname+"!");
						if(wState.nextMove==null)
						{
							wMoveDex=randomMoveDex(wState);
							if(wState.canMove()&&wMoveDex>=0)
							{
								yourMove=wState.moves[wMoveDex];
								wildMon.pp[wMoveDex]--;
							}
						}
						else if(wState.canMove())
							yourMove=wState.nextMove;
						if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
						{
							OverworldGui.print("All of your pokemon fainted!");
							return -1;
						}
					}
					else
					{
						didSomething=false;
						OverworldGui.print("You are trapped!");
					}
					break;
				case 2:
					gui.setWildItems();
					synchronized(OverworldGui.itemLock)
					{
						while (OverworldGui.usingBattleItem) {
							try {
								OverworldGui.itemLock.wait(); // release lock and wait for notify
							} catch (InterruptedException ex) {
								ex.printStackTrace();
								System.exit(1);
							}
						}
						OverworldGui.choosingFromLongList=false;
						if(!OverworldGui.battling)
							return coins(pState, wState);
						if(!OverworldGui.busedItem)
						{
							didSomething=false;
							break;
						}
						if(pState.monster.status.isEmpty())
							pState.sTurns=0;
						if(wState.nextMove==null)
						{
							wMoveDex=randomMoveDex(wState);
							if(wState.canMove()&&wMoveDex>=0)
							{
								yourMove=wState.moves[wMoveDex];
								wildMon.pp[wMoveDex]--;
							}
						}
						else if(wState.canMove())
							yourMove=wState.nextMove;
						if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
						{
							OverworldGui.print("All of your pokemon fainted!");
							return -1;
						}
					}
					break;
				case 3:
					if(monsters[monDex].spd*128.0/wildMon.spd+30*++escapeAttempts>Math.random()*256)
					{
						OverworldGui.print("You got away safely!");
						return coins(pState, wState);
					}
					else
					{
						OverworldGui.print("You couldn't escape!");
						if(wState.nextMove==null)
						{
							wMoveDex=randomMoveDex(wState);
							if(wState.canMove()&&wMoveDex>=0)
							{
								yourMove=wState.moves[wMoveDex];
								wildMon.pp[wMoveDex]--;
							}
						}
						else if(wState.canMove())
							yourMove=wState.nextMove;
						if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
						{
							OverworldGui.print("All of your pokemon fainted!");
							return -1;
						}
					}
			}
			if(didSomething)
			{
				seed(pState, wState);
				seed(wState, pState);
				if(pState.endTurn())
					if(blackedOut(monsters))
					{
						OverworldGui.print("All of your pokemon fainted!");
						return -1;
					}
					else
					{
						monDex=switchMon(monsters, monDex, true);
						pState=new BattleState(monsters[monDex], pState);
						participants.add(pState.monster);
						OverworldGui.playerState=pState;
					}
				if(wState.endTurn())
				{
					OverworldGui.print("The wild "+wildMon.name+" fainted!");
					pState.gainXp(gui.player, participants, wildMon, false, lucky);
					return coins(pState, wState);
				}
			}
		}
	}
	
	public static int trainerBattle(Battler[] monsters, String tName, Battler[] tMonsters, OverworldGui gui)
	{
		int monDex, tMonDex=0, moveDex, wMoveDex, lucky=gui.player.hasItem(LUCKY_EGG)?2:1;
		Move myMove, yourMove;
		for(monDex=0; monDex<monsters.length; monDex++)
			if(monsters[monDex].hp>0)
				break;
		BattleState pState=new BattleState(monsters[monDex]), wState=new BattleState(tMonsters[0]);
		OverworldGui.setBattleStates(pState, wState);
		Set<Battler> participants=new HashSet<>();
		participants.add(monsters[monDex]);
		tMonsters[tMonDex].nickname="Enemy "+tMonsters[0].name;
		OverworldGui.print(tName+" would like to battle!");
		OverworldGui.print(tName+" sent out "+tMonsters[0].name+"!");
		OverworldGui.print("Go! "+monsters[monDex].nickname+"!");
		while(true)
		{
			boolean didSomething=true;
			myMove=STRUGGLE;
			yourMove=STRUGGLE;
			for(int i=0; i<ACTIONS.length; i++)
				OverworldGui.strArr[i]=ACTIONS[i];
			switch(gui.spacebar?0:guiChoice(3))
			{
				case 0:
					if(pState.nextMove==null)
					{
						moveDex=getMoveDex(pState);
						if(moveDex==-2)
						{
							didSomething=false;
							break;
						}
						if(pState.canMove()&&moveDex>=0)
						{
							myMove=pState.moves[moveDex];
							monsters[monDex].pp[moveDex]--;
						}
					}
					else if(pState.canMove())
						myMove=pState.nextMove;
					if(wState.nextMove==null)
					{
						wMoveDex=randomMoveDex(wState);
						if(wState.canMove()&&wMoveDex>=0)
						{
							yourMove=wState.moves[wMoveDex];
							tMonsters[tMonDex].pp[wMoveDex]--;
						}
					}
					else if(wState.canMove())
						yourMove=wState.nextMove;
					if(pState.goBefore(myMove, wState, yourMove))
						if(pState.doMove(myMove, wState))
						{
							if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
							{
								OverworldGui.print("All of your pokemon fainted!");
								return -1;
							}
						}
						else
						{
							pState.tTurns=0;
							OverworldGui.print(tMonsters[tMonDex].nickname+" fainted!");
							pState.gainXp(gui.player, participants, tMonsters[tMonDex], true, lucky);
							if(blackedOut(tMonsters))
							{
								OverworldGui.print("You won the battle!");
								return coins(pState, wState);
							}
							else
							{
								wState=new BattleState(tMonsters[++tMonDex], wState);
								OverworldGui.enemyState=wState;
								OverworldGui.print(tName+" sent out "+tMonsters[tMonDex].name+"!");
								participants.clear();
								participants.add(pState.monster);
							}
						}
					else if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
					{
						OverworldGui.print("All of your pokemon fainted!");
						return -1;
					}
					else if(pState.monster.hp>0&&!pState.doMove(myMove, wState))
					{
						pState.tTurns=0;
						OverworldGui.print(tMonsters[tMonDex].nickname+" fainted!");
						pState.gainXp(gui.player, participants, tMonsters[tMonDex], true, lucky);
						if(blackedOut(tMonsters))
						{
							OverworldGui.print("You won the battle!");
							return coins(pState, wState);
						}
						else
						{
							wState=new BattleState(tMonsters[++tMonDex], wState);
							OverworldGui.enemyState=wState;
							OverworldGui.print(tName+" sent out "+tMonsters[tMonDex].name+"!");
							participants.clear();
							participants.add(pState.monster);
						}
					}
					break;
				case 1:
					if(pState.tTurns==0&&pState.nextMove==null)
					{
						moveDex=switchMon(monsters, monDex, false);
						if(moveDex==-1)
						{
							didSomething=false;
							break;
						}
						monDex=moveDex;
						pState=new BattleState(monsters[monDex], pState);
						OverworldGui.playerState=pState;
						participants.add(monsters[monDex]);
						OverworldGui.print("Go! "+monsters[monDex].nickname+"!");
						if(wState.nextMove==null)
						{
							wMoveDex=randomMoveDex(wState);
							if(wState.canMove()&&wMoveDex>=0)
							{
								yourMove=wState.moves[wMoveDex];
								tMonsters[tMonDex].pp[wMoveDex]--;
							}
						}
						else if(wState.canMove())
							yourMove=wState.nextMove;
						if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
						{
							OverworldGui.print("All of your pokemon fainted!");
							return -1;
						}
					}
					else
					{
						didSomething=false;
						OverworldGui.print("You are trapped!");
					}
					break;
				case 2:
					gui.setTrainerItems();
					synchronized(OverworldGui.itemLock)
					{
						while (OverworldGui.usingBattleItem) {
							try {
								OverworldGui.itemLock.wait();
							} catch (InterruptedException ex) {
								ex.printStackTrace();
								System.exit(1);
							}
						}
						OverworldGui.choosingFromLongList=false;
						if(!OverworldGui.busedItem)
						{
							didSomething=false;
							break;
						}
						if(wState.nextMove==null)
						{
							wMoveDex=randomMoveDex(wState);
							if(wState.canMove()&&wMoveDex>=0)
							{
								yourMove=wState.moves[wMoveDex];
								tMonsters[tMonDex].pp[wMoveDex]--;
							}
						}
						else if(wState.canMove())
							yourMove=wState.nextMove;
						if(!wState.doMove(yourMove, pState)&&blackedOut(monsters))
						{
							OverworldGui.print("All of your pokemon fainted!");
							return -1;
						}
					}
					break;
				case 3:
					didSomething=false;
					OverworldGui.print("Aww come on man, don't give up!");
			}
			if(didSomething)
			{
				seed(pState, wState);
				seed(wState, pState);
				if(pState.endTurn())
					if(blackedOut(monsters))
					{
						OverworldGui.print("All of your pokemon fainted!");
						return -1;
					}
					else
					{
						monDex=switchMon(monsters, monDex, true);
						pState=new BattleState(monsters[monDex], pState);
						participants.add(pState.monster);
						OverworldGui.playerState=pState;
					}
				if(wState.endTurn())
				{
					pState.tTurns=0;
					OverworldGui.print(tMonsters[tMonDex].nickname+" fainted!");
					pState.gainXp(gui.player, participants, tMonsters[tMonDex], true, lucky);
					if(blackedOut(tMonsters))
					{
						OverworldGui.print("You won the battle!");
						return coins(pState, wState);
					}
					else
					{
						wState=new BattleState(tMonsters[++tMonDex], wState);
						OverworldGui.enemyState=wState;
						OverworldGui.print(tName+" sent out "+tMonsters[tMonDex].name+"!");
						participants.clear();
						participants.add(pState.monster);
					}
				}
			}
		}
	}
	
	Battler monster;
	List<Battler> monsters;
	int coins, accStage, evsnStage, spdStage, atkStage, defStage, spatkStage, spdefStage, critMul,
		spd, atk, def, spatk, spdef, sTurns, tTurns, cTurns, disabled, dTurns, spdefTurns, defTurns, subHp, poisonDamage;
	String[] types;
	boolean crit, wasPhysical, flinched, immune, seeded, canLower, raging;
	Move nextMove, lastMove;
	int[] lastDamage;
	Move[] moves;
	private String es;
	public BattleState(Battler monster)
	{
		this.monster=monster;
		monsters=new ArrayList<>();
		monsters.add(monster);
		critMul=1;
		spd=monster.spd;
		atk=monster.atk;
		def=monster.def;
		spatk=monster.spatk;
		spdef=monster.spdef;
		types=new String[monster.types.length];
		for(int i=0; i<types.length; i++)
			types[i]=monster.types[i];
		if(monster.status.equals("SLEEPING")||monster.status.equals("FROZEN"))
			sTurns=2+(int)(Math.random()*4);
		lastDamage=new int[2];
		moves=new Move[monster.moves.length];
		for(int i=0; i<moves.length; i++)
			moves[i]=monster.moves[i];
		canLower=true;
	}
	public BattleState(Battler monster, BattleState b)
	{
		this(monster);
		spdefTurns=b.spdefTurns;
		defTurns=b.defTurns;
	}
	private static double effect(int stage)
	{
		return stage==0?1:stage<0?2.0/(2-stage):(stage+2)/2.0;
	}
	private double speed()
	{
		return (monster.status.equals("PARALYZED")?0.5:1)*effect(spdStage);
	}
	private static double accuracy(int stage)
	{
		return stage==0?1:stage<0?3.0/(3-stage):(stage+3)/3.0;
	}
	public boolean goBefore(Move m, BattleState b, Move o)
	{
		int mp=m.priority(), op=o.priority();
		if(mp==op)
		{
			double s1=spd*effect(spdStage), s2=b.spd*effect(b.spdStage);
			return s1==s2?Math.random()<0.5:s1>s2;
		}
		return mp>op;
	}
	private double stab(Move m)
	{
		for(String t: types)
			if(t.equals(m.type))
				return 1.5;
		return 1;
	}
	private boolean hasType(String type)
	{
		for(String t: types)
			if(t.equals(type))
				return true;
		return false;
	}
	public boolean canMove()
	{
		if(sTurns==0)
			return true;
		if(--sTurns==0)
		{
			OverworldGui.print(monster.nickname+(monster.status.equals("SLEEPING")?" woke up!":"thawed out!"));
			monster.status="";
			return true;
		}
		return false;
	}
	private int calcDamage(Move m, BattleState b)
	{
		double mul=Types.damage(m, b.types), d;
		if (mul == 0.0) {
			return 0;
		} else if (mul == 1.0) {
			es = "";
		} else if (mul == 2.0) {
			es = "It was super effective! ";
		} else if (mul == 4.0) {
			es = "It was crazy effective!! ";
		} else if (mul == 0.5) {
			es = "It was not very effective. ";
		} else if (mul == 0.25) {
			es = "It was very ineffective. ";
		} else {
			es="I am so confused. ";
		}
		crit=Math.random()*24/critMul<m.critChance();
		switch(m.atkType)
		{
			case "Physical":
				if(crit)
					d=((2.0*monster.level*2/5+2)*m.power*atk*effect(Math.max(0, atkStage))/b.def/effect(Math.min(0, b.defStage))/50+2)
						*stab(m)*mul*(217+(int)(Math.random()*39))/255;
				else
				{
					d=((2.0*monster.level/5+2)*m.power*atk*effect(atkStage)/b.def/effect(b.defStage)/50+2)
						*stab(m)*mul*(217+(int)(Math.random()*39))/255;
					if(b.defTurns>0)
						d/=2;
				}
				if(monster.status.equals("BURNED"))
					d/=2;
				return Math.max(1, (int)d);
			case "Special":
			{
				if(crit)
					d=((2.0*monster.level*2/5+2)*m.power*spatk*effect(Math.max(0, spatkStage))/b.spdef/effect(Math.min(0, b.spdefStage))/50+2)
						*stab(m)*mul*(217+(int)(Math.random()*39))/255;
				else
				{
					d=((2.0*monster.level/5+2)*m.power*spatk*effect(spatkStage)/b.spdef/effect(b.spdefStage)/50+2)
						*stab(m)*mul*(217+(int)(Math.random()*39))/255;
					if(b.spdefTurns>0)
						d/=2;
				}
				return Math.max(1, (int)d);
			}
			case "Status":
				return 0;
			default:
				throw new RuntimeException(m.atkType);
		}
	}
	private boolean setLastMove(Move m, int d)
	{
		if(subHp>0)
		{
			subHp-=d;
			if(subHp<=0)
			{
				subHp=0;
				OverworldGui.print("The substitute broke!");
			}
		}
		else
			monster.hp-=d;
		if(monster.hp<=0)
			return false;
		if(raging&&d>0)
		{
			atkStage++;
			OverworldGui.print(monster.nickname+"'s rage is building...");
			if(atkStage==7)
				OverworldGui.print("But it can't get any angrier!");
		}
		lastMove=m;
		lastDamage[0]=lastDamage[1];
		lastDamage[1]=d;
		wasPhysical=m!=null&&m.atkType.equals("Physical");
		return true;
	}
	private boolean endTurn()
	{
		int d;
		flinched=false;
		if(tTurns>0)
		{
			tTurns--;
			d=Math.max(monster.mhp/16, 1);
			OverworldGui.print(monster.nickname+" was squeezed for "+d+" damage!");
			monster.hp-=d;
		}
		switch(monster.status)
		{
			case "BURNED":
				d=Math.max(monster.mhp/16, 1);
				OverworldGui.print(monster.nickname+" took "+d+" burn damage!");
				monster.hp-=d;
				break;
			case "POISONED":
				d=Math.max(monster.mhp/16, 1);
				if(poisonDamage>0)
					d*=poisonDamage++;
				OverworldGui.print(monster.nickname+" took "+d+" poison damage!");
				monster.hp-=d;
				break;
		}
		monster.hp=Math.max(0, monster.hp);
		if(monster.hp<=0)
		{
			OverworldGui.print(monster.nickname+" fainted!");
			return true;
		}
		return false;
	}
	private boolean disable(Move m, int a, int b)
	{
		for(disabled=0; disabled<moves.length; disabled++)
			if(moves[disabled]==m)
			{
				dTurns=a+(int)(Math.random()*b);
				return true;
			}
		return false;
	} 
	private boolean shouldStruggle()
	{
		for(int i=0; i<monster.pp.length; i++)
			if((dTurns==0||disabled!=i)&&monster.pp[i]>0)
				return false;
		return true;
	}
	private void resetStats()
	{
		accStage=0;
		evsnStage=0;
		spdStage=0;
		atkStage=0;
		defStage=0;
		spatkStage=0;
		spdefStage=0;
		critMul=1;
		canLower=true;
	}
	private void processMiss(Move m)
	{
		if(m.effects.isEmpty())
			return;
		Effect e=m.effects.get(0);
		switch(e.effect)
		{
			case "MISS":
				switch(e.stat)
				{
					case "max_hp":
						monster.hp=Math.max(0, monster.hp-(int)(e.modifier*monster.mhp));
						OverworldGui.print("It fell and lost "+e.modifier*100+" percent of its max health!");
						break;
					default:
						throw new RuntimeException(e.stat);
				}
				break;
			case "hp":
				if(e.me&&e.stat==null)
				{
					monster.hp=Math.max(0, monster.hp-e.amount);
					OverworldGui.print("But "+monster.nickname+" still exploded!");
				}
				break;
		}
	}
	public boolean doMove(Move m, BattleState b)
	{
		nextMove=null;
		immune=false;
		raging=false;
		if(b.monster.hp<=0)
			return false;
		if(flinched)
		{
			flinched=false;
			OverworldGui.print(monster.nickname+" flinched and cannot move!");
			return true;
		}
		if(sTurns>0)
		{
			OverworldGui.print(monster.nickname+(monster.status.equals("SLEEPING")?" is fast asleep!":" is frozen solid!"));
			return true;
		}
		if(monster.status.equals("PARALYZED")&&Math.random()<0.25)
		{
			OverworldGui.print(monster.nickname+" is fully paralyzed!");
			return true;
		}
		if(cTurns>0)
		{
			cTurns--;
			if(Math.random()<0.5)
			{
				int damage=calcDamage(CONFUSION, this);
				monster.hp=Math.max(0, monster.hp-damage);
				OverworldGui.print(monster.nickname+" hit itself in confusion for "+damage+" damage!");
				return true;
			}
		}
		OverworldGui.print(monster.nickname+" used "+m.name+"!");
		if(b.immune||Math.random()*100>=m.acc*accuracy(accStage)/accuracy(b.evsnStage))
		{
			OverworldGui.print("The attack missed!");
			processMiss(m);
			return true;
		}
		int damage=calcDamage(m, b);
		if(damage==0&&!m.atkType.equals("Status"))
		{
			OverworldGui.print("It had no effect!");
			processMiss(m);
			return true;
		}
		if(m.shouldPrintDamage())
		{
			if(crit)
				OverworldGui.print("Critical hit!");
			OverworldGui.print(es+"It did "+damage+" damage!");
		}
		for(int i=0, r; i<m.effects.size(); i++)
		{
			Effect e=m.effects.get(i);
			if(Math.random()<e.acc)
				switch(e.effect)
				{
					case "hp":
						switch(e.stat)
						{
							case "damage":
								r=Math.max(1, (int)(Math.min(damage, b.monster.hp)*e.modifier));
								if(e.up)
								{
									monster.hp=Math.min(monster.mhp, monster.hp+r);
									OverworldGui.print(monster.nickname+" gained "+r+" health!");
								}
								else
								{
									monster.hp=Math.max(0, monster.hp-r);
									OverworldGui.print(monster.nickname+" took "+r+" damage in recoil!");
								}
								break;
							case "physical_damage":
								if(wasPhysical)
								{
									damage=lastDamage[1]*2;
									OverworldGui.print(monster.nickname+" countered for "+damage+" damage!");
								}
								else
									OverworldGui.print("But "+monster.nickname+" wasn't hit by a physical attack!");
								break;
							case "max_hp":
								if(monster.hp==monster.mhp)
									OverworldGui.print(monster.nickname+" already has full health!");
								else
								{
									r=monster.mhp/2;
									monster.hp=Math.min(monster.mhp, monster.hp+r);
									OverworldGui.print(monster.nickname+" recovered "+r+" health!");
								}
								break;
							case "oppHp":
								damage=b.monster.hp/2;
								if(damage==0)
									OverworldGui.print("But dingus, you can't KO a pokemon with Super Fang!");
								break;
							case "level":
								damage=monster.level;
								OverworldGui.print(monster.nickname+"'s level determined the damage!");
								break;
							case null:
								if(e.up)
								{
									if(monster.hp==monster.mhp)
									{
										OverworldGui.print(monster.nickname+" already has full health!");
										return true;
									}
									else
									{
										r=monster.mhp-monster.hp;
										monster.hp=monster.mhp;
										OverworldGui.print(monster.nickname+" recovered "+r+" health!");
									}
								}
								else if(e.me)
								{
									r=Math.min(monster.mhp, e.amount);
									monster.hp-=r;
									OverworldGui.print(monster.nickname+" lost "+e.amount+" health!");
								}
								else
								{
									damage=e.amount;
									OverworldGui.print(b.monster.nickname+" took "+e.amount+" damage!");
								}
								break;
							default:
								throw new RuntimeException(e.stat);
						}
						break;
					case "spdef":
						if(e.me)
						{
							if(e.up)
							{
								spdefStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s special defense increased by "+e.amount+" stages!");
								if(spdefStage>6)
								{
									spdefStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								spdefStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s special defense dropped by "+e.amount+" stages!");
								if(spdefStage<-6)
								{
									spdefStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.spdefStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s special defense increased by "+e.amount+" stages!");
								if(b.spdefStage>6)
								{
									b.spdefStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.spdefStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s special defense dropped by "+e.amount+" stages!");
								if(b.spdefStage<-6)
								{
									b.spdefStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "def":
						if(e.me)
						{
							if(e.up)
							{
								defStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s defense increased by "+e.amount+" stages!");
								if(defStage>6)
								{
									defStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								defStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s defense dropped by "+e.amount+" stages!");
								if(defStage<-6)
								{
									defStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.defStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s defense increased by "+e.amount+" stages!");
								if(b.defStage>6)
								{
									b.defStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.defStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s defense dropped by "+e.amount+" stages!");
								if(b.defStage<-6)
								{
									b.defStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "spd":
						if(e.me)
						{
							if(e.up)
							{
								spdStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s speed increased by "+e.amount+" stages!");
								if(spdStage>6)
								{
									spdStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								spdStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s speed dropped by "+e.amount+" stages!");
								if(spdStage<-6)
								{
									spdStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.spdStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s speed increased by "+e.amount+" stages!");
								if(b.spdStage>6)
								{
									b.spdStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.spdStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s speed dropped by "+e.amount+" stages!");
								if(b.spdStage<-6)
								{
									b.spdStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "atk":
						if(e.me)
						{
							if(e.up)
							{
								atkStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s attack increased by "+e.amount+" stages!");
								if(atkStage>6)
								{
									atkStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								atkStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s attack dropped by "+e.amount+" stages!");
								if(atkStage<-6)
								{
									atkStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.atkStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s attack increased by "+e.amount+" stages!");
								if(b.atkStage>6)
								{
									b.atkStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.atkStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s attack dropped by "+e.amount+" stages!");
								if(b.atkStage<-6)
								{
									b.atkStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "spatk":
						if(e.me)
						{
							if(e.up)
							{
								spatkStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s special attack increased by "+e.amount+" stages!");
								if(spatkStage>6)
								{
									spatkStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								spatkStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s special attack dropped by "+e.amount+" stages!");
								if(spatkStage<-6)
								{
									spatkStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.spatkStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s special attack increased by "+e.amount+" stages!");
								if(b.spatkStage>6)
								{
									b.spatkStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.spatkStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s special attack dropped by "+e.amount+" stages!");
								if(b.spatkStage<-6)
								{
									b.spatkStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "evasion":
						if(e.me)
						{
							if(e.up)
							{
								evsnStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s evasion increased by "+e.amount+" stages!");
								if(evsnStage>6)
								{
									evsnStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								evsnStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s evasion dropped by "+e.amount+" stages!");
								if(evsnStage<-6)
								{
									evsnStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.evsnStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s evasion increased by "+e.amount+" stages!");
								if(b.evsnStage>6)
								{
									b.evsnStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.evsnStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s evasion dropped by "+e.amount+" stages!");
								if(b.evsnStage<-6)
								{
									b.evsnStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "accuracy":
						if(e.me)
						{
							if(e.up)
							{
								accStage+=e.amount;
								OverworldGui.print(monster.nickname+"'s accuracy increased by "+e.amount+" stages!");
								if(accStage>6)
								{
									accStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else
							{
								accStage-=e.amount;
								OverworldGui.print(monster.nickname+"'s accuracy dropped by "+e.amount+" stages!");
								if(accStage<-6)
								{
									accStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
						}
						else
						{
							if(b.subHp>0)
								OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
							else if(e.up)
							{
								b.accStage+=e.amount;
								OverworldGui.print(b.monster.nickname+"'s accuracy increased by "+e.amount+" stages!");
								if(b.accStage>6)
								{
									b.accStage=6;
									OverworldGui.print("Although it maxed out at 6!");
								}
							}
							else if(b.canLower)
							{
								b.accStage-=e.amount;
								OverworldGui.print(b.monster.nickname+"'s accuracy dropped by "+e.amount+" stages!");
								if(b.accStage<-6)
								{
									b.accStage=-6;
									OverworldGui.print("Although it bottomed out at -6!");
								}
							}
							else
								OverworldGui.print(b.monster.nickname+"'s stats cannot be lowered!");
						}
						break;
					case "crit_chance":
						critMul*=e.amount;
						OverworldGui.print(b.monster.nickname+" is now "+e.amount+" times more likely to get a critical hit!");
						break;
					case "POISON":
						if(b.monster.status.isEmpty()&&!b.hasType("Poison")&&b.subHp==0)
						{
							b.monster.status="POISONED";
							OverworldGui.print(b.monster.nickname+" was poisoned!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot be poisoned!");
						break;
					case "TOXIC":
						if(b.monster.status.isEmpty()&&!b.hasType("Poison")&&b.subHp==0)
						{
							b.monster.status="POISONED";
							b.poisonDamage=1;
							OverworldGui.print(b.monster.nickname+" was poisoned!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot be poisoned!");
						break;
					case "BURN":
						if(b.monster.status.isEmpty()&&!b.hasType("Fire")&&b.subHp==0)
						{
							b.monster.status="BURNED";
							OverworldGui.print(b.monster.nickname+" was burned!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot be burned!");
						break;
					case "PARALYZE":
						if(b.monster.status.isEmpty()&&!b.hasType("Electric")&&b.subHp==0)
						{
							b.monster.status="PARALYZED";
							OverworldGui.print(b.monster.nickname+" was paralyzed!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot be paralyzed!");
						break;
					case "SLEEP":
						if(b.monster.status.isEmpty()&&b.subHp==0)
						{
							b.sTurns=2+(int)(Math.random()*4);
							b.monster.status="SLEEPING";
							OverworldGui.print(b.monster.nickname+" fell asleep!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot fall asleep!");
						break;
					case "FLINCH":
						b.flinched=true;
						break;
					case "FREEZE":
						if(b.monster.status.isEmpty()&&b.subHp==0)
						{
							b.sTurns=2+(int)(Math.random()*4);
							b.monster.status="FROZEN";
							OverworldGui.print(b.monster.nickname+" froze!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot be frozen!");
						break;
					case "CONFUSE":
						if(b.cTurns==0&&b.subHp==0)
						{
							b.cTurns=2+(int)(Math.random()*4);
							OverworldGui.print(b.monster.nickname+" is confused!");
						}
						else
							OverworldGui.print(b.monster.nickname+" cannot become confused!");
						break;
					case "HIT":
						int hits=e.amount+(int)(Math.random()*e.variation), d;
						if(b.subHp>0&&damage>b.subHp)
						{
							OverworldGui.print(b.monster.nickname+"'s substitute broke!");
							b.subHp=0;
							damage=0;
						}
						for(int j=1; j<hits; j++)
						{
							d=calcDamage(m, b);
							if(crit)
								OverworldGui.print("Critical hit!");
							OverworldGui.print("It did "+d+" damage!");
							damage+=d;
							if(b.subHp>0&&damage>b.subHp)
							{
								OverworldGui.print(b.monster.nickname+"'s substitute broke!");
								b.subHp=0;
								damage=0;
							}
						}
						break;
					case "CHARGE":
						if(m==RECHARGE)
							nextMove=null;
						else
						{
							nextMove=new Move(m, i+1);
							OverworldGui.print("But "+monster.nickname+" has to charge, so it did no damage...");
						}
						return true;
					case "BIDE":
						damage=(lastDamage[0]+lastDamage[1])*2;
						OverworldGui.print(monster.nickname+" unleashed "+damage+" points of damage!");
						break;
					case "END_DAMAGE":
						switch(e.stat)
						{
							case "TRAP":
								if(b.subHp==0&&b.tTurns==0)
								{
									OverworldGui.print(b.monster.nickname+" has been trapped!");
									b.tTurns=e.amount+(int)(Math.random()*e.variation);
								}
								break;
							case "LEECH_SEED":
								if(b.seeded)
									OverworldGui.print(b.monster.nickname+" is already seeded!");
								else if(b.subHp>0)
									OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
								else
								{
									b.seeded=true;
									OverworldGui.print(b.monster.nickname+" was seeded!");
								}
								break;
							default:
								throw new RuntimeException(e.stat);
						}
						break;
					case "CONVERT":
						types=new String[b.types.length];
						for(int j=0; j<types.length; j++)
							types[j]=b.types[j];
						OverworldGui.print(monster.nickname+" turned into a "+String.join("/", types)+" type pokemon!");
						break;
					case "IMMUNE":
						immune=true;
						nextMove=new Move(m, i+1);
						OverworldGui.print(monster.nickname+" became invincible!");
						return true;
					case "DISABLE":
						if(lastMove==null)
							OverworldGui.print(b.monster.nickname+" hasn't attacked you!");
						else if(b.subHp>0)
							OverworldGui.print("But "+b.monster.nickname+" has a substitute!");
						else if(b.dTurns>0)
							OverworldGui.print(b.monster.nickname+" is still disabled!");
						else if(b.disable(lastMove, e.amount, e.variation))
							OverworldGui.print(monster.nickname+" disabled "+lastMove.name+"!");
						else
							OverworldGui.print("But it failed!");
						break;
					case "SLEEPING":
						if(b.monster.status.equals("SLEEPING"))
							switch(e.stat)
							{
								case "damage":
									r=Math.max(1, (int)(e.modifier*damage));
									monster.hp=Math.min(monster.mhp, monster.hp+r);
									OverworldGui.print(monster.nickname+" ate "+r+" health!");
									break;
								default:
									throw new RuntimeException(e.stat);
							}
						else
						{
							OverworldGui.print("But your opponent isn't sleeping, so you did nothing!");
							return true;
						}
						break;
					case "RESET_STATS":
						resetStats();
						b.resetStats();
						OverworldGui.print("All stat changes were reset!");
						break;
					case "RECHARGE":
						nextMove=RECHARGE;
						break;
					case "SPDEF":
						if(spdefTurns>0)
							OverworldGui.print(monster.nickname+" already has a light screen up!");
						else
						{
							spdefTurns=e.variation;
							OverworldGui.print(monster.nickname+" doubled its team's special defense for "+spdefTurns+" turns!");
						}
						break;
					case "DEF":
						if(defTurns>0)
							OverworldGui.print(monster.nickname+" already has reflect up!");
						else
						{
							defTurns=e.variation;
							OverworldGui.print(monster.nickname+" doubled its team's defense for "+defTurns+" turns!");
						}
						break;
					case "RANDOM_MOVE":
						do
						{
							m=Move.MOVES[(int)(Math.random()*Move.MOVES.length)];
						} while(m.name.equals(m.name.toUpperCase()));
						doMove(m, b);
						break;
					case "LAST_OPPONENT_MOVE":
						if(lastMove==null)
							OverworldGui.print("But "+monster.nickname+" hasn't seen any attacks used yet!");
						else
						{
							for(r=0; moves[r]!=m; r++);
							moves[r]=lastMove;
							OverworldGui.print(monster.nickname+" learned "+lastMove.name+"!");
						}
						break;
					case "DISABLE_STAT_CHANGES":
						if(canLower)
						{
							canLower=false;
							OverworldGui.print(monster.nickname+"'s stats can no longer be lowered!");
						}
						else
							OverworldGui.print("But "+monster.nickname+" already has mist!");
						break;
					case "SCATTER_COINS":
						coins+=monster.level*2;
						OverworldGui.print(monster.nickname+" scattered coins everywhere!");
						break;
					case "CONTINUE":
						if(e.amount==0)
							break;
						nextMove=new Move(m);
						return b.setLastMove(m, damage);
					case "CONFUSE_ME":
						if(cTurns<=0)
						{
							cTurns=2+(int)(Math.random()*4);
							OverworldGui.print(monster.nickname+" became confused!");
						}
						break;
					case "RAGING":
						raging=true;
						break;
					case "SLEEP_FOR":
						monster.status="SLEEPING";
						poisonDamage=0;
						sTurns=2;
						OverworldGui.print(monster.nickname+" fell asleep!");
						break;
					case "MAKE_SUBSTITUTE":
						if(subHp>0)
						{
							OverworldGui.print("But "+monster.nickname+" already has a substitute!");
							break;
						}
						r=monster.mhp/4;
						if(r>=monster.hp)
							OverworldGui.print("But "+monster.nickname+" is too weak to create one!");
						else
						{
							monster.hp-=r;
							subHp=r;
							OverworldGui.print(monster.nickname+" created a substitute!");
						}
						break;
					case "TRANSFORM_INTO_OPPONENT":
						accStage=b.accStage;
						evsnStage=b.evsnStage;
						spdStage=b.spdStage;
						atkStage=b.atkStage;
						defStage=b.defStage;
						spatkStage=b.spatkStage;
						spdefStage=b.spdefStage;
						critMul=b.critMul;
						spd=b.spd;
						atk=b.atk;
						def=b.def;
						spatk=b.spatk;
						spdef=b.spdef;
						types=new String[b.types.length];
						for(int j=0; j<types.length; j++)
							types[j]=b.types[j];
						for(int j=0; j<moves.length; j++)
						{
							moves[j]=b.moves[j];
							monster.pp[j]=Math.max(monster.pp[j], 5);
						}
						OverworldGui.print(monster.nickname+" transformed into "+b.monster.nickname+"!");
						break;
					case "SWITCH":
					case "SWITCH_OPPONENT":
						OverworldGui.print("But it failed!");
						break;
					case "ATTACK_FIRST":
					case "ATTACK_SECOND":
					case "CRIT_CHANCE":
					case "MISS":
						break;
					default:
						throw new RuntimeException(e.effect);
			}
		}
		return b.setLastMove(m, damage);
	}
}
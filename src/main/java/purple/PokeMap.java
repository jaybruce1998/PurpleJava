package purple;

import java.util.*;
import java.io.*;
public class PokeMap
{
	/*Wall=rgb(255, 0, 0)/0, Floor=rgb(0, 255, 0)/1, Door=rgb(147, 56, 0)/2, Sign=rgb(0, 19, 0)/3, Water=rgb(0, 0, 255)/4,
	Ledge=rgb(181, 51, 0)/5, Tree=rgb(33, 51, 0)/6, Warp=rgb(0, 179, 255)/7, CaveFloor=rgb(255, 182, 0)/8,
	Mound=rgb(137, 255, 193)/9, LeftLedge=rgb(0, 255, 132)/10, RightLedge=rgb(255, 52, 0)/11, Grass=rgb(255, 255, 70)/12,
	HealPad=rgb(146, 255, 144)/13, Lefter=rgb(0, 0, 0)/14, Righter=rgb(0, 34, 0)/15, Upper=rgb(0, 107, 0)/16,
	Downer=rgb(0, 98, 43)/17, LeftDoor=rgb(148, 92, 0)/18, DetractableWall=rgb(123, 255, 0)/19, TrashCan=rgb(255, 255, 0)/20,
	SwitchSpot=rgb(0, 204, 226)/21*/
	static int[] TILE_TYPES={0, 8, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 7, 0, 0, 0, 0, 0, 0,
	1, 0, 0, 0, 0, 0, 0, 0, 1, 4, 4, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 1, 6, 2, 0, 0, 1, 0, 0, 0, 1, 0, 1, 2, 0, 7, 0, 7, 1, 0, 0, 0, 1, 0, 0, 0, 7, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 7, 1, 1, 0, 0, 0, 0, 20, 0, 9, 0, 0, 0, 0, 0, 0, 0, 8, 7, 0, 0, 8, 0, 0, 8, 0, 4, 0, 0, 0, 7, 0, 0, 2, 0, 0, 0, 0, 0, 1, 1, 7, 1, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1, 1, 1, 1, 1, 
	1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 2, 2, 4, 1, 4, 4, 0, 0, 0, 0, 1, 0, 7, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 1, 4, 4, 0, 
	0, 0, 0, 0, 10, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 7, 0, 1, 0, 0, 0, 0, 0, 0, 7, 0, 0, 7, 11, 0, 0, 0, 0, 3, 19, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 12, 0, 0, 0, 0, 0, 0, 0, 0, 
	0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 7, 8, 0, 0, 7, 0, 0, 0, 0, 0, 0, 13, 0, 1, 14, 16, 17, 15, 0, 0, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 18, 18, 0, 0, 0, 0, 0, 0, 0, 1, 4, 2, 2, 0, 7, 0, 0, 1, 0, 0, 12, 4, 4, 4, 4, 4, 0, 
	0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 12, 1, 1, 12, 12, 12, 4, 4, 18, 4, 1, 4, 18, 18, 1, 18, 4, 4, 0, 0, 0, 0, 0, 0, 7, 0, 0, 3, 0, 0, 0, 9, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 
	2, 2, 7, 7, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 7, 0, 0, 0, 1, 0, 1, 0, 0, 0, 4, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 21, 8, 0, 0, 0, 0, 0, 0,
	2, 18, 18, 2, 18, 18, 2, 18, 18, 2, 2, 18, 18, 2, 6, 2, 18, 2, 2, 12};
	static String[] CONNECTION_STRINGS="""
CeladonCity west Route16 8
CeladonCity east Route7 8
CeruleanCity north Route24 10
CeruleanCity south Route5 10
CeruleanCity west Route4 8
CeruleanCity east Route9 8
CinnabarIsland north Route21 0
CinnabarIsland east Route20 0
FuchsiaCity south Route19 10
FuchsiaCity west Route18 8
FuchsiaCity east Route15 8
IndigoPlateau south Route23 0
LavenderTown north Route10 0
LavenderTown south Route12 0
LavenderTown west Route8 0
PalletTown north Route1 0
PalletTown south Route21 0
PewterCity south Route2 10
PewterCity east Route3 8
Route1 north ViridianCity -10
Route1 south PalletTown 0
Route10 south LavenderTown 0
Route10 west Route9 0
Route11 west VermilionCity -8
Route11 east Route12 -54
Route12 north LavenderTown 0
Route12 south Route13 -40
Route12 west Route11 54
Route15 west FuchsiaCity -8
Route15 east Route14 -36
Route13 north Route12 40
Route13 west Route14 0
Route14 west Route15 36
Route14 east Route13 0
Route16 south Route17 0
Route16 east CeladonCity -8
Route18 north Route17 0
Route18 east FuchsiaCity -8
Route17 north Route16 0
Route17 south Route18 0
Route19 north FuchsiaCity -10
Route19 west Route20 36
Route2 north PewterCity -10
Route2 south ViridianCity -10
Route20 west CinnabarIsland 0
Route20 east Route19 -36
Route22 north Route23 0
Route22 east ViridianCity -8
Route21 north PalletTown 0
Route21 south CinnabarIsland 0
Route23 north IndigoPlateau 0
Route23 south Route22 0
Route24 south CeruleanCity -10
Route24 east Route25 0
Route25 west Route24 0
Route3 north Route4 50
Route3 west PewterCity -8
Route5 north CeruleanCity -10
Route5 south SaffronCity -10
Route4 south Route3 -50
Route4 east CeruleanCity -8
Route6 north SaffronCity -10
Route6 south VermilionCity -10
Route7 west CeladonCity -8
Route7 east SaffronCity -8
Route8 west SaffronCity -8
Route8 east LavenderTown 0
Route9 west CeruleanCity -8
Route9 east Route10 0
SaffronCity north Route5 10
SaffronCity south Route6 10
SaffronCity west Route7 8
SaffronCity east Route8 8
VermilionCity north Route6 10
VermilionCity east Route11 8
ViridianCity north Route2 10
ViridianCity south Route1 10
ViridianCity west Route22 8""".split("\n");
	static Map<String, PokeMap> POKEMAPS=new HashMap<>();
	public static void buildPokeMaps()
	{
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(
					Main.class.getResourceAsStream("/tileIds.txt")))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				int space = line.indexOf(" ");
				String name = line.substring(0, space);
				POKEMAPS.put(name, new PokeMap(name, line.substring(space + 1), name.endsWith("Pokecenter") ? 3 : -2));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("AHH!");
		}
		POKEMAPS.get("RedsHouse1F").setHeal(5);
		POKEMAPS.get("IndigoPlateauLobby").setHeal(7);
		for(String s: CONNECTION_STRINGS)
		{
			String[] a=s.split(" ");
			POKEMAPS.get(a[0]).addConnection(a[1].charAt(0), POKEMAPS.get(a[2]), Integer.parseInt(a[3]));
		}
	}
	
	int[][] grid, types;
	PokeMap north, south, east, west;
	int nOff, sOff, eOff, wOff, healX, healY;
	String name;
	Map<Integer, Warp> warps;
	Map<String, Encounter[]> encounters;
	Trainer[][] trainers;
	int[][] sight;
	WorldObject[][] wob;
	Npc[][] npcs;
	Map<Integer, MartItem[]> martMap;
	public PokeMap(String name, String s, int n)
	{
		this.name=name;
		warps=new HashMap<>();
		encounters=new HashMap<>();
		String[] a=s.split(";"), c=a[0].split(",");
		grid=new int[a.length][c.length];
		int l=a.length-1;
		for(int i=0; i<l;)
		{
			for(int j=0; j<c.length; j++)
				grid[i][j]=Integer.parseInt(c[j]);
			c=a[++i].split(",");
		}
		for(int j=0; j<c.length; j++)
			grid[l][j]=Integer.parseInt(c[j]);
		types=new int[a.length][c.length];
		sight=new int[a.length][c.length];
		for(int i=0; i<a.length; i++)
			for(int j=0; j<c.length; j++)
				types[i][j]=TILE_TYPES[grid[i][j]];
		trainers=new Trainer[a.length][c.length];
		wob=new WorldObject[a.length][c.length];
		npcs=new Npc[a.length][c.length];
		martMap=new HashMap<>();
		healX=n;
		healY=n;
	}
	public Trainer getTrainer(int x, int y, int i)
	{
		if(trainers[y][x]!=null)
			return trainers[y][x];
		for(int r=y-1; r>=0; r--)
			if(sight[r][x]!=i)
				break;
			else if(trainers[r][x]!=null)
				return trainers[r][x];
		for(int r=y+1; r<trainers.length; r++)
			if(sight[r][x]!=i)
				break;
			else if(trainers[r][x]!=null)
				return trainers[r][x];
		for(int c=x-1; c>=0; c--)
			if(sight[y][c]!=i)
				break;
			else if(trainers[y][c]!=null)
				return trainers[y][c];
		for(int c=x+1; c<trainers[y].length; c++)
			if(sight[y][c]!=i)
				break;
			else if(trainers[y][c]!=null)
				return trainers[y][c];
		sight[y][x]=0;
		return null;
	}
	public void deleteTrainer(Trainer t, int i)
	{
		int y=t.y, x=t.x;
		trainers[y][x]=null;
		sight[y][x]=0;
		for(int r=y-1; r>=0; r--)
			if(sight[r][x]!=i)
				break;
			else
				sight[r][x]=0;
		for(int r=y+1; r<trainers.length; r++)
			if(sight[r][x]!=i)
				break;
			else
				sight[r][x]=0;
		for(int c=x-1; c>=0; c--)
			if(sight[y][c]!=i)
				break;
			else
				sight[y][c]=0;
		for(int c=x+1; c<trainers[y].length; c++)
			if(sight[y][c]!=i)
				break;
			else
				sight[y][c]=0;
	}
	public void addConnection(char d, PokeMap pm, int off)
	{
		switch(d)
		{
			case 'n':
				north=pm;
				nOff=off;
				break;
			case 's':
				south=pm;
				sOff=off;
				break;
			case 'e':
				east=pm;
				eOff=off;
				break;
			case 'w':
				west=pm;
				wOff=off;
				break;
			default:
				throw new RuntimeException("wtf is "+d+"?!");
		}
	}
	public void addWarp(int row, int col, Warp w)
	{
		warps.put(row*grid[0].length+col, w);
	}
	public Warp getWarp(int row, int col)
	{
		return warps.get(row*grid[0].length+col);
	}
	public MartItem[] getMartItems(int x, int y)
	{
		return martMap.get(x*grid[0].length+y);
	}
	public Warp getNearbyWarp(int row, int col)
	{
		Warp w=getWarp(row-1, col);
		if(w==null)
		{
			w=getWarp(row+1, col);
			if(w==null)
			{
				w=getWarp(row, col-1);
				if(w==null)
					w=getWarp(row, col+1);
			}
		}
		return w;
	}
	public void addEncounters(String[] a)
	{
		for(int i=1; i<a.length; i++)
		{
			String[] es=a[i].split(",");
			Encounter[] e=new Encounter[es.length-1];
			for(int j=0; j<e.length; j++)
				e[j]=new Encounter(es[j+1]);
			encounters.put(es[0], e);
		}
	}
	public Battler getRandomEncounter(String type)
	{
		Encounter[] a=encounters.get(type);
		if(a==null)
			return null;
		int r=(int)(Math.random()*100), l=0;
		for(Encounter e: a)
		{
			int n=l+e.chance;
			if(r<n)
				return Math.random()<0.5?new Battler(e.rLevel, e.rMonster):new Battler(e.bLevel, e.bMonster);
			else
				l=n;
		}
		throw new RuntimeException("Could not find "+r+" in "+Arrays.toString(a));
	}
	private void fillSight(int v, int y, int x, int dy, int dx)
	{
		for(int i=0; i<4; i++)
		{
			y+=dy;
			if(y<0||y>=grid.length)
				return;
			x+=dx;
			if(x<0||x>=grid[0].length||sight[y][x]!=0)
				return;
			switch(types[y][x])
			{
				case 0:
				case 3:
				case 5:
				case 10:
				case 11:
				case 19:
				case 20:
					return;
				default:
					sight[y][x]=v;
			}
		}
	}
	public void addTrainer(String s, int i)
	{
		Trainer t=new Trainer(s);
		trainers[t.y][t.x]=t;
		sight[t.y][t.x]=i;
		switch(t.dir)
		{
			case 1:
				fillSight(i, t.y, t.x, 1, 0);
				break;
			case 4:
				fillSight(i, t.y, t.x, -1, 0);
				break;
			case 6:
				fillSight(i, t.y, t.x, 0, -1);
				break;
			case 8:
				fillSight(i, t.y, t.x, 0, 1);
				break;
			default:
				throw new RuntimeException(t.dir+" BRO WHAT?!");
		}
		t.id=Trainer.tid++;
		t.phrases=NpcStrings.TRAINER_SPEECH_STRINGS[t.id].split(";");
	}
	public void addTrainers(String[] a)
	{
		for(int i=1; i<a.length; i++)
			addTrainer(a[i], i);
	}
	public void addLeader(int i, String[] a)
	{
		Trainer t=new Trainer.Leader(i, a);
		trainers[t.y][t.x]=t;
		sight[t.y][t.x]=-2;
		t.phrases=NpcStrings.LEADER_SPEECH_STRINGS[i].split(";");
	}
	public void addGioRival(int i, String s)
	{
		Trainer t=new Trainer.GioRival(i, s);
		trainers[t.y][t.x]=t;
		sight[t.y][t.x]=-2;
		t.phrases=NpcStrings.GIO_RIVAL_SPEECH_STRINGS[i].split(";");
	}
	public void addE4Trainer(int id, String s, String p)
	{
		Trainer t=new Trainer.E4Trainer(id, s);
		trainers[t.y][t.x]=t;
		sight[t.y][t.x]=-2;
		t.phrases=p.split(";");
	}
	public void addItem(String s)
	{
		WorldObject wi=new WorldObject(s);
		wob[wi.y][wi.x]=wi;
	}
	public void addItems(String[] a)
	{
		for(int i=1; i<a.length; i++)
			addItem(a[i]);
	}
	public void addEncounter(String s)
	{
		WorldObject.WorldEncounter we=new WorldObject.WorldEncounter(s);
		wob[we.y][we.x]=we;
	}
	public void addSnorlaxEncounter(String s)
	{
		WorldObject.SnorlaxEncounter se=new WorldObject.SnorlaxEncounter(s);
		wob[se.y][se.x]=se;
	}
	public void addGiver(String s, String q)
	{
		Giver g=new Giver(s, q);
		npcs[g.y][g.x]=g;
	}
	public void addGiver(String s, int n, String q)
	{
		Giver.Aide g=new Giver.Aide(s, n, q);
		npcs[g.y][g.x]=g;
	}
	public void addGiver(String s, String i, String q)
	{
		Giver.IfGiver g=new Giver.IfGiver(s, i, q);
		npcs[g.y][g.x]=g;
	}
	public void addMartItems(String[] a)
	{
		MartItem[] mi=new MartItem[a.length-2];
		for(int i=2; i<a.length; i++)
			mi[i-2]=new MartItem(a[i]);
		String[] c=a[1].split(" ");
		martMap.put(Integer.parseInt(c[0])*grid[0].length+Integer.parseInt(c[1]), mi);
	}
	public void addBlocker(String s, String q)
	{
		Blocker b=new Blocker(s, q);
		npcs[b.y][b.x]=b;
	}
	public void addBlocker(String s, String q, int n)
	{
		Blocker b=new Blocker(s, q, n);
		npcs[b.y][b.x]=b;
	}
	public void setHeal(int n)
	{
		healX=n;
		healY=n;
	}
	public void addNpc(String s, String q)
	{
		Npc n=new Npc(s, q);
		npcs[n.y][n.x]=n;
	}
	public void addTrader(String s, String p)
	{
		Trader t=new Trader(s, p);
		npcs[t.y][t.x]=t;
	}
	public void stepOn(Player p, int x, int y)
	{
		p.objectsCollected[wob[y][x].id]=true;
		wob[y][x]=null;
		if(wob[y][x-1]!=null)
		{
			p.objectsCollected[wob[y][x-1].id]=true;
			wob[y][x-1]=null;
		}
		if(wob[y][x+1]!=null)
		{
			p.objectsCollected[wob[y][x+1].id]=true;
			wob[y][x+1]=null;
		}
	}
	public String toString()
	{
		return grid.length+","+grid[0].length;
	}
}
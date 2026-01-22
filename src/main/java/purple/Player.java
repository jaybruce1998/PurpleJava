package purple;

import java.util.*;
import javax.swing.JOptionPane;
public class Player
{
	private static final Item MASTER_BALL=Item.ITEM_MAP.get("Master Ball");
	Set<Move> tmHms;
	List<Item> items;
	int numCaught, money;
	boolean[] trainersBeaten, leadersBeaten, gioRivalsBeaten, objectsCollected, pokedex;
	Set<Item> seenItems;
	Battler[] team;
	List<Battler> pc;
	String name;
	boolean ballin;
	public Player(String name)
	{
		tmHms=new HashSet<>();
		items=new ArrayList<>();
		trainersBeaten=new boolean[Trainer.tid];
		leadersBeaten=new boolean[9];
		gioRivalsBeaten=new boolean[9];
		objectsCollected=new boolean[WorldObject.wid];
		pokedex=new boolean[152];
		team=new Battler[6];
		pc=new ArrayList<>();
		seenItems=new HashSet<>();
		ballin=true;
		this.name=name;
	}
	public Player(String pcS, String partyS, String dexS, String itemS, String tmS)
	{
		pc=new ArrayList<>();
		if(pcS.length()>0)
			for(String p: pcS.split(";"))
				pc.add(new Battler(p));
		team=new Battler[6];
		String[] a=partyS.split(";");
		for(int i=0; i<a.length; i++)
			team[i]=new Battler(a[i]);
		pokedex=new boolean[dexS.length()];
		for(int i=0; i<pokedex.length; i++)
			if(dexS.charAt(i)=='1')
				pokedex[i]=true;
		items=new ArrayList<>();
		seenItems=new HashSet<>();
		if(itemS.length()>0)
			for(String s: itemS.split(";"))
			{
				a=s.split(",");
				Item i=Item.ITEM_MAP.get(a[0]);
				seenItems.add(i);
				i=new Item(i);
				i.quantity=Integer.parseInt(a[1]);
				items.add(i);
			}
		tmHms=new HashSet<>();
		a=tmS.split(",");
		for(int i=1; i<a.length; i++)
			tmHms.add(Move.MOVE_MAP.get(a[i]));
	}
	public boolean give(Move move)
	{
		if(tmHms.add(move))
		{
			OverworldGui.print("You got "+move.name+"!");
			return true;
		}
		OverworldGui.print("You already have "+move.name+"!");
		return false;
	}
	public void give(Item item)
	{
		if(item==MASTER_BALL)
			ballin=false;
		for(Item i: items)
			if(i.name.equals(item.name))
			{
				i.quantity+=item.quantity;
				OverworldGui.print("You got "+item.name+"!");
				return;
			}
		items.add(new Item(item));
		seenItems.add(item);
		OverworldGui.print("You got "+item.name+"!");
	}
	public void give(Item item, int n)
	{
		for(Item i: items)
			if(i.name.equals(item.name))
			{
				i.quantity+=n;
				return;
			}
		Item i=new Item(item);
		i.quantity=n;
		items.add(i);
		seenItems.add(item);
	}
	public void register(Battler battler)
	{
		if(!pokedex[battler.dexNum])
		{
			pokedex[battler.dexNum]=true;
			numCaught++;
			OverworldGui.print("That's a new species!");
		}
	}
	public void give(Battler battler)
	{
		String nick=JOptionPane.showInputDialog("What is "+battler.name+"'s new nickname?");
		if(nick!=null&&nick.length()>0)
			battler.nickname=nick.length()<11?nick:nick.substring(0, 10);
		OverworldGui.print("You got "+battler.nickname+"!");
		if(team[5]!=null)
		{
			pc.add(battler);
			OverworldGui.print(battler.nickname+" was added to the PC!");
		}
		else
			for(int i=0; i<team.length; i++)
				if(team[i]==null)
				{
					team[i]=battler;
					break;
				}
		register(battler);
	}
	public boolean hasItem(Item item)
	{
		return seenItems.contains(item);
	}
	public boolean hasMove(Move move)
	{
		return tmHms.contains(move);
	}
	public void healTeam()
	{
		for(int i=0; i<team.length; i++)
			if(team[i]==null)
				return;
			else
				team[i].fullyHeal();
	}
	public void sell(Item item, int q)
	{
		q=Math.min(q, item.quantity);
		money+=item.price/2*q;
		item.quantity-=q;
		if(item.quantity==0)
			items.remove(item);
	}
	public void use(Item item)
	{
		if(--item.quantity<=0)
			items.remove(item);
	}
}
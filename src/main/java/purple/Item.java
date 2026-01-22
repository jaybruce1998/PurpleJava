package purple;

import java.util.*;
public class Item
{
	static String[] ITEM_STRINGS="""
Antidote,100
Awakening,250
Bicycle,-1
Bike Voucher,-1
Burn Heal,250
Calcium,9800
Carbos,9800
Card Key,-1
Coin,50
Coin Case,-1
Dire Hit,1000
Dome Fossil,-1
Elixir,10000
Escape Rope,550
Ether,2500
Exp. All,-1
Fire Stone,2100
Fresh Water,200
Full Heal,600
Full Restore,3000
Gold Teeth,-1
Good Rod,-1
Great Ball,600
Guard Spec.,700
Helix Fossil,-1
HP Up,9800
Hyper Potion,1500
Ice Heal,250
Iron,9800
Item Finder,-1
Leaf Stone,2100
Lemonade,350
Lift Key,-1
Master Ball,10000
Max Elixir,20000
Max Ether,5000
Max Potion,2500
Max Repel,700
Max Revive,10000
Moon Stone,-1
Nugget,10000
Oak's Parcel,-1
Old Amber,-1
Old Rod,-1
Paralyze Heal,200
PokeBall,200
Pokedex,-1
Pokedoll,1000
Pokeflute,-1
Potion,300
PP Up,10000
Protein,9800
Rare Candy,10000
Repel,350
Revive,1500
SS Ticket,-1
Safari Ball (30),1000
Secret Key,-1
Silph Scope,-1
Soda Pop,300
Super Potion,700
Super Repel,500
Super Rod,-1
Thunder Stone,2100
Town Map,-1
Ultra Ball,1200
Water Stone,2100
X Accuracy,950
X Attack,500
X Defend,550
X Special Attack,350
X Special Defend,650
X Speed,350
Amulet Coin,-1
Lucky Egg,-1
Shiny Charm,-1
Fake ID,-1
...Secret Sauce,-1
Key to Darkness,-1
Zinc,9800""".split("\n");
	static Map<String, Item> ITEM_MAP=new HashMap<>();
	public static void buildItems()
	{
		for(String i: ITEM_STRINGS)
		{
			String[] a=i.split(",");
			ITEM_MAP.put(a[0], new Item(a));
		}
	}
	
	String name;
	int price, quantity;
	boolean heal, battle, wild, world;
	public Item(String[] a)
	{
		name=a[0];
		switch(name)
		{
			case "Antidote":
			case "Awakening":
			case "Fresh Water":
			case "Lemonade":
			case "Soda Pop":
				heal=true;
				break;
			default:
				heal=name.endsWith("Heal")||name.endsWith("Ether")||name.endsWith("Restore")||name.endsWith("Elixir")||name.endsWith("Potion")||name.endsWith("Revive");
		}
		battle=heal||name.startsWith("X")||name.equals("Dire Hit")||name.equals("Guard Spec.")||name.equals("Poke Doll");
		wild=battle||name.endsWith("Ball");
		switch(name)
		{
			case "Calcium":
			case "Carbos":
			case "Escape Rope":
			case "Iron":
			case "Item Finder":
			case "Protein":
			case "Town Map":
			case "Rare Candy":
				world=true;
				break;
			default:
				world=heal||name.endsWith("Stone")||name.endsWith("Rod")||name.endsWith("Repel")||name.endsWith("Up");
		}
		price=Integer.parseInt(a[1]);
		quantity=1;
	}
	public Item(Item i)
	{
		name=i.name;
		price=i.price;
		quantity=i.quantity;
		heal=i.heal;
		battle=i.battle;
		wild=i.wild;
		world=i.world;
	}
	public String toString()
	{
		return name+" x"+quantity;
	}
}
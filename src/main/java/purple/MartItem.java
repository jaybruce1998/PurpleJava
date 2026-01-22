package purple;

public class MartItem
{
	static String[] MART_ITEM_STRINGS="""
ViridianMart,2 5,PokeBall,Antidote,Paralyze Heal,Burn Heal
ViridianMart,0 7,PokeBall,Antidote,Paralyze Heal,Burn Heal
PewterMart,2 5,PokeBall,Potion,Escape Rope,Antidote,Burn Heal,Awakening,Paralyze Heal,Repel
PewterMart,0 7,PokeBall,Potion,Escape Rope,Antidote,Burn Heal,Awakening,Paralyze Heal,Repel
CeruleanMart,2 5,PokeBall,Potion,Repel,Antidote,Burn Heal,Awakening,Paralyze Heal
CeruleanMart,0 7,PokeBall,Potion,Repel,Antidote,Burn Heal,Awakening,Paralyze Heal
VermilionMart,2 5,PokeBall,Super Potion,Ice Heal,Awakening,Paralyze Heal,Repel
VermilionMart,0 7,PokeBall,Super Potion,Ice Heal,Awakening,Paralyze Heal,Repel
LavenderTown,2 5,Great Ball,Super Potion,Revive,Escape Rope,Super Repel,Antidote,Burn Heal,Ice Heal,Paralyze Heal
LavenderTown,0 7,Great Ball,Super Potion,Revive,Escape Rope,Super Repel,Antidote,Burn Heal,Ice Heal,Paralyze Heal
CeladonMart2F,3 3,Great Ball,Super Potion,Revive,Super Repel,Antidote,Burn Heal,Ice Heal,Awakening,Paralyze Heal
CeladonMart2F,5 5,Great Ball,Super Potion,Revive,Super Repel,Antidote,Burn Heal,Ice Heal,Awakening,Paralyze Heal
CeladonMart2F,6 5,Move=Double Team 1000,Move=Reflect 1000,Move=Razor Wind 2000,Move=Horn Drill 2000,Move=Egg Bomb 2000,Move=Mega Punch 3000,Move=Mega Kick 3000,Move=Take Down 3000,Move=Submission 3000
CeladonMart2F,8 3,Move=Double Team 1000,Move=Reflect 1000,Move=Razor Wind 2000,Move=Horn Drill 2000,Move=Egg Bomb 2000,Move=Mega Punch 3000,Move=Mega Kick 3000,Move=Take Down 3000,Move=Submission 3000
CeladonMart4F,5 5,Pokedoll,Fire Stone,Thunder Stone,Water Stone,Leaf Stone
CeladonMart5F,3 3,X Accuracy,Guard Spec.,Dire Hit,X Attack,X Defend,X Speed,X Special Attack,X Special Defend
CeladonMart5F,5 5,X Accuracy,Guard Spec.,Dire Hit,X Attack,X Defend,X Speed,X Special Attack,X Special Defend
CeladonMart5F,6 5,HP Up,Protein,Iron,Carbos,Calcium,Zinc
CeladonMart5F,8 3,HP Up,Protein,Iron,Carbos,Calcium,Zinc
CeladonMartRoof,10 2,Fresh Water,Lemonade,Soda Pop
CeladonMartRoof,11 2,Fresh Water,Lemonade,Soda Pop
CeladonMartRoof,12 3,Fresh Water,Lemonade,Soda Pop
GameCornerPrizeRoom,2 3,Pokemon=Abra 4600,Pokemon=Vulpix 20000,Pokemon=Wigglytuff 53600
GameCornerPrizeRoom,4 3,Pokemon=Scyther 130000,Pokemon=Pinsir 130000,Pokemon=Porygon 200000
GameCornerPrizeRoom,6 3,Move=Solar Beam 66000,Move=Hyper Beam 110000,Move=Substitute 154000
SaffronMart,2 5,Great Ball,Hyper Potion,Max Repel,Escape Rope,Full Heal,Revive
SaffronMart,0 7,Great Ball,Hyper Potion,Max Repel,Escape Rope,Full Heal,Revive
FuchsiaMart,2 5,Ultra Ball,Great Ball,Super Potion,Revive,Full Heal,Super Repel,Move=Big Money 2500
FuchsiaMart,0 7,Ultra Ball,Great Ball,Super Potion,Revive,Full Heal,Super Repel,Move=Big Money 2500
CinnabarMart,2 5,Ultra Ball,Great Ball,Hyper Potion,Max Repel,Escape Rope,Full Heal,Revive
CinnabarMart,0 7,Ultra Ball,Great Ball,Hyper Potion,Max Repel,Escape Rope,Full Heal,Revive
IndigoPlateauLobby,2 5,Ultra Ball,Great Ball,Full Restore,Max Potion,Full Heal,Revive,Max Repel
IndigoPlateauLobby,0 7,Ultra Ball,Great Ball,Full Restore,Max Potion,Full Heal,Revive,Max Repel""".split("\n");
	public static void buildMartItems()
	{
		for(String s: MART_ITEM_STRINGS)
		{
			String[] a=s.split(",");
			PokeMap.POKEMAPS.get(a[0]).addMartItems(a);
		}
	}
	
	Move move;
	Item item;
	Monster mon;
	int price;
	public MartItem(String s)
	{
		if(s.startsWith("Move"))
		{
			int i=s.lastIndexOf(" ");
			move=Move.MOVE_MAP.get(s.substring(5, i));
			price=Integer.parseInt(s.substring(i+1));
		}
		else if(s.startsWith("Pokem"))
		{
			int i=s.lastIndexOf(" ");
			mon=Monster.MONSTER_MAP.get(s.substring(8, i));
			price=Integer.parseInt(s.substring(i+1));
		}
		else
		{
			item=Item.ITEM_MAP.get(s);
			price=item.price;
		}
	}
	public String toString()
	{
		return (move!=null?move.name:item!=null?item.name:mon.name)+" "+price;
	}
}
package purple;

public class Trader extends Giver
{
	static String[] DEX_STRINGS="""
BluesHouse;POKEDEX 3 3 DOWN
MrFujisHouse;POKEDEX 3 3 DOWN
PokemonMansion2F;POKEDEX 18 2 DOWN
PokemonMansion2F;POKEDEX 3 22 DOWN
PokemonMansion3F;POKEDEX 6 12 DOWN
PokemonMansionB1F;POKEDEX 16 20 DOWN""".split("\n");
	static String[] CLIPBOARD_STRINGS="""
MtMoonPokecenter;CLIPBOARD 7 2 DOWN
SilphCo5F;CLIPBOARD 22 12 DOWN
SilphCo5F;CLIPBOARD 25 10 DOWN
SilphCo5F;CLIPBOARD 24 6 DOWN
ViridianNicknameHouse;CLIPBOARD 4 0 DOWN""".split("\n");
	static String[] MONSTER_STRINGS="""
CeladonCity;MONSTER 30 12 RIGHT
CeladonMansion1F;MONSTER 0 5 RIGHT
CeladonMansion1F;MONSTER 4 4 UP
CeruleanCity;MONSTER 28 26 DOWN
CopycatsHouse2F;MONSTER 5 1 DOWN
FuchsiaCity;MONSTER 30 12 RIGHT
LavenderCuboneHouse;MONSTER 3 5 UP
MrFujisHouse;MONSTER 1 3 DOWN
PewterNidoranHouse;MONSTER 4 5 LEFT
SSAnneB1FRooms;MONSTER 21 2 DOWN
VermilionCity;MONSTER 29 9 UP""".split("\n");
	static String[] FAIRY_STRINGS="""
CeladonMansion1F;FAIRY 1 8 RIGHT
CopycatsHouse1F;FAIRY 1 4 UP
CopycatsHouse2F;FAIRY 1 6 RIGHT
FuchsiaCity;FAIRY 31 5 DOWN
PewterPokecenter;FAIRY 1 3 DOWN
SSAnne1FRooms;FAIRY 13 4 DOWN""".split("\n");
	static String[] BIRD_STRINGS="""
CopycatsHouse2F;BIRD 4 6 RIGHT
CopycatsHouse2F;BIRD 2 0 DOWN
Route16FlyHouse;BIRD 6 4 DOWN
SaffronCity;BIRD 31 12 DOWN
SaffronPidgeyHouse;BIRD 0 4 UP
VermilionPidgeyHouse;BIRD 3 5 RIGHT
ViridianNicknameHouse;BIRD 5 5 RIGHT""".split("\n");
	static String[] TRADER_STRINGS="""
CeruleanTradeHouse;GRANNY 5 4 LEFT,null;Kadabra,Golem
CeruleanTradeHouse;GAMBLER 1 2 DOWN,null;Machoke,Alakazam
CinnabarLabTradeRoom;SUPER_NERD 3 2 DOWN,Old Amber;null,Aerodactyl
CinnabarLabTradeRoom;GRAMPS 1 4 DOWN,Dome Fossil;null,Kabuto
CinnabarLabTradeRoom;BEAUTY 5 5 UP,Helix Fossil;null,Omanyte
Route2TradeHouse;SCIENTIST 2 4 RIGHT,null;Kabutops,Omanyte
Route2TradeHouse;GAMEBOY_KID 4 1 DOWN,null;Omastar,Kabuto
VermilionTradeHouse;LITTLE_GIRL 3 5 UP,null;Graveler,Gengar
VermilionPidgeyHouse;YOUNGSTER 5 3 LEFT,null;Haunter,Machamp
SilphCo5F;SILPH_WORKER_M 13 9 DOWN,Gold Teeth;null,Lapras
SilphCo10F;SILPH_WORKER_F 9 15 DOWN,null;Venusaur,Charmander
SaffronMart;SUPER_NERD 4 2 DOWN,null;Blastoise,Bulbasaur
VermilionPokecenter;LINK_RECEPTIONIST 11 2 UP,null;Charizard,Squirtle
SSAnne3F;SAILOR 9 3 RIGHT,null;Chansey,Farfetch'd
Route20;SWIMMER 68 11 UP,null;Tauros,Mr. Mime
PokemonTower2F;CHANNELER 3 7 RIGHT,null;Porygon,Lickitung
Route3;SUPER_NERD 57 11 DOWN,null;Moltres,Jynx
MrFujisHouse;MONSTER 6 4 UP,null;Articuno,Hitmonchan
PokemonFanClub;FAIRY 6 4 LEFT,null;Zapdos,Hitmonlee""".split("\n");
	public static void buildTraders()
	{
		for(String s: DEX_STRINGS)
		{
			String[] a=s.split(";");
			PokeMap.POKEMAPS.get(a[0]).addNpc(a[1], "Oh wow, another pokedex! Nifty.");
		}
		for(String s: CLIPBOARD_STRINGS)
		{
			String[] a=s.split(";");
			PokeMap.POKEMAPS.get(a[0]).addNpc(a[1], "Wait, I can't read!");
		}
		for(String s: MONSTER_STRINGS)
		{
			String[] a=s.split(";");
			PokeMap.POKEMAPS.get(a[0]).addNpc(a[1], "Meow!");
		}
		for(String s: FAIRY_STRINGS)
		{
			String[] a=s.split(";");
			PokeMap.POKEMAPS.get(a[0]).addNpc(a[1], "Hello...");
		}
		for(String s: BIRD_STRINGS)
		{
			String[] a=s.split(";");
			PokeMap.POKEMAPS.get(a[0]).addNpc(a[1], "SCREECH!");
		}
		for(String s: TRADER_STRINGS)
		{
			String[] a=s.split(";");
			PokeMap.POKEMAPS.get(a[0]).addTrader(a[1], a[2]);
		}
	}
	
	String monA;
	Monster monB;
	public Trader(String s, String p)
	{
		super(s+",null", "");
		String[] a=p.split(",");
		monA=a[0];
		monB=Monster.MONSTER_MAP.get(a[1]);
	}
	public void interact(Player p)
	{
		if(item==null)
		{
			if(p.team[0].name.equals(monA))
			{
				for(int i=1; i<6; i++)
					p.team[i-1]=p.team[i];
				p.team[5]=null;
				p.give(new Battler(1, monB));
				OverworldGui.print("Yoink!");
			}
			else
				OverworldGui.print("Don't talk to me unless you're leading with "+monA+"!");
		}
		else if(p.hasItem(item))
		{
			p.use(item);
			p.give(new Battler(1, monB));
			OverworldGui.print("Yoink!");
		}
		else
			OverworldGui.print("Don't talk to me unless you have "+item.name+"!");
	}
}
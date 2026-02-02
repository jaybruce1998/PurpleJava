package purple;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class Giver
{
	static String[] GIVER_STRINGS="""
Route2Gate;SCIENTIST 1 4 LEFT,Move=Flash;10
Route11;SCIENTIST 2 6 DOWN,Amulet Coin;30
Route15Gate2F;SCIENTIST 4 2 DOWN,Lucky Egg;50
HallOfFame;OAK 5 2 DOWN,Shiny Charm;152
RocketHideoutB1F;BIKE_SHOP_CLERK 3 17 UP,Key to Darkness;150
OaksLab;OAK 5 2 DOWN,Pokedex;...Secret Sauce
BikeShop;YOUNGSTER 1 3 UP,Bicycle;Bike Voucher
WardensHouse;WARDEN 2 3 DOWN,Move=Strength;Gold Teeth
VermilionOldRodHouse;FISHING_GURU 2 4 RIGHT,Old Rod
FuchsiaGoodRodHouse;FISHING_GURU 5 3 RIGHT,Good Rod
Route12SuperRodHouse;FISHING_GURU 2 4 RIGHT,Super Rod
Route12Gate2F;BRUNETTE_GIRL 3 4 UP,Move=Swift
CeruleanTrashedHouse;GIRL 5 6 RIGHT,Move=Dig
CeladonCity;GRAMPS 22 16 DOWN,Move=Soft-Boiled
MrPsychicsHouse;FISHING_GURU 5 3 LEFT,Move=Psychic
CopycatsHouse2F;BRUNETTE_GIRL 4 3 DOWN,Move=Mimic
SilphCo2F;SILPH_WORKER_F 10 1 UP,Move=Self-Destruct
SSAnneCaptainsRoom;CAPTAIN 4 2 UP,Move=Cut
Route16FlyHouse;BRUNETTE_GIRL 2 3 RIGHT,Move=Fly
SafariZoneSecretHouse;FISHING_GURU 3 3 DOWN,Move=Surf
BillsHouse;MONSTER 6 5 DOWN,SS Ticket
CeladonDiner;MIDDLE_AGED_MAN 1 4 DOWN,Fake ID
PokemonFanClub;GENTLEMAN 3 1 DOWN,Bike Voucher
ViridianCity;FISHER 6 23 DOWN,Move=Dream Eater
ViridianCity;GAMBLER_ASLEEP 18 9 DOWN,...Secret Sauce
SilphCo11F;SILPH_PRESIDENT 7 5 DOWN,Master Ball
PokemonTower7F;MR_FUJI 10 3 DOWN,Pokeflute""".split("\n");
	static String[] GIVER_QUOTE_STRINGS="""
Hey there! You haven't caught many pokemon, huh?;Howdy, what do you think of this?... what, you don't like being flashed? Well now you can flash, too!;Have fun flashing all of your friends!
Oh, it's you! But... you haven't done much pokemon catching.;Hey, you're pretty poor! I bet this can help you out!;Just you wait, you'll be loaded in no time!
Hmm, I wonder if there are any PokeLovers around here...;Oh! Well you're... so weak. Take this, it should help!;Oh yeah, now you can get strong like me!
Congratulations!... although your pokedex could use some work. Go catch more pokemon!;...heh, you found the forbidden one, huh? I guess you're worthy of this, then.;Seriously though, congratulations! And thanks for saving the world, that beast should serve you well.
Oh, you seem lost... this is the source of the world's voids, I'm sure you've seen them. But you are still so weak.;Maybe you can help.;...although we're probably doomed.
Oh bother, if only I had some secret sauce...;Oh, is that secret sauce?! This will go great with my PokePasta! Here, take this.;Huh, tastes a bit... sour...
Dangit, how am I ever gonna afford a bike here?;Whoa, is that a voucher? I guess I don't need this!;...wait, he doesn't want this, either? Oh no...
Buh? Buh buh buh?;Buh? Oh hey, I can talk! Thanks, take this!;Buh buh... weird, I got so used to buh'ing they just come out sometimes!
Oh no, don't tell me... you don't have Gyarados? Well, maybe this will help you!;What? Yeah, of course you can catch a Gyarados with that! Just be patient...
Okay hear me out, I may not be the BEST fisher, but I'm pretty GOOD! HAH! Get it?;Oh yeah, you got it.
Ooh golly, I can already tell, you're a fishing enthusiast, and a super one at that! Take this.;Oh yeah buddy, now you can catch the BIG one!
Did you know? If you miss an attack, you might faint! Take this.;Okay sure, it doesn't work when you're sleeping, but neither do you!
I often imagine living in a hole for the rest of my days... perhaps you'll join me?;I've been waiting for you, my love, come to the underground with me!
Oh goodness, I seem to have soiled my pantaloons!;Heh, does somebody smell eggs?
Yesss, I can see it... you find me... incredibly attractive! No? Then I don't deserve this...;Wait, how can I hone my craft now??
Heh, bet you didn't know mirrors could talk... HUH?! You know I'm not you? Maybe you can use this better.;No? Well maybe you should give it back now... unless?
Ahh jeez, looks like I'm stuck here forever. I may as well just-;Huh? I'm still alive?
Ooh mama, I do NOT feel good. Oh? You'll rub me? Well shucks, I guess you've earned this!;Mmm yeah, just like that...
Hehe, you came all this way to see me? ...wait what, you're leaving already?;Noo please, I feel so alone...
Wow, I didn't think anybody would find me! Take this!;I learned that one at the beach, if you were wondering.
Heh, nothing like getting hugged by a cutie... oh, you want something? Uhh...;Man, I REALLY gotta stop talking to these kids maaan!
YOU LOVE GAMBLING? Oh, you don't? I can fix that!;Heh, tell me when you need some work...
I love pokemon, but I love you even more... I mean uhh, take this!;Feel free to join my harem, just have a seat.
Yawn... mmm, that was tasty!;Ahh, nothing like a tasty nap... Zzz
Mmm, that's the spot... oh, you want me to wake up? Hmm, come back later...;Zzz
Holy macaroni, you're the real deal! I bet you've got what it takes to be a pokemon master!;Of course, if you waste it, you are a pokemon LOSER!
My my, you've traveled far to save me, thank you! But I'm actually just wandering about for fun... do you like music?;Because I hate it, goodbye now!""".split("\n");
	static int gid=0;
	public static void buildGivers()
	{
		for(int i=0; i<5; i++)
		{
			String[] a=GIVER_STRINGS[i].split(";");
			PokeMap.POKEMAPS.get(a[0]).addGiver(a[1], Integer.parseInt(a[2]), GIVER_QUOTE_STRINGS[i]);
		}
		for(int i=5; i<8; i++)
		{
			String[] a=GIVER_STRINGS[i].split(";");
			PokeMap.POKEMAPS.get(a[0]).addGiver(a[1], a[2], GIVER_QUOTE_STRINGS[i]);
		}
		for(int i=8; i<GIVER_STRINGS.length; i++)
		{
			String[] a=GIVER_STRINGS[i].split(";");
			PokeMap.POKEMAPS.get(a[0]).addGiver(a[1], GIVER_QUOTE_STRINGS[i]);
		}
	}
	
	BufferedImage bi;
	int x, y, id;
	Move move;
	Item item;
	String[] quotes;
	boolean gave;
	public Giver(String s, String q)
	{
		id=gid++;
		String[] a=s.split(","), f=a[0].split(" ");
		int dir;
		if(Giver.class.getResourceAsStream("/sprites/"+f[0]+"/9.png")==null)
			switch(f[3])
			{
				case "DOWN":
					dir=0;
					break;
				case "UP":
					dir=1;
					break;
				case "LEFT":
					dir=2;
					break;
				case "RIGHT":
					dir=3;
					break;
				default:
					throw new RuntimeException(s);
			}
		else
			switch(f[3])
			{
				case "DOWN":
					dir=1;
					break;
				case "UP":
					dir=4;
					break;
				case "LEFT":
					dir=6;
					break;
				case "RIGHT":
					dir=8;
					break;
				default:
					throw new RuntimeException(s);
			}
		bi=Trainer.TRAINER_SPRITES.computeIfAbsent(f[0]+dir, k->{
			try {
				return OverworldGui.scale(ImageIO.read(Giver.class.getResourceAsStream("/sprites/" + f[0] + "/" + dir + ".png")));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("No trainer sprite!");
			}
		});
		x=Integer.parseInt(f[1]);
		y=Integer.parseInt(f[2]);
		if(a[1].startsWith("Move"))
			move=Move.MOVE_MAP.get(a[1].substring(5));
		else
			item=Item.ITEM_MAP.get(a[1]);
		quotes=q.split(";");
	}
	public void interact(Player p)
	{
		if(!gave)
		{
			OverworldGui.print(quotes[0]);
			if(move==null)
				p.give(item);
			else
				p.give(move);
			gave=true;
		}
		OverworldGui.print(quotes[1]);
	}
	
	static class Aide extends Giver
	{
		int dexNum;
		public Aide(String s, int dexNum, String q)
		{
			super(s, q);
			this.dexNum=dexNum;
		}
		public void interact(Player p)
		{
			if(p.numCaught<dexNum)
			{
				OverworldGui.print(quotes[0]);
				return;
			}
			if(!gave)
			{
				OverworldGui.print(quotes[1]);
				if(move==null)
					p.give(item);
				else
					p.give(move);
				gave=true;
			}
			OverworldGui.print(quotes[2]);
		}
	}
	
	static class IfGiver extends Giver
	{
		Item preReq;
		public IfGiver(String s, String i, String q)
		{
			super(s, q);
			preReq=Item.ITEM_MAP.get(i);
		}
		public void interact(Player p)
		{
			if(!p.hasItem(preReq))
			{
				OverworldGui.print(quotes[0]);
				return;
			}
			if(!gave)
			{
				OverworldGui.print(quotes[1]);
				if(move==null)
					p.give(item);
				else
					p.give(move);
				gave=true;
			}
			OverworldGui.print(quotes[2]);
		}
	}
}
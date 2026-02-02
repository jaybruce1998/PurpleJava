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
	static String[] NPC_QUOTE_STRINGS="""
Have you met Noah? Yeah, he's a... well, he's my brother!
How did you get back here?
Oh boy, I sure love biking! I hear there's a place called Cycling Road, I can't wait to go!
Hehe, I am the Chief of this town! Learn some respect sonny!
If there's one thing I learned from being a Rocket, it's to always respect the boss...
I'm the muscle of this organization, wanna tussle?
How did you get back here?
How did you get back here?
These fools won't feed me! I haven't eaten that much...
Ugh, I've been waiting for hours! What's taking them so long?
Did you know? I'm really small!
Rumor has it that a grampa like me has a move for you...
Oh? Yeah, I know you have a crush on me!
My brothers and I have lived here all of our lives. Maybe now I should go out and catch some pokemon!
Darnit, my elevator broke again! How can I get to the second floor now?
Buzz off, chump!
Chump off, buzz!
How did you get back here?
I sure do LOVE this hotel! Not that you could ever afford it.
Wow, that lady sure is a beauty! I wonder if I should talk to her...
Tea? No lad, but maybe you'd like some water. I hear you can get some at the department store!
You're working on your pokedex? Hey, me too!
You're taking on the league challenge? Hey, me too!
You're wondering why we're here? Hey, me too!
Leave me alone, I'm coding a game!
CHUMBA!
Alright, fine, you can have my Eevee... just treat it nicely, okay?
How did you get back here?
How did you get back here?
Hey, do you have any spare change? I spent all my money on balls!
My daddy loves me sooo much! He bought me, like, a billion balls!
How did you get back here?
I looove X items! You can really just trivialize any bettle, heh!
I might be young, but I keep getting money from beating my friends! I wonder what I should spend it on...
How did you get back here?
I sure love playing games! Maybe you should play one, too!
UGH, my eyes hurt, I need to take a break.
No, it broke free! I'd better reset.
Sigh, if only I could afford a game system, too!
Hoho, aren't you a feisty one!
Hey, like these? Yeah, those are MUSCLES!
How did you get back here?
How did you get back here?
How did you get back here?
G'day, mate!
I'm so beautiful! I can hardly stand it!!
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
My glasses look so cool!
EEK, stranger danger!
Yeah I live here, you got a problem with that?
Cerulean's waters are so calming... I could stand here all day.
Did you know the bridge used to be even longer?
I'm cataloging the patterns of the city layout!
The gym's lights can be seen from here at night.
I love the breeze coming off the water.
Hey squirt! This gym uses water-type pokemon, I hope you've got a grass-type!
Intruder alert!
Why yes, I DO come here often!
Ugh, my boyfriend is such a dork.
Intruder alert!
Misty suuure is pretty! Not that she'd ever like a guy like me.
Psst... can I set you up with my daughter?
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Gosh, this place is a mess! Want to go grab lunch?
Yo, this gym uses fire-types! I hope you've got a water-type!
Volcanoes make me nervous, but the science is fascinating.
Those machines must have cost a fortune!
Fossils are like messages from the past.
Be careful, some samples are fragile.
Nothing beats fishing by warm volcanic waters.
This device can bring Pokémon back from extinction.
Metronome is unpredictable, just like science itself.
Intruder alert!
I work at the Silph Company! Have you heard of it?
Nothing like grabbing a snack during your break... just don't tell my boss!
Intruder!
Oh man, I got burned pretty badly...
Well hello there! Aren't you lovely!
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Fair warning, we have a very eccentric daugther!
Are you here to babysit? Perfect, we can have a date night now!
GET OUT OF HERE!
I was robbed down there, I'd be careful!
The guy on the other side lies, don't listen to him!
Bill used to tinker with everything in this house.
Heh, Fuchsia's always good for a gamble.
I wonder if Bill's grandpa still remembers me.
The Safari Zone is just beyond these gates!
You can't win big without taking risks.
The fish bite better when it's foggy.
I'm training hard so I can go to the Safari Zone alone.
An ordinary Poké Ball lies here.
Seel is basking in the cool marsh air.
An old fossil is half-buried in the ground.
NO!
I feel so bad for those pokemon on display.
Have you been to the dungeon? You know the one!
NO!
Hey broseph, you should rock out with me!
I REALLY do not think you should be rocking out with strangers.
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Hello... poison, have you heard of it? PSYCHIC, that's what I'd recommend!
And then *I* was like-
What? He said THAT?!
Well isn't that something!
Try your luck, the odds favor the bold.
Please exchange your coins at the counter.
The slots were better back in my day.
A little risk makes life exciting.
The Game Corner hum never gets old.
I just came for the free snacks.
House rules are posted behind me.
If you need help, I'm your guide.
I've hit the jackpot once, just once.
Prizes this way, don't forget your tickets.
I've seen rookies win big in here.
Pick your prize carefully, trainer.
NO!
I've gotta be honest with you, I have absolutely no confidence in you.
CRUD, I lost again! Maybe I should catch new pokemon.
NO!
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Have you heard of Cubone? I've always wanted to meet one.
NO!
They didn't tell me my hair would start thinning... it's not too bad, is it?
It's days like these, maaan, let me tell you!
NO!
My mustache itches, can you scratch it?
My mom is soo mean, will you adopt me?
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Man, this is the worst vacation ever! I swear my room is haunted...
I'M A GHOST! Haha, just kidding... or am I?
Purple is my favorite color! What's yours?
I wonder where Mr. Fuji ran off to...
Honestly, good riddance to that old fool, he ALWAYS made me wash the dishes!
NO!
The Moon Mountain is apon us, stay close...
My friends call me the gentleman... what's your name?
My son speaks in riddles! How can I ever love him?
You really need to step away RIGHT NOW!
Dinosaurs once walked where we stand.
I lost all my money in the Game Corner again...
These fossils are older than anyone can imagine.
Every specimen here tells a story.
I want to be an archaeologist someday!
Oak used to bring me here when I was little.
The museum helps us understand Pokémon history.
This place feels a little spooky, doesn't it?
Rocks remember things people forget.
I wonder where Professor Oak is today.
Please don't touch the equipment.
These machines help analyze Pokémon data.
Nicknames, huh? Yeah, they're important alright!
Just a small town giiirl.
Living in a looonely woooorld!
Hey buster brown! This here is a rock-type gym, ROCK ON! I feel very bad for you if you have a Charmander.
NO!
I want to work here when I grow up! Although being unemployed is nice, too.
Quit bungling about, you're going to knock something over!
Pewter's gym leader is tougher than he looks.
The mountain air keeps you sharp.
I built my first Pokédex model right here in town.
Fossils from Mt. Moon are priceless.
I'm training so I can beat Brock someday.
My Nidoran is growing up so fast!
Brock used to play here when he was a kid.
MEOW!
I love trains! Do you know where I can catch one?
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Yeah, yeah, I'm a pretty smart dude! But can I ask you a question?
AHHHH! How was my scream? Pretty good, right?
Oh no, he's loose...
Okay I'll admit, cloning and gene tampering might not have been my best idea!
Hey, have you seen a cat-like pokemon? We may have... messed up...
Hi! Do you like my coat? It's comfy and easy to wear!
I've been in a lot of clubs in my life, but this one is my favorite!
Look at my Seel! Soo cute!
BUHH!
Hey now, don't interrupt the master! He's very busy.
BUH!
What? Don't call me that, it's rude!
YOU ARE SO BEAUTIFUL! I mean, hi!
The men in this town are just awful, they'll flirt with anybody!
I'm looking to expand my family, will you be my child?
Are you lost? You don't look like a scientist to me!
Anybody can be a scientist! Me? No, no, I'm a cosplayer.
MY SISTERS MUST BE POSSESSED! Can you take me away from here?
Hello my child. Come sit and enjoy the feast in your favorite chair, perhaps something good will happen!
NOO!
Yes? Yes!... no? Well okay.
Magikarp? Yes, that IS the strongest pokemon, how did you know?
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
Hey pal, wanna battle? No? Okay.
'Scuse me, have you seen Voltorb? That one's my favorite!
Rock Tunnel gets darker the deeper you go.
Only authorized trainers may pass.
I'm heading to Vermilion as soon as I can.
Please present your badge at the counter.
This gate connects two very different routes.
What do you think of these muscles? Heh.
I came here to watch the trainers go by.
I bet you've traveled far already!
I want to go on an adventure too.
Kanto is so much bigger than I thought.
Make sure you're ready before heading north.
I train here to build my endurance.
Did you see the Power Plant nearby?
Route 4 has some sneaky wild Pokémon.
Welcome aboard! Enjoy your trip.
Let me tell you, all the passengers are LOADED with cash! You? Hmm...
Yohoho and a bottle of yo! Ho?
The sea air agrees with me.
I've never been on a ship this big!
The captain loves his rare Pokémon.
I hope we don't hit any storms.
These cabins are surprisingly cozy.
Fishing from a moving ship is tricky.
I'm mapping the layout of the ship.
The view from here is incredible.
Make sure your footing is steady on deck.
We're prepping a feast for the voyage.
Keep the stoves at a steady flame.
Don't let the soup boil over!
Fresh ingredients make all the difference.
We never stop cooking down here.
Everything must be served piping hot.
The captain expects perfection.
I love exploring the tall grass without worry.
These Pokémon patterns are fascinating!
Rock music keeps me energized while walking.
Silph Corporation's tech makes the Safari safer.
I never knew there were so many rare species here.
Data collection is key to understanding wild Pokémon.
Be careful, some Pokémon are shy around humans.
Studying Pokémon behavior is so rewarding.
I've been training my party to react quickly.
Silph's research helps trainers like you.
Mister! This gym has psychic-types, so you'd better have... uhh, a bug or something?
Don't get in my way or you'll regret it.
Hehe, with Team Rocket in charge I can do anything!
Silph Corp keeps the city running smoothly.
Music makes even city life bearable.
Science always comes first, even here.
I've got work to do, no time for visitors.
I admire trainers who follow the rules.
This area is heavily secured - beware.
We don't tolerate troublemakers in Saffron.
Stay clear, or you might get caught.
NO!
Psst, I bet we could get away with ANYTHING in here! What do you say?
I just love spending time with my Pidgey.
I'm training hard to be a top trainer!
There's a note here... I wonder who left it.
NO!
Looking good today, trainer!
Heal often, it's free!
Oh, did you want to get through here? Yeah, you'd like that, wouldn't you?
NO!
Hey, what are you staring at?
Help, we're trapped!
I am soo scared.
Please, don't let them hurt us...  
We're trapped here... save us, trainer!  
I just wanted to finish my work...  
They've taken control of the building!  
Stay back! They've got Pokémon ready to fight!  
I can't move without risking my life...  
Hurry, the boss is dangerous!  
Don't let them take any more Pokémon!  
We're counting on you to stop them...  
I hope someone comes before it's too late...
Darn, if only I had my equipment! I could fix you up in a jiffy.
LEAVE ME TO MY CHAMBERS!
I'm lost, can you help me find the bathroom?
Crazy how the tunnels connect, huh? I heard Digletts helped with construction!
Oddish, yes! That's right!
I walk these routes sometimes. If you see me, say hello!
I've heard that some pokemon evolve through trading... I wonder how you could do that!
I just honestly... yeah. You know?
This city is so beautiful! You should watch the sea at night.
Sailing is my hobby, but I lost all my pokemon! Can you help me find them?
Oh, oh, OH! Boss, this is an electric-type gym! Go catch a Diglett NOW!
NO!
Sometimes I wonder... how can the clerk hear us from behind the counter?
Oh no, you've got a crush on me, don't you? I'm only gonna break your heart!
NO!
Tales of a far-away region have blessed my ears... Johto! I'd love to visit some day.
I've been living here for days now, I wonder if anybody's noticed.
Huh, just a piece of paper? Useless.
You're from Pallet Town? I love visting there!
I might be young, but I know a strong trainer when I see one! I'll tell you if any pass by.
Did you enjoy the woods? Personally, I'm always a little scared in there.
On your way north, eh? That's actually my favorite direction!!
My sister and I hangout here for fun. Do you have friends?
Hi, you look friendly! Now please leave.
Birds are so pretty! I hate bugs, though.
I'm looking for a girlfriend. She should be around here somewhere!
Gaming, yes! That's what life is all about.
I don't know this man, can you rescue me?
NO!
Hey trainer, you look new to shopping! Pro-tip: buy repels... oh wait.
You must be new in town, I'm the city champion! Oh yeah, no trainers stronger than me here...
You finally made it! Ground-types rule here, how about you get them WET?!
NO!
Hiya sonny, are you catching lots of pokemon? Jay will be so proud.
I've heard a super strong trainer lives around here. I wonder where, though...
Howdy, you must be new here! Don't talk to me, ever.
...are you sure? You are so weak. NOBODY can SAVE you down there.
Yum yum, I'm the breeder! I'll borrow your pokemon to make more beautiful children with... just give me a second......
Feeling trapped? Perhaps these lonely statues want to be talked to... make sure to look them in the eyes!
Yes, I AM dancing because Matt is so cool!...Me? No, I'm not Matt in disguise! I just think I'm... I mean, HE'S very cool!""".split("\n");
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
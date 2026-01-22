package purple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

import java.io.InputStream;
import java.util.LinkedHashSet;

public class OverworldGui extends JPanel {
    static final int TILE_SIZE = 64;
    static final int DISPLAY_W = 11; // how many tiles horizontally to draw
    static final int DISPLAY_H = 11; // how many tiles vertically to draw
	static final Item FAKE_ID=Item.ITEM_MAP.get("Fake ID");
	static final Item LIFT_KEY=Item.ITEM_MAP.get("Lift Key");
	static boolean showingText, battling, choosingFromLongList, usingBattleItem, busedItem, rightClicked, inMenu, buySell, buying, selling, checkingPokes, checkingMoves, checkingTms, teachingMove, depWith, depositing, withdraw, flying, inside, showingDex, rareCandy, pickingStarter, surfing;
	static String currentText, currentLoc;
	private static final Deque<String> printQ=new ArrayDeque<>();
    static String mapName = "RedsHouse2F";
    private int playerX=0, playerY=6, repelSteps=0, boxNum, psi;
    static PokeMap pm = PokeMap.POKEMAPS.get(mapName);
	PokeMap lastHeal=PokeMap.POKEMAPS.get("RedsHouse1F");
    private int[][] currentMap;
    private int[][] tileTypes;
    private PokeMap[] connections = new PokeMap[4]; // N S W E
    private int[] connOffsets = new int[4]; // offsets for N,S,W,E
    private final BufferedImage[] tileImages = new BufferedImage[801], playerFrames = new BufferedImage[10], seel = new BufferedImage[10], pBattlers=new BufferedImage[153], eBattlers=new BufferedImage[153];
	static final Move CUT=Move.MOVE_MAP.get("Cut");
	static final Move FLY=Move.MOVE_MAP.get("Fly");
	static final Move SURF=Move.MOVE_MAP.get("Surf");
	static final Move STRENGTH=Move.MOVE_MAP.get("Strength");
	static final Item AMULET_COIN=Item.ITEM_MAP.get("Amulet Coin");
	private Battler chosenMon, wildMon;
	static BattleState playerState, enemyState;
	static int clickedChoice=-1;
	static String[] strArr=new String[4], longArr;
	private BattleInfoPanel battleInfo = new BattleInfoPanel();
	static final Object choiceLock=new Object(), itemLock=new Object();
	static Item[] usableItems;
	private Item usedItem;
	private MartItem[] martItems;
	private Move[] tms;
	private Move tm;

    private float offsetX = 0, offsetY = 0; // movement animation offsets
    private Direction facing = Direction.SOUTH;
    private Direction heldDirection = null;

    private Timer timer;
    private StepPhase stepPhase = StepPhase.NONE;
    private int phaseFrame = 0, frames=0, mouseX, mouseY;
	private final int NUM_STEP_FRAMES=8;
    private int currentStepFrames = NUM_STEP_FRAMES;
    private final int BUMP_STEP_FRAMES = NUM_STEP_FRAMES*2;
    private int timesMoved = 0;
	public static Font pokemonFont;
	public final Player player;
	private int bet=1;
	private final JFrame frame;
	private final BufferedImage[] DANCE_FRAMES=new BufferedImage[10];
	private final Giver MATT=PokeMap.POKEMAPS.get("Route4").givers[3][63];
	private boolean spaceHeld;
	enum Direction {
		SOUTH(0),
		NORTH(1),
		WEST(2),
		EAST(3);

		public final int id;

		Direction(int id) {
			this.id = id;
		}

		private static final Direction[] BY_ID = values();

		public static Direction fromId(int id) {
			if (id < 0 || id >= BY_ID.length)
				throw new IllegalArgumentException("Invalid direction id: " + id);
			return BY_ID[id];
		}
	}
    enum StepPhase { NONE, MOVING, LANDING }
	static final int[] CATCH_RATES={1, 45, 45, 45, 45, 45, 45, 45, 45, 45, 255, 120, 45, 255, 120, 45, 255, 120, 45, 255, 127, 255, 90, 255, 90, 190, 75, 255, 90, 235, 120, 45, 235, 120, 45, 150, 25, 190, 75, 170, 50, 255, 90, 255, 120, 45, 190, 75, 190, 75, 255, 50, 255, 90, 190, 75, 190, 75, 190, 75, 255,
			120, 45, 200, 100, 50, 180, 90, 45, 255, 120, 45, 190, 60, 255, 120, 45, 190, 60, 190, 75, 190, 60, 45, 190, 45, 190, 75, 190, 75, 190, 60, 190, 90, 45, 45, 190, 75, 225, 60, 190, 60, 90, 45, 190, 75, 45, 45, 45, 190, 60, 120, 60, 30, 45, 45, 225, 75, 225, 60, 225, 60, 45, 45, 45, 45, 45, 45,
			45, 255, 45, 45, 35, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 25, 3, 3, 3, 45, 27, 9, 3, 2};
	public static void print(String s)
	{
		if(showingText)
			printQ.add(s);
		else
		{
			currentText=s;
			showingText=true;
		}
	}
	private static BufferedImage flipHorizontally(BufferedImage img) {
		BufferedImage flipped = new BufferedImage(
			img.getWidth(),
			img.getHeight(),
			BufferedImage.TYPE_INT_ARGB
		);

		Graphics2D g = flipped.createGraphics();

		// Flip horizontally:
		// scale(-1, 1) flips left-right, then translate so it stays on screen
		g.drawImage(
			img,
			-img.getWidth(), 0,   // destination x1,y1
			0, img.getHeight(),   // destination x2,y2
			0, 0,                 // source x1,y1
			img.getWidth(), img.getHeight(), // source x2,y2
			null
		);

		g.dispose();
		return flipped;
	}
	private void setup()
	{
		try {
			for(int i=0; i<10; i++)
				DANCE_FRAMES[i]=scale(ImageIO.read(OverworldGui.class.getResourceAsStream("/sprites/COOLTRAINER_F/" + i + ".png")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
        setPreferredSize(new Dimension(DISPLAY_W * TILE_SIZE, DISPLAY_H * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);

        loadTileImages();
        loadPlayerSprites();

        loadMap(pm);
		try {
			InputStream is=OverworldGui.class.getResourceAsStream("/PKMN-RBYGSC.ttf");

			Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
			pokemonFont = baseFont.deriveFont(24f);

			GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(baseFont);
				
			final int BSIZE=256;
			for(int i=1; i<153; i++)
			{
				// Load the original image
				BufferedImage original = ImageIO.read(OverworldGui.class.getResourceAsStream("/battlers/" + i + ".png"));
				
				// Scale to 256x256
				BufferedImage scaled = new BufferedImage(BSIZE, BSIZE, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = scaled.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.drawImage(original, 0, 0, BSIZE, BSIZE, null);
				g2d.dispose();
				
				// Store scaled version (e.g., enemy facing right)
				eBattlers[i] = scaled;
				
				// Create horizontally flipped version (e.g., player facing left)
				BufferedImage flipped = new BufferedImage(BSIZE, BSIZE, BufferedImage.TYPE_INT_ARGB);
				g2d = flipped.createGraphics();
				g2d.drawImage(scaled, BSIZE, 0, 0, BSIZE, 0, 0, BSIZE, BSIZE, null);  // flip horizontally
				g2d.dispose();
				
				pBattlers[i] = flipped;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
        setupKeyBindings();
		
        timer = new Timer(1000 / 60, e -> {
			if(++frames%6==0)
			{
				if(spaceHeld)
					clickMouse();
				if(frames%30==0)
					MATT.bi=DANCE_FRAMES[(int)(Math.random()*10)];
			}
            update();
            repaint();
        });
        timer.start();
	}
    public OverworldGui(JFrame frame, String name) {
		player=new Player(name);
		this.frame=frame;
		setup();
    }
	public OverworldGui(JFrame frame, Player p, String info)
	{
		player=p;
		String[] a=info.split(",");
		pm=PokeMap.POKEMAPS.get(a[0]);
		playerX=Integer.parseInt(a[1]);
		playerY=Integer.parseInt(a[2]);
		facing=Direction.fromId(Integer.parseInt(a[3]));
		repelSteps=Integer.parseInt(a[4]);
		lastHeal=PokeMap.POKEMAPS.get(a[5]);
		if(a[6].equals("1"))
			p.ballin=true;
		else
			PokeMap.POKEMAPS.get("SilphCo11F").givers[5][7]=null;
		p.numCaught=Integer.parseInt(a[7]);
		p.money=Integer.parseInt(a[8]);
		p.name=a[9];
		this.frame=frame;
		setup();
	}

    private void loadMap(PokeMap map) {
        pm = map;
        currentMap = pm.grid;
        tileTypes = pm.types;

        // load connections and offsets
        connections[0] = pm.north; connOffsets[0] = pm.nOff;
        connections[1] = pm.south; connOffsets[1] = pm.sOff;
        connections[2] = pm.west;  connOffsets[2] = pm.wOff;
        connections[3] = pm.east;  connOffsets[3] = pm.eOff;
    }

    private void loadTileImages() {
		for(int i=0; i<781; i++) {
			try {
				tileImages[i]=scale(ImageIO.read(OverworldGui.class.getResourceAsStream("/tiles/"+i+".png")));
			} catch (Exception e) {
				System.err.println("Failed to load tile: " + i);
				System.exit(1);
			}
		}
		//weird doors
		int[] weird={642, 69, 73, 263, 79, 94, 102, 263, 262, 742, 599, 7, 619, 1, 136, 263, 313, 203, 436, 148};
		for(int i=0; i<weird.length; i++)
			tileImages[781+i]=tileImages[weird[i]];
    }

    private void loadPlayerSprites() {
        try {
            for (int i = 0; i < 10; i++) {
                playerFrames[i] = scale(ImageIO.read(OverworldGui.class.getResourceAsStream("/sprites/RED/" + i + ".png")));
                seel[i] = scale(ImageIO.read(OverworldGui.class.getResourceAsStream("/sprites/SEEL/" + i + ".png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected static BufferedImage scale(BufferedImage src) {
        BufferedImage scaled = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, TILE_SIZE, TILE_SIZE, null);
        g.dispose();
        return scaled;
    }

	private void closeMenus()
	{
		choosingFromLongList=false;
		inMenu=false;
		buySell=false;
		buying=false;
		selling=false;
		checkingPokes=false;
		checkingMoves=false;
		checkingTms=false;
		teachingMove=false;
		depWith=false;
		depositing=false;
		withdraw=false;
		flying=false;
		showingDex=false;
	}
	private void append(StringBuilder sb, boolean[] a)
	{
		sb.append('\n');
		for(boolean b: a)
			sb.append(b?'1':'0');
	}
	private void clickMouse()
	{
		if(showingText)
			if(printQ.isEmpty())
				showingText = false;
			else
				currentText=printQ.pop();
		else if(pickingStarter)
		{
			pickingStarter=false;
			int y=mouseY;
			if(y<165)
				return;
			if(y<340)
			{
				int x=mouseX;
				if(x>35&&x<175)
					player.give(new Battler(1, Monster.MONSTER_MAP.get("Bulbasaur")));
				else if(x>285&&x<440)
					player.give(new Battler(1, Monster.MONSTER_MAP.get("Charmander")));
				else if(x>540)
					player.give(new Battler(1, Monster.MONSTER_MAP.get("Squirtle")));
				else
					pickingStarter=true;
			}
			else
				switch((y-540)/25)
				{
					case 0:
						player.give(new Battler(1, Monster.MONSTER_MAP.get("Bulbasaur")));
						break;
					case 1:
						player.give(new Battler(1, Monster.MONSTER_MAP.get("Charmander")));
						break;
					case 2:
						player.give(new Battler(1, Monster.MONSTER_MAP.get("Squirtle")));
						break;
					default:
						pickingStarter=true;
				}
		}
		else if(buySell)
		{
			clickedChoice = (mouseY-540)/25;
			if(clickedChoice==0)
			{
				buying=true;
				buySell=false;
				longArr=new String[martItems.length];
				for(int i=0; i<longArr.length; i++)
					longArr[i]=martItems[i].toString();
				choosingFromLongList=true;
			}
			else if(clickedChoice==1)
			{
				selling=true;
				buySell=false;
				int n=0;
				for(Item i: player.items)
					if(i.price>0)
						n++;
				usableItems=new Item[n];
				longArr=new String[n];
				int i=0;
				for(Item item: player.items)
					if(item.price>0)
					{
						usableItems[i]=item;
						longArr[i++]=item.name+" "+item.price/2;
					}
				choosingFromLongList=true;
			}
		}
		else if(buying)
		{
			clickedChoice = (mouseY-80)/25;
			if(clickedChoice<0||clickedChoice>=martItems.length)
				return;
			MartItem mi=martItems[clickedChoice];
			if(mi.item!=null)
			{
				String input = JOptionPane.showInputDialog("Enter a quantity to purchase:");
				int q;
				try {
					q=Integer.parseInt(input);
				} catch(Exception ex) {
					print("Quit messing around over there!");
					return;
				}
				if(q<1||q>99)
				{
					print("You can buy between 1 and 99 items.");
					return;
				}
				int n=q*mi.price;
				if(n>player.money)
				{
					print("You don't have the money for all of these!");
					return;
				}
				player.money-=n;
				player.give(mi.item, q);
				print("Thank you!");
			}
			else
			{
				if(player.money<mi.price)
				{
					print("You don't have the money for this!");
					return;
				}
				if(mi.mon==null)
				{
					if(player.give(mi.move))
						player.money-=mi.price;
				}
				else
				{
					player.money-=mi.price;
					player.give(new Battler(1, mi.mon));
				}
			}
		}
		else if(selling)
		{
			clickedChoice = (mouseY-80)/25;
			if(clickedChoice<0||clickedChoice>=usableItems.length)
				return;
			String input = JOptionPane.showInputDialog("Enter a quantity to sell:");
			int q;
			try {
				q=Integer.parseInt(input);
			} catch(Exception ex) {
				print("Quit messing around over there!");
				return;
			}
			if(q<1)
			{
				print("Hey come on, be serious!");
				return;
			}
			player.sell(usableItems[clickedChoice], q);
			buySell=true;
			selling=false;
			choosingFromLongList=false;
			print("Thank you!");
		}
		else if(checkingPokes)
		{
			clickedChoice = (mouseY-80)/25;
			if(clickedChoice<0||clickedChoice>=player.team.length)
				return;
			if(player.team[clickedChoice]!=null)
			{
				longArr=player.team[clickedChoice].allInformation();
				checkingPokes=false;
				checkingMoves=true;
			}
		}
		else if(checkingMoves)
		{
			longArr=Battler.partyStrings(player.team);
			checkingPokes=true;
			checkingMoves=false;
		}
		else if(checkingTms)
		{
			clickedChoice=(mouseY-80)/25;
			if(clickedChoice<0||clickedChoice>=tms.length)
				return;
			tm=tms[clickedChoice];
			checkingTms=false;
			teachingMove=true;
			strArr[0]=null;
			longArr=Battler.partyStrings(player.team);
		}
		else if(teachingMove)
		{
			if(strArr[0]==null)
			{
				clickedChoice = (mouseY-80)/25;
				if(clickedChoice<0||clickedChoice>=player.team.length||player.team[clickedChoice]==null)
					return;
				chosenMon=player.team[clickedChoice];
				if(Monster.MONSTERS[chosenMon.dexNum].learnable.contains(tm))
				{
					for(int i=0; i<chosenMon.moves.length; i++)
						if(chosenMon.moves[i]==null)
						{
							strArr[0]=null;
							chosenMon.moves[i]=tm;
							print(chosenMon.nickname+" learned "+tm.name+"!");
							return;
						}
						else if(chosenMon.moves[i]==tm)
						{
							strArr[0]=null;
							OverworldGui.print(chosenMon.nickname+" already knows "+tm.name+"!");
							return;
						}
						else
							strArr[i]=chosenMon.moves[i].name;
					print(chosenMon.nickname+" is trying to learn "+tm.name+"! Select a move to replace it with.");
				}
				else
					print(chosenMon.nickname+" cannot learn "+tm.name+".");
			}
			else
			{
				clickedChoice=(mouseY-540)/25;
				if(clickedChoice<0||clickedChoice>=chosenMon.moves.length)
					return;
				while(clickedChoice>1&&chosenMon.moves[clickedChoice-1]==null)
					clickedChoice--;
				if(chosenMon.moves[clickedChoice]!=null)
					print(chosenMon.nickname+" forgot how to use "+chosenMon.moves[clickedChoice].name+", but...");
				chosenMon.moves[clickedChoice]=tm;
				strArr[0]=null;
				chosenMon.pp[clickedChoice]=Math.min(chosenMon.pp[clickedChoice], tm.pp);
				print(chosenMon.nickname+" learned "+tm.name+"!");
			}
		}
		else if(depWith)
		{
			clickedChoice=(mouseY-540)/25;
			if(clickedChoice==0)
			{
				depositing=true;
				depWith=false;
				longArr=Battler.partyStrings(player.team);
				choosingFromLongList=true;
			}
			else if(clickedChoice==1)
			{
				withdraw=true;
				depWith=false;
				longArr=new String[20];
				int n=Math.min(20, player.pc.size());
				for(int i=0; i<n; i++)
					longArr[i]=player.pc.get(i).toString();
				for(int i=n; i<20; i++)
					longArr[i]="";
				psi=0;
				boxNum=1;
				choosingFromLongList=true;
			}
		}
		else if(depositing)
		{
			clickedChoice = (mouseY-80)/25;
			if(clickedChoice<0||clickedChoice>=player.team.length||player.team[clickedChoice]==null)
				return;
			if(player.team[1]==null)
			{
				depositing=false;
				depWith=true;
				choosingFromLongList=false;
				print("No, you'll never survive in this world without pokemon!");
				return;
			}
			print("You deposited "+player.team[clickedChoice].nickname+"!");
			player.pc.add(player.team[clickedChoice]);
			for(int i=clickedChoice+1; i<player.team.length; i++)
			{
				player.team[i-1]=player.team[i];
				longArr[i-1]=longArr[i];
			}
			player.team[5]=null;
			longArr[5]="";
		}
		else if(withdraw)
		{
			clickedChoice=(mouseY-80)/25;
			if(clickedChoice<0||clickedChoice>19)
				return;
			int ind=clickedChoice+psi;
			if(ind>=player.pc.size())
				return;
			for(int i=1; i<player.team.length; i++)
				if(player.team[i]==null)
				{
					player.team[i]=player.pc.remove(ind);
					for(int j=clickedChoice; j<19;)
						longArr[j]=longArr[++j];
					ind=psi+19;
					if(ind<player.pc.size())
						longArr[19]=player.pc.get(ind).toString();
					print("You withdrew "+player.team[i].nickname+"!");
					return;
				}
			withdraw=false;
			depWith=true;
			choosingFromLongList=false;
			print("Hey, you already have 6 pokemon with you! Don't be greedy.");
		}
		else if(flying)
		{
			String n=FlyLocation.NAME_MAP[mouseY/64][mouseX/64];
			if(n==null)
				return;
			String p=printable(n);
			if(inside||!p.equals(currentLoc)||!player.leadersBeaten[2]||!player.hasMove(FLY))
			{
				currentLoc=p;
				return;
			}
			FlyLocation f=FlyLocation.FLY_LOCATIONS.get(n);
			if(f==null)
				return;
			loadMap(f.dest);
			playerX=f.x;
			playerY=f.y;
			inMenu=false;
			flying=false;
			print("You flew to "+p+"!");
		}
		else if(!showingDex&&choosingFromLongList)
			if(usableItems==null)
				synchronized (choiceLock) {
					clickedChoice = (mouseY-80)/25;
					choiceLock.notify();
				}
			else
			{
				clickedChoice = (mouseY-80)/25;
				if(usedItem==null)
				{
					if(clickedChoice<0||clickedChoice>=usableItems.length)
						return;
					inMenu=false;
					usedItem=usableItems[clickedChoice];
					switch(usedItem.name)
					{
						case "X Accuracy":
							playerState.accStage++;
							print(playerState.monster.nickname+"'s accuracy rose by 1 stage!");
							if(playerState.accStage==7)
							{
								playerState.accStage=6;
								print("Although it maxed out at 6!");
							}
							notifyUseItem(true);
							break;
						case "X Attack":
							playerState.atkStage++;
							print(playerState.monster.nickname+"'s attack rose by 1 stage!");
							if(playerState.atkStage==7)
							{
								playerState.atkStage=6;
								print("Although it maxed out at 6!");
							}
							notifyUseItem(true);
							break;
						case "X Defend":
							playerState.defStage++;
							print(playerState.monster.nickname+"'s defense rose by 1 stage!");
							if(playerState.defStage==7)
							{
								playerState.defStage=6;
								print("Although it maxed out at 6!");
							}
							notifyUseItem(true);
							break;
						case "X Special Attack":
							playerState.spatkStage++;
							print(playerState.monster.nickname+"'s special attack rose by 1 stage!");
							if(playerState.spatkStage==7)
							{
								playerState.spatkStage=6;
								print("Although it maxed out at 6!");
							}
							notifyUseItem(true);
							break;
						case "X Special Defend":
							playerState.spdefStage++;
							print(playerState.monster.nickname+"'s special defense rose by 1 stage!");
							if(playerState.spdefStage==7)
							{
								playerState.spdefStage=6;
								print("Although it maxed out at 6!");
							}
							notifyUseItem(true);
							break;
						case "X Speed":
							playerState.spdStage++;
							print(playerState.monster.nickname+"'s speed rose by 1 stage!");
							if(playerState.spdStage==7)
							{
								playerState.spdStage=6;
								print("Although it maxed out at 6!");
							}
							notifyUseItem(true);
							break;
						case "Dire Hit":
							playerState.critMul*=4;
							print(playerState.monster.nickname+" is now 4 times more likely to get a critical hit!");
							notifyUseItem(true);
							break;
						case "Guard Spec.":
							print(playerState.monster.nickname+"'s stats cannot be lowered!");
							if(playerState.canLower)
								playerState.canLower=false;
							else
								print("Although they already could not be lowered!");
							notifyUseItem(true);
							break;
						case "Poke Doll":
							battling=false;
							print("You got away successfully!");
							notifyUseItem(true);
							break;
						case "PokeBall":
							catchMon(1);
							break;
						case "Great Ball":
							catchMon(1.5);
							break;
						case "Ultra Ball":
							catchMon(2);
							break;
						case "Master Ball":
							catchMon(100000);
							break;
						case "Repel":
							useRepel(101);
							break;
						case "Super Repel":
							useRepel(201);
							break;
						case "Max Repel":
							useRepel(251);
							break;
						case "Escape Rope":
							loadMap(lastHeal);
							playerX=lastHeal.healX;
							playerY=lastHeal.healY;
							closeMenus();
							print("You climbed back to the last heal spot!");
							player.use(usedItem);
							break;
						case "Old Rod":
						case "Good Rod":
						case "Super Rod":
							inMenu=true;
							String n=usedItem.name;
							usedItem=null;
							int nx = playerX, ny = playerY;
							switch(facing) {
								case SOUTH -> ny++;
								case NORTH -> ny--;
								case WEST  -> nx--;
								case EAST  -> nx++;
								default -> throw new RuntimeException("WHERE AM I LOOKING?!"+facing);
							}
							if(nx < 0 || ny < 0 || ny >= currentMap.length || nx >= currentMap[0].length)
							{
								print("What exactly do you expect to find in the void?");
								return;
							}
							if(tileTypes[ny][nx]!=4)
							{
								print("Maybe I should fish in the water...");
								return;
							}
							wildMon=pm.getRandomEncounter(n);
							if(wildMon==null)
							{
								print("Huh, no fish... I should try somewhere else!");
								return;
							}
							closeMenus();
							new SwingWorker<Integer, Void>() {
								@Override protected Integer doInBackground() {
									add(battleInfo);
									return BattleState.wildBattle(player.team, wildMon, OverworldGui.this); // <-- runs in background
								}
								@Override protected void done() {
									try {
										int result = get(); // <-- retrieve what doInBackground() returned
										if(result<0)
											blackout();
										else
											player.money+=result*(player.hasItem(AMULET_COIN)?2:1);
										remove(battleInfo);
										battling=false;
									} catch (Exception e) {
										e.printStackTrace();
										System.exit(1);
									}
								}
							}.execute();
							return;
						default:
							inMenu=true;
							longArr=Battler.partyStrings(player.team);
					}
				}
				else if(chosenMon==null)
				{
					if(clickedChoice<0||clickedChoice>=player.team.length)
						return;
					closeMenus();
					Battler b=player.team[clickedChoice];
					switch(usedItem.name)
					{
						case "Antidote":
							healStatus(b, "POISONED");
							break;
						case "Awakening":
							healStatus(b, "ASLEEP");
							break;
						case "Burn Heal":
							healStatus(b, "BURNED");
							break;
						case "Ice Heal":
							healStatus(b, "FROZEN");
							break;
						case "Paralyze Heal":
							healStatus(b, "PARALYZED");
							break;
						case "Elixir":
							useElixir(b, 10);
							break;
						case "Max Elixir":
							useElixir(b, 99);
							break;
						case "Fire Stone":
						case "Leaf Stone":
						case "Moon Stone":
						case "Thunder Stone":
						case "Water Stone":
							if(b.useStone(usedItem.name))
							{
								player.use(usedItem);
								player.register(b);
							}
							break;
						case "Full Restore":
							b.hp=b.mhp;
							print(b.nickname+"'s HP was fully restored!");
						case "Full Heal":
							if(playerState!=null&&playerState.monster==b)
								playerState.cTurns=0;
							healStatus(b, b.status);
							break;
						case "Fresh Water":
							healHp(b, 50);
							break;
						case "Lemonade":
							healHp(b, 80);
							break;
						case "Soda Pop":
							healHp(b, 60);
							break;
						case "Potion":
							healHp(b, 20);
							break;
						case "Super Potion":
							healHp(b, 50);
							break;
						case "Hyper Potion":
							healHp(b, 200);
							break;
						case "Max Potion":
							healHp(b, 999);
							break;
						case "Revive":
							useRevive(b, b.mhp/2);
							break;
						case "Max Revive":
							useRevive(b, b.mhp);
							break;
						case "Calcium":
							b.spatkXp+=10;
							player.use(usedItem);
							print(b.nickname+" gained special attack experience!");
							break;
						case "Carbos":
							b.spdXp+=10;
							player.use(usedItem);
							print(b.nickname+" gained speed experience!");
							break;
						case "HP Up":
							b.hpXp+=10;
							player.use(usedItem);
							print(b.nickname+" gained HP experience!");
							break;
						case "Iron":
							b.defXp+=10;
							player.use(usedItem);
							print(b.nickname+" gained defense experience!");
							break;
						case "Protein":
							b.atkXp+=10;
							player.use(usedItem);
							print(b.nickname+" gained attack experience!");
							break;
						case "Zinc":
							b.spdefXp+=10;
							player.use(usedItem);
							print(b.nickname+" gained special defense experience!");
							break;
						case "Rare Candy":
							player.use(usedItem);
							rareCandy=true;
							new Thread(() -> {
								b.gainXp(player, b.moves, b.mxp-b.xpNeeded(b.level), 0, 0, 0, 0, 0, 0);
								rareCandy=false;
							}).start();
							break;
						default:
							inMenu=true;
							chosenMon=b;
							longArr=chosenMon.moveStrings();
							return;
					}
					usedItem=null;
					return;
				}
				else if(usedItem!=null)
				{
					if(clickedChoice<0||clickedChoice>=chosenMon.moves.length)
						return;
					Move move=chosenMon.moves[clickedChoice];
					if(move==null)
						return;
					switch(usedItem.name)
					{
						case "Ether":
							if(chosenMon.pp[clickedChoice]==chosenMon.moves[clickedChoice].pp)
							{
								print("DO NOT WASTE THAT!");
								notifyUseItem(false);
								break;
							}
							chosenMon.pp[clickedChoice]=Math.min(chosenMon.moves[clickedChoice].pp, chosenMon.pp[clickedChoice]+10);
							print(move.name+"'s PP was restored by 10!");
							notifyUseItem(true);
							break;
						case "Max Ether":
							if(chosenMon.pp[clickedChoice]==chosenMon.moves[clickedChoice].pp)
							{
								print("DO NOT WASTE THAT!");
								notifyUseItem(false);
								break;
							}
							chosenMon.pp[clickedChoice]=chosenMon.moves[clickedChoice].pp;
							print(move.name+"'s PP was fully restored!");
							notifyUseItem(true);
							break;
						case "PP Up":
							chosenMon.pp[clickedChoice]=(int)(chosenMon.pp[clickedChoice]*1.2);
							print(move+"'s PP was permanently increased!");
							break;
						default:
							throw new RuntimeException(usedItem.name+" huh?");
					}
					usedItem=null;
					chosenMon=null;
					inMenu=false;
					return;
				}
			}
		else
			synchronized (choiceLock) {
				clickedChoice = (mouseY-540)/25;
				choiceLock.notify();
			}
	}
    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "moveNorth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "moveWest");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "moveSouth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "moveEast");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, false), "openInventory");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, false), "showPokemon");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, false), "showTms");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, false), "showMap");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0, false), "showDex");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "holdMouse");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "save");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "shiftUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "swapTwo");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "prevBox");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "nextBox");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "stopNorth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "stopWest");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "stopSouth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "stopEast");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "stopClicking");
		//DVORAK
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0, false), "moveNorth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, false), "moveSouth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false), "moveEast");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, false), "openInventory");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, false), "showPokemon");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "showTms");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false), "showDex");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0, true), "stopNorth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, true), "stopSouth");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, true), "stopEast");

        actionMap.put("moveNorth", new MoveAction(Direction.NORTH, true));
        actionMap.put("moveSouth", new MoveAction(Direction.SOUTH, true));
        actionMap.put("moveWest", new MoveAction(Direction.WEST, true));
        actionMap.put("moveEast", new MoveAction(Direction.EAST, true));
		actionMap.put("stopNorth", new MoveAction(Direction.NORTH, false));
		actionMap.put("stopSouth", new MoveAction(Direction.SOUTH, false));
		actionMap.put("stopWest", new MoveAction(Direction.WEST, false));
		actionMap.put("stopEast", new MoveAction(Direction.EAST, false));
		actionMap.put("openInventory", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inMenu)
				{
					closeMenus();
					return;
				}
				if (battling || showingText || choosingFromLongList||buySell) 
					return;
				int n=0;
				for(Item i: player.items)
					if(i.world)
						n++;
				usableItems=new Item[n];
				longArr=new String[n];
				int i=0;
				for(Item item: player.items)
					if(item.world)
					{
						usableItems[i]=item;
						longArr[i++]=item.toString();
					}
				inMenu=true;
				choosingFromLongList=true;
			}
		});
		actionMap.put("showPokemon", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inMenu)
				{
					closeMenus();
					return;
				}
				if (battling || showingText || choosingFromLongList||buySell) 
					return;
				checkingPokes=true;
				longArr=Battler.partyStrings(player.team);
				inMenu=true;
				choosingFromLongList=true;
			}
		});
		actionMap.put("showTms", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inMenu)
				{
					closeMenus();
					return;
				}
				if (battling || showingText || choosingFromLongList||buySell) 
					return;
				checkingTms=true;
				tms=player.tmHms.toArray(new Move[0]);
				longArr=new String[tms.length];
				for(int i=0; i<tms.length; i++)
					longArr[i]=tms[i].name;
				inMenu=true;
				choosingFromLongList=true;
			}
		});
		actionMap.put("showMap", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inMenu)
				{
					closeMenus();
					return;
				}
				if (battling || showingText || choosingFromLongList||buySell) 
					return;
				flying=true;
				inMenu=true;
				currentLoc=printable(pm.name);
				for(int i=0; i<11; i++)
					for(int j=0; j<11; j++)
						if(pm.name.equals(FlyLocation.NAME_MAP[i][j]))
						{
							inside=false;
							return;
						}
				inside=true;
			}
		});
		actionMap.put("showDex", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inMenu)
				{
					closeMenus();
					return;
				}
				if (battling||showingText||choosingFromLongList||buySell)
					return;
				longArr=new String[20];
					longArr[0]=player.pokedex[0]?"000: Missingo":"";
				for(int i=1; i<20; i++)
				{
					String n=i+"";
					longArr[i]="0".repeat(3-n.length())+n+": "+(player.pokedex[i]?Monster.MONSTERS[i].name:"?");
				}
				psi=0;
				choosingFromLongList=true;
				showingDex=true;
				inMenu=true;
			}
		});
		actionMap.put("holdMouse", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spaceHeld=true;
			}
		});
		actionMap.put("stopClicking", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spaceHeld=false;
			}
		});
		actionMap.put("save", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inMenu||battling||showingText||choosingFromLongList||buySell)
					return;
				switch(pm.name)
				{
					case "RocketHideoutB1F":
						if(playerX!=3)
							break;
						printSpooky();
					case "ChampionsRoom":
						printSpooky();
					case "LancesRoom":
						printSpooky();
					case "AgathasRoom":
					case "BrunosRoom":
					case "LoreleisRoom":
						printSpooky();
						print("You will not be saved.");
						return;
				}
				StringBuilder sb=new StringBuilder(pm.name).append(',').append(playerX).append(',').append(playerY).append(',').append(facing.id).append(',').append(repelSteps).append(',').append(lastHeal.name)
								.append(',').append(player.ballin?'1':'0').append(',').append(player.numCaught).append(',').append(player.money).append(',').append(player.name).append('\n');
				if(player.pc.size()>0)
				{
					player.pc.get(0).append(sb);
					for(int i=1; i<player.pc.size(); i++)
						player.pc.get(i).append(sb.append(';'));
				}
				sb.append('\n');
				player.team[0].append(sb);
				for(int i=1; i<player.team.length; i++)
					if(player.team[i]==null)
						break;
					else
						player.team[i].append(sb.append(';'));
				append(sb, player.trainersBeaten);
				append(sb, player.leadersBeaten);
				append(sb, player.gioRivalsBeaten);
				append(sb, player.objectsCollected);
				append(sb, player.pokedex);
				sb.append('\n');
				if(player.items.size()>0)
				{
					Item i=player.items.get(0);
					sb.append(i.name).append(',').append(i.quantity);
					for(int j=1; j<player.items.size(); j++)
					{
						i=player.items.get(j);
						sb.append(';').append(i.name).append(',').append(i.quantity);
					}
				}
				sb.append('\n');
				for(Move m: player.tmHms)
					sb.append(',').append(m.name);
				sb.append('\n');
				for(int i=1; i<14; i++)
					sb.append(FlyLocation.FLY_LOCATIONS.get(FlyLocation.INDEX_MEANINGS[i]).visited?'1':'0');
				try {
					Files.writeString(Path.of("save.txt"), sb.toString());
					print("Saved!");
				} catch(Exception ex) {
					ex.printStackTrace();
					print(ex.getMessage());
				}
			}
		});
		actionMap.put("shiftUp", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!inMenu)
					return;
				if(checkingPokes||depositing)
				{
					int n=0;
					while(n<player.team.length&&player.team[n]!=null)
						n++;
					if(n<2)
						return;
					Battler b=player.team[0];
					for(int i=1; i<n; i++)
					{
						player.team[i-1]=player.team[i];
						longArr[i-1]=longArr[i];
					}
					player.team[--n]=b;
					longArr[n]=b.toString();
				}
				else if(checkingMoves)
				{
					int n=0;
					Battler b=player.team[clickedChoice];
					while(n<b.moves.length&&b.moves[n]!=null)
						n++;
					if(n<2)
						return;
					Move m=b.moves[0];
					int pp=b.pp[0];
					String s=longArr[10];
					for(int i=1; i<n; i++)
					{
						b.moves[i-1]=b.moves[i];
						b.pp[i-1]=b.pp[i];
						longArr[i+9]=longArr[i+10];
					}
					b.moves[--n]=m;
					b.pp[n]=pp;
					longArr[n+10]=s;
				}
				else if(checkingTms)
				{
					int n=tms.length;
					if(n<2)
						return;
					tm=tms[0];
					for(int i=1; i<n; i++)
					{
						tms[i-1]=tms[i];
						longArr[i-1]=longArr[i];
					}
					tms[--n]=tm;
					longArr[n]=tm.name;
				}
				else if(withdraw)
				{
					if(longArr[1].isEmpty())
						return;
					player.pc.add(player.pc.remove(psi));
					for(int i=0; i<20; i++)
						longArr[i]=psi+i<player.pc.size()?player.pc.get(psi+i).toString():"";
				}
				else
				{
					int n=usableItems.length;
					if(n<2)
						return;
					Item item=usableItems[0];
					for(int i=1; i<n; i++)
					{
						usableItems[i-1]=usableItems[i];
						longArr[i-1]=longArr[i];
					}
					usableItems[--n]=item;
					longArr[n]=item.toString();
				}
			}
		});
		actionMap.put("swapTwo", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				if(!inMenu)
					return;
				if(checkingPokes||depositing)
				{
					int n=0;
					while(n<player.team.length&&player.team[n]!=null)
						n++;
					if(n<2)
						return;
					Battler b=player.team[0];
					player.team[0]=player.team[1];
					player.team[1]=b;
					longArr[0]=player.team[0].toString();
					longArr[1]=b.toString();
				}
				else if(checkingMoves)
				{
					int n=0;
					Battler b=player.team[clickedChoice];
					while(n<b.moves.length&&b.moves[n]!=null)
						n++;
					if(n<2)
						return;
					Move m=b.moves[0];
					int pp=b.pp[0];
					String s=longArr[10];
					b.moves[0]=b.moves[1];
					b.pp[0]=b.pp[1];
					longArr[10]=longArr[11];
					b.moves[1]=m;
					b.pp[1]=pp;
					longArr[11]=s;
				}
				else if(checkingTms)
				{
					int n=tms.length;
					if(n<2)
						return;
					tm=tms[0];
					tms[0]=tms[1];
					tms[1]=tm;
					longArr[0]=tms[0].name;
					longArr[1]=tm.name;
				}
				else if(withdraw)
				{
					if(longArr[1].isEmpty())
						return;
					chosenMon=player.pc.get(psi);
					player.pc.set(psi, player.pc.get(psi+1));
					player.pc.set(psi+1, chosenMon);
					player.pc.add(player.pc.remove(psi));
					String s=longArr[0];
					longArr[0]=longArr[1];
					longArr[1]=s;
				}
				else
				{
					int n=usableItems.length;
					if(n<2)
						return;
					Item item=usableItems[0];
					usableItems[0]=usableItems[1];
					usableItems[1]=item;
					longArr[0]=usableItems[0].toString();
					longArr[1]=item.toString();
				}
			}
		});
		actionMap.put("prevBox", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				if(showingDex)
				{
					if(psi==0)
						return;
					psi-=20;
					if(psi==0)
						longArr[0]=player.pokedex[psi]?"000: Missingo":"";
					else
					{
						String n=psi+"";
						longArr[0]="0".repeat(3-n.length())+n+": "+(player.pokedex[psi]?Monster.MONSTERS[psi].name:"");
					}
					for(int i=1; i<20; i++)
					{
						int x=psi+i;
						String n=x+"";
						longArr[i]="0".repeat(3-n.length())+n+": "+(player.pokedex[x]?Monster.MONSTERS[x].name:"");
					}
					return;
				}
				if(!withdraw||psi==0)
					return;
				boxNum--;
				psi-=20;
				for(int i=0; i<20; i++)
					longArr[i]=psi+i<player.pc.size()?player.pc.get(psi+i).toString():"";
			}
		});
		actionMap.put("nextBox", new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				if(showingDex)
				{
					if(psi==140)
						return;
					psi+=20;
					for(int i=0; i<20; i++)
					{
						int x=psi+i;
						if(x==151)
						{
							longArr[i]=player.pokedex[x]?"151: Mew":"";
							while(i<20)
								longArr[i++]="";
							return;
						}
						String n=x+"";
						longArr[i]="0".repeat(3-n.length())+n+": "+(player.pokedex[x]?Monster.MONSTERS[x].name:"");
					}
					return;
				}
				if(!withdraw||psi+20>=player.pc.size())
					return;
				boxNum++;
				psi+=20;
				for(int i=0; i<20; i++)
					longArr[i]=psi+i<player.pc.size()?player.pc.get(psi+i).toString():"";
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					closeMenus();
					if(showingText)
						if(printQ.isEmpty())
							showingText = false;
						else
							currentText=printQ.pop();
					else if(usableItems==null)
						synchronized(choiceLock) {
							clickedChoice=0;
							rightClicked=true;
							choiceLock.notify();
						}
					else
					{
						usableItems=null;
						notifyUseItem(false);
					}
					return;
				}
				mouseX=e.getX();
				mouseY=e.getY();
				clickMouse();
			}
		});
    }
	private void useRevive(Battler b, int n)
	{
		if(b.hp>0)
		{
			print("Quit messing around!");
			notifyUseItem(false);
		}
		else
		{
			b.hp+=n;
			print(b.nickname+"'s health was restored by "+n+" points!");
			notifyUseItem(true);
		}
	}
	private void healHp(Battler b, int n)
	{
		if(b.hp==0)
		{
			print("No, you'll need something stronger to heal this one...");
			notifyUseItem(false);
		}
		else if(b.hp==b.mhp)
		{
			print("Quit messing around!");
			notifyUseItem(false);
		}
		else
		{
			b.hp=Math.min(b.hp+n, b.mhp);
			print(b.nickname+"'s health was restored by "+n+" points!");
			notifyUseItem(true);
		}
	}
	private void useElixir(Battler b, int n)
	{
		boolean u=false;
		for(int i=0; i<b.moves.length; i++)
			if(b.moves[i]==null)
				break;
			else if(b.pp[i]<b.moves[i].pp)
			{
				b.moves[i].pp=Math.min(b.moves[i].pp+n, b.pp[i]);
				u=true;
			}
		if(u)
		{
			print(b.nickname+"'s PP was restored!");
			notifyUseItem(true);
		}
		else
		{
			print("DO NOT WASTE THAT!");
			notifyUseItem(false);
		}
	}
	private void healStatus(Battler b, String s)
	{
		if(b.status.equals(s))
		{
			if(playerState!=null&&playerState.monster==b)
				playerState.poisonDamage=0;
			b.status="";
			print(b.nickname+"'s status was healed!");
			notifyUseItem(true);
		}
		else
		{
			print("That wouldn't do anything!");
			notifyUseItem(false);
		}
	}
	private void useRepel(int steps)
	{
		repelSteps+=steps;
		player.use(usedItem);
		usedItem=null;
		choosingFromLongList=false;
		print("You gained "+(steps-1)+" repel steps!");
	}
	private void catchMon(double bm)
	{
		int thp=3*wildMon.mhp, sb=0;
		switch(wildMon.status)
		{
			case "ASLEEP":
			case "FROZEN":
				sb=10;
				break;
			case "POISONED":
			case "BURNED":
			case "PARALYZED":
				sb=5;
		}
		print("You threw a "+usedItem.name+"!");
		if((int)(Math.random()*256)<=Math.max(((thp - 2*wildMon.hp)*CATCH_RATES[wildMon.dexNum]*bm) / thp, 1) + sb)
		{
			battling=false;
			print("Oh baby! You caught it!");
			wildMon.nickname=wildMon.name;
			player.give(wildMon);
		}
		else
			print("But it broke out!");
		notifyUseItem(true);
	}
	private void notifyUseItem(boolean b)
	{
		if(b)
			player.use(usedItem);
		usedItem=null;
		choosingFromLongList=false;
		synchronized(itemLock) {
			busedItem=b;
			usingBattleItem=false;
			itemLock.notify();
		}
	}
	public static void setBattleStates(BattleState a, BattleState b)
	{
		playerState=a;
		enemyState=b;
		battling=true;
	}
	public void blackout()
	{
		loadMap(lastHeal);
		playerX=lastHeal.healX;
		playerY=lastHeal.healY;
		int n=player.money/2;
		player.money-=n;
		player.healTeam();
		print("You dropped $"+n+"!");
	}
	private final LinkedHashSet<Direction> pressedKeys = new LinkedHashSet<>();

	private class MoveAction extends AbstractAction {
		private final Direction dir;
		private final boolean pressed;

		public MoveAction(Direction dir, boolean pressed) {
			this.dir = dir;
			this.pressed = pressed;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (pressed) {
				pressedKeys.add(dir);
				heldDirection = dir; // latest key pressed takes priority
			} else {
				pressedKeys.remove(dir);
				if (heldDirection == dir) {
					// assign the most recently pressed remaining key, if any
					heldDirection = pressedKeys.isEmpty() ? null : pressedKeys.iterator().next();
				}
			}
		}
	}

    private void update() {
		if(showingText||battling||buySell||buying||selling||inMenu)
			return;
		if(spinning)
		{
			advanceStep();
			return;
		}
        if (stepPhase == StepPhase.NONE && heldDirection != null) {
			facing = heldDirection;
            currentStepFrames = canMove(facing) ? NUM_STEP_FRAMES : BUMP_STEP_FRAMES;
            stepPhase = StepPhase.MOVING;
            phaseFrame = 0;
        }
        if (stepPhase != StepPhase.NONE)
			advanceStep();
    }
	public String printable(String s)
	{
		return s.replaceAll("([A-Za-z])(?=\\d)", "$1 ")
			// Separate number from trailing F (29F -> 29 F, but we'll rejoin B1F later)
			.replaceAll("(\\d)(?=F)", "$1 ")
			// Separate lowercase->uppercase or uppercase->uppercase when it's a word boundary
			// (ViridianCity -> Viridian City, SilphCo -> Silph Co)
			.replaceAll("([a-z])(?=[A-Z])", "$1 ")
			.replaceAll("([A-Z])(?=[A-Z][a-z])", "$1 ")
			// Now fix cases like "B 1 F" -> "B1F"
			.replaceAll("B 1 F", "B1F")
			// Clean up any double spaces that might have been created
			.replaceAll(" +", " ");
	}
	boolean switchingMaps=false, spinning=false;
    private boolean canMove(Direction d) {
		if(switchingMaps)
			return true;
        int nx = playerX, ny = playerY;
        switch (d) {
            case SOUTH -> ny++;
            case NORTH -> ny--;
            case WEST  -> nx--;
            case EAST  -> nx++;
        }
		if(nx < 0 || ny < 0 || ny >= currentMap.length || nx >= currentMap[0].length)
			return false;
		System.out.println(nx+","+ny+","+currentMap[ny][nx]);
		switch(tileTypes[playerY][playerX])
		{
			case 5:
				return d==Direction.SOUTH;
			case 9:
				return d!=Direction.NORTH&&tileTypes[ny][nx]!=0;
			case 10:
				return d==Direction.WEST;
			case 11:
				return d==Direction.EAST;
		}
        switch(tileTypes[ny][nx])
		{
			case 0:
				return false;
			case 3:
				print(printable(pm.name)+"... I think? Man, I wish I could read!");
				return false;
			case 4:
				return player.leadersBeaten[4]&&player.hasMove(SURF);
			case 5:
				return d==Direction.SOUTH;
			case 6:
				return player.leadersBeaten[1]&&player.hasMove(CUT);
			case 9:
				return d!=Direction.SOUTH;
			case 10:
				return d==Direction.WEST;
			case 11:
				return d==Direction.EAST;
			case 19:
				return player.leadersBeaten[3]&&player.hasMove(STRENGTH);
			case 20:
				print("Ew, that stinks!");
				if(Math.random()<0.1)
				{
					if(Math.random()<0.01)
					{
						print("Whoa, a rare candy! Who would throw this away?!");
						player.give(Item.ITEM_MAP.get("Rare Candy"));
					}
					else
					{
						print("Oh no, I'll have to wash my hands...");
						player.give(Item.ITEM_MAP.get("...Secret Sauce"));
					}
				}
				return false;
		}
		return true;
    }
	private int elevate(String s, boolean B, int m)
	{
		if(!player.hasItem(LIFT_KEY))
		{
			print("Weird, nothing is happening.");
			return 0;
		}
		boolean z=pm.name.equals("ViridianBunglerHouse");
		heldDirection=null;
		String input = JOptionPane.showInputDialog(z?"...":"Enter the floor you would like to go to:");
		if(input==null)
			return 0;
		if(z)
		{
			if(input.equals("DEATH")&&player.pokedex[150])
			{
				loadMap(PokeMap.POKEMAPS.get("RocketHideoutB1F"));
				return 1;
			}
			return 0;
		}
		String u=input.toUpperCase();
		if(B&&u.startsWith("B"))
			u=u.substring(1);
		if(u.endsWith("F"))
			u=u.substring(0, u.length()-1);
		int q;
		try {
			q=Integer.parseInt(u);
		} catch(Exception ex) {
			print(input+", huh? Yeah, okay buddy.");
			return 0;
		}
		if(q<0)
			if(B)
				q=-q;
			else
			{
				print("Oh, you wanna go underground? NO! You can go, up, though, all the way to floor "+m+"!");
				return 0;
			}
		if(q==0)
		{
			print("What is this, Europe?!");
			return 0;
		}
		if(q>m)
		{
			print("Uhh, you know there are only "+m+" floors, right?");
			return 0;
		}
		if(B&&q==3)
		{
			print("...no.");
			return 0;
		}
		loadMap(PokeMap.POKEMAPS.get(s+(B?"B"+q:q)+"F"));
		print(z?"Huh?!":"Wee!");
		return q;
	}
	static int[] HIDEOUT_Y={0, 18, 18, 0, 14};
	static int[] SILPH_X={0, 20, 20, 20, 20, 20, 18, 18, 18, 18, 12, 13};
	private char randomSpooky()
	{
		return (char)(32 + Math.random() * (126 - 32 + 1));
	}
	private void printSpooky()
	{
		StringBuilder sb=new StringBuilder();
		for(int i=0; i<10; i++)
			sb.append(randomSpooky());
		sb.append("DEATH");
		for(int i=0; i<10; i++)
			sb.append(randomSpooky());
		print(sb.toString());
	}
	private void advanceStep() {
		float delta = TILE_SIZE / (float) currentStepFrames;
		int dx = 0, dy = 0;
		PokeMap newMap = null;
		switch (facing)
		{
			case SOUTH:
				dy = 1;
				if(playerY==currentMap.length-1 && connections[1] != null)
				{
					newMap = connections[1];
					playerX-=connOffsets[1];
					playerY = -1;
				}
				break;
			case NORTH:
				dy = -1;
				if(playerY==0 && connections[0] != null)
				{
					newMap = connections[0];
					playerX-=connOffsets[0];
					playerY = newMap.grid.length;
				}
				break;
			case WEST:
				dx = -1;
				if(playerX==0 && connections[2] != null)
				{
					newMap = connections[2];
					playerX = newMap.grid[0].length;
					playerY-=connOffsets[2];
				}
				break;
			case EAST:
				dx = 1;
				if(playerX==currentMap[0].length-1 && connections[3] != null)
				{
					newMap = connections[3];
					playerX = -1;
					playerY-=connOffsets[3];
				}
		}
		if(newMap!=null)
		{
			mapName = newMap.name;
			loadMap(newMap);
			switchingMaps=true;
			currentStepFrames/=2;
		}
		boolean isBump = currentStepFrames == BUMP_STEP_FRAMES;
		int nextX = playerX + dx, nextY = playerY + dy;
		if (isBump) {
			if(tileTypes[playerY][playerX]==2||tileTypes[playerY][playerX]==18)//NS door, EW door
			{
				Warp w=pm.getWarp(playerY, playerX);
				if(w==null)
				{
					w=pm.getNearbyWarp(playerY, playerX);
					if(w==null)
					{
						inMenu=true;
						String s=pm.name;
						int n=elevate("RocketHideout", true, 4);
						if(n>0)
							playerY=HIDEOUT_Y[n];
						else
							playerY--;
						inMenu=false;
						return;
					}
				}
				loadMap(w.pm);
				playerX=w.col;
				playerY=w.row;
				currentStepFrames/=2;
			}
			else if(pm.healX==playerX&&pm.healY==playerY)
			{
				player.healTeam();
				phaseFrame=currentStepFrames;
				lastHeal=pm;
				FlyLocation.visit(pm.name);
				print("Your team was fully healed!");
			}
			else if(nextX>=0&&nextX<currentMap[0].length&&nextY>=0&&nextY<currentMap.length)
			{
				WorldObject wo=pm.wob[nextY][nextX];
				if(wo!=null)
				{
					if(wo.stepOn(this))
						pm.stepOn(player, nextX, nextY);
					phaseFrame=currentStepFrames;
				}
				else if(pm.grid[nextY][nextX]==220)
				{
					inMenu=true;
					depWith=true;
					strArr[0]="Deposit";
					strArr[1]="Withdraw";
					phaseFrame=currentStepFrames;
				}
				else if(pm.name.equals("GameCorner")&&pm.grid[playerY][playerX]==122)
				{
					phaseFrame=currentStepFrames;
					if(player.hasItem(FAKE_ID))
					{
						if(player.money<bet)
							print("No! I want to bet $"+bet+" if I play, but I'm broke!");
						else
						{
							player.money-=bet;
							if(BlackjackDialog.play(frame))
							{
								player.money+=bet;
								print("You won $"+bet+"!");
							}
							else
							{
								player.money-=bet;
								print("You lost $"+bet+"!");
							}
							bet*=2;
						}
					}
					else
						print("Crap, it wants me to scan an ID!!");
				}
				else
				{
					martItems=pm.getMartItems(playerX, playerY);
					if(martItems!=null)
					{
						buySell=true;
						strArr[0]="Buy";
						strArr[1]="Sell";
						phaseFrame=currentStepFrames;
						print("Welcome to the mart!");
					}
				}
			}
		}
		else if (canMove(facing)) {
			offsetX += dx * delta;
			offsetY += dy * delta;
		}
		phaseFrame++;
		if (phaseFrame >= currentStepFrames) {
			switchingMaps=false;
			phaseFrame = 0;
			stepPhase = StepPhase.NONE;
			timesMoved = timesMoved == 0 ? 2 : 0;
			// Check if we are NOT still in bounds
			if (isBump || nextX < 0 || nextX >= currentMap[0].length || nextY < 0 || nextY >= currentMap.length)
				currentStepFrames = BUMP_STEP_FRAMES;
			else
			{
				Giver g=pm.givers[nextY][nextX];
				if(g!=null)
				{
					g.interact(player);
					if(pm.name.equals("Daycare"))
					{
						int p=5000, d=player.team[0].dexNum;
						if(d==0)
							p=50000;
						else if(d>132)
							p=25000;
						if(p>player.money)
							print("Wait, you're sooo poor! You need at least $"+p+" for this child, I guess I'm keeping this one...");
						else
						{
							player.money-=p;
							print("You spent $"+p+"...");
							player.give(new Battler(player.team[0]));
						}
					}
					else if(g.gave)
					{
						if(g.item!=null&&g.item.name.equals("Shiny Charm"))
							Main.SHINY_CHANCE=128;
						pm.givers[nextY][nextX]=null;
					}
					offsetX=0;
					offsetY=0;
					return;
				}
				WorldObject wo=pm.wob[nextY][nextX];
				if(wo!=null)
				{
					Boolean b=wo.stepOn(this);
					if(b==null)
					{
						wildMon=new Battler(wo.level, wo.mon);
						new SwingWorker<Integer, Void>() {
							@Override protected Integer doInBackground() {
								add(battleInfo);
								return BattleState.wildBattle(player.team, wildMon, OverworldGui.this); // <-- runs in background
							}
							@Override protected void done() {
								try {
									int result = get(); // <-- retrieve what doInBackground() returned
									if(result<0)
										blackout();
									else
									{
										player.money+=result*(player.hasItem(AMULET_COIN)?2:1);
										pm.stepOn(player, nextX, nextY);
									}
									remove(battleInfo);
									battling=false;
								} catch (Exception e) {
									e.printStackTrace();
									System.exit(1);
								}
							}
						}.execute();
					}
					else if(b)
						pm.stepOn(player, nextX, nextY);
					offsetX=0;
					offsetY=0;
					return;
				}
				if(--repelSteps==0)
					print("Your repel ran out!");
				repelSteps=Math.max(0, repelSteps-1);
				switch(tileTypes[nextY][nextX])
				{
					case 1:
						surfing=false;
						playerX=nextX;
						playerY=nextY;
						break;
					case 7:
						if(pm.grid[nextY][nextX]==182)
						{
							inMenu=true;
							elevate("CeladonMart", false, 5);
							inMenu=false;
							break;
						}
						else if(pm.grid[nextY][nextX]==697)
						{
							inMenu=true;
							int n=elevate("SilphCo", false, 11);
							if(n>0)
								playerX=SILPH_X[n];
							inMenu=false;
							break;
						}
						Warp w=pm.getWarp(nextY, nextX);
						loadMap(w.pm);
						playerX=w.col;
						playerY=w.row;
						if(playerX==0&&playerY==6&&pm.name.equals("RedsHouse2F"))
						{
							player.ballin=true;
							print("What? How did I get back here??");
						}
						break;
					case 4:
						surfing=true;
						playerX = nextX;
						playerY = nextY;
						if(Math.random()<0.1)
						{
							wildMon=pm.getRandomEncounter("Surfing");
							if(wildMon!=null&&(repelSteps==0||wildMon.level>=player.team[0].level))
							{
								new SwingWorker<Integer, Void>() {
									@Override protected Integer doInBackground() {
										add(battleInfo);
										return BattleState.wildBattle(player.team, wildMon, OverworldGui.this); // <-- runs in background
									}
									@Override protected void done() {
										try {
											int result = get(); // <-- retrieve what doInBackground() returned
											if(result<0)
												blackout();
											else
												player.money+=result*(player.hasItem(AMULET_COIN)?2:1);
											remove(battleInfo);
											battling=false;
										} catch (Exception e) {
											e.printStackTrace();
											System.exit(1);
										}
									}
								}.execute();
								offsetX=0;
								offsetY=0;
								return;
							}
						}
						break;
					case 8:
						playerX = nextX;
						playerY = nextY;
						if(Math.random()<0.1)
						{
							wildMon=pm.getRandomEncounter("Cave");
							if(wildMon!=null&&(repelSteps==0||wildMon.level>=player.team[0].level))
							{
								new SwingWorker<Integer, Void>() {
									@Override protected Integer doInBackground() {
										add(battleInfo);
										return BattleState.wildBattle(player.team, wildMon, OverworldGui.this); // <-- runs in background
									}
									@Override protected void done() {
										try {
											int result = get(); // <-- retrieve what doInBackground() returned
											if(result<0)
												blackout();
											else
												player.money+=result*(player.hasItem(AMULET_COIN)?2:1);
											remove(battleInfo);
											battling=false;
										} catch (Exception e) {
											e.printStackTrace();
											System.exit(1);
										}
									}
								}.execute();
								offsetX=0;
								offsetY=0;
								return;
							}
						}
						break;
					case 12:
						playerX = nextX;
						playerY = nextY;
						if(Math.random()<0.1)
						{
							wildMon=pm.getRandomEncounter("Tall Grass");
							if(wildMon!=null&&(repelSteps==0||wildMon.level>=player.team[0].level))
								new SwingWorker<Integer, Void>() {
									@Override protected Integer doInBackground() {
										add(battleInfo);
										return BattleState.wildBattle(player.team, wildMon, OverworldGui.this); // <-- runs in background
									}
									@Override protected void done() {
										try {
											int result = get(); // <-- retrieve what doInBackground() returned
											if(result<0)
												blackout();
											else
												player.money+=result*(player.hasItem(AMULET_COIN)?2:1);
											remove(battleInfo);
											battling=false;
										} catch (Exception e) {
											e.printStackTrace();
											System.exit(1);
										}
									}
								}.execute();
							offsetX=0;
							offsetY=0;
							return;
						}
						break;
					case 13:
						if(tileTypes[playerY][playerX]!=13)
						{
							player.healTeam();
							print("You feel power flow through you. Your pokemon have been healed!");
						}
						playerX=nextX;
						playerY=nextY;
						break;
					case 14:
						facing=Direction.WEST;
						spinning=true;
						playerX=nextX;
						playerY=nextY;
						break;
					case 15:
						facing=Direction.EAST;
						spinning=true;
						playerX=nextX;
						playerY=nextY;
						break;
					case 16:
						facing=Direction.NORTH;
						spinning=true;
						playerX=nextX;
						playerY=nextY;
						break;
					case 17:
						facing=Direction.SOUTH;
						spinning=true;
					default:
						playerX = nextX;
						playerY = nextY;
				}
				if(currentMap[playerY][playerX]==545)
					spinning=false;
				else if(pm.sight[playerY][playerX]!=0)
				{
					Trainer t=pm.getTrainer(playerX, playerY, pm.sight[playerY][playerX]);
					if(t==null)
						printSpooky();
					else
						new SwingWorker<Integer, Void>() {
							@Override protected Integer doInBackground() {
								print(t.phrases[0]);
								add(battleInfo);
								return BattleState.trainerBattle(player.team, t.type, t.team, OverworldGui.this);
							}
							@Override protected void done() {
								try {
									int result = get();
									if(result<0)
									{
										blackout();
										for(Battler b: t.team)
											b.fullyHeal();
									}
									else
									{
										player.money+=(result+t.reward)*(player.hasItem(AMULET_COIN)?2:1);
										print("You got $"+t.reward+" for winning!");
										print(t.phrases[1]);
										t.beat(player);
										pm.deleteTrainer(t, pm.sight[playerY][playerX]);
									}
									remove(battleInfo);
									battling=false;
								} catch (Exception e) {
									e.printStackTrace();
									System.exit(1);
								}
							}
						}.execute();
				}
				offsetX = 0;
				offsetY = 0;
			}
		}
	}
	public void setWildItems()
	{
		int n=0;
		for(Item i: player.items)
			if(i.wild)
				n++;
		usableItems=new Item[n];
		longArr=new String[n];
		int i=0;
		for(Item item: player.items)
			if(item.wild)
			{
				usableItems[i]=item;
				longArr[i++]=item.toString();
			}
		usingBattleItem=true;
		busedItem=false;
		choosingFromLongList=true;
	}
	public void setTrainerItems()
	{
		int n=0;
		for(Item i: player.items)
			if(i.battle)
				n++;
		usableItems=new Item[n];
		longArr=new String[n];
		int i=0;
		for(Item item: player.items)
			if(item.battle)
			{
				usableItems[i]=item;
				longArr[i++]=item.toString();
			}
		usingBattleItem=true;
		busedItem=false;
		choosingFromLongList=true;
	}
	private void drawStar(Graphics2D g, int cx, int cy, int radius, int points) {
		double angle = Math.PI / points;
		int[] x = new int[points * 2];
		int[] y = new int[points * 2];

		for (int i = 0; i < points * 2; i++) {
			double r = (i % 2 == 0) ? radius : radius / 2.5;
			x[i] = (int) (cx + Math.cos(i * angle) * r);
			y[i] = (int) (cy + Math.sin(i * angle) * r);
		}

		g.fillPolygon(x, y, points * 2);
	}
	private void drawRandom256(Graphics2D g, int x, int y) {
		BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				int rgb = (int)(Math.random() * 0xFFFFFF);
				img.setRGB(i, j, rgb);
			}
		}

		g.drawImage(img, x, y, null);
	}

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		if(pickingStarter)
		{
			if(showingText)
				drawTextBox(g, currentText);
			else
			{
				drawTextBox(g, "Bulbasaur");
				drawWrappedText(g2, "Charmander", 60, getHeight()-140+25, getWidth()-120);
				drawWrappedText(g2, "Squirtle", 60, getHeight()-140+50, getWidth()-120);
			}
			g.drawImage(pBattlers[1], -20, 128, null);
			g.drawImage(pBattlers[4], 236, 128, null);
			g.drawImage(pBattlers[7], 492, 128, null);
			return;
		}
		else if(choosingFromLongList)
		{
			g2.setColor(Color.WHITE);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(4));
			g2.drawRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

			// Draw text
			g2.setFont(pokemonFont);
			g2.setColor(Color.BLACK);
			for(int i=0; i<longArr.length; i++)
				drawWrappedText(g2, longArr[i], 60, i*25+100, getWidth()-60);
			if(showingText)
				drawTextBox(g, currentText);
			else if(buying||selling)
				drawWrappedText(g2, "Money: $"+player.money, 150, 30, getWidth()-120);
			else if(checkingPokes||depositing)
				drawWrappedText(g2, "Party", 150, 30, getWidth()-120);
			else if(withdraw)
				drawWrappedText(g2, "Box "+boxNum, 150, 30, getWidth()-120);
			else if(teachingMove&&strArr[0]!=null)
			{
				drawTextBox(g, strArr[0]);
				drawWrappedText(g2, strArr[1], 60, getHeight()-140+25, getWidth()-120);
				drawWrappedText(g2, strArr[2], 60, getHeight()-140+50, getWidth()-120);
				drawWrappedText(g2, strArr[3], 60, getHeight()-140+75, getWidth()-120);
			}
			else if(checkingTms)
				drawWrappedText(g2, "TM/HMs", 150, 30, getWidth()-120);
			else if(checkingMoves)
				drawWrappedText(g2, "Stats", 150, 30, getWidth()-120);
			else if(showingDex)
				drawWrappedText(g2, "Number Caught: "+player.numCaught, 150, 30, getWidth()-120);
			else
				drawWrappedText(g2, "Inventory", 150, 30, getWidth()-120);
			return;
		}
		else if(flying)
		{
			g.drawImage(FlyLocation.MAP_IMAGE, 0, 0, null);
			g2.setColor(Color.RED);
			for(int i=0; i<11; i++)
				for(int j=0; j<11; j++)
					if(FlyLocation.isRed(i, j))
						g2.fillRoundRect(j*64, i*64, 64, 64, 5, 5);
			if(inside)
				g.drawImage(playerFrames[getCurrentFrame()], 640, 640, null);
			else
				for(int i=0; i<11; i++)
					for(int j=0; j<11; j++)
						if(pm.name.equals(FlyLocation.NAME_MAP[i][j]))
						{
							g.drawImage(playerFrames[getCurrentFrame()], j*64, i*64, null);
							i=10;
							break;
						}
			if(showingText)
			{
				g2.setColor(Color.BLACK);
				drawTextBox(g, currentText);
			}
			else
			{
				g2.setColor(Color.GREEN);
				g2.setFont(pokemonFont);
				drawWrappedText(g2, currentLoc, 80, 40, 600);
			}
			return;
		}
        int camX = Math.round(playerX * TILE_SIZE + offsetX - getWidth() / 2 + TILE_SIZE / 2);
        int camY = Math.round(playerY * TILE_SIZE + offsetY - getHeight() / 2 + TILE_SIZE / 2);

        // Draw visible tiles only
        int startX = Math.max(0, camX / TILE_SIZE - 1);
        int startY = Math.max(0, camY / TILE_SIZE - 1);
        int endX = Math.min(currentMap[0].length, startX + DISPLAY_W + 2);
        int endY = Math.min(currentMap.length, startY + DISPLAY_H + 2);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                BufferedImage tile = tileImages[currentMap[y][x]];
                g.drawImage(tile, x * TILE_SIZE - camX, y * TILE_SIZE - camY, null);
				Trainer t=pm.trainers[y][x];
				if(t==null)
				{
					WorldObject w=pm.wob[y][x];
					if(w==null)
					{
						Giver giv=pm.givers[y][x];
						if(giv!=null)
							g.drawImage(giv.bi, x * TILE_SIZE - camX, y * TILE_SIZE - camY, null);
					}
					else
						g.drawImage(w.bi, x * TILE_SIZE - camX, y * TILE_SIZE - camY, null);
				}
				else
					g.drawImage(t.bi, x * TILE_SIZE - camX, y * TILE_SIZE - camY, null);
            }
        }

        // Draw connections if they exist
        for (int i = 0; i < 4; i++) {
            PokeMap conn = connections[i];
            if (conn != null) drawConnection(g, i, conn, connOffsets[i], camX, camY);
        }

        // Draw player
        int frame = getCurrentFrame();
        int drawX = getWidth() / 2 - TILE_SIZE / 2;
        int drawY = getHeight() / 2 - TILE_SIZE / 2;
        g.drawImage(surfing?seel[frame]:playerFrames[frame], drawX, drawY, null);
		if(battling)
		{
			if(playerState.monster.dexNum==0)
				drawRandom256(g2, 64, 128);
			else
			{
				g.drawImage(pBattlers[playerState.monster.dexNum], 64, 128, null);
				if(playerState.monster.shiny)
				{
					g2.setColor(new Color(255, 215, 0)); // GOLD
					drawStar(g2, 96, 160, 28, 5);  // small star
					drawStar(g2, 160, 160, 22, 5); // another one
				}
			}
			if(enemyState.monster.dexNum==0)
				drawRandom256(g2, 384, 128);
			else
			{
				g.drawImage(eBattlers[enemyState.monster.dexNum], 384, 128, null);
				if (enemyState.monster.shiny) {
					g2.setColor(new Color(255, 215, 0)); // GOLD
					drawStar(g2, 480, 160, 28, 5);
					drawStar(g2, 544, 160, 22, 5);
				}
			}
			battleInfo.update(playerState.monster, enemyState.monster);
			if(showingText)
				drawTextBox(g, currentText);
			else
			{
				drawTextBox(g, strArr[0]);
				drawWrappedText(g2, strArr[1], 60, getHeight()-140+25, getWidth()-120);
				drawWrappedText(g2, strArr[2], 60, getHeight()-140+50, getWidth()-120);
				drawWrappedText(g2, strArr[3], 60, getHeight()-140+75, getWidth()-120);
			}
			return;
		}
		if(showingText)
			drawTextBox(g, currentText);
		else if(buySell||depWith)
		{
			drawTextBox(g, strArr[0]);
			drawWrappedText((Graphics2D)g, strArr[1], 60, getHeight()-140+25, getWidth()-120);
		}
		else if(rareCandy)
		{
			drawTextBox(g, strArr[0]);
			drawWrappedText(g2, strArr[1], 60, getHeight()-140+25, getWidth()-120);
			drawWrappedText(g2, strArr[2], 60, getHeight()-140+50, getWidth()-120);
			drawWrappedText(g2, strArr[3], 60, getHeight()-140+75, getWidth()-120);
		}
    }
	private void drawTextBox(Graphics g, String text) {
		Graphics2D g2 = (Graphics2D) g;
		text=text.replace("🔴", "");
		int boxWidth = getWidth() - 80;
		int boxHeight = 140;
		int x = 40;
		int y = getHeight() - boxHeight - 40;

		// Draw white background
		g2.setColor(Color.WHITE);
		g2.fillRoundRect(x, y, boxWidth, boxHeight, 20, 20);

		// Draw black border
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(4));
		g2.drawRoundRect(x, y, boxWidth, boxHeight, 20, 20);

		// Draw text
		g2.setFont(pokemonFont);
		drawWrappedText(g2, text, x + 20, y + 40, boxWidth - 40);
	}
	private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
		FontMetrics fm = g.getFontMetrics();
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();
		int lineHeight = fm.getHeight();

		for (String word : words) {
			String testLine = line + word + " ";
			if (fm.stringWidth(testLine) > maxWidth) {
				g.drawString(line.toString(), x, y);
				line = new StringBuilder(word + " ");
				y += lineHeight;
			} else {
				line.append(word).append(" ");
			}
		}
		g.drawString(line.toString(), x, y);
	}

    private void drawConnection(Graphics g, int dirIndex, PokeMap conn, int offset, int camX, int camY) {
        int[][] mapGrid = conn.grid;
        int maxY = mapGrid.length;
        int maxX = mapGrid[0].length;

        int baseX = 0, baseY = 0;
        switch (dirIndex) {
            case 0 -> { // NORTH
                baseX = offset * TILE_SIZE;
                baseY = -maxY * TILE_SIZE;
            }
            case 1 -> { // SOUTH
                baseX = offset * TILE_SIZE;
                baseY = currentMap.length * TILE_SIZE;
            }
            case 2 -> { // WEST
                baseX = -maxX * TILE_SIZE;
                baseY = offset * TILE_SIZE;
            }
            case 3 -> { // EAST
                baseX = currentMap[0].length * TILE_SIZE;
                baseY = offset * TILE_SIZE;
            }
        }

        for (int y = 0; y < mapGrid.length; y++) {
            for (int x = 0; x < mapGrid[0].length; x++) {
                BufferedImage tile = tileImages[mapGrid[y][x]];
                g.drawImage(tile, x * TILE_SIZE + baseX - camX, y * TILE_SIZE + baseY - camY, null);
            }
        }
    }

    private int getCurrentFrame() {
        boolean moving = stepPhase == StepPhase.MOVING ||
                         (stepPhase == StepPhase.LANDING && phaseFrame < currentStepFrames / 2);
        return switch (facing) {
            case SOUTH -> moving ? 0 + timesMoved : 1;
            case NORTH -> moving ? 3 + timesMoved : 4;
            case WEST  -> moving ? 7 : 6;
            case EAST  -> moving ? 9 : 8;
        };
    }

    public static void runGUI() {
        SwingUtilities.invokeLater(() -> {
			String name = JOptionPane.showInputDialog("Welcome to Pokemon Purple! Here are the controls, write these down!\n\nP=Show Pokemon\nT=Show TM/HMs\nI=Show Inventory\nX=Show Pokedex\nM=Show Map/fly\nWASD=overworld movement\nArrow keys=Sorting/Box navigation\nClicking=Everything else\n\nWhat is your name?");
			if(name==null||name.isEmpty())
				name="Purple";
			pickingStarter=true;
            JFrame frame = new JFrame("Pokémon Purple");
            OverworldGui viewer = new OverworldGui(frame, name.length()<11?name:name.substring(0, 10));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(viewer);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            viewer.requestFocusInWindow();
			viewer.print("And which starter will you choose?");
        });
    }

    public static void runGUI(Player p, String info) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pokémon Purple");
            OverworldGui viewer = new OverworldGui(frame, p, info);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(viewer);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            viewer.requestFocusInWindow();
        });
    }
}

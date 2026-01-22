package purple;

import java.io.File;
import java.util.Scanner;
public class Main
{
	public static int SHINY_CHANCE=8192;
	public static void main(String[] a)
	{
		Types.buildTypes();
		Monster.buildMonsters();
		Move.buildMoves();
		Learnset.buildLearnsets();
		Evolution.buildEvolutions();
		PokeMap.buildPokeMaps();
		FlyLocation.buildWorldMap();
		Warp.buildWarps();
		TmLearnsets.buildTmLearnsets();
		Encounter.buildEncounterRates();
		Item.buildItems();
		MartItem.buildMartItems();
		Giver.buildGivers();
		Blocker.buildBlockers();
		Trader.buildTraders();
		Npc.buildNpcs();
		try {
			Scanner s=new Scanner(new File("save.txt"));
			String pInfo=s.nextLine(), pcS=s.nextLine(), partyS=s.nextLine(), trainS=s.nextLine(), leadS=s.nextLine(), gioRS=s.nextLine(), wobS=s.nextLine();
			Player p=new Player(pcS, partyS, s.nextLine(), s.nextLine(), s.nextLine());
			String fLoc=s.nextLine();
			for(int i=0; i<fLoc.length();)
				if(fLoc.charAt(i++)=='1')
					FlyLocation.FLY_LOCATIONS.get(FlyLocation.INDEX_MEANINGS[i]).visited=true;
			s.close();
			Trainer.buildTrainers(p, trainS, leadS, gioRS);
			WorldObject.buildWorldObjects(p, wobS);
			OverworldGui.runGUI(p, pInfo);
		} catch(Exception e) {
			e.printStackTrace();
			Trainer.buildTrainers();
			WorldObject.buildWorldObjects();
			OverworldGui.runGUI();
		}
	}
}
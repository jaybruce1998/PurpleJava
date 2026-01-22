package purple;

import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class FlyLocation
{
	static int[][] WORLD_MAP = {
		{ 5, 0, 0, 0, 0, 0, 0,27,38,38, 0},
		{26, 0, 0, 0, 0, 0, 0,27, 0, 0, 0},
		{26, 0, 8,28,28,12,29, 2,34,34,13},
		{26, 0,22, 0, 0, 0, 0,30, 0, 0,15},
		{25,25,11, 0,19, 1,32, 9,33,33, 6},
		{ 0, 0,14, 0,37, 0, 0,31, 0, 0,17},
		{ 0, 0, 7, 0,37, 0, 0,10,16,16,17},
		{ 0, 0,24, 0,37, 0, 0, 0, 0, 0,17},
		{ 0, 0,24, 0,20,20, 4,18,35,36,17},
		{ 0, 0,24, 0, 0, 0,21, 0, 0, 0, 0},
		{ 0, 0, 3,23,23,21,21, 0, 0, 0, 0}
	};
	static String[] INDEX_MEANINGS = {
		null,
		"CeladonCity",
		"CeruleanCity",
		"CinnabarIsland",
		"FuchsiaCity",
		"IndigoPlateau",
		"LavenderTown",
		"PalletTown",
		"PewterCity",
		"SaffronCity",
		"VermilionCity",
		"ViridianCity",
		"MtMoon",
		"RockTunnel",
		"Route1",
		"Route10",
		"Route11",
		"Route12",
		"Route15",
		"Route16",
		"Route18",
		"Route19",
		"Route2",
		"Route20",
		"Route21",
		"Route22",
		"Route23",
		"Route24",
		"Route3",
		"Route4",
		"Route5",
		"Route6",
		"Route7",
		"Route8",
		"Route9",
		"Route14",
		"Route13",
		"Route17",
		"Route25"
	};
	static BufferedImage MAP_IMAGE;
	static String[][] NAME_MAP;
	static Map<String, FlyLocation> FLY_LOCATIONS;
	public static void buildWorldMap()
	{
		try {
			MAP_IMAGE=ImageIO.read(FlyLocation.class.getResourceAsStream("/WORLD_MAP.png"));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		NAME_MAP=new String[11][11];
		for(int i=0; i<11; i++)
			for(int j=0; j<11; j++)
				NAME_MAP[i][j]=INDEX_MEANINGS[WORLD_MAP[i][j]];
		FLY_LOCATIONS=new HashMap<>();
		FlyLocation p=new FlyLocation(PokeMap.POKEMAPS.get("PalletTown"), 5, 6);
		p.visited=true;
		FLY_LOCATIONS.put("PalletTown", p);
		FLY_LOCATIONS.put("IndigoPlateau", new FlyLocation(PokeMap.POKEMAPS.get("IndigoPlateau"), 10, 6));
	}
	public static String normalize(String s)
	{
		String n=s.substring(0, s.length()-10);
		switch(n)
		{
			case "Cinnabar":
				return "CinnabarIsland";
			case "Lavender":
				return "LavenderTown";
			case "MtMoon":
			case "RockTunnel":
				return n;
		}
		return n+"City";
	}
	public static boolean isRed(int i, int j)
	{
		FlyLocation f=FLY_LOCATIONS.get(NAME_MAP[i][j]);
		return f!=null&&!f.visited;
	}
	public static void visit(String name)
	{
		switch(name)
		{
			case "RedsHouse1F":
				name="PalletTown";
				break;
			case "IndigoPlateauLobby":
				name="IndigoPlateau";
		}
		FlyLocation f=FLY_LOCATIONS.get(normalize(name));
		if(f!=null)
			f.visited=true;
	}
	
	boolean visited;
	PokeMap dest;
	int x, y;
	public FlyLocation(PokeMap dest, int x, int y)
	{
		this.dest=dest;
		this.x=x;
		this.y=y;
	}
}
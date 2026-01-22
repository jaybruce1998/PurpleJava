package purple;

import java.util.*;
public class Types
{
	static String[] TYPES={"Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon"};
	static String[] ATTACK_STRINGS="""
111111111111501
155122111112515
125151112111215
112551110211115
152151152515215
115125112211112
211112151555201
111121155112551
121251121015211
111521211112511
111111221151111
151121521521151
121112515212111
011111111101121
111111111111112""".split("\n");
	static Map<String, Integer> TYPE_MAP=new HashMap<>();
	static double[][] TYPE_CHART=new double[TYPES.length][TYPES.length];
	public static double damage(Move move, String[] types)
	{
		double d=1;
		if(move.type.equals("???"))
			return d;
		int i=TYPE_MAP.get(move.type);
		for(String t: types)
			d*=TYPE_CHART[i][TYPE_MAP.get(t)];
		return d;
	}
	public static void buildTypes()
	{
		for(int i=0; i<TYPES.length; i++)
		{
			TYPE_MAP.put(TYPES[i], i);
			for(int j=0; j<TYPES.length; j++)
			{
				char c=ATTACK_STRINGS[i].charAt(j);
				switch(c)
				{
					case '5':
						TYPE_CHART[i][j]=0.5;
						break;
					default:
						TYPE_CHART[i][j]=c-'0';
				}
			}
		}
	}
}
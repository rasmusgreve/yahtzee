package yahtzee;

import player.Player;

public class Play {

	public static void main(String[] args) {
		if (args.length >= 1){
			System.out.println("Trying to load agent: " + args[0]);
			Player p = loadPlayer(args[0]);
			System.out.println("Loaded player: " + p.getName());
		}
		else
		{
			System.out.println("Not enough args");
		}
	}
	
	private static Player loadPlayer(String name)
	{
		Player player;
        try {
            player = (Player) Class.forName (name).newInstance ();
        }
        catch (Exception e) {
            e.printStackTrace ();
            player = null;
            System.exit (1);
        }
        return player;
	}

}

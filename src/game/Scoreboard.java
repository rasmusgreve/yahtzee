package game;

public class Scoreboard {

	int ones = -1;
	int twos = -1;
	int threes = -1;
	int fours = -1;
	int fives = -1;
	int sixes = -1;
	int bonus = -1;
	
	int threeOfAKind = -1;
	int fourOfAKind = -1;
	int fullHouse = -1;
	int smallStraight = -1;
	int bigStraight = -1;
	int yahtzee = -1;
	int chance = -1;
	
	
	
	public void PrintScoreBoard(){
		System.out.println("Scoreboard for player: " + "PLAYERNAMEHERE");
		System.out.println("Ones: " + ones);
		System.out.println("Twos: " + twos);
		System.out.println("Threes: " + threes);
		System.out.println("Fours: " + fours);
		System.out.println("Fives: " + fives);
		System.out.println("Sixes: " + sixes);
		System.out.println("Bonus: " + bonus);
		System.out.println(" ");
		System.out.println("Three of a kind: " + threeOfAKind);
		System.out.println("Four of a kind: " + fourOfAKind);
		System.out.println("Full house: " + fullHouse);
		System.out.println("Small straight: " + smallStraight);
		System.out.println("Big straight: " + bigStraight);
		System.out.println("Yahtzee: " + yahtzee);
		System.out.println("Chance: " + chance);
		System.out.println("--------------");
		
	}
	
	
	
}

package player;

public class RollNode {
	private int[] roll;
	private int rollsLeft;
	public RollNode(int[] roll, int rollsLeft) {
		this.roll = roll;
		this.rollsLeft = rollsLeft;
	}
	public int[] getRoll() {
		return roll;
	}
	public int getRollsLeft() {
		return rollsLeft;
	}
	public void setRoll(int[] roll) {
		this.roll = roll;
	}
	public void setRollsLeft(int rollsLeft) {
		this.rollsLeft = rollsLeft;
	}
	
}

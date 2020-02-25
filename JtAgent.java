package ai.worlds.vacuum;

public class JtAgent extends VacuumAgent {
	State current = new Outline();
	int count = 0; //volatile variable. in outline, is used to find and store sideOne and sideTwo. 
	//otherwise it keeps track of the amount of squares moved during each sweep.
	int bumped = 0; //counts bumps *this should never be > 2
	int turns = 0; //counts turns for 1xn | nx1 rooms
	int forwards = 0; //total squares moved
	int sideOne = -1; //stores x axis length
	int sideTwo = -1; //stores y axis length
	boolean newTurnStatus = false; //flag that decides turning left or right to start a new sweep
	boolean doneTurnStatus = false; //flag that decides turning left or right at the end of a sweep

	public int getAction() {
		//		print data for testing
//		System.out.println("count: " + count );
//		System.out.println("bumps: " + bumped);
//		System.out.println("forwards: " + forwards);
//		System.out.println("turns: " + turns);
//		System.out.println("sideOne: " + sideOne);
//		System.out.println("sideTwo: " + sideTwo);
		//		System.out.println("newTurnStatus: " + newTurnStatus);
		//		System.out.println("doneTurnStatus: " + doneTurnStatus);
		//		System.out.println("_____________________________________________________________________");
		return current.act(this);
	}
	public class TurnAround implements State{ //this state puts the agent onto the next column after a sweep has been finished
		public int act(JtAgent jtAgent){
			current = new Clean();
			count++;
			forwards++;
			return jtAgent.FORWARD;
		}
	}
	public class Outline implements State{ //this state finds the dimensions of the room
		@Override
		public int act(JtAgent jtAgent){
			if(jtAgent.seesDirt()) {
				return jtAgent.SUCK;
			}
			if(jtAgent.bumped()) {
				if(sideTwo == -1 && sideOne > 0) {
					sideTwo = count;
					count = 0;
					forwards = sideOne + sideTwo;
					bumped++;
					turns++;
					current = new Clean();
					return jtAgent.LEFT;
				}
				if(sideOne == -1) {
					sideOne = count;
					count = 0;
				}
				bumped++;
				turns++;
				return jtAgent.LEFT;
			}

			count++;
			forwards++;
			return jtAgent.FORWARD;
		}	
	}
	public class Clean implements State{ //this state vacuums the room after the room's dimensions have been recorded
		@Override
		public int act(JtAgent jtAgent) {
			if(jtAgent.seesDirt()) {
				return jtAgent.SUCK;
			}
			if(sideOne == 1 || sideTwo == 1) { //this block takes care of vacuuming rooms that have a dimension equal to 1
				count = 0;
				System.out.println("im rdy to go home");
				if(count == 0) {
					if(sideTwo == 1) {
						turns = 0;
						if(bumped == 2) { //changes state to goHome since after 2 bumps the room will have been cleaned
							current = new goHome();
							turns++;
							System.out.println("y is 1");
							return jtAgent.SUCK;
						}
					}
					if(sideOne == 1) {
						if(turns == 2) { //changes state to goHome after 2 turns because if room size x = 1 it should be facing home
							current = new goHome();
							turns++;
							System.out.println("x is 1");
							return jtAgent.LEFT;
						}
					}
					turns++;
					return jtAgent.LEFT;
				}
			}
			//here to the end of the state takes care of rooms where x & y > 1
			if(forwards == (sideOne * sideTwo) + 1 && sideOne % 2 == 1) { 
				//checks if a room where x is odd is fully vacuumed and changes state to goHome
				count = 0;
				//doneTurnStatus = false;
				current = new goHome();
				return jtAgent.LEFT;
			}
			if(forwards == (sideOne * sideTwo) + 1) { //checks if a room where x is even is fully vacuumed and changes state to goHome
				count = 0;
				current = new goHome();
				return jtAgent.FORWARD;
			}
			if(count == 0) {
				count++;
				forwards++;
				return jtAgent.FORWARD;
			}
			if(count == 1 ) {
				if( newTurnStatus == false) {
					newTurnStatus = true;
					current = new TurnAround();
					return jtAgent.LEFT;
				}
				newTurnStatus = false;
				current = new TurnAround();
				return jtAgent.RIGHT;
			}
			if(count == sideTwo - 1) {

				if( doneTurnStatus == true) {

					count = 0;
					doneTurnStatus = false;
					current = new TurnAround();
					return jtAgent.LEFT;
				}
				count = 0;
				doneTurnStatus = true;
				current = new TurnAround();
				return jtAgent.RIGHT;
			}
			if(count < sideTwo - 1) {
				count++;
				forwards++;
				return jtAgent.FORWARD;
			}
			return 0;
		}
	}
	public class goHome implements State{ //returns the agent home
		public int act(JtAgent jtAgent){
			if(sideOne == 1 || sideTwo == 1) { //takes care of rooms where x | y = 1
				if(sideOne > sideTwo) { //checks if y = 1
					if(count < sideOne - 1) {
						count++;
						return jtAgent.FORWARD;
					}
					if(count == sideTwo - 1 && jtAgent.isHome()) {
						System.out.println("Goodbye UwU");
						return 0;
					}
				}
				if(count < sideTwo - 1) {
					count++;
					return jtAgent.FORWARD;
				}
				if(count == sideTwo - 1 && jtAgent.isHome()) {
					System.out.println("Goodbye UwU");
					return 0;
				}
			}
			if(sideOne % 2 == 1) { //checks if x is odd because if true, at check the agent will be in the corner above home
				if(count == 0) {
					count++;
					return jtAgent.LEFT;
				}
				if(count < sideTwo ) {
					count++;
					return jtAgent.FORWARD;
				}
				if(count == sideTwo - 1 && jtAgent.isHome()) {
					System.out.println("Goodbye UwU");
					return 0;
				}
			}
			System.out.println("Goodbye UwU");
			return 0;
		}
	}
}


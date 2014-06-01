package bot;

import java.util.ArrayList;

import main.Map;
import main.Region;
import main.SuperRegion;

import move.AttackTransferMove;
import move.PlaceArmiesMove;
import move.Move;

public class BotState {
	
	private String myName = "";
	private String opponentName = "";
	
	private final Map fullMap = new Map(); //This map is known from the start, contains all the regions and how they are connected, doesn't change after initialization
	
	private Map visibleMap; //This map represents everything the player can see, updated at the end of each round.
	
	private ArrayList<Region> pickableStartingRegions; //2 randomly chosen regions from each superregion are given, which the bot can chose to start with
	
	private ArrayList<Move> opponentMoves; //list of all the opponent's moves, reset at the end of each round
	
	private ArrayList<AttackTransferMove> opponentAttackMoves;
	
	private ArrayList<PlaceArmiesMove> opponentPlaceMoves;
	 
	private ArrayList<Map> mapHistory;
	
	private ArrayList<ArrayList<PlaceArmiesMove>> opponentPlaceArmiesHistory;
	
	private ArrayList<ArrayList<AttackTransferMove>> opponentAttackMovesHistory;

	private ArrayList<AttackTransferMove> scheduledAttackTransferMoves0;
	
	private ArrayList<AttackTransferMove> scheduledAttackTransferMoves1;
	
	private ArrayList<AttackTransferMove> scheduledAttackTransferMoves2;
	
	private ArrayList<AttackTransferMove> scheduledAttackTransferMoves3;
	
	private ArrayList<AttackTransferMove> scheduledAttackTransferMoves4;

	private int startingArmies; //number of armies the player can place on map
	
	private int roundNumber;
	
	public BotState()
	{
		pickableStartingRegions = new ArrayList<Region>();
		opponentMoves = new ArrayList<Move>();
		opponentPlaceMoves = new ArrayList<PlaceArmiesMove>();
		opponentAttackMoves = new ArrayList<AttackTransferMove>();
		mapHistory = new ArrayList<Map>();
		opponentPlaceArmiesHistory = new ArrayList<ArrayList<PlaceArmiesMove>>();
		opponentAttackMovesHistory = new ArrayList<ArrayList<AttackTransferMove>>();
		roundNumber = 0;
		scheduledAttackTransferMoves0 = new ArrayList<AttackTransferMove>();
		scheduledAttackTransferMoves1 = new ArrayList<AttackTransferMove>();
		scheduledAttackTransferMoves2 = new ArrayList<AttackTransferMove>();
		scheduledAttackTransferMoves3 = new ArrayList<AttackTransferMove>();
		scheduledAttackTransferMoves4 = new ArrayList<AttackTransferMove>();
	}
	
	public void updateSettings(String key, String value)
	{
		if(key.equals("your_bot")) //bot's own name
			myName = value;
		else if(key.equals("opponent_bot")) //opponent's name
			opponentName = value;
		else if(key.equals("starting_armies")) 
		{
			startingArmies = Integer.parseInt(value);
			roundNumber++; //next round
		}
	}
	
	//initial map is given to the bot with all the information except for player and armies info
	public void setupMap(String[] mapInput)
	{
		int i, regionId, superRegionId, reward;
		
		if(mapInput[1].equals("super_regions"))
		{
			for(i=2; i<mapInput.length; i++)
			{
				try {
					superRegionId = Integer.parseInt(mapInput[i]);
					i++;
					reward = Integer.parseInt(mapInput[i]);
					fullMap.add(new SuperRegion(superRegionId, reward));
				}
				catch(Exception e) {
					System.err.println("Unable to parse SuperRegions");
				}
			}
		}
		else if(mapInput[1].equals("regions"))
		{
			for(i=2; i<mapInput.length; i++)
			{
				try {
					regionId = Integer.parseInt(mapInput[i]);
					i++;
					superRegionId = Integer.parseInt(mapInput[i]);
					SuperRegion superRegion = fullMap.getSuperRegion(superRegionId);
					fullMap.add(new Region(regionId, superRegion));
				}
				catch(Exception e) {
					System.err.println("Unable to parse Regions " + e.getMessage());
				}
			}
		}
		else if(mapInput[1].equals("neighbors"))
		{
			for(i=2; i<mapInput.length; i++)
			{
				try {
					Region region = fullMap.getRegion(Integer.parseInt(mapInput[i]));
					i++;
					String[] neighborIds = mapInput[i].split(",");
					for(int j=0; j<neighborIds.length; j++)
					{
						Region neighbor = fullMap.getRegion(Integer.parseInt(neighborIds[j]));
						region.addNeighbor(neighbor);
					}
				}
				catch(Exception e) {
					System.err.println("Unable to parse Neighbors " + e.getMessage());
				}
			}
		}
	}
	
	//regions from wich a player is able to pick his preferred starting regions
	public void setPickableStartingRegions(String[] mapInput)
	{
		for(int i=2; i<mapInput.length; i++)
		{
			int regionId;
			try {
				regionId = Integer.parseInt(mapInput[i]);
				Region pickableRegion = fullMap.getRegion(regionId);
				pickableStartingRegions.add(pickableRegion);
			}
			catch(Exception e) {
				System.err.println("Unable to parse pickable regions " + e.getMessage());
			}
		}
	}
	
	//visible regions are given to the bot with player and armies info
	public void updateMap(String[] mapInput)
	{
		
		visibleMap = fullMap.getMapCopy();
		for(int i=1; i<mapInput.length; i++)
		{
			try {
				Region region = visibleMap.getRegion(Integer.parseInt(mapInput[i]));
				String playerName = mapInput[i+1];
				int armies = Integer.parseInt(mapInput[i+2]);
				
				region.setPlayerName(playerName);
				region.setArmies(armies);
				i += 2;
			}
			catch(Exception e) {
				System.err.println("Unable to parse Map Update " + e.getMessage());
			}
		}
		mapHistory.add(visibleMap.getMapCopy());
	}

	//Parses a list of the opponent's moves every round. 
	//Clears it at the start, so only the moves of this round are stored.
	public void readOpponentMoves(String[] moveInput)
	{
		opponentMoves.clear();
		opponentPlaceMoves.clear();
		opponentAttackMoves.clear();
		for(int i=1; i<moveInput.length; i++)
		{
			try {
				Move move;
				if(moveInput[i+1].equals("place_armies")) {
					Region region = visibleMap.getRegion(Integer.parseInt(moveInput[i+2]));
					String playerName = moveInput[i];
					int armies = Integer.parseInt(moveInput[i+3]);
					move = new PlaceArmiesMove(playerName, region, armies);
					opponentPlaceMoves.add(new PlaceArmiesMove(playerName, region, armies));
					i += 3;
				}
				else if(moveInput[i+1].equals("attack/transfer")) {
					Region fromRegion = visibleMap.getRegion(Integer.parseInt(moveInput[i+2]));
					if(fromRegion == null) //might happen if the region isn't visible
						fromRegion = fullMap.getRegion(Integer.parseInt(moveInput[i+2]));

					Region toRegion = visibleMap.getRegion(Integer.parseInt(moveInput[i+3]));
					if(toRegion == null) //might happen if the region isn't visible
						toRegion = fullMap.getRegion(Integer.parseInt(moveInput[i+3]));

					String playerName = moveInput[i];
					int armies = Integer.parseInt(moveInput[i+4]);
					move = new AttackTransferMove(playerName, fromRegion, toRegion, armies);
					opponentAttackMoves.add(new AttackTransferMove(playerName, fromRegion, toRegion, armies));
					i += 4;
				}
				else { //never happens
					continue;
				}
				opponentMoves.add(move);
			}
			catch(Exception e) {
				System.err.println("Unable to parse Opponent moves " + e.getMessage());
			}
		}
		opponentPlaceArmiesHistory.add(getAllPlaceArmiesMovesCopy(opponentPlaceMoves));
		opponentAttackMovesHistory.add(getAllAttackTransferMovesCopy(opponentAttackMoves));
	}
	
	public String getMyPlayerName(){
		return myName;
	}
	
	public String getOpponentPlayerName(){
		return opponentName;
	}
	
	public int getStartingArmies(){
		return startingArmies;
	}
	
	public int getRoundNumber(){
		return roundNumber;
	}
	
	public Map getVisibleMap(){
		return visibleMap;
	}
	
	public Map getFullMap(){
		return fullMap;
	}

	public ArrayList<Move> getOpponentMoves(){
		return opponentMoves;
	}
	public ArrayList<AttackTransferMove> getOpponentAttackTransferMoves(){
		return opponentAttackMoves;
	}
	public ArrayList<PlaceArmiesMove> getOpponentPlaceArmiesMoves(){
		return opponentPlaceMoves;
	}
	
	public ArrayList<Region> getPickableStartingRegions(){
		return pickableStartingRegions;
	}
	public ArrayList<AttackTransferMove> getScheduledAttackTransferMoves0(){
		return scheduledAttackTransferMoves0;
	}
	public void setScheduledAttackTransferMoves0(ArrayList<AttackTransferMove> list){
		this.scheduledAttackTransferMoves0 = list;
	}
	public ArrayList<AttackTransferMove> getScheduledAttackTransferMoves1(){
		return scheduledAttackTransferMoves1;
	}
	public void setScheduledAttackTransferMoves1(ArrayList<AttackTransferMove> list){
		this.scheduledAttackTransferMoves1 = list;
	}
	public ArrayList<AttackTransferMove> getScheduledAttackTransferMoves2(){
		return scheduledAttackTransferMoves2;
	}
	public void setScheduledAttackTransferMoves2(ArrayList<AttackTransferMove> list){
		this.scheduledAttackTransferMoves2 = list;
	}
	public ArrayList<AttackTransferMove> getScheduledAttackTransferMoves3(){
		return scheduledAttackTransferMoves3;
	}
	public void setScheduledAttackTransferMoves3(ArrayList<AttackTransferMove> list){
		this.scheduledAttackTransferMoves3 = list;
	}
	public ArrayList<AttackTransferMove> getScheduledAttackTransferMoves4(){
		return scheduledAttackTransferMoves4;
	}
	public void setScheduledAttackTransferMoves4(ArrayList<AttackTransferMove> list){
		this.scheduledAttackTransferMoves4 = list;
	}
	
	public PlaceArmiesMove getPlaceArmiesMoveCopy(PlaceArmiesMove placeMove){
		PlaceArmiesMove newMove = new PlaceArmiesMove(placeMove.getPlayerName(), placeMove.getRegion(), placeMove.getArmies() );
		return newMove;
	}
	public ArrayList<PlaceArmiesMove> getAllPlaceArmiesMovesCopy(ArrayList<PlaceArmiesMove> list){
		ArrayList<PlaceArmiesMove> newList = new ArrayList<PlaceArmiesMove>();
		for (PlaceArmiesMove move : list){
			newList.add(getPlaceArmiesMoveCopy(move));
		}
		return newList;
	}
	public AttackTransferMove getAttackTransferMoveCopy(AttackTransferMove attackMove){
		AttackTransferMove newMove = new AttackTransferMove(attackMove.getPlayerName(), attackMove.getFromRegion(), attackMove.getToRegion(), attackMove.getArmies() );
		return newMove;
	}
	public ArrayList<AttackTransferMove> getAllAttackTransferMovesCopy(ArrayList<AttackTransferMove> list){
		ArrayList<AttackTransferMove> newList = new ArrayList<AttackTransferMove>();
		for (AttackTransferMove move : list){
			newList.add(getAttackTransferMoveCopy(move));
		}
		return newList;
	}
	public ArrayList<ArrayList<PlaceArmiesMove>> getOpponentPlaceArmiesHistory(){
		return opponentPlaceArmiesHistory;
	}
	public ArrayList<ArrayList<AttackTransferMove>> getopponentAttackMovesHistory(){
		return opponentAttackMovesHistory;
	}
	public ArrayList<Map> getMapHistory(){
		return mapHistory;
	}
}

package bot;



import java.util.ArrayList;
import java.util.LinkedList;

import main.Map;
import main.Region;
import main.SuperRegion;
import move.AttackTransferMove;
import move.PlaceArmiesMove;


public class BotStarter implements Bot 
{
	public void setHeuristics(BotState state, Region fromRegion){
		for (Region reg : state.getVisibleMap().getRegions()){
			reg.setPathN(0);
		}
		for (Region reg : fromRegion.getNeighbors()){
			reg.setPathN(1);
		}
		for (int n=1; n<13; n++){
			for (Region reg : state.getVisibleMap().getRegions()){
				if (reg.getPathN()==n){
					for (Region neig : reg.getNeighbors() ){
						if (neig.getPathN()==0 && !neig.equals(fromRegion)){
							neig.setPathN(reg.getPathN()+1);
						}
					}
				}
			}
		}
	}
	public int getDistance(BotState state, Region fromRegion, Region toRegion){
		setHeuristics(state, fromRegion);
		System.err.print("Distance from Region " + fromRegion.getId() + "to Region " + toRegion.getId() + " : ");
			System.err.println(toRegion.getPathN());
		return toRegion.getPathN();
	}
	public int getDistanceFromClosestAlly(BotState state, LinkedList<Region> myRegions, Region fromRegion){
		int d=20;
		setHeuristics(state, fromRegion);
		for (Region reg : myRegions ){
			if (reg.getPathN()<d){
				d=reg.getPathN();
			}
		}
		return d;
	}
	public LinkedList<Region> getPath(BotState state, Region fromRegion, Region toRegion){
		LinkedList<Region> path = new LinkedList<Region>();
		setHeuristics(state, fromRegion);
		for (int i=1; i<toRegion.getPathN(); i++){
			for (Region reg : state.getVisibleMap().getRegions()){
				if (reg.getPathN()==i){
					path.add(reg);
					break;
				}
			}
		}
		System.err.print("path from Region " + fromRegion.getId() + "to Region " + toRegion.getId() + " :");
		for (Region reg : path){
			System.err.print(" " + reg.getId() +";");
		}
		System.err.println("");
		return path;
	}
	public void addUnitPath (Region fromRegion){
		for (Region reg : fromRegion.getNeighbors()){
			if (reg.getPathN()!=(fromRegion.getPathN() ) ){
				reg.setPathN(fromRegion.getPathN()+1);
			}
		}
	}
	public static int getLineNumber() {
	    return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}
	public int getOpponentReinforcement(BotState state){
		int opponentReinforcement = 0;
		String opponentName = state.getOpponentPlayerName();
		
		for (PlaceArmiesMove placeMove : state.getOpponentPlaceArmiesMoves()){
			if (placeMove.getRegion().ownedByPlayer(opponentName)){
				opponentReinforcement+=placeMove.getArmies();
			}
		}
		return Math.max(5, opponentReinforcement);
	}
	public LinkedList<Integer> convertToId( LinkedList<Region> list){
		LinkedList<Integer> newList = new LinkedList<Integer>();
		for (Region reg : list){
			newList.add(reg.getId());
		}
		return newList;
	}
	public LinkedList<Region> getMyRegions(BotState state, Map map){
		String myName = state.getMyPlayerName();
		LinkedList<Region> newList = new LinkedList<Region>();
		for (Region reg : map.getRegions()){
			if (reg.ownedByPlayer(myName)){
				newList.add(reg);
			}
		}
		return newList;
	}
	public LinkedList<Region> getOpponentRegions(BotState state, Map map){
		String opponentName = state.getOpponentPlayerName();
		LinkedList<Region> newList = new LinkedList<Region>();
		for (Region reg : map.getRegions()){
			if (reg.ownedByPlayer(opponentName)){
				newList.add(reg);
			}
		}
		return newList;
	}
	public LinkedList<Region> getNeutralRegions(BotState state, Map map){
		LinkedList<Region> newList = new LinkedList<Region>();
		for (Region reg : map.getRegions()){
			if (reg.ownedByPlayer("neutral")){
				newList.add(reg);
			}
		}
		return newList;
	}
	
	public int getReinforcementOnRegion(BotState state, Region reg){
		int reinforcementOnRegion=0; 
		for (PlaceArmiesMove placeMove : state.getOpponentPlaceArmiesMoves()){
			if (placeMove.getRegion().equals(reg)){
				reinforcementOnRegion+=placeMove.getArmies();
			}	
		}
		return reinforcementOnRegion;
	}
	
	public boolean checkReinforcementOnRegion(BotState state, Region reg) {
		for (PlaceArmiesMove placeMove : state.getOpponentPlaceArmiesMoves()){
			if (placeMove.getRegion().equals(reg)){
				return true;
			}
		}
		return false;	
	}
	
	public boolean checkAttackOnRegion(BotState state, Region reg) {	
		for (AttackTransferMove attMove : state.getOpponentAttackTransferMoves()){
			if (attMove.getToRegion().equals(reg)){
				return true;
			}
		}
		return false;	
	}
	public boolean checkIfSuperRegionIsBorderedByEnnemy(BotState state, SuperRegion sr){
		String opponentName = state.getOpponentPlayerName();
		for (Region reg : sr.getSubRegions()){
			if (!getNeighborsOwnedByPlayerName(reg,opponentName).isEmpty()){
				return true;
			}
		}
		return false;
	}
	public boolean checkIfBorderingPriorityNeutrals(LinkedList<Region> list, Region reg){
		for (Region neig : reg.getNeighbors()){
			if (list.contains(neig)){
				return true;
			}
		}
		return false;
	}
	public LinkedList<Region> getNeighborsOwnedByPlayerName(Region reg, String playerName) {
		LinkedList<Region> neighborsOwnedByPlayerName = new LinkedList<Region>() ;
		for (Region neig : reg.getNeighbors() ){
			if ( neig.ownedByPlayer(playerName) ){
				neighborsOwnedByPlayerName.add(neig);
			}
		}	
		return neighborsOwnedByPlayerName;
	}
	
	public LinkedList<Region> getSubRegionsOwnedByPlayerName(SuperRegion sr, String playerName) {
		LinkedList<Region> subRegionsOwnedByPlayerName = new LinkedList<Region>();
		
		for (Region reg : sr.getSubRegions() ){
			if (reg.ownedByPlayer(playerName)){
				subRegionsOwnedByPlayerName.add(reg);
			}
		}
		return subRegionsOwnedByPlayerName;		
	}

	public Region getRegionWithMaxArmiesOfTheList(LinkedList<Region> list){
		Region regMax = list.get(0);
		for (Region reg : list){
			if ( reg.getArmies() > regMax.getArmies() ){
				regMax=reg;
			}
		}
		return regMax;
	}
	
	public Region getRegionWithMinArmiesOfTheList(LinkedList<Region> list){
		Region regMin = list.get(0);
		for (Region reg : list){
			if ( reg.getArmies() < regMin.getArmies() ){
				regMin=reg;
			}
		}
		return regMin;
	}
	
	public boolean checkListContainsOwnedSR(LinkedList<Region> list, BotState state){
		String myName = state.getMyPlayerName();
		for (Region neig : list){
			if ( neig.getSuperRegion().ownedByPlayer(myName)){
				return true;
			}
		}
		return false;
	}
	public boolean checkListContainsAlmostOwnedSR(LinkedList<Region> list, BotState state){
		String opponentName = state.getOpponentPlayerName();
		String myName = state.getMyPlayerName();
		for (Region neig : list){
			if (getSubRegionsOwnedByPlayerName(neig.getSuperRegion(), myName).size() >= neig.getSuperRegion().getSubRegions().size()-2 && getSubRegionsOwnedByPlayerName(neig.getSuperRegion(), opponentName).isEmpty()){ 
				return true;
			}
		}
		return false;
	}
	public boolean checkIsolatedInDangerousSuperRegion(BotState state, Region reg){
		String myName = state.getMyPlayerName();
		String opponentName = state.getOpponentPlayerName();
		if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && getNeighborsOwnedByPlayerName(reg, myName).isEmpty() && !getSubRegionsOwnedByPlayerName(reg.getSuperRegion(), opponentName).isEmpty()){
				return true;
		}
		return false;
	}
	@Override
	/**
	 * A method used at the start of the game to decide which player start with what Regions. 6 Regions are required to be returned.
	 * This example randomly picks 6 regions from the pickable starting Regions given by the engine.
	 * @return : a list of m (m=6) Regions starting with the most preferred Region and ending with the least preferred Region to start with 
	 */
	public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut)
	{
		ArrayList<Region> preferredStartingRegions = new ArrayList<Region>();
		//id preferred picks from better to worse
	    int preferredStartingRegionsIdIni1[]={12,10,11,13,41,40,39,42,21,22,20,18,23,24,19,15,25,26,17,14,16};
	    int preferredStartingRegionsIdIni2[]={39,41,40,42,12,10,11,13,21,22,23,24,25,26,20,18,19,15,17,14,16};
	    int preferredStartingRegionsIdIni3[]={12,10,11,13,41,40,39,42,38,21,22,23,24,25,26,1,2,3,4,5,6,7,8,9,20,18,19,15,17,14};
	    int preferredStartingRegionsIdIni4[]={41,40,39,42,12,10,11,13,9,21,22,23,24,25,26,20,18,1,2,3,4,5,6,7,8,19,15,17,14,16};
	    
	   // 
	    
	    if (!state.getPickableStartingRegions().contains(state.getFullMap().getRegion(12)) && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(21)) ){
			for (int j=0; j<(int)(preferredStartingRegionsIdIni2.length); j++){
				if ( preferredStartingRegions.size()<6 && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(preferredStartingRegionsIdIni2[j])) ){
					preferredStartingRegions.add(state.getFullMap().getRegion(preferredStartingRegionsIdIni2[j]));
				}	
			}
	    }else if (!state.getPickableStartingRegions().contains(state.getFullMap().getRegion(39)) && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(38)) ){
			for (int j=0; j<(int)(preferredStartingRegionsIdIni3.length); j++){
				if ( preferredStartingRegions.size()<6 && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(preferredStartingRegionsIdIni3[j])) ){
					preferredStartingRegions.add(state.getFullMap().getRegion(preferredStartingRegionsIdIni3[j]));
				}	
			}
	    }else if(!state.getPickableStartingRegions().contains(state.getFullMap().getRegion(10)) && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(9)) ){
	    	for (int j=0; j<(int)(preferredStartingRegionsIdIni4.length); j++){
				if ( preferredStartingRegions.size()<6 && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(preferredStartingRegionsIdIni4[j])) ){
					preferredStartingRegions.add(state.getFullMap().getRegion(preferredStartingRegionsIdIni4[j]));
				}	
			}
	    }else{
			for (int j=0; j<(int)(preferredStartingRegionsIdIni1.length); j++){
				if ( preferredStartingRegions.size()<6 && state.getPickableStartingRegions().contains(state.getFullMap().getRegion(preferredStartingRegionsIdIni1[j])) ){
					preferredStartingRegions.add(state.getFullMap().getRegion(preferredStartingRegionsIdIni1[j]));
				}	
			}
	    }
	    	
		
		if (state.getRoundNumber()<1){
			System.err.print("preferredStartingRegions: ");
			for (Region region : preferredStartingRegions){
				System.err.print(" " + region.getId());
			}
			System.err.println("");
		}
			
		return preferredStartingRegions;
	}

	@Override
	/**
	 * This method is called for at first part of each round. This example puts two armies on random regions
	 * until he has no more armies left to place.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) 
	{
		
		long startTime = System.nanoTime();
		ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
		String myName = state.getMyPlayerName();
		String opponentName = state.getOpponentPlayerName();
//List of regions re-organized
		int[] allRegions={42,41,39,40,10,12,13,11,21,22,23,24,25,26,9,7,4,1,8,5,3,6,2,1,20,36,17,18,14,19,15,16,38,37,33,32,34,28,27,35,31,30,29};
	
// List of visible regions		
		LinkedList<Region> visibleRegions = new LinkedList<Region>();
		for (int i=0;i<allRegions.length;i++){
			visibleRegions.add(state.getVisibleMap().getRegion(allRegions[i]));
		}	
// List of regions under attack
	LinkedList<Region> underAttackRegions = new LinkedList<Region>();
	for ( Region reg : visibleRegions ){
		if (reg.ownedByPlayer(myName) && !getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
			underAttackRegions.add(reg);
		}
	}
	if (state.getRoundNumber()==1){
		if (!underAttackRegions.isEmpty()){
			for (Region reg : underAttackRegions ){
				if (reg.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(4))){
					underAttackRegions.remove(reg);
				}
			}
		}
	}
// List of regions I own
	LinkedList<Region> myRegions = new LinkedList<Region>();
	
	for (Region reg : visibleRegions){
		if(reg.ownedByPlayer(myName)) {
			myRegions.add(reg);
		}
	}
// List of Opponent Regions
	LinkedList<Region> opponentRegions = new LinkedList<Region>();
	
	for (Region reg : visibleRegions){
		if(reg.ownedByPlayer(opponentName)) {
			opponentRegions.add(reg);
		}
	}
	
// List of neutral Regions	
	LinkedList<Region> neutralRegions = new LinkedList<Region>();
	
	for (Region neut : visibleRegions){
		if(neut.ownedByPlayer("neutral")) {
			neutralRegions.add(neut);
		}
	}
// Looking for picks we lost to the ennemy	
	ArrayList<Region> preferredStartingRegions=new ArrayList<Region>();
	for (Region region : getPreferredStartingRegions(state, (long) 2000)){
		preferredStartingRegions.add(state.getVisibleMap().getRegion(region.getId()));
	}
	
	int n=0;
	for (Region reg : preferredStartingRegions){
		if (reg.ownedByPlayer(myName)){
			n++;
		}
		else if (n<3 && !opponentRegions.contains(reg)){ 
				reg.setPlayerName(opponentName);
				opponentRegions.add(reg);
		}
	}	
	
//Special Regions
	LinkedList<Region> specialPriorityRegions = new LinkedList<Region>();//special priority	
	if ( getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), myName).size()>=state.getVisibleMap().getSuperRegion(2).getSubRegions().size()-1 && getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), opponentName).isEmpty() ){
		if (state.getVisibleMap().getRegion(21).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(21).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(21));
		}
	}
	else if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), myName).isEmpty()){
		if (state.getVisibleMap().getRegion(21).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(21).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(21));
		}
	}
	
	if ( getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6), myName).isEmpty() && underAttackRegions.isEmpty()){
		if (state.getVisibleMap().getRegion(23).ownedByPlayer("neutral")){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(23));
		}else if (state.getVisibleMap().getRegion(36).ownedByPlayer("neutral") ){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(36));
		}else if (state.getVisibleMap().getRegion(37).ownedByPlayer("neutral") ){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(37));
		}else if (state.getVisibleMap().getRegion(38).ownedByPlayer("neutral") ){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(38));
		}
		
	}
	if ( state.getVisibleMap().getSuperRegion(6).ownedByPlayer(myName) && getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4), myName).isEmpty() && getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(3), myName).isEmpty() && !state.getVisibleMap().getRegion(12).ownedByPlayer(myName)){
		if (state.getVisibleMap().getRegion(22).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(22).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(22));
		}else if(state.getVisibleMap().getRegion(36).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(36).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(36));
		}else if(state.getVisibleMap().getRegion(37).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(37).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(37));
		}else if (state.getVisibleMap().getRegion(38).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(38).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(38));
		}
	}
	if (state.getVisibleMap().getSuperRegion(4).ownedByPlayer(myName) && getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6), myName).isEmpty() ) {
		if (state.getVisibleMap().getRegion(38).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(38).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(38));
		}else if (state.getVisibleMap().getRegion(37).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(37).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(37));
		}else if (state.getVisibleMap().getRegion(36).ownedByPlayer("neutral") || state.getVisibleMap().getRegion(36).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(36));
		}
	}
	if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4), myName).isEmpty() && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(3), myName).isEmpty() && getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), myName).isEmpty()){
		if (state.getVisibleMap().getRegion(21).ownedByPlayer("neutral")|| state.getVisibleMap().getRegion(21).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(21));
		}else if (state.getVisibleMap().getRegion(18).ownedByPlayer("neutral")|| state.getVisibleMap().getRegion(18).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(18));
		}else if (state.getVisibleMap().getRegion(20).ownedByPlayer("neutral")|| state.getVisibleMap().getRegion(20).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(20));
		}else if (state.getVisibleMap().getRegion(19).ownedByPlayer("neutral")|| state.getVisibleMap().getRegion(19).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(19));
		}else if (state.getVisibleMap().getRegion(15).ownedByPlayer("neutral")|| state.getVisibleMap().getRegion(15).ownedByPlayer(opponentName)){
			specialPriorityRegions.add( state.getVisibleMap().getRegion(15));
		}
	}

// Lists to prioritize neutrals to be taken
	LinkedList<Region> priorityNeutralRegions_0 = new LinkedList<Region>();//derniere region a conquerir pour avoir la SuperRegion
	LinkedList<Region> priorityNeutralRegions_1 = new LinkedList<Region>();//dernieres 2 regionss a conquerir pour avoir la SuperRegion
	LinkedList<Region> priorityNeutralRegions_2 = new LinkedList<Region>();// 3 dernieres regions a conquerir pour avoir la SuperRegion
	LinkedList<Region> priorityNeutralRegions_3 = new LinkedList<Region>();// regions faisant partie d'une SuperRegion que j'ai commence a prendre
	LinkedList<Region> priorityNeutralRegions_4 = new LinkedList<Region>();// autres
	LinkedList<Region> priorityNeutralRegions_5 = new LinkedList<Region>();// autres2
// prio 0					
	for (Region neut : neutralRegions){
		if ( getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), myName).size()==neut.getSuperRegion().getSubRegions().size()-1 ){
			priorityNeutralRegions_0.add(neut);
		}
	}	
	for (Region neut : neutralRegions){
		if (getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), myName).size()==neut.getSuperRegion().getSubRegions().size()-2 && getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), opponentName).isEmpty() && !priorityNeutralRegions_0.contains(neut) && !priorityNeutralRegions_1.contains(neut) ){
			priorityNeutralRegions_1.add(neut);
		}
	}

	if (state.getVisibleMap().getSuperRegion(5).ownedByPlayer(myName) && state.getVisibleMap().getRegion(17).ownedByPlayer("neutral")){
		priorityNeutralRegions_2.add(state.getVisibleMap().getRegion(17));
	}
	if (state.getVisibleMap().getSuperRegion(4).ownedByPlayer(myName) && state.getVisibleMap().getRegion(20).ownedByPlayer("neutral")){
		priorityNeutralRegions_2.add(state.getVisibleMap().getRegion(20));
	}
	if (state.getVisibleMap().getSuperRegion(1).ownedByPlayer(myName) && state.getVisibleMap().getRegion(14).ownedByPlayer("neutral")){
		priorityNeutralRegions_2.add(state.getVisibleMap().getRegion(14));
	}
	if (state.getVisibleMap().getSuperRegion(3).ownedByPlayer(myName) && state.getVisibleMap().getRegion(3).ownedByPlayer("neutral")){
		priorityNeutralRegions_2.add(state.getVisibleMap().getRegion(3));
	}
	
	for (Region neut : neutralRegions){
		if (getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), myName).size()==neut.getSuperRegion().getSubRegions().size()-3 && getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), opponentName).isEmpty() && !priorityNeutralRegions_0.contains(neut) && !priorityNeutralRegions_1.contains(neut) && !priorityNeutralRegions_2.contains(neut)){
			priorityNeutralRegions_2.add(neut);
		}
	}
	if (state.getVisibleMap().getRegion(10).ownedByPlayer(myName) && state.getVisibleMap().getRegion(9).ownedByPlayer("neutral")){
		priorityNeutralRegions_3.add(state.getVisibleMap().getRegion(9));
	}
	for (Region neut : neutralRegions){
		if (getNeighborsOwnedByPlayerName(neut, opponentName).isEmpty() && getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), opponentName).isEmpty() && !getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), myName).isEmpty() && !priorityNeutralRegions_0.contains(neut) && !priorityNeutralRegions_1.contains(neut) && !priorityNeutralRegions_2.contains(neut)){
			priorityNeutralRegions_3.add(neut);
		}
	}
	for (Region neut : neutralRegions){
		if (getNeighborsOwnedByPlayerName(neut, opponentName).isEmpty() && getSubRegionsOwnedByPlayerName(neut.getSuperRegion(), opponentName).isEmpty() && !priorityNeutralRegions_0.contains(neut) && !priorityNeutralRegions_1.contains(neut) && !priorityNeutralRegions_2.contains(neut) && !priorityNeutralRegions_3.contains(neut) && !priorityNeutralRegions_4.contains(neut) ){
			priorityNeutralRegions_4.add(neut);
		}
	}
	for (Region neut : neutralRegions){
		if (getNeighborsOwnedByPlayerName(neut, opponentName).isEmpty() && !priorityNeutralRegions_0.contains(neut) && !priorityNeutralRegions_1.contains(neut) && !priorityNeutralRegions_2.contains(neut) && !priorityNeutralRegions_3.contains(neut) && !priorityNeutralRegions_4.contains(neut) ){
			priorityNeutralRegions_5.add(neut);
		}
	}

//Liste de mes regions "safe" (entourees d'allies)
		LinkedList<Region> safeRegions = new LinkedList<Region>();
		for (Region reg : myRegions){
			if ( getNeighborsOwnedByPlayerName(reg, myName).size() == reg.getNeighbors().size() ){
				safeRegions.add(reg);
			}
		}
// Lists to prioritize deployment on my regions
	LinkedList<Region> priorityMyRegions_0 = new LinkedList<Region>();
	LinkedList<Region> priorityMyRegions_1 = new LinkedList<Region>();
	LinkedList<Region> priorityMyRegions_2 = new LinkedList<Region>();
	LinkedList<Region> priorityMyRegions_3 = new LinkedList<Region>();
	LinkedList<Region> isolatedInDangerousSuperRegion = new LinkedList<Region>();
	
	for ( Region reg : underAttackRegions ){
		if ( reg.getSuperRegion().ownedByPlayer(myName) ){
			priorityMyRegions_0.add(0,reg);
		}
		else if (getSubRegionsOwnedByPlayerName(reg.getSuperRegion(), myName).size()>reg.getSuperRegion().getSubRegions().size()-3 && getSubRegionsOwnedByPlayerName(reg.getSuperRegion(), opponentName).isEmpty()){
			priorityMyRegions_0.add(reg);
		}
		else if (  checkListContainsOwnedSR(reg.getNeighbors(), state)  ) {
					priorityMyRegions_1.add(reg);
		}
		else if (getDistanceFromClosestAlly(state, myRegions, reg)<4 || getSubRegionsOwnedByPlayerName(reg.getSuperRegion(), myName).size() == 1 ){
			priorityMyRegions_2.add(reg);
		}	
		else{
			priorityMyRegions_3.add(reg);
		}
	}
	if (state.getVisibleMap().getRegion(10).ownedByPlayer(myName) && !priorityMyRegions_2.contains(state.getVisibleMap().getRegion(10)) && !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(10), opponentName).isEmpty()){
		priorityMyRegions_2.add(state.getVisibleMap().getRegion(10));
	}
	if (state.getVisibleMap().getRegion(39).ownedByPlayer(myName) && !priorityMyRegions_2.contains(state.getVisibleMap().getRegion(39)) && !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(39), opponentName).isEmpty()){
		priorityMyRegions_2.add(state.getVisibleMap().getRegion(39));
	}
	for (Region reg : myRegions){
		if (checkIsolatedInDangerousSuperRegion(state, reg)){
			isolatedInDangerousSuperRegion.add(reg);
		}
	}
	System.err.println("_____________________________________________________________");
	System.err.println("------------------------  ROUND : " + state.getRoundNumber() + "  ------------------------");
	System.err.println("_____________________________________________________________");
	
	
	
	
	
	
	System.err.print("myRegions: ");
	for (Region region : myRegions){
		System.err.print(" " + region.getId());
	}
	System.err.println("");
	
	if (!opponentRegions.isEmpty()){
		System.err.print("opponentRegions: ");
		for (Region region : opponentRegions){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!underAttackRegions.isEmpty()){
		System.err.print("underAttackRegions: ");
		for (Region region : underAttackRegions){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!safeRegions.isEmpty()){
		System.err.print("safeRegions: ");
		for (Region region : safeRegions){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!specialPriorityRegions.isEmpty()){
		System.err.print("specialPriorityRegions: ");
		for (Region region : specialPriorityRegions){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityNeutralRegions_0.isEmpty()){
		System.err.print("priorityNeutralRegions_0: ");
		for (Region region : priorityNeutralRegions_0){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityNeutralRegions_1.isEmpty()){
		System.err.print("priorityNeutralRegions_1: ");
		for (Region region : priorityNeutralRegions_1){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityNeutralRegions_2.isEmpty()){
		System.err.print("priorityNeutralRegions_2: ");
		for (Region region : priorityNeutralRegions_2){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityNeutralRegions_3.isEmpty()){
		System.err.print("priorityNeutralRegions_3: ");
		for (Region region : priorityNeutralRegions_3){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityNeutralRegions_4.isEmpty()){
		System.err.print("priorityNeutralRegions_4: ");
		for (Region region : priorityNeutralRegions_4){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityNeutralRegions_5.isEmpty()){
		System.err.print("priorityNeutralRegions_5: ");
		for (Region region : priorityNeutralRegions_5){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityMyRegions_0.isEmpty()){
		System.err.print("priorityMyRegions_0: ");
		for (Region region : priorityMyRegions_0){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityMyRegions_1.isEmpty()){
		System.err.print("priorityMyRegions_1: ");
		for (Region region : priorityMyRegions_1){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityMyRegions_2.isEmpty()){
		System.err.print("priorityMyRegions_2: ");
		for (Region region : priorityMyRegions_2){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!priorityMyRegions_3.isEmpty()){
		System.err.print("priorityMyRegions_3: ");
		for (Region region : priorityMyRegions_3){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	if (!isolatedInDangerousSuperRegion.isEmpty()){
		System.err.print("isolatedInDangerousSuperRegion: ");
		for (Region region : isolatedInDangerousSuperRegion){
			System.err.print(" " + region.getId());
		}
		System.err.println("");
	}
	
	
	
	
	
//_______________________________________________________________________________________________________________//
//_______________________________________________________________________________________________________________//

//--------------------------------------STARTING PLACING ARMIES--------------------------------------------------//
//_______________________________________________________________________________________________________________//
//_______________________________________________________________________________________________________________//
		ArrayList<AttackTransferMove> scheduledAttackTransferMoves0 = new ArrayList<AttackTransferMove>();
		ArrayList<AttackTransferMove> scheduledAttackTransferMoves1 = new ArrayList<AttackTransferMove>();
		ArrayList<AttackTransferMove> scheduledAttackTransferMoves2 = new ArrayList<AttackTransferMove>();
		ArrayList<AttackTransferMove> scheduledAttackTransferMoves3 = new ArrayList<AttackTransferMove>();
		ArrayList<AttackTransferMove> scheduledAttackTransferMoves4 = new ArrayList<AttackTransferMove>();
		
		int armiesLeft = state.getStartingArmies();			
		state.setScheduledAttackTransferMoves0(new ArrayList<AttackTransferMove>())	;	
		state.setScheduledAttackTransferMoves1(new ArrayList<AttackTransferMove>())	;	
		state.setScheduledAttackTransferMoves2(new ArrayList<AttackTransferMove>())	;	
		state.setScheduledAttackTransferMoves3(new ArrayList<AttackTransferMove>())	;	
		state.setScheduledAttackTransferMoves4(new ArrayList<AttackTransferMove>())	;
		
		int p = getOpponentReinforcement(state);
		System.err.println("Opponent Reinforcement: " + p);

//round zero:
	if (state.getRoundNumber()==1){
		//put armies in South America
		try{
			if (underAttackRegions.size()>2){
				if ( !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6),myName).isEmpty()){
					Region reg1=getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6),myName).get(0);
					int placeArmies = 5;
					placeArmiesMoves.add(new PlaceArmiesMove(myName, reg1, placeArmies));
					armiesLeft-=placeArmies;
					reg1.setArmies(reg1.getArmies()+placeArmies);	
					for (Region reg0 : myRegions){
						if (!reg0.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(2)) && !reg0.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(6))){
							if ( !getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty() ){
								Region opp0 = getNeighborsOwnedByPlayerName(reg0, opponentName).get(0);
								int takeArmies = 1;
								scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg0,opp0, 1));
								System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);
							}
						}
					}
					Region opp1 = getNeighborsOwnedByPlayerName(reg1, opponentName).get(0);
					int takeArmies = reg1.getArmies()-1;
					scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg1,opp1, takeArmies));
					System.err.println("line " + getLineNumber() + ": attacking Region " + opp1.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
					reg1.setArmies(1);
				}else if ( !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2),myName).isEmpty()){
					Region reg1=getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2),myName).get(0);
					int placeArmies = 5;
					placeArmiesMoves.add(new PlaceArmiesMove(myName, reg1, placeArmies));
					armiesLeft-=placeArmies;
					reg1.setArmies(reg1.getArmies()+placeArmies);
					for (Region reg0 : myRegions){
						if (!reg0.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(2)) && !reg0.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(6))){
							if ( !getNeighborsOwnedByPlayerName(reg1, opponentName).isEmpty() ){
								int takeArmies = 1;
								Region opp0 = getNeighborsOwnedByPlayerName(reg1, opponentName).get(0);
								scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg0,opp0, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-1);
							}
						}
					}
					Region opp1 = getNeighborsOwnedByPlayerName(reg1, opponentName).get(0);
					int takeArmies = reg1.getArmies()-1;
					scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg1,opp1, reg1.getArmies()-1));
					System.err.println("line " + getLineNumber() + ": attacking Region " + opp1.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
					reg1.setArmies(1);
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}
//Counter opponent bonus
	if (state.getRoundNumber()==1){	
		try{
			if ( getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2),myName).isEmpty()){
				for (Region reg : myRegions){
					if (reg.equals(state.getVisibleMap().getRegion(21)) ){
						int placeArmies=0;
						if (underAttackRegions.isEmpty() || (underAttackRegions.size()==1 && underAttackRegions.contains(reg)) ){
							placeArmies=armiesLeft;
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
							armiesLeft-=placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
							Region neut= state.getVisibleMap().getRegion(12);
							int takeArmies = reg.getArmies()-1;
							if (takeArmies>2){
								scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg,neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
								reg.setArmies(reg.getArmies()-takeArmies);
								neut.setPlayerName("unknown");
							}
						}
					}else if ( reg.equals(state.getVisibleMap().getRegion(9))  || (underAttackRegions.size()==1 && underAttackRegions.contains(reg))){
						int placeArmies=0;
						if (underAttackRegions.isEmpty()){
							placeArmies=armiesLeft;
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
							armiesLeft-=placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
							Region neut= state.getVisibleMap().getRegion(10);
							int takeArmies = reg.getArmies()-1;
							if (takeArmies>2){
								scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg,neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
								reg.setArmies(reg.getArmies()-takeArmies);
								neut.setPlayerName("unknown");
							}
						}
					}
				}
			}else if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6),myName).isEmpty() && myRegions.contains(state.getVisibleMap().getRegion(38))){
				Region reg=state.getVisibleMap().getRegion(38);
				int placeArmies=0;
				if (underAttackRegions.isEmpty()  || (underAttackRegions.size()==1 && underAttackRegions.contains(reg))){
					placeArmies=armiesLeft;
					placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
					armiesLeft-=placeArmies;
					reg.setArmies(reg.getArmies()+placeArmies);
					Region neut= state.getVisibleMap().getRegion(39);
					int takeArmies = reg.getArmies()-1;
					if (takeArmies>2){
						scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg,neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");
					}
				}
			}
			
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}

	if (myRegions.contains(state.getVisibleMap().getRegion(38)) && state.getVisibleMap().getRegion(39).ownedByPlayer("neutral")){
		specialPriorityRegions.add(0,state.getVisibleMap().getRegion(39));
	}
	if (myRegions.contains(state.getVisibleMap().getRegion(9)) && state.getVisibleMap().getRegion(10).ownedByPlayer("neutral")){
		specialPriorityRegions.add(0,state.getVisibleMap().getRegion(10));
	}
	if (myRegions.contains(state.getVisibleMap().getRegion(21)) && state.getVisibleMap().getRegion(12).ownedByPlayer("neutral")){
		specialPriorityRegions.add(0,state.getVisibleMap().getRegion(12));
	}
	
//run for tie
	if (getOpponentReinforcement(state)> state.getStartingArmies()+2 && state.getRoundNumber()>70 ){
		Region reg=getRegionWithMaxArmiesOfTheList(myRegions);
		if  (!getNeighborsOwnedByPlayerName(reg, "neutral").isEmpty()){
			Region neut=getRegionWithMinArmiesOfTheList(getNeighborsOwnedByPlayerName(reg, "neutral"));
			placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, armiesLeft));
			armiesLeft=0;
			int takeArmies = reg.getArmies()-1;
			scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg,neut, takeArmies));
			System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
			reg.setArmies(1);
		}else if (!getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
			Region opp=getRegionWithMinArmiesOfTheList(getNeighborsOwnedByPlayerName(reg, opponentName));
			placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, armiesLeft));
			armiesLeft=0;
			int takeArmies = reg.getArmies()-1;
			scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg,opp, takeArmies));
			System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
			reg.setArmies(1);
		}
		for (Region reg1 : myRegions){
			if (reg1.getArmies()>4){
				if  (!getNeighborsOwnedByPlayerName(reg1, "neutral").isEmpty()){
					Region neut=getRegionWithMinArmiesOfTheList(getNeighborsOwnedByPlayerName(reg1, "neutral"));
					int takeArmies = reg1.getArmies()-1;
					scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg1,neut, takeArmies));
					System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
					reg1.setArmies(1);
				}else if (!getNeighborsOwnedByPlayerName(reg1, opponentName).isEmpty()){
					Region opp=getRegionWithMinArmiesOfTheList(getNeighborsOwnedByPlayerName(reg1, opponentName));
					int takeArmies = reg1.getArmies()-1;
					scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg1,opp, takeArmies));
					System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
					reg1.setArmies(1);
				}
			}
		}
	}
	
//reinforcing Middle East
	if (state.getVisibleMap().getSuperRegion(2).ownedByPlayer(myName) && state.getVisibleMap().getRegion(36).ownedByPlayer(myName) ){
		if (state.getVisibleMap().getRegion(36).getArmies()<30 && underAttackRegions.isEmpty()){
			Region reg0=state.getVisibleMap().getRegion(36);
			int placeArmies=armiesLeft;
			if (placeArmies>0){
				System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on North Africa to anticipate protecting SA " );
				placeArmiesMoves.add(new PlaceArmiesMove(myName, reg0, placeArmies));
				armiesLeft-=placeArmies;
				reg0.setArmies(reg0.getArmies()+placeArmies);
				return placeArmiesMoves;
			}
		}
	}
	
// put armies on North Africa to protect SA
	try{
		if (state.getVisibleMap().getSuperRegion(2).ownedByPlayer(myName) && state.getVisibleMap().getRegion(21).ownedByPlayer(myName) && getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(21), opponentName).isEmpty() && state.getVisibleMap().getRegion(21).getArmies()<20 ){
			Region opp1=state.getVisibleMap().getRegion(36);
			Region opp2=state.getVisibleMap().getRegion(37);
			Region reg0=state.getVisibleMap().getRegion(21);
			if (opp1.ownedByPlayer(opponentName) && !getNeighborsOwnedByPlayerName(opp1, myName).isEmpty() && opp1.getArmies()>3){
				int defendingArmies = 0;
				for(Region reg : getNeighborsOwnedByPlayerName(opp1, myName)){
					defendingArmies+=reg.getArmies();
				}
				if (defendingArmies+armiesLeft<(int) 0.8*(opp1.getArmies()+6)){
					int placeArmies=armiesLeft;
					if (placeArmies > 0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on North Africa to anticipate protecting SA " );
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg0, placeArmies));
						armiesLeft-=placeArmies;
						reg0.setArmies(reg0.getArmies()+placeArmies);
					}
					for (Region neig : getNeighborsOwnedByPlayerName(reg0, myName)){
						if (neig.getArmies()>1){
							int transferArmies= neig.getArmies()-1;
							scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, neig, reg0, transferArmies));
							System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig.getId() + " to go to Region " + reg0.getId());
							neig.setArmies(neig.getArmies()-transferArmies);	
						}
					}
					for (Region neig : getNeighborsOwnedByPlayerName(reg0, myName)){
						for (Region neig2 : getNeighborsOwnedByPlayerName(neig, myName)){ 
							if (neig2.getArmies()>1  && !neig2.equals(reg0)){
								int transferArmies= neig2.getArmies()-1;
								scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, neig2, neig, transferArmies));
								System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig2.getId() + " to go to Region " + reg0.getId());
								neig2.setArmies(neig2.getArmies()-transferArmies);
							}
						}
					}
					state.setScheduledAttackTransferMoves0(scheduledAttackTransferMoves0);
					return placeArmiesMoves;
				}
			}else if ( opp2.ownedByPlayer(opponentName) && opp1.ownedByPlayer(myName) && opp2.getArmies()>3 ) {
				if (opp1.getArmies()+armiesLeft<(int) 0.8*(opp2.getArmies()+6)){
					int placeArmies=armiesLeft;
					if (placeArmies > 0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on North Africa to anticipate protecting SA " );
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg0, placeArmies));
						armiesLeft-=placeArmies;
						reg0.setArmies(reg0.getArmies()+placeArmies);
					}
					for (Region neig : getNeighborsOwnedByPlayerName(reg0, myName)){
						if (neig.getArmies()>1){
							int transferArmies= neig.getArmies()-1;
							scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, neig, reg0, transferArmies));
							System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig.getId() + " to go to Region " + reg0.getId());
							neig.setArmies(neig.getArmies()-transferArmies);	
						}
					}
					for (Region neig : getNeighborsOwnedByPlayerName(reg0, myName)){
						for (Region neig2 : getNeighborsOwnedByPlayerName(neig, myName)){ 
							if (neig2.getArmies()>1 && !neig2.equals(reg0)){
								int transferArmies= neig2.getArmies()-1;
								scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, neig2, neig, transferArmies));
								System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig2.getId() + " to go to Region " + reg0.getId());
								neig2.setArmies(neig2.getArmies()-transferArmies);
							}
						}
					}
					state.setScheduledAttackTransferMoves0(scheduledAttackTransferMoves0);
					return placeArmiesMoves;
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}


// transfer maximum armies to defend a SuperRegion
		try{
			for (Region reg : priorityMyRegions_0){
				int attArmies=0;
				for (Region att : getNeighborsOwnedByPlayerName(reg, opponentName)){
					attArmies += (att.getArmies()-1);
				}
				if ( reg.getArmies()< attArmies+p ){
					for (Region neig : getNeighborsOwnedByPlayerName(reg, myName)){
						if ( !priorityMyRegions_0.contains(neig) && neig.getArmies()>1 && !checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, neig)){
							int transferArmies= Math.min(attArmies+p+1-reg.getArmies(), neig.getArmies()-1);
							scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, neig, reg, transferArmies));
							System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig.getId() + " to go to Region " + reg.getId());
							neig.setArmies(neig.getArmies()-transferArmies);	
						}
					}
					for (Region neig : getNeighborsOwnedByPlayerName(reg, myName)){
						for (Region neig2 : getNeighborsOwnedByPlayerName(neig, myName)){ 
							if (!priorityMyRegions_0.contains(neig2) && neig2.getArmies()>1 && !checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, neig2) ){
								int transferArmies= Math.min(attArmies+p+1-reg.getArmies(), neig2.getArmies()-1);
								scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, neig2, neig, transferArmies));
								System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig2.getId() + " to go to Region " + reg.getId());
								neig2.setArmies(neig2.getArmies()-transferArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
		
// transfer maximum armies to defend a SuperRegion (indirectly)
		try{
			for (Region reg : priorityMyRegions_1){
				int attArmies=0;
				for (Region att : getNeighborsOwnedByPlayerName(reg, opponentName)){
					attArmies += (att.getArmies()-1);
				}
				if ( reg.getArmies()< attArmies+7 ){
					for (Region neig : getNeighborsOwnedByPlayerName(reg, myName)){
						if ( !priorityMyRegions_0.contains(neig) && !priorityMyRegions_1.contains(neig) && neig.getArmies()>1 && !checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, neig) ){
							int transferArmies= Math.min(attArmies+7-reg.getArmies(), neig.getArmies()-1);
							scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, neig, reg, transferArmies));
							System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig.getId() + " to go to Region " + reg.getId());
							neig.setArmies(neig.getArmies()-transferArmies);	
						}
					}
					for (Region neig : getNeighborsOwnedByPlayerName(reg, myName)){
						for (Region neig2 : getNeighborsOwnedByPlayerName(neig, myName)){ 
							if (!priorityMyRegions_0.contains(neig2) && !priorityMyRegions_1.contains(neig2) && neig2.getArmies()>1 && !checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, neig2) ){
								int transferArmies= Math.min(attArmies+7-reg.getArmies(), neig2.getArmies()-1);
								scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, neig2, neig, transferArmies));
								System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig2.getId() + " to go to Region " + reg.getId());
								neig2.setArmies(neig2.getArmies()-transferArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	
// transfer leftovers to defend a bonus
		for (Region reg : safeRegions){
			try{
				if ( reg.getArmies()>1){ 
					for (Region neig : reg.getNeighbors()){ 
						if (priorityMyRegions_0.contains(neig)){
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neig, reg.getArmies()-1));
							System.err.println("line " + getLineNumber() + ": transfer " + (reg.getArmies()-1) + " armies from Region  " + reg.getId() + " to go to Region " + neig.getId());
							reg.setArmies(1);
							break;
						}
					}
				}
			} catch ( Exception e ){
				e.printStackTrace();
			}
		}
		for (Region reg : safeRegions){
			try{
				if ( reg.getArmies()>1){ 
					for (Region neig : reg.getNeighbors()){ 
						if (priorityMyRegions_1.contains(neig)){
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neig, reg.getArmies()-1));
							System.err.println("line " + getLineNumber() + ": transfer " + (reg.getArmies()-1) + " armies from Region  " + reg.getId() + " to go to Region " + neig.getId());
							reg.setArmies(1);
							break;
						}
					}
				}
			} catch ( Exception e ){
				e.printStackTrace();
			}
		}
		for (Region reg : safeRegions){
			try{
				if ( reg.getArmies()>1){ 
					for (Region neig : reg.getNeighbors()){ 
						if (underAttackRegions.contains(neig)){
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neig, reg.getArmies()-1));
							System.err.println("line " + getLineNumber() + ": transfer " + (reg.getArmies()-1) + " armies from Region  " + reg.getId() + " to go to Region " + neig.getId());
							reg.setArmies(1);
							break;
						}
					}
				}
			} catch ( Exception e ){
				e.printStackTrace();
			}
		}
// transfert leftovers to a region underattack	
	try{
		for (Region reg : underAttackRegions){
			int attArmies=0;
			for (Region att : getNeighborsOwnedByPlayerName(reg, opponentName)){
				attArmies += (att.getArmies()-1);
			}
			if ( reg.getArmies()< (int) ((attArmies+p)*0.9) ){
				for (Region neig : getNeighborsOwnedByPlayerName(reg, myName) ){
					if (getNeighborsOwnedByPlayerName(neig, opponentName).isEmpty() && neig.getArmies()>1  && !checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, neig)){
						int transferArmies= Math.min((int) (((attArmies+p)*0.9)-reg.getArmies()+1), neig.getArmies()-1);
						scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, neig, reg, transferArmies));
						System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig.getId() + " to go to Region " + reg.getId());
						neig.setArmies(neig.getArmies()-transferArmies);	
					}
				}
				for (Region neig : getNeighborsOwnedByPlayerName(reg, myName) ){
					for (Region neig2 : getNeighborsOwnedByPlayerName(neig, myName)){ 
						if (getNeighborsOwnedByPlayerName(neig2, opponentName).isEmpty()  && neig2.getArmies()>1  && !checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, neig2)){
							int transferArmies= Math.min((int) (((attArmies+p)*0.9)-reg.getArmies()+1), neig2.getArmies()-1);
							scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, neig2, neig, transferArmies));
							System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + neig2.getId() + " to go to Region " + reg.getId());
							neig2.setArmies(neig2.getArmies()-transferArmies);
						}
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}

						
// Placing Armies to directly defend a SuperRegion
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_0){
					if (reg.getArmies()<90){
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < (int) ((opponentArmies+7)*0.97)){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+7)*0.95 - reg.getArmies()+1), armiesLeft );
							if (placeArmies > 0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}	
					}
					else if (reg.getArmies()<130){
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < (int) ((opponentArmies+7)*0.9)){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+7)*0.9 - reg.getArmies()+1), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}	
					}else{
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < (int) ((opponentArmies+7)*0.8)){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+7)*0.8 - reg.getArmies()+1), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}	
					}
				}
			}	
		} catch ( Exception e ){
			e.printStackTrace();
		}

		
// Placing Armies to  indirectly defend a SuperRegion
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_1){
					if (reg.getArmies()<90){
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < (int) ((opponentArmies+7)*0.97)){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+7)*0.95 - reg.getArmies()+1), armiesLeft );
							if (placeArmies > 0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}	
					}
					else if (reg.getArmies()<130){
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < (int) ((opponentArmies+7)*0.9)){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+7)*0.9 - reg.getArmies()+1), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}	
					}else{
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < (int) ((opponentArmies+7)*0.8)){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+7)*0.8 - reg.getArmies()+1), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}	
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
//Placing armies to protect last region keeping opponent bonus
		try{
			if (armiesLeft>0){
				for (Region reg : myRegions){
					if (getSubRegionsOwnedByPlayerName(reg.getSuperRegion(),"neutral").isEmpty() && getSubRegionsOwnedByPlayerName(reg.getSuperRegion(),myName).size()==1){
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
							opponentArmies += (opp.getArmies()-1);
						}
						if (reg.getArmies()< (int) (opponentArmies+p)*0.9 && reg.getArmies()+armiesLeft>= (int) (opponentArmies)*0.8 ){
							int placeArmies = Math.min((int) ((opponentArmies+p)*0.9 - reg.getArmies()+1), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
							if (!getNeighborsOwnedByPlayerName(reg, myName).isEmpty()){
								for (Region reg1 : getNeighborsOwnedByPlayerName(reg, myName)){
									if (reg1.getArmies()>1 && !priorityMyRegions_0.contains(reg1) && !priorityMyRegions_1.contains(reg1)){
										int transferArmies= Math.min((int) (((opponentArmies+p)*0.9)-reg.getArmies()+1), reg1.getArmies()-1);
										scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg1, reg, transferArmies));
										System.err.println("line " + getLineNumber() + ": transfer " + transferArmies + " armies from Region  " + reg1.getId() + " to go to Region " + reg.getId());
										reg1.setArmies(reg1.getArmies()-transferArmies);	
									}
								}
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
		
//Conquer last neutral to get SuperRegion (priorityNeutralRegions_0)
		try{
			for (Region neut : priorityNeutralRegions_0){
				Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
				if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
					if ( reg.getArmies() + armiesLeft >= neut.getArmies()+2 ){
						if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
						else{
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
					}
				}
				else{
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += opp.getArmies();
					}
					if ( reg.getArmies()+armiesLeft-(neut.getArmies()+1) > (int)(opponentArmies+p)*0.6){
						int placeArmies = armiesLeft;
						if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = neut.getArmies()+1;
						scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}		

// 	Placing Armies to protect other Isolated Regions 1er passage
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_2){
					if (reg.getArmies()<5){
						int opponentArmies=0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
							opponentArmies += (opp.getArmies()-1);
						}
						if (  reg.getArmies() < opponentArmies+2 && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min(opponentArmies - reg.getArmies()+2, armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	
// Placing Armies to protect Isolated Regions 2e passage
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_2){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += (opp.getArmies()-1);
					}
					if (reg.getArmies()<90){
						if (  reg.getArmies() < (int) ((opponentArmies+2)*0.95-1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+2)*0.95 - reg.getArmies()), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}else if (reg.getArmies()<130){
						if (  reg.getArmies() < (int) ((opponentArmies+2)*0.9-1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+2)*0.9 - reg.getArmies()), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}else{
						if (  reg.getArmies() < (int) ((opponentArmies+2)*0.8-1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+2)*0.8 - reg.getArmies()), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	

// Placing Armies to protect Isolated Regions 3e passage
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_2){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += (opp.getArmies()-1);
					}
					if (reg.getArmies()<90){
						if (  reg.getArmies() < (int) ((opponentArmies+p)*0.95-1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.95 - reg.getArmies()), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}else if (reg.getArmies()<130){
						if (  reg.getArmies() < (int) ((opponentArmies+p)*0.9-1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.7){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.9 - reg.getArmies()), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}else{
						if (  reg.getArmies() < (int) ((opponentArmies+p)*0.8-1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.7){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.8 - reg.getArmies()), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
//Counter opponent bonus
	if (state.getRoundNumber()<4){	
		try{
			if ( getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2),myName).isEmpty()){
				for (Region reg : myRegions){
					if (reg.equals(state.getVisibleMap().getRegion(21)) &&  state.getVisibleMap().getRegion(12).ownedByPlayer("neutral")){
						Region neut= state.getVisibleMap().getRegion(12);
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg,neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");
					}else if ( reg.equals(state.getVisibleMap().getRegion(9)) &&  state.getVisibleMap().getRegion(10).ownedByPlayer("neutral")){
						Region neut= state.getVisibleMap().getRegion(10);
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg,neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");
					}
				}
			}else if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6),myName).isEmpty() && myRegions.contains(state.getVisibleMap().getRegion(38)) && state.getVisibleMap().getRegion(39).ownedByPlayer("neutral")){
				Region reg=state.getVisibleMap().getRegion(38);
				Region neut= state.getVisibleMap().getRegion(39);
				int takeArmies = reg.getArmies()-1;
				scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg,neut, takeArmies));
				System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
				reg.setArmies(reg.getArmies()-takeArmies);
				neut.setPlayerName("unknown");
			}
			
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}
//Placing Armies to break a bonus		
		try{
			if (state.getRoundNumber()>2){
				if (!underAttackRegions.isEmpty()){
					for (Region reg : underAttackRegions){
						if (!getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
							for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
								if ( getSubRegionsOwnedByPlayerName(opp.getSuperRegion(), myName).isEmpty() && getSubRegionsOwnedByPlayerName(opp.getSuperRegion(), "neutral").isEmpty()){
									Region oppmin=opp;
									for (Region opp1 : getNeighborsOwnedByPlayerName(reg, opponentName)){
										if ( opp1.getSuperRegion().equals(oppmin.getSuperRegion()) && opp1.getArmies()<= oppmin.getArmies() ){
											oppmin=opp1;
										}
									}
									System.err.println("Region to break :" + oppmin.getId());
									Region regmax = getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(oppmin, myName));			
									int myAttackingArmies = 0;
									for (Region reg1 : getNeighborsOwnedByPlayerName(oppmin, myName) ){
										myAttackingArmies+=reg1.getArmies()-1;
									}										
									if ((myAttackingArmies+armiesLeft) > (int) ((oppmin.getArmies()+p)*1.1+1)){
										int placeArmies = armiesLeft;
										if (placeArmies>0){
										System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + regmax.getId() + " to take Region " + oppmin.getId());
										placeArmiesMoves.add(new PlaceArmiesMove(myName, regmax, placeArmies));
										armiesLeft-=placeArmies;
										regmax.setArmies(regmax.getArmies()+placeArmies);
										}
										if (getNeighborsOwnedByPlayerName(regmax, opponentName).size()==1){
											for (Region reg1 : getNeighborsOwnedByPlayerName(oppmin, myName)){
												if (reg1.getArmies()>1){
													int takeArmies = reg1.getArmies()-1;
													int defArmies = reg1.getArmies();
													scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg1, oppmin, takeArmies));
													System.err.println("line " + getLineNumber() + ": attacking Region " + oppmin.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
													reg1.setArmies(defArmies-takeArmies);	
												}
											}	
										}else{
											for (Region reg1 : getNeighborsOwnedByPlayerName(oppmin, myName)){
												if (reg1.getArmies()>1){
													int takeArmies =Math.min(reg1.getArmies()-1, (int) 2.5*(oppmin.getArmies()+p));
													int defArmies = reg1.getArmies();
													scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg1, oppmin, takeArmies));
													System.err.println("line " + getLineNumber() + ": attacking Region " + oppmin.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
													reg1.setArmies(defArmies-takeArmies);	
												}
											}
										}
									}else if ( opp.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(4)) ){
										if ( !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(20), myName).isEmpty() ){
											Region reg0=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(20), myName));
											int placeArmies = armiesLeft;
											if (placeArmies>0){
												System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg0.getId());
												placeArmiesMoves.add(new PlaceArmiesMove(myName, reg0, placeArmies));
												armiesLeft-=placeArmies;
												reg0.setArmies(reg0.getArmies()+placeArmies);
											}
											int takeArmies = reg0.getArmies()-1;
											int defArmies = reg0.getArmies();
											if (takeArmies>0){
												scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg0, state.getVisibleMap().getRegion(20), takeArmies));
												System.err.println("line " + getLineNumber() + ": attacking Region 20 " + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
												reg0.setArmies(defArmies-takeArmies);		
												state.getVisibleMap().getRegion(20).setPlayerName("unknown");	
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
// Placing Armies to protect Isolated Regions 4e passage
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_2){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += (opp.getArmies()-1);
					}
					if (reg.getArmies()<10){
						if (  reg.getArmies() < opponentArmies+p+1 && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( opponentArmies+p - reg.getArmies()+1, armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
// Placing Armies to Defend a region underAttack	
	try{
		if (armiesLeft>0){
			if (!opponentRegions.isEmpty()){
				for (Region opp : opponentRegions){
					if (!getNeighborsOwnedByPlayerName(opp, myName).isEmpty()){
						Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(opp, myName));
						int opponentArmies=0;
						for (Region opp1 : getNeighborsOwnedByPlayerName(reg, opponentName)){
							opponentArmies+=opp1.getArmies()-1;
						}
						if ( reg.getArmies()<130 && reg.getArmies() < (int) ((opponentArmies+p)*0.9) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){ 
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.93 - reg.getArmies())+1, armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}else if ( reg.getArmies()>=130 && reg.getArmies() < (int) ((opponentArmies+p)*0.8)){ 
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.8 - reg.getArmies())+1, armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}
//Conquer last 2 neutrals to get SuperRegion (priorityNeutralRegions_1)
		try{
			for (Region neut : priorityNeutralRegions_1){
				Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
				if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
					if ( reg.getArmies() + armiesLeft >= neut.getArmies()+2 ){
						if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
							if (!underAttackRegions.isEmpty() || reg.getArmies()+armiesLeft==7){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
						else{
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
					}
				}
				else{
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += opp.getArmies();
					}
					if ( reg.getArmies()+armiesLeft-(neut.getArmies()+1) >= (int)(opponentArmies+p)*0.7){
						int placeArmies = armiesLeft;
						if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = neut.getArmies()+1;
						scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}		
// Placing Armies to protect Isolated Regions 4e passage
		try{
			if (armiesLeft>0){
				for (Region reg : priorityMyRegions_2){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += (opp.getArmies()-1);
					}
					if (reg.getArmies()<90){
						if (  reg.getArmies() < (int) ((opponentArmies+p)*0.95+1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.95 - reg.getArmies()+2), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}else if (reg.getArmies()<90){
						if (  reg.getArmies() < (int) ((opponentArmies+p)*0.9+1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.9 - reg.getArmies()+2), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}else{
						if (  reg.getArmies() < (int) ((opponentArmies+p)*0.8+1) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.8 - reg.getArmies()+2), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
// Take special Regions		
	try{
		for (Region opp : specialPriorityRegions){
			if (!getNeighborsOwnedByPlayerName(opp, myName).isEmpty()){
				Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(opp, myName));
				if (reg.getArmies()+armiesLeft>4){
					if (opp.ownedByPlayer("neutral") ){
						if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && reg.getArmies()+armiesLeft >= opp.getArmies()+2){
							int placeArmies = armiesLeft;
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + opp.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies); 
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg, opp, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(1);
							opp.setPlayerName("unknown");
						}else if(reg.getArmies()+armiesLeft >= opp.getArmies()+2 && !priorityMyRegions_0.contains(reg) && !priorityMyRegions_1.contains(reg)){
							int placeArmies = armiesLeft;
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + opp.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies); 
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg, opp, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(1);
							opp.setPlayerName("unknown");
						}
						
					}else if(opp.ownedByPlayer(opponentName)){
						int myAttackingArmies = 0;
						for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName) ){
							myAttackingArmies+=reg1.getArmies()-1;
						}
						if (myAttackingArmies+armiesLeft > (int) ((opp.getArmies()+p)*1.1)){
							int placeArmies = armiesLeft;
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + opp.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
								}	
							for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName)){
								if (reg1.getArmies()>1){
									if ( (!priorityMyRegions_0.contains(reg1) && !priorityMyRegions_1.contains(reg1))){
										int takeArmies = reg1.getArmies()-1;
										int defArmies = reg1.getArmies();
										scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg1, opp, takeArmies));
										System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
										reg1.setArmies(defArmies-takeArmies);
										opp.setPlayerName("unknown");	
									}else if( getNeighborsOwnedByPlayerName(reg, opponentName).size()==1 ){
										int takeArmies = reg1.getArmies()-1;
										int defArmies = reg1.getArmies();
										scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg1, opp, takeArmies));
										System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
										reg1.setArmies(defArmies-takeArmies);
										opp.setPlayerName("unknown");		
									}
								}
							}									
						}	
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}

// Placing Armies to Defend a region underAttack	
	try{
		if (armiesLeft>0){
			if (!opponentRegions.isEmpty()){
				for (Region opp : opponentRegions){
					if (!getNeighborsOwnedByPlayerName(opp, myName).isEmpty()){
						Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(opp, myName));
						int opponentArmies=0;
						for (Region opp1 : getNeighborsOwnedByPlayerName(reg, opponentName)){
							opponentArmies+=opp1.getArmies()-1;
						}
						if ( reg.getArmies()<130 && reg.getArmies() < (int) ((opponentArmies+p)*0.9) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){ 
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.93 - reg.getArmies())+1, armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}else if ( reg.getArmies()>=130 && reg.getArmies() < (int) ((opponentArmies+p)*0.8)){ 
							int placeArmies = Math.min( (int)( (int)(opponentArmies+p)*0.8 - reg.getArmies())+1, armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}
	
// Placing Armies to Defend a region underAttack	
		try{
			if (armiesLeft>0 && state.getStartingArmies()>p+2){
				if (!underAttackRegions.isEmpty()){
					for (Region reg : underAttackRegions){
						int opponentArmies =0;
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
							opponentArmies+=opp.getArmies()-1;
						}
						if (reg.getArmies() < 2*(opponentArmies+p) && reg.getArmies()+armiesLeft> (int) (opponentArmies)*0.8){ 
							int placeArmies = Math.min( 2*(opponentArmies+p) - reg.getArmies(), armiesLeft );
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}		
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	
//take opponent Region to get bonus
	try{
		for (Region opp : opponentRegions){
			if (getSubRegionsOwnedByPlayerName(opp.getSuperRegion(), myName).size()>=opp.getSuperRegion().getSubRegions().size()-2 && getSubRegionsOwnedByPlayerName(opp.getSuperRegion(), opponentName).size()==1 ){
				int myAttackingArmies=0;
				for (Region reg : getNeighborsOwnedByPlayerName(opp, myName) ){
					myAttackingArmies+=reg.getArmies()-1;
				}
				if (myAttackingArmies + armiesLeft > opp.getArmies()+p){
					Region reg0=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(opp, myName));
					int placeArmies = Math.min( (opp.getArmies()+p)*2 - reg0.getArmies(), armiesLeft );
					if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg0.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg0, placeArmies));
						armiesLeft-=placeArmies;
						reg0.setArmies(reg0.getArmies()+placeArmies);
					}
					int takeArmies = reg0.getArmies()-1;
					scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg0, opp, takeArmies));
					System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
					reg0.setArmies(1);
					for (Region reg : getNeighborsOwnedByPlayerName(opp, myName) ){
						if (reg.getArmies()>1){
							int takeArmies1 = reg.getArmies()-1;
							int defArmies = reg.getArmies();
							scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg, opp, takeArmies1));
							System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies1 + " armies");
							reg.setArmies(defArmies-takeArmies);
						}
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}
// Placing Armies to take opponent Regions
	try{
		for (Region reg : underAttackRegions){
			int opponentArmies = 0;
			for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
				opponentArmies+=opp.getArmies();
			}
			if (!priorityMyRegions_0.contains(reg) && !priorityMyRegions_1.contains(reg)){
				if (getNeighborsOwnedByPlayerName(reg, opponentName).size()==1){
					Region opp0=getNeighborsOwnedByPlayerName(reg, opponentName).get(0);
					if ( reg.getArmies()+armiesLeft>=(int) (1.6*(opp0.getArmies()+1+getReinforcementOnRegion(state,opp0))) && state.getRoundNumber()>1 &&  getOpponentRegions(state, state.getMapHistory().get(state.getRoundNumber()-1)).contains(opp0) ){
						int placeArmies = Math.min(armiesLeft, (int) (1.6*(opp0.getArmies()+p) - reg.getArmies()) );
						if (placeArmies>0){
							System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + opp0.getId());
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
							armiesLeft-=placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
						if (!checkAttackOnRegion(state, reg)){ 
							int takeArmies = reg.getArmies()-1;
							int defArmies = reg.getArmies();
							scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, opp0, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(defArmies-takeArmies);
							for (Region reg0 : getNeighborsOwnedByPlayerName(opp0, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
									int takeArmies0 = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp0, takeArmies0));
									System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies0);	
								}
							}
							opp0.setPlayerName("unknown");
						}
					}else if ( reg.getArmies()+armiesLeft-1 > (int) (1.1*(opp0.getArmies()+p)+1)  ){
						int placeArmies = Math.min(armiesLeft, (int) (1.6*(opp0.getArmies()+p+1) - reg.getArmies()) );
						if (placeArmies>0){
							System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + opp0.getId());
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
							armiesLeft-=placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
						int takeArmies = reg.getArmies()-1;
						int defArmies = reg.getArmies();
						scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg, opp0, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(defArmies-takeArmies);
						for (Region reg0 : getNeighborsOwnedByPlayerName(opp0, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
								int takeArmies0 = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp0, takeArmies0));
								System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies0);	
							}
						}
						opp0.setPlayerName("unknown");
					}
				}else if (getNeighborsOwnedByPlayerName(reg, opponentName).size()>1){
					if (reg.getArmies()+armiesLeft-1 >= (int) ( (opponentArmies+p+1)*1.1 ) ){
						int placeArmies = Math.min(armiesLeft, (int) (1.6*(opponentArmies+p+1) - reg.getArmies()+1) );
						if (placeArmies>0){
							System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
							armiesLeft-=placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							if (reg.getArmies()-1 >=(int) (1.6*(opp.getArmies()+getReinforcementOnRegion(state,opp))) ){
								if (getNeighborsOwnedByPlayerName(reg, opponentName).size()==1){
									int takeArmies = reg.getArmies()-1;
									int defArmies = reg.getArmies();
									scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, opp, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
									reg.setArmies(defArmies-takeArmies);
									for (Region reg0 : getNeighborsOwnedByPlayerName(opp, myName)){
										if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
											int takeArmies0 = reg0.getArmies()-1;
											scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp, takeArmies0));
											System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
											reg0.setArmies(reg0.getArmies()-takeArmies0);	
										}
									}
									opp.setPlayerName("unknown");
								}else{
									int takeArmies = (int) (1.6*(opp.getArmies()+1+getReinforcementOnRegion(state,opp)));
									int defArmies = reg.getArmies();
									scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, opp, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
									reg.setArmies(defArmies-takeArmies);
									for (Region reg0 : getNeighborsOwnedByPlayerName(opp, myName)){
										if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
											int takeArmies0 = reg0.getArmies()-1;
											scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp, takeArmies0));
											System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
											reg0.setArmies(reg0.getArmies()-takeArmies0);	
										}
									}
									opp.setPlayerName("unknown");
								}
							}
						}
					}	
				}
			}else{
				if (reg.getArmies()+armiesLeft-1 >= (int) ( (opponentArmies+p)*1.1 ) ){
					int placeArmies = Math.min(armiesLeft, (int) (2*(opponentArmies+p) - reg.getArmies()) );
					if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
					}
					if (getNeighborsOwnedByPlayerName(reg, opponentName).size()==1){
						Region opp0=getNeighborsOwnedByPlayerName(reg, opponentName).get(0);
						int takeArmies = Math.min(reg.getArmies()-1, (int) ( (opp0.getArmies()+p)*2 ));
						int defArmies = reg.getArmies();
						scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg, opp0, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(defArmies-takeArmies);
						for (Region reg0 : getNeighborsOwnedByPlayerName(opp0, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
								int takeArmies0 = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp0, takeArmies0));
								System.err.println("line " + getLineNumber() + ": attacking Region " + opp0.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies0);	
							}
						}
						opp0.setPlayerName("unknown");
					}else if (getNeighborsOwnedByPlayerName(reg, opponentName).size()>1 && reg.getArmies()+armiesLeft >= (int) ( (opponentArmies+p)*1.6 ) ){
						for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName) ){
							if (reg.getArmies()-1 >=(int) (1.6*(opp.getArmies()+1+getReinforcementOnRegion(state,opp))) ){
								if (getNeighborsOwnedByPlayerName(reg, opponentName).size()==1){
									int takeArmies = reg.getArmies()-1;
									int defArmies = reg.getArmies();
									scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, opp, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
									reg.setArmies(defArmies-takeArmies);
									for (Region reg0 : getNeighborsOwnedByPlayerName(opp, myName)){
										if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
											int takeArmies0 = reg0.getArmies()-1;
											scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp, takeArmies0));
											System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
											reg0.setArmies(reg0.getArmies()-takeArmies0);	
										}
									}
									opp.setPlayerName("unknown");
								}else{
									int takeArmies = Math.min(reg.getArmies()-1,(int) (1.6*(opp.getArmies()+1+getReinforcementOnRegion(state,opp))) );
									int defArmies = reg.getArmies();
									scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, opp, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
									reg.setArmies(defArmies-takeArmies);
									for (Region reg0 : getNeighborsOwnedByPlayerName(opp, myName)){
										if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, opponentName).size()==1 ){
											int takeArmies0 = reg0.getArmies()-1;
											scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, opp, takeArmies0));
											System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg0.getId() + " with " + takeArmies0 + " armies");
											reg0.setArmies(reg0.getArmies()-takeArmies0);	
										}
									}
									opp.setPlayerName("unknown");
								}
							}
						}	
					}	
				}
			}
		}
		
	} catch ( Exception e ){
		e.printStackTrace();	}

// Place armies on isolated region in dangerous SuperRegion
	try{
		for (Region reg : myRegions){
			if (checkIsolatedInDangerousSuperRegion(state, reg)){
				if (reg.getArmies()+armiesLeft >=10){
					int placeArmies = Math.min(armiesLeft, 15 - reg.getArmies() );
					if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Isolated Region in dangerous SuperRegion: " + reg.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
					}
					for (Region neut : getSubRegionsOwnedByPlayerName(reg.getSuperRegion(), "neutral")){
						if (reg.getNeighbors().contains(neut)){
							int takeArmies = reg.getArmies()-1;
							int defArmies = reg.getArmies();
							scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(defArmies-takeArmies);
							neut.setPlayerName("unknown");
							break;
						}
					}	
				}else{
					int placeArmies = armiesLeft;
					if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Isolated Region in dangerous SuperRegion: " + reg.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}	
	
	
//Placing armies to take neutrals   (priorityNeutralRegions_2)
	try{
		for (Region neut : priorityNeutralRegions_2){
			Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
			if (priorityNeutralRegions_1.isEmpty()){
				if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
					if ( reg.getArmies() + armiesLeft >= neut.getArmies()+2 || reg.getArmies() >= neut.getArmies()+2){
						if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0 ){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
						else{
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
					}
				}
				else if ( priorityNeutralRegions_1.isEmpty()){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += opp.getArmies();
					}
					if ( reg.getArmies()+armiesLeft-(neut.getArmies()+1) >= (int)(opponentArmies+7)*0.8){
						int placeArmies = armiesLeft;
						if (placeArmies>0){
							System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = neut.getArmies()+1;
						scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}			
		
// Placing armies to take neutrals (priorityNeutralRegions_3)	
	try{
		if (priorityNeutralRegions_1.isEmpty() && priorityNeutralRegions_2.isEmpty()){
			for (Region neut : priorityNeutralRegions_3){
				Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
				if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && !checkIfSuperRegionIsBorderedByEnnemy(state, neut.getSuperRegion()) && !isolatedInDangerousSuperRegion.contains(reg)	){
					if ( (reg.getArmies() + armiesLeft >= neut.getArmies()+2  ) || reg.getArmies() >= neut.getArmies()+2){
						if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
						else{
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
					}
				}
				else if ( priorityNeutralRegions_1.isEmpty() && priorityNeutralRegions_2.isEmpty()){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += opp.getArmies();
					}
					if ( reg.getArmies()+armiesLeft-(neut.getArmies()+1) >= (int)(opponentArmies+7)*0.8){
						int placeArmies = armiesLeft;
						if (placeArmies>0){
							System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = neut.getArmies()+1;
						scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}				
/**	
	try{
		for (Region neut : priorityNeutralRegions_3){
			Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
			if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && !checkIsolatedInDangerousSuperRegion(state, reg) ){
				if ( reg.getArmies() + armiesLeft >= neut.getArmies()+2 ){
					if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
						if (!underAttackRegions.isEmpty()){
							int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}else{
							int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
					else{
						if (!underAttackRegions.isEmpty()){
							int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}else{
							int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
							if (placeArmies>0){
								System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
								placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
								armiesLeft-=placeArmies;
								reg.setArmies(reg.getArmies()+placeArmies);
							}
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
				}
			}
			else{
				int opponentArmies=0;
				for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
					opponentArmies += opp.getArmies();
				}
				if ( reg.getArmies()+armiesLeft-(neut.getArmies()+1) >= (int)(opponentArmies+p)*0.9){
					int placeArmies = armiesLeft;
					if (placeArmies>0){
						System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
					placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
					armiesLeft-=placeArmies;
					reg.setArmies(reg.getArmies()+placeArmies);
					}
					for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
						if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
							int takeArmies = reg0.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
							reg0.setArmies(reg0.getArmies()-takeArmies);	
						}
					}
					int takeArmies = neut.getArmies()+1;
					scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
					System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
					reg.setArmies(reg.getArmies()-takeArmies);
					neut.setPlayerName("unknown");	
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}	
	*/
				
		
// Placing armies to take neutrals (priorityNeutralRegions_4)	
	try{
		if (priorityNeutralRegions_1.isEmpty() && priorityNeutralRegions_2.isEmpty() && priorityNeutralRegions_3.isEmpty() ){
			for (Region neut : priorityNeutralRegions_4){
				Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
				if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && !checkIsolatedInDangerousSuperRegion(state, reg)){
					if ( (reg.getArmies() + armiesLeft >= neut.getArmies()+2 ) || reg.getArmies() >= neut.getArmies()+2){
						if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
						else{
							if (!underAttackRegions.isEmpty()){
								int placeArmies = Math.min(neut.getArmies()+2-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}else{
								int placeArmies = Math.min(neut.getArmies()+3-reg.getArmies(), armiesLeft);
								if (placeArmies>0){
									System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
									placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
									armiesLeft-=placeArmies;
									reg.setArmies(reg.getArmies()+placeArmies);
								}
							}
							for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
								if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
									int takeArmies = reg0.getArmies()-1;
									scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
									System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
									reg0.setArmies(reg0.getArmies()-takeArmies);	
								}
							}
							int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
					}
				}
				else if(priorityNeutralRegions_1.isEmpty() && priorityNeutralRegions_2.isEmpty() && priorityNeutralRegions_3.isEmpty()){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies += opp.getArmies();
					}
					if ( reg.getArmies()+armiesLeft-(neut.getArmies()+1) >= (int)(opponentArmies+7)*0.9){
						int placeArmies = armiesLeft;
						if (placeArmies>0){
							System.err.println("line " + getLineNumber() + ": placing " + placeArmies + " armies on Region: " + reg.getId() + " to take Region " + neut.getId());
						placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, placeArmies));
						armiesLeft-=placeArmies;
						reg.setArmies(reg.getArmies()+placeArmies);
						}
						for (Region reg0 : getNeighborsOwnedByPlayerName(neut, myName)){
							if (!reg.equals(reg0) && reg0.getArmies()>1 && getNeighborsOwnedByPlayerName(reg0, "neutral").size()==1 && getNeighborsOwnedByPlayerName(reg0, opponentName).isEmpty()){
								int takeArmies = reg0.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg0, neut, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg0.getId() + " with " + takeArmies + " armies");
								reg0.setArmies(reg0.getArmies()-takeArmies);	
							}
						}
						int takeArmies = neut.getArmies()+1;
						scheduledAttackTransferMoves3.add(new AttackTransferMove(myName, reg, neut, takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
						neut.setPlayerName("unknown");	
					}
				}
			}
		}
	} catch ( Exception e ){
		e.printStackTrace();
	}	
//defend SuperRegion by attacking the threatening opponent region
		try{
			for (Region reg : priorityMyRegions_0){
				if (!getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty()){
					Region opp=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(reg, opponentName));
					int defendingArmies =0;
					for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName)){
						defendingArmies+=reg1.getArmies()-1;
					}
					if (defendingArmies>( (int) (opp.getArmies()+7)*1.3 ) ){
						for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName)){
							if (reg1.getArmies()>1 && !reg1.equals(reg)){
							int takeArmies = reg1.getArmies()-1;
							scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg1, opp , takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
							}
						}
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg, opp , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}else{
						for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName)){
							if (reg1.getArmies()>1 && !reg1.getSuperRegion().equals(reg.getSuperRegion())){
								int takeArmies = reg1.getArmies()-1;
								scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg1, opp , takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
								reg1.setArmies(reg.getArmies()-takeArmies);
							}
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	
	
// take Brazil	
		try{
			if (!getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), opponentName).isEmpty() && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), myName).isEmpty() && state.getVisibleMap().getRegion(12).ownedByPlayer("neutral") ){
				if ( !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(12), myName).isEmpty()){
					Region neut = state.getVisibleMap().getRegion(12);
					Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
					if (state.getVisibleMap().getSuperRegion(4).ownedByPlayer(myName) && reg.getArmies()>3 && reg.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(2)) ){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg, neut , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}else if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4),myName).isEmpty() && reg.getArmies()>30 && !reg.equals(state.getVisibleMap().getRegion(10)) ){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}

// take North Africa
		try{
			if (!getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4), opponentName).isEmpty() && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4), myName).isEmpty() && state.getVisibleMap().getRegion(21).ownedByPlayer("neutral") ){
				if ( !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(21), myName).isEmpty() ){
					Region neut=state.getVisibleMap().getRegion(21);
					Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(21), myName));
					if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), myName).size()>1 && getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), opponentName).isEmpty() && reg.getArmies()>4 && reg.getSuperRegion().equals(state.getVisibleMap().getSuperRegion(4)) ){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}else if (getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2),myName).isEmpty() && reg.getArmies()>30){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	
// take Venezuela
		try{
			if (!getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), opponentName).isEmpty() && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(2), myName).isEmpty() ){
				if (state.getVisibleMap().getRegion(10).ownedByPlayer("neutral") ){
					if ( !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(10), myName).isEmpty()){
						Region neut = state.getVisibleMap().getRegion(10);
						Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
						if (reg.getArmies()>30){
							if (reg.equals(state.getVisibleMap().getRegion(12)) && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4), myName).isEmpty() && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(4), opponentName).isEmpty()){
								int takeArmies = reg.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
								reg.setArmies(reg.getArmies()-takeArmies);
							}else if ( !reg.equals(state.getVisibleMap().getRegion(12)) ){
								int takeArmies = reg.getArmies()-1;
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
								reg.setArmies(reg.getArmies()-takeArmies);
							}
						}
					}
				}else if ( !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(11), myName).isEmpty() && state.getVisibleMap().getRegion(11).ownedByPlayer("neutral")){
					Region neut = state.getVisibleMap().getRegion(11);
					Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
					if (reg.getArmies()>30 && reg.equals(state.getVisibleMap().getRegion(13))){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
//take Indonesia
		try{
			if (!getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6), opponentName).isEmpty() && !getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6), myName).isEmpty() ){
				Region reg=getRegionWithMaxArmiesOfTheList(getSubRegionsOwnedByPlayerName(state.getVisibleMap().getSuperRegion(6), myName));
				if (state.getVisibleMap().getRegion(39).ownedByPlayer("neutral") ){
					if ( !getNeighborsOwnedByPlayerName(state.getVisibleMap().getRegion(39), myName).isEmpty()){
						Region neut = state.getVisibleMap().getRegion(39);
						if (reg.getArmies()>30){
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
						}
					}
				}else if ( !getNeighborsOwnedByPlayerName(reg, "neutral").isEmpty() && !state.getVisibleMap().getRegion(39).ownedByPlayer(myName)){
					Region neut = getNeighborsOwnedByPlayerName(reg, "neutral").get(0);
					if (reg.getArmies()>30 ){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}
				}else if ( !getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && !state.getVisibleMap().getRegion(39).ownedByPlayer(myName) ){
					Region opp= getRegionWithMinArmiesOfTheList(getNeighborsOwnedByPlayerName(reg, opponentName));
					if (reg.getArmies()>30 && reg.getArmies()>2*opp.getArmies()){
						int takeArmies = reg.getArmies()-1;
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, opp , takeArmies));
						System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
						reg.setArmies(reg.getArmies()-takeArmies);
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
// transfer leftovers to a region bordering a neutral	
	for (Region reg : safeRegions){
		try{
			if ( reg.getArmies()>1){ 
				for (Region neig : reg.getNeighbors()){ 
					if (!getNeighborsOwnedByPlayerName(neig, "neutral").isEmpty()){
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neig, reg.getArmies()-1));
						System.err.println("line " + getLineNumber() + ": transfer " + (reg.getArmies()-1) + " armies from Region  " + reg.getId() + " to go to Region " + neig.getId());
						reg.setArmies(1);
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}	
	
//transfer leftovers aleatory	
	for (Region reg : safeRegions){
		try{
			if (reg.getArmies()>1){ 
				Region neig=reg.getNeighbors().get(0); 
				scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neig, reg.getArmies()-1));
				System.err.println("line " + getLineNumber() + ": transfer " + (reg.getArmies()-1) + " armies from Region  " + reg.getId() + " to go to Region " + neig.getId());
				reg.setArmies(1);
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}
	
//transfer leftovers to stack them
	for (Region reg : myRegions){
		try{
			if ( reg.getArmies()>1 && reg.getArmies()<=3 && getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() ){ 
				for (Region neig : getNeighborsOwnedByPlayerName(reg, myName)){ 
					if (!getNeighborsOwnedByPlayerName(neig, "neutral").isEmpty() && neig.getArmies() >=reg.getArmies()){
						scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neig, reg.getArmies()-1));
						System.err.println("line " + getLineNumber() + ": transfer " + (reg.getArmies()-1) + " armies from Region  " + reg.getId() + " to go to Region " + neig.getId());
						reg.setArmies(1);
						break;
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}	
	
//take neutrals (priorityNeutralRegions_5)	
		try{
			for (Region neut : neutralRegions){
				Region reg=getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(neut, myName));
				if (getNeighborsOwnedByPlayerName(reg, opponentName).isEmpty() && !checkIsolatedInDangerousSuperRegion(state, reg)){
					if ( reg.getArmies()>= neut.getArmies()+2 ){
						if (getNeighborsOwnedByPlayerName(reg, "neutral").size()==1 || reg.getArmies()<8){
							int takeArmies = reg.getArmies()-1;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
						else{
							int takeArmies = Math.min(reg.getArmies()-1, neut.getArmies()+2 ) ;
							scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg, neut, takeArmies));
							System.err.println("line " + getLineNumber() + ": attacking Region " + neut.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
							reg.setArmies(reg.getArmies()-takeArmies);
							neut.setPlayerName("unknown");	
						}
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}	
//Combine forces to attack a big stack		
	for (Region opp : opponentRegions){
		try{
			if (!getNeighborsOwnedByPlayerName(opp, myName).isEmpty()){
				Region reg = getRegionWithMaxArmiesOfTheList(getNeighborsOwnedByPlayerName(opp, myName));	
				if ( reg.getArmies() < (int)((opp.getArmies()+p)*1.1 ) &&  getNeighborsOwnedByPlayerName(reg, opponentName).size()==1) {
					int myAttackArmies=0;
					for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName) ){
						myAttackArmies+=reg1.getArmies();
					}
					if ( myAttackArmies > (int)((opp.getArmies()+p)*1.3) ){
						for (Region reg1 : getNeighborsOwnedByPlayerName(opp, myName) ){
							int takeArmies = reg1.getArmies()-1;
							if (takeArmies>0){
								scheduledAttackTransferMoves2.add(new AttackTransferMove(myName, reg1, opp, takeArmies));
								System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg1.getId() + " with " + takeArmies + " armies");
								reg1.setArmies(reg1.getArmies()-takeArmies);
								opp.setPlayerName("unknown");
							}
						}
	
					}
				}
			}
		} catch ( Exception e ){
			e.printStackTrace();
		}
	}
//______________
	for (Region reg : underAttackRegions){
		int opponentArmies=0;
		for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
			opponentArmies += opp.getArmies();
		} 
		if (reg.getArmies()-1< ((int) (opponentArmies+p)*1.5) && reg.getArmies()-1> ((int) (opponentArmies+p)*0.8)){
			for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
				if (opp.getSuperRegion().equals(reg.getSuperRegion()) && opp.getArmies()+p < (int) (reg.getArmies())*0.7 && opp.getArmies() > (int) (reg.getArmies())*0.3 ){
					int takeArmies = reg.getArmies()-1;
					scheduledAttackTransferMoves1.add(new AttackTransferMove(myName, reg, opp, takeArmies));
					System.err.println("line " + getLineNumber() + ": attacking Region " + opp.getId() + " from Region  " + reg.getId() + " with " + takeArmies + " armies");
					reg.setArmies(reg.getArmies()-takeArmies);
					opp.setPlayerName("unknown");
				}
			}
		}
	}
// isolated regions run for life
	for (Region reg : priorityMyRegions_2){
		int opponentArmies=0;
		for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
			opponentArmies+=opp.getArmies()-1;
		}
		if (reg.getArmies()<(int) (opponentArmies)*0.6){
			for (Region neig : reg.getSuperRegion().getSubRegions()){
				if (reg.getNeighbors().contains(neig) && reg.getArmies()>neig.getArmies()+2){
					int takeArmies = reg.getArmies()-1;
					scheduledAttackTransferMoves0.add(new AttackTransferMove(myName, reg, neig, takeArmies));
					System.err.println("line " + getLineNumber() + ": Region " + reg.getId() + " running to region " + neig.getId() + " with " + takeArmies + " armies");
					reg.setArmies(reg.getArmies()-takeArmies);
					neig.setPlayerName("unknown");
				}
			}
		}
	}
	
	if (state.getRoundNumber()>1){
		System.err.println("__________________________ History __________________________");
		for (int rd=1; rd<state.getRoundNumber(); rd++){
			System.err.println("_____________________________________________________________");
			System.err.println("Round: " + rd);
			System.err.println("........................Opponent Moves.......................");
			System.err.println("Place Armies:");
			for (PlaceArmiesMove  placeMove : state.getOpponentPlaceArmiesHistory().get(rd)){
				System.err.println("opponent deployed " + placeMove.getArmies() + " on region " + placeMove.getRegion().getId());
			}

			System.err.println("Attack/Transfer:");
			for (AttackTransferMove  attackMove : state.getopponentAttackMovesHistory().get(rd)){
				System.err.println("opponent attacked/transfer from region " + attackMove.getFromRegion().getId() + " to region " + attackMove.getToRegion().getId() + " with " + attackMove.getArmies()+" armies");
			}
			System.err.println(".........................Visible Map.........................");
			System.err.print("My Regions: ");
			for (Region reg : getMyRegions(state, state.getMapHistory().get(rd-1)) ){
				System.err.print(reg.getId() + " ; ");
			}
			System.err.println("");
			System.err.print("Opponent Regions: ");
			for (Region reg : getOpponentRegions(state, state.getMapHistory().get(rd-1)) ){
				System.err.print(reg.getId() + " ; ");
			}
			System.err.println("");
		}
		System.err.println("_____________________________________________________________");
	}
// randomly place extra armies
		int armies = 2;
		int r1=0;
		if (!underAttackRegions.isEmpty()){
			while(armiesLeft > 0 && r1<3){
				for (Region reg : underAttackRegions){
					int opponentArmies=0;
					for (Region opp : getNeighborsOwnedByPlayerName(reg, opponentName)){
						opponentArmies+=opp.getArmies()-1;
					}
					if (reg.getArmies()+armies>= (int) (opponentArmies)*0.8){
						int placeArmies=Math.min(armies,armiesLeft);
						if (placeArmies>0){
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, armies));
							armiesLeft -= placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
					}
				}
				r1++;
			}
		}
		int r2=0;
		while(armiesLeft > 0 && r2<3){
			for (Region reg : myRegions){
				if (checkIfBorderingPriorityNeutrals(priorityNeutralRegions_0, reg)){
					for (Region neut:reg.getNeighbors()){
						if (priorityNeutralRegions_0.contains(neut) && neut.ownedByPlayer("neutral")){
							int placeArmies=Math.min(armies,armiesLeft);
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, armies));
							armiesLeft -= placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
					}
				}
			}
			r2++;
		}
		int r3=0;
		while(armiesLeft > 0 && r3<3){
			for (Region reg : myRegions){
				if (checkIfBorderingPriorityNeutrals(priorityNeutralRegions_1, reg)){
					for (Region neut:reg.getNeighbors()){
						if (priorityNeutralRegions_1.contains(neut) && neut.ownedByPlayer("neutral")){
							int placeArmies=Math.min(armies,armiesLeft);
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, armies));
							armiesLeft -= placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
					}
				}
			}
			r3++;
		}
		int r4=0;
		while(armiesLeft > 0 && r4<3){
			for (Region reg : myRegions){
				if (checkIfBorderingPriorityNeutrals(priorityNeutralRegions_2, reg)){
					for (Region neut:reg.getNeighbors()){
						if (priorityNeutralRegions_2.contains(neut) && neut.ownedByPlayer("neutral")){
							int placeArmies=Math.min(armies,armiesLeft);
							placeArmiesMoves.add(new PlaceArmiesMove(myName, reg, armies));
							armiesLeft -= placeArmies;
							reg.setArmies(reg.getArmies()+placeArmies);
						}
					}
				}
			}
			r4++;
		}
		
		state.setScheduledAttackTransferMoves0(scheduledAttackTransferMoves0);
		state.setScheduledAttackTransferMoves1(scheduledAttackTransferMoves1);
		state.setScheduledAttackTransferMoves2(scheduledAttackTransferMoves2);
		state.setScheduledAttackTransferMoves3(scheduledAttackTransferMoves3);
		state.setScheduledAttackTransferMoves4(scheduledAttackTransferMoves4);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.err.println("time spent on method: " + duration);
		return placeArmiesMoves;
	}

	@Override

	public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) 
	{
		
		ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();


attackTransferMoves.addAll(state.getScheduledAttackTransferMoves0());

attackTransferMoves.addAll(state.getScheduledAttackTransferMoves1());

attackTransferMoves.addAll(state.getScheduledAttackTransferMoves2());

attackTransferMoves.addAll(state.getScheduledAttackTransferMoves3());	
		
attackTransferMoves.addAll(state.getScheduledAttackTransferMoves4());
		
		return attackTransferMoves;
	}

	public static void main(String[] args)
	{
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}

}

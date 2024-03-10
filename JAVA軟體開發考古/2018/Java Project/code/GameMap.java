package FinalProject;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class GameMap extends JFrame implements KeyListener{
	private int mapWidth, mapHeight;
	//moveItem -> pushNext -> addPushItem -> swapAllLine -> renewMap -> setStopPushList
	//why setStopPushList is after the move <- ok //因為推的時候要先判斷當下的情境 所以是移動完更新

	private JLabel mapLabel[][];
	private static final int SIZE = 30;
	private JLabel you;
//	private debugMap mapSetArrayDebug;
//	private debugMap maptransparentDebug;
	private Map<String, ImageIcon> getIcon;
	private ImageIcon icon[];
	private String mapId[][], mapIdTrans[][],mapStatic[][];
	private String youStore; //remember now focus who
	private int xCancel,yCancel,cancelFlag,changeFlag;
	private String cancelItem;
	private JLabel winJLabel, failJLabel;
	private ImageIcon winIcon, failIcon;
	ArrayList<String> stopList = new ArrayList<String>();
	ArrayList<String> pushList = new ArrayList<String>();
	ArrayList<String> transList= new ArrayList<String>();
	ArrayList<String> pushItem = new ArrayList<String>();
	ArrayList<String> winList  = new ArrayList<String>();
	ArrayList<String> openList = new ArrayList<String>();

	
	public GameMap(int stage){
		changeFlag = 0;
		cancelItem = "tee_";
		youStore = "baba_p";
		cancelFlag = 0;
		//set frame location 
		setSize(800,700);
		setLocation(600,20);
		//initial map size
		storeMap(stage);
	    //for keyEvent
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		//initial frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
	    //initial map
		mapLabel = new JLabel[mapHeight][mapWidth];
		winJLabel = new JLabel();
		failJLabel = new JLabel();
		setMapIcon();
		initialMap();
		//stopList and pushList and transientList initialize
		setStopPushList();
		setTranList();
//		setPrintMap();		
	}
	
    //set stop and push List
    public void setStopPushList() {
    	int tmp = 0;
		stopList.clear();
		pushList.clear();
		winList.clear();
		openList.clear();
    	pushList.add("is_c");
		for(int i=0; i<mapHeight; ++i) {
			for(int j=0; j<mapWidth; ++j) {
				if( mapId[i][j].equals("black") )
					continue;
				//detect (is statement) to determine the stop condition 
			    else if( mapId[i][j].equals("is_c")  ) {
			    	//the top
			    	if(  i-1>=0 && i+1<mapHeight &&!mapId[i-1][j].equals("black") )
				    	if(mapId[i-1][j].split("_")[1].equals("l")) {
				    		if( mapId[i+1][j].equals("stop_r") ||  mapId[i+1][j].equals("sink_r") ) {
				    			//System.out.println( "-------stopList add ---------show in code 90"+mapId[i-1][j].split("_")[0] + "_p" );
				    			stopList.add(mapId[i-1][j].split("_")[0] + "_p");
				    		}
				    		else if(mapId[i+1][j].equals("win_r"))
			    				winList.add(mapId[i-1][j].split("_")[0] + "_p");
				    		else if(mapId[i+1][j].equals("push_r")) {
				    			//System.out.println("print the pushList add after ->"+mapId[i+1][j].split("_")[0] + "_p");
				    			tmp = 1;
				    			//System.out.println( mapId[i-1][j].split("_")[0] + "_p" );
			    				pushList.add(mapId[i-1][j].split("_")[0] + "_p");
			    				cancelTrans( mapId[i-1][j].split("_")[0] + "_p"  );
				    		}
				    		else if(mapId[i+1][j].equals("open_r")) {
				    			//System.out.println("in code 104 add openList "+ mapId[i-1][j].split("_")[0] + "_p");
				    			openList.add(mapId[i-1][j].split("_")[0] + "_p");
				    		}
				    	}
			    	//the left
			    	if(  j-1>=0 && j+1 < mapWidth && !mapId[i][j-1].equals("black")  )
				    	if(mapId[i][j-1].split("_")[1].equals("l")) {
				    		if( mapId[i][j+1].equals("stop_r") || mapId[i][j+1].equals("sink_r") ) {
				    			//System.out.println( "-------stopList add ---------show in code 110"+mapId[i-1][j].split("_")[0] + "_p" );
				    			stopList.add(mapId[i][j-1].split("_")[0] + "_p");
				    		}
				    		// Add winList elements
			    			else if(mapId[i][j+1].equals("win_r"))
			    				winList.add(mapId[i][j-1].split("_")[0] + "_p");
			    			else if(mapId[i][j+1].equals("push_r")) {
			    				//System.out.println("print the pushList add after ->"+mapId[i][j-1].split("_")[0] + "_p");
			    				tmp = 1;
			    				pushList.add(mapId[i][j-1].split("_")[0] + "_p");
			    				cancelTrans( mapId[i][j-1].split("_")[0] + "_p"  );
			    			}
			    			else if(mapId[i][j+1].equals("open_r")) {
			    				//System.out.println("in code 104 add openList "+ mapId[i][j-1].split("_")[0] + "_p");
			    				openList.add(mapId[i][j-1].split("_")[0] + "_p");
			    			}
				    	}
			    }
				//add pushList
			    else if(mapId[i][j].split("_")[1].equals("r") || mapId[i][j].split("_")[1].equals("l") ) {
			    	pushList.add(mapId[i][j]);  
			    }
			}
		}
		if(tmp != 0) {
			//System.out.println("has something be set can push");
		}
		else if(cancelFlag == 1){
			//if nothing is be set to push -> renew the cancelItem`s mapIdTrans
			//System.out.printf("Renew cancelItem (%d,%d)\n\n",xCancel,yCancel);
			mapIdTrans[yCancel][xCancel] = cancelItem;
			cancelFlag= 0;
		}
    }
    //move Item 
    public void moveItem(int currentX,int currentY,String op) {
    	//System.out.println(pushList);
    	int nextX = 0;
    	int nextY = 0;
    	int nextXpos = 0;
    	int nextYpos = 0;
    	int xcancel=xCancel,ycancel=yCancel;
    	if( op.equals("right") ) {
    		xcancel+=1;
    		nextX = currentX+1;
    		nextY = currentY;
    		nextXpos = SIZE;
        	nextYpos = 0;
    	}
    	else if( op.equals("left") ) {
    		xcancel-=1;
    		nextX = currentX-1;
    		nextY = currentY;		
    		nextXpos = -SIZE;
        	nextYpos = 0;
    	}
    	else if( op.equals("down") ) {
    		ycancel+=1;
    		nextX = currentX;
    		nextY = currentY+1;
    		nextXpos = 0;
        	nextYpos = SIZE;
		}
    	else if( op.equals("up") ) {
    		ycancel-=1;
    		nextX = currentX;
    		nextY = currentY-1;
    		nextXpos = 0;
        	nextYpos = -SIZE;
		}
		//detect next step is_stop
		String tmp = mapId[nextY][nextX];
		//System.out.println("show the stop list in code 175  "+stopList);
		

		if( !stopList.contains(tmp)  ) {
			if( !pushNext(currentX, currentY, op) ) 
				return;
			else {
				//if can change the xyCancel -> change the xyCancel
				if(changeFlag == 1) {
					//System.out.println("Update the axis");
					//System.out.printf("Before (%d,%d)",xCancel,yCancel);
					xCancel = xcancel;
					yCancel = ycancel;
//					System.out.printf("After (%d,%d)",xCancel,yCancel);
//					System.out.println();
//					System.out.println();
					changeFlag = 0;
					//System.out.println("set changeFlag equals 0");
				}
				//set you
				mapLabel[ currentY ][ currentX ].setVisible(true);
				mapLabel[ nextY ][ nextX ].setVisible(false);
				//set renewMap for baba next position because next position is wall by swap
				renewMap(nextX, nextY);
				you.setLocation(you.getX()+nextXpos,you.getY()+nextYpos);
			}
		}
		else 
			return;
    }
    //swap all line in pushNext function
    public boolean swapAllLine(int currentX,int currentY,String op) {
    	//if pushItem has cancelItem set changeFlag equals to 1 -> then later can change the xCancel and yCancel
    	if(pushItem.contains(cancelItem.split("_")[0] + "_p" )) {
    		//System.out.println("set changeFlag equals 1");
    		changeFlag = 1;
    	}
//    	System.out.print("show the pushItem  ");
//    	System.out.print(pushItem);
//    	System.out.println( "  "+cancelItem.split("_")[0] + "_l" );	
    		
    	int itemX,itemY;
    	if(op.equals("left") ) {
	    	if( (currentX-pushItem.size()-1)>=0 && !stopList.contains(mapId[currentY][currentX - pushItem.size() - 1])) {
				for(int j = pushItem.size() - 1; j >= 0; j--) {
					itemX = currentX - j - 2;
					itemY = currentY;
					mapLabel[ itemY ][ itemX ].setIcon(getIcon.get(mapId[itemY][itemX+1]));
					mapLabel[ itemY ][ itemX+1 ].setIcon(getIcon.get(mapId[itemY][itemX]));
					String swapString = mapId[itemY][itemX+1];
					mapId[itemY][itemX+1] = mapId[itemY][itemX];
					mapId[itemY][itemX] = swapString;
				}
				return true;
			}
	    	return false;
    	}
    	else if(op.equals("right") ) {
    		if( (currentX+pushItem.size()+1)<mapWidth && !stopList.contains(mapId[currentY][currentX + pushItem.size() + 1])) {
    			for(int j = pushItem.size() - 1; j >= 0; j--) {
    				itemX = currentX + j + 2;
    				itemY = currentY;
    				mapLabel[ itemY ][ itemX ].setIcon(getIcon.get(mapId[itemY][itemX-1]));
    				mapLabel[ itemY ][ itemX-1 ].setIcon(getIcon.get(mapId[itemY][itemX]));
    				String swapString = mapId[itemY][itemX-1];
    				mapId[itemY][itemX-1] = mapId[itemY][itemX];
    				mapId[itemY][itemX] = swapString;
    			}
    			return true;
    		}
			return false;
    	}
    	else if(op.equals("up") ) {
    		if(  (currentY-pushItem.size()-1)>=0 && !stopList.contains(mapId[currentY - pushItem.size() - 1][currentX])) {
    			for(int j = pushItem.size() - 1; j >= 0; j--) {
    				itemX = currentX;
    				itemY = currentY - j - 2;
    				mapLabel[ itemY ][ itemX ].setIcon(getIcon.get(mapId[itemY+1][itemX]));
    				mapLabel[ itemY+1 ][ itemX ].setIcon(getIcon.get(mapId[itemY][itemX]));
    				String swapString = mapId[itemY+1][itemX];
    				mapId[itemY+1][itemX] = mapId[itemY][itemX];
    				mapId[itemY][itemX] = swapString;
    			}
    			return true;
    		}
			return false;
    	}
    	else { //op.equals("down") 
    		if( (currentY+pushItem.size()+1)<mapHeight && !stopList.contains(mapId[currentY + pushItem.size() + 1][currentX]) ) {
    			for(int j = pushItem.size() - 1; j >= 0; j--) {
    				itemX = currentX;
    				itemY = currentY + j + 2;
    				mapLabel[ itemY ][ itemX ].setIcon(getIcon.get(mapId[itemY-1][itemX]));
    				mapLabel[ itemY-1 ][ itemX ].setIcon(getIcon.get(mapId[itemY][itemX]));
    				String swapString = mapId[itemY-1][itemX];
    				mapId[itemY-1][itemX] = mapId[itemY][itemX];
    				mapId[itemY][itemX] = swapString;
    			}
    			return true;
    		}
	    	return false;
    	}
    }
    public void cancelTrans(String search) { //search is flag_p
		for(int k=0; k<mapHeight; ++k) {
			for(int h=0; h<mapWidth; ++h) {
				if( mapIdTrans[k][h].equals(search) && cancelFlag != 1) {
					xCancel = h;
					yCancel = k;
					cancelItem = search;
					cancelFlag = 1;
					mapIdTrans[k][h] = "black";

				}
			}
		}
    }
    public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		//System.out.print(stopList);
		music("move");
		int currentX = you.getX()/SIZE;
		int currentY = you.getY()/SIZE;
		//set move
		if(e.getKeyCode() == KeyEvent.VK_RIGHT && currentX < mapWidth-1) {
			if( currentX+1<mapWidth && mapId[currentY][currentX+1]=="water_p" ) {
				failCall();
				return;
			}
			else if( currentX+2<mapWidth && mapId[currentY][currentX+2]=="water_p"  && pushList.contains(mapId[currentY][currentX+1])) {
				cancelFlag = 0;
				//System.out.println("door_p is pushed");
				mapId[currentY][currentX+2] = "black";
				mapIdTrans[currentY][currentX+2] = "black";
				moveItem(currentX,currentY,"right");
				mapLabel[currentY][currentX+2].setIcon(null);
				mapId[currentY][currentX+2] = "black";
				mapIdTrans[currentY][currentX+2] = "black";
				//cancelFlag = 0;
			}
			//detect the key is_push or not
			else if( currentX+2<mapWidth && mapId[currentY][currentX+2]=="door_p" && openList.contains(mapId[currentY][currentX+1]) && pushList.contains(mapId[currentY][currentX+1])) {
				cancelFlag = 0;
				//System.out.println("door_p is pushed");
				mapId[currentY][currentX+2] = "black";
				mapIdTrans[currentY][currentX+2] = "black";
				moveItem(currentX,currentY,"right");
				mapLabel[currentY][currentX+2].setIcon(null);
				mapId[currentY][currentX+2] = "black";
				mapIdTrans[currentY][currentX+2] = "black";
				//cancelFlag = 0;
			}
			else if( currentX+1<mapWidth && mapId[currentY][currentX+1]=="door_p" && pushList.contains(mapId[currentY][currentX+1]) ) {
				//delete the door_p in stoplist
		        for(String stop : stopList){
		            if(stop.equalsIgnoreCase("door_p"))
		                stopList.remove(stop);
		        }
			    moveItem(currentX,currentY,"right");
			}
			else
				moveItem(currentX,currentY,"right");
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT && currentX > 0) {
			if( currentX-1>=0 && mapId[currentY][currentX-1]=="water_p" ) {
				failCall();
				return;
			}
			else if( currentX-2>=0 && mapId[currentY][currentX-2]=="water_p"  && pushList.contains(mapId[currentY][currentX-1])) {
				cancelFlag = 0;
				//System.out.println("door_p is pushed");
				mapId[currentY][currentX-2] = "black";
				mapIdTrans[currentY][currentX-2] = "black";
				moveItem(currentX,currentY,"left");
				mapLabel[currentY][currentX-2].setIcon(null);
				mapId[currentY][currentX-2] = "black";
				mapIdTrans[currentY][currentX-2] = "black";
				//cancelFlag = 0;
			}
			else if( currentX-1>=0 && mapId[currentY][currentX-1]=="door_p" && pushList.contains(mapId[currentY][currentX-1]) ) {
				//delete the door_p in stoplist
		        for(String stop : stopList){
		            if(stop.equalsIgnoreCase("door_p"))
		                stopList.remove(stop);
		        }
			    moveItem(currentX,currentY,"left");
			}
			else {
				moveItem(currentX,currentY,"left");
			}			
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN && currentY < mapHeight-1) {
			if( currentY+1<mapHeight && mapId[currentY+1][currentX]=="water_p" ) {
				failCall();
				return;
			}
			else if( currentY+2<mapHeight && mapId[currentY+2][currentX]=="water_p"  && pushList.contains(mapId[currentY+1][currentX])) {
				cancelFlag = 0;
				//System.out.println("door_p is pushed");
				mapId[currentY+2][currentX] = "black";
				mapIdTrans[currentY+2][currentX] = "black";
				moveItem(currentX,currentY,"down");
				mapLabel[currentY+2][currentX].setIcon(null);
				mapId[currentY+2][currentX] = "black";
				mapIdTrans[currentY+2][currentX] = "black";
				//cancelFlag = 0;
			}
			else if( currentY+1<mapHeight && mapId[currentY+1][currentX]=="door_p" && pushList.contains(mapId[currentY+1][currentX]) ) {
				//delete the door_p in stoplist
		        for(String stop : stopList){
		            if(stop.equalsIgnoreCase("door_p"))
		                stopList.remove(stop);
		        }
			    moveItem(currentX,currentY,"down");
			}
			else
				moveItem(currentX,currentY,"down");
			//moveItem(currentX,currentY,"down");
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP && currentY > 0) {
			if( currentY-1>=0 && mapId[currentY-1][currentX]=="water_p" ) {
				failCall();
				return;
			}
			else if( currentY-2>=0 &&  mapId[currentY-2][currentX]=="water_p"  && pushList.contains(mapId[currentY-1][currentX])) {
				cancelFlag = 0;
				//System.out.println("door_p is pushed");
				mapId[currentY-2][currentX] = "black";
				mapIdTrans[currentY-2][currentX] = "black";
				moveItem(currentX,currentY,"up");
				mapLabel[currentY-2][currentX].setIcon(null);
				mapId[currentY-2][currentX] = "black";
				mapIdTrans[currentY-2][currentX] = "black";
				//cancelFlag = 0;
			}
			else if( currentY-1>=0 &&  mapId[currentY-1][currentX]=="door_p" && pushList.contains(mapId[currentY-1][currentX]) ) {
				//delete the door_p in stoplist
		        for(String stop : stopList){
		            if(stop.equalsIgnoreCase("door_p"))
		                stopList.remove(stop);
		        }
			    moveItem(currentX,currentY,"up");
			}
			else
				moveItem(currentX,currentY,"up");
		}
		else 
			return;
		
		if(judgeWin(you.getX()/SIZE, you.getY()/SIZE)) {
			winCall();
			return;
		}
//			System.out.println("STATE: "+judgeWin(you.getX()/SIZE, you.getY()/SIZE));
		
		// renew stopList and pushList
		setStopPushList();
		//renew transList 
		setTranList();
		judgeWho(currentX,currentY);
		judgeYouSurrounding();
//		printMap();
	}
	public boolean judgeOpenDoor(int nextX,int nextY) {
		if(openList.size() != 0)
			if(openList.contains(mapId[nextY][nextX]))
				return true;
		return false;
	}

    //update the mapId by mapIdTrans map (the next position need to be updated)
    public void renewMap(int nextX, int nextY) {
		for(int i = 0; i < mapHeight; i++) {
			for(int j = 0; j < mapWidth; j++) {
				if(i == nextY && j == nextX) {
					mapId[i][j] = mapIdTrans[i][j];
					mapLabel[i][j].setIcon(getIcon.get(mapIdTrans[i][j]));
				}
			}
		}
    }
    //add pushItem to pushItmeList
    public void addPushItem(int currentX,int currentY,String op) {
//    	System.out.println("testbefore");
//    	System.out.println(pushItem);
//    	System.out.println(pushList);
//    	System.out.println("testafter");
    	if(op.equals("left") ) {
			for(int i = currentX; i > 0; i--) 
				if(pushList.contains( mapId[currentY][i-1] ))
					pushItem.add( mapId[currentY][i-1] );
				else
					break;
    	}
    	else if(op.equals("right")) {
    		for(int i = currentX; i < mapWidth - 1; i++) 
				if(pushList.contains( mapId[currentY][i+1] )) 
					pushItem.add( mapId[currentY][i+1] );
				else
					break;
    	}
		else if(op.equals("up")) {
			for(int i = currentY; i > 0; i--) 
				if(pushList.contains( mapId[i-1][currentX] )) 
					pushItem.add( mapId[i-1][currentX] );
				else
					break;
		}
		else if(op.equals("down")) {
			
//			System.out.print("this test new down ->");
//			System.out.print(pushList);
//			System.out.print("   "+mapId[currentY+1][currentX]+"  "+ (currentY+1) +" "+ currentX);
//			System.out.println("");
			for(int i = currentY; i < mapHeight - 1; i++) 
				if(pushList.contains(mapId[i+1][currentX]))
					pushItem.add(mapId[i+1][currentX]);
				else
					break;
		}
    	
    }
    //print current map for debug
    public void printMap() {
//    	maptransparentDebug.update( mapIdTrans);
//    	mapSetArrayDebug.updateDebug(mapId);
    }
    //push by swap 
    public boolean pushNext(int currentX, int currentY, String dir) {
    	pushItem.clear();
    	switch (dir) {
		case "left":
	    	//Add pushItem
			addPushItem(currentX,currentY,"left");
	    	//detect the most left whether is stopList or not
			if( swapAllLine(currentX,currentY,"left") == true )
				return true;
			else
				return false;
		case "right":
			addPushItem(currentX,currentY,"right");
			if( swapAllLine(currentX,currentY,"right") == true )
				return true;
			else
				return false;
		case "up":
			addPushItem(currentX,currentY,"up");
			if( swapAllLine(currentX,currentY,"up") == true )
				return true;
			else
				return false;
		case "down":
			addPushItem(currentX,currentY,"down");
			if( swapAllLine(currentX,currentY,"down") == true )
				return true;
			else
				return false;
		default:
			return false;
		}
    }



    //set transparent List
    public void setTranList() {
    	transList.clear();
    	for(int i=0; i<mapHeight; ++i) {
			for(int j=0; j<mapWidth; ++j) {
				if(mapId[i][j].equals("black"))
					continue;
			    else {
			    	if(!stopList.contains(mapId[i][j]) && !pushList.contains(mapId[i][j]) && !winList.contains(mapId[i][j]) && !openList.contains(mapId[i][j])) {
			    		if(transList.size() != 0) {
			    			if(!transList.contains(mapId[i][j]))
			    				transList.add(mapId[i][j]);
			    		}
			    		else
			    			transList.add(mapId[i][j]);
			    	}
			    }
			}
		}
    }
    //judge who is the new you
    public void judgeWho(int currentX,int currentY) {
    	for(int i=0; i<mapHeight; ++i) {
			for(int j=0; j<mapWidth; ++j) {
				if(mapId[i][j].equals("is_c") ) {
					if( i-1>=0 && i+1< mapHeight && mapId[i+1][j].equals("you_r")  ) { //the top
			    		if( !youStore.equals(mapId[i-1][j]) )
			    			changeYou( mapId[i-1][j],youStore);
			    	}
			    	if( j-1>=0 && j+1 < mapWidth && mapId[i][j+1].equals("you_r") ) {//the left
			    		if( !youStore.equals(mapId[i][j-1]) )
			    			changeYou( mapId[i][j-1],youStore);
			    	}
			    }
			}
		}
    }
    //use winList to check the next move position is or not winList
    public boolean judgeWin(int nextX, int nextY) {
    	if(winList.size() != 0)
    		if(winList.contains(mapId[nextY][nextX]))
    			return true;
    	return false;
    }
    //change you to new item
    public void changeYou(String changeItem,String originalItem) {
    	//something is you (if something is not l end -> set stopSignal true
    	if(changeItem.equals("black")) {
    		failCall();
    		return;
    	}
    	if( !changeItem.split("_")[1].equals("l") ) {
    		failCall();
    		return;
    	}
    	youStore = changeItem;
    	originalItem = originalItem.substring(0,originalItem.length()-1) + "p";
    	changeItem   = changeItem.substring(0,changeItem.length()-1) + "p";    	
    	int cX = 0;
    	int cY = 0;
		for(int k=0; k<mapHeight; ++k) {
			for(int h=0; h<mapWidth; ++h) {
				if( mapId[k][h].equals(changeItem) ) {
					//baba move before (cX,cY)
					//baba move after  (k ,h)
					cX = you.getX()/SIZE;
					cY = you.getY()/SIZE;
					mapIdTrans[cY][cX] = originalItem; 
					mapIdTrans[k][h] = mapStatic[k][h];
					mapId[cY][cX] = originalItem;
					mapId[k][h] = "black";
					
					mapLabel[cY][cX].setVisible(true);
					mapLabel[cY][cX].setIcon(getIcon.get(originalItem));
					mapLabel[k][h].setVisible(false);
					if(mapIdTrans[k][h].equals("black"))
						mapLabel[k][h].setIcon(null);
					else
						mapLabel[k][h].setIcon(getIcon.get(mapIdTrans[k][h]));
					you.setIcon( getIcon.get(changeItem) );
 					you.setLocation(h*SIZE, k*SIZE);
				}
			}
		}
    }
    public void judgeYouSurrounding() {
    	for(int k=0; k<mapHeight; ++k) {
			for(int h=0; h<mapWidth; ++h) {
				if( mapId[k][h].equals("you_r")  && k-1>=0 && k+1< mapHeight && h-1>=0 && h+1 < mapWidth) {
					if( k-1>=0 && mapId[k-1][h].equals("is_c"))
						return;
					else if( k+1<mapHeight && mapId[k+1][h].equals("is_c") )
						return;
					else if( h-1>=0 && mapId[k][h-1].equals("is_c") ) 
						return;
					else if( h+1<mapWidth && mapId[k][h+1].equals("is_c") )
						return;
					else {
						failCall();
						return;
					}
				}
			}
		}
    }
    //initial map JLabel 2-D array
    public void initialMap() {
    	for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				if(mapId[i][j] == "black")
					mapLabel[i][j] = new JLabel();
				else if(mapId[i][j] == "baba_p") {
					mapLabel[i][j] = new JLabel();
					mapLabel[i][j].setVisible(false);
					you = new JLabel(getIcon.get(mapId[i][j]));	
					you.setSize(SIZE, SIZE);
					you.setLocation(SIZE*j, SIZE*i);
					you.setOpaque(true);
			        you.setBackground(Color.black);
			        add(you);
			        mapId[i][j] = "black";
				}
				else
					mapLabel[i][j] = new JLabel(getIcon.get(mapId[i][j]));		
		        mapLabel[i][j].setSize(SIZE, SIZE);
		        mapLabel[i][j].setLocation(SIZE*j, SIZE*i);
		        //set Background color
		        mapLabel[i][j].setOpaque(true);
		        mapLabel[i][j].setBackground(Color.black);
		        add(mapLabel[i][j]);
			}
		}
    	add(winJLabel);
    	add(failJLabel);
    }
//    public void setPrintMap() {
//    	mapSetArrayDebug = new debugMap( mapId , mapWidth ,mapHeight);
//		mapSetArrayDebug.setVisible(true);
//		maptransparentDebug = new debugMap( mapIdTrans,mapWidth,mapHeight);
//		maptransparentDebug.setVisible(true);
//		
//    }
    public void keyReleased(KeyEvent e) {
	}
    //read directory in current filesystem to initial getIcon mapping
    public void setMapIcon() {
    	icon = new ImageIcon[39];
    	getIcon =  new HashMap<>();
   
    	String dir = "./bin/resources/images/";
    	File nowFile = new File(dir);
    	try {
    		if (nowFile.isDirectory()) {
    			File[] filelist = nowFile.listFiles();
//    			System.out.println(filelist.length);
	    		for (int i = 0; i < filelist.length; i++) {
	    			if (filelist[i].isFile()) {
	    				String str = new String(filelist[i].getName());
//	    				System.out.println(str);
//	    				System.out.println("/resources/images/"+str);
	    				icon[i] = new ImageIcon(this.getClass().getResource("/resources/images/"+str));
	    				icon[i].setImage(icon[i].getImage().getScaledInstance(SIZE, SIZE,Image.SCALE_DEFAULT ));
	    				getIcon.put(str.substring(0,str.length()-4),icon[i]);
//	    				System.out.println(str.substring(0,str.length()-4));
	    			}
	    	    }
    		}
    	} 
    	catch (Exception e) {
    		System.out.print(e);
    	}
    }
    //when judge is fail -> call this function 
    public void failCall() {
    	System.out.println("State: False");
    	
    	failIcon = new ImageIcon(getClass().getResource("/resources/icons/fail.png"));
    	failIcon.setImage(failIcon.getImage().getScaledInstance(400, 300,Image.SCALE_DEFAULT ));
    	failJLabel.setIcon(failIcon);
    	failJLabel.setSize(400,300);
    	failJLabel.setLocation(160, 100);
		setComponentZOrder(failJLabel, 0);
    	
    	music("boom");
		setFocusable(false);
    }
    //when judge is win -> call this function 
    public void winCall() {
    	System.out.println("State: Win");
    	
    	winIcon = new ImageIcon(getClass().getResource("/resources/icons/congratulation.png"));
		winIcon.setImage(winIcon.getImage().getScaledInstance(468, 222,Image.SCALE_DEFAULT ));
		winJLabel.setIcon(winIcon);
		winJLabel.setSize(468,222);
		winJLabel.setLocation(130, 100);
		setComponentZOrder(winJLabel, 0);

    	music("cheer");
    	setFocusable(false);
    }
    
    public static void music(String str) 
    {       
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;
        AudioData MD;
        ContinuousAudioDataStream loop = null;

        try
        {
            InputStream test = new FileInputStream("./bin/resources/musics/"+str+".wav");
            BGM = new AudioStream(test);
            AudioPlayer.player.start(BGM);
//            MD = BGM.getData();
//            loop = new ContinuousAudioDataStream(MD);

        }
        catch(FileNotFoundException e){
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        MGP.start(loop);

    }
    //initial store map 2-Dimensional array for String array
    public void storeMap(int stage) {
		if(stage == 1) {
			   mapWidth = 24;
			   mapHeight = 18;  
			   mapId = new String [][]{
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "grass_p", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "grass_p", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "grass_p", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "is_c", "black", "black", "black", "black", "wall_p", "black", "grass_p", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "win_r", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "grass_p", "black", "black", "black"},
			     {"black", "grass_p", "black", "black", "black", "wall_p", "black", "flag_l", "black", "black", "black", "flag_p", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "grass_p", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "baba_p", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "baba_l", "black", "black", "wall_p", "black", "wall_l", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "grass_p", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "is_c", "black", "black", "wall_p", "black", "is_c", "black", "black", "black", "black", "wall_p", "black", "black", "black", "grass_p", "grass_p", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "you_r", "black", "black", "wall_p", "black", "stop_r", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "grass_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "grass_p", "black", "black", "black", "black"},
			     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},    
			   };
			   mapIdTrans = new String[][] {
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "grass_p", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "grass_p", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "grass_p", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "grass_p", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "grass_p", "black", "black", "black"},
				     {"black", "grass_p", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", /*flag_p->*/"flag_p", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "grass_p", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "grass_p", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "grass_p", "grass_p", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "grass_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "grass_p", "black", "black", "black", "black"},
				     {"black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"}, 
			   };
			   mapStatic = new String[][] {
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "grass_p", "black" , "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "grass_p", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "grass_p", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "grass_p", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "wall_p","wall_p", "wall_p" , "wall_p", "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "wall_p","black" , "black"  , "black" , "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "grass_p", "black", "black", "black"},
				     {"black", "grass_p", "black"  , "black", "black", "wall_p","black" , "black"  , "black" , "black", "black", /*flag_p->*/"black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "grass_p", "black", "black", "wall_p","black" , "black"  , "black" , "black", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "wall_p","wall_p", "wall_p" , "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "grass_p", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "grass_p", "grass_p", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "grass_p", "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "black", "black", "black", "black", "black", "black", "wall_p", "black", "black", "black", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black", "grass_p", "black", "black", "black", "black"},
				     {"black", "black"  , "black"  , "black", "black", "black" ,"black" , "black"  , "black" , "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black", "black"}, 
			   };
		 }
		else if(stage == 2) {
			mapWidth = 22;
			mapHeight = 16; 
			mapId   = new String [][]{
			 	{"baba_l" ,"wall_l" ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"},
				{"is_c"   ,"is_c"   ,"wall_p" ,"black"  ,"grass_p","black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p","wall_p","black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"},
				{"you_r"  ,"stop_r" ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"wall_p" ,"wall_p" ,"wall_p" ,"black"  ,"black"  ,"grass_p","black"  ,"wall_p" ,"black"  ,"baba_p" ,"black"  ,"black" ,"rock_p","black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"water_l","wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"grass_p","grass_p","black"  ,"is_c"   ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" /*rock_p->*/,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"sink_r" ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"grass_p","black"  ,"black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"water_p","water_p","water_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p" ,"wall_p","black"  ,"grass_p","black"  ,"black"},
                {"black"  ,"grass_p","black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"rock_p"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"rock_l","is_c"  ,"push_r","black"  ,"wall_p","black"  ,"black"  ,"grass_p","black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"water_p","water_p","water_p","black"  ,"wall_p" ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"grass_p","grass_p","black"  ,"wall_p" ,"water_p","water_p","water_p","black"  ,"black"  ,"black"  ,"wall_p","black" ,"flag_l","is_c"  ,"win_r" ,"black"  ,"wall_p","grass_p","black"  ,"black"  ,"black"},
                {"black"  ,"grass_p","grass_p","black"  ,"wall_p" ,"flag_p" ,"water_p","water_p","black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"grass_p","grass_p"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p","wall_p","wall_p","wall_p" ,"wall_p","black"  ,"black"  ,"grass_p","black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"}
				};
			mapIdTrans = new String[][] {
			 	{"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"},
				{"black"  ,"black"  ,"wall_p" ,"black"  ,"grass_p","black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p","wall_p","black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"},
				{"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"wall_p" ,"wall_p" ,"wall_p" ,"black"  ,"black"  ,"grass_p","black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"rock_p","black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"grass_p","grass_p","black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,/*rock_p->*/"black","black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"grass_p","black"  ,"black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"water_p","water_p","water_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p" ,"wall_p","black"  ,"grass_p","black"  ,"black"},
                {"black"  ,"grass_p","black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"rock_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"grass_p","black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"water_p","water_p","water_p","black"  ,"wall_p" ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"grass_p","grass_p","black"  ,"wall_p" ,"water_p","water_p","water_p","black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","grass_p","black"  ,"black"  ,"black"},
                {"black"  ,"grass_p","grass_p","black"  ,"wall_p" ,"flag_p" ,"water_p","water_p","black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"grass_p","grass_p"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p","wall_p","wall_p","wall_p" ,"wall_p","black"  ,"black"  ,"grass_p","black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"}
			};
			mapStatic = new String[][] {
			 	{"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"},
				{"black"  ,"black"  ,"wall_p" ,"black"  ,"grass_p","black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p","wall_p","black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"},
				{"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"wall_p" ,"wall_p" ,"wall_p" ,"black"  ,"black"  ,"grass_p","black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"grass_p","grass_p","black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"wall_p","black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"},
                {"grass_p","black"  ,"black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"black"  ,"black"  ,"black"  ,"wall_p","wall_p","wall_p","wall_p","wall_p","wall_p" ,"wall_p","black"  ,"grass_p","black"  ,"black"},
                {"black"  ,"grass_p","black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"grass_p","black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"black"  ,"black"},
                {"black"  ,"grass_p","grass_p","black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","grass_p","black"  ,"black"  ,"black"},
                {"black"  ,"grass_p","grass_p","black"  ,"wall_p" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black" ,"black" ,"black" ,"black" ,"black"  ,"wall_p","black"  ,"black"  ,"grass_p","grass_p"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p","wall_p","wall_p","wall_p" ,"wall_p","black"  ,"black"  ,"grass_p","black"},
                {"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black" ,"black" ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"}
			};

		}
		else if(stage == 3) {
			 mapWidth = 24;
			 mapHeight = 14;	 
			 mapId = new String [][]{
				    {"black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black"},
	                {"black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black","black","black","black","grass_p","black","black","black","black","black","black","black","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black","baba_l","is_c","you_r","black","black","black","black","black","black","flag_p","black","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","grass_p","black","black","black","black","black","black","grass_p","black","black","black","black","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black","black","black","black","black","black","black","black","grass_p","black","grass_p","black","wall_p","black","black","black","black","grass_l"},
	                {"black","black","black","black","black","wall_p","black","black","black","black","black","black","grass_p","black","black","grass_p","black","grass_p","wall_p","black","black","black","black","is_c"},
	                {"black","black","black","black","black","wall_p","grass_p","black","black","black","black","black","grass_p","black","flag_l","black","win_r","black","wall_p","black","black","black","black","stop_r"},
	                {"black","black","black","black","black","wall_p","black","grass_p","black","black","black","black","grass_p","black","black","grass_p","black","black","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black","black","black","black","grass_p","black","black","grass_p","black","black","black","baba_p","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black"},
	                {"black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black","black"}
			 };
			 mapIdTrans = new String[][] {
				    {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"},
	                {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p" ,"wall_p","wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black" ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"flag_p" ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","grass_p","black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"grass_p","black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"grass_p","black"  ,"grass_p","black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"grass_p","black"  ,"black"  ,"grass_p","black"  ,"grass_p","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","grass_p","black"  ,"black" ,"black" ,"black"  ,"black" ,"grass_p","black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"grass_p","black" ,"black" ,"black"  ,"black" ,"grass_p","black"  ,"black"  ,"grass_p","black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"grass_p","black" ,"black"  ,"grass_p","black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p" ,"wall_p","wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"},
	                {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"}
			 };
			 mapStatic = new String[][] {
				    {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"},
	                {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p" ,"wall_p","wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"grass_p","black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","grass_p","black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"grass_p","black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"grass_p","black"  ,"grass_p","black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"grass_p","black"  ,"black"  ,"grass_p","black"  ,"grass_p","wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","grass_p","black"  ,"black" ,"black" ,"black"  ,"black" ,"grass_p","black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"grass_p","black" ,"black" ,"black"  ,"black" ,"grass_p","black"  ,"black"  ,"grass_p","black"  ,"black"  ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","black"  ,"black"  ,"black" ,"black" ,"grass_p","black" ,"black"  ,"grass_p","black"  ,"black"  ,"black"  ,"black" ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","wall_p","wall_p" ,"wall_p" ,"wall_p","wall_p","wall_p" ,"wall_p","wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p" ,"wall_p","black","black","black","black","black"},
	                {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"},
	                {"black","black","black","black","black","black" ,"black"  ,"black"  ,"black" ,"black" ,"black"  ,"black" ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black"  ,"black" ,"black","black","black","black","black"}
 
			 };

			 
		}
		else if(stage == 4) {
			mapWidth  = 24;
			mapHeight = 16;	  

			mapId = new String [][]{
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "baba_l", "is_c"  , "you_r" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "door_l", "is_c"  , "stop_r", "black" , "wall_p", "black" , "black" , "black" , "black" , "star_p", "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "wall_p", "black" , "wall_l", "is_c"  , "stop_r", "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "key_l" , "black" , "push_r", "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "door_l", "is_c"  , "shut_r", "black" , "black" , "wall_p", "black", "black"},
			    {"black", "grass_p", "black"  , "black", "black", "black", "wall_p", "black" , "key_l" , "black" , "open_r", "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "door_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "key_p" , "black" , "is_c"  , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "door_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "baba_p", "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black" , "black" , "black" , "black" , "flag_p", "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "flag_l", "is_c"  , "win_r" , "black" , "black" , "black" , "black", "black"}   
			};
			mapIdTrans = new String[][] {
				{"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "star_p", "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "grass_p", "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "door_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "key_p" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "door_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black" , "black" , "black" , "black" , "flag_p", "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"}   
			};

			mapStatic = new String [][]{
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "grass_p", "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "door_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black" , "wall_p", "wall_p", "wall_p", "wall_p", "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "black" , "black" , "black" , "black" , "black" , "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "wall_p", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "black"  , "black", "black", "black", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"},
			    {"black", "black"  , "grass_p", "black", "black", "black", "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black" , "black", "black"}   
			};
		}
    }
    
}
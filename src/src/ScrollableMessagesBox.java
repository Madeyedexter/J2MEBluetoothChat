package src;


import javax.microedition.lcdui.*;
import java.util.Timer;
import java.io.IOException;





public class ScrollableMessagesBox extends CustomItem implements ItemCommandListener
{
  private String username=null;
  private Image eimage[]=new Image[8];
  private int cw=0;
  private int pindex=0;



  private boolean flag=true;


  // offsets for writing text
  private static final int XOFFSET = 5;
  private static final int YOFFSET = 3;

  // timer task delay between repeating
  private static final int KEY_DELAY = 200;  // 0.2 secs

  /* the factor multiplied to the number of text box lines
     to get the number of lines stored internally */
  private static final float LINES_FACTOR = 1.5f;


  private Command upCmd, downCmd;
  private boolean upArrowShowing, downArrowShowing;
     /* whether the up and down arrows are showing on the
        scrollbar drawn on the canvas */

  private int width, height;  // of the scrolling box
  private int fontHeight=0;
  private int imgHeight=0;
  private int maxVisLines;    // max no. of visible lines

  private String[] lines;     // stores the message lines
  private int maxLines;       // max no. of stored lines
  private int numLines = 0;   // current number of stored lines
  private int numImages=0;

  private int firstVisLine = 0;
     /* index of the line that's currently visible at the
         top of the box */

  // key repeating timer and task
  private Timer timer;
  private KeyRepeatTask keyRepeatTask;
  private boolean upKeyPressed = false;
  private boolean downKeyPressed = false;
  private Font f;


  public ScrollableMessagesBox(String title, int w, int h)
  {
    super(title);
    f=Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_MEDIUM);
	fontHeight =f.getHeight();
    width = w;
    height = h;

try
{
		eimage[0]=Image.createImage("/ei00.png");
     	eimage[1]=Image.createImage("/ei01.png");
     	eimage[2]=Image.createImage("/ei02.png");
     	eimage[3]=Image.createImage("/ei03.png");
     	eimage[4]=Image.createImage("/ei04.png");
     	eimage[5]=Image.createImage("/ei05.png");
     	eimage[6]=Image.createImage("/ei06.png");
     	eimage[7]=Image.createImage("/ei07.png");
}


     catch(IOException e)
     	{
     		System.err.println("Unable to locate icon!");

     	}






    maxVisLines = (height-(YOFFSET+1))/fontHeight;
     /* (YOFFSET+1) represents the offset from the top before
        the first message line is drawn and the border offset from
        the bottom of the customItem. */

    // initialize lines storage, the lines[] array
    maxLines = (int)(LINES_FACTOR * maxVisLines);
    lines = new String[maxLines];

    // create commands, but don't show them yet
    upCmd = new Command("Up", Command.ITEM, 1);
    downCmd = new Command("Down", Command.ITEM, 1);
    upArrowShowing = false;
    downArrowShowing = false;

    setItemCommandListener(this);

    setKeyRepetition();
  } // end of ScrollableMessagesBox()




  private void setKeyRepetition()
  /* If key repetition is not supported then set up a timer
     task to trigger repeat operations. */
  {
/*  The following code is edited out since I found on WTK2.2. Beta
    for Win98 that hasKeyRepeats() returned true but key repeats
    were not generated at run time.

    // decide how to offer key repetition
    if(hasKeyRepeats())
      System.out.println("Key Repeat is supported");
    else {  // key repeat not supported
      System.out.println("Key Repeat not supported");
*/
      // start a timer to carry out key repeats
      keyRepeatTask = new KeyRepeatTask(this);
      timer = new Timer();
      timer.schedule(keyRepeatTask, 0, KEY_DELAY);
//    }
  }  // end of setKeyRepetition()


  private boolean hasKeyRepeats()
  // are key repeats supported?
  {  return ((getInteractionModes() & KEY_REPEAT) != 0);  }


  // --------------- accept a new message -----------------------


  public void addMessage(String msg)
  // add a message to the text box
  {
    checkMessageSpace();


    // store the message

	{

    	lines[numLines] = msg;
    	numLines++;

	}
      




    ensureLineVisibility();

    // switch on down arrow if 2 or more lines have been added so far
    if (!downArrowShowing && (numLines > 1)) {
      addCommand(downCmd);
      downArrowShowing = true;
    }
    
    repaint();
  } // end of addMessage()







  private void checkMessageSpace()
  /* Check if the lines[] array is full. If it is, then the
     oldest message (lines[0]) must be deleted. The other lines
     are moved up in the array, making room for the new message
     at the end. */
  {
    if (numLines == maxLines) {  // is lines[] full?
      for (int i=0; i < (maxLines-1); i++)
        lines[i] = lines[i+1];     // delete first line; move others up
      numLines--;
      if (firstVisLine > 0)   // adjust visible line index
        firstVisLine--;
    }
  }  // end of checkMessageSpace()


  private void ensureLineVisibility()
  /* Check if the newly entered line is visible on screen.
     If not then scroll the text lines up so that the new line
     is visible at the bottom of the screen.
  */
  {
    // is the new line positioned off the bottom of the screen?
    int offScreenDist = numLines - (firstVisLine + maxVisLines);
    // System.out.println("offscreen dist: " + offScreenDist);
    if (offScreenDist > 0) {
      firstVisLine += offScreenDist;
                 // move display down so new line is visible
      if (!upArrowShowing) {
        addCommand(upCmd);
        upArrowShowing = true;   // can now move up
      }
    }
  } // end of ensureLineVisibility()


  // ----------------- UI methods ----------------

  /* up and down text movement can be triggered by
     key presses or command menu items. Key repetition
     is supported.  */


  protected void keyPressed(int keyCode)
  {
    // System.out.println("keyPressed: " + keyCode);
	int gameAct = getGameAction(keyCode);
	if ((gameAct == Canvas.UP) && upArrowShowing) {
      textMovesDown();
      upKeyPressed = true;
    }
	else if ((gameAct == Canvas.DOWN) && downArrowShowing) {
      textMovesUp();
      downKeyPressed = true;
    }
  }  // end of keyPressed()


  protected void keyReleased(int keyCode)
  {
    // System.out.println("keyReleased: " + keyCode);
	int gameAct = getGameAction(keyCode);
	if ((gameAct == Canvas.UP) && upKeyPressed)
      upKeyPressed = false;
	else if ((gameAct == Canvas.DOWN) && downKeyPressed)
      downKeyPressed = false;
  }  // end of keyReleased()


  protected void keyRepeated(int keyCode)
  // called if key repeats are supported by CustomItem
  {
    System.out.println("keyRepeated: " + keyCode);
	int gameAct = getGameAction(keyCode);
	if ((gameAct == Canvas.UP) && upArrowShowing)
      textMovesDown();
	else if ((gameAct == Canvas.DOWN) && downArrowShowing)
      textMovesUp();
  } // end of keyRepeated()


  public void repeatTextMove()
  /* Called by KeyRepeatTask periodically to repeat the
     current text movement operation. This timer task is
     only switched on if automatic key repeats aren't
     available. */
  {
    // System.out.println("repeatTextMove()");
	if (upKeyPressed && upArrowShowing)
      textMovesDown();
	else if (downKeyPressed && downArrowShowing)
      textMovesUp();
  }  // end of repeatTextMove()


  public void commandAction(Command c, Item i)
  // use commands to move the text
  { if (c == upCmd)
      textMovesDown();
    else if (c == downCmd)
      textMovesUp();
  } // end of CommandAction()


  private void textMovesDown()
  /* When the up command/key is pressed, then the messages should
     be moved _down_. This allows earlier messages to be seen.
  */
  {
    if (firstVisLine > 0) {  // if not already at messages start
      // System.out.println("textMovesDown()");
      firstVisLine--;
      if (firstVisLine == 0) {  // showing first message at top
        removeCommand(upCmd);   // disable upward scrolling
        upArrowShowing = false;
      }
      if (!downArrowShowing) {
        addCommand(downCmd);    // enable downward scrolling
        downArrowShowing = true;
      }
      repaint();
    }
  } // end of textMovesDown()


  private void textMovesUp()
  /* When the down command/key is pressed, then the messages should
     be moved _up_. This moves newer messages up the screen.
  */
  {
    if (firstVisLine < numLines-1) {   // if not already at end of messages
      // System.out.println("textMovesUp()");
      firstVisLine++;
      if (firstVisLine == numLines-1) {  // showing last message at top
        removeCommand(downCmd);     // disable downward scrolling
        downArrowShowing = false;
      }
      if (!upArrowShowing) {
        addCommand(upCmd);          // enable upward scrolling
        upArrowShowing = true;
      }
      repaint();
    }
  }  // end of textMovesUp()


  // ------------------ painting ------------------------

  protected void paint(Graphics g, int w, int h)
  /* The canvas is white with a black border, with a scrollbar
     drawn on the right. The text is drawn in lines, down the
     left side.
  */
  {

    // a white background with a black border
    g.setColor(200,255, 255);   // white background
    g.fillRect(0, 0, w, h);
    g.setColor(0,0,0);           // black border
    g.drawRect(1, 1, w-2, h-2);

    drawScrollBar(g, w, h);

    int yPos = YOFFSET;


    // calculate the index of the first invisible line
    int invisLine = firstVisLine + maxVisLines;
    int firstInvisLine = (numLines < invisLine) ? numLines : invisLine;

    // write the visible lines onto the canvas


	g.setFont(f);
	int cpindex=0;
	String pstring;
	cw=f.charWidth('W');
	pindex=2*(w-30)/cw;


	for (int i = firstVisLine; i < firstInvisLine; i++)
	{
		for(int j=0;j<8;++j)
		{
		if(lines[i].endsWith("#"+Integer.toString(j)))
		{	if(lines[i].startsWith("You: "))
			g.setColor(0,0,255);
			else g.setColor(255,0,0);
			g.drawString(lines[i].substring(0,lines[i].length()-3),XOFFSET,yPos,0);
			g.drawImage(eimage[j],XOFFSET+f.stringWidth(lines[i].substring(0,lines[i].length())),yPos,Graphics.TOP|Graphics.LEFT);
			imgHeight=eimage[j].getHeight();
			yPos+=imgHeight;
			flag=false;
			break;
		}
		else flag=true;
		}

		if(flag==true)
		{	if(lines[i].startsWith("You: "))
			g.setColor(0,0,255);
			else
			g.setColor(255,0,0);
			if(f.stringWidth(lines[i])-2<w)
			{
			g.drawString(lines[i], XOFFSET, yPos, Graphics.TOP|Graphics.LEFT);
     yPos += fontHeight;
			}
			else
			{
				pstring=lines[i].substring(0,pindex-1);
				g.drawString(pstring,XOFFSET,yPos, Graphics.TOP|Graphics.LEFT);
				pstring=lines[i].substring(pindex);
				yPos+=fontHeight;
				cpindex=pindex;
				while(pstring.length()>=pindex)
				{
				pstring=lines[i].substring(cpindex,cpindex+pindex-1);
				g.drawString(pstring, XOFFSET,yPos,Graphics.TOP|Graphics.LEFT);
				cpindex+=pindex;
				pstring=lines[i].substring(cpindex);
				yPos+=fontHeight;
				}
				g.drawString(pstring, XOFFSET,yPos,Graphics.TOP|Graphics.LEFT);
				yPos+=fontHeight;
			}
		}
                
}


  } // end of paint()



  private void drawScrollBar(Graphics g, int w, int h)
  /* The scrollbar is a two-headed thick arrow parallel to
     the right hand side of the canvas. The upwards
     and downwards triangles change to circles when it's
     not possible to move the text up or down.

     There's a lot of magic numbers here for the coordinates and sizes,
     but at least the coordinates are specified relative to the width
     and height of the canvas (w and h).
  */
  {
    // grey vertical bar
    g.setColor(0,0,255);  // grey
    g.fillRect(w-14, 13, 6, h-26);
    g.setColor(0,0,0);  // black

    // upwards head of the bar
    if (!upArrowShowing) {
      g.setColor(128,128,128);  // grey
      g.fillArc(w-17, 7, 12, 12, 0, 360);   // filled circle
      g.setColor(0,0,0);  // black
    }
    else
      g.fillTriangle(w-11,6, w-6,18, w-16,18);

    // downwards head of the bar
    if (!downArrowShowing) {
      g.setColor(128,128,128);  // grey
      g.fillArc(w-17, h-19, 12, 12, 0, 360);   // filled circle
      g.setColor(0,0,0);  // black
    }
    else
      g.fillTriangle(w-11,h-6, w-6,h-18, w-16,h-18);
  } // end of drawScrollBar()


  // ----------- dimension methods ---------------------

  protected int getMinContentHeight()
  {  return height; }

  protected int getMinContentWidth()
  {  return width;  }

  protected int getPrefContentHeight(int w)
  {  return height;  }

  protected int getPrefContentWidth(int h)
  {  return width;  }


}  // end of ScrollableMessagesBox class


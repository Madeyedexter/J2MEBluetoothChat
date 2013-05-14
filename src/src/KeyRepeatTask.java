package src;

/* This timer task calls ScrollableMessagesBox's
   repeatTextMove(), which repeats the current text
   movement while the key speifying the movement is
   held down.
*/
import java.util.TimerTask;
import src.ScrollableMessagesBox;


public class KeyRepeatTask extends TimerTask
{
  private ScrollableMessagesBox scroller;


  public KeyRepeatTask(ScrollableMessagesBox smb)
  {  scroller = smb;  }

  public void run()
  { scroller.repeatTextMove(); }

} // end of KeyRepeatTask class


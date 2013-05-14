package src;


import java.io.IOException;

import javax.microedition.lcdui.*;


/**
* A simple splash screen.
*
*/
public class SplashScreen extends Canvas implements Runnable
{
private Font f=null;
private Image mImage;
private Display mDisplay;
private Displayable mNextDisplayable;

/**
* Create a new SplashScreen. The constructor
* attempts to load the named image and begins a timeout
* thread. The splash screen can be dismissed with a key
* press, a pointer press, or a timeout (hardcoded at
* three seconds).
*
* @param name the path of the image resource to load
* @param display the application's Display
* @param next the screen to be shown after the splash
*/
public SplashScreen(String name, Display display, Displayable next) throws IOException
{
 mImage = Image.createImage(name);
 mDisplay = display;
 mNextDisplayable = next;
 Thread t = new Thread(this);
 t.start();
  setFullScreenMode(true);
}

/**
* Paints the image centered on the screen.
*/
public void paint(Graphics g)
{
g.setColor(255,255,255);
f=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL);
g.setFont(f);


 int width = getWidth();
 int height = getHeight();
 g.fillRect(0,0,width,height);
 g.setColor(255,0,0);
 g.drawString("Application by:",width/2,height-3*f.getHeight(),Graphics.BOTTOM|Graphics.HCENTER);
 g.drawString("Mohsin Shiraz $ Danish Kamaal",width/2,height-2*f.getHeight(),Graphics.BOTTOM|Graphics.HCENTER);
 g.drawString("ZHCET(AMU), B.Tech.(II Year), CSE",width/2,height-f.getHeight(),Graphics.BOTTOM|Graphics.HCENTER);




 g.drawImage(mImage, width/2,2*f.getHeight(),Graphics.HCENTER | Graphics.TOP);
 g.setColor(0,0,0);
 g.drawString("v1.0",width,0,Graphics.TOP|Graphics.RIGHT);
}

/**
* Dismisses the splash screen.
*/
public void dismiss()
{
 if (isShown())
   mDisplay.setCurrent(mNextDisplayable);
}

/**
* This method is used internally with a timeout thread.
*/
public void run()
{
 try { Thread.sleep(10000); }
 catch (InterruptedException ie) {}
 dismiss();
}

/**
* A key release event causes the dismiss()
* method to be called.
*/
public void keyReleased(int keyCode)
 {

 	dismiss();
 }
/**
* A pointer release event causes the dismiss()
* method to be called.
*/
public void pointerReleased(int x, int y)
 {
	dismiss();
 }
}
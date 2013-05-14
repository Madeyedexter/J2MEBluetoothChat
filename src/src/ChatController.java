package src;


import java.util.*;


import java.io.IOException;

import javax.microedition.io.Connector;

import javax.microedition.lcdui.Item;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Ticker;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.AlertType;





import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.UUID;

import javax.bluetooth.ServiceRecord;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.L2CAPConnection;

import javax.bluetooth.BluetoothStateException;
import javax.microedition.lcdui.ItemStateListener;

public class ChatController extends MIDlet implements CommandListener, ItemStateListener
{

 private String time;
 private Display display = null;
 private Form mainForm = null;
 private ChoiceGroup devices = null;
 private ChoiceGroup atypes=null;
 private ChoiceGroup emoticon=null;
 private TextField outTxt = null;
 private TextField uname=null;
 private Command exit = null;
 private Command start = null;
 private Command connect = null;
 private Command send = null;
 private Command select = null;
 private Command emoticons = null;
 private Command back=null;
 private Command ok=null;
 private StringItem status = null;
 private StringItem status2=null;
 private Ticker ticker=null;
 private Ticker ticker1=null;
 private LocalDevice local = null;
 private RemoteDevice rDevices[];
 private ServiceRecord service = null;
 private DiscoveryAgent agent = null;
 private L2CAPConnectionNotifier notifier;
 private L2CAPConnection connection = null;
 private ScrollableMessagesBox scroller=null;
 private String username=null;
 private boolean flag=false;
 private List elist=null;
 private int eindex=-1;
 private int aindex=-1;
 private Calendar c;
 private Date d;
 private long startTime=0;
 


 private static final String UUID_STRING = "112233445566778899AABBCCDDEEFF";
 private boolean running = false;


 public ChatController()
	{
     super();
     
          try
     		{
     			String eindex[]={"Happy","Sad","Afraid","Shy","Devil","Angel","Sick","Wink"};
     			Image ei[]=	{Image.createImage("/res/ei00.png"),Image.createImage("/res/ei01.png"),Image.createImage("/res/ei02.png"),
     						Image.createImage("/res/ei03.png"),Image.createImage("/res/ei04.png"),Image.createImage("/res/ei05.png"),
     						Image.createImage("/res/ei06.png"),Image.createImage("/res/ei07.png")};
     			elist=new List("Emoticons",List.IMPLICIT,eindex,ei);

     			String atype[]={"Play Sound", "Vibration", "Sound and Vibration","None"};
     			atypes=new ChoiceGroup("Select New Message Alert Type: ",ChoiceGroup.POPUP,atype,null);
     		}
     	catch(IOException e)
     	{
     		System.err.println("Unable to locate icon!");

     	}
     	catch(NullPointerException npe)
     	{
     	}

         startTime=System.currentTimeMillis();
         display = Display.getDisplay(this);
	 mainForm = new Form("CHAT");
	 devices = new ChoiceGroup(null,Choice.EXCLUSIVE);
	 emoticon= new ChoiceGroup(null,Choice.EXCLUSIVE);
	 outTxt = new TextField("Outgoing Message: ","",256,TextField.ANY);
	 uname= new TextField("User Name: ","User",30,TextField.ANY);
	 ticker=new Ticker(" ");
	 ticker1=new Ticker(" ");
	 start = new Command("START",Command.SCREEN,2);
	 connect = new Command("CONNECT",Command.SCREEN,2);
	 send = new Command("SEND",Command.SCREEN,1);
	 select = new Command("SELECT",Command.SCREEN,2);
	 emoticons = new Command("Send Emoticon", Command.SCREEN,2);
	 exit = new Command("EXIT",Command.EXIT,2);
	 ok=new Command("Send",Command.OK,1);
	 back=new Command("Back",Command.BACK,1);
	 status = new StringItem("Status : ",null);
	 mainForm.append(status);
	 mainForm.addCommand(exit);
	 elist.addCommand(back);
	 elist.addCommand(ok);
	 mainForm.setCommandListener(this);
	 elist.setCommandListener(this);
         status2=new StringItem("Info: ",null);
	 int width=mainForm.getWidth();
         int height=mainForm.getHeight();
         int outTxtheight=outTxt.getPreferredHeight();
         scroller=new ScrollableMessagesBox("Messages: ",width,height-outTxtheight-50);


	}



 protected void startApp() throws MIDletStateChangeException
{
    running=true;
	try
            {
                display.setCurrent(new SplashScreen("/res/bt.png",display,mainForm));
            }
	catch(IOException ioe01)
	{
            System.out.println("IOException: "+ioe01.getMessage());
        }
     mainForm.addCommand(start);
     mainForm.addCommand(connect);
     mainForm.append(uname);
     mainForm.append(atypes);
     mainForm.setItemStateListener(this);
     
     try
       {
        local = LocalDevice.getLocalDevice();
	agent = local.getDiscoveryAgent();
       }
     catch(BluetoothStateException bse)
       {
        status.setText("BluetoothStateException unable to start:"+bse.getMessage());
	try
            {
		   Thread.sleep(1000);
	    }
	catch(InterruptedException ie)
            {}
        notifyDestroyed();
       }

    }

 protected void pauseApp()
	{
         running = false;
	 releaseResources();
	}

 protected void destroyApp(boolean uncond) throws MIDletStateChangeException
    {
        running = false;
	releaseResources();
    }

 public void commandAction(final Command cmd,Displayable disp)
    {
     if(cmd==exit)
	{
                 running = false;
		 releaseResources();
		 notifyDestroyed();
	}
     else if(cmd==start)
	{
		   		username=uname.getString();
		   		aindex=atypes.getSelectedIndex();
				mainForm.delete(1);
				mainForm.delete(1);
		   	   new Thread()
			   {
			    public void run()
				   {
				    startServer();
				   }
			   }.start();
 	}
      else if(cmd==connect)
	{

		   	      username=uname.getString();
		   	      aindex=atypes.getSelectedIndex();
		   	      mainForm.delete(1);
		   	      mainForm.delete(1);
		   	      status.setText("Searching for Devices...");
				  mainForm.removeCommand(connect);
				  mainForm.removeCommand(start);
				  mainForm.append(devices);

				  DeviceDiscoverer discoverer = new DeviceDiscoverer(ChatController.this);
				  try
				     {
				  	  agent.startInquiry(DiscoveryAgent.GIAC,discoverer);
				     }

				  catch(IllegalArgumentException iae)
				     {
                      status.setText("BluetoothStateException :"+iae.getMessage());
				     }
				  catch(NullPointerException npe)
				     {
                      status.setText("BluetoothStateException :"+npe.getMessage());
				     }
				  catch(BluetoothStateException bse1)
				     {
                      status.setText("BluetoothStateException :"+bse1.getMessage());
				     }
      }
      else if(cmd==select)
      {
				       status.setText("Searching devices for service...");
					   int index = devices.getSelectedIndex();
					   mainForm.delete(mainForm.size()-1);//deletes choiceGroup
					   mainForm.removeCommand(select);

					   ServiceDiscoverer serviceDListener = new ServiceDiscoverer(ChatController.this);
					   int attrSet[] = {0x0100}; //returns service name attribute
                       UUID[] uuidSet = {new UUID(UUID_STRING,false)};

					   try
					      {
					   	   agent.searchServices(attrSet,uuidSet,rDevices[index],serviceDListener);
					      }
					    catch(IllegalArgumentException iae1)
				          {
                           status.setText("BluetoothStateException :"+iae1.getMessage());
				          }
				        catch(NullPointerException npe1)
				          {
                           status.setText("BluetoothStateException :"+npe1.getMessage());
				          }
				        catch(BluetoothStateException bse11)
				          {
                           status.setText("BluetoothStateException :"+bse11.getMessage());
				          }
	}
        else if(cmd==send||cmd==ok)
	         {
                      new Thread()
			 {
                	      public void run()
				 {
                                	  if(cmd==send)
					  sendMessage();
					  else if(cmd==ok)
                                        	  {
						  	eindex=elist.getSelectedIndex();
						  	sendMessage();
						  	switchDisplay(null,mainForm);
						  }
				}
			 }.start();
                 }
        else if(cmd==emoticons)
                {
			switchDisplay(null, elist);
			ticker1.setString("Choose an Emoticon....");
			elist.setTicker(ticker1);
		}
        else if(cmd==back)
		{
        		switchDisplay(null, mainForm);
		}


	 }

	  private void switchDisplay(Alert alert, Displayable newDisplayable)
	{

        Display display = getDisplay();

        if(alert == null)
        {

            display.setCurrent(newDisplayable);

        }

        else
        {

            display.setCurrent(alert,newDisplayable);

        }
	}



    private Display getDisplay()
    {

        return display.getDisplay(this);

    }








//this method is called from DeviceDiscoverer when device inquiry finishes
 public void deviceInquiryFinished(RemoteDevice[] rDevices,String message)
	{
     this.rDevices = rDevices;
	 String deviceNames[] = new String[rDevices.length];
	 for(int k=0;k<rDevices.length;k++)
		{
	     try
	       {
	     	deviceNames[k] = rDevices[k].getFriendlyName(false);
	       }
	     catch(IOException ioe)
	       {
	        status.setText("IOException :"+ioe.getMessage());
		   }
	    }
     for(int l=0;l<deviceNames.length;l++)
		{
	     devices.append(deviceNames[l],null);
	    }
     mainForm.addCommand(select);
	 status.setText(message);
    }

//called by ServiceDiscoverer when service search gets completed
 public void serviceSearchFinished(ServiceRecord service,String message)
	{
	 String url = "";
     this.service = service;
	 status.setText(message);
	 try
	   {
	    url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
	   }
	 catch (IllegalArgumentException iae1)
	   {}
     try
       {
     	connection = (L2CAPConnection)Connector.open(url);
		status.setText("Connected...");
		new Thread()
		   {
		    public void run()
			   {
                           
			    startReciever();
                            
                           }
		   }.start();
       }
     catch(IOException ioe1)
       {
        status.setText("IOException :"+ioe1.getMessage());
       }
	}

 // this method starts L2CAPConnection chat server from server mode
 public void startServer()
	{
     status.setText("Server Starting...");
	 mainForm.removeCommand(connect);
	 mainForm.removeCommand(start);
	 try
	   {
	 	local.setDiscoverable(DiscoveryAgent.GIAC);

		notifier = (L2CAPConnectionNotifier)Connector.open("btl2cap://localhost:"+UUID_STRING+";name=L2CAPChat");

		ServiceRecord record = local.getRecord(notifier);
		String conURL = record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
		status.setText("Server Running...");

		connection = notifier.acceptAndOpen();
		new Thread()
		   {
		    public void run()
			   {
			    startReciever();
			   }
		   }.start();
	   }
	 catch(IOException ioe3)
	   {
        status.setText("IOException :"+ioe3.getMessage());
	   }
    }

 //starts a message reciever listening for incomming message
 public void startReciever()
	{
	mainForm.append("User Name: ");
	mainForm.append(username);
    mainForm.addCommand(send);
    mainForm.addCommand(emoticons);
    mainForm.append(scroller);
    mainForm.append(status2);
	mainForm.append(outTxt);

	 while(running)

		{
	     try
	        {
	     	 if(connection.ready())
				{

			     int receiveMTU = connection.getReceiveMTU();
				 byte[] data = new byte[receiveMTU];
				 int length = connection.receive(data);


				 String message = new String(data,0,length);
				 for(int i=0;i<8;++i)
				 {
				 	if(message.endsWith("#"+Integer.toString(i)))
				 	{
				 		scroller.addMessage(message);
				 		switch(aindex)
				 	{
				 		case 0: AlertType.INFO.playSound(display);
				 				break;
				 		case 1: Display.getDisplay(this).vibrate(800);
				 				break;
				 		case 2: AlertType.INFO.playSound(display);
				 				Display.getDisplay(this).vibrate(800);
				 				break;
				 		case 3: break;
				 		default: break;
				 	}
				 		flag=false;
				 		break;
				 	}
				 	else flag=true;
				 }
                                 if(message.endsWith("is typing..."))
                                {
                                     
                                 
                                     long time=System.currentTimeMillis();
                                     while(System.currentTimeMillis()-time<2000)
                                     status2.setText(message);
                                     status2.setText(" ");
                                     
                                     
                                }
                                 
                                else if(flag)
                                {
				 
				 d=new Date();
				 c=Calendar.getInstance();
				 c.setTime(d);
				 ticker.setString(message+" (Recieved at: "+c.get(Calendar.HOUR_OF_DAY) + ":" +c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND)+")");
				 mainForm.setTicker(ticker);
				 if((message!=null||message!=""))
				 {	
                                      
                                      scroller.addMessage(message);
				 	switch(aindex)
				 	{
				 		case 0: AlertType.INFO.playSound(display);
				 				break;
				 		case 1: Display.getDisplay(this).vibrate(800);
				 				break;
				 		case 2: AlertType.INFO.playSound(display);
				 				Display.getDisplay(this).vibrate(800);
				 				break;
				 		case 3: break;
				 		default: break;
				 	}
				 }
				 }
                                
			    }
	        }
	     catch(IOException ioe4)
	        {
	        status.setText("IOException :"+ioe4.getMessage());
                }
	    }
     }

 //sends a message over L2CAP

 public void sendMessage()
	{
		try
		{
		for(int i=0;i<8;++i)
		{
			if(eindex==i)
			{
                            String ecode=username+": #"+Integer.toString(eindex);
                            byte[] data=ecode.getBytes();
                            connection.send(data);
                            scroller.addMessage("You: #"+Integer.toString(i));
                            eindex=-1;
                            break;
			}
		}
		}
		catch(IOException ioe00)
		{
			status.setText("IOException: "+ioe00.getMessage());
		}



	 try
       {
     	String message = outTxt.getString();
     	String messageme="You: "+message;
     	message=username+": "+message;


     	byte[] data = message.getBytes();

		int transmitMTU = connection.getTransmitMTU();
		if((data.length <= transmitMTU)&&(!(message.equals(username+": ")||message.equals(""))))
		 {
	      scroller.addMessage(messageme);
		  connection.send(data);
		  int size=outTxt.size();
		  outTxt.delete(0,size);
		  message="";

		 }
        else
		 {
		  status.setText("Message....");
		 }
       }
     catch (IOException ioe5)
       {
        status.setText("IOException :"+ioe5.getMessage());
       }
    }

 public void sendMessage(String n)
{
    
    try
       {     	
     	String message=username+" is typing...";
     	byte[] data = message.getBytes();

		int transmitMTU = connection.getTransmitMTU();
		if((data.length <= transmitMTU))
		 {	      
		  connection.send(data);
		 }   
       }
     catch (IOException ioe5)
       {
        status.setText("IOException :"+ioe5.getMessage());
       }
    }
 //closes L2CAP connection
 public void releaseResources()
	{
     try
       {
     	if(connection != null)
			connection.close();
		if(notifier != null)
			 notifier.close();
       }
     catch(IOException ioe6)
       {
        status.setText("IOException :"+ioe6.getMessage());
	   }
    }

    public void itemStateChanged(Item item) 
    {
        if(item.equals(outTxt)&&(System.currentTimeMillis()-startTime>2000))
        {
            new Thread()
            {
                public void run()
                {
                    startTime=System.currentTimeMillis();
                    sendMessage("#");
                }
            }.start();
        }
        
    }

}
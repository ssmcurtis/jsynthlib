/* Made by Yves Lefebvre
   email : ivanohe@abacom.com
   www.abacom.com/~ivanohe
 */

package synthdrivers.KorgER1;
import core.*;
import javax.swing.*;
import java.io.*;

public class KorgER1SingleDriver extends Driver
{
    
    public KorgER1SingleDriver ()
    {
        manufacturer="Korg";
        model="Electribe ER1";
        patchType="Single";
        id="ER1";
        sysexID="F0423*5140";
        sysexRequestDump=new SysexHandler("F0 42 @@ 51 10 F7");
	patchSize=1085;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=0;
        checksumStart=0;
        checksumEnd=0;
        checksumOffset=0;
        bankNumbers =new String[]
        {"Bank A","Bank B","Bank C","Bank D"};
        patchNumbers=new String[]
        {"01-","02-","03-","04-","05-","06-","07-",
         "08-","09-","10-","11-","12-","13-","14-","15-",
         "16-","17-","18-","19-","20-","21-","22-","23-",
         "24-","25-","26-","27-","28-","29-","30-","31-",
         "32-","33-","34-","35-","36-","37-","38-","39-",
         "40-","41-","42-","43-","44-","45-","46-","47-",
         "48-","49-","50-","51-","52-","53-","54-","55-",
         "56-","57-","58-","59-","60-","61-","62-","63-",
         "64-"};
         
    }
    
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        int patchValue = patchNum;
        int bankValue  = 0;
        
        if(bankNum == 1 || bankNum == 3)
        {
            patchValue += 64;
        }
        if(bankNum > 1)
        {
            bankValue = 1; // bank A and B is at bank 0 location. C and D is at Bank 1
        }
        
        setBankNum (bankValue);
        setPatchNum (patchValue);
        
        try
        {Thread.sleep (100); } catch (Exception e)
        {}
                
        p.sysex[2]=(byte)(0x30 + channel - 1);
        try
        {
            PatchEdit.MidiOut.writeLongMessage (port,p.sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus (e);}
        
        try
        {Thread.sleep (100); } catch (Exception e)
        {}
        // Send a write request to store the patch in eprom
        
        byte [] sysex = new byte[8];
        sysex[0] = (byte)0xF0;
        sysex[1] = (byte)0x42;
        sysex[2] = (byte)(0x30 + channel - 1);
        sysex[3] = (byte)0x51;
        sysex[4] = (byte)0x11; // write request
        sysex[5] = (byte)(bankValue);
        sysex[6] = (byte)(patchValue);
        sysex[7] = (byte)0xF7;
        try
        {
            PatchEdit.MidiOut.writeLongMessage (port,sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus (e);}
        
    }
    
    public void sendPatch (Patch p)
    {
        p.sysex[2]=(byte)(0x30 + channel - 1); // the only thing to do is to set the byte to 3n (n = channel)
        
        try
        {
            PatchEdit.MidiOut.writeLongMessage (port,p.sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus (e);}
    }
    
    public Patch createNewPatch ()
    {
        byte [] sysex = new byte[1085];
        sysex[0]=(byte)0xF0;
        sysex[1]=(byte)0x42;
        sysex[2]=(byte)(0x30+channel-1);
        sysex[3]=(byte)0x51;
        sysex[4]=(byte)0x40;
        sysex[1084]=(byte)0xF7;
        Patch p = new Patch (sysex);
        p.ChooseDriver ();
        setPatchName (p,"New Patch");
        calculateChecksum (p);
        return p;
    }
    
    public void calculateChecksum (Patch p,int start,int end,int ofs)
    {
        // no checksum
    }
    
    
    public void setPatchNum (int patchNum)
    {
        
        try
        {
            PatchEdit.MidiOut.writeShortMessage (port,(byte)(0xC0+(channel-1)),(byte)patchNum);
        } catch (Exception e)
        {};
    }
    
    
    //public JInternalFrame editPatch(Patch p)
    // {
    //     return new KorgER1SingleEditor(p);
    // }
  public void requestPatchDump(int bankNum, int patchNum) {
    byte[] sysex = sysexRequestDump.toByteArray((byte)channel, patchNum+0x30);
   
    SysexHandler.send(port, sysex);
  }

}


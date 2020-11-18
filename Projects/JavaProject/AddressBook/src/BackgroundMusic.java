import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URL;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;


public class BackgroundMusic {

	BasicPlayer player = new BasicPlayer();
	public void musicLoop(){
        try{
        	player.open(new URL("file:///C:/Users/User/workspace/AddressBook/LongMusic.mp3"));
            player.play();
            player.setGain(0.8);
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	public void decreaseVolume() throws BasicPlayerException{
		player.setGain(0.0);
		return ; 
	}
}

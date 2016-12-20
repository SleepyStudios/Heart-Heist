package net.sleepystudios.bankvault.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.sleepystudios.bankvault.BankVault;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Heart Heist";
		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		
		new LwjglApplication(new BankVault(), config);
	}
}

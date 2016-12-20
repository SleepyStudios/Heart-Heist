package net.sleepystudios.bankvault.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.sleepystudios.bankvault.BankVault;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		config.setWindowedMode(1280, 720);
		config.setTitle("Heart Heist");
		
		new Lwjgl3Application(new BankVault(), config);
	}
}

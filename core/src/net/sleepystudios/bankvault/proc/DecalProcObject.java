package net.sleepystudios.bankvault.proc;

import net.sleepystudios.bankvault.MapHandler;

public class DecalProcObject extends ProcObject {
	public DecalProcObject(String name, MapHandler mh) {
		super(name, mh);
		hasCollision = false;
	}
}

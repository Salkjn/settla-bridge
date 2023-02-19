package de.settla.local.protection;

import java.io.File;

import org.bukkit.Bukkit;

import de.settla.local.LocalPlugin;
import de.settla.utilities.module.Module;

public class ProtectionModule extends Module<LocalPlugin> {

	public ProtectionModule(LocalPlugin moduleManager) {
		super(moduleManager);
	}

	@Override
	public void onPreEnable() {
		 File file = new File("/");
		 long totalSpace = file.getTotalSpace(); //total disk space in bytes.
		 long usableSpace = file.getUsableSpace(); ///unallocated / free disk
		 long freeSpace = file.getFreeSpace(); //unallocated / free disk space
		
		 System.out.println(" === bytes ===");
		 System.out.println("Total size : " + totalSpace + " bytes");
		 System.out.println("Space free : " + usableSpace + " bytes");
		 System.out.println("Space free : " + freeSpace + " bytes");
		
		 System.out.println(" === mega bytes ===");
		 System.out.println("Total size : " + (totalSpace /1024 / 1024) + " mb");
		 System.out.println("Space free : " + (usableSpace /1024 / 1024) + "mb");
		 System.out.println("Space free : " + (freeSpace /1024 / 1024) + " mb");
		 
		 if (usableSpace /1024 / 1024 <= 10000) {
			 System.out.println("Server stop... not enough space on device... ");
			 Bukkit.shutdown();
		 }
		 
	}

}

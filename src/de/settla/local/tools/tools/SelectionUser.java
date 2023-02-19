package de.settla.local.tools.tools;

import org.bukkit.entity.Player;

import de.settla.local.tools.SpecialItemUser;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.Clipboard;

public class SelectionUser extends SpecialItemUser {

	private Vector pos1, pos2;
	private Clipboard clipboard;
	
	public SelectionUser(Player player) {
		super(player);
	}

	public Vector getPos1() {
		return pos1;
	}

	public void setPos1(Vector pos1) {
		this.pos1 = pos1;
	}

	public Vector getPos2() {
		return pos2;
	}

	public void setPos2(Vector pos2) {
		this.pos2 = pos2;
	}
	
	public boolean checkPositions() {
		return pos1 != null && pos2 != null;
	}

	public Clipboard getClipboard() {
		return clipboard;
	}

	public void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}
}

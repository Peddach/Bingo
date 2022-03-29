package com.github.peddach.bingoHost.quest;

import org.bukkit.Material;

public class Quest {
	
	private final QuestType type;
	private final Material block;
	
	public Quest (QuestType type, Material block) {
		
		this.block = block;
		this.type = type;
	}

	public QuestType getType() {
		return type;
	}

	public Material getBlock() {
		return block;
	}
	
}

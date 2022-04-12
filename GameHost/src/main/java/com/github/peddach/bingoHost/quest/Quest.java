package com.github.peddach.bingoHost.quest;

import org.bukkit.Material;
import org.bukkit.advancement.Advancement;

public class Quest {
	
	private final QuestType type;
	private final Material block;
	private final Advancement advancement;
	
	public Quest (QuestType type, Material block) {
		this.block = block;
		this.type = type;
		this.advancement = null;
	}
	
	public Quest(QuestType type, Advancement advancement) {
		this.type = type;
		this.block = null;
		this.advancement = advancement;
		
	}

	public QuestType getType() {
		return type;
	}

	public Material getBlock() {
		return block;
	}

	public Advancement getAdvancement() {
		return advancement;
	}
	
}

package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;

abstract class EntityRandomizerAction implements IUndoableAction {

	protected final EntityType<? extends MobEntity> entityType;
	protected final String typeName;
	protected final boolean peaceful;

	EntityRandomizerAction(EntityType<? extends MobEntity> entityType, String typeName, boolean peaceful) {
		this.entityType = entityType;
		this.typeName = typeName;
		this.peaceful = peaceful;
	}

	protected void apply(boolean add) {
		if (peaceful) {
			if (add) {
				EntityRandomizerHelper.addPeacefulMob(entityType);
			} else {
				EntityRandomizerHelper.removePeacefulMob(entityType);
			}
		} else {
			if (add) {
				EntityRandomizerHelper.addHostileMob(entityType);
			} else {
				EntityRandomizerHelper.removeHostileMob(entityType);
			}
		}
	}

	static class Add extends EntityRandomizerAction {

		Add(EntityType<? extends MobEntity> living, String typeName, boolean peaceful) {
			super(living, typeName, peaceful);
		}

		@Override
		public void apply() {
			apply(true);
		}

		@Override
		public String describe() {
			return "Added " + typeName + " to the " + (peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}

		@Override
		public void undo() {
			apply(false);
		}

		@Override
		public String describeUndo() {
			return "Undid Addition of " + typeName + " to the " + (peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}
	}

	static class Remove extends EntityRandomizerAction {

		Remove(EntityType<? extends MobEntity> living, String typeName, boolean peaceful) {
			super(living, typeName, peaceful);
		}

		@Override
		public void apply() {
			apply(false);
		}

		@Override
		public String describe() {
			return "Removed " + typeName + " from the " + (peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}

		@Override
		public void undo() {
			apply(true);
		}

		@Override
		public String describeUndo() {
			return "Undid removal of " + typeName + " from the " + (peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}
	}

	static class Clear implements IUndoableAction {

		private boolean peaceful;

		Clear(boolean peaceful) {
			this.peaceful = peaceful;
		}

		@Override
		public void apply() {
			if (peaceful) {
				EntityRandomizerHelper.clearPeacefulMobs();
			} else {
				EntityRandomizerHelper.clearHostileMobs();
			}
		}

		@Override
		public String describe() {
			return "Cleared the " + (this.peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}

		@Override
		public void undo() {
			if (peaceful) {
				EntityRandomizerHelper.resetPeacefulMobs();
			} else {
				EntityRandomizerHelper.resetHostileMobs();
			}
		}

		@Override
		public String describeUndo() {
			return "Restored the " + (this.peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer to default.";
		}
	}
}
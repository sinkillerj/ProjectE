package moze_intel.projecte.integration.crafttweaker;

import crafttweaker.IAction;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityLiving;

abstract class EntityRandomizerAction implements IAction {
	final boolean peaceful;

	EntityRandomizerAction(boolean peaceful) {
		this.peaceful = peaceful;
	}

	static class Add extends EntityRandomizerAction {
		private final Class<? extends EntityLiving> living;
		private final String typeName;

		Add(Class<? extends EntityLiving> living, String typeName, boolean peaceful) {
			super(peaceful);
			this.living = living;
			this.typeName = typeName;
		}

		@Override
		public void apply() {
			if (this.peaceful) {
				WorldHelper.addPeaceful(living);
			} else {
				WorldHelper.addMob(living);
			}
		}

		@Override
		public String describe() {
			return "Added " + this.typeName + " to the " + (this.peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}
	}

	static class Remove extends EntityRandomizerAction {
		private final Class<? extends EntityLiving> living;
		private final String typeName;

		Remove(Class<? extends EntityLiving> living, String typeName, boolean peaceful) {
			super(peaceful);
			this.living = living;
			this.typeName = typeName;
		}

		@Override
		public void apply() {
			if (this.peaceful) {
				WorldHelper.removePeaceful(this.living);
			} else {
				WorldHelper.removeMob(this.living);
			}
		}

		@Override
		public String describe() {
			return "Removed " + this.typeName + " from the " + (this.peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}
	}

	static class Clear extends EntityRandomizerAction {
		Clear(boolean peaceful) {
			super(peaceful);
		}

		@Override
		public void apply() {
			if (this.peaceful) {
				WorldHelper.clearPeacefuls();
			} else {
				WorldHelper.clearMobs();
			}
		}

		@Override
		public String describe() {
			return "Cleared the " + (this.peaceful ? "peaceful" : "hostile") + " Philosopher Stone Entity Randomizer.";
		}
	}
}
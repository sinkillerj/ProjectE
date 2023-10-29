package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class CreativeTabDeferredRegister extends WrappedDeferredRegister<CreativeModeTab> {

	private final Consumer<BuildCreativeModeTabContentsEvent> addToExistingTabs;
	private final String modid;

	public CreativeTabDeferredRegister(String modid, Consumer<BuildCreativeModeTabContentsEvent> addToExistingTabs) {
		super(Registries.CREATIVE_MODE_TAB, modid);
		this.modid = modid;
		this.addToExistingTabs = addToExistingTabs;
	}

	@Override
	public void register(IEventBus bus) {
		super.register(bus);
		bus.addListener(addToExistingTabs);
	}

	/**
	 * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
	 */
	public CreativeTabRegistryObject registerMain(ILangEntry title, ItemLike icon, UnaryOperator<CreativeModeTab.Builder> operator) {
		return register(modid, title, icon, operator);
	}

	/**
	 * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
	 */
	public CreativeTabRegistryObject register(String name, ILangEntry title, ItemLike icon, UnaryOperator<CreativeModeTab.Builder> operator) {
		return register(name, () -> {
			CreativeModeTab.Builder builder = CreativeModeTab.builder()
					.title(title.translate())
					.icon(() -> icon.asItem().getDefaultInstance());
			return operator.apply(builder).build();
		}, CreativeTabRegistryObject::new);
	}
}
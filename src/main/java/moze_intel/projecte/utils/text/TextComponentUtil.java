package moze_intel.projecte.utils.text;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * @apiNote From Mekanism
 */
public class TextComponentUtil {

	private TextComponentUtil() {
	}

	public static MutableComponent build(Object... components) {
		MutableComponent result = null;
		Style cachedStyle = Style.EMPTY;
		for (Object component : components) {
			if (component == null) {
				//If the component doesn't exist just skip it
				continue;
			}
			MutableComponent current = null;
			if (component instanceof IHasTextComponent hasTextComponent) {
				current = hasTextComponent.getTextComponent().copy();
			} else if (component instanceof IHasTranslationKey hasTranslationKey) {
				current = translate(hasTranslationKey.getTranslationKey());
			} else if (component instanceof Component c) {
				//Just append if a text component is being passed
				current = c.copy();
			} else if (component instanceof ChatFormatting) {
				cachedStyle = cachedStyle.applyFormat((ChatFormatting) component);
			} else if (component instanceof ClickEvent) {
				cachedStyle = cachedStyle.withClickEvent((ClickEvent) component);
			} else if (component instanceof HoverEvent) {
				cachedStyle = cachedStyle.withHoverEvent((HoverEvent) component);
			} else if (component instanceof Block block) {
				current = translate(block.getDescriptionId());
			} else if (component instanceof Item item) {
				current = translate(item.getDescriptionId());
			} else if (component instanceof ItemStack stack) {
				current = stack.getHoverName().copy();
			} else if (component instanceof FluidStack stack) {
				current = translate(stack.getTranslationKey());
			} else if (component instanceof Fluid fluid) {
				current = translate(fluid.getAttributes().getTranslationKey());
			} else {
				//Fallback to a generic replacement
				current = getString(component.toString());
			}
			if (current == null) {
				//If we don't have a component to add, don't
				continue;
			}
			if (!cachedStyle.isEmpty()) {
				//Apply the style and reset
				current.setStyle(cachedStyle);
				cachedStyle = Style.EMPTY;
			}
			if (result == null) {
				result = current;
			} else {
				result.append(current);
			}
		}
		//Ignores any trailing formatting
		return result;
	}

	public static MutableComponent getString(String component) {
		return Component.literal(cleanString(component));
	}

	private static String cleanString(String component) {
		return component.replace("\u00A0", " ");
	}

	public static MutableComponent translate(String key, Object... args) {
		return Component.translatable(key, args);
	}

	public static MutableComponent smartTranslate(String key, Object... components) {
		if (components.length == 0) {
			//If we don't have any args just short circuit to creating the translation key
			return translate(key);
		}
		List<Object> args = new ArrayList<>();
		Style cachedStyle = Style.EMPTY;
		for (Object component : components) {
			if (component == null) {
				//If the component doesn't exist add it anyways, because we may want to be replacing it
				// with a literal null in the formatted text
				args.add(null);
				cachedStyle = Style.EMPTY;
				continue;
			}
			MutableComponent current = null;
			if (component instanceof IHasTextComponent hasTextComponent) {
				current = hasTextComponent.getTextComponent().copy();
			} else if (component instanceof IHasTranslationKey hasTranslationKey) {
				current = translate(hasTranslationKey.getTranslationKey());
			} else if (component instanceof Block block) {
				current = translate(block.getDescriptionId());
			} else if (component instanceof Item item) {
				current = translate(item.getDescriptionId());
			} else if (component instanceof ItemStack stack) {
				current = stack.getHoverName().copy();
			} else if (component instanceof FluidStack stack) {
				current = translate(stack.getTranslationKey());
			} else if (component instanceof Fluid fluid) {
				current = translate(fluid.getAttributes().getTranslationKey());
			}
			//Formatting
			else if (component instanceof ChatFormatting formatting && !hasStyleType(cachedStyle, formatting)) {
				//Specific formatting not in the cached style yet, apply it
				cachedStyle = cachedStyle.applyFormat(formatting);
				continue;
			} else if (component instanceof ClickEvent && cachedStyle.getClickEvent() == null) {
				//No click event set yet in the cached style, add the event
				cachedStyle = cachedStyle.withClickEvent((ClickEvent) component);
				continue;
			} else if (component instanceof HoverEvent && cachedStyle.getHoverEvent() == null) {
				//No hover event set yet in the cached style, add the event
				cachedStyle = cachedStyle.withHoverEvent((HoverEvent) component);
				continue;
			} else if (!cachedStyle.isEmpty()) {
				//Only bother attempting these checks if we have a cached format, because
				// otherwise we are just going to want to use the raw text
				if (component instanceof Component c) {
					//Just append if a text component is being passed
					current = c.copy();
				} else {
					//Fallback to a direct replacement just so that we can properly color it
					current = getString(component.toString());
				}
			} else if (component instanceof String) {
				//If we didn't format it and it is a string make sure we clean it up
				component = cleanString((String) component);
			}
			if (!cachedStyle.isEmpty()) {
				//If we don't have a text component, then we have to just ignore the formatting and
				// add it directly as an argument. (Note: This should never happen because of the fallback)
				if (current == null) {
					args.add(component);
				} else {
					//Otherwise we apply the formatting and then add it
					args.add(current.setStyle(cachedStyle));
				}
				cachedStyle = Style.EMPTY;
			} else if (current == null) {
				//Add raw
				args.add(component);
			} else {
				//Add the text component variant of it
				args.add(current);
			}
		}
		if (!cachedStyle.isEmpty()) {
			//Add trailing formatting as a color name or just directly
			//Note: We know that we have at least one element in the array, so we don't need to safety check here
			args.add(components[components.length - 1]);
		}
		return translate(key, args.toArray());
	}

	private static boolean hasStyleType(Style current, ChatFormatting formatting) {
		return switch (formatting) {
			case OBFUSCATED -> current.isObfuscated();
			case BOLD -> current.isBold();
			case STRIKETHROUGH -> current.isStrikethrough();
			case UNDERLINE -> current.isUnderlined();
			case ITALIC -> current.isItalic();
			case RESET -> current.isEmpty();
			default -> current.getColor() != null;
		};
	}
}
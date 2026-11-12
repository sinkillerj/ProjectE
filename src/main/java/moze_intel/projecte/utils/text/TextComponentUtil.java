package moze_intel.projecte.utils.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * @apiNote From Mekanism
 */
public class TextComponentUtil {

	private static final Component TEXT_NULL = getString("null");

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
			switch (component) {
				case IHasTextComponent hasTextComponent -> current = hasTextComponent.getTextComponent().copy();
				case IHasTranslationKey hasTranslationKey -> current = translate(hasTranslationKey.getTranslationKey());
				case TextColor color -> cachedStyle = cachedStyle.withColor(color);
				//Just append if a text component is being passed
				case Component c -> current = c.copy();
				case ChatFormatting formatting -> cachedStyle = cachedStyle.applyFormat(formatting);
				case ClickEvent event -> cachedStyle = cachedStyle.withClickEvent(event);
				case HoverEvent event -> cachedStyle = cachedStyle.withHoverEvent(event);
				case Block block -> current = block.getName().copy();
				case Item item -> current = Component.translatable(item.getDescriptionId());
				case ItemStack stack -> current = stack.getHoverName().copy();
				case FluidStack stack -> current = stack.getHoverName().copy();
				case Fluid fluid -> current = fluid.getFluidType().getDescription().copy();
				case EntityType<?> entityType -> current = entityType.getDescription().copy();
				case Level level -> current = level.getDescription().copy();
				//Fallback to a generic replacement
				// this handles strings, numbers, and any type we don't necessarily know about
				default -> current = getString(component.toString());
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

	public static MutableComponent translate(String key) {
		return Component.translatable(key);
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
				//If the component doesn't exist add it anyway, because we may want to be replacing it
				// with a literal null in the formatted text
				args.add(TEXT_NULL);
				cachedStyle = Style.EMPTY;
				continue;
			}
			MutableComponent current = null;
			if (component instanceof Component c) {
				//Just append if a text component is being passed
				current = c.copy();
			} else if (component instanceof IHasTextComponent hasTextComponent) {
				current = hasTextComponent.getTextComponent().copy();
			} else if (component instanceof IHasTranslationKey hasTranslationKey) {
				current = translate(hasTranslationKey.getTranslationKey());
			} else if (component instanceof Block block) {
				current = block.getName().copy();
			} else if (component instanceof Item item) {
				current = Component.translatable(item.getDescriptionId());
			} else if (component instanceof ItemStack stack) {
				current = stack.getHoverName().copy();
			} else if (component instanceof FluidStack stack) {
				current = stack.getHoverName().copy();
			} else if (component instanceof Fluid fluid) {
				current = fluid.getFluidType().getDescription().copy();
			} else if (component instanceof EntityType<?> entityType) {
				current = entityType.getDescription().copy();
			} else if (component instanceof Level level) {
				current = level.getDescription().copy();
			}
			//Formatting
			else if (component instanceof TextColor color && cachedStyle.getColor() == null) {
				//No color set yet in the cached style, apply the color
				cachedStyle = cachedStyle.withColor(color);
				continue;
			} else if (component instanceof ChatFormatting formatting && !hasStyleType(cachedStyle, formatting)) {
				//Specific formatting not in the cached style yet, apply it
				cachedStyle = cachedStyle.applyFormat(formatting);
				continue;
			} else if (component instanceof ClickEvent event && cachedStyle.getClickEvent() == null) {
				//No click event set yet in the cached style, add the event
				cachedStyle = cachedStyle.withClickEvent(event);
				continue;
			} else if (component instanceof HoverEvent event && cachedStyle.getHoverEvent() == null) {
				//No hover event set yet in the cached style, add the event
				cachedStyle = cachedStyle.withHoverEvent(event);
				continue;
			} else if (!cachedStyle.isEmpty()) {
				//Only bother attempting these checks if we have a cached format, because
				// otherwise we are just going to want to use the raw text
				//Fallback to a direct replacement just so that we can properly color it
				// this handles strings, numbers, and any type we don't necessarily know about
				current = getString(component.toString());
			} else if (component instanceof String str) {
				//If we didn't format it, and it is a string make sure we clean it up
				component = cleanString(str);
			} else if (!TranslatableContents.isAllowedPrimitiveArgument(component)) {
				//If it isn't a supported primitive type, turn it into a string, as that is a supported type
				//Note: We don't have to turn it into a component as strings are valid for parameters,
				// though we will turn it into one lower down if we have a style to apply
				component = cleanString(component.toString());
			}
			if (!cachedStyle.isEmpty()) {
				//If we don't have a text component, then we have to just ignore the formatting and
				// add it directly as an argument. (Note: This should never happen because of the fallback)
				if (current == null) {
					current = getString(component.toString());
				}
				//Otherwise, we apply the formatting and then add it
				args.add(current.setStyle(cachedStyle));
				cachedStyle = Style.EMPTY;
			} else {
				args.add(Objects.requireNonNullElse(current, component));
			}
		}
		if (!cachedStyle.isEmpty()) {
			//Add trailing formatting as a color name or just directly
			//Note: We know that we have at least one element in the array, so we don't need to safety check here
			Object lastComponent = components[components.length - 1];
			if (lastComponent == null) {
				//Odds are this will never be true, as there is a style, but check it anyway
				args.add(TEXT_NULL);
			} else if (lastComponent instanceof Component || TranslatableContents.isAllowedPrimitiveArgument(lastComponent)) {
				//Odds are this will never be true, but we check it to see if we can avoid having to convert it to a string
				args.add(lastComponent);
			} else {
				//If it isn't a supported primitive type, turn it into a string, as that is a supported type
				//Note: We don't have to turn it into a component as strings are valid for parameters
				args.add(cleanString(lastComponent.toString()));
			}
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

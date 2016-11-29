package moze_intel.projecte.api.state.enums;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum EnumFuelType implements IStringSerializable
{
    ALCHEMICAL_COAL("alchemical_coal"),
    MOBIUS_FUEL("mobius_fuel"),
    AETERNALIS_FUEL("aeternalis_fuel");

    private final String name;

    EnumFuelType(String name)
    {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

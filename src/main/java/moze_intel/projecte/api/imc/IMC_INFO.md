## Overview
This document defines the IMC methods accepted by ProjectE, and their interfaces.

### blacklist_swrg
This method blacklists an Entity Type from the Swiftwolf Rending Gale's repel effect.
The Object sent must be an instance of net.minecraft.entity.EntityType, or else the message is ignored.

### blacklist_interdiction
This method blacklists an Entity Type from the Interdiction Torch's repel effect.
The Object sent must be an instance of net.minecraft.entity.EntityType, or else the message is ignored.

### blacklist_timewatch
This method blacklists a Tile Entity Type from the Watch of Flowing Time's acceleration.
The Object sent must be an instance of net.minecraft.tileentity.TileEntityType, or else the message is ignored.

### register_world_transmutation
This method registers a World Transmutation with the Philosopher's Stone.
The Object sent must be an instance of moze_intel.projecte.api.imc.WorldTransmutationEntry, or else the message is ignored.

### register_custom_emc
Registers a custom EMC value.
The Object sent must be an instance of moze_intel.projecte.api.imc.CustomEMCRegistration, or else the message is ignored.

### register_custom_conversion
Declare a conversion from something to something else. Use to inform ProjectE about recipes it may not know about.
The Object sent must be an instance of moze_intel.projecte.api.imc.CustomConversionRegistration, or else the message is ignored.
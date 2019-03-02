package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class MercurialEye extends ItemMode implements IExtraFunction {
    public MercurialEye() {
        super("mercurial_eye", (byte) 4, new String[]{"Creation", "Extension", "Extension-Classic", "Transmutation", "Transmutation-Classic", "Pillar"});
        this.setNoRepair();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static final int CREATION_MODE = 0;
    private static final int EXTENSION_MODE = 1;
    private static final int EXTENSION_MODE_CLASSIC = 2;
    private static final int TRANSMUTATION_MODE = 3;
    private static final int TRANSMUTATION_MODE_CLASSIC = 4;
    private static final int PILLAR_MODE = 5;

    private static final int PILLAR_STEP_RANGE = 3;

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound prevCapNBT) {
        return new ICapabilitySerializable<NBTTagCompound>() {
            private final IItemHandler inv = new ItemStackHandler(2);

            @Override
            public NBTTagCompound serializeNBT() {
                NBTTagCompound ret = new NBTTagCompound();
                NBTBase nbtBase = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null);
                if (nbtBase != null) {
                    ret.setTag("Items", nbtBase);
                }
                return ret;
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt.getTagList("Items", NBT.TAG_COMPOUND));
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
                return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
            }

            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
                if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
                }
                return null;
            }
        };
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        return world.isRemote ? EnumActionResult.SUCCESS : formBlocks(stack, player, pos, facing);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getMode(stack) == CREATION_MODE) {
            if (world.isRemote) {
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }
            Vec3d eyeVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            Vec3d lookVec = player.getLookVec();
            //I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
            Vec3d targVec = eyeVec.add(lookVec.x * 2, lookVec.y * 2, lookVec.z * 2);
            return ActionResult.newResult(formBlocks(stack, player, new BlockPos(targVec), null), stack);
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    private EnumActionResult formBlocks(ItemStack eye, EntityPlayer player, BlockPos startingPos, @Nullable EnumFacing facing) {
        IItemHandler inventory = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory == null) {
            return EnumActionResult.FAIL;
        }
        ItemStack klein = inventory.getStackInSlot(0);
        if (klein.isEmpty() || !(klein.getItem() instanceof IItemEmc)) {
            return EnumActionResult.FAIL;
        }

        World world = player.getEntityWorld();
        IBlockState startingState = world.getBlockState(startingPos);
        long startingBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(startingState, 1));
        ItemStack target = inventory.getStackInSlot(1);
        IBlockState newState;
        long newBlockEmc;
        byte mode = getMode(eye);

        if (!target.isEmpty()) {
            newState = ItemHelper.stackToState(target);
            newBlockEmc = EMCHelper.getEmcValue(target);
        } else if (mode == EXTENSION_MODE && startingBlockEmc != 0) {
            //If there is no item key, attempt to determine it for extension mode
            newState = startingState;
            newBlockEmc = startingBlockEmc;
        } else {
            return EnumActionResult.FAIL;
        }

        if (newState == null || newState.getBlock().isAir(newState, null, null)) {
            return EnumActionResult.FAIL;
        }

        int charge = getCharge(eye);
        int hitTargets = 0;
        if (mode == CREATION_MODE) {
            Block block = startingState.getBlock();
            if (facing != null && (!block.isReplaceable(world, startingPos) || player.isSneaking() && !block.isAir(startingState, world, startingPos))) {
                BlockPos offsetPos = startingPos.offset(facing);
                IBlockState offsetState = world.getBlockState(offsetPos);
                Block offsetBlock = offsetState.getBlock();
                if (!offsetBlock.isReplaceable(world, offsetPos)) {
                    return EnumActionResult.FAIL;
                }
                long offsetBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(offsetState, 1));
                //Just in case it is not air but is a replaceable block like tall grass, get the proper EMC instead of just using 0
                if (doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc)) {
                    hitTargets++;
                }
            } else if (doBlockPlace(player, startingState, startingPos, newState, eye, startingBlockEmc, newBlockEmc)) {
                //Otherwise replace it (it may have been air)
                hitTargets++;
            }
        } else if (mode == PILLAR_MODE) {
            BlockPos start = startingPos;
            BlockPos end = startingPos;

            int magnitude = (charge + 1) * PILLAR_STEP_RANGE;

            if (magnitude > 0) {
                magnitude--;
                if (facing != null) {
                    switch (facing) {
                        case UP:
                            start = start.add(-1, -magnitude, -1);
                            end = end.add(1, 0, 1);
                            break;
                        case DOWN:
                            start = start.add(-1, 0, -1);
                            end = end.add(1, magnitude, 1);
                            break;
                        case SOUTH:
                            start = start.add(-1, -1, -magnitude);
                            end = end.add(1, 1, 0);
                            break;
                        case NORTH:
                            start = start.add(-1, -1, 0);
                            end = end.add(1, 1, magnitude);
                            break;
                        case EAST:
                            start = start.add(-magnitude, -1, -1);
                            end = end.add(0, 1, 1);
                            break;
                        case WEST:
                            start = start.add(0, -1, -1);
                            end = end.add(magnitude, 1, 1);
                            break;
                    }
                }
            }

            for (BlockPos pos : WorldHelper.getPositionsFromBox(new AxisAlignedBB(start, end))) {
                AxisAlignedBB bb = startingState.getCollisionBoundingBox(world, pos);
                if (bb == null || world.checkNoEntityCollision(bb)) {
                    IBlockState placeState = world.getBlockState(pos);
                    if (!placeState.getBlock().isReplaceable(world, pos)) {
                        //Don't replace blocks that are already there unless they are replaceable
                        continue;
                    }
                    long placeBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(placeState, 1));
                    if (doBlockPlace(player, placeState, pos, newState, eye, placeBlockEmc, newBlockEmc)) {
                        hitTargets++;
                    }
                }
            }
        } else {
            if (startingState.getBlock().isAir(startingState, world, startingPos) || facing == null) {
                return EnumActionResult.FAIL;
            }
            Set<BlockPos> visited = new HashSet<>();
            visited.add(startingPos);
            LinkedList<BlockPos> possibleBlocks = new LinkedList<>();
            possibleBlocks.add(startingPos);

            int actualCharge = charge + 1;
            int magnitude = actualCharge * actualCharge * actualCharge;
            for (int attemptedTargets = 0; !possibleBlocks.isEmpty() && attemptedTargets < magnitude * 4; attemptedTargets++) {
                BlockPos pos = possibleBlocks.poll();
                IBlockState checkState = world.getBlockState(pos);
                if (startingState != checkState)
                    continue;
                BlockPos offsetPos = pos.offset(facing);
                IBlockState offsetState = world.getBlockState(offsetPos);
                boolean hit = false;
                if (!offsetState.isSideSolid(world, offsetPos, facing)) {
                    if (mode == EXTENSION_MODE) {
                        AxisAlignedBB cbBox = startingState.getCollisionBoundingBox(world, offsetPos);
                        if (cbBox == null || world.checkNoEntityCollision(cbBox)) {
                            long offsetBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(offsetState, 1));
                            hit = doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc);
                        }
                    } else if (mode == TRANSMUTATION_MODE) {
                        hit = doBlockPlace(player, checkState, pos, newState, eye, startingBlockEmc, newBlockEmc);
                    }
                }

                if (hit) {
                    hitTargets++;
                    if (hitTargets >= magnitude) {
                        break;
                    }
                    for (EnumFacing e : EnumFacing.VALUES) {
                        if (facing.getAxis() != e.getAxis()) {
                            BlockPos offset = pos.offset(e);
                            BlockPos offsetOpposite = pos.offset(e.getOpposite());
                            if (visited.add(offset))
                                possibleBlocks.offer(offset);
                            if (visited.add(offsetOpposite))
                                possibleBlocks.offer(offsetOpposite);
                        }
                    }
                }
            }
        }

        if (hitTargets > 0) {
            world.playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / getNumCharges(eye)) * charge));
        }

        return EnumActionResult.SUCCESS;
    }

    private boolean doBlockPlace(EntityPlayer player, IBlockState oldState, BlockPos placePos, IBlockState newState, ItemStack eye, long oldEMC, long newEMC) {
        IItemHandler capability = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (capability == null) {
            return false;
        }
        ItemStack klein = capability.getStackInSlot(0);

        if (klein.isEmpty() || oldState == newState || ItemPE.getEmc(klein) - (newEMC - oldEMC) < 0 || player.getEntityWorld().getTileEntity(placePos) != null) {
            return false;
        }

        if (oldEMC == 0 && (oldState.getBlock() == Blocks.BEDROCK || oldState.getBlock() == Blocks.BARRIER)) {
            //Don't allow replacing bedrock/barriers (unless they have an EMC value)
            return false;
        }

        if (PlayerHelper.checkedReplaceBlock((EntityPlayerMP) player, placePos, newState)) {
            IItemEmc itemEMC = (IItemEmc) klein.getItem();
            if (oldEMC == 0) {
                NonNullList<ItemStack> drops = NonNullList.create();
                oldState.getBlock().getDrops(drops, player.getEntityWorld(), placePos, oldState, 0);
                drops.forEach(d -> Block.spawnAsEntity(player.getEntityWorld(), placePos, d));
                itemEMC.extractEmc(klein, newEMC);
            } else if (oldEMC > newEMC) {
                itemEMC.addEmc(klein, oldEMC - newEMC);
            } else if (oldEMC < newEMC) {
                itemEMC.extractEmc(klein, newEMC - oldEMC);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand) {
        player.openGui(PECore.instance, Constants.MERCURIAL_GUI, player.getEntityWorld(), hand == EnumHand.MAIN_HAND ? 0 : 1, -1, -1);
        return true;
    }
}
package com.buuz135.functionalstorage.item;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.block.tile.DrawerControllerTile;
import com.buuz135.functionalstorage.block.tile.EnderDrawerTile;
import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.inventory.EnderInventoryHandler;
import com.buuz135.functionalstorage.world.EnderSavedData;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LinkingToolItem extends BasicItem {

    public static final String NBT_MODE = "Mode";
    public static final String NBT_CONTROLLER = "Controller";
    public static final String NBT_ACTION = "Action";
    public static final String NBT_FIRST = "First";
    public static final String NBT_ENDER = "Ender";
    public static final String NBT_ENDER_SAFETY = "EnderSafety";

    public static LinkingMode getLinkingMode(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_MODE)) {
            return LinkingMode.valueOf(stack.getOrCreateTag().getString(NBT_MODE));
        }
        return LinkingMode.SINGLE;
    }

    public static ActionMode getActionMode(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_ACTION)) {
            return ActionMode.valueOf(stack.getOrCreateTag().getString(NBT_ACTION));
        }
        return ActionMode.ADD;
    }

    static {
        EventManager.forge(PlayerInteractEvent.LeftClickBlock.class).filter(leftClickBlock -> leftClickBlock.getSide() == LogicalSide.SERVER && leftClickBlock.getItemStack().is(FunctionalStorage.LINKING_TOOL.get())).process(leftClickBlock -> {
            ItemStack stack = leftClickBlock.getItemStack();
            BlockEntity blockEntity = leftClickBlock.getWorld().getBlockEntity(leftClickBlock.getPos());
            if (blockEntity instanceof EnderDrawerTile){
                stack.getOrCreateTag().putString(NBT_ENDER, ((EnderDrawerTile) blockEntity).getFrequency());
                leftClickBlock.getPlayer().displayClientMessage(new TextComponent("Stored frequency in the tool").setStyle(Style.EMPTY.withColor(LinkingMode.SINGLE.color)), true);
                leftClickBlock.setCanceled(true);
            }
        }).subscribe();
    }

    public LinkingToolItem() {
        super(new Properties().tab(FunctionalStorage.TAB).stacksTo(1));
    }

    @Override
    public void onCraftedBy(ItemStack p_41447_, Level p_41448_, Player p_41449_) {
        super.onCraftedBy(p_41447_, p_41448_, p_41449_);
        initNbt(p_41447_);
    }

    private ItemStack initNbt(ItemStack stack) {
        stack.getOrCreateTag().putString(NBT_MODE, LinkingMode.SINGLE.name());
        stack.getOrCreateTag().putString(NBT_ACTION, ActionMode.ADD.name());
        return stack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(initNbt(new ItemStack(this)));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().contains(NBT_ENDER);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnderDrawerTile){
            stack.getOrCreateTag().putString(NBT_ENDER, ((EnderDrawerTile) blockEntity).getFrequency());
            player.displayClientMessage(new TextComponent("Stored frequency in the tool").setStyle(Style.EMPTY.withColor(LinkingMode.SINGLE.color)), true);
            return false;
        }
        return super.canAttackBlock(state,level, pos, player);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        LinkingMode linkingMode = getLinkingMode(stack);
        ActionMode linkingAction = getActionMode(stack);
        if (blockEntity instanceof EnderDrawerTile){
            if (stack.getOrCreateTag().contains(NBT_ENDER)){
                String frequency = stack.getOrCreateTag().getString(NBT_ENDER);
                EnderInventoryHandler inventory = EnderSavedData.getInstance(context.getLevel()).getFrequency(((EnderDrawerTile) blockEntity).getFrequency());
                if (inventory.getStackInSlot(0).isEmpty() || (context.getPlayer().isShiftKeyDown() && stack.getOrCreateTag().contains(NBT_ENDER))){
                    ((EnderDrawerTile) blockEntity).setFrequency(frequency);
                    context.getPlayer().displayClientMessage(new TextComponent("Changed drawer frequency").setStyle(Style.EMPTY.withColor(linkingMode.color)), true);
                    stack.getOrCreateTag().remove(NBT_ENDER_SAFETY);
                } else {
                    context.getPlayer().displayClientMessage(new TextComponent("Cannot change frequency, there are items in the drawer. Sneak + Right Click again to ignore this safety").withStyle(ChatFormatting.RED), true);
                    stack.getOrCreateTag().putBoolean(NBT_ENDER_SAFETY, true);
                }
                return InteractionResult.SUCCESS;
            }
        }
        if (blockEntity instanceof StorageControllerTile) {
            CompoundTag controller = new CompoundTag();
            controller.putInt("X", pos.getX());
            controller.putInt("Y", pos.getY());
            controller.putInt("Z", pos.getZ());
            stack.getOrCreateTag().put(NBT_CONTROLLER, controller);
            context.getPlayer().playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.5f, 1);
            context.getPlayer().displayClientMessage(new TextComponent("Controller configured to the tool").withStyle(ChatFormatting.GREEN), true);
            stack.getOrCreateTag().remove(NBT_ENDER);
            return InteractionResult.SUCCESS;
        } else if (blockEntity instanceof ControllableDrawerTile && stack.getOrCreateTag().contains(NBT_CONTROLLER)) {
            CompoundTag controllerNBT = stack.getOrCreateTag().getCompound(NBT_CONTROLLER);
            BlockEntity controller = level.getBlockEntity(new BlockPos(controllerNBT.getInt("X"), controllerNBT.getInt("Y"), controllerNBT.getInt("Z")));
            if (controller instanceof StorageControllerTile) {
                if (linkingMode == LinkingMode.SINGLE) {
                    ((StorageControllerTile) controller).addConnectedDrawers(linkingAction, pos);
                    if (linkingAction == ActionMode.ADD){
                        context.getPlayer().displayClientMessage(new TextComponent("Linked drawer to the controller").setStyle(Style.EMPTY.withColor(linkingMode.color)), true);
                    }else {
                        context.getPlayer().displayClientMessage(new TextComponent("Removed drawer from the controller").setStyle(Style.EMPTY.withColor(linkingMode.color)), true);
                    }
                } else {
                    if (stack.getOrCreateTag().contains(NBT_FIRST)) {
                        CompoundTag firstpos = stack.getOrCreateTag().getCompound(NBT_FIRST);
                        BlockPos firstPos = new BlockPos(firstpos.getInt("X"), firstpos.getInt("Y"), firstpos.getInt("Z"));
                        AABB aabb = new AABB(Math.min(firstPos.getX(), pos.getX()), Math.min(firstPos.getY(), pos.getY()), Math.min(firstPos.getZ(), pos.getZ()), Math.max(firstPos.getX(), pos.getX()) + 1, Math.max(firstPos.getY(), pos.getY()) + 1, Math.max(firstPos.getZ(), pos.getZ()) + 1);
                        ((StorageControllerTile) controller).addConnectedDrawers(linkingAction, getBlockPosInAABB(aabb).toArray(BlockPos[]::new));
                        stack.getOrCreateTag().remove(NBT_FIRST);
                        if (linkingAction == ActionMode.ADD){
                            context.getPlayer().displayClientMessage(new TextComponent("Linked drawers to the controller").setStyle(Style.EMPTY.withColor(linkingMode.color)), true);
                        }else {
                            context.getPlayer().displayClientMessage(new TextComponent("Removed drawers from the controller").setStyle(Style.EMPTY.withColor(linkingMode.color)), true);
                        }
                    } else {
                        CompoundTag firstPos = new CompoundTag();
                        firstPos.putInt("X", pos.getX());
                        firstPos.putInt("Y", pos.getY());
                        firstPos.putInt("Z", pos.getZ());
                        stack.getOrCreateTag().put(NBT_FIRST, firstPos);
                    }
                }
                context.getPlayer().playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 0.5f, 1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            if (stack.getOrCreateTag().contains(NBT_ENDER)){
                if (player.isShiftKeyDown()){
                    stack.getOrCreateTag().remove(NBT_ENDER);
                    player.displayClientMessage(new TextComponent("Cleared drawer frequency").setStyle(Style.EMPTY.withColor(ActionMode.ADD.getColor())), true);
                }
            } else {
                if (player.isShiftKeyDown()) {
                    LinkingMode linkingMode = getLinkingMode(stack);
                    if (linkingMode == LinkingMode.SINGLE) {
                        stack.getOrCreateTag().putString(NBT_MODE, LinkingMode.MULTIPLE.name());
                        player.displayClientMessage(new TextComponent("Swapped mode to " + LinkingMode.MULTIPLE.name().toLowerCase(Locale.ROOT)).setStyle(Style.EMPTY.withColor(LinkingMode.MULTIPLE.getColor())), true);
                    } else {
                        stack.getOrCreateTag().putString(NBT_MODE, LinkingMode.SINGLE.name());
                        player.displayClientMessage(new TextComponent("Swapped mode to " + LinkingMode.SINGLE.name().toLowerCase(Locale.ROOT)).setStyle(Style.EMPTY.withColor(LinkingMode.SINGLE.getColor())), true);
                    }
                    stack.getOrCreateTag().remove(NBT_FIRST);
                } else {
                    ActionMode linkingMode = getActionMode(stack);
                    if (linkingMode == ActionMode.ADD) {
                        stack.getOrCreateTag().putString(NBT_ACTION, ActionMode.REMOVE.name());
                        player.displayClientMessage(new TextComponent("Swapped action to " + ActionMode.REMOVE.name().toLowerCase(Locale.ROOT)).setStyle(Style.EMPTY.withColor(ActionMode.REMOVE.getColor())), true);
                    } else {
                        stack.getOrCreateTag().putString(NBT_ACTION, ActionMode.ADD.name());
                        player.displayClientMessage(new TextComponent("Swapped action to " + ActionMode.ADD.name().toLowerCase(Locale.ROOT)).setStyle(Style.EMPTY.withColor(ActionMode.ADD.getColor())), true);
                    }
                }
            }
            player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 0.5f, 1);
            return InteractionResultHolder.success(stack);
        }
        return super.use(p_41432_, player, hand);
    }

    @Override
    public void addTooltipDetails(@Nullable BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        LinkingMode linkingMode = getLinkingMode(stack);
        ActionMode linkingAction = getActionMode(stack);
        if (key == null) {
            if (stack.getOrCreateTag().contains(NBT_ENDER)){
                TranslatableComponent text = new TranslatableComponent("linkingtool.ender.frequency");
                //frequencyDisplay.forEach(item -> text.append(item.getName(new ItemStack(item))));
                tooltip.add(text.withStyle(ChatFormatting.GRAY));
                tooltip.add(new TextComponent(""));
                tooltip.add(new TextComponent(""));
                tooltip.add(new TranslatableComponent("linkingtool.ender.clear").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(new TranslatableComponent("linkingtool.linkingmode").withStyle(ChatFormatting.YELLOW)
                        .append(new TranslatableComponent("linkingtool.linkingmode." + linkingMode.name().toLowerCase(Locale.ROOT)).withStyle(Style.EMPTY.withColor(linkingMode.getColor()))));
                tooltip.add(new TranslatableComponent("linkingtool.linkingaction").withStyle(ChatFormatting.YELLOW)
                        .append(new TranslatableComponent("linkingtool.linkingaction." + linkingAction.name().toLowerCase(Locale.ROOT)).withStyle(Style.EMPTY.withColor(linkingAction.getColor()))));
                if (stack.getOrCreateTag().contains(NBT_CONTROLLER)) {
                    tooltip.add(new TranslatableComponent("linkingtool.controller").withStyle(ChatFormatting.YELLOW)
                            .append(new TextComponent(stack.getOrCreateTag().getCompound(NBT_CONTROLLER).getInt("X") + "" + ChatFormatting.WHITE + ", " + ChatFormatting.DARK_AQUA + stack.getOrCreateTag().getCompound(NBT_CONTROLLER).getInt("Y") + ChatFormatting.WHITE + ", " + ChatFormatting.DARK_AQUA + stack.getOrCreateTag().getCompound(NBT_CONTROLLER).getInt("Z")).withStyle(ChatFormatting.DARK_AQUA)));
                } else {
                    tooltip.add(new TranslatableComponent("linkingtool.controller").withStyle(ChatFormatting.YELLOW).append(new TextComponent("???").withStyle(ChatFormatting.DARK_AQUA)));
                }
                tooltip.add(new TextComponent(""));
                tooltip.add(new TranslatableComponent("linkingtool.linkingmode." + linkingMode.name().toLowerCase(Locale.ROOT) + ".desc").withStyle(ChatFormatting.GRAY));
                tooltip.add(new TranslatableComponent("linkingtool.use").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static List<BlockPos> getBlockPosInAABB(AABB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<>();
        for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; ++y) {
            for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; ++x) {
                for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; ++z) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable BasicItem.Key key) {
        return key == null;
    }

    public enum LinkingMode {
        SINGLE(TextColor.fromRgb(Color.cyan.getRGB())),
        MULTIPLE(TextColor.fromRgb(Color.GREEN.getRGB()));

        private final TextColor color;

        LinkingMode(TextColor color) {
            this.color = color;
        }

        public TextColor getColor() {
            return color;
        }
    }

    public enum ActionMode {
        ADD(TextColor.fromRgb(new Color(40, 131, 250).getRGB())),
        REMOVE(TextColor.fromRgb(new Color(250, 145, 40).getRGB()));

        private final TextColor color;

        ActionMode(TextColor color) {
            this.color = color;
        }

        public TextColor getColor() {
            return color;
        }
    }
}

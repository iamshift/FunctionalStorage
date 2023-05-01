package com.buuz135.functionalstorage.block.tile;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.item.ConfigurationToolItem;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class ControllerExtensionTile extends StorageControllerExtensionTile<ControllerExtensionTile> {
    public ControllerExtensionTile(BasicTileBlock<ControllerExtensionTile> base, BlockEntityType<ControllerExtensionTile> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }

    @NotNull
    @Override
    public ControllerExtensionTile getSelf() {
        return this;
    }
}

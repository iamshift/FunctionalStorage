package com.buuz135.functionalstorage.block;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.ControllerExtensionTile;
import com.buuz135.functionalstorage.block.tile.DrawerControllerTile;
import com.buuz135.functionalstorage.item.LinkingToolItem;
import com.buuz135.functionalstorage.util.StorageTags;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ControllerExtensionBlock extends StorageControllerExtensionBlock<ControllerExtensionTile> {

    public ControllerExtensionBlock() {
        super("controller_extension", Properties.copy(Blocks.IRON_BLOCK), ControllerExtensionTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (p_155268_, p_155269_) -> new ControllerExtensionTile(this, (BlockEntityType<ControllerExtensionTile>) FunctionalStorage.CONTROLLER_EXTENSION.getRight().get(), p_155268_, p_155269_);
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(FunctionalStorage.CONTROLLER_EXTENSION.getLeft().get())
                .pattern("IBI").pattern("CDC").pattern("IBI")
                .define('I', Tags.Items.STONE)
                .define('B', Tags.Items.STORAGE_BLOCKS_QUARTZ)
                .define('C', StorageTags.DRAWER)
                .define('D', Items.REPEATER)
                .save(consumer);
    }
}

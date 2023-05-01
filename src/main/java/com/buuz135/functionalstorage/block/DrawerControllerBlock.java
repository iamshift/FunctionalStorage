package com.buuz135.functionalstorage.block;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.block.tile.DrawerControllerTile;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DrawerControllerBlock extends StorageControllerBlock<DrawerControllerTile> {

    public DrawerControllerBlock() {
        super("storage_controller", Properties.copy(Blocks.IRON_BLOCK), DrawerControllerTile.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (p_155268_, p_155269_) -> new DrawerControllerTile(this, (BlockEntityType<DrawerControllerTile>) FunctionalStorage.DRAWER_CONTROLLER.getRight().get(), p_155268_, p_155269_);
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(FunctionalStorage.DRAWER_CONTROLLER.getLeft().get())
                .pattern("IBI").pattern("CDC").pattern("IBI")
                .define('I', Tags.Items.STONE)
                .define('B', Tags.Items.STORAGE_BLOCKS_QUARTZ)
                .define('C', StorageTags.DRAWER)
                .define('D', Items.COMPARATOR)
                .save(consumer);
    }
}

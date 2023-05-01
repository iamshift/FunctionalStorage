package com.buuz135.functionalstorage.block.tile;

import com.buuz135.functionalstorage.client.model.FramedDrawerModelData;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class FramedDrawerControllerTile extends StorageControllerTile<FramedDrawerControllerTile>{

    @Save
    private FramedDrawerModelData framedDrawerModelData;

    public FramedDrawerControllerTile(BasicTileBlock<FramedDrawerControllerTile> base, BlockEntityType<FramedDrawerControllerTile> entityType, BlockPos pos, BlockState state) {
        super(base, entityType, pos, state);
        this.framedDrawerModelData = new FramedDrawerModelData(new HashMap<>());
    }

    public FramedDrawerModelData getFramedDrawerModelData() {
        return framedDrawerModelData;
    }

    public void setFramedDrawerModelData(FramedDrawerModelData framedDrawerModelData) {
        this.framedDrawerModelData = framedDrawerModelData;
        markForUpdate();
        if(level.isClientSide) requestModelDataUpdate();
    }

    @NotNull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(FramedDrawerModelData.FRAMED_PROPERTY, framedDrawerModelData).build();
    }

    @NotNull
    @Override
    public FramedDrawerControllerTile getSelf() {
        return this;
    }
}

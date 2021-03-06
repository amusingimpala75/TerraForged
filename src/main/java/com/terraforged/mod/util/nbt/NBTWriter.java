package com.terraforged.mod.util.nbt;

import com.terraforged.core.serialization.serializer.AbstractWriter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

public class NBTWriter extends AbstractWriter<INBT, NBTWriter> {

    public CompoundNBT compound() {
        return (CompoundNBT) getRoot();
    }

    @Override
    protected NBTWriter self() {
        return this;
    }

    @Override
    protected boolean isObject(INBT value) {
        return value instanceof CompoundNBT;
    }

    @Override
    protected boolean isArray(INBT value) {
        return value instanceof ListNBT;
    }

    @Override
    protected void add(INBT parent, String key, INBT value) {
        ((CompoundNBT) parent).put(key, value);
    }

    @Override
    protected void add(INBT parent, INBT value) {
        ((ListNBT) parent).add(value);
    }

    @Override
    protected INBT createObject() {
        return new CompoundNBT();
    }

    @Override
    protected INBT createArray() {
        return new ListNBT();
    }

    @Override
    protected INBT create(String value) {
        return StringNBT.valueOf(value);
    }

    @Override
    protected INBT create(int value) {
        return IntNBT.valueOf(value);
    }

    @Override
    protected INBT create(float value) {
        return FloatNBT.valueOf(value);
    }
}

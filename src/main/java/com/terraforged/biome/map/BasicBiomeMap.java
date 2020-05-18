/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.biome.map;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.terraforged.biome.provider.BiomeHelper;
import com.terraforged.world.biome.BiomeType;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BasicBiomeMap extends AbstractBiomeMap {

    private final Biome[][] biomeTypes;

    public BasicBiomeMap(BiomeMapBuilder builder) {
        super(builder);
        biomeTypes = builder.biomeList();
    }

    @Override
    public List<Biome> getAllBiomes(BiomeType type) {
        if (type.ordinal() >= biomeTypes.length) {
            return Collections.emptyList();
        }
        return Arrays.asList(biomeTypes[type.ordinal()]);
    }

    @Override
    public Set<Biome> getBiomes(BiomeType type) {
        if (type.ordinal() >= biomeTypes.length) {
            return Collections.emptySet();
        }
        return Sets.newHashSet(biomeTypes[type.ordinal()]);
    }

    @Override
    public Biome getBiome(BiomeType type, float temperature, float moisture, float shape) {
        return get(biomeTypes, type, shape, temperature, defaultLand);
    }

    @Override
    public JsonObject toJson() {
        JsonObject groups = new JsonObject();
        for (BiomeType type : BiomeType.values()) {
            JsonArray group = new JsonArray();
            getBiomes(type).stream().map(BiomeHelper::getId).sorted().forEach(group::add);
            groups.add(type.name(), group);
        }
        JsonObject root = super.toJson();
        root.add("biomes", groups);
        return root;
    }
}
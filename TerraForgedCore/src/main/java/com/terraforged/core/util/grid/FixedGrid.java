package com.terraforged.core.util.grid;

import me.dags.noise.util.NoiseUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class FixedGrid<T> implements Iterable<MappedList<FixedList<T>>> {

    private final MappedList<MappedList<FixedList<T>>> grid;

    private FixedGrid(MappedList<MappedList<FixedList<T>>> grid) {
        this.grid = grid;
    }

    public int size() {
        return grid.size();
    }

    public FixedList<T> get(float x, float y) {
        return grid.get(y).get(x);
    }

    public T get(float x, float y, float z) {
        MappedList<FixedList<T>> row = grid.get(y);
        FixedList<T> cell = row.get(x);
        return cell.get(z);
    }

    @Override
    public Iterator<MappedList<FixedList<T>>> iterator() {
        return grid.iterator();
    }

    public static <T> FixedGrid<T> create(List<List<List<T>>> grid, float minX, float minY, float rangeX, float rangeY) {
        List<MappedList<FixedList<T>>> list = new ArrayList<>();
        for (List<List<T>> src : grid) {
            List<FixedList<T>> row = new ArrayList<>(src.size());
            for (List<T> cell : src) {
                row.add(FixedList.of(cell));
            }
            list.add(MappedList.of(row, minX, rangeX));
        }
        return new FixedGrid<>(MappedList.of(list, minY, rangeY));
    }

    public static <T> FixedGrid<T> generate(int size, List<T> values, Function<T, Float> xFunc, Function<T, Float> yFunc) {
        List<List<List<T>>> src = createList(size, () -> createList(size, ArrayList::new));
        List<List<List<T>>> dest = createList(size, () -> createList(size, ArrayList::new));

        float minX = 1F;
        float maxX = 0F;
        float minY = 1F;
        float maxY = 0F;
        for (T value : values) {
            float x = xFunc.apply(value);
            float y = yFunc.apply(value);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int maxIndex = size - 1;
        float rangeX = maxX - minX;
        float rangeY = maxY - minY;
        for (T value : values) {
            float colVal = (xFunc.apply(value) - minX) / rangeX;
            float rowVal = (yFunc.apply(value) - minY) / rangeY;
            int colIndex = NoiseUtil.round(maxIndex * colVal);
            int rowIndex = NoiseUtil.round(maxIndex * rowVal);
            List<List<T>> row = src.get(rowIndex);
            List<T> group = row.get(colIndex);
            group.add(value);
        }

        for (int y = 0; y < size; y++) {
            List<List<T>> srcRow = src.get(y);
            List<List<T>> destRow = dest.get(y);
            for (int x = 0; x < size; x++) {
                List<T> srcGroup = srcRow.get(x);
                List<T> destGroup = destRow.get(x);
                if (srcGroup.isEmpty()) {
                    float fx = minX + (x / (float) maxIndex) * rangeX;
                    float fy = minY + (x / (float) maxIndex) * rangeY;
                    addClosest(values, destGroup, fx, fy, xFunc, yFunc);
                } else {
                    destGroup.addAll(srcGroup);
                }
            }
        }

        return create(dest, minX, minY, rangeX, rangeY);
    }

    private static <T> void addClosest(List<T> source, List<T> dest, float fx, float fy, Function<T, Float> xFunc, Function<T, Float> yFunc) {
        float dist2 = Float.MAX_VALUE;
        Map<T, Float> distances = new HashMap<>();
        for (T t : source) {
            if (!distances.containsKey(t)) {
                float dx = fx - xFunc.apply(t);
                float dy = fy - yFunc.apply(t);
                float d2 = dx * dx + dy * dy;
                distances.put(t, d2);
                if (d2 < dist2) {
                    dist2 = d2;
                }
            }
        }

        if (dist2 <= 0) {
            dist2 = 1F;
        }

        List<T> sorted = new ArrayList<>(distances.keySet());
        sorted.sort((o1, o2) -> Float.compare(distances.getOrDefault(o1, Float.MAX_VALUE), distances.getOrDefault(o2, Float.MAX_VALUE)));

        for (T t : sorted) {
            float d2 = distances.get(t);
            if (d2 / dist2 < 1.025F) {
                dest.add(t);
            }
        }
    }

    private static <T> List<T> createList(int size, Supplier<T> supplier) {
        List<T> list = new ArrayList<>();
        while (list.size() < size) {
            list.add(supplier.get());
        }
        return list;
    }
}
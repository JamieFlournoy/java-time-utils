package com.pervasivecode.utils.time;

import static java.util.Objects.requireNonNull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Immutable
public class ReorderedEnumRange<T extends Enum<T>> {
  private final ImmutableList<T> allValuesSorted;
  private final ImmutableMap<T, Integer> valueIndices;

  public ReorderedEnumRange(T[] allValues, Class<T> klazz, Comparator<T> comparator) {
    T[] allValuesSortedTemp = Arrays.copyOf(allValues, allValues.length);
    Arrays.sort(allValuesSortedTemp, comparator);
    allValuesSorted = ImmutableList.copyOf(allValuesSortedTemp);

    EnumMap<T, Integer> valueIndicesTemp = new EnumMap<T, Integer>(klazz);
    for (int i = 0; i < allValues.length; i++) {
      valueIndicesTemp.put(allValues[i], i);
    }
    valueIndices = ImmutableMap.copyOf(valueIndicesTemp);
  }

  public int indexOf(T value) {
    return valueIndices.get(requireNonNull(value));
  }

  public List<T> range(T from, T to) {
    return rangeInternal(from, to);
  }

  private ImmutableList<T> rangeInternal(T from, T to) {
    int fromIndex = indexOf(from);
    int toIndex = indexOf(to);
    if (toIndex < fromIndex) {
      return rangeInternal(to, from).reverse();
    }

    ImmutableList.Builder<T> rangeBuilder = ImmutableList.builder();
    for (int i = fromIndex; i <= toIndex; i++) {
      rangeBuilder.add(allValuesSorted.get(i));
    }
    return rangeBuilder.build();
  }
}

/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.core.IC2;
import ic2.core.util.ItemComparableItemStack;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.PriorityExecutor;
import ic2.core.util.StackUtil;
import ic2.core.uu.ILateRecipeResolver;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import ic2.core.uu.UuIndex;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UuGraph {
    private static final List<Node> emptyList = Arrays.asList(new Node[0]);
    private static final double epsilon = 1.0E-9;
    private static final Map<ItemComparableItemStack, Node> nodes = new HashMap<ItemComparableItemStack, Node>();
    private static final Map<Item, Set<Node>> itemNodes = new IdentityHashMap<Item, Set<Node>>();
    private static final List<InitialValue> initialValues = new ArrayList<InitialValue>();
    private static volatile Future<?> calculation = null;

    public static void build(boolean reset) {
        if (calculation != null) {
            throw new IllegalStateException("uu graph building is already in progress.");
        }
        if (reset) {
            nodes.clear();
            itemNodes.clear();
        }
        long startTime = System.nanoTime();
        final ArrayList<RecipeTransformation> transformations = new ArrayList<RecipeTransformation>();
        for (IRecipeResolver resolver2 : UuIndex.instance.resolvers) {
            transformations.addAll(resolver2.getTransformations());
        }
        for (RecipeTransformation transform : transformations) {
            for (ItemStack output : transform.outputs) {
                assert (output.getItemDamage() != 32767);
                UuGraph.getInternal(output);
            }
        }
        for (InitialValue initialValue : initialValues) {
            UuGraph.getInternal(initialValue.stack);
        }
        for (ILateRecipeResolver resolver : UuIndex.instance.lateResolvers) {
            transformations.addAll(resolver.getTransformations(nodes.keySet()));
        }
        IC2.log.debug(LogCategory.Uu, "%d UU recipe transformations fetched after %d ms.", transformations.size(), (System.nanoTime() - startTime) / 1000000);
        calculation = IC2.getInstance().threadPool.submit(new Runnable(){

            @Override
            public void run() {
                UuGraph.processRecipes(transformations);
            }
        });
    }

    public static void set(ItemStack stack, double value) {
        if (stack.getItemDamage() == 32767) {
            throw new IllegalArgumentException("setting values for wilcard meta stacks isn't supported.");
        }
        if (calculation != null) {
            throw new IllegalStateException("setting values isn't allowed while the calculation is running, set them earlier.");
        }
        initialValues.add(new InitialValue(stack, value));
    }

    public static double get(ItemStack stack) {
        UuGraph.finishCalculation();
        ItemComparableItemStack key = new ItemComparableItemStack(stack, false);
        Node ret = nodes.get(key);
        if (ret == null) {
            return Double.POSITIVE_INFINITY;
        }
        return ret.value;
    }

    public static ItemStack find(ItemStack stack) {
        UuGraph.finishCalculation();
        ItemComparableItemStack key = new ItemComparableItemStack(stack, false);
        Node exactNode = nodes.get(key);
        if (exactNode != null) {
            return exactNode.stack;
        }
        ItemStack search = stack.copy();
        search.setItemDamage(32767);
        Collection<Node> nodes = UuGraph.getAll(search);
        if (nodes.isEmpty()) {
            return null;
        }
        if (nodes.size() == 1) {
            return nodes.iterator().next().stack;
        }
        ItemStack ret = null;
        int minDmgDiff = Integer.MAX_VALUE;
        for (Node node : nodes) {
            int dmgDiff = Math.abs(stack.getItemDamage() - node.stack.getItemDamage());
            if (dmgDiff >= minDmgDiff) continue;
            ret = node.stack;
            minDmgDiff = dmgDiff;
        }
        return ret;
    }

    public static Iterator<Map.Entry<ItemStack, Double>> iterator() {
        UuGraph.finishCalculation();
        return new ValueIterator();
    }

    private static void processRecipes(List<RecipeTransformation> transformations) {
        long startTime = System.nanoTime();
        for (RecipeTransformation transform : transformations) {
            transform.merge();
            UuGraph.registerTransform(transform);
        }
        for (InitialValue initialValue : initialValues) {
            UuGraph.getInternal(initialValue.stack).setValue(initialValue.value);
        }
        initialValues.clear();
        for (Node node : nodes.values()) {
            node.provides = null;
        }
        IC2.log.debug(LogCategory.Uu, "UU graph built with %d nodes after %d ms.", nodes.size(), (System.nanoTime() - startTime) / 1000000);
    }

    private static Node getInternal(ItemStack stack) {
        assert (stack.getItemDamage() != 32767);
        ItemComparableItemStack key = new ItemComparableItemStack(stack = StackUtil.copyWithSize(stack, 1), false);
        Node ret = nodes.get(key);
        if (ret == null) {
            ret = new Node(stack);
            nodes.put(key.copy(), ret);
            Item item = stack.getItem();
            Set<Node> itemNodeSet = itemNodes.get((Object)item);
            if (itemNodeSet == null) {
                itemNodeSet = new HashSet<Node>(1);
                itemNodes.put(item, itemNodeSet);
            }
            itemNodeSet.add(ret);
        }
        return ret;
    }

    private static Collection<Node> getAll(ItemStack stack) {
        if (stack.getItemDamage() != 32767) {
            return new ArrayList<Node>(Arrays.asList(UuGraph.getInternal(stack)));
        }
        Collection ret = itemNodes.get((Object)stack.getItem());
        if (ret != null) {
            return ret;
        }
        return emptyList;
    }

    private static void registerTransform(RecipeTransformation transform) {
        NodeTransform nt = new NodeTransform(transform);
        for (List<ItemStack> inputs : transform.inputs) {
            for (ItemStack input : inputs) {
                for (Node node : UuGraph.getAll(input)) {
                    node.provides.add(nt);
                }
            }
        }
        for (ItemStack output : transform.outputs) {
            Node node = UuGraph.getInternal(output);
            nt.out.add(node);
        }
    }

    private static void finishCalculation() {
        if (calculation != null) {
            try {
                calculation.get();
            }
            catch (Exception e) {
                IC2.log.warn(LogCategory.Uu, e, "Calculation failed.");
                nodes.clear();
                itemNodes.clear();
            }
            calculation = null;
        }
    }

    static /* synthetic */ Map access$300() {
        return nodes;
    }

    private static class ValueIterator
    implements Iterator<Map.Entry<ItemStack, Double>> {
        private final Iterator<Node> parentIterator = UuGraph.access$300().values().iterator();

        private ValueIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.parentIterator.hasNext();
        }

        @Override
        public Map.Entry<ItemStack, Double> next() {
            Node node = this.parentIterator.next();
            return new AbstractMap.SimpleImmutableEntry<ItemStack, Double>(node.stack, node.value);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class InitialValue {
        ItemStack stack;
        double value;

        InitialValue(ItemStack stack, double value) {
            this.stack = stack;
            this.value = value;
        }
    }

    private static class NodeTransform {
        RecipeTransformation transform;
        Set<Node> out = new HashSet<Node>();

        NodeTransform(RecipeTransformation transform) {
            this.transform = transform;
        }

        int getOutputSize(ItemStack output) {
            for (ItemStack stack : this.transform.outputs) {
                if (!StackUtil.checkItemEquality(stack, output)) continue;
                return stack.stackSize;
            }
            return 0;
        }
    }

    private static class Node {
        ItemStack stack;
        double value = Double.POSITIVE_INFINITY;
        Set<NodeTransform> provides = new HashSet<NodeTransform>();

        Node(ItemStack stack) {
            assert (stack.getItemDamage() != 32767);
            this.stack = stack;
        }

        void setValue(double value) {
            if (value >= this.value - 1.0E-9) {
                return;
            }
            this.value = value;
            for (NodeTransform nt : this.provides) {
                for (Node node : nt.out) {
                    int outputSize = nt.getOutputSize(node.stack);
                    if (outputSize <= 0) {
                        IC2.log.warn(LogCategory.Uu, "UU update: Invalid output size %d in recipetransform %s, expected %s.", new Object[]{outputSize, nt.transform, node.stack});
                        assert (false);
                        continue;
                    }
                    if (node.value <= value / (double)outputSize) continue;
                    node.updateValue(nt, outputSize);
                }
            }
        }

        private void updateValue(NodeTransform nt, int outputSize) {
            double newValue = nt.transform.transformCost;
            for (List<ItemStack> inputs : nt.transform.inputs) {
                double minValue = Double.POSITIVE_INFINITY;
                for (ItemStack input : inputs) {
                    double minValue2 = Double.POSITIVE_INFINITY;
                    for (Node node : UuGraph.getAll(input)) {
                        if (node.value >= minValue2) continue;
                        minValue2 = node.value;
                    }
                    if ((minValue2 *= (double)input.stackSize) >= minValue) continue;
                    minValue = minValue2;
                }
                newValue += minValue;
            }
            this.setValue(newValue / (double)outputSize);
        }
    }

}


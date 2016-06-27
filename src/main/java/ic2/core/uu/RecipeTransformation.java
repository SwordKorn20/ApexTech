/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.item.ItemStack;

public class RecipeTransformation {
    public final double transformCost;
    public List<List<ItemStack>> inputs;
    public List<ItemStack> outputs;

    public /* varargs */ RecipeTransformation(double transformCost, List<List<ItemStack>> inputs, ItemStack ... outputs) {
        this(transformCost, inputs, Arrays.asList(outputs));
    }

    public RecipeTransformation(double transformCost, List<List<ItemStack>> inputs, List<ItemStack> outputs) {
        this.transformCost = transformCost;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    protected void merge() {
        ArrayList<List<ItemStack>> cleanInputs = new ArrayList<List<ItemStack>>();
        for (List<ItemStack> inputList2 : this.inputs) {
            boolean found = false;
            ListIterator<List<ItemStack>> it = cleanInputs.listIterator();
            while (it.hasNext()) {
                List<ItemStack> cleanInputList = (List<ItemStack>)it.next();
                if ((cleanInputList = this.mergeEqualLists(inputList2, cleanInputList)) == null) continue;
                found = true;
                it.set(cleanInputList);
                break;
            }
            if (found) continue;
            cleanInputs.add(inputList2);
        }
        for (List<ItemStack> inputList : this.inputs) {
            block3 : for (List<ItemStack> cleanInputList : cleanInputs) {
                LinkedList<ItemStack> unmatched = new LinkedList<ItemStack>(inputList);
                boolean found = false;
                for (ItemStack stackOffer : cleanInputList) {
                    found = false;
                    Iterator<ItemStack> it = unmatched.iterator();
                    while (it.hasNext()) {
                        ItemStack stackReq = it.next();
                        if (!StackUtil.checkItemEquality(stackOffer, stackReq)) continue;
                        found = true;
                        it.remove();
                        break;
                    }
                    if (found) continue;
                    continue block3;
                }
            }
        }
        this.inputs = cleanInputs;
        ArrayList<ItemStack> cleanOutputs = new ArrayList<ItemStack>();
        for (ItemStack output : this.outputs) {
            boolean found = false;
            ListIterator<ItemStack> it = cleanOutputs.listIterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();
                if (!StackUtil.checkItemEquality(output, stack)) continue;
                found = true;
                it.set(StackUtil.copyWithSize(stack, stack.stackSize + output.stackSize));
                break;
            }
            if (found) continue;
            cleanOutputs.add(output);
        }
        this.outputs = cleanOutputs;
    }

    public String toString() {
        return "{ " + this.transformCost + " + " + StackUtil.toStringSafe2(this.inputs) + " -> " + StackUtil.toStringSafe(this.outputs) + " }";
    }

    private List<ItemStack> mergeEqualLists(List<ItemStack> listA, List<ItemStack> listB) {
        if (listA.size() != listB.size()) {
            return null;
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(listA.size());
        LinkedList<ItemStack> listBCopy = new LinkedList<ItemStack>(listB);
        for (ItemStack a : listA) {
            boolean found = false;
            Iterator<ItemStack> it = listBCopy.iterator();
            while (it.hasNext()) {
                ItemStack b = it.next();
                if (!StackUtil.checkItemEquality(a, b)) continue;
                found = true;
                ret.add(StackUtil.copyWithSize(a, a.stackSize + b.stackSize));
                it.remove();
                break;
            }
            if (found) continue;
            return null;
        }
        return ret;
    }
}


package nullengine.container;

import nullengine.item.Item;
import nullengine.item.ItemStack;

public class BaseContainer implements Runnable {
	public int slots;
	public int UNLIMITED=-1;

	@Override
	public void run() {

	}
	public void insertItem(ItemStack stack){
		for(int i=0;i<slots;i++)
			if(getItemInSlot(i)==ItemStack.EMPTY)
				setItemInSlot(i,stack);
	}

	public ItemStack getItemInSlot(int slot){
		return ItemStack.EMPTY;
	}

	public void setItemInSlot(int slot,ItemStack stack){
		getItemInSlot(slot).setAmount(stack.getAmount());
	}
}

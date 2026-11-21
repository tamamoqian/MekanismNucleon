package meknuc.items;

import meknuc.MeknucReference;
import meknuc.proxy.IClientRegister;
import net.minecraft.world.item.Item;

public class MeknucItemBase extends Item implements IClientRegister {

    private final String itemName;

    public MeknucItemBase(Properties properties) {
        this(null, properties);
    }

    public MeknucItemBase(String itemName, Properties properties) {
        super(properties);
        this.itemName = itemName;
        IClientRegister.addClientRegister(this);
    }

    @Override
    public String getDescriptionId() {
        if (itemName != null) {
            return "item." + MeknucReference.MOD_ID + "." + itemName;
        }
        return super.getDescriptionId();
    }

    @Override
    public void registerClient() {
    }
}

package meknuc.blocks;

import meknuc.MeknucReference;
import meknuc.proxy.IClientRegister;
import net.minecraft.world.level.block.Block;

public class MeknucBlockBase extends Block implements IClientRegister {

    private final String blockName;

    public MeknucBlockBase(Properties properties) {
        this(null, properties);
    }

    public MeknucBlockBase(String blockName, Properties properties) {
        super(properties);
        this.blockName = blockName;
        IClientRegister.addClientRegister(this);
    }

    @Override
    public String getDescriptionId() {
        if (blockName != null) {
            return "block." + MeknucReference.MOD_ID + "." + blockName;
        }
        return super.getDescriptionId();
    }

    @Override
    public void registerClient() {
    }
}

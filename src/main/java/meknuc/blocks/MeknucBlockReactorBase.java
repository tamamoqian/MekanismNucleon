package meknuc.blocks;

import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;

public abstract class MeknucBlockReactorBase extends MeknucBlockBase implements IHasDescription {

    private final ILangEntry description;

    public MeknucBlockReactorBase(Properties properties, ILangEntry description) {
        super(properties);
        this.description = description;
    }

    @Override
    public ILangEntry getDescription() {
        return description;
    }
}

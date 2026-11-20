package meknuc.items;

import meknuc.Config;

/**
 * 钚-239 粉：放在玩家物品栏内会持续产生辐射（23 mSv/tick）。
 */
public class MeknucItemPlutonium239Dust extends MeknucItemRadioactive {

    public MeknucItemPlutonium239Dust(Properties properties) {
        super(properties, Config.PLUTONIUM_239_DUST_RADIATION::get);
    }
}

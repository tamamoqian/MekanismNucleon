package meknuc.items;

import meknuc.Config;

/**
 * 镅-241 粉：放在玩家物品栏内会持续产生辐射（880 mSv/tick）。
 */
public class MeknucItemAmericium241Dust extends MeknucItemRadioactive {

    public MeknucItemAmericium241Dust(Properties properties) {
        super(properties, Config.AMERICIUM_241_DUST_RADIATION::get);
    }
}

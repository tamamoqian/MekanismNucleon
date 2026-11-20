package meknuc.items;

import meknuc.Config;

/**
 * 钋-210 粉：放在玩家物品栏内会持续产生辐射（1 μSv/tick）。
 */
public class MeknucItemPolonium210Dust extends MeknucItemRadioactive {

    public MeknucItemPolonium210Dust(Properties properties) {
        super(properties, Config.POLONIUM_210_DUST_RADIATION::get);
    }
}

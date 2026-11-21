package meknuc.items;

import meknuc.Config;

/**
 * 铀-235 粉：放在玩家物品栏内会持续产生辐射（1 μSv/tick）。
 */
public class MeknucItemUranium235Dust extends MeknucItemRadioactive {

    public MeknucItemUranium235Dust(Properties properties) {
        super(properties, Config.URANIUM_235_DUST_RADIATION::get);
    }
}

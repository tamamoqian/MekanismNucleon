package meknuc.items;

import meknuc.Config;

/**
 * 铀-233 粉：放在玩家物品栏内会持续产生辐射（10 mSv/tick）。
 */
public class MeknucItemUranium233Dust extends MeknucItemRadioactive {

    public MeknucItemUranium233Dust(Properties properties) {
        super(properties, Config.URANIUM_233_DUST_RADIATION::get);
    }
}

package meknuc.particle;

import mekanism.api.text.ILangEntry;

/**
 * 粒子加速器 GUI / JEI / 状态提示的翻译键。
 */
public enum MeknucParticleAcceleratorLang implements ILangEntry {
    GUI_TITLE("gui.meknuc.particle_accelerator.title"),
    GUI_STATUS("gui.meknuc.particle_accelerator.status"),
    GUI_STATE_IDLE("gui.meknuc.particle_accelerator.state.idle"),
    GUI_STATE_RUNNING("gui.meknuc.particle_accelerator.state.running"),
    GUI_STATE_MISSING_ITEM("gui.meknuc.particle_accelerator.state.missing_item"),
    GUI_STATE_MISSING_CHEMICAL("gui.meknuc.particle_accelerator.state.missing_chemical"),
    GUI_STATE_OUTPUT_FULL("gui.meknuc.particle_accelerator.state.output_full"),
    GUI_STATE_NO_ENERGY("gui.meknuc.particle_accelerator.state.no_energy"),
    GUI_STATE_NO_RECIPE("gui.meknuc.particle_accelerator.state.no_recipe"),
    GUI_ENERGY("gui.meknuc.particle_accelerator.energy"),
    GUI_ENERGY_RATE("gui.meknuc.particle_accelerator.energy_rate"),
    GUI_PROGRESS("gui.meknuc.particle_accelerator.progress"),
    GUI_MODE("gui.meknuc.particle_accelerator.mode"),
    GUI_MODE_ACCELERATION("gui.meknuc.particle_accelerator.mode.acceleration"),
    GUI_MODE_COLLECTION("gui.meknuc.particle_accelerator.mode.collection"),
    GUI_CHEMICAL_ROW("gui.meknuc.particle_accelerator.chemical_row"),
    GUI_TARGET_INPUT("gui.meknuc.particle_accelerator.target_input"),
    GUI_TARGET_OUTPUT("gui.meknuc.particle_accelerator.target_output"),
    JEI_TITLE("jei.meknuc.particle_accelerator"),
    JEI_TIME("jei.meknuc.particle_accelerator.time"),
    JEI_ENERGY("jei.meknuc.particle_accelerator.energy"),
    JEI_CHEMICAL("jei.meknuc.particle_accelerator.chemical"),
    JEI_CHEMICAL_OUTPUT("jei.meknuc.particle_accelerator.chemical_output"),
    JEI_MODE("jei.meknuc.particle_accelerator.mode");

    private final String key;

    MeknucParticleAcceleratorLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}

package rc55.mc.cauldronpp;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import rc55.mc.cauldronpp.datagen.ModelDataGen;
import rc55.mc.cauldronpp.datagen.RecipeDataGen;
import rc55.mc.cauldronpp.datagen.lang.LangEnDataGen;
import rc55.mc.cauldronpp.datagen.lang.LangZhCnDataGen;
import rc55.mc.cauldronpp.datagen.lootTable.BlockLootTableDataGen;
import rc55.mc.cauldronpp.datagen.tag.BlockTagDataGen;
import rc55.mc.cauldronpp.datagen.tag.ItemTagDataGen;

public class CauldronppDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator pack) {
		pack.addProvider(BlockTagDataGen::new);//tag
		pack.addProvider(ItemTagDataGen::new);
		pack.addProvider(BlockLootTableDataGen::new);//方块掉落
		pack.addProvider(LangEnDataGen::new);//语言文件
		pack.addProvider(LangZhCnDataGen::new);
		pack.addProvider(ModelDataGen::new);//模型文件
		pack.addProvider(RecipeDataGen::new);//合成配方
	}
}

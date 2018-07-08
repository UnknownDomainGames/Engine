package unknowndomain.engine.client.gui;

import java.util.Collection;
import java.util.HashMap;

/** 
* @author byxiaobai
* GUI管理器
*/
public class GuiManager {
	public static final GuiManager INSTANCE=new GuiManager();
	
	/**
	 * GUI储存
	 */
	private final HashMap<String,Gui> GUI_DATA_MAP;
	
	private GuiManager() {
		 GUI_DATA_MAP=new HashMap<>();
	}


	/**
	 * 渲染所有GUI
	 */
	public void render() {
		Collection<Gui> allGui=GUI_DATA_MAP.values();
		for(Gui gui:allGui) {
			gui.renderer();
		}
	}
}

package fr.reizam.mwarzone.utils.json;

import java.io.File;

import fr.reizam.mwarzone.MWarzone;
import net.minecraft.util.com.google.gson.Gson;

public interface JsonPersist {
	
	public Gson gson = (MWarzone.getInstance().getGson());
	
	public File getFile();
	
	public void loadData();
	
	public void saveData(boolean sync);
	

}

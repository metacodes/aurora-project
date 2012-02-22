package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class DataSetFieldConfig extends DynamicObject  {
	
	public static final String TAG_NAME = "field";
	
	public static final String PROPERTITY_REQUIRED = "required";
	public static final String PROPERTITY_READONLY = "readonly";
	public static final String PROPERTITY_EDITABLE = "editable";
    public static final String PROPERTITY_PROMPT = "prompt";
    public static final String PROPERTITY_RETURN_FIELD = "returnfield";
    public static final String PROPERTITY_VALUE_FIELD = "valuefield";
    public static final String PROPERTITY_VALIDATOR = "validator";
    public static final String PROPERTITY_DEFAULTVALUE = "defaultvalue";    
    
    public static CompositeMap createContext(CompositeMap map,String tagName) {
		CompositeMap context = new CompositeMap(tagName);
		if(map != null){
			context.copy(map);
		}
		return context;		
	}
	
	public static DataSetFieldConfig getInstance(){
		DataSetFieldConfig model = new DataSetFieldConfig();
        model.initialize(DataSetFieldConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static DataSetFieldConfig getInstance(CompositeMap context){
		DataSetFieldConfig model = new DataSetFieldConfig();
        model.initialize(DataSetFieldConfig.createContext(context,TAG_NAME));
        return model;
    }
    
    public boolean getRequired(){
    	return getBoolean(PROPERTITY_REQUIRED, false);
    }
    public void setRequired(boolean required){
    	putBoolean(PROPERTITY_REQUIRED, required);
    }
    
    public boolean getReadOnly(){
    	return getBoolean(PROPERTITY_READONLY, false);
    }
    public void setReadOnly(boolean readonly){
    	putBoolean(PROPERTITY_READONLY, readonly);
    }
    public boolean getEditable(){
    	return getBoolean(PROPERTITY_EDITABLE, true);
    }
    public void setEditable(boolean editable){
    	putBoolean(PROPERTITY_EDITABLE, editable);
    }
    
    
    public String getPrompt(){
    	return getString(PROPERTITY_PROMPT);
    }
    public void setPrompt(String prompt){
    	putString(PROPERTITY_PROMPT, prompt);
    }
    
    public String getValidator(){
    	return getString(PROPERTITY_VALIDATOR, "");
    }
    public void setValidator(String validator){
    	putString(PROPERTITY_VALIDATOR, validator);
    }
    
    public String getReturnField(){
    	return getString(PROPERTITY_RETURN_FIELD);
    }
    public void setReturnField(String field){
    	putString(PROPERTITY_RETURN_FIELD, field);
    }
    
    public String getValueField(){
    	return getString(PROPERTITY_VALUE_FIELD);
    }
    public void setValueField(String field){
    	putString(PROPERTITY_VALUE_FIELD, field);
    }
    
    public String getDefaultValue(){
    	return getString(PROPERTITY_DEFAULTVALUE);
    }
    public void setDefaultValue(String value){
    	putString(PROPERTITY_DEFAULTVALUE, value);
    }
    
    public CompositeMap getMapping(){
    	CompositeMap context = getObjectContext();
    	return context.getChild(DataSetConfig.PROPERTITY_MAPPING);
    }

}

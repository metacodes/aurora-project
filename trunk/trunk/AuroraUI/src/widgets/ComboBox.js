Aurora.ComboBox = Ext.extend(Aurora.TriggerField, {	
	maxHeight:300,
	miniHeight:30,	
	selectedClass:'item-comboBox-li-selected',	
	constructor : function(elId, config) {		
		Aurora.ComboBox.superclass.constructor.call(this, elId, config);		
		this.valueField=config.valueField;
		this.displayField=config.displayField;
		this.data=config.data||[];				
	},
	initComponent : function() {					
		Aurora.ComboBox.superclass.initComponent.call(this);		
	},
	getText : function() {		
		return this.text;
	},
	setText:function(v){
		var v=(v === null || v === undefined ? '' : v);
		if(this.emptyText && this.el && v !== undefined && v !== null && v !== ''){
            this.el.removeClass(this.emptyTextCss);
        }
        this.text = v;
        this.el.dom.value = v;        
        this.applyEmptyText();
	},
	getValue : function() {
		var v=this.value;
		v=(v === null || v === undefined ? '' : v);
		return v;
	},
	setValue:function(v){		
		v=(v === null || v === undefined ? '' : v);
        this.wrap.first('input[type=hidden]').dom.value = v;
        this.value=v;
	},
	onTriggerClick : function() {		
		this.doQuery('',true);				
	},
	onRender:function(){				
        if(!this.view){
			this.initView();			
			this.view.on('click', this.onViewClick,this);
			this.view.on('mouseover',this.onViewOver,this);
			this.view.on('mousemove',this.onViewMove,this);
			for(var i=0,widthArray=[],l=this.data.length;i<l;i++){
				var li=this.view.dom.childNodes[i];
				var width=Aurora.TextMetrics.measure(li,li.innerHTML).width;
				widthArray.push(width);
			}			
			var maxWdith=widthArray.sort(function(a,b){return a-b});
			maxWdith=widthArray[l-1]+30;		
			this.popup.setWidth(maxWdith);
			//设置高度限制
			if(l==0){
				this.view.setHeight(this.miniHeight);
			}else{
				if(this.popup.getHeight()>this.maxHeight){				
					this.popup.setHeight(this.maxHeight);
				}
			}	
		}       
	},
	onViewClick:function(e,target){
		//debugger;
		if(target.tagName!='LI'){
		    return;
		}		
		this.onSelect(target);
		this.popup.hide();		
	},	
	onViewOver:function(e,t){
		this.inKeyMode = false;
	},
	onViewMove:function(e,t){	
		if(this.inKeyMode){ // prevent key nav and mouse over conflicts
            return;
        }		
        var index = t.tabIndex;        
        this.select(index);        
	},
	onSelect:function(target){
		this.text=target.innerHTML;			
		this.setText(this.text);
		this.value=target.value;	
		this.setValue(this.value);		
		this.el.dom.select();
	},
	initQuery:function(){//事件定义中调用
		this.doQuery(this.getText());
	},
	doQuery : function(q,forceAll) {
		if(q === undefined || q === null){
			q = '';
	    }
//		if(forceAll){
//            this.store.clearFilter();
//        }else{
//            this.store.filter(this.displayField, q);
//        }
        
		//值过滤先不添加
		this.onRender();
		this.expand();
	},	
	expand : function(){
		Aurora.ComboBox.superclass.onTriggerClick.call(this);		
	},
	initView: function(){		
		this.view=new Ext.Element(document.createElement('ul'));		
		this.litp=new Ext.Template('<li tabIndex="{index}" value="{'+this.valueField+'}">{'+this.displayField+'}</li>');
		var l=this.data.length;
		for(var i=0;i<l;i++){			
			var data = Ext.apply(this.data[i], {index:i})
			this.litp.append(this.view,data);	//等数据源明确以后再修改		
		}
		this.view.appendTo(this.popup);						
	},
	refresh:function(){//dataModel中触发		
		this.popup.update('');
		this.view=null;		
	},
	select:function(index){
		if(Ext.isEmpty(index)){
			return;
		}	
		var node = this.getNode(index);			
		if(node.tabIndex!=this.selectedIndex){
			if(!Ext.isEmpty(this.selectedIndex)){							
				Ext.fly(this.getNode(this.selectedIndex)).removeClass(this.selectedClass);
			}
			this.selectedIndex=node.tabIndex;
			Ext.fly(node).addClass(this.selectedClass);		
		}			
	},
	getNode:function(index){		
		return this.view.dom.childNodes[index];
	},	
	onDestroy:function(){
		if(this.view){
			Ext.destroy(this.view);			     
		}		
	}
});
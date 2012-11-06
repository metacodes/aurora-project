package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.TableColumnConfig;
import aurora.presentation.component.std.config.TableConfig;

public class Table extends Component {

	private static final String ROW_SPAN = "rowspan";
	private static final String COL_SPAN = "colspan";
	private static final String DEFAULT_CLASS = "item-table";
	private static final String HEADS = "heads";
	private static final String FOOTS = "foots";
	private static final String TITLE = "title";
	
	private static final String COLUMN_TYPE = "type";
	private static final String TYPE_ROW_CHECKBOX = "rowcheck";
	private static final String TYPE_ROW_RADIO = "rowradio";

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}
	protected int getDefaultWidth(){
		return -1;
	}
	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "table/Table-min.css");
		addJavaScript(session, context, "table/Table-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getContextMap();
		CompositeMap view = context.getView();
		TableConfig tc = TableConfig.getInstance(view);
		
		String rowRenderer = tc.getRowRenderer();
		String percentWidth = tc.getPercentWidth();
		if (rowRenderer != null)
			addConfig(TableConfig.PROPERTITY_ROW_RENDERER, rowRenderer);
		if(null!=view.getString(ComponentConfig.PROPERTITY_WIDTH))
			map.put(TableConfig.PROPERTITY_PERCENT_WIDTH, view.getString(ComponentConfig.PROPERTITY_WIDTH)+"px");
		else if(null!=percentWidth)
			map.put(TableConfig.PROPERTITY_PERCENT_WIDTH, percentWidth+"%");
		List cols = new ArrayList();
		createHeads(map, view, session, cols);
		generateColumns(map,cols,hasFooterBar(tc.getColumns()));
		createGridEditors(session, context);
		createNavgationToolBar(session,context);
		String title = session.getLocalizedPrompt(view.getString(TITLE,""));
		if(null!=title&&!"".equals(title)){
			title="<TR><TD class='table_title' colspan='"+cols.size()+"'>"+title+"</TD></TR>";
			map.put(TITLE,title);
		}
		addConfig(TableConfig.PROPERTITY_CAN_WHEEL, new Boolean(tc.isCanWheel()));
		map.put(CONFIG, getConfigString());
	}
	private void processSelectable(Map map,CompositeMap view,CompositeMap cols){
		Boolean selectable = new Boolean(false);
		String selectionmodel = "multiple";
		CompositeMap root = view.getRoot();
		List list = CompositeUtil.findChilds(root, "dataSet");
		if(list!=null){
			String dds = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
			Iterator it = list.iterator();
			while(it.hasNext()){
				CompositeMap ds = (CompositeMap)it.next();
				String id = ds.getString(ComponentConfig.PROPERTITY_ID, "");
				if("".equals(id)) {
					id= IDGenerator.getInstance().generate();
				}
				if(id.equals(dds)){
					selectable = new Boolean(ds.getBoolean(DataSetConfig.PROPERTITY_SELECTABLE, false));
					selectionmodel = ds.getString(DataSetConfig.PROPERTITY_SELECTION_MODEL, "multiple");
					break;
				}
			}
			
		}
		map.put(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		map.put(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
		addConfig(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		addConfig(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
		if(selectable.booleanValue()) {
			CompositeMap column = new CompositeMap("column");
			column.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			column.putInt(ComponentConfig.PROPERTITY_WIDTH, 25);
			if("multiple".equals(selectionmodel)) {
				column.putString(COLUMN_TYPE,TYPE_ROW_CHECKBOX);
			}else{
				column.putString(COLUMN_TYPE,TYPE_ROW_RADIO);
			}
			cols.addChild(0, column);
		}
	}
	private void createGridEditors(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		CompositeMap editors = view.getChild(TableConfig.PROPERTITY_EDITORS);
		StringBuffer sb = new StringBuffer();
		if (editors != null && editors.getChilds() != null) {
			Iterator it = editors.getChildIterator();
			while (it.hasNext()) {
				CompositeMap editor = (CompositeMap) it.next();
				editor.put(ComponentConfig.PROPERTITY_STYLE,
						"position:absolute;left:-1000px;top:-1000px;");
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		map.put(TableConfig.PROPERTITY_EDITORS, sb.toString());
	}

	private void createHeads(Map map, CompositeMap view, BuildSession session,List cols) {
		CompositeMap columns = view.getChild(TableConfig.PROPERTITY_COLUMNS);
		String bindTarget = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
		TableConfig tc = TableConfig.getInstance(view);
		if (null == columns)
			return;
		processSelectable(map,view,columns);
		List children = columns.getChilds();
		if (null == children || children.isEmpty())
			return;
		StringBuffer sb = new StringBuffer();
		Map pro = new HashMap();
		pro.put(ROW_SPAN, new Integer(1));
		processColumns(null, children, cols, pro);
		Integer rows = (Integer) pro.get(ROW_SPAN);
		List ukFirstList = (List) pro.get("l1");
		if (ukFirstList != null) {
			Iterator ufit = ukFirstList.iterator();
			while (ufit.hasNext()) {
				CompositeMap column = (CompositeMap) ufit.next();
				column.put(ROW_SPAN, rows);
				addRowSpan(column);
			}
		}
		
		if(tc.isShowHead()){
			for (int i = 1; i <= rows.intValue(); i++) {
				List list = (List) pro.get("l" + i);
				if (null != list) {
					sb.append("<TR height='25px'>");
					Iterator cit = list.iterator();
					if (null != cit) {
						while (cit.hasNext()) {
							sb.append(createColumn((CompositeMap) cit.next(),
									session, bindTarget));
						}
					}
					sb.append("</TR>");
				}
			}
		}
		map.put(HEADS, sb.toString());
	}

	private void generateColumns(Map map,List cols,boolean hasFoot) {
		StringBuffer sb = new StringBuffer();
		JSONArray jsons = new JSONArray();
		Iterator it = cols.iterator();
		sb.append("<TFOOT><TR class='table-foot'>");
		while (it.hasNext()) {
			CompositeMap column = (CompositeMap) it.next();
			if (null == column.getChilds()) {
				JSONObject json = new JSONObject(column);
				jsons.put(json);
				sb.append("<TD dataindex='");
				sb.append(column.getString("name"));
				sb.append("' align='");
				sb.append(column.getString("align"));
				sb.append("'");
				if(column.getBoolean(TableColumnConfig.PROPERTITY_HIDDEN, false)){
					sb.append(" style='display:none'");
				}
				sb.append(">&#160;</TD>");
			}
		}
		sb.append("</TR></TFOOT>");
		if(hasFoot)map.put(FOOTS, sb.toString());
		addConfig(TableConfig.PROPERTITY_COLUMNS, jsons);
	}

	private void processColumns(CompositeMap parent, List children, List cols,
			Map pro) {
		Iterator it = children.iterator();
		while (it.hasNext()) {
			CompositeMap column = (CompositeMap) it.next();
			int level = (parent == null ? 1 : (parent.getInt("_level")
					.intValue() + 1));
			int rows = ((Integer) pro.get(ROW_SPAN)).intValue();
			if (level > rows)
				pro.put(ROW_SPAN, new Integer(level));

			column.put("_level", new Integer(level));
			column.put("_parent", parent);
			List hlist = (List) pro.get("l" + level);
			if (hlist == null) {
				hlist = new ArrayList();
				pro.put("l" + level, hlist);
			}
			hlist.add(column);
			cols.add(column);
			if (column.getChilds() != null && column.getChilds().size() > 0) {
				processColumns(column, column.getChilds(), cols, pro);
			} else {
				addColSpan(column);
			}
		}
	}

	private void minusRowSpan(CompositeMap column) {
		if (column == null)
			return;
		Integer rowspan = column.getInt(ROW_SPAN);
		if (rowspan != null) {
			int cs = rowspan.intValue() - 1;
			column.put(ROW_SPAN, new Integer(cs));
		}
		CompositeMap parent = (CompositeMap) column.get("_parent");
		if (parent != null) {
			minusRowSpan(parent);
		}

	}

	private void addRowSpan(CompositeMap column) {
		List children = column.getChilds();
		Integer psp = column.getInt(ROW_SPAN);
		if (children != null && children.size() > 0) {
			minusRowSpan(column);
			Iterator it = children.iterator();
			while (it.hasNext()) {
				CompositeMap child = (CompositeMap) it.next();
				child.put(ROW_SPAN, new Integer(psp.intValue() - 1));
				addRowSpan(child);
			}
		}
	}

	private void addColSpan(CompositeMap column) {
		if (column == null)
			return;
		CompositeMap parent = (CompositeMap) column.get("_parent");
		if (parent != null) {
			Integer colspan = parent.getInt(COL_SPAN);
			parent.put(COL_SPAN, new Integer(colspan == null ? 1 : (colspan
					.intValue() + 1)));
		}
		addColSpan(parent);
	}

	private String createColumn(CompositeMap column, BuildSession session,
			String dataset) {
		StringBuffer sb = new StringBuffer();
		TableColumnConfig tcc=TableColumnConfig.getInstance(column);
		String ct =  column.getString(COLUMN_TYPE);
		String pw="";
		if(null!=tcc.getPercentWidth())
			pw = tcc.getPercentWidth()+"%";
		if(null!=column.getString(ComponentConfig.PROPERTITY_WIDTH))
			pw = column.getString(ComponentConfig.PROPERTITY_WIDTH)+"px";
		
		if(TYPE_ROW_CHECKBOX.equals(ct)){
			sb.append("<TD class='table-hc' atype='table.rowcheck' style='width:25px;' rowspan='"+column.getInt(ROW_SPAN)+"'><center><div atype='table.headcheck' class='table-ckb item-ckb-u'></div></center></TD>");
		}else if(TYPE_ROW_RADIO.equals(ct)) {
			sb.append("<TD class='table-hc' atype='table.rowradio' style='width:25px;' rowspan='"+column.getInt(ROW_SPAN)+"'><div style='width:13px'>&nbsp;</div></TD>");
		}else{
			sb.append("<TD class='table-hc' dataindex='");
			sb.append(tcc.getName());
			sb.append("' colspan='");
			sb.append(column.getInt(COL_SPAN, 1));
			sb.append("' rowspan='");
			sb.append(column.getInt(ROW_SPAN));
			sb.append("'");
			sb.append((pw == null ? "" : (" width='" + pw + "'")));
			if(tcc.isHidden()){
				sb.append(" style='display:none'");
			}
			sb.append(">");
			String text = session.getLocalizedPrompt(getFieldPrompt(session,
					column, dataset));
			sb.append("".equals(text) ? "&#160;" : text);
			sb.append("</TD>");
		}
		return sb.toString();
	}

	public boolean hasFooterBar(CompositeMap column) {
		TableColumnConfig tcc = TableColumnConfig.getInstance(column);
		String footerRenderer = tcc.getFooterRenderer();
		if (footerRenderer != null) {
			return true;
		}
		List childs = column.getChilds();
		if (childs != null) {
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				CompositeMap col = (CompositeMap) it.next();
				if (hasFooterBar(col)) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean createNavgationToolBar(BuildSession session, ViewContext context) throws IOException{
		boolean hasNavBar = false;
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		StringBuffer sb = new StringBuffer();
		String dataset = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		
		String nav = view.getString(TableConfig.PROPERTITY_NAVBAR,"");
		if("true".equalsIgnoreCase(nav)){
			hasNavBar = true;
			CompositeMap navbar = new CompositeMap("navBar");
			navbar.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth());
//			String wstr = uncertain.composite.TextParser.parse(widthStr, model);
			Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);//Integer.valueOf("".equals(wstr) ?  "150" : wstr);
			
			
//			Integer width = Integer.valueOf(view.getString(ComponentConfig.PROPERTITY_WIDTH));
			navbar.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_navbar");
			navbar.put(ComponentConfig.PROPERTITY_CLASSNAME, "table-navbar");
//			navbar.put(PROPERTITY_STYLE, "border:none;border-top:1px solid #cccccc;");
			navbar.put(NavBar.PROPERTITY_DATASET, dataset);
			navbar.put(NavBar.PROPERTITY_NAVBAR_TYPE, view.getString(NavBar.PROPERTITY_NAVBAR_TYPE,"complex"));
			navbar.put(NavBar.PROPERTITY_MAX_PAGE_COUNT, new Integer(view.getInt(NavBar.PROPERTITY_MAX_PAGE_COUNT,10)));
			navbar.put(NavBar.PROPERTITY_PAGE_SIZE_EDITABLE,new Boolean(view.getBoolean(NavBar.PROPERTITY_PAGE_SIZE_EDITABLE,true)));
			sb.append("<caption align='bottom'>");
			try {
				sb.append(session.buildViewAsString(model, navbar));
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			sb.append("</caption>");
			map.put("navbar", sb.toString());
		}
		return hasNavBar;
	}
}
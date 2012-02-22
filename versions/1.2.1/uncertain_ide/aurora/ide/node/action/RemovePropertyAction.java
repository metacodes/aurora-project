package aurora.ide.node.action;


import org.eclipse.jface.resource.ImageDescriptor;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.PropertyViewer;
import aurora.ide.helpers.LocaleMessage;



public class RemovePropertyAction extends ActionListener {

	PropertyViewer viewer;

	public RemovePropertyAction(PropertyViewer viewer,int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		viewer.removePropertyAction();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}
}

package de.fha.dpmi.view.e4.rcp.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * super class for tabs containing a model
 */
@SuppressWarnings("restriction")
public class ModelingPart extends JavaFXPart {

	private MPart previousPart;

	public ModelingPart(String name) {
		super(name);
	}

	@Inject
	@Optional
	public void subscribeTopicPartActivation(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {

		Object element = event.getProperty(EventTags.ELEMENT);
		if (!(element instanceof MPart)) {
			return;
		}

		IEclipseContext serviceContext = E4Workbench.getServiceContext();
		MApplication application = serviceContext.get(MApplication.class);
		// EModelService modelService =
		// application.getContext().get(EModelService.class);
		EPartService partService = application.getContext().get(EPartService.class);
		try {
			if (((MPart) element).getLabel().endsWith("Instance State Model")) {
				final MPart p = partService.findPart("de.fha.dpmi.view.e4.rcp.parts.ExecutionPart");
				p.setVisible(true);
				boolean isShared = (((MPart) element).getLabel().contains("Shared"));
				ExecutionController ec = ExecutionController.getInstance();
				if (ec != null && previousPart != null && !previousPart.getLabel().equals("Execution Control"))
					ec.updateInstanceStateList(isShared);
			} else if (((MPart) element).getLabel().endsWith("Model")) {
				final MPart p = partService.findPart("de.fha.dpmi.view.e4.rcp.parts.ExecutionPart");
				p.setVisible(false);
			}
			previousPart = (MPart) element;
		} catch (IllegalStateException e) {
			// no application window
		}

	}
}

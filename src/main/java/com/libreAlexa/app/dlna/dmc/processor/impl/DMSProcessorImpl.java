package com.libreAlexa.app.dlna.dmc.processor.impl;

import android.util.Log;
import com.libreAlexa.app.dlna.dmc.processor.interfaces.DMSProcessor;
import com.libreAlexa.util.LibreLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

@SuppressWarnings("rawtypes")
public class DMSProcessorImpl implements DMSProcessor {

	private static final String TAG = DMSProcessorImpl.class.getName();
	private Device device;
	private ControlPoint controlPoint;
	private final List<DMSProcessorListener> dmsProcessorListeners;

	public DMSProcessorImpl(Device device, ControlPoint controlPoint) {
		
		this.device = device;
		this.controlPoint = controlPoint;
		dmsProcessorListeners = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public synchronized void browse(final String objectID) {
		Service cds = device.findService(new ServiceType(UpnpProcessorImpl.DMS_NAMESPACE, "ContentDirectory"));

		if (cds != null) {

			SortCriterion sortCriterion = new SortCriterion(true,"dc:title");
			final Action action = cds.getAction("Browse");
			ActionInvocation actionInvocation = new ActionInvocation(action);
			actionInvocation.setInput("ObjectID", objectID);
			actionInvocation.setInput("BrowseFlag", "BrowseDirectChildren");
			actionInvocation.setInput("Filter", "*");
			actionInvocation.setInput("StartingIndex", new UnsignedIntegerFourBytes(0));
			actionInvocation.setInput("RequestedCount", new UnsignedIntegerFourBytes(999));
			actionInvocation.setInput("SortCriteria", sortCriterion.toString());
			ActionCallback actionCallback = new ActionCallback(actionInvocation) {

				@Override
				public void success(ActionInvocation invocation) {
					LibreLogger.d(TAG, invocation.getOutput("Result").toString());

					try {
						DIDLParser parser = new DIDLParser();
						DIDLContent content = parser.parse(invocation.getOutput("Result").toString());

						HashMap<String, List<? extends DIDLObject>> m_result = new HashMap<>();

						for (Container container : content.getContainers()) {
							LibreLogger.d(TAG, "Container: " + container.getTitle() + ", Id is =" + container.getId());
						}
						m_result.put("Containers", content.getContainers());

						for (Item item : content.getItems()) {
							item.setRefID(objectID);
							LibreLogger.d(TAG, "Item: " + item.getTitle()+", setRefID = "+objectID);
						}

						m_result.put("Items", content.getItems());

						fireOnBrowseCompleteEvent(objectID,m_result);

					} catch (Exception e) {
						e.printStackTrace();
						LibreLogger.d(TAG, e.getMessage());
					}
				}

				@Override
				public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
					LibreLogger.d(TAG, defaultMsg);
					fireOnBrowseFailEvent(defaultMsg);
				}

			};

			controlPoint.execute(actionCallback);
		}
	}

	public void dispose() {

	}

	@Override
	public void addListener(DMSProcessorListener listener) {
		synchronized (dmsProcessorListeners) {
			dmsProcessorListeners.add(listener);
		}

	}

	@Override
	public void removeListener(DMSProcessorListener listener) {
		synchronized (dmsProcessorListeners) {
			dmsProcessorListeners.remove(listener);
		}
	}

	private void fireOnBrowseCompleteEvent(String parentObjectId, HashMap<String, List<? extends DIDLObject>> m_result) {
		synchronized (dmsProcessorListeners) {
			for (DMSProcessorListener listener : dmsProcessorListeners) {
				listener.onBrowseComplete(parentObjectId,m_result);
			}
		}
	}

	private void fireOnBrowseFailEvent(String message) {
		synchronized (dmsProcessorListeners) {
			for (DMSProcessorListener listener : dmsProcessorListeners) {
				listener.onBrowseFail(message);
			}
		}
	}
}

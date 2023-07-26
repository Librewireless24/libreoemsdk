package com.libreAlexa.app.dlna.dmc.gui.abstractactivity;


import androidx.appcompat.app.AppCompatActivity;
import com.libreAlexa.app.dlna.dmc.processor.interfaces.UpnpProcessor;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;

public abstract class UpnpListenerActivity extends AppCompatActivity implements UpnpProcessor.UpnpProcessorListener {

	@Override
	public void onRemoteDeviceAdded(RemoteDevice device) {

	}

	
	@Override

	public void onRemoteDeviceRemoved(RemoteDevice device) {

	}

	@Override
	public void onLocalDeviceAdded(LocalDevice device) {

	}


	@Override
	public void onLocalDeviceRemoved(LocalDevice device) {

		
	}


	@Override
	public void onStartComplete() {

	}
	
}
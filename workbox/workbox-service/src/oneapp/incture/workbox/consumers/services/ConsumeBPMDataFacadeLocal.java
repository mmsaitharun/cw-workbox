package oneapp.incture.workbox.consumers.services;

import javax.ejb.Local;

import oneapp.incture.workbox.poadapter.dto.ResponseMessage;

@Local
public interface ConsumeBPMDataFacadeLocal {
	
	ResponseMessage getDataFromOdata(String processor, String scode, String processorDisplay, String status);

}

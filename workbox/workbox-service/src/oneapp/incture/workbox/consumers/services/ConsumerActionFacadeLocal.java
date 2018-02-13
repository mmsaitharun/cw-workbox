package oneapp.incture.workbox.consumers.services;

import javax.ejb.Local;

import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.consumers.util.ResponseDto;

@Local
public interface ConsumerActionFacadeLocal {


	ResponseDto executeAction(WorkboxRequestDto requestDto);
	

}

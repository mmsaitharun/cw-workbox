package oneapp.incture.workbox.consumers.services;

import java.util.List;

import javax.ejb.Local;

import oneapp.incture.workbox.consumers.dto.UserDetailDto;
import oneapp.incture.workbox.consumers.dto.WorKBoxDetailDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;

@Local
public interface ConsumeECCDataFacadeLocal {
	
	ResponseMessage getDataFromECC(String processor,String password);

	List<UserDetailDto> getUsers(WorkboxRequestDto requestDto);

	WorKBoxDetailDto getTaskDetails(WorkboxRequestDto requestDto);



}

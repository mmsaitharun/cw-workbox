package oneapp.incture.workbox.inbox.services;

import javax.ejb.Local;

import oneapp.incture.workbox.inbox.dto.TrackingResponse;
import oneapp.incture.workbox.inbox.dto.WorkboxResponseDto;


@Local
public interface WorkboxFacadeLocal {

	public WorkboxResponseDto getWorkboxFilterData(String processName, String requestId, String createdBy, String createdAt,
			String status, Integer skipCount, Integer maxCount, Integer page,String orderBy,String orderType );


	public WorkboxResponseDto getWorkboxCompletedFilterData(String processName, String requestId, String createdBy,
			String createdAt, String completedAt, String period, Integer skipCount, Integer maxCount, Integer page);


	public TrackingResponse getTrackingResults();


	public String sayHello();

}

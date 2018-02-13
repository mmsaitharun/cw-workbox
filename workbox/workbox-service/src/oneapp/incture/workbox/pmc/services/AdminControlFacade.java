package oneapp.incture.workbox.pmc.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import oneapp.incture.workbox.pmc.dto.AdminControlDto;
import oneapp.incture.workbox.pmc.dto.AgingRangeConfigDto;
import oneapp.incture.workbox.pmc.dto.BaseDto;
import oneapp.incture.workbox.pmc.dto.ProcessConfigDto;
import oneapp.incture.workbox.pmc.dto.ReportAgingDto;
import oneapp.incture.workbox.pmc.dto.ReportDto;
import oneapp.incture.workbox.pmc.dto.WorkloadRangeDto;
import oneapp.incture.workbox.poadapter.dao.ProcessConfigDao;
import oneapp.incture.workbox.poadapter.dao.ReportAgingDao;
import oneapp.incture.workbox.poadapter.dao.WorkloadRangeDao;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * Session Bean implementation class AdminControlBean
 */
@WebService(name = "AdminControlFacade", portName = "AdminControlFacadePort", serviceName = "AdminControlFacadeService", targetNamespace = "http://incture.com/pmc/services/")
@Stateless
public class AdminControlFacade implements AdminControlFacadeLocal {

	@EJB
	ConfigurationFacadeLocal config;

	public AdminControlFacade() {
	}

	@EJB
	private EntityManagerProviderLocal em;

	@WebMethod(operationName = "deleteProcessConfig", exclude = false)
	@Override
	public ResponseMessage deleteProcessConfig(@WebParam(name = "processName") String processName) {
		ProcessConfigDao processConfigDao = new ProcessConfigDao(em.getEntityManager());
		ResponseMessage responseDto=new ResponseMessage();
		try {
			ProcessConfigDto processConfigDto = new ProcessConfigDto();
			processConfigDto.setProcessName(processName);
			processConfigDao.delete(processConfigDto);
			responseDto.setMessage("SUCCESS");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} 
		catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	@WebMethod(operationName = "getAdminConfigurationData", exclude = false)
	@Override
	public AdminControlDto getAdminConfigurationData() {

		AdminControlDto resultDto = new AdminControlDto();

		ProcessConfigDao configDao = new ProcessConfigDao(em.getEntityManager());
		List<ProcessConfigDto> configDtos = null;

		try {
			configDtos = configDao.getAllProcessConfigEntry();
		} catch (NoResultFault e) {
			System.err.println("NO Result found for any process Configured");
		}

		List<WorkloadRangeDto> workloadRangeDtos = config.getWorkLoadRange().getWorkloadRangeDtos();

		ReportAgingDao agingDao = new ReportAgingDao(em.getEntityManager());

		List<ReportAgingDto> reportAgingDtos = agingDao.getAllReportConfiguration();

		List<AgingRangeConfigDto> agingRangeConfigDtos = new ArrayList<AgingRangeConfigDto>();
		AgingRangeConfigDto agingRangeConfigDto = null;
		int processCount = 0;
		int taskCount = 0;
		int taskStatusCount = 0;

		List<ReportDto> diffReportDtoList = new ArrayList<ReportDto>();

		ReportDto processAging, taskStatus, taskAging;
		processAging = new ReportDto();
		taskStatus = new ReportDto();
		taskAging = new ReportDto();

		List<ReportAgingDto> processAging_list, taskStatus_list, taskAging_list;
		processAging_list = new ArrayList<ReportAgingDto>();
		taskStatus_list = new ArrayList<ReportAgingDto>();
		taskAging_list = new ArrayList<ReportAgingDto>();

		if (!ServicesUtil.isEmpty(reportAgingDtos)) {
			Iterator<ReportAgingDto> it = reportAgingDtos.iterator();
			while (it.hasNext()) {
				ReportAgingDto dto = it.next();
				if (!ServicesUtil.isEmpty(dto.getAgingRange())) {
					agingRangeConfigDto = new AgingRangeConfigDto();
					agingRangeConfigDto.setAgingRange(dto.getAgingRange());
					agingRangeConfigDto.setReportName(dto.getReportName());
					agingRangeConfigDto.setReportId(dto.getId());
					agingRangeConfigDtos.add(agingRangeConfigDto);
					it.remove();
				} else {
					if (dto.getReportName().equals(PMCConstant.PROCESS_AGING_REPORT)) {
						processAging.setReportName("PROCESS AGING");
						processAging_list.add(dto);
						++processCount;
					} else if (dto.getReportName().equals(PMCConstant.TASK_AGING_REPORT)) {
						taskAging.setReportName("TASK AGING");
						taskAging_list.add(dto);
						++taskCount;
					} else if (dto.getReportName().equals(PMCConstant.USER_TASK_STATUS_GRAPH)) {
						taskStatus.setReportName("TASK STATUS");
						taskStatus_list.add(dto);
						++taskStatusCount;
					}
				}
			}
		}
		processAging.setReportDtoList(processAging_list);
		taskAging.setReportDtoList(taskAging_list);
		taskStatus.setReportDtoList(taskStatus_list);
		diffReportDtoList.add(processAging);
		diffReportDtoList.add(taskAging);
		diffReportDtoList.add(taskStatus);

		resultDto.setProcessConfigDtos(configDtos);
		resultDto.setWorkloadRangeDtos(workloadRangeDtos);
		resultDto.setReportDto(diffReportDtoList);
		resultDto.setAgingRangeConfigDto(agingRangeConfigDtos);
		resultDto.setProcessCount(processCount);
		resultDto.setTaskCount(taskCount);
		resultDto.setTaskStatusCount(taskStatusCount);
		return resultDto;

	}

	@WebMethod(operationName = "createUpdateDataAdmin", exclude = false)
	@Override
	public ResponseMessage createUpdateDataAdmin(@WebParam(name = "adminControlDto") AdminControlDto adminControlDto) {

		ResponseMessage responseDto = new ResponseMessage();
		WorkloadRangeDao workloadRangeDao = new WorkloadRangeDao(em.getEntityManager());
		ReportAgingDao reportAgingDao = new ReportAgingDao(em.getEntityManager());

		if (!ServicesUtil.isEmpty(adminControlDto)) {
			List<ProcessConfigDto> processConfigDtos = adminControlDto.getProcessConfigDtos();
			List<WorkloadRangeDto> workloadRangeDtos = adminControlDto.getWorkloadRangeDtos();
			List<ReportDto> reportAgingDtos = adminControlDto.getReportDto();
			List<AgingRangeConfigDto> agingRangeConfigDtos = adminControlDto.getAgingRangeConfigDto();
			try {

				if (!ServicesUtil.isEmpty(processConfigDtos)) {
					for (ProcessConfigDto dto : processConfigDtos) {
						updateCreateDelete(dto);
						}
				}

				if (!ServicesUtil.isEmpty(workloadRangeDtos)) {

					for (WorkloadRangeDto dto1 : workloadRangeDtos) {
						try {
							workloadRangeDao.update(dto1);
							System.err.println("Workload Range Updated");
						} catch (NoResultFault e) {
							try {
								workloadRangeDao.create(dto1);
								System.err.println("WorkloadRange Created");
							} catch (Exception eWorkloadRange) {
								System.err.println("Exception : " + eWorkloadRange.getMessage());
							}
						} catch (Exception eWorkloadRange) {
							System.err.println("Exception : " + eWorkloadRange.getMessage());
						}
					}
				}
				if (!ServicesUtil.isEmpty(reportAgingDtos)) {

					for (int i = 0; i < reportAgingDtos.size(); i++) {
						for (ReportAgingDto dto2 : reportAgingDtos.get(i).getReportDtoList()) {
							updateCreateDelete(dto2);
							// dont send dao not required as it is in same file.
						}
					}

				}

				if (!ServicesUtil.isEmpty(agingRangeConfigDtos)) {
					for (AgingRangeConfigDto dto4 : agingRangeConfigDtos) {
						ReportAgingDto dto = new ReportAgingDto();
						dto.setAgingRange(dto4.getAgingRange());
						dto.setReportName(dto4.getReportName());
						dto.setId(dto4.getReportId());

						try {
							reportAgingDao.update(dto);
							System.err.println("Aging Range Updated");
						} catch (NoResultFault e) {
							try {
								reportAgingDao.create(dto);
								System.err.println("Aging Range Created");
							} catch (Exception eAgingRange) {
								System.err.println("Exception1 : " + eAgingRange.getMessage());
							}
						} catch (Exception eAgingRange) {
							System.err.println("Exception2 : " + eAgingRange.getMessage());
						}
					}
				}

				responseDto.setMessage("SUCCESS");
				responseDto.setStatus("SUCCESS");
				responseDto.setStatusCode("0");

			} catch (Exception e) {
				System.err.println("Exception : " + e.getMessage());
				responseDto.setMessage("Failed");
				responseDto.setStatus("Failed");
				responseDto.setStatusCode("1");

			}
		}
		return responseDto;
	}
	
	private void updateCreateDelete(BaseDto baseDto) {
		String expr = baseDto.getClass().getSimpleName();

		if (expr.equals("ProcessConfigDto")) {
			ProcessConfigDto dto = (ProcessConfigDto) baseDto;
			ProcessConfigDao dao = new ProcessConfigDao(em.getEntityManager());
			if (!ServicesUtil.isEmpty(dto.getIsDeleted()) && dto.getIsDeleted().equals(true)) {
				try {
					dao.delete(dto);
					System.err.println("Process Config deleted");
				} catch (Exception e) {
					System.err.println("Exception : " + e.getMessage());
				}
			} else {
				try {
					dao.update(dto);
					System.err.println("Process Config Updated");
				} catch (NoResultFault e) {
					try {
						dao.create(dto);
						System.err.println("Process Config Created");
					} catch (Exception eReportingAging) {
						System.err.println("Exception : " + eReportingAging.getMessage());
					}
				} catch (Exception eReportingAging) {
					System.err.println("Exception : " + eReportingAging.getMessage());
				}
			}
		} else if (expr.equals("ReportAgingDto")) {
			ReportAgingDto dto = (ReportAgingDto) baseDto;
			ReportAgingDao dao = new ReportAgingDao(em.getEntityManager());
			if (!ServicesUtil.isEmpty(dto.getIsDeleted()) && dto.getIsDeleted().equals(true)) {
				try {
					dao.delete(dto);
					System.err.println("Reporting Aging deleted");
				} catch (Exception e) {
					System.err.println("Exception : " + e.getMessage());
				}
			} else {
				try {
					dao.update(dto);
					System.err.println("Reporting Aging Updated");
				} catch (NoResultFault e) {
					try {
						dao.create(dto);
						System.err.println("Reporting Aging Created");
					} catch (Exception eReportingAging) {
						System.err.println("Exception : " + eReportingAging.getMessage());
					}
				} catch (Exception eReportingAging) {
					System.err.println("Exception : " + eReportingAging.getMessage());
				}
			}
		}

	}

}

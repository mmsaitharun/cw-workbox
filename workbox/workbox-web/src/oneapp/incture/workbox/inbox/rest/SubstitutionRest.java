package oneapp.incture.workbox.inbox.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.incture.pmc.poadapter.services.ResponseDto;
import com.incture.pmc.poadapter.services.SubstitutionRuleDto;

import oneapp.incture.workbox.inbox.services.SubstitutionRuleFacadeLocal;
import oneapp.incture.workbox.pmc.dto.responses.SubstitutionResponseDto;
import oneapp.incture.workbox.pmc.dto.responses.UserDtoResponse;

@Path("/substitution")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class SubstitutionRest {

	@EJB
	private SubstitutionRuleFacadeLocal substitutionServices;
	
	@POST
	@Path("/createRule")
	 public ResponseDto createRule(SubstitutionRuleDto ruleDto){
    	return substitutionServices.createRule(ruleDto);
    }
    
	@POST
	@Path("/deleteRule")
    public ResponseDto deleteRule(SubstitutionRuleDto ruleDto){
    	return substitutionServices.deleteRule(ruleDto);
    }
    
	@POST
	@Path("/updateRule")
    public ResponseDto updateRule(SubstitutionRuleDto ruleDto){
    	return substitutionServices.updateRule(ruleDto);
    }
	
	@POST
	@Path("/deleteAndCreateRule")
	 public ResponseDto deleteAndCreateRule(SubstitutionRuleDto ruleDto){
    	return substitutionServices.deleteAndCreate(ruleDto);
    }
	
	@GET
	@Path("/getActiveRulesBySubstitute/{user}")
    public SubstitutionResponseDto getActiveRulesBySubstitute(@PathParam("user")String  substitutingUser){
		System.err.println("[PMC][Rest][SubstitutionRest][invoked]"+substitutingUser);
    	return substitutionServices.getActiveRulesBySubstitute(substitutingUser);
    }
	
	@GET
	@Path("/getActiveRulesBySubstituted/{user}")
    public SubstitutionResponseDto getActiveRulesBySubstitutedUser(@PathParam("user")String  substitutedUser){
    	return substitutionServices.getActiveRulesBySubstitutedUser(substitutedUser);
    }
	
	@GET
	@Path("/getInactiveRulesBySubstitute/{user}")
    public SubstitutionResponseDto getInactiveRulesBySubstitute(@PathParam("user")String  substitutingUser){
    	return substitutionServices.getInactiveRulesBySubstitute(substitutingUser);
    }

	@GET
	@Path("/getInactiveRulesBySubstituted/{user}")
    public SubstitutionResponseDto getInactiveRulesBySubstitutedUser(@PathParam("user")String  substitutedUser){
    	return substitutionServices.getInactiveRulesBySubstitutedUser(substitutedUser);
    }

	@GET
	@Path("/getSubstituteUsers/{user}")
    public UserDtoResponse getSubstituteUsers(@PathParam("user")String  substitutedUser){
    	return substitutionServices.getSubstituteUsers(substitutedUser);
    }

	@GET
	@Path("/getSubstitutedUsers/{user}")
    public UserDtoResponse getSubstitutedUsers(@PathParam("user")String  substitutingUserString){
    	return substitutionServices.getSubstitutedUsers(substitutingUserString);
    }
	@GET
	@Path("/getRulesBySubstitute/{user}")
    public SubstitutionResponseDto getRulesBySubstitute(@PathParam("user")String  user){
    	return substitutionServices.getRulesBySubstitute(user);
    }
	@GET
	@Path("/getRulesBySubstitutedUser/{user}")
    public SubstitutionResponseDto getRulesBySubstitutedUser(@PathParam("user")String  user){
    	return substitutionServices.getRulesBySubstitutedUser(user);
    }
	@GET
	@Path("/getAllRulesByUser/{user}")
    public SubstitutionResponseDto getAllRulesByUser(@PathParam("user")String  user){
    	return substitutionServices.getAllRulesByUser(user);
    }
	
}
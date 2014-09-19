package species

import grails.util.Environment;

import species.utils.Utils;
import species.groups.UserGroup;

import grails.converters.JSON;
class SecurityFilters {

	def grailsApplication;
	def springSecurityService;
	
    def filters = {
        all(controller:'*', action:'*') {
            before = {
                log.debug "request params: $params"

				grailsApplication.config.speciesPortal.domain = Utils.getDomain(request);
				//println "Setting domain to : "+grailsApplication.config.speciesPortal.domain;
				
				def appName = grailsApplication.metadata['app.name']
/*                if(params.ajax_login_error == "1") {
                    render ([status:401, error:'Please login to continue'] as JSON)
                    return;
                } 
*/ 
//				println params;
//				request.cookies.each{println it.name+" : "+it.value}
//				def enames = request.getHeaderNames();
//				   while (enames.hasMoreElements()) {
//					  String name = (String) enames.nextElement();
//					  String value = request.getHeader(name);
//					  println name+":"+value;
//				   }
				
            }
            after = { model ->
               //setting user group and permission for view
				if(model){
					def userGroupInstance = model.userGroupInstance
					def userGroup = model.userGroup
					if(!userGroupInstance) {
						if(userGroup) {
							userGroupInstance = userGroup
						} else if(params.userGroup) {
							if(params.userGroup instanceof UserGroup) {
								userGroupInstance = params.userGroup
							} else {
								userGroupInstance = UserGroup.get(params.long('userGroup'));
							}
						} else if(params.webaddress) {
							userGroupInstance = UserGroup.findByWebaddress(params.webaddress);
						} else if(params.userGroupWebaddress) {
							userGroupInstance =UserGroup.findByWebaddress(params.userGroupWebaddress);
						}
					}
					
                    if(userGroupInstance && userGroupInstance.id){
						model.userGroupInstance = userGroupInstance
						def secTagLib = grailsApplication.mainContext.getBean('species.CustomSecurityAclTagLib');
						model.canEditUserGroup = secTagLib.hasPermission(['permission':org.springframework.security.acls.domain.BasePermission.WRITE, 'object':userGroupInstance], 'permitted')
						def user = springSecurityService.getCurrentUser();
						model.isExpertOrFounder = (user && (userGroupInstance.isExpert(user) || userGroupInstance.isFounder(user)))
					}


                    //passing locale Languages
                    model.localeLanguages = grailsApplication.config.speciesPortal.localeLanguages
				}
                log.debug "after rendering"
            }
			
            afterView = {
                log.debug "after view"
            }
        }
    }
    
}

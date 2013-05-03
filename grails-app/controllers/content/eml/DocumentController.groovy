package content.eml

import grails.plugins.springsecurity.Secured

import grails.converters.JSON
import org.grails.taggable.Taggable


class DocumentController {

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def documentService
	def springSecurityService

	def index = {
		redirect(action: "browser", params: params)
	}



	@Secured(['ROLE_USER'])
	def create = {
		def documentInstance = new Document()
		documentInstance.properties = params
		return [documentInstance: documentInstance]
	}

	@Secured(['ROLE_USER'])
	def save = {

		log.debug "params in document save "+ params

		params.author = springSecurityService.currentUser;
		def documentInstance = documentService.createDocument(params)

		log.debug( "document instance with params assigned >>>>>>>>>>>>>>>>: "+ documentInstance)
		if (documentInstance.save(flush: true)) {

			flash.message = "${message(code: 'default.created.message', args: [message(code: 'document.label', default: 'Document'), documentInstance.id])}"
			redirect(action: "show", id: documentInstance.id)

			def tags = (params.tags != null) ? Arrays.asList(params.tags) : new ArrayList();
			
			documentInstance.setTags(tags)
			if(params.groupsWithSharingNotAllowed) {
				documentService.setUserGroups(observationInstance, [
					params.groupsWithSharingNotAllowed
				]);
			} else {
				if(params.userGroupsList) {
					def userGroups = (params.userGroupsList != null) ? params.userGroupsList.split(',').collect{k->k} : new ArrayList();

					documentService.setUserGroups(documentInstance, userGroups);
				}
			}
		}
		else {
			render(view: "create", model: [documentInstance: documentInstance])
		}
	}

	def show = {
		def documentInstance = Document.get(params.id)
		if (!documentInstance) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'document.label', default: 'Document'), params.id])}"
			redirect(action: "list")
		}
		else {
			[documentInstance: documentInstance]
		}
	}

	@Secured(['ROLE_USER'])
	def edit = {
		def documentInstance = Document.get(params.id)
		if (!documentInstance) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'document.label', default: 'Document'), params.id])}"
			redirect(action: "list")
		}
		else {
			render(view: "create", model: [documentInstance: documentInstance])
		}
	}

	@Secured(['ROLE_USER'])	
	def update = {
		def documentInstance = Document.get(params.id)
		if (documentInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (documentInstance.version > version) {

					documentInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [
						message(code: 'document.label', default: 'Document')] as Object[], "Another user has updated this Document while you were editing")
					render(view: "edit", model: [documentInstance: documentInstance])
					return
				}
			}
			documentInstance.properties = params
			if (!documentInstance.hasErrors() && documentInstance.save(flush: true)) {
				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'document.label', default: 'Document'), documentInstance.id])}"
				redirect(action: "show", id: documentInstance.id)
			}
			else {
				render(view: "edit", model: [documentInstance: documentInstance])
			}
		}
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'document.label', default: 'Document'), params.id])}"
			redirect(action: "list")
		}
	}

	@Secured(['ROLE_USER'])	
	def delete = {
		def documentInstance = Document.get(params.id)
		if (documentInstance) {
			try {
				documentInstance.delete(flush: true)
				flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'document.label', default: 'Document'), params.id])}"
				redirect(action: "list")
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'document.label', default: 'Document'), params.id])}"
				redirect(action: "show", id: params.id)
			}
		}
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'document.label', default: 'Document'), params.id])}"
			redirect(action: "list")
		}
	}
	
	def browser = {
		log.debug params

		def model = getDocumentList(params)
		render (view:"browser", model:model)
		return;
	}

	protected def getDocumentList(params) {
		
				def max = Math.min(params.max ? params.int('max') : 12, 100)
				def offset = params.offset ? params.int('offset') : 0
				def filteredDocument = documentService.getFilteredDocuments(params, max, offset)
				def documentInstanceList = filteredDocument.documentInstanceList
				def queryParams = filteredDocument.queryParams
				def activeFilters = filteredDocument.activeFilters
		
				def totalDocumentInstanceList = documentService.getFilteredDocuments(params, -1, -1).documentInstanceList
				def count = totalDocumentInstanceList.size()
		
				return [totalDocumentInstanceList:totalDocumentInstanceList, documentInstanceList: documentInstanceList, documentInstanceTotal: count, queryParams: queryParams, activeFilters:activeFilters, total:count]
		
			}
		


	def tags = {
		log.debug params;
		render Tag.findAllByNameIlike("${params.term}%")*.name as JSON
	}

	
	def tagcloud = { render (view:"tagcloud") }

	//// SEARCH //////
	/**
	 * 	
	 */
	def search = {
		log.debug params;
		def model = documentService.search(params)
		model['isSearch'] = true;

		if(params.loadMore?.toBoolean()){
			params.remove('isGalleryUpdate');
			render(template:"/document/searchResultsTemplate", model:model);
			return;

		} else {
			params.remove('isGalleryUpdate');
			def obvListHtml =  g.render(template:"/document/searchResultsTemplate", model:model);
			model.resultType = "document"
			def obvFilterMsgHtml = g.render(template:"/common/observation/showObservationFilterMsgTemplate", model:model);

			def result = [obvListHtml:obvListHtml, obvFilterMsgHtml:obvFilterMsgHtml]

			render (result as JSON)
			return;
		}
	}
}
<!DOCTYPE html>
<%@page import="species.groups.UserGroup"%>
<%@page import="species.utils.Utils"%>
<%@page
	import="grails.plugin.springsecurity.SpringSecurityUtils"%>
<html  xmlns="http://www.w3.org/1999/xhtml" xmlns:og="http://ogp.me/ns#" 
      xmlns:fb="https://www.facebook.com/2008/fbml">
<head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb#">
    
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title><g:layoutTitle/></title>

<r:require modules="observations_list" />
<g:layoutHead />
<ckeditor:resources />
<r:layoutResources />

<g:set var="domain" value="${Utils.getDomain(request)}"/>
<script src="https://www.google.com/jsapi" type="text/javascript"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.js"></script>
<script src="http://apis.google.com/js/auth.js" type="text/javascript">
</script>
<g:set var="userGroupInstance" value="${userGroupInstance}"/>
<g:if test="${userGroupInstance && userGroupInstance.theme}">
	<link rel="stylesheet" type="text/css"
		href="${resource(dir:'group-themes', file:userGroupInstance.theme + '.css')}" />
</g:if>


<g:if test="${grailsApplication.config.speciesPortal.app.googlePlusUrl}">
	<a href="${grailsApplication.config.speciesPortal.app.googlePlusUrl}" rel="publisher"></a>
</g:if>	

</head>
<body>
	<div id="loading" class="loading" style="display: none;">
		<span><g:message code="msg.loading" /> </span>
	</div>
	<div id="postToUGroup" class="overlay" style="display: none;">
        <i class="icon-plus"></i>
    </div>
	<div id="species_main_wrapper" style="clear: both;">
		<domain:showSiteHeader model="['userGroupInstance':userGroupInstance]" />
            <g:if test ="${params.controller == 'namelist'}">
                <div class="container-fluid outer-wrapper">
            </g:if>
            <g:else>
                <div class="container outer-wrapper">
            </g:else>
			<div>
				<div style="padding: 10px 0px; margin-left: -20px">
					<g:layoutBody />
				</div>
			</div>
		</div>

		<domain:showSiteFooter />
	</div>
<%--	<div id="feedback_button" onclick="location.href='/feedback_form';" style="left: -10px;z-index:1000;"></div>--%>
	<r:layoutResources />



</body>
</html>

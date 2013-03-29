<g:if test="${speciesInstance}">
<div class="species-external-link" style="float:left;">
	<g:each in="${speciesInstance.taxonConcept.externalLinks}" var="r">
		<g:each in="${['eolId', 'iucnId', 'gbifId']}" var="extLinkKey">
			<g:if test="${r[extLinkKey]}">
				<s:showExternalLink model="['key':extLinkKey, 'externalLinks':r, 'taxonConcept':speciesInstance.taxonConcept]"/>										
			</g:if>	
		</g:each>									
	</g:each>
	<s:showExternalLink model="['key':'wikipedia', 'taxonConcept':speciesInstance.taxonConcept]"/>
</div>	
</g:if>
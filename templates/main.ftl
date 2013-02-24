<#include "header.ftl">
<#include "macros.ftl">

<@vegabanner
	vars.bannerLogoURL ! ""
/>

<@topsection 
  doc.alert.title ! "No title set." 
/>


<@infobox 
  doc.alert.class ! "No class set." 
  vars.resource ! ""
  vars.param ! ""
  vars.methods ! "" 
  vars.detectiontype ! ""
  vars.severity ! ""
  vars.severityCSS ! ""
/>

<#if vars.requestText??>
  <div class="section" id="requestlink">
        <h2>Request</h2>
          <div class="content" id="sectioncontent">
                <a href="#" onclick="linkClick('${vars.requestId}');"> ${vars.requestText} </a>
          </div>
  </div>
</#if>

<@outputsection
  vars.output ! ""
/>

<@discussion
  doc.alert.discussion
/>

<@impactsection 
  doc.alert.impact
/>

<@remediationsection
  doc.alert.remediation
/>

<#if doc.alert.external?size != 0>
  <div class="section" id="remrefsection">
	<h2>External Remediation Guidelines</h2>
	  <div class="content" id="sectioncontent">
		Below are some links to third-party guidelines, tutorials and other documentation that may be useful in understanding and/or addressing this finding.
		<br>
		<br>
		<#list doc.alert.external.url as u>
		  <p><a class="reflink" href="${u.@address?html}">${u?html}</a></p>
		</#list>
	  </div>
  </div>
</#if>

<#if doc.alert.references?size != 0>
  <div class="section" id="referencesection">
	<h2>References</h2>
	  <div class="content" id="sectioncontent">
		Some additional links with relevant information published by third-parties:
		<br>
		<br>
		<#list doc.alert.references.url as u>
		  <ul><li><a class="reflink" href="${u.@address?html}">${u?html}</a></li></ul>
		</#list>
	  </div>
  </div>
</#if>

<br>

<#include "footer.ftl" parse=false>

<br>
<br>

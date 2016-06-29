<#include "report-macros.ftl">

<div class="alert-body">
<a name="alert-${vars.num}"></a>

<@vegabanner
	vars.bannerLogoURL ! ""
/>

<@topsection 
  doc.alert.title ! "No title set." 
/>

<div class=alert-nav>
<#if vars.prev??>
  <a href="#alert-${vars.prev}">Previous</a></span>
</#if>
<#if vars.next??>
  <a href="#alert-${vars.next}">Next</a>
</#if>
</div>


<@infobox 
  doc.alert.class ! "No class set." 
  vars.resource ! ""
  vars.hostname ! ""
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
                ${vars.requestText}
          </div>
  </div>
</#if>

<@responsesection
  vars.responseText ! ""
/>

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
<br />
<br />
<br />
<hr>
<div class=footer>
  <a class="innerlinksmall" href="#summary-page">Top</a>
</div>
<span class=footertext>Report generated ${vars.datetime} by the Vega open source web application security testing framework.
  <br />
  <a href="https://subgraph.com">https://subgraph.com</a><br />
</div>


<#include "header.ftl">
<#include "macros.ftl">

<@topsection 
  doc.alert.title ! "No title set." 
  vars.imageURL ! ""
/>

<hr>

<@infobox 
  doc.alert.class ! "No class set." 
  vars.resource ! ""
  vars.severity ! ""
  vars.severityCSS ! ""
/>

<@impactsection 
  doc.alert.impact
/>

<@remediation
  doc.alert.remediation
/>

<@detailsection
  vars.resource ! ""
  vars.methods ! ""
  vars.parameter ! ""
  vars.attackstring ! ""
/>


<@outputsection
  vars.output ! ""
/>

<#if vars.requestText??> 
<h2>Request</h2>
<div class="section" id="requestlink">
<a href="#" onclick="linkClick('${vars.requestId}');"> ${vars.requestText} </a>
</div>
</#if>

<@discussion
  doc.alert.discussion
/>


<#if doc.alert.external?size != 0>
<div class="section" id="remrefsection">

<h2>External Remediation Guidelines</h2>
Below are some links to third-party guidelines, tutorials and other documentation that may be useful in eliminating this and other vulnerabilities.

<br>
<br>

<#list doc.alert.external.url as u>
<p><a class="reflink" href="${u.@address?html}">${u?html}</a></p>
</#list>

</div>

</#if>

<#if doc.alert.references?size != 0>
<div class="section" id="referencesection">

<h2>References</h2>

Some additional links with information about this category of vulnerability published by third parties.

<br>
<br>

<#list doc.alert.references.url as u>
<p><a class="reflink" href="${u.@address?html}">${u?html}</a></p>
</#list>

</div>
</#if>


<#include "footer.ftl" parse=false>
